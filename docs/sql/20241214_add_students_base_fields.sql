-- 为学生基础信息表(st_students_base)添加缺失字段
-- 用于支持普通高中国家助学金申请表
-- 执行日期: 2024-12-14

SET NAMES utf8mb4;

-- 添加出生日期字段
ALTER TABLE `st_students_base` 
ADD COLUMN `birthday` date NULL DEFAULT NULL COMMENT '出生日期' AFTER `id_card_no`;

-- 添加联系电话字段
ALTER TABLE `st_students_base` 
ADD COLUMN `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话' AFTER `domicile`;

-- 添加入学时间字段
ALTER TABLE `st_students_base` 
ADD COLUMN `enrollment_date` date NULL DEFAULT NULL COMMENT '入学时间' AFTER `student_no`;

-- 添加政治面貌字段
ALTER TABLE `st_students_base` 
ADD COLUMN `political_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '政治面貌' AFTER `enrollment_date`;

-- 添加是否民族高中班字段
ALTER TABLE `st_students_base` 
ADD COLUMN `is_ethnic_class` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '是否民族高中班 1=是 0=否' AFTER `political_status`;

-- 可选：从身份证号中提取出生日期并更新（如果身份证号格式正确）
-- 注意：此操作需要确保身份证号格式为18位，且第7-14位为日期
-- UPDATE st_students_base 
-- SET birthday = STR_TO_DATE(CONCAT(
--     SUBSTRING(id_card_no, 7, 4), '-',
--     SUBSTRING(id_card_no, 11, 2), '-',
--     SUBSTRING(id_card_no, 13, 2)
-- ), '%Y-%m-%d')
-- WHERE birthday IS NULL 
--   AND LENGTH(id_card_no) = 18 
--   AND SUBSTRING(id_card_no, 7, 8) REGEXP '^[0-9]{8}$';

-- 验证字段是否添加成功
-- SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT 
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_SCHEMA = DATABASE() 
--   AND TABLE_NAME = 'st_students_base' 
--   AND COLUMN_NAME IN ('birthday', 'phone', 'enrollment_date', 'political_status', 'is_ethnic_class')
-- ORDER BY ORDINAL_POSITION;



