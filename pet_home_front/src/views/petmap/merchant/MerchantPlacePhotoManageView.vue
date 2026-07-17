<script setup>
import { ref } from 'vue'
import { onMounted } from 'vue'

import {
  getPlaceImagesByPlaceId,
  createPlaceImage,
  deletePlaceImage,
} from '@/api/petmap/placeImageApi'

import Swal from 'sweetalert2'

import MerchantNavbar from '@/components/petmap/MerchantNavbar.vue'

const images = ref([])

const loadImages = async () => {
  try {
    const response = await getPlaceImagesByPlaceId(46)

    images.value = response.data.data || []
  } catch (error) {
    console.error(error)
    images.value = []
  }
}

const handleCreateImage = async () => {
  const result = await Swal.fire({
    title: '新增店家照片',

    input: 'text',

    inputLabel: '圖片網址',

    inputPlaceholder: '請輸入 Unsplash 圖片網址',

    showCancelButton: true,

    confirmButtonText: '新增',

    cancelButtonText: '取消',
  })

  if (!result.isConfirmed) {
    return
  }

  try {
    await createPlaceImage({
      placeId: 46,

      imageUrl: result.value,

      sortOrder: images.value.length + 1,
    })

    await Swal.fire({
      icon: 'success',

      title: '新增成功',

      timer: 1200,

      showConfirmButton: false,
    })

    await loadImages()
  } catch (error) {
    console.error(error)

    await Swal.fire({
      icon: 'error',

      title: '新增失敗',
    })
  }
}

const handleDeleteImage = async (imageId) => {
  const result = await Swal.fire({
    title: '刪除照片？',

    text: '刪除後無法恢復',

    icon: 'warning',

    showCancelButton: true,

    confirmButtonText: '刪除',

    cancelButtonText: '取消',
  })

  if (!result.isConfirmed) {
    return
  }

  try {
    await deletePlaceImage(imageId)

    await Swal.fire({
      icon: 'success',

      title: '刪除成功',

      timer: 1200,

      showConfirmButton: false,
    })

    await loadImages()
  } catch (error) {
    console.error(error)

    await Swal.fire({
      icon: 'error',

      title: '刪除失敗',
    })
  }
}

onMounted(async () => {
  await loadImages()
})
</script>

<template>
  <MerchantNavbar />
  <div class="container mt-4">
    <h1>店家照片管理</h1>
    <button class="btn btn-primary mb-3" @click="handleCreateImage">新增照片</button>

    <div v-for="image in images" :key="image.imageId" class="card mt-3">
      <div class="card-body">
        <img :src="image.imageUrl" class="img-fluid rounded" style="max-width: 500px" />

        <div class="mt-2 text-muted">圖片 {{ image.imageId }}</div>
        <button class="btn btn-danger" @click="handleDeleteImage(image.imageId)">刪除照片</button>
      </div>
    </div>
  </div>
</template>
