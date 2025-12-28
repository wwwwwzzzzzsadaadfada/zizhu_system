package com.ruoyi.system.infrastructure.event.handler;

import com.ruoyi.system.domain.subsidy.event.SubsidyApprovedEvent;
import com.ruoyi.system.domain.subsidy.event.SubsidyRejectedEvent;
import com.ruoyi.system.domain.subsidy.event.SubsidySubmittedEvent;
import com.ruoyi.system.mapper.StSemesterBudgetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 预算事件处理器
 * 处理与预算相关的领域事件
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
@Component
public class BudgetEventHandler {
    
    private static final Logger log = LoggerFactory.getLogger(BudgetEventHandler.class);
    
    @Autowired
    private StSemesterBudgetMapper budgetMapper;
    
    /**
     * 处理补助提交事件
     * 注意：预算锁定已经在命令处理器中完成，这里可以做额外的处理（如日志记录）
     */
    @EventListener
    @Async
    public void handleSubsidySubmitted(SubsidySubmittedEvent event) {
        log.info("处理补助提交事件: recordId={}, budgetId={}, amount={}", 
            event.getRecordId(), event.getBudgetId(), event.getAmount());
        // 可以在这里记录审计日志、发送通知等
    }
    
    /**
     * 处理补助审批通过事件
     * 注意：预算更新已经在审批命令处理器中完成，这里可以做额外的处理
     */
    @EventListener
    @Async
    public void handleSubsidyApproved(SubsidyApprovedEvent event) {
        log.info("处理补助审批通过事件: recordId={}, budgetId={}, amount={}, approver={}", 
            event.getRecordId(), event.getBudgetId(), event.getAmount(), event.getApprover());
        // 可以在这里记录审计日志、发送通知等
    }
    
    /**
     * 处理补助驳回事件
     * 注意：预算释放已经在审批命令处理器中完成，这里可以做额外的处理
     */
    @EventListener
    @Async
    public void handleSubsidyRejected(SubsidyRejectedEvent event) {
        log.info("处理补助驳回事件: recordId={}, budgetId={}, amount={}, approver={}", 
            event.getRecordId(), event.getBudgetId(), event.getAmount(), event.getApprover());
        // 可以在这里记录审计日志、发送通知等
    }
}



