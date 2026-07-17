<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

import { useAdminPostStore } from '@/petpost/stores/adminPostStore'
import { userAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = userAuthStore()
const adminPostStore = useAdminPostStore()

async function disablePost(postId) {
  await adminPostStore.disablePost(postId)
}

async function enablePost(postId) {
  await adminPostStore.enablePost(postId)
}

function isActivePost(post) {
  return Number(post.status) === 1
}

function isHiddenPost(post) {
  return Number(post.status) === 2
}

onMounted(async () => {
  if (!authStore.canAccessBackstage) {
    alert('你沒有後台權限')
    router.push('/')
    return
  }

  await adminPostStore.loadPosts()
})
</script>

<template>
  <div>
    <h2 class="mb-4">文章管理</h2>

    <table class="table table-bordered">
      <thead>
        <tr>
          <th>編號</th>
          <th>標題</th>
          <th>作者</th>
          <th>狀態</th>
          <th>操作</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="post in adminPostStore.posts" :key="post.postId">
          <td>{{ post.postId }}</td>
          <td>{{ post.title }}</td>
          <td>{{ post.userName || post.username || post.userId }}</td>
          <td>{{ post.status }}</td>
          <td>
            <button
              v-if="isActivePost(post)"
              class="btn btn-warning me-2"
              @click="disablePost(post.postId)"
            >
              下架
            </button>

            <button
              v-else-if="isHiddenPost(post)"
              class="btn btn-success me-2"
              @click="enablePost(post.postId)"
            >
              恢復
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
h2 {
  font-weight: bold;
}
</style>
