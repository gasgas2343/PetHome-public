<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const cards = [
  {
    title: '論壇交流',
    text: '分享飼養心得、發問討論，一起交流毛孩生活',
    icon: 'bi-chat-dots',
    to: '/community',
    button: '加入討論',
    accent: 'blue',
  },
  {
    title: '廣告專區',
    text: '論壇內的寵物用品、照護服務與活動資訊',
    icon: 'bi-megaphone',
    to: '/community/ads',
    button: '查看廣告',
    accent: 'green',
  },
  {
    title: '毛孩資料',
    text: '建立寵物基本資料，記錄生日、品種與生活資訊',
    icon: 'bi-journal-medical',
    to: '/pets',
    button: '管理資料',
    accent: 'mint',
  },
  {
    title: '查看回憶紀錄',
    text: '記錄疫苗、預約與毛孩成長時刻',
    icon: 'bi-calendar-heart',
    to: '/pets/1/timeline',
    button: '我的回憶',
    accent: 'brown',
  },
]
</script>

<template>
  <section class="life-grid">
    <article
      v-for="card in cards"
      :key="card.title"
      class="life-card"
      :class="`life-card-${card.accent}`"
      @click="router.push(card.to)"
    >
      <div class="life-icon"><i class="bi" :class="card.icon"></i></div>
      <h3>{{ card.title }}</h3>
      <p>{{ card.text }}</p>
      <button type="button">{{ card.button }}</button>
    </article>
  </section>
</template>

<style scoped>
.life-grid {
  display: grid;
  grid-template-columns: 1.05fr 1fr;
  gap: 18px;
}
.life-card {
  min-height: 170px;
  padding: 28px;
  border-radius: 34px;
  border: 1px solid var(--pc-border);
  box-shadow: var(--pc-shadow);
  cursor: pointer;
  overflow: hidden;
  position: relative;
  transition: 0.25s ease;
  background: rgba(255, 255, 255, 0.68);
}
.life-card:hover {
  transform: translateY(-5px) rotate(-0.3deg);
}
.life-card:nth-child(2) {
  transform: rotate(-1.5deg);
}
.life-card:nth-child(2):hover {
  transform: translateY(-5px) rotate(-1.5deg);
}
.life-card:nth-child(3) {
  transform: rotate(1.2deg);
}
.life-card:nth-child(3):hover {
  transform: translateY(-5px) rotate(1.2deg);
}
.life-card::after {
  content: '';
  position: absolute;
  right: -30px;
  bottom: -45px;
  width: 180px;
  height: 140px;
  border-radius: 48% 52% 42% 58%;
  opacity: 0.38;
  background: #d8e7ec;
}
.life-icon {
  font-size: 25px;
  color: var(--pc-primary);
  margin-bottom: 12px;
}
.life-card h3 {
  color: var(--pc-title);
  font-size: 24px;
  font-weight: 800;
  margin: 0 0 10px;
  letter-spacing: 0.06em;
}
.life-card p {
  width: min(270px, 100%);
  color: var(--pc-text);
  line-height: 1.7;
  margin-bottom: 18px;
}
.life-card button {
  position: relative;
  z-index: 1;
  border: none;
  border-radius: 999px;
  padding: 9px 18px;
  background: var(--pc-green);
  color: #fff;
  font-weight: 700;
}
.life-card-green::after {
  background: #eed6b7;
}
.life-card-mint::after {
  background: #d5e8e1;
}
.life-card-brown::after {
  background: #e9d8c5;
}
@media (max-width: 768px) {
  .life-grid {
    grid-template-columns: 1fr;
  }
  .life-card,
  .life-card:nth-child(2),
  .life-card:nth-child(3) {
    transform: none;
  }
}
</style>
