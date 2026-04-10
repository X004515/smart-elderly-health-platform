<template>
  <MyCard v-if="columns.length" class="search-card">
    <div class="table-search">
      <el-form
        ref="formRef"
        :model="searchParam"
        class="search-form"
        label-position="top"
      >
        <Grid
          ref="gridRef"
          :collapsed="collapsed"
          :gap="[18, 18]"
          :cols="searchCol"
        >
          <GridItem
            v-for="(item, index) in columns"
            :key="item.prop"
            v-bind="getResponsive(item)"
            :index="index"
          >
            <el-form-item :label="item.label">
              <SearchFormItem :column="item" :searchParam="searchParam" />
            </el-form-item>
          </GridItem>
          <GridItem suffix>
            <div class="operation search-actions">
              <el-button
                class="search-action-primary"
                type="primary"
                :icon="Search"
                @click="search"
              >
                搜索
              </el-button>
              <el-button :icon="Delete" @click="reset">重置</el-button>
              <el-button
                v-if="showCollapse"
                link
                class="search-toggle"
                @click="collapsed = !collapsed"
              >
                {{ collapsed ? '展开' : '收起' }}
                <el-icon class="el-icon--right">
                  <component :is="collapsed ? ArrowDown : ArrowUp"></component>
                </el-icon>
              </el-button>
            </div>
          </GridItem>
        </Grid>
      </el-form>
    </div>
  </MyCard>
</template>
<script setup lang="ts" name="SearchForm">
import { computed, ref } from 'vue'
import { ColumnProps } from '@/components/ProTable/interface'
import { BreakPoint } from '@/components/Grid/interface'
import { Delete, Search, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import SearchFormItem from './components/SearchFormItem.vue'
import Grid from '@/components/Grid/index.vue'
import GridItem from '@/components/Grid/components/GridItem.vue'
import MyCard from '../my-card/my-card.vue'

interface ProTableProps {
  columns?: ColumnProps[]
  searchParam?: { [key: string]: any }
  searchCol: number | Record<BreakPoint, number>
  search: (params: any) => void
  reset: (params: any) => void
}

const props = withDefaults(defineProps<ProTableProps>(), {
  columns: () => [],
  searchParam: () => ({})
})

const getResponsive = (item: ColumnProps) => {
  return {
    span: item.search?.span,
    offset: item.search?.offset ?? 0,
    xs: item.search?.xs,
    sm: item.search?.sm,
    md: item.search?.md,
    lg: item.search?.lg,
    xl: item.search?.xl
  }
}

const collapsed = ref(true)

const gridRef = ref()
const breakPoint = computed<BreakPoint>(() => gridRef.value?.breakPoint)

const showCollapse = computed(() => {
  let show = false
  props.columns.reduce((prev, current) => {
    prev +=
      (current.search![breakPoint.value]?.span ?? current.search?.span ?? 1) +
      (current.search![breakPoint.value]?.offset ?? current.search?.offset ?? 0)
    if (typeof props.searchCol !== 'number') {
      if (prev >= props.searchCol[breakPoint.value]) show = true
    } else {
      if (prev > props.searchCol) show = true
    }
    return prev
  }, 0)
  return show
})
</script>

<style lang="scss" scoped>
.search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.search-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 28px;
}

.search-actions :deep(.el-button + .el-button) {
  margin-left: 0;
}

.search-toggle {
  min-height: 40px;
}
</style>
