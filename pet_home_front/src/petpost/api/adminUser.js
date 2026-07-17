import api from './http'

// 中文註解：查詢論壇後台會員列表。
// 對應後端 GET /api/admin/forum-users
export function findAdminUsers() {
  return api.get('/admin/forum-users')
}

// 中文註解：論壇停權。
// 對應後端 PUT /api/admin/forum-users/{userId}/suspend
export function suspendAdminUser(userId) {
  return api.put(`/admin/forum-users/${userId}/suspend`)
}

// 中文註解：解除論壇停權。
// 對應後端 PUT /api/admin/forum-users/{userId}/activate
export function activateAdminUser(userId) {
  return api.put(`/admin/forum-users/${userId}/activate`)
}
