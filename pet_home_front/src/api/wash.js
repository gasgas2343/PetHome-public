import { userAuthStore } from '@/stores/auth'
import axios from 'axios'
import { refreshTokenApi } from './auth'

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
      !errorRequest._retry &&
      !errorRequest.url.includes('/auth/refresh')
    ) {
      errorRequest._retry = true

      try {
        const findRefreshToken = localStorage.getItem('refreshToken')

        if (!findRefreshToken) {
          authStore.clearStore()
          return Promise.reject(error)
        }
        const requestBody = { refreshToken: findRefreshToken }
        const response = await refreshTokenApi(requestBody)

        authStore.setToken(response.data.data)
        errorRequest.headers.Authorization = `Bearer ${response.data.data.accessToken}`

        return http(errorRequest)
      } catch (refreshError) {
        authStore.logout()
        return Promise.reject(refreshError)
      }
    }
    return Promise.reject(error)
  },
)

export default http
