<script setup>
import { computed, ref, watch } from 'vue'
import { FALLBACK_IMAGE, getPresetImages } from '@/petpost/data/cloudinaryImages'

const props = defineProps({
  // post | pet | timeline
  type: {
    type: String,
    default: 'post',
  },
  // 外部可指定按鈕文字，例如「選擇圖片」或「新增圖片」
  label: {
    type: String,
    default: '',
  },
  // 目前選到的圖片網址
  modelValue: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:modelValue', 'change'])

const isOpen = ref(false)
const selectedUrl = ref(props.modelValue || '')
const brokenImages = ref(new Set())

const images = computed(() => getPresetImages(props.type))

const buttonText = computed(() => {
  if (props.label) return props.label
  return props.type === 'pet' ? '選擇圖片' : '新增圖片'
})

const selectedImage = computed(() => {
  return images.value.find((item) => item.url === selectedUrl.value)
})

const previewUrl = computed(() => selectedUrl.value || '')

watch(
  () => props.modelValue,
  (value) => {
    selectedUrl.value = value || ''
  },
)

function choosePresetImage(image) {
  selectedUrl.value = image.url
  isOpen.value = false

  // 現在回傳的是「雲端圖片網址字串」，不是 File 物件。
  // 父元件不可再使用 URL.createObjectURL()。
  emit('update:modelValue', image.url)
  emit('change', image.url)
}

function clearImage() {
  selectedUrl.value = ''
  emit('update:modelValue', '')
  emit('change', '')
}

function handleImageError(event, url) {
  brokenImages.value.add(url)
  event.target.src = FALLBACK_IMAGE
  console.warn('Cloudinary 圖片載入失敗，已改用備用圖片：', url)
}
</script>

<template>
  <div class="cloud-image-select">
    <button
      type="button"
      class="image-select-toggle"
      @click="isOpen = !isOpen"
    >
      <i class="bi bi-image"></i>
      <span>{{ buttonText }}</span>
      <i class="bi" :class="isOpen ? 'bi-chevron-up' : 'bi-chevron-down'"></i>
    </button>

    <div v-if="selectedImage" class="selected-image-name">
      目前圖片：{{ selectedImage.name }}
    </div>

    <div v-if="previewUrl" class="selected-preview">
      <img
        :src="previewUrl"
        alt="目前選擇圖片"
        @error="handleImageError($event, previewUrl)"
      />
      <button type="button" class="clear-image-btn" @click="clearImage">
        清除圖片
      </button>
    </div>

    <div v-if="isOpen" class="image-dropdown-panel">
      <button
        v-for="image in images"
        :key="image.url"
        type="button"
        class="image-option"
        :class="{ active: image.url === selectedUrl }"
        @click="choosePresetImage(image)"
      >
        <img
          :src="brokenImages.has(image.url) ? FALLBACK_IMAGE : image.url"
          :alt="image.name"
          @error="handleImageError($event, image.url)"
        />
        <span>{{ image.name }}</span>
      </button>
    </div>
  </div>
</template>

<style scoped>
.cloud-image-select {
  position: relative;
  width: 100%;
}

.image-select-toggle {
  width: 100%;
  min-height: 46px;
  border: 1px solid rgba(160, 125, 92, 0.32);
  border-radius: 14px;
  background: #fffaf2;
  color: #5f4635;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 10px 14px;
}

.selected-image-name {
  margin-top: 8px;
  color: #7b6a5e;
  font-size: 13px;
}

.selected-preview {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.selected-preview img {
  width: 86px;
  height: 86px;
  border-radius: 18px;
  object-fit: cover;
  border: 1px solid rgba(160, 125, 92, 0.24);
}

.clear-image-btn {
  border: 1px solid #dc3545;
  color: #dc3545;
  background: #fff;
  border-radius: 999px;
  padding: 6px 12px;
  font-size: 13px;
}

.image-dropdown-panel {
  margin-top: 10px;
  padding: 12px;
  border-radius: 18px;
  border: 1px solid rgba(160, 125, 92, 0.24);
  background: #fff;
  box-shadow: 0 16px 34px rgba(66, 45, 27, 0.12);
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(126px, 1fr));
  gap: 12px;
  z-index: 20;
}

.image-option {
  border: 1px solid rgba(160, 125, 92, 0.18);
  background: #fffaf2;
  border-radius: 16px;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: center;
  color: #5f4635;
  font-weight: 700;
}

.image-option.active {
  outline: 3px solid rgba(62, 111, 143, 0.24);
  border-color: #3e6f8f;
}

.image-option img {
  width: 100%;
  height: 78px;
  border-radius: 12px;
  object-fit: cover;
}

.image-option span {
  font-size: 12px;
  text-align: center;
}
</style>
