<script setup>
import { onMounted, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/petpost/stores/notificationStore'
import { userAuthStore } from '@/stores/auth'

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['toggle', 'close'])

const router = useRouter()
const notificationStore = useNotificationStore()
const authStore = userAuthStore()

const { unreadCount, hasUnread, notifications } = storeToRefs(notificationStore)

async function loadNotifications() {
  if (!authStore.isLogin) return
  await notificationStore.fetchNotifications()
  await notificationStore.fetchUnreadCount()
}

onMounted(() => {
  loadNotifications()
})

watch(
  () => authStore.isLogin,
  (isLogin) => {
    if (isLogin) loadNotifications()
  },
)

function toggleDropdown() {
  emit('toggle')
}

async function openNotification(notification) {
  if (!notification.read && notification.id) {
    await notificationStore.readNotification(notification.id)
  }

  emit('close')

  if (notification.moduleCode === 'FORUM' && notification.targetType === 'POST') {
    router.push(`/community/posts/${notification.targetId}`)
    return
  }

  if (notification.moduleCode === 'FORUM' && notification.targetType === 'APPEAL') {
    router.push('/community/posts')
    return
  }

  if (notification.targetType === 'MEMBER_PROFILE') {
    router.push('/member/profile')
    return
  }

  if (notification.targetType === 'EMAIL_VERIFY') {
    router.push('/member/security')
  }
}

async function readAll() {
  await notificationStore.readAllNotifications()
}
</script>

<template>
  <div class="notification-wrapper">
    <button class="notification-bell" type="button" @click.stop="toggleDropdown">
      <i class="bi bi-bell"></i>

      <span v-if="hasUnread" class="notification-badge">
        {{ unreadCount > 99 ? '99+' : unreadCount }}
      </span>
    </button>

    <div v-if="props.open" class="notification-panel" @click.stop>
      <div class="panel-header">
        <strong>通知中心</strong>
        <button type="button" class="read-all-btn" @click="readAll">全部已讀</button>
      </div>

      <div v-if="notificationStore.loading" class="notification-empty">載入中...</div>

      <div v-else-if="notifications.length === 0" class="notification-empty">目前沒有通知</div>

      <button
        v-for="notification in notifications.slice(0, 5)"
        :key="notification.id"
        type="button"
        class="notification-item"
        :class="{ unread: !notification.read }"
        @click="openNotification(notification)"
      >
        <strong>{{ notification.title }}</strong>
        <p>{{ notification.content }}</p>
      </button>

      <RouterLink class="view-all-btn" to="/member/notifications" @click="emit('close')">
        查看全部通知
      </RouterLink>
    </div>
  </div>
</template>
