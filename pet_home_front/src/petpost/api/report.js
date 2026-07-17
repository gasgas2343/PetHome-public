import api from './http'

export function createReport(dto) {
  return api.post('/reports', dto)
}

export function findAllReports() {
  return api.get('/reports')
}

export function findReportById(reportId) {
  return api.get(`/reports/${reportId}`)
}

export function findPendingReports() {
  return api.get('/reports/pending')
}

export function approveReport(reportId, note = '檢舉成立') {
  return api.put(`/reports/${reportId}/approve`, null, {
    params: { note },
  })
}

export function rejectReport(reportId, note = '檢舉駁回') {
  return api.put(`/reports/${reportId}/reject`, null, {
    params: { note },
  })
}