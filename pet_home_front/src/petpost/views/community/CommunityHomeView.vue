<script setup>
import '@/petpost/assets/petpost-theme.scss'
import { RouterLink, useRouter } from 'vue-router'
import { userAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = userAuthStore()

function goBackHome() {
  router.push('/')
}
</script>

<template>
  <main class="community-home-page">
    <section class="community-home-shell">
      <p class="kicker">PawCare Community</p>

      <div class="back-area">
        <button class="back-btn" @click="goBackHome">
          <i class="bi bi-arrow-left"></i>
          返回會員首頁
        </button>
      </div>

      <h1>論壇首頁</h1>

      <p class="intro">你可以前往論壇文章交流，也可以查看毛孩時間軸與生活紀錄。</p>

      <div class="entry-grid">
        <RouterLink class="entry-card" to="/community/posts">
          <span class="entry-icon">📝</span>
          <h2>論壇文章</h2>
          <p>查看文章、發文、留言、按讚、收藏與檢舉。</p>
        </RouterLink>

        <RouterLink class="entry-card" to="/pets">
          <span class="entry-icon">🐾</span>
          <h2>毛孩時間軸</h2>
          <p>查看毛孩資料、成長時刻、生活紀錄與回憶日誌。</p>
        </RouterLink>

        <RouterLink
          v-if="authStore.hasRoles('ADMIN') || authStore.hasRoles('STAFF')"
          class="entry-card admin-entry-card"
          to="/forum-admin/posts"
        >
          <span class="entry-icon">👑</span>
          <h2>論壇後台管理</h2>
          <p>管理論壇文章、留言、檢舉案件與申訴審核。</p>
        </RouterLink>
      </div>
    </section>
  </main>
</template>

<style scoped>
.back-area {
  margin-bottom: 20px;
}

.back-btn {
  border: none;
  background: transparent;
  color: #8b5e3c;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
}

.back-btn:hover {
  color: #5c4033;
}

.community-home-page {
  min-height: 100vh;
  padding: 64px 20px;
  background: transparent;
}

.community-home-shell {
  width: min(1080px, 100%);
  margin: 0 auto;
}

.kicker {
  margin: 0 0 10px;
  color: #d6a158;
  font-weight: 900;
  letter-spacing: 0.08em;
}

h1 {
  margin: 0;
  color: #3f332b;
  font-size: 44px;
  font-weight: 900;
}

.intro {
  margin: 16px 0 32px;
  color: #6f6258;
  font-size: 18px;
  line-height: 1.8;
}

.entry-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.entry-card {
  display: block;
  min-height: 220px;
  padding: 32px;
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(111, 98, 88, 0.12);
  box-shadow: 0 18px 40px rgba(92, 61, 35, 0.1);
  color: #3f332b;
  text-decoration: none;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.entry-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 24px 54px rgba(92, 61, 35, 0.16);
}

.entry-icon {
  display: inline-flex;
  margin-bottom: 18px;
  font-size: 42px;
}

.entry-card h2 {
  margin: 0 0 12px;
  font-size: 26px;
  font-weight: 900;
}

.entry-card p {
  margin: 0;
  color: #6f6258;
  line-height: 1.75;
}

@media (max-width: 768px) {
  .entry-grid {
    grid-template-columns: 1fr;
  }

  h1 {
    font-size: 34px;
  }
}
</style>
