package com.ruoyi.system.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.ruoyi.common.xss.Xss;

/**
 * 指标额度分配请求
 *
 * 将指标明细金额下达至具体学期预算池时使用。
 */
public class QuotaAllocationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 指标明细ID */
    @NotNull(message = "指标明细ID不能为空")
    private Long quotaDetailId;

    /** 目标学年学期ID */
    @NotNull(message = "目标学年学期ID不能为空")
    private Long yearSemesterId;

    /** 分配金额 */
    @NotNull(message = "分配金额不能为空")
    @DecimalMin(value = "0.01", message = "分配金额必须大于0")
    private BigDecimal allocateAmount;

    /** 预算类型（可空，默认按经济分类转换） */
    @Size(max = 50, message = "预算类型长度不能超过50个字符")
    private String budgetType;

    /** 备注 */
    @Xss(message = "备注不能包含脚本字符")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String memo;

    public Long getQuotaDetailId() {
        return quotaDetailId;
    }

    public void setQuotaDetailId(Long quotaDetailId) {
        this.quotaDetailId = quotaDetailId;
    }

    public Long getYearSemesterId() {
        return yearSemesterId;
    }

    public void setYearSemesterId(Long yearSemesterId) {
        this.yearSemesterId = yearSemesterId;
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



