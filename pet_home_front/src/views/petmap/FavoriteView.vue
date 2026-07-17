<script setup>
import { onMounted } from "vue";

import { useFavoriteStore } from "@/stores/petmap/favoriteStore";

import PlaceCard from "@/components/petmap/PlaceCard.vue";

import { userAuthStore } from "@/stores/auth";

import UserNavbar from "@/components/petmap/UserNavbar.vue";

const favoriteStore = useFavoriteStore();

const authStore = userAuthStore();

const removeFavoriteFromView = (placeId) => {
  favoriteStore.favorites = favoriteStore.favorites.filter(
    (favorite) => favorite.placeId !== placeId,
  );
};

onMounted(async () => {
  if (!authStore.id) return;

  await favoriteStore.fetchFavorites(authStore.id);

});
</script>
<template>
  <UserNavbar />
  <div class="container mt-4">
    <h2 class="mb-4">❤️ 我的收藏</h2>

    <PlaceCard
      v-for="favorite in favoriteStore.favorites"
      :key="favorite.placeId"
      :place="favorite"
      @favorite-removed="removeFavoriteFromView"
    />
  </div>
</template>
