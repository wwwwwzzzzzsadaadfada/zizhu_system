package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import java.io.File;
import java.io.UnsupportedEncodingException;
import com.ruoyi.system.domain.StReportPdf;
import com.ruoyi.system.domain.StReportPdfBatch;
import com.ruoyi.system.service.IReportPdfService;

/**
 * 报表PDF管理Controller
 * 
 * @author ruoyi
 * @date 2025-12-15
 */
@RestController
@RequestMapping("/system/report/pdf")
public class ReportPdfController extends BaseController
{
    @Autowired
    private IReportPdfService reportPdfService;

    /**
     * 查询报表PDF记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:list')")
    @GetMapping("/list")
    public TableDataInfo list(StReportPdf stReportPdf)
    {
        // 只查询未删除的归档记录
        if (stReportPdf.getStatus() == null)
        {
            stReportPdf.setStatus(1);
        }
        startPage();
        List<StReportPdf> list = reportPdfService.selectStReportPdfList(stReportPdf);
        return getDataTable(list);
    }

    /**
     * 查询文件包列表（按reportId分组）
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:list')")
    @GetMapping("/packages")
    public AjaxResult listPackages(StReportPdf stReportPdf)
    {
        try
        {
            // 只查询未删除的归档记录
            if (stReportPdf.getStatus() == null)
            {
                stReportPdf.setStatus(1);
            }
            List<java.util.Map<String, Object>> packages = reportPdfService.selectPackageList(stReportPdf);
            return success(packages);
        }
        catch (Exception e)
        {
            return error("查询文件包列表失败：" + e.getMessage());
        }
    }

    /**
     * 查询可用报表列表（用于合并PDF）
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:list')")
    @GetMapping("/availableReports")
    public AjaxResult listAvailableReports(StReportPdf stReportPdf)
    {
        try
        {
            // 只查询未删除的归档记录
            if (stReportPdf.getStatus() == null)
            {
                stReportPdf.setStatus(1);
            }
            List<java.util.Map<String, Object>> reports = reportPdfService.selectAvailableReports(stReportPdf);
            return success(reports);
        }
        catch (Exception e)
        {
            return error("查询可用报表列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取报表PDF记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(reportPdfService.selectStReportPdfById(id));
    }

    /**
     * 更新报表PDF记录
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:edit')")
    @Log(title = "更新报表PDF", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody StReportPdf stReportPdf)
    {
        return toAjax(reportPdfService.updateStReportPdf(stReportPdf));
    }

    /**
     * 生成并保存单个学生报表PDF
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:generate')")
    @Log(title = "报表PDF生成", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody GeneratePdfRequest request)
    {
        try
        {
            StReportPdf pdf = reportPdfService.generateAndSavePdf(
                request.getStudentId(), 
                request.getReportId(), 
                request.getYearSemesterId()
            );
            return success(pdf);
        }
        catch (Exception e)
        {
            return error("PDF生成失败：" + e.getMessage());
        }
    }

    /**
     * 批量生成并保存学生报表PDF
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:batchGenerate')")
    @Log(title = "批量生成报表PDF", businessType = BusinessType.INSERT)
    @PostMapping("/batchGenerate")
    public AjaxResult batchGenerate(@RequestBody BatchGeneratePdfRequest request)
    {
        try
        {
            StReportPdfBatch batch = reportPdfService.batchGenerateAndSavePdf(
                request.getStudentIds(),
                request.getReportId(),
                request.getYearSemesterId(),
                request.getBatchName(),
                request.getSchoolingPlanId(),
                request.getStudentName(),
                request.getGradeId(),
                request.getClassId()
            );
            return success(batch);
        }
        catch (Exception e)
        {
            return error("批量生成PDF失败：" + e.getMessage());
        }
    }

    /**
     * 合并PDF文件
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:merge')")
    @Log(title = "合并报表PDF", businessType = BusinessType.UPDATE)
    @PostMapping("/merge")
    public AjaxResult merge(@RequestBody MergePdfRequest request)
    {
        try
        {
            StReportPdfBatch batch = reportPdfService.mergePdfs(
                request.getPdfIds(),
                request.getMergedFileName()
            );
            return success(batch);
        }
        catch (Exception e)
        {
            return error("PDF合并失败：" + e.getMessage());
        }
    }

    /**
     * 按条件批量合并PDF
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:merge')")
    @Log(title = "按条件合并报表PDF", businessType = BusinessType.UPDATE)
    @PostMapping("/mergeByCondition")
    public AjaxResult mergeByCondition(@RequestBody MergeByConditionRequest request)
    {
        try
        {
            StReportPdfBatch batch = reportPdfService.mergePdfsByCondition(
                request.getReportId(),
                request.getYearSemesterId(),
                request.getSchoolingPlanId(),
                request.getGradeId(),
                request.getClassId(),
                request.getMergedFileName()
            );
            return success(batch);
        }
        catch (Exception e)
        {
            return error("PDF合并失败：" + e.getMessage());
        }
    }

    /**
     * 下载PDF文件
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:download')")
    @GetMapping("/download/{id}")
    public void download(@PathVariable("id") Long id, HttpServletResponse response)
    {
        try
        {
            StReportPdf pdf = reportPdfService.selectStReportPdfById(id);
            if (pdf == null)
            {
                throw new RuntimeException("PDF记录不存在");
            }

            if (pdf.getStatus() == 0)
            {
                throw new RuntimeException("PDF文件已删除");
            }

            String filePath = RuoYiConfig.getProfile() + pdf.getFilePath();
            File file = new File(filePath);
            if (!file.exists())
            {
                throw new RuntimeException("PDF文件不存在：" + filePath);
            }

            // 设置响应头
            response.setContentType("application/pdf");
            try
            {
                FileUtils.setAttachmentResponseHeader(response, pdf.getFileName());
            }
            catch (UnsupportedEncodingException e)
            {
                logger.warn("设置响应头失败，使用默认方式", e);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + pdf.getFileName() + "\"");
            }
            
            // 写入文件流
            FileUtils.writeBytes(filePath, response.getOutputStream());
        }
        catch (Exception e)
        {
            logger.error("下载PDF失败：id={}", id, e);
            try
            {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("下载失败：" + e.getMessage());
            }
            catch (Exception ex)
            {
                logger.error("写入错误响应失败", ex);
            }
        }
    }

    /**
     * 删除报表PDF记录
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:remove')")
    @Log(title = "删除报表PDF", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(reportPdfService.deleteStReportPdfByIds(ids));
    }

    /**
     * 删除文件包（按报表ID删除该报表的所有PDF归档）
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:remove')")
    @Log(title = "删除文件包", businessType = BusinessType.DELETE)
    @DeleteMapping("/package/{reportId}")
    public AjaxResult removePackage(@PathVariable String reportId)
    {
        try
        {
            int count = reportPdfService.deletePackageByReportId(reportId);
            return success("删除成功，共删除 " + count + " 个文件");
        }
        catch (Exception e)
        {
            return error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 查询PDF合并批次列表
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:batchList')")
    @GetMapping("/batch/list")
    public TableDataInfo batchList(StReportPdfBatch stReportPdfBatch)
    {
        startPage();
        List<StReportPdfBatch> list = reportPdfService.selectStReportPdfBatchList(stReportPdfBatch);
        return getDataTable(list);
    }

    /**
     * 获取PDF合并批次详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:query')")
    @GetMapping("/batch/{id}")
    public AjaxResult getBatchInfo(@PathVariable("id") Long id)
    {
        return success(reportPdfService.selectStReportPdfBatchById(id));
    }

    /**
     * 下载合并后的PDF文件
     */
    @PreAuthorize("@ss.hasPermi('system:reportPdf:download')")
    @GetMapping("/batch/download/{id}")
    public void downloadBatch(@PathVariable("id") Long id, HttpServletResponse response)
    {
        try
        {
            StReportPdfBatch batch = reportPdfService.selectStReportPdfBatchById(id);
            if (batch == null)
            {
                throw new RuntimeException("合并批次不存在");
            }

            String filePath = RuoYiConfig.getProfile() + batch.getMergedFilePath();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + batch.getMergedFileName() + "\"");
            FileUtils.writeBytes(filePath, response.getOutputStream());
        }
        catch (Exception e)
        {
            logger.error("下载合并PDF失败", e);
        }
    }

    /**
     * 生成PDF请求参数
     */
    public static class GeneratePdfRequest
    {
        private Long studentId;
        private String reportId;
        private Long yearSemesterId;

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        public Long getYearSemesterId() { return yearSemesterId; }
        public void setYearSemesterId(Long yearSemesterId) { this.yearSemesterId = yearSemesterId; }
    }

    /**
     * 批量生成PDF请求参数
     */
    public static class BatchGeneratePdfRequest
    {
        private List<Long> studentIds;
        private String reportId;
        private Long yearSemesterId;
        private String batchName;
        private Long schoolingPlanId;
        private String studentName;
        private Long gradeId;
        private Long classId;

        public List<Long> getStudentIds() { return studentIds; }
        public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }
        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        public Long getYearSemesterId() { return yearSemesterId; }
        public void setYearSemesterId(Long yearSemesterId) { this.yearSemesterId = yearSemesterId; }
        public String getBatchName() { return batchName; }
        public void setBatchName(String batchName) { this.batchName = batchName; }
        public Long getSchoolingPlanId() { return schoolingPlanId; }
        public void setSchoolingPlanId(Long schoolingPlanId) { this.schoolingPlanId = schoolingPlanId; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public Long getGradeId() { return gradeId; }
        public void setGradeId(Long gradeId) { this.gradeId = gradeId; }
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
    }

    /**
     * 合并PDF请求参数
     */
    public static class MergePdfRequest
    {
        private List<Long> pdfIds;
        private String mergedFileName;

        public List<Long> getPdfIds() { return pdfIds; }
        public void setPdfIds(List<Long> pdfIds) { this.pdfIds = pdfIds; }
        public String getMergedFileName() { return mergedFileName; }
        public void setMergedFileName(String mergedFileName) { this.mergedFileName = mergedFileName; }
    }

    /**
     * 按条件合并PDF请求参数
     */
    public static class MergeByConditionRequest
    {
        private String reportId;
        private Long yearSemesterId;
        private Long schoolingPlanId;
        private Long gradeId;
        private Long classId;
        private String mergedFileName;

        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        public Long getYearSemesterId() { return yearSemesterId; }
        public void setYearSemesterId(Long yearSemesterId) { this.yearSemesterId = yearSemesterId; }
        public Long getSchoolingPlanId() { return schoolingPlanId; }
        public void setSchoolingPlanId(Long schoolingPlanId) { this.schoolingPlanId = schoolingPlanId; }
        public Long getGradeId() { return gradeId; }
        public void setGradeId(Long gradeId) { this.gradeId = gradeId; }
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        public String getMergedFileName() { return mergedFileName; }
        public void setMergedFileName(String mergedFileName) { this.mergedFileName = mergedFileName; }
    }
}

