-- ----------------------------
-- Table structure for st_student_bank_cards
-- ----------------------------
DROP TABLE IF EXISTS `st_student_bank_cards`;
CREATE TABLE `st_student_bank_cards` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint(20) UNSIGNED NOT NULL COMMENT '学生ID',
  `bank_account_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '银行卡号',
  `bank_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开户行（银行名称或开户行全称）',
  `branch_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '支行/网点名称',
  `account_holder` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开卡人（持卡人姓名）',
  `is_primary` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否主卡 0-否 1-是',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态 0-正常 1-停用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '创建人',
  `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '学生银行卡信息表' ROW_FORMAT = Dynamic;

