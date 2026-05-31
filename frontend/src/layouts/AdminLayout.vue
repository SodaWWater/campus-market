<template>
  <el-container class="admin-shell">
    <el-aside class="admin-sidebar" width="220px">
      <div class="admin-brand" @click="$router.push('/admin/dashboard')">
        <el-icon :size="24"><Setting /></el-icon>
        <span>Campus Market</span>
      </div>
      <div class="admin-subtitle">后台管理中心</div>

      <el-menu
        :default-active="activeMenu"
        router
        class="admin-menu"
        background-color="#001529"
        text-color="#ffffffb3"
        active-text-color="#fff"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><Collection /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/goods">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/goods/pending">
          <el-icon><Clock /></el-icon>
          <span>商品审核</span>
          <span v-if="pendingCount > 0" class="pending-badge">{{ pendingCount }}</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><Tickets /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><UserFilled /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/reports">
          <el-icon><WarningFilled /></el-icon>
          <span>举报管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/logs">
          <el-icon><Document /></el-icon>
          <span>操作日志</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <el-button text @click="$router.push('/market')" style="color:#ffffffb3">
          <el-icon><Back /></el-icon> 返回商城
        </el-button>
      </div>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">后台</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumb">{{ breadcrumb }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-badge :value="unreadCount" :hidden="!unreadCount">
            <el-icon :size="18"><Bell /></el-icon>
          </el-badge>
          <el-dropdown @command="handleCommand" trigger="click">
            <span class="admin-user">
              <el-avatar :size="28" icon="UserFilled" />
              <span>{{ auth.username }}</span>
              <el-tag size="small" type="danger" effect="dark">ADMIN</el-tag>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="market">返回商城</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>

      <el-footer class="admin-footer">
        Campus Market Admin &copy; {{ year }}
      </el-footer>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Back, Bell, Clock, Collection, DataBoard,
  Document, Goods, Setting, Tickets, UserFilled, WarningFilled
} from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'
import { getUnreadCount, adminPendingGoods } from '../api/market'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const year = new Date().getFullYear()
const unreadCount = ref(0)
const pendingCount = ref(0)

const breadcrumbMap = {
  '/admin/dashboard': '数据看板',
  '/admin/categories': '分类管理',
  '/admin/goods': '商品管理',
  '/admin/goods/pending': '商品审核',
  '/admin/orders': '订单管理',
  '/admin/users': '用户管理',
  '/admin/reports': '举报管理',
  '/admin/logs': '操作日志'
}

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/admin/goods/pending')) return '/admin/goods/pending'
  return '/' + path.split('/').slice(0, 3).join('/')
})

const breadcrumb = computed(() => breadcrumbMap[activeMenu.value] || '')

function handleCommand(cmd) {
  if (cmd === 'logout') { auth.logout(); router.push('/login') }
  else if (cmd === 'market') router.push('/market')
  else if (cmd === 'profile') router.push('/profile')
}

onMounted(async () => {
  try { unreadCount.value = (await getUnreadCount()).unreadCount } catch {}
  try { pendingCount.value = (await adminPendingGoods()).length } catch {}
})
</script>

<style scoped>
.admin-shell { height: 100vh; }

/* ---- Sidebar ---- */
.admin-sidebar {
  background-color: #001529;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}
.admin-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 20px 4px;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
}
.admin-subtitle {
  color: #ffffff73;
  font-size: 12px;
  padding: 0 20px 16px;
}
.admin-menu {
  border-right: none;
  flex: 1;
}
.admin-menu .el-menu-item {
  height: 44px;
  line-height: 44px;
  margin: 2px 8px;
  border-radius: 6px;
}
.admin-menu .el-menu-item:hover { background-color: #ffffff15 !important; }
.admin-menu .el-menu-item.is-active { background-color: #409eff !important; }
.pending-badge {
  margin-left: auto;
  background: #f56c6c;
  color: #fff;
  font-size: 11px;
  min-width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  border-radius: 9px;
  padding: 0 5px;
}
.sidebar-footer {
  padding: 12px 16px;
  border-top: 1px solid #ffffff15;
}

/* ---- Header ---- */
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  height: 56px;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0,0,0,.04);
}
.header-left { display: flex; align-items: center; }
.header-right { display: flex; align-items: center; gap: 20px; }
.admin-user {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
}

/* ---- Main ---- */
.admin-main {
  background: #f0f2f5;
  padding: 24px;
  min-height: 0;
}

/* ---- Footer ---- */
.admin-footer {
  text-align: center;
  color: #999;
  font-size: 12px;
  height: 40px;
  line-height: 40px;
  background: #fff;
  border-top: 1px solid #e8e8e8;
}
</style>
