-- ============================================================
-- 积木报表：报表标题数据集（单独数据集，只返回一条记录）
-- ============================================================
-- 数据集编码：report_title
-- 数据集名称：报表标题信息
-- 是否集合：✓（勾选）
-- 是否分页：✗（不勾选）
-- ============================================================

SELECT 
    CONCAT(
        COALESCE(ys.school_year, ''), 
        '年 ', 
        CASE 
            WHEN ys.semester = '1' THEN '第一'
            WHEN ys.semester = '2' THEN '第二'
            ELSE COALESCE(ys.semester, '')
        END,
        '学期'
    ) AS year_semester_text,
    'XX中学' AS school_name,
    COALESCE(ys.school_year, '') AS school_year,
    COALESCE(ys.semester, '') AS semester
FROM st_student_subsidy_records r
LEFT JOIN st_semester_budget b ON r.budget_id = b.id
LEFT JOIN st_school_year_semester ys ON COALESCE(r.year_semester_id, b.year_semester_id) = ys.id
WHERE r.approval_status = 1
    AND EXISTS (
        SELECT 1 FROM st_subsidy_quota_detail d 
        WHERE d.id = b.quota_detail_id 
        AND (d.economy_category = '1' OR d.economy_category = '助学金')
    )
ORDER BY ys.school_year DESC, ys.semester DESC
LIMIT 1

-- ============================================================
-- 使用说明：
-- ============================================================
-- 1. 在积木报表中创建新数据集
--    编码：report_title
--    名称：报表标题信息
--    是否集合：✓（勾选）
--    是否分页：✗（不勾选）
--
-- 2. 这个数据集只返回一条记录，用于显示标题
--    不会循环，所以标题只会显示一次
--
-- 3. 在标题单元格中使用：
--    #{report_title.year_semester_text}国家助学金项目拟受助学生名单
--
-- 4. 在学校单元格中使用：
--    #{report_title.school_name}
-- ============================================================

