<template>
  <div>
    <el-card header="我买到的订单">
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="goodsId" label="商品ID" width="80" />
        <el-table-column prop="amount" label="金额" width="90" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }"><el-tag :type="statusTag(row.status)">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button size="small" v-if="row.status === 'CREATED'" type="primary" @click="doPay(row.id)">支付</el-button>
            <el-button size="small" v-if="row.status === 'CREATED'" @click="doCancel(row.id)">取消</el-button>
            <el-button size="small" v-if="row.status === 'PAID'" type="success" @click="doFinish(row.id)">确认完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listMyBuyOrders, mockPay, cancelOrder, finishOrder } from '../api/market'

const list = ref([])
function statusTag(s) { return { CREATED: 'warning', PAID: 'primary', CANCELED: 'info', FINISHED: 'success' }[s] || 'info' }

async function load() { list.value = await listMyBuyOrders() }
async function doPay(id) { await mockPay({ orderId: id }); ElMessage.success('已支付'); await load() }
async function doCancel(id) { await cancelOrder(id); ElMessage.success('已取消'); await load() }
async function doFinish(id) { await finishOrder(id); ElMessage.success('已完成'); await load() }

onMounted(load)
</script>
