SELECT 
    @row_number := @row_number + 1 AS serial_number,
    'XX中学' AS school_name,
    sb.id AS student_id,
    sb.name AS student_name,
    CASE 
        WHEN sb.gender = '1' THEN '男' 
        WHEN sb.gender = '2' THEN '女' 
        ELSE NULL 
    END AS gender,
    COALESCE(c.class_name, '') AS class_name,
    COALESCE(g.name, '') AS grade_name,
    CONCAT(COALESCE(g.name, ''), '/', COALESCE(c.class_name, '')) AS grade_class,
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
    COALESCE(r.subsidy_amount, 0) AS subsidy_amount,
    COALESCE(r.memo, '') AS remark,
    COALESCE(ys.school_year, '') AS school_year,
    COALESCE(ys.semester, '') AS semester,
    CONCAT(
        COALESCE(ys.school_year, ''), 
        '年 ', 
        CASE 
            WHEN ys.semester = '1' THEN '第一'
            WHEN ys.semester = '2' THEN '第二'
            ELSE COALESCE(ys.semester, '')
        END,
        '学期'
    ) AS year_semester_text
FROM (SELECT @row_number := 0) AS rn
CROSS JOIN st_student_subsidy_records r
INNER JOIN st_students_base sb ON r.student_id = sb.id
LEFT JOIN st_class_info c ON sb.class_id = c.class_id
LEFT JOIN st_grades g ON sb.grade_id = g.id
LEFT JOIN st_semester_budget b ON r.budget_id = b.id
LEFT JOIN st_school_year_semester ys ON COALESCE(r.year_semester_id, b.year_semester_id) = ys.id
LEFT JOIN st_aided_student_info asi ON (
    asi.student_id = sb.id 
    AND (
        (ys.school_year IS NOT NULL AND ys.semester IS NOT NULL 
         AND asi.academic_year = ys.school_year AND asi.semester = ys.semester)
        OR (ys.school_year IS NULL)
    )
)
LEFT JOIN sys_dict_data dict_level ON (
    dict_level.dict_type = 'sys_difficulty_level' 
    AND dict_level.dict_value = COALESCE(asi.difficulty_level_id, sb.difficulty_level_id)
    AND dict_level.status = '0'
)
LEFT JOIN st_subsidy_quota_detail d ON b.quota_detail_id = d.id
LEFT JOIN st_subsidy_package p ON r.package_id = p.id
WHERE r.approval_status = 1
    AND (d.economy_category = '1' OR d.economy_category = '助学金')
ORDER BY COALESCE(c.class_name, '') ASC, sb.name ASC

