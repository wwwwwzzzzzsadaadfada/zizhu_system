package com.ruoyi.system.application.query;

import java.util.List;

/**
 * 预算业务规则工具类（兼容层）
 * 
 * 注意：此类已重构为细粒度的工具类，保留此类仅用于向后兼容。
 * 新代码请使用以下工具类：
 * - FunctionCategoryUtils：功能分类相关
 * - EconomyCategoryUtils：经济分类相关
 * - QuotaSourceUtils：指标来源相关
 * - HistoricalSurplusUtils：历史结余判断相关
 * 
 * @author ruoyi
 * @deprecated 请使用细粒度的工具类（FunctionCategoryUtils、EconomyCategoryUtils等）
 */
@Deprecated
public class BudgetBusinessRule {
    
    /** @deprecated 请使用 FunctionCategoryUtils.PRIMARY */
    @Deprecated
    public static final String FUNCTION_CATEGORY_PRIMARY = FunctionCategoryUtils.PRIMARY;
    
    /** @deprecated 请使用 FunctionCategoryUtils.JUNIOR */
    @Deprecated
    public static final String FUNCTION_CATEGORY_JUNIOR = FunctionCategoryUtils.JUNIOR;
    
    /** @deprecated 请使用 FunctionCategoryUtils.SENIOR */
    @Deprecated
    public static final String FUNCTION_CATEGORY_SENIOR = FunctionCategoryUtils.SENIOR;
    
    /** @deprecated 请使用 EconomyCategoryUtils.SCHOLARSHIP */
    @Deprecated
    public static final String ECONOMY_CATEGORY_SCHOLARSHIP = EconomyCategoryUtils.SCHOLARSHIP;
    
    /** @deprecated 请使用 EconomyCategoryUtils.TUITION_WAIVER */
    @Deprecated
    public static final String ECONOMY_CATEGORY_TUITION_WAIVER = EconomyCategoryUtils.TUITION_WAIVER;
    
    /** @deprecated 请使用 EconomyCategoryUtils.TUITION_FREE */
    @Deprecated
    public static final String ECONOMY_CATEGORY_TUITION_FREE = EconomyCategoryUtils.TUITION_FREE;
    
    /** @deprecated 请使用 EconomyCategoryUtils.NUTRITION */
    @Deprecated
    public static final String ECONOMY_CATEGORY_NUTRITION = EconomyCategoryUtils.NUTRITION;
    
    /**
     * @deprecated 请使用 FunctionCategoryUtils.determineTargetCategories
     */
    @Deprecated
    public static List<String> determineTargetFunctionCategories(String functionCategory) {
        return FunctionCategoryUtils.determineTargetCategories(functionCategory);
    }
    
    /**
     * @deprecated 请使用 FunctionCategoryUtils.isCompulsoryEducationStage
     */
    @Deprecated
    public static boolean isCompulsoryEducationStage(String functionCategory) {
        return FunctionCategoryUtils.isCompulsoryEducationStage(functionCategory);
    }
    
    /**
     * @deprecated 请使用 FunctionCategoryUtils.isValid
     */
    @Deprecated
    public static boolean isValidFunctionCategory(String functionCategory) {
        return FunctionCategoryUtils.isValid(functionCategory);
    }
    
    /**
     * @deprecated 请使用 EconomyCategoryUtils.isValid
     */
    @Deprecated
    public static boolean isValidEconomyCategory(String economyCategory) {
        return EconomyCategoryUtils.isValid(economyCategory);
    }
    
    /**
     * @deprecated 请使用 EconomyCategoryUtils.codeToName
     */
    @Deprecated
    public static String convertEconomyCategoryCodeToName(String code) {
        return EconomyCategoryUtils.codeToName(code);
    }
    
    /**
     * @deprecated 请使用 QuotaSourceUtils.isCarryOver
     */
    @Deprecated
    public static boolean isCarryOverQuota(Integer quotaSourceType) {
        return QuotaSourceUtils.isCarryOver(quotaSourceType);
    }
    
    /**
     * @deprecated 请使用 HistoricalSurplusUtils.isHistoricalSurplus
     */
    @Deprecated
    public static boolean isHistoricalSurplus(Long budgetSemesterId, 
                                              Long quotaSemesterId, 
                                              Integer quotaSourceType, 
                                              Long currentSemesterId) {
        return HistoricalSurplusUtils.isHistoricalSurplus(budgetSemesterId, quotaSemesterId, quotaSourceType, currentSemesterId);
    }
    
    /**
     * @deprecated 请使用 BudgetQueryService.validateBudgetQueryParams（私有方法）或直接使用工具类验证
     */
    @Deprecated
    public static void validateBudgetQueryParams(Long currentSemesterId, 
                                                 String economyCategory, 
                                                 String functionCategory) {
        if (currentSemesterId == null || currentSemesterId <= 0) {
            throw new IllegalArgumentException("当前学期ID不能为空且必须大于0");
        }
        
        if (economyCategory != null && !economyCategory.trim().isEmpty()) {
            if (!EconomyCategoryUtils.isValid(economyCategory)) {
                throw new IllegalArgumentException("无效的经济分类代码: " + economyCategory);
            }
        }
        
        if (functionCategory != null && !functionCategory.trim().isEmpty()) {
            if (!FunctionCategoryUtils.isValid(functionCategory)) {
                throw new IllegalArgumentException("无效的功能分类代码: " + functionCategory);
            }
        }
    }
}

