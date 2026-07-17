<script setup>// 放統計畫面
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'

import { userAuthStore } from '@/stores/auth'
import { useAdminDashboardStore } from '@/petpost/stores/adminDashboardStore'
import AdminStatisticCard from '@/petpost/components/admin/AdminStatisticCard.vue'

const router = useRouter()
const authStore = userAuthStore()
const adminDashboardStore = useAdminDashboardStore()

const { summary, loading, errorMessage } = storeToRefs(adminDashboardStore)

onMounted(() => {
  if (!authStore.canAccessBackstage) {
    alert('你沒有後台權限')
    router.push('/')
    return
  }

  adminDashboardStore.loadSummary()
})
</script>

<template>
  <div>
    <h2 class="mb-4">👑 管理後台</h2>

    <div v-if="loading" class="alert alert-secondary">
      統計資料載入中...
    </div>

    <div v-else-if="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
    </div>

    <div v-else class="row g-3">
      <AdminStatisticCard title="總文章數" :count="summary.postCount" />
      <AdminStatisticCard title="總會員數" :count="summary.userCount" />
      <AdminStatisticCard title="待處理檢舉" :count="summary.pendingReportCount" />
      <AdminStatisticCard title="待處理申訴" :count="summary.pendingAppealCount" />
    </div>
  </div>
</template>

<style scoped>
h2 {
  font-weight: bold;
}
</style>
