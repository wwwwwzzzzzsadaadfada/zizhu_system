package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报表打包明细对象 st_report_package_item
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public class StReportPackageItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 打包记录ID */
    @Excel(name = "打包记录ID")
    private Long packageId;

    /** 报表PDF记录ID */
    @Excel(name = "报表PDF记录ID")
    private Long reportPdfId;

    /** 文件名称（快照） */
    private String fileName;

    /** 文件相对路径（快照） */
    private String filePath;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setPackageId(Long packageId) 
    {
        this.packageId = packageId;
    }

    public Long getPackageId() 
    {
        return packageId;
    }

    public void setReportPdfId(Long reportPdfId) 
    {
        this.reportPdfId = reportPdfId;
    }

    public Long getReportPdfId() 
    {
        return reportPdfId;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("packageId", getPackageId())
            .append("reportPdfId", getReportPdfId())
            .append("fileName", getFileName())
            .append("filePath", getFilePath())
            .append("createTime", getCreateTime())
            .toString();
    }
}

