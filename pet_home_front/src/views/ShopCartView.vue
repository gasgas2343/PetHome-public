<!-- <template>
  <div class="container my-5">
    <h2 class="mb-4">🛒 我的購物車</h2>
    
    <div class="row">
      <div class="col-lg-8">
        <div v-for="item in cartStore.items" :key="item.id" class="card mb-3 shadow-sm rounded-4 border-0">
          <div class="row g-0 align-items-center p-3">
            <div class="col-3 col-md-2">
              <img :src="item.image" class="img-fluid rounded-3" :alt="item.name" style="width: 100px; height: 100px; object-fit: cover;">
            </div>
            
            <div class="col-9 col-md-10">
              <div class="d-flex justify-content-between align-items-center px-3">
                <div>
                  <h5 class="mb-1">{{ item.name }}</h5>
                  <p class="text-muted small mb-1">規格：{{ item.spec }}</p>
                  <p class="text-danger fw-bold mb-0">單價：${{ item.price }}</p>
                </div>
                
                <div class="text-end">
                  <div class="input-group mb-2">
                    <button class="btn btn-outline-secondary btn-sm" @click="cartStore.decrease(item.id)">-</button>
                    <span class="px-3 border d-flex align-items-center bg-light">{{ item.quantity }}</span>
                    <button class="btn btn-outline-secondary btn-sm" @click="cartStore.increase(item.id)">+</button>
                  </div>
                  <button class="btn btn-link text-danger btn-sm p-0" @click="cartStore.removeFromCart(item.id)">刪除</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-lg-4">
        <div class="card p-4 shadow-sm border-0 rounded-4">
          <h5>訂單摘要</h5>
          <hr>
          <div class="d-flex justify-content-between mb-2">
            <span>商品總額</span>
            <span>${{ totalAmount }}</span>
          </div>
          <button class="btn btn-primary w-100 rounded-3 mt-3">前往結帳</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue';
import { useCartStore } from '../stores/cartStore';
const cartStore = useCartStore();
onMounted(() => {
  cartStore.fetchCart();  // ← 加這行
});
// 自動計算總金額
const totalAmount = computed(() => {
  return cartStore.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
});
</script> -->

<!-- <template>
  <div class="container my-5">
    <h2 class="mb-4">🛒 我的購物車</h2>
    
    <div v-if="cartStore.items.length === 0" class="text-center py-5">
      <p class="text-muted">購物車是空的</p>
    </div>

    <div v-else class="row">
      <div class="col-lg-8">
        <div v-for="item in cartStore.items" :key="item.id" class="card mb-3 shadow-sm rounded-4 border-0">
          <div class="row g-0 align-items-center p-3">
            <div class="col-3 col-md-2">
              <img :src="item.image || '/images/default.jpg'" class="img-fluid rounded-3" :alt="item.name" style="width: 100px; height: 100px; object-fit: cover;">
            </div>
            
            <div class="col-9 col-md-10">
              <div class="d-flex justify-content-between align-items-center px-3">
                <div>
                  <h5 class="mb-1">{{ item.name }}</h5>
                  <p class="text-muted small mb-1">規格：{{ item.spec }}</p>
                  <p class="text-danger fw-bold mb-0">單價：${{ item.price }}</p>
                </div>
                
                <div class="text-end">
                  <div class="input-group mb-2">
                    <button class="btn btn-outline-secondary btn-sm" @click="cartStore.decrease(item.id)">-</button>
                    <span class="px-3 border d-flex align-items-center bg-light">{{ item.quantity }}</span>
                    <button class="btn btn-outline-secondary btn-sm" @click="cartStore.increase(item.id)">+</button>
                  </div>
                  <button class="btn btn-link text-danger btn-sm p-0" @click="cartStore.removeFromCart(item.skuId)">刪除</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-lg-4">
        <div class="card p-4 shadow-sm border-0 rounded-4">
          <h5>訂單摘要</h5>
          <hr>
          <div class="d-flex justify-content-between mb-2">
            <span>商品總額</span>
            <span>${{ totalAmount }}</span>
          </div>
          <button class="btn btn-primary w-100 rounded-3 mt-3">前往結帳</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, watch } from 'vue';
import { useCartStore } from '../stores/cartStore';
import { useUserStore } from '../stores/user'; // 引入 userStore

const cartStore = useCartStore();
const userStore = useUserStore();

// 當 userId 改變時，重新抓取該使用者的購物車
watch(() => userStore.userId, (newId) => {
  if (newId) {
    cartStore.fetchCart(newId);
  }
}, { immediate: true });

const totalAmount = computed(() => {
  return cartStore.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
});
</script> -->

<template>
  <div class="container my-5">
    <h2 class="mb-4">🛒 我的購物車 (會員 ID: {{ authStore.id || '未登入' }})</h2>
    
    <div v-if="cartItems.length === 0" class="text-center py-5">
      <p class="text-muted">購物車是空的</p>
    </div>

    <div v-else class="row">
      <div class="col-lg-8">
        <div v-for="item in cartItems" :key="item.cartId" class="card mb-3 shadow-sm rounded-4 border-0">
          <div class="row g-0 align-items-center p-3">
            <div class="col-3 col-md-2">
              <img :src="item.imageUrl || '/images/default.jpg'" class="img-fluid rounded-3" style="width: 100px; height: 100px; object-fit: cover;">
            </div>
            <div class="col-9 col-md-10">
              <div class="d-flex justify-content-between align-items-center px-3">
                <div>
                  <h5 class="mb-1">{{ item.productName }}</h5>
                  <p class="text-danger fw-bold mb-0">單價：${{ item.unitPrice }}</p>
                </div>
                <div class="text-end">
                  <div class="input-group mb-2">
                    <button class="btn btn-outline-secondary btn-sm" @click="updateQty(item, item.quantity - 1)">-</button>
                    <span class="px-3 border d-flex align-items-center bg-light">{{ item.quantity }}</span>
                    <button class="btn btn-outline-secondary btn-sm" @click="updateQty(item, item.quantity + 1)">+</button>
                  </div>
                  <button class="btn btn-link text-danger btn-sm p-0" @click="removeItem(item.cartId)">刪除</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-lg-4">
        <div class="card p-4 shadow-sm border-0 rounded-4">
          <h5>訂單摘要</h5>
          <hr>
          <div class="d-flex justify-content-between mb-2">
            <span>商品總額</span>
            <span>${{ totalAmount }}</span>
          </div>
          <button class="btn btn-primary w-100 rounded-3 mt-3" @click="goCheckout">前往結帳</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import axios from 'axios';
import { userAuthStore } from '@/stores/auth.js';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

const authStore = userAuthStore(); // ✅ 拿掉 userStore
const cartItems = ref([]);
const loading = ref(false);
const router = useRouter();

const createAuthAxios = () => {
  return axios.create({
    baseURL: `${import.meta.env.VITE_API_BASE_URL}`,
    headers: { Authorization: `Bearer ${authStore.accessToken}` }
  });
};

const goCheckout = () => {
  if (cartItems.value.length === 0) {
    ElMessage.warning('購物車是空的！');
    return;
  }
  router.push('/checkout');
};

const fetchCart = async (userId) => {
  loading.value = true;
  cartItems.value = [];
  const api = createAuthAxios();
  try {
    const res = await api.get(`/cart/user/${userId}`);
    cartItems.value = res.data.data || res.data || [];
  } catch (err) {
    console.error("購物車載入失敗", err);
  } finally {
    loading.value = false;
  }
};

watch(() => authStore.id, (newId) => { // ✅
  if (newId) fetchCart(newId);
}, { immediate: true });

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + Number(item.totalAmount || 0), 0);
});

const updateQty = async (item, newQty) => {
  if (newQty < 1) return;
  const api = createAuthAxios();
  try {
    await api.post(`/cart/add`, {
      userId: authStore.id, // ✅
      productSkuId: item.productSkuId,
      quantity: newQty - item.quantity,
      unitPrice: item.unitPrice
    });
    fetchCart(authStore.id); // ✅
  } catch (err) { ElMessage.error("更新數量失敗"); }
};

const removeItem = async (cartId) => {
  const api = createAuthAxios();
  try {
    const item = cartItems.value.find(i => i.cartId === cartId);
    if (!item) return;
    await api.delete(`/cart/${authStore.id}/sku/${item.productSkuId}`); // ✅
    ElMessage.success("商品已從購物車移除");
    fetchCart(authStore.id); // ✅
  } catch (err) { ElMessage.error("刪除失敗"); }
};
</script>