package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.StSemesterBudget;
import com.ruoyi.system.domain.StSubsidyQuotaDetail;
import com.ruoyi.system.domain.vo.BudgetStatisticVO;
import com.ruoyi.system.mapper.StSemesterBudgetMapper;
import com.ruoyi.system.mapper.StSubsidyQuotaDetailMapper;
import com.ruoyi.system.mapper.StSubsidyQuotaMapper;
import com.ruoyi.system.service.IStSemesterBudgetService;

/**
 * 学期预算支出Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-22
 */
@Service
public class StSemesterBudgetServiceImpl implements IStSemesterBudgetService
{
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Autowired
    private StSemesterBudgetMapper stSemesterBudgetMapper;
    
    @Autowired
    private StSubsidyQuotaDetailMapper stSubsidyQuotaDetailMapper;
    
    @Autowired
    private StSubsidyQuotaMapper stSubsidyQuotaMapper;

    /**
     * 查询学期预算支出
     * 
     * @param id 学期预算支出主键
     * @return 学期预算支出
     */
    @Override
    public StSemesterBudget selectStSemesterBudgetById(Long id)
    {
        StSemesterBudget budget = stSemesterBudgetMapper.selectStSemesterBudgetById(id);
        if (budget != null) {
            // 使用动态计算的锁定金额（更准确，包含所有未审批记录）
            java.math.BigDecimal actualLocked = stSemesterBudgetMapper.calculateActualLockedAmount(id);
            budget.setLockedAmount(actualLocked);
            // 重新计算可用金额
            BigDecimal available = budget.getBudgetAmount()
                .subtract(budget.getUsedAmount() != null ? budget.getUsedAmount() : BigDecimal.ZERO)
                .subtract(actualLocked != null ? actualLocked : BigDecimal.ZERO);
            budget.setAvailableAmount(available);
            // 使用工具类确保学期标签的一致性（即使SQL已计算，也通过工具类校验和设置）
            String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(budget.getSemester());
            budget.setSemesterLabel(semesterLabel);
        }
        return budget;
    }

    /**
     * 查询学期预算支出列表
     * 
     * @param stSemesterBudget 学期预算支出
     * @return 学期预算支出
     */
    @Override
    public List<StSemesterBudget> selectStSemesterBudgetList(StSemesterBudget stSemesterBudget)
    {
        List<StSemesterBudget> list = stSemesterBudgetMapper.selectStSemesterBudgetList(stSemesterBudget);
        // 为每个预算计算实际的锁定金额（从未审批记录中汇总）
        for (StSemesterBudget budget : list) {
            java.math.BigDecimal actualLocked = stSemesterBudgetMapper.calculateActualLockedAmount(budget.getId());
            budget.setLockedAmount(actualLocked);
            // 重新计算可用金额
            BigDecimal available = budget.getBudgetAmount()
                .subtract(budget.getUsedAmount() != null ? budget.getUsedAmount() : BigDecimal.ZERO)
                .subtract(actualLocked != null ? actualLocked : BigDecimal.ZERO);
            budget.setAvailableAmount(available);
            // 计算结转标识：优先使用isCarryover字段，其次使用quotaSourceType（兼容旧数据）
            if ("1".equals(budget.getIsCarryover())) {
                // 明确标记为结转资金
                budget.setCarryOverFlag(true);
            } else if (budget.getQuotaSourceType() != null && 
                       com.ruoyi.system.application.query.QuotaSourceUtils.isCarryOver(budget.getQuotaSourceType())) {
                // 兼容旧数据：根据quotaSourceType判断
                budget.setCarryOverFlag(true);
            } else {
                // 非结转资金
                budget.setCarryOverFlag(false);
            }
            // 使用工具类确保学期标签的一致性（即使SQL已计算，也通过工具类校验和设置）
            String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(budget.getSemester());
            budget.setSemesterLabel(semesterLabel);
        }
        return list;
    }

    /**
     * 新增学期预算支出
     * 
     * @param stSemesterBudget 学期预算支出
     * @return 结果
     */
    @Override
    @Transactional
    public int insertStSemesterBudget(StSemesterBudget stSemesterBudget)
    {
        if (stSemesterBudget.getYearSemesterId() == null) {
            throw new ServiceException("请先选择学年学期");
        }
        BigDecimal amount = safe(stSemesterBudget.getBudgetAmount());
        if (amount.compareTo(ZERO) <= 0) {
            throw new ServiceException("预算总额必须大于0");
        }
        stSemesterBudget.setBudgetAmount(amount);
        stSemesterBudget.setUsedAmount(safe(stSemesterBudget.getUsedAmount()));
        stSemesterBudget.setLockedAmount(safe(stSemesterBudget.getLockedAmount()));
        if (stSemesterBudget.getStatus() == null) {
            stSemesterBudget.setStatus(1);
        }
        validateUsageConstraint(stSemesterBudget, amount, ZERO);
        validateQuotaCapacity(stSemesterBudget, amount, ZERO);
        int result = stSemesterBudgetMapper.insertStSemesterBudget(stSemesterBudget);
        adjustQuotaAllocation(
            stSemesterBudget.getQuotaId(),
            stSemesterBudget.getQuotaDetailId(),
            amount
        );
        return result;
    }

    /**
     * 修改学期预算支出
     * 
     * @param stSemesterBudget 学期预算支出
     * @return 结果
     */
    @Override
    @Transactional
    public int updateStSemesterBudget(StSemesterBudget stSemesterBudget)
    {
        if (stSemesterBudget.getId() == null) {
            throw new ServiceException("缺少预算ID，无法修改");
        }
        StSemesterBudget originalBudget = stSemesterBudgetMapper.selectStSemesterBudgetById(stSemesterBudget.getId());
        if (originalBudget == null) {
            throw new ServiceException("预算不存在或已删除");
        }
        BigDecimal newAmount = safe(stSemesterBudget.getBudgetAmount());
        BigDecimal originalAmount = safe(originalBudget.getBudgetAmount());
        stSemesterBudget.setBudgetAmount(newAmount);
        stSemesterBudget.setUsedAmount(originalBudget.getUsedAmount());
        stSemesterBudget.setLockedAmount(originalBudget.getLockedAmount());

        Long newQuotaId = stSemesterBudget.getQuotaId() != null ? stSemesterBudget.getQuotaId() : originalBudget.getQuotaId();
        Long newDetailId = stSemesterBudget.getQuotaDetailId() != null ? stSemesterBudget.getQuotaDetailId() : originalBudget.getQuotaDetailId();
        stSemesterBudget.setQuotaId(newQuotaId);
        stSemesterBudget.setQuotaDetailId(newDetailId);

        BigDecimal restoreAmount = Objects.equals(newDetailId, originalBudget.getQuotaDetailId()) ? originalAmount : ZERO;
        validateUsageConstraint(stSemesterBudget, newAmount, originalAmount);
        validateQuotaCapacity(stSemesterBudget, newAmount, restoreAmount);

        int result = stSemesterBudgetMapper.updateStSemesterBudget(stSemesterBudget);

        if (Objects.equals(newDetailId, originalBudget.getQuotaDetailId()) && Objects.equals(newQuotaId, originalBudget.getQuotaId())) {
            adjustQuotaAllocation(newQuotaId, newDetailId, newAmount.subtract(originalAmount));
        } else {
            adjustQuotaAllocation(originalBudget.getQuotaId(), originalBudget.getQuotaDetailId(), originalAmount.negate());
            adjustQuotaAllocation(newQuotaId, newDetailId, newAmount);
        }
        return result;
    }

    /**
     * 批量删除学期预算支出
     * 
     * @param ids 需要删除的学期预算支出主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStSemesterBudgetByIds(Long[] ids)
    {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        // 先验证所有预算是否可以删除（在事务中统一检查）
        for (Long id : ids) {
            StSemesterBudget budget = stSemesterBudgetMapper.selectStSemesterBudgetById(id);
            if (budget == null) {
                continue;
            }
            // 动态计算实际的锁定金额
            java.math.BigDecimal actualLocked = stSemesterBudgetMapper.calculateActualLockedAmount(id);
            if (safe(budget.getUsedAmount()).compareTo(ZERO) > 0 || 
                (actualLocked != null && actualLocked.compareTo(ZERO) > 0)) {
                throw new ServiceException(String.format("预算[ID:%d]已被使用或锁定，无法删除", id));
            }
        }
        // 所有验证通过后，批量删除
        int count = 0;
        for (Long id : ids) {
            try {
                int result = deleteStSemesterBudgetById(id);
                count += result;
            } catch (ServiceException e) {
                // 如果删除失败，抛出异常（事务会回滚）
                throw new ServiceException(String.format("删除预算[ID:%d]失败：%s", id, e.getMessage()));
            }
        }
        return count;
    }

    /**
     * 删除学期预算支出信息
     * 
     * @param id 学期预算支出主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStSemesterBudgetById(Long id)
    {
        if (id == null) {
            return 0;
        }
        StSemesterBudget originalBudget = stSemesterBudgetMapper.selectStSemesterBudgetById(id);
        if (originalBudget == null) {
            return 0;
        }
        // 动态计算实际的锁定金额（从未审批记录中汇总）
        java.math.BigDecimal actualLocked = stSemesterBudgetMapper.calculateActualLockedAmount(id);
        if (safe(originalBudget.getUsedAmount()).compareTo(ZERO) > 0 || 
            (actualLocked != null && actualLocked.compareTo(ZERO) > 0)) {
            throw new ServiceException("预算已被使用或锁定，无法删除");
        }
        int result = stSemesterBudgetMapper.deleteStSemesterBudgetById(id);
        adjustQuotaAllocation(
            originalBudget.getQuotaId(),
            originalBudget.getQuotaDetailId(),
            safe(originalBudget.getBudgetAmount()).negate()
        );
        return result;
    }
    
    /**
     * 校准预算数据一致性
     * 定期检查并修复预算表中的已使用金额与实际补助明细总额不一致的问题
     */
    @Override
    public void calibrateBudgetConsistency() {
        // TODO: 预留扩展点，后续结合补助明细实现
    }
    
    /**
     * 根据预算ID查询经济分类
     * 
     * @param budgetId 预算ID
     * @return 经济分类
     */
    @Override
    public String selectEconomyCategoryByBudgetId(Long budgetId) {
        return stSemesterBudgetMapper.selectEconomyCategoryByBudgetId(budgetId);
    }
    
    /**
     * 查询所有预算项目名称（去重）
     * 
     * @param yearSemesterId 学年学期ID
     * @return 预算项目名称列表
     */
    @Override
    public List<String> selectAllBudgetProjectNames(Long yearSemesterId) {
        return stSubsidyQuotaMapper.selectAllBudgetProjectNames(yearSemesterId);
    }
    
    /**
     * 查询所有预算项目名称（去重）
     * 
     * @return 预算项目名称列表
     */
    @Override
    public List<String> selectAllBudgetProjectNames() {
        return stSubsidyQuotaMapper.selectAllBudgetProjectNamesWithoutFilter();
    }

    @Override
    public int updateBudgetStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new ServiceException("参数不完整");
        }
        StSemesterBudget budget = stSemesterBudgetMapper.selectStSemesterBudgetById(id);
        if (budget == null) {
            throw new ServiceException("预算不存在");
        }
        // 验证：已使用资金的预算不允许修改状态
        BigDecimal usedAmount = safe(budget.getUsedAmount());
        if (usedAmount.compareTo(ZERO) > 0) {
            throw new ServiceException("该预算已使用资金，不允许修改状态");
        }
        StSemesterBudget update = new StSemesterBudget();
        update.setId(id);
        update.setStatus(status);
        return stSemesterBudgetMapper.updateStSemesterBudget(update);
    }

    @Override
    public BudgetStatisticVO statistics(StSemesterBudget stSemesterBudget) {
        List<StSemesterBudget> list = stSemesterBudgetMapper.selectStSemesterBudgetList(stSemesterBudget);
        BudgetStatisticVO vo = new BudgetStatisticVO();
        for (StSemesterBudget budget : list) {
            // 使用动态计算的锁定金额
            java.math.BigDecimal actualLocked = stSemesterBudgetMapper.calculateActualLockedAmount(budget.getId());
            vo.setTotalBudget(vo.getTotalBudget().add(safe(budget.getBudgetAmount())));
            vo.setUsedAmount(vo.getUsedAmount().add(safe(budget.getUsedAmount())));
            vo.setLockedAmount(vo.getLockedAmount().add(actualLocked != null ? actualLocked : BigDecimal.ZERO));
        }
        vo.setAvailableAmount(vo.getTotalBudget().subtract(vo.getUsedAmount()).subtract(vo.getLockedAmount()));
        return vo;
    }
    
    @Override
    public java.math.BigDecimal fixBudgetLockedAmount(Long budgetId) {
        int result = stSemesterBudgetMapper.fixBudgetLockedAmount(budgetId);
        if (result > 0) {
            StSemesterBudget budget = stSemesterBudgetMapper.selectStSemesterBudgetById(budgetId);
            return budget != null ? budget.getLockedAmount() : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public int fixAllBudgetLockedAmounts() {
        List<StSemesterBudget> allBudgets = stSemesterBudgetMapper.selectStSemesterBudgetList(new StSemesterBudget());
        int fixedCount = 0;
        for (StSemesterBudget budget : allBudgets) {
            java.math.BigDecimal actualLocked = stSemesterBudgetMapper.calculateActualLockedAmount(budget.getId());
            java.math.BigDecimal storedLocked = budget.getLockedAmount() != null ? budget.getLockedAmount() : BigDecimal.ZERO;
            // 如果计算值与存储值不一致，则修复
            if (actualLocked.compareTo(storedLocked) != 0) {
                int result = stSemesterBudgetMapper.fixBudgetLockedAmount(budget.getId());
                if (result > 0) {
                    fixedCount++;
                }
            }
        }
        return fixedCount;
    }

    private void validateUsageConstraint(StSemesterBudget budget, BigDecimal newAmount, BigDecimal originalAmount) {
        BigDecimal used = safe(budget.getUsedAmount());
        BigDecimal locked = safe(budget.getLockedAmount());
        if (newAmount.compareTo(used.add(locked)) < 0) {
            throw new ServiceException("预算金额不能小于已使用与锁定金额之和");
        }
    }

    private void validateQuotaCapacity(StSemesterBudget budget, BigDecimal newAmount, BigDecimal restoreAmount) {
        if (budget.getQuotaDetailId() == null) {
            return;
        }
        // 使用悲观锁查询指标明细（防止并发修改导致超分配）
        StSubsidyQuotaDetail detail = stSubsidyQuotaDetailMapper.selectStSubsidyQuotaDetailByIdForUpdate(budget.getQuotaDetailId());
        if (detail == null) {
            throw new ServiceException("关联的指标明细不存在");
        }
        BigDecimal total = safe(detail.getTotalAmount());
        BigDecimal allocated = safe(detail.getAllocatedAmount());
        BigDecimal effectiveAvailable = total.subtract(allocated).add(restoreAmount);
        if (effectiveAvailable.compareTo(newAmount) < 0) {
            throw new ServiceException(String.format("分配金额超出指标剩余额度，可用金额：%.2f，需要金额：%.2f", 
                effectiveAvailable, newAmount));
        }
    }

    private void adjustQuotaAllocation(Long quotaId, Long quotaDetailId, BigDecimal amountChange) {
        if (amountChange == null || amountChange.compareTo(ZERO) == 0) {
            return;
        }
        // 先更新明细，再更新指标（明细是基础，指标是汇总）
        if (quotaDetailId != null) {
            int detailResult = stSubsidyQuotaDetailMapper.updateAllocatedAmount(quotaDetailId, amountChange);
            if (detailResult == 0) {
                throw new ServiceException("更新指标明细已分配金额失败，可能金额不足或记录不存在");
            }
        }
        if (quotaId != null) {
            int quotaResult = stSubsidyQuotaMapper.updateAllocatedAmount(quotaId, amountChange);
            if (quotaResult == 0) {
                throw new ServiceException("更新指标已分配金额失败，可能金额不足或记录不存在");
            }
        }
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? ZERO : value;
    }
}