<template>
  <div>
    <el-card header="发布商品">
      <el-form label-position="top" style="max-width:600px">
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="成色">
          <el-select v-model="form.conditionLevel">
            <el-option label="全新" value="NEW" />
            <el-option label="几乎全新" value="LIKE_NEW" />
            <el-option label="良好" value="GOOD" />
            <el-option label="一般" value="NORMAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="交易地点"><el-input v-model="form.tradeLocation" placeholder="如：图书馆门口" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">提交审核</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listCategories, createGoods } from '../api/market'

const router = useRouter()
const categories = ref([])
const form = reactive({ categoryId: null, title: '', description: '', price: 0, conditionLevel: 'GOOD', tradeLocation: '', imageUrls: [] })

async function submit() {
  if (!form.categoryId || !form.title) { ElMessage.warning('请填写分类和标题'); return }
  await createGoods({ ...form })
  ElMessage.success('已提交审核')
  router.push('/seller/goods')
}

onMounted(async () => { categories.value = await listCategories() })
</script>
