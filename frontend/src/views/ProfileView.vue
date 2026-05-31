<template>
  <div>
    <el-card header="个人资料">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ auth.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ auth.username }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="auth.isAdmin ? 'danger' : 'primary'" size="small">{{ auth.role }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">正常</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
const auth = useAuthStore()
</script>
