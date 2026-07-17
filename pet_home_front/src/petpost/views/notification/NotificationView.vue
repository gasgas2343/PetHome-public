<script setup>
// src/views/notification/NotificationView.vue

import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import NotificationItem from '@/petpost/components/notification/NotificationItem.vue'
import { useNotificationStore } from '@/petpost/stores/notificationStore'
import { userAuthStore } from '@/stores/auth'

const notificationStore = useNotificationStore()
const authStore = userAuthStore()

const { notifications, loading, errorMessage, unreadCount } = storeToRefs(notificationStore)

onMounted(async () => {
  if (!authStore.isLogin) return

  await notificationStore.fetchNotifications()
  await notificationStore.fetchUnreadCount()
})

function handleReadAll() {
  if (!authStore.isLogin) return

  notificationStore.readAllNotifications()
}
</script>

<template>
  <main class="notification-page">
    <section class="notification-panel">
      <div class="notification-header">
        <div>
          <h2>通知中心</h2>
          <p>查看你的文章、留言、按讚、收藏與檢舉通知</p>
        </div>

        <button v-if="unreadCount > 0" class="read-all-btn" type="button" @click="handleReadAll">
          全部標記已讀
        </button>
      </div>

      <div v-if="loading" class="state-text">通知載入中...</div>

      <div v-else-if="errorMessage" class="state-text error">
        {{ errorMessage }}
      </div>

      <div v-else-if="notifications.length === 0" class="state-text">目前沒有通知</div>

      <div v-else class="notification-list">
        <NotificationItem
          v-for="item in notifications"
          :key="item.notificationId"
          :notification="item"
        />
      </div>
    </section>
  </main>
</template>

<style scoped>
.notification-page {
  min-height: 100vh;
  padding: 40px 16px;
  background: linear-gradient(135deg, #f7efe6, #ead7c2);
}

.notification-panel {
  max-width: 900px;
  margin: 0 auto;
  padding: 28px;
  border-radius: 24px;
  background: rgba(255, 252, 247, 0.82);
  box-shadow: 0 16px 40px rgba(92, 64, 51, 0.16);
  backdrop-filter: blur(12px);
}

.notification-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 24px;
}

.notification-header h2 {
  margin: 0;
  color: #4b3428;
  font-weight: 700;
}

.notification-header p {
  margin: 6px 0 0;
  color: #8a6a55;
}

.read-all-btn {
  border: none;
  border-radius: 999px;
  padding: 8px 16px;
  background-color: #8b5e3c;
  color: #fff;
  cursor: pointer;
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.state-text {
  padding: 32px;
  text-align: center;
  color: #8a6a55;
}

.state-text.error {
  color: #dc3545;
}
</style>
