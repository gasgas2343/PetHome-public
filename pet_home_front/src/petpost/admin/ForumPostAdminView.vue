<script setup>
import { onMounted } from 'vue'
import { useAdminPostStore } from '@/petpost/stores/adminPostStore'
import { formatMemberName, maskMemberAccount, formatPostStatus } from '@/petpost/utils/adminFormat'
const adminPostStore = useAdminPostStore()

onMounted(() => {
  adminPostStore.loadPosts()
})
</script>

<template>
  <div class="admin-section-header">
    <p>Forum Admin</p>
    <h2>文章管理</h2>

    <table class="admin-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>標題</th>
          <th>作者</th>
          <th>會員帳號</th>
          <th>狀態</th>
          <th>瀏覽</th>
          <th>按讚</th>
          <th>操作</th>
        </tr>
      </thead>

      <tbody>
        <tr
          v-for="post in adminPostStore.posts"
          :key="post.postId"
          :class="{ 'admin-hidden-row': Number(post.status) === 2 }"
        >
          <td>{{ post.postId }}</td>
          <td>
            <div class="post-title">
              {{ post.title }}
            </div>

            <small v-if="Number(post.status) === 2" class="hidden-text"> 此文章已被隱藏 </small>

            <small v-else-if="Number(post.status) === 3" class="deleted-text"> 此文章已刪除 </small>
          </td>
          <td>{{ formatMemberName(post) }}</td>
          <td>
            {{ maskMemberAccount(post.email || post.account || post.userAccount || post.userId) }}
          </td>
          <td>{{ formatPostStatus(post.status) }}</td>
          <td>{{ post.viewCount }}</td>
          <td>{{ post.likeCount }}</td>
          <td>
            <button
              class="btn btn-warning btn-sm me-2"
              @click="adminPostStore.disablePost(post.postId)"
            >
              下架
            </button>
            <button
              class="btn btn-success btn-sm me-2"
              @click="adminPostStore.enablePost(post.postId)"
            >
              恢復
            </button>

            <button v-if="post.canDelete" class="btn btn-danger btn-sm"
              @click="adminPostStore.deletePost(post.postId)">
              刪除
            </button>
            <span v-else class="text-muted small"> 需檢舉成立才可刪除 </span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
