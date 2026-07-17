import { defineStore } from 'pinia'
import { ref } from 'vue'

import { countPostLikes, isPostLiked, likePost, unlikePost } from '@/petpost/api/like'

import { countCommentLikes, isCommentLiked, likeComment, unlikeComment } from '@/petpost/api/commentLike'

export const useLikeStore = defineStore('like', () => {
  const postLikeMap = ref({})
  const commentLikeMap = ref({})

  async function loadPostLike(postId) {
    try {
      const countResponse = await countPostLikes(postId)
      const likedResponse = await isPostLiked(postId)

      postLikeMap.value[postId] = {
        count: countResponse.data,
        liked: likedResponse.data,
      }
    } catch (error) {
      console.error('載入文章按讚資料失敗', error)

      postLikeMap.value[postId] = {
        count: 0,
        liked: false,
      }
    }
  }

  async function togglePost(postId) {
    try {
      const current = postLikeMap.value[postId]?.liked

      if (current) {
        await unlikePost(postId)
      } else {
        await likePost(postId)
      }

      await loadPostLike(postId)
    } catch (error) {
      console.error('切換文章按讚失敗', error)
      throw error
    }
  }

  async function loadCommentLike(commentId) {
    try {
      const countResponse = await countCommentLikes(commentId)
      const likedResponse = await isCommentLiked(commentId)

      commentLikeMap.value[commentId] = {
        count: countResponse.data,
        liked: likedResponse.data,
      }
    } catch (error) {
      console.error('載入留言按讚資料失敗', error)

      commentLikeMap.value[commentId] = {
        count: 0,
        liked: false,
      }
    }
  }

  async function toggleComment(commentId) {
    try {
      const current = commentLikeMap.value[commentId]?.liked

      if (current) {
        await unlikeComment(commentId)
      } else {
        await likeComment(commentId)
      }

      await loadCommentLike(commentId)
    } catch (error) {
      console.error('切換留言按讚失敗', error)
      throw error
    }
  }

  return {
    postLikeMap,
    commentLikeMap,
    loadPostLike,
    togglePost,
    loadCommentLike,
    toggleComment,
  }
})
