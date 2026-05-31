<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的商品</span>
          <el-button type="primary" @click="$router.push('/seller/goods/new')">发布新商品</el-button>
        </div>
      </template>
      <el-table :data="list" v-if="list.length > 0">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="price" label="价格" width="100" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }"><el-tag :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="170" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/goods/${row.id}`)">查看</el-button>
            <el-button size="small" type="danger" v-if="row.status !== 'OFF_SHELF' && row.status !== 'SOLD'" @click="offShelf(row.id)">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无商品" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { pageGoods, offShelfGoods as offShelfMyGoods } from '../api/market'

const list = ref([])

function statusTag(s) { return { ON_SALE: 'success', PENDING_AUDIT: 'warning', REJECTED: 'danger', LOCKED: 'info', SOLD: 'info' }[s] || 'info' }
function statusLabel(s) { return { ON_SALE: '在售', PENDING_AUDIT: '待审核', REJECTED: '已驳回', LOCKED: '已锁定', SOLD: '已售', OFF_SHELF: '已下架' }[s] || s }

async function load() {
  const r = await pageGoods({ current: 1, size: 100 })
  list.value = r.records || []
}
async function offShelf(id) {
  try { await offShelfMyGoods(id); ElMessage.success('已下架'); await load() } catch {}
}
onMounted(load)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
