-- ============================================================
-- 积木报表：国家助学金项目拟受助学生名单 - SQL数据集
-- ============================================================
-- 说明：根据积木报表的规范，使用 ${参数名} 定义参数
-- ============================================================

-- ============================================================
-- 数据集：国家助学金拟受助学生名单
-- 数据集编码：student_aid_list
-- 数据集名称：国家助学金拟受助学生名单
-- ============================================================
SELECT 

    1 AS serial_number_placeholder,
    
    -- 学校名称（可以从系统配置获取，这里使用固定值或参数）
    IFNULL(${schoolName}, 'XX中学') AS school_name,
    
    -- 学生基本信息
    sb.id AS student_id,
    sb.name AS student_name,
    CASE 
        WHEN sb.gender = '1' THEN '男' 
        WHEN sb.gender = '2' THEN '女' 
        ELSE '' 
    END AS gender,
    
    -- 班级信息
    IFNULL(c.class_name, '') AS class_name,
    IFNULL(g.name, '') AS grade_name,
    CONCAT(IFNULL(g.name, ''), '/', IFNULL(c.class_name, '')) AS grade_class,
    
    -- 受助等次：直接根据套餐类型判断，不再使用困难等级
    CASE 
        -- 优先使用套餐类型（package_type）判断
        WHEN p.package_type LIKE '%一档%' OR p.package_type LIKE '%1档%' OR p.package_type LIKE '%一等%' OR p.package_type LIKE '%1等%' THEN '一等'
        WHEN p.package_type LIKE '%二档%' OR p.package_type LIKE '%2档%' OR p.package_type LIKE '%二等%' OR p.package_type LIKE '%2等%' THEN '二等'
        WHEN p.package_type LIKE '%三档%' OR p.package_type LIKE '%3档%' OR p.package_type LIKE '%三等%' OR p.package_type LIKE '%3等%' THEN '三等'
        -- 如果套餐类型为空，则从补助类型（subsidy_type）判断
        WHEN r.subsidy_type LIKE '%一档%' OR r.subsidy_type LIKE '%1档%' OR r.subsidy_type LIKE '%一等%' OR r.subsidy_type LIKE '%1等%' THEN '一等'
        WHEN r.subsidy_type LIKE '%二档%' OR r.subsidy_type LIKE '%2档%' OR r.subsidy_type LIKE '%二等%' OR r.subsidy_type LIKE '%2等%' THEN '二等'
        WHEN r.subsidy_type LIKE '%三档%' OR r.subsidy_type LIKE '%3档%' OR r.subsidy_type LIKE '%三等%' OR r.subsidy_type LIKE '%3等%' THEN '三等'
        -- 如果都没有，返回空字符串
        ELSE ''
    END AS subsidy_level,
    
    -- 受助金额
    IFNULL(r.subsidy_amount, 0) AS subsidy_amount,
    
    -- 备注
    IFNULL(r.memo, '') AS remark,
    
    -- 学年学期信息（用于报表标题）
    IFNULL(ys.school_year, '') AS school_year,
    IFNULL(ys.semester, '') AS semester,
    CONCAT(
        IFNULL(ys.school_year, ''), 
        '年 ', 
        CASE 
            WHEN ys.semester = '1' THEN '第一'
            WHEN ys.semester = '2' THEN '第二'
            ELSE IFNULL(ys.semester, '')
        END,
        '学期'
    ) AS year_semester_text
    
FROM (
    SELECT 
        r.*,
        -- 优先使用student_id，如果没有则通过student_semester_record_id关联
        COALESCE(r.student_id, ssr.student_base_id) AS final_student_id
    FROM st_student_subsidy_records r
    -- 如果student_id为空，则通过student_semester_record_id关联学生学期记录
    LEFT JOIN st_student_semester_records ssr ON r.student_id IS NULL AND r.student_semester_record_id = ssr.id
    WHERE 1=1
        -- 只查询已审批通过的记录
        AND r.approval_status = 1
        -- 学年学期筛选（如果传入了yearSemesterId参数）
        AND (
            '${yearSemesterId}' = '' 
            OR '${yearSemesterId}' IS NULL 
            OR r.year_semester_id = '${yearSemesterId}'
            OR EXISTS (
                SELECT 1 FROM st_semester_budget b 
                WHERE b.id = r.budget_id 
                AND b.year_semester_id = '${yearSemesterId}'
            )
        )
) r
-- 关联学生基础信息（支持两种关联方式）
INNER JOIN st_students_base sb ON (
    r.final_student_id = sb.id 
    OR (r.student_id IS NULL AND r.student_semester_record_id = sb.id)
)
-- 关联班级信息
LEFT JOIN st_class_info c ON sb.class_id = c.class_id
-- 关联年级信息
LEFT JOIN st_grades g ON sb.grade_id = g.id
-- 关联学期预算
LEFT JOIN st_semester_budget b ON r.budget_id = b.id
-- 关联学年学期（先关联，用于后续匹配受助学生信息）
LEFT JOIN st_school_year_semester ys ON COALESCE(r.year_semester_id, b.year_semester_id) = ys.id
-- 关联受助学生信息表（已不再用于受助等次判断，保留用于其他字段）
LEFT JOIN st_aided_student_info asi ON (
    asi.student_id = sb.id 
    AND (
        -- 匹配学年学期（优先使用学年学期表的字段）
        (ys.school_year IS NOT NULL AND ys.semester IS NOT NULL 
         AND asi.academic_year = ys.school_year AND asi.semester = ys.semester)
        -- 如果没有学年学期表数据，则使用参数匹配
        OR ('${schoolYear}' != '' AND '${schoolYear}' IS NOT NULL 
            AND asi.academic_year = '${schoolYear}'
            AND ('${semester}' = '' OR '${semester}' IS NULL OR asi.semester = '${semester}'))
        -- 如果都没有，则取该学生最新的受助记录
        OR (ys.school_year IS NULL AND ('${schoolYear}' = '' OR '${schoolYear}' IS NULL))
    )
)
-- 关联困难等级字典（已不再用于受助等次判断，保留用于其他字段）
LEFT JOIN sys_dict_data dict_level ON (
    dict_level.dict_type = 'sys_difficulty_level' 
    AND dict_level.dict_value = COALESCE(asi.difficulty_level_id, sb.difficulty_level_id)
    AND dict_level.status = '0'
)
-- 关联配额明细（用于判断经济分类）
LEFT JOIN st_subsidy_quota_detail d ON b.quota_detail_id = d.id
-- 关联补助套餐（用于获取套餐类型）
LEFT JOIN st_subsidy_package p ON r.package_id = p.id
WHERE 1=1
    -- 只查询助学金（经济分类=1或'助学金'）
    AND (d.economy_category = '1' OR d.economy_category = '助学金')
    -- 学年筛选（如果传入了schoolYear参数）
    AND (
        '${schoolYear}' = '' 
        OR '${schoolYear}' IS NULL 
        OR ys.school_year = '${schoolYear}'
    )
    -- 学期筛选（如果传入了semester参数）
    AND (
        '${semester}' = '' 
        OR '${semester}' IS NULL 
        OR ys.semester = '${semester}'
    )
ORDER BY 
    IFNULL(c.class_name, '') ASC,
    sb.name ASC

-- ============================================================
-- 参数说明：
-- ============================================================
-- yearSemesterId: 学年学期ID（可选参数，类型：数字）
--                 如果传入，则只查询该学年学期的数据
--                 如果不传或为空，则查询所有学年学期
--
-- schoolYear: 学年（可选参数，类型：字符串，格式：2024-2025）
--             如果传入，则只查询该学年的数据
--
-- semester: 学期（可选参数，类型：字符串，值：1或2）
--           如果传入，则只查询该学期的数据
--
-- schoolName: 学校名称（可选参数，类型：字符串）
--             如果传入，则使用该值作为学校名称
--             如果不传，则使用默认值'XX中学'
-- ============================================================

-- ============================================================
-- 使用说明：
-- ============================================================
-- 1. 在积木报表中创建数据集
--    编码：student_aid_list
--    名称：国家助学金拟受助学生名单
--    是否集合：✓（勾选）
--    是否分页：✗（不勾选）
--    SQL：复制上面的SELECT语句
--
-- 2. 配置报表参数（可选）
--    在"报表参数" tab中，添加参数：
--    
--    a) 学年学期ID（可选）
--       - 参数名：yearSemesterId
--       - 参数文本：学年学期ID
--       - 参数类型：字符串
--       - 是否必填：否
--    
--    b) 学年（可选）
--       - 参数名：schoolYear
--       - 参数文本：学年
--       - 参数类型：字符串
--       - 是否必填：否
--    
--    c) 学期（可选）
--       - 参数名：semester
--       - 参数文本：学期
--       - 参数类型：字符串
--       - 是否必填：否
--    
--    d) 学校名称（可选）
--       - 参数名：schoolName
--       - 参数文本：学校名称
--       - 参数类型：字符串
--       - 是否必填：否
--       - 默认值：XX中学
--
-- 3. 点击"SQL解析"按钮，自动解析字段
--
-- 4. 在报表设计器中设计报表：
--    
--    a) 报表标题：
--       使用 year_semester_text 字段显示"20XX-20XX年 第X学期"
--       例如：{{year_semester_text}}国家助学金项目拟受助学生名单
--    
--    b) 表头设计：
--       - A列：学校（使用 school_name 字段）
--       - B列：序号（使用 serial_number 字段）
--       - C列：姓名（使用 student_name 字段）
--       - D列：性别（使用 gender 字段）
--       - E列：班别（使用 class_name 字段）
--       - F列：受助等次（使用 subsidy_level 字段）
--       - G列：受助金额(元)（使用 subsidy_amount 字段）
--       - H列：备注（使用 remark 字段）
--    
--    c) 数据行设计：
--       使用数据集 student_aid_list 创建表格循环
--       将字段拖拽到对应的单元格中
--    
--    d) 数据汇总（可选）：
--       在表格底部添加汇总行，使用 SUM(subsidy_amount) 计算总金额
--
-- 5. 报表预览：
--    可以通过传入不同的参数来筛选不同学年学期的数据
-- ============================================================

-- ============================================================
-- 简化版SQL（如果上述SQL太复杂，可以使用这个版本）
-- ============================================================
-- 注意：简化版假设补助记录表已经有student_id字段直接关联学生
-- 如果您的表结构不同，请使用上面的完整版SQL
/*
SELECT 
    -- 序号（在报表设计器中使用表达式 {{@index + 1}} 生成）
    1 AS serial_number,
    
    -- 学校名称
    IFNULL('${schoolName}', 'XX中学') AS school_name,
    
    -- 学生基本信息
    sb.id AS student_id,
    sb.name AS student_name,
    CASE 
        WHEN sb.gender = '1' THEN '男' 
        WHEN sb.gender = '2' THEN '女' 
        ELSE '' 
    END AS gender,
    
    -- 班级信息
    IFNULL(c.class_name, '') AS class_name,
    
    -- 受助等次
    CASE 
        WHEN r.subsidy_type LIKE '%一档%' OR r.subsidy_type LIKE '%1档%' THEN '一档'
        WHEN r.subsidy_type LIKE '%二档%' OR r.subsidy_type LIKE '%2档%' THEN '二档'
        WHEN r.subsidy_type LIKE '%三档%' OR r.subsidy_type LIKE '%3档%' THEN '三档'
        ELSE IFNULL(r.subsidy_type, '')
    END AS subsidy_level,
    
    -- 受助金额
    IFNULL(r.subsidy_amount, 0) AS subsidy_amount,
    
    -- 备注
    IFNULL(r.memo, '') AS remark,
    
    -- 学年学期信息
    IFNULL(ys.school_year, '') AS school_year,
    IFNULL(ys.semester, '') AS semester
    
FROM st_student_subsidy_records r
INNER JOIN st_students_base sb ON r.student_id = sb.id
LEFT JOIN st_class_info c ON sb.class_id = c.class_id
LEFT JOIN st_semester_budget b ON r.budget_id = b.id
LEFT JOIN st_school_year_semester ys ON b.year_semester_id = ys.id
LEFT JOIN st_subsidy_quota_detail d ON b.quota_detail_id = d.id
WHERE r.approval_status = 1
    AND (d.economy_category = '1' OR d.economy_category = '助学金')
    AND ('${yearSemesterId}' = '' OR b.year_semester_id = '${yearSemesterId}')
ORDER BY c.class_name ASC, sb.name ASC
*/

-- ============================================================
-- 注意事项：
-- ============================================================
-- 1. 序号字段：在报表设计器中使用表达式 {{@index + 1}} 或 {{rowIndex + 1}} 自动生成
--    或者使用积木报表的"序号"组件
-- 2. 受助等次：直接根据套餐类型（package_type）判断，不再使用困难等级
--    - 优先从补助记录关联的套餐表（st_subsidy_package）获取套餐类型
--    - 套餐类型包含"一档"、"二档"、"三档"或"一等"、"二等"、"三等"等关键词
--    - 如果套餐类型为空，则从补助类型（subsidy_type）中提取
--    - 映射关系：一档/一等 -> 一等，二档/二等 -> 二等，三档/三等 -> 三等
-- 3. 只查询已审批通过（approval_status=1）的助学金记录
-- 4. 如果不需要筛选，可以不传参数，查询所有数据
-- 5. 学校名称可以通过参数传入，也可以在设计器中直接修改
-- 6. 如果您的表结构中没有student_id字段，请使用完整版SQL（上面的版本）
-- 7. 参数处理：积木报表中，如果参数为空，${param} 会返回空字符串，需要用条件判断
-- ============================================================

