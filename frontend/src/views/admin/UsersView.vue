<template>
  <div>
    <h3 style="margin:0 0 20px">用户管理</h3>
    <el-card>
      <el-table :data="list" v-if="list.length > 0">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="role" label="角色" width="90">
          <template #default="{ row }"><el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">{{ row.role }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }"><el-tag :type="row.status === 'ENABLE' ? 'success' : 'danger'">{{ row.status === 'ENABLE' ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" type="warning" v-if="row.status === 'ENABLE' && row.role !== 'ADMIN'" @click="disable(row.id)">禁用</el-button>
            <el-button size="small" type="success" v-if="row.status === 'DISABLED'" @click="enable(row.id)">启用</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无用户数据" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminUsers, disableUser, enableUser } from '../../api/market'
const list = ref([])
async function load() { list.value = await adminUsers() }
async function disable(id) { await disableUser(id); ElMessage.success('已禁用'); await load() }
async function enable(id) { await enableUser(id); ElMessage.success('已启用'); await load() }
onMounted(load)
</script>
