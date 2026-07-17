<script setup>
import { onMounted } from 'vue'
import { useAdminCommentStore } from '@/petpost/stores/adminCommentStore'
import {
  formatMemberName,
  maskMemberAccount,
  formatCommentStatus,
} from '@/petpost/utils/adminFormat'

const adminCommentStore = useAdminCommentStore()

onMounted(() => {
  adminCommentStore.loadComments()
})
</script>

<template>
  <div class="admin-view">
    <div class="admin-section-header">
      <p>Forum Admin</p>
      <h2>留言管理</h2>
    </div>

    <div v-if="adminCommentStore.loading" class="admin-loading">載入中...</div>
    <div v-else-if="adminCommentStore.comments.length === 0" class="admin-empty">目前沒有留言</div>

    <div v-else class="admin-table-wrapper">
      <table class="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>文章ID</th>
            <th>留言者</th>
            <th>會員帳號</th>
            <th>內容</th>
            <th>狀態</th>
            <th>操作</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="comment in adminCommentStore.comments"
            :key="comment.commentId"
            :class="{ 'admin-hidden-row': Number(comment.status) === 2 }"
          >
            <td>{{ comment.commentId }}</td>
            <td>{{ comment.postId }}</td>
            <td>{{ formatMemberName(comment) }}</td>
            <td>{{ maskMemberAccount(comment.userEmail || comment.email || comment.userId) }}</td>

            <td>
              <span v-if="Number(comment.status) === 2" class="hidden-text"> 此留言已被隱藏 </span>
              <span v-else>
                {{ comment.content }}
              </span>
            </td>

            <td>{{ formatCommentStatus(comment.status) }}</td>

            <td>
              <button
                class="btn btn-warning btn-sm me-2"
                @click="adminCommentStore.hideComment(comment.commentId)"
              >
                隱藏
              </button>

              <button
                class="admin-btn admin-btn-success"
                @click="adminCommentStore.restoreComment(comment.commentId)"
              >
                恢復
              </button>

              <button
                class="admin-btn admin-btn-warning"
                @click="adminCommentStore.deleteComment(comment.commentId)"
              >
                刪除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
