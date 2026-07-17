<script setup>
const model = defineModel({
  type: String,
  default: '',
})
</script>

<template>
  <div class="image-source-box">
    <label class="image-source-title">
      <i class="bi bi-cloud"></i>
      雲端圖片網址
    </label>

    <input
      v-model="model"
      type="text"
      class="form-control"
      placeholder="請貼上 Cloudinary 圖片網址"
    />

    <div v-if="model" class="preview-wrap">
      <img :src="model" class="preview-img" />
    </div>

    <small class="hint">
      可貼上已存在的 Cloudinary 圖片網址。
    </small>
  </div>
</template>

<style scoped>
.image-source-box {
  margin-top: 14px;
  padding: 14px;
  border: 1px solid #eadfce;
  border-radius: 16px;
  background: #fffdf8;
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