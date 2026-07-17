<script setup>
import { ref, onMounted, computed } from 'vue'

import { getAllTags } from '@/api/petmap/tagApi'
import { getTagsByPlaceId } from '@/api/petmap/placeApi'
import { createPlaceTag, deletePlaceTag } from '@/api/petmap/placeTagApi'

import Swal from 'sweetalert2'

import MerchantNavbar from '@/components/petmap/MerchantNavbar.vue'

const placeId = 46

const currentTags = ref([])
const allTags = ref([])
const selectedTagId = ref('')

const availableTags = computed(() => {
  return allTags.value.filter(
    (tag) =>
      !currentTags.value.some(
        (current) => current.tagId === tag.tagId
      )
  )
})

const loadData = async () => {
  try {
    const currentResponse = await getTagsByPlaceId(placeId)

    currentTags.value = currentResponse.data.data || []

    const allResponse = await getAllTags()

    allTags.value = allResponse.data.data || []
  } catch (error) {
    console.error(error)

    await Swal.fire({
      icon: 'error',
      title: '載入失敗',
      text: '無法取得標籤資料',
    })
  }
}

const handleAddTag = async () => {
  if (!selectedTagId.value) {
    await Swal.fire({
      icon: 'warning',
      title: '請選擇標籤',
    })

    return
  }

  try {
    await createPlaceTag({
      placeId,
      tagId: Number(selectedTagId.value),
    })

    await Swal.fire({
      icon: 'success',
      title: '新增成功',
      timer: 1200,
      showConfirmButton: false,
    })

    selectedTagId.value = ''

    await loadData()
  } catch (error) {
    console.error(error)

    await Swal.fire({
      icon: 'error',
      title: '新增失敗',
      text: error.response?.data?.message || '請稍後再試',
    })
  }
}

const handleDeleteTag = async (tagId) => {
  const result = await Swal.fire({
    title: '刪除標籤？',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '刪除',
    cancelButtonText: '取消',
  })

  if (!result.isConfirmed) return

  try {
    await deletePlaceTag(placeId, tagId)

    await Swal.fire({
      icon: 'success',
      title: '刪除成功',
      timer: 1200,
      showConfirmButton: false,
    })

    await loadData()
  } catch (error) {
    console.error(error)

    await Swal.fire({
      icon: 'error',
      title: '刪除失敗',
      text: error.response?.data?.message || '請稍後再試',
    })
  }
}

onMounted(async () => {
  await loadData()
})
</script>

<template>
  <MerchantNavbar />

  <div class="container mt-4">
    <h1>標籤管理</h1>

    <div class="card mt-4 mb-4">
      <div class="card-body">
        <h5>新增標籤</h5>

        <select
          v-model="selectedTagId"
          class="form-select mt-3"
        >
          <option value="">請選擇標籤</option>

          <option
            v-for="tag in availableTags"
            :key="tag.tagId"
            :value="tag.tagId"
          >
            {{ tag.name }}
          </option>
        </select>

        <button
          class="btn btn-success mt-3"
          @click="handleAddTag"
        >
          新增標籤
        </button>
      </div>
    </div>

    <div class="mb-3">
      <h5>目前共有 {{ currentTags.length }} 個標籤</h5>
    </div>

    <div
      v-if="currentTags.length === 0"
      class="alert alert-secondary"
    >
      目前尚未設定任何標籤
    </div>

    <div
      v-else
      v-for="tag in currentTags"
      :key="tag.tagId"
      class="card mt-2"
    >
      <div class="card-body d-flex justify-content-between align-items-center">
        <span>{{ tag.name }}</span>

        <button
          class="btn btn-danger btn-sm"
          @click="handleDeleteTag(tag.tagId)"
        >
          刪除
        </button>
      </div>
    </div>
  </div>
</template>
