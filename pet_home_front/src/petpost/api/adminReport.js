// src/api/adminReport.js
import api from './http'

export function findPendingReports() {
  return api.get('/admin/forum/reports/pending')
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