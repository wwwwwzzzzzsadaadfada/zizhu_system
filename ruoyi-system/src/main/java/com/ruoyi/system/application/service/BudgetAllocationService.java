package com.ruoyi.system.application.service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.StBudgetAllocationDetail;
import com.ruoyi.system.domain.StSemesterBudget;
import com.ruoyi.system.mapper.StBudgetAllocationDetailMapper;
import com.ruoyi.system.mapper.StSemesterBudgetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * 预算分配服务
 * 支持从多个同组预算中智能分配金额
 * 
 * @author ruoyi
 * @date 2025-12-15
 */
@Service
public class BudgetAllocationService {
    
    private static final Logger log = LoggerFactory.getLogger(BudgetAllocationService.class);
    
    @Autowired
    private StSemesterBudgetMapper budgetMapper;
    
    @Autowired
    private StBudgetAllocationDetailMapper allocationDetailMapper;
    
    /**
     * 预算分配结果
     */
    public static class AllocationResult {
        /** 主预算ID（用户选择的预算） */
        private Long primaryBudgetId;
        
        /** 分配明细列表 */
        private List<StBudgetAllocationDetail> allocationDetails;
        
        /** 是否使用了多个预算 */
        private boolean multipleBudgetsUsed;
        
        public AllocationResult(Long primaryBudgetId, List<StBudgetAllocationDetail> allocationDetails) {
            this.primaryBudgetId = primaryBudgetId;
            this.allocationDetails = allocationDetails;
            this.multipleBudgetsUsed = allocationDetails != null && allocationDetails.size() > 1;
        }
        
        public Long getPrimaryBudgetId() {
            return primaryBudgetId;
        }
        
        public List<StBudgetAllocationDetail> getAllocationDetails() {
            return allocationDetails;
        }
        
        public boolean isMultipleBudgetsUsed() {
            return multipleBudgetsUsed;
        }
    }
    
    /**
     * 从预算组中智能分配金额（使用用户选择的预算列表）
     * 优先使用历史结余资金，不足时从本期预算补充
     * 
     * @param selectedBudgetIds 用户选择的预算ID列表
     * @param requiredAmount 需要分配的金额
     * @param currentSemesterId 当前学期ID
     * @return 分配结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AllocationResult allocateFromSelectedBudgets(List<Long> selectedBudgetIds, BigDecimal requiredAmount, Long currentSemesterId) {
        if (selectedBudgetIds == null || selectedBudgetIds.isEmpty()) {
            throw new ServiceException("预算ID列表不能为空");
        }
        
        // 去重
        selectedBudgetIds = new ArrayList<>(new LinkedHashSet<>(selectedBudgetIds));
        Long primaryBudgetId = selectedBudgetIds.get(0); // 第一个作为主预算（用于记录）
        
        log.info("BudgetAllocationService: 开始分配预算，用户选择预算数量={}，预算IDs={}，需要金额={}，当前学期ID={}", 
            selectedBudgetIds.size(), selectedBudgetIds, requiredAmount, currentSemesterId);
        
        // 1. 批量锁定所有用户选择的预算（按ID排序避免死锁）
        selectedBudgetIds.sort(Long::compareTo);
        List<StSemesterBudget> lockedBudgets = budgetMapper.selectBudgetsByIdsForUpdate(selectedBudgetIds);
        
        if (lockedBudgets.isEmpty()) {
            throw new ServiceException("无法锁定任何预算，请检查预算是否存在");
        }
        
        // 2. 验证所有预算状态（排除作废状态）
        List<StSemesterBudget> validBudgets = new ArrayList<>();
        for (StSemesterBudget budget : lockedBudgets) {
            if (budget.getStatus() != null && budget.getStatus() == 4) {
                log.warn("BudgetAllocationService: 预算ID={}已作废，跳过", budget.getId());
                continue;
            }
            validBudgets.add(budget);
        }
        
        if (validBudgets.isEmpty()) {
            throw new ServiceException("所有选择的预算都已作废，无法使用");
        }
        
        // 3. 验证所有预算是否为当前学期（非当前学期预算不允许直接分配，只能结转给当前学期）
        if (currentSemesterId == null) {
            throw new ServiceException("无法获取当前学期ID，无法验证预算学期");
        }
        for (StSemesterBudget budget : validBudgets) {
            Long budgetSemesterId = budget.getYearSemesterId();
            if (budgetSemesterId == null || !budgetSemesterId.equals(currentSemesterId)) {
                throw new ServiceException(String.format("预算ID=%d不属于当前学期（学期ID=%s），非当前学期预算不允许直接分配，请先结转到当前学期", 
                    budget.getId(), budgetSemesterId));
            }
        }
        
        // 4. 验证主预算是否在有效预算列表中
        boolean primaryBudgetValid = validBudgets.stream()
            .anyMatch(b -> b.getId().equals(primaryBudgetId));
        if (!primaryBudgetValid) {
            throw new ServiceException("主预算不存在或已作废");
        }
        
        // 5. 验证所有预算的经济分类是否一致（组合使用时必须相同）
        String economyCategory = null;
        for (StSemesterBudget budget : validBudgets) {
            String budgetEconomyCategory = budget.getEconomyCategory();
            if (economyCategory == null) {
                economyCategory = budgetEconomyCategory;
            } else if (!StringUtils.equals(economyCategory, budgetEconomyCategory)) {
                throw new ServiceException(String.format("组合使用的预算经济分类必须一致，预算ID=%d的经济分类为%s，与%s不一致", 
                    budget.getId(), budgetEconomyCategory, economyCategory));
            }
        }
        
        // 6. 验证功能分类兼容性（允许小学和初中预算组合，因为都是义教阶段）
        String firstFunctionCategory = null;
        for (StSemesterBudget budget : validBudgets) {
            String budgetFunctionCategory = budget.getFunctionCategory();
            if (firstFunctionCategory == null) {
                firstFunctionCategory = budgetFunctionCategory;
            } else {
                // 允许小学（1）和初中（2）组合，其他情况必须相同
                boolean isCompatible = StringUtils.equals(firstFunctionCategory, budgetFunctionCategory) ||
                    (isCompulsoryEducationCategory(firstFunctionCategory) && isCompulsoryEducationCategory(budgetFunctionCategory));
                if (!isCompatible) {
                    throw new ServiceException(String.format("组合使用的预算功能分类不兼容，预算ID=%d的功能分类为%s，与%s不兼容（仅允许小学和初中组合）", 
                        budget.getId(), budgetFunctionCategory, firstFunctionCategory));
                }
            }
        }
        
        // 7. 按顺序分配金额（优先使用历史结余，先使用旧资金）
        List<StBudgetAllocationDetail> allocationDetails = new ArrayList<>();
        BigDecimal remainingAmount = requiredAmount;
        
        // 排序规则（优先级从高到低）：
        // 1. 第一优先级：学期ID升序（历史学期优先，学期ID越小越早）
        // 2. 第二优先级：同一学期内，按可用余额升序（先使用小余额的预算，避免大预算被小金额占用）
        // 3. 第三优先级：按创建时间升序（先创建的优先）
        // 4. 第四优先级：按预算ID升序（确保排序稳定）
        validBudgets.sort((a, b) -> {
            // 1. 第一优先级：学期ID升序（历史学期优先）
            Long aSemesterId = a.getYearSemesterId() != null ? a.getYearSemesterId() : Long.MAX_VALUE;
            Long bSemesterId = b.getYearSemesterId() != null ? b.getYearSemesterId() : Long.MAX_VALUE;
            int semesterCompare = aSemesterId.compareTo(bSemesterId);
            if (semesterCompare != 0) {
                return semesterCompare; // 历史学期（学期ID小）在前
            }
            
            // 2. 第二优先级：同一学期内，按可用余额升序（先使用小余额的预算）
            // 计算可用金额（动态计算，确保准确性）
            BigDecimal aLocked = budgetMapper.calculateActualLockedAmount(a.getId());
            if (aLocked == null) aLocked = BigDecimal.ZERO;
            BigDecimal aUsed = budgetMapper.calculateActualUsedAmount(a.getId());
            if (aUsed == null) aUsed = BigDecimal.ZERO;
            BigDecimal aAvailable = a.getBudgetAmount().subtract(aUsed).subtract(aLocked);
            
            BigDecimal bLocked = budgetMapper.calculateActualLockedAmount(b.getId());
            if (bLocked == null) bLocked = BigDecimal.ZERO;
            BigDecimal bUsed = budgetMapper.calculateActualUsedAmount(b.getId());
            if (bUsed == null) bUsed = BigDecimal.ZERO;
            BigDecimal bAvailable = b.getBudgetAmount().subtract(bUsed).subtract(bLocked);
            
            int availableCompare = aAvailable.compareTo(bAvailable);
            if (availableCompare != 0) {
                return availableCompare; // 可用余额小的在前
            }
            
            // 3. 第三优先级：按创建时间升序（先创建的优先）
            if (a.getCreateTime() != null && b.getCreateTime() != null) {
                int timeCompare = a.getCreateTime().compareTo(b.getCreateTime());
                if (timeCompare != 0) {
                    return timeCompare;
                }
            } else if (a.getCreateTime() != null) {
                return -1; // a有创建时间，b没有，a优先
            } else if (b.getCreateTime() != null) {
                return 1; // b有创建时间，a没有，b优先
            }
            
            // 4. 第四优先级：按预算ID升序（确保排序稳定）
            return a.getId().compareTo(b.getId());
        });
        
        log.info("BudgetAllocationService: 预算排序完成，共{}个预算，排序规则：学期ID升序（历史优先），同学期内按可用余额升序（小余额优先）", validBudgets.size());
        for (StSemesterBudget budget : validBudgets) {
            log.debug("BudgetAllocationService: 预算ID={}，学期ID={}，可用金额={}", 
                budget.getId(), budget.getYearSemesterId(), 
                budget.getBudgetAmount().subtract(
                    budget.getUsedAmount() != null ? budget.getUsedAmount() : BigDecimal.ZERO
                ).subtract(
                    budget.getLockedAmount() != null ? budget.getLockedAmount() : BigDecimal.ZERO
                ));
        }
        
        // 8. 从每个预算中分配金额（按排序后的顺序，优先使用历史结余）
        for (StSemesterBudget budget : validBudgets) {
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break; // 已分配足够金额，停止分配
            }
            
            // 计算该预算的可用金额（动态计算，确保准确性）
            BigDecimal actualLockedAmount = budgetMapper.calculateActualLockedAmount(budget.getId());
            if (actualLockedAmount == null) {
                actualLockedAmount = BigDecimal.ZERO;
            }
            BigDecimal actualUsedAmount = budgetMapper.calculateActualUsedAmount(budget.getId());
            if (actualUsedAmount == null) {
                actualUsedAmount = BigDecimal.ZERO;
            }
            
            BigDecimal availableAmount = budget.getBudgetAmount()
                .subtract(actualUsedAmount)
                .subtract(actualLockedAmount);
            
            if (availableAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.debug("BudgetAllocationService: 预算ID={}无可用余额，跳过", budget.getId());
                continue; // 跳过无可用余额的预算
            }
            
            // 从该预算中分配金额（取剩余金额和可用金额的较小值）
            BigDecimal allocateFromThis = remainingAmount.min(availableAmount);
            
            // 锁定预算金额（在悲观锁保护下，确保并发安全）
            int locked = budgetMapper.lockBudgetAmount(budget.getId(), allocateFromThis);
            if (locked == 0) {
                log.error("BudgetAllocationService: 锁定预算金额失败，预算ID={}，金额={}，可用金额={}，可能余额不足或状态异常", 
                    budget.getId(), allocateFromThis, availableAmount);
                throw new ServiceException(String.format("预算余额不足，预算ID=%d，可用金额：%.2f，需要金额：%.2f", 
                    budget.getId(), availableAmount, allocateFromThis));
            }
            
            // 创建分配明细（记录从哪个预算分配了多少金额）
            StBudgetAllocationDetail detail = new StBudgetAllocationDetail();
            detail.setSubsidyRecordId(null); // 稍后设置
            detail.setBudgetId(budget.getId());
            detail.setAllocatedAmount(allocateFromThis);
            detail.setCreatedAt(new Date());
            detail.setUpdatedAt(new Date());
            allocationDetails.add(detail);
            
            remainingAmount = remainingAmount.subtract(allocateFromThis);
            
            // 记录分配日志（区分历史结余和本期预算）
            String budgetType = (budget.getYearSemesterId() != null && 
                                budget.getYearSemesterId() < currentSemesterId) 
                                ? "历史结余" : "本期预算";
            log.info("BudgetAllocationService: 从{}（预算ID={}，学期ID={}）分配金额={}，剩余金额={}", 
                budgetType, budget.getId(), budget.getYearSemesterId(), allocateFromThis, remainingAmount);
        }
        
        // 9. 验证是否分配完成
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 计算总可用金额（用于错误提示）
            BigDecimal totalAvailable = validBudgets.stream()
                .map(budget -> {
                    BigDecimal actualLocked = budgetMapper.calculateActualLockedAmount(budget.getId());
                    if (actualLocked == null) actualLocked = BigDecimal.ZERO;
                    BigDecimal actualUsed = budgetMapper.calculateActualUsedAmount(budget.getId());
                    if (actualUsed == null) actualUsed = BigDecimal.ZERO;
                    return budget.getBudgetAmount().subtract(actualUsed).subtract(actualLocked);
                })
                .filter(amount -> amount.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            log.error("BudgetAllocationService: 预算组金额不足，需要金额={}，总可用金额={}，剩余未分配金额={}", 
                requiredAmount, totalAvailable, remainingAmount);
            throw new ServiceException(String.format("预算组可用余额不足，需要金额：%.2f，总可用金额：%.2f，剩余未分配金额：%.2f", 
                requiredAmount, totalAvailable, remainingAmount));
        }
        
        // 8. 分配完成，记录分配结果
        long historyBudgetCount = allocationDetails.stream()
            .mapToLong(detail -> {
                StSemesterBudget budget = validBudgets.stream()
                    .filter(b -> b.getId().equals(detail.getBudgetId()))
                    .findFirst()
                    .orElse(null);
                if (budget != null && budget.getYearSemesterId() != null && 
                    budget.getYearSemesterId() < currentSemesterId) {
                    return 1;
                }
                return 0;
            })
            .sum();
        long currentBudgetCount = allocationDetails.size() - historyBudgetCount;
        
        log.info("BudgetAllocationService: 预算分配完成，主预算ID={}，共使用{}个预算（历史结余{}个，本期预算{}个），分配金额={}", 
            primaryBudgetId, allocationDetails.size(), historyBudgetCount, currentBudgetCount, requiredAmount);
        
        return new AllocationResult(primaryBudgetId, allocationDetails);
    }
    
    /**
     * 从预算组中智能分配金额（向后兼容方法，根据主预算查询同组预算）
     * 优先使用历史结余资金，不足时从本期预算补充
     * 
     * @param primaryBudgetId 主预算ID（用户选择的预算）
     * @param requiredAmount 需要分配的金额
     * @param currentSemesterId 当前学期ID
     * @return 分配结果
     */
    @Transactional(rollbackFor = Exception.class)
    public AllocationResult allocateFromBudgetGroup(Long primaryBudgetId, BigDecimal requiredAmount, Long currentSemesterId) {
        log.info("BudgetAllocationService: 开始分配预算，主预算ID={}，需要金额={}，当前学期ID={}", 
            primaryBudgetId, requiredAmount, currentSemesterId);
        
        // 1. 查询主预算
        StSemesterBudget primaryBudget = budgetMapper.selectStSemesterBudgetByIdForUpdate(primaryBudgetId);
        if (primaryBudget == null) {
            throw new ServiceException("主预算不存在");
        }
        
        // 2. 验证主预算状态
        if (primaryBudget.getStatus() != null && primaryBudget.getStatus() == 4) {
            throw new ServiceException("主预算已作废，无法使用");
        }
        
        // 3. 验证主预算是否为当前学期（非当前学期预算不允许直接分配，只能结转给当前学期）
        if (currentSemesterId == null) {
            throw new ServiceException("无法获取当前学期ID，无法验证预算学期");
        }
        Long primaryBudgetSemesterId = primaryBudget.getYearSemesterId();
        if (primaryBudgetSemesterId == null || !primaryBudgetSemesterId.equals(currentSemesterId)) {
            throw new ServiceException(String.format("主预算ID=%d不属于当前学期（学期ID=%s），非当前学期预算不允许直接分配，请先结转到当前学期", 
                primaryBudget.getId(), primaryBudgetSemesterId));
        }
        
        // 4. 获取主预算的quota_detail_id，用于查找同组预算
        Long quotaDetailId = primaryBudget.getQuotaDetailId();
        if (quotaDetailId == null) {
            throw new ServiceException("主预算未关联指标明细，无法组合使用");
        }
        
        // 4. 查询同组所有可用预算（已加锁）
        List<Long> budgetIdsToLock = new ArrayList<>();
        budgetIdsToLock.add(primaryBudgetId);
        
        // 查询同组其他预算
        List<StSemesterBudget> groupBudgets = budgetMapper.selectBudgetsByQuotaDetailId(quotaDetailId, currentSemesterId);
        for (StSemesterBudget budget : groupBudgets) {
            if (!budget.getId().equals(primaryBudgetId)) {
                budgetIdsToLock.add(budget.getId());
            }
        }
        
        // 5. 批量锁定所有相关预算（按ID排序避免死锁）
        budgetIdsToLock.sort(Long::compareTo);
        List<StSemesterBudget> lockedBudgets = budgetMapper.selectBudgetsByIdsForUpdate(budgetIdsToLock);
        
        // 6. 验证主预算是否在锁定列表中
        boolean primaryBudgetLocked = lockedBudgets.stream()
            .anyMatch(b -> b.getId().equals(primaryBudgetId));
        if (!primaryBudgetLocked) {
            throw new ServiceException("无法锁定主预算");
        }
        
        // 7. 验证所有预算是否为当前学期（非当前学期预算不允许直接分配）
        for (StSemesterBudget budget : lockedBudgets) {
            Long budgetSemesterId = budget.getYearSemesterId();
            if (budgetSemesterId == null || !budgetSemesterId.equals(currentSemesterId)) {
                throw new ServiceException(String.format("预算ID=%d不属于当前学期（学期ID=%s），非当前学期预算不允许直接分配，请先结转到当前学期", 
                    budget.getId(), budgetSemesterId));
            }
        }
        
        // 8. 按顺序分配金额（优先使用历史结余，先使用旧资金）
        // 核心原则：严格按照学期优先级排序，不强制主预算优先
        // 这样即使单笔补助金额大于结余资金，也能先用完结余资金，不足部分从本学期预算补充
        List<StBudgetAllocationDetail> allocationDetails = new ArrayList<>();
        BigDecimal remainingAmount = requiredAmount;
        
        // 排序规则（优先级从高到低）：
        // 1. 第一优先级：学期ID升序（历史学期优先，学期ID越小越早）
        // 2. 第二优先级：同一学期内，按创建时间升序（先创建的优先）
        // 3. 第三优先级：按预算ID升序（确保排序稳定）
        lockedBudgets.sort((a, b) -> {
            // 1. 第一优先级：学期ID升序（历史学期优先）
            Long aSemesterId = a.getYearSemesterId() != null ? a.getYearSemesterId() : Long.MAX_VALUE;
            Long bSemesterId = b.getYearSemesterId() != null ? b.getYearSemesterId() : Long.MAX_VALUE;
            int semesterCompare = aSemesterId.compareTo(bSemesterId);
            if (semesterCompare != 0) {
                return semesterCompare; // 历史学期（学期ID小）在前
            }
            
            // 2. 第二优先级：同一学期内，按创建时间升序（先创建的优先）
            if (a.getCreateTime() != null && b.getCreateTime() != null) {
                int timeCompare = a.getCreateTime().compareTo(b.getCreateTime());
                if (timeCompare != 0) {
                    return timeCompare;
                }
            } else if (a.getCreateTime() != null) {
                return -1; // a有创建时间，b没有，a优先
            } else if (b.getCreateTime() != null) {
                return 1; // b有创建时间，a没有，b优先
            }
            
            // 3. 第三优先级：按预算ID升序（确保排序稳定）
            return a.getId().compareTo(b.getId());
        });
        
        log.info("BudgetAllocationService: 预算排序完成，共{}个预算，排序规则：学期ID升序（历史优先）", lockedBudgets.size());
        for (StSemesterBudget budget : lockedBudgets) {
            log.debug("BudgetAllocationService: 预算ID={}，学期ID={}，可用金额={}", 
                budget.getId(), budget.getYearSemesterId(), 
                budget.getBudgetAmount().subtract(
                    budget.getUsedAmount() != null ? budget.getUsedAmount() : BigDecimal.ZERO
                ).subtract(
                    budget.getLockedAmount() != null ? budget.getLockedAmount() : BigDecimal.ZERO
                ));
        }
        
        // 9. 从每个预算中分配金额（按排序后的顺序，优先使用历史结余）
        for (StSemesterBudget budget : lockedBudgets) {
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break; // 已分配足够金额，停止分配
            }
            
            // 计算该预算的可用金额（动态计算，确保准确性）
            BigDecimal actualLockedAmount = budgetMapper.calculateActualLockedAmount(budget.getId());
            if (actualLockedAmount == null) {
                actualLockedAmount = BigDecimal.ZERO;
            }
            BigDecimal actualUsedAmount = budgetMapper.calculateActualUsedAmount(budget.getId());
            if (actualUsedAmount == null) {
                actualUsedAmount = BigDecimal.ZERO;
            }
            
            BigDecimal availableAmount = budget.getBudgetAmount()
                .subtract(actualUsedAmount)
                .subtract(actualLockedAmount);
            
            if (availableAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.debug("BudgetAllocationService: 预算ID={}无可用余额，跳过", budget.getId());
                continue; // 跳过无可用余额的预算
            }
            
            // 从该预算中分配金额（取剩余金额和可用金额的较小值）
            BigDecimal allocateFromThis = remainingAmount.min(availableAmount);
            
            // 锁定预算金额（在悲观锁保护下，确保并发安全）
            int locked = budgetMapper.lockBudgetAmount(budget.getId(), allocateFromThis);
            if (locked == 0) {
                log.error("BudgetAllocationService: 锁定预算金额失败，预算ID={}，金额={}，可用金额={}，可能余额不足或状态异常", 
                    budget.getId(), allocateFromThis, availableAmount);
                throw new ServiceException(String.format("预算余额不足，预算ID=%d，可用金额：%.2f，需要金额：%.2f", 
                    budget.getId(), availableAmount, allocateFromThis));
            }
            
            // 创建分配明细（记录从哪个预算分配了多少金额）
            StBudgetAllocationDetail detail = new StBudgetAllocationDetail();
            detail.setSubsidyRecordId(null); // 稍后设置
            detail.setBudgetId(budget.getId());
            detail.setAllocatedAmount(allocateFromThis);
            detail.setCreatedAt(new Date());
            detail.setUpdatedAt(new Date());
            allocationDetails.add(detail);
            
            remainingAmount = remainingAmount.subtract(allocateFromThis);
            
            // 记录分配日志（区分历史结余和本期预算）
            String budgetType = (budget.getYearSemesterId() != null && 
                                budget.getYearSemesterId() < currentSemesterId) 
                                ? "历史结余" : "本期预算";
            log.info("BudgetAllocationService: 从{}（预算ID={}，学期ID={}）分配金额={}，剩余金额={}", 
                budgetType, budget.getId(), budget.getYearSemesterId(), allocateFromThis, remainingAmount);
        }
        
        // 10. 验证是否分配完成
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 计算总可用金额（用于错误提示）
            BigDecimal totalAvailable = lockedBudgets.stream()
                .map(budget -> {
                    BigDecimal actualLocked = budgetMapper.calculateActualLockedAmount(budget.getId());
                    if (actualLocked == null) actualLocked = BigDecimal.ZERO;
                    BigDecimal actualUsed = budgetMapper.calculateActualUsedAmount(budget.getId());
                    if (actualUsed == null) actualUsed = BigDecimal.ZERO;
                    return budget.getBudgetAmount().subtract(actualUsed).subtract(actualLocked);
                })
                .filter(amount -> amount.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            log.error("BudgetAllocationService: 预算组金额不足，需要金额={}，总可用金额={}，剩余未分配金额={}", 
                requiredAmount, totalAvailable, remainingAmount);
            throw new ServiceException(String.format("预算组可用余额不足，需要金额：%.2f，总可用金额：%.2f，剩余未分配金额：%.2f", 
                requiredAmount, totalAvailable, remainingAmount));
        }
        
        // 11. 分配完成，记录分配结果
        long historyBudgetCount = allocationDetails.stream()
            .mapToLong(detail -> {
                StSemesterBudget budget = lockedBudgets.stream()
                    .filter(b -> b.getId().equals(detail.getBudgetId()))
                    .findFirst()
                    .orElse(null);
                if (budget != null && budget.getYearSemesterId() != null && 
                    budget.getYearSemesterId() < currentSemesterId) {
                    return 1;
                }
                return 0;
            })
            .sum();
        long currentBudgetCount = allocationDetails.size() - historyBudgetCount;
        
        log.info("BudgetAllocationService: 预算分配完成，主预算ID={}，共使用{}个预算（历史结余{}个，本期预算{}个），分配金额={}", 
            primaryBudgetId, allocationDetails.size(), historyBudgetCount, currentBudgetCount, requiredAmount);
        
        return new AllocationResult(primaryBudgetId, allocationDetails);
    }
    
    /**
     * 保存分配明细
     * 
     * @param subsidyRecordId 补助记录ID
     * @param allocationDetails 分配明细列表
     */
    public void saveAllocationDetails(Long subsidyRecordId, List<StBudgetAllocationDetail> allocationDetails) {
        if (allocationDetails == null || allocationDetails.isEmpty()) {
            return;
        }
        
        // 设置补助记录ID
        for (StBudgetAllocationDetail detail : allocationDetails) {
            detail.setSubsidyRecordId(subsidyRecordId);
        }
        
        // 批量插入
        allocationDetailMapper.batchInsertStBudgetAllocationDetail(allocationDetails);
        log.info("BudgetAllocationService: 保存分配明细完成，补助记录ID={}，明细数量={}", 
            subsidyRecordId, allocationDetails.size());
    }
    
    /**
     * 释放分配金额（驳回或删除时）
     * 
     * @param subsidyRecordId 补助记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void releaseAllocation(Long subsidyRecordId) {
        // 查询分配明细
        List<StBudgetAllocationDetail> details = allocationDetailMapper.selectBySubsidyRecordId(subsidyRecordId);
        if (details == null || details.isEmpty()) {
            return;
        }
        
        // 从每个预算中释放金额
        for (StBudgetAllocationDetail detail : details) {
            // 使用悲观锁锁定预算
            StSemesterBudget budget = budgetMapper.selectStSemesterBudgetByIdForUpdate(detail.getBudgetId());
            if (budget != null) {
                budgetMapper.releaseBudgetAmount(detail.getBudgetId(), detail.getAllocatedAmount());
                log.info("BudgetAllocationService: 释放预算金额，预算ID={}，金额={}", 
                    detail.getBudgetId(), detail.getAllocatedAmount());
            }
        }
        
        // 删除分配明细
        allocationDetailMapper.deleteBySubsidyRecordId(subsidyRecordId);
        log.info("BudgetAllocationService: 释放分配金额完成，补助记录ID={}", subsidyRecordId);
    }
    
    /**
     * 审批通过时，将锁定金额转为已使用金额
     * 
     * @param subsidyRecordId 补助记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void approveAllocation(Long subsidyRecordId) {
        // 查询分配明细
        List<StBudgetAllocationDetail> details = allocationDetailMapper.selectBySubsidyRecordId(subsidyRecordId);
        if (details == null || details.isEmpty()) {
            return;
        }
        
        // 更新每个预算
        for (StBudgetAllocationDetail detail : details) {
            // 使用悲观锁锁定预算
            StSemesterBudget budget = budgetMapper.selectStSemesterBudgetByIdForUpdate(detail.getBudgetId());
            if (budget == null) {
                log.warn("BudgetAllocationService: 预算不存在，预算ID={}", detail.getBudgetId());
                continue;
            }
            
            // 验证该预算的锁定金额是否足够（使用动态计算）
            BigDecimal actualLockedAmount = budgetMapper.calculateActualLockedAmount(detail.getBudgetId());
            if (actualLockedAmount == null) {
                actualLockedAmount = BigDecimal.ZERO;
            }
            if (actualLockedAmount.compareTo(detail.getAllocatedAmount()) < 0) {
                throw new ServiceException(String.format("预算锁定金额不足，无法审批通过。预算ID=%d，锁定金额：%.2f，需要金额：%.2f", 
                    detail.getBudgetId(), actualLockedAmount, detail.getAllocatedAmount()));
            }
            
            // 更新预算金额（锁定金额转为已使用金额）
            // 注意：这里直接更新，因为已经验证过锁定金额足够
            int updated = budgetMapper.updateBudgetAmountOnApprovalForCombined(
                detail.getBudgetId(), detail.getAllocatedAmount(), subsidyRecordId);
            if (updated == 0) {
                // 如果使用新的SQL更新失败，尝试使用原有的SQL（兼容性）
                updated = budgetMapper.updateBudgetAmountOnApproval(detail.getBudgetId(), detail.getAllocatedAmount());
                if (updated == 0) {
                    log.error("BudgetAllocationService: 更新预算金额失败，预算ID={}，金额={}，锁定金额={}", 
                        detail.getBudgetId(), detail.getAllocatedAmount(), actualLockedAmount);
                    throw new ServiceException(String.format("更新预算金额失败，预算ID=%d", detail.getBudgetId()));
                }
            }
            
            log.info("BudgetAllocationService: 审批通过，更新预算，预算ID={}，金额={}", 
                detail.getBudgetId(), detail.getAllocatedAmount());
        }
        
        log.info("BudgetAllocationService: 审批通过处理完成，补助记录ID={}", subsidyRecordId);
    }
    
    /**
     * 退回时，将已使用金额转回可用金额
     * 
     * @param subsidyRecordId 补助记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnAllocation(Long subsidyRecordId) {
        // 查询分配明细
        List<StBudgetAllocationDetail> details = allocationDetailMapper.selectBySubsidyRecordId(subsidyRecordId);
        if (details == null || details.isEmpty()) {
            log.warn("BudgetAllocationService: 退回时未找到分配明细，补助记录ID={}，可能未使用预算组合", subsidyRecordId);
            return;
        }
        
        // 更新每个预算（按预算ID排序，避免死锁）
        details.sort((a, b) -> Long.compare(a.getBudgetId(), b.getBudgetId()));
        
        for (StBudgetAllocationDetail detail : details) {
            // 使用悲观锁锁定预算
            StSemesterBudget budget = budgetMapper.selectStSemesterBudgetByIdForUpdate(detail.getBudgetId());
            if (budget == null) {
                log.warn("BudgetAllocationService: 退回时预算不存在，预算ID={}，补助记录ID={}", 
                    detail.getBudgetId(), subsidyRecordId);
                continue;
            }
            
            // 验证该预算的已使用金额是否足够（使用动态计算）
            BigDecimal actualUsedAmount = budgetMapper.calculateActualUsedAmount(detail.getBudgetId());
            if (actualUsedAmount == null) {
                actualUsedAmount = BigDecimal.ZERO;
            }
            if (actualUsedAmount.compareTo(detail.getAllocatedAmount()) < 0) {
                throw new ServiceException(String.format("预算已使用金额不足，无法退回。预算ID=%d，已使用金额：%.2f，需要退回金额：%.2f", 
                    detail.getBudgetId(), actualUsedAmount, detail.getAllocatedAmount()));
            }
            
            // 退回预算金额（使用精确匹配的SQL）
            int returned = budgetMapper.returnBudgetAmountForCombined(
                detail.getBudgetId(), detail.getAllocatedAmount(), subsidyRecordId);
            if (returned == 0) {
                // 如果使用新的SQL更新失败，尝试使用原有的SQL（兼容性）
                returned = budgetMapper.returnBudgetAmount(detail.getBudgetId(), detail.getAllocatedAmount());
                if (returned == 0) {
                    log.error("BudgetAllocationService: 退回预算金额失败，预算ID={}，金额={}，补助记录ID={}，已使用金额={}，可能已使用金额不足", 
                        detail.getBudgetId(), detail.getAllocatedAmount(), subsidyRecordId, actualUsedAmount);
                    throw new ServiceException(String.format("退回预算金额失败，预算ID=%d，金额=%.2f，可能已使用金额不足", 
                        detail.getBudgetId(), detail.getAllocatedAmount()));
                }
            }
            
            log.info("BudgetAllocationService: 退回，更新预算，预算ID={}，金额={}，补助记录ID={}", 
                detail.getBudgetId(), detail.getAllocatedAmount(), subsidyRecordId);
        }
        
        log.info("BudgetAllocationService: 退回处理完成，补助记录ID={}，共退回{}个预算", 
            subsidyRecordId, details.size());
    }
    
    /**
     * 判断功能分类是否属于义教阶段（小学或初中）
     * 小学（functionCategory="1"）和初中（functionCategory="2"）都属于义教阶段
     * 
     * @param functionCategory 功能分类
     * @return true表示是义教阶段，false表示不是
     */
    private boolean isCompulsoryEducationCategory(String functionCategory) {
        if (StringUtils.isEmpty(functionCategory)) {
            return false;
        }
        // 小学（1）和初中（2）都属于义教阶段
        return "1".equals(functionCategory) || "2".equals(functionCategory);
    }
}

