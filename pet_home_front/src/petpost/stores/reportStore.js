// src/petpost/stores/reportStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

import { createReport as createReportApi } from '@/petpost/api/report'
import { useForumAuth } from '@/petpost/composables/useForumAuth'
import { getApiErrorMessage, logApiError, unwrapApiData } from '@/petpost/utils/apiErrorHandler'

export const useReportStore = defineStore('report', () => {
  const showDialog = ref(false)
  const loading = ref(false)
  const errorMessage = ref('')

  const { isLoggedIn, loginUserId } = useForumAuth()

  function openDialog() {
    showDialog.value = true
  }

  function closeDialog() {
    showDialog.value = false
  }

  // 中文註解：統一命名，新增檢舉。targetType 可為 POST 或 COMMENT。
  async function createReport(payload) {
    loading.value = true
    errorMessage.value = ''

    try {
      const reporterId = loginUserId.value

      if (!isLoggedIn.value) {
        throw new Error('請先登入後再檢舉')
      }

      if (!payload.reportedUserId) {
        throw new Error('缺少被檢舉人的 userId')
      }

      if (!payload.targetId) {
        throw new Error('缺少 targetId')
      }

      if (Number(reporterId) === Number(payload.reportedUserId)) {
        throw new Error('不可檢舉自己')
      }

      const dto = {
        reportedUserId: Number(payload.reportedUserId),
        postId: payload.targetType === 'POST' ? Number(payload.targetId) : null,
        commentId: payload.targetType === 'COMMENT' ? Number(payload.targetId) : null,
        reason: payload.reason,
      }

      const response = await createReportApi(dto)
      closeDialog()

      return unwrapApiData(response)
    } catch (error) {
      logApiError('檢舉失敗', error)
      errorMessage.value = getApiErrorMessage(error, '檢舉失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 中文註解：addReport 為 createReport 的舊版相容名稱。
  async function addReport(payload) {
    return await createReport(payload)
  }

  return {
    showDialog,
    loading,
    errorMessage,
    openDialog,
    closeDialog,
    createReport,
    addReport,
  }
})
