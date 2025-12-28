package com.ruoyi.system.domain.dto;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.ruoyi.common.xss.Xss;

/**
 * 结转到预算请求
 * 
 * 将历史学期指标的剩余额度结转到当前学期预算
 */
public class CarryOverToBudgetRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 来源指标ID */
    @NotNull(message = "来源指标ID不能为空")
    private Long sourceQuotaId;
    
    /** 目标学年学期ID（通常是当前学期） */
    @NotNull(message = "目标学年学期ID不能为空")
    private Long yearSemesterId;
    
    /** 要结转的经济分类列表（如果为空，则结转所有有剩余额度的经济分类） */
    private List<String> economyCategories;
    
    /** 备注 */
    @Xss(message = "备注不能包含脚本字符")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String memo;

    public Long getSourceQuotaId() {
        return sourceQuotaId;
    }

    public void setSourceQuotaId(Long sourceQuotaId) {
        this.sourceQuotaId = sourceQuotaId;
    }

    public Long getYearSemesterId() {
        return yearSemesterId;
    }

    public void setYearSemesterId(Long yearSemesterId) {
        this.yearSemesterId = yearSemesterId;
    }

    public List<String> getEconomyCategories() {
        return economyCategories;
    }

    public void setEconomyCategories(List<String> economyCategories) {
        this.economyCategories = economyCategories;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}




