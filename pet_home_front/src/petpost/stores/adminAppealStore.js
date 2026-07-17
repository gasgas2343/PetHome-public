// src/petpost/stores/adminAppealStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

import {
  findPendingAppeals,
  approveAppeal as approveAppealApi,
  rejectAppeal as rejectAppealApi,
} from '@/petpost/api/adminAppeal'
import { getApiErrorMessage, logApiError, unwrapApiList } from '@/petpost/utils/apiErrorHandler'

export const useAdminAppealStore = defineStore('adminAppeal', () => {
  const appeals = ref([])
  const loading = ref(false)
  const errorMessage = ref('')

  async function loadAppeals() {
    loading.value = true
    errorMessage.value = ''

    try {
      const response = await findPendingAppeals()
      appeals.value = unwrapApiList(response)
    } catch (error) {
      logApiError('查詢申訴失敗', error)
      appeals.value = []
      errorMessage.value = getApiErrorMessage(error, '查詢申訴失敗')
    } finally {
      loading.value = false
    }
  }

  async function approveAppeal(appealId) {
    loading.value = true
    errorMessage.value = ''

    try {
      await approveAppealApi(appealId, '申訴通過')
      await loadAppeals()
    } catch (error) {
      logApiError('通過申訴失敗', error)
      errorMessage.value = getApiErrorMessage(error, '通過申訴失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  async function rejectAppeal(appealId) {
    loading.value = true
    errorMessage.value = ''

    try {
      await rejectAppealApi(appealId, '申訴駁回')
      await loadAppeals()
    } catch (error) {
      logApiError('駁回申訴失敗', error)
      errorMessage.value = getApiErrorMessage(error, '駁回申訴失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    appeals,
    loading,
    errorMessage,
    loadAppeals,
    approveAppeal,
    rejectAppeal,
  }
})
