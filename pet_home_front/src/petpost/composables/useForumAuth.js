import { computed } from 'vue'
import { userAuthStore } from '@/stores/auth'

export function useForumAuth() {
  const authStore = userAuthStore()

  // 中文註解：論壇模組統一使用 main 登入狀態。
  const isLoggedIn = computed(() => authStore.isLogin)

  // 中文註解：論壇模組統一使用 loginUserId，不再到處判斷 authStore.userId。
  const loginUserId = computed(() => {
    return Number(authStore.id || 0)
  })

  function requireLogin(message = '請先登入後再操作') {
    if (!isLoggedIn.value) {
      alert(message)
      return false
    }

    return true
  }

  return {
    authStore,
    isLoggedIn,
    loginUserId,
    requireLogin,
  }
}
