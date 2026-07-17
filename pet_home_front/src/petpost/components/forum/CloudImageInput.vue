<script setup>
import { ref, watch } from 'vue'

const emit = defineEmits(['change'])
const imageUrl = ref('')

watch(imageUrl, (value) => {
  emit('change', value)
})
</script>

<template>
  <div class="cloud-image-box">
    <label>雲端圖片網址</label>
    <input
      v-model="imageUrl"
      type="url"
      placeholder="請貼上 Cloudinary 或圖片網址"
    />

    <img v-if="imageUrl" class="cloud-preview" :src="imageUrl" alt="雲端圖片預覽" />
  </div>
</template>

<style scoped>
.cloud-image-box {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 18px;
  padding: 16px;
}

.cloud-image-box label {
  display: block;
  margin-bottom: 8px;
  color: #374151;
  font-weight: 700;
}

.cloud-image-box input {
  width: 100%;
  border: 1px solid #d1d5db;
  border-radius: 12px;
  padding: 10px 12px;
}

.cloud-preview {
  margin-top: 12px;
  width: 100%;
  max-height: 220px;
  object-fit: cover;
  border-radius: 14px;
}
</style>
