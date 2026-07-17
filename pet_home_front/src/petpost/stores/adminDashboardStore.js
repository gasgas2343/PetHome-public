// src/petpost/stores/adminDashboardStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { findAdminDashboardSummary } from '@/petpost/api/adminDashboard'
import { getApiErrorMessage, logApiError, unwrapApiData } from '@/petpost/utils/apiErrorHandler'

export const useAdminDashboardStore = defineStore('adminDashboard', () => {
  const summary = ref({
    postCount: 0,
    userCount: 0,
    pendingReportCount: 0,
    pendingAppealCount: 0,
  })

  const loading = ref(false)
  const errorMessage = ref('')

  async function loadSummary() {
    loading.value = true
    errorMessage.value = ''

    try {
      const response = await findAdminDashboardSummary()
      const data = unwrapApiData(response, {})

      summary.value = {
        postCount: data?.postCount ?? 0,
        userCount: data?.userCount ?? 0,
        pendingReportCount: data?.pendingReportCount ?? 0,
        pendingAppealCount: data?.pendingAppealCount ?? 0,
      }
    } catch (error) {
      logApiError('管理首頁統計查詢失敗', error)
      errorMessage.value = getApiErrorMessage(error, '管理首頁統計查詢失敗')
    } finally {
      loading.value = false
    }
  }

  return {
    summary,
    loading,
    errorMessage,
    loadSummary,
  }
})
