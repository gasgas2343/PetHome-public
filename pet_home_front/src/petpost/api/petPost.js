import api from './http';

// 查詢全部回憶錄
export function findAllPetPosts() {
  return api.get("/pet-posts");
}

// 查詢單篇回憶錄
export function findPetPostById(petPostId) {
  return api.get(`/pet-posts/${petPostId}`);
}

// 新增回憶錄
export function createPetPost(dto) {
  return api.post(
    "/pet-posts",

    dto,
  );
}

// 修改回憶錄
export function updatePetPost(petPostId, dto) {
  return api.put(
    `/pet-posts/${petPostId}`,

    dto,
  );
}

// 刪除回憶錄
export function deletePetPost(petPostId) {
  return api.delete(`/pet-posts/${petPostId}`);
}
