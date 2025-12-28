package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StReportAttachment;

/**
 * 报表附件Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public interface StReportAttachmentMapper 
{
    /**
     * 查询报表附件
     * 
     * @param id 报表附件主键
     * @return 报表附件
     */
    public StReportAttachment selectStReportAttachmentById(Long id);

    /**
     * 查询报表附件列表
     * 
     * @param stReportAttachment 报表附件
     * @return 报表附件集合
     */
    public List<StReportAttachment> selectStReportAttachmentList(StReportAttachment stReportAttachment);

    /**
     * 根据类型和关联ID查询附件列表
     * 
     * @param attachType 附件类型
     * @param relatedId 关联ID
     * @return 附件列表
     */
    public List<StReportAttachment> selectAttachmentByTypeAndRelatedId(String attachType, String relatedId);

    /**
     * 统计附件数量
     * 
     * @param attachType 附件类型
     * @param relatedId 关联ID
     * @return 数量
     */
    public int countAttachmentByTypeAndRelatedId(String attachType, String relatedId);

    /**
     * 新增报表附件
     * 
     * @param stReportAttachment 报表附件
     * @return 结果
     */
    public int insertStReportAttachment(StReportAttachment stReportAttachment);

    /**
     * 修改报表附件
     * 
     * @param stReportAttachment 报表附件
     * @return 结果
     */
    public int updateStReportAttachment(StReportAttachment stReportAttachment);

    /**
     * 删除报表附件
     * 
     * @param id 报表附件主键
     * @return 结果
     */
    public int deleteStReportAttachmentById(Long id);

    /**
     * 批量删除报表附件
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStReportAttachmentByIds(Long[] ids);

    /**
     * 根据类型和关联ID删除附件
     * 
     * @param attachType 附件类型
     * @param relatedId 关联ID
     * @return 结果
     */
    public int deleteAttachmentByTypeAndRelatedId(String attachType, String relatedId);
}
