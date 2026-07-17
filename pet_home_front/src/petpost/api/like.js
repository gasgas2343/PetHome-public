import api from './http'

export function likePost(postId) {
  return api.post(`/post-likes/${postId}`)
}

export function unlikePost(postId) {
  return api.delete(`/post-likes/${postId}`)
}

export function isPostLiked(postId) {
  return api.get(`/post-likes/${postId}/status`)
}

export function countPostLikes(postId) {
  return api.get(`/post-likes/${postId}/count`)
}