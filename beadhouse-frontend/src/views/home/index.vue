<template>
  <div class="app-page home-page">
    <section class="page-intro">
      <div class="page-intro-copy">
        <p class="page-intro-kicker">Daily Care Dashboard</p>
        <h1>运营与照护一体化看板</h1>
        <p>
          把咨询、入住、床位、服务和本月表现放在同一张工作台里，帮助管理员和护理员更快找到当天重点。
        </p>
      </div>
      <div class="page-intro-aside">
        <span class="soft-pill">护理优先</span>
        <span class="soft-pill">实时状态</span>
        <span class="soft-pill">清晰协作</span>
      </div>
    </section>

    <section class="dashboard-grid">
      <todayOverview class="dashboard-span-12" />
      <SoldBed class="dashboard-span-6" />
      <todaySold class="dashboard-span-6" />
      <monthPerformance class="dashboard-span-12" />
      <trend class="dashboard-span-12" />
    </section>
  </div>
</template>

<script setup lang="ts">
import todaySold from './todaySold.vue'
import monthPerformance from './monthPerformance.vue'
import trend from './trend.vue'
import SoldBed from './soldBed.vue'
import todayOverview from './todayOverview.vue'
</script>

<style lang="scss" scoped>
.home-page {
  width: 100%;
}
</style>
