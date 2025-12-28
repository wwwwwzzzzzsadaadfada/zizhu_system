-- 补助记录表增加直接关联学生与学期的字段，避免依赖已废弃的 st_student_semester_records
-- 建议在业务低峰期执行，并先备份数据

ALTER TABLE st_student_subsidy_records
    ADD COLUMN student_id BIGINT NULL COMMENT '学生ID（直接关联学生基础表）' AFTER id,
    ADD COLUMN year_semester_id BIGINT NULL COMMENT '学年学期ID（资金来源学期）' AFTER student_id;

-- 为新字段创建索引，便于按学生/学期查询与去重校验
CREATE INDEX idx_student_subsidy_student ON st_student_subsidy_records (student_id);
CREATE INDEX idx_student_subsidy_semester ON st_student_subsidy_records (year_semester_id);
CREATE INDEX idx_student_subsidy_student_semester ON st_student_subsidy_records (student_id, year_semester_id);

-- 迁移已有数据：以现有字段推导填充（如无可用值可保持 NULL，后续新数据将写入正确值）
UPDATE st_student_subsidy_records
SET student_id = COALESCE(student_id, student_semester_record_id);

UPDATE st_student_subsidy_records
SET year_semester_id = COALESCE(year_semester_id, source_semester_id);

-- 如果希望强制非空，可在数据填充完成并确认后执行：
-- ALTER TABLE st_student_subsidy_records MODIFY COLUMN student_id BIGINT NOT NULL;
-- ALTER TABLE st_student_subsidy_records MODIFY COLUMN year_semester_id BIGINT NOT NULL;


