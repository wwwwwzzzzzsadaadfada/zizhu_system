-- ----------------------------
-- 自定义档案包表（完整版）
-- ----------------------------
DROP TABLE IF EXISTS `st_report_package_custom`;
CREATE TABLE `st_report_package_custom` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `package_code` varchar(100) NOT NULL COMMENT '档案包编码（唯一标识）',
  `package_name` varchar(200) NOT NULL COMMENT '档案包名称',
  `package_type` varchar(50) DEFAULT 'custom' COMMENT '档案包类型',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `year_semester_id` bigint(20) DEFAULT NULL COMMENT '学年学期ID',
  `schooling_plan_id` bigint(20) DEFAULT NULL COMMENT '学制ID',
  `grade_id` bigint(20) DEFAULT NULL COMMENT '年级ID',
  `class_id` bigint(20) DEFAULT NULL COMMENT '班级ID',
  `file_count` int(11) DEFAULT 0 COMMENT '文件数量',
  `total_size` bigint(20) DEFAULT 0 COMMENT '总大小（字节）',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态（1=正常，0=已删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_package_code` (`package_code`),
  KEY `idx_package_name` (`package_name`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义档案包表';
