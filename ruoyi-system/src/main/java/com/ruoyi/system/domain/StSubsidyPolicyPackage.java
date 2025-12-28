package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 政策与套餐关联对象 st_subsidy_policy_package
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public class StSubsidyPolicyPackage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 政策ID */
    private Long policyId;

    /** 补助套餐ID */
    private Long packageId;

    /** 套餐名称（冗余字段，方便查询） */
    private String packageName;

    /** 补助金额（冗余字段，记录政策规定的金额） */
    private BigDecimal subsidyAmount;

    /** 排序 */
    private Integer sortOrder;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

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

    public void setPackageId(Long packageId) 
    {
        this.packageId = packageId;
    }

    public Long getPackageId() 
    {
        return packageId;
    }

    public void setPackageName(String packageName) 
    {
        this.packageName = packageName;
    }

    public String getPackageName() 
    {
        return packageName;
    }

    public void setSubsidyAmount(BigDecimal subsidyAmount) 
    {
        this.subsidyAmount = subsidyAmount;
    }

    public BigDecimal getSubsidyAmount() 
    {
        return subsidyAmount;
    }

    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("policyId", getPolicyId())
            .append("packageId", getPackageId())
            .append("packageName", getPackageName())
            .append("subsidyAmount", getSubsidyAmount())
            .append("sortOrder", getSortOrder())
            .append("createdAt", getCreatedAt())
            .toString();
    }
}


