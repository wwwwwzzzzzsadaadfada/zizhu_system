package com.ruoyi.system.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 学期预算视图对象
 *
 * @author
 */
public class BudgetVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long yearSemesterId;
    private String schoolYear;
    private Integer semester;
    private String budgetType;
    private String budgetProjectName;
    private String economyCategory;
    private String functionCategory;
    private BigDecimal budgetAmount;
    private BigDecimal usedAmount;
    private BigDecimal lockedAmount;
    private BigDecimal availableAmount;
    
    // 预算状态:1启用,2冻结,3已用完,4作废
    private Integer status;
    
    // 指标相关信息（关联查询，不存数据库）
    private String quotaDocNo;          // 指标文号
    private Integer quotaSourceType;     // 指标来源类型（1=本学期新下达, 2=上学期结转, 3=历史学期结转）
    /** 是否结转生成的预算（后端计算） */
    private Boolean carryOverFlag;
    
    /** 是否为历史结余预算（后端计算，用于前端显示） */
    private Boolean isHistorical;
    
    /** 预算来源类型标签（用于前端显示，格式：学期下达、结余（小学）、结余（初中）） */
    private String sourceTypeLabel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getYearSemesterId() {
        return yearSemesterId;
    }

    public void setYearSemesterId(Long yearSemesterId) {
        this.yearSemesterId = yearSemesterId;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(String budgetType) {
        this.budgetType = budgetType;
    }

    public String getBudgetProjectName() {
        return budgetProjectName;
    }

    public void setBudgetProjectName(String budgetProjectName) {
        this.budgetProjectName = budgetProjectName;
    }

    public String getEconomyCategory() {
        return economyCategory;
    }

    public void setEconomyCategory(String economyCategory) {
        this.economyCategory = economyCategory;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
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

    public String getFunctionCategory() {
        return functionCategory;
    }

    public void setFunctionCategory(String functionCategory) {
        this.functionCategory = functionCategory;
    }

    public String getQuotaDocNo() {
        return quotaDocNo;
    }

    public void setQuotaDocNo(String quotaDocNo) {
        this.quotaDocNo = quotaDocNo;
    }

    public Integer getQuotaSourceType() {
        return quotaSourceType;
    }

    public void setQuotaSourceType(Integer quotaSourceType) {
        this.quotaSourceType = quotaSourceType;
    }

    public Boolean getCarryOverFlag() {
        return carryOverFlag;
    }

    public void setCarryOverFlag(Boolean carryOverFlag) {
        this.carryOverFlag = carryOverFlag;
    }

    public Boolean getIsHistorical() {
        return isHistorical;
    }

    public void setIsHistorical(Boolean isHistorical) {
        this.isHistorical = isHistorical;
    }

    public String getSourceTypeLabel() {
        return sourceTypeLabel;
    }

    public void setSourceTypeLabel(String sourceTypeLabel) {
        this.sourceTypeLabel = sourceTypeLabel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}



