// 匯入 axios 實例
// 注意：這裡假設你已經有 src/api/axios.js
import api from './http';

/**
 * 查詢全部時間軸
 *
 * 對應後端：
 * GET /api/pet-posts
 */
export function findAllTimelines() {
  return api.get("/pet-posts");
}

/**
 * 查詢某一隻毛孩的時間軸
 *
 * 對應後端：
 * GET /api/pet-posts/pet/{petId}
 */
export function findTimelinesByPetId(petId) {
  return api.get(`/pet-posts/pet/${petId}`);
}

/**
 * 查詢單一時間軸
 *
 * 對應後端：
 * GET /api/pet-posts/{petPostId}
 */
export function findTimelineById(petPostId) {
  return api.get(`/pet-posts/${petPostId}`);
}

/**
 * 新增時間軸
 *
 * 對應後端：
 * POST /api/pet-posts
 *
 * dto 範例：
 * {
 *   petId: 1,
 *   title: "第一次洗澡",
 *   content: "今天第一次洗澡",
 *   postDate: "2026-06-12",
 *   imageUrl: "https://res.cloudinary.com/..."
 * }
 */
export function createTimeline(dto) {
  return api.post("/pet-posts", dto);
}

/**
 * 修改時間軸
 *
 * 對應後端：
 * PUT /api/pet-posts/{petPostId}
 */
export function updateTimeline(petPostId, dto) {
  return api.put(`/pet-posts/${petPostId}`, dto);
}

/**
 * 刪除時間軸
 *
 * 對應後端：
 * DELETE /api/pet-posts/{petPostId}
 */
export function deleteTimeline(petPostId) {
  return api.delete(`/pet-posts/${petPostId}`);
}
