-- 资助政策管理相关表结构

-- 1. 资助政策表
DROP TABLE IF EXISTS `st_subsidy_policy`;
CREATE TABLE `st_subsidy_policy` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `policy_code` varchar(50) NOT NULL COMMENT '政策编号（唯一）',
  `policy_name` varchar(200) NOT NULL COMMENT '政策名称',
  `policy_version` varchar(20) NOT NULL COMMENT '政策版本（如：v1.0）',
  `policy_type` varchar(50) DEFAULT NULL COMMENT '政策类型（如：国家助学金、免学杂费）',
  `content` text COMMENT '政策内容（富文本）',
  `file_path` varchar(500) DEFAULT NULL COMMENT '政策文件路径（Word/PDF）',
  `file_name` varchar(200) DEFAULT NULL COMMENT '政策文件名称',
  `effective_date` date NOT NULL COMMENT '生效日期',
  `expiry_date` date DEFAULT NULL COMMENT '失效日期（NULL表示长期有效）',
  `status` int DEFAULT 0 COMMENT '状态：0=草稿,1=已发布,2=已废止',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `publisher` varchar(64) DEFAULT NULL COMMENT '发布人',
  `abolish_time` datetime DEFAULT NULL COMMENT '废止时间',
  `abolish_reason` text COMMENT '废止原因',
  `memo` text COMMENT '备注',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_policy_code` (`policy_code`),
  INDEX `idx_status` (`status`),
  INDEX `idx_effective_date` (`effective_date`),
  INDEX `idx_policy_type` (`policy_type`)
) COMMENT='资助政策表';

-- 2. 政策适用范围表
DROP TABLE IF EXISTS `st_subsidy_policy_scope`;
CREATE TABLE `st_subsidy_policy_scope` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `policy_id` bigint NOT NULL COMMENT '政策ID',
  `schooling_plan_id` bigint DEFAULT NULL COMMENT '适用学制ID（NULL表示所有学制）',
  `grade_name` varchar(50) DEFAULT NULL COMMENT '适用年级（如：高一、高二，NULL表示所有年级）',
  `semester` int DEFAULT NULL COMMENT '适用学期（1=春季,2=秋季，NULL表示所有学期）',
  `school_year` varchar(20) DEFAULT NULL COMMENT '适用学年（如：2024-2025，NULL表示所有学年）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_policy_id` (`policy_id`),
  INDEX `idx_schooling_plan_id` (`schooling_plan_id`),
  CONSTRAINT `fk_policy_scope_policy` FOREIGN KEY (`policy_id`) REFERENCES `st_subsidy_policy` (`id`) ON DELETE CASCADE
) COMMENT='政策适用范围表';

-- 3. 政策与套餐关联表
-- 注意：st_subsidy_package 表已存在，主键为 bigint UNSIGNED，外键约束会引用该表
DROP TABLE IF EXISTS `st_subsidy_policy_package`;
CREATE TABLE `st_subsidy_policy_package` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `policy_id` bigint NOT NULL COMMENT '政策ID',
  `package_id` bigint UNSIGNED NOT NULL COMMENT '补助套餐ID（关联st_subsidy_package表，类型需与主键一致）',
  `package_name` varchar(200) DEFAULT NULL COMMENT '套餐名称（冗余字段，方便查询）',
  `subsidy_amount` decimal(18, 2) DEFAULT NULL COMMENT '补助金额（冗余字段，记录政策规定的金额）',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_policy_package` (`policy_id`, `package_id`),
  INDEX `idx_policy_id` (`policy_id`),
  INDEX `idx_package_id` (`package_id`),
  CONSTRAINT `fk_policy_package_policy` FOREIGN KEY (`policy_id`) REFERENCES `st_subsidy_policy` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_policy_package_package` FOREIGN KEY (`package_id`) REFERENCES `st_subsidy_package` (`id`) ON DELETE CASCADE
) COMMENT='政策与套餐关联表';

-- 4. 政策附件表
DROP TABLE IF EXISTS `st_subsidy_policy_attachment`;
CREATE TABLE `st_subsidy_policy_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `policy_id` bigint NOT NULL COMMENT '政策ID',
  `file_name` varchar(200) NOT NULL COMMENT '文件名称',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型（如：pdf, docx）',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `upload_by` varchar(64) DEFAULT NULL COMMENT '上传人',
  PRIMARY KEY (`id`),
  INDEX `idx_policy_id` (`policy_id`),
  CONSTRAINT `fk_policy_attachment_policy` FOREIGN KEY (`policy_id`) REFERENCES `st_subsidy_policy` (`id`) ON DELETE CASCADE
) COMMENT='政策附件表';

