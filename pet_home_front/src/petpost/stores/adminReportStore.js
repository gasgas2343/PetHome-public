// src/petpost/stores/adminReportStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

import {
  findPendingReports,
  approveReport as approveReportApi,
  rejectReport as rejectReportApi,
} from '@/petpost/api/adminReport'
import { getApiErrorMessage, logApiError, unwrapApiList } from '@/petpost/utils/apiErrorHandler'

export const useAdminReportStore = defineStore('adminReport', () => {
  const reports = ref([])
  const loading = ref(false)
  const errorMessage = ref('')

  async function loadReports() {
    loading.value = true
    errorMessage.value = ''

    try {
      const response = await findPendingReports()
      reports.value = unwrapApiList(response)
    } catch (error) {
      logApiError('查詢檢舉失敗', error)
      reports.value = []
      errorMessage.value = getApiErrorMessage(error, '查詢檢舉失敗')
    } finally {
      loading.value = false
    }
  }

  async function approveReport(reportId) {
    loading.value = true
    errorMessage.value = ''

    try {
      await approveReportApi(reportId, '檢舉成立')
      await loadReports()
    } catch (error) {
      logApiError('通過檢舉失敗', error)
      errorMessage.value = getApiErrorMessage(error, '通過檢舉失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  async function rejectReport(reportId) {
    loading.value = true
    errorMessage.value = ''

    try {
      await rejectReportApi(reportId, '檢舉駁回')
      await loadReports()
    } catch (error) {
      logApiError('駁回檢舉失敗', error)
      errorMessage.value = getApiErrorMessage(error, '駁回檢舉失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    reports,
    loading,
    errorMessage,
    loadReports,
    approveReport,
    rejectReport,
  }
})
