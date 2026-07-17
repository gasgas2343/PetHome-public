import api from './http'

export function findAllAdminNotifications() {
  return api.get('/admin/notifications')
}

export function deleteAdminNotification(notificationId) {
  return api.delete(`/admin/notifications/${notificationId}`)
}

export function deleteAllAdminNotifications() {
  return api.delete('/admin/notifications')
}
