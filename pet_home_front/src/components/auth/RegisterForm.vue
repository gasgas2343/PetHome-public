<script setup>
import { reactive, ref } from 'vue'
import { Eye, EyeOff } from 'lucide-vue-next'
import { registerApi } from '@/api/auth'
import router from '@/router'
import Swal from 'sweetalert2'

const form = reactive({
  email: '',
  nickname: '',
  password: '',
  passwordConfirm: '',
})

const errors = reactive({
  email: '',
  nickname: '',
  password: '',
  passwordConfirm: '',
})

const apiError = ref('')
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const pwdRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/
const shakeKey = ref(0)
const loading = ref('')
const showPassword = ref(false)
const showPasswordConfirm = ref(false)

function togglePassword() {
  showPassword.value = !showPassword.value
}

function togglePasswordConfirm() {
  showPasswordConfirm.value = !showPasswordConfirm.value
}

function validForm() {
  errors.email = ''
  errors.nickname = ''
  errors.password = ''
  errors.passwordConfirm = ''

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
  } else if (!pwdRegex.test(form.password)) {
    errors.password = '密碼需為 6 到 50 碼，且包含大小寫英文與數字'
    result = false
  }

  if (!form.passwordConfirm) {
    errors.passwordConfirm = '請再次輸入密碼'
    result = false
  } else if (form.password !== form.passwordConfirm) {
    errors.passwordConfirm = '兩次輸入的密碼不一致'
    result = false
  }

  if (!form.nickname) {
    errors.nickname = '暱稱不可為空'
    result = false
  } else if (form.nickname.length < 2 || form.nickname.length > 50) {
    errors.nickname = '暱稱不可少於 2 個字和超過 50 個字'
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
  let errorMessage = ''

  Swal.fire({
    title: '正在建立帳號',
    text: '正在寄送驗證信，請稍候...',
    allowOutsideClick: false,
    allowEscapeKey: false,
    showConfirmButton: false,
    customClass: {
      popup: 'maokilai-swal-popup',
      title: 'maokilai-swal-title',
      htmlContainer: 'maokilai-swal-text',
      actions: 'maokilai-swal-actions',
      confirmButton: 'maokilai-swal-confirm',
      cancelButton: 'maokilai-swal-cancel',
      loader: 'maokilai-swal-loader',
    },
    didOpen: () => {
      Swal.showLoading()
    },
  })

  try {
    const requestBody = {
      email: form.email,
      nickname: form.nickname,
      password: form.password,
      passwordConfirm: form.passwordConfirm,
    }

    const response = await registerApi(requestBody)
    console.log('註冊成功 response:', response.data)

    Swal.close()
    loading.value = false

    await router.replace({
      name: 'RegisterEmailSent',
      query: {
        email: form.email,
      },
    })
  } catch (error) {
    console.log('註冊失敗 error:', error)

    const response = error.response?.data
    errorMessage = response?.message || '註冊失敗，請稍後再試'
    apiError.value = errorMessage

    Swal.close()
    loading.value = false
  }

  if (errorMessage) {
    Swal.fire({
      icon: 'error',
      title: '註冊失敗',
      text: errorMessage,
      confirmButtonText: '我知道了',
      customClass: {
        popup: 'maokilai-swal-popup',
        title: 'maokilai-swal-title',
        htmlContainer: 'maokilai-swal-text',
        actions: 'maokilai-swal-actions',
        confirmButton: 'maokilai-swal-confirm danger',
        cancelButton: 'maokilai-swal-cancel',
        loader: 'maokilai-swal-loader',
      },
    })
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
        {{ errors.email || '' }}
      </p>
    </label>

    <label class="form-field">
      <span>暱稱</span>
      <input
        v-model="form.nickname"
        type="text"
        placeholder="想讓大家怎麼稱呼你"
        class="auth-input"
        :class="{ 'is-invalid': errors.nickname }"
      />
      <p :key="`name-${shakeKey}`" class="field-error" :class="{ 'is-shake': errors.nickname }">
        {{ errors.nickname || '' }}
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
          autocomplete="new-password"
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
        {{ errors.password || '' }}
      </p>
    </label>

    <label class="form-field">
      <span>確認密碼</span>
      <div class="password-input-wrap">
        <input
          v-model="form.passwordConfirm"
          :type="showPasswordConfirm ? 'text' : 'password'"
          placeholder="請再次輸入密碼"
          class="auth-input password-input"
          :class="{ 'is-invalid': errors.passwordConfirm }"
          autocomplete="new-password"
        />

        <button
          type="button"
          class="password-eye-btn"
          @click="togglePasswordConfirm"
          :aria-label="showPasswordConfirm ? '隱藏密碼' : '顯示密碼'"
        >
          <EyeOff v-if="showPasswordConfirm" :size="20" :stroke-width="2.2" />
          <Eye v-else :size="20" :stroke-width="2.2" />
        </button>
      </div>
      <p
        :key="`pwd-duble-${shakeKey}`"
        class="field-error"
        :class="{ 'is-shake': errors.passwordConfirm }"
      >
        {{ errors.passwordConfirm || '' }}
      </p>
    </label>

    <p :key="`api-${shakeKey}`" class="api-error" :class="{ 'is-visible': apiError }">
      {{ apiError || 'placeholder' }}
    </p>
    <button class="primary-btn" type="submit">建立帳號</button>

    <p class="switch-text">
      已經有帳號？
      <RouterLink to="/login">前往登入</RouterLink>
    </p>
  </form>
</template>

<style scoped lang="scss">
.auth-form {
  display: grid;
  gap: 5px;
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
    box-shadow: 0 0 0 3px rgba(213, 106, 91, 0.12);
  }
}

.form-field {
  display: flex;
  flex-direction: column;

  span {
    color: #5c4633;
    font-weight: 700;
    font-size: 0.92rem;
    padding-bottom: 11px;
  }

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
  }
}

.field-error {
  height: 16px;
  margin: 4px 18px 0;
  font-size: 12px;
  line-height: 16px;
  color: #c75c4a;

  &.is-shake {
    animation: error-shake 0.24s ease;
  }
}

.api-error {
  min-height: 22px;
  margin: 0;
  color: #c44736;
  font-size: 0.9rem;
  line-height: 1.5;
  visibility: hidden;

  &.is-visible {
    visibility: visible;
    animation: error-shake 0.32s ease;
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

.primary-btn {
  height: 50px;
  margin-top: 8px;
  border: none;
  border-radius: 999px;
  background: #5d919f;
  color: #fff;
  font-weight: 800;
  font-size: 1rem;
  letter-spacing: 0.06em;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 24px rgba(93, 145, 159, 0.26);
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

.form-alert {
  padding: 10px 12px;
  border-radius: 12px;
  background: #fff4ef;
  color: #b95545;
  font-size: 13px;
  line-height: 1.5;
}
</style>
