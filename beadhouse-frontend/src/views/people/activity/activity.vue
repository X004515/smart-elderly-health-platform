<template>
  <div class="table-box">
    <ProTable
      ref="proTable"
      title="活动管理"
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
        <el-popconfirm title="确认删除该活动吗？" @confirm="deleteData(scope.row)" confirm-button-type="danger">
          <template #reference>
            <el-button size="small" link :icon="Delete">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </ProTable>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="920px" destroy-on-close>
      <el-form :model="formData" label-width="100px">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <el-form-item label="活动分类">
            <el-select v-model="formData.typeId" class="w-full" placeholder="请选择活动分类">
              <el-option v-for="item in activeTypeList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="活动主题">
            <el-input v-model="formData.theme" />
          </el-form-item>
          <el-form-item label="活动名称">
            <el-input v-model="formData.name" />
          </el-form-item>
          <el-form-item label="活动日期">
            <el-date-picker v-model="formData.activeDate" class="w-full" type="date" value-format="YYYY-MM-DD" />
          </el-form-item>
          <el-form-item label="活动地点">
            <el-input v-model="formData.address" />
          </el-form-item>
          <el-form-item label="组织者">
            <el-input v-model="formData.organizer" />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="formData.phone" />
          </el-form-item>
          <el-form-item label="活动图片">
            <el-input v-model="formData.activePicture" placeholder="请输入图片 URL" />
          </el-form-item>
          <el-form-item label="活动内容" class="md:col-span-2">
            <el-input v-model="formData.content" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="参与老人" class="md:col-span-2">
            <el-select
              v-model="formData.elderIdList"
              class="w-full"
              multiple
              filterable
              collapse-tags
              collapse-tags-tooltip
              placeholder="请选择参与老人"
            >
              <el-option
                v-for="item in elderList"
                :key="item.id"
                :label="`${item.name}(${item.phone || '-'})`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </div>
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
  addActive,
  deleteActive,
  editActive,
  getActiveById,
  getActiveType,
  pageActiveByKey,
  pageSearchElderByKey
} from '@/apis/active'

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
  return pageActiveByKey(params)
}

const activeTypeList = ref<any[]>([])
const elderList = ref<any[]>([])

const dialogVisible = ref(false)
const dialogTitle = ref('新增')
const formData = ref<any>({
  id: '',
  typeId: '',
  theme: '',
  name: '',
  content: '',
  address: '',
  organizer: '',
  phone: '',
  activeDate: '',
  activePicture: '',
  elderIdList: []
})

const loadEnum = async () => {
  const [typeRes, elderRes] = await Promise.all([
    getActiveType(),
    pageSearchElderByKey({ pageNum: 1, pageSize: 500 })
  ])
  activeTypeList.value = (typeRes as any)?.data || []
  elderList.value = (elderRes as any)?.data?.list || []
}

const openDialog = async (title: string, row: any = {}) => {
  dialogTitle.value = title
  await loadEnum()
  if (title === '编辑') {
    const res: any = await getActiveById({ activeId: row.id })
    const detail = res?.data || {}
    formData.value = {
      ...detail,
      elderIdList: (detail.participateElderVoList || []).map((item: any) => item.id)
    }
  } else {
    formData.value = {
      id: '',
      typeId: '',
      theme: '',
      name: '',
      content: '',
      address: '',
      organizer: '',
      phone: '',
      activeDate: '',
      activePicture: '',
      elderIdList: []
    }
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  const requiredFieldList = ['typeId', 'theme', 'name', 'content', 'address', 'organizer', 'phone', 'activeDate', 'activePicture']
  const invalidField = requiredFieldList.some((key: string) => !formData.value[key])
  if (invalidField) {
    ElMessage.warning('请完善活动基础信息')
    return
  }
  if (!formData.value.elderIdList?.length) {
    ElMessage.warning('请至少选择一位参与老人')
    return
  }
  const req = dialogTitle.value === '新增' ? addActive : editActive
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
  const res: any = await deleteActive({ activeId: row.id })
  if (res.code === 200) {
    ElMessage.success(res.msg || '删除成功')
    proTable.value.getTableList()
  } else {
    ElMessage.error(res.msg || '删除失败')
  }
}

const columns: ColumnProps<any>[] = [
  { prop: 'rank', label: '序号', width: 55 },
  { prop: 'name', label: '活动名称', search: { el: 'input' } },
  { prop: 'theme', label: '活动主题' },
  {
    enum: getActiveType,
    prop: 'typeName',
    label: '活动分类',
    search: { el: 'select', key: 'typeName' },
    isFilterEnum: false,
    fieldNames: { label: 'name', value: 'id' }
  },
  {
    prop: 'activeDate',
    label: '活动日期',
    search: {
      el: 'date-picker',
      key: 'activeDateRange',
      props: {
        type: 'daterange',
        valueFormat: 'YYYY-MM-DD',
        rangeSeparator: '至',
        startPlaceholder: '开始日期',
        endPlaceholder: '结束日期'
      }
    }
  },
  { prop: 'address', label: '地点' },
  { prop: 'organizer', label: '组织者' },
  { prop: 'phone', label: '联系电话' },
  { prop: 'operation', label: '操作', width: 170, fixed: 'right' }
]
</script>
