package com.ruoyi.system.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资助政策对象 st_subsidy_policy
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public class StSubsidyPolicy extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 政策编号（唯一） */
    @Excel(name = "政策编号")
    private String policyCode;

    /** 政策名称 */
    @Excel(name = "政策名称")
    private String policyName;

    /** 政策版本（如：v1.0） */
    @Excel(name = "政策版本")
    private String policyVersion;

    /** 政策类型（如：国家助学金、免学杂费） */
    @Excel(name = "政策类型")
    private String policyType;

    /** 政策内容（富文本） */
    private String content;

    /** 政策文件路径（Word/PDF） */
    private String filePath;

    /** 政策文件名称 */
    private String fileName;

    /** 生效日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date effectiveDate;

    /** 失效日期（NULL表示长期有效） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "失效日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expiryDate;

    /** 状态：0=草稿,1=已发布,2=已废止 */
    @Excel(name = "状态", dictType = "sys_policy_status")
    private Integer status;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    /** 发布人 */
    private String publisher;

    /** 废止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date abolishTime;

    /** 废止原因 */
    private String abolishReason;

    /** 备注 */
    @Excel(name = "备注")
    private String memo;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 创建人 */
    private String createdBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    /** 更新人 */
    private String updatedBy;

    /** 适用范围列表（关联查询，不存数据库） */
    private List<StSubsidyPolicyScope> scopeList;

    /** 关联套餐列表（关联查询，不存数据库） */
    private List<StSubsidyPolicyPackage> packageList;


    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setPolicyCode(String policyCode) 
    {
        this.policyCode = policyCode;
    }

    public String getPolicyCode() 
    {
        return policyCode;
    }

    public void setPolicyName(String policyName) 
    {
        this.policyName = policyName;
    }

    public String getPolicyName() 
    {
        return policyName;
    }

    public void setPolicyVersion(String policyVersion) 
    {
        this.policyVersion = policyVersion;
    }

    public String getPolicyVersion() 
    {
        return policyVersion;
    }

    public void setPolicyType(String policyType) 
    {
        this.policyType = policyType;
    }

    public String getPolicyType() 
    {
        return policyType;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setFilePath(String filePath) 
    {
        this.filePath = filePath;
    }

    public String getFilePath() 
    {
        return filePath;
    }

    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }

    public void setEffectiveDate(Date effectiveDate) 
    {
        this.effectiveDate = effectiveDate;
    }

    public Date getEffectiveDate() 
    {
        return effectiveDate;
    }

    public void setExpiryDate(Date expiryDate) 
    {
        this.expiryDate = expiryDate;
    }

    public Date getExpiryDate() 
    {
        return expiryDate;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public void setPublishTime(Date publishTime) 
    {
        this.publishTime = publishTime;
    }

    public Date getPublishTime() 
    {
        return publishTime;
    }

    public void setPublisher(String publisher) 
    {
        this.publisher = publisher;
    }

    public String getPublisher() 
    {
        return publisher;
    }

    public void setAbolishTime(Date abolishTime) 
    {
        this.abolishTime = abolishTime;
    }

    public Date getAbolishTime() 
    {
        return abolishTime;
    }

    public void setAbolishReason(String abolishReason) 
    {
        this.abolishReason = abolishReason;
    }

    public String getAbolishReason() 
    {
        return abolishReason;
    }

    public void setMemo(String memo) 
    {
        this.memo = memo;
    }

    public String getMemo() 
    {
        return memo;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public void setCreatedBy(String createdBy) 
    {
        this.createdBy = createdBy;
    }

    public String getCreatedBy() 
    {
        return createdBy;
    }

    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }

    public void setUpdatedBy(String updatedBy) 
    {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedBy() 
    {
        return updatedBy;
    }

    public List<StSubsidyPolicyScope> getScopeList() 
    {
        return scopeList;
    }

    public void setScopeList(List<StSubsidyPolicyScope> scopeList) 
    {
        this.scopeList = scopeList;
    }

    public List<StSubsidyPolicyPackage> getPackageList() 
    {
        return packageList;
    }

    public void setPackageList(List<StSubsidyPolicyPackage> packageList) 
    {
        this.packageList = packageList;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("policyCode", getPolicyCode())
            .append("policyName", getPolicyName())
            .append("policyVersion", getPolicyVersion())
            .append("policyType", getPolicyType())
            .append("content", getContent())
            .append("filePath", getFilePath())
            .append("fileName", getFileName())
            .append("effectiveDate", getEffectiveDate())
            .append("expiryDate", getExpiryDate())
            .append("status", getStatus())
            .append("publishTime", getPublishTime())
            .append("publisher", getPublisher())
            .append("abolishTime", getAbolishTime())
            .append("abolishReason", getAbolishReason())
            .append("memo", getMemo())
            .append("createdAt", getCreatedAt())
            .append("createdBy", getCreatedBy())
            .append("updatedAt", getUpdatedAt())
            .append("updatedBy", getUpdatedBy())
            .toString();
    }
}


