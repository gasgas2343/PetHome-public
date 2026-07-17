<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { usePostStore } from '@/petpost/stores/postStore'
import ImageUploader from '@/petpost/components/common/ImageUploader.vue'

const route = useRoute()
const router = useRouter()
const postStore = usePostStore()

const categoryId = ref(1)
const title = ref('')
const content = ref('')
const coverImageUrl = ref('')

const categories = [
  { categoryId: 1, categoryName: '新手知識' },
  { categoryId: 2, categoryName: '飼主交流' },
  { categoryId: 3, categoryName: '公告互助' },
  { categoryId: 4, categoryName: '好物推薦' },
  { categoryId: 5, categoryName: '毛孩回憶' },
]

onMounted(async () => {
  await postStore.loadPostById(Number(route.params.postId))

  if (!postStore.post) {
    alert('找不到文章資料')
    router.push('/community')
    return
  }

  categoryId.value = postStore.post.categoryId ?? 1
  title.value = postStore.post.title ?? ''
  content.value = postStore.post.content ?? ''
  coverImageUrl.value = postStore.post.coverImageUrl ?? postStore.post.imageUrl ?? ''
})

async function updatePost() {
  if (!title.value || title.value.trim().length < 2) {
    alert('文章標題至少需要 2 個字')
    return
  }

  if (!content.value || content.value.trim().length < 10) {
    alert('文章內容至少需要 10 個字')
    return
  }

  const imageUrls = coverImageUrl.value ? [coverImageUrl.value] : []

  const dto = {
    categoryId: Number(categoryId.value),
    title: title.value.trim(),
    content: content.value.trim(),
    coverImageUrl: coverImageUrl.value || '',
    imageUrls,
  }

  await postStore.updatePost(Number(route.params.postId), dto)

  router.push(`/post/${route.params.postId}`)
}
</script>

<template>
  <div class="container post-update-page petpost-photo-page">
    <h2>修改文章</h2>

    <select class="form-select mb-3" v-model="categoryId">
      <option
        v-for="category in categories"
        :key="category.categoryId"
        :value="category.categoryId"
      >
        {{ category.categoryName }}
      </option>
    </select>

    <input class="form-control mb-3" v-model="title" placeholder="請輸入標題" />

    <textarea
      class="form-control mb-3"
      rows="6"
      v-model="content"
      placeholder="請輸入內容"
    ></textarea>

    <ImageUploader
      v-model="coverImageUrl"
      type="post"
      label="新增圖片"
    />

    <button class="btn btn-primary mt-3" @click="updatePost">儲存修改</button>
  </div>
</template>

<style scoped></style>
