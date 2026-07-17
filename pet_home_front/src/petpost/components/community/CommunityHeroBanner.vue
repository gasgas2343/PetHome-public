<script setup>
import { RouterLink } from 'vue-router'
import heroPets from '@/petpost/assets/hero/hero-pets.png'
</script>

<template>
  <section class="community-hero">
    <div class="hero-text">
      <p class="hero-kicker">PawCare Community</p>
      <h1>一起分享<br />毛孩生活點滴</h1>
      <p class="hero-desc">分享・交流・領養・陪伴</p>

      <RouterLink to="/post/create" class="hero-btn"> 開始發文 </RouterLink>
    </div>

    <img :src="heroPets" class="hero-img" alt="毛孩論壇插圖" />
  </section>
</template>

<style scoped>
.community-hero {
  min-height: 340px;
  border-radius: 34px;
  padding: 56px;
  margin-bottom: 38px;
  background: #f8ead7;
  display: flex;
  align-items: center;
  justify-content: space-between;
  overflow: hidden;
  box-shadow: 0 18px 45px rgba(106, 74, 44, 0.12);
}

.hero-kicker {
  color: #b98754;
  font-weight: 900;
  letter-spacing: 0.08em;
}

.hero-text h1 {
  color: #5f3d23;
  font-size: 46px;
  font-weight: 900;
  line-height: 1.25;
}

.hero-desc {
  margin: 18px 0 28px;
  color: #7b5a3a;
  font-size: 20px;
  font-weight: 700;
}

.hero-btn {
  display: inline-block;
  padding: 13px 32px;
  border-radius: 999px;
  background: #a96f3d;
  color: white;
  text-decoration: none;
  font-weight: 800;
}

.hero-img {
  width: 50%;
  max-height: 280px;
  object-fit: contain;
}

@media (max-width: 768px) {
  .community-hero {
    padding: 34px 24px;
    flex-direction: column;
    text-align: center;
  }

  .hero-img {
    width: 100%;
    margin-top: 24px;
  }

  .hero-text h1 {
    font-size: 34px;
  }
}
</style>
