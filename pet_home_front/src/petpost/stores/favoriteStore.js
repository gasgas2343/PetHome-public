import { defineStore } from 'pinia'
import { ref } from 'vue'

import { favoritePost, unfavoritePost, isPostFavorited } from '@/petpost/api/favorite'

export const useFavoriteStore = defineStore('favorite', () => {
  const favoriteMap = ref({})

  async function loadFavorite(postId) {
    try {
      const response = await isPostFavorited(postId)

      favoriteMap.value[postId] = {
        favorited: response.data,
      }
    } catch (error) {
      console.log('收藏狀態載入失敗', error)

      favoriteMap.value[postId] = {
        favorited: false,
      }
    }
  }

  async function toggle(postId) {
    try {
      const current = favoriteMap.value[postId]?.favorited

      if (current) {
        await unfavoritePost(postId)
      } else {
        await favoritePost(postId)
      }

      await loadFavorite(postId)
    } catch (error) {
      console.log('切換收藏失敗', error)
      throw error
    }
  }

  return {
    favoriteMap,
    loadFavorite,
    toggle,
  }
})
