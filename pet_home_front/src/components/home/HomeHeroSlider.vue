<script setup>
import { onMounted, onUnmounted, ref } from 'vue'

import heroPet01 from '@/assets/images/home/hero-pet-01.png'
import heroPet02 from '@/assets/images/home/hero-pet-02.png'
import heroPet03 from '@/assets/images/home/hero-pet-03.png'

const currentIndex = ref(0)

const slides = [
  {
    label: 'PET HOME',
    title: {
      main: '每一天，都值得',
      sub: '和毛孩好好生活',
    },
    description: '我們把需要的服務與資訊整理在一起，讓你和毛孩的日常更簡單、更安心。',
    image: heroPet01,
  },
  {
    label: 'PET SERVICE',
    title: {
      main: '附近服務，一次整理',
      sub: '照顧毛孩更輕鬆',
    },
    description: '快速查看附近商家、照護服務與生活資訊，讓日常安排更有效率。',
    image: heroPet02,
  },
  {
    label: 'PET MEMBER',
    title: {
      main: '會員資料整合',
      sub: '紀錄毛孩生活',
    },
    description: '登入後可以管理個人資料、寵物資訊與常用服務，打造專屬的毛孩生活入口。',
    image: heroPet03,
  },
]

const nextSlide = () => {
  currentIndex.value = (currentIndex.value + 1) % slides.length
}

const prevSlide = () => {
  currentIndex.value = currentIndex.value === 0 ? slides.length - 1 : currentIndex.value - 1
}

const goToSlide = (index) => {
  currentIndex.value = index
}

let timer = null

onMounted(() => {
  timer = window.setInterval(nextSlide, 4500)
})

onUnmounted(() => {
  if (timer) {
    window.clearInterval(timer)
  }
})
</script>

<template>
  <section class="hero-slider">
    <!-- 手繪裝飾 -->
    <div class="hero-doodles" aria-hidden="true">
      <!-- 愛心 -->
      <svg
        class="doodle doodle-heart doodle-heart-1"
        viewBox="0 0 80 70"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          d="M40 58
       C28 47, 14 37, 15 24
       C16 14, 39 2, 40 37
       C51 12, 64 4, 65 24
       C66 37, 42 47, 40 58Z"
          fill="none"
          stroke="currentColor"
          stroke-width="5"
          stroke-linecap="round"
          stroke-linejoin="round"
        ></path>
      </svg>

      <!-- 手繪愛心 2 -->
      <svg
        class="doodle doodle-heart doodle-heart-2"
        viewBox="0 0 80 70"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          d="M40 58
       C28 47, 14 37, 15 24
       C16 14, 39 2, 40 37
       C51 12, 64 4, 65 24
       C66 37, 42 47, 40 58Z"
          fill="none"
          stroke="currentColor"
          stroke-width="4.5"
          stroke-linecap="round"
          stroke-linejoin="round"
        ></path>
      </svg>

      <!-- 腳掌印 1：比較明顯 -->
      <svg
        class="doodle doodle-paw doodle-paw-1"
        viewBox="0 0 90 82"
        xmlns="http://www.w3.org/2000/svg"
      >
        <ellipse cx="44" cy="54" rx="20" ry="16" fill="currentColor"></ellipse>
        <ellipse
          cx="20"
          cy="31"
          rx="9"
          ry="12"
          fill="currentColor"
          transform="rotate(-20 20 31)"
        ></ellipse>
        <ellipse
          cx="38"
          cy="18"
          rx="9"
          ry="13"
          fill="currentColor"
          transform="rotate(-5 38 18)"
        ></ellipse>
        <ellipse
          cx="58"
          cy="18"
          rx="9"
          ry="13"
          fill="currentColor"
          transform="rotate(8 58 18)"
        ></ellipse>
        <ellipse
          cx="74"
          cy="32"
          rx="9"
          ry="12"
          fill="currentColor"
          transform="rotate(22 74 32)"
        ></ellipse>
      </svg>

      <!-- 腳掌印 2：小一點 -->
      <svg
        class="doodle doodle-paw doodle-paw-2"
        viewBox="0 0 90 82"
        xmlns="http://www.w3.org/2000/svg"
      >
        <ellipse cx="44" cy="54" rx="20" ry="16" fill="currentColor"></ellipse>
        <ellipse
          cx="20"
          cy="31"
          rx="9"
          ry="12"
          fill="currentColor"
          transform="rotate(-20 20 31)"
        ></ellipse>
        <ellipse
          cx="38"
          cy="18"
          rx="9"
          ry="13"
          fill="currentColor"
          transform="rotate(-5 38 18)"
        ></ellipse>
        <ellipse
          cx="58"
          cy="18"
          rx="9"
          ry="13"
          fill="currentColor"
          transform="rotate(8 58 18)"
        ></ellipse>
        <ellipse
          cx="74"
          cy="32"
          rx="9"
          ry="12"
          fill="currentColor"
          transform="rotate(22 74 32)"
        ></ellipse>
      </svg>

      <!-- 手繪小閃光 -->
      <svg class="doodle doodle-spark" viewBox="0 0 70 70" xmlns="http://www.w3.org/2000/svg">
        <path
          d="M35 8C39 24 45 31 62 35C45 39 39 46 35 62C31 46 25 39 8 35C25 31 31 24 35 8Z"
          fill="none"
          stroke="currentColor"
          stroke-width="4"
          stroke-linejoin="round"
        ></path>
      </svg>
    </div>
    <button
      class="slider-arrow slider-arrow-left"
      type="button"
      aria-label="上一張"
      @click="prevSlide"
    >
      ‹
    </button>

    <div class="hero-inner">
      <div class="hero-content">
        <p class="hero-label">
          {{ slides[currentIndex].label }}
        </p>

        <h1 class="hero-title">
          <span class="title-line title-main">
            {{ slides[currentIndex].title.main }}
          </span>

          <span class="title-line title-sub">
            {{ slides[currentIndex].title.sub }}
          </span>
        </h1>

        <p class="hero-description">
          {{ slides[currentIndex].description }}
        </p>

        <div class="hero-actions">
          <a href="#service-links" class="primary-btn">
            <svg
              class="btn-location-icon"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
              aria-hidden="true"
            >
              <path
                d="M12 21C12 21 5.5 14.9 5.5 9.8C5.5 6.2 8.4 3.5 12 3.5C15.6 3.5 18.5 6.2 18.5 9.8C18.5 14.9 12 21 12 21Z"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              ></path>
              <circle
                cx="12"
                cy="9.8"
                r="2.3"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              ></circle>
            </svg>
            探索附近商家
          </a>

          <a href="#web-intro" class="secondary-btn"> 了解更多 </a>
        </div>

        <div class="slider-dots" aria-label="切換輪播">
          <button
            v-for="(_, index) in slides"
            :key="index"
            class="dot"
            :class="{ active: currentIndex === index }"
            type="button"
            :aria-label="`第 ${index + 1} 張`"
            @click="goToSlide(index)"
          ></button>
        </div>
      </div>

      <div class="hero-visual">
        <!-- 米色幾何背景：只保留這一個背景 -->
        <svg
          class="beige-bg-svg"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 10 530 420"
          preserveAspectRatio="xMidYMid meet"
        >
          <path
            class="beige-bg-path"
            d="M88.4,73.7C163.4,10.7,283.4-1.3,358.4,52.7c81,57,105,171,42,243s-124.6,33.7-170.5-87.4c-5.7-14.9-19.9-24.9-35.9-25-142.3-.6-182-44.6-105.7-109.6Z"
          ></path>
        </svg>

        <!-- 主圖片：藍色幾何 path 拿來當圖片邊框 -->
        <div class="image-panel">
          <svg
            class="image-panel-svg"
            xmlns="http://www.w3.org/2000/svg"
            viewBox="227 40 1103 820"
            preserveAspectRatio="xMidYMid slice"
          >
            <defs>
              <clipPath id="heroImagePanelClip" clipPathUnits="userSpaceOnUse">
                <path
                  id="heroImageShape"
                  d="M543,187c76.3-50.1,166.7-75.2,260-83.2,29.7-2.5,59.6-3,89.4-.9,12.2.9,24.5,2.2,36.6,4.1,68.3,10.5,70.9,26.4,148,38.9,33.8,5.5,79.6-34,131-39s115.1,24.1,145,43.2v684c-180-30-279,14.9-419-9.1-160-26-499.5,62.2-609.5-7.8-21.4-61.5-11.1-135.8-2-198.6,18-125.1,46.7-252.7,130.2-351.6,26.9-31.8,57.3-58.3,90.4-80Z"
                ></path>
              </clipPath>
            </defs>

            <!-- 固定的外框陰影：這個不要動 -->
            <path
              class="image-shadow-shape"
              d="M543,187c76.3-50.1,166.7-75.2,260-83.2,29.7-2.5,59.6-3,89.4-.9,12.2.9,24.5,2.2,36.6,4.1,68.3,10.5,70.9,26.4,148,38.9,33.8,5.5,79.6-34,131-39s115.1,24.1,145,43.2v684c-180-30-279,14.9-419-9.1-160-26-499.5,62.2-609.5-7.8-21.4-61.5-11.1-135.8-2-198.6,18-125.1,46.7-252.7,130.2-351.6,26.9-31.8,57.3-58.3,90.4-80Z"
            ></path>

            <g clip-path="url(#heroImagePanelClip)">
              <image
                class="hero-slide-image"
                :key="currentIndex"
                :href="slides[currentIndex].image"
                x="-170"
                y="-25"
                width="1500"
                height="960"
                preserveAspectRatio="xMidYMid slice"
              ></image>
            </g>
          </svg>
        </div>

        <div class="paw-mark">🐾</div>
      </div>
    </div>

    <button
      class="slider-arrow slider-arrow-right"
      type="button"
      aria-label="下一張"
      @click="nextSlide"
    >
      ›
    </button>

    <!-- 底部波浪 -->
    <div class="wave-divider">
      <svg
        class="hero-wave-svg"
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 420 960 120"
        preserveAspectRatio="none"
      >
        <path
          class="wave-brown"
          d="M0,462.2l25.7-2.8c26.6,10.5,81,.6,134.3,10.6s106.7,9,160,3.8c53.3-5.1,127-10.4,177.4-14.7,114.6-9.9,171.5,3.8,203.7,10.4s51.1,8.7,71.5,9.7c22.4,1.7,10.4-13.4,27.5-15.9,42.3,1.1,43.7,14.2,72.7,5.4,12.6-3.8-4.2-9.1,28.7-9.4l58.6,10.6v71.1H0v-78.8Z"
        ></path>

        <path
          class="wave-blue"
          d="M0,452l26.7-1.3c26.6-1.4,80-4,133.3-4.5s106.7,1.1,160,6.3s106.7,13.8,160,23.7c53.3,9.8,106.7,20.8,160,18.6,53.3-2.1,91.6-1.8,140.9-22.7,20.1-8.5,43.7-17.5,65.1-22.4,18.5-4.3,29.2,3.8,46.8,10.9l67.2,40.4v40H0v-89Z"
        ></path>

        <path
          class="wave-cream"
          d="M960,478v62.1H0v-72l26.7-2.5c26.6-2.5,80.6-11.4,133.3-20,25.4-4.1,50.9-8.3,76.6-9.9,25.5-1.6,52-1.8,76.8,4.6,24.4,6.2,47.5,16.7,71.2,24.9,14.4,5,28.6,9.3,43.5,12.3,15.6,3.1,31.3,6,47,8.6,29.6,5,59.1,9.5,89.2,9.1,24.5-.3,48.8-3.4,72.9-6.9,23.1-3.4,46.1-6.8,68.7-12.5,25.8-6.5,49.9-16.9,76.5-20.6s52.4-3.4,78.4.2c22.1,3,43.8,8.6,65.2,14.5,10.4,2.9,23,8.2,33.8,8.2h.2Z"
        ></path>
      </svg>
    </div>
  </section>
</template>

<style scoped lang="scss">
.hero-slider {
  position: relative;
  min-height: calc(100vh - 76px);
  padding: 0;
  background:
    radial-gradient(circle at 8% 55%, rgba(224, 234, 235, 0.64), transparent 20%),
    linear-gradient(135deg, #fffaf3 0%, #f8efe3 100%);
  overflow: hidden;

  .hero-doodles {
    position: absolute;
    inset: 0;
    z-index: 6;
    pointer-events: none;
  }

  .doodle {
    position: absolute;
    display: block;
    filter: drop-shadow(0 6px 10px rgba(107, 79, 63, 0.06));
  }

  /* 愛心 */
  .doodle-heart {
    color: #d99b8f;
    opacity: 0.62;
  }

  .doodle-heart-1 {
    width: 54px;
    height: 254px;
    left: clamp(242px, 5vw, 92px);
    top: 22%;
    transform: rotate(-30deg);
  }

  .doodle-heart-2 {
    width: 34px;
    height: 34px;
    right: 26%;
    top: 8%;
    transform: rotate(14deg);
    opacity: 0.45;
  }

  /* 腳掌印：顏色加深，不然會看不到 */
  .doodle-paw {
    color: #9a6f4f;
  }

  .doodle-paw-1 {
    width: 230px;
    height: 60px;
    right: 53%;
    bottom: 16%;
    opacity: 0.34;
    transform: rotate(-22deg);
  }

  .doodle-paw-2 {
    width: 206px;
    height: 42px;
    right: 51%;
    bottom: 20%;
    opacity: 0.34;
    transform: rotate(20deg);
  }

  /* 小閃光 */
  .doodle-spark {
    width: 280px;
    height: 44px;
    left: clamp(320px, 29vw, 520px);
    top: 60%;
    color: #9fb8c9;
    opacity: 0.5;
    transform: rotate(10deg);
  }
}

.hero-inner {
  position: relative;
  z-index: 4;
  width: 100%;
  min-height: calc(100vh - 76px);
  display: flex;
  align-items: center;
}

/* 左側文字 */
.hero-content {
  position: relative;
  z-index: 8;
  width: 42%;
  max-width: 620px;
  padding-left: clamp(300px, 8vw, 150px);
  padding-bottom: 80px;

  .hero-title {
    margin: 0 0 24px;
    color: #3f332b;
    font-family: 'Noto Serif TC', 'Noto Sans TC', serif;
    line-height: 1.18;

    .title-line {
      display: block;
      white-space: nowrap;
      word-break: keep-all;
      overflow-wrap: normal;
    }

    .title-main {
      font-size: clamp(36px, 2.8vw, 52px);
      letter-spacing: 0.12em;
      font-weight: 600;
    }

    .title-sub {
      margin-top: 12px;
      padding-left: 42px;
      font-size: clamp(46px, 4vw, 72px);
      letter-spacing: 0.03em;
      font-weight: 900;
    }
  }
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 34px;

  a {
    height: 48px;
    padding: 0 26px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    border-radius: 999px;
    text-decoration: none;
    font-family: 'Noto Sans TC', 'Microsoft JhengHei', sans-serif;
    font-size: 15px;
    font-weight: 800;
    letter-spacing: 0.04em;
    transition:
      transform 0.2s ease,
      box-shadow 0.2s ease,
      background 0.2s ease;
  }

  .primary-btn {
    background: #3f6f91;
    color: #ffffff;
    box-shadow: 0 14px 28px rgba(63, 111, 145, 0.22);

    &:hover {
      background: #315c79;
      transform: translateY(-2px);
    }
  }

  .secondary-btn {
    background: rgba(255, 255, 255, 0.74);
    color: #8a6a52;
    border: 1px solid rgba(138, 106, 82, 0.2);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 14px 28px rgba(107, 79, 63, 0.1);
    }
  }
}

.slider-dots {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-left: 8px;

  .dot {
    width: 10px;
    height: 10px;
    padding: 0;
    border: 1px solid rgba(107, 79, 63, 0.35);
    border-radius: 50%;
    background: transparent;
    cursor: pointer;

    &.active {
      background: #6b3f26;
      border-color: #6b3f26;
    }
  }
}

/* 右側圖片區 */
.hero-visual {
  position: absolute;
  z-index: 5;
  top: 0;
  right: 0;
  bottom: 0;

  /* 讓右側圖片區真的吃到約 60% 以上 */
  width: 55vw;
  min-width: 1040px;

  pointer-events: auto;
}

.hero-slide-image {
  transition: transform 0.25s ease;
  transform-box: fill-box;
  transform-origin: center;
}

/* 米色背景幾何 */
.beige-bg-svg {
  position: absolute;
  z-index: 2;
  top: 104px;
  left: 120px;
  width: 360px;
  height: 270px;
  display: block;
  pointer-events: none;
  transform: rotate(-8deg);

  filter: drop-shadow(0 18px 32px rgba(107, 79, 63, 0.1));
}

.beige-bg-path {
  fill: #9fb8c9;
  opacity: 0.45;
  isolation: isolate;
}

/* 主圖片：藍色幾何 path 已經放進 clipPath，這裡不需要再畫藍色底 */
.image-panel {
  position: absolute;
  z-index: 5;
  top: 56px;
  right: 0;
  width: 100%;
  height: calc(100% - 128px);
  pointer-events: auto;
}

.image-panel-svg {
  display: block;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

/* 固定外框陰影 */
.image-shadow-shape {
  fill: rgba(255, 250, 243, 0.01);
  filter: drop-shadow(0 24px 48px rgba(107, 79, 63, 0.1))
    drop-shadow(0 6px 14px rgba(107, 79, 63, 0.06));
  transition:
    filter 0.25s ease,
    transform 0.25s ease;
}

.image-panel-svg:hover .image-shadow-shape {
  filter: drop-shadow(0 28px 56px rgba(107, 79, 63, 0.12))
    drop-shadow(0 8px 18px rgba(107, 79, 63, 0.07));
}

.image-panel-svg:hover .hero-slide-image {
  transform: scale(1.02);
}

.btn-location-icon {
  width: 18px;
  height: 18px;
  flex: 0 0 auto;
  color: currentColor;
}

/* 小腳印 */
.paw-mark {
  position: absolute;
  z-index: 8;
  right: 20%;
  bottom: 118px;
  color: rgba(75, 54, 43, 0.45);
  font-size: 30px;
  transform: rotate(-18deg);
}

/* 左右箭頭 */
.slider-arrow {
  position: absolute;
  z-index: 10;
  top: 54%;
  width: 54px;
  height: 54px;
  border: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  color: #9a765c;
  font-size: 34px;
  line-height: 1;
  box-shadow: 0 12px 26px rgba(107, 79, 63, 0.12);
  cursor: pointer;
  transform: translateY(-50%);
  transition:
    transform 0.2s ease,
    background 0.2s ease;

  &:hover {
    background: #ffffff;
    transform: translateY(-50%) scale(1.05);
  }
}

.slider-arrow-left {
  left: 40px;
}

.slider-arrow-right {
  right: 32px;
}

/* 底部波浪 */
.wave-divider {
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  height: 220px;
  z-index: 7;
  pointer-events: none;
  overflow: hidden;

  .hero-wave-svg {
    display: block;
    width: 100%;
    height: 100%;
  }

  .wave-blue {
    fill: #d6c0a5;
    opacity: 0.95;
  }

  .wave-brown {
    fill: #cad6de;
    opacity: 0.95;
  }

  .wave-cream {
    fill: rgb(255, 249, 241);
  }
}

/* RWD */
@media (max-width: 1180px) {
  .hero-content {
    width: 44%;
    padding-left: 64px;
  }

  .hero-visual {
    width: 72vw;
    min-width: 860px;
  }
}

@media (max-width: 960px) {
  .hero-slider {
    min-height: auto;
    .doodle-heart {
      left: 26px;
      top: 110px;
      width: 42px;
      height: 42px;
    }

    .doodle-bone,
    .doodle-spark {
      display: none;
    }

    .doodle-paw-1 {
      right: 18%;
      bottom: 170px;
      width: 42px;
      height: 42px;
    }

    .doodle-paw-2 {
      right: 10%;
      bottom: 230px;
      width: 32px;
      height: 32px;
    }
  }

  .hero-inner {
    min-height: auto;
    display: block;
    padding: 56px 24px 0;
  }

  .hero-content {
    width: 100%;
    max-width: 640px;
    margin: 0 auto;
    padding: 0 0 24px;
    text-align: center;

    .hero-description {
      margin-inline: auto;
    }
  }

  .hero-actions,
  .slider-dots {
    justify-content: center;
  }

  .hero-visual {
    position: relative;
    width: 100vw;
    min-width: 0;
    height: 430px;
    margin-left: calc(50% - 50vw);
  }

  .beige-bg-svg {
    top: 24px;
    left: 36px;
    width: 230px;
    height: 170px;
  }

  .image-panel {
    top: 0;
    height: 420px;
  }

  .wave-divider {
    height: 150px;
  }

  .slider-arrow {
    display: none;
  }
}

@media (max-width: 560px) {
  .hero-content {
    h1 {
      font-size: 34px;
    }

    .hero-description {
      font-size: 16px;
    }
  }

  .hero-actions {
    flex-direction: column;

    a {
      width: 100%;
      max-width: 260px;
    }
  }

  .hero-visual {
    height: 340px;
  }

  .image-panel {
    height: 330px;
  }

  .wave-divider {
    height: 120px;
  }
}
</style>
