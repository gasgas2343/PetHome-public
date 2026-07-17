<!-- src/components/HomeBenefits.vue -->
<script setup>
import benefitImage from '@/assets/images/home/home-benefits-dog.jpg'

const benefits = [
  {
    title: '建立毛孩資料',
    desc: ['記錄毛孩身高體重', '個性與健康習慣'],
    icon: `<svg viewBox="0 0 24 24">
        <path d="M6.5 11.5c-1.4 0-2.5-1.1-2.5-2.5S5.1 6.5 6.5 6.5 9 7.6 9 9s-1.1 2.5-2.5 2.5Z"/>
        <path d="M17.5 11.5c-1.4 0-2.5-1.1-2.5-2.5s1.1-2.5 2.5-2.5S20 7.6 20 9s-1.1 2.5-2.5 2.5Z"/>
        <path d="M9.5 7C8.4 7 7.5 6.1 7.5 5S8.4 3 9.5 3s2 .9 2 2-.9 2-2 2Z"/>
        <path d="M14.5 7c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2Z"/>
        <path d="M12 13c-3.2 0-5.5 2.1-5.5 4.4 0 1.5 1.1 2.6 2.5 2.6.9 0 1.7-.5 3-.5s2.1.5 3 .5c1.4 0 2.5-1.1 2.5-2.6C17.5 15.1 15.2 13 12 13Z"/>
      </svg>`,
  },
  {
    title: '收藏寵物商家',
    desc: ['快速找到喜愛店家', '下次消費更方便'],
    icon: ` <svg viewBox="0 0 24 24">
        <path d="M6 4h12a1 1 0 0 1 1 1v15l-7-4-7 4V5a1 1 0 0 1 1-1Z"/>
        <path d="M9 8h6"/>
        <path d="M9 11h4"/>
      </svg>`,
  },
  {
    title: '追蹤預約與訂單',
    desc: ['即時掌握服務進度', '狀態與交易紀錄'],
    icon: `<svg viewBox="0 0 24 24">
        <path d="M7 3h10v4H7z"/>
        <path d="M6 5H5a2 2 0 0 0-2 2v13h18V7a2 2 0 0 0-2-2h-1"/>
        <path d="M8 12h5"/>
        <path d="M8 16h4"/>
        <path d="M15.5 15.5l1.5 1.5 3-3"/>
      </svg>`,
  },
  {
    title: '參與社群交流',
    desc: ['加入討論與活動', '認識更多同好夥伴'],
    icon: `<svg viewBox="0 0 24 24">
        <path d="M8 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z"/>
        <path d="M16 12a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7Z"/>
        <path d="M2.5 21c.5-4 2.7-6.5 5.5-6.5S13 17 13.5 21"/>
        <path d="M12.5 15.5c1-.8 2.1-1.2 3.5-1.2 2.6 0 4.8 2.3 5.3 6.7"/>
      </svg>`,
  },
  {
    title: '查看個人化提醒',
    desc: ['接收通知與點數', '掌握照護相關消息'],
    icon: ` <svg viewBox="0 0 24 24">
        <path d="M18 9a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9Z"/>
        <path d="M10 21h4"/>
        <path d="M9.5 4.5C10 3.6 10.8 3 12 3s2 .6 2.5 1.5"/>
      </svg>`,
  },
]
</script>

<template>
  <section class="home-benefits">
    <div class="benefits-card">
      <!-- 左側登入區 -->
      <div class="benefits-hero">
        <img :src="benefitImage" alt="會員與毛孩互動" class="benefits-hero-img" />

        <div class="benefits-hero-content">
          <p class="eyebrow">登入會員後</p>

          <h2 class="benefits-title">
            <span>享受更多</span>
            <span>
              貼心服務
              <i class="doodle-heart">♡</i>
            </span>
          </h2>

          <p class="hero-desc">
            打造專屬於你與毛孩的個人化體驗<br />
            讓 PawCare 成為你們每日的生活夥伴
          </p>

          <div class="hero-actions">
            <RouterLink to="/login" class="primary-btn"> 立即加入會員 </RouterLink>
          </div>
        </div>
      </div>

      <!-- 右側好處列表 -->
      <div class="benefits-list">
        <article v-for="item in benefits" :key="item.title" class="benefit-item">
          <div class="icon-circle" v-html="item.icon"></div>

          <h3>{{ item.title }}</h3>

          <p class="benefit-desc">
            <span v-for="line in item.desc" :key="line">
              {{ line }}
            </span>
          </p>
        </article>
      </div>
    </div>
  </section>
</template>

<style scoped>
.home-benefits {
  width: 100%;
  padding: 8px 16px 48px; 
  background: transparent;
}

.benefits-card {
  max-width: 1280px;
  min-height: 260px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1.08fr 1.32fr;
  overflow: hidden;
  border-radius: 24px;
  background: #f8efe4;
  box-shadow:
    0 18px 45px rgba(84, 55, 32, 0.08),
    inset 0 0 0 1px rgba(255, 255, 255, 0.55);
}

/* left */
.benefits-hero {
  position: relative;
  min-height: 260px;
  overflow: hidden;
  z-index: 2;

  /* 右邊切斜 */
  clip-path: polygon(0 0, 100% 0, calc(100% - 48px) 100%, 0 100%);

  /* 讓斜切不要露出空白 */
  margin-right: -48px;
}

.benefits-hero::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(
    90deg,
    rgba(248, 239, 228, 0.96) 20%,
    rgba(248, 239, 228, 0.88) 14%,
    rgba(248, 239, 228, 0.18) 38%,
    rgba(248, 239, 228, 0) 100%
  );
  pointer-events: none;
}

.benefits-hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: 68% center;
  display: block;
}

.benefits-hero-content {
  position: absolute;
  z-index: 2;
  top: 34px;
  left: 40px;
  width: 290px;
  color: #4f3728;
}

.eyebrow {
  margin: 0 0 8px;
  font-size: 17px;
  font-weight: 800;
  letter-spacing: 0.12em;
  color: #6b4a35;
}

.benefits-hero-content h2 {
  position: relative;
  margin: 0;
  font-size: 30px;
  line-height: 1.28;
  font-weight: 900;
  letter-spacing: 0.08em;
}

.benefits-title {
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 31px;
  line-height: 1.22;
  font-weight: 900;
  letter-spacing: 0.1em;
  color: #4b3324;
}

.benefits-title span {
  position: relative;
  display: inline-block;
}

.benefits-title span:first-child {
  padding-left: 2px;
}

.benefits-title span:nth-child(2) {
  padding-left: 14px;
}

.doodle-heart {
  position: absolute;
  top: -4px;
  right: -28px;
  font-style: normal;
  font-size: 24px;
  font-weight: 400;
  color: #d59754;
  transform: rotate(-12deg);
}
.hero-desc {
  margin: 16px 0 20px;
  padding-left: 2px;
  font-size: 13px;
  line-height: 1.9;
  letter-spacing: 0.04em;
  color: #7b6656;
}

.hero-actions {
  display: flex;
  align-items: center;
}

.primary-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 36px;
  padding: 0 20px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0.06em;
  text-decoration: none;
  color: #fff;
  background: #2f78a7;
  box-shadow: 0 8px 18px rgba(47, 120, 167, 0.22);
  transition:
    transform 0.25s ease,
    box-shadow 0.25s ease,
    background-color 0.25s ease;
}

.primary-btn:hover {
  transform: translateY(-2px);
  background: #286b96;
  box-shadow: 0 12px 24px rgba(47, 120, 167, 0.28);
}

/* right */
.benefits-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  align-items: center;
  gap: 8px;
  padding: 36px 34px 36px 72px;
  background: rgba(255, 250, 244, 0.72);
}

.benefit-item {
  min-width: 0;
  text-align: center;
  color: #5a4233;
}

.icon-circle {
  width: 68px;
  height: 68px;
  margin: 0 auto 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.92), rgba(255, 248, 239, 0.82));
  box-shadow:
    0 8px 20px rgba(117, 82, 49, 0.08),
    inset 0 0 0 1px rgba(214, 170, 120, 0.18);
}

.icon-circle :deep(svg) {
  width: 34px;
  height: 34px;
  fill: none;
  stroke: #b88756;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.benefit-item h3 {
  margin: 0 0 9px;
  font-size: 15px;
  font-weight: 900;
  letter-spacing: 0.06em;
  color: #4c3728;
  white-space: nowrap;
}

.benefit-item p {
  max-width: 120px;
  margin: 0 auto;
  font-size: 12px;
  line-height: 1.6;
  color: #8a7463;
}

.benefit-desc {
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 12px;
  line-height: 1.55;
  letter-spacing: 0.04em;
  color: #8a7463;
}

.benefit-desc span {
  display: block;
  white-space: nowrap;
}

/* responsive */
@media (max-width: 1100px) {
  .benefits-hero {
    clip-path: none;
    margin-right: 0;
  }

  .benefits-list {
    padding: 36px 34px;
  }
}
@media (max-width: 768px) {
  .home-benefits {
    padding: 32px 14px;
  }

  .benefits-card {
    border-radius: 22px;
  }

  .benefits-hero {
    min-height: 300px;
  }

  .benefits-hero-content {
    top: 28px;
    left: 24px;
    width: 245px;
  }

  .benefits-hero-content h2 {
    font-size: 26px;
  }

  .benefits-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 22px 12px;
    padding: 28px 20px 30px;
    overflow: visible;
  }

  .benefit-item p {
    max-width: 150px;
  }
}

@media (max-width: 480px) {
  .benefit-desc {
    margin: 0;
    align-items: flex-start;
  }

  .benefit-desc span {
    white-space: normal;
  }
}
</style>
