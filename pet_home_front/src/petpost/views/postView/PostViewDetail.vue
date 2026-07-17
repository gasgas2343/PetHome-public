<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { usePostStore } from '@/petpost/stores/postStore'
import { useCommentStore } from '@/petpost/stores/commentStore'

import CommentList from '@/petpost/components/comment/CommentList.vue'
import CommentForm from '@/petpost/components/comment/CommentForm.vue'

import AppealButton from '@/petpost/components/appeal/AppealButton.vue'
import AppealDialog from '@/petpost/components/appeal/AppealDialog.vue'

import PostLikeButton from '@/petpost/components/post/PostLikeButton.vue'
import PostFavoriteButton from '@/petpost/components/post/PostFavoriteButton.vue'
import PostReportButton from '@/petpost/components/post/PostReportButton.vue'

const route = useRoute()
const router = useRouter()

const postStore = usePostStore()
const commentStore = useCommentStore()

const postId = computed(() => Number(route.params.postId))

const isReportedHidden = computed(() => {
  return Number(postStore.post?.status) === 2
})

onMounted(async () => {
  await postStore.loadPostById(postId.value)

  if (!isReportedHidden.value) {
    await commentStore.loadComments(postId.value)
  }
})

function backCommunity() {
  router.push('/community/posts')
}
</script>

<template>
  <main class="post-detail-page">
    <button class="back-btn" type="button" @click="backCommunity">返回文章列表</button>

    <section v-if="postStore.post" class="post-detail-card">
      <template v-if="isReportedHidden">
        <div class="hidden-post-box">
          <div class="hidden-post-content">
            <div>
              <h2>此文章已被檢舉</h2>
              <p>此文章已暫時隱藏，等待管理員審核。</p>
              <p class="appeal-tip">若您認為此文章被誤判，可以提出申訴。</p>
            </div>

            <div class="hidden-post-actions">
              <AppealButton />

              <AppealDialog
                target-type="POST"
                :target-id="postStore.post.postId"
                appeal-type="POST"
              />
            </div>
          </div>
        </div>
      </template>

      <template v-else>
        <h1>{{ postStore.post.title }}</h1>

        <div class="post-meta">
          作者：
          {{ postStore.post.userName || `會員 ${postStore.post.userId}` }}
        </div>

        <div v-if="postStore.post.tags?.length" class="post-tags">
          <span v-for="tag in postStore.post.tags" :key="tag" class="tag"> # {{ tag }} </span>
        </div>

        <img
          v-if="postStore.post.coverImageUrl"
          :src="postStore.post.coverImageUrl"
          class="post-image"
          alt="文章圖片"
        />

        <div class="post-content">
          {{ postStore.post.content }}
        </div>

        <div class="post-actions">
          <PostLikeButton :postId="postId" />
          <PostFavoriteButton :postId="postId" />
          <PostReportButton :postId="postId" :reportedUserId="postStore.post.userId" />
        </div>

        <hr />

        <CommentList :comments="commentStore.comments" />

        <CommentForm :postId="postId" />
      </template>
    </section>

    <section v-else class="post-detail-card">文章載入中...</section>
  </main>
</template>

<style scoped>
.post-detail-page {
  min-height: 100vh;
  padding: 40px 20px;
  background: #fff7ed;
}

.back-btn {
  margin-bottom: 20px;
  border: none;
  background: transparent;
  color: #8b5e3c;
  font-weight: 800;
  cursor: pointer;
}

.post-detail-card {
  max-width: 920px;
  margin: 0 auto;
  padding: 32px;
  border-radius: 24px;
  background: #fff;
  box-shadow: 0 18px 40px rgba(92, 61, 35, 0.1);
}

.post-meta {
  margin: 12px 0;
  color: #6f6258;
  font-weight: 700;
}

.post-tags {
  margin: 12px 0;
}

.tag {
  display: inline-block;
  margin-right: 8px;
  padding: 4px 10px;
  border-radius: 999px;
  background: #fff7ed;
  color: #8b5e3c;
  font-weight: 700;
}

.post-image {
  width: 100%;
  max-height: 420px;
  object-fit: cover;
  margin: 20px 0;
  border-radius: 20px;
}

.post-content {
  margin-top: 20px;
  color: #3f332b;
  line-height: 1.9;
  white-space: pre-wrap;
}

.post-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 24px;
}

.hidden-post-box {
  padding: 32px;
  border-radius: 20px;
  background: #eeeeee;
  color: #777;
  text-align: center;
  border: 1px solid #d7d7d7;
}

.hidden-post-box h2 {
  margin: 0 0 12px;
  color: #666;
  font-size: 30px;
  font-weight: 900;
}

.hidden-post-box p {
  margin: 0;
  font-weight: 700;
}

.hidden-post-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.hidden-post-actions {
  flex-shrink: 0;
}

.appeal-tip {
  margin-top: 8px;
  color: #8b5e3c;
  font-weight: 700;
}

@media (max-width: 768px) {
  .hidden-post-content {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
