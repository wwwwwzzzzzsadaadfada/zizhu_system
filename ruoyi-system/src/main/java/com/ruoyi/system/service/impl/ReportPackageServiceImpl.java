package com.ruoyi.system.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.SemesterUtils;
import com.ruoyi.system.domain.StReportPackage;
import com.ruoyi.system.domain.StReportPackageItem;
import com.ruoyi.system.domain.StReportPdf;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.domain.StReportAttachment;
import com.ruoyi.system.domain.StReportPackageCustom;
import com.ruoyi.system.mapper.StReportPackageItemMapper;
import com.ruoyi.system.mapper.StReportPackageMapper;
import com.ruoyi.system.mapper.StReportPdfMapper;
import com.ruoyi.system.mapper.StSchoolYearSemesterMapper;
import com.ruoyi.system.mapper.StReportAttachmentMapper;
import com.ruoyi.system.mapper.StReportPackageCustomMapper;
import com.ruoyi.system.service.IReportPackageService;

/**
 * 报表打包服务实现类
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@Service
public class ReportPackageServiceImpl implements IReportPackageService
{
    private static final Logger log = LoggerFactory.getLogger(ReportPackageServiceImpl.class);

    @Autowired
    private StReportPackageMapper stReportPackageMapper;

    @Autowired
    private StReportPackageItemMapper stReportPackageItemMapper;

    @Autowired
    private StReportPdfMapper stReportPdfMapper;

    @Autowired
    private StSchoolYearSemesterMapper stSchoolYearSemesterMapper;

    @Autowired
    private StReportAttachmentMapper stReportAttachmentMapper;

    @Autowired
    private StReportPackageCustomMapper stReportPackageCustomMapper;

    /**
     * 查询报表打包记录
     */
    @Override
    public StReportPackage selectStReportPackageById(Long id)
    {
        return stReportPackageMapper.selectStReportPackageById(id);
    }

    /**
     * 查询报表打包记录列表
     */
    @Override
    public List<StReportPackage> selectStReportPackageList(StReportPackage stReportPackage)
    {
        return stReportPackageMapper.selectStReportPackageList(stReportPackage);
    }

    /**
     * 新增报表打包记录（手动选择文件打包）
     */
    @Override
    @Transactional
    public StReportPackage createPackage(List<Long> pdfIds, String packageName)
    {
        if (pdfIds == null || pdfIds.isEmpty())
        {
            throw new ServiceException("请至少选择一个PDF文件");
        }

        // 1. 查询PDF记录
        List<StReportPdf> pdfList = stReportPdfMapper.selectStReportPdfByIds(pdfIds);
        if (pdfList == null || pdfList.isEmpty())
        {
            throw new ServiceException("未找到有效的PDF文件");
        }

        // 过滤掉已删除的记录
        pdfList = pdfList.stream()
                .filter(pdf -> pdf.getStatus() != null && pdf.getStatus() == 1)
                .collect(java.util.stream.Collectors.toList());

        if (pdfList.isEmpty())
        {
            throw new ServiceException("所选PDF文件均为已删除状态");
        }

        // 2. 生成打包名称（如果未提供，根据PDF列表自动生成）
        if (StringUtils.isBlank(packageName))
        {
            packageName = generatePackageNameByPdfList(pdfList);
        }

        // 3. 创建打包记录
        StReportPackage pkg = new StReportPackage();
        pkg.setPackageName(packageName);
        pkg.setPackageType("manual");
        pkg.setStatus(0); // 待打包
        pkg.setFileCount(pdfList.size());
        pkg.setTotalSize(pdfList.stream().mapToLong(p -> p.getFileSize() != null ? p.getFileSize() : 0L).sum());
        pkg.setCreateBy(SecurityUtils.getUsername());

        // 生成目标目录和zip文件名
        String targetDir = generateTargetDir();
        String zipFileName = generateZipFileName(pkg.getPackageName(), pdfList);
        String zipFilePath = targetDir + "/" + zipFileName;

        pkg.setTargetDir(targetDir);
        pkg.setZipFileName(zipFileName);
        pkg.setZipFilePath(zipFilePath);

        stReportPackageMapper.insertStReportPackage(pkg);
        Long packageId = pkg.getId();

        // 3. 创建打包明细
        List<StReportPackageItem> itemList = new ArrayList<>();
        for (StReportPdf pdf : pdfList)
        {
            StReportPackageItem item = new StReportPackageItem();
            item.setPackageId(packageId);
            item.setReportPdfId(pdf.getId());
            item.setFileName(pdf.getFileName());
            item.setFilePath(pdf.getFilePath());
            itemList.add(item);
        }
        stReportPackageItemMapper.batchInsertStReportPackageItem(itemList);

        // 4. 执行打包（生成zip文件）
        try
        {
            createZipFile(pkg, pdfList);
            pkg.setStatus(1); // 已生成
            pkg.setErrorMsg(null);
        }
        catch (Exception e)
        {
            log.error("打包失败：packageId={}", packageId, e);
            pkg.setStatus(2); // 失败
            pkg.setErrorMsg(e.getMessage());
        }

        stReportPackageMapper.updateStReportPackage(pkg);

        return pkg;
    }

    /**
     * 按条件打包全部档案
     */
    @Override
    @Transactional
    public StReportPackage createPackageByCondition(StReportPdf queryParams, String packageName)
    {
        // 1. 查询符合条件的PDF记录
        if (queryParams == null)
        {
            queryParams = new StReportPdf();
        }
        queryParams.setStatus(1); // 只查询正常状态的记录

        List<StReportPdf> pdfList = stReportPdfMapper.selectStReportPdfList(queryParams);
        if (pdfList == null || pdfList.isEmpty())
        {
            throw new ServiceException("没有符合条件的PDF文件");
        }

        // 2. 提取PDF ID列表
        List<Long> pdfIds = pdfList.stream().map(StReportPdf::getId).collect(java.util.stream.Collectors.toList());

        // 3. 创建打包记录（与手动打包类似，但类型为"all"）
        if (pdfIds.isEmpty())
        {
            throw new ServiceException("未找到有效的PDF文件");
        }

        // 过滤掉已删除的记录
        pdfList = pdfList.stream()
                .filter(pdf -> pdf.getStatus() != null && pdf.getStatus() == 1)
                .collect(java.util.stream.Collectors.toList());

        if (pdfList.isEmpty())
        {
            throw new ServiceException("所选PDF文件均为已删除状态");
        }

        // 创建打包记录
        StReportPackage pkg = new StReportPackage();
        pkg.setPackageName(StringUtils.isNotBlank(packageName) ? packageName : generateDefaultPackageName());
        pkg.setPackageType("all"); // 全部档案打包
        pkg.setStatus(0); // 待打包
        pkg.setFileCount(pdfList.size());
        pkg.setTotalSize(pdfList.stream().mapToLong(p -> p.getFileSize() != null ? p.getFileSize() : 0L).sum());
        pkg.setCreateBy(SecurityUtils.getUsername());

        // 生成目标目录和zip文件名
        String targetDir = generateTargetDir();
        String zipFileName = generateZipFileName(pkg.getPackageName(), pdfList);
        String zipFilePath = targetDir + "/" + zipFileName;

        pkg.setTargetDir(targetDir);
        pkg.setZipFileName(zipFileName);
        pkg.setZipFilePath(zipFilePath);

        stReportPackageMapper.insertStReportPackage(pkg);
        Long packageId = pkg.getId();

        // 创建打包明细
        List<StReportPackageItem> itemList = new ArrayList<>();
        for (StReportPdf pdf : pdfList)
        {
            StReportPackageItem item = new StReportPackageItem();
            item.setPackageId(packageId);
            item.setReportPdfId(pdf.getId());
            item.setFileName(pdf.getFileName());
            item.setFilePath(pdf.getFilePath());
            itemList.add(item);
        }
        stReportPackageItemMapper.batchInsertStReportPackageItem(itemList);

        // 执行打包（生成zip文件）
        try
        {
            createZipFile(pkg, pdfList);
            pkg.setStatus(1); // 已生成
            pkg.setErrorMsg(null);
        }
        catch (Exception e)
        {
            log.error("打包失败：packageId={}", packageId, e);
            pkg.setStatus(2); // 失败
            pkg.setErrorMsg(e.getMessage());
        }

        stReportPackageMapper.updateStReportPackage(pkg);

        return pkg;
    }

    /**
     * 一键打包全部文件包（所有报表）
     */
    @Override
    @Transactional
    public StReportPackage createAllPackage(Long yearSemesterId, Long schoolingPlanId, Long gradeId, Long classId,
                                           String studentName, String beginCreateTime, String endCreateTime,
                                           String packageName)
    {
        // 1. 查询所有符合条件的PDF记录（不限制报表名称）
        StReportPdf queryParams = new StReportPdf();
        queryParams.setStatus(1); // 只查询正常状态的记录
        queryParams.setYearSemesterId(yearSemesterId);
        queryParams.setSchoolingPlanId(schoolingPlanId);
        queryParams.setGradeId(gradeId);
        queryParams.setClassId(classId);
        queryParams.setStudentName(studentName);

        // 处理时间范围
        if (beginCreateTime != null || endCreateTime != null)
        {
            java.util.Map<String, Object> params = new java.util.HashMap<>();
            if (beginCreateTime != null)
            {
                params.put("beginCreateTime", beginCreateTime);
            }
            if (endCreateTime != null)
            {
                params.put("endCreateTime", endCreateTime);
            }
            queryParams.setParams(params);
        }

        // 查询所有符合条件的PDF（不限制报表名称）
        List<StReportPdf> pdfList = stReportPdfMapper.selectStReportPdfList(queryParams);

        if (pdfList.isEmpty())
        {
            throw new ServiceException("没有符合条件的PDF文件");
        }

        // 2. 创建打包记录
        StReportPackage pkg = new StReportPackage();
        pkg.setPackageName(StringUtils.isNotBlank(packageName) ? packageName : generatePackageNameByPdfList(pdfList));
        pkg.setPackageType("all"); // 一键打包全部
        pkg.setStatus(0); // 待打包
        pkg.setFileCount(pdfList.size());
        pkg.setTotalSize(pdfList.stream().mapToLong(p -> p.getFileSize() != null ? p.getFileSize() : 0L).sum());
        pkg.setCreateBy(SecurityUtils.getUsername());

        // 生成目标目录和zip文件名
        String targetDir = generateTargetDir();
        String zipFileName = generateZipFileName(pkg.getPackageName(), pdfList);
        String zipFilePath = targetDir + "/" + zipFileName;

        pkg.setTargetDir(targetDir);
        pkg.setZipFileName(zipFileName);
        pkg.setZipFilePath(zipFilePath);

        stReportPackageMapper.insertStReportPackage(pkg);
        Long packageId = pkg.getId();

        // 3. 创建打包明细
        List<StReportPackageItem> itemList = new ArrayList<>();
        for (StReportPdf pdf : pdfList)
        {
            StReportPackageItem item = new StReportPackageItem();
            item.setPackageId(packageId);
            item.setReportPdfId(pdf.getId());
            item.setFileName(pdf.getFileName());
            item.setFilePath(pdf.getFilePath());
            itemList.add(item);
        }
        stReportPackageItemMapper.batchInsertStReportPackageItem(itemList);

        // 4. 执行打包（生成zip文件）
        try
        {
            createMixedZipFile(pkg, pdfList);
            pkg.setStatus(1); // 已生成
            pkg.setErrorMsg(null);
        }
        catch (Exception e)
        {
            log.error("一键打包全部失败：packageId={}", packageId, e);
            pkg.setStatus(2); // 失败
            pkg.setErrorMsg(e.getMessage());
        }

        stReportPackageMapper.updateStReportPackage(pkg);

        return pkg;
    }

    /**
     * 混合打包多个文件包（报表）
     */
    @Override
    @Transactional
    public StReportPackage createMixedPackage(List<String> reportNames, Long yearSemesterId,
                                             Long schoolingPlanId, Long gradeId, Long classId,
                                             String studentName, String beginCreateTime, String endCreateTime,
                                             String packageName)
    {
        if (reportNames == null || reportNames.isEmpty())
        {
            throw new ServiceException("请至少选择一个文件包");
        }

        // 1. 查询所有符合条件的PDF记录（多个报表名称）
        StReportPdf queryParams = new StReportPdf();
        queryParams.setStatus(1); // 只查询正常状态的记录
        queryParams.setYearSemesterId(yearSemesterId);
        queryParams.setSchoolingPlanId(schoolingPlanId);
        queryParams.setGradeId(gradeId);
        queryParams.setClassId(classId);
        queryParams.setStudentName(studentName);

        // 处理时间范围
        if (beginCreateTime != null || endCreateTime != null)
        {
            java.util.Map<String, Object> params = new java.util.HashMap<>();
            if (beginCreateTime != null)
            {
                params.put("beginCreateTime", beginCreateTime);
            }
            if (endCreateTime != null)
            {
                params.put("endCreateTime", endCreateTime);
            }
            queryParams.setParams(params);
        }

        // 查询所有符合条件的PDF
        List<StReportPdf> allPdfList = stReportPdfMapper.selectStReportPdfList(queryParams);

        // 过滤出指定报表名称的PDF
        List<StReportPdf> pdfList = allPdfList.stream()
                .filter(pdf -> reportNames.contains(pdf.getReportName()))
                .collect(java.util.stream.Collectors.toList());

        if (pdfList.isEmpty())
        {
            throw new ServiceException("所选文件包下没有符合条件的PDF文件");
        }

        // 2. 创建打包记录
        StReportPackage pkg = new StReportPackage();
        pkg.setPackageName(StringUtils.isNotBlank(packageName) ? packageName : generateDefaultPackageName());
        pkg.setPackageType("mixed"); // 混合打包
        pkg.setStatus(0); // 待打包
        pkg.setFileCount(pdfList.size());
        pkg.setTotalSize(pdfList.stream().mapToLong(p -> p.getFileSize() != null ? p.getFileSize() : 0L).sum());
        pkg.setCreateBy(SecurityUtils.getUsername());

        // 生成目标目录和zip文件名
        String targetDir = generateTargetDir();
        String zipFileName = generateZipFileName(pkg.getPackageName(), pdfList);
        String zipFilePath = targetDir + "/" + zipFileName;

        pkg.setTargetDir(targetDir);
        pkg.setZipFileName(zipFileName);
        pkg.setZipFilePath(zipFilePath);

        stReportPackageMapper.insertStReportPackage(pkg);
        Long packageId = pkg.getId();

        // 3. 创建打包明细
        List<StReportPackageItem> itemList = new ArrayList<>();
        for (StReportPdf pdf : pdfList)
        {
            StReportPackageItem item = new StReportPackageItem();
            item.setPackageId(packageId);
            item.setReportPdfId(pdf.getId());
            item.setFileName(pdf.getFileName());
            item.setFilePath(pdf.getFilePath());
            itemList.add(item);
        }
        stReportPackageItemMapper.batchInsertStReportPackageItem(itemList);

        // 4. 执行打包（生成zip文件）
        try
        {
            createMixedZipFile(pkg, pdfList);
            pkg.setStatus(1); // 已生成
            pkg.setErrorMsg(null);
        }
        catch (Exception e)
        {
            log.error("混合打包失败：packageId={}", packageId, e);
            pkg.setStatus(2); // 失败
            pkg.setErrorMsg(e.getMessage());
        }

        stReportPackageMapper.updateStReportPackage(pkg);

        return pkg;
    }

    /**
     * 根据打包记录ID查询明细列表
     */
    @Override
    public List<StReportPackageItem> selectPackageItemListByPackageId(Long packageId)
    {
        return stReportPackageItemMapper.selectStReportPackageItemListByPackageId(packageId);
    }

    /**
     * 删除报表打包记录
     */
    @Override
    @Transactional
    public int deleteStReportPackageById(Long id)
    {
        StReportPackage pkg = stReportPackageMapper.selectStReportPackageById(id);
        if (pkg != null && StringUtils.isNotBlank(pkg.getZipFilePath()))
        {
            // 删除物理zip文件
            File zipFile = new File(RuoYiConfig.getProfile() + pkg.getZipFilePath());
            if (zipFile.exists())
            {
                zipFile.delete();
            }
            // 删除目标目录（如果为空）
            File targetDir = new File(RuoYiConfig.getProfile() + pkg.getTargetDir());
            if (targetDir.exists() && targetDir.isDirectory())
            {
                File[] files = targetDir.listFiles();
                if (files == null || files.length == 0)
                {
                    targetDir.delete();
                }
            }
        }
        // 删除明细
        stReportPackageItemMapper.deleteStReportPackageItemByPackageId(id);
        // 删除主记录
        return stReportPackageMapper.deleteStReportPackageById(id);
    }

    /**
     * 批量删除报表打包记录
     */
    @Override
    @Transactional
    public int deleteStReportPackageByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            deleteStReportPackageById(id);
        }
        return ids.length;
    }

    /**
     * 生成混合打包的zip文件（多个报表，每个报表一个文件夹）
     */
    private void createMixedZipFile(StReportPackage pkg, List<StReportPdf> pdfList) throws IOException
    {
        // 混合打包的ZIP生成逻辑与普通打包相同，因为generateZipEntryName已经包含了报表名称作为第一级目录
        createZipFile(pkg, pdfList);
    }

    /**
     * 生成zip文件
     */
    private void createZipFile(StReportPackage pkg, List<StReportPdf> pdfList) throws IOException
    {
        String fullZipPath = RuoYiConfig.getProfile() + pkg.getZipFilePath();
        File zipFile = new File(fullZipPath);
        File parentDir = zipFile.getParentFile();
        if (!parentDir.exists())
        {
            parentDir.mkdirs();
        }

        // 用于跟踪已使用的文件名，避免重复
        java.util.Map<String, Integer> fileNameCounter = new java.util.HashMap<>();

        // 收集所有报表名称，用于查询附件
        java.util.Set<String> reportNames = new java.util.HashSet<>();
        for (StReportPdf pdf : pdfList)
        {
            if (StringUtils.isNotBlank(pdf.getReportName()))
            {
                reportNames.add(pdf.getReportName());
            }
        }

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile)))
        {
            // 1. 打包PDF文件
            for (StReportPdf pdf : pdfList)
            {
                String fullPdfPath = RuoYiConfig.getProfile() + pdf.getFilePath();
                File pdfFile = new File(fullPdfPath);
                if (!pdfFile.exists())
                {
                    log.warn("PDF文件不存在，跳过：{}", fullPdfPath);
                    continue;
                }

                // 生成zip内的文件路径（保持目录结构，便于查找）
                String zipEntryName = generateZipEntryName(pdf, fileNameCounter);
                ZipEntry entry = new ZipEntry(zipEntryName);
                zos.putNextEntry(entry);

                // 复制文件内容到zip
                try (FileInputStream fis = new FileInputStream(pdfFile))
                {
                    IOUtils.copy(fis, zos);
                }

                zos.closeEntry();
            }

            // 2. 打包附件文件
            for (String reportName : reportNames)
            {
                // 查询该报表名称对应的所有附件
                List<StReportAttachment> attachments = stReportAttachmentMapper.selectAttachmentByTypeAndRelatedId("report", reportName);
                if (attachments != null && !attachments.isEmpty())
                {
                    for (StReportAttachment attachment : attachments)
                    {
                        // 附件路径处理：filePath已包含完整相对路径，直接拼接uploadPath即可
                        String attachFilePath = attachment.getFilePath();
                        String fullAttachPath;
                        
                        // 如果路径以/profile开头，需要去掉/profile，因为getProfile()已包含
                        if (attachFilePath.startsWith("/profile/"))
                        {
                            // 去掉/profile前缀，只保留/upload/...部分
                            attachFilePath = attachFilePath.substring(8); // "/profile/".length() = 9，去掉后从/upload开始
                            fullAttachPath = RuoYiConfig.getProfile() + attachFilePath;
                        }
                        else if (attachFilePath.startsWith("/"))
                        {
                            // 路径以/开头但不是/profile/，直接拼接
                            fullAttachPath = RuoYiConfig.getProfile() + attachFilePath;
                        }
                        else
                        {
                            // 相对路径，直接拼接
                            fullAttachPath = RuoYiConfig.getProfile() + "/" + attachFilePath;
                        }
                        
                        File attachFile = new File(fullAttachPath);
                        if (!attachFile.exists())
                        {
                            log.warn("附件文件不存在，跳过：{}", fullAttachPath);
                            continue;
                        }

                        // 生成附件在ZIP中的路径：报表名称/附件/文件名
                        String attachmentFileName = attachment.getOriginalName();
                        if (StringUtils.isBlank(attachmentFileName))
                        {
                            attachmentFileName = attachment.getFileName();
                        }
                        String zipAttachmentPath = reportName + "/附件/" + attachmentFileName;

                        // 检查是否有重名，如果有则添加序号
                        int count = fileNameCounter.getOrDefault(zipAttachmentPath, 0);
                        if (count > 0)
                        {
                            int lastDotIndex = attachmentFileName.lastIndexOf('.');
                            if (lastDotIndex > 0)
                            {
                                String nameWithoutExt = attachmentFileName.substring(0, lastDotIndex);
                                String ext = attachmentFileName.substring(lastDotIndex);
                                zipAttachmentPath = reportName + "/附件/" + nameWithoutExt + "_" + (count + 1) + ext;
                            }
                            else
                            {
                                zipAttachmentPath = reportName + "/附件/" + attachmentFileName + "_" + (count + 1);
                            }
                        }
                        fileNameCounter.put(reportName + "/附件/" + attachmentFileName, count + 1);

                        // 添加附件到ZIP
                        ZipEntry attachEntry = new ZipEntry(zipAttachmentPath);
                        zos.putNextEntry(attachEntry);

                        try (FileInputStream fis = new FileInputStream(attachFile))
                        {
                            IOUtils.copy(fis, zos);
                        }

                        zos.closeEntry();
                        log.info("添加附件到ZIP：{}", zipAttachmentPath);
                    }
                }
            }
        }

        // 更新实际文件大小
        if (zipFile.exists())
        {
            pkg.setTotalSize(zipFile.length());
        }
    }

    /**
     * 生成zip内的文件路径
     * 格式：报表名称/原档案名称.pdf
     * 说明：保持原档案名称，去掉"学期"前缀（如果有）
     */
    private String generateZipEntryName(StReportPdf pdf, java.util.Map<String, Integer> fileNameCounter)
    {
        StringBuilder sb = new StringBuilder();
        
        // 报表名称（第一级目录）
        String reportName;
        if (StringUtils.isNotBlank(pdf.getReportName()))
        {
            reportName = pdf.getReportName();
        }
        else
        {
            reportName = "未命名报表";
        }
        sb.append(reportName).append("/");

        // 获取原档案名称
        String originalFileName = pdf.getFileName();
        if (StringUtils.isBlank(originalFileName))
        {
            originalFileName = "未命名文件.pdf";
        }
        
        // 去掉"学期"前缀（如果文件名以"学期"开头）
        String fileName = originalFileName;
        if (fileName.startsWith("学期"))
        {
            // 去掉"学期"前缀，例如："学期3_xx.pdf" -> "3_xx.pdf"
            fileName = fileName.substring(2);
        }
        
        // 生成基础文件名：报表名称/文件名
        String baseFileName = reportName + "/" + fileName;
        
        // 检查是否有重名，如果有则添加序号
        int count = fileNameCounter.getOrDefault(baseFileName, 0);
        if (count > 0)
        {
            // 有重名，添加序号
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex > 0)
            {
                String nameWithoutExt = fileName.substring(0, lastDotIndex);
                String ext = fileName.substring(lastDotIndex);
                sb.append(nameWithoutExt).append("_").append(count + 1).append(ext);
            }
            else
            {
                sb.append(fileName).append("_").append(count + 1);
            }
        }
        else
        {
            // 无重名，使用原文件名
            sb.append(fileName);
        }
        
        // 更新计数器
        fileNameCounter.put(baseFileName, count + 1);

        return sb.toString();
    }

    /**
     * 根据PDF列表生成打包名称（格式：学年学期_学段资助档案）
     */
    private String generatePackageNameByPdfList(List<StReportPdf> pdfList)
    {
        if (pdfList == null || pdfList.isEmpty())
        {
            return generateDefaultPackageName();
        }

        // 获取当前学年学期
        String yearSemesterName = "";
        try
        {
            StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
            if (currentSemester != null)
            {
                String schoolYear = currentSemester.getSchoolYear() != null ? currentSemester.getSchoolYear() : "";
                Integer semester = currentSemester.getSemester();
                String semesterLabel = SemesterUtils.getSemesterLabel(semester);
                if (semesterLabel != null)
                {
                    yearSemesterName = schoolYear + semesterLabel;
                }
                else
                {
                    yearSemesterName = schoolYear + "学期";
                }
            }
        }
        catch (Exception e)
        {
            log.warn("获取当前学年学期失败：{}", e.getMessage());
        }

        // 获取学段信息（从PDF记录中获取，如果有多条记录，使用第一条的学段）
        String schoolingPlanName = "";
        if (pdfList != null && !pdfList.isEmpty())
        {
            Long schoolingPlanId = pdfList.get(0).getSchoolingPlanId();
            if (schoolingPlanId != null)
            {
                // 根据学段ID获取学段名称（1=小学，2=初中，3=高中）
                switch (schoolingPlanId.intValue())
                {
                    case 1:
                        schoolingPlanName = "小学";
                        break;
                    case 2:
                        schoolingPlanName = "初中";
                        break;
                    case 3:
                        schoolingPlanName = "高中";
                        break;
                    default:
                        schoolingPlanName = "";
                        break;
                }
            }
        }

        // 构建打包名称：学年学期_学段资助档案
        StringBuilder nameBuilder = new StringBuilder();
        
        // 学年学期
        if (StringUtils.isNotBlank(yearSemesterName))
        {
            nameBuilder.append(yearSemesterName);
        }
        else
        {
            nameBuilder.append("未知学期");
        }
        
        // 下划线和学段
        nameBuilder.append("_");
        if (StringUtils.isNotBlank(schoolingPlanName))
        {
            nameBuilder.append(schoolingPlanName).append("资助档案");
        }
        else
        {
            nameBuilder.append("资助档案");
        }

        return nameBuilder.toString();
    }

    /**
     * 预览打包信息（不实际打包，仅返回统计信息）
     */
    @Override
    public java.util.Map<String, Object> previewPackage(List<Long> pdfIds)
    {
        if (pdfIds == null || pdfIds.isEmpty())
        {
            throw new ServiceException("请至少选择一个PDF文件");
        }

        // 1. 查询PDF记录
        List<StReportPdf> pdfList = stReportPdfMapper.selectStReportPdfByIds(pdfIds);
        if (pdfList == null || pdfList.isEmpty())
        {
            throw new ServiceException("未找到有效的PDF文件");
        }

        // 过滤掉已删除的记录
        pdfList = pdfList.stream()
                .filter(pdf -> pdf.getStatus() != null && pdf.getStatus() == 1)
                .collect(java.util.stream.Collectors.toList());

        if (pdfList.isEmpty())
        {
            throw new ServiceException("所选PDF文件均为已删除状态");
        }

        // 2. 统计信息
        int fileCount = pdfList.size();
        long totalSize = pdfList.stream().mapToLong(p -> p.getFileSize() != null ? p.getFileSize() : 0L).sum();

        // 统计不同报表的数量
        java.util.Map<String, Integer> reportCounts = new java.util.HashMap<>();
        for (StReportPdf pdf : pdfList)
        {
            String reportName = StringUtils.isNotBlank(pdf.getReportName()) ? pdf.getReportName() : "未命名报表";
            reportCounts.put(reportName, reportCounts.getOrDefault(reportName, 0) + 1);
        }

        java.util.List<String> reportNames = new java.util.ArrayList<>(reportCounts.keySet());
        String suggestedPackageName = generatePackageNameByPdfList(pdfList);

        // 3. 构建返回结果
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("fileCount", fileCount);
        result.put("totalSize", totalSize);
        result.put("reportCounts", reportCounts);
        result.put("reportNames", reportNames);
        result.put("suggestedPackageName", suggestedPackageName);
        result.put("selectedRecords", pdfList); // 返回选中的记录详情

        return result;
    }

    /**
     * 生成默认打包名称
     */
    private String generateDefaultPackageName()
    {
        return "档案打包_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    /**
     * 生成目标目录路径
     */
    private String generateTargetDir()
    {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        return "/report-packages/" + dateStr;
    }

    /**
     * 生成zip文件名
     * 格式：学年学期_学段资助档案_时间戳.zip（如果未提供packageName）
     * 或：自定义名称_时间戳.zip（如果提供了packageName）
     * 说明：获取当前学年学期和学段信息，生成默认文件名
     */
    private String generateZipFileName(String packageName, List<StReportPdf> pdfList)
    {
        // 如果提供了自定义名称，使用自定义名称
        if (StringUtils.isNotBlank(packageName))
        {
            String safeName = packageName.replaceAll("[\\\\/:*?\"<>|]", "_");
            String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            return safeName + "_" + timeStr + ".zip";
        }
        
        // 否则，使用默认格式：学年学期_学段资助档案_时间戳.zip
        try
        {
            // 获取当前学年学期
            StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
            String yearSemesterName = "未知学期";
            if (currentSemester != null)
            {
                String schoolYear = currentSemester.getSchoolYear() != null ? currentSemester.getSchoolYear() : "";
                Integer semester = currentSemester.getSemester();
                String semesterLabel = SemesterUtils.getSemesterLabel(semester);
                if (semesterLabel != null)
                {
                    yearSemesterName = schoolYear + semesterLabel;
                }
                else
                {
                    yearSemesterName = schoolYear + "学期";
                }
            }
            
            // 获取学段信息（从PDF记录中获取，如果有多条记录，使用第一条的学段）
            String schoolingPlanName = "资助档案";
            if (pdfList != null && !pdfList.isEmpty())
            {
                // 从第一条记录中获取学段ID，然后查询学段名称
                Long schoolingPlanId = pdfList.get(0).getSchoolingPlanId();
                if (schoolingPlanId != null)
                {
                    // 根据学段ID获取学段名称（1=小学，2=初中，3=高中）
                    switch (schoolingPlanId.intValue())
                    {
                        case 1:
                            schoolingPlanName = "小学资助档案";
                            break;
                        case 2:
                            schoolingPlanName = "初中资助档案";
                            break;
                        case 3:
                            schoolingPlanName = "高中资助档案";
                            break;
                        default:
                            schoolingPlanName = "资助档案";
                            break;
                    }
                }
            }
            
            String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            return yearSemesterName + "_" + schoolingPlanName + "_" + timeStr + ".zip";
        }
        catch (Exception e)
        {
            log.warn("生成zip文件名时出错，使用默认名称：{}", e.getMessage());
            String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            return "档案打包_" + timeStr + ".zip";
        }
    }

    /**
     * 混合打包（报表档案 + 自定义档案包）
     */
    @Override
    @Transactional
    public StReportPackage createHybridPackage(List<String> reportIdentifiers, boolean useReportId,
                                                List<String> customPackageCodes, String packageName)
    {
        log.info("开始混合打包: reportIdentifiers={}, useReportId={}, customPackageCodes={}", 
                 reportIdentifiers, useReportId, customPackageCodes);

        // 验证参数
        if ((reportIdentifiers == null || reportIdentifiers.isEmpty()) && (customPackageCodes == null || customPackageCodes.isEmpty()))
        {
            throw new ServiceException("请至少选择一个报表档案或自定义档案包");
        }

        List<StReportPdf> pdfList = new ArrayList<>();
        List<HybridPackageSource> sources = new ArrayList<>();

        // 1. 查询报表档案PDF
        if (reportIdentifiers != null && !reportIdentifiers.isEmpty())
        {
            for (String identifier : reportIdentifiers)
            {
                StReportPdf queryParam = new StReportPdf();
                if (useReportId)
                {
                    // 按reportId查询（推荐，自动包含历史改名的所有归档）
                    queryParam.setReportId(identifier);
                }
                else
                {
                    // 按reportName查询（兼容旧逻辑）
                    queryParam.setReportName(identifier);
                }
                queryParam.setStatus(1);
                List<StReportPdf> pdfs = stReportPdfMapper.selectStReportPdfList(queryParam);
                if (pdfs != null && !pdfs.isEmpty())
                {
                    pdfList.addAll(pdfs);
                    sources.add(new HybridPackageSource(
                        "report", 
                        identifier, 
                        pdfs.size()
                    ));
                }
            }
        }

        // 2. 查询自定义档案包（暂存编码，后续打包时使用）
        List<String> validCustomPackageCodes = new ArrayList<>();
        if (customPackageCodes != null && !customPackageCodes.isEmpty())
        {
            validCustomPackageCodes.addAll(customPackageCodes);
            for (String code : customPackageCodes)
            {
                sources.add(new HybridPackageSource("custom", code, 0));
            }
        }

        if (pdfList.isEmpty() && validCustomPackageCodes.isEmpty())
        {
            throw new ServiceException("没有找到有效的文件");
        }

        // 3. 生成打包名称
        if (StringUtils.isBlank(packageName))
        {
            packageName = generateHybridPackageName(reportIdentifiers, customPackageCodes);
        }

        // 4. 创建打包记录
        StReportPackage pkg = new StReportPackage();
        pkg.setPackageName(packageName);
        pkg.setPackageType("hybrid"); // 混合打包
        pkg.setStatus(0); // 待打包
        pkg.setFileCount(pdfList.size());
        pkg.setTotalSize(pdfList.stream().mapToLong(p -> p.getFileSize() != null ? p.getFileSize() : 0L).sum());
        pkg.setCreateBy(SecurityUtils.getUsername());

        // 生成目标目录和zip文件名
        String targetDir = generateTargetDir();
        String zipFileName = generateZipFileName(packageName, pdfList);
        String zipFilePath = targetDir + "/" + zipFileName;

        pkg.setTargetDir(targetDir);
        pkg.setZipFileName(zipFileName);
        pkg.setZipFilePath(zipFilePath);

        stReportPackageMapper.insertStReportPackage(pkg);
        Long packageId = pkg.getId();

        // 5. 创建打包明细（仅针对PDF）
        if (!pdfList.isEmpty())
        {
            List<StReportPackageItem> itemList = new ArrayList<>();
            for (StReportPdf pdf : pdfList)
            {
                StReportPackageItem item = new StReportPackageItem();
                item.setPackageId(packageId);
                item.setReportPdfId(pdf.getId());
                item.setFileName(pdf.getFileName());
                item.setFilePath(pdf.getFilePath());
                itemList.add(item);
            }
            stReportPackageItemMapper.batchInsertStReportPackageItem(itemList);
        }

        // 6. 执行混合打包
        try
        {
            createHybridZipFile(pkg, pdfList, validCustomPackageCodes);
            pkg.setStatus(1); // 已生成
            pkg.setErrorMsg(null);
            log.info("混合打包成功: packageId={}, packageName={}", packageId, packageName);
        }
        catch (Exception e)
        {
            log.error("混合打包失败：packageId={}", packageId, e);
            pkg.setStatus(2); // 失败
            pkg.setErrorMsg(e.getMessage());
        }

        stReportPackageMapper.updateStReportPackage(pkg);

        return pkg;
    }

    /**
     * 创建混合ZIP文件（报表PDF + 自定义档案包附件）
     */
    private void createHybridZipFile(StReportPackage pkg, List<StReportPdf> pdfList, List<String> customPackageCodes) throws IOException
    {
        // 构建完整的文件路径
        String zipFilePath = RuoYiConfig.getProfile() + pkg.getZipFilePath();
        File zipFile = new File(zipFilePath);
        File targetDirFile = zipFile.getParentFile();
        if (!targetDirFile.exists())
        {
            targetDirFile.mkdirs();
        }

        // 收集所有报表名称，用于查询附件
        java.util.Set<String> reportNames = new java.util.HashSet<>();
        for (StReportPdf pdf : pdfList)
        {
            if (StringUtils.isNotBlank(pdf.getReportName()))
            {
                reportNames.add(pdf.getReportName());
            }
        }

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile)))
        {
            // 设置注释
            zos.setComment("混合打包 - 包含报表档案和自定义档案包");

            // 1. 打包报表PDF文件
            log.info("开始打包报表PDF，总数：{}", pdfList.size());
            for (StReportPdf pdf : pdfList)
            {
                String reportName = pdf.getReportName();
                if (StringUtils.isBlank(reportName))
                {
                    reportName = "未命名报表";
                }

                String pdfPath = RuoYiConfig.getProfile() + pdf.getFilePath();
                File pdfFile = new File(pdfPath);
                if (!pdfFile.exists())
                {
                    log.warn("PDF文件不存在，跳过：{}", pdfPath);
                    continue;
                }

                // PDF在ZIP中的路径：报表名称/PDF文件名
                String zipEntryName = reportName + "/" + pdf.getFileName();
                ZipEntry entry = new ZipEntry(zipEntryName);
                zos.putNextEntry(entry);
                try (FileInputStream fis = new FileInputStream(pdfFile))
                {
                    IOUtils.copy(fis, zos);
                }
                zos.closeEntry();
            }

            // 2. 打包报表附件
            log.info("开始打包报表附件，报表数量：{}", reportNames.size());
            for (String reportName : reportNames)
            {
                List<StReportAttachment> attachments = stReportAttachmentMapper.selectAttachmentByTypeAndRelatedId("report", reportName);
                if (attachments != null && !attachments.isEmpty())
                {
                    for (StReportAttachment attachment : attachments)
                    {
                        String attachFilePath = attachment.getFilePath();
                        String fullAttachPath;

                        if (attachFilePath.startsWith("/profile/"))
                        {
                            attachFilePath = attachFilePath.substring(8);
                            fullAttachPath = RuoYiConfig.getProfile() + attachFilePath;
                        }
                        else if (attachFilePath.startsWith("/"))
                        {
                            fullAttachPath = RuoYiConfig.getProfile() + attachFilePath;
                        }
                        else
                        {
                            fullAttachPath = RuoYiConfig.getProfile() + "/" + attachFilePath;
                        }

                        File attachFile = new File(fullAttachPath);
                        if (!attachFile.exists())
                        {
                            log.warn("附件文件不存在，跳过：{}", fullAttachPath);
                            continue;
                        }

                        String attachmentFileName = attachment.getOriginalName() != null ? attachment.getOriginalName() : attachment.getFileName();
                        String zipAttachmentPath = reportName + "/附件/" + attachmentFileName;

                        ZipEntry attachEntry = new ZipEntry(zipAttachmentPath);
                        zos.putNextEntry(attachEntry);
                        try (FileInputStream fis = new FileInputStream(attachFile))
                        {
                            IOUtils.copy(fis, zos);
                        }
                        zos.closeEntry();
                    }
                }
            }

            // 3. 打包自定义档案包附件
            log.info("开始打包自定义档案包，数量：{}", customPackageCodes.size());
            for (String packageCode : customPackageCodes)
            {
                List<StReportAttachment> customAttachments = stReportAttachmentMapper.selectAttachmentByTypeAndRelatedId("custom", packageCode);
                if (customAttachments != null && !customAttachments.isEmpty())
                {
                    for (StReportAttachment attachment : customAttachments)
                    {
                        String attachFilePath = attachment.getFilePath();
                        String fullAttachPath;

                        if (attachFilePath.startsWith("/profile/"))
                        {
                            attachFilePath = attachFilePath.substring(8);
                            fullAttachPath = RuoYiConfig.getProfile() + attachFilePath;
                        }
                        else if (attachFilePath.startsWith("/"))
                        {
                            fullAttachPath = RuoYiConfig.getProfile() + attachFilePath;
                        }
                        else
                        {
                            fullAttachPath = RuoYiConfig.getProfile() + "/" + attachFilePath;
                        }

                        File attachFile = new File(fullAttachPath);
                        if (!attachFile.exists())
                        {
                            log.warn("自定义档案包附件不存在，跳过：{}", fullAttachPath);
                            continue;
                        }

                        String attachmentFileName = attachment.getOriginalName() != null ? attachment.getOriginalName() : attachment.getFileName();
                        // 自定义档案包的附件放在独立目录
                        String zipAttachmentPath = "自定义档案包_" + packageCode + "/" + attachmentFileName;

                        ZipEntry attachEntry = new ZipEntry(zipAttachmentPath);
                        zos.putNextEntry(attachEntry);
                        try (FileInputStream fis = new FileInputStream(attachFile))
                        {
                            IOUtils.copy(fis, zos);
                        }
                        zos.closeEntry();
                    }
                }
            }

            log.info("混合打包ZIP文件生成成功：{}", zipFilePath);
        }
    }

    /**
     * 生成混合打包名称
     */
    private String generateHybridPackageName(List<String> reportNames, List<String> customPackageCodes)
    {
        try
        {
            // 获取当前学期信息
            String yearSemesterName = "未知学期";
            StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
            if (currentSemester != null)
            {
                String schoolYear = currentSemester.getSchoolYear() != null ? currentSemester.getSchoolYear() : "";
                Integer semester = currentSemester.getSemester();
                String semesterLabel = SemesterUtils.getSemesterLabel(semester);
                if (semesterLabel != null)
                {
                    yearSemesterName = schoolYear + semesterLabel;
                }
                else
                {
                    yearSemesterName = schoolYear + "学期";
                }
            }
            
            // 获取学段信息（从报表PDF或自定义档案包中获取）
            String schoolingPlanName = "高中资助档案";
            
            // 尝试从报表PDF中获取学段
            if (reportNames != null && !reportNames.isEmpty())
            {
                for (String reportName : reportNames)
                {
                    StReportPdf queryPdf = new StReportPdf();
                    queryPdf.setReportName(reportName);
                    List<StReportPdf> pdfList = stReportPdfMapper.selectStReportPdfList(queryPdf);
                    if (pdfList != null && !pdfList.isEmpty())
                    {
                        Long schoolingPlanId = pdfList.get(0).getSchoolingPlanId();
                        if (schoolingPlanId != null)
                        {
                            switch (schoolingPlanId.intValue())
                            {
                                case 1:
                                    schoolingPlanName = "小学资助档案";
                                    break;
                                case 2:
                                    schoolingPlanName = "初中资助档案";
                                    break;
                                case 3:
                                    schoolingPlanName = "高中资助档案";
                                    break;
                                default:
                                    schoolingPlanName = "资助档案";
                                    break;
                            }
                            break;
                        }
                    }
                }
            }
            
            return yearSemesterName + "_" + schoolingPlanName;
        }
        catch (Exception e)
        {
            log.warn("生成混合打包名称时出错，使用默认名称：{}", e.getMessage());
            String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            return "混合打包_" + timeStr;
        }
    }

    /**
     * 混合打包源信息
     */
    private static class HybridPackageSource
    {
        private String type; // report / custom
        private String identifier; // reportName / packageCode
        private int fileCount;

        public HybridPackageSource(String type, String identifier, int fileCount)
        {
            this.type = type;
            this.identifier = identifier;
            this.fileCount = fileCount;
        }
    }
}

