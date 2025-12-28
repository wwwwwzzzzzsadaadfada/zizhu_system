package com.ruoyi.system.domain.dto;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

/**
 * 补助发放导入DTO（Excel导入用）
 * 
 * @author ruoyi
 * @date 2024-12-XX
 */
public class PaymentImportDTO {
    
    /** 姓名（必填，用于校验） */
    @Excel(name = "姓名", prompt = "必填：学生姓名，用于校验")
    private String name;
    
    /** 性别（非必填，用于校验） */
    @Excel(name = "性别", prompt = "非必填：男/女，用于校验")
    private String gender;
    
    /** 身份证号（非必填，用于校验） */
    @Excel(name = "身份证号", prompt = "非必填：用于校验学生身份")
    private String idCardNo;
    
    /** 学籍号（必填） */
    @Excel(name = "学籍号", prompt = "必填：学生学籍号，系统根据此字段查找学生")
    private String studentNo;
    
    /** 资助金额（必填） */
    @Excel(name = "资助金额", prompt = "必填：资助金额，单位：元，例如：500.00")
    private BigDecimal subsidyAmount;
    
    /** 资助日期（必填） */
    @Excel(name = "资助日期", dateFormat = "yyyy-MM-dd", prompt = "必填：发放日期，格式：2024-01-15")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date paymentDate;
    
    /** 发放人（必填） */
    @Excel(name = "发放人", prompt = "必填：发放人姓名")
    private String paymentPerson;
    
    /** 经济分类（必填：1=助学金，2=免学杂费，3=免学费） */
    @Excel(name = "经济分类", prompt = "必填：1=助学金，2=免学杂费，3=免学费", readConverterExp = "1=助学金,2=免学杂费,3=免学费")
    private String economyCategory;
    
    /** 预算ID（必填，用于确定使用哪个预算） */
    @Excel(name = "预算ID", prompt = "必填：预算ID（st_semester_budget表的id字段），可在预算管理页面或批量发放对话框的预算下拉框中查看，注意不是指标ID")
    private Long budgetId;
    
    /** 错误信息（导入校验用，不存数据库） */
    private String errorMsg;
    
    /** 行号（导入校验用，不存数据库） */
    private Integer rowNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public BigDecimal getSubsidyAmount() {
        return subsidyAmount;
    }

    public void setSubsidyAmount(BigDecimal subsidyAmount) {
        this.subsidyAmount = subsidyAmount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentPerson() {
        return paymentPerson;
    }

    public void setPaymentPerson(String paymentPerson) {
        this.paymentPerson = paymentPerson;
    }

    public String getEconomyCategory() {
        return economyCategory;
    }

    public void setEconomyCategory(String economyCategory) {
        this.economyCategory = economyCategory;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }
}

