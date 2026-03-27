<template>
  <div class="table-box">
    <ProTable
      ref="proTable"
      title="租户管理"
      :columns="columns"
      :requestApi="getTableList"
      :initParam="initParam"
      :dataCallback="dataCallback"
    >
      <template #tableHeader>
        <el-button @click="openDialog('新增')">
          <IconPark :icon="Plus" class="mr-1"></IconPark>
          <span>新增租户</span>
        </el-button>
      </template>

      <template #activeFlag="scope">
        <el-tag :type="scope.row.activeFlag === 'Y' ? 'success' : 'danger'">
          {{ scope.row.activeFlag === 'Y' ? '启用' : '停用' }}
        </el-tag>
      </template>

      <template #operation="scope">
        <el-button size="small" link :icon="View" @click="openDialog('查看', scope)">查看</el-button>
        <el-button size="small" link :icon="EditPen" @click="openDialog('编辑', scope)">编辑</el-button>
        <el-popconfirm
          :title="scope.row.activeFlag === 'Y' ? '确认停用该租户？' : '确认启用该租户？'"
          @confirm="changeStatus(scope.row)"
          confirm-button-type="danger"
        >
          <template #reference>
            <el-button size="small" link>
              {{ scope.row.activeFlag === 'Y' ? '停用' : '启用' }}
            </el-button>
          </template>
        </el-popconfirm>
        <el-popconfirm title="确认重置租户管理员密码为默认值？" @confirm="resetPass(scope.row)" confirm-button-type="danger">
          <template #reference>
            <el-button size="small" link>重置密码</el-button>
          </template>
        </el-popconfirm>
      </template>
    </ProTable>
    <TenantDialog ref="dialogRef" />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@icon-park/vue-next'
import { EditPen, View } from '@element-plus/icons-vue'
import ProTable from '@/components/ProTable/index.vue'
import { ColumnProps } from '@/components/ProTable/interface'
import TenantDialog from './tenantDialog/index.vue'
import { addTenant, editTenant, editTenantStatus, pageTenantByKey, resetTenantAdminPass } from '@/apis/tenant'

const proTable = ref()
const dialogRef = ref()
const initParam = reactive({})

const dataCallback = (data: any) => ({
  list: data.list,
  total: data.total,
  pageNum: data.pageNum,
  pageSize: data.pageSize
})

const getTableList = (params: any) => {
  const newParams = JSON.parse(JSON.stringify(params))
  return pageTenantByKey(newParams)
}

const openDialog = (title: string, rowData: any = {}) => {
  dialogRef.value.acceptParams({
    title,
    rowData: { ...rowData.row },
    isView: title === '查看',
    api: title === '新增' ? addTenant : title === '编辑' ? editTenant : '',
    getTableList: proTable.value.getTableList
  })
}

const changeStatus = async (row: any) => {
  const nextFlag = row.activeFlag === 'Y' ? 'N' : 'Y'
  const res: any = await editTenantStatus({
    tenantId: row.id,
    activeFlag: nextFlag
  })
  if (res.code === 200) {
    ElMessage.success(res.msg)
    proTable.value.getTableList()
  } else {
    ElMessage.error(res.msg)
  }
}

const resetPass = async (row: any) => {
  const res: any = await resetTenantAdminPass({
    tenantId: row.id
  })
  if (res.code === 200) {
    ElMessage.success(res.msg)
  } else {
    ElMessage.error(res.msg)
  }
}

const columns: ColumnProps<any>[] = [
  { prop: 'rank', label: '序号', width: 55 },
  { prop: 'tenantCode', label: '租户编码', search: { el: 'input' } },
  { prop: 'tenantName', label: '租户名称', search: { el: 'input' } },
  { prop: 'contactName', label: '联系人' },
  { prop: 'contactPhone', label: '联系电话' },
  { prop: 'activeFlag', label: '状态' },
  { prop: 'operation', label: '操作', width: 280 }
]
</script>

<style scoped></style>
