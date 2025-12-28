package com.ruoyi.system.domain.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 批量发放请求DTO
 * 
 * @author ruoyi
 * @date 2024-12-XX
 */
public class BatchPaymentRequest {
    
    /** 补助记录ID列表（批量录入方式，如果有待发放记录） */
    private List<Long> recordIds;
    
    /** 学生发放信息列表（支持直接录入金额发放） */
    private List<StudentPaymentInfo> studentPayments;
    
    /** 预算ID（向后兼容） */
    private Long budgetId;
    
    /** 预算ID列表（支持多选，如果只选一个则只从该预算支出，如果选多个则使用组合逻辑） */
    private List<Long> budgetIds;
    
    /** 资金来源学期ID */
    private Long sourceSemesterId;
    
    /** 发放日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date paymentDate;
    
    /** 发放人 */
    private String paymentPerson;
    
    /** 备注 */
    private String memo;

    public List<Long> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<Long> recordIds) {
        this.recordIds = recordIds;
    }

    public List<StudentPaymentInfo> getStudentPayments() {
        return studentPayments;
    }

    public void setStudentPayments(List<StudentPaymentInfo> studentPayments) {
        this.studentPayments = studentPayments;
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

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public Long getSourceSemesterId() {
        return sourceSemesterId;
    }

    public void setSourceSemesterId(Long sourceSemesterId) {
        this.sourceSemesterId = sourceSemesterId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public List<Long> getBudgetIds() {
        return budgetIds;
    }
    
    public void setBudgetIds(List<Long> budgetIds) {
        this.budgetIds = budgetIds;
    }
    
    /**
     * 学生发放信息
     */
    public static class StudentPaymentInfo {
        /** 学生ID */
        private Long studentId;
        
        /** 学生姓名 */
        private String studentName;
        
        /** 学籍号 */
        private String studentNo;
        
        /** 学年 */
        private String academicYear;
        
        /** 学期 */
        private String semester;
        
        /** 学制ID */
        private Long schoolingPlanId;
        
        /** 待发放记录ID（如果有） */
        private Long pendingRecordId;
        
        /** 经济分类（助学金/免学杂费） */
        private String economyCategory;
        
        /** 套餐ID */
        private Long packageId;
        
        /** 发放金额 */
        private BigDecimal paymentAmount;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getStudentNo() {
            return studentNo;
        }

        public void setStudentNo(String studentNo) {
            this.studentNo = studentNo;
        }

        public String getAcademicYear() {
            return academicYear;
        }

        public void setAcademicYear(String academicYear) {
            this.academicYear = academicYear;
        }

        public String getSemester() {
            return semester;
        }

        public void setSemester(String semester) {
            this.semester = semester;
        }

        public Long getSchoolingPlanId() {
            return schoolingPlanId;
        }

        public void setSchoolingPlanId(Long schoolingPlanId) {
            this.schoolingPlanId = schoolingPlanId;
        }

        public Long getPendingRecordId() {
            return pendingRecordId;
        }

        public void setPendingRecordId(Long pendingRecordId) {
            this.pendingRecordId = pendingRecordId;
        }

        public String getEconomyCategory() {
            return economyCategory;
        }

        public void setEconomyCategory(String economyCategory) {
            this.economyCategory = economyCategory;
        }

        public Long getPackageId() {
            return packageId;
        }

        public void setPackageId(Long packageId) {
            this.packageId = packageId;
        }

        public BigDecimal getPaymentAmount() {
            return paymentAmount;
        }

        public void setPaymentAmount(BigDecimal paymentAmount) {
            this.paymentAmount = paymentAmount;
        }
    }
}


