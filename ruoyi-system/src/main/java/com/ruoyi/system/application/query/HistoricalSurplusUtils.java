package com.ruoyi.system.application.query;

import com.ruoyi.system.domain.StSemesterBudget;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 历史结余工具类
 * 处理历史结余判断相关的业务逻辑
 * 
 * 注意：判断逻辑使用学年（schoolYear）和学期（semester）进行比较，
 * 而不是使用ID进行比较，因为ID是自增的，不能反映实际的时间顺序。
 * 如果管理员后来补录了历史学期的数据，这些数据的ID可能比当前学期的ID大。
 * 
 * @author ruoyi
 */
public class HistoricalSurplusUtils {
    
    /**
     * 判断预算是否为历史结余（使用学年和学期进行比较）
     * 
     * 历史结余包括三种情况：
     * 1. 预算的学期早于当前学期（传统的历史预算）
     * 2. 预算的学期等于当前学期，但关联的指标来自历史学期（结转资金）
     * 3. 预算的学期等于当前学期，但关联的指标的来源类型为结转（quota_source_type in (2,3)）
     * 
     * 注意：使用学年（schoolYear）和学期（semester）进行比较，而不是使用ID，
     * 因为ID是自增的，不能反映实际的时间顺序。
     * 
     * @param budget 预算对象（必须包含schoolYear和semester字段）
     * @param currentSchoolYear 当前学期的学年（如 "2024-2025"）
     * @param currentSemester 当前学期的学期（1=秋季，2=春季）
     * @return true=是历史结余，false=不是
     */
    public static boolean isHistoricalSurplus(StSemesterBudget budget, String currentSchoolYear, Integer currentSemester) {
        if (budget == null || currentSchoolYear == null || currentSemester == null) {
            return false;
        }
        
        String budgetSchoolYear = budget.getSchoolYear();
        Integer budgetSemester = budget.getSemester();
        
        if (budgetSchoolYear == null || budgetSemester == null) {
            // 如果预算的学年或学期信息缺失，无法判断，返回false
            return false;
        }
        
        // 情况1：预算的学期早于当前学期（传统的历史预算）
        if (SemesterComparisonUtils.isBefore(budgetSchoolYear, budgetSemester, currentSchoolYear, currentSemester)) {
            return true;
        }
        
        // 情况2和3：预算的学期等于当前学期，但关联的指标来自历史学期或为结转指标
        if (SemesterComparisonUtils.isEqual(budgetSchoolYear, budgetSemester, currentSchoolYear, currentSemester)) {
            // 情况3：指标的来源类型为结转（quota_source_type in (2,3)）
            // 注意：情况2（指标的学期早于当前学期）的判断需要指标的学年学期信息
            // 但StSemesterBudget中没有指标的学年学期信息，只有quotaYearSemesterId
            // 如果指标的quotaSourceType为结转，说明是历史结余
            // 如果quotaYearSemesterId不为null且不等于预算的学期ID，需要通过额外查询判断
            // 但为了性能考虑，这里主要通过quotaSourceType判断
            if (QuotaSourceUtils.isCarryOver(budget.getQuotaSourceType())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 判断预算是否为历史结余（兼容旧版本，使用ID比较，已废弃）
     * 
     * @deprecated 请使用 {@link #isHistoricalSurplus(StSemesterBudget, String, Integer)} 方法
     *             使用学年和学期进行比较，而不是ID
     * 
     * @param budget 预算对象
     * @param currentSemesterId 当前学期ID
     * @return true=是历史结余，false=不是
     */
    @Deprecated
    public static boolean isHistoricalSurplus(StSemesterBudget budget, Long currentSemesterId) {
        // 为了向后兼容，保留此方法
        // 但建议使用新的方法，使用学年和学期进行比较
        if (budget == null || currentSemesterId == null) {
            return false;
        }
        
        Long budgetSemesterId = budget.getYearSemesterId();
        if (budgetSemesterId == null) {
            return false;
        }
        
        // 如果预算有学年和学期信息，使用新方法判断
        String budgetSchoolYear = budget.getSchoolYear();
        Integer budgetSemester = budget.getSemester();
        if (budgetSchoolYear != null && budgetSemester != null) {
            // 需要查询当前学期的信息，但这里没有，所以回退到ID比较
            // 调用者应该使用新方法
        }
        
        // 情况1：预算的学期ID小于当前学期ID（传统的历史预算）
        if (budgetSemesterId < currentSemesterId) {
            return true;
        }
        
        // 情况2和3：预算的学期ID等于当前学期ID，但关联的指标来自历史学期或为结转指标
        if (budgetSemesterId.equals(currentSemesterId)) {
            Long quotaSemesterId = budget.getQuotaYearSemesterId();
            
            // 情况2：指标的学期ID存在且小于当前学期ID（结转资金）
            if (quotaSemesterId != null && quotaSemesterId < currentSemesterId) {
                return true;
            }
            
            // 情况3：指标的来源类型为结转（quota_source_type in (2,3)）
            if (QuotaSourceUtils.isCarryOver(budget.getQuotaSourceType())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 判断预算是否为历史结余（使用详细参数）
     * 
     * @param budgetSemesterId 预算的学期ID
     * @param quotaSemesterId 指标的学期ID（可能为null）
     * @param quotaSourceType 指标来源类型（可能为null）
     * @param currentSemesterId 当前学期ID
     * @return true=是历史结余，false=不是
     */
    public static boolean isHistoricalSurplus(Long budgetSemesterId, 
                                              Long quotaSemesterId, 
                                              Integer quotaSourceType, 
                                              Long currentSemesterId) {
        if (budgetSemesterId == null || currentSemesterId == null) {
            return false;
        }
        
        // 情况1：预算的学期ID小于当前学期ID（传统的历史预算）
        if (budgetSemesterId < currentSemesterId) {
            return true;
        }
        
        // 情况2和3：预算的学期ID等于当前学期ID，但关联的指标来自历史学期或为结转指标
        if (budgetSemesterId.equals(currentSemesterId)) {
            // 情况2：指标的学期ID存在且小于当前学期ID（结转资金）
            if (quotaSemesterId != null && quotaSemesterId < currentSemesterId) {
                return true;
            }
            
            // 情况3：指标的来源类型为结转（quota_source_type in (2,3)）
            if (QuotaSourceUtils.isCarryOver(quotaSourceType)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 从预算列表中过滤出历史结余预算（使用学年和学期进行比较）
     * 
     * @param budgets 预算列表
     * @param currentSchoolYear 当前学期的学年（如 "2024-2025"）
     * @param currentSemester 当前学期的学期（1=秋季，2=春季）
     * @return 历史结余预算列表
     */
    public static List<StSemesterBudget> filterHistoricalSurplus(List<StSemesterBudget> budgets, String currentSchoolYear, Integer currentSemester) {
        if (budgets == null || budgets.isEmpty() || currentSchoolYear == null || currentSemester == null) {
            return java.util.Collections.emptyList();
        }
        
        return budgets.stream()
            .filter(budget -> isHistoricalSurplus(budget, currentSchoolYear, currentSemester))
            .collect(Collectors.toList());
    }
    
    /**
     * 从预算列表中过滤出历史结余预算（兼容旧版本，使用ID比较，已废弃）
     * 
     * @deprecated 请使用 {@link #filterHistoricalSurplus(List, String, Integer)} 方法
     * 
     * @param budgets 预算列表
     * @param currentSemesterId 当前学期ID
     * @return 历史结余预算列表
     */
    @Deprecated
    public static List<StSemesterBudget> filterHistoricalSurplus(List<StSemesterBudget> budgets, Long currentSemesterId) {
        if (budgets == null || budgets.isEmpty() || currentSemesterId == null) {
            return java.util.Collections.emptyList();
        }
        
        return budgets.stream()
            .filter(budget -> isHistoricalSurplus(budget, currentSemesterId))
            .collect(Collectors.toList());
    }
    
    /**
     * 从预算列表中过滤出非历史结余预算（当前学期预算，使用学年和学期进行比较）
     * 
     * @param budgets 预算列表
     * @param currentSchoolYear 当前学期的学年（如 "2024-2025"）
     * @param currentSemester 当前学期的学期（1=秋季，2=春季）
     * @return 当前学期预算列表
     */
    public static List<StSemesterBudget> filterCurrentSemester(List<StSemesterBudget> budgets, String currentSchoolYear, Integer currentSemester) {
        if (budgets == null || budgets.isEmpty() || currentSchoolYear == null || currentSemester == null) {
            return java.util.Collections.emptyList();
        }
        
        return budgets.stream()
            .filter(budget -> !isHistoricalSurplus(budget, currentSchoolYear, currentSemester))
            .collect(Collectors.toList());
    }
    
    /**
     * 从预算列表中过滤出非历史结余预算（兼容旧版本，使用ID比较，已废弃）
     * 
     * @deprecated 请使用 {@link #filterCurrentSemester(List, String, Integer)} 方法
     * 
     * @param budgets 预算列表
     * @param currentSemesterId 当前学期ID
     * @return 当前学期预算列表
     */
    @Deprecated
    public static List<StSemesterBudget> filterCurrentSemester(List<StSemesterBudget> budgets, Long currentSemesterId) {
        if (budgets == null || budgets.isEmpty() || currentSemesterId == null) {
            return java.util.Collections.emptyList();
        }
        
        return budgets.stream()
            .filter(budget -> !isHistoricalSurplus(budget, currentSemesterId))
            .collect(Collectors.toList());
    }
    
    /**
     * 判断预算是否为当前学期下达的预算（使用学年和学期进行比较）
     * 
     * 业务规则：当前学期预算需要满足以下条件：
     * 1. 预算的学期等于当前学期
     * 2. 关联的指标的来源类型为下达（quota_source_type = 1），或者为NULL
     * 
     * 注意：这个方法与 isHistoricalSurplus 相反，用于过滤当前学期预算
     * 使用学年（schoolYear）和学期（semester）进行比较，而不是使用ID
     * 
     * @param budget 预算对象（必须包含schoolYear和semester字段）
     * @param currentSchoolYear 当前学期的学年（如 "2024-2025"）
     * @param currentSemester 当前学期的学期（1=秋季，2=春季）
     * @return true=是当前学期预算，false=不是
     */
    public static boolean isCurrentSemesterBudget(StSemesterBudget budget, String currentSchoolYear, Integer currentSemester) {
        if (budget == null || currentSchoolYear == null || currentSemester == null) {
            return false;
        }
        
        String budgetSchoolYear = budget.getSchoolYear();
        Integer budgetSemester = budget.getSemester();
        
        if (budgetSchoolYear == null || budgetSemester == null) {
            return false;
        }
        
        // 预算的学期必须等于当前学期
        if (!SemesterComparisonUtils.isEqual(budgetSchoolYear, budgetSemester, currentSchoolYear, currentSemester)) {
            return false;
        }
        
        // 如果指标来源类型存在，必须为下达（1），不能是结转（2或3）
        Integer quotaSourceType = budget.getQuotaSourceType();
        if (quotaSourceType != null && QuotaSourceUtils.isCarryOver(quotaSourceType)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 判断预算是否为当前学期下达的预算（兼容旧版本，使用ID比较，已废弃）
     * 
     * @deprecated 请使用 {@link #isCurrentSemesterBudget(StSemesterBudget, String, Integer)} 方法
     *             使用学年和学期进行比较，而不是ID
     * 
     * @param budget 预算对象
     * @param currentSemesterId 当前学期ID
     * @return true=是当前学期预算，false=不是
     */
    @Deprecated
    public static boolean isCurrentSemesterBudget(StSemesterBudget budget, Long currentSemesterId) {
        if (budget == null || currentSemesterId == null) {
            return false;
        }
        
        Long budgetSemesterId = budget.getYearSemesterId();
        if (budgetSemesterId == null || !budgetSemesterId.equals(currentSemesterId)) {
            return false;
        }
        
        Long quotaSemesterId = budget.getQuotaYearSemesterId();
        Integer quotaSourceType = budget.getQuotaSourceType();
        
        if (quotaSemesterId != null && !quotaSemesterId.equals(currentSemesterId)) {
            return false;
        }
        
        if (quotaSourceType != null && QuotaSourceUtils.isCarryOver(quotaSourceType)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 从预算列表中过滤出当前学期下达的预算（使用学年和学期进行比较）
     * 
     * @param budgets 预算列表
     * @param currentSchoolYear 当前学期的学年（如 "2024-2025"）
     * @param currentSemester 当前学期的学期（1=秋季，2=春季）
     * @return 当前学期下达的预算列表
     */
    public static List<StSemesterBudget> filterCurrentSemesterBudgets(List<StSemesterBudget> budgets, String currentSchoolYear, Integer currentSemester) {
        if (budgets == null || budgets.isEmpty() || currentSchoolYear == null || currentSemester == null) {
            return java.util.Collections.emptyList();
        }
        
        return budgets.stream()
            .filter(budget -> isCurrentSemesterBudget(budget, currentSchoolYear, currentSemester))
            .collect(Collectors.toList());
    }
    
    /**
     * 从预算列表中过滤出当前学期下达的预算（兼容旧版本，使用ID比较，已废弃）
     * 
     * @deprecated 请使用 {@link #filterCurrentSemesterBudgets(List, String, Integer)} 方法
     * 
     * @param budgets 预算列表
     * @param currentSemesterId 当前学期ID
     * @return 当前学期下达的预算列表
     */
    @Deprecated
    public static List<StSemesterBudget> filterCurrentSemesterBudgets(List<StSemesterBudget> budgets, Long currentSemesterId) {
        if (budgets == null || budgets.isEmpty() || currentSemesterId == null) {
            return java.util.Collections.emptyList();
        }
        
        return budgets.stream()
            .filter(budget -> isCurrentSemesterBudget(budget, currentSemesterId))
            .collect(Collectors.toList());
    }
}

