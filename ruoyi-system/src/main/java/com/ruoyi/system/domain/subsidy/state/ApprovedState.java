package com.ruoyi.system.domain.subsidy.state;

/**
 * 已审批状态
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public class ApprovedState implements ApprovalState {
    
    @Override
    public void approve(ApprovalStateContext context, String approver, String memo) {
        throw new IllegalStateException("已审批的记录不能再次审批");
    }
    
    @Override
    public void reject(ApprovalStateContext context, String approver, String memo) {
        throw new IllegalStateException("已审批的记录不能驳回");
    }
    
    @Override
    public Integer getStatusCode() {
        return 1; // 已审批
    }
    
    @Override
    public String getStatusDescription() {
        return "已审批";
    }
}



