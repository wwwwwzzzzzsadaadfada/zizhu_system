package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 自定义档案包对象 st_report_package_custom
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
public class StReportPackageCustom extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 档案包编码（唯一标识） */
    @Excel(name = "档案包编码")
    private String packageCode;

    /** 档案包名称 */
    @Excel(name = "档案包名称")
    private String packageName;

    /** 档案包类型 */
    @Excel(name = "档案包类型")
    private String packageType;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 学年学期ID */
    @Excel(name = "学年学期ID")
    private Long yearSemesterId;

    /** 学制ID */
    @Excel(name = "学制ID")
    private Long schoolingPlanId;

    /** 年级ID */
    @Excel(name = "年级ID")
    private Long gradeId;

    /** 班级ID */
    @Excel(name = "班级ID")
    private Long classId;

    /** 文件数量 */
    @Excel(name = "文件数量")
    private Integer fileCount;

    /** 总大小（字节） */
    @Excel(name = "总大小")
    private Long totalSize;

    /** 状态（1=正常，0=已删除） */
    @Excel(name = "状态")
    private Integer status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setPackageCode(String packageCode) 
    {
        this.packageCode = packageCode;
    }

    public String getPackageCode() 
    {
        return packageCode;
    }

    public void setPackageName(String packageName) 
    {
        this.packageName = packageName;
    }

    public String getPackageName() 
    {
        return packageName;
    }

    public void setPackageType(String packageType) 
    {
        this.packageType = packageType;
    }

    public String getPackageType() 
    {
        return packageType;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setYearSemesterId(Long yearSemesterId) 
    {
        this.yearSemesterId = yearSemesterId;
    }

    public Long getYearSemesterId() 
    {
        return yearSemesterId;
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

    public void setFileCount(Integer fileCount) 
    {
        this.fileCount = fileCount;
    }

    public Integer getFileCount() 
    {
        return fileCount;
    }

    public void setTotalSize(Long totalSize) 
    {
        this.totalSize = totalSize;
    }

    public Long getTotalSize() 
    {
        return totalSize;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("packageCode", getPackageCode())
            .append("packageName", getPackageName())
            .append("packageType", getPackageType())
            .append("description", getDescription())
            .append("yearSemesterId", getYearSemesterId())
            .append("schoolingPlanId", getSchoolingPlanId())
            .append("gradeId", getGradeId())
            .append("classId", getClassId())
            .append("fileCount", getFileCount())
            .append("totalSize", getTotalSize())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
