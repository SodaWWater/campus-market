<template>
  <el-container class="main-layout">
    <el-header class="main-header">
      <div class="header-left">
        <span class="brand" @click="$router.push('/market')">🎓 校园二手交易</span>
        <el-menu mode="horizontal" :default-active="activeRoute" router class="header-menu">
          <el-menu-item index="/market">商品广场</el-menu-item>
          <el-menu-item index="/seller/goods" v-if="auth.isLoggedIn">我的商品</el-menu-item>
          <el-menu-item index="/seller/goods/new" v-if="auth.isLoggedIn">发布商品</el-menu-item>
          <el-menu-item index="/buyer/orders" v-if="auth.isLoggedIn">我买到的</el-menu-item>
          <el-menu-item index="/seller/orders" v-if="auth.isLoggedIn">我卖出的</el-menu-item>
          <el-menu-item index="/favorites" v-if="auth.isLoggedIn">收藏夹</el-menu-item>
          <el-menu-item index="/messages" v-if="auth.isLoggedIn">
            消息
            <el-badge v-if="unreadCount > 0" :value="unreadCount" style="margin-left:6px" />
          </el-menu-item>
          <el-menu-item index="/admin/dashboard" v-if="auth.isAdmin">后台管理</el-menu-item>
        </el-menu>
      </div>
      <div class="header-right">
        <template v-if="auth.isLoggedIn">
          <el-dropdown @command="handleCommand">
            <span class="user-info">{{ auth.username }} · {{ auth.role }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button size="small" @click="$router.push('/login')">登录</el-button>
          <el-button size="small" type="primary" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getUnreadCount } from '../api/market'

const route = useRoute()
const auth = useAuthStore()
const unreadCount = ref(0)

const activeRoute = computed(() => '/' + route.path.split('/').slice(1, 3).join('/'))

async function loadUnread() {
  if (!auth.isLoggedIn) return
  try { unreadCount.value = await getUnreadCount() } catch {}
}

function handleCommand(cmd) {
  if (cmd === 'logout') {
    auth.logout()
    location.href = '/market'
  } else if (cmd === 'profile') {
    location.href = '/profile'
  }
}

onMounted(loadUnread)
</script>

<style scoped>
.main-layout { min-height: 100vh; background: #f5f7fa; }
.main-header { display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #e4e7ed; padding: 0 20px; height: 60px; }
.header-left { display: flex; align-items: center; gap: 20px; }
.brand { font-size: 18px; font-weight: bold; color: #409eff; cursor: pointer; white-space: nowrap; }
.header-menu { border-bottom: none !important; }
.header-menu .el-menu-item { height: 60px; line-height: 60px; }
.header-right { display: flex; align-items: center; gap: 10px; }
.user-info { cursor: pointer; color: #409eff; font-size: 14px; }
</style>
