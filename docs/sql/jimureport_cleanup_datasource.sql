-- 积木报表数据源清理和配置脚本
-- 说明：此脚本会清理不需要的数据源，只保留指向当前系统数据库（ry-vue）的数据源

-- 1. 删除所有旧的数据源配置（可选，如果只想保留默认数据源）
-- DELETE FROM jimu_report_data_source;

-- 2. 或者只删除指向其他数据库的数据源，保留指向 ry-vue 的数据源
-- 删除指向其他数据库的数据源（保留指向 ry-vue 的）
DELETE FROM jimu_report_data_source 
WHERE db_url NOT LIKE '%ry-vue%' 
  AND db_url NOT LIKE '%localhost:3306/ry-vue%'
  AND db_url NOT LIKE '%127.0.0.1:3306/ry-vue%';

-- 3. 确保 default_datasource 存在且密码正确
-- 注意：积木报表的密码如果是加密的（以 @JmAES 或 @JimuReportS 开头），需要在设计器中修改
-- 这里我们使用明文密码，积木报表会自动加密

-- 先删除旧的 default_datasource（如果存在）
DELETE FROM jimu_report_data_source WHERE id = 'default_datasource';

-- 插入/更新 default_datasource（使用明文密码，积木报表会自动加密）
INSERT INTO jimu_report_data_source (
    id,
    name,
    remark,
    db_type,
    db_driver,
    db_url,
    db_username,
    db_password,
    create_by,
    create_time,
    update_by,
    update_time,
    connect_times,
    tenant_id,
    type
) VALUES (
    'default_datasource',
    '系统默认数据源',
    '系统主数据源，用于积木报表',
    'mysql',
    'com.mysql.cj.jdbc.Driver',
    -- 数据库连接URL（已根据 application-druid.yml 配置）
    'jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8',
    -- 数据库用户名（已根据 application-druid.yml 配置）
    'root',
    -- 数据库密码（已根据 application-druid.yml 配置，如果不同请修改）
    'wzt123456',
    'admin',
    NOW(),
    'admin',
    NOW(),
    0,
    NULL,
    NULL
);

-- 4. 查询结果
SELECT '数据源清理完成！' AS result;
SELECT id, name, db_type, db_url, db_username, 
       CASE 
           WHEN db_password LIKE '@%' THEN '已加密'
           ELSE '明文'
       END AS password_status
FROM jimu_report_data_source 
ORDER BY create_time DESC;

