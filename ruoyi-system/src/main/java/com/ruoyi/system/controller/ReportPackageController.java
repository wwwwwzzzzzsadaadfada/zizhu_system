package com.ruoyi.system.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.system.domain.StReportPackage;
import com.ruoyi.system.domain.StReportPackageItem;
import com.ruoyi.system.domain.StReportPdf;
import com.ruoyi.system.service.IReportPackageService;

/**
 * 报表打包管理Controller
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/system/report/package")
public class ReportPackageController extends BaseController
{
    @Autowired
    private IReportPackageService reportPackageService;

    /**
     * 查询报表打包记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackage:list')")
    @GetMapping("/list")
    public TableDataInfo list(StReportPackage stReportPackage)
    {
        startPage();
        List<StReportPackage> list = reportPackageService.selectStReportPackageList(stReportPackage);
        return getDataTable(list);
    }

    /**
     * 获取报表打包记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackage:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(reportPackageService.selectStReportPackageById(id));
    }

    /**
     * 预览打包信息（不实际打包，仅返回统计信息）
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackage:add')")
    @PostMapping("/preview")
    public AjaxResult previewPackage(@RequestBody PreviewPackageRequest request)
    {
        try
        {
            java.util.Map<String, Object> preview = reportPackageService.previewPackage(request.getPdfIds());
            return success(preview);
        }
        catch (Exception e)
        {
            return error("预览失败：" + e.getMessage());
        }
    }

    /**
     * 手动选择文件打包
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackage:add')")
    @Log(title = "报表打包", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult createPackage(@RequestBody CreatePackageRequest request)
    {
        try
        {
            // 后端会验证ID有效性、权限检查、自动生成打包名称等
            StReportPackage pkg = reportPackageService.createPackage(
                request.getPdfIds(), 
                request.getPackageName() // 如果为空，后端会自动生成
            );
            return success(pkg);
        }
        catch (Exception e)
        {
            return error("打包失败：" + e.getMessage());
        }
    }

    /**
     * 按条件打包全部档案
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackage:add')")
    @Log(title = "报表打包（按条件）", businessType = BusinessType.INSERT)
    @PostMapping("/createByCondition")
    public AjaxResult createPackageByCondition(@RequestBody CreatePackageByConditionRequest request)
    {
        try
        {
            StReportPackage pkg;
            
            // 判断是否为混合打包（报表 + 自定义档案包）
            if ((request.getReportIds() != null && !request.getReportIds().isEmpty()) 
                || (request.getReportNames() != null && !request.getReportNames().isEmpty())
                || (request.getCustomPackageCodes() != null && !request.getCustomPackageCodes().isEmpty()))
            {
                // 混合打包：优先使用reportIds，其次reportNames（兼容旧版本）
                pkg = reportPackageService.createHybridPackage(
                    request.getReportIds() != null && !request.getReportIds().isEmpty() 
                        ? request.getReportIds() : request.getReportNames(),
                    request.getReportIds() != null && !request.getReportIds().isEmpty(), // 是否使用reportId模式
                    request.getCustomPackageCodes(),
                    request.getPackageName()
                );
            }
            else if (request.isAllPackages() != null && request.isAllPackages())
            {
                // 一键打包全部
                pkg = reportPackageService.createAllPackage(
                    request.getYearSemesterId(),
                    request.getSchoolingPlanId(),
                    request.getGradeId(),
                    request.getClassId(),
                    request.getStudentName(),
                    request.getBeginCreateTime(),
                    request.getEndCreateTime(),
                    request.getPackageName()
                );
            }
            else
            {
                // 单个报表名称打包
                StReportPdf queryParams = new StReportPdf();
                queryParams.setReportId(request.getReportId());
                queryParams.setYearSemesterId(request.getYearSemesterId());
                queryParams.setSchoolingPlanId(request.getSchoolingPlanId());
                queryParams.setGradeId(request.getGradeId());
                queryParams.setClassId(request.getClassId());
                queryParams.setReportName(request.getReportName());
                queryParams.setStudentName(request.getStudentName());
                
                // 处理时间范围
                if (request.getBeginCreateTime() != null || request.getEndCreateTime() != null)
                {
                    java.util.Map<String, Object> params = new java.util.HashMap<>();
                    if (request.getBeginCreateTime() != null)
                    {
                        params.put("beginCreateTime", request.getBeginCreateTime());
                    }
                    if (request.getEndCreateTime() != null)
                    {
                        params.put("endCreateTime", request.getEndCreateTime());
                    }
                    queryParams.setParams(params);
                }

                pkg = reportPackageService.createPackageByCondition(
                    queryParams, 
                    request.getPackageName()
                );
            }
            return success(pkg);
        }
        catch (Exception e)
        {
            return error("打包失败：" + e.getMessage());
        }
    }

    /**
     * 下载打包的zip文件
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackage:download')")
    @GetMapping("/download/{id}")
    public void downloadPackage(@PathVariable("id") Long id, HttpServletResponse response)
    {
        try
        {
            StReportPackage pkg = reportPackageService.selectStReportPackageById(id);
            if (pkg == null)
            {
                throw new RuntimeException("打包记录不存在");
            }

            if (pkg.getStatus() == null || pkg.getStatus() != 1)
            {
                throw new RuntimeException("打包文件尚未生成或已失败");
            }

            String zipFilePath = pkg.getZipFilePath();
            if (zipFilePath == null || zipFilePath.isEmpty())
            {
                throw new RuntimeException("压缩包路径为空");
            }

            String fullPath = RuoYiConfig.getProfile() + zipFilePath;
            File file = new File(fullPath);
            if (!file.exists())
            {
                throw new RuntimeException("压缩包文件不存在");
            }

            // 设置响应头
            String fileName = pkg.getZipFileName() != null ? pkg.getZipFileName() : "package.zip";
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");

            // 下载文件
            FileUtils.writeBytes(fullPath, response.getOutputStream());
        }
        catch (Exception e)
        {
            logger.error("下载打包文件失败", e);
            try
            {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("下载失败：" + e.getMessage());
            }
            catch (Exception ex)
            {
                // 忽略
            }
        }
    }

    /**
     * 删除报表打包记录
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackage:remove')")
    @Log(title = "报表打包记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable("ids") Long[] ids)
    {
        return toAjax(reportPackageService.deleteStReportPackageByIds(ids));
    }

    /**
     * 查询打包明细列表
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackage:query')")
    @GetMapping("/item/{packageId}")
    public AjaxResult getPackageItems(@PathVariable("packageId") Long packageId)
    {
        List<StReportPackageItem> items = reportPackageService.selectPackageItemListByPackageId(packageId);
        return success(items);
    }

    /**
     * 预览打包请求对象
     */
    public static class PreviewPackageRequest
    {
        private List<Long> pdfIds;

        public List<Long> getPdfIds()
        {
            return pdfIds;
        }

        public void setPdfIds(List<Long> pdfIds)
        {
            this.pdfIds = pdfIds;
        }
    }

    /**
     * 手动打包请求对象
     */
    public static class CreatePackageRequest
    {
        private List<Long> pdfIds;
        private String packageName;

        public List<Long> getPdfIds()
        {
            return pdfIds;
        }

        public void setPdfIds(List<Long> pdfIds)
        {
            this.pdfIds = pdfIds;
        }

        public String getPackageName()
        {
            return packageName;
        }

        public void setPackageName(String packageName)
        {
            this.packageName = packageName;
        }
    }

    /**
     * 按条件打包请求对象
     */
    public static class CreatePackageByConditionRequest
    {
        private String reportId;
        private Long yearSemesterId;
        private Long schoolingPlanId;
        private Long gradeId;
        private Long classId;
        private String reportName;
        private String studentName;
        private String packageName;
        private String beginCreateTime;
        private String endCreateTime;
        private List<String> reportNames; // 多个报表名称（混合打包，兼容旧逻辑）
        private List<String> reportIds; // 多个报表ID（混合打包，推荐使用）
        private Boolean allPackages; // 是否一键打包全部（true=一键打包全部，false/null=其他模式）
        private List<String> customPackageCodes; // 自定义档案包编码列表（混合打包）

        public String getReportId()
        {
            return reportId;
        }

        public void setReportId(String reportId)
        {
            this.reportId = reportId;
        }

        public Long getYearSemesterId()
        {
            return yearSemesterId;
        }

        public void setYearSemesterId(Long yearSemesterId)
        {
            this.yearSemesterId = yearSemesterId;
        }

        public Long getSchoolingPlanId()
        {
            return schoolingPlanId;
        }

        public void setSchoolingPlanId(Long schoolingPlanId)
        {
            this.schoolingPlanId = schoolingPlanId;
        }

        public Long getGradeId()
        {
            return gradeId;
        }

        public void setGradeId(Long gradeId)
        {
            this.gradeId = gradeId;
        }

        public Long getClassId()
        {
            return classId;
        }

        public void setClassId(Long classId)
        {
            this.classId = classId;
        }

        public String getReportName()
        {
            return reportName;
        }

        public void setReportName(String reportName)
        {
            this.reportName = reportName;
        }

        public String getStudentName()
        {
            return studentName;
        }

        public void setStudentName(String studentName)
        {
            this.studentName = studentName;
        }

        public String getPackageName()
        {
            return packageName;
        }

        public void setPackageName(String packageName)
        {
            this.packageName = packageName;
        }

        public String getBeginCreateTime()
        {
            return beginCreateTime;
        }

        public void setBeginCreateTime(String beginCreateTime)
        {
            this.beginCreateTime = beginCreateTime;
        }

        public String getEndCreateTime()
        {
            return endCreateTime;
        }

        public void setEndCreateTime(String endCreateTime)
        {
            this.endCreateTime = endCreateTime;
        }

        public List<String> getReportNames()
        {
            return reportNames;
        }

        public void setReportNames(List<String> reportNames)
        {
            this.reportNames = reportNames;
        }

        public List<String> getReportIds()
        {
            return reportIds;
        }

        public void setReportIds(List<String> reportIds)
        {
            this.reportIds = reportIds;
        }

        public Boolean isAllPackages()
        {
            return allPackages;
        }

        public void setAllPackages(Boolean allPackages)
        {
            this.allPackages = allPackages;
        }

        public List<String> getCustomPackageCodes()
        {
            return customPackageCodes;
        }

        public void setCustomPackageCodes(List<String> customPackageCodes)
        {
            this.customPackageCodes = customPackageCodes;
        }
    }
}

