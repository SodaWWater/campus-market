<template>
  <div>
    <el-card header="我的商品">
      <el-button type="primary" @click="$router.push('/seller/goods/new')" style="margin-bottom:16px">发布新商品</el-button>
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="price" label="价格" width="100" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }"><el-tag :type="row.status === 'ON_SALE' ? 'success' : 'warning'">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/goods/${row.id}`)">查看</el-button>
            <el-button size="small" type="danger" v-if="row.status !== 'OFF_SHELF' && row.status !== 'SOLD'" @click="offShelf(row.id)">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { pageGoods, offShelfGoods as offShelfMyGoods } from '../api/market'

const list = ref([])

async function load() {
  const r = await pageGoods({ current: 1, size: 100 })
  list.value = r.records || []
}

async function offShelf(id) {
  try { await offShelfMyGoods(id); ElMessage.success('已下架'); await load() } catch {}
}

onMounted(load)
</script>
