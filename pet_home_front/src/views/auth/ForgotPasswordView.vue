<script setup>
import { reactive, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { forgotPasswordApi } from '@/api/auth'
import Swal from 'sweetalert2'

const form = reactive({
  email: '',
})

const errors = reactive({
  email: '',
})

const loading = ref(false)
const apiError = ref('')
const successMessage = ref('')
const shakeKey = ref(0)

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function validateForm() {
  errors.email = ''
  apiError.value = ''
  successMessage.value = ''

  let valid = true

  if (!form.email.trim()) {
    errors.email = '請輸入 Email'
    valid = false
  } else if (!emailRegex.test(form.email)) {
    errors.email = 'Email 格式不正確'
    valid = false
  }

  if (!valid) {
    shakeKey.value++
  }

  return valid
}

async function submitForgotPassword() {
  if (!validateForm()) return

  loading.value = true
  apiError.value = ''
  successMessage.value = ''

  Swal.fire({
    title: '寄送中',
    text: '正在寄出重設密碼信，請稍候...',
    allowOutsideClick: false,
    allowEscapeKey: false,
    showConfirmButton: false,
    background: '#fffaf2',
    customClass: {
      popup: 'maokilai-swal-popup',
      title: 'maokilai-swal-title',
      htmlContainer: 'maokilai-swal-text',
      loader: 'maokilai-swal-loader',
    },
    didOpen: () => {
      Swal.showLoading()
    },
  })

  try {
    await forgotPasswordApi({
      email: form.email.trim(),
    })

    form.email = ''

    await Swal.fire({
      icon: 'success',
      title: '已送出申請',
      text: '如果此 Email 已註冊，我們會寄出重設密碼連結。',
      confirmButtonText: '知道了',
      confirmButtonColor: '#7ca6a0',
      background: '#fffaf2',
      customClass: {
        popup: 'maokilai-swal-popup',
        title: 'maokilai-swal-title',
        htmlContainer: 'maokilai-swal-text',
        confirmButton: 'maokilai-swal-confirm',
      },
    })
  } catch (error) {
    console.error('忘記密碼失敗', error)
    console.error('後端錯誤內容：', error.response?.data)

    await Swal.fire({
      icon: 'error',
      title: '寄送失敗',
      text: error.response?.data?.message || '寄送重設密碼信失敗，請稍後再試',
      confirmButtonText: '知道了',
      confirmButtonColor: '#c75345',
      background: '#fffaf2',
      customClass: {
        popup: 'maokilai-swal-popup',
        title: 'maokilai-swal-title',
        htmlContainer: 'maokilai-swal-text',
        confirmButton: 'maokilai-swal-confirm danger',
      },
    })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-card">
      <div class="auth-head">
        <p class="auth-kicker">Password Help</p>
        <h1>忘記密碼</h1>
        <p>輸入註冊 Email，我們會寄送重設密碼連結到你的信箱。</p>
      </div>

      <form class="auth-form" @submit.prevent="submitForgotPassword" novalidate>
        <label>
          <span>Email</span>
          <input
            v-model="form.email"
            type="email"
            placeholder="請輸入註冊 Email"
            :class="{ 'is-invalid': errors.email }"
          />
          <small v-if="errors.email" :key="shakeKey" class="field-error is-shake">
            {{ errors.email }}
          </small>
        </label>

        <p v-if="apiError" class="api-error">
          {{ apiError }}
        </p>

        <p v-if="successMessage" class="success-message">
          {{ successMessage }}
        </p>

        <button type="submit" class="auth-submit" :disabled="loading">
          {{ loading ? '寄送中...' : '寄送重設連結' }}
        </button>

        <RouterLink to="/login" class="back-link"> 回到登入 </RouterLink>
      </form>
    </section>
  </main>
</template>

<style scoped>
.auth-page {
  min-height: calc(100vh - 86px);
  display: grid;
  place-items: center;
  padding: 80px 20px;
  background:
    radial-gradient(circle at 18% 20%, rgba(124, 166, 160, 0.2), transparent 34%),
    radial-gradient(circle at 82% 18%, rgba(242, 223, 199, 0.45), transparent 32%), #f8efe4;
}

.auth-card {
  width: min(460px, 100%);
  padding: 34px;
  border-radius: 32px;
  background: rgba(255, 250, 242, 0.92);
  border: 1px solid rgba(138, 113, 89, 0.16);
  box-shadow: 0 24px 70px rgba(65, 48, 34, 0.12);
}

.auth-head {
  margin-bottom: 26px;
}

.auth-kicker {
  margin: 0 0 8px;
  color: #8a5e4c;
  font-size: 13px;
  letter-spacing: 0.08em;
}

.auth-head h1 {
  margin: 0 0 10px;
  color: #3f3024;
  font-size: 32px;
  font-weight: 900;
}

.auth-head p {
  margin: 0;
  color: #7a5f4f;
  line-height: 1.7;
}

.auth-form {
  display: grid;
  gap: 18px;
}

.auth-form label span {
  display: block;
  margin-bottom: 8px;
  color: #5f4c3d;
  font-size: 14px;
  font-weight: 800;
}

.auth-form input {
  width: 100%;
  min-height: 48px;
  padding: 0 15px;
  border: 1px solid rgba(138, 113, 89, 0.22);
  border-radius: 17px;
  background: rgba(255, 255, 255, 0.9);
  color: #3f3024;
  font-size: 15px;
  outline: none;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease;
}

.auth-form input:focus {
  border-color: rgba(124, 166, 160, 0.82);
  box-shadow: 0 0 0 4px rgba(124, 166, 160, 0.16);
  background: #fff;
}

.auth-form input.is-invalid {
  border-color: rgba(199, 83, 69, 0.7);
}

.field-error {
  display: block;
  margin-top: 7px;
  color: #c75345;
  font-size: 13px;
  font-weight: 700;
}

.is-shake {
  animation: shake 0.28s ease;
}

.api-error,
.success-message {
  margin: 0;
  padding: 12px 14px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 800;
}

.api-error {
  background: rgba(199, 83, 69, 0.08);
  color: #b94d42;
}

.success-message {
  background: rgba(124, 166, 160, 0.12);
  color: #547b75;
}

.auth-submit {
  border: 0;
  border-radius: 999px;
  padding: 14px 20px;
  background: #7ca6a0;
  color: #fff;
  font-size: 16px;
  font-weight: 900;
  cursor: pointer;
  box-shadow: 0 12px 26px rgba(124, 166, 160, 0.24);
}

.auth-submit:disabled {
  opacity: 0.62;
  cursor: not-allowed;
  box-shadow: none;
}

.back-link {
  justify-self: center;
  color: #7a5f4f;
  font-weight: 800;
  text-decoration: none;
}

.back-link:hover {
  color: #547b75;
}

@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }

  25% {
    transform: translateX(-4px);
  }

  50% {
    transform: translateX(4px);
  }

  75% {
    transform: translateX(-3px);
  }
}
</style>
