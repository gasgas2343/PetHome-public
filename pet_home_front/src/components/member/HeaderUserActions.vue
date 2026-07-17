<script setup>
import { computed, ref, onBeforeUnmount, onMounted } from 'vue'
import { userAuthStore } from '@/stores/auth'
import NotificationBell from '@/components/member/NotificationBell.vue'
import AccountAvatarMenu from '@/components/member/AccountAvatarMenu.vue'

const authStore = userAuthStore()
const wrapperRef = ref(null)
const activePanel = ref(null)

const isLogin = computed(() => authStore.isLogin)

function togglePanel(panelName) {
  activePanel.value = activePanel.value === panelName ? null : panelName
}

function closePanel() {
  activePanel.value = null
}

function handleClickOutside(event) {
  if (wrapperRef.value && !wrapperRef.value.contains(event.target)) {
    closePanel()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <div ref="wrapperRef" class="header-user-actions">
    <template v-if="!isLogin">
      <RouterLink to="/login" class="login-link">登入</RouterLink>
      <RouterLink to="/register" class="register-btn">註冊</RouterLink>
    </template>

    <template v-else>
      <NotificationBell
        :open="activePanel === 'notification'"
        @toggle="togglePanel('notification')"
        @close="closePanel"
      />

      <AccountAvatarMenu
        :open="activePanel === 'account'"
        @toggle="togglePanel('account')"
        @close="closePanel"
      />
    </template>
  </div>
</template>

<style scoped lang="scss">
.header-user-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.login-link {
  color: #5f4a38;
  font-size: 15px;
  font-weight: 800;
  text-decoration: none;
}

.register-btn {
  padding: 10px 25px;
  border-radius: 999px;
  background: #e0a354;
  color: #fff;
  font-size: 15px;
  font-weight: 800;
  text-decoration: none;
  box-shadow: 0 8px 18px rgba(185, 124, 45, 0.22);
}
</style>
