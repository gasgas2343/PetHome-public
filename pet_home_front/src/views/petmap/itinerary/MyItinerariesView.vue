<script setup>
import { ref } from "vue";
import { onMounted } from "vue";

import {
  getMyItineraries,
  createItinerary,
  updateItinerary,
  deleteItinerary,
} from "@/api/petmap/itineraryApi";

import Swal from "sweetalert2";

import { userAuthStore } from "@/stores/auth";

import UserNavbar from "@/components/petmap/UserNavbar.vue";

const authStore = userAuthStore();

const itineraries = ref([]);

const loadItineraries = async () => {
  const response = await getMyItineraries(authStore.id);

  itineraries.value = response.data.data;

};

const handleCreateItinerary = async () => {
  const result = await Swal.fire({
    title: "新增行程",

    input: "text",

    inputLabel: "行程名稱",

    inputPlaceholder: "例：台北兩天一夜",

    showCancelButton: true,

    confirmButtonText: "新增",

    cancelButtonText: "取消",
  });

  if (!result.isConfirmed) {
    return;
  }

  try {
    await createItinerary({
      userId: authStore.id,

      title: result.value,
    });

    await Swal.fire({
      icon: "success",

      title: "新增成功",

      timer: 1200,

      showConfirmButton: false,
    });

    await loadItineraries();
  } catch (error) {
    console.error(error);

    await Swal.fire({
      icon: "error",

      title: "新增失敗",
    });
  }
};

const handleUpdateItinerary = async (itinerary) => {
  const result = await Swal.fire({
    title: "修改行程",

    input: "text",

    inputValue: itinerary.title,

    showCancelButton: true,

    confirmButtonText: "修改",

    cancelButtonText: "取消",
  });

  if (!result.isConfirmed) {
    return;
  }

  try {
    await updateItinerary(
      itinerary.itineraryId,

      {
        title: result.value,
      },
    );

    await Swal.fire({
      icon: "success",

      title: "修改成功",

      timer: 1200,

      showConfirmButton: false,
    });

    await loadItineraries();
  } catch (error) {
    console.error(error);
  }
};

const handleDeleteItinerary = async (itineraryId) => {
  const result = await Swal.fire({
    title: "刪除行程？",

    text: "刪除後將無法復原",

    icon: "warning",

    showCancelButton: true,

    confirmButtonText: "刪除",

    cancelButtonText: "取消",
  });

  if (!result.isConfirmed) {
    return;
  }

  try {
    await deleteItinerary(itineraryId);

    await Swal.fire({
      icon: "success",

      title: "刪除成功",

      timer: 1200,

      showConfirmButton: false,
    });

    await loadItineraries();
  } catch (error) {
    console.error(error);
  }
};

onMounted(async () => {
  await loadItineraries();
});
</script>

<template>
  <UserNavbar />
  <div class="container mt-4">
    <h1>我的行程</h1>

    <button class="btn btn-success mb-3" @click="handleCreateItinerary">
      新增行程
    </button>

    <div
      v-for="itinerary in itineraries"
      :key="itinerary.itineraryId"
      class="card mt-3"
    >
      <div class="card-body">
        <h4>
          {{ itinerary.title }}
        </h4>
        <RouterLink
  :to="{
    name: 'PetMapItineraryDetail',
    params: {
      id: itinerary.itineraryId,
    },
  }"
  class="btn btn-primary me-2"
>
  查看
</RouterLink>

        <div class="mt-3">
          <button
            class="btn btn-warning me-2"
            @click="handleUpdateItinerary(itinerary)"
          >
            修改
          </button>

          <button
            class="btn btn-danger"
            @click="handleDeleteItinerary(itinerary.itineraryId)"
          >
            刪除
          </button>
        </div>

        <small>
          {{ itinerary.createdAt }}
        </small>
      </div>
    </div>
  </div>
</template>
