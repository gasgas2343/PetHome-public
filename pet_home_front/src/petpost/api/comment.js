import api from './http'

export function findCommentsByPostId(postId) {
  return api.get(`/comments/post/${postId}`)
}

export function findCommentById(commentId) {
  return api.get(`/comments/${commentId}`)
}

export function findReplies(commentId) {
  return api.get(`/comments/${commentId}/replies`)
}

export function createComment(dto) {
  return api.post('/comments', dto)
}

export function updateComment(commentId, dto) {
  return api.put(`/comments/${commentId}`, dto)
}

export function deleteComment(commentId) {
  return api.delete(`/comments/${commentId}`)
}
