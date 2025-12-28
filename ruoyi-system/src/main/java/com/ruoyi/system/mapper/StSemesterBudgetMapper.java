package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.StSemesterBudget;

/**
 * 学期预算支出Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
public interface StSemesterBudgetMapper 
{
    /**
     * 查询学期预算支出
     * 
     * @param id 学期预算支出主键
     * @return 学期预算支出
     */
    public StSemesterBudget selectStSemesterBudgetById(Long id);
    
    /**
     * 查询学期预算支出（带悲观锁，用于并发控制）
     * 使用 SELECT FOR UPDATE 锁定行，防止并发更新
     * 
     * @param id 学期预算支出主键
     * @return 学期预算支出
     */
    public StSemesterBudget selectStSemesterBudgetByIdForUpdate(Long id);

    /**
     * 查询学期预算支出列表
     * 
     * @param stSemesterBudget 学期预算支出
     * @return 学期预算支出集合
     */
    public List<StSemesterBudget> selectStSemesterBudgetList(StSemesterBudget stSemesterBudget);

    /**
     * 新增学期预算支出
     * 
     * @param stSemesterBudget 学期预算支出
     * @return 结果
     */
    public int insertStSemesterBudget(StSemesterBudget stSemesterBudget);

    /**
     * 修改学期预算支出
     * 
     * @param stSemesterBudget 学期预算支出
     * @return 结果
     */
    public int updateStSemesterBudget(StSemesterBudget stSemesterBudget);

    /**
     * 删除学期预算支出
     * 
     * @param id 学期预算支出主键
     * @return 结果
     */
    public int deleteStSemesterBudgetById(Long id);

    /**
     * 批量删除学期预算支出
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStSemesterBudgetByIds(Long[] ids);
    
    /**
     * 根据预算ID查询经济分类代码
     * 
     * 注意：返回的是代码（如"1"、"2"），不是中文名称。中文名称转换应在Service层使用工具类处理。
     * 
     * @param budgetId 预算ID
     * @return 经济分类代码，如果不存在则返回null
     */
    public String selectEconomyCategoryByBudgetId(Long budgetId);
    
    /**
     * 锁定预算金额（申请补助时）
     * 
     * @param budgetId 预算ID
     * @param amount 锁定金额
     * @return 结果
     */
    public int lockBudgetAmount(Long budgetId, java.math.BigDecimal amount);

    /**
     * 释放预算金额（驳回补助时）
     * 
     * @param budgetId 预算ID
     * @param amount 释放金额
     * @return 结果
     */
    public int releaseBudgetAmount(Long budgetId, java.math.BigDecimal amount);
    
    /**
     * 审批通过时更新预算金额（锁定金额转为已使用金额）- 支持组合预算
     * 
     * @param budgetId 预算ID
     * @param amount 金额
     * @param subsidyRecordId 补助记录ID（用于精确匹配分配明细）
     * @return 结果
     */
    public int updateBudgetAmountOnApprovalForCombined(@Param("budgetId") Long budgetId, 
                                                        @Param("amount") java.math.BigDecimal amount,
                                                        @Param("subsidyRecordId") Long subsidyRecordId);
    
    /**
     * 审批通过时更新预算金额（锁定金额转为已使用金额）- 兼容旧数据
     * 
     * @param budgetId 预算ID
     * @param amount 金额
     * @return 结果
     */
    public int updateBudgetAmountOnApproval(Long budgetId, java.math.BigDecimal amount);
    
    /**
     * 退回时更新预算金额（已使用金额转回可用金额）- 支持组合预算
     * 
     * @param budgetId 预算ID
     * @param amount 金额
     * @param subsidyRecordId 补助记录ID（用于精确匹配分配明细）
     * @return 结果
     */
    public int returnBudgetAmountForCombined(@Param("budgetId") Long budgetId, 
                                              @Param("amount") java.math.BigDecimal amount,
                                              @Param("subsidyRecordId") Long subsidyRecordId);
    
    /**
     * 退回时更新预算金额（已使用金额转回可用金额）- 兼容旧数据
     * 
     * @param budgetId 预算ID
     * @param amount 金额
     * @return 结果
     */
    public int returnBudgetAmount(Long budgetId, java.math.BigDecimal amount);

    /**
     * 查询当前学期可用预算
     * 
     * 注意：业务逻辑（如功能分类组合规则）应在Service层处理，这里只负责数据查询
     *
     * @param yearSemesterId 学年学期ID（必填）
     * @param economyCategory 经济分类（可选）
     * @param functionCategories 功能分类列表（可选，如果为空则不过滤功能分类）
     * @return 预算列表
     */
    public List<StSemesterBudget> selectAvailableBudgets(@Param("yearSemesterId") Long yearSemesterId,
                                                         @Param("economyCategory") String economyCategory,
                                                         @Param("functionCategories") List<String> functionCategories);

    /**
     * 查询所有可能的预算（不过滤历史结余）
     * 
     * 注意：业务逻辑（如功能分类组合规则、历史结余判断）应在Service层处理，这里只负责数据查询
     * 此方法用于查询所有可能的预算，包括当前学期和历史结余，由Service层进行过滤
     *
     * @param yearSemesterId 当前学年学期ID（可选，用于过滤学期）
     * @param economyCategory 经济分类（可选）
     * @param functionCategories 功能分类列表（可选，如果为空则不过滤功能分类）
     * @return 预算列表
     */
    public List<StSemesterBudget> selectAllPossibleBudgets(@Param("yearSemesterId") Long yearSemesterId,
                                                           @Param("economyCategory") String economyCategory,
                                                           @Param("functionCategories") List<String> functionCategories);
    
    /**
     * 查询历史结余预算（已废弃，保留用于兼容）
     * 
     * @deprecated 请使用 selectAllPossibleBudgets 并在Service层使用 HistoricalSurplusUtils 过滤
     */
    @Deprecated
    public List<StSemesterBudget> selectHistoricalSurplus(@Param("yearSemesterId") Long yearSemesterId,
                                                          @Param("economyCategory") String economyCategory,
                                                          @Param("functionCategories") List<String> functionCategories);
    
    /**
     * 根据预算ID计算实际的锁定金额（从未审批的补助记录中汇总）
     * 
     * @param budgetId 预算ID
     * @return 实际锁定金额
     */
    public java.math.BigDecimal calculateActualLockedAmount(@Param("budgetId") Long budgetId);
    
    /**
     * 根据预算ID计算实际的已使用金额（从已审批通过的补助记录中汇总）
     * 
     * @param budgetId 预算ID
     * @return 实际已使用金额
     */
    public java.math.BigDecimal calculateActualUsedAmount(@Param("budgetId") Long budgetId);
    
    /**
     * 查询所有需要校验的预算ID列表
     * 
     * @return 预算ID列表
     */
    public List<Long> selectAllBudgetIds();
    
    /**
     * 修复预算的锁定金额（根据实际未审批记录重新计算）
     * 
     * @param budgetId 预算ID
     * @return 修复后的锁定金额
     */
    public int fixBudgetLockedAmount(@Param("budgetId") Long budgetId);
    
    /**
     * 统计指定指标明细关联的预算数量
     * 
     * @param quotaDetailId 指标明细ID
     * @return 预算数量
     */
    public int countByQuotaDetailId(@Param("quotaDetailId") Long quotaDetailId);
    
    /**
     * 查询指定指标明细在学年学期的预算（用于累加）
     */
    StSemesterBudget selectByQuotaDetailAndSemester(@Param("quotaDetailId") Long quotaDetailId,
                                                     @Param("yearSemesterId") Long yearSemesterId);
    
    /**
     * 累加预算金额
     */
    int increaseBudgetAmount(@Param("budgetId") Long budgetId, @Param("delta") java.math.BigDecimal delta);
    
    /**
     * 查询与指定预算同组的预算列表（同一指标明细ID）
     * 用于预算组合使用：查询同一quota_detail_id下的所有可用预算
     * 
     * @param quotaDetailId 指标明细ID
     * @param currentSemesterId 当前学期ID（用于筛选历史结余）
     * @return 预算列表
     */
    List<StSemesterBudget> selectBudgetsByQuotaDetailId(@Param("quotaDetailId") Long quotaDetailId,
                                                         @Param("currentSemesterId") Long currentSemesterId);
    
    /**
     * 批量查询预算（带悲观锁，用于并发控制）
     * 用于预算组合分配时，同时锁定多个预算
     * 
     * @param budgetIds 预算ID列表
     * @return 预算列表（已锁定）
     */
    List<StSemesterBudget> selectBudgetsByIdsForUpdate(@Param("budgetIds") List<Long> budgetIds);
}