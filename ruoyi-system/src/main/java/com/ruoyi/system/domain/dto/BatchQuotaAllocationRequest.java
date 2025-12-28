package com.ruoyi.system.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 批量指标额度分配请求。
 * 前端多选经济分类时，将多条分配明细一次性下达到学期预算。
 */
public class BatchQuotaAllocationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 目标学年学期ID（所有分配共用同一目标学期） */
    private Long yearSemesterId;

    /** 分配项列表 */
    private List<AllocationItem> items;

    public Long getYearSemesterId() {
        return yearSemesterId;
    }

    public void setYearSemesterId(Long yearSemesterId) {
        this.yearSemesterId = yearSemesterId;
    }

    public List<AllocationItem> getItems() {
        return items;
    }

    public void setItems(List<AllocationItem> items) {
        this.items = items;
    }

    /**
     * 单条分配项
     */
    public static class AllocationItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 指标明细ID */
        private Long quotaDetailId;

        /** 分配金额 */
        private BigDecimal allocateAmount;

        /** 预算类型（可空，默认按经济分类转换） */
        private String budgetType;

        /** 备注 */
        private String memo;

        public Long getQuotaDetailId() {
            return quotaDetailId;
        }

        public void setQuotaDetailId(Long quotaDetailId) {
            this.quotaDetailId = quotaDetailId;
        }

        public BigDecimal getAllocateAmount() {
            return allocateAmount;
        }

        public void setAllocateAmount(BigDecimal allocateAmount) {
            this.allocateAmount = allocateAmount;
        }

        public String getBudgetType() {
            return budgetType;
        }

        public void setBudgetType(String budgetType) {
            this.budgetType = budgetType;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }
    }
}


