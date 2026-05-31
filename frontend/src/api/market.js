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
  return http.get('/category/list')
}

export function createCategory(payload) {
  return http.post('/admin/category', payload)
}

export function updateCategory(id, payload) {
  return http.put(`/admin/category/${id}`, payload)
}

export function deleteCategory(id) {
  return http.delete(`/admin/category/${id}`)
}

export function createOrder(payload) {
  return http.post('/order', payload)
}

export function listMyOrders() {
  return http.get('/order/my')
}

export function getOrder(id) {
  return http.get(`/order/${id}`)
}

export function payOrder(id) {
  return http.put(`/order/${id}/pay`)
}

export function cancelOrder(id) {
  return http.put(`/order/${id}/cancel`)
}

export function finishOrder(id) {
  return http.put(`/order/${id}/finish`)
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
