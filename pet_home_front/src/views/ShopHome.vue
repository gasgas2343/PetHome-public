<!-- <template>
  <div class="home-page">
    <section class="hero-section">
      <Swiper 
        :loop="true" 
        :autoplay="{ delay: 3000 }" 
        :modules="[Navigation, Autoplay]" 
        class="hero-swiper"
      >
        <SwiperSlide v-for="i in 5" :key="i">
          <img :src="`/images/home${i}.jpg`" class="hero-img" />
        </SwiperSlide>
      </Swiper>
    </section>

    <ProductCarousel 
      v-if="flashSaleProducts && flashSaleProducts.length > 0"
      title="🔥 限時特價" 
      :products="flashSaleProducts" 
      class="sale-section" 
    />

    <ProductCarousel title="熱銷商品" :products="allProducts" />
    <ProductCarousel title="貓咪專區" :products="allProducts.filter(p => p.categoryId === 1)" />
    <ProductCarousel title="狗狗專區" :products="allProducts.filter(p => p.categoryId === 2)" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation, Autoplay } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/navigation'
import ProductCarousel from '../components/ProductCarousel.vue'

const allProducts = ref([])
const flashSaleProducts = ref([])

onMounted(async () => {
  try {
    // 1. 同步獲取所有基礎商品與閃購名單
    const [productRes, flashRes] = await Promise.all([
      axios.get(`${import.meta.env.VITE_API_BASE_URL}/products`),
      axios.get(`${import.meta.env.VITE_API_BASE_URL}/flash-sales/active`)
    ]);

    const rawProducts = productRes.data?.data ?? productRes.data ?? [];
    const rawFlashSales = flashRes.data?.data ?? flashRes.data ?? [];

    // 儲存所有上架中的一般商品
    allProducts.value = rawProducts.filter(p => p.status === 1);

    console.log("1. 收到活動原始資料清單：", rawFlashSales);

    // 2. 💡 修正非同步核心：使用 Promise.all 確保所有商品規格「同時且完全」載入完畢
    const tempFlashProducts = await Promise.all(
      rawFlashSales.map(async (sale) => {
        const targetProductId = sale.productId || 13213;
        const matchedProduct = allProducts.value.find(p => p.id === targetProductId);

        // 預設特價規格 ID
        let dbSkuId = sale.productSkuId || sale.skuId || 10;

        // 嘗試去後端撈取這個商品的所有規格，做好備用防呆
        let fetchedSkus = [];
        try {
          const skuRes = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/products/${targetProductId}/skus`);
          fetchedSkus = skuRes.data?.data ?? skuRes.data ?? [];
          if (fetchedSkus.length > 0 && !sale.productSkuId) {
            dbSkuId = fetchedSkus[0].id; // 如果活動沒設，抓商品的第一個規格 ID
          }
        } catch (skuErr) {
          console.error(`撈取商品 ID ${targetProductId} 規格失敗，啟用防呆。`, skuErr);
        }

        // 回傳組裝好的完全體物件
        return {
          id: targetProductId,                       // 真正的商品 ID (13213)，確保輪播圖不破圖
          name: sale.productName || matchedProduct?.name || '限時特惠商品',
          imageUrl: sale.imageUrl || matchedProduct?.imageUrl || '/images/default-product.jpg',
          basePrice: sale.salePrice,                 // 特價 100
          originalPrice: sale.originalPrice || matchedProduct?.price || 500,
          description: matchedProduct?.description || '限時搶購極速優惠商品',
          isFlashSale: true,
          flashSaleId: sale.id,                      // 閃購活動主鍵 ID
          
          // 💡 這裏是關鍵：直接灌入 skus 規格包，讓 ProductCarousel 的 addToCart 點擊時可以直接抓到
          skus: fetchedSkus.length > 0 ? fetchedSkus : [{
            id: Number(dbSkuId),
            price: sale.salePrice,
            stock: 99
          }],
          // 同時多塞這兩個欄位當雙重保險，防止其他元件只認這兩個名稱
          productSkuId: Number(dbSkuId),
          skuId: Number(dbSkuId)
        };
      })
    );

    // 3. 💡 精準賦值：這時候 tempFlashProducts 裡面絕對有資料了，不再是空陣列！
    flashSaleProducts.value = tempFlashProducts;
    console.log("🔥 2. 終極檢查首頁特價區【完全體】資料陣列：", flashSaleProducts.value);

  } catch (err) {
    console.error("首頁初始化載入資料失敗:", err);
  }
});
</script>

<style scoped>
.home-page { background-color: #fcf9f2; min-height: 100vh; padding-bottom: 50px; }

/* 英雄區樣式 */
.hero-section { 
  max-width: 1200px; 
  margin: 20px auto; 
  overflow: hidden; 
  border-radius: 20px; 
  box-shadow: 0 4px 15px rgba(0,0,0,0.1); 
}
.hero-img {
  width: 100%;
  aspect-ratio: 10 / 4; 
  object-fit: cover;    
  display: block;
}

.sale-section { background: #fffaf0; }
</style> -->
<template>
  <div class="home-page">
    <section class="hero-section">
      <Swiper :loop="true" :autoplay="{ delay: 3000 }" :modules="[Navigation, Autoplay]" class="hero-swiper">
        <SwiperSlide v-for="i in 5" :key="i">
          <img :src="`/images/images/home${i}.jpg`" class="hero-img" />
        </SwiperSlide>
      </Swiper>
    </section>

    <ProductCarousel 
      v-if="flashSaleProducts && flashSaleProducts.length > 0"
      title="🔥 限時特價" 
      :products="flashSaleProducts" 
      class="sale-section" 
    />

    <ProductCarousel 
      v-if="hotProducts.length > 0"
      title="熱銷商品" 
      :products="hotProducts.filter(p => !flashSaleProducts.some(f => f.id === p.id))" 
    />

    <ProductCarousel 
      v-if="catNewProducts.length > 0"
      title="🐱 貓咪新品" 
      :products="catNewProducts.filter(p => !flashSaleProducts.some(f => f.id === p.id))" 
    />

    <ProductCarousel 
      v-if="dogNewProducts.length > 0"
      title="🐶 狗狗新品" 
      :products="dogNewProducts.filter(p => !flashSaleProducts.some(f => f.id === p.id))" 
    />

    <ProductCarousel 
      title="貓咪專區" 
      :products="catProducts.filter(p => !flashSaleProducts.some(f => f.id === p.id))" 
    />

    <ProductCarousel 
      title="狗狗專區" 
      :products="dogProducts.filter(p => !flashSaleProducts.some(f => f.id === p.id))" 
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation, Autoplay } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/navigation'
import ProductCarousel from '../components/ProductCarousel.vue'
import http from '@/api/http.js'

const allProducts = ref([])
const flashSaleProducts = ref([])
const dogProducts = ref([])
const catProducts = ref([])
const dogNewProducts = ref([])
const catNewProducts = ref([])
const hotProducts = ref([])

// ⭐ 手動指定熱銷商品 ID（要換熱銷商品就改這裡）
const HOT_PRODUCT_IDS = [13213, 1, 5, 9]

// ⭐ 分類對照表（直接寫死，不用打 API 查）
// 狗館 id=1 底下子分類：8~14
// 貓館 id=2 底下子分類：15~20
const DOG_CATEGORY_IDS = [8, 9, 10, 11, 12, 13, 14]
const CAT_CATEGORY_IDS = [15, 16, 17, 18, 19, 20]
const DOG_NEW_CATEGORY_ID = 8   // 狗|新品上市
const CAT_NEW_CATEGORY_ID = 15  // 喵|新品上市

onMounted(async () => {
  try {
    // 1. 同步獲取所有基礎商品與閃購名單（拿掉 categories 那支，避免 404 連帶炸掉整個 Promise.all）
    const [productRes, flashRes] = await Promise.all([
      http.get('/products'),
      http.get('/flash-sales/active')
    ]);

    const rawProducts = productRes.data?.data ?? productRes.data ?? [];
    const rawFlashSales = flashRes.data?.data ?? flashRes.data ?? [];

    // 儲存所有上架中的一般商品
    allProducts.value = rawProducts.filter(p => p.status === 1);

    // 2. 貓咪 / 狗狗專區：用寫死的對照表篩出真正所屬大類的商品
    dogProducts.value = allProducts.value.filter(p => DOG_CATEGORY_IDS.includes(p.categoryId));
    catProducts.value = allProducts.value.filter(p => CAT_CATEGORY_IDS.includes(p.categoryId));

    // 3. 新品：只抓「狗|新品上市」「喵|新品上市」這兩個子分類，依上架時間排序取最新
    dogNewProducts.value = allProducts.value
      .filter(p => p.categoryId === DOG_NEW_CATEGORY_ID)
      .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    catNewProducts.value = allProducts.value
      .filter(p => p.categoryId === CAT_NEW_CATEGORY_ID)
      .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    // 4. 熱銷商品：依手動指定的 ID 陣列，從 allProducts 撈出對應商品（保持指定順序）
    hotProducts.value = HOT_PRODUCT_IDS
      .map(id => allProducts.value.find(p => p.id === id))
      .filter(Boolean);

    console.log("1. 收到活動原始資料清單：", rawFlashSales);

    // 5. 使用 Promise.all 確保所有商品規格「同時且完全」載入完畢
    const tempFlashProducts = await Promise.all(
      rawFlashSales.map(async (sale) => {
        const targetProductId = sale.productId || 13213;
        const matchedProduct = allProducts.value.find(p => p.id === targetProductId);

        let dbSkuId = sale.productSkuId || sale.skuId || 10;

        let fetchedSkus = [];
        try {
          const skuRes = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/products/${targetProductId}/skus`);
          fetchedSkus = skuRes.data?.data ?? skuRes.data ?? [];
          if (fetchedSkus.length > 0 && !sale.productSkuId) {
            dbSkuId = fetchedSkus[0].id;
          }
        } catch (skuErr) {
          console.error(`撈取商品 ID ${targetProductId} 規格失敗，啟用防呆。`, skuErr);
        }

        return {
          id: targetProductId,
          name: sale.productName || matchedProduct?.name || '限時特惠商品',
          imageUrl: sale.imageUrl || matchedProduct?.imageUrl || '/images/default-product.jpg',
          basePrice: sale.salePrice,
          originalPrice: sale.originalPrice || matchedProduct?.price || 500,
          description: matchedProduct?.description || '限時搶購極速優惠商品',
          isFlashSale: true,
          flashSaleId: sale.id,
          skus: fetchedSkus.length > 0 ? fetchedSkus : [{
            id: Number(dbSkuId),
            price: sale.salePrice,
            stock: 99
          }],
          productSkuId: Number(dbSkuId),
          skuId: Number(dbSkuId)
        };
      })
    );

    flashSaleProducts.value = tempFlashProducts;
    console.log("🔥 2. 終極檢查首頁特價區【完全體】資料陣列：", flashSaleProducts.value);

  } catch (err) {
    console.error("首頁初始化載入資料失敗:", err);
  }
});
</script>

<style scoped>
.home-page { background-color: #fcf9f2; min-height: 100vh; padding-bottom: 50px; }

/* 英雄區樣式 */
.hero-section { 
  max-width: 1200px; 
  margin: 20px auto; 
  overflow: hidden; 
  border-radius: 20px; 
  box-shadow: 0 4px 15px rgba(0,0,0,0.1); 
}
.hero-img {
  width: 100%;
  aspect-ratio: 10 / 4; 
  object-fit: cover;    
  display: block;
}

.sale-section { background: #fffaf0; }
</style>