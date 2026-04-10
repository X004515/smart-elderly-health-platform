<template>
  <SearchForm
    v-show="isShowSearch"
    ref="searchForm"
    :search="search"
    :reset="reset"
    :searchParam="searchParam"
    :columns="searchColumns"
    :searchCol="searchCol"
  />

  <MyCard class="table-card">
    <div class="table-main">
      <div class="table-toolbar">
        <div class="table-toolbar__primary">
          <slot
            name="tableHeader"
            :selectedListIds="selectedListIds"
            :selectedList="selectedList"
            :isSelected="isSelected"
          />
        </div>

        <div class="table-toolbar__secondary">
          <slot name="toolButton">
            <el-button :icon="Refresh" circle @click="getTableList" />
            <el-button
              v-if="columns.length"
              :icon="Operation"
              circle
              @click="openColSetting"
            />
            <el-button
              v-if="searchColumns.length"
              :icon="Search"
              circle
              @click="isShowSearch = !isShowSearch"
            />
          </slot>
        </div>
      </div>

      <el-table
        ref="tableRef"
        class="pro-table"
        v-bind="$attrs"
        :data="tableData"
        :border="border"
        :row-key="rowKey"
        @selection-change="selectionChange"
      >
        <slot></slot>
        <template v-for="item in tableColumns" :key="item.prop || item.type || item.label">
          <el-table-column
            v-if="item.type == 'selection' || item.type == 'index'"
            v-bind="item"
            :align="item.align ?? 'center'"
            :reserve-selection="item.type == 'selection'"
          />

          <el-table-column
            v-else-if="item.type == 'expand'"
            v-bind="item"
            :align="item.align ?? 'center'"
            v-slot="scope"
          >
            <component :is="item.render" :row="scope.row" v-if="item.render" />
            <slot :name="item.type" :row="scope.row" v-else></slot>
          </el-table-column>

          <TableColumn
            v-else-if="item.prop && item.isShow"
            :column="item"
          >
            <template v-for="slot in Object.keys($slots)" #[slot]="scope">
              <slot :name="slot" :row="scope.row"></slot>
            </template>
          </TableColumn>
        </template>

        <template #append>
          <slot name="append"></slot>
        </template>

        <template #empty>
          <div class="table-empty">
            <slot name="empty">
              <div>暂无数据</div>
            </slot>
          </div>
        </template>
      </el-table>

      <slot name="pagination">
        <div class="table-pagination">
          <Pagination
            v-if="pagination"
            :pageable="pageable"
            :handleSizeChange="handleSizeChange"
            :handleCurrentChange="handleCurrentChange"
          />
        </div>
      </slot>
    </div>
  </MyCard>

  <ColSetting v-if="toolButton" ref="colRef" v-model:colSetting="colSetting" />
</template>

<script setup lang="ts" name="ProTable">
import { ref, watch, provide, onMounted } from 'vue'
import { useTable } from '@/hooks/useTable'
import { BreakPoint } from '@/components/Grid/interface'
import { ColumnProps } from '@/components/ProTable/interface'
import { ElTable, TableProps } from 'element-plus'
import { Refresh, Operation, Search } from '@element-plus/icons-vue'
import {
  handleProp,
} from '@/utils/util'
import SearchForm from '@/components/SearchForm/index.vue'
import Pagination from './components/Pagination.vue'
import ColSetting from './components/ColSetting.vue'
import TableColumn from './components/TableColumn.vue'
import { useSelection } from '@/hooks/useSelection'

const searchForm = ref()

interface ProTableProps extends Partial<Omit<TableProps<any>, 'data'>> {
  columns: ColumnProps[]
  requestApi: (params: any) => Promise<any>
  requestAuto?: boolean
  dataCallback?: (data: any) => any
  title?: string
  pagination?: boolean
  initParam?: any
  border?: boolean
  toolButton?: boolean
  rowKey?: string
  searchCol?: number | Record<BreakPoint, number>
}

const props = withDefaults(defineProps<ProTableProps>(), {
  requestAuto: true,
  columns: () => [],
  pagination: true,
  initParam: () => ({}),
  border: true,
  toolButton: true,
  rowKey: 'id',
  searchCol: () => ({ xs: 1, sm: 2, md: 2, lg: 3, xl: 4 })
})

const isShowSearch = ref(true)
const tableRef = ref<InstanceType<typeof ElTable>>()

const { selectionChange, selectedList, selectedListIds, isSelected } =
  useSelection(props.rowKey)

const {
  tableData,
  pageable,
  searchParam,
  searchInitParam,
  getTableList,
  search,
  reset,
  handleSizeChange,
  handleCurrentChange
} = useTable(
  props.requestApi,
  props.initParam,
  props.pagination,
  props.dataCallback
)

const clearSelection = () => tableRef.value?.clearSelection()

onMounted(() => props.requestAuto && getTableList())

watch(() => props.initParam, getTableList, { deep: true })

const tableColumns = ref<ColumnProps[]>(props.columns)

const enumMap = ref(new Map<string, { [key: string]: any }[]>())
provide('enumMap', enumMap)
const setEnumMap = async (col: ColumnProps) => {
  if (!col.enum) return
  if (typeof col.enum !== 'function') {
    enumMap.value.set(col.prop!, col.enum!)
    return
  }
  const { data } = await col.enum()
  enumMap.value.set(col.prop!, data)
}

const flatColumnsFunc = (
  columns: ColumnProps[],
  flatArr: ColumnProps[] = []
) => {
  columns.forEach(async col => {
    if (col._children?.length) flatArr.push(...flatColumnsFunc(col._children))
    flatArr.push(col)

    col.isShow = col.isShow ?? true
    col.isFilterEnum = col.isFilterEnum ?? true

    setEnumMap(col)
  })
  return flatArr.filter(item => !item._children?.length)
}

const flatColumns = ref<ColumnProps[]>()
flatColumns.value = flatColumnsFunc(tableColumns.value)
const searchColumns = flatColumns.value.filter(item => item.search?.el)

searchColumns.forEach((column, index) => {
  column.search!.order = column.search!.order ?? index + 2
  if (
    column.search?.defaultValue !== undefined &&
    column.search?.defaultValue !== null
  ) {
    searchInitParam.value[column.search.key ?? handleProp(column.prop!)] =
      column.search?.defaultValue
    searchParam.value[column.search.key ?? handleProp(column.prop!)] =
      column.search?.defaultValue
  }
})

searchColumns.sort((a, b) => a.search!.order! - b.search!.order!)

const colRef = ref()
const colSetting = tableColumns.value.filter(
  item =>
    !['selection', 'index', 'expand'].includes(item.type!) &&
    item.prop !== 'operation'
)
const openColSetting = () => colRef.value.openColSetting()

defineExpose({
  element: tableRef,
  tableData,
  searchParam,
  pageable,
  getTableList,
  reset,
  clearSelection,
  enumMap,
  isSelected,
  selectedList,
  selectedListIds
})
</script>

<style lang="scss" scoped>
.table-main {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.table-toolbar__primary,
.table-toolbar__secondary {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
}

.table-pagination :deep(.el-button + .el-button),
.table-toolbar :deep(.el-button + .el-button) {
  margin-left: 0;
}
</style>
