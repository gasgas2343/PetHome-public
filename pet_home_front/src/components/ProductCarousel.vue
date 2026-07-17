<!-- <template>
  <div class="carousel-wrapper">
    <div class="header-row">
      <h2 class="section-title">{{ title }}</h2>
      <slot name="extra"></slot>
    </div>

    <Swiper
      :slides-per-view="2"
      :space-between="20"
      :breakpoints="{ 768: { slidesPerView: 4 } }"
      :navigation="true"
      :modules="[Navigation]"
      class="product-swiper"
    >
      <SwiperSlide v-for="product in products" :key="product.id">
        <div :class="['product-card', { 'sale-card': product.isSale }]">
          <div v-if="product.isSale" class="sale-badge">⚡ 限時特價</div>
          <img :src="product.img" class="card-img" />
          <h3 class="card-title">{{ product.name }}</h3>
          <p class="card-text">{{ product.desc }}</p>
          <div class="price-section">
            <span class="price">${{ product.price }}</span>
            <div class="button-group">
              <button
      :class="['btn-fav', { 'is-active': cartStore.favorites.find(item => item.id === product.id) }]"
      @click="cartStore.toggleFavorite(product)"
    >
      ❤️
    </button>
              <button class="btn-cart" @click="cartStore.addToCart(product)">
    加入購物車
  </button>

            </div>
          </div>
        </div>
      </SwiperSlide>
    </Swiper>
  </div>
</template>

<script setup>
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation } from 'swiper/modules'
import { useCartStore } from '../stores/cartStore' // 確保路徑指向正確的 Pinia store
import 'swiper/css'
import 'swiper/css/navigation'

// 1. 唯一一次宣告 Props
const props = defineProps(['title', 'products'])

// 2. 初始化 Store
const cartStore = useCartStore()
</script>


<style scoped>
.carousel-wrapper { margin: 40px auto; max-width: 1200px; padding: 0 20px; overflow: hidden; }
.header-row { display: flex; align-items: center; margin-bottom: 20px; }
.section-title { color: #5d4037; margin: 0; }
.product-card { background: white; border-radius: 20px; padding: 15px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); position: relative; }
.card-img { width: 100%; aspect-ratio: 4 / 3; object-fit: cover; border-radius: 15px; display: block; margin-bottom: 10px; }
.card-title { font-size: 1rem; color: #3e2723; margin: 5px 0; }
.card-text { font-size: 0.8rem; color: #8d6e63; height: 1.2em; overflow: hidden; }
.price-section { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.price { font-weight: bold; color: #d84315; }
.button-group { display: flex; gap: 5px; }
.btn-fav { background: #eee; border: none; border-radius: 8px; padding: 5px 8px; cursor: pointer; }
.btn-cart { background: #5d4037; color: white; border: none; border-radius: 8px; padding: 5px 10px; font-size: 0.8rem; cursor: pointer; }
.sale-badge { position: absolute; top: 25px; left: 25px; background: #d84315; color: white; padding: 3px 8px; border-radius: 5px; font-size: 0.7rem; }
.btn-fav { background: #eee; border: none; border-radius: 8px; padding: 5px 8px; cursor: pointer; transition: 0.3s; }
.btn-fav.is-active { background: #ffcdd2; color: #d32f2f; }

</style> -->
<template>
  <div class="carousel-wrapper">
    <div class="header-row">
      <h2 class="section-title">{{ title }}</h2>
      <slot name="extra"></slot>
    </div>

    <Swiper
      :slides-per-view="2"
      :space-between="20"
      :breakpoints="{ 768: { slidesPerView: 4 } }"
      :navigation="true"
      :modules="[Navigation]"
      class="product-swiper"
    >
      <SwiperSlide v-for="product in products" :key="product.id">
        <div :class="['product-card', { 'sale-card': product.isSale }]">
          <div v-if="product.isSale" class="sale-badge">⚡ 限時特價</div>
          <img :src="product.imageUrl" :alt="product.name" class="card-img" />
          <h3 class="card-title">{{ product.name }}</h3>
          <p class="card-text">{{ product.description }}</p>
          <div class="price-section">
           <div class="price-wrapper">
  <!-- 閃購：顯示原價劃掉 + 特賣價 -->
  <template v-if="product.isFlashSale">
    <span class="original-price">${{ product.originalPrice }}</span>
    <span class="sale-price">${{ product.basePrice }}</span>
  </template>
  <!-- 一般商品：只顯示原價 -->
  <span v-else class="price">${{ product.basePrice }}</span>
</div>
            <div class="button-group">
              <button
                :class="['btn-fav', { 'is-active': cartStore.favorites.find(item => item.id === product.id) }]"
                @click="cartStore.toggleFavorite(product)"
              >❤️</button>
              <button class="btn-cart" @click="addToCart(product)">
                加入購物車
              </button>
            </div>
          </div>
        </div>
      </SwiperSlide>
    </Swiper>
  </div>
</template>

<script setup>
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation } from 'swiper/modules'
import { useCartStore } from '../stores/cartStore'
import { userAuthStore } from '@/stores/auth.js'
import axios from 'axios'
import Swal from 'sweetalert2'
import 'swiper/css'
import 'swiper/css/navigation'
import http from '@/api/http'

defineProps(['title', 'products'])
const cartStore = useCartStore()
const authStore = userAuthStore() // ✅ 拿掉 userStore

const createAuthAxios = () => {
  return axios.create({
    // baseURL: 'http://192.168.36.143:8080/api'
    baseURL: 'http://localhost:8080/api',
    headers: { Authorization: `Bearer ${authStore.accessToken}` }
  });
};

const addToCart = async (product) => {
  const api = createAuthAxios();
  try {
    let finalSkuId = null;
    if (product.isFlashSale && product.flashSkuId) {
      finalSkuId = product.flashSkuId;
    } else {
      const skuRes = await api.get(`/products/${product.id}/skus`);
      const skus = skuRes.data.data || skuRes.data;
      if (!skus || skus.length === 0) {
        Swal.fire({ text: '此商品無可用規格', icon: 'warning' });
        return;
      }
      finalSkuId = skus[0].id;
    }
    const payload = {
      userId: authStore.id, // ✅
      productSkuId: Number(finalSkuId),
      quantity: 1,
      unitPrice: Number(product.basePrice)
    };
    await api.post('/cart/add', payload);
    Swal.fire({ text: '商品已成功加入購物車！', icon: 'success', timer: 1500, showConfirmButton: false });
  } catch (error) {
    Swal.fire({ text: error.response?.data?.message || '加入購物車失敗', icon: 'error' });
  }
};
</script>

<style scoped>
.original-price {
  text-decoration: line-through;
  color: #aaa;
  font-size: 0.85em;
  margin-right: 4px;
}
.sale-price {
  color: #e74c3c;
  font-weight: bold;
}
.price-wrapper { display: flex; align-items: center; gap: 4px; }
.carousel-wrapper { margin: 40px auto; max-width: 1200px; padding: 0 20px; overflow: hidden; }
.header-row { display: flex; align-items: center; margin-bottom: 20px; }
.section-title { color: #5d4037; margin: 0; }
.product-card { background: white; border-radius: 20px; padding: 15px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); position: relative; }
.card-img { width: 100%; aspect-ratio: 4 / 3; object-fit: cover; border-radius: 15px; display: block; margin-bottom: 10px; }
.card-title { font-size: 1rem; color: #3e2723; margin: 5px 0; }
.card-text { font-size: 0.8rem; color: #8d6e63; height: 1.2em; overflow: hidden; }
.price-section { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.price { font-weight: bold; color: #d84315; }
.button-group { display: flex; gap: 5px; }
.btn-fav { background: #eee; border: none; border-radius: 8px; padding: 5px 8px; cursor: pointer; transition: 0.3s; }
.btn-fav.is-active { background: #ffcdd2; color: #d32f2f; }
.btn-cart { background: #5d4037; color: white; border: none; border-radius: 8px; padding: 5px 10px; font-size: 0.8rem; cursor: pointer; }
.sale-badge { position: absolute; top: 25px; left: 25px; background: #d84315; color: white; padding: 3px 8px; border-radius: 5px; font-size: 0.7rem; }
</style>
