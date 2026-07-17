<script setup>
import { onMounted } from 'vue'
import { useAdminNotificationStore } from '@/petpost/stores/adminNotificationStore'
import { formatMemberName, maskMemberAccount } from '@/petpost/utils/adminFormat'

const adminNotificationStore = useAdminNotificationStore()

onMounted(() => {
  adminNotificationStore.loadNotifications()
})
</script>

<template>
  <div class="admin-view">
    <p class="kicker">Forum Admin</p>
    <h1>通知紀錄管理</h1>

    <button class="btn-delete-all" @click="adminNotificationStore.deleteAllNotifications">
      刪除所有通知紀錄
    </button>

    <div v-if="adminNotificationStore.loading">載入中...</div>
    <div v-else-if="adminNotificationStore.notifications.length === 0">目前沒有通知紀錄</div>

    <table v-else class="table table-bordered mt-3">
      <thead>
        <tr>
          <th>通知ID</th>
          <th>會員名稱</th>
          <th>會員帳號</th>
          <th>類型</th>
          <th>內容</th>
          <th>時間</th>
          <th>操作</th>
        </tr>
      </thead>

      <tbody>
        <tr
          v-for="notification in adminNotificationStore.notifications"
          :key="notification.notificationId"
        >
          <td>{{ notification.notificationId }}</td>
          <td>{{ formatMemberName(notification) }}</td>
          <td>{{ maskMemberAccount(notification.userEmail) }}</td>
          <td>{{ notification.type }}</td>
          <td>{{ notification.message }}</td>
          <td>{{ notification.createdAt }}</td>
          <td>
            <button
              class="btn-delete"
              @click="adminNotificationStore.deleteNotification(notification.notificationId)"
            >
              刪除
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.btn-delete-all {
  border: none;
  border-radius: 999px;
  padding: 8px 16px;
  background: #8b3a2b;
  color: white;
  font-weight: 700;
}

.btn-delete {
  border: none;
  border-radius: 999px;
  padding: 6px 12px;
  background: #9b6239;
  color: white;
}
</style>
