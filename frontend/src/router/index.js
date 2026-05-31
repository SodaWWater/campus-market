import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import MainLayout from '../layouts/MainLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
    meta: { guest: true }
  },
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', redirect: '/market' },
      { path: 'market', name: 'Market', component: () => import('../views/MarketView.vue') },
      { path: 'goods/:id', name: 'GoodsDetail', component: () => import('../views/GoodsDetailView.vue') },
      { path: 'seller/goods', name: 'SellerGoods', component: () => import('../views/SellerGoodsView.vue'), meta: { auth: true } },
      { path: 'seller/goods/new', name: 'PublishGoods', component: () => import('../views/PublishGoodsView.vue'), meta: { auth: true } },
      { path: 'buyer/orders', name: 'BuyerOrders', component: () => import('../views/BuyerOrdersView.vue'), meta: { auth: true } },
      { path: 'seller/orders', name: 'SellerOrders', component: () => import('../views/SellerOrdersView.vue'), meta: { auth: true } },
      { path: 'favorites', name: 'Favorites', component: () => import('../views/FavoritesView.vue'), meta: { auth: true } },
      { path: 'messages', name: 'Messages', component: () => import('../views/MessagesView.vue'), meta: { auth: true } },
      { path: 'profile', name: 'Profile', component: () => import('../views/ProfileView.vue'), meta: { auth: true } },
      { path: 'admin/dashboard', name: 'AdminDashboard', component: () => import('../views/admin/DashboardView.vue'), meta: { auth: true, admin: true } },
      { path: 'admin/categories', name: 'AdminCategories', component: () => import('../views/admin/CategoriesView.vue'), meta: { auth: true, admin: true } },
      { path: 'admin/goods', name: 'AdminGoods', component: () => import('../views/admin/GoodsView.vue'), meta: { auth: true, admin: true } },
      { path: 'admin/goods/pending', name: 'AdminPendingGoods', component: () => import('../views/admin/PendingGoodsView.vue'), meta: { auth: true, admin: true } },
      { path: 'admin/orders', name: 'AdminOrders', component: () => import('../views/admin/OrdersView.vue'), meta: { auth: true, admin: true } },
      { path: 'admin/users', name: 'AdminUsers', component: () => import('../views/admin/UsersView.vue'), meta: { auth: true, admin: true } },
      { path: 'admin/reports', name: 'AdminReports', component: () => import('../views/admin/ReportsView.vue'), meta: { auth: true, admin: true } },
      { path: 'admin/logs', name: 'AdminLogs', component: () => import('../views/admin/LogsView.vue'), meta: { auth: true, admin: true } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/market' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  auth.load()
  if (to.meta.auth && !auth.isLoggedIn) return next('/login')
  if (to.meta.admin && !auth.isAdmin) return next('/market')
  if (to.meta.guest && auth.isLoggedIn) return next('/market')
  next()
})

export default router
