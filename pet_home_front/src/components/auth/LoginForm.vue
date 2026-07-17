<script setup>
import { loginApi } from '@/api/auth'
import router from '@/router'
import { userAuthStore } from '@/stores/auth'
import { reactive, ref } from 'vue'
import { Eye, EyeOff } from 'lucide-vue-next'
import Swal from 'sweetalert2'

const form = reactive({
  email: '',
  password: '',
})

const errors = reactive({
  email: '',
  password: '',
})

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const loading = ref(false)
const apiError = ref('')
const shakeKey = ref(0)
const authStore = userAuthStore()
const showPassword = ref(false)

function togglePassword() {
  showPassword.value = !showPassword.value
}

function validForm() {
  errors.email = ''
  errors.password = ''

  let result = true

  if (!form.email) {
    errors.email = '請輸入 Email'
    result = false
  } else if (!emailRegex.test(form.email)) {
    errors.email = 'Email 格式不正確'
    result = false
  }
  if (!form.password) {
    errors.password = '請輸入密碼'
    result = false
  }
  return result
}

async function handleSubmit() {
  if (loading.value) return

  if (!validForm()) {
    shakeKey.value++
    return
  }

  loading.value = true
  apiError.value = ''

  try {
    const requestBody = {
      email: form.email,
      password: form.password,
    }

    const response = await loginApi(requestBody)
    const data = response.data.data

    console.log('login data:', data)

    if (data.requiresTwoFactor) {
      if (!data.loginChallengeToken) {
        apiError.value = '登入驗證資料異常，請重新登入'
        console.error('缺少 loginChallengeToken:', data)
        return
      }

      sessionStorage.setItem('twoFactorToken', data.loginChallengeToken)

      await router.replace('/two-factor-verify')
      return
    }

    authStore.setLoginData(data)

    await Swal.fire({
      icon: 'success',
      title: '登入成功',
      text: '歡迎回來毛起來',
      timer: 900,
      showConfirmButton: false,
      background: '#fffaf2',
      color: '#3f2a1d',
      customClass: {
        popup: 'maokilai-swal-popup',
        title: 'maokilai-swal-title',
        htmlContainer: 'maokilai-swal-text',
      },
    })

    await router.replace({
      path: '/',
      query: {
        login: 'success',
      },
    })
  } catch (error) {
    console.error('登入失敗:', error)
    console.error('後端錯誤內容:', error.response?.data)

    apiError.value = error.response?.data?.message || '帳號或密碼錯誤'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <form class="auth-form" @submit.prevent="handleSubmit" novalidate>
    <label class="form-field">
      <span>Email</span>
      <input
        v-model="form.email"
        type="email"
        placeholder="請輸入 Email"
        class="auth-input"
        :class="{ 'is-invalid': errors.email }"
      />
      <p :key="`email-${shakeKey}`" class="field-error" :class="{ 'is-shake': errors.email }">
        {{ errors.email || '\u00A0' }}
      </p>
    </label>

    <label class="form-field">
      <span>密碼</span>
      <div class="password-input-wrap">
        <input
          v-model="form.password"
          :type="showPassword ? 'text' : 'password'"
          placeholder="請輸入密碼"
          class="auth-input password-input"
          :class="{ 'is-invalid': errors.password }"
          autocomplete="current-password"
        />

        <button
          type="button"
          class="password-eye-btn"
          @click="togglePassword"
          :aria-label="showPassword ? '隱藏密碼' : '顯示密碼'"
        >
          <EyeOff v-if="showPassword" :size="20" :stroke-width="2.2" />
          <Eye v-else :size="20" :stroke-width="2.2" />
        </button>
      </div>
      <p :key="`pwd-${shakeKey}`" class="field-error" :class="{ 'is-shake': errors.password }">
        {{ errors.password || '\u00A0' }}
      </p>
    </label>
    <p :key="`api-${shakeKey}`" class="api-error" :class="{ 'is-visible': apiError }">
      {{ apiError || 'placeholder' }}
    </p>

    <div class="form-helper">
      <RouterLink to="/forgot-password">忘記密碼？</RouterLink>
    </div>

    <button class="primary-btn" type="submit" :disabled="loading">
      {{ loading ? '登入中...' : '登入' }}
    </button>

    <button class="line-btn" type="button">使用 LINE 登入</button>

    <p class="switch-text">
      還沒有帳號？
      <RouterLink to="/register">立即註冊</RouterLink>
    </p>
  </form>
</template>

<style scoped lang="scss">
.auth-form {
  display: grid;
  gap: 18px;
}

.api-error {
  min-height: 22px;
  margin: 0;
  padding-left: 20px;
  color: #c44736;
  font-size: 0.9rem;
  line-height: 1.5;
  visibility: hidden;

  &.is-visible {
    visibility: visible;
    animation: error-shake 0.32s ease;
  }
}

.auth-input {
  border: 1px solid rgba(120, 88, 58, 0.22);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background-color 0.2s ease;

  &.is-invalid {
    border-color: #d56a5b;
    background-color: #fff8f5;
    box-shadow: 0 0 0 3px rgba(213, 106, 91, 0.16);
  }
}

.field-error {
  height: 16px;
  margin: 4px 0 0;
  font-size: 12px;
  line-height: 16px;
  color: #c75c4a;

  &.is-shake {
    animation: error-shake 0.24s ease;
  }
}

@keyframes error-shake {
  0% {
    transform: translateX(0);
  }

  25% {
    transform: translateX(-3px);
  }

  50% {
    transform: translateX(3px);
  }

  75% {
    transform: translateX(-2px);
  }

  100% {
    transform: translateX(0);
  }
}

.form-field {
  input {
    width: 100%;
    height: 48px;
    padding: 0 16px;
    border-radius: 14px;
    border: 1px solid rgba(121, 91, 61, 0.22);
    background: #fffaf3;
    color: #4b3828;
    outline: none;
    font-size: 1rem;

    &:focus {
      border-color: rgba(93, 145, 159, 0.86);
      box-shadow: 0 0 0 4px rgba(93, 145, 159, 0.14);
    }

    &.is-invalid,
    &.is-invalid:focus {
      border-color: #d56a5b;
      background-color: #fff8f5;
      box-shadow: 0 0 0 3px rgba(213, 106, 91, 0.16);
    }
  }
}

.form-helper {
  display: flex;
  justify-content: flex-end;
  margin-top: -4px;

  a {
    color: #5d919f;
    font-size: 0.92rem;
    font-weight: 700;
    text-decoration: none;
  }
}

.primary-btn,
.line-btn {
  height: 50px;
  border: none;
  border-radius: 999px;
  font-weight: 800;
  font-size: 1rem;
  letter-spacing: 0.06em;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-2px);
  }
}

.primary-btn {
  margin-top: 4px;
  background: #5d919f;
  color: #fff;

  &:hover {
    box-shadow: 0 12px 24px rgba(93, 145, 159, 0.26);
  }
}

.line-btn {
  background: #3e9b5b;
  color: #ffffff;
  border: 1px solid rgba(47, 143, 78, 0.28);

  &:hover {
    box-shadow: 0 12px 24px rgba(47, 143, 78, 0.12);
  }
}

.switch-text {
  margin: 8px 0 0;
  text-align: center;
  color: rgba(75, 56, 40, 0.68);

  a {
    color: #5d919f;
    font-weight: 800;
    text-decoration: none;
  }
}

.password-input-wrap {
  position: relative;
  width: 100%;
}

.password-input {
  padding-right: 52px;
}

.password-eye-btn {
  position: absolute;
  top: 50%;
  right: 14px;
  transform: translateY(-50%);

  width: 34px;
  height: 34px;
  border: 0;
  border-radius: 50%;
  background: transparent;
  color: #8a7764;

  display: flex;
  align-items: center;
  justify-content: center;

  cursor: pointer;
  transition:
    background 0.18s ease,
    color 0.18s ease,
    transform 0.18s ease;
}

.password-eye-btn:hover {
  background: rgba(138, 113, 89, 0.12);
  color: #5f9baa;
}

.password-eye-btn:active {
  transform: translateY(-50%) scale(0.94);
}
</style>
