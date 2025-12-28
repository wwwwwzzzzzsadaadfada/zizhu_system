-- ============================================================
-- 修复预算表中负数锁定金额的问题
-- ============================================================
-- 问题：locked_amount 字段可能为负数，导致可用金额计算错误
-- 修复：将所有负数锁定金额修复为0
-- ============================================================

-- 1. 查看需要修复的记录
SELECT 
    id,
    budget_amount,
    used_amount,
    locked_amount,
    (budget_amount - COALESCE(used_amount, 0) - COALESCE(locked_amount, 0)) AS available_amount_before_fix,
    (budget_amount - COALESCE(used_amount, 0) - GREATEST(COALESCE(locked_amount, 0), 0)) AS available_amount_after_fix
FROM st_semester_budget
WHERE COALESCE(locked_amount, 0) < 0
   OR COALESCE(used_amount, 0) < 0;

-- 2. 修复负数锁定金额（设置为0）
UPDATE st_semester_budget
SET locked_amount = 0,
    updated_at = NOW()
WHERE COALESCE(locked_amount, 0) < 0;

-- 3. 修复负数已用金额（设置为0）
UPDATE st_semester_budget
SET used_amount = 0,
    updated_at = NOW()
WHERE COALESCE(used_amount, 0) < 0;

-- 4. 验证修复结果
SELECT 
    id,
    budget_amount,
    used_amount,
    locked_amount,
    (budget_amount - COALESCE(used_amount, 0) - COALESCE(locked_amount, 0)) AS available_amount
FROM st_semester_budget
WHERE id IN (
    SELECT id FROM st_semester_budget 
    WHERE COALESCE(locked_amount, 0) < 0 
       OR COALESCE(used_amount, 0) < 0
);

-- ============================================================
-- 说明：
-- ============================================================
-- 1. 先执行第1步查看需要修复的记录
-- 2. 确认无误后执行第2、3步进行修复
-- 3. 执行第4步验证修复结果
-- 4. 修复后，系统代码已经添加了防护，不会再出现负数
-- ============================================================


