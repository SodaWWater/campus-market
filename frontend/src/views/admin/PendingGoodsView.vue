<template>
  <div>
    <el-card header="待审核商品">
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="price" label="价格" width="90" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="approve(row.id)">通过</el-button>
            <el-button size="small" type="danger" @click="showReject(row.id)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" title="驳回原因" width="400px">
      <el-input v-model="rejectReason" placeholder="输入驳回原因" />
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="danger" @click="doReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminPendingGoods, approveGoods, rejectGoods } from '../../api/market'

const list = ref([])
const dialogVisible = ref(false)
const rejectReason = ref('')
let rejectId = null

async function load() { list.value = await adminPendingGoods() }
async function approve(id) { await approveGoods(id); ElMessage.success('已通过'); await load() }
function showReject(id) { rejectId = id; rejectReason.value = ''; dialogVisible.value = true }
async function doReject() {
  await rejectGoods(rejectId, rejectReason.value)
  dialogVisible.value = false
  ElMessage.success('已驳回')
  await load()
}
onMounted(load)
</script>
