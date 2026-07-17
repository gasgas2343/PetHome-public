<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { verifyLoginTotpApi } from '@/api/auth'
import { userAuthStore } from '@/stores/auth'
import Swal from 'sweetalert2'

const router = useRouter()
const authStore = userAuthStore()

const code = ref('')
const errorMessage = ref('')
const loading = ref(false)

onMounted(() => {
  const loginChallengeToken = sessionStorage.getItem('twoFactorToken')

  if (!loginChallengeToken) {
    router.replace('/login')
  }
})

const submitVerify = async () => {
  errorMessage.value = ''

  if (!/^\d{6}$/.test(code.value)) {
    errorMessage.value = '請輸入 6 位數驗證碼'
    return
  }

  try {
    loading.value = true

    const requestBody = {
      loginChallengeToken: sessionStorage.getItem('twoFactorToken'),
      code: code.value,
    }

    const response = await verifyLoginTotpApi(requestBody)
    const data = response.data.data

    sessionStorage.removeItem('twoFactorToken')

    await Swal.fire({
      icon: 'success',
      title: '登入成功',
      text: '歡迎回來毛起來',
      timer: 900,
      showConfirmButton: false,
      background: '#fffaf2',
      color: '#3f2a1d',
      customClass: {
        popup: 'maokilai-swal-popup',
        title: 'maokilai-swal-title',
        htmlContainer: 'maokilai-swal-text',
      },
    })

    authStore.setLoginData(data)

    router.push('/')
  } catch (error) {
    console.error('TOTP 登入驗證失敗', error)
    errorMessage.value = error.response?.data?.message || '驗證碼錯誤，請重新確認'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="two-factor-page">
    <section class="verify-card">
      <p class="eyebrow">Two-Factor Authentication</p>
      <h1>輸入驗證碼</h1>

      <p class="desc">請開啟你的驗證器 App，輸入目前顯示的 6 位數驗證碼。</p>

      <input
        v-model.trim="code"
        maxlength="6"
        inputmode="numeric"
        autocomplete="one-time-code"
        placeholder="輸入 6 位數驗證碼"
        @keyup.enter="submitVerify"
      />

      <p v-if="errorMessage" class="error-text">
        {{ errorMessage }}
      </p>

      <button type="button" :disabled="loading" @click="submitVerify">
        {{ loading ? '驗證中...' : '確認登入' }}
      </button>
    </section>
  </main>
</template>

<style scoped>
.two-factor-page {
  min-height: 77vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background: #fff7ec;
  color: #3f2a1d;
}

.verify-card {
  width: min(460px, 100%);
  padding: 38px;
  border-radius: 32px;
  background: #fffaf2;
  box-shadow: 0 24px 70px rgba(52, 38, 24, 0.16);
  border: 1px solid rgba(170, 132, 94, 0.18);
}

.eyebrow {
  margin: 0 0 10px;
  color: #9a6b45;
  font-size: 13px;
  font-weight: 900;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

h1 {
  margin: 0 0 12px;
  font-size: 30px;
  font-weight: 950;
}

.desc {
  margin: 0 0 24px;
  color: #6f5745;
  line-height: 1.8;
}

input {
  width: 100%;
  height: 54px;
  border-radius: 18px;
  border: 1px solid rgba(170, 132, 94, 0.3);
  background: #fff;
  padding: 0 18px;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 6px;
  text-align: center;
  outline: none;
  color: #3f2a1d;
}

input::placeholder {
  color: rgba(111, 93, 77, 0.5);
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

input:focus {
  border-color: #8db8b2;
  box-shadow: 0 0 0 4px rgba(141, 184, 178, 0.18);
}

.error-text {
  margin: 12px 0 0;
  color: #c75345;
  font-size: 14px;
  font-weight: 800;
}

button {
  width: 100%;
  margin-top: 24px;
  height: 52px;
  border: none;
  border-radius: 999px;
  background: #7ca6a0;
  color: #fff;
  font-weight: 900;
  cursor: pointer;
  box-shadow: 0 12px 24px rgba(124, 166, 160, 0.22);
}

button:disabled {
  opacity: 0.62;
  cursor: not-allowed;
  box-shadow: none;
}
</style>
