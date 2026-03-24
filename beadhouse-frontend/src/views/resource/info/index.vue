<template>
  <div class="table-box">
    <ProTable
      ref="proTable"
      title="物资信息"
      :columns="columns"
      :requestApi="getTableList"
      :initParam="initParam"
      :dataCallback="dataCallback"
    >
      <template #tableHeader>
        <el-button type="primary" :icon="Plus" plain @click="openDialog('新增')">新增</el-button>
      </template>
      <template #operation="scope">
        <el-button size="small" link :icon="EditPen" @click="openDialog('编辑', scope.row)">编辑</el-button>
        <el-popconfirm title="确认删除该物资吗？" @confirm="deleteData(scope.row)" confirm-button-type="danger">
          <template #reference>
            <el-button size="small" link :icon="Delete">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </ProTable>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" destroy-on-close>
      <el-form :model="formData" label-width="100px">
        <el-form-item label="物资分类">
          <el-select v-model="formData.typeId" class="w-full" placeholder="请选择分类">
            <el-option v-for="item in materialTypeList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="物资名称">
          <el-input v-model="formData.name" placeholder="请输入物资名称" />
        </el-form-item>
        <el-form-item label="单价(元)">
          <el-input-number v-model="formData.price" :min="0" :precision="2" class="w-full" />
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
import { Delete, EditPen, Plus } from '@element-plus/icons-vue'
import ProTable from '@/components/ProTable/index.vue'
import { ColumnProps } from '@/components/ProTable/interface'
import {
  addMaterial,
  getMaterialById,
  getMaterialType,
  IOperateMaterial,
  pageMaterialByKey,
  deleteMaterial,
  editMaterial
} from '@/apis/material'

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
  return pageMaterialByKey(params)
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增')
const formData = ref<IOperateMaterial>({
  typeId: '',
  name: '',
  price: 0
})
const materialTypeList = ref<any[]>([])

const loadMaterialType = async () => {
  const res: any = await getMaterialType()
  materialTypeList.value = res?.data || []
}

const openDialog = async (title: string, row: any = {}) => {
  dialogTitle.value = title
  await loadMaterialType()
  if (title === '编辑') {
    const res: any = await getMaterialById({ materialId: row.id })
    formData.value = { ...res.data }
  } else {
    formData.value = {
      typeId: '',
      name: '',
      price: 0
    }
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.value.typeId || !formData.value.name) {
    ElMessage.warning('请完善必填项')
    return
  }
  const req = dialogTitle.value === '新增' ? addMaterial : editMaterial
  const res: any = await req(formData.value)
  if (res.code === 200) {
    ElMessage.success(res.msg || '操作成功')
    dialogVisible.value = false
    proTable.value.getTableList()
  } else {
    ElMessage.error(res.msg || '操作失败')
  }
}

const deleteData = async (row: any) => {
  const res: any = await deleteMaterial({ materialId: row.id })
  if (res.code === 200) {
    ElMessage.success(res.msg || '删除成功')
    proTable.value.getTableList()
  } else {
    ElMessage.error(res.msg || '删除失败')
  }
}

const columns: ColumnProps<any>[] = [
  { prop: 'rank', label: '序号', width: 55 },
  {
    prop: 'name',
    label: '物资名称',
    search: { el: 'input' }
  },
  {
    enum: getMaterialType,
    prop: 'typeName',
    label: '物资分类',
    search: { el: 'select', key: 'materialTypeName' },
    isFilterEnum: false,
    fieldNames: { label: 'name', value: 'id' }
  },
  { prop: 'price', label: '单价(元)' },
  { prop: 'operation', label: '操作', width: 170 }
]
</script>
