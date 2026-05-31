<template>
  <el-config-provider>
    <div v-if="!auth.isLoggedIn" class="login-layout">
      <el-card class="login-card" shadow="never">
        <div class="login-title">
          <div class="brand-mark">M</div>
          <div>
            <h1>校园二手交易后台</h1>
            <p>JWT 登录后进入管理台</p>
          </div>
        </div>
        <el-tabs v-model="loginMode">
          <el-tab-pane label="登录" name="login">
            <el-form label-position="top">
              <el-form-item label="用户名">
                <el-input v-model="loginForm.username" placeholder="student1" />
              </el-form-item>
              <el-form-item label="密码">
                <el-input v-model="loginForm.password" type="password" show-password placeholder="123456" />
              </el-form-item>
              <el-button type="primary" :icon="Key" :loading="authLoading" @click="submitLogin">登录</el-button>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="注册" name="register">
            <el-form label-position="top">
              <el-form-item label="用户名">
                <el-input v-model="registerForm.username" />
              </el-form-item>
              <el-form-item label="密码">
                <el-input v-model="registerForm.password" type="password" show-password />
              </el-form-item>
              <el-form-item label="昵称">
                <el-input v-model="registerForm.nickname" />
              </el-form-item>
              <el-form-item label="角色">
                <el-select v-model="registerForm.role">
                  <el-option label="USER" value="USER" />
                  <el-option label="ADMIN" value="ADMIN" />
                </el-select>
              </el-form-item>
              <el-button type="primary" :icon="UserFilled" :loading="authLoading" @click="submitRegister">
                注册并登录
              </el-button>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <div v-else class="app-shell">
      <aside class="sidebar">
        <div class="brand">
          <div class="brand-mark">M</div>
          <div>
            <h1>校园二手交易后台</h1>
            <p>{{ auth.username }} · {{ auth.role }}</p>
          </div>
        </div>

        <el-menu :default-active="activeView" class="nav-menu" @select="activeView = $event">
          <el-menu-item index="dashboard">
            <el-icon><DataBoard /></el-icon>
            <span>总览</span>
          </el-menu-item>
          <el-menu-item index="goods">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="category">
            <el-icon><Collection /></el-icon>
            <span>分类缓存</span>
          </el-menu-item>
          <el-menu-item index="orders">
            <el-icon><Tickets /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="admin" :disabled="!auth.isAdmin">
            <el-icon><Setting /></el-icon>
            <span>后台管理</span>
          </el-menu-item>
        </el-menu>

        <el-button :icon="SwitchButton" @click="logout">退出登录</el-button>
      </aside>

      <main class="main-panel">
        <header class="topbar">
          <div>
            <h2>{{ viewTitle }}</h2>
            <p>业务请求通过 Axios 自动携带 Authorization Bearer token。</p>
          </div>
          <el-button :icon="Refresh" @click="bootstrap">刷新</el-button>
        </header>

        <section v-if="activeView === 'dashboard'" class="workspace summary-grid">
          <el-card shadow="never">
            <template #header>后端状态</template>
            <el-tag :type="healthStatus === 'ok' ? 'success' : 'warning'">{{ healthStatus }}</el-tag>
          </el-card>
          <el-card shadow="never">
            <template #header>当前用户</template>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="userId">{{ auth.userId }}</el-descriptions-item>
              <el-descriptions-item label="username">{{ auth.username }}</el-descriptions-item>
              <el-descriptions-item label="role">{{ auth.role }}</el-descriptions-item>
            </el-descriptions>
          </el-card>
          <el-card shadow="never">
            <template #header>测试建议</template>
            <ol class="ordered">
              <li>发布一个商品</li>
              <li>用另一个 USER 登录创建订单</li>
              <li>用 ADMIN 登录查看后台商品并下架</li>
            </ol>
          </el-card>
        </section>

        <section v-if="activeView === 'goods'" class="workspace two-column">
          <el-card shadow="never">
            <template #header>发布商品</template>
            <el-form label-position="top">
              <el-form-item label="分类">
                <el-select v-model="goodsForm.categoryId" placeholder="选择分类">
                  <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="标题">
                <el-input v-model="goodsForm.title" />
              </el-form-item>
              <el-form-item label="描述">
                <el-input v-model="goodsForm.description" type="textarea" :rows="3" />
              </el-form-item>
              <el-form-item label="价格">
                <el-input-number v-model="goodsForm.price" :min="0" :precision="2" />
              </el-form-item>
              <el-button type="primary" :icon="Plus" @click="submitGoods">发布</el-button>
            </el-form>
          </el-card>

          <el-card shadow="never">
            <template #header>商品查询</template>
            <div class="filter-row">
              <el-input v-model="goodsQuery.keyword" placeholder="关键词" clearable />
              <el-select v-model="goodsQuery.categoryId" clearable placeholder="分类">
                <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
              <el-button :icon="Search" @click="loadGoods">查询</el-button>
            </div>
            <el-table :data="goodsList" height="360">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="title" label="标题" />
              <el-table-column prop="price" label="价格" width="90" />
              <el-table-column prop="status" label="状态" width="110" />
              <el-table-column label="操作" width="130">
                <template #default="{ row }">
                  <el-button size="small" :icon="ShoppingCart" @click="quickOrder(row.id)">下单</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </section>

        <section v-if="activeView === 'category'" class="workspace two-column">
          <el-card shadow="never">
            <template #header>分类列表</template>
            <p class="muted">后端使用 Redis 缓存 key: market:category:list。</p>
            <el-button :icon="Refresh" @click="loadCategories">刷新分类</el-button>
            <el-table :data="categories" class="inner-table">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="name" label="名称" />
              <el-table-column prop="status" label="状态" />
            </el-table>
          </el-card>

          <el-card shadow="never">
            <template #header>ADMIN 分类维护</template>
            <el-alert v-if="!auth.isAdmin" type="warning" show-icon title="当前用户不是 ADMIN，无法调用分类维护接口。" />
            <el-form label-position="top">
              <el-form-item label="分类名称">
                <el-input v-model="categoryForm.name" />
              </el-form-item>
              <el-form-item label="排序">
                <el-input-number v-model="categoryForm.sort" :min="0" />
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="categoryForm.status">
                  <el-option label="ENABLE" value="ENABLE" />
                  <el-option label="DISABLE" value="DISABLE" />
                </el-select>
              </el-form-item>
              <el-button type="primary" :disabled="!auth.isAdmin" :icon="Plus" @click="submitCategory">新增分类</el-button>
            </el-form>
          </el-card>
        </section>

        <section v-if="activeView === 'orders'" class="workspace">
          <el-card shadow="never">
            <template #header>我的订单</template>
            <el-button :icon="Refresh" @click="loadOrders">刷新订单</el-button>
            <el-table :data="orders" class="inner-table">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="orderNo" label="订单号" />
              <el-table-column prop="goodsId" label="商品" width="90" />
              <el-table-column prop="amount" label="金额" width="100" />
              <el-table-column prop="status" label="状态" width="110" />
              <el-table-column label="操作" width="260">
                <template #default="{ row }">
                  <el-button size="small" @click="pay(row.id)">支付</el-button>
                  <el-button size="small" @click="cancel(row.id)">取消</el-button>
                  <el-button size="small" @click="finish(row.id)">完成</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </section>

        <section v-if="activeView === 'admin'" class="workspace">
          <el-card shadow="never">
            <template #header>ADMIN 后台</template>
            <div class="filter-row">
              <el-button :icon="UserFilled" @click="loadAdminUsers">用户列表</el-button>
              <el-button :icon="Goods" @click="loadAdminGoods">商品列表</el-button>
              <el-button :icon="Tickets" @click="loadAdminOrders">订单列表</el-button>
            </div>
            <el-table v-if="adminMode === 'users'" :data="adminRows" class="inner-table">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="username" label="用户名" />
              <el-table-column prop="role" label="角色" />
            </el-table>
            <el-table v-else-if="adminMode === 'goods'" :data="adminRows" class="inner-table">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="title" label="标题" />
              <el-table-column prop="status" label="状态" />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button size="small" type="warning" @click="offGoods(row.id)">下架</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-table v-else :data="adminRows" class="inner-table">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="orderNo" label="订单号" />
              <el-table-column prop="status" label="状态" />
            </el-table>
          </el-card>
        </section>
      </main>
    </div>
  </el-config-provider>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Collection,
  DataBoard,
  Goods,
  Key,
  Plus,
  Refresh,
  Search,
  Setting,
  ShoppingCart,
  SwitchButton,
  Tickets,
  UserFilled
} from '@element-plus/icons-vue'
import { useAuthStore } from './stores/auth'
import {
  adminGoods,
  adminOrders,
  adminUsers,
  cancelOrder,
  createCategory,
  createGoods,
  createOrder,
  finishOrder,
  health,
  listCategories,
  listMyOrders,
  login,
  offShelfGoods,
  pageGoods,
  payOrder,
  register
} from './api/market'

const auth = useAuthStore()
const loginMode = ref('login')
const authLoading = ref(false)
const activeView = ref('dashboard')
const healthStatus = ref('checking')
const categories = ref([])
const goodsList = ref([])
const orders = ref([])
const adminMode = ref('users')
const adminRows = ref([])

const loginForm = reactive({ username: 'student1', password: '123456' })
const registerForm = reactive({ username: '', password: '123456', nickname: '', phone: '', role: 'USER' })
const goodsForm = reactive({ categoryId: null, title: '', description: '', price: 0 })
const goodsQuery = reactive({ current: 1, size: 10, keyword: '', categoryId: null })
const categoryForm = reactive({ name: '', sort: 0, status: 'ENABLE' })

const viewTitle = computed(() => ({
  dashboard: '系统总览',
  goods: '商品管理',
  category: '分类缓存',
  orders: '我的订单',
  admin: '后台管理'
}[activeView.value]))

async function submitLogin() {
  authLoading.value = true
  try {
    auth.save(await login({ ...loginForm }))
    ElMessage.success('登录成功')
    await bootstrap()
  } finally {
    authLoading.value = false
  }
}

async function submitRegister() {
  authLoading.value = true
  try {
    auth.save(await register({ ...registerForm }))
    ElMessage.success('注册成功')
    await bootstrap()
  } finally {
    authLoading.value = false
  }
}

function logout() {
  auth.logout()
  activeView.value = 'dashboard'
}

async function bootstrap() {
  try {
    healthStatus.value = await health()
  } catch {
    healthStatus.value = 'offline'
  }
  if (auth.isLoggedIn) {
    await Promise.allSettled([loadCategories(), loadGoods(), loadOrders()])
  }
}

async function loadCategories() {
  categories.value = await listCategories()
  if (!goodsForm.categoryId && categories.value.length) {
    goodsForm.categoryId = categories.value[0].id
  }
}

async function submitCategory() {
  await createCategory({ ...categoryForm })
  ElMessage.success('分类已创建')
  categoryForm.name = ''
  await loadCategories()
}

async function submitGoods() {
  if (!goodsForm.categoryId || !goodsForm.title) {
    ElMessage.warning('请填写分类和标题')
    return
  }
  await createGoods({ ...goodsForm })
  ElMessage.success('商品已发布')
  goodsForm.title = ''
  goodsForm.description = ''
  goodsForm.price = 0
  await loadGoods()
}

async function loadGoods() {
  const page = await pageGoods({ ...goodsQuery })
  goodsList.value = page.records || []
}

async function quickOrder(goodsId) {
  await createOrder({ goodsId })
  ElMessage.success('订单已创建')
  await Promise.allSettled([loadGoods(), loadOrders()])
}

async function loadOrders() {
  orders.value = await listMyOrders()
}

async function pay(id) {
  await payOrder(id)
  ElMessage.success('已支付')
  await loadOrders()
}

async function cancel(id) {
  await cancelOrder(id)
  ElMessage.success('已取消')
  await Promise.allSettled([loadOrders(), loadGoods()])
}

async function finish(id) {
  await finishOrder(id)
  ElMessage.success('已完成')
  await loadOrders()
}

async function loadAdminUsers() {
  adminMode.value = 'users'
  adminRows.value = await adminUsers()
}

async function loadAdminGoods() {
  adminMode.value = 'goods'
  adminRows.value = await adminGoods()
}

async function loadAdminOrders() {
  adminMode.value = 'orders'
  adminRows.value = await adminOrders()
}

async function offGoods(id) {
  await offShelfGoods(id)
  ElMessage.success('商品已下架')
  await loadAdminGoods()
}

onMounted(() => {
  auth.load()
  bootstrap()
})
</script>
