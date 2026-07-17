<script setup>
import { onMounted } from 'vue'
import { useNotificationStore } from '@/petpost/stores/notificationStore'
const notificationStore = useNotificationStore()
onMounted(async () => {
    await notificationStore.loadNotifications()
    await notificationStore.loadUnreadCount()
})
function openNotification(notification){
    notificationStore.readNotification(notification.notificationId)
}
</script>