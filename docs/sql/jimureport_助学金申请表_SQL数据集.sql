-- ============================================================
-- 积木报表：普通高中国家助学金申请表 - SQL数据集
-- ============================================================
-- 说明：根据积木报表的规范，使用 ${参数名} 定义参数
-- ============================================================

-- ============================================================
-- 数据集1：学生基本信息（主数据集）
-- 数据集编码：student_info
-- 数据集名称：学生基本信息
-- ============================================================
SELECT 
    -- 基本信息
    s.id AS student_id,
    s.name AS name,
    s.id_card_no AS id_card_no,
    s.birthday AS birthday,
    DATE_FORMAT(s.birthday, '%Y年%m月') AS birth_date,
    s.gender AS gender,
    CASE WHEN s.gender = '1' THEN '男' WHEN s.gender = '2' THEN '女' ELSE '' END AS gender_text,
    s.ethnicity AS ethnicity,
    COALESCE(dict_eth.dict_label, s.ethnicity) AS ethnicity_text,
    s.domicile AS home_address,
    s.phone AS phone,
    
    -- 学籍信息
    s.student_no AS student_no,
    s.enrollment_date AS enrollment_date,
    DATE_FORMAT(s.enrollment_date, '%Y年%m月') AS enrollment_date_text,
    
    -- 政治面貌和班级类型（新增字段）
    s.political_status AS political_status,
    s.is_ethnic_class AS is_ethnic_class,
    CASE WHEN s.is_ethnic_class = '1' THEN '是' ELSE '否' END AS is_ethnic_class_text,
    
    -- 年级班级信息
    s.grade_id AS grade_id,
    s.class_id AS class_id,
    g.id AS grade_table_id,
    g.name AS grade_name,
    c.class_id AS class_table_id,
    c.class_name AS class_name,
    CONCAT(IFNULL(g.name, ''), '/', IFNULL(c.class_name, '')) AS grade_class,
    
    -- 银行卡信息
    b.id AS bank_card_id,
    b.bank_account_no AS bank_card_no,
    b.bank_name AS bank_name,
    b.branch_name AS bank_branch_name,
    b.account_holder AS bank_account_holder,
    
    -- 其他信息
    s.schooling_plan_id AS schooling_plan_id,
    s.study_status AS study_status,
    s.difficulty_type_id AS difficulty_type_id,
    COALESCE(dict_difficulty.dict_label, '') AS difficulty_type_text,
    s.difficulty_level_id AS difficulty_level_id,
    COALESCE(dict_level.dict_label, '') AS difficulty_level_text,
    s.is_poverty_relief_family AS is_poverty_relief_family,
    s.poverty_relief_year AS poverty_relief_year,
    s.memo AS memo,
    s.current_year_semester_id AS current_year_semester_id,
    
    -- 申请理由相关字段（用于复选框判断）
    -- 注意：需要根据实际的困难类型字典值来调整判断条件
    CASE WHEN s.is_poverty_relief_family = '1' THEN '√' ELSE '□' END AS reason_1_poverty_relief,
    CASE WHEN s.is_poverty_relief_family = '1' AND s.poverty_relief_year IS NOT NULL 
         THEN CONCAT(s.poverty_relief_year, '年') 
         ELSE '' 
    END AS poverty_relief_year_text,
    
    -- 脱贫年份复选框（各年份单独字段）
    CASE WHEN s.poverty_relief_year = 2014 THEN '√' ELSE '□' END AS year_2014_check,
    CASE WHEN s.poverty_relief_year = 2015 THEN '√' ELSE '□' END AS year_2015_check,
    CASE WHEN s.poverty_relief_year = 2016 THEN '√' ELSE '□' END AS year_2016_check,
    CASE WHEN s.poverty_relief_year = 2017 THEN '√' ELSE '□' END AS year_2017_check,
    CASE WHEN s.poverty_relief_year = 2018 THEN '√' ELSE '□' END AS year_2018_check,
    CASE WHEN s.poverty_relief_year = 2019 THEN '√' ELSE '□' END AS year_2019_check,
    CASE WHEN s.poverty_relief_year = 2020 THEN '√' ELSE '□' END AS year_2020_check,
    
    -- 根据困难类型判断各种申请理由（需要根据实际字典值调整）
    CASE WHEN s.difficulty_type_id LIKE '%监测%' OR s.difficulty_type_id LIKE '%monitor%' THEN '√' ELSE '□' END AS reason_2_monitor,
    CASE WHEN s.difficulty_type_id LIKE '%低保%' OR s.difficulty_type_id LIKE '%low_income%' THEN '√' ELSE '□' END AS reason_3_low_income,
    CASE WHEN s.difficulty_type_id LIKE '%特困%' OR s.difficulty_type_id LIKE '%special%' THEN '√' ELSE '□' END AS reason_4_special_hardship,
    CASE WHEN s.difficulty_type_id LIKE '%残疾%' OR s.difficulty_type_id LIKE '%disabled%' THEN '√' ELSE '□' END AS reason_5_disabled,
    CASE WHEN s.difficulty_type_id LIKE '%孤儿%' OR s.difficulty_type_id LIKE '%orphan%' THEN '√' ELSE '□' END AS reason_6_orphan,
    CASE WHEN s.difficulty_type_id LIKE '%烈士%' OR s.difficulty_type_id LIKE '%martyr%' THEN '√' ELSE '□' END AS reason_7_martyr,
    CASE WHEN s.difficulty_type_id LIKE '%职工%' OR s.difficulty_type_id LIKE '%worker%' THEN '√' ELSE '□' END AS reason_8_worker,
    CASE WHEN s.difficulty_type_id LIKE '%边缘%' OR s.difficulty_type_id LIKE '%margin%' THEN '√' ELSE '□' END AS reason_9_margin,
    CASE WHEN s.difficulty_type_id LIKE '%支出%' OR s.difficulty_type_id LIKE '%expenditure%' THEN '√' ELSE '□' END AS reason_10_expenditure,
    CASE WHEN s.is_ethnic_class = '1' THEN '√' ELSE '□' END AS reason_11_ethnic_class,
    
    -- 其他原因（如果困难类型不在上述类型中，且不为空，则显示）
    CASE WHEN s.difficulty_type_id IS NOT NULL 
              AND s.difficulty_type_id != '' 
              AND s.difficulty_type_id NOT LIKE '%脱贫%'
              AND s.difficulty_type_id NOT LIKE '%监测%'
              AND s.difficulty_type_id NOT LIKE '%低保%'
              AND s.difficulty_type_id NOT LIKE '%特困%'
              AND s.difficulty_type_id NOT LIKE '%残疾%'
              AND s.difficulty_type_id NOT LIKE '%孤儿%'
              AND s.difficulty_type_id NOT LIKE '%烈士%'
              AND s.difficulty_type_id NOT LIKE '%职工%'
              AND s.difficulty_type_id NOT LIKE '%边缘%'
              AND s.difficulty_type_id NOT LIKE '%支出%'
         THEN '√' 
         ELSE '□' 
    END AS reason_12_other,
    
    -- 其他原因的具体说明
    CASE WHEN s.difficulty_type_id IS NOT NULL 
              AND s.difficulty_type_id != '' 
              AND s.difficulty_type_id NOT LIKE '%脱贫%'
              AND s.difficulty_type_id NOT LIKE '%监测%'
              AND s.difficulty_type_id NOT LIKE '%低保%'
              AND s.difficulty_type_id NOT LIKE '%特困%'
              AND s.difficulty_type_id NOT LIKE '%残疾%'
              AND s.difficulty_type_id NOT LIKE '%孤儿%'
              AND s.difficulty_type_id NOT LIKE '%烈士%'
              AND s.difficulty_type_id NOT LIKE '%职工%'
              AND s.difficulty_type_id NOT LIKE '%边缘%'
              AND s.difficulty_type_id NOT LIKE '%支出%'
         THEN IFNULL(s.memo, COALESCE(dict_difficulty.dict_label, s.difficulty_type_id))
         ELSE '' 
    END AS other_reason_text,
    
    -- 报表系统字段
    DATE_FORMAT(NOW(), '%Y年%m月%d日') AS fill_date,
    DATE_FORMAT(NOW(), '%Y-%m-%d') AS fill_date_standard,
    'XX中学' AS school_name
    
FROM st_students_base s
LEFT JOIN st_grades g ON s.grade_id = g.id
LEFT JOIN st_class_info c ON s.class_id = c.class_id
LEFT JOIN st_student_bank_cards b ON s.id = b.student_id AND b.is_primary = 1 AND b.status = 0
LEFT JOIN sys_dict_data dict_eth ON dict_eth.dict_type = 'sys_student_ethnicity' AND dict_eth.dict_value = s.ethnicity AND dict_eth.status = '0'
LEFT JOIN sys_dict_data dict_difficulty ON dict_difficulty.dict_type = 'sys_difficulty_type' AND dict_difficulty.dict_value = s.difficulty_type_id AND dict_difficulty.status = '0'
LEFT JOIN sys_dict_data dict_level ON dict_level.dict_type = 'sys_difficulty_level' AND dict_level.dict_value = s.difficulty_level_id AND dict_level.status = '0'
WHERE s.id = ${studentId}

-- ============================================================
-- 数据集2：家庭成员信息（用于表格循环）
-- 数据集编码：family_members
-- 数据集名称：家庭成员信息
-- ============================================================
SELECT 
    fm.name AS family_name,
    fm.age AS family_age,
    fm.relation AS relationship,
    IFNULL(fm.employer, '') AS work_unit
FROM st_family_members fm
WHERE fm.student_id = ${studentId}
ORDER BY fm.id

-- ============================================================
-- 参数说明：
-- studentId: 学生ID（必填参数，类型：字符串或数字）
-- ============================================================

-- ============================================================
-- 使用说明：
-- 1. 在积木报表中创建第一个数据集（学生基本信息）
--    编码：student_info
--    名称：学生基本信息
--    是否集合：✓（勾选）
--    是否分页：✗（不勾选）
--    SQL：复制上面的第一个SELECT语句
--
-- 2. 在积木报表中创建第二个数据集（家庭成员信息）
--    编码：family_members
--    名称：家庭成员信息
--    是否集合：✓（勾选）
--    是否分页：✗（不勾选）
--    SQL：复制上面的第二个SELECT语句
--
-- 3. 配置报表参数
--    在"报表参数" tab中，添加参数：
--    - 参数名：studentId
--    - 参数文本：学生ID
--    - 参数类型：字符串
--    - 是否必填：是
--
-- 4. 点击"SQL解析"按钮，自动解析字段
--
-- 5. 在报表设计器中：
--    - 使用 student_info 数据集的字段显示学生基本信息
--    - 使用 family_members 数据集创建表格循环显示家庭成员
-- ============================================================


