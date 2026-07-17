import { userAuthStore } from '@/stores/auth'
import axios from 'axios'
import { refreshTokenApi } from './auth'
import Swal from 'sweetalert2'
import router from '@/router'

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

  router.push('/login')

  setTimeout(() => {
    isHandlingAuthExpired = false
  }, 300)
}

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

http.interceptors.request.use(
  (config) => {
    const authStore = userAuthStore()
    if (authStore.accessToken) {
      config.headers.Authorization = `Bearer ${authStore.accessToken}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

http.interceptors.response.use(
  (response) => {
    return response
  },
  async (error) => {
    const authStore = userAuthStore()
    const errorRequest = error.config

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

        authStore.setToken(response.data.data)
        errorRequest.headers.Authorization = `Bearer ${response.data.data.accessToken}`

        return http(errorRequest)
      } catch (refreshError) {
        await handleAuthExpired(refreshError, authStore)
        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  },
)

export default http
