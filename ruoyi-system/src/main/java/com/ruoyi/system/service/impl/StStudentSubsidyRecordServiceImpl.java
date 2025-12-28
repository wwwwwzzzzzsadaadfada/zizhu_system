package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.StStudentSubsidyRecord;
import com.ruoyi.system.domain.StStudentsBase;
import com.ruoyi.system.domain.dto.BatchPaymentRequest;
import com.ruoyi.system.domain.dto.PaymentImportDTO;
import com.ruoyi.system.domain.vo.BatchPaymentErrorVO;
import com.ruoyi.system.domain.vo.BatchPaymentResultVO;
import com.ruoyi.system.domain.vo.PaymentImportErrorVO;
import com.ruoyi.system.domain.vo.PaymentImportResultVO;
import com.ruoyi.system.mapper.StStudentSubsidyRecordMapper;
import com.ruoyi.system.mapper.StSemesterBudgetMapper;
import com.ruoyi.system.mapper.StSchoolYearSemesterMapper;
import com.ruoyi.system.mapper.StStudentsBaseMapper;
import com.ruoyi.system.mapper.StBudgetAllocationDetailMapper;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.service.IStStudentSubsidyRecordService;
import com.ruoyi.common.core.redis.RedisDistributedLock;

/**
 * 学生补助发放记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-22
 */
@Service
public class StStudentSubsidyRecordServiceImpl implements IStStudentSubsidyRecordService 
{
    private static final Logger log = LoggerFactory.getLogger(StStudentSubsidyRecordServiceImpl.class);
    
    @Autowired
    private StStudentSubsidyRecordMapper stStudentSubsidyRecordMapper;
    
    @Autowired
    private StSemesterBudgetMapper stSemesterBudgetMapper;
    
    @Autowired
    private StSchoolYearSemesterMapper stSchoolYearSemesterMapper;
    
    @Autowired
    private StStudentsBaseMapper stStudentsBaseMapper;
    
    @Autowired
    private RedisDistributedLock redisDistributedLock;
    
    @Autowired
    private com.ruoyi.system.application.service.BudgetAllocationService budgetAllocationService;
    
    @Autowired
    private StBudgetAllocationDetailMapper stBudgetAllocationDetailMapper;

    /**
     * 查询学生补助发放记录
     * 
     * @param id 学生补助发放记录主键
     * @return 学生补助发放记录
     */
    @Override
    public StStudentSubsidyRecord selectStStudentSubsidyRecordById(Long id)
    {
        return stStudentSubsidyRecordMapper.selectStStudentSubsidyRecordById(id);
    }

    /**
     * 查询学生补助发放记录列表
     * 
     * @param stStudentSubsidyRecord 学生补助发放记录
     * @return 学生补助发放记录
     */
    @Override
    public List<StStudentSubsidyRecord> selectStStudentSubsidyRecordList(StStudentSubsidyRecord stStudentSubsidyRecord)
    {
        return stStudentSubsidyRecordMapper.selectStStudentSubsidyRecordList(stStudentSubsidyRecord);
    }

    /**
     * 新增学生补助发放记录
     * 
     * @param stStudentSubsidyRecord 学生补助发放记录
     * @return 结果
     */
    @Override
    @Transactional(isolation = org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
    public int insertStStudentSubsidyRecord(StStudentSubsidyRecord stStudentSubsidyRecord)
    {
        Long budgetId = stStudentSubsidyRecord.getBudgetId();
        String lockKey = "budget:" + budgetId;
        String lockValue = null;
        
        try {
            // 1. 获取分布式锁（30秒超时）
            lockValue = redisDistributedLock.tryLock(lockKey, 30);
            if (lockValue == null) {
                log.warn("insertStStudentSubsidyRecord: 获取预算锁失败，预算ID={}，可能正在被其他操作使用", budgetId);
                throw new RuntimeException("预算正在被其他操作使用，请稍后重试");
            }
            
            log.info("insertStStudentSubsidyRecord: 获取预算锁成功，预算ID={}，金额={}", 
                budgetId, stStudentSubsidyRecord.getSubsidyAmount());
            
            // 2. 使用悲观锁查询预算（双重保护：分布式锁 + 数据库锁）
            com.ruoyi.system.domain.StSemesterBudget budget = stSemesterBudgetMapper
                .selectStSemesterBudgetByIdForUpdate(budgetId);
            
            if (budget == null) {
                throw new RuntimeException("预算不存在");
            }
            
            // 3. 验证可用金额（在锁保护下，使用动态计算的锁定金额确保准确性）
            BigDecimal usedAmount = budget.getUsedAmount() != null ? budget.getUsedAmount() : BigDecimal.ZERO;
            BigDecimal actualLockedAmount = stSemesterBudgetMapper.calculateActualLockedAmount(budgetId);
            if (actualLockedAmount == null) {
                actualLockedAmount = BigDecimal.ZERO;
            }
            BigDecimal availableAmount = budget.getBudgetAmount()
                .subtract(usedAmount)
                .subtract(actualLockedAmount);
            
            if (availableAmount.compareTo(stStudentSubsidyRecord.getSubsidyAmount()) < 0) {
                log.warn("insertStStudentSubsidyRecord: 预算余额不足，预算ID={}，可用金额={}，需要金额={}，已用金额={}，实际锁定金额={}", 
                    budgetId, availableAmount, stStudentSubsidyRecord.getSubsidyAmount(), usedAmount, actualLockedAmount);
                throw new RuntimeException("预算余额不足，无法申请补助");
            }

            // 补全学生ID/学期ID（兼容旧字段）
            if (stStudentSubsidyRecord.getStudentId() == null && stStudentSubsidyRecord.getStudentSemesterRecordId() != null) {
                stStudentSubsidyRecord.setStudentId(stStudentSubsidyRecord.getStudentSemesterRecordId());
            }
            if (stStudentSubsidyRecord.getYearSemesterId() == null) {
                stStudentSubsidyRecord.setYearSemesterId(stStudentSubsidyRecord.getSourceSemesterId());
            }
            
            // 4. 更新锁定金额（此时其他事务被阻塞，保证数据一致性）
            log.info("insertStStudentSubsidyRecord: 准备锁定预算金额，预算ID={}，金额={}", 
                budgetId, stStudentSubsidyRecord.getSubsidyAmount());
            int locked = stSemesterBudgetMapper.lockBudgetAmount(
                budgetId, 
                stStudentSubsidyRecord.getSubsidyAmount()
            );
            
            if (locked == 0) {
                log.warn("insertStStudentSubsidyRecord: 锁定预算金额失败，预算ID={}，金额={}，可能余额不足", 
                    budgetId, stStudentSubsidyRecord.getSubsidyAmount());
                throw new RuntimeException("预算余额不足，无法申请补助");
            }
            
            log.info("insertStStudentSubsidyRecord: 锁定预算金额成功，预算ID={}，金额={}，影响行数={}", 
                budgetId, stStudentSubsidyRecord.getSubsidyAmount(), locked);
            
            // 5. 检查是否已有相同经济分类记录
            if (stStudentSubsidyRecord.getStudentId() != null 
                && stStudentSubsidyRecord.getYearSemesterId() != null) {
                // 获取预算的经济分类（从预算对象中获取，如果为空则从数据库查询）
                String economyCategory = budget.getEconomyCategory();
                if (economyCategory == null || economyCategory.isEmpty()) {
                    economyCategory = stSemesterBudgetMapper.selectEconomyCategoryByBudgetId(budgetId);
                }
                if (economyCategory != null && !economyCategory.isEmpty()) {
                    // 5.1 检查是否已有未审核的相同经济分类记录
                    int pendingCount = stStudentSubsidyRecordMapper.countPendingRecordsByStudentAndEconomyCategory(
                        stStudentSubsidyRecord.getStudentId(), 
                        economyCategory, 
                        stStudentSubsidyRecord.getYearSemesterId()
                    );
                    if (pendingCount > 0) {
                        // 获取学生信息用于提示
                        String studentName = "该学生";
                        StStudentsBase student = stStudentsBaseMapper.selectStStudentsBaseById(stStudentSubsidyRecord.getStudentId());
                        if (student != null && student.getName() != null) {
                            studentName = student.getName();
                        }
                        String economyCategoryName = getEconomyCategoryName(economyCategory);
                        throw new RuntimeException(String.format("%s已有%s记录待审核，不能重复录入", 
                            studentName, economyCategoryName));
                    }
                    
                    // 5.2 检查是否已有已审核通过的相同经济分类记录
                    int approvedCount = stStudentSubsidyRecordMapper.countApprovedRecordsByStudentAndEconomyCategory(
                        stStudentSubsidyRecord.getStudentId(), 
                        economyCategory, 
                        stStudentSubsidyRecord.getYearSemesterId()
                    );
                    if (approvedCount > 0) {
                        // 获取学生信息用于提示
                        String studentName = "该学生";
                        StStudentsBase student = stStudentsBaseMapper.selectStStudentsBaseById(stStudentSubsidyRecord.getStudentId());
                        if (student != null && student.getName() != null) {
                            studentName = student.getName();
                        }
                        String economyCategoryName = getEconomyCategoryName(economyCategory);
                        throw new RuntimeException(String.format("%s已有%s记录已审核通过，不能重复录入。如需重新录入，请先在审批页面退回该记录", 
                            studentName, economyCategoryName));
                    }
                }
            }
            
            // 6. 插入补助记录
            return stStudentSubsidyRecordMapper.insertStStudentSubsidyRecord(stStudentSubsidyRecord);
            
        } finally {
            // 6. 释放分布式锁
            if (lockValue != null) {
                boolean released = redisDistributedLock.releaseLock(lockKey, lockValue);
                if (released) {
                    log.info("insertStStudentSubsidyRecord: 释放预算锁成功，预算ID={}", budgetId);
                } else {
                    log.warn("insertStStudentSubsidyRecord: 释放预算锁失败，预算ID={}，锁可能已过期", budgetId);
                }
            }
        }
    }

    /**
     * 修改学生补助发放记录
     * 
     * @param stStudentSubsidyRecord 学生补助发放记录
     * @return 结果
     */
    @Override
    public int updateStStudentSubsidyRecord(StStudentSubsidyRecord stStudentSubsidyRecord)
    {
        return stStudentSubsidyRecordMapper.updateStStudentSubsidyRecord(stStudentSubsidyRecord);
    }

    /**
     * 批量删除学生补助发放记录
     * 
     * @param ids 需要删除的学生补助发放记录主键集合
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStStudentSubsidyRecordByIds(Long[] ids)
    {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        
        int successCount = 0;
        for (Long id : ids) {
            try {
                // 查询补助记录
                StStudentSubsidyRecord record = stStudentSubsidyRecordMapper.selectStStudentSubsidyRecordById(id);
                if (record == null) {
                    log.warn("删除补助记录失败：记录不存在，ID={}", id);
                    continue;
                }
                
                // 只有待审批状态的记录才能删除
                if (record.getApprovalStatus() == null || record.getApprovalStatus() == 0) {
                    // 释放锁定的预算金额
                    if (record.getBudgetId() != null && record.getSubsidyAmount() != null) {
                        // 检查是否有分配明细（预算组合）
                        List<com.ruoyi.system.domain.StBudgetAllocationDetail> allocationDetails = 
                            stBudgetAllocationDetailMapper.selectBySubsidyRecordId(id);
                        
                        if (allocationDetails != null && !allocationDetails.isEmpty()) {
                            // 使用预算组合，释放所有分配明细
                            budgetAllocationService.releaseAllocation(id);
                        } else {
                            // 未使用预算组合，直接释放主预算
                            int released = stSemesterBudgetMapper.releaseBudgetAmount(
                                record.getBudgetId(), record.getSubsidyAmount());
                            if (released == 0) {
                                log.warn("释放预算锁定金额失败：可能锁定金额不足，记录ID={}，预算ID={}，金额={}", 
                                    id, record.getBudgetId(), record.getSubsidyAmount());
                            }
                        }
                    }
                    
                    // 删除补助记录
                    int result = stStudentSubsidyRecordMapper.deleteStStudentSubsidyRecordById(id);
                    if (result > 0) {
                        successCount++;
                    }
                } else {
                    log.warn("删除补助记录失败：只能删除待审批状态的记录，当前状态={}，记录ID={}", 
                        record.getApprovalStatus(), id);
                }
            } catch (Exception e) {
                log.error("删除补助记录失败，记录ID={}", id, e);
            }
        }
        
        return successCount;
    }

    /**
     * 删除学生补助发放记录信息
     * 
     * @param id 学生补助发放记录主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStStudentSubsidyRecordById(Long id)
    {
        // 查询补助记录
        StStudentSubsidyRecord record = stStudentSubsidyRecordMapper.selectStStudentSubsidyRecordById(id);
        if (record == null) {
            throw new ServiceException("补助记录不存在");
        }
        
        // 只有待审批状态的记录才能删除
        if (record.getApprovalStatus() != null && record.getApprovalStatus() != 0) {
            throw new ServiceException("只能删除待审批状态的记录");
        }
        
        // 释放锁定的预算金额
        if (record.getBudgetId() != null && record.getSubsidyAmount() != null) {
            // 检查是否有分配明细（预算组合）
            List<com.ruoyi.system.domain.StBudgetAllocationDetail> allocationDetails = 
                stBudgetAllocationDetailMapper.selectBySubsidyRecordId(id);
            
            if (allocationDetails != null && !allocationDetails.isEmpty()) {
                // 使用预算组合，释放所有分配明细
                budgetAllocationService.releaseAllocation(id);
            } else {
                // 未使用预算组合，直接释放主预算
                int released = stSemesterBudgetMapper.releaseBudgetAmount(
                    record.getBudgetId(), record.getSubsidyAmount());
                if (released == 0) {
                    log.warn("释放预算锁定金额失败：可能锁定金额不足，记录ID={}，预算ID={}，金额={}", 
                        id, record.getBudgetId(), record.getSubsidyAmount());
                }
            }
        }
        
        // 删除补助记录
        return stStudentSubsidyRecordMapper.deleteStStudentSubsidyRecordById(id);
    }

    /**
     * 审批补助
     * 
     * @param id 补助记录ID
     * @param approvalStatus 审批状态
     * @param approvalMemo 审批意见
     * @return 结果
     */
    @Override
    @Transactional
    public int approveSubsidy(Long id, Integer approvalStatus, String approvalMemo)
    {
        // 获取当前登录用户
        String approver = SecurityUtils.getUsername();
        
        // 获取补助记录
        StStudentSubsidyRecord record = stStudentSubsidyRecordMapper.selectStStudentSubsidyRecordById(id);
        if (record == null) {
            throw new ServiceException("补助记录不存在");
        }
        
        // 验证状态转换的合法性
        Integer currentStatus = record.getApprovalStatus();
        if (currentStatus == null) {
            currentStatus = 0; // 默认为待审批
        }
        
        // 如果审批通过（状态1），只能从待审批（状态0）转换
        if (approvalStatus == 1 && currentStatus != 0) {
            throw new ServiceException("只能审批待审批状态的记录");
        }
        // 如果审批驳回（状态2），只能从待审批（状态0）转换
        if (approvalStatus == 2 && currentStatus != 0) {
            throw new ServiceException("只能驳回待审批状态的记录");
        }
        // 如果退回（状态3），只能从已审批（状态1）转换
        if (approvalStatus == 3 && currentStatus != 1) {
            throw new ServiceException("只能退回已审批通过的记录");
        }
        
        Long budgetId = record.getBudgetId();
        if (budgetId == null) {
            throw new ServiceException("补助记录未关联预算");
        }
        
        String lockKey = "budget:" + budgetId;
        String lockValue = null;
        
        try {
            // 获取分布式锁
            lockValue = redisDistributedLock.tryLock(lockKey, 30);
            if (lockValue == null) {
                log.warn("approveSubsidy: 获取预算锁失败，预算ID={}，可能正在被其他操作使用", budgetId);
                throw new ServiceException("预算正在被其他操作使用，请稍后重试");
            }
            
            // 使用悲观锁查询预算
            com.ruoyi.system.domain.StSemesterBudget budget = stSemesterBudgetMapper
                .selectStSemesterBudgetByIdForUpdate(budgetId);
            if (budget == null) {
                throw new ServiceException("预算不存在");
            }
            
            // 检查是否有分配明细（如果使用了预算组合）
            List<com.ruoyi.system.domain.StBudgetAllocationDetail> allocationDetails = 
                stBudgetAllocationDetailMapper.selectBySubsidyRecordId(id);
            
            // 如果审批通过，验证锁定金额是否足够
            if (approvalStatus == 1) {
                if (allocationDetails != null && !allocationDetails.isEmpty()) {
                    // 使用了预算组合，验证所有分配明细的锁定金额总和
                    BigDecimal totalAllocatedAmount = allocationDetails.stream()
                        .map(com.ruoyi.system.domain.StBudgetAllocationDetail::getAllocatedAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    // 验证每个预算的锁定金额是否足够
                    for (com.ruoyi.system.domain.StBudgetAllocationDetail detail : allocationDetails) {
                        BigDecimal actualLockedAmount = stSemesterBudgetMapper.calculateActualLockedAmount(detail.getBudgetId());
                        if (actualLockedAmount == null) {
                            actualLockedAmount = BigDecimal.ZERO;
                        }
                        if (actualLockedAmount.compareTo(detail.getAllocatedAmount()) < 0) {
                            throw new ServiceException(String.format("预算锁定金额不足，无法审批通过。预算ID=%d，锁定金额：%.2f，需要金额：%.2f", 
                                detail.getBudgetId(), actualLockedAmount, detail.getAllocatedAmount()));
                        }
                    }
                    
                    // 验证分配总额是否等于补助金额
                    if (totalAllocatedAmount.compareTo(record.getSubsidyAmount()) != 0) {
                        throw new ServiceException(String.format("预算分配金额与补助金额不匹配。分配总额：%.2f，补助金额：%.2f", 
                            totalAllocatedAmount, record.getSubsidyAmount()));
                    }
                } else {
                    // 未使用预算组合，验证主预算的锁定金额
                    BigDecimal actualLockedAmount = stSemesterBudgetMapper.calculateActualLockedAmount(budgetId);
                    if (actualLockedAmount == null) {
                        actualLockedAmount = BigDecimal.ZERO;
                    }
                    if (actualLockedAmount.compareTo(record.getSubsidyAmount()) < 0) {
                        throw new ServiceException(String.format("预算锁定金额不足，无法审批通过。锁定金额：%.2f，需要金额：%.2f", 
                            actualLockedAmount, record.getSubsidyAmount()));
                    }
                }
            }
            
            // 如果退回，验证已使用金额是否足够（使用动态计算的已使用金额确保准确性）
            if (approvalStatus == 3) {
                if (allocationDetails != null && !allocationDetails.isEmpty()) {
                    // 使用了预算组合，验证所有分配明细的已使用金额总和
                    BigDecimal totalAllocatedAmount = allocationDetails.stream()
                        .map(com.ruoyi.system.domain.StBudgetAllocationDetail::getAllocatedAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    // 验证每个预算的已使用金额是否足够
                    for (com.ruoyi.system.domain.StBudgetAllocationDetail detail : allocationDetails) {
                        BigDecimal actualUsedAmount = stSemesterBudgetMapper.calculateActualUsedAmount(detail.getBudgetId());
                        if (actualUsedAmount == null) {
                            actualUsedAmount = BigDecimal.ZERO;
                        }
                        if (actualUsedAmount.compareTo(detail.getAllocatedAmount()) < 0) {
                            throw new ServiceException(String.format("预算已使用金额不足，无法退回。预算ID=%d，已使用金额：%.2f，需要退回金额：%.2f", 
                                detail.getBudgetId(), actualUsedAmount, detail.getAllocatedAmount()));
                        }
                    }
                    
                    // 验证分配总额是否等于补助金额
                    if (totalAllocatedAmount.compareTo(record.getSubsidyAmount()) != 0) {
                        throw new ServiceException(String.format("预算分配金额与补助金额不匹配。分配总额：%.2f，补助金额：%.2f", 
                            totalAllocatedAmount, record.getSubsidyAmount()));
                    }
                } else {
                    // 未使用预算组合，验证主预算的已使用金额
                    BigDecimal actualUsedAmount = stSemesterBudgetMapper.calculateActualUsedAmount(budgetId);
                    if (actualUsedAmount == null) {
                        actualUsedAmount = BigDecimal.ZERO;
                    }
                    if (actualUsedAmount.compareTo(record.getSubsidyAmount()) < 0) {
                        throw new ServiceException(String.format("预算已使用金额不足，无法退回。已使用金额：%.2f，退回金额：%.2f", 
                            actualUsedAmount, record.getSubsidyAmount()));
                    }
                }
            }
            
            // 如果审批驳回，释放所有分配明细的锁定金额
            if (approvalStatus == 2) {
                if (allocationDetails != null && !allocationDetails.isEmpty()) {
                    // 使用了预算组合，使用分配服务释放
                    budgetAllocationService.releaseAllocation(id);
                } else {
                    // 未使用预算组合，使用原有逻辑（兼容旧数据）
                    // 移除过于严格的验证，直接尝试释放，如果失败只记录日志
                    int released = stSemesterBudgetMapper.releaseBudgetAmount(budgetId, record.getSubsidyAmount());
                    if (released == 0) {
                        // 释放失败只记录警告，不阻止驳回操作（避免数据不一致导致无法驳回）
                        log.warn("释放预算锁定金额失败：可能锁定金额不足或数据不一致，记录ID={}，预算ID={}，金额={}", 
                            id, budgetId, record.getSubsidyAmount());
                    }
                }
            }
            // 如果审批通过，更新所有分配明细（锁定金额转为已使用金额）
            else if (approvalStatus == 1) {
                if (allocationDetails != null && !allocationDetails.isEmpty()) {
                    // 使用了预算组合，使用分配服务更新
                    budgetAllocationService.approveAllocation(id);
                } else {
                    // 未使用预算组合，使用原有逻辑（兼容旧数据）
                    int updated = stSemesterBudgetMapper.updateBudgetAmountOnApproval(budgetId, record.getSubsidyAmount());
                    if (updated == 0) {
                        throw new ServiceException("更新预算金额失败，可能锁定金额不足");
                    }
                }
            }
            // 如果退回（状态3），退回所有分配明细（已使用金额转回可用金额）
            else if (approvalStatus == 3) {
                if (allocationDetails != null && !allocationDetails.isEmpty()) {
                    // 使用了预算组合，使用分配服务退回
                    budgetAllocationService.returnAllocation(id);
                } else {
                    // 未使用预算组合，使用原有逻辑（兼容旧数据）
                    int returned = stSemesterBudgetMapper.returnBudgetAmount(budgetId, record.getSubsidyAmount());
                    if (returned == 0) {
                        throw new ServiceException("退回预算金额失败，可能已使用金额不足");
                    }
                }
            }
            
            // 最后更新补助记录状态（在所有预算操作成功之后）
            int result = stStudentSubsidyRecordMapper.approveSubsidy(id, approvalStatus, approver, approvalMemo);
            if (result == 0) {
                throw new ServiceException("审批失败");
            }
            
            return result;
        } finally {
            if (lockValue != null) {
                boolean released = redisDistributedLock.releaseLock(lockKey, lockValue);
                if (!released) {
                    log.warn("approveSubsidy: 释放预算锁失败，预算ID={}，锁可能已过期", budgetId);
                }
            }
        }
    }
    
    /**
     * 退回补助（已审批通过的记录退回）
     * 
     * @param id 补助记录ID
     * @param returnMemo 退回原因
     * @return 结果
     */
    @Override
    @Transactional
    public int returnSubsidy(Long id, String returnMemo)
    {
        // 获取当前登录用户
        String approver = SecurityUtils.getUsername();
        
        // 获取补助记录
        StStudentSubsidyRecord record = stStudentSubsidyRecordMapper.selectStStudentSubsidyRecordById(id);
        if (record == null) {
            throw new ServiceException("补助记录不存在");
        }
        
        // 验证状态：只能退回已审批通过的记录（状态1）
        if (record.getApprovalStatus() == null || record.getApprovalStatus() != 1) {
            throw new ServiceException("只能退回已审批通过的记录");
        }
        
        Long budgetId = record.getBudgetId();
        String lockKey = "budget:" + budgetId;
        String lockValue = null;
        
        try {
            // 1. 获取分布式锁（30秒超时）
            lockValue = redisDistributedLock.tryLock(lockKey, 30);
            if (lockValue == null) {
                log.warn("returnSubsidy: 获取预算锁失败，预算ID={}，可能正在被其他操作使用", budgetId);
                throw new ServiceException("预算正在被其他操作使用，请稍后重试");
            }
            
            log.info("returnSubsidy: 获取预算锁成功，预算ID={}，退回金额={}", budgetId, record.getSubsidyAmount());
            
            // 2. 使用悲观锁查询预算（双重保护：分布式锁 + 数据库锁）
            com.ruoyi.system.domain.StSemesterBudget budget = stSemesterBudgetMapper
                .selectStSemesterBudgetByIdForUpdate(budgetId);
            
            if (budget == null) {
                throw new ServiceException("预算不存在");
            }
            
            // 3. 检查是否有分配明细（如果使用了预算组合）
            List<com.ruoyi.system.domain.StBudgetAllocationDetail> allocationDetails = 
                stBudgetAllocationDetailMapper.selectBySubsidyRecordId(id);
            
            // 4. 验证已使用金额是否足够退回
            if (allocationDetails != null && !allocationDetails.isEmpty()) {
                // 使用了预算组合，验证所有相关预算的已使用金额总和
                BigDecimal totalAllocatedAmount = allocationDetails.stream()
                    .map(com.ruoyi.system.domain.StBudgetAllocationDetail::getAllocatedAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                // 验证每个预算的已使用金额是否足够
                for (com.ruoyi.system.domain.StBudgetAllocationDetail detail : allocationDetails) {
                    BigDecimal actualUsedAmount = stSemesterBudgetMapper.calculateActualUsedAmount(detail.getBudgetId());
                    if (actualUsedAmount == null) {
                        actualUsedAmount = BigDecimal.ZERO;
                    }
                    if (actualUsedAmount.compareTo(detail.getAllocatedAmount()) < 0) {
                        throw new ServiceException(String.format("预算已使用金额不足，无法退回。预算ID=%d，已使用金额：%.2f，需要退回金额：%.2f", 
                            detail.getBudgetId(), actualUsedAmount, detail.getAllocatedAmount()));
                    }
                }
                
                // 验证分配总额是否等于补助金额
                if (totalAllocatedAmount.compareTo(record.getSubsidyAmount()) != 0) {
                    throw new ServiceException(String.format("预算分配金额与补助金额不匹配。分配总额：%.2f，补助金额：%.2f", 
                        totalAllocatedAmount, record.getSubsidyAmount()));
                }
                
                // 退回预算金额（使用分配服务）
                budgetAllocationService.returnAllocation(id);
            } else {
                // 未使用预算组合，验证主预算的已使用金额
                BigDecimal actualUsedAmount = stSemesterBudgetMapper.calculateActualUsedAmount(budgetId);
                if (actualUsedAmount == null) {
                    actualUsedAmount = BigDecimal.ZERO;
                }
                if (actualUsedAmount.compareTo(record.getSubsidyAmount()) < 0) {
                    log.warn("returnSubsidy: 预算已使用金额不足，预算ID={}，已使用金额={}，退回金额={}", 
                        budgetId, actualUsedAmount, record.getSubsidyAmount());
                    throw new ServiceException("预算已使用金额不足，无法退回");
                }
                
                // 退回预算金额（使用原有逻辑）
                log.info("returnSubsidy: 准备退回预算金额，预算ID={}，金额={}，当前已使用金额={}", 
                    budgetId, record.getSubsidyAmount(), actualUsedAmount);
                int returned = stSemesterBudgetMapper.returnBudgetAmount(budgetId, record.getSubsidyAmount());
                if (returned == 0) {
                    log.warn("returnSubsidy: 退回预算金额失败，预算ID={}，金额={}，可能已使用金额不足", 
                        budgetId, record.getSubsidyAmount());
                    throw new ServiceException("退回预算金额失败，可能已使用金额不足");
                }
                log.info("returnSubsidy: 退回预算金额成功，预算ID={}，金额={}，影响行数={}", 
                    budgetId, record.getSubsidyAmount(), returned);
            }
            
            // 6. 更新补助记录状态为已退回（状态3）
            int result = stStudentSubsidyRecordMapper.approveSubsidy(id, 3, approver, returnMemo);
            if (result == 0) {
                throw new ServiceException("更新补助记录状态失败");
            }
            
            return result;
            
        } finally {
            // 6. 释放分布式锁
            if (lockValue != null) {
                boolean released = redisDistributedLock.releaseLock(lockKey, lockValue);
                if (released) {
                    log.info("returnSubsidy: 释放预算锁成功，预算ID={}", budgetId);
                } else {
                    log.warn("returnSubsidy: 释放预算锁失败，预算ID={}，锁可能已过期", budgetId);
                }
            }
        }
    }
    
    /**
     * 批量发放补助（批量录入方式）
     * 
     * @param recordIds 补助记录ID列表
     * @param paymentDate 发放日期
     * @param paymentPerson 发放人
     * @param memo 备注
     * @return 成功发放的数量
     */
    @Override
    @Transactional
    public int batchPayment(List<Long> recordIds, Date paymentDate, String paymentPerson, String memo) {
        if (recordIds == null || recordIds.isEmpty()) {
            throw new ServiceException("补助记录ID列表不能为空");
        }
        
        if (paymentDate == null) {
            throw new ServiceException("发放日期不能为空");
        }
        
        if (StringUtils.isEmpty(paymentPerson)) {
            // 如果没有指定发放人，使用当前登录用户
            paymentPerson = SecurityUtils.getUsername();
        }
        
        // 验证记录是否存在且状态为待发放
        for (Long recordId : recordIds) {
            StStudentSubsidyRecord record = stStudentSubsidyRecordMapper.selectStStudentSubsidyRecordById(recordId);
            if (record == null) {
                throw new ServiceException("补助记录不存在，ID: " + recordId);
            }
            if (record.getApprovalStatus() == null || record.getApprovalStatus() != 1) {
                throw new ServiceException("补助记录未审批通过，无法发放，ID: " + recordId);
            }
            if (record.getPaymentStatus() != null && record.getPaymentStatus() != 0) {
                throw new ServiceException("补助记录已发放或发放失败，无法重复发放，ID: " + recordId);
            }
        }
        
        // 批量更新发放状态
        return stStudentSubsidyRecordMapper.batchPayment(recordIds, paymentDate, paymentPerson, memo);
    }
    
    /**
     * 批量发放补助（支持直接录入金额）
     * 
     * @param request 批量发放请求
     * @return 批量发放结果
     */
    @Override
    @Transactional
    public BatchPaymentResultVO batchPaymentWithStudents(BatchPaymentRequest request) {
        BatchPaymentResultVO result = new BatchPaymentResultVO();
        List<BatchPaymentErrorVO> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        int totalCount = 0;
        
        if (request == null) {
            throw new ServiceException("请求参数不能为空");
        }
        
        if (request.getPaymentDate() == null) {
            throw new ServiceException("发放日期不能为空");
        }
        
        String paymentPerson = request.getPaymentPerson();
        if (StringUtils.isEmpty(paymentPerson)) {
            paymentPerson = SecurityUtils.getUsername();
        }
        
        // 处理有待发放记录的学生（通过recordIds）
        if (request.getRecordIds() != null && !request.getRecordIds().isEmpty()) {
            try {
                int count = batchPayment(request.getRecordIds(), request.getPaymentDate(), paymentPerson, request.getMemo());
                successCount += count;
                totalCount += request.getRecordIds().size();
                if (count < request.getRecordIds().size()) {
                    failureCount += (request.getRecordIds().size() - count);
                    // 这里可以添加详细的错误信息
                }
            } catch (Exception e) {
                failureCount += request.getRecordIds().size();
                totalCount += request.getRecordIds().size();
                errors.add(new BatchPaymentErrorVO("批量记录", null, e.getMessage()));
            }
        }
        
        // 处理学生支付信息列表
        if (request.getStudentPayments() != null && !request.getStudentPayments().isEmpty()) {
            totalCount += request.getStudentPayments().size();
            List<Long> recordIdsToPay = new ArrayList<>();
            
            for (BatchPaymentRequest.StudentPaymentInfo studentPayment : request.getStudentPayments()) {
                try {
                    // 1. 验证必填字段
                    if (studentPayment.getStudentId() == null && StringUtils.isEmpty(studentPayment.getStudentNo())) {
                        failureCount++;
                        errors.add(new BatchPaymentErrorVO(
                            studentPayment.getStudentName(),
                            studentPayment.getStudentNo(),
                            "学生ID或学籍号不能为空"
                        ));
                        continue;
                    }
                    
                    // 2. 验证金额
                    if (studentPayment.getPaymentAmount() == null || studentPayment.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
                        failureCount++;
                        errors.add(new BatchPaymentErrorVO(
                            studentPayment.getStudentName(),
                            studentPayment.getStudentNo(),
                            "发放金额必须大于0"
                        ));
                        continue;
                    }
                    
                    // 3. 查询该学生的待发放记录（根据学生ID和金额匹配）
                    Long pendingRecordId = null;
                    if (studentPayment.getStudentId() != null) {
                        List<StStudentSubsidyRecord> pendingRecords = stStudentSubsidyRecordMapper.selectPendingPaymentByStudentAndAmount(
                            studentPayment.getStudentId(),
                            studentPayment.getPaymentAmount()
                        );
                        if (pendingRecords != null && !pendingRecords.isEmpty()) {
                            // 取第一条匹配的记录
                            pendingRecordId = pendingRecords.get(0).getId();
                        }
                    }
                    
                    // 4. 如果有待发放记录，直接使用（这些记录已经审批通过，可以直接发放）
                    if (pendingRecordId != null) {
                        recordIdsToPay.add(pendingRecordId);
                    } else {
                        // 5. 如果没有待发放记录，需要创建补助记录（状态为待审核）
                        try {
                            // 验证经济分类
                            if (StringUtils.isEmpty(studentPayment.getEconomyCategory())) {
                                failureCount++;
                                errors.add(new BatchPaymentErrorVO(
                                    studentPayment.getStudentName(),
                                    studentPayment.getStudentNo(),
                                    "必须选择经济分类（助学金/免学杂费）"
                                ));
                                continue;
                            }
                            
                            // 验证预算（支持多选）
                            List<Long> budgetIds = request.getBudgetIds();
                            if (budgetIds == null || budgetIds.isEmpty()) {
                                // 向后兼容：如果没有设置budgetIds，检查budgetId
                                if (request.getBudgetId() == null) {
                                    failureCount++;
                                    errors.add(new BatchPaymentErrorVO(
                                        studentPayment.getStudentName(),
                                        studentPayment.getStudentNo(),
                                        "必须选择预算"
                                    ));
                                    continue;
                                }
                            }
                            
                            Long recordId = createAndApproveSubsidyRecord(studentPayment, request);
                            if (recordId != null) {
                                // 创建记录成功，计入成功数（记录状态为待审核，需要先审批才能发放）
                                successCount++;
                                // 注意：新创建的记录是待审核状态，不能立即发放，所以不添加到 recordIdsToPay
                            } else {
                                failureCount++;
                                errors.add(new BatchPaymentErrorVO(
                                    studentPayment.getStudentName(),
                                    studentPayment.getStudentNo(),
                                    "创建补助记录失败"
                                ));
                            }
                        } catch (Exception e) {
                            failureCount++;
                            errors.add(new BatchPaymentErrorVO(
                                studentPayment.getStudentName(),
                                studentPayment.getStudentNo(),
                                "创建补助记录失败：" + e.getMessage()
                            ));
                        }
                    }
                } catch (Exception e) {
                    failureCount++;
                    errors.add(new BatchPaymentErrorVO(
                        studentPayment.getStudentName(),
                        studentPayment.getStudentNo(),
                        e.getMessage()
                    ));
                }
            }
            
            // 批量发放
            if (!recordIdsToPay.isEmpty()) {
                try {
                    int count = this.batchPayment(recordIdsToPay, request.getPaymentDate(), paymentPerson, request.getMemo());
                    successCount += count;
                    if (count < recordIdsToPay.size()) {
                        failureCount += (recordIdsToPay.size() - count);
                    }
                } catch (Exception e) {
                    failureCount += recordIdsToPay.size();
                    errors.add(new BatchPaymentErrorVO("批量发放", null, "批量发放失败：" + e.getMessage()));
                }
            }
        }
        
        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setTotalCount(totalCount);
        result.setErrors(errors.isEmpty() ? null : errors);
        
        return result;
    }
    
    /**
     * 创建并审批补助记录（用于直接发放）
     * 关联预算和经济分类
     */
    private Long createAndApproveSubsidyRecord(BatchPaymentRequest.StudentPaymentInfo studentPayment, BatchPaymentRequest request) {
        // 根据学生ID或学籍号查找学生基础信息
        StStudentsBase studentBase = null;
        if (studentPayment.getStudentId() != null) {
            studentBase = stStudentsBaseMapper.selectStStudentsBaseById(studentPayment.getStudentId());
        } else if (StringUtils.isNotEmpty(studentPayment.getStudentNo())) {
            // 根据学籍号查找
            // 这里需要添加根据学籍号查询的方法，暂时简化处理
        }
        
        if (studentBase == null) {
            throw new ServiceException("找不到学生信息：" + studentPayment.getStudentName());
        }
        
        // 验证预算（支持多选）
        List<Long> selectedBudgetIds = request.getBudgetIds();
        if (selectedBudgetIds == null || selectedBudgetIds.isEmpty()) {
            // 向后兼容：如果没有设置budgetIds，使用budgetId
            if (request.getBudgetId() != null) {
                selectedBudgetIds = java.util.Arrays.asList(request.getBudgetId());
            } else {
                throw new ServiceException("必须选择预算");
            }
        }
        
        // 去重
        selectedBudgetIds = new java.util.ArrayList<>(new java.util.LinkedHashSet<>(selectedBudgetIds));
        
        Long primaryBudgetId = selectedBudgetIds.get(0); // 主预算ID（用于记录）
        BigDecimal requiredAmount = studentPayment.getPaymentAmount();
        
        // 获取当前学期ID（用于预算组合查询）
        Long currentSemesterId = request.getSourceSemesterId();
        if (currentSemesterId == null) {
            // 获取当前学期
            StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
            if (currentSemester != null) {
                currentSemesterId = currentSemester.getId();
            }
        }
        if (currentSemesterId == null) {
            throw new ServiceException("无法确定资金来源学期");
        }
        
        // 判断是单选还是多选，使用预算分配服务
        com.ruoyi.system.application.service.BudgetAllocationService.AllocationResult allocationResult = null;
        String lockKey = "budget:" + primaryBudgetId; // 默认值
        String lockValue = null;
        
        try {
            if (selectedBudgetIds.size() == 1) {
                // 单选模式：只从该预算支出，不使用组合逻辑
                Long singleBudgetId = selectedBudgetIds.get(0);
                lockKey = "budget:" + singleBudgetId;
                
                // 获取分布式锁（30秒超时）
                lockValue = redisDistributedLock.tryLock(lockKey, 30);
                if (lockValue == null) {
                    log.warn("createAndApproveSubsidyRecord: 获取预算锁失败，预算ID={}，可能正在被其他操作使用", singleBudgetId);
                    throw new ServiceException("预算正在被其他操作使用，请稍后重试");
                }
                
                // 使用悲观锁查询预算
                com.ruoyi.system.domain.StSemesterBudget budget = stSemesterBudgetMapper
                    .selectStSemesterBudgetByIdForUpdate(singleBudgetId);
                if (budget == null) {
                    throw new ServiceException("预算不存在");
                }
                
                // 验证经济分类是否匹配预算的经济分类
                if (!StringUtils.equals(budget.getEconomyCategory(), studentPayment.getEconomyCategory())) {
                    throw new ServiceException("学生经济分类与预算经济分类不匹配");
                }
                
                // 计算可用金额
                BigDecimal actualLockedAmount = stSemesterBudgetMapper.calculateActualLockedAmount(singleBudgetId);
                if (actualLockedAmount == null) {
                    actualLockedAmount = BigDecimal.ZERO;
                }
                BigDecimal actualUsedAmount = stSemesterBudgetMapper.calculateActualUsedAmount(singleBudgetId);
                if (actualUsedAmount == null) {
                    actualUsedAmount = BigDecimal.ZERO;
                }
                BigDecimal availableAmount = budget.getBudgetAmount()
                    .subtract(actualUsedAmount)
                    .subtract(actualLockedAmount);
                
                if (availableAmount.compareTo(requiredAmount) < 0) {
                    throw new ServiceException(String.format("预算余额不足，可用金额：%.2f，需要金额：%.2f", 
                        availableAmount, requiredAmount));
                }
                
                // 锁定预算金额
                int locked = stSemesterBudgetMapper.lockBudgetAmount(singleBudgetId, requiredAmount);
                if (locked == 0) {
                    throw new ServiceException("锁定预算金额失败，可能余额不足");
                }
                
                // 创建单个分配明细（不使用组合逻辑）
                List<com.ruoyi.system.domain.StBudgetAllocationDetail> singleDetail = new java.util.ArrayList<>();
                com.ruoyi.system.domain.StBudgetAllocationDetail detail = new com.ruoyi.system.domain.StBudgetAllocationDetail();
                detail.setBudgetId(singleBudgetId);
                detail.setAllocatedAmount(requiredAmount);
                detail.setCreatedAt(new java.util.Date());
                detail.setUpdatedAt(new java.util.Date());
                singleDetail.add(detail);
                
                // 创建 AllocationResult
                allocationResult = new com.ruoyi.system.application.service.BudgetAllocationService.AllocationResult(
                    singleBudgetId, singleDetail);
                log.info("createAndApproveSubsidyRecord: 单选模式，预算ID={}，金额={}", singleBudgetId, requiredAmount);
            } else {
                // 多选模式：使用预算组合逻辑，从用户选择的多个预算中智能分配
                // 使用第一个预算的锁（分配服务内部会锁定所有相关预算）
                lockKey = "budget:" + primaryBudgetId;
                lockValue = redisDistributedLock.tryLock(lockKey, 30);
                if (lockValue == null) {
                    log.warn("createAndApproveSubsidyRecord: 获取预算锁失败，预算ID={}，可能正在被其他操作使用", primaryBudgetId);
                    throw new ServiceException("预算正在被其他操作使用，请稍后重试");
                }
                
                // 使用预算分配服务分配预算
                allocationResult = budgetAllocationService.allocateFromSelectedBudgets(
                    selectedBudgetIds, requiredAmount, currentSemesterId);
                log.info("createAndApproveSubsidyRecord: 组合模式，主预算ID={}，选择预算数量={}，使用预算数量={}", 
                    primaryBudgetId, selectedBudgetIds.size(), allocationResult.getAllocationDetails().size());
            }
            
            Long sourceSemesterId = currentSemesterId;
            Long studentId = studentBase.getId();
            
            // 检查是否已有相同经济分类记录
            String economyCategory = studentPayment.getEconomyCategory();
            if (economyCategory != null && !economyCategory.isEmpty()) {
                // 5.1 检查是否已有未审核的相同经济分类记录
                int pendingCount = stStudentSubsidyRecordMapper.countPendingRecordsByStudentAndEconomyCategory(
                    studentId, economyCategory, sourceSemesterId
                );
                if (pendingCount > 0) {
                    String economyCategoryName = getEconomyCategoryName(economyCategory);
                    throw new ServiceException(String.format("%s已有%s记录待审核，不能重复录入", 
                        studentBase.getName(), economyCategoryName));
                }
                
                // 5.2 检查是否已有已审核通过的相同经济分类记录
                int approvedCount = stStudentSubsidyRecordMapper.countApprovedRecordsByStudentAndEconomyCategory(
                    studentId, economyCategory, sourceSemesterId
                );
                if (approvedCount > 0) {
                    String economyCategoryName = getEconomyCategoryName(economyCategory);
                    throw new ServiceException(String.format("%s已有%s记录已审核通过，不能重复录入。如需重新录入，请先在审批页面退回该记录", 
                        studentBase.getName(), economyCategoryName));
                }
            }
            
            // 创建补助记录，关联预算和经济分类
            StStudentSubsidyRecord record = new StStudentSubsidyRecord();
            record.setStudentId(studentId);
            // 兼容旧字段，保持非空以满足表结构约束
            record.setStudentSemesterRecordId(studentId);
            record.setBudgetId(primaryBudgetId); // 关联主预算ID
            record.setSourceSemesterId(sourceSemesterId);
            record.setYearSemesterId(sourceSemesterId);
            record.setPackageId(studentPayment.getPackageId()); // 关联套餐ID
            record.setSubsidyType("批量发放-" + getEconomyCategoryName(studentPayment.getEconomyCategory())); // 标记为批量发放
            record.setSubsidyAmount(studentPayment.getPaymentAmount());
            record.setApprovalStatus(0); // 待审批（参照个人录入）
            record.setPaymentStatus(0); // 待发放
            record.setMemo(request.getMemo());
            record.setSchoolingPlanId(studentBase.getSchoolingPlanId());
            record.setCreatedAt(new Date());
            record.setUpdatedAt(new Date());
            
            // 插入记录
            int result = stStudentSubsidyRecordMapper.insertStStudentSubsidyRecord(record);
            if (result > 0 && record.getId() != null) {
                // 保存分配明细（如果使用了组合预算）
                if (allocationResult != null && allocationResult.getAllocationDetails() != null 
                    && !allocationResult.getAllocationDetails().isEmpty()) {
                    for (com.ruoyi.system.domain.StBudgetAllocationDetail detail : allocationResult.getAllocationDetails()) {
                        detail.setSubsidyRecordId(record.getId());
                        stBudgetAllocationDetailMapper.insertStBudgetAllocationDetail(detail);
                    }
                    log.info("createAndApproveSubsidyRecord: 保存分配明细成功，补助记录ID={}，分配明细数量={}", 
                        record.getId(), allocationResult.getAllocationDetails().size());
                }
                
                // 预算金额已经锁定，待审批通过后再转为已使用金额
                // 审批通过时会调用 updateBudgetAmountOnApproval 或 approveAllocation
                return record.getId();
            } else {
                // 如果插入失败，释放已分配的预算
                if (allocationResult != null && allocationResult.getAllocationDetails() != null) {
                    for (com.ruoyi.system.domain.StBudgetAllocationDetail detail : allocationResult.getAllocationDetails()) {
                        stSemesterBudgetMapper.releaseBudgetAmount(detail.getBudgetId(), detail.getAllocatedAmount());
                    }
                }
                throw new ServiceException("创建补助记录失败");
            }
        } catch (Exception e) {
            // 如果创建失败，释放已分配的预算
            if (allocationResult != null && allocationResult.getAllocationDetails() != null) {
                log.error("createAndApproveSubsidyRecord: 创建补助记录失败，释放已分配的预算", e);
                try {
                    for (com.ruoyi.system.domain.StBudgetAllocationDetail detail : allocationResult.getAllocationDetails()) {
                        stSemesterBudgetMapper.releaseBudgetAmount(detail.getBudgetId(), detail.getAllocatedAmount());
                    }
                } catch (Exception releaseException) {
                    log.error("createAndApproveSubsidyRecord: 释放预算失败", releaseException);
                }
            }
            throw e;
        } finally {
            // 释放分布式锁
            if (lockValue != null) {
                boolean released = redisDistributedLock.releaseLock(lockKey, lockValue);
                if (released) {
                    log.info("createAndApproveSubsidyRecord: 释放预算锁成功，预算ID={}", primaryBudgetId);
                } else {
                    log.warn("createAndApproveSubsidyRecord: 释放预算锁失败，预算ID={}，锁可能已过期", primaryBudgetId);
                }
            }
        }
    }
    
    @SuppressWarnings("unused")
    /**
     * 查找或创建学期记录（参照个人录入的实现）
     * 新方案：不再依赖学期记录表，直接返回学生ID作为关联键
     */
    private Long findOrCreateSemesterRecord(Long studentId, String academicYear, String semester, Long sourceSemesterId) {
        if (studentId == null) {
            throw new ServiceException("学生ID不能为空");
        }
        
        if (sourceSemesterId == null) {
            throw new ServiceException("资金来源学期ID不能为空，无法确定当前学期");
        }
        
        return studentId;
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
     * Excel导入批量发放
     * 
     * @param importList 导入数据列表
     * @return 导入结果
     */
    @Override
    @Transactional
    public PaymentImportResultVO importPaymentFromExcel(List<PaymentImportDTO> importList) {
        log.info("开始Excel导入批量发放，导入数据条数：{}", importList != null ? importList.size() : 0);
        
        PaymentImportResultVO result = new PaymentImportResultVO();
        List<PaymentImportErrorVO> errorList = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        
        if (importList == null || importList.isEmpty()) {
            log.error("导入数据为空");
            throw new ServiceException("导入数据不能为空");
        }
        
        // 获取当前学期
        StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
        if (currentSemester == null) {
            log.error("未设置当前学期，无法导入发放数据");
            throw new ServiceException("未设置当前学期，无法导入发放数据");
        }
        log.info("当前学期：{}，学期ID：{}", currentSemester.getSchoolYear() + "-" + currentSemester.getSemester(), currentSemester.getId());
        
        // 遍历导入数据
        for (int i = 0; i < importList.size(); i++) {
            PaymentImportDTO dto = importList.get(i);
            int rowNum = i + 2; // Excel行号（从第2行开始，第1行是表头）
            
            log.info("处理第{}行数据：姓名={}，学籍号={}，金额={}，经济分类={}，预算ID={}", 
                rowNum, dto.getName(), dto.getStudentNo(), dto.getSubsidyAmount(), dto.getEconomyCategory(), dto.getBudgetId());
            
            try {
                // 1. 数据校验
                String errorMsg = validatePaymentImport(dto);
                if (StringUtils.isNotEmpty(errorMsg)) {
                    log.warn("第{}行数据校验失败：{}", rowNum, errorMsg);
                    errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), errorMsg));
                    failureCount++;
                    continue;
                }
                
                // 2. 根据学籍号查找学生
                StStudentsBase student = stStudentsBaseMapper.selectStStudentsBaseByStudentNo(dto.getStudentNo());
                if (student == null) {
                    log.warn("第{}行：未找到学籍号为{}的学生", rowNum, dto.getStudentNo());
                    errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), 
                        "未找到学籍号为" + dto.getStudentNo() + "的学生"));
                    failureCount++;
                    continue;
                }
                log.debug("第{}行：找到学生，学生ID={}，姓名={}，学制ID={}", rowNum, student.getId(), student.getName(), student.getSchoolingPlanId());
                
                // 3. 验证姓名是否匹配（如果提供了姓名）
                if (StringUtils.isNotEmpty(dto.getName()) && !dto.getName().equals(student.getName())) {
                    errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), 
                        "姓名不匹配，学籍号对应的学生姓名为：" + student.getName()));
                    failureCount++;
                    continue;
                }
                
                // 4. 验证身份证号是否匹配（如果提供了身份证号）
                if (StringUtils.isNotEmpty(dto.getIdCardNo()) && StringUtils.isNotEmpty(student.getIdCardNo()) 
                    && !dto.getIdCardNo().equals(student.getIdCardNo())) {
                    errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), 
                        "身份证号不匹配"));
                    failureCount++;
                    continue;
                }
                
                // 5. 验证经济分类
                if (StringUtils.isEmpty(dto.getEconomyCategory())) {
                    errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), 
                        "经济分类不能为空（1=助学金，2=免学杂费，3=免学费）"));
                    failureCount++;
                    continue;
                }
                
                // 6. 根据学制获取功能分类（用于日志记录，不进行验证，与其他录入方式保持一致）
                String functionCategory = getFunctionCategoryBySchoolingPlanId(student.getSchoolingPlanId());
                log.debug("第{}行：学生功能分类={}（学制ID={}）", rowNum, functionCategory, student.getSchoolingPlanId());
                
                // 7. 验证预算ID
                if (dto.getBudgetId() == null) {
                    log.warn("第{}行：预算ID为空", rowNum);
                    errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), 
                        "预算ID不能为空"));
                    failureCount++;
                    continue;
                }
                
                // 8. 根据预算ID查询预算
                com.ruoyi.system.domain.StSemesterBudget matchedBudget = stSemesterBudgetMapper.selectStSemesterBudgetById(dto.getBudgetId());
                
                if (matchedBudget == null) {
                    log.warn("第{}行：预算不存在，预算ID={}，当前学期ID={}", rowNum, dto.getBudgetId(), currentSemester.getId());
                    // 提供更详细的错误信息，帮助用户查找正确的预算ID
                    String budgetErrorMsg = String.format("预算不存在（预算ID：%d）。提示：请在预算管理页面查看当前学期（%s）的可用预算ID，或使用批量发放功能查看预算列表中的ID", 
                        dto.getBudgetId(), currentSemester.getSchoolYear() + "-" + currentSemester.getSemester());
                    errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), budgetErrorMsg));
                    failureCount++;
                    continue;
                }
                log.debug("第{}行：找到预算，预算ID={}，预算项目名称={}，经济分类={}，功能分类={}，状态={}", 
                    rowNum, matchedBudget.getId(), matchedBudget.getBudgetProjectName(), 
                    matchedBudget.getEconomyCategory(), matchedBudget.getFunctionCategory(), matchedBudget.getStatus());
                
                // 9. 验证预算状态（必须是启用状态）
                if (matchedBudget.getStatus() == null || matchedBudget.getStatus() != 1) {
                    log.warn("第{}行：预算状态异常，预算ID={}，状态={}", rowNum, dto.getBudgetId(), matchedBudget.getStatus());
                    errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), 
                        "预算状态异常，无法使用（预算ID：" + dto.getBudgetId() + "）"));
                    failureCount++;
                    continue;
                }
                
                // 10. 验证预算的经济分类是否匹配
                if (!StringUtils.equals(matchedBudget.getEconomyCategory(), dto.getEconomyCategory())) {
                    log.warn("第{}行：预算经济分类不匹配，预算经济分类={}，导入数据经济分类={}", 
                        rowNum, matchedBudget.getEconomyCategory(), dto.getEconomyCategory());
                    errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), 
                        "预算经济分类不匹配（预算经济分类：" + getEconomyCategoryName(matchedBudget.getEconomyCategory()) + 
                        "，导入数据经济分类：" + getEconomyCategoryName(dto.getEconomyCategory()) + "）"));
                    failureCount++;
                    continue;
                }
                
                // 11. 验证预算可用金额（移除功能分类验证，与其他录入方式保持一致）
                BigDecimal availableAmount = matchedBudget.getBudgetAmount()
                    .subtract(matchedBudget.getUsedAmount() != null ? matchedBudget.getUsedAmount() : BigDecimal.ZERO)
                    .subtract(matchedBudget.getLockedAmount() != null ? matchedBudget.getLockedAmount() : BigDecimal.ZERO);
                
                log.debug("第{}行：预算金额信息，总金额={}，已用金额={}，锁定金额={}，可用金额={}，需要金额={}", 
                    rowNum, matchedBudget.getBudgetAmount(), matchedBudget.getUsedAmount(), 
                    matchedBudget.getLockedAmount(), availableAmount, dto.getSubsidyAmount());
                
                if (availableAmount.compareTo(dto.getSubsidyAmount()) < 0) {
                    log.warn("第{}行：预算余额不足，可用金额={}，需要金额={}", rowNum, availableAmount, dto.getSubsidyAmount());
                    errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), 
                        "预算余额不足（可用金额：" + availableAmount + "，需要金额：" + dto.getSubsidyAmount() + "）"));
                    failureCount++;
                    continue;
                }
                
                // 13. 查找该学生的待发放补助记录（按学生ID、金额和经济分类匹配）
                List<StStudentSubsidyRecord> records = stStudentSubsidyRecordMapper.selectPendingPaymentByStudentAndAmount(
                    student.getId(), dto.getSubsidyAmount());
                
                // 过滤出经济分类匹配的记录
                StStudentSubsidyRecord matchedRecord = null;
                if (records != null && !records.isEmpty()) {
                    for (StStudentSubsidyRecord record : records) {
                        // 检查记录的经济分类是否匹配（通过预算关联查询）
                        if (record.getBudgetId() != null && record.getBudgetId().equals(matchedBudget.getId())) {
                            matchedRecord = record;
                            break;
                        }
                    }
                }
                
                // 14. 如果有匹配的待发放记录，直接发放
                if (matchedRecord != null) {
                    log.info("第{}行：找到待发放记录，记录ID={}，直接发放", rowNum, matchedRecord.getId());
                    List<Long> recordIds = new ArrayList<>();
                    recordIds.add(matchedRecord.getId());
                    int updateCount = stStudentSubsidyRecordMapper.batchPayment(recordIds, dto.getPaymentDate(), dto.getPaymentPerson(), null);
                    
                    if (updateCount > 0) {
                        log.info("第{}行：发放成功，记录ID={}", rowNum, matchedRecord.getId());
                        successCount++;
                    } else {
                        log.warn("第{}行：发放失败，记录可能已被其他操作修改，记录ID={}", rowNum, matchedRecord.getId());
                        errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), 
                            "发放失败，记录可能已被其他操作修改"));
                        failureCount++;
                    }
                } else {
                    // 15. 如果没有待发放记录，创建新记录（参照批量发放逻辑）
                    log.info("第{}行：未找到待发放记录，创建新记录", rowNum);
                    try {
                        // 创建批量发放请求对象
                        BatchPaymentRequest.StudentPaymentInfo studentPayment = new BatchPaymentRequest.StudentPaymentInfo();
                        studentPayment.setStudentId(student.getId());
                        studentPayment.setStudentName(student.getName());
                        studentPayment.setStudentNo(student.getStudentNo());
                        studentPayment.setPaymentAmount(dto.getSubsidyAmount());
                        studentPayment.setEconomyCategory(dto.getEconomyCategory());
                        studentPayment.setSchoolingPlanId(student.getSchoolingPlanId());
                        studentPayment.setAcademicYear(currentSemester.getSchoolYear());
                        studentPayment.setSemester(String.valueOf(currentSemester.getSemester()));
                        
                        BatchPaymentRequest batchRequest = new BatchPaymentRequest();
                        batchRequest.setBudgetId(matchedBudget.getId());
                        batchRequest.setSourceSemesterId(currentSemester.getId());
                        batchRequest.setMemo("Excel导入");
                        
                        Long recordId = createAndApproveSubsidyRecord(studentPayment, batchRequest);
                        if (recordId != null) {
                            log.info("第{}行：创建补助记录成功，记录ID={}，状态为待审核", rowNum, recordId);
                            // 创建成功，状态是待审核，计入成功数（与批量发放逻辑一致）
                            successCount++;
                        } else {
                            log.error("第{}行：创建补助记录失败，返回ID为null", rowNum);
                            errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), 
                                "创建补助记录失败"));
                            failureCount++;
                        }
                    } catch (Exception e) {
                        log.error("第{}行：创建补助记录异常", rowNum, e);
                        errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), 
                            "创建补助记录失败：" + e.getMessage()));
                        failureCount++;
                    }
                }
                
            } catch (Exception e) {
                log.error("第{}行：处理异常", rowNum, e);
                errorList.add(new PaymentImportErrorVO(rowNum, dto.getName(), dto.getStudentNo(), 
                    "处理异常：" + e.getMessage()));
                failureCount++;
            }
        }
        
        result.setTotalCount(importList.size());
        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setErrorList(errorList);
        
        log.info("Excel导入完成，总计：{}条，成功：{}条，失败：{}条", importList.size(), successCount, failureCount);
        if (!errorList.isEmpty()) {
            log.warn("导入错误详情：");
            for (PaymentImportErrorVO error : errorList) {
                log.warn("  第{}行：{}（学籍号：{}） - {}", 
                    error.getRowNum(), error.getName(), error.getStudentNo(), error.getErrorMsg());
            }
        }
        
        return result;
    }
    
    /**
     * 校验导入数据
     */
    private String validatePaymentImport(PaymentImportDTO dto) {
        if (StringUtils.isEmpty(dto.getName())) {
            return "姓名不能为空";
        }
        if (StringUtils.isEmpty(dto.getStudentNo())) {
            return "学籍号不能为空";
        }
        if (dto.getSubsidyAmount() == null || dto.getSubsidyAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return "资助金额必须大于0";
        }
        if (dto.getPaymentDate() == null) {
            return "资助日期不能为空";
        }
        if (StringUtils.isEmpty(dto.getPaymentPerson())) {
            return "发放人不能为空";
        }
        if (StringUtils.isEmpty(dto.getEconomyCategory())) {
            return "经济分类不能为空（1=助学金，2=免学杂费，3=免学费）";
        }
        if (dto.getBudgetId() == null) {
            return "预算ID不能为空";
        }
        return null;
    }
    
    /**
     * 根据学制ID获取功能分类
     */
    private String getFunctionCategoryBySchoolingPlanId(Long schoolingPlanId) {
        if (schoolingPlanId == null) {
            return null;
        }
        // 1=小学，2=初中，3=高中
        if (schoolingPlanId == 1L) {
            return "1"; // 小学
        } else if (schoolingPlanId == 2L) {
            return "2"; // 初中
        } else if (schoolingPlanId == 3L) {
            return "3"; // 高中
        }
        return null;
    }
}