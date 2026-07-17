<script setup>
import MerchantNavbar from '@/components/petmap/MerchantNavbar.vue'

import { ref } from 'vue'
import { onMounted } from 'vue'

import { getPlaceById } from '@/api/petmap/placeApi'

// import { userAuthStore } from '@/stores/auth'

// const authStore = userAuthStore()

const place = ref(null)

const loadPlace = async () => {
  const response = await getPlaceById(46)

  place.value = response.data.data
}

onMounted(async () => {
  await loadPlace()
})
</script>

<template>
  <MerchantNavbar />
  <div class="container mt-4">
    <h1 class="mb-4">商家中心</h1>
    <div v-if="place" class="card mb-4 shadow-sm">
      <div class="card-body">
        <h4>
          {{ place.name }}
        </h4>

        <p class="mb-2">
          <strong>類型：</strong>
          {{ place.placeType }}
        </p>

        <p class="mb-2">
          <strong>地址：</strong>
          {{ place.address }}
        </p>

        <p class="mb-2">
          <strong>電話：</strong>
          {{ place.phone || '未提供' }}
        </p>
        <p class="mb-0">
          <strong>狀態：</strong>

          <span class="badge bg-success">
            {{ place.status }}
          </span>
        </p>
      </div>
    </div>

    <div class="row g-4">
      <div class="col-md-3">
        <RouterLink :to="{ name: 'PetMapMerchantPlace' }" class="text-decoration-none">
          <div class="card shadow-sm h-100">
            <div class="card-body text-center">
              <h3>資料</h3>

              <h3>📄</h3>
              <h5>店家資料管理</h5>
            </div>
          </div>
        </RouterLink>
      </div>

      <div class="col-md-3">
        <RouterLink :to="{ name: 'PetMapMerchantPlacePhoto' }" class="text-decoration-none">
          <div class="card shadow-sm h-100">
            <div class="card-body text-center">
              <h3>照片</h3>

              <h3>🖼️</h3>
              <h5>店家照片管理</h5>
            </div>
          </div>
        </RouterLink>
      </div>

      <div class="col-md-3">
        <RouterLink :to="{ name: 'PetMapMerchantReview' }" class="text-decoration-none">
          <div class="card shadow-sm h-100">
            <div class="card-body text-center">
              <h3>評論</h3>

              <h3>⭐</h3>
              <h5>評論管理</h5>
            </div>
          </div>
        </RouterLink>
      </div>

      <div class="col-md-3">
        <RouterLink :to="{ name: 'PetMapMerchantTag' }" class="text-decoration-none">
          <div class="card shadow-sm h-100">
            <div class="card-body text-center">
              <h3>標籤</h3>

              <h3>🏷️</h3>
              <h5>標籤管理</h5>
            </div>
          </div>
        </RouterLink>
      </div>
    </div>
  </div>
</template>
<style scoped>

.card{

transition:.2s;

cursor:pointer;

}

.card:hover{

transform:translateY(-4px);

box-shadow:0 .5rem 1rem rgba(0,0,0,.15)!important;

}

</style>
