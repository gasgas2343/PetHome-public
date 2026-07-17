<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { sendVerificationEmailApi } from '@/api/auth'
import { MailCheck } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()

const email = computed(() => route.query.email || '')

const loading = ref(false)
const message = ref('')
const errorMessage = ref('')
const countdown = ref(0)

function goLogin() {
  router.push('/login')
}

async function resendEmail() {
  if (!email.value || countdown.value > 0) return

  loading.value = true
  message.value = ''
  errorMessage.value = ''

  try {
    await sendVerificationEmailApi({
      email: email.value,
    })

    message.value = '驗證信已重新寄出，請查看你的信箱'
    startCountdown()
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '重寄驗證信失敗，請稍後再試'
  } finally {
    loading.value = false
  }
}

function startCountdown() {
  countdown.value = 60

  const timer = setInterval(() => {
    countdown.value--

    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}
</script>

<template>
  <main class="verify-page">
    <section class="verify-card">
      <div class="mail-icon">
        <MailCheck :size="42" :stroke-width="1.8" />
      </div>

      <p class="eyebrow">註冊成功</p>

      <h1>請前往信箱完成驗證</h1>

      <p class="desc">
        我們已經寄出一封驗證信到
        <strong>{{ email }}</strong>
        ，請打開信件並點擊驗證連結，完成後就可以登入毛起來。
      </p>

      <div class="notice-box">
        <p>沒有收到信嗎？</p>
        <span>請先檢查垃圾郵件、促銷內容或稍等幾分鐘。</span>
      </div>

      <p v-if="message" class="status-text success">
        {{ message }}
      </p>

      <p v-if="errorMessage" class="status-text error">
        {{ errorMessage }}
      </p>

      <div class="actions">
        <button class="primary-btn" type="button" @click="goLogin">前往登入</button>

        <button
          class="secondary-btn"
          type="button"
          :disabled="loading || countdown > 0"
          @click="resendEmail"
        >
          <span v-if="loading">寄送中...</span>
          <span v-else-if="countdown > 0"> {{ countdown }} 秒後可重寄 </span>
          <span v-else>重新寄送驗證信</span>
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped lang="scss">
.verify-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background:
    radial-gradient(circle at 16% 18%, rgba(254, 247, 233, 0.75), transparent 34%),
    radial-gradient(circle at 82% 72%, rgba(233, 216, 198, 0.32), transparent 36%),
    radial-gradient(circle at 50% 100%, rgba(249, 220, 194, 0.35), transparent 42%),
    linear-gradient(135deg, #f6ead6 0%, #f7efe5 45%, #f4ebe2 100%);
}

.verify-card {
  width: min(520px, 100%);
  padding: 46px 42px;
  border-radius: 34px;
  text-align: center;
  background: rgba(255, 248, 235, 0.96);
  border: 1px solid rgba(145, 106, 73, 0.18);
  box-shadow:
    0 24px 60px rgba(8, 24, 31, 0.32),
    inset 0 0 0 1px rgba(255, 255, 255, 0.45);
}

.mail-icon {
  width: 86px;
  height: 86px;
  margin: 0 auto 22px;
  border-radius: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff6e6;
  background: linear-gradient(135deg, #f1d9bc 0%, #f7e8c9 100%);
  box-shadow: 0 16px 34px rgba(93, 74, 54, 0.22);

  svg {
    display: block;
  }
}

.eyebrow {
  margin: 0 0 10px;
  font-size: 14px;
  letter-spacing: 0.18em;
  color: #9b744f;
  font-weight: 700;
}

h1 {
  margin: 0;
  color: #253f49;
  font-size: 30px;
  line-height: 1.35;
  letter-spacing: 0.04em;
}

.desc {
  margin: 18px 0 0;
  color: #5e6b6f;
  font-size: 15px;
  line-height: 1.9;

  strong {
    color: #2f5f6b;
    font-weight: 800;
    word-break: break-all;
  }
}

.notice-box {
  margin-top: 26px;
  padding: 18px 20px;
  border-radius: 22px;
  background: rgba(127, 165, 173, 0.14);
  border: 1px dashed rgba(75, 116, 125, 0.28);

  p {
    margin: 0 0 6px;
    color: #314f59;
    font-weight: 800;
  }

  span {
    color: #6d7473;
    font-size: 14px;
    line-height: 1.7;
  }
}

.status-text {
  margin: 18px 0 0;
  font-size: 14px;
  font-weight: 700;

  &.success {
    color: #5f7f55;
  }

  &.error {
    color: #b35f55;
  }
}

.actions {
  display: flex;
  gap: 14px;
  margin-top: 30px;

  button {
    flex: 1;
    height: 48px;
    border-radius: 999px;
    border: none;
    font-weight: 800;
    cursor: pointer;
    transition:
      transform 0.2s ease,
      box-shadow 0.2s ease,
      opacity 0.2s ease;

    &:hover:not(:disabled) {
      transform: translateY(-2px);
    }

    &:disabled {
      cursor: not-allowed;
      opacity: 0.55;
    }
  }
}

.primary-btn {
  color: #fff8ea;
  background: linear-gradient(135deg, #5f91a0 25%, #3f7d92 100%);
  box-shadow: 0 12px 24px rgba(39, 79, 92, 0.3);
}

.secondary-btn {
  color: #385963;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(82, 123, 132, 0.22) !important;
}

@media (max-width: 560px) {
  .verify-card {
    padding: 38px 24px;
    border-radius: 28px;
  }

  h1 {
    font-size: 25px;
  }

  .actions {
    flex-direction: column;
  }
}
</style>
