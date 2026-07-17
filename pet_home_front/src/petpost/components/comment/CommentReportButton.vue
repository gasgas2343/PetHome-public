<script setup>
import { useRouter } from 'vue-router'
import ReportModal from '@/petpost/components/report/ReportModal.vue'
import { useReportStore } from '@/petpost/stores/reportStore'
import { useForumAuth } from '@/petpost/composables/useForumAuth'

const props = defineProps({
  commentId: {
    type: Number,
    required: true,
  },
  reportedUserId: {
    type: Number,
    required: true,
  },
})

const router = useRouter()
const reportStore = useReportStore()
const { requireLogin } = useForumAuth()

function openReportDialog() {
if (!requireLogin('請先登入後再檢舉')) {
  router.push('/login')
  return
}

  reportStore.openDialog()
}
</script>

<template>
  <button class="btn btn-outline-danger btn-sm" type="button" @click="openReportDialog">
    檢舉留言
  </button>

  <ReportModal
    target-type="COMMENT"
    :target-id="props.commentId"
    :reported-user-id="props.reportedUserId"
  />
</template>
