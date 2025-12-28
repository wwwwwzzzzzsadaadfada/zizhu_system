-- 检查积木报表数据的SQL脚本
-- 用于排查"获取不到模板"的问题

-- 1. 检查jimu_report表是否存在
SELECT 
    'jimu_report表存在' AS check_result,
    COUNT(*) AS table_exists
FROM information_schema.tables 
WHERE table_schema = DATABASE() 
  AND table_name = 'jimu_report';

-- 2. 查看jimu_report表结构
DESC jimu_report;

-- 3. 查看所有报表（不限制状态）
SELECT 
    id,
    name,
    code,
    status,
    create_time,
    update_time
FROM jimu_report
ORDER BY create_time DESC;

-- 4. 查看启用状态的报表（status = '1'）
SELECT 
    id,
    name,
    code,
    status,
    create_time
FROM jimu_report
WHERE status = '1'
ORDER BY create_time DESC;

-- 5. 统计报表数量
SELECT 
    COUNT(*) AS total_count,
    SUM(CASE WHEN status = '1' THEN 1 ELSE 0 END) AS enabled_count,
    SUM(CASE WHEN status = '0' THEN 1 ELSE 0 END) AS disabled_count
FROM jimu_report;

-- 6. 如果表不存在，提示创建表的SQL（仅供参考，实际表结构可能不同）
-- 注意：这个SQL仅供参考，实际表结构需要根据积木报表的初始化脚本确定
/*
CREATE TABLE IF NOT EXISTS jimu_report (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(200) NOT NULL COMMENT '报表名称',
    code VARCHAR(100) COMMENT '报表编码',
    status CHAR(1) DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积木报表表';
*/

