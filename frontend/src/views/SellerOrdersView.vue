<template>
  <div>
    <el-card header="我卖出的订单">
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="goodsId" label="商品ID" width="80" />
        <el-table-column prop="amount" label="金额" width="90" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }"><el-tag :type="statusTag(row.status)">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { listMySellOrders } from '../api/market'

const list = ref([])
function statusTag(s) { return { CREATED: 'warning', PAID: 'primary', CANCELED: 'info', FINISHED: 'success' }[s] || 'info' }

onMounted(async () => { list.value = await listMySellOrders() })
</script>
