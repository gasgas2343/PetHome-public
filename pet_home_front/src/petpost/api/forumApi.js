import api from './http'

// 中文註解：舊版論壇 API 相容檔。
// 新程式建議優先使用 modules/petpost/api/post、notification、timeline 等專責 API。
export const getForumPosts = (params = {}) => api.get('/posts', { params })
export const getForumCategories = () => api.get('/categories')
export const getNotifications = () => api.get('/notifications')
export const getUnreadNotificationCount = () => api.get('/notifications/unread-count')
export const markNotificationRead = (notificationId) => api.put(`/notifications/${notificationId}/read`)

export const getPetTimeline = (petId) => api.get(`/pet-posts/pet/${petId}`)
export const createPetTimeline = (payload) => api.post('/pet-posts', payload)
export const updatePetTimeline = (id, payload) => api.put(`/pet-posts/${id}`, payload)
export const deletePetTimeline = (id) => api.delete(`/pet-posts/${id}`)

export const uploadLocalPetImage = (petPostId, file) => {
  const formData = new FormData()
  formData.append('file', file)

  return api.post(`/pet-posts/${petPostId}/images/upload`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export const saveCloudPetImage = (petPostId, imageUrl) => {
  return api.post(`/pet-posts/${petPostId}/images/url`, { imageUrl })
}
