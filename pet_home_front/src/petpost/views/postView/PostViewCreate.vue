<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { HOT_TAGS, FORUM_CATEGORIES } from '@/petpost/constants/forumConstants'

import { usePostStore } from '@/petpost/stores/postStore'
import { useForumAuth } from '@/petpost/composables/useForumAuth'

import ImageUploader from '@/petpost/components/common/ImageUploader.vue'
const router = useRouter()
const postStore = usePostStore()
const { isLoggedIn } = useForumAuth()

// 中文註解：文章分類 ID，對應後端 categoryId。
// 注意：這裡的 id 要和資料庫 forum_post_categories 的 category_id 一致。
const categoryId = ref(1)

// 中文註解：文章標題。
const title = ref('')

// 中文註解：文章內容。
const content = ref('')

// 中文註解：封面圖片網址，通常是 Cloudinary URL 或本地上傳後的 URL。
const coverImageUrl = ref('')

// 中文註解：錯誤訊息。
const errorMessage = ref('')

// 中文註解：熱門標籤，可多選。
const selectedTags = ref([])

// 中文註解：分類是文章的大主題。
// 這裡先對齊目前 SQL 預設分類：毛孩日常、健康醫療、飼養心得、領養送養。
const categories = FORUM_CATEGORIES

// 中文註解：熱門標籤是文章的關鍵字，可以讓首頁熱門標籤篩選文章。
const hotTags = HOT_TAGS

async function submitPost() {
  errorMessage.value = ''

  if (!categoryId.value) {
    errorMessage.value = '請選擇文章分類'
    return
  }

  // 中文註解：尚未登入禁止發文
  if (!isLoggedIn.value) {
    errorMessage.value = '請先登入後再發文'
    return
  }

  if (title.value.trim().length < 2) {
    errorMessage.value = '文章標題至少需要 2 個字'
    return
  }

  if (content.value.trim().length < 10) {
    errorMessage.value = '文章內容至少需要 10 個字'
    return
  }

  if (selectedTags.value.length === 0) {
    errorMessage.value = '請至少選擇一個熱門標籤'
    return
  }

  const imageUrls = coverImageUrl.value ? [coverImageUrl.value] : []

  const dto = {
    categoryId: Number(categoryId.value),
    title: title.value.trim(),
    content: content.value.trim(),
    coverImageUrl: coverImageUrl.value || '',
    imageUrls,

    // 中文註解：把使用者選到的熱門標籤一起送出給後端 PostCreateDTO.tags。
    tags: selectedTags.value,
  }

  try {
    await postStore.addPost(dto)
    router.push('/community')
  } catch (error) {
    console.error('新增文章失敗', error)

    // 中文註解：
    // 後端公版錯誤格式可能是 { message: "..." }。
    // 這裡把錯誤顯示在畫面上，避免 Vue 出現 Unhandled error。
    errorMessage.value =
      error.response?.data?.message || error.response?.data?.error || '新增文章失敗，請稍後再試'
  }
}
</script>

<template>
  <div class="container py-4 post-create-page petpost-photo-page">
    <h3 class="mb-3">新增文章</h3>

    <div v-if="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
    </div>

    <label class="form-label fw-bold">文章分類</label>
    <select class="form-select mb-3" v-model.number="categoryId">
      <option disabled :value="null">請選擇分類</option>
      <option v-for="category in categories" :key="category.id" :value="category.id">
        {{ category.name }}
      </option>
    </select>

    <!-- 中文註解：熱門標籤區，可以讓文章同時擁有多個標籤。 -->
    <div class="tag-box mb-3">
      <label class="form-label fw-bold">熱門標籤</label>

      <div class="d-flex gap-3 flex-wrap">
        <label v-for="tag in hotTags" :key="tag" class="tag-option">
          <input type="checkbox" :value="tag" v-model="selectedTags" />
          {{ tag }}
        </label>
      </div>
    </div>

    <label class="form-label fw-bold">文章標題</label>
    <input class="form-control mb-3" v-model="title" placeholder="請輸入標題" />

    <label class="form-label fw-bold">文章內容</label>
    <textarea
      class="form-control mb-3"
      rows="5"
      v-model="content"
      placeholder="文章內容至少 10 個字"
    ></textarea>

    <ImageUploader v-model="coverImageUrl" type="post" label="新增圖片" />

    <button class="btn btn-success mt-3" type="button" @click="submitPost">發文</button>
  </div>
</template>

<style scoped>
.tag-option {
  cursor: pointer;
  user-select: none;
}
</style>
