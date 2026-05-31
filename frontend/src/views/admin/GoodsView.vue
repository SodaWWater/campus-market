<template>
  <div>
    <h3 style="margin:0 0 20px">商品管理</h3>
    <el-card>
      <el-table :data="list" v-if="list.length > 0">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip />
        <el-table-column label="卖家" width="110">
          <template #default="{ row }">{{ sellerName(row.sellerId) }}</template>
        </el-table-column>
        <el-table-column label="分类" width="90">
          <template #default="{ row }">{{ categoryName(row.categoryId) }}</template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" v-if="row.status !== 'OFF_SHELF' && row.status !== 'SOLD'" @click="off(row.id)">下架</el-button>
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
import { adminGoods, offShelfGoods, adminUsers, listCategories } from '../../api/market'

const list = ref([])
const userMap = ref({})
const categoryMap = ref({})

function sellerName(id) { return userMap.value[id] || `用户#${id}` }
function categoryName(id) { return categoryMap.value[id] || `分类#${id}` }
function statusTag(s) { return { ON_SALE: 'success', PENDING_AUDIT: 'warning', LOCKED: 'info', SOLD: 'info', REJECTED: 'danger', OFF_SHELF: 'danger' }[s] || 'info' }
function statusLabel(s) { return { ON_SALE: '在售', PENDING_AUDIT: '待审核', LOCKED: '已锁定', SOLD: '已售', REJECTED: '已驳回', OFF_SHELF: '已下架' }[s] || s }

async function load() {
  const [goods, users, cats] = await Promise.all([
    adminGoods(),
    adminUsers().catch(() => []),
    listCategories().catch(() => [])
  ])
  list.value = goods || []
  for (const u of users) userMap.value[u.id] = u.nickname || u.username
  for (const c of cats) categoryMap.value[c.id] = c.name
}
async function off(id) { await offShelfGoods(id); ElMessage.success('已下架'); await load() }
onMounted(load)
</script>
