<script setup>
import CommentLikeButton from '@/petpost/components/comment/CommentLikeButton.vue'
import CommentReportButton from '@/petpost/components/comment/CommentReportButton.vue'
import { useCommentStore } from '@/petpost/stores/commentStore'
import AppealButton from '@/petpost/components/appeal/AppealButton.vue'
import AppealDialog from '@/petpost/components/appeal/AppealDialog.vue'

const props = defineProps({
  comment: {
    type: Object,
    required: true,
  },
})

const commentStore = useCommentStore()

async function deleteCurrentComment() {
  if (confirm('確定刪除留言？')) {
    await commentStore.removeComment(props.comment.commentId)
  }
}
</script>

<template>
  <div
    class="card mb-2 comment-card"
    :class="{ 'is-hidden-comment': Number(comment.status) === 2 }"
  >
    <div class="card-body">
      <div class="fw-bold">
        {{ comment.userName || `會員 ${comment.userId}` }}
      </div>

      <div class="mt-2">
        <template v-if="Number(comment.status) === 2">
          <div class="hidden-comment-row">
            <span class="hidden-comment-message">此留言已被隱藏</span>
            <AppealButton />
            <AppealDialog
              target-type="COMMENT"
              :target-id="comment.commentId"
              appeal-type="COMMENT"
            />
          </div>
        </template>

        <template v-else>
          {{ comment.content }}
        </template>
      </div>

      <hr />

      <div class="d-flex gap-2">
        <CommentLikeButton v-if="Number(comment.status) !== 2" :commentId="comment.commentId" />

        <CommentReportButton
          v-if="Number(comment.status) !== 2"
          :commentId="comment.commentId"
          :reportedUserId="comment.userId"
        />

        <button
          v-if="Number(comment.status) !== 2"
          class="btn btn-danger btn-sm"
          type="button"
          @click="deleteCurrentComment"
        >
          刪除留言
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.comment-card {
  border-radius: 14px;
}

.is-hidden-comment {
  background: #eeeeee;
  border-color: #d7d7d7;
  opacity: 0.75;
}

.hidden-comment-message {
  color: #777;
  font-weight: 800;
  font-style: italic;
}

.hidden-comment-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.hidden-comment-message {
  color: #777;
  font-weight: 800;
  font-style: italic;
}
</style>
