<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useReportStore } from '@/petpost/stores/reportStore'
import { useForumAuth } from '@/petpost/composables/useForumAuth'

// 中文註解：由上層傳入檢舉目標資料
const props = defineProps({
  targetType: {
    type: String,
    required: true,
  },
  targetId: {
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

const reason = ref('')

// 中文註解：送出檢舉
async function submitReport() {
  if (!requireLogin('請先登入後再檢舉')) {
    router.push('/login')
    return
  }

  if (!reason.value.trim()) {
    alert('請輸入檢舉原因')
    return
  }

  try {
    await reportStore.addReport({
      targetType: props.targetType,
      targetId: props.targetId,
      reportedUserId: props.reportedUserId,
      reason: reason.value.trim(),
    })

    reason.value = ''
    reportStore.closeDialog()

    alert('檢舉成功，該內容已暫時隱藏，等待管理員審核')
    location.reload()
  } catch (error) {
    const message =
      error?.response?.data?.message || error?.message || reportStore.errorMessage || '檢舉失敗'

    alert(message)
  }
}
</script>

<template>
  <div v-if="reportStore.showDialog" class="card mt-3">
    <div class="card-body">
      <h5>🚨 檢舉內容</h5>

      <textarea
        v-model="reason"
        class="form-control"
        rows="4"
        placeholder="請輸入檢舉原因"
      ></textarea>

      <div v-if="reportStore.errorMessage" class="text-danger mt-2">
        {{ reportStore.errorMessage }}
      </div>

      <div class="mt-3">
        <button
          class="btn btn-danger me-2"
          type="button"
          :disabled="reportStore.loading"
          @click="submitReport"
        >
          {{ reportStore.loading ? '送出中...' : '送出檢舉' }}
        </button>

        <button
          class="btn btn-secondary"
          type="button"
          :disabled="reportStore.loading"
          @click="reportStore.closeDialog()"
        >
          取消
        </button>
      </div>
    </div>
  </div>
</template>
