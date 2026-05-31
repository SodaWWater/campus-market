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
        </el-menu>
      </div>
      <div class="header-right">
        <template v-if="auth.isLoggedIn">
          <el-button v-if="auth.isAdmin" size="small" type="warning" @click="$router.push('/admin')" style="margin-right:8px">
            后台管理
          </el-button>
          <el-dropdown @command="handleCommand" trigger="click">
            <span class="user-info">
              <el-avatar :size="24" icon="UserFilled" style="margin-right:6px" />
              {{ auth.username }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><UserFilled /></el-icon> 个人资料
                </el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="admin" divided>
                  <el-icon><Setting /></el-icon> 后台管理
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
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
    <el-footer class="main-footer">
      Campus Market &copy; {{ new Date().getFullYear() }}
    </el-footer>
  </el-container>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Setting, SwitchButton, UserFilled } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'
import { getUnreadCount } from '../api/market'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const unreadCount = ref(0)

const activeRoute = computed(() => {
  const path = route.path
  if (path.startsWith('/seller/goods/new')) return '/seller/goods/new'
  if (path.startsWith('/seller/goods')) return '/seller/goods'
  if (path.startsWith('/buyer/orders')) return '/buyer/orders'
  if (path.startsWith('/seller/orders')) return '/seller/orders'
  if (path.startsWith('/goods')) return '/market'
  return '/' + path.split('/')[1]
})

function handleCommand(cmd) {
  if (cmd === 'logout') { auth.logout(); router.push('/login') }
  else if (cmd === 'profile') router.push('/profile')
  else if (cmd === 'admin') router.push('/admin')
}

onMounted(async () => {
  if (auth.isLoggedIn) {
    try { unreadCount.value = (await getUnreadCount()).unreadCount } catch {}
  }
})
</script>

<style scoped>
.main-layout { min-height: 100vh; background: #f5f7fa; }
.main-header {
  display: flex; align-items: center; justify-content: space-between;
  background: #fff; border-bottom: 1px solid #e4e7ed;
  padding: 0 24px; height: 60px; box-shadow: 0 1px 4px rgba(0,0,0,.04);
}
.header-left { display: flex; align-items: center; gap: 24px; }
.brand { font-size: 18px; font-weight: bold; color: #409eff; cursor: pointer; white-space: nowrap; }
.header-menu { border-bottom: none !important; }
.header-menu .el-menu-item { height: 60px; line-height: 60px; }
.header-right { display: flex; align-items: center; gap: 12px; }
.user-info {
  display: flex; align-items: center; cursor: pointer;
  color: #303133; font-size: 14px;
}
.main-footer {
  text-align: center; color: #999; font-size: 12px;
  height: 40px; line-height: 40px;
  background: #fff; border-top: 1px solid #e8e8e8;
}
</style>
