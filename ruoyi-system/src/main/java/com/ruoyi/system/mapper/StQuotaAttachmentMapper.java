package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StQuotaAttachment;

/**
 * 指标附件Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public interface StQuotaAttachmentMapper 
{
    /**
     * 查询指标附件
     * 
     * @param id 指标附件主键
     * @return 指标附件
     */
    public StQuotaAttachment selectStQuotaAttachmentById(Long id);

    /**
     * 查询指标附件列表
     * 
     * @param stQuotaAttachment 指标附件
     * @return 指标附件集合
     */
    public List<StQuotaAttachment> selectStQuotaAttachmentList(StQuotaAttachment stQuotaAttachment);

    /**
     * 根据指标ID查询附件列表
     * 
     * @param quotaId 指标ID
     * @return 指标附件集合
     */
    public List<StQuotaAttachment> selectStQuotaAttachmentListByQuotaId(Long quotaId);

    /**
     * 新增指标附件
     * 
     * @param stQuotaAttachment 指标附件
     * @return 结果
     */
    public int insertStQuotaAttachment(StQuotaAttachment stQuotaAttachment);

    /**
     * 修改指标附件
     * 
     * @param stQuotaAttachment 指标附件
     * @return 结果
     */
    public int updateStQuotaAttachment(StQuotaAttachment stQuotaAttachment);

    /**
     * 删除指标附件
     * 
     * @param id 指标附件主键
     * @return 结果
     */
    public int deleteStQuotaAttachmentById(Long id);

    /**
     * 批量删除指标附件
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStQuotaAttachmentByIds(Long[] ids);
}


