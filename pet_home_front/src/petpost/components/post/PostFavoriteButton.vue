<script setup>
import { computed, onMounted } from "vue";
import { useFavoriteStore } from "@/petpost/stores/favoriteStore";

const props = defineProps({
  postId: {
    type: Number,
    required: true,
  },
});

const favoriteStore = useFavoriteStore();

onMounted(async () => {
  await favoriteStore.loadFavorite(props.postId);
});

const favorite = computed(() => favoriteStore.favoriteMap[props.postId]);

async function toggleFavorite() {
  await favoriteStore.toggle(props.postId);
}
</script>

<template>
  <button class="btn btn-outline-warning" @click="toggleFavorite">
    {{ favorite?.favorited ? "⭐ 已收藏" : "☆ 收藏" }}
  </button>
</template>