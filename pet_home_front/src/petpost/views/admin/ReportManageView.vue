<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

import { useAdminReportStore } from '@/petpost/stores/adminReportStore'
import { userAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = userAuthStore()
const adminReportStore = useAdminReportStore()

onMounted(() => {
  if (!authStore.canAccessBackstage) {
    alert('你沒有後台權限')
    router.push('/')
    return
  }

  adminReportStore.loadReports()
})

async function handleApprove(reportId) {
  try {
    await adminReportStore.approveReport(reportId)
    alert('檢舉已通過')
  } catch (error) {
    alert(error?.response?.data?.message || error?.message || '通過檢舉失敗')
  }
}

async function handleReject(reportId) {
  try {
    await adminReportStore.rejectReport(reportId)
    alert('檢舉已駁回')
  } catch (error) {
    alert(error?.response?.data?.message || error?.message || '駁回檢舉失敗')
  }
}
</script>

<template>
  <div>
    <h3>檢舉管理</h3>

    <div v-if="adminReportStore.loading" class="text-secondary">
      載入中...
    </div>

    <div v-else-if="adminReportStore.errorMessage" class="alert alert-danger">
      {{ adminReportStore.errorMessage }}
    </div>

    <div v-else-if="adminReportStore.reports.length === 0" class="alert alert-info mt-3">
      目前沒有待審核檢舉
    </div>

    <table v-else class="table table-bordered mt-3">
      <thead>
        <tr>
          <th>編號</th>
          <th>類型</th>
          <th>目標ID</th>
          <th>檢舉人</th>
          <th>被檢舉人</th>
          <th>原因</th>
          <th>狀態</th>
          <th>操作</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="report in adminReportStore.reports" :key="report.reportId">
          <td>{{ report.reportId }}</td>
          <td>{{ report.postId ? '文章' : '留言' }}</td>
          <td>{{ report.postId || report.commentId }}</td>
          <td>{{ report.reporterId }}</td>
          <td>{{ report.reportedUserId }}</td>
          <td>{{ report.reason }}</td>
          <td>待審核</td>
          <td>
            <button
              class="btn btn-success btn-sm me-2"
              :disabled="adminReportStore.loading"
              @click="handleApprove(report.reportId)"
            >
              通過
            </button>

            <button
              class="btn btn-danger btn-sm"
              :disabled="adminReportStore.loading"
              @click="handleReject(report.reportId)"
            >
              駁回
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>