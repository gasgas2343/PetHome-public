import api from './http'

// 中文註解：取得管理後台首頁統計資料。
// 對應後端 GET /api/admin/dashboard/summary
export function findAdminDashboardSummary() {
  return api.get('/admin/dashboard/summary')
}
