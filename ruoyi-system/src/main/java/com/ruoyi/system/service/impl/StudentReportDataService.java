package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.EncryptionUtil;
import com.ruoyi.common.utils.DictTranslateUtil;
import com.ruoyi.common.utils.SensitiveDataUtil;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.mapper.StStudentsBaseMapper;
import org.jeecg.modules.jmreport.api.data.IDataSetFactory;
import org.jeecg.modules.jmreport.desreport.model.JmPage;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 学生报表数据服务
 * 为积木报表提供解密后的学生数据
 * 
 * @author ruoyi
 * @date 2025-12-30
 */
@Component("studentReportDataService")
public class StudentReportDataService implements IDataSetFactory
{
    private static final Logger log = LoggerFactory.getLogger(StudentReportDataService.class);
    
    /**
     * 获取Mapper（通过Spring容器获取）
     */
    private StStudentsBaseMapper getMapper()
    {
        return SpringUtils.getBean(StStudentsBaseMapper.class);
    }
    
    /**
     * 不分页时返回list
     * @param param 参数 包括浏览器地址栏 和 查询条件
     * @return 解密后的学生数据列表
     */
    @Override
    public List<Map<String, Object>> createData(Map<String, Object> param)
    {
        log.info("✅ [导出/预览] 开始查询报表学生数据（不分页）");
        log.info("📦 接收到的原始参数: {}", param);
        log.info("🔍 参数类型详情: {}", param != null ? param.entrySet().stream()
            .map(e -> e.getKey() + "=" + (e.getValue() != null ? e.getValue().getClass().getSimpleName() + ":" + e.getValue() : "null"))
            .reduce((a, b) -> a + ", " + b).orElse("空") : "null");
        
        try
        {
            // 判断是否需要脱敏（预览时脱敏，归档/下载时不脱敏）
            // 注意：JimuReport可能不会将URL参数传递给JavaBean，需要在报表参数中定义
            boolean needDesensitize = false;
            if (param != null)
            {
                // 先尝试从参数中获取
                Object desensitizeObj = param.get("desensitize");
                if (desensitizeObj != null)
                {
                    needDesensitize = "true".equalsIgnoreCase(desensitizeObj.toString()) || "1".equals(desensitizeObj.toString());
                }
                
                log.info("🔒 脱敏模式: {}", needDesensitize ? "开启（预览模式）" : "关闭（归档/下载模式）");
                log.info("📝 desensitize参数值: {}", desensitizeObj);
            }
            // 如果传入了studentId参数，只查询指定学生
            if (param != null && param.containsKey("studentId"))
            {
                Object studentIdObj = param.get("studentId");
                String studentIdStr = studentIdObj != null ? studentIdObj.toString().trim() : "";
                
                // 忽略JimuReport的占位符格式（如 ${studentId}）
                if (!studentIdStr.isEmpty() && !studentIdStr.startsWith("${") && !studentIdStr.endsWith("}"))
                {
                    try
                    {
                        Long studentId = Long.parseLong(studentIdStr);
                        param.put("studentId", studentId);
                        log.info("查询指定学生，studentId: {}", studentId);
                    }
                    catch (NumberFormatException e)
                    {
                        log.warn("studentId格式不正确: {}，将查询所有学生", studentIdStr);
                        param.remove("studentId"); // 移除无效的studentId
                    }
                }
                else
                {
                    log.warn("studentId是占位符或无效值: {}，将查询所有学生", studentIdStr);
                    param.remove("studentId"); // 移除占位符
                }
            }
            
            // 1. 从数据库查询学生数据（加密状态）
            List<Map<String, Object>> students = getMapper().selectStudentListForReport(param);
            
            log.info("查询到 {} 条学生记录", students != null ? students.size() : 0);
            
            if (students == null || students.isEmpty())
            {
                return students;
            }
            
            // 2. 解密敏感字段
            for (Map<String, Object> student : students)
            {
                decryptField(student, "student_no");    // 学籍号
                decryptField(student, "id_card_no");    // 身份证号
                decryptField(student, "phone");         // 手机号
                decryptField(student, "home_address");  // 家庭住址
                decryptField(student, "domicile");      // 户籍地址
                
                // 3. 根据参数决定是否脱敏
                if (needDesensitize)
                {
                    desensitizeField(student, "id_card_no");   // 身份证脱敏
                    desensitizeField(student, "phone");        // 手机号脱敏
                    desensitizeField(student, "student_no");   // 学籍号脱敏
                    desensitizeField(student, "home_address"); // 家庭住址脱敏
                    log.debug("已对学生 {} 的敏感信息进行脱敏", student.get("name"));
                }
                
                // 4. 转换字典值为显示文本
                translateDictFields(student);
            }
            
            log.info("学生数据解密完成");
            return students;
        }
        catch (Exception e)
        {
            log.error("查询报表学生数据失败", e);
            throw new RuntimeException("查询报表数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页时返回 JmPage 并且参数param里会传入pageNo, pageSize
     * @param param 参数 包括浏览器地址栏 和 查询条件
     * @return 分页数据
     */
    @Override
    public JmPage createPageData(Map<String, Object> param)
    {
        log.info("开始查询报表学生数据（分页），参数: {}", param);
        
        JmPage page = new JmPage();
        
        try
        {
            // 从参数中获取分页信息
            int pageNo = param.containsKey("pageNo") ? Integer.parseInt(param.get("pageNo").toString()) : 1;
            int pageSize = param.containsKey("pageSize") ? Integer.parseInt(param.get("pageSize").toString()) : 10;
            
            // 处理studentId参数（与 createData 相同的逻辑）
            if (param != null && param.containsKey("studentId"))
            {
                Object studentIdObj = param.get("studentId");
                String studentIdStr = studentIdObj != null ? studentIdObj.toString().trim() : "";
                
                // 忽略JimuReport的占位符格式（如 ${studentId}）
                if (!studentIdStr.isEmpty() && !studentIdStr.startsWith("${") && !studentIdStr.endsWith("}"))
                {
                    try
                    {
                        Long studentId = Long.parseLong(studentIdStr);
                        param.put("studentId", studentId);
                        log.info("分页查询指定学生，studentId: {}", studentId);
                    }
                    catch (NumberFormatException e)
                    {
                        log.warn("分页查询studentId格式不正确: {}，将查询所有学生", studentIdStr);
                        param.remove("studentId");
                    }
                }
                else
                {
                    log.warn("分页查询studentId是占位符: {}，将查询所有学生", studentIdStr);
                    param.remove("studentId");
                }
            }
            
            // 查询数据（这里简化处理，实际应该分页查询）
            List<Map<String, Object>> students = getMapper().selectStudentListForReport(param);
            
            if (students != null && !students.isEmpty())
            {
                // 解密敏感字段
                for (Map<String, Object> student : students)
                {
                    decryptField(student, "student_no");
                    decryptField(student, "id_card_no");
                    decryptField(student, "phone");
                    decryptField(student, "home_address");
                    decryptField(student, "domicile");
                    
                    // 转换字典字段
                    translateDictFields(student);
                }
            }
            
            // 设置分页信息
            page.setPageSize(pageSize);
            page.setTotal(students != null ? students.size() : 0);
            page.setRecords(students);
            
            log.info("学生数据分页查询完成，总数: {}", page.getTotal());
            return page;
        }
        catch (Exception e)
        {
            log.error("分页查询报表学生数据失败", e);
            throw new RuntimeException("查询报表数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 解密单个字段
     */
    private void decryptField(Map<String, Object> record, String fieldName)
    {
        Object value = record.get(fieldName);
        if (value != null && value instanceof String)
        {
            String encryptedValue = (String) value;
            if (encryptedValue.length() > 0)
            {
                try
                {
                    String decryptedValue = EncryptionUtil.decrypt(encryptedValue);
                    record.put(fieldName, decryptedValue);
                    log.debug("字段 {} 已解密", fieldName);
                }
                catch (Exception e)
                {
                    log.warn("解密字段 {} 失败: {}, 保持原值", fieldName, e.getMessage());
                }
            }
        }
    }
    
    /**
     * 脱敏单个字段（在解密后调用）
     * 使用项目统一的 SensitiveDataUtil 工具类（基于 Hutool）
     * 身份证：前6后4位
     * 手机号：前3后4位
     * 学籍号：G + 脱敏后的身份证号
     * 家庭住址：保留省市县区镇，后续部分替换为'**村**屯*号'
     */
    private void desensitizeField(Map<String, Object> record, String fieldName)
    {
        Object value = record.get(fieldName);
        if (value != null && value instanceof String)
        {
            String str = (String) value;
            String desensitized = null;
            
            if ("id_card_no".equals(fieldName))
            {
                // 身份证：使用 Hutool 脱敏，前6后4位
                desensitized = SensitiveDataUtil.maskIdCardNo(str);
            }
            else if ("phone".equals(fieldName))
            {
                // 手机号：使用 Hutool 脱敏，前3后4位
                desensitized = SensitiveDataUtil.maskPhone(str);
            }
            else if ("student_no".equals(fieldName))
            {
                // 学籍号：G + 脱敏后的身份证号
                // 需要从学籍号中提取身份证号（去掉前缀G）
                String idCardNo = str.startsWith("G") ? str.substring(1) : str;
                desensitized = SensitiveDataUtil.maskStudentNo(idCardNo);
            }
            else if ("home_address".equals(fieldName))
            {
                // 家庭住址：使用自定义地址脱敏
                desensitized = SensitiveDataUtil.maskAddress(str);
            }
            
            if (desensitized != null)
            {
                record.put(fieldName, desensitized);
                log.debug("字段 {} 已脱敏", fieldName);
            }
        }
    }
    
    /**
     * 转换字典字段为显示文本
     */
    private void translateDictFields(Map<String, Object> record)
    {
        log.info("🔄 开始转换字典字段，原始数据: {}", record);
        
        // 转换性别
        if (record.containsKey("gender"))
        {
            Object gender = record.get("gender");
            log.info("⚡ 转换性别: {} -> ?", gender);
            String genderText = DictTranslateUtil.translateGender(gender != null ? gender.toString() : "");
            record.put("gender_text", genderText);
            log.info("✅ 性别转换完成: {}", genderText);
        }
        
        // 转换民族
        if (record.containsKey("ethnicity"))
        {
            Object ethnicity = record.get("ethnicity");
            log.info("⚡ 转换民族: {} -> ?", ethnicity);
            String ethnicityText = DictTranslateUtil.translateEthnicity(ethnicity != null ? ethnicity.toString() : "");
            record.put("ethnicity_text", ethnicityText);
            log.info("✅ 民族转换完成: {}", ethnicityText);
        }
        
        // 转换是否民族高中班
        if (record.containsKey("is_ethnic_class"))
        {
            Object isEthnicClass = record.get("is_ethnic_class");
            log.info("⚡ 转换是否民族高中班: {} -> ?", isEthnicClass);
            String isEthnicClassText = DictTranslateUtil.translateYesNo(isEthnicClass);
            record.put("is_ethnic_class_text", isEthnicClassText);
            log.info("✅ 是否民族高中班转换完成: {}", isEthnicClassText);
        }
        
        // 转换政治面貌
        if (record.containsKey("political_status"))
        {
            Object politicalStatus = record.get("political_status");
            if (politicalStatus != null)
            {
                log.info("⚡ 转换政治面貌: {} -> ?", politicalStatus);
                String politicalStatusText = DictTranslateUtil.translatePoliticalStatus(politicalStatus.toString());
                record.put("political_status", politicalStatusText);
                log.info("✅ 政治面貌转换完成: {}", politicalStatusText);
            }
        }
        
        log.info("✅ 所有字典字段转换完成，最终数据: {}", record);
    }
}
