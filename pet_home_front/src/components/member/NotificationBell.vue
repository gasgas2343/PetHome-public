<script setup>
import { ref, watch } from 'vue'
import { Bell } from 'lucide-vue-next'
import {
  getAllnotificationsApi,
  getNotificationsCountApi,
  readAllNotificationsApi,
  readNotificationApi,
} from '@/api/user'

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['toggle', 'close'])

const notifications = ref([])
const unreadCount = ref(0)
const loading = ref(false)

async function loadNotifications() {
  loading.value = true

  try {
    const response = await getAllnotificationsApi()

    notifications.value = response.data.data || []
  } catch (error) {
    console.error('取得通知失敗', error)
  } finally {
    loading.value = false
  }
}

async function loadUnreadCount() {
  try {
    const response = await getNotificationsCountApi()
    unreadCount.value = response.data.data?.count || 0
  } catch (error) {
    console.error('取得未讀通知數失敗', error)
  }
}

async function handleRead(notification) {
  if (!notification.isRead) {
    try {
      await readNotificationApi(notification.id)
      notification.isRead = true
      unreadCount.value = Math.max(unreadCount.value - 1, 0)
    } catch (error) {
      console.error('通知設為已讀失敗', error)
    }
  }
  // 之後如果要依照 targetType 跳轉，可以寫在這裡
  // 例如 MEMBER_PROFILE -> router.push('/member/profile')
}

async function handleReadAll() {
  if (unreadCount.value <= 0) return

  try {
    await readAllNotificationsApi()

    notifications.value = notifications.value.map((notification) => ({
      ...notification,
      isRead: true,
      readAt: new Date().toISOString(),
    }))

    unreadCount.value = 0
  } catch (error) {
    console.error('全部通知已讀失敗', error)
  }
}

watch(
  () => props.open,
  async (isOpen) => {
    if (isOpen) {
      await loadNotifications()
      await loadUnreadCount()
    }
  },
)

loadUnreadCount()
</script>

<template>
  <div class="notification-wrapper">
    <button class="notification-btn" type="button" @click.stop="emit('toggle')">
      <Bell class="bell-icon" :size="22" :stroke-width="2.2" />

      <span v-if="unreadCount > 0" class="notification-badge">
        {{ unreadCount > 99 ? '99+' : unreadCount }}
      </span>
    </button>

    <div v-if="open" class="notification-panel">
      <div class="panel-title-row">
        <h3>通知中心</h3>
        <span>最新提醒</span>

        <button v-if="unreadCount > 0" class="read-all-btn" type="button" @click="handleReadAll">
          全部已讀
        </button>
      </div>

      <div class="notification-list">
        <div v-if="loading" class="notification-empty">載入中...</div>

        <div v-else-if="notifications.length === 0" class="notification-empty">目前沒有通知</div>

        <button
          v-for="notification in notifications"
          :key="notification.id"
          class="notification-item"
          :class="{ unread: !notification.isRead }"
          type="button"
          @click="handleRead(notification)"
        >
          <span class="notification-title">
            {{ notification.title }}
          </span>

          <span class="notification-content">
            {{ notification.content }}
          </span>
        </button>
      </div>

      <button class="view-all-btn" type="button" @click="emit('close')">查看全部通知</button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.notification-wrapper {
  position: relative;
}

.notification-btn {
  position: relative;
  width: 38px;
  height: 38px;
  border: none;
  border-radius: 12px;
  background: transparent;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition:
    background 0.18s ease,
    transform 0.18s ease;

  &:hover {
    background: rgba(224, 163, 84, 0.14);
    transform: translateY(-1px);
  }

  &:active {
    transform: scale(0.94);
  }
}

.notification-badge {
  position: absolute;
  top: -4px;
  right: -6px;
  min-width: 17px;
  height: 17px;
  padding: 0 5px;
  border-radius: 999px;
  background: #d85c45;
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  line-height: 17px;
  text-align: center;
  box-shadow: 0 2px 6px rgba(126, 72, 49, 0.22);
}

.notification-btn:hover .bell-icon {
  color: #9c6633;
  transform: rotate(-8deg);
}

.bell-icon {
  color: #7a5a3a;
  transition:
    color 0.18s ease,
    transform 0.18s ease;
}

.notification-panel {
  position: absolute;
  top: 56px;
  right: 0;
  width: 300px;
  padding: 18px;
  border-radius: 24px;
  background: rgba(255, 250, 242, 0.98);
  border: 1px solid rgba(132, 92, 52, 0.16);
  box-shadow: 0 18px 42px rgba(68, 45, 25, 0.18);
  z-index: 200;
}

.panel-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(132, 92, 52, 0.14);

  h3 {
    margin: 0;
    color: #4f3928;
    font-size: 17px;
    font-weight: 900;
  }

  span {
    color: #a58464;
    font-size: 12px;
    font-weight: 700;
  }
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 14px 0;
}

.notification-item {
  width: 100%;
  padding: 12px 14px;
  border: none;
  border-radius: 16px;
  background: #fffaf3;
  text-align: left;
  cursor: pointer;
  transition:
    background 0.2s ease,
    transform 0.2s ease,
    box-shadow 0.2s ease;

  &:hover {
    background: #fff3df;
    transform: translateY(-1px);
  }
}

.read-all-btn {
  border: 0;
  background: #fff3df;
  color: #8a5635;
  border-radius: 999px;
  padding: 5px 10px;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
  transition:
    background 0.2s ease,
    transform 0.2s ease;
}

.read-all-btn:hover {
  background: #f5dfbd;
  transform: translateY(-1px);
}

.notification-item.unread {
  background: #fff1dc;
  box-shadow: inset 4px 0 0 #d98c54;
}

.notification-item.unread .notification-title {
  color: #6f3f24;
  font-weight: 800;
}

.notification-item.unread .notification-title::after {
  content: '未讀';
  margin-left: 8px;
  padding: 2px 7px;
  border-radius: 999px;
  background: #d98c54;
  color: #fff;
  font-size: 11px;
  font-weight: 700;
}

.notification-item:not(.unread) {
  opacity: 0.72;
}

.notification-item:not(.unread) .notification-title {
  color: #8a715f;
  font-weight: 600;
}

.notification-item:not(.unread) .notification-content {
  color: #9b8778;
}

.notification-title {
  display: block;
  color: #513b2a;
  font-size: 14px;
  font-weight: 900;
}

.notification-content {
  display: block;
  margin-top: 5px;
  color: #8b6d50;
  font-size: 13px;
  line-height: 1.5;
}

.view-all-btn {
  width: 100%;
  padding: 10px 12px;
  border: none;
  border-radius: 999px;
  background: #e0a354;
  color: #fff;
  font-size: 14px;
  font-weight: 800;
  cursor: pointer;
}
</style>
