<template>
  <div>
    <h3 style="margin:0 0 20px">数据看板</h3>
    <div class="dash-grid">
      <el-card v-for="s in stats" :key="s.label" shadow="always" class="stat-card">
        <div class="stat-value">{{ s.value }}</div>
        <div class="stat-label">{{ s.label }}</div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { adminDashboard } from '../../api/market'

const stats = ref([])

onMounted(async () => {
  try {
    const d = await adminDashboard()
    stats.value = [
      { label: '用户总数', value: d.userCount ?? 0 },
      { label: '商品总数', value: d.goodsCount ?? 0 },
      { label: '待审核商品', value: d.pendingGoodsCount ?? 0 },
      { label: '在售商品', value: d.onSaleGoodsCount ?? 0 },
      { label: '订单总数', value: d.orderCount ?? 0 },
      { label: '待支付订单', value: d.createdOrderCount ?? 0 },
      { label: '已支付订单', value: d.paidOrderCount ?? 0 }
    ]
  } catch {}
})
</script>

<style scoped>
.dash-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 16px; }
.stat-card { text-align: center; }
.stat-value { font-size: 36px; font-weight: bold; color: #409eff; }
.stat-label { font-size: 14px; color: #909399; margin-top: 8px; }
</style>
