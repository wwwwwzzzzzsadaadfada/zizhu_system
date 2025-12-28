SELECT 
    CAST(@row_number := @row_number + 1 AS UNSIGNED) AS serial_number,
    t.school_name,
    t.student_id,
    t.student_name,
    t.gender,
    t.class_name,
    t.grade_name,
    t.class_name_full,
    t.subsidy_level,
    t.subsidy_amount,
    t.remark,
    t.school_year,
    t.semester,
    t.year_semester_text
FROM (
    SELECT 
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
        CONCAT(COALESCE(g.name, ''), COALESCE(c.class_name, '')) AS class_name_full,
        CASE 
            WHEN p.package_type LIKE '%一档%' OR p.package_type LIKE '%1档%' OR p.package_type LIKE '%一等%' OR p.package_type LIKE '%1等%' THEN '一等'
            WHEN p.package_type LIKE '%二档%' OR p.package_type LIKE '%2档%' OR p.package_type LIKE '%二等%' OR p.package_type LIKE '%2等%' THEN '二等'
            WHEN p.package_type LIKE '%三档%' OR p.package_type LIKE '%3档%' OR p.package_type LIKE '%三等%' OR p.package_type LIKE '%3等%' THEN '三等'
            WHEN r.subsidy_type LIKE '%一档%' OR r.subsidy_type LIKE '%1档%' OR r.subsidy_type LIKE '%一等%' OR r.subsidy_type LIKE '%1等%' THEN '一等'
            WHEN r.subsidy_type LIKE '%二档%' OR r.subsidy_type LIKE '%2档%' OR r.subsidy_type LIKE '%二等%' OR r.subsidy_type LIKE '%2等%' THEN '二等'
            WHEN r.subsidy_type LIKE '%三档%' OR r.subsidy_type LIKE '%3档%' OR r.subsidy_type LIKE '%三等%' OR r.subsidy_type LIKE '%3等%' THEN '三等'
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
    FROM st_student_subsidy_records r
    INNER JOIN st_students_base sb ON r.student_id = sb.id
    LEFT JOIN st_class_info c ON sb.class_id = c.class_id
    LEFT JOIN st_grades g ON sb.grade_id = g.id
    LEFT JOIN st_semester_budget b ON r.budget_id = b.id
    LEFT JOIN st_school_year_semester ys ON COALESCE(r.year_semester_id, b.year_semester_id) = ys.id
    LEFT JOIN st_subsidy_quota_detail d ON b.quota_detail_id = d.id
    LEFT JOIN st_subsidy_package p ON r.package_id = p.id
    WHERE r.approval_status = 1
        AND (d.economy_category = '1' OR d.economy_category = '助学金')
    ORDER BY COALESCE(c.class_name, '') ASC, sb.name ASC
) t
CROSS JOIN (SELECT @row_number := 0) AS rn

