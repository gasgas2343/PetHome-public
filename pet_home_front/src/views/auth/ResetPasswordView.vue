<script setup>
import { computed, reactive, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { resetPasswordApi } from '@/api/auth'
import { Eye, EyeOff } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const showPassword = ref(false)
const showConfirmPassword = ref(false)

const resetToken = computed(() => route.query.token || '')

const form = reactive({
  password: '',
  confirmPassword: '',
})

const errors = reactive({
  password: '',
  confirmPassword: '',
})

const loading = ref(false)
const apiError = ref('')
const successMessage = ref('')
const shakeKey = ref(0)

const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d\S]{6,50}$/

function validateForm() {
  errors.password = ''
  errors.confirmPassword = ''
  apiError.value = ''
  successMessage.value = ''

  let valid = true

  if (!resetToken.value) {
    apiError.value = '重設密碼連結無效，請重新申請'
    valid = false
  }

  if (!form.password) {
    errors.password = '請輸入新密碼'
    valid = false
  } else if (!passwordRegex.test(form.password)) {
    errors.password = '密碼需為 6～50 碼，且包含英文與數字'
    valid = false
  }

  if (!form.confirmPassword) {
    errors.confirmPassword = '請再次輸入新密碼'
    valid = false
  } else if (form.password !== form.confirmPassword) {
    errors.confirmPassword = '兩次輸入的密碼不一致'
    valid = false
  }

  if (!valid) {
    shakeKey.value++
  }

  return valid
}

async function submitResetPassword() {
  if (!validateForm()) return

  loading.value = true

  try {
    await resetPasswordApi({
      resetToken: resetToken.value,
      password: form.password,
      confirmPassword: form.confirmPassword,
    })

    successMessage.value = '密碼已重新設定，請使用新密碼登入'

    setTimeout(() => {
      router.push('/login')
    }, 1200)
  } catch (error) {
    console.error('重設密碼失敗', error)
    console.error('後端錯誤內容：', error.response?.data.data)

    apiError.value = error.response?.data?.message || '重設密碼失敗，請確認連結是否過期'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-card">
      <div class="auth-head">
        <p class="auth-kicker">Reset Password</p>
        <h1>設定新密碼</h1>
        <p>請輸入新的登入密碼，完成後即可使用新密碼登入。</p>
      </div>

      <form class="auth-form" @submit.prevent="submitResetPassword" novalidate>
        <label>
          <span>新密碼</span>

          <div class="password-field">
            <input
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="請輸入新密碼"
              :class="{ 'is-invalid': errors.password }"
            />

            <button
              type="button"
              class="password-toggle"
              :aria-label="showPassword ? '隱藏密碼' : '顯示密碼'"
              @click="showPassword = !showPassword"
            >
              <EyeOff v-if="showPassword" :size="18" />
              <Eye v-else :size="18" />
            </button>
          </div>

          <small v-if="errors.password" :key="`new-${shakeKey}`" class="field-error is-shake">
            {{ errors.password }}
          </small>
        </label>

        <label>
          <span>確認新密碼</span>

          <div class="password-field">
            <input
              v-model="form.confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              placeholder="再次輸入新密碼"
              :class="{ 'is-invalid': errors.confirmPassword }"
            />

            <button
              type="button"
              class="password-toggle"
              :aria-label="showConfirmPassword ? '隱藏密碼' : '顯示密碼'"
              @click="showConfirmPassword = !showConfirmPassword"
            >
              <EyeOff v-if="showConfirmPassword" :size="18" />
              <Eye v-else :size="18" />
            </button>
          </div>

          <small
            v-if="errors.confirmPassword"
            :key="`confirm-${shakeKey}`"
            class="field-error is-shake"
          >
            {{ errors.confirmPassword }}
          </small>
        </label>

        <p v-if="apiError" class="api-error">
          {{ apiError }}
        </p>

        <p v-if="successMessage" class="success-message">
          {{ successMessage }}
        </p>

        <button type="submit" class="auth-submit" :disabled="loading">
          {{ loading ? '設定中...' : '設定新密碼' }}
        </button>

        <RouterLink to="/forgot-password" class="back-link"> 重新寄送重設連結 </RouterLink>
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

.password-field {
  position: relative;
}

.password-field input {
  padding-right: 48px;
}

.password-toggle {
  position: absolute;
  top: 50%;
  right: 14px;
  transform: translateY(-50%);

  width: 32px;
  height: 32px;
  border: 0;
  border-radius: 50%;
  background: transparent;
  color: #8a6f5b;

  display: grid;
  place-items: center;

  cursor: pointer;
  transition:
    background 0.18s ease,
    color 0.18s ease;
}

.password-toggle:hover {
  background: rgba(138, 113, 89, 0.12);
  color: #547b75;
}

.password-toggle:focus-visible {
  outline: 3px solid rgba(124, 166, 160, 0.22);
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
