<template>
  <myCard title="床位与房间状态">
    <div class="stat-pair">
      <div class="stat-panel">
        <span class="data-caption">空闲房间</span>
        <strong class="data-value">{{ AvailableBed?.idleRoomNum ?? 0 }}</strong>
        <span class="data-subvalue">帮助管理员快速判断接待能力。</span>
      </div>
      <div class="stat-panel">
        <span class="data-caption">空闲床位</span>
        <strong class="data-value">{{ AvailableBed?.idleBedNum ?? 0 }}</strong>
        <span class="data-subvalue">让入住安排和护理排班更有余量。</span>
      </div>
    </div>
  </myCard>
</template>

<script lang="ts" setup>
import { getAvailableBed } from '@/apis/home'
import { onMounted, ref } from 'vue'
const AvailableBed = ref<any>()
onMounted(async () => {
  const data: any = await getAvailableBed()
  if (data.code === 200 && data.data) {
    AvailableBed.value = data.data
  }
})
</script>
