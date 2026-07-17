import api from "./http";

// 留言按讚
export function likeComment(commentId) {
  return api.post(`/comment-likes/${commentId}`);
}

// 取消按讚
export function unlikeComment(commentId) {
  return api.delete(`/comment-likes/${commentId}`);
}

// 是否已按讚
export function isCommentLiked(commentId) {
  return api.get(`/comment-likes/${commentId}/status`);
}

// 按讚數
export function countCommentLikes(commentId) {
  return api.get(`/comment-likes/${commentId}/count`);
}