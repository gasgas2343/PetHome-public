<script setup>
import { computed, onMounted } from "vue";
import { useLikeStore } from "@/petpost/stores/likeStore";

const props = defineProps({
  commentId: {
    type: Number,
    required: true,
  },
});
const likeStore = useLikeStore();
onMounted(async () => {
  await likeStore.loadCommentLike(props.commentId);
});
const commentLike = computed(() => likeStore.commentLikeMap[props.commentId]);
async function toggleLike() {
  await likeStore.toggleComment(props.commentId);
}
</script>

<template>
  <button
    class="btn btn-sm"
    :class="commentLike?.liked ? 'btn-danger' : 'btn-outline-danger'"
    @click="toggleLike"
  >
    {{ commentLike?.liked ? "❤️ 已按讚" : "🤍 按讚" }} {{ commentLike?.count ?? 0 }}
  </button>
</template>