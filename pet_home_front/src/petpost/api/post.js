import api from './http'

export function findAllPosts() {
  return api.get('/posts')
}

export function findHotPosts() {
  return api.get('/posts/hot')
}

export function findPostById(postId) {
  return api.get(`/posts/${postId}`)
}

export function createPost(dto) {
  return api.post('/posts', dto)
}

export function updatePost(postId, dto) {
  return api.put(`/posts/${postId}`, dto)
}

export function deletePost(postId) {
  return api.delete(`/posts/${postId}`)
}
