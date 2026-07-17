<template>
  <div class="container my-5" v-loading="loading">
    <h2 class="mb-4">🎫 我的優惠券 (會員 ID: {{ userStore.userId || '未登入' }})</h2>
    
    <div v-if="coupons.length === 0" class="text-center py-5">
      <p class="text-muted">目前沒有可使用的優惠券</p>
    </div>

    <div v-else class="row">
      <div class="col-md-6 col-lg-4" v-for="coupon in coupons" :key="coupon.id">
        <div class="card mb-3 shadow-sm border-primary rounded-4">
          <div class="card-body">
            <h5 class="card-title text-primary">{{ coupon.name }}</h5>
            <p class="card-text text-danger fw-bold mb-1">折扣折抵：${{ coupon.discountAmount || coupon.discount }}</p>
            <p class="card-text text-muted small">有效期限：{{ coupon.expiryDate || '永久有效' }}</p>
            <span class="badge bg-success">可使用</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import axios from 'axios';
import { useUserStore } from '../stores/user.js';

const userStore = useUserStore();
const coupons = ref([]);
const loading = ref(false);

const fetchCoupons = async (userId) => {
  loading.value = true;
  try {
    const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/coupons/user/${userId}`);
    coupons.value = res.data.data || res.data || [];
  } catch (err) {
    console.error("優惠券載入失敗", err);
  } finally {
    loading.value = false;
  }
};

watch(() => userStore.userId, (newId) => {
  if (newId) fetchCoupons(newId);
}, { immediate: true });
</script>