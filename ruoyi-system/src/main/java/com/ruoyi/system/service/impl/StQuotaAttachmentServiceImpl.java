package com.ruoyi.system.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.system.domain.StQuotaAttachment;
import com.ruoyi.system.domain.vo.PreviewResult;
import com.ruoyi.system.mapper.StQuotaAttachmentMapper;
import com.ruoyi.system.service.IStQuotaAttachmentService;

/**
 * 指标附件Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@Service
public class StQuotaAttachmentServiceImpl implements IStQuotaAttachmentService 
{
    private static final Logger log = LoggerFactory.getLogger(StQuotaAttachmentServiceImpl.class);

    @Autowired
    private StQuotaAttachmentMapper stQuotaAttachmentMapper;

    // ServerConfig 在 ruoyi-framework 模块中，为避免循环依赖，使用反射或直接构建URL
    // @Autowired(required = false)
    // private com.ruoyi.framework.config.ServerConfig serverConfig;

    /** 允许上传的文件类型 */
    private static final String[] ALLOWED_EXTENSIONS = {
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
        "jpg", "jpeg", "png", "gif", "bmp", "ico", "svg",
        "txt", "xml", "json", "md", "java", "js", "css", "html", "py", "sql"
    };

    /** 文件大小限制（50MB） */
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024L;

    /** 预览结果缓存时间（秒） */
    @Value("${file.preview.cache.timeout:3600}")
    private long previewCacheTimeout;

    /**
     * 查询指标附件
     */
    @Override
    public StQuotaAttachment selectStQuotaAttachmentById(Long id)
    {
        return stQuotaAttachmentMapper.selectStQuotaAttachmentById(id);
    }

    /**
     * 查询指标附件列表
     */
    @Override
    public List<StQuotaAttachment> selectStQuotaAttachmentList(StQuotaAttachment stQuotaAttachment)
    {
        return stQuotaAttachmentMapper.selectStQuotaAttachmentList(stQuotaAttachment);
    }

    /**
     * 根据指标ID查询附件列表
     */
    @Override
    public List<StQuotaAttachment> selectStQuotaAttachmentListByQuotaId(Long quotaId)
    {
        return stQuotaAttachmentMapper.selectStQuotaAttachmentListByQuotaId(quotaId);
    }

    /**
     * 上传附件
     */
    @Override
    @Transactional
    public StQuotaAttachment uploadAttachment(MultipartFile file, Long quotaId)
    {
        // 1. 文件校验
        validateFile(file);

        try
        {
            // 2. 上传文件到服务器
            String filePath = RuoYiConfig.getUploadPath();
            String fileName = FileUploadUtils.upload(filePath, file, ALLOWED_EXTENSIONS, true);

            // 3. 获取文件信息
            String originalFilename = file.getOriginalFilename();
            String fileType = FilenameUtils.getExtension(originalFilename);
            if (fileType != null) {
                fileType = fileType.toLowerCase();
            }
            String mimeType = file.getContentType();
            long fileSize = file.getSize();

            // 4. 保存附件记录到数据库
            StQuotaAttachment attachment = new StQuotaAttachment();
            attachment.setQuotaId(quotaId);
            attachment.setFileName(originalFilename);
            attachment.setFileUrl(fileName); // 存储相对路径
            attachment.setFileSize(fileSize);
            attachment.setFileType(fileType);
            attachment.setMimeType(mimeType);
            attachment.setUploadBy(SecurityUtils.getUsername());
            attachment.setUploadTime(new Date());
            attachment.setStatus(1);
            attachment.setSortOrder(0);
            attachment.setCreateBy(SecurityUtils.getUsername());
            attachment.setCreateTime(new Date());

            stQuotaAttachmentMapper.insertStQuotaAttachment(attachment);

            log.info("指标附件上传成功：quotaId={}, fileName={}, fileUrl={}", quotaId, originalFilename, fileName);
            return attachment;
        }
        catch (Exception e)
        {
            log.error("指标附件上传失败：quotaId={}, fileName={}", quotaId, file.getOriginalFilename(), e);
            throw new ServiceException("附件上传失败：" + e.getMessage());
        }
    }

    /**
     * 新增指标附件
     */
    @Override
    public int insertStQuotaAttachment(StQuotaAttachment stQuotaAttachment)
    {
        stQuotaAttachment.setCreateTime(new Date());
        return stQuotaAttachmentMapper.insertStQuotaAttachment(stQuotaAttachment);
    }

    /**
     * 修改指标附件
     */
    @Override
    public int updateStQuotaAttachment(StQuotaAttachment stQuotaAttachment)
    {
        stQuotaAttachment.setUpdateTime(new Date());
        return stQuotaAttachmentMapper.updateStQuotaAttachment(stQuotaAttachment);
    }

    /**
     * 批量删除指标附件
     */
    @Override
    @Transactional
    public int deleteStQuotaAttachmentByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            deleteStQuotaAttachmentById(id);
        }
        return ids.length;
    }

    /**
     * 删除指标附件信息
     */
    @Override
    @Transactional
    public int deleteStQuotaAttachmentById(Long id)
    {
        StQuotaAttachment attachment = stQuotaAttachmentMapper.selectStQuotaAttachmentById(id);
        if (attachment == null)
        {
            throw new ServiceException("附件不存在");
        }

        // 1. 删除物理文件
        try
        {
            String filePath = RuoYiConfig.getUploadPath() + File.separator + attachment.getFileUrl().replace("/profile/upload/", "");
            File file = new File(filePath);
            if (file.exists())
            {
                file.delete();
                log.info("删除附件物理文件：{}", filePath);
            }
        }
        catch (Exception e)
        {
            log.warn("删除附件物理文件失败：{}", attachment.getFileUrl(), e);
        }

        // 2. 逻辑删除数据库记录
        return stQuotaAttachmentMapper.deleteStQuotaAttachmentById(id);
    }

    /**
     * 预览文件（异步）
     */
    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<PreviewResult> previewFile(Long id)
    {
        try
        {
            StQuotaAttachment attachment = stQuotaAttachmentMapper.selectStQuotaAttachmentById(id);
            if (attachment == null)
            {
                throw new ServiceException("附件不存在");
            }

            if (attachment.getStatus() == 0)
            {
                throw new ServiceException("附件已删除");
            }

            File file = getFile(attachment);
            String fileType = attachment.getFileType();
            if (fileType == null || fileType.isEmpty())
            {
                throw new ServiceException("无法识别文件类型");
            }
            fileType = fileType.toLowerCase();
            
            PreviewResult result = null;
            
            // 根据文件类型选择预览方式
            if ("pdf".equals(fileType))
            {
                result = previewPdf(file, attachment);
            }
            else if (isImageType(fileType))
            {
                result = previewImage(file, attachment);
            }
            else if (isOfficeType(fileType))
            {
                result = previewOffice(file, attachment);
            }
            else if (isTextType(fileType))
            {
                result = previewText(file, attachment);
            }
            else
            {
                throw new ServiceException("不支持预览的文件类型：" + fileType);
            }

            result.setFileName(attachment.getFileName());
            result.setFileType(fileType);
            
            log.info("文件预览成功：fileId={}, type={}", id, fileType);
            return CompletableFuture.completedFuture(result);
        }
        catch (Exception e)
        {
            log.error("文件预览失败：fileId={}", id, e);
            throw new ServiceException("文件预览失败：" + e.getMessage());
        }
    }

    /**
     * 获取预览结果（同步，从缓存或直接生成）
     */
    @Override
    public PreviewResult getPreviewResult(Long id)
    {
        // 先尝试从缓存获取
        // TODO: 可以添加Redis缓存
        
        // 直接生成预览（小文件）或返回异步任务
        StQuotaAttachment attachment = stQuotaAttachmentMapper.selectStQuotaAttachmentById(id);
        if (attachment == null)
        {
            throw new ServiceException("附件不存在");
        }

        File file = getFile(attachment);
        String fileType = attachment.getFileType().toLowerCase();
        
        // 小文件直接同步处理
        if (file.length() < 5 * 1024 * 1024) // 小于5MB
        {
            try
            {
                if ("pdf".equals(fileType))
                {
                    return previewPdf(file, attachment);
                }
                else if (isImageType(fileType))
                {
                    return previewImage(file, attachment);
                }
                else if (isOfficeType(fileType))
                {
                    return previewOffice(file, attachment);
                }
                else if (isTextType(fileType))
                {
                    return previewText(file, attachment);
                }
            }
            catch (Exception e)
            {
                log.error("同步预览失败，转为异步处理：fileId={}", id, e);
            }
        }
        
        // 大文件或复杂文件，返回异步任务
        throw new ServiceException("文件较大，请使用异步预览接口");
    }

    /**
     * PDF预览（转图片）
     */
    private PreviewResult previewPdf(File pdfFile, StQuotaAttachment attachment) throws Exception
    {
        List<String> imageUrls = new ArrayList<>();
        PDDocument document = null;
        
        try
        {
            document = PDDocument.load(pdfFile);
            PDFRenderer renderer = new PDFRenderer(document);
            
            int pageCount = document.getNumberOfPages();
            // 限制最多预览前10页
            int maxPages = Math.min(pageCount, 10);
            
            for (int i = 0; i < maxPages; i++)
            {
                BufferedImage image = renderer.renderImageWithDPI(i, 150);
                String imageUrl = savePreviewImage(image, attachment.getId() + "_page_" + i + ".png");
                imageUrls.add(imageUrl);
            }
            
            PreviewResult result = new PreviewResult("pdf", imageUrls);
            result.setFileName(attachment.getFileName());
            result.setFileType("pdf");
            return result;
        }
        finally
        {
            if (document != null)
            {
                document.close();
            }
        }
    }

    /**
     * 图片预览
     */
    private PreviewResult previewImage(File imageFile, StQuotaAttachment attachment) throws Exception
    {
        // 直接返回相对路径，前端会自动拼接完整URL
        String imageUrl = attachment.getFileUrl();
        
        PreviewResult result = new PreviewResult("image", imageUrl);
        result.setFileName(attachment.getFileName());
        result.setFileType(attachment.getFileType());
        return result;
    }

    /**
     * Office预览（转HTML）
     */
    private PreviewResult previewOffice(File officeFile, StQuotaAttachment attachment) throws Exception
    {
        String fileType = attachment.getFileType();
        if (fileType == null || fileType.isEmpty())
        {
            throw new ServiceException("无法识别文件类型");
        }
        fileType = fileType.toLowerCase();
        String htmlContent = "";
        
        if ("docx".equals(fileType))
        {
            // Word 2007+
            XWPFDocument document = new XWPFDocument(new FileInputStream(officeFile));
            htmlContent = convertWordToHtml(document);
            document.close();
        }
        else if ("doc".equals(fileType))
        {
            // Word 2003 - 简化处理，只提取文本
            HWPFDocument document = new HWPFDocument(new FileInputStream(officeFile));
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>文档预览</title>");
            html.append("<style>body{font-family:Microsoft YaHei,Arial,sans-serif;padding:20px;line-height:1.6;}</style>");
            html.append("</head><body>");
            
            // 提取段落文本
            String text = document.getDocumentText();
            if (StringUtils.isNotEmpty(text))
            {
                String[] paragraphs = text.split("\r\n");
                for (String para : paragraphs)
                {
                    if (StringUtils.isNotEmpty(para.trim()))
                    {
                        html.append("<p>").append(escapeHtml(para.trim())).append("</p>");
                    }
                }
            }
            
            html.append("</body></html>");
            htmlContent = html.toString();
            document.close();
        }
        else if ("xlsx".equals(fileType) || "xls".equals(fileType))
        {
            // Excel
            Workbook workbook = WorkbookFactory.create(officeFile);
            htmlContent = convertExcelToHtml(workbook);
            workbook.close();
        }
        else if ("ppt".equals(fileType) || "pptx".equals(fileType))
        {
            // PowerPoint - 暂不支持，返回提示
            throw new ServiceException("暂不支持PowerPoint文件预览，请下载后查看");
        }
        else
        {
            throw new ServiceException("不支持的Office格式：" + fileType);
        }
        
        PreviewResult result = new PreviewResult("html", htmlContent);
        result.setFileName(attachment.getFileName());
        result.setFileType(fileType);
        return result;
    }

    /**
     * 文本预览
     */
    private PreviewResult previewText(File textFile, StQuotaAttachment attachment) throws Exception
    {
        String textContent = IOUtils.toString(new FileInputStream(textFile), StandardCharsets.UTF_8);
        
        // 限制文本长度（避免过大）
        if (textContent.length() > 100000)
        {
            textContent = textContent.substring(0, 100000) + "\n\n... (文件过大，仅显示前100000字符)";
        }
        
        PreviewResult result = new PreviewResult("text", textContent);
        result.setFileName(attachment.getFileName());
        result.setFileType(attachment.getFileType());
        return result;
    }

    /**
     * Word转HTML（简化版）
     */
    private String convertWordToHtml(XWPFDocument document)
    {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>文档预览</title>");
        html.append("<style>body{font-family:Microsoft YaHei,Arial,sans-serif;padding:20px;line-height:1.6;}</style>");
        html.append("</head><body>");
        
        // 简化处理：提取段落文本
        document.getParagraphs().forEach(paragraph -> {
            String text = paragraph.getText();
            if (StringUtils.isNotEmpty(text))
            {
                html.append("<p>").append(escapeHtml(text)).append("</p>");
            }
        });
        
        // 处理表格
        document.getTables().forEach(table -> {
            html.append("<table border='1' style='border-collapse:collapse;width:100%;margin:10px 0;'>");
            table.getRows().forEach(row -> {
                html.append("<tr>");
                row.getTableCells().forEach(cell -> {
                    html.append("<td>").append(escapeHtml(cell.getText())).append("</td>");
                });
                html.append("</tr>");
            });
            html.append("</table>");
        });
        
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * Excel转HTML（简化版）
     */
    private String convertExcelToHtml(Workbook workbook)
    {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>表格预览</title>");
        html.append("<style>body{font-family:Microsoft YaHei,Arial,sans-serif;padding:20px;}");
        html.append("table{border-collapse:collapse;width:100%;margin:10px 0;}");
        html.append("td,th{border:1px solid #ddd;padding:8px;text-align:left;}</style>");
        html.append("</head><body>");
        
        // 只处理第一个Sheet
        if (workbook.getNumberOfSheets() > 0)
        {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            html.append("<h3>").append(sheet.getSheetName()).append("</h3>");
            html.append("<table>");
            
            for (org.apache.poi.ss.usermodel.Row row : sheet)
            {
                html.append("<tr>");
                for (org.apache.poi.ss.usermodel.Cell cell : row)
                {
                    html.append("<td>").append(getCellValue(cell)).append("</td>");
                }
                html.append("</tr>");
            }
            
            html.append("</table>");
        }
        
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * 获取单元格值
     */
    private String getCellValue(org.apache.poi.ss.usermodel.Cell cell)
    {
        if (cell == null) return "";
        
        switch (cell.getCellType())
        {
            case STRING:
                return escapeHtml(cell.getStringCellValue());
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * HTML转义
     */
    private String escapeHtml(String text)
    {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    /**
     * 保存预览图片
     */
    private String savePreviewImage(BufferedImage image, String fileName) throws IOException
    {
        String previewDir = RuoYiConfig.getUploadPath() + "/preview";
        File dir = new File(previewDir);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        
        File imageFile = new File(previewDir, fileName);
        ImageIO.write(image, "PNG", imageFile);
        
        // 返回相对路径
        return "/profile/upload/preview/" + fileName;
    }

    /**
     * 获取文件对象
     */
    private File getFile(StQuotaAttachment attachment)
    {
        String fileUrl = attachment.getFileUrl();
        // 去掉 /profile/upload/ 前缀
        if (fileUrl.startsWith("/profile/upload/"))
        {
            fileUrl = fileUrl.substring("/profile/upload/".length());
        }
        String filePath = RuoYiConfig.getUploadPath() + File.separator + fileUrl.replace("/", File.separator);
        return new File(filePath);
    }

    /**
     * 判断是否为图片类型
     */
    private boolean isImageType(String fileType)
    {
        return "jpg".equals(fileType) || "jpeg".equals(fileType) || 
               "png".equals(fileType) || "gif".equals(fileType) || 
               "bmp".equals(fileType) || "ico".equals(fileType) || 
               "svg".equals(fileType);
    }

    /**
     * 判断是否为Office类型
     */
    private boolean isOfficeType(String fileType)
    {
        return "doc".equals(fileType) || "docx".equals(fileType) || 
               "xls".equals(fileType) || "xlsx".equals(fileType) || 
               "ppt".equals(fileType) || "pptx".equals(fileType);
    }

    /**
     * 判断是否为文本类型
     */
    private boolean isTextType(String fileType)
    {
        return "txt".equals(fileType) || "xml".equals(fileType) || 
               "json".equals(fileType) || "md".equals(fileType) || 
               "java".equals(fileType) || "js".equals(fileType) || 
               "css".equals(fileType) || "html".equals(fileType) || 
               "py".equals(fileType) || "sql".equals(fileType);
    }

    /**
     * 文件校验
     */
    private void validateFile(MultipartFile file)
    {
        if (file == null || file.isEmpty())
        {
            throw new ServiceException("文件不能为空");
        }

        if (file.getSize() > MAX_FILE_SIZE)
        {
            throw new ServiceException("文件大小不能超过 " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isEmpty(originalFilename))
        {
            throw new ServiceException("文件名不能为空");
        }

        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\"))
        {
            throw new ServiceException("文件名不合法");
        }

        String fileType = FilenameUtils.getExtension(originalFilename != null ? originalFilename : "");
        if (fileType == null)
        {
            throw new ServiceException("无法识别文件类型");
        }
        fileType = fileType.toLowerCase();
        
        boolean isAllowed = false;
        for (String allowedType : ALLOWED_EXTENSIONS)
        {
            if (allowedType.equalsIgnoreCase(fileType))
            {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed)
        {
            throw new ServiceException("不支持的文件类型：" + fileType + "，支持的类型：" + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }
}

