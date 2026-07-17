<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

import { useAdminAppealStore } from '@/petpost/stores/adminAppealStore'
import { userAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = userAuthStore()
const adminAppealStore = useAdminAppealStore()

onMounted(() => {
  if (!authStore.canAccessBackstage) {
    alert('你沒有後台權限')
    router.push('/')
    return
  }

  adminAppealStore.loadAppeals()
})

async function handleApprove(appealId) {
  try {
    await adminAppealStore.approveAppeal(appealId)
    alert('申訴已通過')
  } catch (error) {
    alert(error?.response?.data?.message || error?.message || '通過申訴失敗')
  }
}

async function handleReject(appealId) {
  try {
    await adminAppealStore.rejectAppeal(appealId)
    alert('申訴已駁回')
  } catch (error) {
    alert(error?.response?.data?.message || error?.message || '駁回申訴失敗')
  }
}
</script>

<template>
  <div>
    <h3>申訴管理</h3>

    <div v-if="adminAppealStore.loading" class="text-secondary">
      載入中...
    </div>

    <div v-else-if="adminAppealStore.errorMessage" class="alert alert-danger">
      {{ adminAppealStore.errorMessage }}
    </div>

    <div v-else-if="adminAppealStore.appeals.length === 0" class="alert alert-info mt-3">
      目前沒有待審核申訴
    </div>

    <table v-else class="table table-bordered mt-3">
      <thead>
        <tr>
          <th>編號</th>
          <th>檢舉編號</th>
          <th>申訴人</th>
          <th>類型</th>
          <th>主旨</th>
          <th>原因</th>
          <th>狀態</th>
          <th>操作</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="appeal in adminAppealStore.appeals" :key="appeal.appealId">
          <td>{{ appeal.appealId }}</td>
          <td>{{ appeal.reportId }}</td>
          <td>{{ appeal.userId }}</td>
          <td>{{ appeal.appealType }}</td>
          <td>{{ appeal.subject }}</td>
          <td>{{ appeal.reason }}</td>
          <td>待審核</td>
          <td>
            <button
              class="btn btn-success btn-sm me-2"
              :disabled="adminAppealStore.loading"
              @click="handleApprove(appeal.appealId)"
            >
              通過
            </button>

            <button
              class="btn btn-danger btn-sm"
              :disabled="adminAppealStore.loading"
              @click="handleReject(appeal.appealId)"
            >
              駁回
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>