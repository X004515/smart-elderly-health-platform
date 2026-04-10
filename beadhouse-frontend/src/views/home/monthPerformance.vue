<template>
  <myCard title="本月业务表现">
    <div class="metric-grid metric-grid--compact">
      <article class="metric-card is-secondary compact-card">
        <span class="metric-label">咨询客户</span>
        <strong class="metric-value">{{ MonthPerformance?.consultClientNum ?? 0 }}</strong>
        <p class="metric-note">环比 {{ MonthPerformance?.consultClientFloatRate ?? 0 }}%</p>
      </article>
      <article class="metric-card is-primary compact-card">
        <span class="metric-label">签约合同</span>
        <strong class="metric-value">{{ MonthPerformance?.signContractNum ?? 0 }}</strong>
        <p class="metric-note">环比 {{ MonthPerformance?.signContractFloatRate ?? 0 }}%</p>
      </article>
      <article class="metric-card is-accent compact-card">
        <span class="metric-label">咨询转化率</span>
        <strong class="metric-value">{{ MonthPerformance?.consultConversionRate ?? 0 }}%</strong>
        <p class="metric-note">环比 {{ MonthPerformance?.consultConversionFloatRate ?? 0 }}%</p>
      </article>
    </div>

    <el-table :data="tableData" stripe fit>
      <el-table-column prop="rank" label="排名" width="90" />
      <el-table-column prop="name" label="业务员" min-width="180" />
      <el-table-column prop="consultNum" sortable label="接待咨询" min-width="180" />
      <el-table-column prop="contractNum" sortable label="签约合同" min-width="180" />
    </el-table>
  </myCard>
</template>

<script lang="ts" setup>
import { getMonthPerformanceRank } from '@/apis/home'
import { onMounted, ref } from 'vue'
const MonthPerformance = ref<any>()
const tableData = ref<any[]>([])
onMounted(async () => {
  const data: any = await getMonthPerformanceRank()
  if (data.code === 200 && data.data) {
    MonthPerformance.value = data.data
    tableData.value = data.data.saleRankList || []
  }
})
</script>

<style lang="scss" scoped>
.compact-card {
  min-height: 136px;
}
</style>
