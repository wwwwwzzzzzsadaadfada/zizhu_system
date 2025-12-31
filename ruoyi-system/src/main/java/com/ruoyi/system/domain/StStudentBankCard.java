package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 学生银行卡信息对象 st_student_bank_cards
 */
public class StStudentBankCard extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 学生ID */
    private Long studentId;
    /** 银行卡号（19位数字） */
    @NotBlank(message = "银行卡号不能为空")
    @Pattern(regexp = "^\\d{19}$", message = "银行卡号必须为19位数字")
    private String bankAccountNo;
    /** 所属银行（默认为信用社） */
    private String bankType;
    /** 开户行（银行名称或开户行全称） */
    private String bankName;
    /** 支行/网点名称 */
    private String branchName;
    /** 开卡人（持卡人姓名） */
    private String accountHolder;
    /** 是否主卡 0-否 1-是 */
    private Integer isPrimary;
    /** 状态 0-正常 1-停用 */
    private Integer status;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
    /** 创建人 */
    private String createdBy;
    /** 更新人 */
    private String updatedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getBankAccountNo() {
        return bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo) {
        this.bankAccountNo = bankAccountNo;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public Integer getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Integer isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}


