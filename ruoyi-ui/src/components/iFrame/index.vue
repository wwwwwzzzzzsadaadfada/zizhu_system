<template>
  <div v-loading="loading" :style="'height:' + height">
    <iframe
      ref="iframe"
      :src="src"
      frameborder="no"
      style="width: 100%; height: 100%"
      scrolling="auto"
      @load="handleLoad"
    />
  </div>
</template>
<script>
export default {
  props: {
    src: {
      type: String,
      required: true
    },
  },
  data() {
    return {
      height: document.documentElement.clientHeight - 94.5 + "px;",
      loading: true,
      url: this.src
    }
  },
  watch: {
    src(newVal) {
      // URL 变化时显示加载状态
      if (newVal !== this.url) {
        this.loading = true
        this.url = newVal
      }
    }
  },
  mounted: function () {
    this.url = this.src
    // 初始化时不立即隐藏loading，等待iframe加载
    const that = this
    window.onresize = function temp() {
      that.height = document.documentElement.clientHeight - 94.5 + "px;"
    }
  },
  methods: {
    handleLoad() {
      // iframe 加载完成后隐藏加载状态
      // 给报表一点渲染时间，但不要太长（500ms足够）
      setTimeout(() => {
        this.loading = false
      }, 500)
      // 触发自定义事件，通知父组件iframe已加载
      this.$emit('iframe-load')
    }
  }
}
</script>
