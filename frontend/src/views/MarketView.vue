<template>
  <div class="market-page">
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item><el-input v-model="query.keyword" placeholder="搜索商品" clearable /></el-form-item>
        <el-form-item>
          <el-select v-model="query.categoryId" clearable placeholder="全部分类">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="search">搜索</el-button></el-form-item>
      </el-form>
    </el-card>

    <div class="goods-grid">
      <el-card v-for="item in goods" :key="item.id" class="goods-card" shadow="hover" @click="$router.push(`/goods/${item.id}`)">
        <div class="goods-cover">{{ item.title.charAt(0) }}</div>
        <h3>{{ item.title }}</h3>
        <div class="goods-price">¥{{ item.price }}</div>
        <div class="goods-meta">
          <el-tag size="small">{{ item.conditionLevel }}</el-tag>
          <span>{{ item.tradeLocation || '' }}</span>
        </div>
        <div class="goods-stats">
          <span>❤ {{ item.favoriteCount || 0 }}</span>
          <span>👁 {{ item.viewCount || 0 }}</span>
          <el-tag size="small" :type="item.status === 'ON_SALE' ? 'success' : 'info'">{{ item.status }}</el-tag>
        </div>
      </el-card>
    </div>

    <el-pagination v-if="total > 0" :total="total" :page-size="10" :current-page="query.page" layout="prev, pager, next" @current-change="page => { query.page = page; search() }" />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { listCategories, pageGoods } from '../api/market'

const categories = ref([])
const goods = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, keyword: '', categoryId: null })

async function search() {
  const r = await pageGoods({ current: query.page, size: query.size, keyword: query.keyword || undefined, categoryId: query.categoryId || undefined })
  goods.value = r.records || []
  total.value = r.total || 0
}

onMounted(async () => {
  try { categories.value = await listCategories() } catch {}
  await search()
})
</script>

<style scoped>
.search-card { margin-bottom: 20px; }
.goods-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; }
.goods-card { cursor: pointer; }
.goods-cover { height: 120px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center; font-size: 40px; color: #fff; font-weight: bold; border-radius: 6px; margin-bottom: 12px; }
.goods-card h3 { margin: 0 0 8px; font-size: 15px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.goods-price { color: #f56c6c; font-size: 20px; font-weight: bold; margin-bottom: 8px; }
.goods-meta { display: flex; gap: 8px; align-items: center; color: #909399; font-size: 12px; margin-bottom: 8px; }
.goods-stats { display: flex; justify-content: space-between; align-items: center; font-size: 13px; color: #909399; }
</style>
