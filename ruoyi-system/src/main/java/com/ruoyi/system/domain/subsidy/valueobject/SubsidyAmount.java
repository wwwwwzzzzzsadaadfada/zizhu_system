package com.ruoyi.system.domain.subsidy.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 补助金额值对象（不可变）
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public class SubsidyAmount {
    
    private final BigDecimal value;
    
    public SubsidyAmount(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("补助金额必须大于0");
        }
        this.value = value.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * 从字符串创建
     */
    public static SubsidyAmount of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("补助金额不能为空");
        }
        try {
            return new SubsidyAmount(new BigDecimal(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的金额格式: " + value, e);
        }
    }
    
    /**
     * 从BigDecimal创建
     */
    public static SubsidyAmount of(BigDecimal value) {
        return new SubsidyAmount(value);
    }
    
    /**
     * 加法运算
     */
    public SubsidyAmount add(SubsidyAmount other) {
        if (other == null) {
            throw new IllegalArgumentException("不能与null相加");
        }
        return new SubsidyAmount(this.value.add(other.value));
    }
    
    /**
     * 乘法运算（用于批量计算）
     */
    public SubsidyAmount multiply(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }
        return new SubsidyAmount(this.value.multiply(new BigDecimal(count)));
    }
    
    /**
     * 比较大小
     */
    public boolean isGreaterThan(SubsidyAmount other) {
        if (other == null) {
            return false;
        }
        return this.value.compareTo(other.value) > 0;
    }
    
    /**
     * 比较大小（大于等于）
     */
    public boolean isGreaterThanOrEqual(SubsidyAmount other) {
        if (other == null) {
            return false;
        }
        return this.value.compareTo(other.value) >= 0;
    }
    
    /**
     * 比较大小（小于等于）
     */
    public boolean isLessThanOrEqual(SubsidyAmount other) {
        if (other == null) {
            return false;
        }
        return this.value.compareTo(other.value) <= 0;
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
        SubsidyAmount that = (SubsidyAmount) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}



