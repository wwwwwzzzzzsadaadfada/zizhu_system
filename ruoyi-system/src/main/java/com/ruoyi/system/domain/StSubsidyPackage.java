package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 补助套餐配置对象 st_subsidy_package
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public class StSubsidyPackage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 学制ID（1=小学，2=初中，3=高中） */
    @Excel(name = "学制ID")
    private Long schoolingPlanId;

    /** 套餐名称 */
    @Excel(name = "套餐名称")
    private String packageName;

    /** 套餐类型（助学金一档/助学金二档/助学金三档/免学杂费） */
    @Excel(name = "套餐类型")
    private String packageType;

    /** 补助金额 */
    @Excel(name = "补助金额")
    private BigDecimal subsidyAmount;

    /** 经济分类（助学金/免学杂费） */
    @Excel(name = "经济分类")
    private String economyCategory;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 状态：1启用，0禁用 */
    @Excel(name = "状态", dictType = "sys_normal_disable")
    private Integer status;

    /** 备注 */
    @Excel(name = "备注")
    private String memo;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    /** 学制名称（关联查询，不存数据库） */
    private String schoolingPlanName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setSchoolingPlanId(Long schoolingPlanId) 
    {
        this.schoolingPlanId = schoolingPlanId;
    }

    public Long getSchoolingPlanId() 
    {
        return schoolingPlanId;
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

    public void setSubsidyAmount(BigDecimal subsidyAmount) 
    {
        this.subsidyAmount = subsidyAmount;
    }

    public BigDecimal getSubsidyAmount() 
    {
        return subsidyAmount;
    }

    public void setEconomyCategory(String economyCategory) 
    {
        this.economyCategory = economyCategory;
    }

    public String getEconomyCategory() 
    {
        return economyCategory;
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

    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }

    public String getSchoolingPlanName() 
    {
        return schoolingPlanName;
    }

    public void setSchoolingPlanName(String schoolingPlanName) 
    {
        this.schoolingPlanName = schoolingPlanName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("schoolingPlanId", getSchoolingPlanId())
            .append("packageName", getPackageName())
            .append("packageType", getPackageType())
            .append("subsidyAmount", getSubsidyAmount())
            .append("economyCategory", getEconomyCategory())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("memo", getMemo())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}



