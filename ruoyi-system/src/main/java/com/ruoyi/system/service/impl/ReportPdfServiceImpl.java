package com.ruoyi.system.service.impl;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.system.domain.StReportPdf;
import com.ruoyi.system.domain.StReportPdfBatch;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.domain.StStudentsBase;
import com.ruoyi.system.mapper.StReportPdfBatchMapper;
import com.ruoyi.system.mapper.StReportPdfMapper;
import com.ruoyi.system.mapper.StSchoolYearSemesterMapper;
import com.ruoyi.system.mapper.StStudentsBaseMapper;
import com.ruoyi.system.service.IReportPdfService;

/**
 * 报表PDF服务实现类
 * 
 * @author ruoyi
 * @date 2025-12-15
 */
@Service
public class ReportPdfServiceImpl implements IReportPdfService
{
    private static final Logger log = LoggerFactory.getLogger(ReportPdfServiceImpl.class);

    @Autowired
    private StReportPdfMapper stReportPdfMapper;

    @Autowired
    private StReportPdfBatchMapper stReportPdfBatchMapper;

    @Autowired
    private StStudentsBaseMapper stStudentsBaseMapper;

    @Autowired
    private StSchoolYearSemesterMapper stSchoolYearSemesterMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * 查询报表PDF记录
     */
    @Override
    public StReportPdf selectStReportPdfById(Long id)
    {
        return stReportPdfMapper.selectStReportPdfById(id);
    }

    /**
     * 查询报表PDF记录列表
     */
    @Override
    public List<StReportPdf> selectStReportPdfList(StReportPdf stReportPdf)
    {
        List<StReportPdf> list = stReportPdfMapper.selectStReportPdfList(stReportPdf);

        // 使用学期工具类为每条记录补充“学年学期名称”（如：2024-2025春季学期）
        if (list != null && !list.isEmpty())
        {
            // 收集所有非空的学年学期ID
            Set<Long> yearSemesterIds = list.stream()
                    .map(StReportPdf::getYearSemesterId)
                    .filter(id -> id != null && id > 0)
                    .collect(Collectors.toSet());

            if (!yearSemesterIds.isEmpty())
            {
                Map<Long, StSchoolYearSemester> semesterMap = new HashMap<>();
                for (Long id : yearSemesterIds)
                {
                    StSchoolYearSemester ys = stSchoolYearSemesterMapper.selectStSchoolYearSemesterById(id);
                    if (ys != null)
                    {
                        semesterMap.put(id, ys);
                    }
                }

                list.forEach(pdf -> {
                    Long ysId = pdf.getYearSemesterId();
                    if (ysId != null)
                    {
                        StSchoolYearSemester ys = semesterMap.get(ysId);
                        if (ys != null)
                        {
                            String schoolYear = ys.getSchoolYear() != null ? ys.getSchoolYear() : "";
                            Integer semester = ys.getSemester();
                            String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(semester);
                            if (semesterLabel == null)
                            {
                                semesterLabel = "";
                            }
                            pdf.setYearSemesterName(schoolYear + semesterLabel);
                        }
                    }
                });
            }
        }

        // 合并记录补充学生姓名列表
        if (list != null && !list.isEmpty())
        {
            // 收集合并来源ID
            Set<Long> mergedSourceIds = new HashSet<>();
            Map<Long, List<Long>> mergedIdMap = new HashMap<>();
            list.forEach(pdf -> {
                if (pdf.getIsMerged() != null && pdf.getIsMerged() == 1
                        && StringUtils.isNotEmpty(pdf.getMergedFromIds()))
                {
                    try
                    {
                        List<Long> ids = JSON.parseArray(pdf.getMergedFromIds(), Long.class);
                        if (ids != null && !ids.isEmpty())
                        {
                            mergedIdMap.put(pdf.getId(), ids);
                            mergedSourceIds.addAll(ids);
                        }
                    }
                    catch (Exception ignored)
                    {
                        // 解析失败忽略
                    }
                }
            });

            if (!mergedSourceIds.isEmpty())
            {
                List<StReportPdf> sourceList = stReportPdfMapper.selectStReportPdfByIds(new ArrayList<>(mergedSourceIds));
                Map<Long, String> idNameMap = new HashMap<>();
                for (StReportPdf pdf : sourceList)
                {
                    idNameMap.put(pdf.getId(), pdf.getStudentName());
                }

                list.forEach(pdf -> {
                    if (pdf.getIsMerged() != null && pdf.getIsMerged() == 1)
                    {
                        List<Long> ids = mergedIdMap.get(pdf.getId());
                        if (ids != null && !ids.isEmpty())
                        {
                            List<String> names = ids.stream()
                                    .map(idNameMap::get)
                                    .filter(StringUtils::isNotEmpty)
                                    .collect(Collectors.toList());
                            if (!names.isEmpty())
                            {
                                pdf.setMergedStudentNames(String.join("，", names));
                            }
                        }
                    }
                });
            }
        }

        return list;
    }

    /**
     * 查询文件包列表（按reportId分组）
     */
    @Override
    public List<Map<String, Object>> selectPackageList(StReportPdf stReportPdf)
    {
        // 构建基础SQL，按reportId分组，统计数量、大小、最新时间、最新名称
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  report_id AS reportId, ");
        sql.append("  (SELECT report_name FROM st_report_pdf WHERE report_id = t.report_id AND status = 1 ORDER BY create_time DESC LIMIT 1) AS reportName, ");
        sql.append("  COUNT(*) AS count, ");
        sql.append("  COALESCE(SUM(file_size), 0) AS totalSize, ");
        sql.append("  MAX(create_time) AS latestTime, ");
        sql.append("  (CASE WHEN COUNT(DISTINCT report_name) > 1 THEN 1 ELSE 0 END) AS hasMultipleNames ");
        sql.append("FROM st_report_pdf t ");
        sql.append("WHERE status = 1 ");
        
        List<Object> params = new ArrayList<>();
        
        // 添加查询条件
        if (StringUtils.isNotBlank(stReportPdf.getReportName()))
        {
            sql.append("AND report_name LIKE ? ");
            params.add("%" + stReportPdf.getReportName() + "%");
        }
        if (stReportPdf.getYearSemesterId() != null)
        {
            sql.append("AND year_semester_id = ? ");
            params.add(stReportPdf.getYearSemesterId());
        }
        if (stReportPdf.getSchoolingPlanId() != null)
        {
            sql.append("AND schooling_plan_id = ? ");
            params.add(stReportPdf.getSchoolingPlanId());
        }
        if (stReportPdf.getGradeId() != null)
        {
            sql.append("AND grade_id = ? ");
            params.add(stReportPdf.getGradeId());
        }
        if (stReportPdf.getClassId() != null)
        {
            sql.append("AND class_id = ? ");
            params.add(stReportPdf.getClassId());
        }
        if (StringUtils.isNotBlank(stReportPdf.getStudentName()))
        {
            sql.append("AND student_name LIKE ? ");
            params.add("%" + stReportPdf.getStudentName() + "%");
        }
        
        // 按reportId分组并按最新时间排序
        sql.append("GROUP BY report_id ");
        sql.append("ORDER BY latestTime DESC");
        
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    /**
     * 查询可用报表列表（用于合并PDF）
     */
    @Override
    public List<Map<String, Object>> selectAvailableReports(StReportPdf stReportPdf)
    {
        // 构建基础SQL，统计每个报表的PDF数量
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  report_id AS reportId, ");
        sql.append("  (SELECT report_name FROM st_report_pdf WHERE report_id = t.report_id AND status = 1 ORDER BY create_time DESC LIMIT 1) AS reportName, ");
        sql.append("  COUNT(*) AS pdfCount ");
        sql.append("FROM st_report_pdf t ");
        sql.append("WHERE status = 1 AND is_merged = 0 ");
        
        List<Object> params = new ArrayList<>();
        
        // 添加查询条件
        if (stReportPdf.getYearSemesterId() != null)
        {
            sql.append("AND year_semester_id = ? ");
            params.add(stReportPdf.getYearSemesterId());
        }
        if (stReportPdf.getSchoolingPlanId() != null)
        {
            sql.append("AND schooling_plan_id = ? ");
            params.add(stReportPdf.getSchoolingPlanId());
        }
        if (stReportPdf.getGradeId() != null)
        {
            sql.append("AND grade_id = ? ");
            params.add(stReportPdf.getGradeId());
        }
        if (stReportPdf.getClassId() != null)
        {
            sql.append("AND class_id = ? ");
            params.add(stReportPdf.getClassId());
        }
        
        // 按reportId分组，只返回pdfCount>0的报表
        sql.append("GROUP BY report_id ");
        sql.append("HAVING pdfCount > 0 ");
        sql.append("ORDER BY reportName");
        
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    /**
     * 生成并保存单个学生报表PDF
     * @param studentId 学生ID（统计报表可为null）
     * @param reportId 报表ID
     * @param yearSemesterId 学年学期ID
     * @return PDF记录
     */
    @Override
    @Transactional
    public StReportPdf generateAndSavePdf(Long studentId, String reportId, Long yearSemesterId)
    {
        try
        {
            // 1. 查询学生信息（统计报表可为null）
            StStudentsBase student = null;
            if (studentId != null)
            {
                student = stStudentsBaseMapper.selectStStudentsBaseById(studentId);
                if (student == null)
                {
                    throw new ServiceException("学生不存在");
                }
            }

            // 2. 如果yearSemesterId为null，从学生信息或当前学期获取
            if (yearSemesterId == null)
            {
                if (student != null)
                {
                    // 优先使用学生的当前学年学期ID
                    yearSemesterId = student.getCurrentYearSemesterId();
                }
                
                // 如果学生也没有，则使用系统当前学期
                if (yearSemesterId == null)
                {
                    StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
                    if (currentSemester != null)
                    {
                        yearSemesterId = currentSemester.getId();
                    }
                }
            }

            // 3. 查询报表名称（从积木报表表）
            String reportName = getReportName(reportId);

            // 4. 查询学年学期信息
            StSchoolYearSemester yearSemester = null;
            if (yearSemesterId != null)
            {
                yearSemester = stSchoolYearSemesterMapper.selectStSchoolYearSemesterById(yearSemesterId);
            }

            // 5. 生成PDF文件
            String filePath = generatePdfFile(studentId, reportId, yearSemesterId, student, reportName, yearSemester);

            // 6. 获取文件大小
            File file = new File(RuoYiConfig.getProfile() + filePath);
            long fileSize = file.exists() ? file.length() : 0;

            // 7. 保存数据库记录（包含学生信息快照，避免学生被删除或信息变更后无法查询历史档案）
            StReportPdf pdfRecord = new StReportPdf();
            pdfRecord.setStudentId(studentId);
            if (student != null)
            {
                pdfRecord.setStudentName(student.getName());
                pdfRecord.setStudentNo(student.getStudentNo());
                pdfRecord.setSchoolingPlanId(student.getSchoolingPlanId());
                pdfRecord.setGradeId(student.getGradeId());
                pdfRecord.setGradeName(student.getGradeName());
                pdfRecord.setClassId(student.getClassId());
                pdfRecord.setClassName(student.getClassName());
            }
            else
            {
                // 统计报表：使用报表名称作为标识
                pdfRecord.setStudentName("统计报表");
                pdfRecord.setStudentNo("");
            }
            pdfRecord.setReportId(reportId);
            pdfRecord.setReportName(reportName);
            pdfRecord.setFileName(file.getName());
            pdfRecord.setFilePath(filePath);
            pdfRecord.setFileSize(fileSize);
            pdfRecord.setYearSemesterId(yearSemesterId);
            pdfRecord.setIsMerged(0);
            pdfRecord.setStatus(1);
            pdfRecord.setCreateBy(SecurityUtils.getUsername());

            stReportPdfMapper.insertStReportPdf(pdfRecord);

            log.info("PDF生成并保存成功：studentId={}, reportId={}, filePath={}", studentId, reportId, filePath);
            return pdfRecord;
        }
        catch (Exception e)
        {
            log.error("PDF生成并保存失败：studentId={}, reportId={}", studentId, reportId, e);
            throw new ServiceException("PDF生成失败：" + e.getMessage());
        }
    }

    /**
     * 批量生成并保存学生报表PDF
     */
    @Override
    @Transactional
    public StReportPdfBatch batchGenerateAndSavePdf(List<Long> studentIds,
                                                   String reportId,
                                                   Long yearSemesterId,
                                                   String batchName,
                                                   Long schoolingPlanId,
                                                   String studentName,
                                                   Long gradeId,
                                                   Long classId)
    {
        String batchId = UUID.randomUUID().toString().replace("-", "");
        List<Long> successIds = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        try
        {
            // 若未显式指定学生ID，则按筛选条件查询
            List<Long> targetStudentIds = new ArrayList<>();
            if (studentIds != null && !studentIds.isEmpty())
            {
                // 去重：使用 LinkedHashSet 保持顺序并去重
                targetStudentIds.addAll(new LinkedHashSet<>(studentIds));
            }
            else
            {
                StStudentsBase query = new StStudentsBase();
                if (StringUtils.isNotBlank(studentName))
                {
                    query.setName(studentName);
                }
                query.setSchoolingPlanId(schoolingPlanId);
                query.setGradeId(gradeId);
                query.setClassId(classId);

                List<StStudentsBase> students = stStudentsBaseMapper.selectStStudentsBaseList(query);
                if (students != null)
                {
                    // 使用 LinkedHashSet 去重
                    Set<Long> studentIdSet = new LinkedHashSet<>();
                    for (StStudentsBase s : students)
                    {
                        if (s.getId() != null)
                        {
                            studentIdSet.add(s.getId());
                        }
                    }
                    targetStudentIds.addAll(studentIdSet);
                }
            }

            if (targetStudentIds.isEmpty())
            {
                throw new ServiceException("未找到符合条件的学生，无法批量生成");
            }

            // 1. 批量生成PDF
            for (Long studentId : targetStudentIds)
            {
                try
                {
                    StReportPdf pdfRecord = generateAndSavePdf(studentId, reportId, yearSemesterId);
                    pdfRecord.setBatchId(batchId);
                    stReportPdfMapper.updateStReportPdf(pdfRecord);
                    successIds.add(pdfRecord.getId());
                }
                catch (Exception e)
                {
                    log.error("批量生成PDF失败：studentId={}", studentId, e);
                    errorMessages.add("学生ID " + studentId + "：" + e.getMessage());
                }
            }

            // 2. 保存批次记录
            StReportPdfBatch batch = new StReportPdfBatch();
            batch.setBatchId(batchId);
            batch.setBatchName(batchName != null ? batchName : "批量生成PDF_" + LocalDate.now());
            batch.setMergedFileName(""); // 批量生成时还没有合并文件，使用空字符串
            batch.setMergedFilePath(""); // 批量生成时还没有合并文件，使用空字符串
            batch.setMergedFileSize(0L);
            batch.setStudentCount(successIds.size());
            batch.setYearSemesterId(yearSemesterId);
            batch.setStatus(1);
            batch.setCreateBy(SecurityUtils.getUsername());

            stReportPdfBatchMapper.insertStReportPdfBatch(batch);

            log.info("批量生成PDF完成：batchId={}, successCount={}, failCount={}", 
                batchId, successIds.size(), errorMessages.size());

            if (!errorMessages.isEmpty())
            {
                throw new ServiceException("部分PDF生成失败：" + String.join("; ", errorMessages));
            }

            return batch;
        }
        catch (Exception e)
        {
            log.error("批量生成PDF失败：batchId={}", batchId, e);
            throw new ServiceException("批量生成PDF失败：" + e.getMessage());
        }
    }

    /**
     * 合并PDF文件
     */
    @Override
    @Transactional
    public StReportPdfBatch mergePdfs(List<Long> pdfIds, String mergedFileName)
    {
        try
        {
            // 1. 查询PDF记录
            List<StReportPdf> pdfList = new ArrayList<>();
            for (Long pdfId : pdfIds)
            {
                StReportPdf pdf = stReportPdfMapper.selectStReportPdfById(pdfId);
                if (pdf == null || pdf.getStatus() == 0)
                {
                    throw new ServiceException("PDF记录不存在或已删除：ID=" + pdfId);
                }
                pdfList.add(pdf);
            }

            if (pdfList.isEmpty())
            {
                throw new ServiceException("没有可合并的PDF文件");
            }

            // 2. 合并PDF文件
            String mergedFilePath = mergePdfFiles(pdfList, mergedFileName);

            // 3. 获取合并后的文件大小
            File mergedFile = new File(RuoYiConfig.getProfile() + mergedFilePath);
            long mergedFileSize = mergedFile.exists() ? mergedFile.length() : 0;

            // 4. 保存合并批次记录
            String batchId = UUID.randomUUID().toString().replace("-", "");
            StReportPdfBatch batch = new StReportPdfBatch();
            batch.setBatchId(batchId);
            batch.setBatchName(mergedFileName);
            batch.setMergedFileName(mergedFile.getName());
            batch.setMergedFilePath(mergedFilePath);
            batch.setMergedFileSize(mergedFileSize);
            batch.setStudentCount(pdfList.size());
            batch.setYearSemesterId(pdfList.get(0).getYearSemesterId());
            batch.setSchoolingPlanId(pdfList.get(0).getSchoolingPlanId());
            batch.setStatus(1);
            batch.setCreateBy(SecurityUtils.getUsername());
            batch.setFilterConditions(JSON.toJSONString(pdfIds));

            stReportPdfBatchMapper.insertStReportPdfBatch(batch);

            // 5. 创建合并后的PDF记录
            StReportPdf mergedPdf = new StReportPdf();
            mergedPdf.setStudentId(null);
            mergedPdf.setReportId(pdfList.get(0).getReportId());
            mergedPdf.setReportName(pdfList.get(0).getReportName());
            mergedPdf.setFileName(mergedFile.getName());
            mergedPdf.setFilePath(mergedFilePath);
            mergedPdf.setFileSize(mergedFileSize);
            mergedPdf.setYearSemesterId(pdfList.get(0).getYearSemesterId());
            mergedPdf.setSchoolingPlanId(pdfList.get(0).getSchoolingPlanId());
            mergedPdf.setBatchId(batchId);
            mergedPdf.setIsMerged(1);
            mergedPdf.setMergedFromIds(JSON.toJSONString(pdfIds));
            mergedPdf.setStatus(1);
            mergedPdf.setCreateBy(SecurityUtils.getUsername());

            stReportPdfMapper.insertStReportPdf(mergedPdf);

            log.info("PDF合并成功：batchId={}, mergedFilePath={}", batchId, mergedFilePath);
            return batch;
        }
        catch (Exception e)
        {
            log.error("PDF合并失败", e);
            throw new ServiceException("PDF合并失败：" + e.getMessage());
        }
    }

    /**
     * 按条件批量合并PDF
     */
    @Override
    @Transactional
    public StReportPdfBatch mergePdfsByCondition(String reportId, Long yearSemesterId, 
                                                 Long schoolingPlanId, Long gradeId, Long classId, 
                                                 String mergedFileName)
    {
        // 1. 查询符合条件的PDF记录
        StReportPdf query = new StReportPdf();
        query.setReportId(reportId);
        query.setYearSemesterId(yearSemesterId);
        query.setSchoolingPlanId(schoolingPlanId);
        query.setGradeId(gradeId);
        query.setClassId(classId);
        query.setIsMerged(0);
        query.setStatus(1);

        List<StReportPdf> pdfList = stReportPdfMapper.selectStReportPdfList(query);

        if (pdfList.isEmpty())
        {
            throw new ServiceException("没有符合条件的PDF文件");
        }

        // 2. 提取PDF ID列表
        List<Long> pdfIds = pdfList.stream().map(StReportPdf::getId).collect(Collectors.toList());

        // 3. 合并PDF
        return mergePdfs(pdfIds, mergedFileName);
    }

    @Override
    public List<StReportPdf> selectStReportPdfByIds(List<Long> ids)
    {
        if (ids == null || ids.isEmpty())
        {
            return Collections.emptyList();
        }
        return stReportPdfMapper.selectStReportPdfByIds(ids);
    }

    /**
     * 更新报表PDF记录
     * 
     * @param stReportPdf 报表PDF记录
     * @return 结果
     */
    @Override
    public int updateStReportPdf(StReportPdf stReportPdf)
    {
        return stReportPdfMapper.updateStReportPdf(stReportPdf);
    }

    /**
     * 删除报表PDF记录
     */
    @Override
    @Transactional
    public int deleteStReportPdfById(Long id)
    {
        StReportPdf pdf = stReportPdfMapper.selectStReportPdfById(id);
        if (pdf != null)
        {
            // 删除物理文件
            File file = new File(RuoYiConfig.getProfile() + pdf.getFilePath());
            if (file.exists())
            {
                file.delete();
            }
        }
        return stReportPdfMapper.deleteStReportPdfById(id);
    }

    /**
     * 批量删除报表PDF记录
     */
    @Override
    @Transactional
    public int deleteStReportPdfByIds(Long[] ids)
    {
        // 删除物理文件
        for (Long id : ids)
        {
            StReportPdf pdf = stReportPdfMapper.selectStReportPdfById(id);
            if (pdf != null)
            {
                File file = new File(RuoYiConfig.getProfile() + pdf.getFilePath());
                if (file.exists())
                {
                    file.delete();
                }
            }
        }
        return stReportPdfMapper.deleteStReportPdfByIds(ids);
    }

    /**
     * 查询PDF合并批次列表
     */
    @Override
    public List<StReportPdfBatch> selectStReportPdfBatchList(StReportPdfBatch stReportPdfBatch)
    {
        return stReportPdfBatchMapper.selectStReportPdfBatchList(stReportPdfBatch);
    }

    /**
     * 查询PDF合并批次
     */
    @Override
    public StReportPdfBatch selectStReportPdfBatchById(Long id)
    {
        return stReportPdfBatchMapper.selectStReportPdfBatchById(id);
    }

    /**
     * 生成PDF文件
     */
    private String generatePdfFile(Long studentId, String reportId, Long yearSemesterId, StStudentsBase student, 
            String reportName, StSchoolYearSemester yearSemester)
            throws IOException
    {
        // 1. 构建文件路径
        String filePath = generateFilePath(student, reportId, reportName, yearSemester);
        String fullPath = RuoYiConfig.getProfile() + filePath;

        // 2. 确保目录存在
        File file = new File(fullPath);
        File parentDir = file.getParentFile();
        if (!parentDir.exists())
        {
            parentDir.mkdirs();
        }

        // 3. 如果文件已存在，添加时间戳避免覆盖
        if (file.exists())
        {
            String originalPath = filePath;
            int lastDotIndex = originalPath.lastIndexOf(".");
            if (lastDotIndex > 0)
            {
                String pathWithoutExt = originalPath.substring(0, lastDotIndex);
                String ext = originalPath.substring(lastDotIndex);
                filePath = pathWithoutExt + "_" + System.currentTimeMillis() + ext;
                fullPath = RuoYiConfig.getProfile() + filePath;
                file = new File(fullPath);
            }
        }

        // 3. 调用积木报表API生成PDF（多重路径和方法尝试）
        String baseUrl = "http://localhost:" + serverPort;
        String token = resolveAuthToken();

        class Attempt
        {
            String url;
            String method; // GET or POST
            Attempt(String url, String method)
            {
                this.url = url;
                this.method = method;
            }
        }

        List<Attempt> attempts = new ArrayList<>();
        // 1. 优先使用 exportPdfStream（前端抓包确认可用）
        attempts.add(new Attempt(baseUrl + "/jmreport/exportPdfStream", "POST"));
        // 2. 兼容旧的 exportPdf
        attempts.add(new Attempt(baseUrl + "/jmreport/exportPdf", "POST"));
        // 3. 兜底：view 页面导出
        attempts.add(new Attempt(baseUrl + "/jmreport/view/" + reportId + "?studentId=" + studentId + "&exportType=pdf", "GET"));

        ServiceException lastEx = null;
        for (Attempt attempt : attempts)
        {
            String requestUrl = attempt.url;
            try
            {
                // GET时预先拼好token
                if ("GET".equalsIgnoreCase(attempt.method) && StringUtils.isNotBlank(token) && !requestUrl.contains("token="))
                {
                    requestUrl = requestUrl + (requestUrl.contains("?") ? "&" : "?") + "token=" + token;
                }

                URL url = new URL(requestUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.setConnectTimeout(10_000);
                connection.setReadTimeout(20_000);
                connection.setRequestMethod(attempt.method);
                connection.setRequestProperty("Accept", "application/pdf");

                // 传递认证信息（在连接前）
                applyAuthHeaders(connection, token);

                // 写入POST主体（使用application/json）
                if ("POST".equalsIgnoreCase(attempt.method))
                {
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                    String jsonBody;
                    if (requestUrl.contains("/exportPdfStream"))
                    {
                        // 按前端抓包格式：excelConfigId + queryParam
                        jsonBody = String.format(
                            "{\"excelConfigId\":\"%s\",\"queryParam\":{\"studentId\":\"%d\",\"pageNo\":\"1\",\"pageSize\":10,\"currentPageNo\":\"1\",\"currentPageSize\":10}}",
                            reportId, studentId);
                    }
                    else
                    {
                        // 旧接口保持兼容
                        jsonBody = "{\"id\":\"" + reportId + "\",\"params\":{\"studentId\":" + studentId + "}}";
                    }

                    try (DataOutputStream out = new DataOutputStream(connection.getOutputStream()))
                    {
                        out.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                        out.flush();
                    }
                }

                int status = connection.getResponseCode();
                String contentType = connection.getContentType();
                if (status >= 300)
                {
                    String sample = readSample(connection.getErrorStream());
                    throw new ServiceException("HTTP状态：" + status + "，内容：" + sample);
                }

                try (InputStream inputStream = connection.getInputStream())
                {
                    // 使用缓冲流以便预读魔数
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    bis.mark(8);
                    byte[] head = new byte[5];
                    int read = bis.read(head);
                    bis.reset();

                    // 非PDF内容（例如登录页HTML），直接报错
                    boolean isPdfMagic = (read == 5) && new String(head).startsWith("%PDF-");
                    if (!isPdfMagic || (contentType != null && !contentType.toLowerCase().contains("pdf")))
                    {
                        String sample = readSample(bis);
                        throw new ServiceException("返回内容非PDF，可能未登录或权限不足：" + sample);
                    }

                    try (FileOutputStream outputStream = new FileOutputStream(file))
                    {
                        IOUtils.copy(bis, outputStream);
                    }
                }

                // 成功则返回
                return filePath;
            }
            catch (ServiceException e)
            {
                lastEx = e;
                log.warn("尝试导出报表失败，url={}, method={}，原因={}", requestUrl, attempt.method, e.getMessage());
            }
        }

        // 所有尝试都失败
        if (lastEx != null)
        {
            throw new ServiceException("导出报表失败：" + lastEx.getMessage());
        }
        throw new ServiceException("导出报表失败：未知原因");
    }

    /**
     * 合并PDF文件
     */
    private String mergePdfFiles(List<StReportPdf> pdfList, String mergedFileName) throws IOException
    {
        PDFMergerUtility merger = new PDFMergerUtility();

        // 添加所有PDF文件
        for (StReportPdf pdf : pdfList)
        {
            String fullPath = RuoYiConfig.getProfile() + pdf.getFilePath();
            File pdfFile = new File(fullPath);
            if (pdfFile.exists())
            {
                merger.addSource(pdfFile);
            }
            else
            {
                log.warn("PDF文件不存在：{}", fullPath);
            }
        }

        // 生成合并后的文件路径
        StReportPdf firstPdf = pdfList.get(0);
        String mergedFilePath = generateMergedFilePath(firstPdf.getReportId(), firstPdf.getYearSemesterId(), 
                                                       firstPdf.getSchoolingPlanId(), mergedFileName);
        String fullMergedPath = RuoYiConfig.getProfile() + mergedFilePath;

        // 确保目录存在
        File mergedFile = new File(fullMergedPath);
        File parentDir = mergedFile.getParentFile();
        if (!parentDir.exists())
        {
            parentDir.mkdirs();
        }

        // 执行合并
        merger.setDestinationFileName(fullMergedPath);
        merger.mergeDocuments(null); // 使用null参数避免deprecated警告

        return mergedFilePath;
    }

    /**
     * 生成文件路径
     * 路径格式：/学年学期名称/报表名称文件夹/
     * 文件名：学生姓名_报表名称.pdf（统计报表时：统计报表_报表名称.pdf）
     */
    private String generateFilePath(StStudentsBase student, String reportId, String reportName, 
            StSchoolYearSemester yearSemester)
    {
        String basePath = "/report/pdf";
        
        // 1. 构建学年学期名称（如：2024-2025春季学期）
        String semesterFolderName = "未知学期";
        if (yearSemester != null)
        {
            String schoolYear = yearSemester.getSchoolYear() != null ? yearSemester.getSchoolYear() : "";
            Integer semester = yearSemester.getSemester();
            // 使用工具类统一转换学期标签
            String semesterName = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(semester);
            if (semesterName == null) {
                semesterName = "未知学期";
            }
            semesterFolderName = schoolYear + semesterName;
        }
        
        // 2. 清理文件夹名称中的特殊字符（替换为下划线）
        semesterFolderName = cleanFileName(semesterFolderName);
        
        // 3. 构建报表名称文件夹（清理特殊字符）
        String reportFolderName = reportName != null ? cleanFileName(reportName) : reportId;
        
        // 4. 构建文件路径
        String path = String.format("%s/%s/%s/", basePath, semesterFolderName, reportFolderName);
        
        // 5. 构建文件名：学生姓名_报表名称.pdf（统计报表时使用“统计报表”）
        String studentName;
        if (student != null)
        {
            studentName = student.getName() != null ? student.getName() : "学生" + student.getId();
        }
        else
        {
            studentName = "统计报表";
        }
        String cleanStudentName = cleanFileName(studentName);
        String cleanReportName = reportName != null ? cleanFileName(reportName) : reportId;
        String fileName = String.format("%s_%s.pdf", cleanStudentName, cleanReportName);
        
        return path + fileName;
    }
    
    /**
     * 清理文件名中的特殊字符，替换为下划线
     * 避免文件系统不支持的特殊字符
     */
    private String cleanFileName(String fileName)
    {
        if (fileName == null)
        {
            return "";
        }
        // 替换Windows和Linux文件系统不支持的特殊字符：\ / : * ? " < > |
        String cleaned = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        // 多个连续空格或下划线替换为单个下划线
        cleaned = cleaned.replaceAll("[\\s_]+", "_");
        // 去除首尾的下划线和空格
        cleaned = cleaned.trim().replaceAll("^_+|_+$", "");
        // 如果清理后为空，使用默认值
        if (cleaned.isEmpty())
        {
            cleaned = "未命名";
        }
        return cleaned;
    }

    /**
     * 生成合并文件路径
     * 规则：
     * - 目录：/report/pdf/{year}/{semesterId}/{planId}/{reportId}/
     * - 文件名：{原始名称}_{序号}.pdf（序号从1开始递增）
     */
    private String generateMergedFilePath(String reportId, Long yearSemesterId, Long schoolingPlanId, String fileName)
    {
        int year = LocalDate.now().getYear();
        String basePath = "/report/pdf";

        // 处理null值：如果yearSemesterId为null，使用0作为默认值
        long semesterId = yearSemesterId != null ? yearSemesterId : 0L;
        long planId = schoolingPlanId != null ? schoolingPlanId : 0L;

        String path = String.format("%s/%d/%d/%d/%s/",
            basePath, year, semesterId, planId, reportId);

        // 如果没有扩展名，添加.pdf
        if (!fileName.endsWith(".pdf"))
        {
            fileName += ".pdf";
        }

        String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf("."));
        String ext = fileName.substring(fileName.lastIndexOf("."));

        // 计算序号：扫描目录下同名前缀的文件，取最大序号+1
        String dirFullPath = RuoYiConfig.getProfile() + path;
        File dir = new File(dirFullPath);
        if (!dir.exists())
        {
            dir.mkdirs();
        }

        int nextIndex = 1;
        File[] existingFiles = dir.listFiles();
        if (existingFiles != null)
        {
            String prefix = nameWithoutExt + "_";
            for (File f : existingFiles)
            {
                String existingName = f.getName();
                if (!existingName.startsWith(prefix) || !existingName.endsWith(ext))
                {
                    continue;
                }
                // 提取最后一个下划线后的数字部分
                int lastUnderscore = existingName.lastIndexOf('_');
                int dotIndex = existingName.lastIndexOf('.');
                if (lastUnderscore < 0 || dotIndex <= lastUnderscore)
                {
                    continue;
                }
                String numStr = existingName.substring(lastUnderscore + 1, dotIndex);
                try
                {
                    int n = Integer.parseInt(numStr);
                    if (n >= nextIndex)
                    {
                        nextIndex = n + 1;
                    }
                }
                catch (NumberFormatException ignore)
                {
                    // 忽略非数字后缀的旧文件（例如时间戳），不影响新规则
                }
            }
        }

        fileName = nameWithoutExt + "_" + nextIndex + ext;
        return path + fileName;
    }

    /**
     * 获取报表名称
     */
    private String getReportName(String reportId)
    {
        try
        {
            // 从数据库查询报表名称
            String sql = "SELECT name FROM jimu_report WHERE id = ?";
            String reportName = jdbcTemplate.queryForObject(sql, String.class, reportId);
            return reportName != null ? reportName : "报表_" + reportId;
        }
        catch (Exception e)
        {
            log.warn("查询报表名称失败：reportId={}", reportId, e);
            return "报表_" + reportId;
        }
    }

    /**
     * 获取Cookie Header（用于调用积木报表API）
     * 注意：由于是内部调用，可以直接使用当前请求的认证信息
     * 如果需要跨服务调用，需要传递token
     */
    private String resolveAuthToken()
    {
        try
        {
            String token = null;

            // 1. 优先从当前请求头获取
            if (ServletUtils.getRequest() != null)
            {
                token = ServletUtils.getRequest().getHeader("Authorization");
                if (StringUtils.isBlank(token))
                {
                    token = ServletUtils.getRequest().getHeader(Constants.TOKEN);
                }
            }

            // 2. 再从当前登录用户获取
            if (StringUtils.isBlank(token))
            {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser != null)
                {
                    token = loginUser.getToken();
                }
            }

            if (StringUtils.isBlank(token))
            {
                return "";
            }

            // 去掉前缀，保留纯token
            if (token != null && token.startsWith(Constants.TOKEN_PREFIX))
            {
                token = token.replace(Constants.TOKEN_PREFIX, "").trim();
            }
            return token;
        }
        catch (Exception e)
        {
            log.warn("获取认证Token失败", e);
            return "";
        }
    }

    private void applyAuthHeaders(HttpURLConnection connection, String pureToken)
    {
        try
        {
            if (StringUtils.isBlank(pureToken))
            {
                return;
            }

            String bearer = Constants.TOKEN_PREFIX + pureToken;
            // Authorization 头（RuoYi后端鉴权）
            connection.setRequestProperty("Authorization", bearer);
            // X-Access-Token 头（积木报表常用头）
            connection.setRequestProperty("X-Access-Token", pureToken);
            // 兼容部分场景直接传 token 头
            connection.setRequestProperty(Constants.TOKEN, pureToken);
        }
        catch (Exception e)
        {
            log.warn("设置报表导出鉴权信息失败，将尝试无鉴权访问", e);
        }
    }

    /**
     * 读取部分内容用于错误提示，避免保存HTML
     */
    private String readSample(InputStream inputStream) throws IOException
    {
        if (inputStream == null)
        {
            return "空响应流";
        }
        byte[] buf = new byte[512];
        int len = inputStream.read(buf);
        if (len <= 0)
        {
            return "空响应";
        }
        String text = new String(buf, 0, len);
        text = text.replaceAll("\\s+", " ");
        return text.length() > 200 ? text.substring(0, 200) + "..." : text;
    }

    /**
     * 按报表ID删除文件包（删除该报表的所有PDF归档记录及物理文件）
     * 
     * @param reportId 报表ID
     * @return 删除的记录数
     */
    @Override
    @Transactional
    public int deletePackageByReportId(String reportId)
    {
        if (StringUtils.isBlank(reportId))
        {
            throw new ServiceException("报表ID不能为空");
        }

        try
        {
            // 1. 查询该报表的所有PDF记录
            StReportPdf query = new StReportPdf();
            query.setReportId(reportId);
            List<StReportPdf> pdfList = stReportPdfMapper.selectStReportPdfList(query);

            if (pdfList.isEmpty())
            {
                log.warn("未找到报表ID={}的PDF记录", reportId);
                return 0;
            }

            int deletedCount = 0;
            int fileDeletedCount = 0;

            // 2. 逐个删除物理文件和数据库记录
            for (StReportPdf pdf : pdfList)
            {
                // 删除物理文件
                if (StringUtils.isNotEmpty(pdf.getFilePath()))
                {
                    String fullPath = RuoYiConfig.getProfile() + pdf.getFilePath();
                    File file = new File(fullPath);
                    if (file.exists())
                    {
                        boolean deleted = file.delete();
                        if (deleted)
                        {
                            fileDeletedCount++;
                            log.info("删除文件成功：{}", fullPath);
                        }
                        else
                        {
                            log.warn("删除文件失败：{}", fullPath);
                        }
                    }
                }

                // 删除数据库记录
                int result = stReportPdfMapper.deleteStReportPdfById(pdf.getId());
                if (result > 0)
                {
                    deletedCount++;
                }
            }

            log.info("删除文件包完成：reportId={}, 删除记录数={}, 删除文件数={}", 
                     reportId, deletedCount, fileDeletedCount);
            return deletedCount;
        }
        catch (Exception e)
        {
            log.error("删除文件包失败：reportId={}", reportId, e);
            throw new ServiceException("删除文件包失败：" + e.getMessage());
        }
    }
}

