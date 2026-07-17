<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAppealStore } from '@/petpost/stores/appealStore'
import { useForumAuth } from '@/petpost/composables/useForumAuth'

// 中文註解：申訴必須對應某一筆檢舉案件
const props = defineProps({
  reportId: {
    type: Number,
    default: null,
  },
  targetType: {
    type: String,
    default: 'POST',
  },
  targetId: {
    type: Number,
    default: null,
  },
  appealType: {
    type: String,
    default: 'POST',
  },
})

const router = useRouter()
const appealStore = useAppealStore()
const { requireLogin } = useForumAuth()

const subject = ref('')
const reason = ref('')
const imageUrl = ref('')

// 中文註解：送出申訴
async function submitAppeal() {
  if (!requireLogin('請先登入後再申訴')) {
    router.push('/login')
    return
  }

  if (!subject.value.trim()) {
    alert('請輸入申訴主旨')
    return
  }

  if (!reason.value.trim()) {
    alert('請輸入申訴原因')
    return
  }

  try {
    await appealStore.addAppeal({
      reportId: props.reportId,
      targetType: props.targetType,
      targetId: props.targetId,
      appealType: props.appealType,
      subject: subject.value.trim(),
      reason: reason.value.trim(),
      imageUrl: imageUrl.value.trim() || null,
    })

    subject.value = ''
    reason.value = ''
    imageUrl.value = ''

    appealStore.closeDialog()

    alert('申訴送出成功')
  } catch (error) {
    const message =
      error?.response?.data?.message || error?.message || appealStore.errorMessage || '申訴送出失敗'

    alert(message)
  }
}
</script>

<template>
  <div v-if="appealStore.showDialog" class="card mt-3">
    <div class="card-body">
      <h5>提出申訴</h5>

      <input v-model="subject" class="form-control mb-2" placeholder="請輸入申訴主旨" />

      <textarea
        v-model="reason"
        class="form-control"
        rows="4"
        placeholder="請輸入申訴原因"
      ></textarea>

      <input v-model="imageUrl" class="form-control mt-2" placeholder="圖片網址，可不填" />

      <div v-if="appealStore.errorMessage" class="text-danger mt-2">
        {{ appealStore.errorMessage }}
      </div>

      <div class="mt-3">
        <button
          class="btn btn-primary me-2"
          type="button"
          :disabled="appealStore.loading"
          @click="submitAppeal"
        >
          {{ appealStore.loading ? '送出中...' : '送出申訴' }}
        </button>

        <button
          class="btn btn-secondary"
          type="button"
          :disabled="appealStore.loading"
          @click="appealStore.closeDialog()"
        >
          取消
        </button>
      </div>
    </div>
  </div>
</template>
