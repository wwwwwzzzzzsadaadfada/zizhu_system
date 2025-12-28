# 结余资金判断逻辑（基于source_detail_id）

## 一、业务场景理解

### 1. 数据流转过程

1. **录入历史学期指标**：
   - 用户在历史学期（如2023年秋季）录入了一个指标
   - 这个指标在历史学期时是"当前学期下达"的（quotaSourceType = 1）
   - 指标明细表（st_subsidy_quota_detail）中的记录：`source_detail_id = NULL`

2. **结转到当前学期**：
   - 现在把这个历史学期的指标结转到当前学期（如2024年春季）
   - 结转时，会在指标明细表中创建新的明细记录
   - 新记录的`source_detail_id`指向原来的明细ID
   - 例如：`INSERT INTO st_subsidy_quota_detail VALUES (21, 1, 20, ...)` 
     - `id = 21`：新明细ID
     - `quota_id = 1`：关联到当前学期的指标
     - `source_detail_id = 20`：指向来源明细ID（表示这是从ID=20的明细结转来的）

3. **分配预算**：
   - 从结转后的指标明细分配预算
   - 预算表（st_semester_budget）关联到指标明细：`quota_detail_id = 21`
   - 预算的`year_semester_id` = 当前学期ID（2024年春季）
   - **但预算关联的明细的`source_detail_id`不为NULL，说明这是结转资金**

## 二、判断结余资金的正确方式

### 关键字段

**st_subsidy_quota_detail表**：
- `source_detail_id`：来源明细ID
  - `NULL`：非结转资金（原本就是这个指标的明细）
  - `NOT NULL`：结转资金（从其他明细结转来的）

**st_semester_budget表**：
- `quota_detail_id`：关联的指标明细ID

### 判断逻辑

**如果预算关联的指标明细的`source_detail_id`不为NULL，那么这个预算就是结转资金。**

## 三、实现方案

### 1. 修改SQL查询，添加source_detail_id字段

在`selectStSemesterBudgetVo`中添加：
```sql
d.source_detail_id as source_detail_id
```

### 2. 在StSemesterBudget实体类中添加字段

```java
/** 指标明细的来源明细ID（关联查询，不存数据库，用于判断是否为结转资金） */
private Long sourceDetailId;
```

### 3. 在resultMap中映射字段

```xml
<result property="sourceDetailId" column="source_detail_id" />
```

### 4. 修改toVO方法，根据sourceDetailId判断

```java
private BudgetVO toVO(@NonNull StSemesterBudget budget, boolean isHistorical) {
    BudgetVO vo = new BudgetVO();
    BeanUtils.copyProperties(budget, vo);
    
    // 判断是否为结余资金：如果指标明细的sourceDetailId不为NULL，就是结转资金
    boolean isSurplus = budget.getSourceDetailId() != null;
    
    vo.setIsHistorical(isSurplus);
    
    // 如果是结余资金，在预算项目名称后面加上（结余）标识
    if (isSurplus && budget.getBudgetProjectName() != null && !budget.getBudgetProjectName().trim().isEmpty()) {
        String budgetProjectName = budget.getBudgetProjectName();
        if (!budgetProjectName.endsWith("（结余）") && !budgetProjectName.endsWith("(结余)")) {
            vo.setBudgetProjectName(budgetProjectName + "（结余）");
        }
    }
    
    return vo;
}
```

### 5. 修改getAvailableBudgets方法

**不需要过滤**，返回所有当前学期的预算：
```java
public List<BudgetVO> getAvailableBudgets(Long currentSemesterId, String economyCategory, String functionCategory) {
    // ... 参数验证和查询当前学期信息 ...
    
    // 查询所有当前学期的预算（包括结余资金）
    List<StSemesterBudget> allBudgets = semesterBudgetMapper.selectAvailableBudgets(
        currentSemesterId, 
        economyCategory, 
        targetFunctionCategories.isEmpty() ? null : targetFunctionCategories
    );
    
    // 直接转换，在toVO中根据sourceDetailId判断是否为结余资金
    return toVOList(allBudgets, false);
}
```

## 四、数据示例

### 表数据示例

**st_subsidy_quota_detail表**：
```
id | quota_id | source_detail_id | economy_category | ...
20 | 18       | NULL             | 1                | ...  (原始明细，非结转)
21 | 1        | 20               | 1                | ...  (从ID=20结转来的明细)
```

**st_semester_budget表**：
```
id | year_semester_id | quota_id | quota_detail_id | ...
1  | 100 (当前学期)   | 1        | 21              | ...  (关联到ID=21的明细，source_detail_id=20，所以是结转资金)
```

**判断结果**：
- 预算ID=1关联的明细ID=21
- 明细ID=21的`source_detail_id = 20`（不为NULL）
- 所以这是结余资金，应该在预算项目名称后显示"（结余）"

## 五、总结

### 核心逻辑

1. **标识字段**：`st_subsidy_quota_detail.source_detail_id`
   - NULL = 非结转资金
   - NOT NULL = 结转资金

2. **判断方式**：预算关联的指标明细的`source_detail_id`不为NULL

3. **显示方式**：如果是结转资金，在预算项目名称后加上"（结余）"标识

### 优势

- **准确**：直接通过数据库字段判断，不依赖复杂的业务逻辑
- **清晰**：`source_detail_id`字段明确记录了资金来源
- **可追溯**：可以通过`source_detail_id`追踪到原始明细


