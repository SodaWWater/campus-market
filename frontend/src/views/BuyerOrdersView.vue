<template>
  <div>
    <el-card header="我买到的订单">
      <el-table :data="list" v-if="list.length > 0">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="goodsId" label="商品ID" width="80" />
        <el-table-column prop="amount" label="金额" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <el-button size="small" v-if="row.status === 'CREATED'" type="primary" @click="doPay(row.id)">支付</el-button>
            <el-button size="small" v-if="row.status === 'CREATED'" @click="doCancel(row.id)">取消</el-button>
            <el-button size="small" v-if="row.status === 'PAID'" type="success" @click="doFinish(row.id)">确认完成</el-button>
            <el-button size="small" v-if="row.status === 'FINISHED'" type="warning" @click="doReview(row.id)">评价</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无订单" />
    </el-card>

    <el-dialog v-model="reviewVisible" title="评价交易" width="400px">
      <el-form label-position="top">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="reviewForm.content" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listMyBuyOrders, mockPay, cancelOrder, finishOrder, createReview } from '../api/market'

const list = ref([])
const reviewVisible = ref(false)
const reviewForm = reactive({ orderId: null, rating: 5, content: '' })

function statusTag(s) { return { CREATED: 'warning', PAID: 'primary', CANCELED: 'info', FINISHED: 'success' }[s] || 'info' }
function statusLabel(s) { return { CREATED: '待支付', PAID: '已支付', CANCELED: '已取消', FINISHED: '已完成' }[s] || s }

async function load() { list.value = await listMyBuyOrders() }
async function doPay(id) { await mockPay({ orderId: id }); ElMessage.success('已支付'); await load() }
async function doCancel(id) { await cancelOrder(id); ElMessage.success('已取消'); await load() }
async function doFinish(id) { await finishOrder(id); ElMessage.success('已完成'); await load() }
function doReview(orderId) { reviewForm.orderId = orderId; reviewForm.rating = 5; reviewForm.content = ''; reviewVisible.value = true }
async function submitReview() {
  await createReview({ orderId: reviewForm.orderId, rating: reviewForm.rating, content: reviewForm.content })
  ElMessage.success('评价成功')
  reviewVisible.value = false
  await load()
}
onMounted(load)
</script>
