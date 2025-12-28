package com.ruoyi.system.domain.vo;

/**
 * 批量发放错误信息VO
 * 
 * @author ruoyi
 * @date 2024-12-XX
 */
public class BatchPaymentErrorVO {
    
    /** 学生姓名 */
    private String studentName;
    
    /** 学籍号 */
    private String studentNo;
    
    /** 错误信息 */
    private String errorMessage;

    public BatchPaymentErrorVO() {
    }

    public BatchPaymentErrorVO(String studentName, String studentNo, String errorMessage) {
        this.studentName = studentName;
        this.studentNo = studentNo;
        this.errorMessage = errorMessage;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}



