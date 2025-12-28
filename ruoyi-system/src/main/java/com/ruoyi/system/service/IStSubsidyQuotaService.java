package com.ruoyi.system.service;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.system.domain.StSubsidyQuota;
import com.ruoyi.system.domain.StSubsidyQuotaDetail;
import com.ruoyi.system.domain.dto.QuotaAllocationRequest;
import com.ruoyi.system.domain.vo.QuotaStatisticVO;

/**
 * 资助指标下达Service接口
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
public interface IStSubsidyQuotaService 
{
    /**
     * 查询资助指标下达
     * 
     * @param id 资助指标下达主键
     * @return 资助指标下达
     */
    public StSubsidyQuota selectStSubsidyQuotaById(Long id);

    /**
     * 查询资助指标下达列表
     * 
     * @param stSubsidyQuota 资助指标下达
     * @return 资助指标下达集合
     */
    public List<StSubsidyQuota> selectStSubsidyQuotaList(StSubsidyQuota stSubsidyQuota);

    /**
     * 新增资助指标下达
     * 
     * @param stSubsidyQuota 资助指标下达
     * @return 结果
     */
    public int insertStSubsidyQuota(StSubsidyQuota stSubsidyQuota);

    /**
     * 修改资助指标下达
     * 
     * @param stSubsidyQuota 资助指标下达
     * @return 结果
     */
    public int updateStSubsidyQuota(StSubsidyQuota stSubsidyQuota);

    /**
     * 批量删除资助指标下达
     * 
     * @param ids 需要删除的资助指标下达主键数组
     * @return 结果
     */
    public int deleteStSubsidyQuotaByIds(Long[] ids);

    /**
     * 删除资助指标下达信息
     * 
     * @param id 资助指标下达主键
     * @return 结果
     */
    public int deleteStSubsidyQuotaById(Long id);
    
    /**
     * 查询指标使用情况（含已使用名额统计）
     * 
     * @param stSubsidyQuota 资助指标下达
     * @return 资助指标下达集合（含使用情况）
     */
    public List<StSubsidyQuota> selectStSubsidyQuotaListWithUsage(StSubsidyQuota stSubsidyQuota);
    
    /**
     * 修复所有指标的已分配金额（根据学期预算重新计算）
     * 
     * @return 修复的指标数量
     */
    public int fixAllocatedAmount();
    
    /**
     * 根据经济分类更新指标明细的已分配金额
     * 
     * @param quotaId 指标ID
     * @param economyCategory 经济分类
     * @param amount 金额
     * @return 结果
     */
    public int updateAllocatedAmountByEconomyCategory(Long quotaId, String economyCategory, BigDecimal amount);

    /**
     * 查询可分配的指标明细列表
     *
     * @param filter 查询条件
     * @return 可用明细集合
     */
    List<StSubsidyQuotaDetail> listAllocatableDetails(StSubsidyQuotaDetail filter);

    /**
     * 将指标额度分配到具体学期预算
     *
     * @param request 分配请求
     * @return 新建的学期预算ID
     */
    Long allocateQuota(QuotaAllocationRequest request);

    /**
     * 批量将指标额度分配到学期预算
     *
     * @param request 批量分配请求
     * @return 新建的学期预算ID列表
     */
    List<Long> allocateQuotaBatch(com.ruoyi.system.domain.dto.BatchQuotaAllocationRequest request);

    /**
     * 查询指标统计信息（不受分页影响，统计所有符合条件的数据）
     *
     * @param stSubsidyQuota 查询条件
     * @return 统计结果
     */
    QuotaStatisticVO statistics(StSubsidyQuota stSubsidyQuota);
    
    /**
     * 查询指标已分配的预算列表
     *
     * @param quotaId 指标ID
     * @return 预算列表
     */
    List<com.ruoyi.system.domain.StSemesterBudget> listAllocatedBudgets(Long quotaId);
    
    /**
     * 收回已分配的预算
     *
     * @param budgetIds 要收回的预算ID数组
     * @return 收回的预算数量
     */
    int reclaimBudgets(Long[] budgetIds);
    
    /**
     * 将历史学期指标的剩余额度批量结转到当前学期预算
     *
     * @param request 结转请求
     * @return 创建的预算ID列表
     */
    List<Long> carryOverToBudget(com.ruoyi.system.domain.dto.CarryOverToBudgetRequest request);
    
    /**
     * 根据学年学期ID和功能分类查询指标文号列表（去重）
     *
     * @param yearSemesterId 学年学期ID
     * @param functionCategory 功能分类
     * @return 指标文号列表
     */
    List<String> getQuotaDocNosByYearSemesterAndFunction(Long yearSemesterId, String functionCategory);
    
    /**
     * 将来源指标的资金结转到目标指标（如果目标指标存在，则增加其总指标；如果不存在，则创建新指标）
     *
     * @param request 结转请求
     * @return 目标指标ID
     */
    Long carryOverToTargetQuota(com.ruoyi.system.domain.dto.CarryOverToTargetQuotaRequest request);
}