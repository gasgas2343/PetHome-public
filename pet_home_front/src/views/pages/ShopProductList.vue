<!-- <template>
  <div class="shop-container">
    <h2 class="page-title">{{ categoryName }}</h2>

    <div v-if="loading" class="loading-state">載入中...</div>

    <div v-else-if="products.length === 0" class="empty-state">
      此分類目前沒有商品
    </div>

    <div v-else class="product-grid">
      <div v-for="product in products" :key="product.id" class="product-card">
        <img
          :src="product.imageUrl || '/images/default.jpg'"
          class="card-img"
          :alt="product.name"
          @error="$event.target.src='/images/default.jpg'"
        >
        <div class="card-body">
          <h5 class="card-title">{{ product.name }}</h5>
          <p class="card-text">{{ product.description || '暫無詳細描述' }}</p>
          
          <div class="sku-badge-container mb-3">
            <div v-if="product.skus && product.skus.length > 0" class="d-flex flex-wrap gap-1">
              <span v-for="sku in product.skus" :key="sku.id" 
                    class="badge sku-pill"
                    :class="sku.stock > 0 ? 'available' : 'out-of-stock'">
                <span v-if="sku.flavor">🎨 {{ sku.flavor }}</span>
                <span v-if="sku.flavor && sku.size" class="mx-1 text-muted">|</span>
                <span v-if="sku.size">📏 {{ sku.size }}</span>
                <span v-if="sku.stock <= 0" class="stock-tip">(缺貨)</span>
              </span>
            </div>
            <div v-else class="text-muted text-start" style="font-size: 11px;">
              標準單一規格
            </div>
          </div>

          <div class="price-section">
            <div class="price-wrapper">
              <template v-if="product.isFlashSale">
                <span class="original-price">NT$ {{ product.originalPrice }}</span>
                <span class="price">NT$ {{ product.basePrice }}</span>
              </template>
              <span v-else class="price">NT$ {{ product.basePrice }}</span>
            </div>
            <div class="btn-group">
              <button
                :class="['btn-fav', { 'is-active': isFavorite(product.id) }]"
                @click="cartStore.toggleFavorite(product)"
              >❤️</button>
              <button class="btn-add" @click="addToCart(product)">放入購物車</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axiosapi from '@/plugins/axios.js';
import Swal from 'sweetalert2';
import { useCartStore } from '@/stores/cartStore';

const route = useRoute();
const cartStore = useCartStore();
const products = ref([]);
const categoryName = ref('');
const loading = ref(false);

const isFavorite = (productId) => {
  return cartStore.favorites.some(item => item.id === productId);
};

const FLASH_SALE_CATEGORY_ID = 32;  // 限時特賣的分類 ID

// 撈取分類商品與串接規格 (SKU)
const fetchProductsByCategory = async (categoryId) => {
  if (!categoryId) return;
  loading.value = true;
  try {
    // 情況 A：如果是限時特賣分類，從閃購 API 撈取並封裝
    if (Number(categoryId) === FLASH_SALE_CATEGORY_ID) {
      const flashRes = await axiosapi.get('/flash-sales/active');
      const flashSales = flashRes.data.data ?? [];
      
      const mappedProducts = flashSales.map(sale => ({
  id: sale.productId,
  name: sale.productName,
  imageUrl: sale.imageUrl,
  basePrice: sale.salePrice,
  originalPrice: sale.originalPrice,
  description: '',
  status: 1,
  isFlashSale: true,
  flashSaleId: sale.flashSaleId,
  flashSkuId: sale.skuId,   // ✅ 帶入閃購綁定的真實 SKU ID
  skus: []
}));
     

      // 閃購商品非同步串接個別的 SKU 清單
      for (let product of mappedProducts) {
        try {
          const skuRes = await axiosapi.get(`/products/${product.id}/skus`);
          product.skus = skuRes.data.data ?? skuRes.data ?? [];
        } catch (err) {
          product.skus = [];
        }
      }
      
      // 💡 修正：只保留處理完畢的完全體資料，刪除原本會將資料蓋成空規格的錯誤程式碼
      products.value = mappedProducts;

   } else {
  // 情況 B：一般商品分類
  // 💡 改動 1：改用 Promise.all 同時撈取「一般商品」與「進行中的閃購活動」
  const [productRes, flashRes] = await Promise.all([
    axiosapi.get(`/products/category/${categoryId}`),
    axiosapi.get('/flash-sales/active')
  ]);

  const all = productRes.data.data ?? productRes.data ?? [];
  const activeProducts = all.filter(p => p.status === 1);
  const flashSales = flashRes.data.data ?? flashRes.data ?? [];

  // 💡 改動 2：精準過濾！如果這件商品正在特價，就直接從 activeProducts 裡面踢掉
  const filteredProducts = activeProducts.filter(p => {
    return !flashSales.some(sale => sale.productId === p.id);
  });

  // 💡 改動 3：後續的 SKU 包裝對象改為「過濾乾淨後」的 filteredProducts
  for (let product of filteredProducts) {
    try {
      const skuRes = await axiosapi.get(`/products/${product.id}/skus`);
      product.skus = skuRes.data.data ?? skuRes.data ?? [];
    } catch (skuErr) {
      console.error(`商品 ID ${product.id} 規格載入失敗`, skuErr);
      product.skus = [];
    }
  }

  // 💡 改動 4：最後把過濾完、包裝好規格的資料送給畫面渲染
  products.value = filteredProducts;
}
    console.log(`成功獲取分類 ${categoryId} 商品列表(含完整規格):`, products.value);
  } catch (error) {
    console.error('載入商品失敗:', error);
    Swal.fire({ text: '無法載入商品列表', icon: 'error' });
  } finally {
    loading.value = false;
  }
};

// 撈取麵包屑/分類名稱
const fetchCategoryName = async (categoryId) => {
  try {
    const response = await axiosapi.get('/products/categories');
    const allCategories = response.data.data ?? response.data ?? [];

    const findCategory = (list, id) => {
      for (const cat of list) {
        if (cat.id === Number(id)) return cat;
        if (cat.children?.length) {
          const found = findCategory(cat.children, id);
          if (found) return found;
        }
      }
      return null;
    };

    const matched = findCategory(allCategories, categoryId);
    categoryName.value = matched?.name ?? '';
  } catch {
    categoryName.value = '';
  }
};

// 加入購物車（含特價與一般規格防爆比對）
const addToCart = async (product) => {
  try {
    let finalSkuId = null;

    // ✅ 閃購商品優先使用閃購綁定的 SKU ID
    if (product.isFlashSale && product.flashSkuId) {
      finalSkuId = product.flashSkuId;
    } else if (product.skus && product.skus.length > 0) {
      finalSkuId = product.skus[0].id;
    } else {
      finalSkuId = product.id;
    }

    if (!finalSkuId) {
      Swal.fire({ text: '此商品規格不健全，目前無法加入購物車', icon: 'warning' });
      return;
    }

    const payload = {
  userId: 1,
  productSkuId: Number(finalSkuId),
  quantity: 1,
  unitPrice: "10.00"   // ✅ 用字串格式的 BigDecimal
};

    await axiosapi.post('/cart/add', payload);
    Swal.fire({ text: '🎉 商品已成功加入購物車！', icon: 'success', timer: 1500, showConfirmButton: false });
  } catch (error) {
    console.error('加入購物車失敗:', error);
    Swal.fire({ text: '加入購物車失敗', icon: 'error' });
  }
};

watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      fetchProductsByCategory(newId);
      fetchCategoryName(newId);
    }
  }
);

onMounted(() => {
  const categoryId = route.params.id;
  fetchProductsByCategory(categoryId);
  fetchCategoryName(categoryId);
});
</script>

<style scoped>
.shop-container { background-color: #fcf9f2; min-height: 100vh; padding: 40px 20px; }
.page-title { text-align: center; color: #5d4037; margin-bottom: 40px; font-weight: 700; }
.loading-state, .empty-state { text-align: center; color: #8d6e63; margin-top: 80px; font-size: 1.1rem; }
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 30px; max-width: 1200px; margin: 0 auto; }
.product-card { background: white; border-radius: 20px; padding: 20px; box-shadow: 0 10px 20px rgba(0,0,0,0.05); transition: transform 0.3s ease; display: flex; flex-direction: column; justify-content: space-between; }
.product-card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0,0,0,0.1); }
.card-img { width: 100%; aspect-ratio: 1/1; object-fit: cover; border-radius: 15px; margin-bottom: 15px; }
.card-body { display: flex; flex-direction: column; flex-grow: 1; }
.card-title { color: #3e2723; font-weight: bold; margin-bottom: 8px; }
.card-text { color: #8d6e63; font-size: 0.9rem; margin-bottom: 12px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; height: 2.7rem; }

.sku-badge-container { min-height: 32px; }
.sku-pill { font-size: 11px; padding: 4px 8px; border-radius: 20px; background-color: #f5f5f5; border: 1px solid #e0e0e0; color: #4e342e; }
.sku-pill.available { border-color: #d7ccc8; background-color: #fff; }
.sku-pill.out-of-stock { background-color: #e0e0e0; color: #9e9e9e; border-color: #bdbdbd; text-decoration: line-through; }
.stock-tip { color: #c62828; font-weight: bold; margin-left: 2px; text-decoration: none; display: inline-block; }

.price-section { display: flex; justify-content: space-between; align-items: center; margin-top: auto; }
.price { font-weight: bold; color: #d84315; font-size: 1.15rem; }
.btn-group { display: flex; gap: 8px; align-items: center; }
.btn-fav { background: #eee; border: none; border-radius: 8px; padding: 5px 8px; cursor: pointer; transition: 0.3s; }
.btn-fav.is-active { background: #ffcdd2; color: #d32f2f; }
.btn-add { background-color: #d7ccc8; border: none; border-radius: 10px; padding: 8px 16px; color: #3e2723; cursor: pointer; transition: background 0.3s; font-weight: bold; font-size: 0.9rem; }
.btn-add:hover { background-color: #bcaaa4; }
.price-wrapper { display: flex; flex-direction: column; }
.original-price { text-decoration: line-through; color: #aaa; font-size: 0.85em; }
</style> -->

<template>
  <!-- 完全不動 -->
  <div class="shop-container">
    <h2 class="page-title">{{ categoryName }}</h2>

    <div v-if="loading" class="loading-state">載入中...</div>

    <div v-else-if="products.length === 0" class="empty-state">
      此分類目前沒有商品
    </div>

    <div v-else class="product-grid">
      <div v-for="product in products" :key="product.id" class="product-card">
        <img
          :src="product.imageUrl || '/images/default.jpg'"
          class="card-img"
          :alt="product.name"
          @error="$event.target.src='/images/default.jpg'"
        >
        <div class="card-body">
          <h5 class="card-title">{{ product.name }}</h5>
          <p class="card-text">{{ product.description || '暫無詳細描述' }}</p>
          
          <div class="sku-badge-container mb-3">
            <div v-if="product.skus && product.skus.length > 0" class="d-flex flex-wrap gap-1">
              <span v-for="sku in product.skus" :key="sku.id" 
                    class="badge sku-pill"
                    :class="sku.stock > 0 ? 'available' : 'out-of-stock'">
                <span v-if="sku.flavor">🎨 {{ sku.flavor }}</span>
                <span v-if="sku.flavor && sku.size" class="mx-1 text-muted">|</span>
                <span v-if="sku.size">📏 {{ sku.size }}</span>
                <span v-if="sku.stock <= 0" class="stock-tip">(缺貨)</span>
              </span>
            </div>
            <div v-else class="text-muted text-start" style="font-size: 11px;">
              標準單一規格
            </div>
          </div>

          <div class="price-section">
            <div class="price-wrapper">
              <template v-if="product.isFlashSale">
                <span class="original-price">NT$ {{ product.originalPrice }}</span>
                <span class="price">NT$ {{ product.basePrice }}</span>
              </template>
              <span v-else class="price">NT$ {{ product.basePrice }}</span>
            </div>
            <div class="btn-group">
              <button
                :class="['btn-fav', { 'is-active': isFavorite(product.id) }]"
                @click="cartStore.toggleFavorite(product)"
              >❤️</button>
              <button class="btn-add" @click="addToCart(product)">放入購物車</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axiosapi from '@/plugins/axios.js';
import Swal from 'sweetalert2';
import { useCartStore } from '@/stores/cartStore';

const route = useRoute();
const cartStore = useCartStore();
const products = ref([]);
const categoryName = ref('');
const loading = ref(false);

const isFavorite = (productId) => {
  return cartStore.favorites.some(item => item.id === productId);
};

const FLASH_SALE_CATEGORY_ID = 32;

// ✅ 抽出共用的「撈商品 + 過濾閃購 + 串 SKU」邏輯
const fetchAndFilterProducts = async (apiUrl) => {
  const [productRes, flashRes] = await Promise.all([
    axiosapi.get(apiUrl),
    axiosapi.get('/flash-sales/active')
  ]);

  const all = productRes.data.data ?? productRes.data ?? [];
  const activeProducts = all.filter(p => p.status === 1);
  const flashSales = flashRes.data.data ?? flashRes.data ?? [];

  const filteredProducts = activeProducts.filter(p =>
    !flashSales.some(sale => sale.productId === p.id)
  );

  for (let product of filteredProducts) {
    try {
      const skuRes = await axiosapi.get(`/products/${product.id}/skus`);
      product.skus = skuRes.data.data ?? skuRes.data ?? [];
    } catch {
      product.skus = [];
    }
  }

  return filteredProducts;
};

const fetchProductsByCategory = async (categoryId) => {
  if (!categoryId) return;
  loading.value = true;
  try {
    // 情況 A：限時特賣
    if (Number(categoryId) === FLASH_SALE_CATEGORY_ID) {
      const flashRes = await axiosapi.get('/flash-sales/active');
      const flashSales = flashRes.data.data ?? [];

      const mappedProducts = flashSales.map(sale => ({
        id: sale.productId,
        name: sale.productName,
        imageUrl: sale.imageUrl,
        basePrice: sale.salePrice,
        originalPrice: sale.originalPrice,
        description: '',
        status: 1,
        isFlashSale: true,
        flashSaleId: sale.flashSaleId,
        flashSkuId: sale.skuId,
        skus: []
      }));

      for (let product of mappedProducts) {
        try {
          const skuRes = await axiosapi.get(`/products/${product.id}/skus`);
          product.skus = skuRes.data.data ?? skuRes.data ?? [];
        } catch {
          product.skus = [];
        }
      }

      products.value = mappedProducts;

    // ✅ 情況 C：品牌頁
    } else if (route.name === 'ProductBrand') {
      products.value = await fetchAndFilterProducts(`/products/brand/${categoryId}`);

    // 情況 B：一般分類
    } else {
      products.value = await fetchAndFilterProducts(`/products/category/${categoryId}`);
    }

    console.log(`成功獲取商品列表:`, products.value);
  } catch (error) {
    console.error('載入商品失敗:', error);
    Swal.fire({ text: '無法載入商品列表', icon: 'error' });
  } finally {
    loading.value = false;
  }
};

// ✅ 品牌頁改打 /brands/:id，一般分類走原本邏輯
const fetchCategoryName = async (categoryId) => {
  try {
    if (route.name === 'ProductBrand') {
      const res = await axiosapi.get(`/brands/${categoryId}`);
      categoryName.value = res.data.data?.name ?? '';
      return;
    }

    const response = await axiosapi.get('/products/categories');
    const allCategories = response.data.data ?? response.data ?? [];

    const findCategory = (list, id) => {
      for (const cat of list) {
        if (cat.id === Number(id)) return cat;
        if (cat.children?.length) {
          const found = findCategory(cat.children, id);
          if (found) return found;
        }
      }
      return null;
    };

    const matched = findCategory(allCategories, categoryId);
    categoryName.value = matched?.name ?? '';
  } catch {
    categoryName.value = '';
  }
};

const addToCart = async (product) => {
  try {
    let finalSkuId = null;

    if (product.isFlashSale && product.flashSkuId) {
      finalSkuId = product.flashSkuId;
    } else if (product.skus && product.skus.length > 0) {
      finalSkuId = product.skus[0].id;
    } else {
      finalSkuId = product.id;
    }

    if (!finalSkuId) {
      Swal.fire({ text: '此商品規格不健全，目前無法加入購物車', icon: 'warning' });
      return;
    }

    const payload = {
      userId: 1,
      productSkuId: Number(finalSkuId),
      quantity: 1,
      unitPrice: "10.00"
    };

    await axiosapi.post('/cart/add', payload);
    Swal.fire({ text: '🎉 商品已成功加入購物車！', icon: 'success', timer: 1500, showConfirmButton: false });
  } catch (error) {
    console.error('加入購物車失敗:', error);
    Swal.fire({ text: '加入購物車失敗', icon: 'error' });
  }
};

// ✅ watch 改成 route.params.id + route.name 同時監聽，切換分類↔品牌頁都會觸發
watch(
  () => [route.params.id, route.name],
  ([newId]) => {
    if (newId) {
      fetchCategoryName(newId);
      fetchProductsByCategory(newId);
    }
  }
);

onMounted(() => {
  const categoryId = route.params.id;
  fetchCategoryName(categoryId);
  fetchProductsByCategory(categoryId);
});
</script>

<style scoped>
.shop-container { background-color: #fcf9f2; min-height: 100vh; padding: 40px 20px; }
.page-title { text-align: center; color: #5d4037; margin-bottom: 40px; font-weight: 700; }
.loading-state, .empty-state { text-align: center; color: #8d6e63; margin-top: 80px; font-size: 1.1rem; }
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 30px; max-width: 1200px; margin: 0 auto; }
.product-card { background: white; border-radius: 20px; padding: 20px; box-shadow: 0 10px 20px rgba(0,0,0,0.05); transition: transform 0.3s ease; display: flex; flex-direction: column; justify-content: space-between; }
.product-card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0,0,0,0.1); }
.card-img { width: 100%; aspect-ratio: 1/1; object-fit: cover; border-radius: 15px; margin-bottom: 15px; }
.card-body { display: flex; flex-direction: column; flex-grow: 1; }
.card-title { color: #3e2723; font-weight: bold; margin-bottom: 8px; }
.card-text { color: #8d6e63; font-size: 0.9rem; margin-bottom: 12px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; height: 2.7rem; }
.sku-badge-container { min-height: 32px; }
.sku-pill { font-size: 11px; padding: 4px 8px; border-radius: 20px; background-color: #f5f5f5; border: 1px solid #e0e0e0; color: #4e342e; }
.sku-pill.available { border-color: #d7ccc8; background-color: #fff; }
.sku-pill.out-of-stock { background-color: #e0e0e0; color: #9e9e9e; border-color: #bdbdbd; text-decoration: line-through; }
.stock-tip { color: #c62828; font-weight: bold; margin-left: 2px; text-decoration: none; display: inline-block; }
.price-section { display: flex; justify-content: space-between; align-items: center; margin-top: auto; }
.price { font-weight: bold; color: #d84315; font-size: 1.15rem; }
.btn-group { display: flex; gap: 8px; align-items: center; }
.btn-fav { background: #eee; border: none; border-radius: 8px; padding: 5px 8px; cursor: pointer; transition: 0.3s; }
.btn-fav.is-active { background: #ffcdd2; color: #d32f2f; }
.btn-add { background-color: #d7ccc8; border: none; border-radius: 10px; padding: 8px 16px; color: #3e2723; cursor: pointer; transition: background 0.3s; font-weight: bold; font-size: 0.9rem; }
.btn-add:hover { background-color: #bcaaa4; }
.price-wrapper { display: flex; flex-direction: column; }
.original-price { text-decoration: line-through; color: #aaa; font-size: 0.85em; }
</style>