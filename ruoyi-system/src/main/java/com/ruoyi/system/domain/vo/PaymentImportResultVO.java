package com.ruoyi.system.domain.vo;

import java.util.List;

/**
 * 批量发放导入结果VO
 * 
 * @author ruoyi
 * @date 2024-12-XX
 */
public class PaymentImportResultVO {
    
    /** 成功数量 */
    private Integer successCount;
    
    /** 失败数量 */
    private Integer failureCount;
    
    /** 总数量 */
    private Integer totalCount;
    
    /** 失败记录列表（包含错误信息） */
    private List<PaymentImportErrorVO> errorList;

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<PaymentImportErrorVO> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<PaymentImportErrorVO> errorList) {
        this.errorList = errorList;
    }
}




