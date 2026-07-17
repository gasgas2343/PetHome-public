<script setup>
import { ref } from 'vue'
import { Mail, X } from 'lucide-vue-next'
import Swal from 'sweetalert2'
import { requestChangeEmailApi } from '@/api/user'

const emit = defineEmits(['close', 'requested'])

const newEmail = ref('')
const loading = ref(false)
const apiError = ref('')

const close = () => {
  if (loading.value) return
  emit('close')
}

const submitEmailChange = async () => {
  apiError.value = ''

  if (!newEmail.value.trim()) {
    apiError.value = '請輸入新的 Email'
    return
  }

  loading.value = true

  Swal.fire({
    title: '驗證信寄送中',
    text: '正在寄送更改帳號驗證信，請稍候...',
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
    await requestChangeEmailApi({
      newEmail: newEmail.value.trim(),
    })

    Swal.close()

    await Swal.fire({
      icon: 'success',
      title: '驗證信已寄出',
      text: '請到新的信箱收信，點擊驗證連結後才會完成帳號變更。',
      confirmButtonText: '知道了',
      background: '#fffaf2',
      customClass: {
        popup: 'maokilai-swal-popup',
        title: 'maokilai-swal-title',
        htmlContainer: 'maokilai-swal-text',
        confirmButton: 'maokilai-swal-confirm',
      },
    })

    // 成功後看你要不要清空或關閉 Modal
    newEmail.value = ''
    // emit('close')
  } catch (error) {
    Swal.close()

    console.error('申請更改 Email 失敗', error)
    console.error('後端錯誤內容：', error.response?.data)

    const message = error.response?.data?.message || '申請更改 Email 失敗'
    apiError.value = message

    await Swal.fire({
      icon: 'error',
      title: '寄送失敗',
      text: message,
      confirmButtonText: '確認',
      background: '#fffaf2',
      customClass: {
        popup: 'maokilai-swal-popup',
        title: 'maokilai-swal-title',
        htmlContainer: 'maokilai-swal-text',
        confirmButton: 'maokilai-swal-confirm',
      },
    })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="modal-backdrop" @click.self="close">
    <section class="email-modal">
      <button type="button" class="close-button" @click="close">
        <X :size="20" />
      </button>

      <div class="modal-icon">
        <Mail :size="30" :stroke-width="2.2" />
      </div>

      <p class="modal-kicker">Change Email</p>
      <h2>更改帳號 Email</h2>
      <p class="modal-desc">
        請輸入新的 Email。系統會寄送驗證信到新信箱，完成驗證後才會正式更新帳號。
      </p>

      <form class="email-form" @submit.prevent="submitEmailChange">
        <label>
          <span>新的 Email</span>
          <input
            v-model="newEmail"
            type="email"
            autocomplete="email"
            placeholder="請輸入新的 Email"
            :disabled="loading"
          />
        </label>

        <p v-if="apiError" class="error-text">
          {{ apiError }}
        </p>

        <div class="form-actions">
          <button type="button" class="cancel-button" :disabled="loading" @click="close">
            取消
          </button>

          <button type="submit" class="submit-button" :disabled="loading">
            {{ loading ? '寄送中...' : '寄送驗證信' }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: grid;
  place-items: center;
  padding: 20px;
  background: rgba(63, 48, 36, 0.36);
  backdrop-filter: blur(6px);
}

.email-modal {
  position: relative;
  width: min(460px, 100%);
  padding: 34px 32px 30px;
  border-radius: 30px;
  background: #fffaf2;
  border: 1px solid rgba(138, 113, 89, 0.18);
  box-shadow: 0 28px 70px rgba(65, 48, 34, 0.22);
  color: #4f3928;
}

.close-button {
  position: absolute;
  top: 18px;
  right: 18px;
  width: 38px;
  height: 38px;
  border: 0;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: rgba(124, 166, 160, 0.12);
  color: #547b75;
  cursor: pointer;
}

.modal-icon {
  width: 64px;
  height: 64px;
  border-radius: 22px;
  display: grid;
  place-items: center;
  margin-bottom: 16px;
  background: rgba(124, 166, 160, 0.14);
  color: #6f9b94;
}

.modal-kicker {
  margin: 0 0 8px;
  color: #b9854f;
  font-size: 12px;
  font-weight: 900;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

h2 {
  margin: 0;
  font-size: 26px;
  font-weight: 950;
  letter-spacing: 0.03em;
  color: #3f3024;
}

.modal-desc {
  margin: 12px 0 24px;
  color: #7b644f;
  font-size: 14px;
  line-height: 1.7;
}

.email-form {
  display: grid;
  gap: 16px;
}

label {
  display: grid;
  gap: 8px;
}

label span {
  color: #5d4533;
  font-size: 14px;
  font-weight: 900;
}

input {
  width: 100%;
  height: 46px;
  padding: 0 14px;
  border-radius: 16px;
  border: 1px solid rgba(138, 113, 89, 0.22);
  background: rgba(255, 255, 255, 0.82);
  color: #3f3024;
  font-size: 15px;
  outline: none;
}

input:focus {
  border-color: rgba(124, 166, 160, 0.75);
  box-shadow: 0 0 0 4px rgba(124, 166, 160, 0.13);
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

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 4px;
}

.cancel-button,
.submit-button {
  border: 0;
  border-radius: 999px;
  padding: 11px 18px;
  font-size: 14px;
  font-weight: 900;
  cursor: pointer;
}

.cancel-button {
  background: rgba(138, 113, 89, 0.12);
  color: #7b644f;
}

.submit-button {
  background: #8db8b2;
  color: #fff;
}

.cancel-button:disabled,
.submit-button:disabled {
  opacity: 0.62;
  cursor: not-allowed;
}

@media (max-width: 520px) {
  .email-modal {
    padding: 30px 22px 24px;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .cancel-button,
  .submit-button {
    width: 100%;
  }
}
</style>
