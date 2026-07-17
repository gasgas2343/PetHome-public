<template>
  <div class="coupon-issue-page">
    <h3>優惠券管理</h3>
    <hr>

    <!-- 新增優惠券 -->
    <div class="card p-4 shadow-sm mb-4">
      <h5>新增優惠券</h5>
      <div class="row g-2">
        <div class="col-md-4">
          <input v-model="newCoupon.title" class="form-control" placeholder="優惠券名稱">
        </div>
        <div class="col-md-3">
          <select v-model="newCoupon.discountType" class="form-control">
            <option value="FIXED">固定折扣</option>
            <option value="PERCENT">百分比折扣</option>
          </select>
        </div>
        <div class="col-md-2">
          <input v-model="newCoupon.discountValue" type="number" class="form-control" placeholder="折扣值">
        </div>
        <div class="col-md-3">
          <input v-model="newCoupon.minSpend" type="number" class="form-control" placeholder="最低消費">
        </div>
        <div class="col-md-2">
          <input v-model="newCoupon.validDays" type="number" class="form-control" placeholder="有效天數">
        </div>
        <div class="col-12 mt-2">
          <button @click="createCoupon" class="btn btn-success w-100">新增優惠券</button>
        </div>
      </div>
    </div>

    <!-- 優惠券列表 -->
    <div class="card p-4 shadow-sm mb-4">
      <h5>現有優惠券</h5>
      <table class="table table-hover">
        <thead class="table-light">
          <tr>
            <th>名稱</th>
            <th>類型</th>
            <th>折扣</th>
            <th>最低消費</th>
            <th>有效天數</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="c in coupons" :key="c.id">
            <td>{{ c.title }}</td>
            <td>{{ c.discountType === 'FIXED' ? '固定折扣' : '百分比' }}</td>
            <td>{{ c.discountType === 'FIXED' ? `NT$${c.discountValue}` : `${c.discountValue}%` }}</td>
            <td>{{ c.minSpend ? `NT$${c.minSpend}` : '無限制' }}</td>
            <td>{{ c.validDays ?? 30 }} 天</td>
            <td>
              <button @click="deleteCoupon(c.id)" class="btn btn-danger btn-sm">刪除</button>
            </td>
          </tr>
          <tr v-if="coupons.length === 0">
            <td colspan="6" class="text-center text-muted">尚無優惠券</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 發放優惠券給會員 -->
    <div class="card p-4 shadow-sm">
      <h5>發放優惠券給會員</h5>
      <div class="mb-3">
        <label>目標會員 ID</label>
        <input v-model="issueForm.userId" type="number" class="form-control" placeholder="請輸入會員 ID">
      </div>
      <div class="mb-3">
        <label>選擇優惠券</label>
        <select v-model="issueForm.couponId" class="form-control">
          <option value="" disabled>請選擇優惠券</option>
          <option v-for="c in coupons" :key="c.id" :value="c.id">
            {{ c.title }} ({{ c.discountType === 'FIXED' ? `NT$${c.discountValue}` : `${c.discountValue}%` }})
          </option>
        </select>
      </div>
      <button @click="submitIssue" class="btn btn-primary w-100">發放優惠券</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { userAuthStore } from '@/stores/auth.js';

const createAuthAxios = () => {
  const authStore = userAuthStore();
  return axios.create({
    baseURL: `${import.meta.env.VITE_API_BASE_URL}`,
    headers: { Authorization: `Bearer ${authStore.accessToken}` }
  });
};

const coupons = ref([]);
const newCoupon = ref({
  title: '',
  discountType: 'FIXED',
  couponScope: 'ALL',
  discountValue: 0,
  minSpend: null,
  validDays: 30
});
const issueForm = ref({
  userId: '',
  couponId: '',
  sourceType: 'MANUAL',
  sourceKey: 'ADMIN_' + Date.now()
});

const fetchCoupons = async () => {
  const api = createAuthAxios();
  try {
    const res = await api.get('/coupons');
    coupons.value = res.data.data ?? res.data;
  } catch (error) {
    console.error("無法取得優惠券列表", error);
  }
};

const createCoupon = async () => {
  if (!newCoupon.value.title) return alert("請填寫優惠券名稱");
  const api = createAuthAxios();
  try {
    await api.post('/coupons', newCoupon.value);
    alert("新增成功");
    fetchCoupons();
    newCoupon.value = { title: '', discountType: 'FIXED', couponScope: 'ALL', discountValue: 0, minSpend: null, validDays: 30 };
  } catch (error) {
    alert("新增失敗");
  }
};

const deleteCoupon = async (id) => {
  if (!confirm("確定刪除此優惠券？")) return;
  const api = createAuthAxios();
  try {
    await api.delete(`/coupons/${id}`);
    fetchCoupons();
  } catch (error) {
    alert("刪除失敗");
  }
};

const submitIssue = async () => {
  if (!issueForm.value.userId || !issueForm.value.couponId) {
    return alert("請填寫完整資訊");
  }
  const api = createAuthAxios();
  try {
    const res = await api.post('/admin/coupons/issue', {
      userId: issueForm.value.userId,
      couponId: issueForm.value.couponId,
      sourceType: 'MANUAL',
      sourceKey: `USER_${issueForm.value.userId}_COUPON_${issueForm.value.couponId}`,
      validDays: null
    });
    if (res.data.code !== 200) {
      alert("發放失敗：" + res.data.message);
    } else {
      alert("優惠券發放成功！");
    }
  } catch (error) {
    const msg = error.response?.data?.message || "發放失敗，請稍後再試";
    alert("發放失敗：" + msg);
  }
};

onMounted(fetchCoupons);
</script>