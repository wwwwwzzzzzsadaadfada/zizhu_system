-- 批量更新学生基础信息表的默认值
-- 入学时间：2024年9月1日
-- 政治面貌：群众
-- 联系电话：18278188453
-- 执行日期: 2024-12-14

SET NAMES utf8mb4;

-- 更新入学时间为2024年9月1日（仅更新为空值的记录）
UPDATE `st_students_base` 
SET `enrollment_date` = '2024-09-01'
WHERE `enrollment_date` IS NULL;

-- 或者更新所有记录（如果需要统一所有学生的入学时间，取消注释下面这行并注释上面的UPDATE）
-- UPDATE `st_students_base` SET `enrollment_date` = '2024-09-01';

-- 更新政治面貌为"群众"（仅更新为空值的记录）
UPDATE `st_students_base` 
SET `political_status` = '群众'
WHERE `political_status` IS NULL OR `political_status` = '';

-- 或者更新所有记录（如果需要统一所有学生的政治面貌，取消注释下面这行并注释上面的UPDATE）
-- UPDATE `st_students_base` SET `political_status` = '群众';

-- 更新联系电话为18278188453（仅更新为空值的记录）
UPDATE `st_students_base` 
SET `phone` = '18278188453'
WHERE `phone` IS NULL OR `phone` = '';

-- 或者更新所有记录（如果需要统一所有学生的联系电话，取消注释下面这行并注释上面的UPDATE）
-- UPDATE `st_students_base` SET `phone` = '18278188453';

-- 验证更新结果
-- SELECT id, name, enrollment_date, political_status, phone 
-- FROM st_students_base 
-- ORDER BY id;



