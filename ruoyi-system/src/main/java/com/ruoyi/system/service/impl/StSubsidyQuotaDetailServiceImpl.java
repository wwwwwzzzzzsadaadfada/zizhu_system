package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StSubsidyQuotaDetailMapper;
import com.ruoyi.system.domain.StSubsidyQuotaDetail;
import com.ruoyi.system.service.IStSubsidyQuotaDetailService;

/**
 * 资助指标经济分类明细Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
@Service
public class StSubsidyQuotaDetailServiceImpl implements IStSubsidyQuotaDetailService 
{
    @Autowired
    private StSubsidyQuotaDetailMapper stSubsidyQuotaDetailMapper;

    /**
     * 查询资助指标经济分类明细
     * 
     * @param id 资助指标经济分类明细主键
     * @return 资助指标经济分类明细
     */
    @Override
    public StSubsidyQuotaDetail selectStSubsidyQuotaDetailById(Long id)
    {
        return stSubsidyQuotaDetailMapper.selectStSubsidyQuotaDetailById(id);
    }

    /**
     * 查询资助指标经济分类明细列表
     * 
     * @param stSubsidyQuotaDetail 资助指标经济分类明细
     * @return 资助指标经济分类明细
     */
    @Override
    public List<StSubsidyQuotaDetail> selectStSubsidyQuotaDetailList(StSubsidyQuotaDetail stSubsidyQuotaDetail)
    {
        return stSubsidyQuotaDetailMapper.selectStSubsidyQuotaDetailList(stSubsidyQuotaDetail);
    }

    /**
     * 根据指标ID查询明细列表
     * 
     * @param quotaId 指标ID
     * @return 明细列表
     */
    @Override
    public List<StSubsidyQuotaDetail> selectDetailListByQuotaId(Long quotaId)
    {
        return stSubsidyQuotaDetailMapper.selectDetailListByQuotaId(quotaId);
    }

    /**
     * 新增资助指标经济分类明细
     * 
     * @param stSubsidyQuotaDetail 资助指标经济分类明细
     * @return 结果
     */
    @Override
    public int insertStSubsidyQuotaDetail(StSubsidyQuotaDetail stSubsidyQuotaDetail)
    {
        return stSubsidyQuotaDetailMapper.insertStSubsidyQuotaDetail(stSubsidyQuotaDetail);
    }

    /**
     * 修改资助指标经济分类明细
     * 
     * @param stSubsidyQuotaDetail 资助指标经济分类明细
     * @return 结果
     */
    @Override
    public int updateStSubsidyQuotaDetail(StSubsidyQuotaDetail stSubsidyQuotaDetail)
    {
        return stSubsidyQuotaDetailMapper.updateStSubsidyQuotaDetail(stSubsidyQuotaDetail);
    }

    /**
     * 批量删除资助指标经济分类明细
     * 
     * @param ids 需要删除的资助指标经济分类明细主键
     * @return 结果
     */
    @Override
    public int deleteStSubsidyQuotaDetailByIds(Long[] ids)
    {
        return stSubsidyQuotaDetailMapper.deleteStSubsidyQuotaDetailByIds(ids);
    }

    /**
     * 删除资助指标经济分类明细信息
     * 
     * @param id 资助指标经济分类明细主键
     * @return 结果
     */
    @Override
    public int deleteStSubsidyQuotaDetailById(Long id)
    {
        return stSubsidyQuotaDetailMapper.deleteStSubsidyQuotaDetailById(id);
    }
}
