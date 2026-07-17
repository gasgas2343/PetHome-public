<script setup>
import { ref, onMounted } from "vue";

import Swal from "sweetalert2";

import {
  createFavorite,
  deleteFavorite,
  checkFavorite,
} from "@/api/petmap/favoriteApi";

const props = defineProps({
  userId: Number,

  placeId: Number,
});

const isFavorite = ref(false);

onMounted(async () => {
  const response = await checkFavorite(
    props.userId,
    props.placeId,
  );

  isFavorite.value = response.data.data;
});

const toggleFavorite = async () => {
  if (isFavorite.value) {
    await deleteFavorite(
      props.userId,

      props.placeId,
    );
    emit("favorite-removed", props.placeId);

    isFavorite.value = false;

    Swal.fire({
      icon: "success",

      title: "已取消收藏",

      timer: 1000,

      showConfirmButton: false,
    });
  } else {
    await createFavorite({
      userId: props.userId,

      placeId: props.placeId,
    });

    isFavorite.value = true;

    Swal.fire({
      icon: "success",

      title: "收藏成功",

      timer: 1000,

      showConfirmButton: false,
    });
  }
};
const emit = defineEmits(["favorite-removed"]);
</script>

<template>
  <button
    class="btn rounded-pill"
    :class="isFavorite ? 'btn-danger' : 'btn-outline-danger'"
    @click="toggleFavorite"
  >
    {{ isFavorite ? "❤️ 已收藏" : "🤍 收藏" }}
  </button>
</template>
