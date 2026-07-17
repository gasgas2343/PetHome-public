<script setup>
import { reactive, ref } from 'vue'
import { Eye, EyeOff } from 'lucide-vue-next'
import { changePwdApi } from '@/api/user'
import { userAuthStore } from '@/stores/auth'
import Swal from 'sweetalert2'

const emit = defineEmits(['close', 'changed'])

const authStore = userAuthStore()

const savingPassword = ref(false)
const apiError = ref('')
const showOldPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const errors = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

function resetErrors() {
  errors.oldPassword = ''
  errors.newPassword = ''
  errors.confirmPassword = ''
  apiError.value = ''
}

function validatePasswordForm() {
  resetErrors()

  let valid = true

  if (!passwordForm.oldPassword.trim()) {
    errors.oldPassword = '請輸入目前密碼'
    valid = false
  }

  if (!passwordForm.newPassword.trim()) {
    errors.newPassword = '請輸入新密碼'
    valid = false
  } else if (passwordForm.newPassword.length < 6) {
    errors.newPassword = '新密碼至少需要 6 碼'
    valid = false
  }

  if (!passwordForm.confirmPassword.trim()) {
    errors.confirmPassword = '請再次輸入新密碼'
    valid = false
  } else if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    errors.confirmPassword = '兩次輸入的新密碼不一致'
    valid = false
  }

  return valid
}

function closeModal() {
  if (savingPassword.value) return
  emit('close')
}

async function submitPasswordChange() {
  if (!validatePasswordForm()) {
    return
  }

  savingPassword.value = true

  Swal.fire({
    title: '更新中',
    text: '正在更新你的登入密碼，請稍候...',
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
    const requestBody = {
      oldPwd: passwordForm.oldPassword,
      newPwd: passwordForm.newPassword,
      confirmPwd: passwordForm.confirmPassword,
      refreshToken: localStorage.getItem('refreshToken'),
    }

    const response = await changePwdApi(requestBody)
    const tokenData = response.data.data

    authStore.setToken(tokenData)

    Swal.close()

    emit('changed')

    await Swal.fire({
      icon: 'success',
      title: '密碼已更新',
      text: '下次登入時請使用新的密碼。',
      confirmButtonText: '知道了',
      background: '#fffaf2',
      customClass: {
        popup: 'maokilai-swal-popup',
        title: 'maokilai-swal-title',
        htmlContainer: 'maokilai-swal-text',
        confirmButton: 'maokilai-swal-confirm',
      },
    })
  } catch (error) {
    console.error('修改密碼失敗', error)
    console.error('後端錯誤內容：', error.response?.data)

    Swal.close()

    apiError.value = error.response?.data?.message || '修改密碼失敗，請確認目前密碼是否正確。'
  } finally {
    savingPassword.value = false
  }
}
</script>

<template>
  <div class="password-modal-mask" @click.self="closeModal">
    <div class="password-modal-card">
      <button class="password-close" type="button" @click="closeModal">×</button>

      <p class="modal-eyebrow">PASSWORD SECURITY</p>
      <h2>修改登入密碼</h2>

      <p class="modal-desc">請輸入目前密碼與新的登入密碼。密碼更新後，下次登入請使用新密碼。</p>

      <form class="password-form" @submit.prevent="submitPasswordChange" novalidate>
        <label>
          <span>目前密碼</span>
          <div class="password-input-wrap">
            <input
              v-model="passwordForm.oldPassword"
              :type="showOldPassword ? 'text' : 'password'"
              placeholder="請輸入目前密碼"
              autocomplete="current-password"
            />

            <button
              type="button"
              class="password-eye-btn"
              @click="showOldPassword = !showOldPassword"
              :aria-label="showOldPassword ? '隱藏目前密碼' : '顯示目前密碼'"
            >
              <EyeOff v-if="showOldPassword" :size="20" :stroke-width="2.2" />
              <Eye v-else :size="20" :stroke-width="2.2" />
            </button>
          </div>
          <small v-if="errors.oldPassword">{{ errors.oldPassword }}</small>
        </label>

        <label>
          <span>新密碼</span>
          <div class="password-input-wrap">
            <input
              v-model="passwordForm.newPassword"
              :type="showNewPassword ? 'text' : 'password'"
              placeholder="請輸入新密碼"
              autocomplete="new-password"
            />

            <button
              type="button"
              class="password-eye-btn"
              @click="showNewPassword = !showNewPassword"
              :aria-label="showNewPassword ? '隱藏新密碼' : '顯示新密碼'"
            >
              <EyeOff v-if="showNewPassword" :size="20" :stroke-width="2.2" />
              <Eye v-else :size="20" :stroke-width="2.2" />
            </button>
          </div>
          <small v-if="errors.newPassword">{{ errors.newPassword }}</small>
        </label>

        <label>
          <span>確認新密碼</span>
          <div class="password-input-wrap">
            <input
              v-model="passwordForm.confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              placeholder="再次輸入新密碼"
              autocomplete="new-password"
            />

            <button
              type="button"
              class="password-eye-btn"
              @click="showConfirmPassword = !showConfirmPassword"
              :aria-label="showConfirmPassword ? '隱藏確認新密碼' : '顯示確認新密碼'"
            >
              <EyeOff v-if="showConfirmPassword" :size="20" :stroke-width="2.2" />
              <Eye v-else :size="20" :stroke-width="2.2" />
            </button>
          </div>
          <small v-if="errors.confirmPassword">{{ errors.confirmPassword }}</small>
        </label>

        <p v-if="apiError" class="error-text">
          {{ apiError }}
        </p>

        <div class="modal-actions">
          <button type="button" class="btn-cancel" :disabled="savingPassword" @click="closeModal">
            取消
          </button>

          <button type="submit" class="btn-primary" :disabled="savingPassword">
            {{ savingPassword ? '儲存中...' : '儲存變更' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped lang="scss">
.password-modal-mask {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: rgba(46, 38, 30, 0.42);
  backdrop-filter: blur(3px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.password-modal-card {
  width: min(560px, 100%);
  max-height: calc(100vh - 48px);
  overflow-y: auto;
  position: relative;
  background: #fffaf2;
  border-radius: 32px;
  padding: 38px 42px 34px;
  color: #3f2a1d;
  border: 1px solid rgba(170, 132, 94, 0.18);
  box-shadow: 0 24px 80px rgba(52, 38, 24, 0.28);
}

.password-close {
  position: absolute;
  top: 22px;
  right: 26px;
  width: 42px;
  height: 42px;
  border: none;
  border-radius: 50%;
  background: #eee6da;
  color: #7a6758;
  font-size: 30px;
  line-height: 1;
  cursor: pointer;
  transition: 0.2s ease;

  &:hover {
    background: #e5d8c8;
    color: #4c3828;
  }
}

.modal-eyebrow {
  margin: 0 0 12px;
  color: #9a6b45;
  font-size: 14px;
  letter-spacing: 0.08em;
  font-weight: 700;
}

h2 {
  margin: 0 0 16px;
  color: #3f2a1d;
  font-size: 28px;
  font-weight: 900;
}

.modal-desc {
  margin: 0 0 24px;
  color: #6f5745;
  font-size: 15px;
  line-height: 1.8;
}

.password-form {
  display: grid;
  gap: 16px;
}

.password-form label span {
  display: block;
  margin-bottom: 7px;
  color: #5f4c3d;
  font-size: 14px;
  font-weight: 800;
}

.password-form input {
  width: 100%;
  min-height: 50px;
  padding: 0 15px;
  border: 1px solid rgba(138, 113, 89, 0.22);
  border-radius: 17px;
  background: rgba(255, 255, 255, 0.92);
  color: #3f3024;
  font-size: 15px;
  outline: none;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease;

  &::placeholder {
    color: rgba(111, 93, 77, 0.52);
  }

  &:focus {
    border-color: rgba(124, 166, 160, 0.82);
    box-shadow: 0 0 0 4px rgba(124, 166, 160, 0.16);
    background: #fff;
  }
}

.password-form small {
  display: block;
  margin-top: 6px;
  color: #c75345;
  font-size: 12px;
  font-weight: 700;
}

.error-text {
  margin: 0;
  padding: 12px 14px;
  border-radius: 16px;
  background: rgba(199, 83, 69, 0.08);
  color: #b94d42;
  font-size: 14px;
  font-weight: 800;
}

.modal-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-cancel,
.btn-primary {
  border: none;
  border-radius: 999px;
  padding: 13px 22px;
  font-weight: 900;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease,
    opacity 0.2s ease;

  &:disabled {
    opacity: 0.62;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
}

.btn-cancel {
  background: #eee6da;
  color: #6f5745;

  &:hover:not(:disabled) {
    background: #e5d8c8;
    transform: translateY(-1px);
  }
}

.btn-primary {
  background: #7ca6a0;
  color: #fff;
  box-shadow: 0 12px 24px rgba(124, 166, 160, 0.22);

  &:hover:not(:disabled) {
    background: #719c96;
    transform: translateY(-1px);
    box-shadow: 0 15px 28px rgba(124, 166, 160, 0.28);
  }
}

.password-input-wrap {
  position: relative;
  width: 100%;

  input {
    width: 100%;
    padding-right: 52px;
  }
}

.password-eye-btn {
  position: absolute;
  top: 50%;
  right: 14px;
  transform: translateY(-50%);

  width: 34px;
  height: 34px;
  padding: 0;
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

  &:hover {
    background: rgba(138, 113, 89, 0.12);
    color: #5f9baa;
  }

  &:active {
    transform: translateY(-50%) scale(0.94);
  }
}

@media (max-width: 640px) {
  .password-modal-mask {
    padding: 16px;
  }

  .password-modal-card {
    border-radius: 26px;
    padding: 32px 24px 28px;
  }

  .password-close {
    top: 18px;
    right: 20px;
  }

  h2 {
    font-size: 24px;
  }

  .modal-actions {
    flex-direction: column-reverse;
  }

  .btn-cancel,
  .btn-primary {
    width: 100%;
  }
}
</style>
