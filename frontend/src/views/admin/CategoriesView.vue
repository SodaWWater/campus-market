<template>
  <div>
    <el-card header="分类管理">
      <el-form :inline="true" style="margin-bottom:16px">
        <el-input v-model="form.name" placeholder="分类名称" style="width:160px" />
        <el-input-number v-model="form.sort" :min="0" placeholder="排序" style="width:120px;margin-left:8px" />
        <el-select v-model="form.status" style="width:120px;margin-left:8px">
          <el-option label="启用" value="ENABLE" />
          <el-option label="禁用" value="DISABLED" />
        </el-select>
        <el-button type="primary" @click="create" style="margin-left:8px">新增</el-button>
      </el-form>
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" v-if="row.status==='DISABLED'" @click="enable(row.id)">启用</el-button>
            <el-button size="small" type="warning" v-if="row.status==='ENABLE'" @click="disable(row.id)">禁用</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listCategories, createCategory, enableCategory, disableCategory } from '../../api/market'

const list = ref([])
const form = reactive({ name: '', sort: 0, status: 'ENABLE' })

async function load() { list.value = await listCategories() }
async function create() {
  if (!form.name) { ElMessage.warning('请输入分类名称'); return }
  await createCategory({ ...form })
  ElMessage.success('分类已创建')
  form.name = ''
  await load()
}
async function enable(id) { await enableCategory(id); ElMessage.success('已启用'); await load() }
async function disable(id) { await disableCategory(id); ElMessage.success('已禁用'); await load() }

onMounted(load)
</script>
