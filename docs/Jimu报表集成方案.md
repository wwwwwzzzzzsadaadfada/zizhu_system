# Jimu报表（积木报表）集成方案

> 参考文章：[若依框架集成JimuReport积木报表](https://blog.csdn.net/qq_28775077/article/details/125475740)

## 一、概述

Jimu报表（积木报表）是一个功能强大的开源报表工具，支持在线设计报表、数据源配置、报表预览和导出等功能。

## 二、技术栈

- **后端**: Spring Boot 2.5.15
- **报表引擎**: Jimu报表（积木报表）1.5.0-beta
- **数据源**: Druid + MySQL
- **前端**: Vue.js

## 三、集成步骤

### 3.1 添加 Maven 依赖

在 `pom.xml` 中添加 Jimu 报表依赖：

```xml
<!-- Jimu报表（积木报表）-->
<dependency>
    <groupId>org.jeecgframework.jimureport</groupId>
    <artifactId>jimureport-spring-boot-starter</artifactId>
    <version>1.5.0-beta</version>
</dependency>
```

**注意**: 
- 该jar包可以在Maven本地仓库找到
- 如果无法下载，可能需要配置Maven仓库或手动安装

### 3.2 创建数据库表

**重要**: 需要执行Jimu报表的SQL初始化脚本！

1. 从Jimu报表官方获取 `jimureport.sql` 脚本
2. 在MySQL数据库中执行该脚本
3. 确保数据库版本为MySQL 5.7或更高版本

SQL脚本位置：
- Jimu报表官方文档
- GitHub: https://github.com/jeecgboot/JimuReport
- 或从Maven依赖的jar包中提取

### 3.3 配置主程序扫描包

在 `RuoYiApplication.java` 中添加扫描包：

```java
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class }, 
                       scanBasePackages = {"org.jeecg.modules.jmreport", "com.ruoyi"})
```

**关键点**：
- 扫描包名是 `org.jeecg.modules.jmreport`（不是 `org.jeecgframework.jimureport`）
- 如果引用包后还是报错，可以重启项目解决

### 3.4 配置 Spring Security

在 `SecurityConfig` 中允许访问 Jimu 报表路径：

**方式1：匿名访问**（如果没有Token机制）：
```java
.antMatchers("/jmreport/**").permitAll()
```

**方式2：需要认证**（如果有Token机制）：
```java
.antMatchers("/jmreport/**").authenticated()
```

### 3.5 配置文件

在 `application.yml` 中无需特殊配置，Jimu报表会自动使用系统的主数据源。

## 四、访问路径

集成完成后，可以通过以下路径访问：

- **报表列表**: `http://localhost:8080/jmreport/list`
- **报表设计器**: `http://localhost:8080/jmreport/designer`
- **报表预览**: `http://localhost:8080/jmreport/view`

**注意**: 访问路径是 `/jmreport/**`，不是 `/jimu/**`

## 五、验证集成

启动项目后，在浏览器访问：
```
http://localhost:8080/jmreport/list
```

如果能够正常访问报表列表页面，说明集成成功！

## 六、功能实现

### 5.1 报表模板管理

管理员可以通过报表设计器创建和管理报表模板：
- 设计报表布局
- 配置数据源
- 设置报表参数
- 保存报表模板

### 5.2 报表生成

#### 单个学生报表生成
- 选择报表模板
- 选择学生
- 生成并下载报表（PDF/Word/Excel）

#### 批量报表生成
- 选择报表模板
- 选择多个学生或全部学生
- 异步批量生成
- 打包成 ZIP 下载

### 5.3 报表预览

- 在线预览报表
- 支持参数化查询
- 实时数据展示

## 七、数据源配置

Jimu 报表支持多种数据源：
1. **JDBC 数据源** - 直接连接数据库
2. **Spring Bean 数据源** - 通过 Spring Bean 获取数据
3. **API 数据源** - 通过 HTTP API 获取数据

### 6.1 JDBC 数据源配置

在报表设计器中配置：
- 数据源名称
- 数据库连接信息
- SQL 查询语句

### 6.2 Spring Bean 数据源

可以通过实现 Jimu 报表的数据源接口，从 Spring Bean 中获取数据。

## 八、报表生成服务

### 7.1 后端接口设计

```java
@RestController
@RequestMapping("/system/report")
public class ReportController {
    
    /**
     * 获取报表模板列表
     */
    @GetMapping("/templates")
    public AjaxResult getTemplates() { }
    
    /**
     * 生成单个学生报表
     */
    @PostMapping("/generate/single")
    public void generateSingle(@RequestBody GenerateReportRequest request, 
                               HttpServletResponse response) { }
    
    /**
     * 批量生成报表
     */
    @PostMapping("/generate/batch")
    public AjaxResult generateBatch(@RequestBody BatchGenerateRequest request) { }
    
    /**
     * 生成全部学生报表
     */
    @PostMapping("/generate/all")
    public AjaxResult generateAll(@RequestBody GenerateAllRequest request) { }
    
    /**
     * 预览报表
     */
    @PostMapping("/preview")
    public void previewReport(@RequestBody PreviewRequest request, 
                              HttpServletResponse response) { }
}
```

### 7.2 前端页面设计

**报表生成页面**: `/system/report/generate`

功能模块：
1. 报表模板选择
2. 生成范围选择（单个/批量/全部）
3. 学生选择
4. 参数配置
5. 生成操作

## 九、注意事项

1. **SQL脚本**: 必须执行Jimu报表的SQL初始化脚本，否则无法正常使用
2. **扫描包**: 主程序必须添加 `org.jeecg.modules.jmreport` 扫描包
3. **访问路径**: Jimu报表的访问路径是 `/jmreport/**`，不是 `/jimu/**`
4. **版本**: 当前使用版本为 `1.5.0-beta`，可根据需要调整
5. **权限控制**: 根据项目需求选择 `permitAll()` 或 `authenticated()`

1. **依赖版本**: 确保 Jimu 报表版本与 Spring Boot 版本兼容
2. **数据源**: Jimu 报表需要访问数据库，确保数据源配置正确
3. **权限控制**: 报表设计器应该只有管理员可以访问
4. **性能优化**: 批量生成时使用异步任务，避免阻塞
5. **文件存储**: 生成的报表文件需要合理存储和管理

## 十、后续开发

1. 实现报表生成服务接口
2. 创建前端报表生成页面
3. 实现批量生成和异步任务
4. 添加报表模板管理功能
5. 优化用户体验和性能

## 十一、参考资源

- **集成教程**: [若依框架集成JimuReport积木报表](https://blog.csdn.net/qq_28775077/article/details/125475740)
- **Jimu报表官网**: https://www.jimureport.com/
- **Jimu报表文档**: https://help.jimureport.com/
- **GitHub**: https://github.com/jeecgboot/JimuReport

- Jimu报表官网: https://www.jimureport.com/
- Jimu报表文档: https://help.jimureport.com/
- GitHub: https://github.com/jeecgboot/JimuReport

