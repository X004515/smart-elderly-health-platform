<template>
  <myCard title="业务趋势">
    <div ref="chartRef" class="chart-shell"></div>
  </myCard>
</template>

<script lang="ts" setup>
import * as echarts from 'echarts'
import type { ECharts, EChartsOption } from 'echarts'
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { getBusinessTrend } from '@/apis/home'

const trendData = ref<any[]>([])
const chartRef = ref<HTMLElement | null>(null)
let chart: ECharts | null = null

const resizeChart = () => {
  chart?.resize()
}

const buildOption = (): EChartsOption => ({
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(32, 47, 44, 0.92)',
    borderWidth: 0,
    textStyle: {
      color: '#f7fbf9'
    }
  },
  grid: {
    left: 24,
    right: 24,
    top: 20,
    bottom: 26,
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: trendData.value.map((item: any) => item.month),
    axisLine: {
      lineStyle: {
        color: 'rgba(114, 140, 130, 0.26)'
      }
    },
    axisLabel: {
      color: '#6d8279'
    }
  },
  yAxis: {
    type: 'value',
    splitLine: {
      lineStyle: {
        color: 'rgba(114, 140, 130, 0.14)'
      }
    },
    axisLabel: {
      color: '#6d8279'
    }
  },
  series: [
    {
      data: trendData.value.map((item: any) => item.consultNum),
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: {
        width: 3,
        color: '#69a993'
      },
      itemStyle: {
        color: '#69a993',
        borderColor: '#ffffff',
        borderWidth: 2
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(105, 169, 147, 0.26)' },
          { offset: 1, color: 'rgba(105, 169, 147, 0.03)' }
        ])
      }
    }
  ]
})

const renderChart = () => {
  if (!chartRef.value) return
  if (!chart) {
    chart = echarts.init(chartRef.value)
  }
  chart.setOption(buildOption())
}

onMounted(async () => {
  const data: any = await getBusinessTrend()
  if (data.code === 200 && data.data) {
    trendData.value = data.data
  }
  renderChart()
  window.addEventListener('resize', resizeChart)
})

watch(trendData, () => {
  renderChart()
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeChart)
  chart?.dispose()
  chart = null
})
</script>
