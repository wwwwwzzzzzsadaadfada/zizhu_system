package com.ruoyi.system.domain.vo;

/**
 * 批量发放导入错误信息VO
 * 
 * @author ruoyi
 * @date 2024-12-XX
 */
public class PaymentImportErrorVO {
    
    /** 行号 */
    private Integer rowNum;
    
    /** 姓名 */
    private String name;
    
    /** 学籍号 */
    private String studentNo;
    
    /** 错误信息 */
    private String errorMsg;

    public PaymentImportErrorVO() {
    }

    public PaymentImportErrorVO(Integer rowNum, String name, String studentNo, String errorMsg) {
        this.rowNum = rowNum;
        this.name = name;
        this.studentNo = studentNo;
        this.errorMsg = errorMsg;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}




