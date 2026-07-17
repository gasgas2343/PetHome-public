import { defineStore } from 'pinia'
import { ref } from 'vue'

import { findAdminUsers, suspendAdminUser, activateAdminUser } from '@/petpost/api/adminUser'

export const useAdminUserStore = defineStore('adminUser', () => {
  const users = ref([])
  const loading = ref(false)
  const errorMessage = ref('')

  function unwrapApiData(response) {
    const body = response?.data

    if (body && typeof body === 'object' && Object.prototype.hasOwnProperty.call(body, 'data')) {
      return body.data
    }

    return body
  }

  async function loadUsers() {
    loading.value = true
    errorMessage.value = ''

    try {
      const response = await findAdminUsers()
      const data = unwrapApiData(response)

      users.value = Array.isArray(data) ? data : []
    } catch (error) {
      console.error('查詢會員失敗', error)
      users.value = []
      errorMessage.value = error?.response?.data?.message || error?.message || '查詢會員失敗'
    } finally {
      loading.value = false
    }
  }

  async function suspendUser(userId) {
    if (!confirm('確定要停權此會員的論壇功能？')) {
      return
    }

    try {
      await suspendAdminUser(userId)
      await loadUsers()
    } catch (error) {
      console.error('停權會員失敗', error)
      alert(error?.response?.data?.message || error?.message || '停權會員失敗')
    }
  }

  async function activateUser(userId) {
    if (!confirm('確定要解除此會員的論壇停權？')) {
      return
    }

    try {
      await activateAdminUser(userId)
      await loadUsers()
    } catch (error) {
      console.error('解除停權失敗', error)
      alert(error?.response?.data?.message || error?.message || '解除停權失敗')
    }
  }

  return {
    users,
    loading,
    errorMessage,
    loadUsers,
    suspendUser,
    activateUser,
  }
})
