<script setup></script>

<template>
  <nav class="navbar navbar-expand-lg navbar-light custom-navbar">
    <div class="container">
      <RouterLink to="/" class="navbar-brand brand-logo">
        <img src="/logo.png" alt="毛起來" class="logo-image" />

        <div>
          <div class="brand-title">管理員中心</div>

          <div class="brand-subtitle">管理員後台</div>
        </div>
      </RouterLink>

      <div class="navbar-nav ms-auto">
        <RouterLink :to="{ name: 'PetMapAdminDashboard' }" class="nav-link">
          管理首頁
        </RouterLink>

        <RouterLink :to="{ name: 'PetMapAdminPlace' }" class="nav-link"> 景點管理 </RouterLink>

        <RouterLink :to="{ name: 'PetMapAdminSubmission' }" class="nav-link">
          投稿管理
        </RouterLink>

        <RouterLink :to="{ name: 'PetMapAdminReport' }" class="nav-link"> 檢舉管理 </RouterLink>



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

.brand-logo {
  display: flex;

  align-items: center;

  gap: 12px;

  text-decoration: none;
}

.logo-image {
  width: 60px;

  height: 60px;

  object-fit: cover;

  border-radius: 12px;
}

.brand-title {
  font-size: 1.7rem;
  color: #6d4c41;
  font-weight: 700;
}

.brand-subtitle {
  font-size: 0.8rem;

  color: #666;
}

.nav-link {
  font-weight: 600;

  margin-left: 20px;
}

.nav-link.router-link-active {
  color: #c53030;

  border-bottom: 2px solid #c53030;
}
</style>
