-- =====================================================
-- 指标表结构更新SQL（简化版）
-- 执行前请务必备份数据库！
-- =====================================================

-- 方式一：MySQL 8.0+ 支持 IF EXISTS（推荐）
-- =====================================================

-- 删除已移除的字段
ALTER TABLE st_subsidy_quota DROP COLUMN IF EXISTS superior_doc_no;
ALTER TABLE st_subsidy_quota DROP COLUMN IF EXISTS quota_doc_title;
ALTER TABLE st_subsidy_quota DROP COLUMN IF EXISTS budget_project_code;
ALTER TABLE st_subsidy_quota DROP COLUMN IF EXISTS quota_summary;
ALTER TABLE st_subsidy_quota DROP COLUMN IF EXISTS budget_source;

-- =====================================================
-- 方式二：MySQL 5.7 及以下版本（不支持 IF EXISTS）
-- =====================================================

-- 如果您的MySQL版本不支持 IF EXISTS，请手动检查字段是否存在
-- 或者使用以下方式（需要先检查）：

/*
-- 检查字段是否存在
SELECT COLUMN_NAME 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'st_subsidy_quota' 
  AND COLUMN_NAME = 'superior_doc_no';

-- 如果存在，则执行删除
ALTER TABLE st_subsidy_quota DROP COLUMN superior_doc_no;
ALTER TABLE st_subsidy_quota DROP COLUMN quota_doc_title;
ALTER TABLE st_subsidy_quota DROP COLUMN budget_project_code;
ALTER TABLE st_subsidy_quota DROP COLUMN quota_summary;
ALTER TABLE st_subsidy_quota DROP COLUMN budget_source;
*/

-- =====================================================
-- 验证SQL（执行后验证字段是否已删除）
-- =====================================================

-- 验证：查询应该返回0行
SELECT COLUMN_NAME 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'st_subsidy_quota' 
  AND COLUMN_NAME IN (
    'superior_doc_no',
    'quota_doc_title', 
    'budget_project_code',
    'quota_summary',
    'budget_source'
  );

-- =====================================================
-- 查看更新后的表结构
-- =====================================================

DESC st_subsidy_quota;

-- 或者
SHOW CREATE TABLE st_subsidy_quota;




