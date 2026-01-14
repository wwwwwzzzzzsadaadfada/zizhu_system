package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学生补助发放记录对象 st_student_subsidy_records
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
public class StStudentSubsidyRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 学生学期记录ID（旧版兼容字段） */
    @Excel(name = "学生学期记录ID")
    private Long studentSemesterRecordId;

    /** 学生ID（新版直接关联学生） */
    @Excel(name = "学生ID")
    private Long studentId;

    /** 预算支出表ID */
    @Excel(name = "预算支出表ID")
    private Long budgetId;

    /** 补助类型 */
    @Excel(name = "补助类型")
    private String subsidyType;

    /** 补助金额 */
    @Excel(name = "补助金额")
    private BigDecimal subsidyAmount;

    /** 审批状态:0待审批,1已审批,2已驳回 */
    @Excel(name = "审批状态", dictType = "sys_approval_status")
    private Integer approvalStatus;

    /** 发放状态:0待发放,1已发放,2发放失败 */
    @Excel(name = "发放状态", dictType = "sys_payment_status")
    private Integer paymentStatus;

    /** 发放日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发放日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date paymentDate;

    /** 发放人 */
    @Excel(name = "发放人")
    private String paymentPerson;

    /** 审批人 */
    @Excel(name = "审批人")
    private String approver;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审批时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date approvalDate;

    /** 审批意见 */
    @Excel(name = "审批意见")
    private String approvalMemo;

    /** 备注 */
    @Excel(name = "备注")
    private String memo;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    /** 学生信息（关联查询，不存数据库） */
    private String studentName;
    private String idCardNo;
    private String className;
    private String gradeName;
    private String difficultyTypeId;  // 添加困难类型ID
    private String difficultyLevelId;  // 添加困难等级ID

    /** 资金来源学期ID（当前学期或历史学期） */
    @Excel(name = "资金来源学期ID")
    private Long sourceSemesterId;

    /** 学年学期ID（与资金来源一致，便于直接查询） */
    @Excel(name = "学年学期ID")
    private Long yearSemesterId;
    
    /** 补助套餐ID */
    @Excel(name = "补助套餐ID")
    private Long packageId;
    
    /** 学制ID（冗余，用于快速查询） */
    @Excel(name = "学制ID")
    private Long schoolingPlanId;

    /** 预算信息（关联查询/冗余） */
    private String budgetType;
    private BigDecimal budgetAvailableAmount;
    private String economyCategory;
    private String budgetProjectName;
    private String schoolYear;  // 添加学年
    private String semester;  // 添加学期
    
    /** 状态标签（关联查询，不存数据库） */
    private String approvalStatusLabel;  // 审批状态标签
    private String paymentStatusLabel;  // 发放状态标签
    private String semesterLabel;  // 学期标签

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setStudentSemesterRecordId(Long studentSemesterRecordId) 
    {
        this.studentSemesterRecordId = studentSemesterRecordId;
    }

    public Long getStudentSemesterRecordId() 
    {
        return studentSemesterRecordId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setBudgetId(Long budgetId) 
    {
        this.budgetId = budgetId;
    }

    public Long getBudgetId() 
    {
        return budgetId;
    }

    public void setSubsidyType(String subsidyType) 
    {
        this.subsidyType = subsidyType;
    }

    public String getSubsidyType() 
    {
        return subsidyType;
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

    public void setPaymentStatus(Integer paymentStatus) 
    {
        this.paymentStatus = paymentStatus;
    }

    public Integer getPaymentStatus() 
    {
        return paymentStatus;
    }

    public void setPaymentDate(Date paymentDate) 
    {
        this.paymentDate = paymentDate;
    }

    public Date getPaymentDate() 
    {
        return paymentDate;
    }

    public void setPaymentPerson(String paymentPerson) 
    {
        this.paymentPerson = paymentPerson;
    }

    public String getPaymentPerson() 
    {
        return paymentPerson;
    }

    public void setApprover(String approver) 
    {
        this.approver = approver;
    }

    public String getApprover() 
    {
        return approver;
    }

    public void setApprovalDate(Date approvalDate) 
    {
        this.approvalDate = approvalDate;
    }

    public Date getApprovalDate() 
    {
        return approvalDate;
    }

    public void setApprovalMemo(String approvalMemo) 
    {
        this.approvalMemo = approvalMemo;
    }

    public String getApprovalMemo() 
    {
        return approvalMemo;
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

    public String getStudentName() 
    {
        return studentName;
    }

    public void setStudentName(String studentName) 
    {
        this.studentName = studentName;
    }

    public String getIdCardNo() 
    {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) 
    {
        this.idCardNo = idCardNo;
    }

    public String getClassName() 
    {
        return className;
    }

    public void setClassName(String className) 
    {
        this.className = className;
    }

    public String getGradeName() 
    {
        return gradeName;
    }

    public void setGradeName(String gradeName) 
    {
        this.gradeName = gradeName;
    }

    public String getDifficultyTypeId() {
        return difficultyTypeId;
    }

    public void setDifficultyTypeId(String difficultyTypeId) {
        this.difficultyTypeId = difficultyTypeId;
    }

    public String getDifficultyLevelId() {
        return difficultyLevelId;
    }

    public void setDifficultyLevelId(String difficultyLevelId) {
        this.difficultyLevelId = difficultyLevelId;
    }

    public String getBudgetType() 
    {
        return budgetType;
    }

    public void setBudgetType(String budgetType) 
    {
        this.budgetType = budgetType;
    }

    public BigDecimal getBudgetAvailableAmount() 
    {
        return budgetAvailableAmount;
    }

    public void setBudgetAvailableAmount(BigDecimal budgetAvailableAmount) 
    {
        this.budgetAvailableAmount = budgetAvailableAmount;
    }

    public String getEconomyCategory() 
    {
        return economyCategory;
    }

    public void setEconomyCategory(String economyCategory) 
    {
        this.economyCategory = economyCategory;
    }

    public String getBudgetProjectName() 
    {
        return budgetProjectName;
    }

    public void setBudgetProjectName(String budgetProjectName) 
    {
        this.budgetProjectName = budgetProjectName;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Long getSourceSemesterId() {
        return sourceSemesterId;
    }

    public void setSourceSemesterId(Long sourceSemesterId) {
        this.sourceSemesterId = sourceSemesterId;
    }

    public Long getYearSemesterId() {
        return yearSemesterId;
    }

    public void setYearSemesterId(Long yearSemesterId) {
        this.yearSemesterId = yearSemesterId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getSchoolingPlanId() {
        return schoolingPlanId;
    }

    public void setSchoolingPlanId(Long schoolingPlanId) {
        this.schoolingPlanId = schoolingPlanId;
    }

    public String getApprovalStatusLabel() {
        return approvalStatusLabel;
    }

    public void setApprovalStatusLabel(String approvalStatusLabel) {
        this.approvalStatusLabel = approvalStatusLabel;
    }

    public String getPaymentStatusLabel() {
        return paymentStatusLabel;
    }

    public void setPaymentStatusLabel(String paymentStatusLabel) {
        this.paymentStatusLabel = paymentStatusLabel;
    }

    public String getSemesterLabel() {
        return semesterLabel;
    }

    public void setSemesterLabel(String semesterLabel) {
        this.semesterLabel = semesterLabel;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("studentSemesterRecordId", getStudentSemesterRecordId())
            .append("studentId", getStudentId())
            .append("budgetId", getBudgetId())
            .append("yearSemesterId", getYearSemesterId())
            .append("sourceSemesterId", getSourceSemesterId())
            .append("subsidyType", getSubsidyType())
            .append("subsidyAmount", getSubsidyAmount())
            .append("approvalStatus", getApprovalStatus())
            .append("paymentStatus", getPaymentStatus())
            .append("approvalStatusLabel", getApprovalStatusLabel())
            .append("paymentStatusLabel", getPaymentStatusLabel())
            .append("semesterLabel", getSemesterLabel())
            .append("paymentDate", getPaymentDate())
            .append("approver", getApprover())
            .append("approvalDate", getApprovalDate())
            .append("approvalMemo", getApprovalMemo())
            .append("memo", getMemo())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
