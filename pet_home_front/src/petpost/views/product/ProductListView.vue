<script setup>
import SearchBar from "@/petpost/components/common/SearchBar.vue";
import ProductList from "@/petpost/components/product/ProductList.vue";
import AdvertisementBanner from "@/petpost/components/common/AdvertisementBanner.vue";
import { useProductStore } from "@/petpost/stores/productStore";

const productStore = useProductStore();
</script>

<template>
  <main class="container product-page">
    <SearchBar
      v-model="productStore.keyword"
      placeholder="搜尋商品名稱或分類..."
      @search="productStore.searchProducts"
    />

    <div class="row align-items-start product-layout">
      <!-- 左側商品：固定 3 張 -->
      <div class="col-12 col-lg-9">
        <ProductList />
      </div>

      <!-- 右側廣告：2 張圖自動輪播 -->
      <div class="col-12 col-lg-3">
        <AdvertisementBanner />
      </div>
    </div>
  </main>
</template>

<style scoped>
.product-page {
  padding-top: 28px;
}

.product-layout {
  margin-top: 10px;
}
</style>
