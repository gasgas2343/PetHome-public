<script setup></script>

<template>
  <nav class="petmap-navbar">
    <div class="petmap-container">
      <RouterLink
  to="/"
  class="petmap-brand"
>
  <img
  src="/logo.png"
  class="petmap-logo-image"
/>

  <div>
    <div class="petmap-title">毛起來</div>
    <div class="petmap-subtitle">陪伴．毛孩．好好生活</div>
  </div>
</RouterLink>

      <div class="petmap-nav">
        <RouterLink :to="{ name: 'PetMapHome' }" class="petmap-link">
  首頁
</RouterLink>

        <RouterLink :to="{ name: 'PetMapFavorite' }" class="petmap-link"> 我的收藏 </RouterLink>

        <RouterLink :to="{ name: 'PetMapMySubmission' }" class="petmap-link">
          我的投稿
        </RouterLink>

        <RouterLink :to="{ name: 'PetMapCreateSubmission' }" class="petmap-link">
          新增投稿
        </RouterLink>
        <RouterLink :to="{ name: 'PetMapMyReport' }" class="petmap-link"> 我的檢舉 </RouterLink>
        <RouterLink :to="{ name: 'PetMapMyItinerary' }" class="petmap-link">
          我的行程
        </RouterLink>
        <!-- <RouterLink :to="{ name: 'PetMapAIRecommend' }" class="petmap-link"> AI推薦 </RouterLink> -->
      </div>
    </div>
  </nav>
</template>

<style scoped>
.petmap-navbar {
  background: rgba(255, 255, 255, 0.9);

  backdrop-filter: blur(10px);

  border-radius: 0 0 20px 20px;

  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);

  margin-bottom: 20px;

  padding-top: 8px;

  padding-bottom: 8px;

  min-height: 80px;
}

.petmap-link {
  font-weight: 600;
}

.petmap-link.router-link-active {
  color: #2f855a;

  border-bottom: 2px solid #2f855a;
}

.petmap-brand {
  display: flex;
  align-items: center;
  gap: 12px;

  color: #2f855a;
  font-size: 1.5rem;

  text-decoration: none;
}

.petmap-title {
  font-size: 1.7rem;

  font-weight: 700;

  color: #6d4c41;

  line-height: 1;
}

.petmap-subtitle {
  font-size: 0.8rem;

  color: #9c7c5d;

  margin-top: 4px;
}

.petmap-logo-image {
  width: 60px;

  height: 60px;

  object-fit: contain;

  border-radius: 12px;
}

.petmap-container {
 width: min(1200px, calc(100% - 48px));
  margin: auto;

  display: flex;
  align-items: center;
  justify-content: space-between;
}

.petmap-nav {
  display: flex;
  align-items: center;
  gap: 20px;
}

.petmap-link {
  color: #6f6258;
  text-decoration: none;
  font-weight: 600;

  transition: .2s;
}

.petmap-link:hover {
  color: #2f855a;
}
</style>
