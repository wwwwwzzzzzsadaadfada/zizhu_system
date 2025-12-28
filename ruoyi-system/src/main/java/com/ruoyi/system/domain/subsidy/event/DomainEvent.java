package com.ruoyi.system.domain.subsidy.event;

import java.time.LocalDateTime;

/**
 * 领域事件基类
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public abstract class DomainEvent {
    
    private final LocalDateTime occurredOn;
    
    public DomainEvent() {
        this.occurredOn = LocalDateTime.now();
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}



