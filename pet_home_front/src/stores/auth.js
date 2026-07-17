import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { getMeApi } from '@/api/user'
import { refreshTokenApi } from '@/api/auth'

export const userAuthStore = defineStore('auth', () => {
  const id = ref(null)
  const email = ref('')
  const name = ref('')
  const avatarUrl = ref('')
  const status = ref('')
  const emailVerified = ref(false)
  const twoFactorEnabled = ref(false)
  const accessToken = ref('')
  const accessTokenExpiresIn = ref('')
  const roles = ref([])
  const modules = ref([])
  const modulesLoaded = ref(false)

  const isLogin = computed(() => !!accessToken.value)

  const canAccessBackstage = computed(() => {
    return (roles.value || []).includes('ADMIN')
  })

  function setLoginData(data) {
    id.value = data.id
    email.value = data.email
    name.value = data.nickName
    avatarUrl.value = data.avatarUrl
    roles.value = data.roles
    accessToken.value = data.accessToken
    accessTokenExpiresIn.value = data.accessTokenExpiresIn

    if (data.refreshToken) {
      localStorage.setItem('refreshToken', data.refreshToken)
    }

    if (data.refreshTokenExpiresIn) {
      localStorage.setItem('refreshTokenExpiresIn', data.refreshTokenExpiresIn)
    }
  }

  function setToken(data) {
    accessToken.value = data.accessToken
    accessTokenExpiresIn.value = data.accessTokenExpiresIn

    if (data.refreshToken) {
      localStorage.setItem('refreshToken', data.refreshToken)
    }

    if (data.refreshTokenExpiresIn) {
      localStorage.setItem('refreshTokenExpiresIn', data.refreshTokenExpiresIn)
    }
  }

  function setUser(data) {
    id.value = data.id
    email.value = data.email
    avatarUrl.value = data.avatarUrl || avatarUrl.value
    name.value = data.nickName
    status.value = data.status
    emailVerified.value = data.emailVerified
    twoFactorEnabled.value = data.twoFactorEnabled
    roles.value = Array.isArray(data.roles) ? data.roles : []
  }

  function hasRoles(roleCode) {
    return roles.value.includes(roleCode)
  }

  function setModules(data) {
    modules.value = data.modules
    modulesLoaded.value = true
  }

  function hasModules(moduleCode) {
    return modules.value.some((module) => module.moduleCode === moduleCode)
  }

  function updateProfile(data) {
    if (data.nickname !== undefined) {
      this.nickname = data.nickname
    }

    if (data.avatarUrl !== undefined) {
      this.avatarUrl = data.avatarUrl
    }

    if(data.emailVerified !== undefined){
      this.emailVerified = data.emailVerified
    }

    if(data.twoFactorEnabled !== undefined){
      this.twoFactorEnabled = data.twoFactorEnabled
    }
  }

  function clearStore() {
    id.value = null
    email.value = ''
    name.value = ''
    avatarUrl.value = ''
    status.value = ''
    emailVerified.value = false
    twoFactorEnabled.value = false
    accessToken.value = ''
    accessTokenExpiresIn.value = ''
    roles.value = []
    modules.value = []
    modulesLoaded.value = false

    localStorage.removeItem('refreshToken')
    localStorage.removeItem('refreshTokenExpiresIn')
  }

  async function initAuth() {
    const refreshTokenValue = localStorage.getItem('refreshToken')
    if (!refreshTokenValue) {
      clearStore()
      return
    }
    try {
      const response = await refreshTokenApi({
        refreshToken: refreshTokenValue,
      })

      const tokenData = response.data.data
      setToken(tokenData)
      const meResponse = await getMeApi()
      const userData = meResponse.data.data

      setUser(userData)
    } catch (error) {
      clearStore()
    }
  }

  return {
    id,
    email,
    name,
    avatarUrl,
    emailVerified,
    twoFactorEnabled,
    accessToken,
    accessTokenExpiresIn,
    roles,
    modules,
    modulesLoaded,
    isLogin,
    canAccessBackstage,
    setLoginData,
    setToken,
    setUser,
    clearStore,
    hasRoles,
    setModules,
    hasModules,
    updateProfile,
    initAuth,
  }
})
