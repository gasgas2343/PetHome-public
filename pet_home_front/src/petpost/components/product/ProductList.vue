<script setup>
import { useProductStore } from "@/petpost/stores/productStore";
import ProductCard from "@/petpost/components/product/ProductCard.vue";

const productStore = useProductStore();
</script>

<template>
  <section class="product-section pc-page">
    <div class="section-heading">
      <span>Shopping</span>
      <h2>商品推薦區</h2>
      <p>精選飼料、用品、玩具與照護好物。</p>
    </div>

    <div v-if="productStore.filteredProducts.length === 0" class="empty-box">
      找不到符合條件的商品
    </div>

    <div v-else class="row g-4">
      <!-- 左側商品區固定一列 3 張卡片 -->
      <div
        class="col-12 col-sm-6 col-lg-4"
        v-for="product in productStore.filteredProducts"
        :key="product.productId"
      >
        <ProductCard :product="product" />
      </div>
    </div>
  </section>
</template>

<style scoped>
.product-section {
  padding: 38px 0 70px;
}
.section-heading {
  margin-bottom: 24px;
}
.section-heading span {
  color: #3f6f90;
  font-weight: 800;
  letter-spacing: .12em;
  text-transform: uppercase;
}
.section-heading h2 {
  font-weight: 900;
  color: #2c2118;
  margin: 6px 0;
}
.section-heading p {
  color: #6d6258;
  margin: 0;
}
.empty-box {
  border-radius: 20px;
  padding: 24px;
  background: rgba(255,255,255,.72);
  color: #7c5a3d;
  border: 1px solid rgba(166, 128, 93, .16);
}
</style>
