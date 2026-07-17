<script setup>
import { reactive, ref } from 'vue'
import Swal from 'sweetalert2'
import { disableTotpApi } from '@/api/totp'

const emit = defineEmits(['close', 'disabled'])

const form = reactive({
  code: '',
})

const loading = ref(false)
const errorMessage = ref('')

const closeModal = () => {
  if (loading.value) return
  emit('close')
}

async function submitDisable() {
  errorMessage.value = ''

  if (!form.code) {
    errorMessage.value = '請輸入驗證器 App 的 6 位數驗證碼'
    return
  }

  if (!/^\d{6}$/.test(form.code)) {
    errorMessage.value = '驗證碼格式錯誤，請輸入 6 位數字'
    return
  }

  const result = await Swal.fire({
    icon: 'warning',
    title: '確定要關閉兩步驟驗證嗎？',
    text: '關閉後，登入時將不再要求驗證器 App 的驗證碼。',
    showCancelButton: true,
    confirmButtonText: '確認關閉',
    cancelButtonText: '取消',
    customClass: {
      popup: 'maokilai-swal-popup',
      title: 'maokilai-swal-title',
      htmlContainer: 'maokilai-swal-text',
      actions: 'maokilai-swal-actions',
      confirmButton: 'maokilai-swal-confirm danger',
      cancelButton: 'maokilai-swal-cancel',
      loader: 'maokilai-swal-loader',
    },
    buttonsStyling: false,
  })

  if (!result.isConfirmed) return

  loading.value = true

  try {
    await disableTotpApi({
      code: form.code,
    })

    emit('disabled')
  } catch (error) {
    console.error('關閉 TOTP 失敗', error)
    console.error('後端錯誤內容：', error.response?.data)

    errorMessage.value = error.response?.data?.message || '關閉兩步驟驗證失敗'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="modal-mask" @click.self="closeModal">
    <div class="modal-card">
      <button type="button" class="modal-close" @click="closeModal">×</button>

      <div class="modal-header">
        <p class="modal-kicker">Two-factor authentication</p>
        <h2>關閉兩步驟驗證</h2>
        <p>請輸入驗證器 App 目前顯示的 6 位數驗證碼，確認是本人操作。</p>
      </div>

      <form class="modal-form" @submit.prevent="submitDisable">
        <label class="form-field">
          <span>驗證碼</span>
          <input
            v-model.trim="form.code"
            type="text"
            inputmode="numeric"
            maxlength="6"
            placeholder="請輸入 6 位數驗證碼"
            autocomplete="one-time-code"
          />
        </label>

        <p v-if="errorMessage" class="error-text">
          {{ errorMessage }}
        </p>

        <div class="modal-actions">
          <button type="button" class="cancel-btn" :disabled="loading" @click="closeModal">
            取消
          </button>

          <button type="submit" class="danger-btn" :disabled="loading">
            {{ loading ? '關閉中...' : '確認關閉' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped lang="scss">
.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: grid;
  place-items: center;
  padding: 24px;
  background: rgba(49, 38, 29, 0.38);
}

.modal-card {
  position: relative;
  width: min(460px, 100%);
  padding: 32px;
  border-radius: 28px;
  background: #fffaf2;
  border: 1px solid rgba(138, 113, 89, 0.16);
  box-shadow: 0 24px 70px rgba(54, 42, 32, 0.22);
}

.modal-close {
  position: absolute;
  top: 18px;
  right: 20px;
  width: 34px;
  height: 34px;
  border: 0;
  border-radius: 999px;
  background: rgba(138, 113, 89, 0.12);
  color: #6d5948;
  font-size: 22px;
  cursor: pointer;
}

.modal-header {
  display: block;
  margin-bottom: 24px;
  padding-right: 42px;

  h2 {
    margin: 6px 0 10px;
    color: #3f3024;
    font-size: 24px;
    font-weight: 900;
    line-height: 1.35;
    letter-spacing: 0.02em;
    white-space: normal;
  }

  p {
    margin: 0;
    color: #7a6655;
    line-height: 1.7;
    font-size: 15px;
  }
}

.modal-kicker {
  margin: 0 0 6px;
  color: #9a7a54 !important;
  font-size: 12px;
  font-weight: 900;
  line-height: 1.4;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.modal-form {
  display: grid;
  gap: 18px;
}

.form-field {
  display: grid;
  gap: 8px;

  span {
    color: #4f3d2f;
    font-size: 14px;
    font-weight: 800;
  }

  input {
    width: 100%;
    border: 1px solid rgba(138, 113, 89, 0.22);
    border-radius: 16px;
    padding: 13px 15px;
    background: #fff;
    color: #3f3024;
    outline: none;

    &:focus {
      border-color: #c6a16f;
      box-shadow: 0 0 0 4px rgba(198, 161, 111, 0.16);
    }
  }
}

.error-text {
  margin: 0;
  color: #b94a3e;
  font-size: 14px;
  font-weight: 700;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 6px;
}

.cancel-btn,
.danger-btn {
  border: 0;
  border-radius: 999px;
  padding: 12px 22px;
  font-size: 14px;
  font-weight: 900;
  cursor: pointer;

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.cancel-btn {
  background: rgba(138, 113, 89, 0.12);
  color: #6d5948;
}

.danger-btn {
  background: #c75345;
  color: #fff;
  box-shadow: 0 12px 24px rgba(199, 83, 69, 0.2);
}
</style>
