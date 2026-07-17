<script setup>
import { computed, ref } from 'vue'
import { LockKeyhole, MailCheck, ShieldCheck } from 'lucide-vue-next'
import { userAuthStore } from '@/stores/auth'
import Swal from 'sweetalert2'
import TotpSetupModal from '@/components/member/TotpSetupModal.vue'
import PasswordChangeModal from '@/components/member/PasswordChangeModal.vue'
import EmailChangeModal from '@/components/member/EmailChangeModal.vue'
import TotpDisableModal from '@/components/member/TotpDisableModal.vue'

const authStore = userAuthStore()

const sendingEmail = ref(false)
const apiError = ref('')
const successMessage = ref('')

const showPasswordModal = ref(false)

const openPasswordModal = () => {
  showPasswordModal.value = true
}

const closePasswordModal = () => {
  showPasswordModal.value = false
}

const handlePasswordChanged = () => {
  showPasswordModal.value = false
}

const isEmailVerified = computed(() => {
  return authStore.emailVerified === true
})

async function resendEmailVerification() {
  apiError.value = ''
  successMessage.value = ''

  if (isEmailVerified.value) {
    return
  }

  sendingEmail.value = true

  try {
    console.log('重新寄送 Email 驗證信')

    await Swal.fire({
      icon: 'success',
      title: '驗證信已重新寄出',
      text: '請到你的信箱查看驗證信，並完成 Email 驗證。',
      confirmButtonText: '知道了',
      customClass: {
        popup: 'maokilai-swal-popup',
        title: 'maokilai-swal-title',
        htmlContainer: 'maokilai-swal-text',
        actions: 'maokilai-swal-actions',
        confirmButton: 'maokilai-swal-confirm',
        loader: 'maokilai-swal-loader',
      },
      buttonsStyling: false,
    })
  } catch (error) {
    console.error('重新寄送驗證信失敗', error)
    console.error('後端錯誤內容：', error.response?.data)

    await Swal.fire({
      icon: 'error',
      title: '寄送驗證信失敗',
      text: error.response?.data?.message || '重新寄送驗證信失敗，請稍後再試。',
      confirmButtonText: '知道了',
      customClass: {
        popup: 'maokilai-swal-popup',
        title: 'maokilai-swal-title',
        htmlContainer: 'maokilai-swal-text',
        actions: 'maokilai-swal-actions',
        confirmButton: 'maokilai-swal-confirm',
        loader: 'maokilai-swal-loader',
      },
      buttonsStyling: false,
    })
  } finally {
    sendingEmail.value = false
  }
}

const showTotpModal = ref(false)

const openTotpModal = () => {
  showTotpModal.value = true
}

const closeTotpModal = () => {
  showTotpModal.value = false
}

const handleTotpEnabled = async () => {
  authStore.twoFactorEnabled = true
  showTotpModal.value = false

  await Swal.fire({
    icon: 'success',
    title: '兩步驟驗證已開啟',
    text: '下次登入時，系統會要求輸入驗證器 App 的 6 位數驗證碼。',
    confirmButtonText: '知道了',
    customClass: {
      popup: 'maokilai-swal-popup',
      title: 'maokilai-swal-title',
      htmlContainer: 'maokilai-swal-text',
      actions: 'maokilai-swal-actions',
      confirmButton: 'maokilai-swal-confirm',
      loader: 'maokilai-swal-loader',
    },
    buttonsStyling: false,
  })
}
const showEmailChangeModal = ref(false)

const openEmailChangeModal = () => {
  showEmailChangeModal.value = true
}

const closeEmailChangeModal = () => {
  showEmailChangeModal.value = false
}

const handleEmailChangeRequested = () => {
  showEmailChangeModal.value = false
}

const showTotpDisableModal = ref(false)

const openTotpDisableModal = () => {
  showTotpDisableModal.value = true
}

const closeTotpDisableModal = () => {
  showTotpDisableModal.value = false
}

const handleTotpDisabled = async () => {
  authStore.twoFactorEnabled = false
  showTotpDisableModal.value = false

  await Swal.fire({
    icon: 'success',
    title: '兩步驟驗證已關閉',
    text: '下次登入時，系統將不再要求輸入驗證器 App 的驗證碼。',
    confirmButtonText: '知道了',
    customClass: {
      popup: 'maokilai-swal-popup',
      title: 'maokilai-swal-title',
      htmlContainer: 'maokilai-swal-text',
      actions: 'maokilai-swal-actions',
      confirmButton: 'maokilai-swal-confirm',
    },
    buttonsStyling: false,
  })
}

const handleTotpSwitchClick = () => {
  if (authStore.twoFactorEnabled) {
    openTotpDisableModal()
    return
  }

  openTotpModal()
}
</script>

<template>
  <section class="account-security">
    <div class="section-head">
      <div>
        <p class="section-kicker">Account Security</p>
        <h1>帳號與安全</h1>
        <p class="description">管理登入密碼、Email 驗證與帳號保護設定。</p>
      </div>
    </div>

    <div class="security-list">
      <article class="security-card">
        <div class="security-main">
          <div class="security-icon">
            <LockKeyhole :size="24" :stroke-width="2.2" />
          </div>

          <div>
            <h3>登入密碼</h3>
            <p>定期更新密碼，可以降低帳號被盜用的風險。</p>
          </div>
        </div>

        <button type="button" class="security-action" @click="openPasswordModal">修改密碼</button>
      </article>

      <article class="security-card">
        <div class="security-main">
          <div class="security-icon">
            <MailCheck :size="24" :stroke-width="2.2" />
          </div>

          <div>
            <h3>Email 驗證</h3>
            <p v-if="isEmailVerified">你的 Email 已完成驗證，可用於找回密碼與接收重要帳號通知。</p>
            <p v-else>尚未完成驗證時，可重新寄送驗證信。</p>
          </div>
        </div>

        <span v-if="isEmailVerified" class="status-badge verified"> 已完成 </span>

        <button
          v-else
          type="button"
          class="security-action warning-action"
          :disabled="sendingEmail"
          @click="resendEmailVerification"
        >
          {{ sendingEmail ? '寄送中...' : '重新寄送' }}
        </button>

        <button
          v-if="isEmailVerified"
          type="button"
          class="security-action"
          @click="openEmailChangeModal"
        >
          更改帳號
        </button>
      </article>

      <article class="security-card">
        <div class="security-main">
          <div class="security-icon">
            <ShieldCheck :size="24" :stroke-width="2.2" />
          </div>

          <div>
            <h3>兩步驟驗證</h3>
            <p>登入時增加第二層驗證，讓帳號保護更完整。</p>
          </div>
        </div>

        <div class="totp-switch-wrap">
          <span class="switch-label">
            {{ authStore.twoFactorEnabled ? '已開啟' : '未開啟' }}
          </span>

          <button
            type="button"
            class="totp-switch"
            :class="{ active: authStore.twoFactorEnabled }"
            :aria-pressed="authStore.twoFactorEnabled"
            @click="handleTotpSwitchClick"
          >
            <span class="switch-thumb"></span>
          </button>
        </div>
      </article>
    </div>
  </section>
  <PasswordChangeModal
    v-if="showPasswordModal"
    @close="closePasswordModal"
    @changed="handlePasswordChanged"
  />

  <EmailChangeModal
    v-if="showEmailChangeModal"
    @close="closeEmailChangeModal"
    @requested="handleEmailChangeRequested"
  />
  <TotpSetupModal v-if="showTotpModal" @close="closeTotpModal" @enabled="handleTotpEnabled" />
  <TotpDisableModal
    v-if="showTotpDisableModal"
    @close="closeTotpDisableModal"
    @disabled="handleTotpDisabled"
  />
</template>

<style scoped>
.account-security {
  color: #4f3928;
  padding: 0;
}

.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 28px;
  margin-bottom: 28px;
}

.section-kicker {
  margin: 0 0 8px;
  color: #b9854f;
  font-size: 13px;
  font-weight: 900;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

h1 {
  margin: 0;
  font-size: 32px;
  font-weight: 950;
  letter-spacing: 0.04em;
}

.description {
  margin: 10px 0 0;
  color: #8b6d50;
  font-size: 15px;
  line-height: 1.7;
}

.security-list {
  display: grid;
  gap: 16px;
}

.security-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 22px 20px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(138, 113, 89, 0.16);
  box-shadow: 0 14px 34px rgba(65, 48, 34, 0.04);
}

.security-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 0 0 230px;
}

.danger-action {
  background: rgba(199, 83, 69, 0.1);
  color: #b94a3e;

  &:hover {
    background: rgba(199, 83, 69, 0.16);
  }
}

.security-main {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.security-icon {
  width: 56px;
  height: 56px;
  flex: 0 0 56px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  color: #7ca6a0;
}

.security-main h3 {
  margin: 0 0 6px;
  color: #3f3024;
  font-size: 18px;
  font-weight: 900;
}

.security-main p {
  margin: 0;
  color: #6f5d4d;
  font-size: 14px;
  line-height: 1.6;
}

.status-badge,
.security-action {
  flex: 0 0 auto;
  border: 0;
  border-radius: 999px;
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 900;
  white-space: nowrap;
}

.security-action {
  background: rgba(124, 166, 160, 0.16);
  color: #547b75;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    background 0.2s ease,
    opacity 0.2s ease;
}

.security-action:hover {
  transform: translateY(-1px);
  background: rgba(124, 166, 160, 0.26);
}

.security-action:disabled {
  opacity: 0.62;
  cursor: not-allowed;
  transform: none;
}

.warning-action {
  background: rgba(199, 143, 67, 0.14);
  color: #a86b2e;
}

.warning-action:hover {
  background: rgba(199, 143, 67, 0.22);
}

.status-badge.verified {
  background: rgba(185, 133, 79, 0.16);
  color: #9a6b45;
  margin-left: 140px;
}

.status-badge.protected {
  background: rgba(124, 166, 160, 0.16);
  color: #547b75;
}

.error-text,
.success-text {
  margin: 0 0 16px;
  padding: 12px 14px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 800;
}

.error-text {
  background: rgba(199, 83, 69, 0.08);
  color: #b94d42;
}

.success-text {
  background: rgba(124, 166, 160, 0.12);
  color: #547b75;
}

@media (max-width: 720px) {
  .security-card {
    align-items: flex-start;
    flex-direction: column;
  }

  .security-action,
  .status-badge {
    align-self: flex-start;
    flex-direction: column;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .form-actions button {
    width: 100%;
  }
}
.totp-switch-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}

.switch-label {
  min-width: 52px;
  color: #5f766f;
  font-size: 14px;
  font-weight: 800;
}

.totp-switch {
  position: relative;
  width: 58px;
  height: 32px;
  border: 0;
  border-radius: 999px;
  background: rgba(138, 113, 89, 0.16);
  cursor: pointer;
  transition:
    background 0.2s ease,
    box-shadow 0.2s ease;

  .switch-thumb {
    position: absolute;
    top: 4px;
    left: 4px;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background: #fff;
    box-shadow: 0 4px 12px rgba(54, 42, 32, 0.18);
    transition:
      transform 0.2s ease,
      box-shadow 0.2s ease;
  }

  &.active {
    background: #8db8b2;
    box-shadow: 0 10px 22px rgba(141, 184, 178, 0.22);

    .switch-thumb {
      transform: translateX(26px);
    }
  }

  &:hover {
    box-shadow: 0 10px 20px rgba(54, 42, 32, 0.12);
  }
}
</style>
