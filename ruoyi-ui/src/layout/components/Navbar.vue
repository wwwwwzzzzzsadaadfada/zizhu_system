<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb v-if="!topNav" id="breadcrumb-container" class="breadcrumb-container" />
    <top-nav v-if="topNav" id="topmenu-container" class="topmenu-container" />

    <div
      class="right-menu"
      :class="{'collapse': !sidebar.opened}"
      :style="{
        width: sidebar.opened ? '280px' : '64px',
        minWidth: sidebar.opened ? '280px' : '64px',
        maxWidth: sidebar.opened ? '280px' : '64px'
      }">
<!--      <template v-if="device!=='mobile'">-->
<!--        <search id="header-search" class="right-menu-item" />-->

<!--        <el-tooltip content="源码地址" effect="dark" placement="bottom">-->
<!--          <ruo-yi-git id="ruoyi-git" class="right-menu-item hover-effect" />-->
<!--        </el-tooltip>-->

<!--        <el-tooltip content="文档地址" effect="dark" placement="bottom">-->
<!--          <ruo-yi-doc id="ruoyi-doc" class="right-menu-item hover-effect" />-->
<!--        </el-tooltip>-->

<!--        <screenfull id="screenfull" class="right-menu-item hover-effect" />-->

<!--        <el-tooltip content="布局大小" effect="dark" placement="bottom">-->
<!--          <size-select id="size-select" class="right-menu-item hover-effect" />-->
<!--        </el-tooltip>-->

<!--      </template>-->

      <div class="avatar-container-wrapper">
        <el-dropdown
          class="avatar-container right-menu-item hover-effect"
          trigger="hover">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar">
          <span class="user-nickname"> {{ nickName }} </span>
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="setLayout" v-if="setting">
            <span>布局设置</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from '@/components/TopNav'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import SizeSelect from '@/components/SizeSelect'
import Search from '@/components/HeaderSearch'
import RuoYiGit from '@/components/RuoYi/Git'
import RuoYiDoc from '@/components/RuoYi/Doc'
import variables from '@/assets/styles/variables.scss'

export default {
  emits: ['setLayout'],
  components: {
    Breadcrumb,
    TopNav,
    Hamburger,
    Screenfull,
    SizeSelect,
    Search,
    RuoYiGit,
    RuoYiDoc
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'device',
      'nickName'
    ]),
    setting: {
      get() {
        return this.$store.state.settings.showSettings
      }
    },
    topNav: {
      get() {
        return this.$store.state.settings.topNav
      }
    },
    variables() {
      return variables
    },
  },
  mounted() {
    this.setRightMenuWidth()
    setTimeout(() => {
      this.setRightMenuWidth()
    }, 100)
  },
  watch: {
    'sidebar.opened'() {
      this.$nextTick(() => {
        this.setRightMenuWidth()
        setTimeout(() => {
          this.setRightMenuWidth()
        }, 100)
      })
    }
  },
  methods: {
    setRightMenuWidth() {
      this.$nextTick(() => {
        const rightMenu = this.$el.querySelector('.right-menu')
        if (rightMenu) {
          const width = this.sidebar.opened ? '280px' : '64px'
          rightMenu.style.setProperty('width', width, 'important')
          rightMenu.style.setProperty('min-width', width, 'important')
          rightMenu.style.setProperty('max-width', width, 'important')
        }
      })
    },
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    setLayout(event) {
      this.$emit('setLayout')
    },
    logout() {
      this.$confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          location.href = '/index'
        })
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';
.navbar {
  height: 80px;
  overflow: visible;
  position: relative;
  width: 100%;
  background: #635fd6;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  align-items: center;

  .hamburger-container {
    line-height: 80px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;
    padding: 0 15px;
    box-sizing: border-box;
    display: flex;
    align-items: center;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }

    // 确保Hamburger组件内部的div也有正确的padding
    ::v-deep > div {
      padding: 0 !important;
      display: flex;
      align-items: center;
      height: 100%;
    }
  }

  .breadcrumb-container {
    float: left;
    margin-left: 0;
    height: 100%;
    display: flex;
    align-items: center;
    padding-top: 0;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
    padding-top: 6px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    position: absolute !important;
    right: 0 !important;
    top: 0 !important;
    height: 80px !important;
    width: 280px !important;
    min-width: 280px !important;
    max-width: 280px !important;
    background-color: #635fd6 !important;
    transition: width 0.28s, min-width 0.28s, max-width 0.28s;
    z-index: 10 !important;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding-right: 8px;

    &:focus {
      outline: none;
    }

    &.collapse {
      width: 64px !important;
      min-width: 64px !important;
      max-width: 64px !important;
    }

    .avatar-container-wrapper {
      width: 100% !important;
      height: 100% !important;
      background-color: transparent !important;
      display: block !important;
      box-sizing: border-box !important;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container-wrapper {
      width: $base-sidebar-width !important;
      min-width: $base-sidebar-width !important;
      max-width: $base-sidebar-width !important;
      transition: width 0.28s, min-width 0.28s, max-width 0.28s;

      &.collapse,
      .collapse {
        width: 64px !important;
        min-width: 64px !important;
        max-width: 64px !important;
      }
    }

    .avatar-container {
      margin-right: 0px !important;
      padding: 0 !important;
      width: 100% !important;
      min-width: 100% !important;
      max-width: 100% !important;
      text-align: center;
      display: block !important;
      box-sizing: border-box !important;
      background-color: transparent !important;

      &.right-menu-item {
        display: block !important;
        padding: 0 !important;
        width: $base-sidebar-width !important;
        min-width: $base-sidebar-width !important;
        max-width: $base-sidebar-width !important;
      }

      &.collapse {
        width: 64px !important;
        min-width: 64px !important;
        max-width: 64px !important;
      }

      // 确保Element UI的dropdown组件宽度正确
      ::v-deep .el-dropdown {
        width: 100% !important;
      }

      .avatar-wrapper {
        margin: 0;
        position: relative;
        display: flex !important;
        align-items: center !important;
        justify-content: flex-end !important;
        width: 100% !important;
        height: 100% !important;
        padding: 0 4px;
        box-sizing: border-box;

        .user-avatar {
          cursor: pointer;
          width: 30px;
          height: 30px;
          border-radius: 50%;
          vertical-align: middle;
        }

        .user-nickname{
          position: relative;
          bottom: 0;
          left: 2px;
          font-size: 14px;
          font-weight: bold;
          vertical-align: middle;
          color: #ffffff;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>

<style lang="scss">
// 全局样式确保右侧个人信息区域宽度与logo区域一致
@import '@/assets/styles/variables.scss';

// 确保折叠按钮的padding-top生效
.navbar .hamburger-container {
  padding-top: 0 !important;
}

.navbar .hamburger-container > div {
  padding-top: 0 !important;
  padding-bottom: 0 !important;
  padding-left: 0 !important;
  padding-right: 0 !important;
  display: flex !important;
  align-items: center !important;
  height: 100% !important;
}

// 确保right-menu占据navbar右侧整个宽度区域，与左侧logo区域对称
.navbar {
  .right-menu {
    position: absolute !important;
    right: 0 !important;
    top: 0 !important;
    width: 280px !important;
    min-width: 280px !important;
    max-width: 280px !important;
    background-color: #635fd6 !important;
    box-sizing: border-box !important;
    height: 80px !important;
    z-index: 1 !important;

    &.collapse {
      width: 64px !important;
      min-width: 64px !important;
      max-width: 64px !important;
    }
  }
}

// 确保dropdown组件占满容器宽度
.navbar .right-menu .avatar-container-wrapper .avatar-container,
.navbar .right-menu .avatar-container-wrapper .avatar-container.el-dropdown {
  width: 100% !important;
  min-width: 100% !important;
  max-width: 100% !important;
  box-sizing: border-box !important;
  height: 100% !important;
}

// 确保avatar-wrapper也占满宽度
.navbar .right-menu .avatar-container-wrapper .avatar-wrapper {
  width: 100% !important;
  height: 100% !important;
  display: flex !important;
  align-items: center !important;
  justify-content: flex-end !important;
  box-sizing: border-box !important;
}
</style>
