<script setup>
import { ref } from 'vue'

const emit = defineEmits(['select'])
const previewUrl = ref('')

function handleFileChange(event) {
  const file = event.target.files?.[0]
  if (!file) return

  previewUrl.value = URL.createObjectURL(file)
  emit('select', file)
}
</script>

<template>
  <div class="image-upload-box">
    <label class="upload-label">
      <i class="bi bi-image"></i>
      <span>上傳本地圖片</span>
      <input type="file" accept="image/*" @change="handleFileChange" />
    </label>

    <img v-if="previewUrl" class="image-preview" :src="previewUrl" alt="本地圖片預覽" />
  </div>
</template>

<style scoped>
.image-upload-box {
  border: 1px dashed #d1d5db;
  border-radius: 18px;
  padding: 16px;
  background: #fff;
}

.upload-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: #4b5563;
}

.upload-label input {
  display: none;
}

.image-preview {
  margin-top: 12px;
  width: 100%;
  max-height: 220px;
  object-fit: cover;
  border-radius: 14px;
}
</style>
