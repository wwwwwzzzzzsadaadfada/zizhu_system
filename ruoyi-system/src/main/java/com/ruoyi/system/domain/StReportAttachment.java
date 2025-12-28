package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报表附件对象 st_report_attachment
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public class StReportAttachment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 附件类型（report=报表附件，custom=自定义档案） */
    @Excel(name = "附件类型")
    private String attachType;

    /** 关联ID（report_name或package_code） */
    @Excel(name = "关联ID")
    private String relatedId;

    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileName;

    /** 文件路径 */
    @Excel(name = "文件路径")
    private String filePath;

    /** 文件大小（字节） */
    @Excel(name = "文件大小")
    private Long fileSize;

    /** 文件类型（pdf/jpg/png/doc等） */
    @Excel(name = "文件类型")
    private String fileType;

    /** 文件扩展名 */
    @Excel(name = "文件扩展名")
    private String fileExt;

    /** 原始文件名 */
    @Excel(name = "原始文件名")
    private String originalName;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 学年学期ID */
    @Excel(name = "学年学期ID")
    private Long yearSemesterId;

    /** 学年学期名称（非数据库字段，用于显示） */
    private String yearSemesterName;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer sortOrder;

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

    public void setAttachType(String attachType) 
    {
        this.attachType = attachType;
    }

    public String getAttachType() 
    {
        return attachType;
    }

    public void setRelatedId(String relatedId) 
    {
        this.relatedId = relatedId;
    }

    public String getRelatedId() 
    {
        return relatedId;
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

    public void setFileType(String fileType) 
    {
        this.fileType = fileType;
    }

    public String getFileType() 
    {
        return fileType;
    }

    public void setFileExt(String fileExt) 
    {
        this.fileExt = fileExt;
    }

    public String getFileExt() 
    {
        return fileExt;
    }

    public void setOriginalName(String originalName) 
    {
        this.originalName = originalName;
    }

    public String getOriginalName() 
    {
        return originalName;
    }

    @Override
    public void setRemark(String remark) 
    {
        this.remark = remark;
    }

    @Override
    public String getRemark() 
    {
        return remark;
    }

    public void setYearSemesterId(Long yearSemesterId) 
    {
        this.yearSemesterId = yearSemesterId;
    }

    public Long getYearSemesterId() 
    {
        return yearSemesterId;
    }

    public void setYearSemesterName(String yearSemesterName) 
    {
        this.yearSemesterName = yearSemesterName;
    }

    public String getYearSemesterName() 
    {
        return yearSemesterName;
    }

    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
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
            .append("attachType", getAttachType())
            .append("relatedId", getRelatedId())
            .append("fileName", getFileName())
            .append("filePath", getFilePath())
            .append("fileSize", getFileSize())
            .append("fileType", getFileType())
            .append("fileExt", getFileExt())
            .append("originalName", getOriginalName())
            .append("remark", getRemark())
            .append("yearSemesterId", getYearSemesterId())
            .append("yearSemesterName", getYearSemesterName())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
