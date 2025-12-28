package com.ruoyi.system.domain.subsidy.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 预算金额值对象（不可变）
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public class BudgetAmount {
    
    private final BigDecimal value;
    
    public BudgetAmount(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("预算金额不能为负数");
        }
        this.value = value.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * 从字符串创建
     */
    public static BudgetAmount of(String value) {
        if (value == null || value.trim().isEmpty()) {
            return new BudgetAmount(BigDecimal.ZERO);
        }
        try {
            return new BudgetAmount(new BigDecimal(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的金额格式: " + value, e);
        }
    }
    
    /**
     * 从BigDecimal创建
     */
    public static BudgetAmount of(BigDecimal value) {
        if (value == null) {
            return new BudgetAmount(BigDecimal.ZERO);
        }
        return new BudgetAmount(value);
    }
    
    /**
     * 零值
     */
    public static BudgetAmount zero() {
        return new BudgetAmount(BigDecimal.ZERO);
    }
    
    /**
     * 加法运算
     */
    public BudgetAmount add(BudgetAmount other) {
        if (other == null) {
            return this;
        }
        return new BudgetAmount(this.value.add(other.value));
    }
    
    /**
     * 减法运算
     */
    public BudgetAmount subtract(BudgetAmount other) {
        if (other == null) {
            return this;
        }
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("预算金额不能为负数");
        }
        return new BudgetAmount(result);
    }
    
    /**
     * 比较大小
     */
    public boolean isGreaterThan(BudgetAmount other) {
        if (other == null) {
            return this.value.compareTo(BigDecimal.ZERO) > 0;
        }
        return this.value.compareTo(other.value) > 0;
    }
    
    /**
     * 比较大小（大于等于）
     */
    public boolean isGreaterThanOrEqual(BudgetAmount other) {
        if (other == null) {
            return this.value.compareTo(BigDecimal.ZERO) >= 0;
        }
        return this.value.compareTo(other.value) >= 0;
    }
    
    /**
     * 检查是否足够
     */
    public boolean isSufficient(BudgetAmount required) {
        if (required == null) {
            return true;
        }
        return this.value.compareTo(required.value) >= 0;
    }
    
    /**
     * 获取BigDecimal值（用于数据库存储）
     */
    public BigDecimal getValue() {
        return value;
    }
    
    /**
     * 转换为字符串
     */
    public String toString() {
        return value.toPlainString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BudgetAmount that = (BudgetAmount) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}



