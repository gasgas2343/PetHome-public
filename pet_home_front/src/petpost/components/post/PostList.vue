<script setup>
// 中文註解：論壇文章列表元件，負責把文章陣列渲染成卡片網格。
import PostCard from '@/petpost/components/post/PostCard.vue'

defineProps({
  posts: {
    type: Array,
    required: true,
  },
  variant: {
    type: String,
    default: 'grid',
  },
})
</script>

<template>
  <div>
    <div v-if="posts.length === 0" class="empty-state">暫無文章</div>

    <div v-else :class="variant === 'list' ? 'post-list-row' : 'post-list-grid'">
      <PostCard
        v-for="post in posts"
        :key="post.postId || post.id"
        :post="post"
        :variant="variant"
      />
    </div>
  </div>
</template>

<style scoped>
.post-list-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 22px;
}

.empty-state {
  margin-top: 28px;
  padding: 26px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.72);
  color: #8a7666;
  text-align: center;
  font-weight: 700;
}

.post-list-row {
  display: grid;
  gap: 16px;
}
</style>
