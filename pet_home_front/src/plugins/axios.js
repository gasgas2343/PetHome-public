import axios from 'axios'
import { userAuthStore } from '@/stores/auth'
import { refreshTokenApi } from '@/api/auth'
import Swal from 'sweetalert2'

const washBaseURL =
  import.meta.env.DEV
    ? '/api'
    : import.meta.env.VITE_WASH_BACKEND_API ||
      import.meta.env.VITE_BACKEND_API ||
      ''

const instance = axios.create({
  baseURL: washBaseURL,
})

// 請求攔截器：自動帶入 token
instance.interceptors.request.use((config) => {
  const authStore = userAuthStore()
  const token = authStore.accessToken || sessionStorage.getItem('washAccessToken')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

let isHandlingAuthExpired = false

function getAuthExpiredMessage(error) {
  const code = error.response?.data?.code

  const map = {
    SESSION_REVOKED: '你的登入狀態已失效，請重新登入。',
    TOKEN_VERSION_CHANGED: '你的帳號安全設定已變更，請重新登入。',
    PERMISSION_VERSION_CHANGED: '你的角色或權限已變更，請重新登入。',
    REFRESH_TOKEN_EXPIRED: '登入已過期，請重新登入。',
    REFRESH_TOKEN_INVALID: '登入驗證失效，請重新登入。',
  }

  return map[code] || '登入狀態已失效，請重新登入。'
}

async function handleAuthExpired(error, authStore) {
  if (isHandlingAuthExpired) return

  isHandlingAuthExpired = true

  authStore.clearStore()
  localStorage.removeItem('refreshToken')

  await Swal.fire({
    icon: 'warning',
    title: '請重新登入',
    text: getAuthExpiredMessage(error),
    confirmButtonText: '確定',
    customClass: {
      popup: 'maokilai-swal-popup',
      title: 'maokilai-swal-title',
      htmlContainer: 'maokilai-swal-text',
      confirmButton: 'maokilai-swal-confirm',
    },
    buttonsStyling: false,
  })

  window.location.href = '/login'

  setTimeout(() => {
    isHandlingAuthExpired = false
  }, 300)
}

// 回應攔截器：處理 401 自動刷新 Token 與 403 導頁
instance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const authStore = userAuthStore()
    const errorRequest = error.config

    // 處理 403
    if (error.response && error.response.status === 403) {
      window.location.href = '/403'
      return Promise.reject(error)
    }

    // 處理 401 且非刷新 Token 的請求
    if (
      error.response?.status === 401 &&
      errorRequest &&
      !errorRequest._retry &&
      !errorRequest.url.includes('/auth/refresh')
    ) {
      errorRequest._retry = true

      try {
        const findRefreshToken = localStorage.getItem('refreshToken')

        if (!findRefreshToken) {
          await handleAuthExpired(error, authStore)
          return Promise.reject(error)
        }

        const requestBody = { refreshToken: findRefreshToken }
        const response = await refreshTokenApi(requestBody)

        // 成功取得新 Token，更新 authStore
        authStore.setToken(response.data.data)
        
        // 更新原請求的 Authorization Header 並重新發送
        errorRequest.headers.Authorization = `Bearer ${response.data.data.accessToken}`
        return instance(errorRequest)
      } catch (refreshError) {
        await handleAuthExpired(refreshError, authStore)
        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  },
)

export default instance