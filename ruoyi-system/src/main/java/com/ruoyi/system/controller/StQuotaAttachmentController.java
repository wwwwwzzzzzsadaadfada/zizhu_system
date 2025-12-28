package com.ruoyi.system.controller;

import java.util.List;
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
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.domain.StQuotaAttachment;
import com.ruoyi.system.domain.vo.PreviewResult;
import com.ruoyi.system.service.IStQuotaAttachmentService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 指标附件Controller
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@RestController
@RequestMapping("/system/quotaAttachment")
public class StQuotaAttachmentController extends BaseController
{
    @Autowired
    private IStQuotaAttachmentService stQuotaAttachmentService;

    /**
     * 查询指标附件列表
     */
    @PreAuthorize("@ss.hasPermi('system:quotaAttachment:list')")
    @GetMapping("/list")
    public TableDataInfo list(StQuotaAttachment stQuotaAttachment)
    {
        startPage();
        List<StQuotaAttachment> list = stQuotaAttachmentService.selectStQuotaAttachmentList(stQuotaAttachment);
        return getDataTable(list);
    }

    /**
     * 根据指标ID查询附件列表
     */
    @PreAuthorize("@ss.hasPermi('system:quotaAttachment:list')")
    @GetMapping("/listByQuotaId/{quotaId}")
    public AjaxResult listByQuotaId(@PathVariable("quotaId") Long quotaId)
    {
        List<StQuotaAttachment> list = stQuotaAttachmentService.selectStQuotaAttachmentListByQuotaId(quotaId);
        return success(list);
    }

    /**
     * 获取指标附件详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:quotaAttachment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stQuotaAttachmentService.selectStQuotaAttachmentById(id));
    }

    /**
     * 上传指标附件
     */
    @PreAuthorize("@ss.hasPermi('system:quotaAttachment:upload')")
    @Log(title = "指标附件", businessType = BusinessType.INSERT)
    @PostMapping("/upload/{quotaId}")
    public AjaxResult upload(@PathVariable("quotaId") Long quotaId, @RequestParam("file") MultipartFile file)
    {
        StQuotaAttachment attachment = stQuotaAttachmentService.uploadAttachment(file, quotaId);
        return success(attachment);
    }

    /**
     * 新增指标附件
     */
    @PreAuthorize("@ss.hasPermi('system:quotaAttachment:add')")
    @Log(title = "指标附件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StQuotaAttachment stQuotaAttachment)
    {
        return toAjax(stQuotaAttachmentService.insertStQuotaAttachment(stQuotaAttachment));
    }

    /**
     * 修改指标附件
     */
    @PreAuthorize("@ss.hasPermi('system:quotaAttachment:edit')")
    @Log(title = "指标附件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StQuotaAttachment stQuotaAttachment)
    {
        return toAjax(stQuotaAttachmentService.updateStQuotaAttachment(stQuotaAttachment));
    }

    /**
     * 删除指标附件
     */
    @PreAuthorize("@ss.hasPermi('system:quotaAttachment:remove')")
    @Log(title = "指标附件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stQuotaAttachmentService.deleteStQuotaAttachmentByIds(ids));
    }

    /**
     * 下载指标附件
     */
    @PreAuthorize("@ss.hasPermi('system:quotaAttachment:download')")
    @GetMapping("/download/{id}")
    public void download(@PathVariable("id") Long id, HttpServletResponse response)
    {
        try
        {
            StQuotaAttachment attachment = stQuotaAttachmentService.selectStQuotaAttachmentById(id);
            if (attachment == null)
            {
                throw new Exception("附件不存在");
            }

            if (attachment.getStatus() == 0)
            {
                throw new Exception("附件已删除");
            }

            // 获取文件路径
            String fileUrl = attachment.getFileUrl();
            if (fileUrl.startsWith("/profile/upload/"))
            {
                fileUrl = fileUrl.substring("/profile/upload/".length());
            }
            String filePath = com.ruoyi.common.config.RuoYiConfig.getUploadPath() + 
                             java.io.File.separator + fileUrl.replace("/", java.io.File.separator);

            java.io.File file = new java.io.File(filePath);
            if (!file.exists())
            {
                throw new Exception("文件不存在：" + filePath);
            }

            // 下载文件
            String downloadName = attachment.getFileName();
            if (downloadName == null || downloadName.isEmpty())
            {
                downloadName = file.getName();
            }

            response.setContentType("application/octet-stream");
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
        }
        catch (Exception e)
        {
            logger.error("下载附件失败", e);
        }
    }

    /**
     * 预览文件（异步）
     */
    @PreAuthorize("@ss.hasPermi('system:quotaAttachment:preview')")
    @GetMapping("/preview/{id}")
    public AjaxResult preview(@PathVariable("id") Long id)
    {
        try
        {
            PreviewResult result = stQuotaAttachmentService.getPreviewResult(id);
            return success(result);
        }
        catch (Exception e)
        {
            // 如果同步预览失败，返回提示信息
            return AjaxResult.success("文件较大，正在异步处理，请稍后查询", "async");
        }
    }

    /**
     * 预览文件（异步，获取结果）
     */
    @PreAuthorize("@ss.hasPermi('system:quotaAttachment:preview')")
    @GetMapping("/previewAsync/{id}")
    public AjaxResult previewAsync(@PathVariable("id") Long id)
    {
        java.util.concurrent.CompletableFuture<PreviewResult> future = 
            stQuotaAttachmentService.previewFile(id);
        
        try
        {
            PreviewResult result = future.get();
            return success(result);
        }
        catch (Exception e)
        {
            return error("预览失败：" + e.getMessage());
        }
    }
}


