package com.ruoyi.system.domain;

import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 困难学生基础信息对象 st_students
 * 
 * @author ruoyi
 * @date 2025-11-19
 */
public class StStudents extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

    /** 姓名 */
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 50, message = "姓名长度应在2-50之间")
    @Excel(name = "*姓名", cellType = Excel.ColumnType.STRING, prompt = "请输入姓名，必填项")
    private String name;

    /** 身份证号 */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{18}$|^\\d{17}[Xx]$", message = "身份证号格式不正确(18位数字或17位数字+X)")
    @Excel(name = "*身份证号", cellType = Excel.ColumnType.STRING, prompt = "请输入18位身份证号，最后一位可以是X，必填项")
    private String idCardNo;

    /** 性别 */
    @Pattern(regexp = "^[01]?$", message = "性别值不正确")
    @Excel(name = "性别", dictType = "sys_student_gender", prompt = "请输入性别，如：男、女")
    private String gender;

    /** 民族 */
    @Size(max = 10, message = "民族不超过10字符")
    @Excel(name = "民族", dictType = "sys_student_ethnicity", prompt = "请输入民族，如：汉族、回族等")
    private String ethnicity;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", width = 30, dateFormat = "yyyy-MM-dd", prompt = "请输入出生日期，格式：yyyy-MM-dd")
    private Date birthday;

    /** 联系电话 */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    @Excel(name = "联系电话", prompt = "请输入11位手机号码")
    private String phone;

    /** 政治面貌 */
    @Size(max = 20, message = "政治面貌不超过20字符")
    @Excel(name = "政治面貌", prompt = "请输入政治面貌，如：共青团员、党员等")
    private String politicalStatus;

    /** 入学时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入学时间", width = 30, dateFormat = "yyyy-MM-dd", prompt = "请输入入学时间，格式：yyyy-MM-dd")
    private Date enrollmentDate;

    /** 是否民族高中班 */
    @Pattern(regexp = "^[01]?$", message = "是否民族高中班只能为0或1")
    @Excel(name = "是否民族高中班", readConverterExp = "0=否,1=是", prompt = "请输入是否民族高中班，0=否，1=是")
    private String isEthnicClass;

    /** 最近一次升年级日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最近升年级日期", width = 30, dateFormat = "yyyy-MM-dd", type = Excel.Type.EXPORT)
    private Date lastGradeUpdate;

    /** 户籍所在地 */
    @Size(max = 200, message = "户籍所在地不超过200字符")
    @Excel(name = "户籍所在地", prompt = "请输入户籍所在地，不超过200字符")
    private String domicile;

    /** 学籍号 */
    @NotBlank(message = "学籍号不能为空")
    @Size(max = 32, message = "学籍号不超过32字符")
    @Excel(name = "*学籍号", cellType = Excel.ColumnType.STRING, prompt = "请输入学籍号，必填项")
    private String studentNo;

    /** 学制 */
    @Excel(name = "*学制ID", prompt = "请输入学制ID（1-六年小学，2-三年初中，3-三年普通高中，请参考st_schooling_plans表），必填项")
    private Long schoolingPlanId;
    
    /** 学制名称（冗余，用于显示，导入时接收名称） */
    @Excel(name = "学制名称", prompt = "学制名称（如：六年小学、三年初中、三年普通高中等，请参考st_schooling_plans表）")
    private String schoolingPlanName;

    /** 当前年级 */
    @Excel(name = "*年级ID", prompt = "请输入年级ID（1-七年级，2-高一，3-高二，4-高三，5-六年级，请参考st_grades表），必填项")
    private Long gradeId;
    
    /** 年级名称（冗余，用于显示，导入时接收名称） */
    @Excel(name = "年级名称", prompt = "年级名称（如：七年级、高一、高二、高三、六年级等，请参考st_grades表）")
    private String gradeName;

    /** 当前班级 */
    @Excel(name = "*班级ID", prompt = "请输入班级ID（请参考st_class_info表中各年级对应的班级），必填项")
    private Long classId;
    
    /** 班级名称（冗余，用于显示，导入时接收名称） */
    @Excel(name = "班级名称", prompt = "班级名称（如：1班、2班等，请参考st_class_info表）")
    private String className;

    /** 当前学年学期ID */
    private Long currentYearSemesterId;

    /** 学年学期ID（前端查询参数，兼容旧接口） */
    private Long yearSemesterId;

    /** 当前学年（冗余，用于显示） */
    @Excel(name = "当前学年", type = Excel.Type.EXPORT)
    private String currentSchoolYear;

    /** 当前学期（冗余，用于显示，1=第一学期，2=第二学期） */
    @Excel(name = "当前学期", type = Excel.Type.EXPORT)
    private Integer currentSemester;
    
    /** 当前学期中文名称（秋季学期/春季学期，由后端SQL计算） */
    @Excel(name = "当前学期", type = Excel.Type.EXPORT)
    private String currentSemesterLabel;

    /** 学制年限 */
    @Excel(name = "学制年限", type = Excel.Type.EXPORT)
    private Integer schoolingYears;

    /** 就读状态 */
    @Pattern(regexp = "^[01]?$", message = "就读状态值不正确")
    @Excel(name = "就读状态", dictType = "sys_study_status", prompt = "请输入就读状态，如：在读、毕业")
    private String studyStatus;

    /** 困难类型 */
    @Excel(name = "困难类型", dictType = "sys_difficulty_type", prompt = "请输入困难类型，如：脱贫户、低保户、特困供养家庭、孤儿、残疾学生、残疾人子女、烈士子女、其他困难等")
    private String difficultyTypeId;

    /** 困难等级 */
    @Excel(name = "困难等级", dictType = "sys_difficulty_level", prompt = "请输入困难等级，如：特别困难、困难、一般困难")
    private String difficultyLevelId;

    /** 是否脱贫户 */
    @Pattern(regexp = "^[01]?$", message = "是否脱贫户只能为0或1")
    @Excel(name = "是否脱贫户", readConverterExp = "0=否,1=是", prompt = "请输入是否脱贫户，如：是、否")
    private String isPovertyReliefFamily;

    /** 脱贫年份 */
    @Min(value = 1990, message = "脱贫年份不能早于1990年")
    @Max(value = 2099, message = "脱贫年份不能晚于2099年")
    @Excel(name = "脱贫年份", prompt = "请输入脱贫年份，1990-2099之间")
    private Integer povertyReliefYear;

    /** 备注 */
    @Size(max = 500, message = "备注不超过500字符")
    @Excel(name = "备注", prompt = "请输入备注，不超过500字符")
    private String memo;

    /** 查询参数：是否只查询未认定的学生（用于批量认定功能） */
    private Boolean unrecognized;

    /** 地区代码列表（临时字段，用于前端级联选择器，不持久化） */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private java.util.List<String> regionCodes;

    /** 村/社区（临时字段，用于前端输入，不持久化） */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String village;

    /** 屯/组（临时字段，用于前端输入，不持久化） */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String hamlet;

    /** 家庭成员（随表单提交） */
    @Valid
    private List<StFamilyMember> familyMembers;

    /** 银行卡信息（随表单提交） */
    @Valid
    private List<StStudentBankCard> bankCards;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd", type = Excel.Type.EXPORT)
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd", type = Excel.Type.EXPORT)
    private Date updatedAt;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setIdCardNo(String idCardNo) 
    {
        this.idCardNo = idCardNo;
    }

    public String getIdCardNo() 
    {
        return idCardNo;
    }

    public void setGender(String gender) 
    {
        this.gender = gender;
    }

    public String getGender() 
    {
        return gender;
    }

    public void setEthnicity(String ethnicity) 
    {
        this.ethnicity = ethnicity;
    }

    public String getEthnicity() 
    {
        return ethnicity;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPoliticalStatus()
    {
        return politicalStatus;
    }

    public void setPoliticalStatus(String politicalStatus)
    {
        this.politicalStatus = politicalStatus;
    }

    public Date getEnrollmentDate()
    {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate)
    {
        this.enrollmentDate = enrollmentDate;
    }

    public String getIsEthnicClass()
    {
        return isEthnicClass;
    }

    public void setIsEthnicClass(String isEthnicClass)
    {
        this.isEthnicClass = isEthnicClass;
    }

    public Date getLastGradeUpdate()
    {
        return lastGradeUpdate;
    }

    public void setLastGradeUpdate(Date lastGradeUpdate)
    {
        this.lastGradeUpdate = lastGradeUpdate;
    }

    public void setDomicile(String domicile) 
    {
        this.domicile = domicile;
    }

    public String getDomicile() 
    {
        return domicile;
    }

    public void setStudentNo(String studentNo) 
    {
        this.studentNo = studentNo;
    }

    public String getStudentNo() 
    {
        return studentNo;
    }

    public Long getSchoolingPlanId()
    {
        return schoolingPlanId;
    }

    public void setSchoolingPlanId(Long schoolingPlanId)
    {
        this.schoolingPlanId = schoolingPlanId;
    }

    public Long getGradeId()
    {
        return gradeId;
    }

    public void setGradeId(Long gradeId)
    {
        this.gradeId = gradeId;
    }

    public Long getClassId()
    {
        return classId;
    }

    public void setClassId(Long classId)
    {
        this.classId = classId;
    }

    public Long getCurrentYearSemesterId()
    {
        return currentYearSemesterId;
    }

    public void setCurrentYearSemesterId(Long currentYearSemesterId)
    {
        this.currentYearSemesterId = currentYearSemesterId;
    }

    public Long getYearSemesterId()
    {
        return yearSemesterId;
    }

    public void setYearSemesterId(Long yearSemesterId)
    {
        this.yearSemesterId = yearSemesterId;
    }

    public String getCurrentSchoolYear()
    {
        return currentSchoolYear;
    }

    public void setCurrentSchoolYear(String currentSchoolYear)
    {
        this.currentSchoolYear = currentSchoolYear;
    }

    public Integer getCurrentSemester()
    {
        return currentSemester;
    }

    public void setCurrentSemester(Integer currentSemester)
    {
        this.currentSemester = currentSemester;
    }

    public String getCurrentSemesterLabel()
    {
        return currentSemesterLabel;
    }

    public void setCurrentSemesterLabel(String currentSemesterLabel)
    {
        this.currentSemesterLabel = currentSemesterLabel;
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

    public String getStudyStatus()
    {
        return studyStatus;
    }

    public void setStudyStatus(String studyStatus)
    {
        this.studyStatus = studyStatus;
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

    public Integer getPovertyReliefYear()
    {
        return povertyReliefYear;
    }

    public void setPovertyReliefYear(Integer povertyReliefYear)
    {
        this.povertyReliefYear = povertyReliefYear;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String memo)
    {
        this.memo = memo;
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

    public List<StFamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(List<StFamilyMember> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public List<StStudentBankCard> getBankCards() {
        return bankCards;
    }

    public void setBankCards(List<StStudentBankCard> bankCards) {
        this.bankCards = bankCards;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }

    public Boolean getUnrecognized()
    {
        return unrecognized;
    }

    public void setUnrecognized(Boolean unrecognized)
    {
        this.unrecognized = unrecognized;
    }

    public java.util.List<String> getRegionCodes()
    {
        return regionCodes;
    }

    public void setRegionCodes(java.util.List<String> regionCodes)
    {
        this.regionCodes = regionCodes;
    }

    public String getVillage()
    {
        return village;
    }

    public void setVillage(String village)
    {
        this.village = village;
    }

    public String getHamlet()
    {
        return hamlet;
    }

    public void setHamlet(String hamlet)
    {
        this.hamlet = hamlet;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("idCardNo", getIdCardNo())
            .append("gender", getGender())
            .append("ethnicity", getEthnicity())
            .append("domicile", getDomicile())
            .append("studentNo", getStudentNo())
            .append("schoolingPlanId", getSchoolingPlanId())
            .append("gradeId", getGradeId())
            .append("classId", getClassId())
            .append("studyStatus", getStudyStatus())
            .append("difficultyTypeId", getDifficultyTypeId())
            .append("difficultyLevelId", getDifficultyLevelId())
            .append("isPovertyReliefFamily", getIsPovertyReliefFamily())
            .append("povertyReliefYear", getPovertyReliefYear())
            .append("memo", getMemo())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}