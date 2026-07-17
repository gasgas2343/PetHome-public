<script setup>
import { ref, onMounted } from 'vue'
import { setupTotpApi, confirmTotpApi } from '@/api/totp'

const emit = defineEmits(['close', 'enabled'])

const qrCodeUrl = ref('')
const secret = ref('')
const code = ref('')
const errorMessage = ref('')
const loading = ref(false)
const confirmLoading = ref(false)

onMounted(() => {
  setupTotp()
})

const setupTotp = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    const response = await setupTotpApi()
    const data = response.data.data

    console.log('TOTP setup data:', data)

    secret.value = data.secret || ''

    if (!data.qrCode) {
      errorMessage.value = 'QR Code 資料不存在'
      return
    }

    // 後端如果已經回完整 data:image/png;base64,...
    if (data.qrCode.startsWith('data:image')) {
      qrCodeUrl.value = data.qrCode
      return
    }

    // 後端如果只回純 base64
    qrCodeUrl.value = `data:image/png;base64,${data.qrCode}`
  } catch (error) {
    console.error(error)
    errorMessage.value =
      error.response?.data?.message || '取得兩步驟驗證設定失敗'
  } finally {
    loading.value = false
  }
}

const confirmTotp = async () => {
  if (!code.value) {
    errorMessage.value = '請輸入 6 位數驗證碼'
    return
  }

  if (!/^\d{6}$/.test(code.value)) {
    errorMessage.value = '驗證碼格式錯誤，請輸入 6 位數字'
    return
  }

  try {
    confirmLoading.value = true
    errorMessage.value = ''

    await confirmTotpApi(code.value)

    emit('enabled')
  } catch (error) {
    console.error(error)
    errorMessage.value =
      error.response?.data?.message || '驗證碼錯誤，請重新確認'
  } finally {
    confirmLoading.value = false
  }
}

const closeModal = () => {
  if (confirmLoading.value) return
  emit('close')
}
</script>

<template>
  <div class="totp-modal-mask" @click.self="closeModal">
    <div class="totp-modal-card">
      <button class="totp-close" type="button" @click="closeModal">
        ×
      </button>

      <p class="modal-eyebrow">TWO-FACTOR AUTHENTICATION</p>
      <h2>設定兩步驟驗證</h2>

      <div v-if="loading" class="loading-box">
        <div class="loading-spinner"></div>
        <p>正在產生驗證設定...</p>
      </div>

      <template v-else>
        <p class="modal-desc">
          請使用 Google Authenticator、Microsoft Authenticator 或其他驗證器 App
          掃描 QR Code，並輸入 App 顯示的 6 位數驗證碼。
        </p>

        <div v-if="qrCodeUrl" class="qr-box">
          <img :src="qrCodeUrl" alt="TOTP QR Code" />
        </div>

        <div class="secret-box">
          <span>無法掃描？可手動輸入密鑰</span>
          <strong>{{ secret }}</strong>
        </div>

        <label class="input-label" for="totp-code">
          驗證碼
        </label>

        <input
          id="totp-code"
          v-model.trim="code"
          class="totp-input"
          maxlength="6"
          inputmode="numeric"
          autocomplete="one-time-code"
          placeholder="輸入 6 位數驗證碼"
          @keyup.enter="confirmTotp"
        />

        <p v-if="errorMessage" class="error-text">
          {{ errorMessage }}
        </p>

        <div class="modal-actions">
          <button
            class="btn-cancel"
            type="button"
            :disabled="confirmLoading"
            @click="closeModal"
          >
            取消
          </button>

          <button
            class="btn-primary"
            type="button"
            :disabled="confirmLoading"
            @click="confirmTotp"
          >
            {{ confirmLoading ? '驗證中...' : '確認開啟' }}
          </button>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped lang="scss">
.totp-modal-mask {
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

.totp-modal-card {
  width: min(620px, 100%);
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

.totp-close {
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
  margin: 0 0 18px;
  font-size: 28px;
  font-weight: 800;
  color: #3f2a1d;
}

.modal-desc {
  margin: 0 0 22px;
  color: #6f5745;
  font-size: 15px;
  line-height: 1.8;
}

.loading-box {
  min-height: 280px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 14px;
  color: #7b5f49;

  p {
    margin: 0;
    font-size: 15px;
  }
}

.loading-spinner {
  width: 38px;
  height: 38px;
  border: 4px solid rgba(141, 184, 178, 0.25);
  border-top-color: #8db8b2;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.qr-box {
  width: 224px;
  height: 224px;
  margin: 0 auto 18px;
  background: #ffffff;
  border-radius: 26px;
  border: 1px solid rgba(170, 132, 94, 0.2);
  box-shadow: 0 12px 28px rgba(85, 61, 38, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;

  img {
    width: 184px;
    height: 184px;
    object-fit: contain;
    display: block;
  }
}

.secret-box {
  background: #f8efe3;
  color: #7a5637;
  border-radius: 18px;
  padding: 14px 16px;
  font-size: 14px;
  margin-bottom: 18px;
  border: 1px solid rgba(170, 132, 94, 0.14);

  span {
    display: block;
    margin-bottom: 7px;
    font-weight: 600;
  }

  strong {
    display: block;
    color: #3f2a1d;
    letter-spacing: 1.4px;
    word-break: break-all;
    font-size: 15px;
  }
}

.input-label {
  display: block;
  margin-bottom: 8px;
  color: #4b3728;
  font-weight: 700;
  font-size: 14px;
}

.totp-input {
  width: 100%;
  height: 54px;
  border-radius: 18px;
  border: 1px solid rgba(170, 132, 94, 0.3);
  padding: 0 18px;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 6px;
  text-align: center;
  outline: none;
  color: #3f2a1d;
  background: #ffffff;
  transition: 0.2s ease;

  &::placeholder {
    color: #b8aaa0;
    font-size: 15px;
    font-weight: 500;
    letter-spacing: 0;
  }

  &:focus {
    border-color: #8db8b2;
    box-shadow: 0 0 0 4px rgba(141, 184, 178, 0.18);
  }
}

.error-text {
  margin: 10px 0 0;
  color: #c45a3c;
  font-size: 14px;
  font-weight: 600;
}

.modal-actions {
  margin-top: 26px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-cancel,
.btn-primary {
  border: none;
  border-radius: 999px;
  padding: 13px 22px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.2s ease;

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.btn-cancel {
  background: #eee6da;
  color: #6f5745;

  &:hover:not(:disabled) {
    background: #e5d8c8;
  }
}

.btn-primary {
  background: #8db8b2;
  color: #ffffff;
  box-shadow: 0 10px 22px rgba(141, 184, 178, 0.28);

  &:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 14px 26px rgba(141, 184, 178, 0.34);
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 640px) {
  .totp-modal-mask {
    padding: 16px;
  }

  .totp-modal-card {
    border-radius: 26px;
    padding: 32px 24px 28px;
  }

  .totp-close {
    top: 18px;
    right: 20px;
  }

  h2 {
    font-size: 24px;
  }

  .qr-box {
    width: 204px;
    height: 204px;

    img {
      width: 168px;
      height: 168px;
    }
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