<script setup>
// Vue
import { ref } from 'vue'

// Pinia
import { useCommentStore } from '@/petpost/stores/commentStore'

import { useRouter } from 'vue-router'
import { useForumAuth } from '@/petpost/composables/useForumAuth'

const router = useRouter()
const { requireLogin } = useForumAuth()

// 接收父元件傳入的 postId
const props = defineProps({
  postId: {
    type: [String, Number],

    required: true,
  },
})

// 建立 Comment Store
const commentStore = useCommentStore()

// 留言內容
const content = ref('')

const submitting = ref(false)

// ======================
// 送出留言
// ======================
async function submitComment() {
  const trimmedContent = content.value.trim()
  if (submitting.value) {
    return
  }

  submitting.value = true

  if (!requireLogin('請先登入後再留言')) {
    router.push('/login')
    return
  }

  if (trimmedContent.length < 2) {
    alert('留言內容至少需要 2 個字')
    return
  }

  // 防止空字串
  if (content.value.trim() === '') {
    alert('請輸入留言')

    return
  }

  // 建立 DTO
  const dto = {
    // 文章編號
    postId: props.postId,

    // 留言內容
    content: content.value,
  }

  try {
    // userId 目前先固定測試
    await commentStore.addComment(dto)

    // 清空輸入框
    content.value = ''
  } catch (error) {
    console.log(
      '留言失敗',

      error,
    )
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="mt-4">
    <h5>發表留言</h5>

    <!-- 留言輸入框 -->
    <textarea class="form-control" rows="4" placeholder="請輸入留言" v-model="content"> </textarea>

    <!-- 送出按鈕 -->
    <button class="btn btn-primary mt-3" :disabled="submitting" @click="submitComment">
      {{ submitting ? '送出中...' : '送出留言' }}
    </button>
  </div>
</template>

<style scoped></style>
