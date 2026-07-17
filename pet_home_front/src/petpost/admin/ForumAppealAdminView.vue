<script setup>
import { onMounted } from 'vue'
import { useAdminAppealStore } from '@/petpost/stores/adminAppealStore'
import { formatMemberName, maskMemberAccount } from '@/petpost/utils/adminFormat'

const adminAppealStore = useAdminAppealStore()

onMounted(() => {
  adminAppealStore.loadAppeals()
})

function formatTargetType(appeal) {
  if (appeal.targetType === 'POST') return '文章'
  if (appeal.targetType === 'COMMENT') return '留言'
  return appeal.appealType || '-'
}
</script>

<template>
  <div class="admin-view">
    <p class="kicker">Forum Admin</p>
    <h1>申訴管理</h1>

    <div v-if="adminAppealStore.loading">載入中...</div>
    <div v-else-if="adminAppealStore.appeals.length === 0">目前沒有待審核申訴</div>

    <table v-else class="table table-bordered mt-3">
      <thead>
        <tr>
          <th>申訴ID</th>
          <th>會員名稱</th>
          <th>會員帳號</th>
          <th>類型</th>
          <th>目標ID</th>
          <th>主旨</th>
          <th>原因</th>
          <th>操作</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="appeal in adminAppealStore.appeals" :key="appeal.appealId">
          <td>{{ appeal.appealId }}</td>
          <td>{{ formatMemberName(appeal) }}</td>
          <td>{{ maskMemberAccount(appeal.userEmail) }}</td>
          <td>{{ formatTargetType(appeal) }}</td>
          <td>{{ appeal.targetId || appeal.postId || appeal.commentId || appeal.reportId }}</td>
          <td>{{ appeal.subject || '-' }}</td>
          <td>{{ appeal.reason }}</td>
          <td>
            <button
              class="btn btn-success btn-sm me-2"
              @click="adminAppealStore.approveAppeal(appeal.appealId)"
            >
              通過
            </button>
            <button
              class="btn btn-danger btn-sm"
              @click="adminAppealStore.rejectAppeal(appeal.appealId)"
            >
              駁回
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
