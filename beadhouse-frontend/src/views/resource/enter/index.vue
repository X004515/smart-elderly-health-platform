<template>
  <div class="table-box">
    <ProTable
      ref="proTable"
      title="入库管理"
      :columns="columns"
      :requestApi="getTableList"
      :initParam="initParam"
      :dataCallback="dataCallback"
    >
      <template #tableHeader>
        <el-button type="primary" :icon="Plus" plain @click="openDialog">新增入库</el-button>
      </template>

      <template #warehouseFlag="scope">
        <el-tag :type="scope.row.warehouseFlag === '待审核' ? 'warning' : scope.row.warehouseFlag === '通过' ? 'success' : 'danger'">
          {{ scope.row.warehouseFlag }}
        </el-tag>
      </template>

      <template #operation="scope">
        <el-popconfirm
          v-if="scope.row.warehouseFlag === '待审核'"
          title="确认审核通过吗？"
          @confirm="auditData(scope.row, '通过')"
        >
          <template #reference>
            <el-button size="small" link type="success">通过</el-button>
          </template>
        </el-popconfirm>
        <el-popconfirm
          v-if="scope.row.warehouseFlag === '待审核'"
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

    <el-dialog v-model="dialogVisible" title="新增入库记录" width="920px" destroy-on-close>
      <el-form :model="formData" label-width="100px">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <el-form-item label="仓库">
            <el-select v-model="formData.warehouseId" class="w-full" placeholder="请选择仓库">
              <el-option v-for="item in warehouseList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="经办人">
            <el-select v-model="formData.staffId" class="w-full" placeholder="请选择经办人">
              <el-option v-for="item in staffList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="物资来源">
            <el-input v-model="formData.source" placeholder="例如：采购/捐赠" />
          </el-form-item>
          <el-form-item label="入库日期">
            <el-date-picker v-model="formData.warehouseDate" class="w-full" type="date" value-format="YYYY-MM-DD" />
          </el-form-item>
        </div>

        <el-form-item label="入库明细">
          <div class="w-full">
            <div class="mb-3">
              <el-button type="primary" plain :icon="Plus" @click="addMaterialRow">添加物资</el-button>
            </div>
            <el-table :data="formData.warehouseMaterialQueryList" border>
              <el-table-column label="物资" min-width="220">
                <template #default="scope">
                  <el-select v-model="scope.row.materialId" class="w-full" placeholder="请选择物资">
                    <el-option v-for="item in materialList" :key="item.id" :label="item.name" :value="item.id" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="数量" width="120">
                <template #default="scope">
                  <el-input-number v-model="scope.row.warehouseNum" :min="1" class="w-full" />
                </template>
              </el-table-column>
              <el-table-column label="生产日期" min-width="170">
                <template #default="scope">
                  <el-date-picker v-model="scope.row.productDate" type="date" value-format="YYYY-MM-DD" class="w-full" />
                </template>
              </el-table-column>
              <el-table-column label="有效期" min-width="170">
                <template #default="scope">
                  <el-date-picker v-model="scope.row.expireDate" type="date" value-format="YYYY-MM-DD" class="w-full" />
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
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Plus } from '@element-plus/icons-vue'
import ProTable from '@/components/ProTable/index.vue'
import { ColumnProps } from '@/components/ProTable/interface'
import {
  addWarehouseRecord,
  auditWarehouseRecord,
  deleteWarehouseRecord,
  listWarehouse,
  listWarehouseStaff,
  pageMaterialByKey,
  pageWarehouseRecordByKey
} from '@/apis/warehouseRecord'

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
  return pageWarehouseRecordByKey(params)
}

const warehouseList = ref<any[]>([])
const staffList = ref<any[]>([])
const materialList = ref<any[]>([])

const defaultMaterialRow = () => ({
  materialId: '',
  warehouseNum: 1,
  productDate: '',
  expireDate: ''
})

const dialogVisible = ref(false)
const formData = ref<any>({
  warehouseId: '',
  staffId: '',
  source: '',
  warehouseDate: '',
  warehouseMaterialQueryList: [defaultMaterialRow()]
})

const loadFormEnum = async () => {
  const [warehouseRes, staffRes, materialRes] = await Promise.all([
    listWarehouse(),
    listWarehouseStaff(),
    pageMaterialByKey({ pageNum: 1, pageSize: 500 })
  ])
  warehouseList.value = (warehouseRes as any)?.data || []
  staffList.value = (staffRes as any)?.data || []
  materialList.value = (materialRes as any)?.data?.list || []
}

const openDialog = async () => {
  await loadFormEnum()
  formData.value = {
    warehouseId: '',
    staffId: '',
    source: '',
    warehouseDate: '',
    warehouseMaterialQueryList: [defaultMaterialRow()]
  }
  dialogVisible.value = true
}

const addMaterialRow = () => {
  formData.value.warehouseMaterialQueryList.push(defaultMaterialRow())
}

const removeMaterialRow = (index: number) => {
  formData.value.warehouseMaterialQueryList.splice(index, 1)
}

const submitForm = async () => {
  if (!formData.value.warehouseId || !formData.value.staffId || !formData.value.source || !formData.value.warehouseDate) {
    ElMessage.warning('请完善基础信息')
    return
  }
  if (!formData.value.warehouseMaterialQueryList.length) {
    ElMessage.warning('请至少添加一条入库物资')
    return
  }
  const invalidRow = formData.value.warehouseMaterialQueryList.some(
    (item: any) => !item.materialId || !item.warehouseNum || !item.productDate || !item.expireDate
  )
  if (invalidRow) {
    ElMessage.warning('请完善入库明细')
    return
  }
  const res: any = await addWarehouseRecord(formData.value)
  if (res.code === 200) {
    ElMessage.success(res.msg || '新增成功')
    dialogVisible.value = false
    proTable.value.getTableList()
  } else {
    ElMessage.error(res.msg || '新增失败')
  }
}

const auditData = async (row: any, auditResult: string) => {
  const res: any = await auditWarehouseRecord({
    warehouseRecordId: row.id,
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
  const res: any = await deleteWarehouseRecord({ warehouseRecordId: row.id })
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
  {
    prop: 'warehouseDate',
    label: '入库日期',
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
  { prop: 'source', label: '来源' },
  { prop: 'staffName', label: '经办人', search: { el: 'input' } },
  { prop: 'warehouseFlag', label: '状态', width: 120 },
  { prop: 'operation', label: '操作', width: 230, fixed: 'right' }
]
</script>
