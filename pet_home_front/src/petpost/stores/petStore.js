import { normalizeCloudinaryImageUrl } from "@/petpost/data/cloudinaryImages";

import { defineStore } from "pinia";
import { ref } from "vue";

import {
  findMyPets,
  createPet,
  updatePetApi,
  deletePetApi,
} from "@/petpost/api/pet";

import { userAuthStore } from "@/stores/auth";

// 毛孩資料 Store：串接後端 API
export const usePetStore = defineStore("pet", () => {
  const pets = ref([]);
  const loading = ref(false);
  const errorMessage = ref("");

  // 中文註解：統一使用 member/auth 登入狀態，不再使用舊的 userStore。
  const authStore = userAuthStore();

  function normalizePet(pet) {
    return {
      ...pet,
      petId: pet.petId ?? pet.id,
      petName: pet.petName ?? pet.name ?? "",
      name: pet.petName ?? pet.name ?? "",
      petIntro: pet.petIntro ?? pet.description ?? "",
      description: pet.petIntro ?? pet.description ?? "",
      petAvatarUrl: normalizeCloudinaryImageUrl(
        pet.petAvatarUrl ?? pet.avatarUrl,
        "pet"
      ),
      avatarUrl: normalizeCloudinaryImageUrl(
        pet.petAvatarUrl ?? pet.avatarUrl,
        "pet"
      ),
    };
  }

  // 中文註解：支援後端回傳 ApiResponse<T> 或直接回傳陣列
  function unwrapApiData(response) {
    return response?.data?.data ?? response?.data ?? [];
  }

  // 載入目前登入會員的寵物資料
  async function loadPets() {
    loading.value = true;
    errorMessage.value = "";

    if (!authStore.isLogin) {
      errorMessage.value = "請先登入後再查看毛孩資料";
      pets.value = [];
      loading.value = false;
      return;
    }

    try {
      const response = await findMyPets();
      const data = unwrapApiData(response);

      pets.value = Array.isArray(data)
        ? data.map(normalizePet)
        : [];
    } catch (error) {
      console.error("寵物資料 API 載入失敗", error);
      errorMessage.value =
        error.response?.data?.message || "寵物資料載入失敗";
      pets.value = [];
      throw error;
    } finally {
      loading.value = false;
    }
  }

  // 新增目前登入會員的寵物
  async function addPet(dto) {
    if (!authStore.isLogin) {
      throw new Error("請先登入後再新增毛孩資料");
    }

    const payload = {
      petName: dto.petName ?? dto.name,
      petIntro: dto.petIntro ?? dto.description ?? "",
      petAvatarUrl: normalizeCloudinaryImageUrl(
        dto.petAvatarUrl ?? dto.avatarUrl,
        "pet"
      ),
    };

    const response = await createPet(payload);
    const data = response?.data?.data ?? response?.data;

    pets.value.unshift(normalizePet(data));

    return data;
  }

  async function removePet(petId) {
    await deletePetApi(petId);

    pets.value = pets.value.filter(
      (pet) => Number(pet.petId) !== Number(petId)
    );
  }

  function findPetById(petId) {
    return pets.value.find((pet) => Number(pet.petId) === Number(petId));
  }

  async function updatePet(petId, dto) {
    const payload = {
      petName: dto.petName ?? dto.name,
      petIntro: dto.petIntro ?? dto.description ?? "",
      petAvatarUrl: normalizeCloudinaryImageUrl(
        dto.petAvatarUrl ?? dto.avatarUrl,
        "pet"
      ),
    };

    const response = await updatePetApi(petId, payload);
    const data = response?.data?.data ?? response?.data;

    const index = pets.value.findIndex(
      (pet) => Number(pet.petId) === Number(petId)
    );

    if (index !== -1) {
      pets.value[index] = normalizePet(data);
    }

    return data;
  }

  return {
    pets,
    loading,
    errorMessage,
    loadPets,
    addPet,
    removePet,
    findPetById,
    updatePet,
  };
});