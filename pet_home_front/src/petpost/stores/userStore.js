import { defineStore } from 'pinia'
import { computed } from 'vue'

import { userAuthStore } from '@/stores/auth'

// 中文註解：petpost 舊版相容 Store。
// 新程式請直接使用 '@/stores/auth' 的 userAuthStore。
// 此檔只保留給尚未完全移除的舊 import，避免再讀 localStorage token/user。
export const useUserStore = defineStore('petpostLegacyUser', () => {
  const authStore = userAuthStore()

 const userId = computed(() => Number(authStore.id || 0))
  const username = computed(() => authStore.name || authStore.email || '')
  const role = computed(() => (authStore.isAdmin ? 'ADMIN' : 'USER'))
  const isLogin = computed(() => authStore.isLogin)
  const isAdmin = computed(() => authStore.isAdmin)

  function login() {
    console.warn('petpost/stores/userStore.js 已棄用，請改用 stores/auth.js')
  }

  function logout() {
    authStore.clearStore()
  }

  function switchRole() {
    console.warn('petpost/stores/userStore.js 已棄用，請改用 authStore.roles / authStore.permissions')
  }

  return {
    userId,
    username,
    role,
    isLogin,
    isAdmin,
    login,
    logout,
    switchRole,
  }
})
