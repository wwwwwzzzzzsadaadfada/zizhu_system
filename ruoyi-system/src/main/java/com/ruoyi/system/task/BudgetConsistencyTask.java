package com.ruoyi.system.task;

import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.ruoyi.system.domain.StSemesterBudget;
import com.ruoyi.system.mapper.StSemesterBudgetMapper;
import com.ruoyi.system.mapper.StSubsidyQuotaMapper;
import com.ruoyi.system.service.IStSemesterBudgetService;

/**
 * 预算数据一致性修复任务
 * 定期检查并修复预算表与指标表之间的数据不一致问题
 * 增强版：增加预算表内部数据一致性校验
 */
@Component
public class BudgetConsistencyTask {
    
    private static final Logger log = LoggerFactory.getLogger(BudgetConsistencyTask.class);
    
    @Autowired
    private StSubsidyQuotaMapper stSubsidyQuotaMapper;
    
    @Autowired
    private IStSemesterBudgetService stSemesterBudgetService;
    
    @Autowired
    private StSemesterBudgetMapper stSemesterBudgetMapper;
    
    /**
     * 每天凌晨2点执行数据一致性检查和修复
     * 修复指标表中的已分配金额与学期预算表中的预算总额不一致的问题
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void fixBudgetConsistency() {
        log.info("开始执行预算数据一致性修复任务");
        try {
            // 1. 修复指标表中的已分配金额
            int fixedCount = stSubsidyQuotaMapper.fixAllocatedAmount();
            log.info("成功修复 {} 条指标数据的已分配金额", fixedCount);
            
            // 2. 校准预算表中的已使用金额
            stSemesterBudgetService.calibrateBudgetConsistency();
            log.info("成功校准预算表中的已使用金额");
            
            // 3. 校验并修复预算表内部数据一致性
            validateAndFixBudgetData();
            
            log.info("预算数据一致性修复任务执行完成");
        } catch (Exception e) {
            log.error("执行预算数据一致性修复任务时发生错误", e);
        }
    }
    
    /**
     * 校验并修复预算表内部数据一致性
     * 检查项：
     * 1. 负数金额（locked_amount, used_amount）
     * 2. 锁定金额与实际未审批记录是否一致
     * 3. 已使用金额与实际已审批记录是否一致
     * 4. 可用金额计算是否正确
     * 5. 预算状态是否合理
     */
    private void validateAndFixBudgetData() {
        log.info("开始校验预算表内部数据一致性");
        
        List<Long> budgetIds = stSemesterBudgetMapper.selectAllBudgetIds();
        if (budgetIds == null || budgetIds.isEmpty()) {
            log.info("没有需要校验的预算记录");
            return;
        }
        
        int fixedNegativeCount = 0;
        int fixedLockedAmountCount = 0;
        int fixedUsedAmountCount = 0;
        int fixedStatusCount = 0;
        int totalChecked = 0;
        
        for (Long budgetId : budgetIds) {
            try {
                StSemesterBudget budget = stSemesterBudgetMapper.selectStSemesterBudgetById(budgetId);
                if (budget == null) {
                    continue;
                }
                
                totalChecked++;
                boolean needUpdate = false;
                
                // 1. 检查并修复负数金额
                BigDecimal lockedAmount = budget.getLockedAmount() != null ? budget.getLockedAmount() : BigDecimal.ZERO;
                BigDecimal usedAmount = budget.getUsedAmount() != null ? budget.getUsedAmount() : BigDecimal.ZERO;
                BigDecimal budgetAmount = budget.getBudgetAmount() != null ? budget.getBudgetAmount() : BigDecimal.ZERO;
                
                if (lockedAmount.compareTo(BigDecimal.ZERO) < 0) {
                    log.warn("检测到预算ID={}的锁定金额为负数：{}，将修复为0", budgetId, lockedAmount);
                    lockedAmount = BigDecimal.ZERO;
                    budget.setLockedAmount(BigDecimal.ZERO);
                    needUpdate = true;
                    fixedNegativeCount++;
                }
                
                if (usedAmount.compareTo(BigDecimal.ZERO) < 0) {
                    log.warn("检测到预算ID={}的已使用金额为负数：{}，将修复为0", budgetId, usedAmount);
                    usedAmount = BigDecimal.ZERO;
                    budget.setUsedAmount(BigDecimal.ZERO);
                    needUpdate = true;
                    fixedNegativeCount++;
                }
                
                // 2. 检查锁定金额与实际未审批记录是否一致
                BigDecimal actualLockedAmount = stSemesterBudgetMapper.calculateActualLockedAmount(budgetId);
                if (actualLockedAmount == null) {
                    actualLockedAmount = BigDecimal.ZERO;
                }
                
                if (lockedAmount.compareTo(actualLockedAmount) != 0) {
                    log.warn("预算ID={}的锁定金额不一致：记录值={}，实际值={}，将修复", 
                        budgetId, lockedAmount, actualLockedAmount);
                    budget.setLockedAmount(actualLockedAmount);
                    lockedAmount = actualLockedAmount;
                    needUpdate = true;
                    fixedLockedAmountCount++;
                }
                
                // 3. 检查已使用金额与实际已审批记录是否一致
                BigDecimal actualUsedAmount = stSemesterBudgetMapper.calculateActualUsedAmount(budgetId);
                if (actualUsedAmount == null) {
                    actualUsedAmount = BigDecimal.ZERO;
                }
                
                if (usedAmount.compareTo(actualUsedAmount) != 0) {
                    log.warn("预算ID={}的已使用金额不一致：记录值={}，实际值={}，将修复", 
                        budgetId, usedAmount, actualUsedAmount);
                    budget.setUsedAmount(actualUsedAmount);
                    usedAmount = actualUsedAmount;
                    needUpdate = true;
                    fixedUsedAmountCount++;
                }
                
                // 4. 检查预算状态是否合理
                Integer status = budget.getStatus();
                if (status != null && status != 4) { // 4=作废，不检查
                    BigDecimal available = budgetAmount.subtract(usedAmount).subtract(lockedAmount);
                    
                    // 如果可用金额 <= 0，状态应该是"已用完"(3)
                    if (available.compareTo(BigDecimal.ZERO) <= 0 && status != 3) {
                        log.warn("预算ID={}的可用金额<=0，但状态不是'已用完'，当前状态={}，将更新为'已用完'", 
                            budgetId, status);
                        budget.setStatus(3);
                        needUpdate = true;
                        fixedStatusCount++;
                    }
                    // 如果可用金额 > 0，状态不应该是"已用完"(3)
                    else if (available.compareTo(BigDecimal.ZERO) > 0 && status == 3) {
                        log.warn("预算ID={}的可用金额>0，但状态是'已用完'，将更新为'启用'", budgetId);
                        budget.setStatus(1); // 1=启用
                        needUpdate = true;
                        fixedStatusCount++;
                    }
                }
                
                // 5. 执行更新
                if (needUpdate) {
                    stSemesterBudgetMapper.updateStSemesterBudget(budget);
                }
                
            } catch (Exception e) {
                log.error("校验预算ID={}时发生错误", budgetId, e);
            }
        }
        
        log.info("预算表内部数据一致性校验完成，共检查{}条记录，修复负数金额{}条，修复锁定金额{}条，修复已使用金额{}条，修复状态{}条", 
            totalChecked, fixedNegativeCount, fixedLockedAmountCount, fixedUsedAmountCount, fixedStatusCount);
    }
}