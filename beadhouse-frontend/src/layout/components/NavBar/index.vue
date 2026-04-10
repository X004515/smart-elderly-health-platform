<template>
  <div class="nav-container">
    <div class="nav-left">
      <div class="nav-accent"></div>
      <div class="nav-copy">
        <div class="nav-eyebrow">照护运营台</div>
        <Breadcrumb />
      </div>
    </div>
    <div class="nav-right">
      <span class="nav-status">系统运行中</span>
      <Avatar class="nav-item" />
    </div>
  </div>
</template>

<script setup lang="ts">
import Avatar from './components/Avatar.vue'
import Breadcrumb from './components/Breadcrumb.vue'
</script>

<style lang="scss" scoped>
.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18px;
  width: 100%;
  min-height: 72px;
  margin-top: 16px;
  padding: 0 24px;
  border: 1px solid rgba(132, 163, 151, 0.16);
  border-radius: 26px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 18px 40px rgba(68, 94, 86, 0.08);
  backdrop-filter: blur(16px);

  .nav-right,
  .nav-left {
    display: flex;
    align-items: center;
    gap: 14px;
    min-width: 0;
  }
}

.nav-left {
  flex: 1;
}

.nav-copy {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.nav-accent {
  width: 12px;
  height: 42px;
  border-radius: 999px;
  background: linear-gradient(180deg, rgba(118, 185, 163, 0.95), rgba(226, 188, 126, 0.9));
}

.nav-eyebrow {
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: var(--app-text-muted);
}

.nav-status {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 0 12px;
  border: 1px solid rgba(123, 157, 145, 0.16);
  border-radius: 999px;
  background: rgba(243, 248, 245, 0.88);
  color: var(--app-text-soft);
  font-size: 13px;
  font-weight: 600;
}
</style>
