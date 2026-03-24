<template>
  <div class="table-box">
    <ProTable
      ref="proTable"
      title="库存查询"
      :columns="columns"
      :requestApi="getTableList"
      :initParam="initParam"
      :dataCallback="dataCallback"
    />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import ProTable from '@/components/ProTable/index.vue'
import { ColumnProps } from '@/components/ProTable/interface'
import { listWarehouse, pageInventoryByKey } from '@/apis/inventory'

const proTable = ref()
const initParam = reactive({})

const dataCallback = (data: any) => {
  return {
    list: data.list,
    total: data.total,
    pageNum: data.pageNum,
    pageSize: data.pageSize
  }
}

const getTableList = (params: any) => {
  return pageInventoryByKey(params)
}

const columns: ColumnProps<any>[] = [
  { prop: 'rank', label: '序号', width: 55 },
  {
    enum: listWarehouse,
    prop: 'warehouseName',
    label: '仓库',
    search: { el: 'select', key: 'warehouseId' },
    isFilterEnum: false,
    fieldNames: { label: 'name', value: 'id' }
  },
  { prop: 'materialName', label: '物资名称', search: { el: 'input' } },
  { prop: 'total', label: '总库存' },
  { prop: 'warehouseNum', label: '入库数量' },
  { prop: 'outboundNum', label: '出库数量' },
  { prop: 'inventory', label: '当前库存' },
  { prop: 'price', label: '单价(元)' }
]
</script>
