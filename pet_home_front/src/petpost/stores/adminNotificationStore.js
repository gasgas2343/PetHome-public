// src/petpost/stores/adminNotificationStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  findAllAdminNotifications,
  deleteAdminNotification,
  deleteAllAdminNotifications,
} from '@/petpost/api/adminNotification'
import { getApiErrorMessage, logApiError, unwrapApiList } from '@/petpost/utils/apiErrorHandler'

export const useAdminNotificationStore = defineStore('adminNotification', () => {
  const notifications = ref([])
  const loading = ref(false)
  const errorMessage = ref('')

  async function loadNotifications() {
    loading.value = true
    errorMessage.value = ''
    try {
      const response = await findAllAdminNotifications()
      notifications.value = unwrapApiList(response)
    } catch (error) {
      logApiError('查詢通知失敗', error)
      notifications.value = []
      errorMessage.value = getApiErrorMessage(error, '查詢通知失敗')
    } finally {
      loading.value = false
    }
  }

  async function deleteAllNotifications() {
    const result = confirm('確定刪除所有通知紀錄？')
    if (!result) return
    loading.value = true
    errorMessage.value = ''
    try {
      await deleteAllAdminNotifications()
      notifications.value = []
    } catch (error) {
      logApiError('刪除所有通知紀錄失敗', error)
      errorMessage.value = getApiErrorMessage(error, '刪除所有通知紀錄失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  async function deleteNotification(notificationId) {
    const result = confirm('確定刪除這筆通知？')
    if (!result) {
      return
    }
    loading.value = true
    errorMessage.value = ''
    try {
      await deleteAdminNotification(notificationId)
      await loadNotifications()
    } catch (error) {
      logApiError('刪除通知失敗', error)
      errorMessage.value = getApiErrorMessage(error, '刪除通知失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    notifications,
    loading,
    errorMessage,
    loadNotifications,
    deleteNotification,
    deleteAllNotifications,
  }
})
