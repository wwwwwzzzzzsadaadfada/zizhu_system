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
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.StSubsidyQuotaDetail;
import com.ruoyi.system.service.IStSubsidyQuotaDetailService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 资助指标经济分类明细Controller
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
@RestController
@RequestMapping("/system/subsidyQuotaDetail")
public class StSubsidyQuotaDetailController extends BaseController
{
    @Autowired
    private IStSubsidyQuotaDetailService stSubsidyQuotaDetailService;

    /**
     * 查询资助指标经济分类明细列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:list')")
    @GetMapping("/list")
    public TableDataInfo list(StSubsidyQuotaDetail stSubsidyQuotaDetail)
    {
        startPage();
        List<StSubsidyQuotaDetail> list = stSubsidyQuotaDetailService.selectStSubsidyQuotaDetailList(stSubsidyQuotaDetail);
        return getDataTable(list);
    }

    /**
     * 根据指标ID查询明细列表（不分页）
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:query')")
    @GetMapping("/listByQuotaId/{quotaId}")
    public AjaxResult listByQuotaId(@PathVariable("quotaId") Long quotaId)
    {
        List<StSubsidyQuotaDetail> list = stSubsidyQuotaDetailService.selectDetailListByQuotaId(quotaId);
        return success(list);
    }

    /**
     * 导出资助指标经济分类明细列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:export')")
    @Log(title = "资助指标经济分类明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StSubsidyQuotaDetail stSubsidyQuotaDetail)
    {
        List<StSubsidyQuotaDetail> list = stSubsidyQuotaDetailService.selectStSubsidyQuotaDetailList(stSubsidyQuotaDetail);
        ExcelUtil<StSubsidyQuotaDetail> util = new ExcelUtil<StSubsidyQuotaDetail>(StSubsidyQuotaDetail.class);
        util.exportExcel(response, list, "资助指标经济分类明细数据");
    }

    /**
     * 获取资助指标经济分类明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stSubsidyQuotaDetailService.selectStSubsidyQuotaDetailById(id));
    }

    /**
     * 新增资助指标经济分类明细
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:add')")
    @Log(title = "资助指标经济分类明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StSubsidyQuotaDetail stSubsidyQuotaDetail)
    {
        return toAjax(stSubsidyQuotaDetailService.insertStSubsidyQuotaDetail(stSubsidyQuotaDetail));
    }

    /**
     * 修改资助指标经济分类明细
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:edit')")
    @Log(title = "资助指标经济分类明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StSubsidyQuotaDetail stSubsidyQuotaDetail)
    {
        return toAjax(stSubsidyQuotaDetailService.updateStSubsidyQuotaDetail(stSubsidyQuotaDetail));
    }

    /**
     * 删除资助指标经济分类明细
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:remove')")
    @Log(title = "资助指标经济分类明细", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stSubsidyQuotaDetailService.deleteStSubsidyQuotaDetailByIds(ids));
    }
}
