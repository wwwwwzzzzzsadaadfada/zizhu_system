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
 * 学生学期认定记录对象 st_student_semester_records
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
public class StStudentSemesterRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 学年学期ID */
    @Excel(name = "学年学期ID")
    private Long yearSemesterId;

    /** 学生基础信息ID */
    @Excel(name = "学生ID")
    private Long studentBaseId;

    /** 所属学制 */
    @Excel(name = "所属学制")
    private Long schoolingPlanId;

    /** 所属年级 */
    @Excel(name = "所属年级")
    private Long gradeId;

    /** 所属班级 */
    @Excel(name = "所属班级")
    private Long classId;

    /** 就读状态（1=在校，2=离校） */
    @Excel(name = "就读状态", dictType = "sys_study_status")
    private String studyStatus;

    /** 困难类型 */
    @Excel(name = "困难类型", dictType = "sys_difficulty_type")
    private String difficultyTypeId;

    /** 困难等级 */
    @Excel(name = "困难等级", dictType = "sys_difficulty_level")
    private String difficultyLevelId;

    /** 脱贫年份（仅当困难类型为脱贫户时填写） */
    @Excel(name = "脱贫年份")
    private Integer povertyReliefYear;
    
    /** 关联的学期预算ID */
    @Excel(name = "学期预算ID")
    private Long budgetId;
    
    /** 关联的资助指标ID */
    @Excel(name = "资助指标ID")
    private Long quotaId;
    
    /** 关联指标明细ID */
    @Excel(name = "指标明细ID")
    private Long quotaDetailId;
    
    /** 经济分类（助学金/免学杂费） */
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

    // ==================== 关联查询字段（不存数据库） ====================
    
    /** 学年 */
    private String schoolYear;
    
    /** 学期 */
    private Integer semester;
    
    /** 学生信息（关联查询，不存数据库） */
    private String name;
    private String idCardNo;
    private String gender;
    private String ethnicity;
    private String domicile;
    private String studentNo;
    
    /** 学制信息（关联查询，不存数据库） */
    private String schoolingPlanName;
    private Integer schoolingYears;
    
    /** 年级班级信息（关联查询，不存数据库） */
    private String gradeName;
    private String className;
    
    /** 指标预算信息（关联查询，不存数据库） */
    private String budgetName;
    private String quotaName;
    private String budgetType;  // 添加budgetType字段
    private String budgetProjectName;  // 添加budgetProjectName字段
    
    /** 学生受助明细列表（关联查询，不存数据库） */
    private List<StStudentSubsidyDetail> subsidyDetails;

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

    public void setStudentBaseId(Long studentBaseId) 
    {
        this.studentBaseId = studentBaseId;
    }

    public Long getStudentBaseId() 
    {
        return studentBaseId;
    }

    public void setSchoolingPlanId(Long schoolingPlanId) 
    {
        this.schoolingPlanId = schoolingPlanId;
    }

    public Long getSchoolingPlanId() 
    {
        return schoolingPlanId;
    }

    public void setGradeId(Long gradeId) 
    {
        this.gradeId = gradeId;
    }

    public Long getGradeId() 
    {
        return gradeId;
    }

    public void setClassId(Long classId) 
    {
        this.classId = classId;
    }

    public Long getClassId() 
    {
        return classId;
    }

    public void setStudyStatus(String studyStatus) 
    {
        this.studyStatus = studyStatus;
    }

    public String getStudyStatus() 
    {
        return studyStatus;
    }

    public void setDifficultyTypeId(String difficultyTypeId) 
    {
        this.difficultyTypeId = difficultyTypeId;
    }

    public String getDifficultyTypeId() 
    {
        return difficultyTypeId;
    }

    public void setDifficultyLevelId(String difficultyLevelId) 
    {
        this.difficultyLevelId = difficultyLevelId;
    }

    public String getDifficultyLevelId() 
    {
        return difficultyLevelId;
    }

    public void setPovertyReliefYear(Integer povertyReliefYear) 
    {
        this.povertyReliefYear = povertyReliefYear;
    }

    public Integer getPovertyReliefYear() 
    {
        return povertyReliefYear;
    }
    
    public void setBudgetId(Long budgetId) 
    {
        this.budgetId = budgetId;
    }

    public Long getBudgetId() 
    {
        return budgetId;
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

    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getIdCardNo() 
    {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) 
    {
        this.idCardNo = idCardNo;
    }

    public String getGender() 
    {
        return gender;
    }

    public void setGender(String gender) 
    {
        this.gender = gender;
    }

    public String getEthnicity() 
    {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) 
    {
        this.ethnicity = ethnicity;
    }

    public String getDomicile() 
    {
        return domicile;
    }

    public void setDomicile(String domicile) 
    {
        this.domicile = domicile;
    }

    public String getStudentNo() 
    {
        return studentNo;
    }

    public void setStudentNo(String studentNo) 
    {
        this.studentNo = studentNo;
    }

    public String getSchoolingPlanName() 
    {
        return schoolingPlanName;
    }

    public void setSchoolingPlanName(String schoolingPlanName) 
    {
        this.schoolingPlanName = schoolingPlanName;
    }

    public Integer getSchoolingYears() 
    {
        return schoolingYears;
    }

    public void setSchoolingYears(Integer schoolingYears) 
    {
        this.schoolingYears = schoolingYears;
    }

    public String getGradeName() 
    {
        return gradeName;
    }

    public void setGradeName(String gradeName) 
    {
        this.gradeName = gradeName;
    }

    public String getClassName() 
    {
        return className;
    }

    public void setClassName(String className) 
    {
        this.className = className;
    }

    public String getBudgetName() 
    {
        return budgetName;
    }

    public void setBudgetName(String budgetName) 
    {
        this.budgetName = budgetName;
    }

    public String getQuotaName() 
    {
        return quotaName;
    }

    public String getBudgetType() 
    {
        return budgetType;
    }

    public void setBudgetType(String budgetType) 
    {
        this.budgetType = budgetType;
    }

    public String getBudgetProjectName() 
    {
        return budgetProjectName;
    }

    public void setBudgetProjectName(String budgetProjectName) 
    {
        this.budgetProjectName = budgetProjectName;
    }

    public void setSubsidyDetails(List<StStudentSubsidyDetail> subsidyDetails) 
    {
        this.subsidyDetails = subsidyDetails;
    }

    public List<StStudentSubsidyDetail> getSubsidyDetails() 
    {
        return subsidyDetails;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("yearSemesterId", getYearSemesterId())
            .append("studentBaseId", getStudentBaseId())
            .append("gender", getGender())
            .append("schoolingPlanId", getSchoolingPlanId())
            .append("gradeId", getGradeId())
            .append("classId", getClassId())
            .append("studyStatus", getStudyStatus())
            .append("difficultyTypeId", getDifficultyTypeId())
            .append("difficultyLevelId", getDifficultyLevelId())
            .append("povertyReliefYear", getPovertyReliefYear())
            .append("subsidyAmount", getSubsidyAmount())
            .append("approvalStatus", getApprovalStatus())
            .append("memo", getMemo())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}