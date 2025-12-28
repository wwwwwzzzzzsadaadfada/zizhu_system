-- 积木报表默认数据源初始化脚本
-- 如果遇到 NullPointerException，可能是因为 jimu_report_data_source 表中没有数据源配置
-- 执行此脚本可以添加一个默认的数据源配置

-- 注意：需要根据实际的数据库连接信息修改以下配置
-- 请根据 application-druid.yml 中的配置修改 db_url、db_username、db_password

-- 检查表是否存在（如果表不存在，请先执行完整的 jimureport.sql）
-- 如果执行报错"表不存在"，说明需要先执行完整的 jimureport.sql 脚本

-- 插入默认数据源配置
-- 请根据实际情况修改以下配置：
-- 1. db_url: 数据库连接URL（参考 application-druid.yml 中的 spring.datasource.druid.master.url）
-- 2. db_username: 数据库用户名（参考 application-druid.yml 中的 spring.datasource.druid.master.username）
-- 3. db_password: 数据库密码（参考 application-druid.yml 中的 spring.datasource.druid.master.password）

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
    update_time
)
SELECT 
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
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM jimu_report_data_source WHERE id = 'default_datasource'
);

-- 查询结果
SELECT '默认数据源配置完成！' AS result;
SELECT id, name, db_type, db_url, db_username FROM jimu_report_data_source WHERE id = 'default_datasource';

