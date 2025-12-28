package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学生受助明细对象 st_student_subsidy_details
 * 
 * @author ruoyi
 * @date 2024-11-22
 */
public class StStudentSubsidyDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 学生学期记录ID */
    @Excel(name = "学生学期记录ID")
    private Long studentRecordId;

    /** 学期预算ID */
    @Excel(name = "学期预算ID")
    private Long budgetId;

    /** 经济分类（1=助学金,2=免学杂费,3=免学费） */
    @Excel(name = "经济分类", dictType = "sys_economy_category")
    private String economyCategory;

    /** 受助金额 */
    @Excel(name = "受助金额")
    private BigDecimal subsidyAmount;

    /** 审批状态（0=待审核，1=已通过，2=未通过） */
    @Excel(name = "审批状态", readConverterExp = "0=待审核,1=已通过,2=未通过")
    private Integer approvalStatus = 1; // 直接设置为已通过状态，去掉审批机制

    /** 备注 */
    @Excel(name = "备注")
    private String memo;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setStudentRecordId(Long studentRecordId) 
    {
        this.studentRecordId = studentRecordId;
    }

    public Long getStudentRecordId() 
    {
        return studentRecordId;
    }
    public void setBudgetId(Long budgetId) 
    {
        this.budgetId = budgetId;
    }

    public Long getBudgetId() 
    {
        return budgetId;
    }
    public void setEconomyCategory(String economyCategory) 
    {
        this.economyCategory = economyCategory;
    }

    public String getEconomyCategory() 
    {
        return economyCategory;
    }
    public void setSubsidyAmount(BigDecimal subsidyAmount) 
    {
        this.subsidyAmount = subsidyAmount;
    }

    public BigDecimal getSubsidyAmount() 
    {
        return subsidyAmount;
    }
    public void setApprovalStatus(Integer approvalStatus) 
    {
        this.approvalStatus = approvalStatus;
    }

    public Integer getApprovalStatus() 
    {
        return approvalStatus;
    }
    public void setMemo(String memo) 
    {
        this.memo = memo;
    }

    public String getMemo() 
    {
        return memo;
    }
    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }
    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("studentRecordId", getStudentRecordId())
            .append("budgetId", getBudgetId())
            .append("economyCategory", getEconomyCategory())
            .append("subsidyAmount", getSubsidyAmount())
            .append("approvalStatus", getApprovalStatus())
            .append("memo", getMemo())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}