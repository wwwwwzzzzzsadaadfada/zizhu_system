# 业务逻辑从SQL迁移到Java代码重构总结

## 一、重构目标

将SQL中的业务逻辑迁移到Java代码中，提高代码的可维护性、可测试性和可扩展性，同时确保安全性和逻辑严谨性。

## 二、重构内容

### 1. 功能分类组合规则（义教阶段预算组合）

**原实现（SQL）：**
```xml
<choose>
    <when test='"1".equals(functionCategory) or "2".equals(functionCategory)'>
        and (q.function_category IN ('1', '2') or ...)
    </when>
    <otherwise>
        and (q.function_category = #{functionCategory} or ...)
    </otherwise>
</choose>
```

**新实现（Java）：**
- 创建 `BudgetBusinessRule.determineTargetFunctionCategories()` 方法
- 业务规则：小学（1）和初中（2）可以互相看到对方的预算
- 在 `BudgetQueryService` 中调用该方法，确定目标功能分类列表
- 将列表传递给Mapper，SQL只负责数据查询

**优点：**
- ✅ 业务规则清晰，易于理解和维护
- ✅ 可以编写单元测试验证规则
- ✅ 规则变更时只需修改Java代码

### 2. 经济分类映射（代码转中文名称）

**原实现（SQL）：**
```sql
CASE 
    WHEN d.economy_category = '1' THEN '助学金'
    WHEN d.economy_category = '2' THEN '免学杂费'
    ...
END as economy_category
```

**新实现（Java）：**
- 创建 `BudgetBusinessRule.convertEconomyCategoryCodeToName()` 方法
- SQL返回代码（如"1"、"2"），不再返回中文名称
- 在需要显示中文名称的地方调用工具类方法转换

**优点：**
- ✅ 数据层和展示层分离
- ✅ 可以在多个地方复用转换逻辑
- ✅ 便于国际化扩展

### 3. 学期标签计算

**原实现（SQL）：**
```sql
CASE 
    WHEN ys.semester = 1 THEN '秋季学期'
    WHEN ys.semester = 2 THEN '春季学期'
    ELSE null
END as semester_label
```

**新实现（Java）：**
- 使用已有的 `SemesterUtils.getSemesterLabel()` 工具类
- SQL返回NULL，在Java代码中统一设置学期标签

**优点：**
- ✅ 统一使用工具类，确保一致性
- ✅ 减少SQL复杂度

### 4. 结转标志计算

**原实现（SQL）：**
```sql
CASE WHEN q.quota_source_type in (2,3) THEN 1 ELSE 0 END as carry_over_flag
```

**新实现（Java）：**
- 创建 `BudgetBusinessRule.isCarryOverQuota()` 方法
- 在 `BudgetQueryService.toVOList()` 中统一计算结转标志

**优点：**
- ✅ 业务逻辑集中，便于维护
- ✅ 可以编写单元测试

### 5. 参数验证和安全性检查

**新增：**
- `BudgetBusinessRule.validateBudgetQueryParams()` 方法
- 验证学期ID、经济分类代码、功能分类代码的有效性
- 防止SQL注入和非法参数

**优点：**
- ✅ 提前发现参数错误，提供清晰的错误信息
- ✅ 防止非法数据进入数据库查询

## 三、修改的文件清单

### 新增文件
1. `ruoyi-system/src/main/java/com/ruoyi/system/application/query/BudgetBusinessRule.java`
   - 业务规则工具类，包含所有业务逻辑方法

### 修改的文件
1. **Mapper接口**
   - `ruoyi-system/src/main/java/com/ruoyi/system/mapper/StSemesterBudgetMapper.java`
     - 修改 `selectAvailableBudgets` 和 `selectHistoricalSurplus` 方法签名
     - 接受 `List<String> functionCategories` 参数，而不是单个 `String functionCategory`

2. **SQL映射文件**
   - `ruoyi-system/src/main/resources/mapper/system/StSemesterBudgetMapper.xml`
     - 简化功能分类查询逻辑，使用 `<foreach>` 处理列表参数
     - 移除经济分类映射的CASE WHEN
     - 移除学期标签计算的CASE WHEN
     - 移除结转标志计算的CASE WHEN
     - `selectEconomyCategoryByBudgetId` 改为只返回代码

3. **Service层**
   - `ruoyi-system/src/main/java/com/ruoyi/system/application/query/BudgetQueryService.java`
     - 添加参数验证
     - 在Java代码中确定目标功能分类列表
     - 在 `toVOList` 中统一计算结转标志
   
   - `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/StSemesterBudgetServiceImpl.java`
     - 使用工具类计算结转标志

4. **Controller层**
   - `ruoyi-system/src/main/java/com/ruoyi/system/controller/StSemesterBudgetController.java`
     - `convertEconomyCategoryCodeToName` 方法改为调用工具类

## 四、安全性增强

### 1. 参数验证
- 验证学期ID必须大于0
- 验证经济分类代码必须在有效范围内（1-4）
- 验证功能分类代码必须在有效范围内（1-3）
- 所有验证失败都抛出 `IllegalArgumentException`，提供清晰的错误信息

### 2. SQL注入防护
- 所有参数都通过MyBatis的参数绑定（`#{param}`）传递
- 列表参数使用 `<foreach>` 安全处理
- 移除了SQL中的字符串拼接逻辑

### 3. 空值处理
- 所有方法都进行了空值检查
- 使用 `trim()` 处理字符串参数，去除前后空格
- 空列表返回 `Collections.emptyList()`，避免返回null

## 五、逻辑严谨性

### 1. 业务规则明确
- 义教阶段预算组合规则：明确只有小学（1）和初中（2）可以组合
- 其他功能分类（如高中）只查询匹配的分类
- 规则通过常量定义，避免硬编码

### 2. 边界情况处理
- 空参数处理：如果功能分类为空，返回空列表
- null值处理：所有可能为null的地方都进行了检查
- 无效值处理：验证方法会拒绝无效的代码值

### 3. 代码一致性
- 统一使用工具类进行转换和计算
- 避免了重复代码
- 所有业务规则集中在一个类中，便于维护

## 六、测试建议

### 1. 单元测试
建议为 `BudgetBusinessRule` 类编写单元测试：

```java
@Test
public void testDetermineTargetFunctionCategories() {
    // 测试小学
    List<String> result = BudgetBusinessRule.determineTargetFunctionCategories("1");
    assertEquals(Arrays.asList("1", "2"), result);
    
    // 测试初中
    result = BudgetBusinessRule.determineTargetFunctionCategories("2");
    assertEquals(Arrays.asList("1", "2"), result);
    
    // 测试高中
    result = BudgetBusinessRule.determineTargetFunctionCategories("3");
    assertEquals(Collections.singletonList("3"), result);
    
    // 测试空值
    result = BudgetBusinessRule.determineTargetFunctionCategories(null);
    assertEquals(Collections.emptyList(), result);
}

@Test
public void testConvertEconomyCategoryCodeToName() {
    assertEquals("助学金", BudgetBusinessRule.convertEconomyCategoryCodeToName("1"));
    assertEquals("免学杂费", BudgetBusinessRule.convertEconomyCategoryCodeToName("2"));
    assertEquals("免学费", BudgetBusinessRule.convertEconomyCategoryCodeToName("3"));
    assertEquals("营养改善计划", BudgetBusinessRule.convertEconomyCategoryCodeToName("4"));
}

@Test
public void testValidateBudgetQueryParams() {
    // 测试正常参数
    assertDoesNotThrow(() -> {
        BudgetBusinessRule.validateBudgetQueryParams(1L, "1", "1");
    });
    
    // 测试无效的学期ID
    assertThrows(IllegalArgumentException.class, () -> {
        BudgetBusinessRule.validateBudgetQueryParams(null, "1", "1");
    });
    
    // 测试无效的经济分类
    assertThrows(IllegalArgumentException.class, () -> {
        BudgetBusinessRule.validateBudgetQueryParams(1L, "99", "1");
    });
}
```

### 2. 集成测试
- 测试预算查询功能是否正常工作
- 测试小学和初中预算是否可以互相看到
- 测试经济分类代码转换是否正确

## 七、性能影响

### 分析
- **功能分类组合**：两种方案的SQL查询性能相同（最终执行的都是 `IN ('1', '2')`）
- **经济分类映射**：性能影响可忽略（只是在展示时转换，不涉及数据库查询）
- **学期标签计算**：性能影响可忽略（Java代码中的简单switch语句）
- **结转标志计算**：性能影响可忽略（Java代码中的简单判断）

### 结论
**本次重构对性能的影响可以忽略不计**，因为：
1. 数据库查询层面没有变化（SQL查询逻辑相同）
2. Java代码中的计算逻辑非常轻量
3. 避免了N+1查询问题（都在一次查询中完成）

## 八、后续优化建议

1. **历史结余判断逻辑**：
   - 当前历史结余判断仍保留在SQL中（因为性能考虑）
   - 如果将来需要更灵活的控制，可以考虑在Java代码中实现

2. **统一工具类**：
   - 可以考虑将 `BudgetBusinessRule` 中的方法进一步整合
   - 或者创建更细粒度的工具类（如 `FunctionCategoryUtils`、`EconomyCategoryUtils`）

3. **缓存优化**：
   - 如果经济分类映射调用频繁，可以考虑添加缓存
   - 但当前场景下，缓存可能不是必需的

## 九、总结

本次重构成功将SQL中的业务逻辑迁移到Java代码中，实现了以下目标：

✅ **可维护性提升**：业务规则集中在Java代码中，易于理解和修改  
✅ **可测试性提升**：可以编写单元测试验证业务逻辑  
✅ **安全性增强**：添加了参数验证，防止非法数据  
✅ **逻辑严谨性**：明确的业务规则定义，完善的边界情况处理  
✅ **代码质量提升**：减少重复代码，统一使用工具类  

重构后的代码更加符合软件工程最佳实践，为后续的功能扩展和维护打下了良好的基础。

