package com.ruoyi.system.domain.subsidy.state;

/**
 * 审批状态接口
 * 使用状态模式管理补助记录的审批状态流转
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public interface ApprovalState {
    
    /**
     * 审批通过
     * 
     * @param context 状态上下文
     * @param approver 审批人
     * @param memo 审批意见
     */
    void approve(ApprovalStateContext context, String approver, String memo);
    
    /**
     * 审批驳回
     * 
     * @param context 状态上下文
     * @param approver 审批人
     * @param memo 审批意见
     */
    void reject(ApprovalStateContext context, String approver, String memo);
    
    /**
     * 获取状态码
     * 0=待审批，1=已审批，2=已驳回
     * 
     * @return 状态码
     */
    Integer getStatusCode();
    
    /**
     * 获取状态描述
     * 
     * @return 状态描述
     */
    String getStatusDescription();
}



