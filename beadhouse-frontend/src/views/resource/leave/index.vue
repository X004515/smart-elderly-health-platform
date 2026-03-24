<template>
  <div class="table-box">
    <ProTable
      ref="proTable"
      title="出库管理"
      :columns="columns"
      :requestApi="getTableList"
      :initParam="initParam"
      :dataCallback="dataCallback"
    >
      <template #tableHeader>
        <el-button type="primary" :icon="Plus" plain @click="openDialog">新增出库</el-button>
      </template>

      <template #outboundFlag="scope">
        <el-tag :type="scope.row.outboundFlag === '待审核' ? 'warning' : scope.row.outboundFlag === '通过' ? 'success' : 'danger'">
          {{ scope.row.outboundFlag }}
        </el-tag>
      </template>

      <template #operation="scope">
        <el-popconfirm
          v-if="scope.row.outboundFlag === '待审核'"
          title="确认审核通过吗？"
          @confirm="auditData(scope.row, '通过')"
        >
          <template #reference>
            <el-button size="small" link type="success">通过</el-button>
          </template>
        </el-popconfirm>
        <el-popconfirm
          v-if="scope.row.outboundFlag === '待审核'"
          title="确认审核不通过吗？"
          @confirm="auditData(scope.row, '不通过')"
        >
          <template #reference>
            <el-button size="small" link type="warning">驳回</el-button>
          </template>
        </el-popconfirm>
        <el-popconfirm title="确认删除该记录吗？" @confirm="deleteData(scope.row)" confirm-button-type="danger">
          <template #reference>
            <el-button size="small" link :icon="Delete">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </ProTable>

    <el-dialog v-model="dialogVisible" title="新增出库记录" width="940px" destroy-on-close>
      <el-form :model="formData" label-width="100px">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <el-form-item label="领用人类型">
            <el-select v-model="formData.recipientType" class="w-full" placeholder="请选择类型">
              <el-option label="老人" value="老人" />
              <el-option label="员工" value="员工" />
            </el-select>
          </el-form-item>
          <el-form-item label="领用人">
            <el-select v-model="formData.recipientId" class="w-full" filterable placeholder="请选择领用人">
              <el-option
                v-for="item in recipientOptions"
                :key="item.id"
                :label="`${item.name}(${item.phone || '-'})`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="仓库">
            <el-select v-model="formData.warehouseId" class="w-full" placeholder="请选择仓库">
              <el-option v-for="item in warehouseList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="登记人">
            <el-select v-model="formData.staffId" class="w-full" placeholder="请选择登记人">
              <el-option v-for="item in staffList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="出库日期">
            <el-date-picker v-model="formData.outboundDate" class="w-full" type="date" value-format="YYYY-MM-DD" />
          </el-form-item>
          <el-form-item label="物资去向">
            <el-input v-model="formData.materialUse" placeholder="例如：活动使用/日常领用" />
          </el-form-item>
        </div>

        <el-form-item label="出库明细">
          <div class="w-full">
            <div class="mb-3">
              <el-button type="primary" plain :icon="Plus" @click="addMaterialRow">添加物资</el-button>
            </div>
            <el-table :data="formData.outboundMaterialQueryList" border>
              <el-table-column label="仓库物资" min-width="280">
                <template #default="scope">
                  <el-select v-model="scope.row.warehouseMaterialId" class="w-full" placeholder="请选择物资">
                    <el-option
                      v-for="item in warehouseMaterialList"
                      :key="item.id"
                      :label="`${item.materialName}(库存:${item.inventory})`"
                      :value="item.id"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="数量" width="140">
                <template #default="scope">
                  <el-input-number v-model="scope.row.outboundNum" :min="1" class="w-full" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template #default="scope">
                  <el-button link type="danger" @click="removeMaterialRow(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Plus } from '@element-plus/icons-vue'
import ProTable from '@/components/ProTable/index.vue'
import { ColumnProps } from '@/components/ProTable/interface'
import {
  addOutboundRecord,
  auditOutboundRecord,
  deleteOutboundRecord,
  listWarehouse,
  listWarehouseStaff,
  pageOutboundRecordByKey,
  pageSearchElderByKey,
  pageSearchStaffByKey,
  pageWarehouseMaterialByKey
} from '@/apis/outboundRecord'

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
  return pageOutboundRecordByKey(params)
}

const dialogVisible = ref(false)
const warehouseList = ref<any[]>([])
const staffList = ref<any[]>([])
const elderList = ref<any[]>([])
const staffRecipientList = ref<any[]>([])
const warehouseMaterialList = ref<any[]>([])

const defaultMaterialRow = () => ({
  warehouseMaterialId: '',
  outboundNum: 1
})

const formData = ref<any>({
  recipientType: '老人',
  recipientId: '',
  warehouseId: '',
  outboundDate: '',
  materialUse: '',
  staffId: '',
  outboundMaterialQueryList: [defaultMaterialRow()]
})

const recipientOptions = computed(() => {
  return formData.value.recipientType === '员工' ? staffRecipientList.value : elderList.value
})

const loadWarehouseMaterial = async () => {
  if (!formData.value.warehouseId) {
    warehouseMaterialList.value = []
    return
  }
  const res: any = await pageWarehouseMaterialByKey({
    pageNum: 1,
    pageSize: 500,
    warehouseId: formData.value.warehouseId
  })
  warehouseMaterialList.value = res?.data?.list || []
}

watch(
  () => formData.value.warehouseId,
  () => {
    formData.value.outboundMaterialQueryList = [defaultMaterialRow()]
    loadWarehouseMaterial()
  }
)

watch(
  () => formData.value.recipientType,
  () => {
    formData.value.recipientId = ''
  }
)

const loadFormEnum = async () => {
  const [warehouseRes, staffRes, elderRes, staffRecipientRes] = await Promise.all([
    listWarehouse(),
    listWarehouseStaff(),
    pageSearchElderByKey({ pageNum: 1, pageSize: 500 }),
    pageSearchStaffByKey({ pageNum: 1, pageSize: 500 })
  ])
  warehouseList.value = (warehouseRes as any)?.data || []
  staffList.value = (staffRes as any)?.data || []
  elderList.value = (elderRes as any)?.data?.list || []
  staffRecipientList.value = (staffRecipientRes as any)?.data?.list || []
}

const openDialog = async () => {
  await loadFormEnum()
  formData.value = {
    recipientType: '老人',
    recipientId: '',
    warehouseId: '',
    outboundDate: '',
    materialUse: '',
    staffId: '',
    outboundMaterialQueryList: [defaultMaterialRow()]
  }
  warehouseMaterialList.value = []
  dialogVisible.value = true
}

const addMaterialRow = () => {
  formData.value.outboundMaterialQueryList.push(defaultMaterialRow())
}

const removeMaterialRow = (index: number) => {
  formData.value.outboundMaterialQueryList.splice(index, 1)
}

const submitForm = async () => {
  if (
    !formData.value.recipientType ||
    !formData.value.recipientId ||
    !formData.value.warehouseId ||
    !formData.value.outboundDate ||
    !formData.value.materialUse ||
    !formData.value.staffId
  ) {
    ElMessage.warning('请完善基础信息')
    return
  }
  if (!formData.value.outboundMaterialQueryList.length) {
    ElMessage.warning('请至少添加一条出库明细')
    return
  }
  const invalidRow = formData.value.outboundMaterialQueryList.some((item: any) => !item.warehouseMaterialId || !item.outboundNum)
  if (invalidRow) {
    ElMessage.warning('请完善出库明细')
    return
  }
  const res: any = await addOutboundRecord(formData.value)
  if (res.code === 200) {
    ElMessage.success(res.msg || '新增成功')
    dialogVisible.value = false
    proTable.value.getTableList()
  } else {
    ElMessage.error(res.msg || '新增失败')
  }
}

const auditData = async (row: any, auditResult: string) => {
  const res: any = await auditOutboundRecord({
    outboundRecordId: row.id,
    auditResult
  })
  if (res.code === 200) {
    ElMessage.success(res.msg || '审核成功')
    proTable.value.getTableList()
  } else {
    ElMessage.error(res.msg || '审核失败')
  }
}

const deleteData = async (row: any) => {
  const res: any = await deleteOutboundRecord({ outboundRecordId: row.id })
  if (res.code === 200) {
    ElMessage.success(res.msg || '删除成功')
    proTable.value.getTableList()
  } else {
    ElMessage.error(res.msg || '删除失败')
  }
}

const columns: ColumnProps<any>[] = [
  { prop: 'rank', label: '序号', width: 55 },
  { prop: 'warehouseName', label: '仓库名称', search: { el: 'input' } },
  { prop: 'materialName', label: '物资名称', search: { el: 'input' } },
  { prop: 'recipient', label: '领用人', search: { el: 'input' } },
  {
    prop: 'outboundDate',
    label: '出库日期',
    search: {
      el: 'date-picker',
      props: {
        type: 'daterange',
        valueFormat: 'YYYY-MM-DD',
        rangeSeparator: '至',
        startPlaceholder: '开始日期',
        endPlaceholder: '结束日期'
      }
    }
  },
  { prop: 'materialUse', label: '去向' },
  { prop: 'staffName', label: '登记人' },
  { prop: 'outboundFlag', label: '状态', width: 120 },
  { prop: 'operation', label: '操作', width: 230, fixed: 'right' }
]
</script>
