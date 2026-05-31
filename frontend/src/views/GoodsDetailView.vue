<template>
  <div class="detail-page" v-if="goods">
    <el-card>
      <div class="detail-header">
        <el-button text @click="$router.back()">← 返回</el-button>
      </div>
      <div class="detail-body">
        <div class="detail-main">
          <div class="detail-cover">{{ goods.title.charAt(0) }}</div>
          <h2>{{ goods.title }}</h2>
          <div class="detail-price">¥{{ goods.price }}</div>
          <div class="detail-tags">
            <el-tag>{{ conditionLabel(goods.conditionLevel) }}</el-tag>
            <el-tag :type="goods.status === 'ON_SALE' ? 'success' : 'warning'" style="margin-left:8px">{{ statusLabel(goods.status) }}</el-tag>
          </div>
          <p class="detail-desc">{{ goods.description || '暂无描述' }}</p>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="交易地点">{{ goods.tradeLocation || '未指定' }}</el-descriptions-item>
            <el-descriptions-item label="卖家ID">{{ goods.sellerId }}</el-descriptions-item>
            <el-descriptions-item label="收藏数">{{ goods.favoriteCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="浏览量">{{ goods.viewCount || 0 }}</el-descriptions-item>
          </el-descriptions>
          <div class="detail-actions" v-if="auth.isLoggedIn && goods.status === 'ON_SALE'">
            <el-button type="primary" size="large" @click="buyNow">立即购买</el-button>
            <el-button size="large" @click="toggleFavorite">{{ favorited ? '❤ 取消收藏' : '🤍 收藏' }}</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { getGoods, createOrder, favoriteGoods, unfavoriteGoods } from '../api/market'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const goods = ref(null)
const favorited = ref(false)

function conditionLabel(v) { return { NEW: '全新', LIKE_NEW: '几乎全新', GOOD: '良好', NORMAL: '一般' }[v] || v }
function statusLabel(v) { return { ON_SALE: '在售', LOCKED: '已锁定', SOLD: '已售', OFF_SHELF: '已下架', PENDING_AUDIT: '待审核', REJECTED: '已驳回' }[v] || v }

async function load() { goods.value = await getGoods(route.params.id) }
async function buyNow() {
  try { await createOrder({ goodsId: Number(route.params.id) }); ElMessage.success('订单已创建'); router.push('/buyer/orders') } catch {}
}
async function toggleFavorite() {
  try {
    if (favorited.value) { await unfavoriteGoods(Number(route.params.id)); favorited.value = false }
    else { await favoriteGoods(Number(route.params.id)); favorited.value = true }
  } catch {}
}
onMounted(load)
</script>

<style scoped>
.detail-header { margin-bottom: 16px; }
.detail-body { display: flex; gap: 24px; }
.detail-main { flex: 1; }
.detail-cover { height: 240px; background: linear-gradient(135deg, #667eea, #764ba2); display: flex; align-items: center; justify-content: center; font-size: 72px; color: #fff; font-weight: bold; border-radius: 12px; margin-bottom: 20px; }
.detail-price { font-size: 28px; color: #f56c6c; font-weight: bold; margin: 12px 0; }
.detail-tags { margin-bottom: 12px; }
.detail-desc { margin: 16px 0; color: #606266; line-height: 1.8; font-size: 15px; }
.detail-actions { margin-top: 24px; display: flex; gap: 12px; }
</style>
