package com.ruoyi.system.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 学期预算统计信息
 */
public class BudgetStatisticVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 预算总额 */
    private BigDecimal totalBudget = BigDecimal.ZERO;

    /** 已使用金额 */
    private BigDecimal usedAmount = BigDecimal.ZERO;

    /** 锁定金额 */
    private BigDecimal lockedAmount = BigDecimal.ZERO;

    /** 可用金额 */
    private BigDecimal availableAmount = BigDecimal.ZERO;

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public BigDecimal getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(BigDecimal usedAmount) {
        this.usedAmount = usedAmount;
    }

    public BigDecimal getLockedAmount() {
        return lockedAmount;
    }

    public void setLockedAmount(BigDecimal lockedAmount) {
        this.lockedAmount = lockedAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public void recomputeAvailable() {
        BigDecimal total = totalBudget != null ? totalBudget : BigDecimal.ZERO;
        BigDecimal used = usedAmount != null ? usedAmount : BigDecimal.ZERO;
        BigDecimal locked = lockedAmount != null ? lockedAmount : BigDecimal.ZERO;
        this.availableAmount = total.subtract(used).subtract(locked);
    }
}



