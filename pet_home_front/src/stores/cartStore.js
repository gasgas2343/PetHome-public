// import { defineStore } from 'pinia';
// import axios from 'axios';

// const BASE = 'http://localhost:8080';
// const USER_ID = 1; // 之後換成登入的 userId

// export const useCartStore = defineStore('cart', {
//   state: () => ({
//     items: [],
//     favorites: []
//   }),
//   actions: {
//     // 從後端載入購物車
//     async fetchCart() {
//       try {
//         const res = await axios.get(`${BASE}/api/cart/${USER_ID}`);
//         const data = res.data.data ?? res.data;
//         // 把後端資料轉成前端需要的格式
//         this.items = data.map(item => ({
//   id: item.cartId,
//   skuId: item.productSkuId,
//   name: item.productName,
//   price: item.unitPrice,
//   quantity: item.quantity,
//   image: item.imageUrl,
//   spec: '標準規格'
// }));
//       } catch (err) {
//         console.error('載入購物車失敗', err);
//       }
//     },

//     // 加入購物車（打 API）
//     async addToCart(product) {
//   try {
//     await axios.post(`${BASE}/api/cart/add`, {
//       userId: USER_ID,
//       productSkuId: product.skuId ?? product.id,
//       quantity: 1,
//       unitPrice: product.isFlashSale ? product.basePrice : null  // ← 新增
//     });
//     await this.fetchCart();
//   } catch (err) {
//     console.error('加入購物車失敗', err);
//   }
// },

//     // 移除購物車項目（打 API）
//     async removeFromCart(cartItemId) {
//       try {
//         await axios.delete(`${BASE}/api/cart/${USER_ID}/sku/${cartItemId}`);
//         await this.fetchCart();
//       } catch (err) {
//         console.error('移除失敗', err);
//       }
//     },

//     // 喜愛清單（維持本地）
//     toggleFavorite(product) {
//       const index = this.favorites.findIndex(item => item.id === product.id);
//       if (index > -1) {
//         this.favorites.splice(index, 1);
//       } else {
//         this.favorites.push({
//           id: product.id,
//           name: product.name,
//           price: product.basePrice || product.price,
//           image: product.imageUrl || product.image,
//           spec: product.spec || '標準規格'
//         });
//       }
//     },

//     increase(productId) {
//       const item = this.items.find(item => item.id === productId);
//       if (item) item.quantity++;
//     },

//     decrease(productId) {
//       const item = this.items.find(item => item.id === productId);
//       if (item && item.quantity > 1) item.quantity--;
//     }
//   }
// });

// import { defineStore } from 'pinia';
// import axios from 'axios';
// import { useUserStore } from './user.js';

// const BASE = 'http://localhost:8080';

// export const useCartStore = defineStore('cart', {
//   state: () => ({
//     items: [],
//     favorites: []
//   }),
//   actions: {
//     // 1. 從後端載入購物車
//     async fetchCart(userId) {
//       const currentUserId = userId || useUserStore().userId;
//       if (!currentUserId) return;

//       try {
//         // 【核心修正】：移除原來的 /user/，精準配合你的 Controller @GetMapping("/{userId}")
//         const res = await axios.get(`${BASE}/api/cart/${currentUserId}`);
//         const data = res.data.data ?? res.data;
        
//         // 映射後端傳回的 CartItemResponse 結構到前端
//         this.items = Array.isArray(data) ? data.map(item => ({
//           id: item.cartId || item.id,
//           skuId: item.productSkuId,
//           name: item.productName || item.name,
//           price: item.unitPrice,
//           quantity: item.quantity,
//           image: item.imageUrl || item.image,
//           spec: '標準規格'
//         })) : [];
//       } catch (err) {
//         console.error('載入購物車失敗', err);
//       }
//     },

//     // 2. 加入購物車 (打 API) 
//     async addToCart(product) {
//       const userStore = useUserStore();
      
//       if (!userStore.userId) {
//         alert("請先在上方選單選擇或登入會員！");
//         return;
//       }

//       const finalPrice = product.isFlashSale 
//         ? product.basePrice 
//         : (product.price || product.basePrice || 0);

//       try {
//         // 對應後端 @PostMapping("/add")
//         await axios.post(`${BASE}/api/cart/add`, {
//           userId: userStore.userId,
//           productSkuId: product.skuId ?? product.id,
//           quantity: 1,
//           unitPrice: finalPrice
//         });
        
//         await this.fetchCart(userStore.userId);
//         alert("成功加入購物車！");
//       } catch (err) {
//         console.error('加入購物車失敗', err);
//         alert("加入購物車失敗，請確認後端服務是否正常");
//       }
//     },

//     // 3. 移除購物車項目
//     async removeFromCart(productSkuId) {
//       const userStore = useUserStore();
//       if (!userStore.userId) return;

//       try {
//         // 對應後端 @DeleteMapping("/{userId}/sku/{productSkuId}")
//         await axios.delete(`${BASE}/api/cart/${userStore.userId}/sku/${productSkuId}`);
//         await this.fetchCart(userStore.userId);
//       } catch (err) {
//         console.error('移除失敗', err);
//       }
//     },

//     // 喜愛清單維持本地操作
//     toggleFavorite(product) {
//       const index = this.favorites.findIndex(item => item.id === product.id);
//       if (index > -1) {
//         this.favorites.splice(index, 1);
//       } else {
//         this.favorites.push({
//           id: product.id,
//           name: product.name,
//           price: product.basePrice || product.price,
//           image: product.imageUrl || product.image,
//           spec: product.spec || '標準規格'
//         });
//       }
//     },

//     increase(productId) {
//       const item = this.items.find(item => item.id === productId);
//       if (item) item.quantity++;
//     },

//     decrease(productId) {
//       const item = this.items.find(item => item.id === productId);
//       if (item && item.quantity > 1) item.quantity--;
//     }
//   }
// });

import { defineStore } from 'pinia';
import { useUserStore } from './user.js';
import { userAuthStore } from './auth.js'; // ✅ 新增
import axios from 'axios';

const BASE = import.meta.env.VITE_API_BASE_URL.replace('/api', '');

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
    favoritesByUser: {}
  }),

  getters: {
    favorites() {
      const authStore = userAuthStore();
      const userId = authStore.id || useUserStore().userId; // ✅
      return this.favoritesByUser[userId] || [];
    }
  },

  actions: {
    async fetchCart(userId) {
      const authStore = userAuthStore();
      const currentUserId = userId || authStore.id || useUserStore().userId; // ✅
      if (!currentUserId) return;

      try {
        const res = await axios.get(`${BASE}/api/cart/user/${currentUserId}`);
        const data = res.data.data ?? res.data;
        this.items = Array.isArray(data) ? data.map(item => ({
          id: item.cartId || item.id,
          skuId: item.productSkuId,
          name: item.productName || item.name,
          price: item.unitPrice,
          quantity: item.quantity,
          image: item.imageUrl || item.image,
          spec: '標準規格'
        })) : [];
      } catch (err) {
        console.error('載入購物車失敗', err);
      }
    },

    async addToCart(product) {
      const authStore = userAuthStore();
      const userId = authStore.id || useUserStore().userId; // ✅
      if (!userId) {
        alert("請先登入會員！");
        return;
      }

      const finalPrice = product.isFlashSale
        ? product.basePrice
        : (product.price || product.basePrice || 0);

      try {
        await axios.post(`${BASE}/api/cart/add`, {
          userId: userId, // ✅
          productSkuId: product.skuId ?? product.id,
          quantity: 1,
          unitPrice: finalPrice
        });
        await this.fetchCart(userId); // ✅
        alert("成功加入購物車！");
      } catch (err) {
        console.error('加入購物車失敗', err);
        alert("加入購物車失敗，請確認後端服務是否正常");
      }
    },

    async removeFromCart(productSkuId) {
      const authStore = userAuthStore();
      const userId = authStore.id || useUserStore().userId; // ✅
      if (!userId) return;

      try {
        await axios.delete(`${BASE}/api/cart/${userId}/sku/${productSkuId}`); // ✅
        await this.fetchCart(userId); // ✅
      } catch (err) {
        console.error('移除失敗', err);
      }
    },

    toggleFavorite(product) {
      const authStore = userAuthStore();
      const userId = authStore.id || useUserStore().userId; // ✅
      if (!userId) return;

      if (!this.favoritesByUser[userId]) {
        this.favoritesByUser[userId] = [];
      }

      const list = this.favoritesByUser[userId];
      const index = list.findIndex(item => item.id === product.id);

      if (index > -1) {
        list.splice(index, 1);
      } else {
        list.push({
          id: product.id,
          name: product.name,
          price: product.basePrice || product.price,
          image: product.imageUrl || product.image,
          spec: product.spec || '標準規格'
        });
      }
    },

    increase(productId) {
      const item = this.items.find(item => item.id === productId);
      if (item) item.quantity++;
    },

    decrease(productId) {
      const item = this.items.find(item => item.id === productId);
      if (item && item.quantity > 1) item.quantity--;
    }
  },

  persist: {
    storage: localStorage
  }
});