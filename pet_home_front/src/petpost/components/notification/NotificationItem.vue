<script setup>
// src/components/notification/NotificationItem.vue
// 中文註解：單一通知卡片。

import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/petpost/stores/notificationStore'

const props = defineProps({
  notification: {
    type: Object,
    required: true,
  },
})

const router = useRouter()
const notificationStore = useNotificationStore()

const notificationId = computed(() => props.notification.notificationId)

const typeText = computed(() => {
  const map = {
    POST_LIKE: '文章按讚',
    POST_COMMENT: '文章留言',
    POST_FAVORITE: '文章收藏',
    POST_SHARE: '文章分享',
    COMMENT_REPLY: '留言回覆',
    COMMENT_LIKE: '留言按讚',
    REPORT_RESULT: '檢舉結果',
    APPEAL_RESULT: '申訴結果',
    SYSTEM: '系統通知',

    COMMENT: '文章留言',
    FAVORITE: '文章收藏',
    REPORT: '檢舉結果',
    APPEAL: '申訴結果',
  }

  return map[props.notification.type] || '通知'
})

const typeIcon = computed(() => {
  const map = {
    POST_LIKE: 'bi-hand-thumbs-up',
    POST_COMMENT: 'bi-chat-dots',
    POST_FAVORITE: 'bi-bookmark-heart',
    POST_SHARE: 'bi-share',
    COMMENT_REPLY: 'bi-reply',
    REPORT_RESULT: 'bi-flag',
    APPEAL_RESULT: 'bi-shield-check',
    SYSTEM: 'bi-megaphone',
  }

  return map[props.notification.type] || 'bi-bell'
})

function formatTime(time) {
  if (!time) return ''

  return new Date(time).toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

async function handleClick() {
  await notificationStore.readNotification(notificationId.value)

  const referenceType = props.notification.referenceType
  const referenceId = props.notification.referenceId

  // 中文註解：目前論壇通知主要跳文章頁。
  if (referenceType === 'POST' && referenceId) {
    router.push(`/post/${referenceId}`)
  }
}

function handleDelete(event) {
  event.stopPropagation()
  notificationStore.removeNotification(notificationId.value)
}
</script>

<template>
  <article class="notification-item" :class="{ unread: !notification.isRead }" @click="handleClick">
    <div class="notification-icon">
      <i :class="['bi', typeIcon]"></i>
    </div>

    <div class="notification-content">
      <div class="notification-top">
        <span class="notification-type">
          {{ typeText }}
        </span>

        <span v-if="!notification.isRead" class="unread-dot"></span>
      </div>

      <p class="notification-message">
        {{ notification.message }}
      </p>

      <time class="notification-time">
        {{ formatTime(notification.createdAt) }}
      </time>
    </div>

    <button class="delete-btn" type="button" @click="handleDelete">
      <i class="bi bi-x-lg"></i>
    </button>
  </article>
</template>

<style scoped>
.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 16px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(139, 94, 60, 0.12);
  cursor: pointer;
  transition: 0.2s ease;
}

.notification-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(92, 64, 51, 0.12);
}

.notification-item.unread {
  background: rgba(255, 246, 232, 0.95);
  border-color: rgba(139, 94, 60, 0.35);
}

.notification-icon {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: #f1dfcc;
  color: #7a4f35;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
}

.notification-top {
  display: flex;
  align-items: center;
  gap: 8px;
}

.notification-type {
  font-size: 0.85rem;
  font-weight: 700;
  color: #7a4f35;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #dc3545;
}

.notification-message {
  margin: 6px 0;
  color: #4b3428;
}

.notification-time {
  font-size: 0.8rem;
  color: #9a7a65;
}

.delete-btn {
  border: none;
  background: transparent;
  color: #9a7a65;
  cursor: pointer;
}

.delete-btn:hover {
  color: #dc3545;
}
</style>
