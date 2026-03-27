<template>
  <el-dialog style="width: 70%" v-model="dialogVisible" :title="dialogProps.title" destroy-on-close>
    <el-form :model="formData" ref="ruleFormRef" :rules="rules" label-width="120px">
      <div class="flex justify-around flex-wrap">
        <div class="w-full md:w-1/2">
          <el-form-item label="租户编码:" prop="tenantCode">
            <el-input v-model="formData.tenantCode" :disabled="dialogProps.title !== '新增'" clearable />
          </el-form-item>
        </div>
        <div class="w-full md:w-1/2">
          <el-form-item label="租户名称:" prop="tenantName">
            <el-input v-model="formData.tenantName" :disabled="dialogProps.isView" clearable />
          </el-form-item>
        </div>

        <div class="w-full md:w-1/2">
          <el-form-item label="联系人:" prop="contactName">
            <el-input v-model="formData.contactName" :disabled="dialogProps.isView" clearable />
          </el-form-item>
        </div>
        <div class="w-full md:w-1/2">
          <el-form-item label="联系电话:" prop="contactPhone">
            <el-input v-model="formData.contactPhone" :disabled="dialogProps.isView" clearable />
          </el-form-item>
        </div>

        <div class="w-full md:w-1/2">
          <el-form-item label="到期时间:" prop="expireTime">
            <el-date-picker
              v-model="formData.expireTime"
              type="datetime"
              value-format="YYYY-MM-DD HH:mm:ss"
              :disabled="dialogProps.isView"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        <div class="w-full md:w-1/2">
          <el-form-item label="状态:" prop="activeFlag">
            <el-radio-group v-model="formData.activeFlag" :disabled="dialogProps.isView">
              <el-radio label="Y">启用</el-radio>
              <el-radio label="N">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>

        <div class="w-full md:w-1/2">
          <el-form-item label="管理员姓名:" prop="adminName">
            <el-input v-model="formData.adminName" :disabled="dialogProps.title !== '新增'" clearable />
          </el-form-item>
        </div>
        <div class="w-full md:w-1/2">
          <el-form-item label="管理员手机:" prop="adminPhone">
            <el-input v-model="formData.adminPhone" :disabled="dialogProps.title !== '新增'" clearable />
          </el-form-item>
        </div>

        <div class="w-full md:w-1/2">
          <el-form-item label="管理员邮箱:" prop="adminEmail">
            <el-input v-model="formData.adminEmail" :disabled="dialogProps.title !== '新增'" clearable />
          </el-form-item>
        </div>
        <div class="w-full md:w-1/2">
          <el-form-item label="备注:" prop="remark">
            <el-input v-model="formData.remark" :disabled="dialogProps.isView" clearable />
          </el-form-item>
        </div>
      </div>
    </el-form>

    <template v-if="!dialogProps.isView" #footer>
      <span>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { getTenantById } from '@/apis/tenant'

const ruleFormRef = ref<FormInstance | null>(null)
const dialogVisible = ref(false)

interface DialogProps {
  title: string
  isView: boolean
  rowData?: any
  api?: (params: any) => Promise<any>
  getTableList?: () => Promise<any>
}

const dialogProps = ref<DialogProps>({
  isView: false,
  title: ''
})

const defaultFormData = () => ({
  id: '',
  tenantCode: '',
  tenantName: '',
  activeFlag: 'Y',
  expireTime: '',
  contactName: '',
  contactPhone: '',
  remark: '',
  adminName: '',
  adminPhone: '',
  adminEmail: ''
})

const formData = ref<any>(defaultFormData())

watch(dialogVisible, value => {
  if (!value) {
    formData.value = defaultFormData()
  }
})

const rules = reactive<FormRules>({
  tenantCode: [{ required: true, message: '租户编码不能为空', trigger: 'blur' }],
  tenantName: [{ required: true, message: '租户名称不能为空', trigger: 'blur' }],
  adminName: [
    {
      validator: (rule, value, callback) => {
        if (dialogProps.value.title === '新增' && !value) {
          callback(new Error('管理员姓名不能为空'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  adminPhone: [
    {
      validator: (rule, value, callback) => {
        if (dialogProps.value.title === '新增' && !value) {
          callback(new Error('管理员手机号不能为空'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
})

const acceptParams = async (params: DialogProps) => {
  dialogProps.value = params
  if (params.title !== '新增') {
    const res: any = await getTenantById({
      tenantId: params.rowData.id
    })
    formData.value = {
      ...defaultFormData(),
      ...res.data
    }
  }
  dialogVisible.value = true
}

const handleSubmit = () => {
  ruleFormRef.value!.validate(async valid => {
    if (!valid) return
    const submitData = { ...formData.value }
    if (dialogProps.value.title === '编辑') {
      delete submitData.adminName
      delete submitData.adminPhone
      delete submitData.adminEmail
    }
    const res: any = await dialogProps.value.api!(submitData)
    if (res.code === 200) {
      ElMessage.success(res.msg)
      dialogProps.value.getTableList!()
      dialogVisible.value = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

defineExpose({
  acceptParams
})
</script>

<style scoped></style>
