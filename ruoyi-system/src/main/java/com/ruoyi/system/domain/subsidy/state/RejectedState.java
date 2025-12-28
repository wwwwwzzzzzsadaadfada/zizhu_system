package com.ruoyi.system.domain.subsidy.state;

/**
 * 已驳回状态
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public class RejectedState implements ApprovalState {
    
    @Override
    public void approve(ApprovalStateContext context, String approver, String memo) {
        // 驳回后可以重新审批（可选业务规则）
        // 这里可以根据实际业务需求决定是否允许
        throw new IllegalStateException("已驳回的记录不能直接审批通过，请重新提交");
    }
    
    @Override
    public void reject(ApprovalStateContext context, String approver, String memo) {
        throw new IllegalStateException("已驳回的记录不能再次驳回");
    }
    
    @Override
    public Integer getStatusCode() {
        return 2; // 已驳回
    }
    
    @Override
    public String getStatusDescription() {
        return "已驳回";
    }
}



