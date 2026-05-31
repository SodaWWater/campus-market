<template>
  <div>
    <el-card header="我的收藏">
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="goodsId" label="商品ID" width="120" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/goods/${row.goodsId}`)">查看详情</el-button>
            <el-button size="small" type="danger" @click="unfavorite(row.goodsId)">取消收藏</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listMyFavorites, unfavoriteGoods } from '../api/market'

const list = ref([])
async function unfavorite(id) { await unfavoriteGoods(id); ElMessage.success('已取消'); await load() }
async function load() { list.value = await listMyFavorites() }

onMounted(load)
</script>
