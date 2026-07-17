import api from './http'

export function favoritePost(postId) {
  return api.post(`/post-favorites/${postId}`)
}

export function unfavoritePost(postId) {
  return api.delete(`/post-favorites/${postId}`)
}

export function isPostFavorited(postId) {
  return api.get(`/post-favorites/${postId}/status`)
}