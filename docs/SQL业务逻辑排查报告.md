# SQL业务逻辑排查报告

## 一、排查范围

对 `StSemesterBudgetMapper.xml` 文件进行了全面排查，识别出所有在SQL中实现的业务逻辑。

## 二、发现的业务逻辑

### 1. ✅ 已迁移到Java代码的逻辑

以下逻辑已经迁移到Java代码中：

1. **功能分类组合规则**（义教阶段预算组合）
   - 原位置：SQL中的 `<choose><when>` 条件判断
   - 新位置：`FunctionCategoryUtils.determineTargetCategories()`

2. **历史结余判断逻辑**
   - 原位置：SQL中的三种情况的OR条件
   - 新位置：`HistoricalSurplusUtils.isHistoricalSurplus()`

3. **经济分类映射**（代码转中文名称）
   - 原位置：SQL中的 `CASE WHEN`
   - 新位置：`EconomyCategoryUtils.codeToName()`

4. **学期标签计算**
   - 原位置：SQL中的 `CASE WHEN`
   - 新位置：`SemesterUtils.getSemesterLabel()`

5. **结转标志计算**
   - 原位置：SQL中的 `CASE WHEN q.quota_source_type in (2,3)`
   - 新位置：`QuotaSourceUtils.isCarryOver()`

### 2. ⚠️ 可以迁移但需权衡的逻辑

#### 2.1 当前学期预算过滤逻辑（第102-104行）

**SQL实现**：
```sql
and (q.year_semester_id IS NULL OR q.year_semester_id = #{yearSemesterId})
and (q.quota_source_type IS NULL OR q.quota_source_type = 1)
```

**业务规则**：
- 只查询真正学期下达的预算
- 排除关联指标来自历史学期的预算（这些是结转资金，应该在历史结余中）
- 排除指标来源类型为结转的预算（quota_source_type in (2,3) 表示结转指标）

**分析**：
- ✅ **应该迁移**：这是业务规则，而不是简单的数据过滤
- ✅ **可以迁移**：可以在Java代码中查询所有预算后过滤
- ⚠️ **性能考虑**：如果迁移到Java代码，需要查询更多数据，然后过滤

**建议**：**可以迁移**，因为：
1. 这是明确的业务规则，应该在Java代码中
2. 性能影响可控（可以在查询时先过滤掉大部分数据）

#### 2.2 状态过滤逻辑（第98行、125行等）

**SQL实现**：
```sql
and b.status != 4  -- 排除作废状态
```

**业务规则**：
- 排除作废状态的预算（status = 4）
- 如果前端没有显式传入状态，则默认过滤掉"作废(4)"记录（第65行）

**分析**：
- ✅ **可以迁移**：这是业务规则
- ⚠️ **性能考虑**：状态过滤是非常常见的操作，在SQL中过滤性能更好
- ✅ **简化建议**：可以保留在SQL中，因为这是简单的数据过滤，不是复杂的业务规则

**建议**：**保留在SQL中**，因为：
1. 状态过滤是标准的数据过滤操作
2. 性能更好
3. 不是复杂的业务规则

### 3. ⚠️ 应该保留在SQL中的逻辑

#### 3.1 可用金额计算（第47行）

**SQL实现**：
```sql
(b.budget_amount - COALESCE(b.used_amount, 0) - COALESCE(b.locked_amount, 0)) as available_amount
```

**分析**：
- ✅ **已在Java代码中重新计算**：`BudgetQueryService.toVOList()` 中已经重新计算
- ⚠️ **SQL中的计算**：SQL中的计算主要用于查询条件过滤
- ✅ **建议**：保留SQL中的计算，因为：
  1. 用于WHERE条件过滤（可用余额 > 0）
  2. Java代码中会重新计算，确保准确性
  3. 这是性能优化的需要

#### 3.2 可用余额过滤（第99行、126行等）

**SQL实现**：
```sql
and (b.budget_amount - COALESCE(b.used_amount, 0) - COALESCE(b.locked_amount, 0)) > 0
```

**业务规则**：
- 只查询有可用余额的预算

**分析**：
- ✅ **应该保留在SQL中**：这是性能优化的需要
- ✅ **已在Java代码中重新计算**：确保准确性
- ⚠️ **建议**：保留在SQL中用于过滤，Java代码中用于最终计算

#### 3.3 锁定金额和已使用金额的计算（UPDATE语句中的子查询）

**SQL实现**：
```sql
-- 例如在 lockBudgetAmount 中
GREATEST(COALESCE((
    SELECT COALESCE(SUM(r.subsidy_amount), 0)
    FROM st_student_subsidy_records r
    WHERE r.budget_id = b.id
    AND r.approval_status = 0
), 0), 0)
```

**分析**：
- ✅ **应该保留在SQL中**：这是数据一致性检查的需要
- ✅ **已经在Java代码中有对应方法**：`calculateActualLockedAmount()` 和 `calculateActualUsedAmount()`
- ⚠️ **建议**：保留在SQL中，因为：
  1. 这是并发控制的需要（在UPDATE时检查）
  2. 需要原子性保证
  3. Java代码中的方法是用于查询，SQL中的是用于更新验证

#### 3.4 GREATEST函数的使用（防止负数）

**SQL实现**：
```sql
GREATEST(COALESCE(locked_amount, 0) - #{param2}, 0)
```

**分析**：
- ✅ **应该保留在SQL中**：这是数据完整性约束
- ✅ **建议**：保留在SQL中，因为这是数据库层面的约束，防止数据异常

### 4. ✅ 应该迁移的逻辑

#### 4.1 当前学期预算过滤逻辑

**位置**：`selectAvailableBudgets` 查询中的第102-104行

**当前SQL**：
```sql
<if test="yearSemesterId != null">
    and (q.year_semester_id IS NULL OR q.year_semester_id = #{yearSemesterId})
    and (q.quota_source_type IS NULL OR q.quota_source_type = 1)
</if>
```

**业务规则**：
- 只查询真正学期下达的预算
- 排除关联指标来自历史学期的预算
- 排除指标来源类型为结转的预算

**迁移方案**：
1. 在 `BudgetQueryService.getAvailableBudgets()` 中查询所有可能的预算
2. 使用 `HistoricalSurplusUtils` 或创建新的工具类方法过滤掉历史结余和结转预算
3. 或者在 `selectAvailableBudgets` 中移除这个过滤条件，在Java代码中过滤

## 三、建议

### 1. 应该迁移的逻辑

**当前学期预算过滤逻辑**（selectAvailableBudgets中的过滤）

**理由**：
- 这是业务规则，不是简单的数据过滤
- 可以在Java代码中更清晰地表达业务意图
- 与历史结余判断逻辑保持一致

**实现方式**：
- 在 `BudgetQueryService.getAvailableBudgets()` 中查询所有预算
- 使用工具类方法过滤掉不符合条件的预算（历史学期预算、结转预算）

### 2. 应该保留在SQL中的逻辑

以下逻辑建议保留在SQL中：

1. **状态过滤**（`status != 4`）：标准的数据过滤操作
2. **可用余额计算**：用于WHERE条件过滤，性能需要
3. **锁定金额计算**（UPDATE中的子查询）：并发控制需要
4. **GREATEST函数**：数据完整性约束

### 3. 权衡建议

对于可用余额过滤（`available_amount > 0`）：
- **SQL中**：用于初步过滤，提升性能
- **Java代码中**：重新计算，确保准确性
- **建议**：两者都保留，SQL用于过滤，Java用于最终计算

## 四、总结

经过排查，发现以下业务逻辑可以迁移到Java代码：

1. ✅ **当前学期预算过滤逻辑**（第102-104行）- **建议迁移**

其他逻辑建议保留在SQL中，因为：
- 状态过滤：标准的数据过滤操作
- 可用余额计算和过滤：性能优化需要
- 锁定金额计算（UPDATE中）：并发控制需要
- GREATEST函数：数据完整性约束

所有复杂的业务规则（功能分类组合、历史结余判断、经济分类映射等）已经迁移到Java代码中。

