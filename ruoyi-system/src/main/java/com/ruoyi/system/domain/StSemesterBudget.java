package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学期预算支出对象 st_semester_budget
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
public class StSemesterBudget extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 学年学期ID */
    @Excel(name = "学年学期")
    private Long yearSemesterId;

    /** 关联指标ID */
    @Excel(name = "关联指标ID")
    private Long quotaId;
    
    /** 关联指标明细ID */
    @Excel(name = "关联指标明细ID")
    private Long quotaDetailId;
    
    /** 是否结转资金（0-否 1-是） */
    @Excel(name = "是否结转资金")
    private String isCarryover;
    
    /** 来源学期ID（结转时继承） */
    private Long sourceSemesterId;

    /** 预算类型 */
    @Excel(name = "预算类型")
    private String budgetType;

    /** 预算总额 */
    @Excel(name = "预算总额")
    private BigDecimal budgetAmount;

    /** 已使用金额 */
    @Excel(name = "已使用金额")
    private BigDecimal usedAmount;

    /** 锁定金额(审批中) */
    @Excel(name = "锁定金额")
    private BigDecimal lockedAmount;

    /** 预算状态:1启用,2冻结,3已用完,4作废 */
    @Excel(name = "预算状态", dictType = "sys_budget_status")
    private Integer status;

    /** 备注 */
    @Excel(name = "备注")
    private String memo;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    /** 学年学期信息（关联查询，不存数据库） */
    private String schoolYear;
    private Integer semester;
    /** 学期中文名称（秋季学期/春季学期，由后端计算） */
    private String semesterLabel;

    /** 可用金额（虚拟字段，不存数据库） */
    private BigDecimal availableAmount;

    /** 指标信息（关联查询，不存数据库） */
    private String quotaDocNo;
    private String budgetProjectName;
    private String economyCategory;
    private String functionCategory;
    
    /** 指标来源类型（关联查询，不存数据库） */
    private Integer quotaSourceType;
    
    /** 指标的学年学期ID（关联查询，不存数据库，用于判断历史结余） */
    private Long quotaYearSemesterId;

    /** 是否结转生成的预算（关联查询，不存数据库） */
    private Boolean carryOverFlag;
    
    /** 指标明细的来源明细ID（关联查询，不存数据库，用于判断是否为结转资金） */
    private Long sourceDetailId;
    
    /** 收回状态（计算字段："可收回"、"已使用"、"已锁定"） */
    private String reclaimStatus;
    
    /** 收回状态类型（计算字段："success"、"danger"、"warning"） */
    private String reclaimStatusType;
    
    /** 是否可收回（计算字段） */
    private Boolean isReclaimable;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setYearSemesterId(Long yearSemesterId) 
    {
        this.yearSemesterId = yearSemesterId;
    }

    public Long getYearSemesterId() 
    {
        return yearSemesterId;
    }

    public void setQuotaId(Long quotaId) 
    {
        this.quotaId = quotaId;
    }

    public Long getQuotaId() 
    {
        return quotaId;
    }

    public void setQuotaDetailId(Long quotaDetailId) 
    {
        this.quotaDetailId = quotaDetailId;
    }

    public Long getQuotaDetailId() 
    {
        return quotaDetailId;
    }
    
    public String getIsCarryover() 
    {
        return isCarryover;
    }
    
    public void setIsCarryover(String isCarryover) 
    {
        this.isCarryover = isCarryover;
    }
    
    public Long getSourceSemesterId() 
    {
        return sourceSemesterId;
    }
    
    public void setSourceSemesterId(Long sourceSemesterId) 
    {
        this.sourceSemesterId = sourceSemesterId;
    }

    public void setBudgetType(String budgetType) 
    {
        this.budgetType = budgetType;
    }

    public String getBudgetType() 
    {
        return budgetType;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) 
    {
        this.budgetAmount = budgetAmount;
    }

    public BigDecimal getBudgetAmount() 
    {
        return budgetAmount;
    }

    public void setUsedAmount(BigDecimal usedAmount) 
    {
        this.usedAmount = usedAmount;
    }

    public BigDecimal getUsedAmount() 
    {
        return usedAmount;
    }

    public void setLockedAmount(BigDecimal lockedAmount) 
    {
        this.lockedAmount = lockedAmount;
    }

    public BigDecimal getLockedAmount() 
    {
        return lockedAmount;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
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

    public String getSchoolYear() 
    {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) 
    {
        this.schoolYear = schoolYear;
    }

    public Integer getSemester() 
    {
        return semester;
    }

    public void setSemester(Integer semester) 
    {
        this.semester = semester;
    }

    public String getSemesterLabel() {
        return semesterLabel;
    }

    public void setSemesterLabel(String semesterLabel) {
        this.semesterLabel = semesterLabel;
    }

    public BigDecimal getAvailableAmount() 
    {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) 
    {
        this.availableAmount = availableAmount;
    }

    public String getQuotaDocNo() 
    {
        return quotaDocNo;
    }

    public void setQuotaDocNo(String quotaDocNo) 
    {
        this.quotaDocNo = quotaDocNo;
    }

    public String getBudgetProjectName() 
    {
        return budgetProjectName;
    }

    public void setBudgetProjectName(String budgetProjectName) 
    {
        this.budgetProjectName = budgetProjectName;
    }

    public String getEconomyCategory() 
    {
        return economyCategory;
    }

    public void setEconomyCategory(String economyCategory) 
    {
        this.economyCategory = economyCategory;
    }

    public String getFunctionCategory() 
    {
        return functionCategory;
    }
    
    public void setQuotaSourceType(Integer quotaSourceType) 
    {
        this.quotaSourceType = quotaSourceType;
    }

    public Boolean getCarryOverFlag() {
        return carryOverFlag;
    }

    public void setCarryOverFlag(Boolean carryOverFlag) {
        this.carryOverFlag = carryOverFlag;
    }

    public Integer getQuotaSourceType()
    {
        return quotaSourceType;
    }
    
    public Long getQuotaYearSemesterId()
    {
        return quotaYearSemesterId;
    }
    
    public void setQuotaYearSemesterId(Long quotaYearSemesterId)
    {
        this.quotaYearSemesterId = quotaYearSemesterId;
    }
    
    public Long getSourceDetailId()
    {
        return sourceDetailId;
    }
    
    public void setSourceDetailId(Long sourceDetailId)
    {
        this.sourceDetailId = sourceDetailId;
    }

    public String getReclaimStatus() 
    {
        return reclaimStatus;
    }

    public void setReclaimStatus(String reclaimStatus) 
    {
        this.reclaimStatus = reclaimStatus;
    }

    public String getReclaimStatusType() 
    {
        return reclaimStatusType;
    }

    public void setReclaimStatusType(String reclaimStatusType) 
    {
        this.reclaimStatusType = reclaimStatusType;
    }

    public Boolean getIsReclaimable() 
    {
        return isReclaimable;
    }

    public void setIsReclaimable(Boolean isReclaimable) 
    {
        this.isReclaimable = isReclaimable;
    }

    public void setFunctionCategory(String functionCategory) 
    {
        this.functionCategory = functionCategory;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("yearSemesterId", getYearSemesterId())
            .append("quotaId", getQuotaId())
            .append("budgetType", getBudgetType())
            .append("budgetAmount", getBudgetAmount())
            .append("usedAmount", getUsedAmount())
            .append("lockedAmount", getLockedAmount())
            .append("status", getStatus())
            .append("memo", getMemo())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
