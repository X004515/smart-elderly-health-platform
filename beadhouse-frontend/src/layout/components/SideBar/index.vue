<template>
  <div class="sidebar-container">
    <div class="logo-container">
      <router-link title="敬老院管理系统" to="/">
        <div class="brand-mark">
          <img class="sidebar-logo" src="@/assets/imgs/logo.png" />
        </div>
        <div class="brand-copy">
          <span class="sidebar-title">敬老院管理系统</span>
          <span class="sidebar-subtitle">护理与运营协同</span>
        </div>
      </router-link>
    </div>
    <el-scrollbar>
      <el-menu
        :active-text-color="variables.menuActiveText"
        :background-color="variables.menuBg"
        :default-active="active"
        text-color="#fefefea6"
        unique-opened
        :collapse="!$store.state.app.siderType"
      >
        <MenuItem :menuList="store.state.app.routeTree" />
      </el-menu>
    </el-scrollbar>
    <div class="menufold-container">
      <MenuFold />
    </div>
  </div>
</template>

<script setup lang="ts">
import MenuItem from './components/MenuItem.vue'
import MenuFold from './components/MenuFold.vue'
import variables from '@/styles/variables.module.scss'
import { ref, watch } from 'vue'
import { RouteRecordName, useRoute } from 'vue-router'
import store from '@/store'

const route = useRoute()

let active = ref<RouteRecordName>('Home')

watch(
  () => route,
  newVal => {
    if (newVal.name) {
      active.value = newVal.name
    }
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style lang="scss">
.logo-container {
  .router-link-active {
    display: flex;
    align-items: center;
    gap: 14px;
    width: 100%;
    min-height: 98px;
    padding: 18px 16px;
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 26px;
    background: rgba(255, 255, 255, 0.05);
    flex-wrap: nowrap;
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.06);

    .brand-mark {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 52px;
      height: 52px;
      border-radius: 18px;
      background: rgba(255, 255, 255, 0.12);
      box-shadow: 0 16px 30px rgba(11, 23, 20, 0.18);
      flex-shrink: 0;
    }

    .sidebar-logo {
      width: 30px;
      height: 30px;
    }

    .brand-copy {
      display: flex;
      flex-direction: column;
      gap: 6px;
      min-width: 0;
    }

    .sidebar-title {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-family: var(--font-display);
      font-size: 20px;
      color: #fff;
    }

    .sidebar-subtitle {
      font-size: 12px;
      letter-spacing: 0.08em;
      color: rgba(239, 246, 243, 0.7);
    }
  }
}

.menufold-container {
  position: absolute;
  bottom: 14px;
  left: 14px;
  right: 14px;
  width: auto;
  height: 48px;
  line-height: 48px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.06);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.05);
  z-index: 999;
}
</style>
