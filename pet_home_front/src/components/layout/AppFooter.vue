<!-- src/components/HomeFooter.vue -->
<script setup>
import footerLogo from '@/assets/images/home/pawcare-logo.png'

const footerLinks = [
  {
    title: '服務探索',
    links: [
      { label: '找商家', to: '/merchants' },
      { label: '購物商城', to: '/shop' },
      { label: '預約服務', to: '/booking' },
      { label: '毛孩百科', to: '/articles' },
    ],
  },
  {
    title: '飼主交流',
    links: [
      { label: '論壇交流', to: '/forum' },
      { label: '活動專區', to: '/events' },
      { label: '飼主部落格', to: '/blog' },
    ],
  },
  {
    title: '關於我們',
    links: [
      { label: '關於毛起來', to: '/about' },
      { label: '最新消息', to: '/news' },
      { label: '合作夥伴', to: '/partners' },
    ],
  },
  {
    title: '客服支援',
    links: [
      { label: '常見問題', to: '/faq' },
      { label: '聯絡我們', to: '/contact' },
      { label: '服務條款', to: '/terms' },
      { label: '隱私權政策', to: '/privacy' },
    ],
  },
]
</script>

<template>
  <footer class="home-footer">
    <div class="footer-wave" aria-hidden="true">
      <svg class="footer-wave-svg" viewBox="0 0 1200 150" preserveAspectRatio="xMidYMid slice">
        <path
          class="footer-wave-fill"
          d="M0,105 C18,98 36,101 57.1,93.8c9.3-7,21.7-8.6,32.2-2.1,2.9,1.8,6.4,7.6,9.6,9,4.1,1.9,6.6-.4,9.6-2.9,5.5-4.7,11.5-9.5,18.5-9.7,10.4,0,16.9,10.5,19.5,13.1s7.2-13.9,24.8-13.9,22.2,15,26.3,14.2,9.7-9.1,20.5-9,13.6,9.8,20.8,12.4c7.2,2.6,6.5-11.3,24.6-12.4,5.7-.3,9.5,2.1,14.7,4.3s8.5,6.2,14.4,5.9c8.3-.4,14.3-8.3,22.1-10.7s9.3-1.4,13.4.6c5.2,2.6,9.1,6.9,14.6,9.3,1.1.5,2.4.9,3.4.3.5-.3.9-.8,1.3-1.4,4.9-7.3,11.5-12.5,20.9-12.2,6.2.2,10.7,2.1,16.3,4.8,3.6,1.8,7.7,4.5,11.1.9,3.4-3.5,6.7-5.5,11.6-6.8,6-1.6,12.4-1.6,18.4,0,5.6,1.5,12,7.6,17.9,5,2-.9,4.1-1.8,5.6-3.3,6.5-6.6,12.8-7.1,21.3-5s5.8,2.1,8.6,4.5c1.8,1.5,3.9,2.9,6.2,2.8,2.6,0,4.9-1.9,6.7-3.8,4.6-4.5,11.3-6.8,17.7-7.1,17-.9,24.4,8.9,31.4,8.9,6.8,0,12.6-6.8,19.1-8.4,6.3-1.6,12.9-1.8,19.3-.6,3.2.6,6.3,1.9,9,3.7,2.6,1.6,4.3,4.2,7.5,4.6,3.8.4,7.3-2.7,10.6-4.2,4.1-1.9,8.5-3.2,13.1-3.2,5,0,9.9,1.5,14.2,4.1s5.8,6.2,9,7.1c1.8.5,3.6-.3,5.2-1.3,6.2-4,14.3-9,22-9.2,14.2-.3,24,4.5,29.8,10.4,5.8,5.9,12-8.6,27.9-11.1,16.7.4,22.4,11,29,11.1s5.4-11.2,20.9-10c15.5,1.2,22.5,10.3,30.2,10s10.8-6.5,23.2-6.5,18.6,8.5,22.8,8.5,7.4-7.3,20.1-7,12.8,8.9,19.7,8.9,14.3-6.6,24-6.6,11.6,5.4,15.1,5.4,9.9-5.1,20.8-3.6,11.4,6.2,17.2,8.3,12.3-3.6,16.3-5c7.8-2.9,10.6,1.3,15.7,1.3s4.8-3,14.5-3.7,14.3,6.3,22.1,6.7,5.6-9.5,19.7-10.3c14.1-.9,17.4,9.1,24,9.5s11.2-5.6,22.5-5.9c4.3-.1,8.3,1.2,12,3.3s6.6,5,10.2,3.8c2.9-.9,4.1-3.9,6.4-5.6s5.8-3.1,9-3.7 L1200,150 L0,150 Z"
        />
      </svg>
    </div>

    <div class="footer-inner">
      <!-- 左側品牌 -->
      <div class="footer-brand">
        <RouterLink to="/" class="footer-logo">
          <img :src="footerLogo" alt="毛起來 Logo" class="footer-logo-img" />
        </RouterLink>

        <p class="brand-desc">
          <span class="brand-line brand-line-main">
            陪你和毛孩
            <i class="brand-paw">🐾</i>
          </span>

          <span class="brand-line brand-line-sub"> 一起展開日常 </span>

          <span class="brand-line brand-line-note">
            讓每個選擇都更
            <em>安心美好</em>
          </span>
        </p>
      </div>

      <!-- 中間連結 -->
      <nav class="footer-nav" aria-label="footer navigation">
        <div v-for="group in footerLinks" :key="group.title" class="footer-column">
          <h3>{{ group.title }}</h3>

          <ul>
            <li v-for="link in group.links" :key="link.label">
              <RouterLink :to="link.to">
                {{ link.label }}
              </RouterLink>
            </li>
          </ul>
        </div>
      </nav>

      <!-- 右側客服資訊 -->
      <div class="footer-contact-card">
        <h3>有任何問題？</h3>

        <p class="contact-subtitle">我們很樂意幫助你</p>

        <ul class="contact-list">
          <li>
            <span class="contact-icon">
              <svg viewBox="0 0 24 24">
                <path
                  d="M6.5 4.5 9 9l-2 1.5c1.4 3 3.5 5.1 6.5 6.5L15 15l4.5 2.5c.4.2.6.7.5 1.1-.4 1.5-1.8 2.4-3.3 2.1C9.5 19.4 4.6 14.5 3.3 7.3 3 5.8 3.9 4.4 5.4 4c.4-.1.9.1 1.1.5Z"
                />
              </svg>
            </span>

            <span class="contact-text">
              <strong>02-1234-5678</strong>
              <small>週一至週五 09:00 - 18:00</small>
            </span>
          </li>

          <li>
            <span class="contact-icon">
              <svg viewBox="0 0 24 24">
                <path d="M4 6h16v12H4z" />
                <path d="m4 7 8 6 8-6" />
              </svg>
            </span>

            <span class="contact-text">
              <strong>service@maoqilai.com.tw</strong>
              <small>客服信箱</small>
            </span>
          </li>

          <li>
            <span class="contact-icon">
              <svg viewBox="0 0 24 24">
                <path d="M12 21s7-5.1 7-11a7 7 0 1 0-14 0c0 5.9 7 11 7 11Z" />
                <circle cx="12" cy="10" r="2.5" />
              </svg>
            </span>

            <span class="contact-text">
              <strong>台北市信義區忠孝東路五段 68 號 5 樓</strong>
              <small>服務據點</small>
            </span>
          </li>
        </ul>
      </div>
    </div>

    <div class="footer-bottom">
      <p>© 2026 毛起來. All rights reserved.</p>
    </div>
  </footer>
</template>

<style scoped lang="scss">
.home-footer {
  --footer-bg: #dce3e9;
  --footer-text: #fffaf2;
  --footer-text-soft: rgba(255, 250, 242, 0.78);

  position: relative;
  margin-top: 120px;
  padding: 72px 8% 2px;
  background: var(--footer-bg);
  color: var(--footer-text);
  overflow: visible;
}

.footer-wave {
  position: absolute;
  left: 0;
  right: 0;
  top: -148px;
  height: 150px;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

.footer-wave-svg {
  display: block;
  width: 110%;
  height: 100%;
  margin-left: -5%;
}

.footer-wave-fill {
  fill: var(--footer-bg);
}

.footer-inner {
  position: relative;
  z-index: 1;
  max-width: 1180px;
  margin: 0 auto;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 40px;
}

/* 背景柔和裝飾 */
.footer-bg-shape {
  position: absolute;
  z-index: 1;
  border-radius: 999px;
  pointer-events: none;
}

.footer-bg-left {
  width: 360px;
  height: 160px;
  left: -120px;
  bottom: -70px;
  background: rgba(214, 232, 238, 0.45);
}

.footer-bg-right {
  width: 260px;
  height: 260px;
  right: -100px;
  top: -90px;
  background: rgba(239, 217, 190, 0.34);
}

/* 左側品牌 */
.footer-brand {
  min-width: 0;
}

.footer-logo {
  display: inline-flex;
  align-items: center;
  text-decoration: none;
}

.footer-logo-img {
  width: 250px;
  height: auto;
  display: block;
  object-fit: contain;
}
.brand-desc {
  position: relative;
  margin: 24px 40px 0;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  color: #6f5140;
}

.brand-line {
  display: block;
  font-weight: 800;
  letter-spacing: 0.08em;
}

.brand-line-main {
  position: relative;
  font-size: 18px;
  line-height: 1.4;
  color: #5a4030;
}

.brand-line-main::after {
  content: '';
  position: absolute;
  left: 4px;
  bottom: -5px;
  width: 92px;
  height: 8px;
  border-bottom: 3px solid rgba(91, 130, 160, 0.45);
  border-radius: 50%;
  transform: rotate(-2deg);
}

.brand-line-sub {
  padding-left: 22px;
  font-size: 17px;
  line-height: 1.45;
  color: #7a5b45;
}

.brand-line-note {
  margin-top: 4px;
  padding-left: 6px;
  font-size: 15px;
  line-height: 1.6;
  color: #8a7463;
}

.brand-line-note em {
  position: relative;
  font-style: normal;
  color: #c58a3f;
  font-weight: 900;
}

.brand-line-note em::after {
  content: '♡';
  position: absolute;
  right: -22px;
  top: -8px;
  font-size: 18px;
  color: #d79a5f;
  transform: rotate(-12deg);
}

.brand-paw {
  display: inline-block;
  margin-left: 4px;
  font-style: normal;
  font-size: 15px;
  transform: rotate(-12deg) translateY(-2px);
}

/* 中間連結 */
.footer-nav {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 34px;
}

.footer-column h3 {
  margin: 0 0 20px;
  font-size: 17px;
  line-height: 1.3;
  font-weight: 900;
  letter-spacing: 0.12em;
  color: #4f382b;
  white-space: nowrap;
}

.footer-column ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

.footer-column li + li {
  margin-top: 14px;
}

.footer-column a {
  display: inline-block;
  font-size: 15px;
  line-height: 1.4;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: rgba(79, 56, 43, 0.68);
  text-decoration: none;
  transition:
    color 0.22s ease,
    transform 0.22s ease;
}

.footer-column a:hover {
  color: #8f643f;
  transform: translateX(3px);
}

/* 右側客服卡 */
.footer-contact-card {
  padding: 26px 26px 24px;
  border-radius: 24px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(255, 250, 244, 0.68));
  box-shadow:
    0 16px 36px rgba(103, 72, 44, 0.08),
    inset 0 0 0 1px rgba(255, 255, 255, 0.55);
}

.footer-contact-card h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 900;
  letter-spacing: 0.08em;
  color: #5a4030;
}

.contact-subtitle {
  margin: 6px 0 20px;
  font-size: 15px;
  font-weight: 800;
  letter-spacing: 0.08em;
  color: #8a7463;
}

.contact-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.contact-list li {
  display: grid;
  grid-template-columns: 24px 1fr;
  gap: 12px;
  align-items: start;
}

.contact-list li + li {
  margin-top: 18px;
}

.contact-icon {
  width: 24px;
  height: 24px;
  margin-top: 2px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.contact-icon svg {
  width: 22px;
  height: 22px;
  fill: none;
  stroke: #a07346;
  stroke-width: 1.9;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.contact-text {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.contact-text strong {
  font-size: 14px;
  line-height: 1.45;
  font-weight: 900;
  letter-spacing: 0.04em;
  color: #6a4d3b;
}

.contact-text small {
  font-size: 12px;
  line-height: 1.45;
  font-weight: 700;
  letter-spacing: 0.04em;
  color: #9a8574;
}

/* 最底部 */
.footer-bottom {
  position: relative;
  z-index: 2;
  max-width: 1280px;
  margin: 34px auto 0;
  padding-top: 5px;
  border-top: 1px solid rgba(170, 132, 94, 0.1);
  text-align: center;
}

.footer-bottom p {
  margin: 0;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: #9a8574;
}

/* RWD */
@media (max-width: 1180px) {
  .footer-inner {
    grid-template-columns: 1fr;
    gap: 38px;
  }

  .footer-brand {
    text-align: center;
  }

  .footer-logo {
    justify-content: center;
  }

  .brand-desc {
    align-items: center;
  }

  .brand-line-sub,
  .brand-line-note {
    padding-left: 0;
  }

  .brand-line-main::after {
    left: 50%;
    transform: translateX(-50%) rotate(-2deg);
  }

  .footer-nav {
    max-width: 760px;
    width: 100%;
    margin: 0 auto;
  }

  .footer-contact-card {
    max-width: 520px;
    width: 100%;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .home-footer {
    padding: 44px 16px 22px;
  }

  .footer-nav {
    grid-template-columns: repeat(2, 1fr);
    gap: 34px 28px;
  }

  .logo-text strong {
    font-size: 28px;
  }

  .brand-desc {
    margin-top: 22px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .footer-logo {
    flex-direction: column;
    gap: 8px;
  }

  .logo-text {
    align-items: center;
  }

  .footer-nav {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .footer-column li + li {
    margin-top: 12px;
  }

  .footer-contact-card {
    padding: 24px 20px;
  }

  .contact-list li {
    grid-template-columns: 22px 1fr;
    gap: 10px;
  }
}
</style>
