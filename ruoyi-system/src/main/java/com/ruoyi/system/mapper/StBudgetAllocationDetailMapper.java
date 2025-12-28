package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.StBudgetAllocationDetail;

/**
 * 预算分配明细Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-15
 */
public interface StBudgetAllocationDetailMapper {
    /**
     * 查询预算分配明细
     * 
     * @param id 预算分配明细主键
     * @return 预算分配明细
     */
    public StBudgetAllocationDetail selectStBudgetAllocationDetailById(Long id);

    /**
     * 查询预算分配明细列表
     * 
     * @param stBudgetAllocationDetail 预算分配明细
     * @return 预算分配明细集合
     */
    public List<StBudgetAllocationDetail> selectStBudgetAllocationDetailList(StBudgetAllocationDetail stBudgetAllocationDetail);

    /**
     * 根据补助记录ID查询分配明细列表
     * 
     * @param subsidyRecordId 补助记录ID
     * @return 分配明细列表
     */
    public List<StBudgetAllocationDetail> selectBySubsidyRecordId(@Param("subsidyRecordId") Long subsidyRecordId);

    /**
     * 根据预算ID查询分配明细列表
     * 
     * @param budgetId 预算ID
     * @return 分配明细列表
     */
    public List<StBudgetAllocationDetail> selectByBudgetId(@Param("budgetId") Long budgetId);

    /**
     * 新增预算分配明细
     * 
     * @param stBudgetAllocationDetail 预算分配明细
     * @return 结果
     */
    public int insertStBudgetAllocationDetail(StBudgetAllocationDetail stBudgetAllocationDetail);

    /**
     * 批量新增预算分配明细
     * 
     * @param details 分配明细列表
     * @return 结果
     */
    public int batchInsertStBudgetAllocationDetail(@Param("list") List<StBudgetAllocationDetail> details);

    /**
     * 修改预算分配明细
     * 
     * @param stBudgetAllocationDetail 预算分配明细
     * @return 结果
     */
    public int updateStBudgetAllocationDetail(StBudgetAllocationDetail stBudgetAllocationDetail);

    /**
     * 删除预算分配明细
     * 
     * @param id 预算分配明细主键
     * @return 结果
     */
    public int deleteStBudgetAllocationDetailById(Long id);

    /**
     * 批量删除预算分配明细
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStBudgetAllocationDetailByIds(Long[] ids);

    /**
     * 根据补助记录ID删除分配明细
     * 
     * @param subsidyRecordId 补助记录ID
     * @return 结果
     */
    public int deleteBySubsidyRecordId(@Param("subsidyRecordId") Long subsidyRecordId);
}




