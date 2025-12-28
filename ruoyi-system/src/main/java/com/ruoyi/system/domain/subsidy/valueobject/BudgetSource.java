package com.ruoyi.system.domain.subsidy.valueobject;

import java.util.Objects;

/**
 * 预算来源值对象（不可变）
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public class BudgetSource {
    
    /**
     * 预算来源类型枚举
     */
    public enum SourceType {
        CURRENT_SEMESTER("CURRENT_SEMESTER", "当前学期预算"),
        HISTORICAL_SURPLUS("HISTORICAL_SURPLUS", "历史学期结余");
        
        private final String code;
        private final String description;
        
        SourceType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static SourceType fromCode(String code) {
            for (SourceType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("未知的预算来源类型: " + code);
        }
    }
    
    private final Long semesterId;
    private final SourceType type;
    private final Long budgetId;
    
    private BudgetSource(Long semesterId, SourceType type, Long budgetId) {
        if (semesterId == null) {
            throw new IllegalArgumentException("学期ID不能为空");
        }
        if (type == null) {
            throw new IllegalArgumentException("预算来源类型不能为空");
        }
        if (budgetId == null) {
            throw new IllegalArgumentException("预算ID不能为空");
        }
        this.semesterId = semesterId;
        this.type = type;
        this.budgetId = budgetId;
    }
    
    /**
     * 创建当前学期预算来源
     */
    public static BudgetSource currentSemester(Long semesterId, Long budgetId) {
        return new BudgetSource(semesterId, SourceType.CURRENT_SEMESTER, budgetId);
    }
    
    /**
     * 创建历史学期结余来源
     */
    public static BudgetSource historicalSurplus(Long semesterId, Long budgetId) {
        return new BudgetSource(semesterId, SourceType.HISTORICAL_SURPLUS, budgetId);
    }
    
    /**
     * 根据类型创建
     */
    public static BudgetSource of(SourceType type, Long semesterId, Long budgetId) {
        return new BudgetSource(semesterId, type, budgetId);
    }
    
    public Long getSemesterId() {
        return semesterId;
    }
    
    public SourceType getType() {
        return type;
    }
    
    public Long getBudgetId() {
        return budgetId;
    }
    
    public boolean isCurrentSemester() {
        return type == SourceType.CURRENT_SEMESTER;
    }
    
    public boolean isHistoricalSurplus() {
        return type == SourceType.HISTORICAL_SURPLUS;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BudgetSource that = (BudgetSource) o;
        return Objects.equals(semesterId, that.semesterId) &&
               type == that.type &&
               Objects.equals(budgetId, that.budgetId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(semesterId, type, budgetId);
    }
    
    @Override
    public String toString() {
        return String.format("BudgetSource{type=%s, semesterId=%d, budgetId=%d}", 
            type.getDescription(), semesterId, budgetId);
    }
}



