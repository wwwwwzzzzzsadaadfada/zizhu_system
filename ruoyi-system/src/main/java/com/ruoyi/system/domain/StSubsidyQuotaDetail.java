package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资助指标经济分类明细对象 st_subsidy_quota_detail
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
public class StSubsidyQuotaDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 关联指标主表ID */
    @Excel(name = "关联指标ID")
    private Long quotaId;

    /** 来源明细ID（结转时记录来源明细的ID，NULL表示非结转资金） */
    private Long sourceDetailId;

    /** 是否结转资金（0-否 1-是） */
    @Excel(name = "是否结转资金")
    private String isCarryover;

    /** 来源学期ID（结转时记录原学期） */
    private Long sourceSemesterId;

    /** 经济分类（1=助学金,2=免学杂费,3=免学费） */
    @Excel(name = "经济分类", dictType = "sys_economy_category")
    private String economyCategory;

    /** 该分类下的总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalAmount;

    /** 已分配金额 */
    @Excel(name = "已分配金额")
    private BigDecimal allocatedAmount;

    /** 状态（1=启用,0=停用） */
    @Excel(name = "状态")
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

    // ==================== 关联查询字段（不存数据库） ====================
    
    /** 剩余金额（计算字段） */
    private BigDecimal availableAmount;
    
    /** 经济分类名称 */
    private String economyCategoryName;
    
    /** 是否可编辑（计算字段：已分配金额为0且不是结转资金时可编辑） */
    private Boolean isEditable;

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

    public void setSourceDetailId(Long sourceDetailId) 
    {
        this.sourceDetailId = sourceDetailId;
    }

    public Long getSourceDetailId() 
    {
        return sourceDetailId;
    }

    public void setIsCarryover(String isCarryover) 
    {
        this.isCarryover = isCarryover;
    }

    public String getIsCarryover() 
    {
        return isCarryover;
    }

    public void setSourceSemesterId(Long sourceSemesterId) 
    {
        this.sourceSemesterId = sourceSemesterId;
    }

    public Long getSourceSemesterId() 
    {
        return sourceSemesterId;
    }

    public void setEconomyCategory(String economyCategory) 
    {
        this.economyCategory = economyCategory;
    }

    public String getEconomyCategory() 
    {
        return economyCategory;
    }

    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) 
    {
        this.allocatedAmount = allocatedAmount;
    }

    public BigDecimal getAllocatedAmount() 
    {
        return allocatedAmount;
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

    public BigDecimal getAvailableAmount() 
    {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) 
    {
        this.availableAmount = availableAmount;
    }

    public String getEconomyCategoryName() 
    {
        return economyCategoryName;
    }

    public void setEconomyCategoryName(String economyCategoryName) 
    {
        this.economyCategoryName = economyCategoryName;
    }

    public Boolean getIsEditable() 
    {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) 
    {
        this.isEditable = isEditable;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("quotaId", getQuotaId())
            .append("sourceDetailId", getSourceDetailId())
            .append("isCarryover", getIsCarryover())
            .append("sourceSemesterId", getSourceSemesterId())
            .append("economyCategory", getEconomyCategory())
            .append("totalAmount", getTotalAmount())
            .append("allocatedAmount", getAllocatedAmount())
            .append("status", getStatus())
            .append("memo", getMemo())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
