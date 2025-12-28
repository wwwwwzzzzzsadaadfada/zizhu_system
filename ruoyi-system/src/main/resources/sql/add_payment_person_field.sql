-- 添加发放人字段到补助发放记录表
-- 执行时间：2024-12-XX

-- 方案1：直接添加字段（如果字段已存在会报错，可以忽略）
ALTER TABLE st_student_subsidy_records 
ADD COLUMN payment_person VARCHAR(64) NULL COMMENT '发放人' AFTER payment_date;

-- 方案2：先检查字段是否存在，如果不存在则添加（推荐使用存储过程或应用层处理）
-- 检查字段是否存在的SQL：
-- SELECT COUNT(*) as count FROM information_schema.COLUMNS 
-- WHERE TABLE_SCHEMA = DATABASE() 
-- AND TABLE_NAME = 'st_student_subsidy_records' 
-- AND COLUMN_NAME = 'payment_person';
-- 
-- 如果count为0，则执行添加字段的SQL

-- 修改审批通过时的逻辑：审批通过后应该是待发放状态（payment_status=0），而不是已发放
-- 注意：这个修改需要更新现有的审批逻辑，确保审批通过后不会自动设置为已发放

-- 更新现有数据：如果审批通过但payment_status=1的记录，需要检查是否需要调整
-- 这里只是添加字段，不修改现有数据逻辑

