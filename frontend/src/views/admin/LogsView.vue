<template>
  <div>
    <h3 style="margin:0 0 20px">操作日志</h3>
    <el-card>
      <el-table :data="list" v-if="list.length > 0">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="adminId" label="管理员" width="90" />
        <el-table-column prop="module" label="模块" width="100" />
        <el-table-column prop="operation" label="操作" width="100" />
        <el-table-column prop="targetId" label="目标ID" width="90" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="时间" width="170" />
      </el-table>
      <el-empty v-else description="暂无操作记录" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { adminOperationLogs } from '../../api/market'
const list = ref([])
onMounted(async () => { list.value = await adminOperationLogs() })
</script>
