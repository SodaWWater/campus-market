<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2>注册</h2>
      <el-form label-position="top" @submit.prevent="submit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="输入密码" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="输入昵称" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="输入手机号" />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="submit" style="width:100%">注册并登录</el-button>
      </el-form>
      <p class="muted">已有账号？<router-link to="/login">去登录</router-link></p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { register } from '../api/market'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const form = reactive({ username: '', password: '', nickname: '', phone: '', role: 'USER' })

async function submit() {
  if (!form.username || !form.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  loading.value = true
  try {
    auth.save(await register({ ...form }))
    ElMessage.success('注册成功')
    router.push(auth.isAdmin ? '/admin/dashboard' : '/market')
  } finally { loading.value = false }
}
</script>

<style scoped>
.auth-page { display: flex; justify-content: center; align-items: center; min-height: 100vh; background: #f5f7fa; }
.auth-card { width: 380px; }
.auth-card h2 { text-align: center; margin-bottom: 20px; }
.muted { text-align: center; margin-top: 16px; color: #909399; font-size: 14px; }
</style>
