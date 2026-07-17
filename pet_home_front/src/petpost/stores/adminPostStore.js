// src/petpost/stores/adminPostStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

import {
  findAllAdminPosts,
  disablePost as apiDisablePost,
  enablePost as apiEnablePost,
  deletePost as apiDeletePost,
} from '@/petpost/api/adminPost'
import { getApiErrorMessage, logApiError, unwrapApiList } from '@/petpost/utils/apiErrorHandler'

export const useAdminPostStore = defineStore('adminPost', () => {
  const posts = ref([])
  const loading = ref(false)
  const errorMessage = ref('')

  async function loadPosts() {
    loading.value = true
    errorMessage.value = ''

    try {
      const response = await findAllAdminPosts()
      posts.value = unwrapApiList(response)
    } catch (error) {
      logApiError('後台文章查詢失敗', error)
      posts.value = []
      errorMessage.value = getApiErrorMessage(error, '後台文章查詢失敗')
    } finally {
      loading.value = false
    }
  }

  // 中文註解：統一命名，隱藏 / 下架文章。
  async function hidePost(postId) {
    loading.value = true
    errorMessage.value = ''

    try {
      await apiDisablePost(postId)
      await loadPosts()
    } catch (error) {
      logApiError('文章下架失敗', error)
      errorMessage.value = getApiErrorMessage(error, '文章下架失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 中文註解：disablePost 為 hidePost 的舊版相容名稱。
  async function disablePost(postId) {
    return await hidePost(postId)
  }

  // 中文註解：統一命名，恢復文章。
  async function restorePost(postId) {
    loading.value = true
    errorMessage.value = ''

    try {
      await apiEnablePost(postId)
      await loadPosts()
    } catch (error) {
      logApiError('文章恢復失敗', error)
      errorMessage.value = getApiErrorMessage(error, '文章恢復失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 中文註解：enablePost 為 restorePost 的舊版相容名稱。
  async function enablePost(postId) {
    return await restorePost(postId)
  }

  async function deletePost(postId) {
    const result = confirm('確定刪除這篇文章？')

    if (!result) {
      return
    }

    loading.value = true
    errorMessage.value = ''

    try {
      await apiDeletePost(postId)
      posts.value = posts.value.filter((post) => Number(post.postId) !== Number(postId))
    } catch (error) {
      logApiError('文章刪除失敗', error)
      errorMessage.value = getApiErrorMessage(error, '文章刪除失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    posts,
    loading,
    errorMessage,
    loadPosts,
    hidePost,
    restorePost,
    disablePost,
    enablePost,
    deletePost,
  }
})
