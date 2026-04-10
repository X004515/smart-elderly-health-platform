<template>
  <myCard title="今日回访进度">
    <div class="metric-grid metric-grid--compact">
      <article class="metric-card is-secondary">
        <span class="metric-label">应回访</span>
        <strong class="metric-value">{{ SaleFollow?.todayReturnVisitNum ?? 0 }}</strong>
        <p class="metric-note">当天计划回访总量。</p>
      </article>
      <article class="metric-card is-primary">
        <span class="metric-label">已回访</span>
        <strong class="metric-value">{{ SaleFollow?.todayReturnedVisitNum ?? 0 }}</strong>
        <p class="metric-note">已完成的客户跟进数量。</p>
      </article>
      <article class="metric-card is-deep">
        <span class="metric-label">待处理</span>
        <strong class="metric-value">{{ pendingVisit }}</strong>
        <p class="metric-note">仍需安排的回访任务。</p>
      </article>
    </div>
  </myCard>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { getTodaySaleFollow } from '@/apis/home'

const SaleFollow = ref<any>()
const pendingVisit = computed(() => {
  const total = SaleFollow.value?.todayReturnVisitNum ?? 0
  const finished = SaleFollow.value?.todayReturnedVisitNum ?? 0
  return Math.max(total - finished, 0)
})

onMounted(async () => {
  const data: any = await getTodaySaleFollow()
  if (data.code === 200 && data.data) {
    SaleFollow.value = data.data
  }
})
</script>
