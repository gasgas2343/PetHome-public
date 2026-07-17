import api from './http'

export function findAllAdminComments() {
  return api.get('/admin/forum/comments')
}

export function hideAdminComment(commentId) {
  return api.put(`/admin/comments/${commentId}/hide`)
}

export function restoreAdminComment(commentId) {
  return api.put(`/admin/comments/${commentId}/restore`)
}

export function deleteAdminComment(commentId) {
  return api.delete(`/admin/comments/${commentId}`)
}
