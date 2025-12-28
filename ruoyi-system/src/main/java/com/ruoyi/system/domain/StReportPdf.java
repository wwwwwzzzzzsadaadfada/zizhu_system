package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报表PDF记录对象 st_report_pdf
 * 
 * @author ruoyi
 * @date 2025-12-15
 */
public class StReportPdf extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 学生ID */
    @Excel(name = "学生ID")
    private Long studentId;

    /** 报表ID（积木报表ID） */
    @Excel(name = "报表ID")
    private String reportId;

    /** 报表名称 */
    @Excel(name = "报表名称")
    private String reportName;

    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileName;

    /** 文件相对路径 */
    @Excel(name = "文件路径")
    private String filePath;

    /** 文件大小（字节） */
    @Excel(name = "文件大小")
    private Long fileSize;

    /** 学年学期ID */
    @Excel(name = "学年学期ID")
    private Long yearSemesterId;

    /** 学年学期名称（仅用于展示） */
    private String yearSemesterName;

    /** 学制ID */
    @Excel(name = "学制ID")
    private Long schoolingPlanId;

    /** 年级ID */
    @Excel(name = "年级ID")
    private Long gradeId;

    /** 班级ID */
    @Excel(name = "班级ID")
    private Long classId;

    /** 批次ID */
    @Excel(name = "批次ID")
    private String batchId;

    /** 是否合并文件（0=单个，1=合并） */
    @Excel(name = "是否合并", readConverterExp = "0=单个,1=合并")
    private Integer isMerged;

    /** 合并来源ID列表（JSON数组） */
    private String mergedFromIds;

    /** 状态（1=正常，0=已删除） */
    @Excel(name = "状态", readConverterExp = "1=正常,0=已删除")
    private Integer status;

    /** 学生姓名（快照） */
    private String studentName;

    /** 学号（快照） */
    private String studentNo;

    /** 年级名称（快照） */
    private String gradeName;

    /** 班级名称（快照） */
    private String className;

    /** 合并学生姓名（非持久化，仅返回） */
    private String mergedStudentNames;

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

    public void setReportId(String reportId) 
    {
        this.reportId = reportId;
    }

    public String getReportId() 
    {
        return reportId;
    }

    public void setReportName(String reportName) 
    {
        this.reportName = reportName;
    }

    public String getReportName() 
    {
        return reportName;
    }

    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }

    public void setFilePath(String filePath) 
    {
        this.filePath = filePath;
    }

    public String getFilePath() 
    {
        return filePath;
    }

    public void setFileSize(Long fileSize) 
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize() 
    {
        return fileSize;
    }

    public void setYearSemesterId(Long yearSemesterId) 
    {
        this.yearSemesterId = yearSemesterId;
    }

    public Long getYearSemesterId() 
    {
        return yearSemesterId;
    }

    public String getYearSemesterName()
    {
        return yearSemesterName;
    }

    public void setYearSemesterName(String yearSemesterName)
    {
        this.yearSemesterName = yearSemesterName;
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

    public void setBatchId(String batchId) 
    {
        this.batchId = batchId;
    }

    public String getBatchId() 
    {
        return batchId;
    }

    public void setIsMerged(Integer isMerged) 
    {
        this.isMerged = isMerged;
    }

    public Integer getIsMerged() 
    {
        return isMerged;
    }

    public void setMergedFromIds(String mergedFromIds) 
    {
        this.mergedFromIds = mergedFromIds;
    }

    public String getMergedFromIds() 
    {
        return mergedFromIds;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }

    public String getStudentName()
    {
        return studentName;
    }

    public String getStudentNo()
    {
        return studentNo;
    }

    public void setStudentNo(String studentNo)
    {
        this.studentNo = studentNo;
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

    public String getMergedStudentNames()
    {
        return mergedStudentNames;
    }

    public void setMergedStudentNames(String mergedStudentNames)
    {
        this.mergedStudentNames = mergedStudentNames;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("studentId", getStudentId())
            .append("reportId", getReportId())
            .append("reportName", getReportName())
            .append("fileName", getFileName())
            .append("filePath", getFilePath())
            .append("fileSize", getFileSize())
            .append("yearSemesterId", getYearSemesterId())
            .append("schoolingPlanId", getSchoolingPlanId())
            .append("gradeId", getGradeId())
            .append("classId", getClassId())
            .append("batchId", getBatchId())
            .append("isMerged", getIsMerged())
            .append("mergedFromIds", getMergedFromIds())
            .append("mergedStudentNames", getMergedStudentNames())
            .append("status", getStatus())
            .append("studentName", getStudentName())
            .append("studentNo", getStudentNo())
            .append("gradeName", getGradeName())
            .append("className", getClassName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}




