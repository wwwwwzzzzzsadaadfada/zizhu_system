package com.ruoyi.system.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.system.domain.StReportAttachment;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.mapper.StReportAttachmentMapper;
import com.ruoyi.system.mapper.StSchoolYearSemesterMapper;
import com.ruoyi.system.service.IReportAttachmentService;

/**
 * 报表附件Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@Service
public class ReportAttachmentServiceImpl implements IReportAttachmentService 
{
    private static final Logger log = LoggerFactory.getLogger(ReportAttachmentServiceImpl.class);

    @Autowired
    private StReportAttachmentMapper stReportAttachmentMapper;

    @Autowired
    private StSchoolYearSemesterMapper stSchoolYearSemesterMapper;

    /**
     * 查询报表附件
     * 
     * @param id 报表附件主键
     * @return 报表附件
     */
    @Override
    public StReportAttachment selectStReportAttachmentById(Long id)
    {
        return stReportAttachmentMapper.selectStReportAttachmentById(id);
    }

    /**
     * 查询报表附件列表
     * 
     * @param stReportAttachment 报表附件
     * @return 报表附件
     */
    @Override
    public List<StReportAttachment> selectStReportAttachmentList(StReportAttachment stReportAttachment)
    {
        return stReportAttachmentMapper.selectStReportAttachmentList(stReportAttachment);
    }

    /**
     * 根据类型和关联ID查询附件列表
     * 
     * @param attachType 附件类型
     * @param relatedId 关联ID
     * @return 附件列表
     */
    @Override
    public List<StReportAttachment> selectAttachmentByTypeAndRelatedId(String attachType, String relatedId)
    {
        List<StReportAttachment> list = stReportAttachmentMapper.selectAttachmentByTypeAndRelatedId(attachType, relatedId);
        
        // 转换学期信息
        if (list != null && !list.isEmpty())
        {
            // 收集所有学期ID
            java.util.Set<Long> yearSemesterIds = list.stream()
                .filter(item -> item.getYearSemesterId() != null)
                .map(StReportAttachment::getYearSemesterId)
                .collect(java.util.stream.Collectors.toSet());
            
            if (!yearSemesterIds.isEmpty())
            {
                // 批量查询学期信息
                java.util.Map<Long, StSchoolYearSemester> semesterMap = new java.util.HashMap<>();
                for (Long id : yearSemesterIds)
                {
                    try
                    {
                        StSchoolYearSemester semester = stSchoolYearSemesterMapper.selectStSchoolYearSemesterById(id);
                        if (semester != null)
                        {
                            semesterMap.put(id, semester);
                        }
                    }
                    catch (Exception e)
                    {
                        log.error("查询学期ID {} 失败", id, e);
                    }
                }
                
                // 设置学期名称
                list.forEach(attachment -> {
                    Long ysId = attachment.getYearSemesterId();
                    if (ysId != null && semesterMap.containsKey(ysId))
                    {
                        StSchoolYearSemester ys = semesterMap.get(ysId);
                        String schoolYear = ys.getSchoolYear() != null ? ys.getSchoolYear() : "";
                        Integer semester = ys.getSemester();
                        String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(semester);
                        if (semesterLabel == null)
                        {
                            semesterLabel = "";
                        }
                        attachment.setYearSemesterName(schoolYear + semesterLabel);
                    }
                });
            }
        }
        
        return list;
    }

    /**
     * 上传附件（复用系统现有文件上传功能）
     * 
     * @param file 上传的文件
     * @param attachType 附件类型
     * @param relatedId 关联ID
     * @param remark 备注
     * @return 附件记录
     */
    @Override
    @Transactional
    public StReportAttachment uploadAttachment(MultipartFile file, String attachType, String relatedId, String remark) throws Exception
    {
        try
        {
            // 验证参数
            if (file == null || file.isEmpty())
            {
                throw new ServiceException("上传文件不能为空");
            }
            if (attachType == null || attachType.isEmpty())
            {
                throw new ServiceException("附件类型不能为空");
            }
            if (relatedId == null || relatedId.isEmpty())
            {
                throw new ServiceException("关联ID不能为空");
            }

            // 使用系统的FileUploadUtils上传文件
            String filePath = RuoYiConfig.getUploadPath();
            String fileName = FileUploadUtils.upload(filePath, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
            
            // 获取文件信息
            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            long fileSize = file.getSize();
            
            // 确定文件类型
            String fileType = determineFileType(extension);
            
            // 获取当前学期ID（仅对自定义档案包类型的附件）
            Long yearSemesterId = null;
            if ("custom".equals(attachType))
            {
                try
                {
                    StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
                    if (currentSemester != null)
                    {
                        yearSemesterId = currentSemester.getId();
                        log.info("自定义档案包附件使用当前学期ID: {}", yearSemesterId);
                    }
                    else
                    {
                        log.warn("未找到当前学期，附件将不设置学期ID");
                    }
                }
                catch (Exception e)
                {
                    log.error("获取当前学期失败", e);
                }
            }
            
            // 创建附件记录
            StReportAttachment attachment = new StReportAttachment();
            attachment.setAttachType(attachType);
            attachment.setRelatedId(relatedId);
            attachment.setFileName(fileName);
            attachment.setFilePath(fileName); // FileUploadUtils返回的已是相对路径
            attachment.setFileSize(fileSize);
            attachment.setFileType(fileType);
            attachment.setFileExt(extension);
            attachment.setOriginalName(originalFilename);
            attachment.setRemark(remark);
            attachment.setYearSemesterId(yearSemesterId);
            attachment.setStatus(1);
            attachment.setSortOrder(0);
            attachment.setCreateBy(SecurityUtils.getUsername());
            attachment.setCreateTime(new Date());
            
            // 保存到数据库
            stReportAttachmentMapper.insertStReportAttachment(attachment);
            
            log.info("上传附件成功: attachType={}, relatedId={}, fileName={}", attachType, relatedId, fileName);
            
            return attachment;
        }
        catch (Exception e)
        {
            log.error("上传附件失败: attachType={}, relatedId={}", attachType, relatedId, e);
            throw new ServiceException("上传附件失败: " + e.getMessage());
        }
    }

    /**
     * 批量上传附件
     * 
     * @param files 文件列表
     * @param attachType 附件类型
     * @param relatedId 关联ID
     * @param remark 备注
     * @return 附件记录列表
     */
    @Override
    @Transactional
    public List<StReportAttachment> uploadAttachments(List<MultipartFile> files, String attachType, String relatedId, String remark) throws Exception
    {
        List<StReportAttachment> attachments = new ArrayList<>();
        
        if (files == null || files.isEmpty())
        {
            throw new ServiceException("上传文件列表不能为空");
        }
        
        for (MultipartFile file : files)
        {
            if (file != null && !file.isEmpty())
            {
                StReportAttachment attachment = uploadAttachment(file, attachType, relatedId, remark);
                attachments.add(attachment);
            }
        }
        
        log.info("批量上传附件成功: attachType={}, relatedId={}, count={}", attachType, relatedId, attachments.size());
        
        return attachments;
    }

    /**
     * 新增报表附件
     * 
     * @param stReportAttachment 报表附件
     * @return 结果
     */
    @Override
    public int insertStReportAttachment(StReportAttachment stReportAttachment)
    {
        stReportAttachment.setCreateTime(new Date());
        return stReportAttachmentMapper.insertStReportAttachment(stReportAttachment);
    }

    /**
     * 修改报表附件
     * 
     * @param stReportAttachment 报表附件
     * @return 结果
     */
    @Override
    public int updateStReportAttachment(StReportAttachment stReportAttachment)
    {
        stReportAttachment.setUpdateTime(new Date());
        return stReportAttachmentMapper.updateStReportAttachment(stReportAttachment);
    }

    /**
     * 批量删除报表附件
     * 
     * @param ids 需要删除的报表附件主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStReportAttachmentByIds(Long[] ids)
    {
        // 逻辑删除
        return stReportAttachmentMapper.deleteStReportAttachmentByIds(ids);
    }

    /**
     * 删除报表附件信息
     * 
     * @param id 报表附件主键
     * @return 结果
     */
    @Override
    public int deleteStReportAttachmentById(Long id)
    {
        // 逻辑删除
        return stReportAttachmentMapper.deleteStReportAttachmentById(id);
    }

    /**
     * 根据类型和关联ID删除附件
     * 
     * @param attachType 附件类型
     * @param relatedId 关联ID
     * @return 结果
     */
    @Override
    public int deleteAttachmentByTypeAndRelatedId(String attachType, String relatedId)
    {
        return stReportAttachmentMapper.deleteAttachmentByTypeAndRelatedId(attachType, relatedId);
    }

    /**
     * 统计附件数量
     * 
     * @param attachType 附件类型
     * @param relatedId 关联ID
     * @return 数量
     */
    @Override
    public int countAttachmentByTypeAndRelatedId(String attachType, String relatedId)
    {
        return stReportAttachmentMapper.countAttachmentByTypeAndRelatedId(attachType, relatedId);
    }

    /**
     * 根据文件扩展名判断文件类型
     * 
     * @param extension 文件扩展名
     * @return 文件类型
     */
    private String determineFileType(String extension)
    {
        if (extension == null)
        {
            return "other";
        }
        
        extension = extension.toLowerCase();
        
        if ("pdf".equals(extension))
        {
            return "pdf";
        }
        else if ("jpg".equals(extension) || "jpeg".equals(extension) || "png".equals(extension) || 
                 "gif".equals(extension) || "bmp".equals(extension))
        {
            return "image";
        }
        else if ("doc".equals(extension) || "docx".equals(extension))
        {
            return "word";
        }
        else if ("xls".equals(extension) || "xlsx".equals(extension))
        {
            return "excel";
        }
        else if ("ppt".equals(extension) || "pptx".equals(extension))
        {
            return "powerpoint";
        }
        else if ("zip".equals(extension) || "rar".equals(extension) || "7z".equals(extension) || 
                 "gz".equals(extension) || "bz2".equals(extension))
        {
            return "archive";
        }
        else if ("txt".equals(extension))
        {
            return "text";
        }
        else
        {
            return "other";
        }
    }
}
