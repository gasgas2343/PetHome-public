<script setup>
defineProps({
  post: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <RouterLink class="forum-card" :to="`/community/posts/${post.postId || post.post_id}`">
    <div class="card-img-wrap">
      <img
        :src="post.coverImageUrl || post.cover_image_url || post.imageUrl || '/images/forum-default.jpg'"
        :alt="post.title"
      />
    </div>

    <div class="card-body">
      <span class="card-category">{{ post.categoryName || '毛孩交流' }}</span>
      <h3>{{ post.title }}</h3>
      <p>{{ post.summary || post.content }}</p>

      <div class="card-meta">
        <span><i class="bi bi-eye"></i> {{ post.viewCount || 0 }}</span>
        <span><i class="bi bi-heart"></i> {{ post.likeCount || 0 }}</span>
        <span><i class="bi bi-chat"></i> {{ post.commentCount || 0 }}</span>
      </div>
    </div>
  </RouterLink>
</template>

<style scoped>
.forum-card {
  display: block;
  overflow: hidden;
  border-radius: 22px;
  background: #fff;
  color: inherit;
  text-decoration: none;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
}

.forum-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.12);
}

.card-img-wrap {
  height: 190px;
  background: #f3f4f6;
}

.card-img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-body {
  padding: 18px;
}

.card-category {
  display: inline-block;
  margin-bottom: 8px;
  color: #f97316;
  font-weight: 700;
  font-size: 13px;
}

.card-body h3 {
  font-size: 19px;
  margin: 0 0 8px;
}

.card-body p {
  color: #6b7280;
  font-size: 14px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  gap: 14px;
  margin-top: 14px;
  color: #9ca3af;
  font-size: 13px;
}
</style>
