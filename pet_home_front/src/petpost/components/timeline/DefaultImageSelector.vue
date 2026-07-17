<script setup>
import { DEFAULT_TIMELINE_IMAGES } from '@/petpost/constants/timelineConstants'

const timelineImages = DEFAULT_TIMELINE_IMAGES

const emit = defineEmits(['select'])
</script>

<template>
  <div class="image-source-box">
    <label class="image-source-title">
      <i class="bi bi-images"></i>
      預設雲端圖片
    </label>

    <div class="default-grid">
      <button
        v-for="img in timelineImages"
        :key="img.url"
        type="button"
        class="default-btn"
        @click="emit('select', img.url)"
      >
        <img :src="img.url" :alt="img.name" />
      </button>
    </div>
  </div>
</template>

<style scoped>
.image-source-box {
  margin-top: 14px;
  padding: 14px;
  border: 1px solid #eadfce;
  border-radius: 16px;
  background: #fffaf3;
}

.image-source-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #5f4b3b;
  font-weight: 700;
  margin-bottom: 10px;
}

.default-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.default-btn {
  border: 2px solid transparent;
  background: white;
  padding: 4px;
  border-radius: 14px;
  cursor: pointer;
}

.default-btn:hover {
  border-color: #c89b6d;
}

.default-btn img {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: 12px;
}
</style>
