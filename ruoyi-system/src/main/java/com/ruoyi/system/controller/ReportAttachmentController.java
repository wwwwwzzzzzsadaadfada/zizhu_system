package com.ruoyi.system.controller;

import java.io.File;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.domain.StReportAttachment;
import com.ruoyi.system.service.IReportAttachmentService;
import com.ruoyi.system.service.IReportPackageCustomService;

/**
 * 报表附件Controller
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/system/report/attachment")
public class ReportAttachmentController extends BaseController
{
    @Autowired
    private IReportAttachmentService reportAttachmentService;

    @Autowired
    private IReportPackageCustomService reportPackageCustomService;

    /**
     * 查询报表附件列表
     */
    @PreAuthorize("@ss.hasPermi('system:reportAttachment:list')")
    @GetMapping("/list")
    public TableDataInfo list(StReportAttachment stReportAttachment)
    {
        startPage();
        List<StReportAttachment> list = reportAttachmentService.selectStReportAttachmentList(stReportAttachment);
        return getDataTable(list);
    }

    /**
     * 根据类型和关联ID查询附件列表（不分页）
     */
    @PreAuthorize("@ss.hasPermi('system:reportAttachment:list')")
    @GetMapping("/listByRelated")
    public AjaxResult listByRelated(@RequestParam String attachType, @RequestParam String relatedId)
    {
        List<StReportAttachment> list = reportAttachmentService.selectAttachmentByTypeAndRelatedId(attachType, relatedId);
        return success(list);
    }

    /**
     * 获取报表附件详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:reportAttachment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(reportAttachmentService.selectStReportAttachmentById(id));
    }

    /**
     * 上传单个附件
     */
    @PreAuthorize("@ss.hasPermi('system:reportAttachment:upload')")
    @Log(title = "上传附件", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("attachType") String attachType,
            @RequestParam("relatedId") String relatedId,
            @RequestParam(value = "remark", required = false) String remark)
    {
        try
        {
            StReportAttachment attachment = reportAttachmentService.uploadAttachment(file, attachType, relatedId, remark);
            
            // 如果是自定义档案包的附件，自动更新统计信息
            if ("custom".equals(attachType))
            {
                try
                {
                    reportPackageCustomService.updateFileStatistics(relatedId);
                }
                catch (Exception e)
                {
                    logger.warn("更新档案包统计信息失败", e);
                }
            }
            
            return success(attachment);
        }
        catch (Exception e)
        {
            logger.error("上传附件失败", e);
            return error("上传附件失败: " + e.getMessage());
        }
    }

    /**
     * 批量上传附件
     */
    @PreAuthorize("@ss.hasPermi('system:reportAttachment:upload')")
    @Log(title = "批量上传附件", businessType = BusinessType.INSERT)
    @PostMapping("/uploads")
    public AjaxResult uploads(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("attachType") String attachType,
            @RequestParam("relatedId") String relatedId,
            @RequestParam(value = "remark", required = false) String remark)
    {
        try
        {
            List<StReportAttachment> attachments = reportAttachmentService.uploadAttachments(files, attachType, relatedId, remark);
            
            // 如果是自定义档案包的附件，自动更新统计信息
            if ("custom".equals(attachType))
            {
                try
                {
                    reportPackageCustomService.updateFileStatistics(relatedId);
                }
                catch (Exception e)
                {
                    logger.warn("更新档案包统计信息失败", e);
                }
            }
            
            return success(attachments);
        }
        catch (Exception e)
        {
            logger.error("批量上传附件失败", e);
            return error("批量上传附件失败: " + e.getMessage());
        }
    }

    /**
     * 新增报表附件
     */
    @PreAuthorize("@ss.hasPermi('system:reportAttachment:add')")
    @Log(title = "报表附件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StReportAttachment stReportAttachment)
    {
        return toAjax(reportAttachmentService.insertStReportAttachment(stReportAttachment));
    }

    /**
     * 修改报表附件
     */
    @PreAuthorize("@ss.hasPermi('system:reportAttachment:edit')")
    @Log(title = "报表附件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StReportAttachment stReportAttachment)
    {
        return toAjax(reportAttachmentService.updateStReportAttachment(stReportAttachment));
    }

    /**
     * 删除报表附件
     */
    @PreAuthorize("@ss.hasPermi('system:reportAttachment:remove')")
    @Log(title = "报表附件", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        // 在删除前，获取附件信息用于后续更新统计
        List<StReportAttachment> attachmentsToDelete = new java.util.ArrayList<>();
        for (Long id : ids)
        {
            StReportAttachment attachment = reportAttachmentService.selectStReportAttachmentById(id);
            if (attachment != null)
            {
                attachmentsToDelete.add(attachment);
            }
        }
        
        int result = reportAttachmentService.deleteStReportAttachmentByIds(ids);
        
        // 删除成功后，更新自定义档案包的统计信息
        if (result > 0)
        {
            java.util.Set<String> customPackageCodes = new java.util.HashSet<>();
            for (StReportAttachment attachment : attachmentsToDelete)
            {
                if ("custom".equals(attachment.getAttachType()))
                {
                    customPackageCodes.add(attachment.getRelatedId());
                }
            }
            
            // 更新每个影响的档案包
            for (String packageCode : customPackageCodes)
            {
                try
                {
                    reportPackageCustomService.updateFileStatistics(packageCode);
                }
                catch (Exception e)
                {
                    logger.warn("更新档案包统计信息失败: " + packageCode, e);
                }
            }
        }
        
        return toAjax(result);
    }

    /**
     * 下载附件
     */
    @PreAuthorize("@ss.hasPermi('system:reportAttachment:download')")
    @GetMapping("/download/{id}")
    public void download(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            StReportAttachment attachment = reportAttachmentService.selectStReportAttachmentById(id);
            if (attachment == null)
            {
                throw new RuntimeException("附件记录不存在");
            }

            if (attachment.getStatus() == 0)
            {
                throw new RuntimeException("附件已删除");
            }

            // 构建完整的文件路径
            String storedPath = attachment.getFilePath();
            String filePath;
            
            // 如果存储的路径已经包含/profile前缀，则直接使用，否则拼接
            if (storedPath.startsWith("/profile"))
            {
                filePath = RuoYiConfig.getProfile() + storedPath.substring(8); // 去掉/profile前缀
            }
            else
            {
                filePath = RuoYiConfig.getProfile() + storedPath;
            }
            
            File file = new File(filePath);
            if (!file.exists())
            {
                throw new RuntimeException("文件不存在: " + filePath);
            }

            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            response.setContentLength((int) file.length());
            
            String fileName = attachment.getOriginalName();
            if (fileName == null || fileName.isEmpty())
            {
                fileName = attachment.getFileName();
            }
            
            response.setHeader("Content-Disposition", "attachment;filename=" + FileUtils.setFileDownloadHeader(request, fileName));
            FileUtils.writeBytes(filePath, response.getOutputStream());
        }
        catch (Exception e)
        {
            logger.error("下载附件失败", e);
        }
    }

    /**
     * 统计附件数量
     */
    @PreAuthorize("@ss.hasPermi('system:reportAttachment:list')")
    @GetMapping("/count")
    public AjaxResult count(@RequestParam String attachType, @RequestParam String relatedId)
    {
        int count = reportAttachmentService.countAttachmentByTypeAndRelatedId(attachType, relatedId);
        return success(count);
    }
}
