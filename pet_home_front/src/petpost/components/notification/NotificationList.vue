<script setup>
import NotificationItem from "./NotificationItem.vue";

defineProps({
  notifications: {
    type: Array,

    required: true,
  },
});
</script>

<template>
  <NotificationItem
    v-for="notification in notifications"
    :key="notification.notificationId"
    :notification="notification"
  />
</template>

<style scoped></style>
