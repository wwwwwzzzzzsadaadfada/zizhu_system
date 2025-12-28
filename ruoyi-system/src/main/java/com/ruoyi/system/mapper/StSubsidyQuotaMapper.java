package com.ruoyi.system.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.StSubsidyQuota;

/**
 * 资助指标下达Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
public interface StSubsidyQuotaMapper 
{
    /**
     * 查询资助指标下达
     * 
     * @param id 资助指标下达主键
     * @return 资助指标下达
     */
    public StSubsidyQuota selectStSubsidyQuotaById(Long id);
    
    /**
     * 根据指标文号和学年学期ID查询指标
     * 
     * @param quotaDocNo 指标文号
     * @param yearSemesterId 学年学期ID
     * @return 资助指标下达
     */
    public StSubsidyQuota selectStSubsidyQuotaByDocNoAndSemester(@Param("quotaDocNo") String quotaDocNo, @Param("yearSemesterId") Long yearSemesterId);

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
     * 删除资助指标下达
     * 
     * @param id 资助指标下达主键
     * @return 结果
     */
    public int deleteStSubsidyQuotaById(Long id);

    /**
     * 批量删除资助指标下达
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStSubsidyQuotaByIds(Long[] ids);

    /**
     * 查询指标使用情况（含已使用名额统计）
     * 
     * @param stSubsidyQuota 资助指标下达
     * @return 资助指标下达集合（含使用情况）
     */
    public List<StSubsidyQuota> selectStSubsidyQuotaListWithUsage(StSubsidyQuota stSubsidyQuota);
    
    /**
     * 根据学年学期ID和功能分类查询指标文号列表（去重）
     * 
     * @param yearSemesterId 学年学期ID
     * @param functionCategory 功能分类
     * @return 指标文号列表
     */
    public List<String> selectQuotaDocNosByYearSemesterAndFunction(@Param("yearSemesterId") Long yearSemesterId, @Param("functionCategory") String functionCategory);
    
    /**
     * 根据明细ID获取已分配金额
     * 
     * @param detailId 明细ID
     * @return 已分配金额
     */
    public BigDecimal getAllocatedAmountByDetailId(Long detailId);
    
    /**
     * 更新指标的已分配金额
     * 
     * @param quotaId 指标ID
     * @param amount 增减金额（正数为增加，负数为减少）
     * @return 结果
     */
    public int updateAllocatedAmount(@Param("quotaId") Long quotaId, @Param("amount") java.math.BigDecimal amount);
    
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
    public int updateAllocatedAmountByEconomyCategory(@Param("quotaId") Long quotaId, @Param("economyCategory") String economyCategory, @Param("amount") java.math.BigDecimal amount);
    
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
    public List<String> selectAllBudgetProjectNamesWithoutFilter();

    /**
     * 更新指标的汇总字段（总指标、已分配、状态）
     *
     * @param stSubsidyQuota 指标对象
     * @return 结果
     */
    public int updateQuotaAggregateFields(StSubsidyQuota stSubsidyQuota);
    
    /**
     * 查询来源指标的每个经济分类已结转的金额
     * 
     * @param sourceQuotaId 来源指标ID
     * @return List<Map>，每个Map包含economyCategory和carriedOverAmount
     */
    public List<Map<String, Object>> getCarriedOverAmountByEconomyCategory(@Param("sourceQuotaId") Long sourceQuotaId);
}