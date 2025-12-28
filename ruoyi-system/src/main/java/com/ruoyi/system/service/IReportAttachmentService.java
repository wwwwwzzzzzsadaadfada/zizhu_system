package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StReportAttachment;
import org.springframework.web.multipart.MultipartFile;

/**
 * 报表附件Service接口
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public interface IReportAttachmentService 
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
     * @param attachType 附件类型（report/custom）
     * @param relatedId 关联ID（report_name或package_code）
     * @return 附件列表
     */
    public List<StReportAttachment> selectAttachmentByTypeAndRelatedId(String attachType, String relatedId);

    /**
     * 上传附件（复用系统现有文件上传功能）
     * 
     * @param file 上传的文件
     * @param attachType 附件类型（report/custom）
     * @param relatedId 关联ID
     * @param remark 备注
     * @return 附件记录
     */
    public StReportAttachment uploadAttachment(MultipartFile file, String attachType, String relatedId, String remark) throws Exception;

    /**
     * 批量上传附件
     * 
     * @param files 文件列表
     * @param attachType 附件类型
     * @param relatedId 关联ID
     * @param remark 备注
     * @return 附件记录列表
     */
    public List<StReportAttachment> uploadAttachments(List<MultipartFile> files, String attachType, String relatedId, String remark) throws Exception;

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
     * 批量删除报表附件
     * 
     * @param ids 需要删除的报表附件主键集合
     * @return 结果
     */
    public int deleteStReportAttachmentByIds(Long[] ids);

    /**
     * 删除报表附件信息
     * 
     * @param id 报表附件主键
     * @return 结果
     */
    public int deleteStReportAttachmentById(Long id);

    /**
     * 根据类型和关联ID删除附件
     * 
     * @param attachType 附件类型
     * @param relatedId 关联ID
     * @return 结果
     */
    public int deleteAttachmentByTypeAndRelatedId(String attachType, String relatedId);

    /**
     * 统计附件数量
     * 
     * @param attachType 附件类型
     * @param relatedId 关联ID
     * @return 数量
     */
    public int countAttachmentByTypeAndRelatedId(String attachType, String relatedId);
}
