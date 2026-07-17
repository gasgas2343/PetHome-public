<script setup>
import { onMounted } from 'vue'
import { useAdminReportStore } from '@/petpost/stores/adminReportStore'

import { formatMemberName, maskMemberAccount } from '@/petpost/utils/adminFormat'

const adminReportStore = useAdminReportStore()

onMounted(() => {
  adminReportStore.loadReports()
})
</script>

<template>
  <div class="admin-view">
    <p class="kicker">Forum Admin</p>
    <h1>檢舉管理</h1>

    <div v-if="adminReportStore.loading">載入中...</div>
    <div v-else-if="adminReportStore.reports.length === 0">目前沒有待審核檢舉</div>

    <table v-else class="table table-bordered mt-3">
      <thead>
        <tr>
          <th>ID</th>
          <th>檢舉者</th>
          <th>檢舉者帳號</th>
          <th>被檢舉者</th>
          <th>被檢舉者帳號</th>
          <th>類型</th>
          <th>目標ID</th>
          <th>原因</th>
          <th>操作</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="report in adminReportStore.reports" :key="report.reportId">
          <td>{{ report.reportId }}</td>
          <td>{{ formatMemberName(report.reporter || report) }}</td>
          <td>
            {{
              maskMemberAccount(
                report.reporterEmail || report.reporterAccount || report.reporterUserId,
              )
            }}
          </td>
          <td>{{ report.reportedUserName || `會員 ${report.reportedUserId || '-'}` }}</td>
          <td>
            {{
              maskMemberAccount(
                report.reportedUserEmail || report.reportedUserAccount || report.reportedUserId,
              )
            }}
          </td>
          <td>{{ report.postId ? '文章' : '留言' }}</td>
          <td>{{ report.postId || report.commentId }}</td>
          <td>{{ report.reason }}</td>
          <td>
            <button
              class="btn btn-success btn-sm me-2"
              @click="adminReportStore.approveReport(report.reportId)"
            >
              通過
            </button>
            <button
              class="btn btn-danger btn-sm"
              @click="adminReportStore.rejectReport(report.reportId)"
            >
              駁回
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
