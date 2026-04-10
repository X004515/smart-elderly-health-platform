<template>
  <myCard title="今日总览">
    <div class="metric-grid">
      <article
        v-for="item in metricItems"
        :key="item.label"
        class="metric-card"
        :class="item.tone"
      >
        <span class="metric-label">{{ item.label }}</span>
        <strong class="metric-value">{{ item.value }}</strong>
        <p class="metric-note">{{ item.note }}</p>
      </article>
    </div>
  </myCard>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { getTodayOverview } from '@/apis/home'

const overViewData = ref<any>()

const metricItems = computed(() => [
  {
    label: '今日新增咨询',
    value: overViewData.value?.todayAddConsultNum ?? 0,
    note: '及时跟进新增意向，避免信息沉淀。',
    tone: 'is-secondary'
  },
  {
    label: '今日新增预定',
    value: overViewData.value?.todayAddReserveNum ?? 0,
    note: '关注预定转入住的衔接效率。',
    tone: 'is-primary'
  },
  {
    label: '今日新增合同',
    value: overViewData.value?.todayAddContractNum ?? 0,
    note: '同步合同签约与后续照护准备。',
    tone: 'is-accent'
  },
  {
    label: '合同到期提醒',
    value: overViewData.value?.todayContractExpireNum ?? 0,
    note: '提前处理续签与沟通，减少服务中断。',
    tone: 'is-deep'
  }
])

onMounted(async () => {
  const data: any = await getTodayOverview()
  if (data.code === 200 && data.data) {
    overViewData.value = data.data
  }
})
</script>
