package com.ruoyi.system.application.command;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.domain.StStudentsBase;
import com.ruoyi.system.domain.StSubsidyPackage;
import com.ruoyi.system.domain.StStudentSubsidyRecord;
import com.ruoyi.system.domain.subsidy.valueobject.SubsidyAmount;
import com.ruoyi.system.domain.subsidy.event.SubsidySubmittedEvent;
import com.ruoyi.system.infrastructure.event.DomainEventPublisher;
import com.ruoyi.system.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 补助录入命令处理器
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
@Component
public class CreateSubsidyCommandHandler {
    private static final Logger log = LoggerFactory.getLogger(CreateSubsidyCommandHandler.class);
    
    @Autowired
    private StStudentSubsidyRecordMapper subsidyRecordMapper;
    
    @Autowired
    private StSubsidyPackageMapper packageMapper;
    
    @Autowired
    private StSemesterBudgetMapper budgetMapper;
    
    @Autowired
    private StSchoolYearSemesterMapper yearSemesterMapper;
    
    @Autowired
    private StStudentsBaseMapper studentsBaseMapper;
    
    @Autowired
    private DomainEventPublisher eventPublisher;
    
    @Autowired
    private com.ruoyi.system.application.service.BudgetAllocationService budgetAllocationService;
    
    /**
     * 处理补助录入命令
     * 
     * @param command 补助录入命令
     * @return 创建的补助记录ID列表
     */
    @Transactional
    public List<Long> handle(CreateSubsidyCommand command) {
        // 1. 验证命令
        validateCommand(command);
        
        // 2. 获取套餐信息
        StSubsidyPackage packageInfo = packageMapper.selectStSubsidyPackageById(command.getPackageId());
        if (packageInfo == null || packageInfo.getStatus() == 0) {
            throw new ServiceException("补助套餐不存在或已禁用");
        }
        
        // 3. 确定补助金额
        BigDecimal subsidyAmount = command.getCustomAmount() != null 
            ? command.getCustomAmount() 
            : packageInfo.getSubsidyAmount();
        
        SubsidyAmount amount = new SubsidyAmount(subsidyAmount);
        
        // 4. 获取学生ID列表（新方案直接关联学生）
        List<Long> studentIds = getStudentIds(command);
        if (studentIds.isEmpty()) {
            throw new ServiceException("无法获取学生信息，请检查学生列表");
        }
        
        // 确定预算ID列表（支持多选）
        List<Long> selectedBudgetIds = command.getBudgetIds();
        if (selectedBudgetIds == null || selectedBudgetIds.isEmpty()) {
            // 向后兼容：如果没有设置budgetIds，使用budgetId
            if (command.getBudgetId() != null) {
                selectedBudgetIds = java.util.Arrays.asList(command.getBudgetId());
            } else {
                throw new ServiceException("请至少选择一个预算");
            }
        }
        
        // 去重
        selectedBudgetIds = new java.util.ArrayList<>(new java.util.LinkedHashSet<>(selectedBudgetIds));
        
        Long primaryBudgetId = selectedBudgetIds.get(0); // 主预算ID（用于记录）
        BigDecimal required = amount.multiply(studentIds.size()).getValue();
        
        // 获取当前学期ID（用于预算组合查询）
        Long currentSemesterId = command.getSourceSemesterId();
        if (currentSemesterId == null) {
            throw new ServiceException("资金来源学期ID不能为空");
        }
        
        // 判断是单选还是多选
        com.ruoyi.system.application.service.BudgetAllocationService.AllocationResult allocationResult;
        if (selectedBudgetIds.size() == 1) {
            // 单选模式：只从该预算支出，不使用组合逻辑
            Long singleBudgetId = selectedBudgetIds.get(0);
            com.ruoyi.system.domain.StSemesterBudget singleBudget = budgetMapper.selectStSemesterBudgetByIdForUpdate(singleBudgetId);
            if (singleBudget == null) {
                throw new ServiceException("预算不存在");
            }
            
            // 计算可用金额
            BigDecimal actualLockedAmount = budgetMapper.calculateActualLockedAmount(singleBudgetId);
            if (actualLockedAmount == null) {
                actualLockedAmount = BigDecimal.ZERO;
            }
            BigDecimal actualUsedAmount = budgetMapper.calculateActualUsedAmount(singleBudgetId);
            if (actualUsedAmount == null) {
                actualUsedAmount = BigDecimal.ZERO;
            }
            BigDecimal availableAmount = singleBudget.getBudgetAmount()
                .subtract(actualUsedAmount)
                .subtract(actualLockedAmount);
            
            if (availableAmount.compareTo(required) < 0) {
                throw new ServiceException(String.format("预算余额不足，可用金额：%.2f，需要金额：%.2f", 
                    availableAmount, required));
            }
            
            // 锁定预算金额
            int locked = budgetMapper.lockBudgetAmount(singleBudgetId, required);
            if (locked == 0) {
                throw new ServiceException("锁定预算金额失败，可能余额不足");
            }
            
            // 创建单个分配明细（不使用组合逻辑）
            List<com.ruoyi.system.domain.StBudgetAllocationDetail> singleDetail = new java.util.ArrayList<>();
            com.ruoyi.system.domain.StBudgetAllocationDetail detail = new com.ruoyi.system.domain.StBudgetAllocationDetail();
            detail.setBudgetId(singleBudgetId);
            detail.setAllocatedAmount(required);
            detail.setCreatedAt(new java.util.Date());
            detail.setUpdatedAt(new java.util.Date());
            singleDetail.add(detail);
            
            // 创建 AllocationResult（使用正确的构造函数：primaryBudgetId, allocationDetails）
            allocationResult = new com.ruoyi.system.application.service.BudgetAllocationService.AllocationResult(
                singleBudgetId, singleDetail);
            log.info("CreateSubsidyCommandHandler: 单选模式，预算ID={}，金额={}", singleBudgetId, required);
        } else {
            // 多选模式：使用预算组合逻辑，从用户选择的多个预算中智能分配
            // 直接使用用户选择的预算列表，不通过quota_detail_id查询（因为用户已经明确选择了）
            allocationResult = budgetAllocationService.allocateFromSelectedBudgets(
                selectedBudgetIds, required, currentSemesterId);
            log.info("CreateSubsidyCommandHandler: 组合模式，主预算ID={}，选择预算数量={}，使用预算数量={}", 
                primaryBudgetId, selectedBudgetIds.size(), allocationResult.getAllocationDetails().size());
        }
            
        // 获取预算的经济分类（从主预算获取）
        com.ruoyi.system.domain.StSemesterBudget primaryBudget = budgetMapper.selectStSemesterBudgetById(primaryBudgetId);
        if (primaryBudget == null) {
            throw new ServiceException("主预算不存在");
        }
        String economyCategory = budgetMapper.selectEconomyCategoryByBudgetId(primaryBudgetId);
        if (economyCategory == null || economyCategory.isEmpty()) {
            economyCategory = primaryBudget.getEconomyCategory();
        }
        
        // 批量创建补助记录
        List<Long> recordIds = new ArrayList<>();
        try {
            for (Long studentId : studentIds) {
                // 检查是否已有相同经济分类记录
                if (economyCategory != null && !economyCategory.isEmpty()) {
                    // 1. 检查是否已有未审核的相同经济分类记录
                    int pendingCount = subsidyRecordMapper.countPendingRecordsByStudentAndEconomyCategory(
                        studentId, economyCategory, command.getSourceSemesterId()
                    );
                    if (pendingCount > 0) {
                        String studentName = resolveStudentName(studentId);
                        String economyCategoryName = getEconomyCategoryName(economyCategory);
                        throw new ServiceException(String.format("%s已有%s记录待审核，不能重复录入", 
                            studentName, economyCategoryName));
                    }
                    
                    // 2. 检查是否已有已审核通过的相同经济分类记录
                    int approvedCount = subsidyRecordMapper.countApprovedRecordsByStudentAndEconomyCategory(
                        studentId, economyCategory, command.getSourceSemesterId()
                    );
                    if (approvedCount > 0) {
                        String studentName = resolveStudentName(studentId);
                        String economyCategoryName = getEconomyCategoryName(economyCategory);
                        throw new ServiceException(String.format("%s已有%s记录已审核通过，不能重复录入。如需重新录入，请先在审批页面退回该记录", 
                            studentName, economyCategoryName));
                    }
                }
                
                // 创建补助记录（budget_id 使用主预算ID）
                StStudentSubsidyRecord record = createSubsidyRecord(
                    command, studentId, packageInfo, amount.getValue(), primaryBudgetId
                );
                
                int result = subsidyRecordMapper.insertStStudentSubsidyRecord(record);
                if (result > 0) {
                    recordIds.add(record.getId());
                    
                    // 保存分配明细（每个学生一条补助记录，分配金额为该学生的补助金额）
                    // 注意：如果多个学生共享同一个分配结果，需要按学生数量分摊分配明细
                    // 这里简化处理：每个学生使用相同的分配比例
                    if (allocationResult.getAllocationDetails() != null && !allocationResult.getAllocationDetails().isEmpty()) {
                        // 为每个学生创建分配明细（按学生数量分摊总金额）
                        BigDecimal studentAmount = amount.getValue();
                        List<com.ruoyi.system.domain.StBudgetAllocationDetail> studentAllocationDetails = 
                            createStudentAllocationDetails(record.getId(), allocationResult.getAllocationDetails(), 
                                studentAmount, required);
                        budgetAllocationService.saveAllocationDetails(record.getId(), studentAllocationDetails);
                    }
                    
                    // 发布领域事件
                    eventPublisher.publish(new SubsidySubmittedEvent(
                        record.getId(),
                        primaryBudgetId,
                        command.getSourceSemesterId(),
                        amount.getValue(),
                        studentId
                    ));
                }
            }
            
            return recordIds;
        } catch (Exception e) {
            // 如果创建失败，需要释放已锁定的预算
            log.error("CreateSubsidyCommandHandler: 创建补助记录失败，释放已分配的预算", e);
            try {
                // 释放所有已分配的预算金额
                if (allocationResult != null && allocationResult.getAllocationDetails() != null) {
                    for (com.ruoyi.system.domain.StBudgetAllocationDetail detail : allocationResult.getAllocationDetails()) {
                        budgetMapper.releaseBudgetAmount(detail.getBudgetId(), detail.getAllocatedAmount());
                    }
                }
            } catch (Exception releaseException) {
                log.error("CreateSubsidyCommandHandler: 释放预算失败", releaseException);
            }
            throw new ServiceException("创建补助记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取学生ID列表（新方案：直接关联学生，校验当前学期存在）
     */
    private List<Long> getStudentIds(CreateSubsidyCommand command) {
        List<Long> studentIds = new ArrayList<>();

        Long currentSemesterId = command.getSourceSemesterId();
        if (currentSemesterId == null) {
            throw new ServiceException("资金来源学期ID不能为空，无法确定当前学期");
        }
        // 确认学期存在
        StSchoolYearSemester yearSemester = yearSemesterMapper.selectStSchoolYearSemesterById(currentSemesterId);
        if (yearSemester == null) {
            throw new ServiceException("找不到对应的学年学期记录，ID: " + currentSemesterId);
        }

        if (command.getStudentInfos() != null && !command.getStudentInfos().isEmpty()) {
            for (CreateSubsidyCommand.StudentInfo studentInfo : command.getStudentInfos()) {
                if (studentInfo.getStudentId() == null) {
                    throw new ServiceException("学生ID不能为空");
                }
                studentIds.add(studentInfo.getStudentId());
            }
        } else if (command.getStudentSemesterRecordIds() != null && !command.getStudentSemesterRecordIds().isEmpty()) {
            // 兼容：旧字段直接视为学生ID列表使用（不再查学期记录表）
            studentIds.addAll(command.getStudentSemesterRecordIds());
        }

        return studentIds;
    }
    
    /**
     * 验证命令
     */
    private void validateCommand(CreateSubsidyCommand command) {
        // 新版本：验证学生信息列表
        if (command.getStudentInfos() != null && !command.getStudentInfos().isEmpty()) {
            for (CreateSubsidyCommand.StudentInfo info : command.getStudentInfos()) {
                if (info.getStudentId() == null) {
                    throw new ServiceException("学生ID不能为空");
                }
                if (info.getAcademicYear() == null || info.getAcademicYear().trim().isEmpty()) {
                    throw new ServiceException("学年不能为空");
                }
                if (info.getSemester() == null || info.getSemester().trim().isEmpty()) {
                    throw new ServiceException("学期不能为空");
                }
            }
        }
        // 旧版本：验证学生学期记录ID列表（向后兼容）
        else if (command.getStudentSemesterRecordIds() == null || command.getStudentSemesterRecordIds().isEmpty()) {
            throw new ServiceException("学生信息或学生学期记录ID列表不能为空");
        }
        
        if (command.getPackageId() == null) {
            throw new ServiceException("补助套餐ID不能为空");
        }
        // 验证预算ID（支持多选）
        if ((command.getBudgetIds() == null || command.getBudgetIds().isEmpty()) 
            && command.getBudgetId() == null) {
            throw new ServiceException("预算ID不能为空，请至少选择一个预算");
        }
        if (command.getSourceSemesterId() == null) {
            throw new ServiceException("资金来源学期ID不能为空");
        }
    }
    
    /**
     * 创建补助记录实体
     */
    private StStudentSubsidyRecord createSubsidyRecord(
        CreateSubsidyCommand command,
        Long studentId,
        StSubsidyPackage packageInfo,
        BigDecimal amount,
        Long primaryBudgetId
    ) {
        StStudentSubsidyRecord record = new StStudentSubsidyRecord();
        record.setStudentId(studentId);
        // 兼容旧字段，保持非空以满足表结构约束
        record.setStudentSemesterRecordId(studentId);
        record.setBudgetId(primaryBudgetId); // 使用主预算ID
        record.setSourceSemesterId(command.getSourceSemesterId());
        record.setYearSemesterId(command.getSourceSemesterId());
        record.setPackageId(command.getPackageId());
        record.setSubsidyType(packageInfo.getPackageName());
        record.setSubsidyAmount(amount);
        record.setApprovalStatus(0); // 待审批
        record.setPaymentStatus(0); // 待发放
        record.setMemo(command.getMemo());
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());
        
        // 设置学制ID（从套餐获取）
        record.setSchoolingPlanId(packageInfo.getSchoolingPlanId());
        
        return record;
    }
    
    /**
     * 获取经济分类名称
     */
    private String getEconomyCategoryName(String economyCategory) {
        if (economyCategory == null || economyCategory.isEmpty()) {
            return "未知";
        }
        switch (economyCategory) {
            case "1":
            case "助学金":
                return "助学金";
            case "2":
            case "免学杂费":
                return "免学杂费";
            case "3":
            case "免学费":
                return "免学费";
            case "4":
            case "营养改善计划":
                return "营养改善计划";
            default:
                return economyCategory;
        }
    }

    /**
     * 根据学生ID获取姓名（用于提示）
     */
    private String resolveStudentName(Long studentId) {
        if (studentId == null) {
            return "该学生";
        }
        StStudentsBase student = studentsBaseMapper.selectStStudentsBaseById(studentId);
        if (student != null && student.getName() != null && !student.getName().isEmpty()) {
            return student.getName();
        }
        return "该学生";
    }
    
    /**
     * 为学生创建分配明细（按比例分摊总分配明细）
     * 
     * @param subsidyRecordId 补助记录ID
     * @param totalAllocationDetails 总分配明细
     * @param studentAmount 学生补助金额
     * @param totalAmount 总补助金额
     * @return 学生分配明细列表
     */
    private List<com.ruoyi.system.domain.StBudgetAllocationDetail> createStudentAllocationDetails(
        Long subsidyRecordId,
        List<com.ruoyi.system.domain.StBudgetAllocationDetail> totalAllocationDetails,
        BigDecimal studentAmount,
        BigDecimal totalAmount) {
        
        List<com.ruoyi.system.domain.StBudgetAllocationDetail> studentDetails = new ArrayList<>();
        
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return studentDetails;
        }
        
        // 按比例计算每个预算的分配金额
        BigDecimal ratio = studentAmount.divide(totalAmount, 10, java.math.RoundingMode.HALF_UP);
        BigDecimal allocatedSum = BigDecimal.ZERO;
        
        for (int i = 0; i < totalAllocationDetails.size(); i++) {
            com.ruoyi.system.domain.StBudgetAllocationDetail totalDetail = totalAllocationDetails.get(i);
            BigDecimal allocatedAmount;
            
            // 最后一个预算，使用剩余金额（避免精度问题）
            if (i == totalAllocationDetails.size() - 1) {
                allocatedAmount = studentAmount.subtract(allocatedSum);
            } else {
                allocatedAmount = totalDetail.getAllocatedAmount().multiply(ratio)
                    .setScale(2, java.math.RoundingMode.HALF_UP);
                allocatedSum = allocatedSum.add(allocatedAmount);
            }
            
            com.ruoyi.system.domain.StBudgetAllocationDetail detail = new com.ruoyi.system.domain.StBudgetAllocationDetail();
            detail.setSubsidyRecordId(subsidyRecordId);
            detail.setBudgetId(totalDetail.getBudgetId());
            detail.setAllocatedAmount(allocatedAmount);
            detail.setCreatedAt(new Date());
            detail.setUpdatedAt(new Date());
            studentDetails.add(detail);
        }
        
        return studentDetails;
    }
}

