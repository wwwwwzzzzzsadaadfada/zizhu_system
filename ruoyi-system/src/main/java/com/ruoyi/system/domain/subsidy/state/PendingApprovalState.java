package com.ruoyi.system.domain.subsidy.state;

/**
 * 待审批状态
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public class PendingApprovalState implements ApprovalState {
    
    @Override
    public void approve(ApprovalStateContext context, String approver, String memo) {
        // 状态转换为已审批
        context.setCurrentState(new ApprovedState());
        // 业务逻辑：预算锁定金额转为已使用金额
        // 这个逻辑应该在领域服务或命令处理器中处理
    }
    
    @Override
    public void reject(ApprovalStateContext context, String approver, String memo) {
        // 状态转换为已驳回
        context.setCurrentState(new RejectedState());
        // 业务逻辑：释放预算锁定金额
        // 这个逻辑应该在领域服务或命令处理器中处理
    }
    
    @Override
    public Integer getStatusCode() {
        return 0; // 待审批
    }
    
    @Override
    public String getStatusDescription() {
        return "待审批";
    }
}



