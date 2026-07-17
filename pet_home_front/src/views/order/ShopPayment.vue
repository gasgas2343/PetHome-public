<template>
  <div class="payment-wrapper">
    <div class="payment-card">
      <h2>💳 確認付款</h2>
      <el-divider />

      <!-- 倒數計時 -->
      <div v-if="countdown > 0" style="text-align: center; margin-bottom: 16px;">
        <el-alert
          :title="`⏰ 請在 ${formatCountdown} 內完成付款，逾時訂單將自動取消`"
          type="warning"
          :closable="false"
          show-icon
        />
      </div>
      <div v-else style="text-align: center; margin-bottom: 16px;">
        <el-alert
          title="⚠️ 付款時間已逾時，請重新下單"
          type="error"
          :closable="false"
          show-icon
        />
      </div>

      <el-descriptions border :column="1" style="margin-bottom: 24px;">
        <el-descriptions-item label="訂單編號">{{ orderId }}</el-descriptions-item>
        <el-descriptions-item label="付款金額">
          <span style="font-size: 20px; color: #f56c6c; font-weight: bold;">
            NT$ {{ amount }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="付款方式">信用卡</el-descriptions-item>
      </el-descriptions>

      <!-- 模擬信用卡 -->
      <div class="fake-card">
        <div class="fake-card-number">**** **** **** 1234</div>
        <div class="fake-card-row">
          <span>有效期限：12/28</span>
          <span>CVV：***</span>
        </div>
        <p class="fake-hint">（專題模擬用，不會實際扣款）</p>
      </div>

      <el-button
        type="danger"
        size="large"
        style="width: 100%; margin-top: 24px;"
        :loading="loading"
        :disabled="countdown <= 0"
        @click="submitPayment"
      >
        {{ loading ? '處理中...' : `確認付款 NT$ ${amount}（模擬）` }}
      </el-button>

      <!-- 綠界付款按鈕 -->
      <el-button
        type="primary"
        size="large"
        style="width: 100%; margin-top: 12px;"
        :loading="ecpayLoading"
        :disabled="countdown <= 0"
        @click="payWithECPay"
      >
        {{ ecpayLoading ? '導向中...' : `使用綠界付款 NT$ ${amount}` }}
      </el-button>

      <div v-if="resultMsg" :class="['result-msg', success ? 'success' : 'error']">
        {{ resultMsg }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { userAuthStore } from '@/stores/auth.js'

const route = useRoute()
const router = useRouter()
const authStore = userAuthStore()

const orderId = route.query.orderId
const amount = route.query.amount
const loading = ref(false)
const ecpayLoading = ref(false)
const resultMsg = ref('')
const success = ref(false)

// 倒數計時
const countdown = ref(30 * 60) // 30分鐘
let timer = null

const formatCountdown = computed(() => {
  const m = Math.floor(countdown.value / 60).toString().padStart(2, '0')
  const s = (countdown.value % 60).toString().padStart(2, '0')
  return `${m}:${s}`
})

onMounted(() => {
  timer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      clearInterval(timer)
    }
  }, 1000)
})

onUnmounted(() => {
  clearInterval(timer)
})

const submitPayment = async () => {
  if (countdown.value <= 0) {
    ElMessage.error('付款時間已逾時，請重新下單')
    return
  }
  loading.value = true
  resultMsg.value = ''
  try {
    await axios.post(`${import.meta.env.VITE_API_BASE_URL}/payments/mock-pay`, {
      orderId: Number(orderId),
      method: 'CREDIT',
      amount: Number(amount)
    }, {
      headers: { Authorization: `Bearer ${authStore.accessToken}` }
    })
    success.value = true
    resultMsg.value = '✅ 付款成功！即將跳轉至訂單頁...'
    ElMessage.success('付款成功！')
    clearInterval(timer)
    setTimeout(() => router.push('/shop/member?tab=orders'), 2000)
  } catch (e) {
    success.value = false
    resultMsg.value = '❌ 付款失敗，請稍後再試'
    ElMessage.error('付款失敗')
  } finally {
    loading.value = false
  }
}

// ===== 綠界付款 =====
const payWithECPay = async () => {
  if (countdown.value <= 0) {
    ElMessage.error('付款時間已逾時，請重新下單')
    return
  }
  ecpayLoading.value = true
  try {
    const res = await axios.post(`${import.meta.env.VITE_API_BASE_URL}/payments/ecpay/create`, {
      orderId: Number(orderId),
      totalAmount: Number(amount),
      itemName: `PetHome訂單#${orderId}`
    }, {
      headers: { Authorization: `Bearer ${authStore.accessToken}` }
    })

    const params = res.data.data || res.data
    submitToECPay(params)
  } catch (e) {
    ElMessage.error('建立綠界付款訂單失敗，請稍後再試')
    ecpayLoading.value = false
  }
}

// 動態組一個隱藏 form，自動 submit（POST）到綠界付款頁
const submitToECPay = (params) => {
  const form = document.createElement('form')
  form.method = 'POST'
  form.action = params.actionUrl   // 改用後端動態回傳的網址

  Object.entries(params).forEach(([key, value]) => {
    if (key === 'actionUrl') return   // actionUrl 只是給前端用的，不是要送給綠界的參數，要排除掉
    const input = document.createElement('input')
    input.type = 'hidden'
    input.name = key
    input.value = value
    form.appendChild(input)
  })

  document.body.appendChild(form)
  form.submit()
}
</script>

<style scoped>
.payment-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f5f7fa;
}
.payment-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  width: 480px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}
.fake-card {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  padding: 24px;
  color: white;
}
.fake-card-number {
  font-size: 20px;
  letter-spacing: 4px;
  margin-bottom: 16px;
}
.fake-card-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}
.fake-hint {
  margin-top: 12px;
  font-size: 12px;
  opacity: 0.7;
  text-align: center;
}
.result-msg {
  margin-top: 16px;
  padding: 12px;
  border-radius: 8px;
  text-align: center;
  font-weight: bold;
}
.success { background: #f0f9eb; color: #67c23a; }
.error { background: #fef0f0; color: #f56c6c; }
</style>