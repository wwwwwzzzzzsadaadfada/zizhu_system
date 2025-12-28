package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * PDF合并批次对象 st_report_pdf_batch
 * 
 * @author ruoyi
 * @date 2025-12-15
 */
public class StReportPdfBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 批次ID（UUID） */
    @Excel(name = "批次ID")
    private String batchId;

    /** 批次名称 */
    @Excel(name = "批次名称")
    private String batchName;

    /** 合并后的文件名 */
    @Excel(name = "合并文件名")
    private String mergedFileName;

    /** 合并后的文件路径 */
    @Excel(name = "合并文件路径")
    private String mergedFilePath;

    /** 合并后的文件大小 */
    @Excel(name = "合并文件大小")
    private Long mergedFileSize;

    /** 包含的学生数量 */
    @Excel(name = "学生数量")
    private Integer studentCount;

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

    /** 筛选条件（JSON格式） */
    private String filterConditions;

    /** 状态（1=正常，0=已删除） */
    @Excel(name = "状态", readConverterExp = "1=正常,0=已删除")
    private Integer status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setBatchId(String batchId) 
    {
        this.batchId = batchId;
    }

    public String getBatchId() 
    {
        return batchId;
    }

    public void setBatchName(String batchName) 
    {
        this.batchName = batchName;
    }

    public String getBatchName() 
    {
        return batchName;
    }

    public void setMergedFileName(String mergedFileName) 
    {
        this.mergedFileName = mergedFileName;
    }

    public String getMergedFileName() 
    {
        return mergedFileName;
    }

    public void setMergedFilePath(String mergedFilePath) 
    {
        this.mergedFilePath = mergedFilePath;
    }

    public String getMergedFilePath() 
    {
        return mergedFilePath;
    }

    public void setMergedFileSize(Long mergedFileSize) 
    {
        this.mergedFileSize = mergedFileSize;
    }

    public Long getMergedFileSize() 
    {
        return mergedFileSize;
    }

    public void setStudentCount(Integer studentCount) 
    {
        this.studentCount = studentCount;
    }

    public Integer getStudentCount() 
    {
        return studentCount;
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

    public void setFilterConditions(String filterConditions) 
    {
        this.filterConditions = filterConditions;
    }

    public String getFilterConditions() 
    {
        return filterConditions;
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
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("batchId", getBatchId())
            .append("batchName", getBatchName())
            .append("mergedFileName", getMergedFileName())
            .append("mergedFilePath", getMergedFilePath())
            .append("mergedFileSize", getMergedFileSize())
            .append("studentCount", getStudentCount())
            .append("yearSemesterId", getYearSemesterId())
            .append("schoolingPlanId", getSchoolingPlanId())
            .append("gradeId", getGradeId())
            .append("classId", getClassId())
            .append("filterConditions", getFilterConditions())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}




