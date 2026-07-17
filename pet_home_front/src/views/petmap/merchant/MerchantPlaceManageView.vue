<script setup>
import { ref, onMounted } from 'vue'

import { getPlaceById, updatePlace } from '@/api/petmap/placeApi'

import Swal from 'sweetalert2'

import MerchantNavbar from '@/components/petmap/MerchantNavbar.vue'

const place = ref(null)

const loadPlace = async () => {
  try {

    // TODO：正式版改成登入商家的 placeId
    const response = await getPlaceById(46)

    place.value = response.data.data

  } catch (error) {
    console.error('取得店家失敗', error)

    await Swal.fire({
      icon: 'error',
      title: '載入失敗',
      text: error.response?.data?.message || '無法取得店家資料',
    })
  }
}

const handleUpdatePlace = async () => {
  try {

    const request = {
      name: place.value.name,
      placeType: place.value.placeType,
      address: place.value.address,
      phone: place.value.phone,
      description: place.value.description,
      status: place.value.status,
      latitude: place.value.latitude,
      longitude: place.value.longitude,
    }

    await updatePlace(place.value.placeId, request)

    await Swal.fire({
      icon: "success",
      title: "店家資料已更新",
      timer: 1500,
      showConfirmButton: false,
    })

    await loadPlace()

  } catch (error) {
    console.error(error)

    await Swal.fire({
      icon: "error",
      title: "修改失敗",
      text: error.response?.data?.message || "請稍後再試",
    })
  }
}

onMounted(loadPlace)
</script>

<template>
  <MerchantNavbar />

  <div class="container py-4">
    <div class="card shadow-sm border-0">
      <div class="card-header bg-success text-white">
        <h3 class="mb-0">店家資料管理</h3>
      </div>

      <div class="card-body" v-if="place">
        <div class="row">
          <div class="col-md-6 mb-3">
            <label class="form-label fw-bold">店名</label>

            <input v-model="place.name" class="form-control" />
          </div>

          <div class="col-md-6 mb-3">
            <label class="form-label fw-bold">類型</label>

            <select v-model="place.placeType" class="form-select">
              <option value="CAFE">咖啡廳</option>
              <option value="RESTAURANT">餐廳</option>
              <option value="HOTEL">寵物旅館</option>
              <option value="PARK">公園</option>
              <option value="HOSPITAL">動物醫院</option>
            </select>
          </div>

          <div class="col-md-12 mb-3">
            <label class="form-label fw-bold">地址</label>

            <textarea v-model="place.address" rows="2" class="form-control"></textarea>
          </div>

          <div class="col-md-6 mb-3">
            <label class="form-label fw-bold">電話</label>

            <input v-model="place.phone" class="form-control" />
          </div>

          <div class="col-md-6 mb-3">
            <label class="form-label fw-bold">營業狀態</label>

            <select v-model="place.status" class="form-select">
              <option value="APPROVED">已通過</option>
              <option value="PENDING">待審核</option>
              <option value="REJECTED">已駁回</option>
            </select>
          </div>

          <div class="col-md-12 mb-3">
            <label class="form-label fw-bold">店家介紹</label>

            <textarea
              v-model="place.description"
              rows="5"
              class="form-control"
              placeholder="請輸入店家介紹..."
            ></textarea>
          </div>
        </div>

        <hr />

        <div class="text-end">
          <button class="btn btn-success px-5" @click="handleUpdatePlace">💾 儲存修改</button>
        </div>
      </div>
    </div>
  </div>
</template>
