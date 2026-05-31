<template>
  <div>
    <el-card header="商品管理">
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="price" label="价格" width="90" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }"><el-tag>{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" type="danger" v-if="row.status !== 'OFF_SHELF' && row.status !== 'SOLD'" @click="off(row.id)">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminGoods, offShelfGoods } from '../../api/market'

const list = ref([])
async function load() { list.value = await adminGoods() }
async function off(id) { await offShelfGoods(id); ElMessage.success('已下架'); await load() }
onMounted(load)
</script>
