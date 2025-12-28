package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资助指标下达对象 st_subsidy_quota
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
public class StSubsidyQuota extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 学年学期ID */
    @Excel(name = "学年学期")
    private Long yearSemesterId;

    /** 发文时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发文时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date issueDate;

    /** 指标文号名称 */
    @Excel(name = "指标文号")
    private String quotaDocNo;

    /** 预算项目名称 */
    @Excel(name = "预算项目名称")
    private String budgetProjectName;

    /** 总指标(总金额) */
    @Excel(name = "总指标")
    private BigDecimal totalQuota;

    /** 功能分类名称 */
    @Excel(name = "功能分类", dictType = "sys_function_category")
    private String functionCategory;

    /** 部门经济分类名称 */
    @Excel(name = "经济分类", dictType = "sys_economy_category")
    private String economyCategory;

    /** 预算级次 */
    @Excel(name = "预算级次", dictType = "sys_budget_level")
    private String budgetLevel;

    /** 已分配金额 */
    @Excel(name = "已分配金额")
    private BigDecimal allocatedAmount;

    /** 状态 */
    @Excel(name = "状态", dictType = "sys_quota_status")
    private Integer status;

    /** 指标来源类型:1本学期新下达,2上学期结转,3历史学期结转 */
    @Excel(name = "指标来源", dictType = "sys_quota_source_type")
    private Integer quotaSourceType;

    /** 来源指标ID（结转时记录原指标ID） */
    private Long sourceQuotaId;
    
    /** 是否结转资金（0-否 1-是） */
    @Excel(name = "是否结转资金")
    private String isCarryover;
    
    /** 来源学期ID（结转时记录原学期） */
    private Long sourceSemesterId;
    
    /** 来源指标文号（关联查询，不存数据库） */
    private String sourceQuotaDocNo;

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
    /** 学期中文名称（秋季学期/春季学期，由后端SQL计算，不存数据库） */
    private String semesterLabel;
    
    /** 可用指标金额（计算字段：total_quota - allocated_amount，不存数据库） */
    private BigDecimal availableQuota;
    
    /** 上期结余金额（计算字段：历史学期的 total_quota - allocated_amount，不存数据库） */
    private BigDecimal carryOverAmount;
    
    /** 是否为历史学期（计算字段，不存数据库） */
    private Boolean isHistoricalSemester;
    
    /** 是否允许结转（计算字段，不存数据库） */
    private Boolean canCarryOver;
    
    /** 是否已结转（计算字段：历史学期且已分配金额>0，不存数据库） */
    private Boolean hasCarriedOver;
    
    /** 经济分类文本（计算字段：格式化后的文本，如"助学金、免学杂费"，不存数据库） */
    private String economyCategoryText;
    
    /** 经济分类明细列表（关联查询，不存数据库） */
    private List<StSubsidyQuotaDetail> detailList;
    
    /** 结转金额（计算字段：该指标结转到其他指标的金额，不存数据库） */
    private BigDecimal carriedOverAmount;
    
    /** 分配到预算的金额（计算字段：allocatedAmount - carriedOverAmount，不存数据库） */
    private BigDecimal allocatedToBudget;

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

    public void setIssueDate(Date issueDate) 
    {
        this.issueDate = issueDate;
    }

    public Date getIssueDate() 
    {
        return issueDate;
    }

    public void setQuotaDocNo(String quotaDocNo) 
    {
        this.quotaDocNo = quotaDocNo;
    }

    public String getQuotaDocNo() 
    {
        return quotaDocNo;
    }

    public void setBudgetProjectName(String budgetProjectName) 
    {
        this.budgetProjectName = budgetProjectName;
    }

    public String getBudgetProjectName() 
    {
        return budgetProjectName;
    }

    public void setTotalQuota(BigDecimal totalQuota) 
    {
        this.totalQuota = totalQuota;
    }

    public BigDecimal getTotalQuota() 
    {
        return totalQuota;
    }

    public void setFunctionCategory(String functionCategory) 
    {
        this.functionCategory = functionCategory;
    }

    public String getFunctionCategory() 
    {
        return functionCategory;
    }

    public void setEconomyCategory(String economyCategory) 
    {
        this.economyCategory = economyCategory;
    }

    public String getEconomyCategory() 
    {
        return economyCategory;
    }

    public void setBudgetLevel(String budgetLevel) 
    {
        this.budgetLevel = budgetLevel;
    }

    public String getBudgetLevel() 
    {
        return budgetLevel;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) 
    {
        this.allocatedAmount = allocatedAmount;
    }

    public BigDecimal getAllocatedAmount() 
    {
        return allocatedAmount;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public void setQuotaSourceType(Integer quotaSourceType) 
    {
        this.quotaSourceType = quotaSourceType;
    }

    public Integer getQuotaSourceType() 
    {
        return quotaSourceType;
    }

    public void setSourceQuotaId(Long sourceQuotaId) 
    {
        this.sourceQuotaId = sourceQuotaId;
    }

    public Long getSourceQuotaId() 
    {
        return sourceQuotaId;
    }

    public String getSourceQuotaDocNo() 
    {
        return sourceQuotaDocNo;
    }

    public void setSourceQuotaDocNo(String sourceQuotaDocNo) 
    {
        this.sourceQuotaDocNo = sourceQuotaDocNo;
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

    public String getSemesterLabel() 
    {
        return semesterLabel;
    }

    public void setSemesterLabel(String semesterLabel) 
    {
        this.semesterLabel = semesterLabel;
    }

    public BigDecimal getAvailableQuota() 
    {
        return availableQuota;
    }

    public void setAvailableQuota(BigDecimal availableQuota) 
    {
        this.availableQuota = availableQuota;
    }

    public BigDecimal getCarryOverAmount() 
    {
        return carryOverAmount;
    }

    public void setCarryOverAmount(BigDecimal carryOverAmount) 
    {
        this.carryOverAmount = carryOverAmount;
    }

    public Boolean getIsHistoricalSemester() 
    {
        return isHistoricalSemester;
    }

    public void setIsHistoricalSemester(Boolean isHistoricalSemester) 
    {
        this.isHistoricalSemester = isHistoricalSemester;
    }

    public Boolean getCanCarryOver() 
    {
        return canCarryOver;
    }

    public void setCanCarryOver(Boolean canCarryOver) 
    {
        this.canCarryOver = canCarryOver;
    }

    public Boolean getHasCarriedOver() 
    {
        return hasCarriedOver;
    }

    public void setHasCarriedOver(Boolean hasCarriedOver) 
    {
        this.hasCarriedOver = hasCarriedOver;
    }

    public String getEconomyCategoryText() 
    {
        return economyCategoryText;
    }

    public void setEconomyCategoryText(String economyCategoryText) 
    {
        this.economyCategoryText = economyCategoryText;
    }

    public List<StSubsidyQuotaDetail> getDetailList() 
    {
        return detailList;
    }

    public void setDetailList(List<StSubsidyQuotaDetail> detailList) 
    {
        this.detailList = detailList;
    }
    
    public BigDecimal getCarriedOverAmount() 
    {
        return carriedOverAmount;
    }
    
    public void setCarriedOverAmount(BigDecimal carriedOverAmount) 
    {
        this.carriedOverAmount = carriedOverAmount;
    }
    
    public BigDecimal getAllocatedToBudget() 
    {
        return allocatedToBudget;
    }
    
    public void setAllocatedToBudget(BigDecimal allocatedToBudget) 
    {
        this.allocatedToBudget = allocatedToBudget;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("yearSemesterId", getYearSemesterId())
            .append("issueDate", getIssueDate())
            .append("quotaDocNo", getQuotaDocNo())
            .append("budgetProjectName", getBudgetProjectName())
            .append("totalQuota", getTotalQuota())
            .append("functionCategory", getFunctionCategory())
            .append("economyCategory", getEconomyCategory())
            .append("budgetLevel", getBudgetLevel())
            .append("allocatedAmount", getAllocatedAmount())
            .append("status", getStatus())
            .append("quotaSourceType", getQuotaSourceType())
            .append("sourceQuotaId", getSourceQuotaId())
            .append("memo", getMemo())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
