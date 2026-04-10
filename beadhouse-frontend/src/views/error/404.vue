<template>
  <div class="error-page">
    <div class="error-card">
      <p class="error-kicker">404</p>
      <h1>页面暂时找不到</h1>
      <p>
        当前地址可能已变更，或者你还没有访问权限。返回首页后，可以继续从侧边导航进入对应模块。
      </p>
      <el-button type="primary" @click="toHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import router from '@/router'

const toHome = () => {
  router.push({ path: '/' })
}
</script>

<style lang="scss" scoped>
.error-page {
  min-height: calc(100vh - 160px);
  display: grid;
  place-items: center;
}

.error-card {
  width: min(560px, 100%);
  padding: 40px;
  border: 1px solid rgba(132, 163, 151, 0.16);
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.84);
  box-shadow: 0 24px 60px rgba(68, 94, 86, 0.1);
  text-align: center;
}

.error-kicker {
  margin: 0 0 8px;
  color: var(--app-primary-strong);
  letter-spacing: 0.2em;
  text-transform: uppercase;
}

.error-card h1 {
  margin: 0;
  font-family: var(--font-display);
  font-size: clamp(2.2rem, 5vw, 3.4rem);
}

.error-card p {
  margin: 16px auto 28px;
  max-width: 32rem;
  line-height: 1.8;
  color: var(--app-text-soft);
}
</style>
