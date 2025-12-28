package com.ruoyi.system.domain.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 文件预览结果
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public class PreviewResult implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 预览类型：pdf, image, html, text */
    private String previewType;

    /** 预览内容（根据类型不同，内容格式不同） */
    private Object content;

    /** PDF预览：图片URL列表 */
    private List<String> imageUrls;

    /** HTML预览：HTML内容 */
    private String htmlContent;

    /** 文本预览：文本内容 */
    private String textContent;

    /** 图片预览：图片URL */
    private String imageUrl;

    /** 文件信息 */
    private String fileName;
    private String fileType;

    public PreviewResult() {
    }

    public PreviewResult(String previewType, Object content) {
        this.previewType = previewType;
        this.content = content;
        
        // 根据类型设置对应字段
        if ("pdf".equals(previewType) && content instanceof List) {
            this.imageUrls = (List<String>) content;
        } else if ("html".equals(previewType) && content instanceof String) {
            this.htmlContent = (String) content;
        } else if ("text".equals(previewType) && content instanceof String) {
            this.textContent = (String) content;
        } else if ("image".equals(previewType) && content instanceof String) {
            this.imageUrl = (String) content;
        }
    }

    public String getPreviewType() {
        return previewType;
    }

    public void setPreviewType(String previewType) {
        this.previewType = previewType;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}


