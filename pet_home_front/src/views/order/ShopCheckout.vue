<template>
  
  <div style="max-width: 900px; margin: 30px auto; padding: 0 20px;">
    <h2>🛒 結帳</h2>
    <el-divider />

    <el-row :gutter="24">
      <!-- 左側：購物車商品摘要 -->
      <el-col :span="10">
        <h4>訂單商品</h4>
        <el-card style="margin-bottom: 16px;" v-for="item in cartItems" :key="item.cartId">
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div style="display: flex; align-items: center; gap: 12px;">
              <img :src="item.imageUrl || '/images/default.jpg'" style="width: 60px; height: 60px; object-fit: cover; border-radius: 8px;">
              <div>
                <div style="font-weight: bold;">{{ item.productName }}</div>
                <div style="color: #909399; font-size: 13px;">x{{ item.quantity }}</div>
              </div>
            </div>
            <div style="color: #f56c6c; font-weight: bold;">${{ item.totalAmount }}</div>
          </div>
        </el-card>

        <el-divider />
        <div style="text-align: right; font-size: 15px;">
          商品小計：<span style="font-weight: bold;">${{ cartTotal }}</span>
        </div>
        <div v-if="discountAmount > 0" style="text-align: right; color: #67c23a;">
          優惠折抵：-${{ discountAmount }}
        </div>
        <div style="text-align: right; font-size: 18px; font-weight: bold; color: #f56c6c; margin-top: 8px;">
          實付金額：${{ cartTotal - discountAmount }}
        </div>
      </el-col>

      <!-- 右側：填寫資料 -->
      <el-col :span="14">
        <h4>填寫收件資料</h4>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
          <el-form-item label="收件人姓名" prop="recipientName">
            <el-input v-model="form.recipientName" placeholder="請輸入收件人真實姓名" />
          </el-form-item>

          <el-form-item label="收件人電話" prop="recipientPhone">
            <el-input v-model="form.recipientPhone" placeholder="例：0912345678" />
          </el-form-item>

          <el-form-item label="配送方式" prop="shippingMethod">
            <el-select v-model="form.shippingMethod" style="width: 100%;" @change="handleMethodChange">
              <el-option label="宅配到府" value="HOME_DELIVERY" />
              <el-option label="超商取貨" value="CVS" />
            </el-select>
          </el-form-item>

          <template v-if="form.shippingMethod === 'HOME_DELIVERY'">
            <el-form-item label="配送城市" prop="shippingCity">
              <el-input v-model="form.shippingCity" placeholder="例如：台北市" />
            </el-form-item>
            <el-form-item label="詳細地址" prop="shippingAddress">
              <el-input v-model="form.shippingAddress" placeholder="例如：信義路五段7號7樓" />
            </el-form-item>
            <el-form-item label="郵遞區號" prop="postalCode">
              <el-input v-model="form.postalCode" placeholder="例如：110" />
            </el-form-item>
          </template>

          <template v-if="form.shippingMethod === 'CVS'">
            <el-form-item label="超商門市ID" prop="cvsStoreId">
              <el-input v-model="form.cvsStoreId" placeholder="請輸入門市店號（5-6碼）" />
            </el-form-item>
          </template>

          <el-divider />

          <!-- 優惠券 -->
          <el-form-item label="使用優惠券">
            <el-select v-model="form.userCouponId" placeholder="選擇可使用的優惠券" clearable style="width: 100%;" @change="applyDiscount">
              <el-option
                v-for="coupon in availableCoupons"
                :key="coupon.id"
                :label="coupon.label"
                :value="coupon.id"
              />
            </el-select>
            <div v-if="form.userCouponId" style="color: #67c23a; font-size: 13px; margin-top: 4px;">
              已套用優惠，折抵 ${{ discountAmount }}
            </div>
          </el-form-item>

          <el-form-item label-width="0" style="text-align: right; margin-top: 16px;">
            <el-button @click="router.push('/cart')" style="margin-right: 12px;">返回購物車</el-button>
            <el-button type="primary" size="large" :loading="submitting" @click="handleCheckout">
              確認下單
            </el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { userAuthStore } from '@/stores/auth.js';

const router = useRouter();
const authStore = userAuthStore(); // ✅ 拿掉 userStore
const formRef = ref(null);
const submitting = ref(false);
const cartItems = ref([]);
const availableCoupons = ref([]);
const discountAmount = ref(0);

const createAuthAxios = () => {
  return axios.create({
    baseURL: `${import.meta.env.VITE_API_BASE_URL}`,
    headers: { Authorization: `Bearer ${authStore.accessToken}` }
  });
};

const form = ref({
  userId: authStore.id, // ✅
  userCouponId: null,
  recipientName: '',
  recipientPhone: '',
  shippingCity: '',
  shippingAddress: '',
  postalCode: '',
  shippingMethod: 'HOME_DELIVERY',
  cvsStoreId: ''
});

const rules = {
  recipientName: [{ required: true, message: '請輸入收件人姓名', trigger: 'blur' }],
  recipientPhone: [{ required: true, message: '請輸入收件人電話', trigger: 'blur' }],
  shippingMethod: [{ required: true, message: '請選擇配送方式', trigger: 'change' }]
};

const cartTotal = computed(() =>
  cartItems.value.reduce((sum, item) => sum + Number(item.totalAmount || 0), 0)
);

const fetchCart = async () => {
  const api = createAuthAxios();
  try {
    const res = await api.get(`/cart/user/${authStore.id}`); // ✅
    cartItems.value = res.data.data || res.data || [];
  } catch (err) {
    ElMessage.error('載入購物車失敗');
  }
};

const fetchCoupons = async () => {
  const api = createAuthAxios();
  try {
    const res = await api.get(`/coupons/user/${authStore.id}`);
    const data = res.data.data || res.data || [];
    availableCoupons.value = data.map(c => ({
      id: c.userCouponId, // ✅ 用 userCouponId 作為選擇的 id
      label: `${c.title}（折扣 ${c.discountType === 'FIXED' ? 'NT$' : ''}${c.discountValue}${c.discountType === 'PERCENT' ? '%' : ''}）`,
      discountType: c.discountType,
      discountValue: c.discountValue
    }));
  } catch (err) {
    console.warn('優惠券載入失敗');
    availableCoupons.value = [];
  }
};
const applyDiscount = () => {
  if (!form.value.userCouponId) {
    discountAmount.value = 0
    return
  }
  const coupon = availableCoupons.value.find(c => c.id === form.value.userCouponId)
  if (!coupon) return

  // 只計算非閃購商品的金額（unitPrice < originalPrice 代表閃購）
  const originalTotal = cartItems.value.reduce((sum, item) => {
    const isFlashSale = Number(item.unitPrice) < Number(item.originalPrice)
    if (isFlashSale) return sum
    return sum + Number(item.totalAmount || 0)
  }, 0)

  if (originalTotal === 0) {
    discountAmount.value = 0
    ElMessage.warning('購物車內商品均為特賣商品，無法使用優惠券')
    form.value.userCouponId = null
    return
  }

  if (coupon.discountType === 'FIXED') {
    discountAmount.value = Math.min(coupon.discountValue, originalTotal)
  } else if (coupon.discountType === 'PERCENT') {
    discountAmount.value = Math.floor(originalTotal * (1 - coupon.discountValue / 100))
  }
}

const handleMethodChange = () => {
  if (form.value.shippingMethod === 'HOME_DELIVERY') {
    form.value.cvsStoreId = '';
  } else {
    form.value.shippingCity = '';
    form.value.shippingAddress = '';
    form.value.postalCode = '';
  }
};

const handleCheckout = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    submitting.value = true;
    const api = createAuthAxios();
    try {
      form.value.userId = authStore.id; // ✅
      const res = await api.post('/orders/create', form.value);
      const savedOrder = res.data.data || res.data;
      ElMessage.success(`訂單建立成功！單號：${savedOrder.orderNo}`);
      router.push({
  path: '/payment',
  query: {
    orderId: savedOrder.id,
    amount: cartTotal.value - discountAmount.value
  }
})
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '下單失敗，請檢查庫存或重試');
    } finally {
      submitting.value = false;
    }
  });
};

onMounted(() => {
  fetchCart();
  fetchCoupons();
});
</script>