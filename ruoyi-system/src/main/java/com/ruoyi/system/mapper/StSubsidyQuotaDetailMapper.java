package com.ruoyi.system.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.StSubsidyQuotaDetail;

/**
 * 资助指标经济分类明细Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
public interface StSubsidyQuotaDetailMapper 
{
    /**
     * 查询资助指标经济分类明细
     * 
     * @param id 资助指标经济分类明细主键
     * @return 资助指标经济分类明细
     */
    public StSubsidyQuotaDetail selectStSubsidyQuotaDetailById(Long id);
    
    /**
     * 查询资助指标经济分类明细（带悲观锁，用于并发控制）
     * 
     * @param id 资助指标经济分类明细主键
     * @return 资助指标经济分类明细
     */
    public StSubsidyQuotaDetail selectStSubsidyQuotaDetailByIdForUpdate(Long id);

    /**
     * 查询资助指标经济分类明细列表
     * 
     * @param stSubsidyQuotaDetail 资助指标经济分类明细
     * @return 资助指标经济分类明细集合
     */
    public List<StSubsidyQuotaDetail> selectStSubsidyQuotaDetailList(StSubsidyQuotaDetail stSubsidyQuotaDetail);

    /**
     * 根据指标ID查询明细列表
     * 
     * @param quotaId 指标ID
     * @return 明细列表
     */
    public List<StSubsidyQuotaDetail> selectDetailListByQuotaId(Long quotaId);

    /**
     * 新增资助指标经济分类明细
     * 
     * @param stSubsidyQuotaDetail 资助指标经济分类明细
     * @return 结果
     */
    public int insertStSubsidyQuotaDetail(StSubsidyQuotaDetail stSubsidyQuotaDetail);

    /**
     * 修改资助指标经济分类明细
     * 
     * @param stSubsidyQuotaDetail 资助指标经济分类明细
     * @return 结果
     */
    public int updateStSubsidyQuotaDetail(StSubsidyQuotaDetail stSubsidyQuotaDetail);

    /**
     * 删除资助指标经济分类明细
     * 
     * @param id 资助指标经济分类明细主键
     * @return 结果
     */
    public int deleteStSubsidyQuotaDetailById(Long id);

    /**
     * 批量删除资助指标经济分类明细
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStSubsidyQuotaDetailByIds(Long[] ids);

    /**
     * 根据指标ID删除所有明细
     * 
     * @param quotaId 指标ID
     * @return 结果
     */
    public int deleteDetailsByQuotaId(Long quotaId);

    /**
     * 更新已分配金额
     * 
     * @param detailId 明细ID
     * @param amount 金额（可以是正数增加，也可以是负数减少）
     * @return 结果
     */
    public int updateAllocatedAmount(@Param("detailId") Long detailId, @Param("amount") BigDecimal amount);
    
    /**
     * 批量插入资助指标明细
     * 
     * @param detailList 明细列表
     * @return 结果
     */
    public int batchInsertStSubsidyQuotaDetail(@Param("detailList") List<StSubsidyQuotaDetail> detailList);
    
    /**
     * 批量更新已分配金额
     * 
     * @param updates 更新列表，每个元素包含 detailId 和 amount
     * @return 结果
     */
    public int batchUpdateAllocatedAmount(@Param("updates") List<java.util.Map<String, Object>> updates);
    
    /**
     * 计算指标的结转金额（查询目标指标反向统计）
     * 
     * @param quotaId 指标ID
     * @return 结转金额总计
     */
    public BigDecimal sumCarriedOverAmount(@Param("quotaId") Long quotaId);
}
