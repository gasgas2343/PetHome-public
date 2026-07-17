<!-- <template>
  <div class="container mt-5">
    <h2 class="mb-4">❤️ 我的喜愛清單</h2>

    <div v-if="cartStore.favorites.length === 0" class="text-center py-5">
      <p>目前沒有收藏的商品喔！</p>
      <RouterLink to="/" class="btn btn-brown">去逛逛</RouterLink>
    </div>

    <div class="row g-4" v-else>
      <div v-for="item in cartStore.favorites" :key="item.id" class="col-6 col-md-3">
        <div class="favorite-card p-3 border rounded-4 shadow-sm bg-white h-100 d-flex flex-column">
          
          <div class="img-wrapper mb-2" style="aspect-ratio: 1/1; overflow: hidden;">
          <img 
  :src="item.image" 
  class="w-100 h-100 rounded-3" 
  style="object-fit: cover; background: #eee;" 
  alt="商品圖片"
  @error="console.log('圖片載入失敗，路徑為:', item.image)"
>
          </div>

          <h5 class="small fw-bold mt-2">{{ item.name }}</h5>
          <p class="text-danger fw-bold mt-auto">${{ item.price }}</p>
          
          <button 
            class="btn btn-sm btn-outline-danger w-100" 
            @click="cartStore.toggleFavorite(item)"
          >
            移除
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useCartStore } from '../stores/cartStore';

const cartStore = useCartStore();

// 除錯用：打開瀏覽器 F12 Console 即可看到資料結構
onMounted(() => {
  console.log('目前的喜愛清單內容:', cartStore.favorites);
});
</script>

<style scoped>
.btn-brown { 
  background: #5d4037; 
  color: white; 
}
.img-wrapper img {
  transition: transform 0.3s ease;
}
.img-wrapper img:hover {
  transform: scale(1.05); /* 滑鼠移入時圖片微微放大，增加互動感 */
}
</style> -->
<!-- <template>
  <div class="container mt-5">
    <h2 class="mb-4">❤️ 我的喜愛清單</h2>

    <div v-if="cartStore.favorites.length === 0" class="text-center py-5">
      <p>目前沒有收藏的商品喔！</p>
      <RouterLink to="/" class="btn btn-brown">去逛逛</RouterLink>
    </div>

    <div class="row g-4" v-else>
      <div v-for="item in cartStore.favorites" :key="item.id" class="col-6 col-md-3">
        <div class="favorite-card p-3 border rounded-4 shadow-sm bg-white h-100 d-flex flex-column">
          
          <div class="img-wrapper mb-2" style="aspect-ratio: 1/1; overflow: hidden;">
            <img 
              :src="item.image" 
              class="w-100 h-100 rounded-3" 
              style="object-fit: cover; background: #eee;" 
              alt="商品圖片"
            >
          </div>

          <h5 class="small fw-bold mt-2">{{ item.name }}</h5>
          <p class="text-danger fw-bold mt-auto">${{ item.price }}</p>
          
          <!-- 加入購物車 -->
          <!-- <button 
            class="btn btn-sm btn-brown w-100 mb-2" 
            @click="addToCart(item)"
          >
            加入購物車
          </button>

          <button 
            class="btn btn-sm btn-outline-danger w-100" 
            @click="cartStore.toggleFavorite(item)"
          >
            移除
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useCartStore } from '../stores/cartStore';
import axiosapi from '@/plugins/axios.js';
import Swal from 'sweetalert2';

const cartStore = useCartStore();

const addToCart = async (item) => {
  try {
    // 先查該商品的 SKU
    const skuRes = await axiosapi.get(`/api/products/${item.id}/skus`);
    const skus = skuRes.data.data ?? skuRes.data;
    if (!skus || skus.length === 0) {
      Swal.fire({ text: '此商品目前無法加入購物車', icon: 'warning' });
      return;
    }

    await axiosapi.post('/api/cart/add', {
      userId: 1,
      productSkuId: skus[0].id,  // ← 用 SKU id 而不是 product id
      quantity: 1
    });
    Swal.fire({ text: '已加入購物車', icon: 'success', timer: 1500, showConfirmButton: false });
  } catch (error) {
    console.error('加入購物車失敗:', error);
    Swal.fire({ text: '加入購物車失敗', icon: 'error' });
  }
};

onMounted(() => {
  console.log('目前的喜愛清單內容:', cartStore.favorites);
});
</script>

<style scoped>
.btn-brown { 
  background: #5d4037; 
  color: white; 
}
.btn-brown:hover {
  background: #4e342e;
  color: white;
}
.img-wrapper img {
  transition: transform 0.3s ease;
}
.img-wrapper img:hover {
  transform: scale(1.05);
}
</style>  -->

<template>
  <div class="container mt-5">
    <h2 class="mb-4">❤️ 我的喜愛清單</h2>

    <div v-if="cartStore.favorites.length === 0" class="text-center py-5">
      <p>目前沒有收藏的商品喔！</p>
      <RouterLink to="/shop" class="btn btn-brown">去逛逛</RouterLink>
    </div>

    <div class="row g-4" v-else>
      <div v-for="item in cartStore.favorites" :key="item.id" class="col-6 col-md-3">
        <div class="favorite-card p-3 border rounded-4 shadow-sm bg-white h-100 d-flex flex-column">
          
          <div class="img-wrapper mb-2" style="aspect-ratio: 1/1; overflow: hidden;">
            <img 
              :src="item.image" 
              class="w-100 h-100 rounded-3" 
              style="object-fit: cover; background: #eee;" 
              alt="商品圖片"
            >
          </div>

          <h5 class="small fw-bold mt-2">{{ item.name }}</h5>
          <p class="text-danger fw-bold mt-auto">${{ item.price }}</p>
          
          <button class="btn btn-sm btn-brown w-100 mb-2" @click="addToCart(item)">
            加入購物車
          </button>

          <button class="btn btn-sm btn-outline-danger w-100" @click="cartStore.toggleFavorite(item)">
            移除
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useCartStore } from '../stores/cartStore';
import { userAuthStore } from '@/stores/auth.js'; // ✅
import axios from 'axios';
import Swal from 'sweetalert2';

const cartStore = useCartStore();
const authStore = userAuthStore(); // ✅

const createAuthAxios = () => {
  return axios.create({
    baseURL: `${import.meta.env.VITE_API_BASE_URL}`,
    headers: { Authorization: `Bearer ${authStore.accessToken}` }
  });
};

const addToCart = async (item) => {
  const api = createAuthAxios(); // ✅
  try {
    const skuRes = await api.get(`/products/${item.id}/skus`); // ✅
    const skus = skuRes.data.data ?? skuRes.data;

    if (!skus || skus.length === 0) {
      Swal.fire({ text: '此商品目前無法加入購物車', icon: 'warning' });
      return;
    }

    await api.post('/cart/add', { // ✅
      userId: authStore.id, // ✅
      productSkuId: skus[0].id,
      quantity: 1
    });

    Swal.fire({ text: '已加入購物車', icon: 'success', timer: 1500, showConfirmButton: false });
  } catch (error) {
    console.error('加入購物車失敗:', error);
    Swal.fire({ text: '加入購物車失敗', icon: 'error' });
  }
};
</script>
<style scoped>
.btn-brown { 
  background: #5d4037; 
  color: white; 
}
.btn-brown:hover {
  background: #4e342e;
  color: white;
}
.img-wrapper img {
  transition: transform 0.3s ease;
}
.img-wrapper img:hover {
  transform: scale(1.05);
}
</style>