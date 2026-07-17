// src/petpost/stores/notificationStore.js
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

import {
  findMyNotifications,
  findMyUnreadNotificationCount,
  markNotificationAsRead,
  markAllMyNotificationsAsRead,
  deleteNotification as deleteNotificationApi,
} from '@/petpost/api/notification'

import { getApiErrorMessage, logApiError, unwrapApiData, unwrapApiList } from '@/petpost/utils/apiErrorHandler'

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref([])
  const unreadCount = ref(0)
  const loading = ref(false)
  const errorMessage = ref('')

  const hasUnread = computed(() => unreadCount.value > 0)

  // 中文註解：統一命名，載入目前登入會員的通知。
  async function loadNotifications() {
    loading.value = true
    errorMessage.value = ''

    try {
      const response = await findMyNotifications()
      notifications.value = unwrapApiList(response)
    } catch (error) {
      logApiError('載入通知失敗', error)
      errorMessage.value = getApiErrorMessage(error, '載入通知失敗')
      notifications.value = []
    } finally {
      loading.value = false
    }
  }

  // 中文註解：fetchNotifications 為 loadNotifications 的舊版相容名稱。
  async function fetchNotifications() {
    return await loadNotifications()
  }

  async function loadUnreadCount() {
    try {
      const response = await findMyUnreadNotificationCount()
      const data = unwrapApiData(response, 0)
      unreadCount.value = Number(data) || 0
    } catch (error) {
      logApiError('載入未讀通知數量失敗', error)
      unreadCount.value = 0
    }
  }

  // 中文註解：fetchUnreadCount 為 loadUnreadCount 的舊版相容名稱。
  async function fetchUnreadCount() {
    return await loadUnreadCount()
  }

  async function readNotification(notificationId) {
    if (!notificationId) return

    try {
      await markNotificationAsRead(notificationId)

      const target = notifications.value.find((item) => item.notificationId === notificationId)

      if (target && !target.isRead) {
        target.isRead = true
        unreadCount.value = Math.max(unreadCount.value - 1, 0)
      }
    } catch (error) {
      logApiError('通知標記已讀失敗', error)
      errorMessage.value = getApiErrorMessage(error, '通知標記已讀失敗')
    }
  }

  async function readAllNotifications() {
    try {
      await markAllMyNotificationsAsRead()

      notifications.value = notifications.value.map((item) => ({
        ...item,
        isRead: true,
      }))

      unreadCount.value = 0
    } catch (error) {
      logApiError('全部通知標記已讀失敗', error)
      errorMessage.value = getApiErrorMessage(error, '全部通知標記已讀失敗')
    }
  }

  async function deleteNotification(notificationId) {
    if (!notificationId) return

    try {
      const target = notifications.value.find((item) => item.notificationId === notificationId)

      await deleteNotificationApi(notificationId)

      notifications.value = notifications.value.filter(
        (item) => item.notificationId !== notificationId,
      )

      if (target && !target.isRead) {
        unreadCount.value = Math.max(unreadCount.value - 1, 0)
      }
    } catch (error) {
      logApiError('刪除通知失敗', error)
      errorMessage.value = getApiErrorMessage(error, '刪除通知失敗')
    }
  }

  // 中文註解：removeNotification 為 deleteNotification 的舊版相容名稱。
  async function removeNotification(notificationId) {
    return await deleteNotification(notificationId)
  }

  return {
    notifications,
    unreadCount,
    loading,
    errorMessage,
    hasUnread,
    markAsRead: readNotification,
    loadNotifications,
    fetchNotifications,
    loadUnreadCount,
    fetchUnreadCount,
    readNotification,
    readAllNotifications,
    deleteNotification,
    removeNotification,
  }
})
