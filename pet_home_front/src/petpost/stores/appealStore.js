// src/petpost/stores/appealStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

import { createAppeal as createAppealApi } from '@/petpost/api/appeal'
import { useForumAuth } from '@/petpost/composables/useForumAuth'
import { getApiErrorMessage, logApiError, unwrapApiData } from '@/petpost/utils/apiErrorHandler'

export const useAppealStore = defineStore('appeal', () => {
  const showDialog = ref(false)
  const loading = ref(false)
  const errorMessage = ref('')

  const { isLoggedIn } = useForumAuth()

  function openDialog() {
    showDialog.value = true
  }

  function closeDialog() {
    showDialog.value = false
  }

  // 中文註解：統一命名，新增申訴。
  async function createAppeal(payload) {
    loading.value = true
    errorMessage.value = ''

    try {
      if (!isLoggedIn.value) {
        throw new Error('請先登入後再申訴')
      }

      if (!payload.reportId && !payload.targetId) {
        throw new Error('缺少申訴目標')
      }

      if (!payload.reason?.trim()) {
        throw new Error('請輸入申訴原因')
      }

      const dto = {
        reportId: payload.reportId ? Number(payload.reportId) : null,
        targetType: payload.targetType || null,
        targetId: payload.targetId ? Number(payload.targetId) : null,
        appealType: payload.appealType || payload.targetType || 'POST',
        subject: payload.subject,
        reason: payload.reason,
        imageUrl: payload.imageUrl || null,
      }

      const response = await createAppealApi(dto)
      closeDialog()

      return unwrapApiData(response)
    } catch (error) {
      logApiError('申訴送出失敗', error)
      errorMessage.value = getApiErrorMessage(error, '申訴送出失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 中文註解：addAppeal 為 createAppeal 的舊版相容名稱。
  async function addAppeal(payload) {
    return await createAppeal(payload)
  }

  return {
    showDialog,
    loading,
    errorMessage,
    openDialog,
    closeDialog,
    createAppeal,
    addAppeal,
  }
})
