// src/petpost/stores/adminCommentStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  findAllAdminComments,
  hideAdminComment,
  restoreAdminComment,
  deleteAdminComment,
} from '@/petpost/api/adminComment'
import { getApiErrorMessage, logApiError, unwrapApiList } from '@/petpost/utils/apiErrorHandler'

export const useAdminCommentStore = defineStore('adminComment', () => {
  const comments = ref([])
  const loading = ref(false)
  const errorMessage = ref('')

  async function loadComments() {
    loading.value = true
    errorMessage.value = ''

    try {
      const response = await findAllAdminComments()
      comments.value = unwrapApiList(response)
    } catch (error) {
      logApiError('查詢留言失敗', error)
      comments.value = []
      errorMessage.value = getApiErrorMessage(error, '查詢留言失敗')
    } finally {
      loading.value = false
    }
  }

  async function hideComment(commentId) {
    loading.value = true
    errorMessage.value = ''

    try {
      await hideAdminComment(commentId)
      await loadComments()
    } catch (error) {
      logApiError('隱藏留言失敗', error)
      errorMessage.value = getApiErrorMessage(error, '隱藏留言失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  async function restoreComment(commentId) {
    loading.value = true
    errorMessage.value = ''

    try {
      await restoreAdminComment(commentId)
      await loadComments()
    } catch (error) {
      logApiError('恢復留言失敗', error)
      errorMessage.value = getApiErrorMessage(error, '恢復留言失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  async function deleteComment(commentId) {
    const result = confirm('確定要刪除這則留言嗎？')

    if (!result) {
      return
    }

    loading.value = true
    errorMessage.value = ''

    try {
      await deleteAdminComment(commentId)
      await loadComments()
    } catch (error) {
      logApiError('刪除留言失敗', error)
      errorMessage.value = getApiErrorMessage(error, '刪除留言失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    comments,
    loading,
    errorMessage,
    loadComments,
    hideComment,
    restoreComment,
    deleteComment,
  }
})
