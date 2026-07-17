import api from './axios'

// 中文註解：查詢後台會員列表。
// 對應後端 GET /api/admin/users
export function findAdminUsers() {
  return api.get('/admin/users')
}

// 中文註解：查詢全部角色。
// 後端若尚未完成，角色權限頁會顯示讀取失敗，不影響其他後台頁面。
export function getAllRolesApi() {
  return api.get('/admin/roles')
}

// 中文註解：查詢全部權限，通常後端回傳依 module 分組的權限資料。
export function getAllPermissionsApi() {
  return api.get('/admin/permissions')
}

// 中文註解：查詢單一角色目前擁有的權限。
export function getRolePermissionsApi(roleId) {
  return api.get(`/admin/roles/${roleId}/permissions`)
}

// 中文註解：更新角色權限。
export function updateRolePermissionsApi(roleId, data) {
  return api.put(`/admin/roles/${roleId}/permissions`, data)
}

// 中文註解：查詢目前登入者權限，可供後台 Layout / Menu 使用。
export function getPermissionApi() {
  return api.get('/admin/permissions/me')
}
