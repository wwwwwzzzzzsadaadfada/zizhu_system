package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.EncryptionUtil;
import com.ruoyi.common.utils.DictTranslateUtil;
import com.ruoyi.common.utils.SensitiveDataUtil;
import com.ruoyi.common.utils.spring.SpringUtils;
import org.jeecg.modules.jmreport.api.data.IDataSetFactory;
import org.jeecg.modules.jmreport.desreport.model.JmPage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 助学金发放汇总表数据服务
 * 为积木报表提供解密和脱敏后的助学金发放数据
 * 
 * @author ruoyi
 * @date 2026-01-03
 */
@Component("subsidySummaryReportDataService")
public class SubsidySummaryReportDataService implements IDataSetFactory
{
    private static final Logger log = LoggerFactory.getLogger(SubsidySummaryReportDataService.class);
    
    /**
     * 获取JdbcTemplate（通过Spring容器获取）
     */
    private JdbcTemplate getJdbcTemplate()
    {
        return SpringUtils.getBean(JdbcTemplate.class);
    }
    
    /**
     * 不分页时返回list
     * @param param 参数 包括浏览器地址栏 和 查询条件
     * @return 解密后的助学金发放数据列表
     */
    @Override
    public List<Map<String, Object>> createData(Map<String, Object> param)
    {
        log.info("✅ [助学金汇总] 开始查询报表数据（不分页）");
        log.info("📦 接收到的原始参数: {}", param);
        
        try
        {
            // 判断是否需要脱敏（预览时脱敏，归档/下载时不脱敏）
            boolean needDesensitize = false;
            if (param != null)
            {
                Object desensitizeObj = param.get("desensitize");
                log.info("🔍 检查脱敏参数: desensitize = {} (类型: {})", desensitizeObj, desensitizeObj != null ? desensitizeObj.getClass().getSimpleName() : "null");
                if (desensitizeObj != null)
                {
                    String desensitizeStr = desensitizeObj.toString().toLowerCase();
                    needDesensitize = "true".equals(desensitizeStr) || "1".equals(desensitizeStr);
                    log.info("🔍 脱敏参数解析: {} -> {}", desensitizeStr, needDesensitize);
                }
                log.info("🔒 脱敏模式: {}", needDesensitize ? "开启（预览模式）" : "关闭（归档/下载模式）");
            }
            
            // 1. 查询助学金发放汇总数据
            List<Map<String, Object>> list = querySubsidySummaryData(param);
            
            log.info("查询到 {} 条助学金发放记录", list != null ? list.size() : 0);
            
            if (list == null || list.isEmpty())
            {
                return list;
            }
            
            // 2. 解密和脱敏处理
            log.info("📊 开始处理 {} 条记录的解密和脱敏", list.size());
            for (Map<String, Object> row : list)
            {
                log.debug("🔍 处理单条记录，原始数据: {}", row);
                
                // 解密敏感字段（注意：SQL中字段名为 id_card, student_no, home_address, contact_phone）
                decryptField(row, "student_no");    // 学籍号
                decryptField(row, "id_card");        // 身份证号（SQL中是 id_card）
                decryptField(row, "contact_phone");  // 手机号（SQL中是 contact_phone）
                decryptField(row, "home_address");   // 家庭住址（SQL中是 home_address，实际来自 domicile 字段）
                decryptField(row, "domicile");       // 户籍地址（如果字段名未被重命名）
                
                log.debug("🔓 解密后数据: {}", row);
                
                // 转换字典字段
                translateDictFields(row);
                
                log.debug("🔄 字典转换后数据: {}", row);
                
                // 根据参数决定是否脱敏
                if (needDesensitize)
                {
                    log.debug("🔄 开始脱敏处理，原始数据: {}", row);
                    desensitizeField(row, "id_card");        // 身份证脱敏
                    desensitizeField(row, "contact_phone");  // 手机号脱敏
                    desensitizeField(row, "student_no");     // 学籍号脱敏
                    desensitizeField(row, "home_address");   // 家庭住址脱敏
                    desensitizeField(row, "domicile");       // 户籍地址脱敏
                    log.debug("🔄 脱敏处理完成，脱敏后数据: {}", row);
                }
                else
                {
                    log.debug("⚠️ 脱敏未开启，保持解密后数据");
                }
            }
            log.info("✅ 数据处理完成");
            
            log.info("助学金发放数据解密完成");
            return list;
        }
        catch (Exception e)
        {
            log.error("查询助学金发放汇总数据失败", e);
            throw new RuntimeException("查询报表数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页时返回 JmPage
     * @param param 参数 包括浏览器地址栏 和 查询条件
     * @return 分页数据
     */
    @Override
    public JmPage createPageData(Map<String, Object> param)
    {
        log.info("开始查询助学金发放数据（分页），参数: {}", param);
        
        JmPage page = new JmPage();
        
        try
        {
            // 从参数中获取分页信息
            int pageNo = param.containsKey("pageNo") ? Integer.parseInt(param.get("pageNo").toString()) : 1;
            int pageSize = param.containsKey("pageSize") ? Integer.parseInt(param.get("pageSize").toString()) : 10;
            
            // 判断是否需要脱敏（预览时脱敏，归档/下载时不脱敏）
            boolean needDesensitize = false;
            if (param != null)
            {
                Object desensitizeObj = param.get("desensitize");
                log.info("🔍 分页查询检查脱敏参数: desensitize = {} (类型: {})", desensitizeObj, desensitizeObj != null ? desensitizeObj.getClass().getSimpleName() : "null");
                if (desensitizeObj != null)
                {
                    String desensitizeStr = desensitizeObj.toString().toLowerCase();
                    needDesensitize = "true".equals(desensitizeStr) || "1".equals(desensitizeStr);
                    log.info("🔍 分页查询脱敏参数解析: {} -> {}", desensitizeStr, needDesensitize);
                }
                log.info("🔒 分页查询脱敏模式: {}", needDesensitize ? "开启（预览模式）" : "关闭（归档/下载模式）");
            }
            
            // 查询数据
            List<Map<String, Object>> list = querySubsidySummaryData(param);
            
            if (list != null && !list.isEmpty())
            {
                log.info("📊 分页查询开始处理 {} 条记录的解密和脱敏", list.size());
                // 解密和脱敏敏感字段
                for (Map<String, Object> row : list)
                {
                    log.debug("🔍 分页查询处理单条记录，原始数据: {}", row);
                    
                    decryptField(row, "student_no");
                    decryptField(row, "id_card");
                    decryptField(row, "contact_phone");
                    decryptField(row, "home_address");
                    decryptField(row, "domicile");
                    
                    log.debug("🔓 分页查询解密后数据: {}", row);
                    
                    // 转换字典字段
                    translateDictFields(row);
                    
                    log.debug("🔄 分页查询字典转换后数据: {}", row);
                    
                    // 根据参数决定是否脱敏
                    if (needDesensitize)
                    {
                        log.debug("🔄 分页查询开始脱敏处理，原始数据: {}", row);
                        desensitizeField(row, "id_card");        // 身份证脱敏
                        desensitizeField(row, "contact_phone");  // 手机号脱敏
                        desensitizeField(row, "student_no");     // 学籍号脱敏
                        desensitizeField(row, "home_address");   // 家庭住址脱敏
                        desensitizeField(row, "domicile");       // 户籍地址脱敏
                        log.debug("🔄 分页查询脱敏处理完成，脱敏后数据: {}", row);
                    }
                    else
                    {
                        log.debug("⚠️ 分页查询脱敏未开启，保持解密后数据");
                    }
                }
                log.info("✅ 分页查询数据处理完成");
            }
            
            // 设置分页信息
            page.setPageSize(pageSize);
            page.setTotal(list != null ? list.size() : 0);
            page.setRecords(list);
            
            log.info("助学金发放数据分页查询完成，总数: {}", page.getTotal());
            return page;
        }
        catch (Exception e)
        {
            log.error("分页查询助学金发放数据失败", e);
            throw new RuntimeException("查询报表数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询助学金发放汇总数据
     * 包含学生基本信息、困难类型、补助金额等
     */
    public List<Map<String, Object>> querySubsidySummaryData(Map<String, Object> param)
    {
        String sql = "SELECT " +
            "    ROW_NUMBER() OVER (ORDER BY g.id ASC, asi.clazz_name ASC, asi.student_name ASC) AS seq_no, " +
            "    (SELECT CONCAT(school_year, ' ', CASE semester WHEN '1' THEN '秋季学期' WHEN '2' THEN '春季学期' ELSE semester END) " +
            "     FROM st_school_year_semester " +
            "     WHERE id = asi.current_year_semester_id LIMIT 1) AS year_semester, " +
            "    asi.student_name, " +
            "    CASE asi.gender " +
            "        WHEN '1' THEN '男' " +
            "        WHEN '0' THEN '女' " +
            "        ELSE '未知' " +
            "    END AS gender, " +
            "    asi.nation_name AS nation, " +
            "    CONCAT(COALESCE(g.name, ''), '/', COALESCE(asi.clazz_name, '')) AS grade_class, " +
            "    CASE " +
            "        WHEN dict.dict_label LIKE '%脱贫%' THEN CONCAT('1（', COALESCE(asi.poverty_relief_year, ''), '）') " +
            "        WHEN dict.dict_label LIKE '%低保边缘%' THEN '16' " +
            "        WHEN dict.dict_label LIKE '%城市低保%' THEN '5' " +
            "        WHEN dict.dict_label LIKE '%农村低保%' OR dict.dict_label LIKE '%低保%' THEN '6' " +
            "        WHEN dict.dict_label LIKE '%城市特困%' THEN '7' " +
            "        WHEN dict.dict_label LIKE '%农村特困%' OR dict.dict_label LIKE '%特困%' THEN '8' " +
            "        WHEN dict.dict_label LIKE '%残疾%' THEN '9' " +
            "        WHEN dict.dict_label LIKE '%孤儿%' THEN '10' " +
            "        WHEN dict.dict_label LIKE '%烈士%' THEN '11' " +
            "        WHEN dict.dict_label LIKE '%困难职工%' THEN '12' " +
            "        WHEN dict.dict_label LIKE '%支出型困难%' OR dict.dict_label LIKE '%低收入%' THEN '13' " +
            "        WHEN dict.dict_label LIKE '%民族班%' THEN '14' " +
            "        ELSE '15' " +
            "    END AS difficulty_type, " +
            "    asi.id_card AS id_card, " +
            "    asi.student_no AS student_no, " +
            "    asi.domicile AS home_address, " +
            "    bc.bank_account_no AS bank_card, " +
            "    bc.bank_name AS bank_name, " +
            "    COALESCE(SUM(CASE WHEN r.approval_status = 1 AND d.economy_category = '1' THEN r.subsidy_amount ELSE 0 END), 0) AS subsidy_amount, " +
            "    asi.phone AS contact_phone, " +
            "    '' AS student_sign " +
            "FROM st_aided_student_info asi " +
            "INNER JOIN st_student_subsidy_records r ON asi.student_id = r.student_id AND r.year_semester_id = asi.current_year_semester_id AND r.approval_status = 1 " +
            "INNER JOIN st_semester_budget b ON r.budget_id = b.id " +
            "INNER JOIN st_subsidy_quota_detail d ON b.quota_detail_id = d.id AND d.economy_category = '1' " +
            "INNER JOIN st_subsidy_quota q ON b.quota_id = q.id AND q.function_category = '3' " +
            "LEFT JOIN st_grades g ON asi.grade_id = g.id " +
            "LEFT JOIN st_class_info c ON asi.clazz_id = c.class_id " +
            "LEFT JOIN st_student_bank_cards bc ON asi.student_id = bc.student_id AND bc.is_primary = 1 " +
            "LEFT JOIN sys_dict_data dict ON dict.dict_type = 'sys_difficulty_type' AND dict.dict_value = asi.difficulty_type_id AND dict.status = '0' " +
            "WHERE 1=1 ";
        
        // 动态添加查询条件
        if (param != null)
        {
            // 按当前学期过滤（根据项目规范：查询需按当前学期过滤）
            if (param.containsKey("currentYearSemesterId"))
            {
                sql += " AND asi.current_year_semester_id = " + param.get("currentYearSemesterId");
            }
            else
            {
                // 默认使用当前学期（如果参数中没有指定）
                sql += " AND asi.current_year_semester_id = asi.current_year_semester_id";
            }
            
            // 如果传入了yearSemesterId参数，也支持按指定学期查询
            if (param.containsKey("yearSemesterId"))
            {
                sql += " AND asi.current_year_semester_id = " + param.get("yearSemesterId");
            }
        }
        
        sql += " GROUP BY asi.student_id, asi.student_name, asi.gender, asi.nation, asi.nation_name, g.name, asi.clazz_name, asi.difficulty_type_id, asi.poverty_relief_year, dict.dict_label, asi.id_card, asi.student_no, asi.domicile, bc.bank_account_no, bc.bank_name, asi.phone, asi.current_year_semester_id, g.id, asi.clazz_id " +
            "ORDER BY g.id ASC, asi.clazz_name ASC, asi.student_name ASC";
        
        log.info("📋 执行助学金发放汇总SQL查询");
        log.debug("SQL: {}", sql);
        
        return getJdbcTemplate().queryForList(sql);
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
     * 转换字典字段为显示文本
     */
    private void translateDictFields(Map<String, Object> record)
    {
        log.debug("🔄 开始转换字典字段，原始数据: {}", record);
        
        // 转换性别
        if (record.containsKey("gender"))
        {
            Object gender = record.get("gender");
            String genderText = DictTranslateUtil.translateGender(gender != null ? gender.toString() : "");
            record.put("gender_text", genderText);
            log.debug("性别转换: {} -> {}", gender, genderText);
        }
        
        // st_aided_student_info表中民族字段已经是nation_name（转换好的名称），不需要再转换
        // 如果record中存在nation字段但没有nation_name，则尝试转换
        if (record.containsKey("nation") && !record.containsKey("nation_name"))
        {
            Object ethnicity = record.get("nation");
            String ethnicityText = DictTranslateUtil.translateEthnicity(ethnicity != null ? ethnicity.toString() : "");
            record.put("nation_name", ethnicityText);  // 存储为nation_name字段
            log.debug("民族转换: {} -> {}", ethnicity, ethnicityText);
        }
        
        // 如果存在nation_name字段，也设置nation_text（兼容性处理）
        if (record.containsKey("nation_name") && !record.containsKey("nation_text"))
        {
            record.put("nation_text", record.get("nation_name"));
            log.debug("民族名称已存在: {}", record.get("nation_name"));
        }
        
        // 转换政治面貌（如果存在）
        if (record.containsKey("political_status"))
        {
            Object politicalStatus = record.get("political_status");
            String politicalStatusText = DictTranslateUtil.translatePoliticalStatus(politicalStatus != null ? politicalStatus.toString() : "");
            record.put("political_status_text", politicalStatusText);
            log.debug("政治面貌转换: {} -> {}", politicalStatus, politicalStatusText);
        }
        
        // 转换困难类型（如果需要）
        if (record.containsKey("difficulty_type"))
        {
            Object difficultyType = record.get("difficulty_type");
            String difficultyTypeText = DictTranslateUtil.translate("difficulty_type", difficultyType);
            record.put("difficulty_type_text", difficultyTypeText);
            log.debug("困难类型转换: {} -> {}", difficultyType, difficultyTypeText);
        }
        
        log.debug("✅ 字典字段转换完成，结果数据: {}", record);
    }
    
    /**
     * 脱敏单个字段（在解密后调用）
     * 使用项目统一的 SensitiveDataUtil 工具类（基于 Hutool）
     */
    private void desensitizeField(Map<String, Object> record, String fieldName)
    {
        Object value = record.get(fieldName);
        if (value != null && value instanceof String)
        {
            String str = (String) value;
            String desensitized = null;
            
            // 支持多种字段名：id_card 和 id_card_no
            if ("id_card".equals(fieldName) || "id_card_no".equals(fieldName))
            {
                // 身份证：使用 Hutool 脱敏，前6后4位
                desensitized = SensitiveDataUtil.maskIdCardNo(str);
            }
            // 支持多种字段名：contact_phone 和 phone
            else if ("contact_phone".equals(fieldName) || "phone".equals(fieldName))
            {
                // 手机号：使用 Hutool 脱敏，前3后4位
                desensitized = SensitiveDataUtil.maskPhone(str);
            }
            else if ("student_no".equals(fieldName))
            {
                // 学籍号：G + 脱敏后的身份证号
                String idCardNo = str.startsWith("G") ? str.substring(1) : str;
                desensitized = SensitiveDataUtil.maskStudentNo(idCardNo);
            }
            // 支持多种字段名：home_address 和 domicile
            else if ("home_address".equals(fieldName) || "domicile".equals(fieldName))
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
}
