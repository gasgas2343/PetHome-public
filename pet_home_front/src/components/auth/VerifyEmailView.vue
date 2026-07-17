<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CheckCircle2, XCircle, LoaderCircle } from 'lucide-vue-next'
import { verifyEmailApi } from '@/api/auth'

const route = useRoute()
const router = useRouter()

const status = ref('loading')
// loading | success | error

const message = ref('正在驗證您的 Email，請稍候...')

async function verifyEmail() {
  const token = route.query.token

  console.log('email token:', token)

  if (!token) {
    status.value = 'error'
    message.value = '驗證連結不完整，請重新申請驗證信。'
    return
  }

  try {
    const response = await verifyEmailApi(token)

    status.value = 'success'
    message.value = response.data?.message || 'Email 驗證成功'
  } catch (error) {
    console.error('Email 驗證失敗', error)

    message.value = error.response?.data?.message || 'Email 驗證失敗，連結可能已過期或已使用。'

    status.value = 'error'
  }
}

function goLogin() {
  router.push('/login')
}

function goHome() {
  router.push('/')
}

onMounted(() => {
  verifyEmail()
})
</script>

<template>
  <main class="verify-email-page">
    <section class="verify-card">
      <div class="icon-wrap" :class="status">
        <LoaderCircle v-if="status === 'loading'" class="spin" :size="46" />
        <CheckCircle2 v-else-if="status === 'success'" :size="48" />
        <XCircle v-else :size="48" />
      </div>

      <h1>
        <template v-if="status === 'loading'">Email 驗證中</template>
        <template v-else-if="status === 'success'">驗證成功</template>
        <template v-else>驗證失敗</template>
      </h1>

      <p>{{ message }}</p>

      <div v-if="status !== 'loading'" class="action-row">
        <button v-if="status === 'success'" type="button" class="primary-btn" @click="goHome">
          回到首頁
        </button>

        <button v-else type="button" class="primary-btn" @click="goLogin">回到登入頁</button>
      </div>
    </section>
  </main>
</template>

<style scoped lang="scss">
.verify-email-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 40px 20px;
  background:
    radial-gradient(circle at top left, rgba(229, 167, 80, 0.18), transparent 36%),
    linear-gradient(135deg, #fffaf3 0%, #f3e7d6 100%);
}

.verify-card {
  width: min(460px, 100%);
  padding: 42px 34px 36px;
  border-radius: 32px;
  background: rgba(255, 250, 243, 0.95);
  border: 1px solid rgba(171, 133, 94, 0.2);
  box-shadow: 0 26px 70px rgba(70, 47, 31, 0.18);
  text-align: center;

  h1 {
    margin: 18px 0 12px;
    color: #2f2118;
    font-size: 30px;
    font-weight: 900;
    letter-spacing: 0.04em;
  }

  p {
    margin: 0;
    color: #7a6655;
    font-size: 15px;
    line-height: 1.8;
  }
}

.icon-wrap {
  width: 86px;
  height: 86px;
  margin: 0 auto;
  display: grid;
  place-items: center;
  border-radius: 999px;

  &.loading {
    background: #f3e3cf;
    color: #9a6b45;
  }

  &.success {
    background: rgba(124, 166, 160, 0.18);
    color: #2f8fc2;
  }

  &.error {
    background: rgba(199, 83, 69, 0.12);
    color: #c75345;
  }
}

.spin {
  animation: spin 0.85s linear infinite;
}

.action-row {
  margin-top: 28px;
}

.primary-btn {
  border: 0;
  border-radius: 999px;
  padding: 13px 28px;
  background: #2f8fc2;
  color: #fff;
  font-size: 15px;
  font-weight: 900;
  cursor: pointer;
  box-shadow: 0 14px 28px rgba(47, 143, 194, 0.22);
  transition:
    transform 0.18s ease,
    background 0.18s ease,
    box-shadow 0.18s ease;

  &:hover {
    transform: translateY(-1px);
    background: #247cad;
    box-shadow: 0 18px 34px rgba(47, 143, 194, 0.28);
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
