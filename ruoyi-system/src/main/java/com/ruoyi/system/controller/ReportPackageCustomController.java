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
import com.ruoyi.system.domain.StReportPackageCustom;
import com.ruoyi.system.service.IReportPackageCustomService;

/**
 * 自定义档案包Controller
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
@RestController
@RequestMapping("/system/report/packageCustom")
public class ReportPackageCustomController extends BaseController
{
    @Autowired
    private IReportPackageCustomService reportPackageCustomService;

    /**
     * 查询自定义档案包列表
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackageCustom:list')")
    @GetMapping("/list")
    public TableDataInfo list(StReportPackageCustom stReportPackageCustom)
    {
        startPage();
        List<StReportPackageCustom> list = reportPackageCustomService.selectStReportPackageCustomList(stReportPackageCustom);
        return getDataTable(list);
    }

    /**
     * 获取自定义档案包详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackageCustom:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(reportPackageCustomService.selectStReportPackageCustomById(id));
    }

    /**
     * 根据档案包编码获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackageCustom:query')")
    @GetMapping(value = "/code/{packageCode}")
    public AjaxResult getInfoByCode(@PathVariable("packageCode") String packageCode)
    {
        return success(reportPackageCustomService.selectStReportPackageCustomByCode(packageCode));
    }

    /**
     * 创建自定义档案包
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackageCustom:add')")
    @Log(title = "创建自定义档案包", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult create(@RequestBody CreateCustomPackageRequest request)
    {
        try
        {
            StReportPackageCustom pkg = reportPackageCustomService.createCustomPackage(
                request.getPackageName(),
                request.getDescription()
            );
            return success(pkg);
        }
        catch (Exception e)
        {
            logger.error("创建自定义档案包失败", e);
            return error("创建失败：" + e.getMessage());
        }
    }

    /**
     * 新增自定义档案包
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackageCustom:add')")
    @Log(title = "自定义档案包", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StReportPackageCustom stReportPackageCustom)
    {
        return toAjax(reportPackageCustomService.insertStReportPackageCustom(stReportPackageCustom));
    }

    /**
     * 修改自定义档案包
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackageCustom:edit')")
    @Log(title = "自定义档案包", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StReportPackageCustom stReportPackageCustom)
    {
        return toAjax(reportPackageCustomService.updateStReportPackageCustom(stReportPackageCustom));
    }

    /**
     * 删除自定义档案包
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackageCustom:remove')")
    @Log(title = "自定义档案包", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(reportPackageCustomService.deleteStReportPackageCustomByIds(ids));
    }

    /**
     * 更新档案包统计信息
     */
    @PreAuthorize("@ss.hasPermi('system:reportPackageCustom:edit')")
    @Log(title = "更新档案包统计", businessType = BusinessType.UPDATE)
    @PutMapping("/updateStatistics/{packageCode}")
    public AjaxResult updateStatistics(@PathVariable String packageCode)
    {
        try
        {
            reportPackageCustomService.updateFileStatistics(packageCode);
            return success();
        }
        catch (Exception e)
        {
            logger.error("更新统计信息失败", e);
            return error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 创建自定义档案包请求对象
     */
    public static class CreateCustomPackageRequest
    {
        private String packageName;
        private String description;

        public String getPackageName()
        {
            return packageName;
        }

        public void setPackageName(String packageName)
        {
            this.packageName = packageName;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }
    }
}
