<template>
  <div class="table-box">
    <ProTable
      ref="proTable"
      title="意向客户"
      :columns="columns"
      :requestApi="getTableList"
      :initParam="initParam"
      :dataCallback="dataCallback"
    >
      <template #tableHeader>
        <el-button type="primary" :icon="CirclePlus" plain @click="openDrawer('新增')">新增</el-button>
      </template>
      <template #operation="scope">
        <el-button size="small" link :icon="View" @click="openDrawer('查看', scope.row)">查看</el-button>
        <el-button size="small" link :icon="EditPen" @click="openDrawer('编辑', scope.row)">编辑</el-button>
      </template>
    </ProTable>
    <counselDialong ref="DialongRef" />
  </div>
</template>

<script setup lang="ts" name="useProTable">
import { reactive, ref } from 'vue'
import { ColumnProps } from '@/components/ProTable/interface'
import ProTable from '@/components/ProTable/index.vue'
import counselDialong from './counselDialog/index.vue'
import { CirclePlus, EditPen, View } from '@element-plus/icons-vue'
import { addIntention, editIntention, pageIntentionByKey } from '@/apis/soldManage'

const proTable = ref()
const initParam = reactive({
  type: 1
})

const dataCallback = (data: any) => {
  return {
    list: data.list,
    total: data.total,
    pageNum: data.pageNum,
    pageSize: data.pageSize
  }
}

const DialongRef = ref()
const openDrawer = (title: string, rowData: any = {}) => {
  const params = {
    title,
    rowData: { ...rowData },
    isView: title === '查看',
    api: title === '新增' ? addIntention : title === '编辑' ? editIntention : '',
    getTableList: proTable.value.getTableList
  }
  DialongRef.value.acceptParams(params)
}

const getTableList = (params: any) => {
  return pageIntentionByKey(params)
}

const columns: ColumnProps<any>[] = [
  { prop: 'rank', label: '序号', width: 55 },
  { prop: 'name', label: '老人姓名', search: { el: 'input' } },
  { prop: 'phone', label: '老人联系电话', search: { el: 'input' } },
  { prop: 'sex', label: '性别' },
  { prop: 'age', label: '年龄' },
  { prop: 'address', label: '地址', width: 200 },
  { prop: 'operation', label: '操作', fixed: 'right', width: 150 }
]
</script>
