<template>
  <div>
    <el-card header="管理员操作日志">
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="adminId" label="管理员" width="90" />
        <el-table-column prop="module" label="模块" width="100" />
        <el-table-column prop="operation" label="操作" width="100" />
        <el-table-column prop="targetId" label="目标ID" width="90" />
        <el-table-column prop="content" label="内容" />
        <el-table-column prop="createdAt" label="时间" width="170" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { adminOperationLogs } from '../../api/market'
const list = ref([])
onMounted(async () => { list.value = await adminOperationLogs() })
</script>
