package com.ruoyi.system.application.command;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.StStudentSubsidyRecord;
import com.ruoyi.system.domain.subsidy.event.SubsidyApprovedEvent;
import com.ruoyi.system.domain.subsidy.event.SubsidyRejectedEvent;
import com.ruoyi.system.infrastructure.event.DomainEventPublisher;
import com.ruoyi.system.mapper.StStudentSubsidyRecordMapper;
import com.ruoyi.system.mapper.StSemesterBudgetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 审批补助命令处理器
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
@Component
public class ApproveSubsidyCommandHandler {
    
    @Autowired
    private StStudentSubsidyRecordMapper subsidyRecordMapper;
    
    @Autowired
    private StSemesterBudgetMapper budgetMapper;
    
    @Autowired
    private DomainEventPublisher eventPublisher;
    
    /**
     * 处理审批命令
     * 
     * @param command 审批命令
     */
    @Transactional
    public void handle(ApproveSubsidyCommand command) {
        // 1. 获取补助记录
        StStudentSubsidyRecord record = subsidyRecordMapper.selectStStudentSubsidyRecordById(command.getRecordId());
        if (record == null) {
            throw new ServiceException("补助记录不存在");
        }
        
        // 2. 验证状态（只能审批待审批状态的记录）
        if (record.getApprovalStatus() != null && record.getApprovalStatus() != 0) {
            throw new ServiceException("该补助记录已审批，不能重复审批");
        }
        
        // 3. 获取审批人
        String approver = SecurityUtils.getUsername();
        
        // 4. 执行审批
        if (command.isApproved()) {
            approve(record, approver, command.getApprovalMemo());
        } else if (command.isRejected()) {
            reject(record, approver, command.getApprovalMemo());
        } else {
            throw new ServiceException("无效的审批状态");
        }
    }
    
    /**
     * 审批通过
     */
    private void approve(StStudentSubsidyRecord record, String approver, String memo) {
        // 1. 先更新预算：锁定金额转为已使用金额（必须在更新记录状态之前，因为更新操作需要查询approval_status=0的记录）
        int updated = budgetMapper.updateBudgetAmountOnApproval(record.getBudgetId(), record.getSubsidyAmount());
        if (updated == 0) {
            throw new ServiceException("更新预算金额失败，可能锁定金额不足");
        }
        
        // 2. 更新补助记录状态
        int result = subsidyRecordMapper.approveSubsidy(
            record.getId(), 
            1, // 已审批
            approver, 
            memo
        );
        
        if (result == 0) {
            throw new ServiceException("审批失败");
        }
        
        // 3. 发布领域事件
        eventPublisher.publish(new SubsidyApprovedEvent(
            record.getId(),
            record.getBudgetId(),
            record.getSubsidyAmount(),
            approver,
            memo
        ));
    }
    
    /**
     * 审批驳回
     */
    private void reject(StStudentSubsidyRecord record, String approver, String memo) {
        // 1. 先释放预算锁定金额（必须在更新记录状态之前，因为释放操作需要查询approval_status=0的记录）
        int released = budgetMapper.releaseBudgetAmount(record.getBudgetId(), record.getSubsidyAmount());
        if (released == 0) {
            throw new ServiceException("释放预算锁定金额失败，可能锁定金额不足");
        }
        
        // 2. 更新补助记录状态
        int result = subsidyRecordMapper.approveSubsidy(
            record.getId(), 
            2, // 已驳回
            approver, 
            memo
        );
        
        if (result == 0) {
            throw new ServiceException("审批失败");
        }
        
        // 3. 发布领域事件
        eventPublisher.publish(new SubsidyRejectedEvent(
            record.getId(),
            record.getBudgetId(),
            record.getSubsidyAmount(),
            approver,
            memo
        ));
    }
}

