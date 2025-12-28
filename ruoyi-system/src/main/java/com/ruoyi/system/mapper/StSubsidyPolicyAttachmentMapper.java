package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StSubsidyPolicyAttachment;

/**
 * 政策附件Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public interface StSubsidyPolicyAttachmentMapper 
{
    /**
     * 查询政策附件
     * 
     * @param id 政策附件主键
     * @return 政策附件
     */
    public StSubsidyPolicyAttachment selectStSubsidyPolicyAttachmentById(Long id);

    /**
     * 根据政策ID查询附件列表
     * 
     * @param policyId 政策ID
     * @return 政策附件集合
     */
    public List<StSubsidyPolicyAttachment> selectStSubsidyPolicyAttachmentByPolicyId(Long policyId);

    /**
     * 查询政策附件列表
     * 
     * @param stSubsidyPolicyAttachment 政策附件
     * @return 政策附件集合
     */
    public List<StSubsidyPolicyAttachment> selectStSubsidyPolicyAttachmentList(StSubsidyPolicyAttachment stSubsidyPolicyAttachment);

    /**
     * 新增政策附件
     * 
     * @param stSubsidyPolicyAttachment 政策附件
     * @return 结果
     */
    public int insertStSubsidyPolicyAttachment(StSubsidyPolicyAttachment stSubsidyPolicyAttachment);

    /**
     * 批量新增政策附件
     * 
     * @param attachmentList 附件列表
     * @return 结果
     */
    public int batchInsertStSubsidyPolicyAttachment(List<StSubsidyPolicyAttachment> attachmentList);

    /**
     * 修改政策附件
     * 
     * @param stSubsidyPolicyAttachment 政策附件
     * @return 结果
     */
    public int updateStSubsidyPolicyAttachment(StSubsidyPolicyAttachment stSubsidyPolicyAttachment);

    /**
     * 删除政策附件
     * 
     * @param id 政策附件主键
     * @return 结果
     */
    public int deleteStSubsidyPolicyAttachmentById(Long id);

    /**
     * 根据政策ID删除附件
     * 
     * @param policyId 政策ID
     * @return 结果
     */
    public int deleteStSubsidyPolicyAttachmentByPolicyId(Long policyId);

    /**
     * 批量删除政策附件
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStSubsidyPolicyAttachmentByIds(Long[] ids);
}


