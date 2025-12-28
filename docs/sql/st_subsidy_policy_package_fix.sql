-- 修改政策与套餐关联表的package_id字段类型，使其与st_subsidy_package表的主键类型一致
-- 注意：如果表中已有数据，请先备份数据

-- 方案一：如果外键已存在，先删除外键约束
-- 执行前请先查询外键是否存在：SHOW CREATE TABLE st_subsidy_policy_package;
-- 如果存在 fk_policy_package_package 外键，执行以下语句删除：
-- ALTER TABLE `st_subsidy_policy_package` DROP FOREIGN KEY `fk_policy_package_package`;

-- 方案二：直接修改字段类型（如果外键不存在，直接执行此步骤）
-- 2. 修改package_id字段类型为bigint UNSIGNED
ALTER TABLE `st_subsidy_policy_package` 
MODIFY COLUMN `package_id` bigint UNSIGNED NOT NULL COMMENT '补助套餐ID（关联st_subsidy_package表，类型需与主键一致）';

-- 3. 添加外键约束（如果已存在会报错，可以忽略）
ALTER TABLE `st_subsidy_policy_package` 
ADD CONSTRAINT `fk_policy_package_package` 
FOREIGN KEY (`package_id`) REFERENCES `st_subsidy_package` (`id`) ON DELETE CASCADE;

