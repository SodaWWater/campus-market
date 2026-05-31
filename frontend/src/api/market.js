import http from './http'

export function health() {
  return http.get('/health')
}

export function register(payload) {
  return http.post('/auth/register', payload)
}

export function login(payload) {
  return http.post('/auth/login', payload)
}

export function createGoods(payload) {
  return http.post('/goods', payload)
}

export function updateGoods(id, payload) {
  return http.put(`/goods/${id}`, payload)
}

export function deleteGoods(id) {
  return http.delete(`/goods/${id}`)
}

export function pageGoods(params) {
  return http.get('/goods/page', { params })
}

export function getGoods(id) {
  return http.get(`/goods/${id}`)
}

export function listCategories() {
  return http.get('/categories')
}

export function createCategory(payload) {
  return http.post('/admin/categories', payload)
}

export function updateCategory(id, payload) {
  return http.put(`/admin/categories/${id}`, payload)
}

export function deleteCategory(id) {
  return http.delete(`/admin/categories/${id}`)
}

export function createOrder(payload) {
  return http.post('/orders', payload)
}

export function listMyBuyOrders() {
  return http.get('/orders/my-buy')
}

export function listMySellOrders() {
  return http.get('/orders/my-sell')
}

export function getOrder(id) {
  return http.get(`/orders/${id}`)
}

export function mockPay(payload) {
  return http.post('/payments/mock-pay', payload)
}

export function getOrderPayment(orderId) {
  return http.get(`/payments/order/${orderId}`)
}

export function cancelOrder(id) {
  return http.put(`/orders/${id}/cancel`)
}

export function finishOrder(id) {
  return http.put(`/orders/${id}/finish`)
}

export function favoriteGoods(id) {
  return http.post(`/goods/${id}/favorite`)
}

export function unfavoriteGoods(id) {
  return http.delete(`/goods/${id}/favorite`)
}

export function listMyFavorites() {
  return http.get('/favorites/my')
}

export function adminUsers() {
  return http.get('/admin/users')
}

export function adminGoods() {
  return http.get('/admin/goods')
}

export function offShelfGoods(id) {
  return http.put(`/admin/goods/${id}/off`)
}

export function adminOrders() {
  return http.get('/admin/orders')
}

export function adminDashboard() {
  return http.get('/admin/dashboard')
}

export function disableUser(id) {
  return http.put(`/admin/users/${id}/disable`)
}

export function enableUser(id) {
  return http.put(`/admin/users/${id}/enable`)
}

export function enableCategory(id) {
  return http.put(`/admin/categories/${id}/enable`)
}

export function disableCategory(id) {
  return http.put(`/admin/categories/${id}/disable`)
}

export function adminPendingGoods() {
  return http.get('/admin/goods/pending')
}

export function approveGoods(id) {
  return http.put(`/admin/goods/${id}/approve`)
}

export function rejectGoods(id, reason) {
  return http.put(`/admin/goods/${id}/reject`, { reason })
}

export function adminOperationLogs() {
  return http.get('/admin/operation-logs')
}

export function sendMessage(payload) {
  return http.post('/messages', payload)
}

export function listConversations() {
  return http.get('/messages/conversations')
}

export function getConversation(userId) {
  return http.get(`/messages/conversations/${userId}`)
}

export function getUnreadCount() {
  return http.get('/messages/unread-count')
}

export function markMessageRead(id) {
  return http.put(`/messages/${id}/read`)
}

export function createReview(payload) {
  return http.post('/reviews', payload)
}

export function listGoodsReviews(goodsId) {
  return http.get(`/reviews/goods/${goodsId}`)
}

export function listUserReviews(userId) {
  return http.get(`/reviews/users/${userId}`)
}

export function submitReport(payload) {
  return http.post('/reports', payload)
}

export function listMyReports() {
  return http.get('/reports/my')
}

export function adminListReports() {
  return http.get('/admin/reports')
}

export function adminHandleReport(id, payload) {
  return http.put(`/admin/reports/${id}/handle`, payload)
}
