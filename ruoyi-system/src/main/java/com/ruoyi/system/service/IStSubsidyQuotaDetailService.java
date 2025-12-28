package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StSubsidyQuotaDetail;

/**
 * 资助指标经济分类明细Service接口
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
public interface IStSubsidyQuotaDetailService 
{
    /**
     * 查询资助指标经济分类明细
     * 
     * @param id 资助指标经济分类明细主键
     * @return 资助指标经济分类明细
     */
    public StSubsidyQuotaDetail selectStSubsidyQuotaDetailById(Long id);

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
     * 批量删除资助指标经济分类明细
     * 
     * @param ids 需要删除的资助指标经济分类明细主键集合
     * @return 结果
     */
    public int deleteStSubsidyQuotaDetailByIds(Long[] ids);

    /**
     * 删除资助指标经济分类明细信息
     * 
     * @param id 资助指标经济分类明细主键
     * @return 结果
     */
    public int deleteStSubsidyQuotaDetailById(Long id);
}
