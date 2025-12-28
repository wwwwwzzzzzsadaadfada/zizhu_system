package com.ruoyi.system.application.command;

import java.math.BigDecimal;
import java.util.List;

/**
 * 补助录入命令
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public class CreateSubsidyCommand {
    
    /** 学生学期记录ID列表（批量录入，向后兼容旧版本） */
    private List<Long> studentSemesterRecordIds;
    
    /** 学生信息列表（新版本，推荐使用） */
    private List<StudentInfo> studentInfos;
    
    /** 补助套餐ID */
    private Long packageId;
    
    /** 资金来源学期ID */
    private Long sourceSemesterId;
    
    /** 预算ID（向后兼容，如果设置了budgetIds则忽略此字段） */
    private Long budgetId;
    
    /** 预算ID列表（支持多选，如果只选一个则只从该预算支出，如果选多个则使用组合逻辑） */
    private List<Long> budgetIds;
    
    /** 自定义金额（可选，如果为空则使用套餐默认金额） */
    private BigDecimal customAmount;
    
    /** 备注 */
    private String memo;
    
    /**
     * 学生信息内部类
     */
    public static class StudentInfo {
        /** 学生基础信息ID（st_aided_student_info.student_id） */
        private Long studentId;
        
        /** 学年（st_aided_student_info.academic_year） */
        private String academicYear;
        
        /** 学期（st_aided_student_info.semester） */
        private String semester;
        
        public Long getStudentId() {
            return studentId;
        }
        
        public void setStudentId(Long studentId) {
            this.studentId = studentId;
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
    }
    
    public List<Long> getStudentSemesterRecordIds() {
        return studentSemesterRecordIds;
    }
    
    public void setStudentSemesterRecordIds(List<Long> studentSemesterRecordIds) {
        this.studentSemesterRecordIds = studentSemesterRecordIds;
    }
    
    public Long getPackageId() {
        return packageId;
    }
    
    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }
    
    public Long getSourceSemesterId() {
        return sourceSemesterId;
    }
    
    public void setSourceSemesterId(Long sourceSemesterId) {
        this.sourceSemesterId = sourceSemesterId;
    }
    
    public Long getBudgetId() {
        return budgetId;
    }
    
    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }
    
    public BigDecimal getCustomAmount() {
        return customAmount;
    }
    
    public void setCustomAmount(BigDecimal customAmount) {
        this.customAmount = customAmount;
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public List<StudentInfo> getStudentInfos() {
        return studentInfos;
    }
    
    public void setStudentInfos(List<StudentInfo> studentInfos) {
        this.studentInfos = studentInfos;
    }
    
    public List<Long> getBudgetIds() {
        return budgetIds;
    }
    
    public void setBudgetIds(List<Long> budgetIds) {
        this.budgetIds = budgetIds;
    }
}



