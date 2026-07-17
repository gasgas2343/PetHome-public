<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  post: {
    type: Object,
    required: true,
  },
  variant: {
    type: String,
    default: 'grid',
  },
})

const router = useRouter()

function goDetail() {
  // 中文註解：後端可能回傳 postId，前端假資料可能是 id，兩種都支援
  const postId = props.post?.postId ?? props.post?.id

  // 中文註解：沒有文章 ID 就不要跳轉，避免 RouterView 更新錯誤
  if (!postId) {
    console.error('文章 ID 不存在，無法進入詳情頁', props.post)
    return
  }

  router.push(`/post/${postId}`)
}
</script>

<template>
  <article
    class="post-card"
    :class="[
      { 'is-hidden': Number(post.status) === 2 },
      variant === 'list' ? 'post-card-list' : 'post-card-grid',
    ]"
  >
    <div class="post-card-body">
      <span v-if="Number(post.status) === 2" class="hidden-badge"> 已被檢舉隱藏 </span>
      <!-- 標題 -->
      <span v-if="Number(post.status) === 2" class="hidden-badge"> 此文章已被檢舉 </span>
      <h3 v-if="Number(post.status) !== 2">
        {{ post.title }}
      </h3>
      <h3 v-else class="hidden-title">此文章已被檢舉</h3>

      <!-- 中文註解：顯示文章標籤，確認新增文章後是否真的有存到 tags。 -->
      <div v-if="Array.isArray(post.tags) && post.tags.length" class="mb-2">
        <span v-for="tag in post.tags" :key="tag" class="post-tag"> # {{ tag }} </span>
      </div>
      <!-- 中文註解：首頁文章縮圖 -->
      <img
        v-if="Number(post.status) !== 2 && post.coverImageUrl"
        :src="post.coverImageUrl"
        class="post-card-image"
        alt="文章圖片"
      />
      <!-- 內容 -->
      <p class="post-content">
        <span v-if="Number(post.status) === 2" class="hidden-text">
          此文章已被檢舉，暫時隱藏，等待管理員審核。
        </span>
        <span v-else>
          {{ post.content }}
        </span>
      </p>

      <!-- 查看詳情 -->
      <button class="read-btn" type="button" @click="goDetail">查看文章</button>
    </div>
  </article>
</template>

<style scoped>
.post-card {
  overflow: hidden;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(111, 98, 88, 0.1);
  box-shadow: 0 18px 40px rgba(92, 61, 35, 0.1);
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 24px 50px rgba(92, 61, 35, 0.14);
}

.post-card-body {
  padding: 20px;
}

.post-card h3 {
  margin: 0 0 12px;
  color: #3f332b;
  font-size: 20px;
  font-weight: 900;
  line-height: 1.45;
}

.post-tag {
  display: inline-flex;
  align-items: center;
  margin: 0 6px 8px 0;
  padding: 5px 10px;
  border-radius: 999px;
  background: rgba(220, 163, 89, 0.14);
  color: #8a5b28;
  font-size: 13px;
  font-weight: 800;
}

.post-content {
  min-height: 52px;
  margin: 12px 0 18px;
  color: #6f6258;
  line-height: 1.75;
  white-space: pre-wrap;
}

.post-card-image {
  width: 100%;
  height: 210px;
  object-fit: cover;
  border-radius: 18px;
  margin-bottom: 14px;
}

.read-btn {
  border: 0;
  border-radius: 999px;
  padding: 10px 18px;
  background: #7ca6a0;
  color: #fff;
  font-weight: 900;
  cursor: pointer;
  transition:
    background 0.2s ease,
    transform 0.2s ease;
}

.read-btn:hover {
  background: #6f9892;
  transform: translateY(-1px);
}

.post-card.is-hidden {
  background: #eeeeee;
  opacity: 0.65;
  filter: grayscale(0.7);
}

.hidden-badge {
  display: inline-block;
  margin-bottom: 8px;
  padding: 4px 10px;
  border-radius: 999px;
  background: #999;
  color: #fff;
  font-size: 13px;
  font-weight: 800;
}

.hidden-title {
  color: #777;
  font-style: italic;
}

.hidden-text {
  color: #777;
  font-weight: 800;
}

.post-card-list .post-card-body {
  display: grid;
  grid-template-columns: 170px 1fr auto;
  gap: 18px;
  align-items: center;
}

.post-card-list .post-card-image {
  width: 170px;
  height: 105px;
  margin-bottom: 0;
}

.post-card-list .post-content {
  min-height: auto;
  margin: 8px 0 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (max-width: 768px) {
  .post-card-list .post-card-body {
    grid-template-columns: 1fr;
  }

  .post-card-list .post-card-image {
    width: 100%;
    height: 180px;
  }
}
</style>
