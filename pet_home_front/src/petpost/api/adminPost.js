import api from './http'

// 查詢全部文章，對應後端 GET /api/admin/forum/posts
export function findAllAdminPosts() {
  return api.get('/admin/forum/posts')
}

// 下架文章，對應後端 PUT /api/posts/{postId}/lock
export function disablePost(postId) {
  return api.put(`/posts/${postId}/lock`)
}

// 恢復文章，對應後端 PUT /api/posts/{postId}/unlock
export function enablePost(postId) {
  return api.put(`/posts/${postId}/unlock`)
}

// 管理員強制刪除文章，對應後端 DELETE /api/posts/{postId}/admin
export function deletePost(postId) {
  return api.delete(`/posts/${postId}/admin`)
}