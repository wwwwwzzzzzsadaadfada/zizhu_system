package com.ruoyi.system.domain.subsidy.event;

import java.math.BigDecimal;

/**
 * 补助提交事件
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public class SubsidySubmittedEvent extends DomainEvent {
    
    private final Long recordId;
    private final Long budgetId;
    private final Long semesterId;
    private final BigDecimal amount;
    private final Long studentId;
    
    public SubsidySubmittedEvent(Long recordId, Long budgetId, Long semesterId, 
                                 BigDecimal amount, Long studentId) {
        this.recordId = recordId;
        this.budgetId = budgetId;
        this.semesterId = semesterId;
        this.amount = amount;
        this.studentId = studentId;
    }
    
    public Long getRecordId() {
        return recordId;
    }
    
    public Long getBudgetId() {
        return budgetId;
    }
    
    public Long getSemesterId() {
        return semesterId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public Long getStudentId() {
        return studentId;
    }
    
    @Override
    public String toString() {
        return String.format("SubsidySubmittedEvent{recordId=%d, budgetId=%d, amount=%s}", 
            recordId, budgetId, amount);
    }
}



