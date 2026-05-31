<template>
  <div>
    <el-card header="订单管理">
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="buyerId" label="买家" width="80" />
        <el-table-column prop="sellerId" label="卖家" width="80" />
        <el-table-column prop="goodsId" label="商品" width="80" />
        <el-table-column prop="amount" label="金额" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag>{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { adminOrders } from '../../api/market'
const list = ref([])
onMounted(async () => { list.value = await adminOrders() })
</script>
