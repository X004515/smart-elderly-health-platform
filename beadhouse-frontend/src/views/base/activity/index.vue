<template>
  <div class="table-box">
    <ProTable
      ref="proTable"
      title="活动分类"
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
        <el-popconfirm title="确认删除该分类吗？" @confirm="deleteData(scope.row)" confirm-button-type="danger">
          <template #reference>
            <el-button size="small" link :icon="Delete">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </ProTable>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="460px" destroy-on-close>
      <el-form :model="formData" label-width="90px">
        <el-form-item label="分类名称">
          <el-input v-model="formData.name" placeholder="请输入活动分类名称" />
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
  addActiveType,
  deleteActiveType,
  editActiveType,
  getActiveTypeById,
  pageActiveTypeByKey
} from '@/apis/activeType'

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
  return pageActiveTypeByKey(params)
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增')
const formData = ref<any>({
  id: '',
  name: ''
})

const openDialog = async (title: string, row: any = {}) => {
  dialogTitle.value = title
  if (title === '编辑') {
    const res: any = await getActiveTypeById({ activeTypeId: row.id })
    formData.value = { ...res.data }
  } else {
    formData.value = {
      id: '',
      name: ''
    }
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.value.name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  let res: any
  if (dialogTitle.value === '新增') {
    res = await addActiveType(formData.value.name)
  } else {
    res = await editActiveType(formData.value)
  }
  if (res.code === 200) {
    ElMessage.success(res.msg || '操作成功')
    dialogVisible.value = false
    proTable.value.getTableList()
  } else {
    ElMessage.error(res.msg || '操作失败')
  }
}

const deleteData = async (row: any) => {
  const res: any = await deleteActiveType({ activeTypeId: row.id })
  if (res.code === 200) {
    ElMessage.success(res.msg || '删除成功')
    proTable.value.getTableList()
  } else {
    ElMessage.error(res.msg || '删除失败')
  }
}

const columns: ColumnProps<any>[] = [
  { prop: 'rank', label: '序号', width: 55 },
  { prop: 'name', label: '分类名称', search: { el: 'input' } },
  { prop: 'operation', label: '操作', width: 170 }
]
</script>
