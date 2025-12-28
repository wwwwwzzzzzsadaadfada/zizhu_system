package com.ruoyi.system.infrastructure.event;

import com.ruoyi.system.domain.subsidy.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 领域事件发布器
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
@Component
public class DomainEventPublisher {
    
    private static final Logger log = LoggerFactory.getLogger(DomainEventPublisher.class);
    
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    
    /**
     * 发布领域事件
     * 
     * @param event 领域事件
     */
    public void publish(DomainEvent event) {
        log.debug("发布领域事件: {}", event);
        applicationEventPublisher.publishEvent(event);
    }
}



