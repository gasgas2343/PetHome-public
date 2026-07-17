import { defineStore } from "pinia";
import { ref } from "vue";

import {
  findAllTimelines,
  findTimelinesByPetId,
  findTimelineById,
  createTimeline,
  updateTimeline,
  deleteTimeline,
} from "@/petpost/api/timeline";

import { getDefaultCloudinaryImage, normalizeCloudinaryImageUrl } from "@/petpost/data/cloudinaryImages";

function resolveApiBaseUrl() {
  return (import.meta.env.VITE_API_BASE_URL || `${import.meta.env.VITE_API_BASE_URL}`).replace(/\/$/, '')
}

function normalizeTimelineImageUrl(url) {
  if (!url) {
    return getDefaultCloudinaryImage("timeline")
  }

  const trimmedUrl = String(url).trim()

  if (!trimmedUrl) {
    return getDefaultCloudinaryImage("timeline")
  }

  // 中文註解：後端本地圖片會回傳 /uploads/xxx。
  // 因為 Spring Boot 有 /api context-path，所以前端需補成 ${import.meta.env.VITE_API_BASE_URL}/uploads/xxx。
  if (trimmedUrl.startsWith('/uploads/')) {
    return `${resolveApiBaseUrl()}${trimmedUrl}`
  }

  // 中文註解：相容舊資料若已存成 http://localhost:8080/uploads/xxx，統一補回 /api。
  if (trimmedUrl.startsWith('http://localhost:8080/uploads/')) {
    return trimmedUrl.replace('http://localhost:8080/uploads/', `${resolveApiBaseUrl()}/uploads/`)
  }

  // 雲端圖片才走原本 Cloudinary 正規化
  return normalizeCloudinaryImageUrl(trimmedUrl, "timeline")
}

function normalizeTimeline(item, index = 0, petId = 1) {
  return {
    ...item,
    petPostId: item.petPostId ?? item.id ?? index + 1,
    petId: item.petId ?? petId,
    title: item.title ?? "毛孩回憶",
    content: item.content ?? "",
    postDate: item.postDate ?? item.createdAt?.slice?.(0, 10) ?? "",
    imageUrl: normalizeTimelineImageUrl(item.imageUrl),
  };
}

function toTimelineArray(data, petId = 1) {
  if (!Array.isArray(data)) {
    console.warn("時間軸 API 回傳格式不是陣列，已改成空陣列：", data);
    return [];
  }

  return data
    .filter((item) => item && typeof item === "object")
    .map((item, index) => normalizeTimeline(item, index, petId));
}

/**
 * 寵物時間軸 Store
 */
export const useTimelineStore = defineStore("timeline", () => {
  const timelines = ref([]);
  const selectedTimeline = ref(null);
  const loading = ref(false);
  const errorMessage = ref("");

  async function loadTimelines() {
    loading.value = true;
    errorMessage.value = "";

    try {
      const response = await findAllTimelines();
      timelines.value = toTimelineArray(response.data);
    } catch (error) {
      console.error("查詢時間軸失敗", error);
      errorMessage.value =
        "查詢時間軸失敗";
      timelines.value = [];
    } finally {
      loading.value = false;
    }
  }

  async function loadTimelinesByPetId(petId) {
    loading.value = true;
    errorMessage.value = "";

    try {
      const response = await findTimelinesByPetId(petId);
      timelines.value = toTimelineArray(response.data, petId);
    } catch (error) {
      console.error("查詢毛孩時間軸失敗", error);
      errorMessage.value =
        error.response?.data?.message || "查詢毛孩時間軸失敗";
      timelines.value = [];
    } finally {
      loading.value = false;
    }
  }

  async function loadTimelineById(petPostId) {
    loading.value = true;
    errorMessage.value = "";

    try {
      const response = await findTimelineById(petPostId);
      selectedTimeline.value =
        response.data && typeof response.data === "object"
          ? normalizeTimeline(response.data)
          : null;
    } catch (error) {
      console.error("查詢單一時間軸失敗", error);
      errorMessage.value = "查詢單一時間軸失敗";
      selectedTimeline.value = null;
    } finally {
      loading.value = false;
    }
  }

  async function addTimeline(dto) {
    loading.value = true;
    errorMessage.value = "";

    const payload = {
      ...dto,
      imageUrl: normalizeTimelineImageUrl(dto.imageUrl),
    };

    try {
      await createTimeline(payload);
      await loadTimelinesByPetId(payload.petId);
    } catch (error) {
      console.error("新增時間軸失敗", error);
      errorMessage.value =
        error.response?.data?.message || "新增時間軸失敗，請確認資料是否正確。";
      throw error;
    } finally {
      loading.value = false;
    }
  }

  async function editTimeline(petPostId, dto) {
    loading.value = true;
    errorMessage.value = "";

    const payload = {
      ...dto,
      imageUrl: normalizeTimelineImageUrl(dto.imageUrl),
    };

    try {
      await updateTimeline(petPostId, payload);
      if (payload.petId) {
        await loadTimelinesByPetId(payload.petId);
      } else {
        await loadTimelines();
      }
    } catch (error) {
      console.error("修改時間軸失敗", error);
      errorMessage.value = error.response?.data?.message || "修改時間軸失敗";
      throw error;
    } finally {
      loading.value = false;
    }
  }

  async function removeTimeline(petPostId, petId) {
    loading.value = true;
    errorMessage.value = "";

    try {
      await deleteTimeline(petPostId);
      if (petId) {
        await loadTimelinesByPetId(petId);
      } else {
        await loadTimelines();
      }
    } catch (error) {
      console.error("刪除時間軸失敗", error);
      errorMessage.value = error.response?.data?.message || "刪除時間軸失敗";
      throw error;
    } finally {
      loading.value = false;
    }
  }

  function clearSelectedTimeline() {
    selectedTimeline.value = null;
  }

  return {
    timelines,
    selectedTimeline,
    loading,
    errorMessage,
    loadTimelines,
    loadTimelinesByPetId,
    loadTimelineById,
    addTimeline,
    editTimeline,
    removeTimeline,
    clearSelectedTimeline,
  };
});
