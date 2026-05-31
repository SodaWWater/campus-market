<template>
  <div>
    <h3 style="margin:0 0 20px">商品管理</h3>
    <el-card>
      <el-table :data="list" v-if="list.length > 0">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="sellerId" label="卖家" width="80" />
        <el-table-column prop="price" label="价格" width="90" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }"><el-tag :type="statusTag(row.status)">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="100">
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
import { adminGoods, offShelfGoods } from '../../api/market'

const list = ref([])
function statusTag(s) { return { ON_SALE: 'success', PENDING_AUDIT: 'warning', LOCKED: 'info', SOLD: 'info', REJECTED: 'danger' }[s] || 'info' }

async function load() { list.value = await adminGoods() }
async function off(id) { await offShelfGoods(id); ElMessage.success('已下架'); await load() }
onMounted(load)
</script>
