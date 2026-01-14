package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Excel报表对象 st_report_excel
 * 
 * @author ruoyi
 * @date 2025-01-03
 */
public class StReportExcel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 学生ID */
    @Excel(name = "学生ID")
    private Long studentId;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String studentName;

    /** 学生学号 */
    @Excel(name = "学生学号")
    private String studentNo;

    /** 培养计划ID */
    @Excel(name = "培养计划ID")
    private Long schoolingPlanId;

    /** 年级ID */
    @Excel(name = "年级ID")
    private Long gradeId;

    /** 年级名称 */
    @Excel(name = "年级名称")
    private String gradeName;

    /** 班级ID */
    @Excel(name = "班级ID")
    private Long classId;

    /** 班级名称 */
    @Excel(name = "班级名称")
    private String className;

    /** 报表ID */
    @Excel(name = "报表ID")
    private String reportId;

    /** 报表名称 */
    @Excel(name = "报表名称")
    private String reportName;

    /** 文件名 */
    @Excel(name = "文件名")
    private String fileName;

    /** 文件路径 */
    @Excel(name = "文件路径")
    private String filePath;

    /** 文件大小 */
    @Excel(name = "文件大小")
    private Long fileSize;

    /** 学年学期ID */
    @Excel(name = "学年学期ID")
    private Long yearSemesterId;

    /** 是否合并（0-未合并，1-已合并） */
    @Excel(name = "是否合并", readConverterExp = "0=未合并,1=已合并")
    private Integer isMerged;

    /** 合并来源ID列表（JSON格式） */
    @Excel(name = "合并来源")
    private String mergedFromIds;

    /** 状态（0-删除，1-正常） */
    @Excel(name = "状态", readConverterExp = "0=删除,1=正常")
    private Integer status;

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
    public void setStudentName(String studentName) 
    {
        this.studentName = studentName;
    }

    public String getStudentName() 
    {
        return studentName;
    }
    public void setStudentNo(String studentNo) 
    {
        this.studentNo = studentNo;
    }

    public String getStudentNo() 
    {
        return studentNo;
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
    public void setGradeName(String gradeName) 
    {
        this.gradeName = gradeName;
    }

    public String getGradeName() 
    {
        return gradeName;
    }
    public void setClassId(Long classId) 
    {
        this.classId = classId;
    }

    public Long getClassId() 
    {
        return classId;
    }
    public void setClassName(String className) 
    {
        this.className = className;
    }

    public String getClassName() 
    {
        return className;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("studentId", getStudentId())
            .append("studentName", getStudentName())
            .append("studentNo", getStudentNo())
            .append("schoolingPlanId", getSchoolingPlanId())
            .append("gradeId", getGradeId())
            .append("gradeName", getGradeName())
            .append("classId", getClassId())
            .append("className", getClassName())
            .append("reportId", getReportId())
            .append("reportName", getReportName())
            .append("fileName", getFileName())
            .append("filePath", getFilePath())
            .append("fileSize", getFileSize())
            .append("yearSemesterId", getYearSemesterId())
            .append("isMerged", getIsMerged())
            .append("mergedFromIds", getMergedFromIds())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}