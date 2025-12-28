package com.ruoyi.system.domain.subsidy.event;

import java.math.BigDecimal;

/**
 * 补助驳回事件
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public class SubsidyRejectedEvent extends DomainEvent {
    
    private final Long recordId;
    private final Long budgetId;
    private final BigDecimal amount;
    private final String approver;
    private final String approvalMemo;
    
    public SubsidyRejectedEvent(Long recordId, Long budgetId, BigDecimal amount, 
                                String approver, String approvalMemo) {
        this.recordId = recordId;
        this.budgetId = budgetId;
        this.amount = amount;
        this.approver = approver;
        this.approvalMemo = approvalMemo;
    }
    
    public Long getRecordId() {
        return recordId;
    }
    
    public Long getBudgetId() {
        return budgetId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public String getApprover() {
        return approver;
    }
    
    public String getApprovalMemo() {
        return approvalMemo;
    }
    
    @Override
    public String toString() {
        return String.format("SubsidyRejectedEvent{recordId=%d, budgetId=%d, amount=%s, approver=%s}", 
            recordId, budgetId, amount, approver);
    }
}



