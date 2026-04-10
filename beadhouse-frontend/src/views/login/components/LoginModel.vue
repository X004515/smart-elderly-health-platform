<template>
  <div class="auth-card">
    <div class="auth-header">
      <p class="auth-kicker">欢迎回来</p>
      <h2>登录系统</h2>
      <p>继续处理入住登记、护理安排、健康记录和日常运营事务。</p>
    </div>

    <el-form
      ref="ruleFormRef"
      :model="formData"
      :rules="loginRules"
      class="auth-form"
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
          placeholder="密码"
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
            <ReImageVerify ref="reImageVerify" v-model:code="imgCode" />
          </template>
        </el-input>
      </el-form-item>
      <el-form-item class="auth-actions">
        <div class="password-set">
          <el-button link @click="emit('forgetPassHandle')">忘记密码</el-button>
        </div>
        <el-button
          class="auth-submit"
          type="primary"
          @click="handleLogin(ruleFormRef)"
          :loading="loading"
        >
          登录
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRenderIcon } from '@/hooks/useIcons'
import { ReImageVerify } from '@/components/ReImageVerify'
import { FormInstance, FormRules, ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import store from '@/store'
import { isNotAccount } from '@/utils/is'

const emit = defineEmits<{
  (e: 'forgetPassHandle'): void
}>()

const router = useRouter()

const imgCode = ref('')
const ruleFormRef = ref<FormInstance | null>(null)
const reImageVerify = ref()
const rememberPWD = ref(false)
const formData = ref({
  tenantCode: 'default',
  phone: '13547584400',
  password: '123456',
  verifyCode: ''
})
const loading = ref(false)

const handleLogin = async (formRef: FormInstance | null) => {
  if (!formRef) return
  loading.value = true
  formRef.validate(valid => {
    if (!valid) {
      loading.value = false
      return
    }

    if (imgCode.value == formData.value.verifyCode) {
      store
        .dispatch('app/actionLogin', {
          tenantCode: formData.value.tenantCode,
          pass: formData.value.password,
          phone: formData.value.phone,
          rememberPWD: rememberPWD.value
        })
        .then(res => {
          if (res && res.code == 200) {
            ElMessage({
              message: '登录成功',
              type: 'success'
            })
            router.push({ name: 'Layout' })
          } else {
            loading.value = false
            ElMessage({
              message: '账号或密码错误',
              type: 'error'
            })
          }
        })
    } else {
      ElMessage({
        message: '验证码错误',
        type: 'error'
      })
      loading.value = false
      reImageVerify.value.getImgCode()
      formData.value.verifyCode = ''
    }
  })
}

const loginRules = reactive<FormRules>({
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
        if (pass === '') {
          callback(new Error('密码不能为空'))
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
