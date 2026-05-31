<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的商品</span>
          <el-button type="primary" @click="openPublish">发布新商品</el-button>
        </div>
      </template>
      <el-table :data="list" v-if="list.length > 0">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/goods/${row.id}`)">查看</el-button>
            <el-button size="small" type="danger" v-if="row.status !== 'OFF_SHELF' && row.status !== 'SOLD'" @click="offShelf(row.id)">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无商品，发布第一个吧" />
    </el-card>

    <!-- Publish Dialog -->
    <el-dialog v-model="dialogVisible" title="发布商品" width="560px" destroy-on-close>
      <el-form label-position="top" :model="form">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="分类" required>
              <el-select v-model="form.categoryId" placeholder="选择分类" style="width:100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成色">
              <el-select v-model="form.conditionLevel" style="width:100%">
                <el-option label="全新" value="NEW" />
                <el-option label="几乎全新" value="LIKE_NEW" />
                <el-option label="良好" value="GOOD" />
                <el-option label="一般" value="NORMAL" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="请输入商品标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="描述商品成色、使用情况等" maxlength="500" show-word-limit />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="价格" required>
              <el-input-number v-model="form.price" :min="0.01" :precision="2" :step="1" style="width:100%" placeholder="0.00" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交易地点">
              <el-input v-model="form.tradeLocation" placeholder="如：图书馆门口" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">提交审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { pageMyGoods, offShelfGoods as offShelfMyGoods, listCategories, createGoods } from '../api/market'

const list = ref([])
const categories = ref([])
const dialogVisible = ref(false)
const submitting = ref(false)

const defaultForm = { categoryId: null, title: '', description: '', price: 0, conditionLevel: 'GOOD', tradeLocation: '', imageUrls: [] }
const form = reactive({ ...defaultForm })

function statusTag(s) { return { ON_SALE: 'success', PENDING_AUDIT: 'warning', REJECTED: 'danger', LOCKED: 'info', SOLD: 'info' }[s] || 'info' }
function statusLabel(s) { return { ON_SALE: '在售', PENDING_AUDIT: '待审核', REJECTED: '已驳回', LOCKED: '已锁定', SOLD: '已售', OFF_SHELF: '已下架' }[s] || s }

async function load() {
  const r = await pageMyGoods({ current: 1, size: 100 })
  list.value = r.records || []
}
async function offShelf(id) {
  try { await offShelfMyGoods(id); ElMessage.success('已下架'); await load() } catch {}
}
async function openPublish() {
  try { categories.value = await listCategories() } catch {}
  Object.assign(form, defaultForm)
  dialogVisible.value = true
}
async function submit() {
  if (!form.categoryId || !form.title) { ElMessage.warning('请填写分类和标题'); return }
  if (!form.price || form.price <= 0) { ElMessage.warning('请填写有效的价格'); return }
  submitting.value = true
  try {
    await createGoods({ ...form, imageUrls: [] })
    ElMessage.success('已提交审核')
    dialogVisible.value = false
    await load()
  } finally { submitting.value = false }
}

onMounted(load)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
