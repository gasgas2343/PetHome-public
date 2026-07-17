// src/api/adminAppeal.js
import api from './http'

export function findAllAppeals() {
  return api.get('/appeals')
}

export function findPendingAppeals() {
  return api.get('/appeals/pending')
}

export function approveAppeal(appealId, note = '申訴通過') {
  return api.put(`/appeals/${appealId}/approve`, null, {
    params: { note },
  })
}

export function rejectAppeal(appealId, note = '申訴駁回') {
  return api.put(`/appeals/${appealId}/reject`, null, {
    params: { note },
  })
}