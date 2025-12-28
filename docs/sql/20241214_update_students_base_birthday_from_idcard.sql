-- 从身份证号中提取出生日期并更新到birthday字段
-- 适用于身份证号为18位标准格式的情况（第7-14位为出生日期YYYYMMDD）
-- 执行日期: 2024-12-14

SET NAMES utf8mb4;

-- 更新出生日期字段（从身份证号第7-14位提取）
UPDATE st_students_base 
SET birthday = STR_TO_DATE(
    CONCAT(
        SUBSTRING(id_card_no, 7, 4), '-',
        SUBSTRING(id_card_no, 11, 2), '-',
        SUBSTRING(id_card_no, 13, 2)
    ), 
    '%Y-%m-%d'
)
WHERE birthday IS NULL 
  AND LENGTH(id_card_no) = 18 
  AND SUBSTRING(id_card_no, 7, 8) REGEXP '^[0-9]{8}$'
  AND STR_TO_DATE(
        CONCAT(
            SUBSTRING(id_card_no, 7, 4), '-',
            SUBSTRING(id_card_no, 11, 2), '-',
            SUBSTRING(id_card_no, 13, 2)
        ), 
        '%Y-%m-%d'
    ) IS NOT NULL;

-- 验证更新结果
-- SELECT id, name, id_card_no, birthday, 
--        STR_TO_DATE(CONCAT(SUBSTRING(id_card_no, 7, 4), '-', SUBSTRING(id_card_no, 11, 2), '-', SUBSTRING(id_card_no, 13, 2)), '%Y-%m-%d') AS extracted_birthday
-- FROM st_students_base 
-- WHERE LENGTH(id_card_no) = 18;



