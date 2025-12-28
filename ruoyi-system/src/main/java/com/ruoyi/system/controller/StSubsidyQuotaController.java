package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
import com.ruoyi.system.domain.StSubsidyQuota;
import com.ruoyi.system.domain.StSubsidyQuotaDetail;
import com.ruoyi.system.domain.dto.BatchQuotaAllocationRequest;
import com.ruoyi.system.domain.dto.QuotaAllocationRequest;
import com.ruoyi.system.domain.vo.QuotaStatisticVO;
import com.ruoyi.system.service.IStSubsidyQuotaService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 资助指标下达Controller
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
@RestController
@RequestMapping("/system/subsidyQuota")
public class StSubsidyQuotaController extends BaseController
{
    @Autowired
    private IStSubsidyQuotaService stSubsidyQuotaService;

    /**
     * 查询资助指标下达列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:list')")
    @GetMapping("/list")
    public TableDataInfo list(StSubsidyQuota stSubsidyQuota)
    {
        startPage();
        List<StSubsidyQuota> list = stSubsidyQuotaService.selectStSubsidyQuotaList(stSubsidyQuota);
        return getDataTable(list);
    }

    /**
     * 查询资助指标使用情况列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:list')")
    @GetMapping("/listWithUsage")
    public TableDataInfo listWithUsage(StSubsidyQuota stSubsidyQuota)
    {
        startPage();
        List<StSubsidyQuota> list = stSubsidyQuotaService.selectStSubsidyQuotaListWithUsage(stSubsidyQuota);
        return getDataTable(list);
    }

    /**
     * 导出资助指标下达列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:export')")
    @Log(title = "资助指标下达", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StSubsidyQuota stSubsidyQuota)
    {
        List<StSubsidyQuota> list = stSubsidyQuotaService.selectStSubsidyQuotaListWithUsage(stSubsidyQuota);
        ExcelUtil<StSubsidyQuota> util = new ExcelUtil<StSubsidyQuota>(StSubsidyQuota.class);
        util.exportExcel(response, list, "资助指标下达数据");
    }

    /**
     * 获取资助指标下达详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stSubsidyQuotaService.selectStSubsidyQuotaById(id));
    }

    /**
     * 新增资助指标下达
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:add')")
    @Log(title = "资助指标下达", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StSubsidyQuota stSubsidyQuota)
    {
        return toAjax(stSubsidyQuotaService.insertStSubsidyQuota(stSubsidyQuota));
    }

    /**
     * 修改资助指标下达
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:edit')")
    @Log(title = "资助指标下达", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StSubsidyQuota stSubsidyQuota)
    {
        return toAjax(stSubsidyQuotaService.updateStSubsidyQuota(stSubsidyQuota));
    }

    /**
     * 删除资助指标下达
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:remove')")
    @Log(title = "资助指标下达", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stSubsidyQuotaService.deleteStSubsidyQuotaByIds(ids));
    }
    
    /**
     * 修复指标已分配金额（根据学期预算重新计算）
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:edit')")
    @Log(title = "修复指标已分配金额", businessType = BusinessType.UPDATE)
    @PutMapping("/fixAllocatedAmount")
    public AjaxResult fixAllocatedAmount()
    {
        return toAjax(stSubsidyQuotaService.fixAllocatedAmount());
    }

    /**
     * 查询可分配的指标明细
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:query')")
    @GetMapping("/availableDetails")
    public AjaxResult availableDetails(StSubsidyQuotaDetail detail)
    {
        return success(stSubsidyQuotaService.listAllocatableDetails(detail));
    }

    /**
     * 指标额度下达到学期预算
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:allocate')")
    @Log(title = "指标额度分配", businessType = BusinessType.INSERT)
    @PostMapping("/allocate")
    public AjaxResult allocate(@Validated @RequestBody QuotaAllocationRequest request)
    {
        Long budgetId = stSubsidyQuotaService.allocateQuota(request);
        return success(budgetId);
    }

    /**
     * 指标额度批量下达到学期预算
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:allocate')")
    @Log(title = "指标额度批量分配", businessType = BusinessType.INSERT)
    @PostMapping("/allocate/batch")
    public AjaxResult allocateBatch(@RequestBody BatchQuotaAllocationRequest request)
    {
        List<Long> budgetIds = stSubsidyQuotaService.allocateQuotaBatch(request);
        return success(budgetIds);
    }

    /**
     * 查询指标统计信息
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:list')")
    @GetMapping("/statistics")
    public AjaxResult statistics(StSubsidyQuota stSubsidyQuota)
    {
        QuotaStatisticVO statistics = stSubsidyQuotaService.statistics(stSubsidyQuota);
        return success(statistics);
    }
    
    /**
     * 查询指标已分配的预算列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:query')")
    @GetMapping("/{quotaId}/budgets")
    public AjaxResult listAllocatedBudgets(@PathVariable("quotaId") Long quotaId)
    {
        return success(stSubsidyQuotaService.listAllocatedBudgets(quotaId));
    }
    
    /**
     * 收回已分配的预算
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:reclaim')")
    @Log(title = "收回已分配预算", businessType = BusinessType.DELETE)
    @PostMapping("/reclaimBudgets")
    public AjaxResult reclaimBudgets(@RequestBody Long[] budgetIds)
    {
        int count = stSubsidyQuotaService.reclaimBudgets(budgetIds);
        return success(count);
    }
    
    /**
     * 将历史学期指标的剩余额度批量结转到当前学期预算
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:carryover')")
    @Log(title = "指标结转到预算", businessType = BusinessType.INSERT)
    @PostMapping("/carryOverToBudget")
    public AjaxResult carryOverToBudget(@Validated @RequestBody com.ruoyi.system.domain.dto.CarryOverToBudgetRequest request)
    {
        List<Long> budgetIds = stSubsidyQuotaService.carryOverToBudget(request);
        return success(budgetIds);
    }
    
    /**
     * 根据学年学期ID和功能分类查询指标文号列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:query')")
    @GetMapping("/quotaDocNos")
    public AjaxResult getQuotaDocNos(Long yearSemesterId, String functionCategory)
    {
        List<String> docNos = stSubsidyQuotaService.getQuotaDocNosByYearSemesterAndFunction(yearSemesterId, functionCategory);
        return success(docNos);
    }
    
    /**
     * 将来源指标的资金结转到目标指标
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyQuota:carryover')")
    @Log(title = "指标结转到目标指标", businessType = BusinessType.UPDATE)
    @PostMapping("/carryOverToTargetQuota")
    public AjaxResult carryOverToTargetQuota(@Validated @RequestBody com.ruoyi.system.domain.dto.CarryOverToTargetQuotaRequest request)
    {
        Long targetQuotaId = stSubsidyQuotaService.carryOverToTargetQuota(request);
        return success(targetQuotaId);
    }
}
