-- 查询积木报表ID的SQL脚本
-- 用于查找"普通高中国家助学金申请表"的报表ID

-- ============================================================
-- 方式1：查询所有报表（推荐）
-- ============================================================
SELECT 
    id AS report_id,
    name AS report_name,
    code AS report_code,
    create_time,
    update_time,
    create_by
FROM jimu_report
ORDER BY create_time DESC;

-- ============================================================
-- 方式2：根据报表名称模糊查询
-- ============================================================
SELECT 
    id AS report_id,
    name AS report_name,
    code AS report_code,
    create_time
FROM jimu_report
WHERE name LIKE '%助学金%'
   OR name LIKE '%申请表%'
ORDER BY create_time DESC;

-- ============================================================
-- 方式3：根据报表编码查询（如果知道编码）
-- ============================================================
-- SELECT 
--     id AS report_id,
--     name AS report_name,
--     code AS report_code
-- FROM jimu_report
-- WHERE code = 'your_report_code';

-- ============================================================
-- 方式4：查询最近创建的报表
-- ============================================================
SELECT 
    id AS report_id,
    name AS report_name,
    code AS report_code,
    create_time
FROM jimu_report
ORDER BY create_time DESC
LIMIT 10;

-- ============================================================
-- 说明：
-- 1. 报表ID字段：id（主键，varchar类型）
-- 2. 报表名称字段：name
-- 3. 报表编码字段：code（可选）
-- 4. 执行上述SQL后，找到对应的报表，复制id字段的值
-- 5. 将id值填写到前端代码的reportId字段中
-- ============================================================

