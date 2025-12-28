package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.application.command.ApproveSubsidyCommand;
import com.ruoyi.system.application.command.CreateSubsidyCommand;
import com.ruoyi.system.application.query.BudgetQueryService;
import com.ruoyi.system.application.service.SubsidyApplicationService;
import com.ruoyi.system.domain.StSubsidyPackage;
import com.ruoyi.system.domain.vo.BudgetVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 补助录入控制器
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
@RestController
@RequestMapping("/system/subsidy")
public class SubsidyController extends BaseController {
    
    @Autowired
    private SubsidyApplicationService applicationService;
    @Autowired
    private BudgetQueryService budgetQueryService;
    
    /**
     * 录入补助
     */
    @PreAuthorize("@ss.hasPermi('system:subsidy:create')")
    @Log(title = "补助录入", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult createSubsidy(@RequestBody CreateSubsidyCommand command) {
        List<Long> recordIds = applicationService.createSubsidy(command);
        return success(recordIds);
    }
    
    /**
     * 审批补助
     */
    @PreAuthorize("@ss.hasPermi('system:subsidy:approve')")
    @Log(title = "补助审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approve")
    public AjaxResult approveSubsidy(@RequestBody ApproveSubsidyCommand command) {
        applicationService.approveSubsidy(command);
        return success();
    }
    
    /**
     * 查询可用套餐
     * 根据学制ID查询可用套餐，可选根据经济分类过滤
     * 
     * @param schoolingPlanId 学制ID（必填）
     * @param economyCategory 经济分类（可选），如果提供则只返回该经济分类的套餐
     */
    @PreAuthorize("@ss.hasPermi('system:subsidy:query')")
    @GetMapping("/packages")
    public AjaxResult getAvailablePackages(
            @RequestParam Long schoolingPlanId,
            @RequestParam(required = false) String economyCategory) {
        List<StSubsidyPackage> packages = applicationService.getAvailablePackages(schoolingPlanId, economyCategory);
        return success(packages);
    }

    /**
     * 查询当前学期可用预算（增强版）
     * 
     * @param currentSemesterId 当前学期ID
     * @param economyCategory 经济分类（从套餐获取）
     * @param functionCategory 功能分类（从学生学制获取）
     * @return 预算列表（包含指标信息）
     */
    @PreAuthorize("@ss.hasPermi('system:subsidy:query')")
    @GetMapping("/budgets")
    public AjaxResult getAvailableBudgets(@RequestParam Long currentSemesterId,
                                          @RequestParam(required = false) String economyCategory,
                                          @RequestParam(required = false) String functionCategory) {
        List<BudgetVO> budgets = budgetQueryService.getAvailableBudgets(currentSemesterId, economyCategory, functionCategory);
        return success(budgets);
    }

    /**
     * 查询历史结余预算（增强版）
     * 
     * @param currentSemesterId 当前学期ID
     * @param economyCategory 经济分类（从套餐获取）
     * @param functionCategory 功能分类（从学生学制获取）
     * @return 预算列表（包含指标信息）
     */
    @PreAuthorize("@ss.hasPermi('system:subsidy:query')")
    @GetMapping("/budgets/history")
    public AjaxResult getHistoricalBudgets(@RequestParam Long currentSemesterId,
                                           @RequestParam(required = false) String economyCategory,
                                           @RequestParam(required = false) String functionCategory) {
        List<BudgetVO> budgets = budgetQueryService.getHistoricalSurplus(currentSemesterId, economyCategory, functionCategory);
        return success(budgets);
    }
}

