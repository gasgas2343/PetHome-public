<template>
  <div class="payment-page">
    <h2>確認付款</h2>
    <div class="order-summary">
      <p>訂單編號：#{{ orderId }}</p>
      <p>付款金額：NT$ {{ amount }}</p>
    </div>

    <!-- 模擬信用卡 UI -->
    <div class="fake-card-form">
      <h3>信用卡付款</h3>
      <input value="**** **** **** 1234" disabled />
      <input value="有效期限：12/28" disabled />
      <input value="CVV：***" disabled />
      <p class="hint">（專題模擬用，不會實際扣款）</p>
    </div>

    <button @click="submitPayment" :disabled="loading">
      {{ loading ? '處理中...' : '確認付款 NT$ ' + amount }}
    </button>

    <div v-if="result" :class="result.success ? 'msg-success' : 'msg-error'">
      {{ result.message }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const orderId = route.query.orderId
const amount = route.query.amount
const loading = ref(false)
const result = ref(null)

async function submitPayment() {
  loading.value = true
  result.value = null
  try {
    await axios.post('/payments/mock-pay', {
      orderId: Number(orderId),
      method: 'CREDIT',
      amount: Number(amount)
    })
    result.value = { success: true, message: '付款成功！即將跳轉至訂單頁...' }
    setTimeout(() => router.push('/shop/orders'), 2000)
  } catch (e) {
    result.value = { success: false, message: '付款失敗，請稍後再試' }
  } finally {
    loading.value = false
  }
}
</script>