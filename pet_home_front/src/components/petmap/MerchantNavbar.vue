<script setup></script>

<template>
  <nav class="navbar navbar-expand-lg navbar-light custom-navbar">
    <div class="container">
      <RouterLink
  to="/"
  class="navbar-brand brand-logo"
>
        <img src="/logo.png" alt="毛起來" class="logo-image" />

        <div>
          <div class="brand-title">商家中心</div>

          <div class="brand-subtitle">店家管理後台</div>
        </div>
      </RouterLink>

      <div class="navbar-nav ms-auto">
        <RouterLink
  :to="{ name: 'PetMapMerchantDashboard' }"
  class="nav-link"
>
  商家首頁
</RouterLink>

        <RouterLink :to="{ name: 'PetMapMerchantPlace' }" class="nav-link">
          店家資料
        </RouterLink>

        <RouterLink :to="{ name: 'PetMapMerchantPlacePhoto' }" class="nav-link">
          照片管理
        </RouterLink>

        <RouterLink :to="{ name: 'PetMapMerchantReview' }" class="nav-link">
          評論管理
        </RouterLink>

        <RouterLink :to="{ name: 'PetMapMerchantTag' }" class="nav-link"> 標籤管理 </RouterLink>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.custom-navbar {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 0 0 20px 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  padding-top: 8px;
  padding-bottom: 8px;
}

.nav-link {
  font-weight: 600;
  margin-left: 20px;
}

.nav-link.router-link-active {
  color: #2f855a;
  border-bottom: 2px solid #2f855a;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
}

.brand-title {
  font-size: 1.7rem;
  font-weight: 700;
  color: #6d4c41;
  line-height: 1;
}

.brand-subtitle {
  font-size: 0.8rem;
  color: #9c7c5d;
  margin-top: 4px;
}

.logo-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 12px;
}
</style>
