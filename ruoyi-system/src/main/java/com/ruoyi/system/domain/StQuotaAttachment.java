package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 指标附件对象 st_quota_attachment
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public class StQuotaAttachment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 关联指标ID */
    @Excel(name = "关联指标ID")
    private Long quotaId;

    /** 原始文件名 */
    @Excel(name = "原始文件名")
    private String fileName;

    /** 文件存储路径 */
    @Excel(name = "文件存储路径")
    private String fileUrl;

    /** 文件大小（字节） */
    @Excel(name = "文件大小")
    private Long fileSize;

    /** 文件类型（扩展名） */
    @Excel(name = "文件类型")
    private String fileType;

    /** MIME类型 */
    private String mimeType;

    /** 上传人 */
    @Excel(name = "上传人")
    private String uploadBy;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    /** 状态：1=正常，0=已删除 */
    @Excel(name = "状态", readConverterExp = "1=正常,0=已删除")
    private Integer status;

    /** 排序顺序 */
    private Integer sortOrder;

    /** 备注 */
    @Excel(name = "备注")
    private String memo;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setQuotaId(Long quotaId) 
    {
        this.quotaId = quotaId;
    }

    public Long getQuotaId() 
    {
        return quotaId;
    }

    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }

    public void setFileUrl(String fileUrl) 
    {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() 
    {
        return fileUrl;
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

    public void setMimeType(String mimeType) 
    {
        this.mimeType = mimeType;
    }

    public String getMimeType() 
    {
        return mimeType;
    }

    public void setUploadBy(String uploadBy) 
    {
        this.uploadBy = uploadBy;
    }

    public String getUploadBy() 
    {
        return uploadBy;
    }

    public void setUploadTime(Date uploadTime) 
    {
        this.uploadTime = uploadTime;
    }

    public Date getUploadTime() 
    {
        return uploadTime;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
    }

    public void setMemo(String memo) 
    {
        this.memo = memo;
    }

    public String getMemo() 
    {
        return memo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("quotaId", getQuotaId())
            .append("fileName", getFileName())
            .append("fileUrl", getFileUrl())
            .append("fileSize", getFileSize())
            .append("fileType", getFileType())
            .append("mimeType", getMimeType())
            .append("uploadBy", getUploadBy())
            .append("uploadTime", getUploadTime())
            .append("status", getStatus())
            .append("sortOrder", getSortOrder())
            .append("memo", getMemo())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}


