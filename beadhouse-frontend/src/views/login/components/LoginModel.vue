<template>
  <div class="container789">

    <div style="width: 50%; display: flex;background-color: white; border-radius: 10px;">
      <div style="width: 50%; border-radius: 10px;" >
        <img src="@/assets/imgs/fang.png" style="width: 100%; height: 100%">
      </div>

      <div style="flex: 1;width: 100%;padding: 30px;display: flex;flex-direction: column;justify-content: center;" >
        <div style="text-align: center; font-size: 30px; margin-bottom: 30px; color: #ff9666;font-weight: bold">敬老院管理系统</div>
        <el-form
          :model="formData"
          ref="ruleFormRef"
          :rules="loginRules"
          size="large"
        >
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
              <template v-slot:append>
                <ReImageVerify ref="reImageVerify" v-model:code="imgCode" />
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <div class="password-set">
              <el-button
                link
                @click="this.$emit('forgetPassHandle')"
              >
                忘记密码
              </el-button>
            </div>
            <el-button
              class="bg-blue"
              size="default"
              type=""
              style="width: 100%;background-color: #ff9666;color: #FFF;height: 40px"
              @click="handleLogin(ruleFormRef)"
              :loading="loading"
            >登录
            </el-button>
          </el-form-item>
        </el-form>

      </div>
    </div>



  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { useRenderIcon } from '@/hooks/useIcons'
import { ReImageVerify } from '@/components/ReImageVerify'
import { FormInstance, FormRules, ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import store from '@/store'
import { isNotAccount } from '@/utils/is'
import { initRoutes } from '@/router/utils'
const router = useRouter()

const imgCode = ref('')
const ruleFormRef = ref<FormInstance | null>(null)
const reImageVerify = ref()
const rememberPWD = ref(false)
const formData = ref({
  phone: '13547584400',
  password: '123456',
  verifyCode: imgCode
})
const loading = ref(false)

const handleLogin = async (formRef: FormInstance | null) => {
  loading.value = true
  if (!formRef) return
  formRef.validate((valid, fields) => {
    if (valid) {
      //单独校验图形验证码
      if (imgCode.value == formData.value.verifyCode) {
        store
          .dispatch('app/actionLogin', {
            pass: formData.value.password,
            phone: formData.value.phone,
            rememberPWD: rememberPWD.value
          })
          .then(res => {
            if (res && res.code == 200) {
              // initRoutes().then(router => {
              //   router.push({ name: 'Layout' })
              // })
              // await initRoutes()
              ElMessage({
                message: '登陆成功',
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
    } else {
      loading.value = false
      return fields
    }
  })
}

/* 登录校验 */
const loginRules = reactive<FormRules>({
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
::v-deep .el-input-group__append {
  padding: 0;
}


.container789{
  height: 100vh;
  overflow: hidden;
  //background-color: #ff9666;
  background-image: url("@/assets/imgs/jia.png");
  background-size: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

</style>
