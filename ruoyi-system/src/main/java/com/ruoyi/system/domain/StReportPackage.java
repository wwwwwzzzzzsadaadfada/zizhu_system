package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报表打包记录对象 st_report_package
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public class StReportPackage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 打包名称 */
    @Excel(name = "打包名称")
    private String packageName;

    /** 打包类型（manual=手动打包，all=全部档案打包） */
    @Excel(name = "打包类型", readConverterExp = "manual=手动打包,all=全部档案打包")
    private String packageType;

    /** 物理存储相对路径 */
    private String targetDir;

    /** 压缩包文件名 */
    @Excel(name = "压缩包文件名")
    private String zipFileName;

    /** 压缩包相对路径 */
    private String zipFilePath;

    /** 状态（0=待打包，1=已生成，2=失败） */
    @Excel(name = "状态", readConverterExp = "0=待打包,1=已生成,2=失败")
    private Integer status;

    /** 包含的文件数 */
    @Excel(name = "文件数量")
    private Integer fileCount;

    /** 总大小（字节） */
    @Excel(name = "总大小")
    private Long totalSize;

    /** 错误信息 */
    private String errorMsg;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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

    public void setTargetDir(String targetDir) 
    {
        this.targetDir = targetDir;
    }

    public String getTargetDir() 
    {
        return targetDir;
    }

    public void setZipFileName(String zipFileName) 
    {
        this.zipFileName = zipFileName;
    }

    public String getZipFileName() 
    {
        return zipFileName;
    }

    public void setZipFilePath(String zipFilePath) 
    {
        this.zipFilePath = zipFilePath;
    }

    public String getZipFilePath() 
    {
        return zipFilePath;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
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

    public void setErrorMsg(String errorMsg) 
    {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() 
    {
        return errorMsg;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("packageName", getPackageName())
            .append("packageType", getPackageType())
            .append("targetDir", getTargetDir())
            .append("zipFileName", getZipFileName())
            .append("zipFilePath", getZipFilePath())
            .append("status", getStatus())
            .append("fileCount", getFileCount())
            .append("totalSize", getTotalSize())
            .append("errorMsg", getErrorMsg())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

