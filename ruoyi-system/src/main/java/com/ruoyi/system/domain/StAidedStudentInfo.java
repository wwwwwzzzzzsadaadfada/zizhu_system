package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 受助学生信息对象 st_aided_student_info
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public class StAidedStudentInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 学生基础信息表ID */
    @Excel(name = "学生基础信息表ID")
    private Long studentId;

    /** 学号 */
    @Excel(name = "学号")
    private String studentNo;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String studentName;

    /** 身份证号 */
    @Excel(name = "身份证号")
    private String idCard;

    /** 性别（0男 1女 2未知） */
    @Excel(name = "性别", dictType = "sys_student_gender")
    private String gender;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date birthday;

    /** 政治面貌 */
    @Excel(name = "政治面貌")
    private String politicalStatus;

    /** 民族编码 */
    private String nation;

    /** 民族名称 */
    @Excel(name = "民族")
    private String nationName;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String phone;

    /** 户籍所在地 */
    @Excel(name = "户籍所在地")
    private String domicile;

    /** 是否民族高中班 1=是 0=否 */
    @Excel(name = "是否民族高中班", readConverterExp = "0=否,1=是")
    private String isEthnicClass;

    /** 学院ID */
    @Excel(name = "学院ID")
    private Long collegeId;

    /** 学院名称 */
    @Excel(name = "学院名称")
    private String collegeName;

    /** 专业ID */
    @Excel(name = "专业ID")
    private Long majorId;

    /** 专业名称 */
    @Excel(name = "专业名称")
    private String majorName;

    /** 年级 */
    @Excel(name = "年级")
    private String grade;

    /** 年级ID（数据库字段） */
    private Long gradeIdDb;

    /** 班级ID */
    @Excel(name = "班级ID")
    private Long clazzId;

    /** 班级名称 */
    @Excel(name = "班级名称")
    private String clazzName;

    /** 入学日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入学日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date enrollmentDate;

    /** 毕业日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "毕业日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date graduationDate;

    /** 就读状态 */
    @Excel(name = "就读状态")
    private String studyStatus;

    /** 最近一次升年级日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最近一次升年级日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastGradeUpdate;

    /** 学年 */
    @Excel(name = "学年")
    private String academicYear;

    /** 学期 */
    @Excel(name = "学期")
    private String semester;

    /** 当前学年学期ID（数据库字段） */
    private Long currentYearSemesterId;

    /** 困难类型（字典值） */
    @Excel(name = "困难类型")
    private String difficultyTypeId;

    /** 困难等级（字典值） */
    @Excel(name = "困难等级")
    private String difficultyLevelId;

    /** 是否脱贫户 */
    @Excel(name = "是否脱贫户", readConverterExp = "0=否,1=是")
    private String isPovertyReliefFamily;

    /** 脱贫年份 */
    @Excel(name = "脱贫年份")
    private Integer povertyReliefYear;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 审批状态（关联查询，不存数据库） */
    private Integer approvalStatus;

    /** 补助记录金额（关联查询，不存数据库，从补助记录表获取） */
    private BigDecimal recordSubsidyAmount;

    /** 助学金金额（关联查询，不存数据库） */
    private BigDecimal scholarshipAmount;

    /** 免学杂费金额（关联查询，不存数据库） */
    private BigDecimal tuitionWaiverAmount;

    /** 所属学制ID（数据库字段） */
    private Long schoolingPlanIdDb;

    /** 学制ID（关联字段，用于筛选，从st_students_base表获取） */
    private Long schoolingPlanId;

    /** 年级ID（关联字段，用于筛选，从st_students_base表获取） */
    private Long gradeId;

    /** 班级ID（查询参数，用于兼容前端传递的classId参数） */
    private Long classId;

    /** 功能分类（由后端根据学制计算，不存数据库） */
    private String functionCategory;

    /** 是否排除有补助记录的学生（查询参数，不存数据库，用于批量发放时过滤） */
    private Boolean excludeWithSubsidyRecords;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setStudentId(Long studentId) 
    {
        this.studentId = studentId;
    }

    public Long getStudentId() 
    {
        return studentId;
    }
    public void setStudentNo(String studentNo) 
    {
        this.studentNo = studentNo;
    }

    public String getStudentNo() 
    {
        return studentNo;
    }
    public void setStudentName(String studentName) 
    {
        this.studentName = studentName;
    }

    public String getStudentName() 
    {
        return studentName;
    }
    public void setIdCard(String idCard) 
    {
        this.idCard = idCard;
    }

    public String getIdCard() 
    {
        return idCard;
    }
    public void setGender(String gender) 
    {
        this.gender = gender;
    }

    public String getGender() 
    {
        return gender;
    }
    public void setBirthday(Date birthday) 
    {
        this.birthday = birthday;
    }

    public Date getBirthday() 
    {
        return birthday;
    }
    public void setPoliticalStatus(String politicalStatus) 
    {
        this.politicalStatus = politicalStatus;
    }

    public String getPoliticalStatus() 
    {
        return politicalStatus;
    }
    public void setNation(String nation) 
    {
        this.nation = nation;
    }

    public String getNation() 
    {
        return nation;
    }
    public void setNationName(String nationName) 
    {
        this.nationName = nationName;
    }

    public String getNationName() 
    {
        return nationName;
    }
    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }
    public void setDomicile(String domicile) 
    {
        this.domicile = domicile;
    }

    public String getDomicile() 
    {
        return domicile;
    }
    public void setIsEthnicClass(String isEthnicClass) 
    {
        this.isEthnicClass = isEthnicClass;
    }

    public String getIsEthnicClass() 
    {
        return isEthnicClass;
    }
    public void setCollegeId(Long collegeId) 
    {
        this.collegeId = collegeId;
    }

    public Long getCollegeId() 
    {
        return collegeId;
    }
    public void setCollegeName(String collegeName) 
    {
        this.collegeName = collegeName;
    }

    public String getCollegeName() 
    {
        return collegeName;
    }
    public void setMajorId(Long majorId) 
    {
        this.majorId = majorId;
    }

    public Long getMajorId() 
    {
        return majorId;
    }
    public void setMajorName(String majorName) 
    {
        this.majorName = majorName;
    }

    public String getMajorName() 
    {
        return majorName;
    }
    public void setGrade(String grade) 
    {
        this.grade = grade;
    }

    public String getGrade() 
    {
        return grade;
    }
    public void setClazzId(Long clazzId) 
    {
        this.clazzId = clazzId;
    }

    public Long getClazzId() 
    {
        return clazzId;
    }
    public void setClazzName(String clazzName) 
    {
        this.clazzName = clazzName;
    }

    public String getClazzName() 
    {
        return clazzName;
    }
    public void setEnrollmentDate(Date enrollmentDate) 
    {
        this.enrollmentDate = enrollmentDate;
    }

    public Date getEnrollmentDate() 
    {
        return enrollmentDate;
    }
    public void setGraduationDate(Date graduationDate) 
    {
        this.graduationDate = graduationDate;
    }

    public Date getGraduationDate() 
    {
        return graduationDate;
    }
    public void setStudyStatus(String studyStatus) 
    {
        this.studyStatus = studyStatus;
    }

    public String getStudyStatus() 
    {
        return studyStatus;
    }
    public void setLastGradeUpdate(Date lastGradeUpdate) 
    {
        this.lastGradeUpdate = lastGradeUpdate;
    }

    public Date getLastGradeUpdate() 
    {
        return lastGradeUpdate;
    }
    public void setAcademicYear(String academicYear) 
    {
        this.academicYear = academicYear;
    }

    public String getAcademicYear() 
    {
        return academicYear;
    }
    public void setSemester(String semester) 
    {
        this.semester = semester;
    }

    public String getSemester() 
    {
        return semester;
    }
    public void setCurrentYearSemesterId(Long currentYearSemesterId) 
    {
        this.currentYearSemesterId = currentYearSemesterId;
    }

    public Long getCurrentYearSemesterId() 
    {
        return currentYearSemesterId;
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
    public void setIsPovertyReliefFamily(String isPovertyReliefFamily)
    {
        this.isPovertyReliefFamily = isPovertyReliefFamily;
    }

    public String getIsPovertyReliefFamily()
    {
        return isPovertyReliefFamily;
    }
    public void setPovertyReliefYear(Integer povertyReliefYear)
    {
        this.povertyReliefYear = povertyReliefYear;
    }

    public Integer getPovertyReliefYear()
    {
        return povertyReliefYear;
    }
    public void setRemark(String remark) 
    {
        this.remark = remark;
    }

    public String getRemark() 
    {
        return remark;
    }
    public void setApprovalStatus(Integer approvalStatus) 
    {
        this.approvalStatus = approvalStatus;
    }

    public Integer getApprovalStatus() 
    {
        return approvalStatus;
    }
    public void setRecordSubsidyAmount(BigDecimal recordSubsidyAmount) 
    {
        this.recordSubsidyAmount = recordSubsidyAmount;
    }

    public BigDecimal getRecordSubsidyAmount() 
    {
        return recordSubsidyAmount;
    }

    public void setScholarshipAmount(BigDecimal scholarshipAmount) 
    {
        this.scholarshipAmount = scholarshipAmount;
    }

    public BigDecimal getScholarshipAmount() 
    {
        return scholarshipAmount;
    }

    public void setTuitionWaiverAmount(BigDecimal tuitionWaiverAmount) 
    {
        this.tuitionWaiverAmount = tuitionWaiverAmount;
    }

    public BigDecimal getTuitionWaiverAmount() 
    {
        return tuitionWaiverAmount;
    }

    public void setSchoolingPlanIdDb(Long schoolingPlanIdDb)
    {
        this.schoolingPlanIdDb = schoolingPlanIdDb;
    }

    public Long getSchoolingPlanIdDb()
    {
        return schoolingPlanIdDb;
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

    public void setGradeIdDb(Long gradeIdDb)
    {
        this.gradeIdDb = gradeIdDb;
    }

    public Long getGradeIdDb()
    {
        return gradeIdDb;
    }

    public void setClassId(Long classId)
    {
        this.classId = classId;
    }

    public Long getClassId()
    {
        return classId;
    }

    public void setExcludeWithSubsidyRecords(Boolean excludeWithSubsidyRecords) 
    {
        this.excludeWithSubsidyRecords = excludeWithSubsidyRecords;
    }

    public Boolean getExcludeWithSubsidyRecords() 
    {
        return excludeWithSubsidyRecords;
    }

    public void setFunctionCategory(String functionCategory)
    {
        this.functionCategory = functionCategory;
    }

    public String getFunctionCategory()
    {
        return functionCategory;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("studentId", getStudentId())
            .append("studentNo", getStudentNo())
            .append("studentName", getStudentName())
            .append("idCard", getIdCard())
            .append("gender", getGender())
            .append("birthday", getBirthday())
            .append("politicalStatus", getPoliticalStatus())
            .append("nation", getNation())
            .append("nationName", getNationName())
            .append("phone", getPhone())
            .append("collegeId", getCollegeId())
            .append("collegeName", getCollegeName())
            .append("majorId", getMajorId())
            .append("majorName", getMajorName())
            .append("grade", getGrade())
            .append("clazzId", getClazzId())
            .append("clazzName", getClazzName())
            .append("enrollmentDate", getEnrollmentDate())
            .append("graduationDate", getGraduationDate())
            .append("academicYear", getAcademicYear())
            .append("semester", getSemester())
            .append("difficultyTypeId", getDifficultyTypeId())
            .append("difficultyLevelId", getDifficultyLevelId())
            .append("isPovertyReliefFamily", getIsPovertyReliefFamily())
            .append("povertyReliefYear", getPovertyReliefYear())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}