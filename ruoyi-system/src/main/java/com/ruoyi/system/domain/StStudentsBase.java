package com.ruoyi.system.domain;

import java.util.Date;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学生基础信息对象 st_students_base
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
public class StStudentsBase extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 姓名 */
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 50, message = "姓名长度应在2-50之间")
    @Excel(name = "姓名")
    private String name;

    /** 身份证号 */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{18}$|^\\d{17}[Xx]$", message = "身份证号格式不正确(18位数字或17位数字+X)")
    @Excel(name = "身份证号")
    private String idCardNo;

    /** 性别 */
    @Pattern(regexp = "^[01]?$", message = "性别值不正确")
    @Excel(name = "性别", dictType = "sys_student_gender")
    private String gender;

    /** 民族 */
    @Size(max = 10, message = "民族不超过10字符")
    @Excel(name = "民族", dictType = "sys_student_ethnicity")
    private String ethnicity;

    /** 户籍所在地 */
    @Size(max = 200, message = "户籍所在地不超过200字符")
    @Excel(name = "户籍所在地")
    private String domicile;

    /** 学籍号 */
    @NotBlank(message = "学籍号不能为空")
    @Size(max = 32, message = "学籍号不超过32字符")
    @Excel(name = "学籍号")
    private String studentNo;

    /** 联系电话 */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    @Excel(name = "联系电话")
    private String phone;

    /** 学制 */
    @Excel(name = "学制")
    private Long schoolingPlanId;

    /** 当前年级 */
    @Excel(name = "当前年级")
    private Long gradeId;

    /** 当前班级 */
    @Excel(name = "当前班级")
    private Long classId;

    /** 当前学年学期ID */
    private Long currentYearSemesterId;

    /** 年级名称（冗余） */
    private String gradeName;

    /** 班级名称（冗余） */
    private String className;

    /** 最后一次年级更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastGradeUpdate;

    /** 是否脱贫户 */
    @Pattern(regexp = "^[01]?$", message = "是否脱贫户只能为0或1")
    private String isPovertyReliefFamily;

    /** 脱贫年份 */
    @Min(value = 1990, message = "脱贫年份不能早于1990年")
    @Max(value = 2099, message = "脱贫年份不能晚于2099年")
    private Integer povertyReliefYear;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
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

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPhone()
    {
        return phone;
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

    public Date getLastGradeUpdate()
    {
        return lastGradeUpdate;
    }

    public void setLastGradeUpdate(Date lastGradeUpdate)
    {
        this.lastGradeUpdate = lastGradeUpdate;
    }

    public String getIsPovertyReliefFamily()
    {
        return isPovertyReliefFamily;
    }

    public void setIsPovertyReliefFamily(String isPovertyReliefFamily)
    {
        this.isPovertyReliefFamily = isPovertyReliefFamily;
    }

    public Integer getPovertyReliefYear()
    {
        return povertyReliefYear;
    }

    public void setPovertyReliefYear(Integer povertyReliefYear)
    {
        this.povertyReliefYear = povertyReliefYear;
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
            .append("name", getName())
            .append("idCardNo", getIdCardNo())
            .append("gender", getGender())
            .append("ethnicity", getEthnicity())
            .append("domicile", getDomicile())
            .append("studentNo", getStudentNo())
            .append("schoolingPlanId", getSchoolingPlanId())
            .append("gradeId", getGradeId())
            .append("classId", getClassId())
            .append("isPovertyReliefFamily", getIsPovertyReliefFamily())
            .append("povertyReliefYear", getPovertyReliefYear())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
