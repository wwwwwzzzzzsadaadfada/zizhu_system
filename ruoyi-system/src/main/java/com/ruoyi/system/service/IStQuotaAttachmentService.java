package com.ruoyi.system.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.system.domain.StQuotaAttachment;
import com.ruoyi.system.domain.vo.PreviewResult;

/**
 * 指标附件Service接口
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public interface IStQuotaAttachmentService 
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
     * 上传附件
     * 
     * @param file 文件
     * @param quotaId 指标ID
     * @return 指标附件
     */
    public StQuotaAttachment uploadAttachment(MultipartFile file, Long quotaId);

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
     * 批量删除指标附件
     * 
     * @param ids 需要删除的指标附件主键集合
     * @return 结果
     */
    public int deleteStQuotaAttachmentByIds(Long[] ids);

    /**
     * 删除指标附件信息
     * 
     * @param id 指标附件主键
     * @return 结果
     */
    public int deleteStQuotaAttachmentById(Long id);

    /**
     * 预览文件（异步）
     * 
     * @param id 附件ID
     * @return 预览结果
     */
    public CompletableFuture<PreviewResult> previewFile(Long id);

    /**
     * 获取预览结果
     * 
     * @param id 附件ID
     * @return 预览结果
     */
    public PreviewResult getPreviewResult(Long id);
}

