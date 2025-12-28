package com.ruoyi.system.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 指标下达统计信息
 */
public class QuotaStatisticVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总指标金额 */
    private BigDecimal totalQuota = BigDecimal.ZERO;

    /** 已分配金额 */
    private BigDecimal allocatedAmount = BigDecimal.ZERO;

    /** 剩余额度 */
    private BigDecimal availableAmount = BigDecimal.ZERO;

    /** 按经济分类统计的已分配金额 */
    private Map<String, BigDecimal> allocatedByEconomy;

    public BigDecimal getTotalQuota() {
        return totalQuota;
    }

    public void setTotalQuota(BigDecimal totalQuota) {
        this.totalQuota = totalQuota;
    }

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Map<String, BigDecimal> getAllocatedByEconomy() {
        return allocatedByEconomy;
    }

    public void setAllocatedByEconomy(Map<String, BigDecimal> allocatedByEconomy) {
        this.allocatedByEconomy = allocatedByEconomy;
    }
}

