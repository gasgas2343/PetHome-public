// Pinia
import { defineStore } from "pinia";

// Vue
import { ref } from "vue";

// API
import {
  findAllPetPosts,
  findPetPostById,
  createPetPost,
  updatePetPost,
  deletePetPost
} from "@/petpost/api/petPost";

export const usePetPostStore = defineStore("petPost", () => {
  // 中文註解：回憶錄列表
  const petPosts = ref([]);

  // 中文註解：目前選中的單篇回憶錄
  const currentPetPost = ref(null);

  // 中文註解：載入狀態
  const loading = ref(false);

  // 中文註解：錯誤訊息
  const error = ref("");

  // 中文註解：支援後端回傳 ApiResponse<T> 或直接回傳資料
  function unwrapApiData(response) {
    return response?.data?.data ?? response?.data ?? null;
  }

  // 中文註解：查詢全部回憶錄
  async function loadPetPosts() {
    loading.value = true;
    error.value = "";

    try {
      const response = await findAllPetPosts();
      petPosts.value = unwrapApiData(response) ?? [];
    } catch (err) {
      console.error("查詢回憶錄失敗：", err);
      error.value = "查詢回憶錄失敗";
      petPosts.value = [];
    } finally {
      loading.value = false;
    }
  }

  // 中文註解：查詢單篇回憶錄
  async function loadPetPostById(petPostId) {
    loading.value = true;
    error.value = "";

    try {
      const response = await findPetPostById(petPostId);
      currentPetPost.value = unwrapApiData(response);
      return currentPetPost.value;
    } catch (err) {
      console.error("查詢單篇回憶錄失敗：", err);
      error.value = "查詢單篇回憶錄失敗";
      currentPetPost.value = null;
      return null;
    } finally {
      loading.value = false;
    }
  }

  // 中文註解：新增回憶錄
  async function addPetPost(dto) {
    loading.value = true;
    error.value = "";

    try {
      const response = await createPetPost(dto);
      const created = unwrapApiData(response);

      if (created) {
        petPosts.value.unshift(created);
      } else {
        await loadPetPosts();
      }

      return created;
    } catch (err) {
      console.error("新增回憶錄失敗：", err);
      error.value = "新增時間軸失敗，請確認資料是否正確。";
      throw err;
    } finally {
      loading.value = false;
    }
  }

  // 中文註解：修改回憶錄
  async function editPetPost(petPostId, dto) {
    loading.value = true;
    error.value = "";

    try {
      const response = await updatePetPost(petPostId, dto);
      const updated = unwrapApiData(response);

      petPosts.value = petPosts.value.map((post) =>
        post.petPostId === petPostId ? { ...post, ...updated } : post
      );

      return updated;
    } catch (err) {
      console.error("修改回憶錄失敗：", err);
      error.value = "修改回憶錄失敗";
      throw err;
    } finally {
      loading.value = false;
    }
  }

  // 中文註解：刪除回憶錄
  async function removePetPost(petPostId) {
    loading.value = true;
    error.value = "";

    try {
      await deletePetPost(petPostId);

      petPosts.value = petPosts.value.filter(
        (post) => post.petPostId !== petPostId
      );
    } catch (err) {
      console.error("刪除回憶錄失敗：", err);
      error.value = "刪除回憶錄失敗";
      throw err;
    } finally {
      loading.value = false;
    }
  }

  return {
    petPosts,
    currentPetPost,
    loading,
    error,
    loadPetPosts,
    loadPetPostById,
    addPetPost,
    editPetPost,
    removePetPost
  };
});