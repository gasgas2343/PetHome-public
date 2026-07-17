<script setup>
import { computed, onMounted } from "vue";
import { useLikeStore } from "@/petpost/stores/likeStore";

const props = defineProps({
  postId: {
    type: Number,
    required: true,
  },
});

const likeStore = useLikeStore();

onMounted(async () => {
  await likeStore.loadPostLike(props.postId);
});

const postLike = computed(() => likeStore.postLikeMap[props.postId]);

async function toggleLike() {
  await likeStore.togglePost(props.postId);
}
</script>

<template>
  <button
    class="btn"
    :class="postLike?.liked ? 'btn-danger' : 'btn-outline-danger'"
    @click="toggleLike"
  >
    {{ postLike?.liked ? "❤️ 已按讚" : "🤍 按讚" }} {{ postLike?.count ?? 0 }}
  </button>
</template>