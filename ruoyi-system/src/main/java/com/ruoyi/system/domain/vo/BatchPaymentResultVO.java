package com.ruoyi.system.domain.vo;

import java.util.List;

/**
 * 批量发放结果VO
 * 
 * @author ruoyi
 * @date 2024-12-XX
 */
public class BatchPaymentResultVO {
    
    /** 成功数量 */
    private Integer successCount;
    
    /** 失败数量 */
    private Integer failureCount;
    
    /** 总数量 */
    private Integer totalCount;
    
    /** 失败记录列表（包含错误信息） */
    private List<BatchPaymentErrorVO> errors;

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

    public List<BatchPaymentErrorVO> getErrors() {
        return errors;
    }

    public void setErrors(List<BatchPaymentErrorVO> errors) {
        this.errors = errors;
    }
}



