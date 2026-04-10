<template>
  <el-breadcrumb class="breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item
        v-for="item in breadcrumbList"
        :key="item.path"
        :to="{ path: item.path }"
      >
        {{ item.meta?.title }}
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { watch, ref } from 'vue'
import { useRoute } from 'vue-router'
import { RouteRecordRaw } from 'vue-router'
const route = useRoute()

const breadcrumbList = ref<RouteRecordRaw[]>([])

const initBreadcrumbList = () => {
  breadcrumbList.value = route.matched.slice(1)
}

watch(
  route,
  () => {
    initBreadcrumbList()
  },
  { deep: true, immediate: true }
)
</script>

<style lang="scss" scoped>
.el-breadcrumb {
  display: flex;
  align-items: center;
  min-width: 0;
  margin-left: 0;
}
</style>
