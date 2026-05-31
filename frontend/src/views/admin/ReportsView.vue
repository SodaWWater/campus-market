<template>
  <div>
    <h3 style="margin:0 0 20px">举报管理</h3>
    <el-card>
      <el-table :data="list" v-if="list.length > 0">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="reporterId" label="举报人" width="90" />
        <el-table-column prop="targetType" label="类型" width="90" />
        <el-table-column prop="targetId" label="目标ID" width="90" />
        <el-table-column prop="reason" label="原因" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="reportTag(row.status)">{{ statusLabel(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button v-if="row.status==='PENDING'" size="small" type="success" @click="handle(row.id,'RESOLVED')">成立</el-button>
            <el-button v-if="row.status==='PENDING'" size="small" type="danger" @click="handle(row.id,'REJECTED')">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无举报" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminListReports, adminHandleReport } from '../../api/market'

const list = ref([])
function reportTag(s) { return { PENDING: 'warning', RESOLVED: 'success', REJECTED: 'info' }[s] || 'info' }
function statusLabel(s) { return { PENDING: '待处理', PROCESSING: '处理中', RESOLVED: '已解决', REJECTED: '已驳回' }[s] || s }

async function load() { list.value = await adminListReports() }
async function handle(id, status) {
  await adminHandleReport(id, { status, handleResult: status === 'RESOLVED' ? '举报成立' : '举报不成立' })
  ElMessage.success('已处理')
  await load()
}
onMounted(load)
</script>
