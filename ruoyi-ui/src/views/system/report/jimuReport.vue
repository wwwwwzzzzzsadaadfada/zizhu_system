<template>
  <div class="jimu-report-container">
    <!-- 工具栏：提供常用功能的快速访问 -->
    <div class="jimu-report-toolbar">
      <el-button-group>
        <el-button size="small" icon="el-icon-menu" @click="navigateTo('/jmreport/list')">报表列表</el-button>
        <el-button size="small" icon="el-icon-plus" @click="createNewReport">新建报表</el-button>
        <el-button size="small" icon="el-icon-refresh" @click="refreshIframe">刷新</el-button>
      </el-button-group>
    </div>
    <!-- iframe 容器 -->
    <div class="jimu-report-iframe-wrapper">
      <i-frame ref="iframe" :src="currentUrl" />
    </div>
  </div>
</template>
<script>
import iFrame from "@/components/iFrame/index"
export default {
  name: "JimuReport",
  components: { iFrame },
  data() {
    return {
      baseUrl: process.env.NODE_ENV === 'development'
        ? 'http://localhost:8080'
        : window.location.origin,
      currentUrl: ''
    }
  },
  mounted() {
    // 初始化URL - 默认显示报表列表
    this.currentUrl = this.baseUrl + '/jmreport/list'
  },
  methods: {
    // 导航到指定页面
    navigateTo(path) {
      let fullUrl = path
      if (path.startsWith('/')) {
        fullUrl = this.baseUrl + path
      } else if (!path.startsWith('http')) {
        fullUrl = this.baseUrl + '/jmreport/' + path
      }
      this.currentUrl = fullUrl
    },

    // 刷新 iframe
    refreshIframe() {
      const iframe = this.$refs.iframe
      if (iframe && iframe.$refs && iframe.$refs.iframe) {
        iframe.$refs.iframe.src = this.currentUrl
      } else {
        // 强制重新加载
        const url = this.currentUrl
        this.currentUrl = ''
        this.$nextTick(() => {
          this.currentUrl = url
        })
      }
    },

    // 新建报表
    createNewReport() {
      // 根据实际使用情况，JimuReport 的设计器路径格式为：/jmreport/index/{reportId}?menuType=chartinfo
      // 新建报表需要先创建报表记录获取ID，通常通过列表页面的"新建"按钮完成
      // 方案：跳转到列表页面，用户可以在列表页面点击"新建"按钮来创建报表
      // 列表页面的"新建"按钮会自动创建报表并跳转到设计器
      this.navigateTo('/jmreport/list')
    }
  }
}
</script>
<style scoped>
.jimu-report-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.jimu-report-toolbar {
  padding: 10px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}

.jimu-report-iframe-wrapper {
  flex: 1;
  overflow: hidden;
  position: relative;
}
</style>

