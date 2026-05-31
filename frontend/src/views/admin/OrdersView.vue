<template>
  <div>
    <h3 style="margin:0 0 20px">订单管理</h3>
    <el-card>
      <el-table :data="list" v-if="list.length > 0">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="buyerId" label="买家" width="80" />
        <el-table-column prop="sellerId" label="卖家" width="80" />
        <el-table-column prop="goodsId" label="商品" width="80" />
        <el-table-column prop="amount" label="金额" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
      </el-table>
      <el-empty v-else description="暂无订单" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { adminOrders } from '../../api/market'

const list = ref([])
function statusTag(s) { return { CREATED: 'warning', PAID: 'primary', CANCELED: 'info', FINISHED: 'success' }[s] || 'info' }
function statusLabel(s) { return { CREATED: '待支付', PAID: '已支付', CANCELED: '已取消', FINISHED: '已完成' }[s] || s }

onMounted(async () => { list.value = await adminOrders() })
</script>
