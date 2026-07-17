// src/petpost/stores/postStore.js
// Pinia 文章 Store：統一處理文章 API、圖片網址、標籤資料格式。
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import {
  findAllPosts,
  findPostById,
  createPost as createPostApi,
  updatePost as updatePostApi,
  deletePost as deletePostApi,
  findHotPosts,
} from '@/petpost/api/post'

import { filterPostsByKeyword } from '@/petpost/utils/postSearch'
import { normalizeCloudinaryImageUrl } from '@/petpost/data/cloudinaryImages'
import {
  getApiErrorMessage,
  logApiError,
  unwrapApiData,
  unwrapApiList,
} from '@/petpost/utils/apiErrorHandler'

// 中文註解：把後端回傳的 tags 統一轉成字串陣列。
function normalizeTags(tags) {
  if (!Array.isArray(tags)) {
    return []
  }

  return tags
    .map((tag) => {
      if (typeof tag === 'string') {
        return tag.trim()
      }

      if (tag && typeof tag === 'object') {
        return (tag.tagName ?? tag.name ?? '').trim()
      }

      return ''
    })
    .filter(Boolean)
}

function normalizePost(item) {
  return {
    ...item,

    // 中文註解：後端可能回傳 coverImageUrl 或 imageUrl，前端統一使用 coverImageUrl。
    coverImageUrl: normalizeCloudinaryImageUrl(item?.coverImageUrl || item?.imageUrl, 'post'),

    // 中文註解：文章標籤統一轉成 ['寵物心得', '領養送養'] 這種格式，方便 CommunityView 篩選。
    tags: normalizeTags(item?.tags),
  }
}

function normalizePostDto(dto) {
  return {
    ...dto,
    coverImageUrl: normalizeCloudinaryImageUrl(dto?.coverImageUrl, 'post'),
    tags: Array.isArray(dto?.tags) ? dto.tags : [],
  }
}

export const usePostStore = defineStore('post', () => {
  const posts = ref([])
  const post = ref(null)
  const keyword = ref('')
  const hotPosts = ref([])
  const loading = ref(false)
  const errorMessage = ref('')

  const filteredPosts = computed(() => {
    return filterPostsByKeyword(posts.value, keyword.value)
  })

  function setKeyword(value) {
    keyword.value = value ?? ''
  }

  async function loadPosts() {
    loading.value = true
    errorMessage.value = ''

    try {
      const response = await findAllPosts()
      posts.value = unwrapApiList(response).map(normalizePost)
    } catch (error) {
      logApiError('文章查詢失敗', error)
      posts.value = []
      errorMessage.value = getApiErrorMessage(error, '文章查詢失敗')
    } finally {
      loading.value = false
    }
  }

  async function loadPostById(postId) {
    loading.value = true
    errorMessage.value = ''

    try {
      const response = await findPostById(postId)
      const data = unwrapApiData(response)
      post.value = data && typeof data === 'object' ? normalizePost(data) : null
    } catch (error) {
      logApiError('單篇文章查詢失敗', error)
      post.value = null
      errorMessage.value = getApiErrorMessage(error, '單篇文章查詢失敗')
    } finally {
      loading.value = false
    }
  }

  // 中文註解：統一命名，查詢單一文章。保留 loadPostById 供舊程式使用。
  async function loadPost(postId) {
    return await loadPostById(postId)
  }

  async function loadHotPosts() {
    try {
      const response = await findHotPosts()
      hotPosts.value = unwrapApiList(response).map(normalizePost)
    } catch (error) {
      logApiError('熱門文章查詢失敗', error)
      hotPosts.value = []
    }
  }

  // 中文註解：舊名稱 addPost 保留，實際新增文章邏輯集中於 createPost。
  async function createPost(dto) {
    loading.value = true
    errorMessage.value = ''

    try {
      await createPostApi(normalizePostDto(dto))
      await loadPosts()
    } catch (error) {
      logApiError('新增文章失敗', error)
      errorMessage.value = getApiErrorMessage(error, '新增文章失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  async function addPost(dto) {
    return await createPost(dto)
  }

  async function updatePost(postId, dto) {
    loading.value = true
    errorMessage.value = ''

    try {
      await updatePostApi(postId, normalizePostDto(dto))
      await loadPostById(postId)
      await loadPosts()
    } catch (error) {
      logApiError('修改文章失敗', error)
      errorMessage.value = getApiErrorMessage(error, '修改文章失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  async function deletePost(postId) {
    loading.value = true
    errorMessage.value = ''

    try {
      await deletePostApi(postId)
      posts.value = posts.value.filter((item) => Number(item.postId ?? item.id) !== Number(postId))
      hotPosts.value = hotPosts.value.filter((item) => Number(item.postId ?? item.id) !== Number(postId))

      if (Number(post.value?.postId ?? post.value?.id) === Number(postId)) {
        post.value = null
      }
    } catch (error) {
      logApiError('刪除文章失敗', error)
      errorMessage.value = getApiErrorMessage(error, '刪除文章失敗')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 中文註解：removePost 為 deletePost 的相容別名。
  async function removePost(postId) {
    return await deletePost(postId)
  }

  return {
    post,
    posts,
    keyword,
    hotPosts,
    loading,
    errorMessage,
    filteredPosts,

    setKeyword,
    loadPosts,
    loadPost,
    loadPostById,
    loadHotPosts,
    createPost,
    addPost,
    updatePost,
    deletePost,
    removePost,
  }
})
