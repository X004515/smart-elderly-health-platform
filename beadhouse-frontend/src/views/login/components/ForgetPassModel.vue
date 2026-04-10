<template>
  <div class="auth-card auth-card--compact">
    <div class="auth-header">
      <p class="auth-kicker">账号找回</p>
      <h2>重置密码</h2>
      <p>通过租户、账号和验证码完成密码重置，然后返回登录页继续使用系统。</p>
    </div>

    <el-form
      :model="formData"
      class="forget-pass-form"
      ref="ruleFormRef"
      :rules="forgetPassRules"
      size="large"
    >
      <el-form-item prop="tenantCode">
        <el-input
          v-model="formData.tenantCode"
          placeholder="租户编码"
          :prefix-icon="useRenderIcon('user', { size: 12 })"
          clearable
        />
      </el-form-item>
      <el-form-item prop="phone">
        <el-input
          v-model="formData.phone"
          placeholder="账号"
          :prefix-icon="useRenderIcon('user', { size: 12 })"
          clearable
        />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="formData.password"
          placeholder="新密码"
          type="password"
          :prefix-icon="useRenderIcon('password', { size: 12 })"
          clearable
          show-password
        />
      </el-form-item>

      <el-form-item prop="confirmPassword">
        <el-input
          v-model="formData.confirmPassword"
          placeholder="确认密码"
          type="password"
          :prefix-icon="useRenderIcon('password', { size: 12 })"
          clearable
          show-password
        />
      </el-form-item>

      <el-form-item prop="verifyCode">
        <el-input
          v-model="formData.verifyCode"
          placeholder="验证码"
          :prefix-icon="useRenderIcon('verify')"
          clearable
        >
          <template #append>
            <el-button @click="sendCodeHandle" :disabled="SEND_code_FLAG">
              {{ SEND_CODE }}
            </el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item class="auth-actions">
        <div class="password-set">
          <el-button link @click="emit('returnLoginHandle')">返回登录</el-button>
        </div>
        <el-button
          class="auth-submit"
          type="primary"
          @click="handleForgetPass(ruleFormRef)"
          :loading="loading"
        >
          重置密码
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRenderIcon } from '@/hooks/useIcons'
import { FormInstance, FormRules, ElMessage } from 'element-plus'
import { isNotAccount } from '@/utils/is'
import { forgetPass, sendCode } from '@/apis/user'

const emit = defineEmits<{
  (e: 'returnLoginHandle'): void
}>()

const ruleFormRef = ref<FormInstance | null>(null)
const formData = ref({
  tenantCode: 'default',
  phone: '13547584400',
  password: '123456',
  confirmPassword: '123456',
  verifyCode: ''
})
let SEND_CODE = ref('发送验证码')
let SEND_code_FLAG = ref(false)
const loading = ref(false)

const sendCodeHandle = async () => {
  const account = formData.value.phone.trim()
  const pass = formData.value.password.trim()
  if (!account || !pass) {
    ElMessage({
      message: '账号和密码不能为空',
      type: 'warning'
    })
    return
  }
  const res: any = await sendCode({
    tenantCode: formData.value.tenantCode,
    account: formData.value.phone,
    pass: formData.value.password
  })
  if (res.code === 200) {
    if (res.data !== null) {
      formData.value.verifyCode = res.data
    }
    SEND_code_FLAG.value = true
    let time = 60
    const timmer = setInterval(() => {
      SEND_CODE.value = '重新发送(' + time-- + ')'
      if (!time) {
        clearInterval(timmer)
        SEND_CODE.value = '发送验证码'
        SEND_code_FLAG.value = false
      }
    }, 1000)
  } else {
    ElMessage({
      message: res.msg,
      type: 'warning'
    })
  }
}

const handleForgetPass = (formRef: FormInstance | null) => {
  if (!formRef) return
  loading.value = true
  formRef.validate(async (valid, fields) => {
    if (valid) {
      const res: any = await forgetPass({
        tenantCode: formData.value.tenantCode,
        code: formData.value.verifyCode,
        pass: formData.value.password,
        account: formData.value.phone
      })
      if (res.code === 200) {
        emit('returnLoginHandle')
        ElMessage({
          message: res.msg,
          type: 'success'
        })
      } else {
        loading.value = false
        ElMessage({
          message: res.msg,
          type: 'warning'
        })
      }
    } else {
      loading.value = false
      return fields
    }
  })
}

const forgetPassRules = reactive<FormRules>({
  tenantCode: [
    {
      validator: (rule, value, callback) => {
        const tenantCode = value?.trim()
        if (!tenantCode) {
          callback(new Error('租户编码不能为空'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [
    {
      validator: (rule, value, callback) => {
        let phone = value?.trim()
        if (phone === '') {
          callback(new Error('账号不能为空'))
        } else if (isNotAccount(phone)) {
          callback(new Error('账号格式有误'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  password: [
    {
      validator: (rule, value, callback) => {
        const pass = value?.trim()
        if (!pass) {
          callback(new Error('新密码不能为空'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        const confirmPass = value?.trim()
        const pass = formData.value.password.trim()
        if (confirmPass === '') {
          callback(new Error('确认密码不能为空'))
        } else if (confirmPass !== pass) {
          callback('与新密码不一致')
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  verifyCode: [
    {
      validator(rule, value, callback) {
        const verifyCode = value?.trim()
        if (verifyCode === '') {
          callback(new Error('验证码不能为空'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})
</script>

<style scoped>
:deep(.el-input-group__append) {
  padding: 0;
}

.auth-card {
  padding: 32px;
  border: 1px solid rgba(132, 163, 151, 0.16);
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 24px 60px rgba(68, 94, 86, 0.12);
  backdrop-filter: blur(18px);
}

.auth-card--compact {
  padding-top: 30px;
}

.auth-header {
  margin-bottom: 26px;
}

.auth-kicker {
  margin: 0 0 10px;
  color: var(--app-primary-strong);
  letter-spacing: 0.14em;
  text-transform: uppercase;
  font-size: 0.82rem;
}

.auth-header h2 {
  margin: 0;
  font-family: var(--font-display);
  font-size: 2rem;
  color: var(--app-text);
}

.auth-header p {
  margin: 10px 0 0;
  line-height: 1.8;
  color: var(--app-text-soft);
}

.auth-actions {
  margin-bottom: 0;
}

.password-set {
  display: flex;
  justify-content: flex-end;
  width: 100%;
  margin-bottom: 10px;
}

.auth-submit {
  width: 100%;
}
</style>
