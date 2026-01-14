package com.ruoyi.system.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.EncryptionUtil;
import com.ruoyi.system.domain.StReportExcel;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.domain.StStudentsBase;
import com.ruoyi.system.mapper.StReportExcelMapper;
import com.ruoyi.system.mapper.StSchoolYearSemesterMapper;
import com.ruoyi.system.mapper.StStudentsBaseMapper;
import com.ruoyi.system.service.IReportExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;

/**
 * Excel报表Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-03
 */
@Service
public class ReportExcelServiceImpl implements IReportExcelService
{
    private static final Logger log = LoggerFactory.getLogger(ReportExcelServiceImpl.class);

    @Autowired
    private StReportExcelMapper stReportExcelMapper;

    @Autowired
    private StStudentsBaseMapper stStudentsBaseMapper;

    @Autowired
    private StSchoolYearSemesterMapper stSchoolYearSemesterMapper;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Autowired
    private com.ruoyi.system.service.impl.SubsidySummaryReportDataService subsidySummaryReportDataService;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * 查询Excel报表
     * 
     * @param id Excel报表主键
     * @return Excel报表
     */
    @Override
    public StReportExcel selectStReportExcelById(Long id)
    {
        return stReportExcelMapper.selectStReportExcelById(id);
    }

    /**
     * 查询Excel报表列表
     * 
     * @param stReportExcel Excel报表
     * @return Excel报表
     */
    @Override
    public List<StReportExcel> selectStReportExcelList(StReportExcel stReportExcel)
    {
        return stReportExcelMapper.selectStReportExcelList(stReportExcel);
    }

    /**
     * 新增Excel报表
     * 
     * @param stReportExcel Excel报表
     * @return 结果
     */
    @Override
    public int insertStReportExcel(StReportExcel stReportExcel)
    {
        return stReportExcelMapper.insertStReportExcel(stReportExcel);
    }

    /**
     * 修改Excel报表
     * 
     * @param stReportExcel Excel报表
     * @return 结果
     */
    @Override
    public int updateStReportExcel(StReportExcel stReportExcel)
    {
        return stReportExcelMapper.updateStReportExcel(stReportExcel);
    }

    /**
     * 批量删除Excel报表
     * 
     * @param ids 需要删除的Excel报表主键集合
     * @return 结果
     */
    @Override
    public int deleteStReportExcelByIds(Long[] ids)
    {
        return stReportExcelMapper.deleteStReportExcelByIds(ids);
    }

    /**
     * 删除Excel报表信息
     * 
     * @param id Excel报表主键
     * @return 结果
     */
    @Override
    public int deleteStReportExcelById(Long id)
    {
        return stReportExcelMapper.deleteStReportExcelById(id);
    }

    /**
     * 生成并保存Excel报表
     */
    @Override
    @Transactional
    public StReportExcel generateAndSaveExcel(Long studentId, String reportId, Long yearSemesterId)
    {
        try
        {
            // 1. 查询学生信息
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

            // 3. 查询报表名称
            String reportName = getReportName(reportId);

            // 4. 查询学年学期信息
            StSchoolYearSemester yearSemester = null;
            if (yearSemesterId != null)
            {
                yearSemester = stSchoolYearSemesterMapper.selectStSchoolYearSemesterById(yearSemesterId);
            }

            // 5. 生成Excel文件
            String filePath = generateExcelFile(studentId, reportId, yearSemesterId, student, reportName, yearSemester);

            // 6. 获取文件大小
            File file = new File(RuoYiConfig.getProfile() + filePath);
            long fileSize = file.exists() ? file.length() : 0;

            // 7. 保存数据库记录（包含学生信息快照，避免学生被删除或信息变更后无法查询历史档案）
            StReportExcel excelRecord = new StReportExcel();
            excelRecord.setStudentId(studentId);
            if (student != null)
            {
                excelRecord.setStudentName(student.getName());
                // 解密学籍号
                String encryptedStudentNo = student.getStudentNo();
                if (encryptedStudentNo != null && !encryptedStudentNo.isEmpty()) {
                    try {
                        String decryptedStudentNo = EncryptionUtil.decrypt(encryptedStudentNo);
                        excelRecord.setStudentNo(decryptedStudentNo);
                    } catch (Exception e) {
                        log.warn("解密学籍号失败，使用原值: {}", e.getMessage());
                        excelRecord.setStudentNo(encryptedStudentNo); // 保持原值
                    }
                } else {
                    excelRecord.setStudentNo(encryptedStudentNo);
                }
                excelRecord.setSchoolingPlanId(student.getSchoolingPlanId());
                excelRecord.setGradeId(student.getGradeId());
                excelRecord.setGradeName(student.getGradeName());
                excelRecord.setClassId(student.getClassId());
                excelRecord.setClassName(student.getClassName());
            }
            else
            {
                // 统计报表：使用报表名称作为标识
                excelRecord.setStudentName("统计报表");
                excelRecord.setStudentNo("");
            }
            excelRecord.setReportId(reportId);
            excelRecord.setReportName(reportName);
            excelRecord.setFileName(file.getName());
            excelRecord.setFilePath(filePath);
            excelRecord.setFileSize(fileSize);
            excelRecord.setYearSemesterId(yearSemesterId);
            excelRecord.setIsMerged(0);
            excelRecord.setStatus(1);
            excelRecord.setCreateBy(SecurityUtils.getUsername());

            stReportExcelMapper.insertStReportExcel(excelRecord);

            log.info("Excel生成并保存成功：studentId={}, reportId={}, filePath={}", studentId, reportId, filePath);
            return excelRecord;
        }
        catch (Exception e)
        {
            log.error("Excel生成并保存失败：studentId={}, reportId={}", studentId, reportId, e);
            throw new ServiceException("Excel生成失败：" + e.getMessage());
        }
    }

    /**
     * 批量生成Excel报表
     */
    @Override
    @Transactional
    public void batchGenerateExcel(List<Long> studentIds, String reportId, Long yearSemesterId, 
                                 String batchName, Long schoolingPlanId, String studentName, 
                                 Long gradeId, Long classId)
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
                if (StringUtils.hasText(studentName))
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

            // 批量生成Excel
            for (Long studentId : targetStudentIds)
            {
                try
                {
                    StReportExcel excelRecord = generateAndSaveExcel(studentId, reportId, yearSemesterId);
                    successIds.add(excelRecord.getId());
                }
                catch (Exception e)
                {
                    log.error("批量生成Excel失败：studentId={}", studentId, e);
                    errorMessages.add("学生ID " + studentId + "：" + e.getMessage());
                }
            }

            log.info("批量生成Excel完成：batchId={}, successCount={}, failCount={}", 
                batchId, successIds.size(), errorMessages.size());

            if (!errorMessages.isEmpty())
            {
                throw new ServiceException("部分Excel生成失败：" + String.join("; ", errorMessages));
            }
        }
        catch (Exception e)
        {
            log.error("批量生成Excel失败：batchId={}", batchId, e);
            throw new ServiceException("批量生成Excel失败：" + e.getMessage());
        }
    }

    /**
     * 下载Excel报表
     */
    @Override
    public void downloadExcel(Long id, HttpServletResponse response)
    {
        StReportExcel excel = stReportExcelMapper.selectStReportExcelById(id);
        if (excel == null || excel.getStatus() == 0)
        {
            throw new ServiceException("Excel报表不存在或已删除");
        }

        try
        {
            String filePath = RuoYiConfig.getProfile() + excel.getFilePath();
            File file = new File(filePath);
            if (!file.exists())
            {
                throw new ServiceException("Excel文件不存在");
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = java.net.URLEncoder.encode(excel.getFileName(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // 读取文件并写入响应
            javax.servlet.ServletOutputStream outputStream = response.getOutputStream();
            java.nio.file.Files.copy(file.toPath(), outputStream);
            outputStream.flush();
        }
        catch (Exception e)
        {
            log.error("下载Excel失败：id={}", id, e);
            throw new ServiceException("下载Excel失败：" + e.getMessage());
        }
    }

    /**
     * 生成Excel文件
     */
    private String generateExcelFile(Long studentId, String reportId, Long yearSemesterId, StStudentsBase student, 
            String reportName, StSchoolYearSemester yearSemester) throws IOException
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

        // 4. 通过报表数据服务生成Excel文件
        try {
            // 构建查询参数
            java.util.Map<String, Object> queryParam = new java.util.HashMap<>();
            if (studentId != null) {
                queryParam.put("studentId", studentId);
            }
            queryParam.put("desensitize", "false");
            
            // 获取报表数据
            java.util.List<java.util.Map<String, Object>> reportData = getReportData(reportId, queryParam);
            
            // 创建Excel工作簿
            org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            
            // 创建工作表
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("报表数据");
            
            if (reportData != null && !reportData.isEmpty()) {
                // 获取表头（使用第一行数据的键作为表头）
                java.util.Map<String, Object> firstRow = reportData.get(0);
                java.util.Set<String> headers = firstRow.keySet();

                // 创建表头样式
                org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // 创建表头
                org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
                int colIndex = 0;
                for (String header : headers) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(colIndex++);
                    // 将字段名转换为中文标题
                    String headerTitle = convertFieldToTitle(header);
                    cell.setCellValue(headerTitle != null ? headerTitle : header != null ? header : "");
                    cell.setCellStyle(headerStyle);
                }

                // 填充数据
                int rowIndex = 1;
                for (java.util.Map<String, Object> rowData : reportData) {
                    org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIndex++);
                    colIndex = 0;
                    for (String header : headers) {
                        Object value = rowData.get(header);
                        org.apache.poi.ss.usermodel.Cell cell = row.createCell(colIndex++);
                        if (value != null) {
                            if (value instanceof Number) {
                                cell.setCellValue(((Number) value).doubleValue());
                            } else {
                                cell.setCellValue(value.toString());
                            }
                        } else {
                            cell.setCellValue("");
                        }
                    }
                }

                // 自动调整列宽
                for (int i = 0; i < headers.size(); i++) {
                    sheet.autoSizeColumn(i);
                }
                

            }
            
            // 保存Excel文件
            try (java.io.FileOutputStream outputStream = new java.io.FileOutputStream(file)) {
                workbook.write(outputStream);
            } finally {
                workbook.close();
            }
            
            return filePath;
        } catch (Exception e) {
            log.error("通过报表数据服务生成Excel失败：", e);
            throw new ServiceException("Excel生成失败：" + e.getMessage());
        }
    }

    /**
     * 通过报表ID和参数获取报表数据
     * 使用已有的报表数据服务来获取数据
     */
    private java.util.List<java.util.Map<String, Object>> getReportData(String reportId, java.util.Map<String, Object> queryParam) {
        try {
            // 尝试通过报表ID查找对应的报表数据服务
            // 首先查找报表类型以确定使用哪个数据服务
            String sql = "SELECT name FROM jimu_report WHERE id = ?";
            String reportName = jdbcTemplate.queryForObject(sql, String.class, reportId);
            
            // 根据报表名称或ID判断是否为特殊报表，使用对应的服务
            // 对于助学金汇总报表，使用现有的SubsidySummaryReportDataService
            java.util.List<java.util.Map<String, Object>> reportData;
            if (reportName != null && (reportName.contains("助学金") || reportName.contains("汇总"))) {
                reportData = subsidySummaryReportDataService.querySubsidySummaryData(queryParam);
            }
            
            // 对于其他报表，尝试通用方法
            // 由于无法直接访问JimuReport内部API，这里返回空列表
            else {
                log.warn("无法确定报表类型，返回空数据：{}", reportId);
                reportData = java.util.Collections.emptyList();
            }
            
            // 解密敏感字段（归档Excel时必须解密）
            decryptSensitiveData(reportData);
            
            return reportData;
            
        } catch (Exception e) {
            log.error("获取报表数据失败：reportId={}, params={}", reportId, queryParam, e);
            return java.util.Collections.emptyList();
        }
    }

    /**
     * 生成文件路径
     * 路径格式：/report/excel/{year}/{semesterName}/{reportName}/
     * 文件名：学生姓名_报表名称.xlsx（统计报表时：统计报表_报表名称.xlsx）
     */
    private String generateFilePath(StStudentsBase student, String reportId, String reportName, 
            StSchoolYearSemester yearSemester)
    {
        String basePath = "/report/excel";
        
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
        
        // 5. 构建文件名：学生姓名_报表名称.xlsx（统计报表时使用"统计报表"）
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
        String fileName = String.format("%s_%s.xlsx", cleanStudentName, cleanReportName);
        
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
                if (!StringUtils.hasText(token))
                {
                    token = ServletUtils.getRequest().getHeader(Constants.TOKEN);
                }
            }

            // 2. 再从当前登录用户获取
            if (!StringUtils.hasText(token))
            {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser != null)
                {
                    token = loginUser.getToken();
                }
            }

            if (!StringUtils.hasText(token))
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

    private void applyAuthHeaders(java.net.HttpURLConnection connection, String pureToken)
    {
        try
        {
            if (!StringUtils.hasText(pureToken))
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
    private String readSample(java.io.InputStream inputStream) throws IOException
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
     * 解密报表数据中的敏感字段
     * 在生成Excel时对敏感数据进行解密
     */
    private void decryptSensitiveData(java.util.List<java.util.Map<String, Object>> reportData) {
        if (reportData == null || reportData.isEmpty()) {
            return;
        }
        
        log.info("🔓 开始解密报表数据中的敏感字段，共 {} 条记录", reportData.size());
        
        for (java.util.Map<String, Object> record : reportData) {
            // 解密身份证号字段
            decryptField(record, "id_card");        // 报表中身份证字段名
            decryptField(record, "id_card_no");     // 另一种身份证字段名
            
            // 解密学籍号字段
            decryptField(record, "student_no");
            
            // 解密手机号字段
            decryptField(record, "phone");
            decryptField(record, "contact_phone");  // 另一种手机号字段名
            
            // 解密住址字段
            decryptField(record, "home_address");
            decryptField(record, "domicile");       // 户籍地址
            
            log.debug("解密后的记录: {}", record);
        }
        
        log.info("✅ 敏感字段解密完成");
    }
    
    /**
     * 解密单个字段
     */
    private void decryptField(java.util.Map<String, Object> record, String fieldName) {
        Object value = record.get(fieldName);
        if (value != null && value instanceof String) {
            String encryptedValue = (String) value;
            if (encryptedValue.length() > 0) {
                try {
                    String decryptedValue = EncryptionUtil.decrypt(encryptedValue);
                    record.put(fieldName, decryptedValue);
                    log.debug("字段 {} 已解密", fieldName);
                } catch (Exception e) {
                    log.warn("解密字段 {} 失败: {}, 保持原值", fieldName, e.getMessage());
                }
            }
        }
    }
    
    /**
     * 将字段名转换为中文标题
     * @param fieldName 字段名
     * @return 对应的中文标题
     */
    private String convertFieldToTitle(String fieldName) {
        if (fieldName == null) {
            return null;
        }
        
        // 定义字段名到中文标题的映射
        java.util.Map<String, String> fieldTitleMap = new java.util.HashMap<>();
        fieldTitleMap.put("id", "ID");
        fieldTitleMap.put("name", "姓名");
        fieldTitleMap.put("student_no", "学籍号");
        fieldTitleMap.put("id_card_no", "身份证号");
        fieldTitleMap.put("seq_no", "序号");
        fieldTitleMap.put("year_semester", "学年学期");
        fieldTitleMap.put("gender", "性别");
        fieldTitleMap.put("nation", "民族");
        fieldTitleMap.put("grade_class", "年级/班级");
        fieldTitleMap.put("bank_card", "银行卡号");
        fieldTitleMap.put("bank_name", "开户行");
        fieldTitleMap.put("student_sign", "学生确认签名");
        fieldTitleMap.put("id_card", "身份证号");
        fieldTitleMap.put("phone", "联系电话");
        fieldTitleMap.put("contact_phone", "联系电话");
        fieldTitleMap.put("gender", "性别");
        fieldTitleMap.put("ethnicity", "民族");
        fieldTitleMap.put("domicile", "户籍所在地");
        fieldTitleMap.put("home_address", "家庭住址");
        fieldTitleMap.put("schooling_plan_id", "学制");
        fieldTitleMap.put("grade_id", "年级ID");
        fieldTitleMap.put("grade_name", "年级名称");
        fieldTitleMap.put("class_id", "班级ID");
        fieldTitleMap.put("class_name", "班级名称");
        fieldTitleMap.put("student_id", "学生ID");
        fieldTitleMap.put("student_name", "学生姓名");
        fieldTitleMap.put("report_id", "报表ID");
        fieldTitleMap.put("report_name", "报表名称");
        fieldTitleMap.put("file_name", "文件名");
        fieldTitleMap.put("file_path", "文件路径");
        fieldTitleMap.put("file_size", "文件大小");
        fieldTitleMap.put("year_semester_id", "学年学期ID");
        fieldTitleMap.put("is_merged", "是否合并");
        fieldTitleMap.put("merged_from_ids", "合并来源");
        fieldTitleMap.put("status", "状态");
        fieldTitleMap.put("create_time", "创建时间");
        fieldTitleMap.put("create_by", "创建者");
        fieldTitleMap.put("update_time", "更新时间");
        fieldTitleMap.put("update_by", "更新者");
        fieldTitleMap.put("is_poverty_relief_family", "是否脱贫户");
        fieldTitleMap.put("poverty_relief_year", "脱贫年份");
        fieldTitleMap.put("difficulty_type", "困难类型");
        fieldTitleMap.put("difficulty_type_text", "困难类型");
        fieldTitleMap.put("subsidy_amount", "补贴金额");
        fieldTitleMap.put("grant_date", "发放日期");
        fieldTitleMap.put("grant_status", "发放状态");
        
        return fieldTitleMap.get(fieldName.toLowerCase());
    }
}