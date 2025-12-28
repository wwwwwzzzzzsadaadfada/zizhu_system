package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 政策适用范围对象 st_subsidy_policy_scope
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public class StSubsidyPolicyScope extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 政策ID */
    private Long policyId;

    /** 适用学制ID（NULL表示所有学制） */
    private Long schoolingPlanId;

    /** 适用年级（如：高一、高二，NULL表示所有年级） */
    private String gradeName;

    /** 适用学期（1=春季,2=秋季，NULL表示所有学期） */
    private Integer semester;

    /** 适用学年（如：2024-2025，NULL表示所有学年） */
    private String schoolYear;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 学制名称（关联查询，不存数据库） */
    private String schoolingPlanName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setPolicyId(Long policyId) 
    {
        this.policyId = policyId;
    }

    public Long getPolicyId() 
    {
        return policyId;
    }

    public void setSchoolingPlanId(Long schoolingPlanId) 
    {
        this.schoolingPlanId = schoolingPlanId;
    }

    public Long getSchoolingPlanId() 
    {
        return schoolingPlanId;
    }

    public void setGradeName(String gradeName) 
    {
        this.gradeName = gradeName;
    }

    public String getGradeName() 
    {
        return gradeName;
    }

    public void setSemester(Integer semester) 
    {
        this.semester = semester;
    }

    public Integer getSemester() 
    {
        return semester;
    }

    public void setSchoolYear(String schoolYear) 
    {
        this.schoolYear = schoolYear;
    }

    public String getSchoolYear() 
    {
        return schoolYear;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public String getSchoolingPlanName() 
    {
        return schoolingPlanName;
    }

    public void setSchoolingPlanName(String schoolingPlanName) 
    {
        this.schoolingPlanName = schoolingPlanName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("policyId", getPolicyId())
            .append("schoolingPlanId", getSchoolingPlanId())
            .append("gradeName", getGradeName())
            .append("semester", getSemester())
            .append("schoolYear", getSchoolYear())
            .append("createdAt", getCreatedAt())
            .toString();
    }
}


