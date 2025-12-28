package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StSemesterBudget;
import com.ruoyi.system.domain.vo.BudgetStatisticVO;

/**
 * 学期预算支出Service接口
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
public interface IStSemesterBudgetService 
{
    /**
     * 查询学期预算支出
     * 
     * @param id 学期预算支出主键
     * @return 学期预算支出
     */
    public StSemesterBudget selectStSemesterBudgetById(Long id);

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
     * 批量删除学期预算支出
     * 
     * @param ids 需要删除的学期预算支出主键
     * @return 结果
     */
    public int deleteStSemesterBudgetByIds(Long[] ids);

    /**
     * 删除学期预算支出信息
     * 
     * @param id 学期预算支出主键
     * @return 结果
     */
    public int deleteStSemesterBudgetById(Long id);
    
    /**
     * 根据预算ID查询经济分类
     * 
     * @param budgetId 预算ID
     * @return 经济分类
     */
    public String selectEconomyCategoryByBudgetId(Long budgetId);
    
    /**
     * 校准预算数据一致性
     * 定期检查并修复预算表中的已使用金额与实际补助明细总额不一致的问题
     */
    public void calibrateBudgetConsistency();
    
    /**
     * 查询所有预算项目名称（去重）
     * 
     * @param yearSemesterId 学年学期ID
     * @return 预算项目名称列表
     */
    public List<String> selectAllBudgetProjectNames(Long yearSemesterId);
    
    /**
     * 查询所有预算项目名称（去重）
     * 
     * @return 预算项目名称列表
     */
    public List<String> selectAllBudgetProjectNames();

    /**
     * 更新预算状态
     *
     * @param id 预算ID
     * @param status 状态
     * @return 结果
     */
    int updateBudgetStatus(Long id, Integer status);

    /**
     * 查询预算统计信息
     *
     * @param stSemesterBudget 查询条件
     * @return 统计结果
     */
    BudgetStatisticVO statistics(StSemesterBudget stSemesterBudget);
    
    /**
     * 修复预算的锁定金额（根据实际未审批记录重新计算）
     * 
     * @param budgetId 预算ID
     * @return 修复后的锁定金额
     */
    java.math.BigDecimal fixBudgetLockedAmount(Long budgetId);
    
    /**
     * 修复所有预算的锁定金额（批量修复）
     * 
     * @return 修复的预算数量
     */
    int fixAllBudgetLockedAmounts();
}