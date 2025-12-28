package com.ruoyi.system.domain.subsidy.state;

/**
 * 审批状态上下文
 * 封装状态转换所需的信息
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public class ApprovalStateContext {
    
    private ApprovalState currentState;
    private Long recordId;
    private Long budgetId;
    private java.math.BigDecimal subsidyAmount;
    
    public ApprovalStateContext(Long recordId, Long budgetId, java.math.BigDecimal subsidyAmount) {
        this.recordId = recordId;
        this.budgetId = budgetId;
        this.subsidyAmount = subsidyAmount;
        this.currentState = new PendingApprovalState(); // 默认待审批状态
    }
    
    public ApprovalState getCurrentState() {
        return currentState;
    }
    
    public void setCurrentState(ApprovalState state) {
        this.currentState = state;
    }
    
    public Long getRecordId() {
        return recordId;
    }
    
    public Long getBudgetId() {
        return budgetId;
    }
    
    public java.math.BigDecimal getSubsidyAmount() {
        return subsidyAmount;
    }
    
    /**
     * 执行审批通过操作
     */
    public void approve(String approver, String memo) {
        currentState.approve(this, approver, memo);
    }
    
    /**
     * 执行审批驳回操作
     */
    public void reject(String approver, String memo) {
        currentState.reject(this, approver, memo);
    }
}



