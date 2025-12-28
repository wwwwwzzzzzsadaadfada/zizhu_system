-- 家庭成员表（支持一对多）
-- 如果已存在同名表，请根据实际情况调整或跳过

CREATE TABLE IF NOT EXISTS `st_family_members` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id` BIGINT UNSIGNED NOT NULL COMMENT '学生ID，关联 st_students_base.id',
  `year_semester_id` BIGINT UNSIGNED NULL COMMENT '学年学期ID（可选冗余）',
  `name` VARCHAR(64) NOT NULL COMMENT '家庭成员姓名',
  `age` SMALLINT UNSIGNED NULL COMMENT '年龄',
  `relation` VARCHAR(32) NOT NULL COMMENT '关系（父亲/母亲/祖父母/兄弟姐妹/监护人/其他）',
  `employer` VARCHAR(128) NULL COMMENT '工作单位',
  `occupation` VARCHAR(64) NULL COMMENT '职业',
  `health_status` VARCHAR(32) NULL COMMENT '健康状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(64) NULL COMMENT '创建人',
  `updated_by` VARCHAR(64) NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_year_semester_id` (`year_semester_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭成员信息';

-- 如需外键，可按需启用（生产环境谨慎）：
-- ALTER TABLE `st_family_members` ADD CONSTRAINT `fk_family_member_student`
--   FOREIGN KEY (`student_id`) REFERENCES `st_students_base` (`id`);


