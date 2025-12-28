-- ============================================================
-- 修复预算表：添加约束防止负数金额
-- ============================================================
-- 说明：MySQL 8.0.16+ 支持 CHECK 约束
-- 如果您的MySQL版本低于8.0.16，请使用触发器代替
-- ============================================================

-- 1. 先修复现有数据中的负数
UPDATE st_semester_budget
SET locked_amount = 0,
    updated_at = NOW()
WHERE COALESCE(locked_amount, 0) < 0;

UPDATE st_semester_budget
SET used_amount = 0,
    updated_at = NOW()
WHERE COALESCE(used_amount, 0) < 0;

UPDATE st_semester_budget
SET budget_amount = 0,
    updated_at = NOW()
WHERE COALESCE(budget_amount, 0) < 0;

-- 2. 添加CHECK约束（MySQL 8.0.16+）
-- 如果您的MySQL版本支持CHECK约束，执行以下语句
ALTER TABLE st_semester_budget
ADD CONSTRAINT chk_budget_amount CHECK (budget_amount >= 0),
ADD CONSTRAINT chk_used_amount CHECK (used_amount >= 0),
ADD CONSTRAINT chk_locked_amount CHECK (locked_amount >= 0);

-- 3. 如果MySQL版本不支持CHECK约束，使用触发器代替
-- 删除上面的CHECK约束（如果已添加）
-- ALTER TABLE st_semester_budget DROP CONSTRAINT chk_budget_amount;
-- ALTER TABLE st_semester_budget DROP CONSTRAINT chk_used_amount;
-- ALTER TABLE st_semester_budget DROP CONSTRAINT chk_locked_amount;

-- 创建触发器防止负数（MySQL 5.7或更低版本）
DELIMITER $$

DROP TRIGGER IF EXISTS trg_check_budget_amount_before_update$$
CREATE TRIGGER trg_check_budget_amount_before_update
BEFORE UPDATE ON st_semester_budget
FOR EACH ROW
BEGIN
    IF NEW.budget_amount < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '预算总额不能为负数';
    END IF;
    IF NEW.used_amount < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '已使用金额不能为负数';
    END IF;
    IF NEW.locked_amount < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '锁定金额不能为负数';
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_check_budget_amount_before_insert$$
CREATE TRIGGER trg_check_budget_amount_before_insert
BEFORE INSERT ON st_semester_budget
FOR EACH ROW
BEGIN
    IF NEW.budget_amount < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '预算总额不能为负数';
    END IF;
    IF NEW.used_amount < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '已使用金额不能为负数';
    END IF;
    IF NEW.locked_amount < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '锁定金额不能为负数';
    END IF;
END$$

DELIMITER ;

-- 4. 验证修复结果
SELECT 
    id,
    budget_amount,
    used_amount,
    locked_amount,
    (budget_amount - COALESCE(used_amount, 0) - COALESCE(locked_amount, 0)) AS available_amount,
    CASE 
        WHEN COALESCE(budget_amount, 0) < 0 THEN '预算总额为负数'
        WHEN COALESCE(used_amount, 0) < 0 THEN '已用金额为负数'
        WHEN COALESCE(locked_amount, 0) < 0 THEN '锁定金额为负数'
        ELSE '正常'
    END AS status_check
FROM st_semester_budget;

-- ============================================================
-- 说明：
-- ============================================================
-- 1. 先执行第1步修复现有数据
-- 2. 如果MySQL版本 >= 8.0.16，执行第2步添加CHECK约束
-- 3. 如果MySQL版本 < 8.0.16，执行第3步创建触发器
-- 4. 执行第4步验证修复结果
-- ============================================================


