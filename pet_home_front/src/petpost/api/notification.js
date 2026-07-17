import api from './http'

export function findMyNotifications() {
  return api.get('/notifications/me')
}

export function findMyUnreadNotificationCount() {
  return api.get('/notifications/me/unread-count')
}

export function findNotificationById(notificationId) {
  return api.get(`/notifications/${notificationId}`)
}

export function markNotificationAsRead(notificationId) {
  return api.put(`/notifications/${notificationId}/read`)
}

export function markAllMyNotificationsAsRead() {
  return api.put('/notifications/me/read-all')
}

export function deleteNotification(notificationId) {
  return api.delete(`/notifications/${notificationId}`)
}