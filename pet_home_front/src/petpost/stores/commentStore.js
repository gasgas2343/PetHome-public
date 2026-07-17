// src/petpost/stores/commentStore.js
// 中文註解：管理文章留言資料。
import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  findCommentsByPostId,
  createComment as createCommentApi,
  deleteComment as apiDeleteComment,
} from '@/petpost/api/comment'
import { getApiErrorMessage, logApiError, unwrapApiList } from '@/petpost/utils/apiErrorHandler'

export const useCommentStore = defineStore('comment', () => {
  const comments = ref([])
  const loading = ref(false)
  const errorMessage = ref('')

  async function loadComments(postId) {
    loading.value = true
    errorMessage.value = ''

    try {
      const response = await findCommentsByPostId(postId)
      comments.value = unwrapApiList(response)
    } catch (error) {
      logApiError('留言查詢失敗', error)
      comments.value = []
      errorMessage.value = getApiErrorMessage(error, '留言查詢失敗')
    } finally {
      loading.value = false
    }
  }

  async function createComment(dto) {
    loading.value = true
    errorMessage.value = ''

    try {
      await createCommentApi(dto)
      await loadComments(dto.postId)
    } catch (error) {
      logApiError('新增留言失敗', error)
      errorMessage.value = getApiErrorMessage(error, '新增留言失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 中文註解：addComment 為 createComment 的舊版相容名稱。
  async function addComment(dto) {
    return await createComment(dto)
  }

  async function deleteComment(commentId) {
    loading.value = true
    errorMessage.value = ''

    try {
      await apiDeleteComment(commentId)
      comments.value = comments.value.filter((item) => Number(item.commentId) !== Number(commentId))
    } catch (error) {
      logApiError('刪除留言失敗', error)
      errorMessage.value = getApiErrorMessage(error, '刪除留言失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 中文註解：removeComment 為 deleteComment 的舊版相容名稱。
  async function removeComment(commentId) {
    return await deleteComment(commentId)
  }

  return {
    comments,
    loading,
    errorMessage,
    loadComments,
    createComment,
    addComment,
    deleteComment,
    removeComment,
  }
})
