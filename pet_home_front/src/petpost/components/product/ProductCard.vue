<script setup>
import defaultPetImage from "@/petpost/assets/images/default-pet.png";

const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
});

function getProductImage() {
  return props.product.imageUrl || defaultPetImage;
}

function useDefaultImage(event) {
  event.target.src = defaultPetImage;
}
</script>

<template>
  <article class="product-card h-100">
    <img
      :src="getProductImage()"
      class="product-image"
      alt="商品圖片"
      @error="useDefaultImage"
    />

    <div class="product-body">
      <span class="product-category">{{ product.category }}</span>
      <h5>{{ product.productName }}</h5>
      <p>{{ product.description || "毛孩日常推薦好物" }}</p>
      <div class="product-footer">
        <strong>NT$ {{ product.price }}</strong>
        <button type="button">查看商品</button>
      </div>
    </div>
  </article>
</template>

<style scoped>
.product-card {
  border-radius: 28px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(166, 128, 93, 0.14);
  box-shadow: 0 16px 40px rgba(93, 63, 38, 0.08);
  transition: .25s ease;
}
.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 18px 46px rgba(93, 63, 38, 0.13);
}
.product-image {
  width: 100%;
  height: 230px;
  object-fit: cover;
  display: block;
  background: #f2eadf;
}
.product-body {
  padding: 20px;
}
.product-category {
  display: inline-flex;
  padding: 5px 12px;
  border-radius: 999px;
  background: #f3eadf;
  color: #7c5a3d;
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 10px;
}
h5 {
  font-weight: 800;
  color: #2c2118;
  margin-bottom: 8px;
}
p {
  min-height: 48px;
  color: #6d6258;
  margin-bottom: 16px;
}
.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
.product-footer strong {
  color: #3f6f90;
  font-size: 20px;
}
.product-footer button {
  border: none;
  border-radius: 999px;
  padding: 9px 16px;
  background: #3f6f90;
  color: #fff;
  font-weight: 700;
}
</style>
