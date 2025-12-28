package com.ruoyi.system.domain.dto;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import com.ruoyi.common.xss.Xss;

/**
 * 指标结转到目标指标请求
 */
public class CarryOverToTargetQuotaRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 来源指标ID */
    @NotNull(message = "来源指标ID不能为空")
    private Long sourceQuotaId;

    /** 目标指标文号 */
    @NotBlank(message = "目标指标文号不能为空")
    @Xss(message = "指标文号不能包含脚本字符")
    @Size(max = 100, message = "指标文号长度不能超过100个字符")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5\\-\\(\\)\\[\\]【】\\s]+$", 
             message = "指标文号只能包含字母、数字、中文、横线和括号")
    private String targetQuotaDocNo;

    /** 目标学年学期ID */
    @NotNull(message = "目标学年学期ID不能为空")
    private Long yearSemesterId;

    /** 目标功能分类 */
    @Size(max = 20, message = "功能分类长度不能超过20个字符")
    private String functionCategory;

    /** 要结转的经济分类列表（可选，不选择则结转所有有剩余额度的） */
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

    public String getTargetQuotaDocNo() {
        return targetQuotaDocNo;
    }

    public void setTargetQuotaDocNo(String targetQuotaDocNo) {
        this.targetQuotaDocNo = targetQuotaDocNo;
    }

    public Long getYearSemesterId() {
        return yearSemesterId;
    }

    public void setYearSemesterId(Long yearSemesterId) {
        this.yearSemesterId = yearSemesterId;
    }

    public String getFunctionCategory() {
        return functionCategory;
    }

    public void setFunctionCategory(String functionCategory) {
        this.functionCategory = functionCategory;
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

    @Override
    public String toString() {
        return "CarryOverToTargetQuotaRequest{" +
               "sourceQuotaId=" + sourceQuotaId +
               ", targetQuotaDocNo='" + targetQuotaDocNo + '\'' +
               ", yearSemesterId=" + yearSemesterId +
               ", functionCategory='" + functionCategory + '\'' +
               ", economyCategories=" + economyCategories +
               ", memo='" + memo + '\'' +
               '}';
    }
}



