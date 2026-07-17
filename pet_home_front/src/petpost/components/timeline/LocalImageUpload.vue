<script setup>
import { ref } from 'vue'
import api from '@/api/http'

const emit = defineEmits(['uploaded'])

const uploading = ref(false)
const previewUrl = ref('')

async function handleFileChange(event) {
  const file = event.target.files?.[0]

  if (!file) return

  if (!file.type.startsWith('image/')) {
    alert('請選擇圖片檔案')
    return
  }

  previewUrl.value = URL.createObjectURL(file)

  const formData = new FormData()
  formData.append('file', file)

  uploading.value = true

  try {
    const response = await api.post('/pet-posts/images/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })

    /**
     * 後端建議回傳：
     * {
     *   "imageUrl": "/uploads/xxx.jpg"
     * }
     */
    const imageUrl = response.data.imageUrl || response.data

    emit('uploaded', imageUrl)
  } catch (error) {
    console.error('本地圖片上傳失敗', error)
    alert('本地圖片上傳失敗，請確認後端 API 是否存在')
  } finally {
    uploading.value = false
  }
}
</script>

<template>
  <div class="image-source-box">
    <label class="image-source-title">
      <i class="bi bi-image"></i>
      本地端圖片
    </label>

    <input
      type="file"
      accept="image/*"
      class="form-control"
      :disabled="uploading"
      @change="handleFileChange"
    />

    <div v-if="previewUrl" class="preview-wrap">
      <img :src="previewUrl" class="preview-img" />
    </div>

    <small class="hint">
      從電腦選擇圖片，上傳到後端 uploads 資料夾。
    </small>
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

.preview-wrap {
  margin-top: 12px;
  text-align: center;
}

.preview-img {
  width: 96px;
  height: 96px;
  object-fit: cover;
  border-radius: 18px;
}

.hint {
  display: block;
  margin-top: 8px;
  color: #7b6a5e;
}
</style>