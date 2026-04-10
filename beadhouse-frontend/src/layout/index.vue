<template>
  <el-container
    class="layout-container"
    :class="{ hideSidebar: !$store.state.app.siderType }"
  >
    <el-aside
      class="app-sidebar"
      :width="$store.state.app.siderType ? '248px' : '88px'"
    >
      <SideBar />
    </el-aside>
    <el-container class="main-container">
      <el-header class="app-header">
        <NavBar />
      </el-header>
      <el-main class="app-main">
        <el-scrollbar class="main-scrollbar">
          <div class="page-shell">
            <router-view v-slot="{ Component }">
              <transition name="fade-transform" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </div>
        </el-scrollbar>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import SideBar from './components/SideBar/index.vue'
import NavBar from './components/NavBar/index.vue'

// window.onresize = () =>
//   (() => {
//     /** width app-wrapper */
//     let width = document.body.clientWidth
//
//     if (width > 0 && width <= 760) {
//       store.commit('app/setDeviceType', 'phone')
//     } else if (width > 760 && width <= 990) {
//       store.commit('app/setDeviceType', 'ipaid')
//     } else if (width > 990) {
//       store.commit('app/setDeviceType', 'desktop')
//     }
//   })()
</script>

<style lang="scss" scoped>
.layout-container {
  position: relative;
  width: 100%;
  height: 100%;
  background: transparent;
}

.app-sidebar {
  position: relative;
  z-index: 3;
}

.app-header {
  position: relative;
  height: 88px !important;
  padding: 0 !important;
  background: transparent;
}

.app-main {
  padding: 0 !important;
  background: transparent;
}

.main-scrollbar {
  height: 100%;
}

.page-shell {
  min-height: 100%;
  padding: 10px clamp(18px, 2.4vw, 36px) 36px;
}
</style>
