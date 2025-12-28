# 积木报表iframe加载问题解决方案

## 问题描述

在iframe中加载积木报表时，出现以下错误：
- `Uncaught SyntaxError: Unexpected token '<'`
- `Uncaught ReferenceError: Vue is not defined`
- JS文件返回HTML内容（通常是登录页面或错误页面）

## 问题原因

1. **认证问题**：积木报表需要登录，但iframe中无法传递认证信息
2. **URL格式错误**：积木报表的预览URL格式可能不正确
3. **跨域问题**：静态资源路径不正确
4. **权限配置**：SecurityConfig中可能没有正确配置

## 解决方案

### 方案1：使用新窗口打开（推荐，最简单）

如果iframe加载有问题，直接使用新窗口打开：

```javascript
// 修改openReportInNewWindow方法，默认使用新窗口
openReportInNewWindow() {
  if (this.reportUrl) {
    window.open(this.reportUrl, '_blank', 'width=1200,height=800,scrollbars=yes')
  } else {
    this.$modal.msgWarning('报表URL未生成，请稍后再试')
  }
}
```

然后在报表tab中，默认显示"新窗口打开"按钮，而不是iframe预览。

### 方案2：检查URL格式

积木报表的预览URL可能有以下几种格式：

1. **标准格式**：`/jmreport/view/{reportId}?参数名=参数值`
2. **带token格式**：`/jmreport/view/{reportId}?参数名=参数值&token=xxx`
3. **其他格式**：`/jmreport/preview/{reportId}?参数名=参数值`

**验证方法**：
1. 在浏览器中直接访问：`http://localhost:8080/jmreport/view/{reportId}?studentId=1`
2. 如果能够正常显示，说明URL格式正确
3. 如果不能显示，检查是否需要登录或URL格式

### 方案3：配置认证（如果需要）

如果积木报表需要认证，可以：

1. **在URL中传递token**：
```javascript
const token = this.$store.state.user.token
if (token) {
  this.reportUrl = `${baseUrl}/jmreport/view/${reportId}?studentId=${studentId}&token=${token}`
}
```

2. **配置SecurityConfig允许匿名访问**（已配置）：
```java
.antMatchers("/jmreport/**").permitAll()
```

### 方案4：使用后端代理（高级）

如果跨域问题无法解决，可以通过后端代理：

1. 创建后端接口：
```java
@GetMapping("/system/report/preview/{reportId}")
public void previewReport(@PathVariable String reportId, 
                         @RequestParam String studentId,
                         HttpServletResponse response) {
    // 调用积木报表接口，转发响应
}
```

2. 前端使用代理URL：
```javascript
this.reportUrl = `${baseUrl}/system/report/preview/${reportId}?studentId=${studentId}`
```

## 快速修复步骤

### 步骤1：验证URL是否正确

在浏览器中直接访问：
```
http://localhost:8080/jmreport/view/1159821341794144256?studentId=2
```

如果能够正常显示报表，说明URL格式正确。

### 步骤2：检查是否需要登录

如果访问时跳转到登录页，说明需要认证。

**解决方案**：
- 确保SecurityConfig中配置了：`.antMatchers("/jmreport/**").permitAll()`
- 或者在URL中传递token

### 步骤3：如果iframe仍然失败，使用新窗口

修改报表tab，默认使用新窗口打开：

```vue
<div v-show="activeTab === 'report'">
  <el-card shadow="never" class="form-card report-card">
    <div slot="header" class="card-header">
      <i class="el-icon-document"></i>
      <span>普通高中国家助学金申请表</span>
    </div>
    <div class="report-preview-container">
      <el-empty description="点击下方按钮在新窗口中查看报表" :image-size="100">
        <el-button 
          type="primary" 
          icon="el-icon-view" 
          @click="openReportInNewWindow"
        >在新窗口中打开报表</el-button>
      </el-empty>
    </div>
  </el-card>
</div>
```

## 推荐的最终方案

考虑到iframe的兼容性问题，推荐使用**新窗口打开**的方式：

1. **优点**：
   - 避免iframe跨域问题
   - 避免认证问题
   - 打印更方便
   - 用户体验更好

2. **实现**：
   - 在报表tab中显示一个按钮
   - 点击按钮在新窗口中打开报表
   - 新窗口中可以正常打印和导出

3. **代码示例**：
```vue
<div v-show="activeTab === 'report'">
  <el-card shadow="never" class="form-card">
    <div slot="header" class="card-header">
      <i class="el-icon-document"></i>
      <span>普通高中国家助学金申请表</span>
    </div>
    <div class="report-action-container">
      <el-button 
        type="primary" 
        icon="el-icon-view" 
        @click="openReportInNewWindow"
        size="medium"
      >查看报表</el-button>
      <el-button 
        type="success" 
        icon="el-icon-printer" 
        @click="printReport"
        size="medium"
      >打印报表</el-button>
      <el-button 
        type="warning" 
        icon="el-icon-download" 
        @click="exportReport"
        size="medium"
      >导出PDF</el-button>
    </div>
  </el-card>
</div>
```

## 验证步骤

1. 确认报表ID已配置
2. 在浏览器中直接访问报表URL，验证是否正常
3. 如果直接访问正常，iframe应该也能正常
4. 如果iframe失败，使用新窗口方案

