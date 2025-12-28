-- =====================================================
-- 指标表结构更新SQL脚本
-- 说明：简化指标主表，移除冗余字段
-- 执行前请务必备份数据库！
-- =====================================================

-- =====================================================
-- 一、指标主表 (st_subsidy_quota) 字段移除
-- =====================================================

-- 检查并移除已删除的字段
-- 注意：MySQL 5.7+ 支持 IF EXISTS，低版本需要手动检查

-- 1. 移除上级文号字段
ALTER TABLE st_subsidy_quota 
    DROP COLUMN IF EXISTS superior_doc_no;

-- 2. 移除指标文标题字段
ALTER TABLE st_subsidy_quota 
    DROP COLUMN IF EXISTS quota_doc_title;

-- 3. 移除预算项目编码字段
ALTER TABLE st_subsidy_quota 
    DROP COLUMN IF EXISTS budget_project_code;

-- 4. 移除指标摘要字段
ALTER TABLE st_subsidy_quota 
    DROP COLUMN IF EXISTS quota_summary;

-- 5. 移除预算来源字段
ALTER TABLE st_subsidy_quota 
    DROP COLUMN IF EXISTS budget_source;

-- =====================================================
-- 二、索引优化（可选，根据实际查询需求）
-- =====================================================

-- 如果之前没有这些索引，可以添加以提高查询性能

-- 添加指标文号索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_quota_doc_no ON st_subsidy_quota(quota_doc_no);

-- 添加功能分类索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_function_category ON st_subsidy_quota(function_category);

-- 添加经济分类索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_economy_category ON st_subsidy_quota(economy_category);

-- 添加来源指标ID索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_source_quota_id ON st_subsidy_quota(source_quota_id);

-- 添加状态索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_status ON st_subsidy_quota(status);

-- 添加指标来源类型索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_quota_source_type ON st_subsidy_quota(quota_source_type);

-- =====================================================
-- 三、兼容性处理（适用于不支持 IF EXISTS 的MySQL版本）
-- =====================================================

-- 如果您的MySQL版本不支持 IF EXISTS，可以使用以下存储过程方式
-- 或者手动检查字段是否存在后再执行

/*
DELIMITER $$

DROP PROCEDURE IF EXISTS drop_column_if_exists$$

CREATE PROCEDURE drop_column_if_exists(
    IN tableName VARCHAR(128),
    IN columnName VARCHAR(128)
)
BEGIN
    DECLARE columnExists INT DEFAULT 0;
    
    SELECT COUNT(*) INTO columnExists
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = tableName
      AND COLUMN_NAME = columnName;
    
    IF columnExists > 0 THEN
        SET @sql = CONCAT('ALTER TABLE ', tableName, ' DROP COLUMN ', columnName);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

-- 使用存储过程删除字段
CALL drop_column_if_exists('st_subsidy_quota', 'superior_doc_no');
CALL drop_column_if_exists('st_subsidy_quota', 'quota_doc_title');
CALL drop_column_if_exists('st_subsidy_quota', 'budget_project_code');
CALL drop_column_if_exists('st_subsidy_quota', 'quota_summary');
CALL drop_column_if_exists('st_subsidy_quota', 'budget_source');

-- 删除临时存储过程
DROP PROCEDURE IF EXISTS drop_column_if_exists;
*/

-- =====================================================
-- 四、数据验证（执行后验证）
-- =====================================================

-- 验证字段是否已成功删除
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
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

-- 如果上面的查询返回0行，说明字段已成功删除
-- 如果返回有数据，说明字段仍然存在，需要手动处理

-- =====================================================
-- 五、查看当前表结构（验证用）
-- =====================================================

-- 查看指标主表的完整结构
SHOW CREATE TABLE st_subsidy_quota;

-- 或者使用以下SQL查看表结构
DESC st_subsidy_quota;

-- =====================================================
-- 六、回滚脚本（紧急情况使用）
-- =====================================================

-- 如果需要回滚，可以执行以下SQL重新添加字段
-- 注意：回滚后需要手动恢复数据（如果有备份）

/*
-- 回滚：重新添加已删除的字段
ALTER TABLE st_subsidy_quota 
    ADD COLUMN superior_doc_no VARCHAR(100) COMMENT '上级文号' AFTER issue_date;

ALTER TABLE st_subsidy_quota 
    ADD COLUMN quota_doc_title VARCHAR(200) COMMENT '指标文标题' AFTER superior_doc_no;

ALTER TABLE st_subsidy_quota 
    ADD COLUMN budget_project_code VARCHAR(50) COMMENT '预算项目编码' AFTER quota_doc_no;

ALTER TABLE st_subsidy_quota 
    ADD COLUMN quota_summary TEXT COMMENT '指标摘要' AFTER budget_project_name;

ALTER TABLE st_subsidy_quota 
    ADD COLUMN budget_source VARCHAR(20) COMMENT '预算来源' AFTER economy_category;
*/

-- =====================================================
-- 七、执行建议
-- =====================================================

/*
执行步骤：
1. 备份数据库
   mysqldump -u用户名 -p密码 数据库名 > backup_$(date +%Y%m%d_%H%M%S).sql

2. 在测试环境先执行，验证无误后再在生产环境执行

3. 执行顺序：
   a. 先执行字段删除SQL（第一部分）
   b. 执行索引优化SQL（第二部分，可选）
   c. 执行验证SQL（第四部分），确认字段已删除
   d. 查看表结构（第五部分），确认结构正确

4. 如果出现问题，使用回滚脚本（第六部分）恢复

5. 更新完成后，重启应用服务
*/




