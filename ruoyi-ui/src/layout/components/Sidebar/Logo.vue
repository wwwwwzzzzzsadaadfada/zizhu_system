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
  background: #635fd6;
  text-align: center;
  overflow: visible !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
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
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 6px;
      width: 100%;
    }

    & .sidebar-logo {
      width: 44px;
      height: 44px;
      display: block;
      margin: 0 auto;
      flex-shrink: 0;
    }

    & .sidebar-title {
      display: block;
      margin: 0;
      padding: 0;
      font-weight: 600;
      line-height: 1.3;
      font-size: 16px;
      font-family: 'Source Han Sans SC', 'Noto Sans SC', 'Microsoft YaHei', 'SimHei', sans-serif;
      color: #ffffff;
      white-space: nowrap;
      width: 100%;
      text-align: center;
      overflow: visible;
      text-overflow: clip;
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
