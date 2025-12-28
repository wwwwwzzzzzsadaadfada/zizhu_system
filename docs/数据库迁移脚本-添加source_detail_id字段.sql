-- ============================================
-- 上期结余计算优化：添加 source_detail_id 字段
-- ============================================

-- 1. 添加 source_detail_id 字段
ALTER TABLE st_subsidy_quota_detail 
ADD COLUMN source_detail_id BIGINT NULL COMMENT '来源明细ID（结转时记录来源明细的ID，NULL表示非结转资金）' AFTER quota_id;

-- 2. 添加索引以提升查询性能
CREATE INDEX idx_source_detail_id ON st_subsidy_quota_detail(source_detail_id);

-- 3. 数据迁移：为现有结转数据补充 source_detail_id
-- 说明：根据指标主表的 source_quota_id 和明细的经济分类，匹配来源明细
UPDATE st_subsidy_quota_detail d
INNER JOIN st_subsidy_quota q ON d.quota_id = q.id
INNER JOIN st_subsidy_quota source_quota ON q.source_quota_id = source_quota.id
INNER JOIN st_subsidy_quota_detail source_detail 
    ON source_detail.quota_id = source_quota.id 
    AND source_detail.economy_category = d.economy_category
SET d.source_detail_id = source_detail.id
WHERE q.quota_source_type IN (2, 3)
  AND q.source_quota_id IS NOT NULL
  AND d.source_detail_id IS NULL
  AND (
      -- 如果明细的memo包含"结转"标记，说明是结转资金
      d.memo LIKE '%从指标%结转%' 
      OR d.memo LIKE '%从%结转%'
      OR d.memo LIKE '%结转%'
  );

-- 4. 验证迁移结果（可选，用于检查迁移是否成功）
-- SELECT 
--     q.id as quota_id,
--     q.quota_doc_no,
--     q.quota_source_type,
--     d.id as detail_id,
--     d.economy_category,
--     d.total_amount,
--     d.source_detail_id,
--     d.memo
-- FROM st_subsidy_quota_detail d
-- INNER JOIN st_subsidy_quota q ON d.quota_id = q.id
-- WHERE q.quota_source_type IN (2, 3)
--   AND q.source_quota_id IS NOT NULL
-- ORDER BY q.id, d.id;

