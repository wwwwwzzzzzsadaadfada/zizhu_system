<template>
  <div class="sidebar-logo-container" :class="{'collapse':collapse}">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <div class="logo-content">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
          <h1 class="sidebar-title">资助管理系统</h1>
        </div>
      </router-link>
    </transition>
  </div>
</template>

<script>
import logoImg from '@/assets/logo/logo.png'
import variables from '@/assets/styles/variables.scss'

export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    variables() {
      return variables
    },
    sideTheme() {
      return this.$store.state.settings.sideTheme
    }
  },
  data() {
    return {
      title: '资助管理系统',
      logo: logoImg
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 80px;
  background: linear-gradient(to bottom, rgba(165, 214, 167, 0.9), rgba(200, 230, 201, 0.85)); // 增强绿色渐变
  backdrop-filter: blur(20px) saturate(180%); // 毛玻璃效果
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  text-align: center;
  overflow: visible !important;
  border-bottom: 2px solid rgba(102, 187, 106, 0.4); // 加深绿色边框
  box-shadow: 
    0 4px 16px rgba(102, 187, 106, 0.15),
    inset 0 1px 1px rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 12px;
  box-sizing: border-box;
  z-index: 10;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: visible !important;

    & .logo-content {
      display: flex;
      flex-direction: row; // 改为横向排列
      align-items: center; // 垂直居中对齐
      justify-content: center; // 水平居中对齐
      gap: 12px; // 增加logo和标题之间的间距
      width: 100%;
    }

    & .sidebar-logo {
      width: 44px;
      height: 44px;
      display: block;
      flex-shrink: 0; // 防止logo被压缩
      object-fit: contain; // 保持图片比例
    }

    & .sidebar-title {
      display: block;
      margin: 0;
      padding: 0;
      font-weight: 700; // 加粗
      line-height: 1.3;
      font-size: 16px;
      font-family: 'Source Han Sans SC', 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
      color: #2E7D32; // 深绿色
      white-space: nowrap; // 防止标题换行
      flex-shrink: 0; // 防止标题被压缩
      overflow: visible;
      text-overflow: clip;
      text-shadow: 0 1px 2px rgba(46, 125, 50, 0.1); // 文字阴影
    }
  }

  &.collapse {
    .sidebar-logo {
      width: 40px;
      height: 40px;
      margin: 0 auto;
    }
  }
}
</style>
