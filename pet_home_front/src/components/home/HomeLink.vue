 <script setup>
import linkExplore from '@/assets/images/home/link-explore.jpg'
import linkDaily from '@/assets/images/home/link-daily.jpg'
import linkCare from '@/assets/images/home/link-care.jpg'
import linkForum from '@/assets/images/home/link-forum.jpg'

const linkItems = [
  {
    id: 'explore',
    className: 'link-explore',
    title: '探索附近',
    desc: '發現附近的優質寵物店、醫療院所與服務。',
    button: '立即探索',
    image: linkExplore,
    icon: 'map',
    textClass: 'text-explore',
    // 中文註解：目前尚未有商家探索頁，先導到論壇首頁，避免 RouterLink to 為 undefined。
    routePath: '/merchants',
    fadeColor: '#d9e9ec',
    routePath: '/petmap',
    imageBox: {
      x: 15,
      y: 20,
      width: 415,
      height: 220,
    },
    path: 'M28.9,117.8c.7-8.3,2.3-16.4,7.1-23,5.3-7.3,13.9-11.7,22.7-13.8,13-3,27.7-2.9,41-3.6s29.6-.9,44.4-2c37.2-2.8,73.6-10.9,110-18.8,36-7.9,71.8-13.7,108.4-18.3,15.5-1.9,43-7.8,48,0s5.8,14.7,0,30.8c-8,22.2-22.3,51.6-29.5,74-3.6,11.2-7.2,22.4-10.9,33.6s-4.9,21.4-11.1,28.9c-3.8,4.6-8.6,5.5-14.4,6.1-16.1,1.6-32.6,1-48.8.3-42.4-1.7-84.9-1.7-127.4,0-42.9,1.7-85.4,5.8-128.1,10.5-5.2.6-11,1-15.3-2-5.5-3.9-6.2-11.7-5-18.3,3.6-20,5.4-40.2,7.7-60.4.8-7.5.6-15.9,1.2-24h0Z',
  },
  {
    id: 'daily',
    className: 'link-daily',
    title: '補充日常',
    desc: '日常所需一站購足，安心又方便。',
    button: '逛逛商城',
    image: linkDaily,
    icon: 'bag',
    textClass: 'text-daily',
    routePath: '/products',
    fadeColor: '#f7efe4',
    routePath:'/shop',
    imageBox: {
      x: 390,
      y: 0,
      width: 385,
      height: 225,
    },
    path: 'M421.5,129.2c5.9-15.3,11.7-30.6,17.6-45.9s5.1-14.7,10.4-21c4.8-5.6,11-9.8,17.6-13,27.1-13.2,60.4-28.6,99.1-34.5,38.7-5.8,104.5-3.9,130.5-3.1s48.8,1.9,52.3,15.1c1.9,7.2-.2,16.2-1.3,23.4-1.9,12.6-4.4,25-7,37.4-4.2,19.8-8.3,39.7-11.7,59.6-2.2,13-1,29.5-10.1,40.2-9.5,11.3-32.7,11.3-46.4,12.3-85.4,6.1-171.1,9.7-256.7,9.7-14.27,0-18.8-2.1-18.8-10.8.2-10.4,5.8-20.6,9.4-30.2,4.2-11.1,8.5-22.2,12.7-33.3l2.3-6h.1v.1Z',
  },
  {
    id: 'care',
    className: 'link-care',
    title: '安排照護',
    desc: '洗澡、美容、照護服務，幫毛孩安排好。',
    button: '預約服務',
    image: linkCare,
    icon: 'calendar',
    textClass: 'text-care',
    routePath: '/wash',
    fadeColor: '#f7efe4',
    imageBox: {
      x: 0,
      y: 225,
      width: 470,
      height: 190,
    },
    path: 'M68.8,247.4c15-1.7,30-3.1,45-4.2,75.9-5.8,152.6-5.5,228.7-5.7,76.1-.2,14.2,0,21.2,0,33.5.5,50.1-4.2,56.3,5.6,6.2,9.9,14.5,42.9,19.6,62,3.6,13.2,7.5,26.4,11.4,39.5s3.5,13.1,5.5,19.5,7.5,15.6,4.1,24.3c-2.6,6.6-9.6,10.8-16.6,12-9.8,1.6-20.6.1-30.4,0-30.6-.6-61.3-1.4-91.9-2.5-20-.7-40.2-2.8-60.2-2.8-16-1.5-82.1-6.2-115.1-7.2s-87.2,1.5-100.1,0-20.1-6.2-25.8-20.1S2.4,296.1.9,282.6s1-23.2,10.8-26.8c14.1-5.2,31.4-5.2,46.2-7,3.6-.4,7.3-.9,10.9-1.3h0v-.1Z',
  },
  {
    id: 'forum',
    className: 'link-forum',
    title: '交流經驗',
    desc: '看看其他飼主怎麼照顧毛孩\n分享日常。',
    button: '進入論壇',
    image: linkForum,
    icon: 'chat',
    textClass: 'text-forum',
    routePath:"/community",
    fadeColor: '#d9e9ec',
    imageBox: {
      x: 455,
      y: 215,
      width: 350,
      height: 190,
    },
    path: 'M496.5,234.6c34.2-1.7,68.6-.4,102.7-3.7,26.8-2.6,94.5-7.2,118.2-8.8,23.7-1.5,49.5-3.6,60.4,6.2,10.8,9.8,7.7,27.4,8.3,43.4.5,14-.4,28-1,42-.5,11.8,1.1,24.5-.8,36.1-2.1,13.2-10.3,22.5-23.1,26.3-12.3,3.6-25.9,3.3-38.6,5.1-34.2,4.9-70,6.1-104.6,7.3-23.1.8-46.2.9-69.3.4s-23.4.5-34.6-1.2-12-7-15-13.7c-7.2-16-29.4-97-32.5-106.3-2.7-8.1-7.2-17.1-2.7-25.4s18-6.7,26.8-7.2c1.9-.1,3.9-.2,5.8-.3v-.2h0Z',
  },
]

function handleLinkClick(event) {
  const href = event.currentTarget.getAttribute('href')

  if (href === '#') {
    event.preventDefault()
  }
}
</script>

<template>
  <section class="pet-link-section">
    <div class="pet-link-inner">
      <div class="pet-link-heading">
        <div class="heading-kicker">生活的每一刻</div>

        <h2>我們都在你身邊</h2>
      </div>

      <div class="pet-link-board">
        <svg class="stamp-paw-badge link-stamp" viewBox="0 0 180 180" aria-hidden="true">
          <defs>
            <!-- 上方文字路徑 -->
            <path id="stampTextTop" d="M 36 93 A 54 54 0 0 1 144 88" />

            <!-- 下方文字路徑 -->
            <path id="stampTextBottom" d="M 144 96 A 54 54 0 0 1 36 85" />
          </defs>

          <!-- 外圈 -->
          <circle class="stamp-ring stamp-ring-outer" cx="90" cy="90" r="72" />

          <!-- 內圈 -->
          <circle class="stamp-ring stamp-ring-inner" cx="90" cy="90" r="48" />

          <!-- 上方文字 -->
          <text class="stamp-text">
            <textPath href="#stampTextTop" startOffset="50%" text-anchor="middle">
              FOR YOU, FOR THEM
            </textPath>
          </text>

          <!-- 下方文字 -->
          <text class="stamp-text">
            <textPath href="#stampTextBottom" startOffset="45%" text-anchor="middle">
              ALWAYS TOGETHER
            </textPath>
          </text>

          <!-- 中間狗掌拓印 -->
          <g class="stamp-paw">
            <ellipse cx="66" cy="78" rx="8" ry="11" transform="rotate(-28 66 78)" />
            <ellipse cx="82" cy="66" rx="8" ry="11" transform="rotate(-8 82 66)" />
            <ellipse cx="99" cy="66" rx="8" ry="11" transform="rotate(10 99 66)" />
            <ellipse cx="114" cy="79" rx="7.5" ry="10.5" transform="rotate(28 114 79)" />

            <path
              d="M62 110
       C63 94, 76 83, 90 84
       C104 83, 117 94, 118 110
       C119 121, 106 125, 91 117
       C75 125, 61 121, 62 110Z"
            />
          </g>

          <!-- 一點點仿印章缺口，不想要可以刪掉 -->
          <g class="stamp-noise">
            <path d="M43 48 C48 45, 52 44, 57 45" />
            <path d="M124 39 C130 41, 134 44, 137 49" />
            <path d="M41 130 C46 134, 52 136, 58 136" />
            <path d="M123 140 C129 138, 134 134, 138 129" />
          </g>
        </svg>
        <!-- slider 風格：手繪愛心 -->
        <svg class="floating-doodle doodle-heart" viewBox="0 0 80 70" aria-hidden="true">
          <path
            d="M40 58
       C28 47, 14 37, 15 24
       C16 14, 39 2, 40 37
       C51 12, 64 4, 65 24
       C66 37, 42 47, 40 58Z"
          />
        </svg>

        <!-- slider 風格：柔和腳印，大 -->
        <svg
          class="floating-doodle doodle-paw doodle-paw-large"
          viewBox="0 0 90 70"
          aria-hidden="true"
        >
          <ellipse cx="22" cy="28" rx="9" ry="12" transform="rotate(-28 22 28)" />
          <ellipse cx="38" cy="18" rx="9" ry="12" transform="rotate(-10 38 18)" />
          <ellipse cx="56" cy="17" rx="9" ry="12" transform="rotate(18 56 21)" />
          <ellipse cx="70" cy="24" rx="8" ry="11" transform="rotate(54 70 34)" />
          <ellipse cx="46" cy="48" rx="19" ry="18" transform="rotate(-8 86 38)" />
        </svg>

        <!-- 你的 SVG 框 + 圖片套入 clipPath -->
        <svg
          class="pet-link-svg"
          viewBox="0 0 810 416"
          preserveAspectRatio="xMidYMid meet"
          aria-hidden="true"
        >
          <defs>
            <template v-for="item in linkItems" :key="`defs-${item.id}`">
              <clipPath :id="`clip-${item.id}`">
                <path :d="item.path" />
              </clipPath>

              <linearGradient :id="`fade-${item.id}`" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" :stop-color="item.fadeColor" stop-opacity="0.96" />
                <stop offset="38%" :stop-color="item.fadeColor" stop-opacity="0.38" />
                <stop offset="24%" :stop-color="item.fadeColor" stop-opacity="0.18" />
                <stop offset="100%" :stop-color="item.fadeColor" stop-opacity="0" />
              </linearGradient>
            </template>
          </defs>

          <!-- 圖片疊層：襯底 → 米白外框 → 照片 → 漸層 -->
          <template v-for="item in linkItems" :key="`image-${item.id}`">
            <!-- 1. 最底層襯底：讓邊界不要被背景吃掉 -->
            <path class="shape-underlay" :class="`underlay-${item.id}`" :d="item.path" />

            <!-- 2. 米白厚外框：模擬 REF 那種照片外圍留白 -->
            <path class="shape-frame" :d="item.path" />

            <!-- 3. 真正照片：套進你的 SVG 不規則框 -->
            <image
              class="shape-image"
              :href="item.image"
              :x="item.imageBox.x"
              :y="item.imageBox.y"
              :width="item.imageBox.width"
              :height="item.imageBox.height"
              preserveAspectRatio="xMidYMid slice"
              :clip-path="`url(#clip-${item.id})`"
            />

            <!-- 4. 照片到文字區的漸層遮罩 -->
            <path class="shape-fade" :d="item.path" :fill="`url(#fade-${item.id})`" />
          </template>

          <!-- 你原本 SVG 裡面的實心裝飾線條 -->
          <path
            class="decor decor-orange"
            d="M393.5,175.1c7.1-19.1,17.6-48.1,24.7-66.9,4.8-11.9,13.5-39.2,20.6-49.5,3.2-5.1,7.1-9.8,11.7-13.8,4.5-4,9.8-7,15-10,5.2-2.9,10.5-5.7,15.9-8.2,6.1-2.8,12.3-5.5,18.6-7.9,3.9-1.3,10.9-3.5,14.9-4.8,17.1-5.2,34.6-9.2,52.3-11.7,5.9-.8,11.8-1.6,17.7-2.3.2,0,.2.3,0,.3-17.5,3.3-35,6.6-52.1,11.4-9.7,2.7-20.1,6.1-29.7,9.3-7,2.4-13.7,5.4-20.5,8.4-5.4,2.4-10.7,5.1-15.9,7.9-5.1,2.8-10.3,5.6-14.8,9.5-8.9,7.6-15.1,17.9-19.5,28.8l-13,33.2c-3.2,8.2-9.8,25-12.9,33.2l-12.9,33.3c0,.2-.4,0-.3-.1h.2q0-.07,0,0v-.1Z"
          />

          <path
            class="decor decor-blue"
            d="M255.3,39.9c13.7-3.7,27.5-6.7,41.5-9.1,10.2-1.5,21.4-2.9,31.5-4.7,10.4-1.9,21.1-3.2,31.6-4.1,16.9-1.3,37.2-3.5,52.9,4,3.2,1.6,6.1,4,8,7.1.1,0-.1.3-.2.2-2.3-2.7-5.2-4.6-8.4-5.9-9.8-3.8-20.5-3.6-30.9-3.5-7,.2-14.1.5-21.1.9-12.5.8-30,1.4-42.1,4-5.9,1.1-14.8,3.2-20.7,4.5-6.9,1.5-13.9,2.7-20.8,3.9-7,1.2-14,2.2-21,3.1-.2,0-.2-.3,0-.3h-.3v-.1h0Z"
          />

          <path
            class="decor decor-brown"
            d="M80.4,398.2c19.1,2.8,38.5,4.1,57.8,3.1,1.1,0,2.7-.2,3.8-.1,1.3.1,2.5,1,3.6,1.5,9.4,4.6,19.8,6.3,30,8,12.7,2,25.6,3,38.5,3.6,25.8,1.1,51.5-2.8,77.3-3.9,19.4-1.1,38.8,0,58.1,1.5,12.9,1,25.9,1.5,38.7.7-12.7,2-25.9,1.4-38.8.9-16.1-.8-32.2-1.7-48.3-1.3-22.6.5-45,2.9-67.7,3.2-12.9.1-25.8-.7-38.7-1.8-12.9-1-25.8-2.2-38.4-5.2-3.9-1-7.9-2.3-11.5-4.2-1.4-.7-2.4-1.5-4.1-1.3-7.3.3-14.6.4-21.8.1-12.7-.8-26.7-1.8-38.4-4.9h0q0,.07,0,0l-.1.1Z"
          />

          <path
            class="decor decor-blue"
            d="M704.5,213.9c17.7-2.5,35.4-4.9,53.3-5.6,9,0,18.4-.6,26.9,3.1,3,1.3,6.1,2.9,8.3,5.5,6.6,7.7,5.6,18.6,5.5,27.9v10.7c0,10.3-.4,21.7-.7,32.1,0,3.6-.3,7.1-.4,10.7-.6-17.8-1.2-35.6-1.5-53.5-.2-9.6,1.1-22.2-7.8-28.2-5.8-3.8-12.7-5.2-19.6-5.4-20.5-.9-43.3,1.6-63.9,2.7h-.1Z"
          />
        </svg>

        <!-- 文字連結層 -->
        <RouterLink
          v-for="item in linkItems"
          :key="`text-${item.id}`"
          href="#"
          :to="item.routePath"
          class="pet-link-text"
          :class="[item.textClass, item.className]"
          @click="handleLinkClick"
          >

          <div class="text-panel">
            <h3>
              <span class="title-icon">
                <svg v-if="item.icon === 'map'" viewBox="0 0 24 24" aria-hidden="true">
                  <path d="M12 21s6-5.3 6-11a6 6 0 0 0-12 0c0 5.7 6 11 6 11z" />
                  <circle cx="12" cy="10" r="2.2" />
                </svg>

                <svg v-if="item.icon === 'bag'" viewBox="0 0 24 24" aria-hidden="true">
                  <path d="M6 8h12l-1 12H7L6 8z" />
                  <path d="M9 8V6a3 3 0 0 1 6 0v2" />
                </svg>

                <svg v-if="item.icon === 'calendar'" viewBox="0 0 24 24" aria-hidden="true">
                  <rect x="4" y="5" width="16" height="15" rx="3" />
                  <path d="M8 3v4" />
                  <path d="M16 3v4" />
                  <path d="M4 10h16" />
                </svg>

                <svg v-if="item.icon === 'chat'" viewBox="0 0 24 24" aria-hidden="true">
                  <path
                    d="M5 6.5c0-2 1.7-3.5 3.8-3.5h6.4C17.3 3 19 4.5 19 6.5v4.8c0 2-1.7 3.5-3.8 3.5H11l-4.5 4v-4.1C4.9 14.2 4 12.9 4 11.3V6.5z"
                  />
                  <circle cx="8.5" cy="9" r="0.7" />
                  <circle cx="12" cy="9" r="0.7" />
                  <circle cx="15.5" cy="9" r="0.7" />
                </svg>
              </span>

              {{ item.title }}
            </h3>

            <p>{{ item.desc }}</p>

            <span class="link-button">
              <span class="button-text">{{ item.button }}</span>

              <span class="button-arrow" aria-hidden="true"> → </span>
            </span>
          </div>
        </RouterLink>
      </div>
    </div>
  </section>
</template>

<style scoped lang="scss">
.pet-link-section {
  --bg: #fff8ef;
  --cream: #f7efe4;
  --brown: #5e4332;
  --brown-soft: #ae8d76;
  --orange: #d99a6c;
  --blue: #cfe7ee;
  --blue-deep: #87bbca;
  --green-blue: #6f9b98;

  position: relative;
  padding: 70px 8% 90px;
  background: transparent;
  overflow: hidden;

  .pet-link-inner {
    max-width: 1180px;
    margin: 0 auto;
  }

  .pet-link-heading {
    margin-bottom: 24px;

    .heading-kicker {
      margin-bottom: 6px;
      color: #d99a6c;
      font-family: 'Noto Serif TC', 'Noto Sans TC', serif;
      font-size: 18px;
      font-weight: 700;
      line-height: 1.4;
      letter-spacing: 0.14em;
    }

    h2 {
      display: inline-flex;
      align-items: center;
      gap: 12px;
      margin: 0;
      color: #5e4332;

      font-family: 'Noto Serif TC', 'Noto Sans TC', serif;
      font-size: 36px;
      font-weight: 800;
      line-height: 1.35;
      letter-spacing: 0.08em;
    }
  }

  .pet-link-board {
    position: relative;
    width: 100%;
    aspect-ratio: 780 / 416;
    min-height: 520px;
  }

  .link-stamp {
    position: absolute;
    right: -12%;
    top: 2%;
    z-index: 2;
    pointer-events: none;
  }

  .stamp-paw-badge {
    width: 126px;
    height: 126px;
    display: block;
    overflow: visible;
    transform: rotate(30deg);
    opacity: 0.82;

    .stamp-ring {
      fill: none;
      stroke: #d49b6a;
      stroke-linecap: round;
      stroke-linejoin: round;
    }

    .stamp-ring-outer {
      stroke-width: 3.2;
    }

    .stamp-ring-inner {
      stroke-width: 2;
      opacity: 0.85;
    }

    .stamp-text {
      fill: #c9895f;
      font-size: 11px;
      font-weight: 800;
      letter-spacing: 2.2px;
    }

    .stamp-paw {
      fill: #d49b6a;
      opacity: 0.78;
      transform: scale(0.8);
      transform-origin: center;
      transform-box: fill-box;
    }

    .stamp-noise {
      fill: none;
      stroke: #d49b6a;
      stroke-width: 2;
      stroke-linecap: round;
      opacity: 0.35;
    }
  }

  .pet-link-svg {
    position: absolute;
    inset: 0;
    z-index: 1;
    width: 100%;
    height: 100%;
    filter: drop-shadow(0 20px 30px rgba(112, 78, 49, 0.12));

    .shape-underlay {
      opacity: 0.9;
      transform-box: fill-box;
      transform-origin: center;
      pointer-events: none;
      filter: blur(2px);
    }

    .underlay-explore {
      fill: #f2dfcb;
      transform: translate(-8px, px);
    }

    .underlay-daily {
      fill: #f2dfcb;
      transform: translate(8px, 14px);
    }

    .underlay-care {
      fill: #f2dfcb;
      transform: translate(-8px, 7px);
    }

    .underlay-forum {
      fill: #f2dfcb;
      transform: translate(8px, 12px);
    }

    .shape-frame {
      fill: #f7efe4;
      transform: scale(1.01);
      transform-box: fill-box;
      transform-origin: center;
      opacity: 0.8;
      pointer-events: none;
    }

    .shape-image {
      transition: transform 0.35s ease;
      transform-box: fill-box;
      transform-origin: center;
    }

    .shape-fade {
      pointer-events: none;
    }

    .decor {
      stroke: none;
      opacity: 0.22;
      pointer-events: none;
    }

    .decor-orange {
      fill: var(--orange);
    }

    .decor-blue {
      fill: var(--blue-deep);
    }

    .decor-brown {
      fill: var(--brown-soft);
    }
  }

  .floating-doodle {
    position: absolute;
    z-index: 2;
    pointer-events: none;
  }

  /* 愛心：像 slider 那種淡橘色手繪線 */
  .doodle-heart {
    width: 48px;
    left: -7%;
    top: 5%;
    fill: none;
    stroke: rgba(224, 159, 137, 0.55);
    stroke-width: 5;
    stroke-linecap: round;
    stroke-linejoin: round;
    transform: rotate(-22deg) scale(1.2);
  }

  /* 腳印共用：不要 stroke，要柔和填色 */
  .doodle-paw {
    fill: rgba(184, 160, 136, 0.45);
    stroke: none;
  }

  /* 大腳印 */
  .doodle-paw-large {
    width: 78px;
    left: -20%;
    bottom: 3%;
    transform: rotate(30deg) scale(1.2);
    // filter: blur(2px);
  }

  .pet-link-text {
    position: absolute;
    z-index: 4;
    display: block;
    color: var(--brown);
    text-decoration: none;
    transition: transform 0.25s ease;

    &::before {
      content: '';
      position: absolute;
      inset: -18px -36px -18px -18px;
      z-index: -1;
      border-radius: 999px;
      background: linear-gradient(
        90deg,
        rgba(247, 239, 228, 0.88) 0%,
        rgba(247, 239, 228, 0.62) 52%,
        rgba(247, 239, 228, 0) 100%
      );
      filter: blur(10px);
      pointer-events: none;
    }
  }

  .text-explore::before,
  .text-forum::before {
    background: linear-gradient(
      90deg,
      rgba(217, 233, 236, 0.92) 0%,
      rgba(217, 233, 236, 0.68) 48%,
      rgba(217, 233, 236, 0.24) 76%,
      rgba(217, 233, 236, 0) 100%
    );
  }

  .pet-link-text:active {
    .link-button {
      transform: translateY(1px) scale(0.98);
      box-shadow: 0 5px 10px rgba(94, 67, 50, 0.12);
    }
  }

  .text-panel {
    position: relative;
    padding: 0;
    border-radius: 0;
    background: transparent;
    backdrop-filter: none;
    box-shadow: none;

    h3 {
      display: flex;
      align-items: center;
      gap: 9px;
      margin: 0 0 8px;
      color: #5e4332;
      font-size: 21px;
      font-weight: 800;
      line-height: 1.3;
      letter-spacing: 0.04em;
      text-shadow: 0 1px 0 rgba(255, 248, 239, 0.8);
    }

    p {
      margin: 0 0 12px;
      color: #6f5a4a;
      font-size: 14px;
      line-height: 1.55;
      text-shadow: 0 1px 0 rgba(255, 248, 239, 0.7);
    }
  }

  .title-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    flex: 0 0 29px;
    width: 29px;
    height: 29px;
    border-radius: 50%;
    background: rgba(207, 231, 238, 0.75);
    color: var(--green-blue);

    svg {
      width: 18px;
      height: 18px;
      fill: none;
      stroke: currentColor;
      stroke-width: 1.9;
      stroke-linecap: round;
      stroke-linejoin: round;

      circle[cx='8.5'],
      circle[cx='12'],
      circle[cx='15.5'] {
        fill: currentColor;
        stroke: none;
      }
    }
  }

  .link-button {
    position: relative;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: fit-content;
    min-width: 96px;
    min-height: 34px;
    padding: 0 17px;
    border-radius: 999px;
    overflow: hidden;

    background: var(--green-blue);
    color: #fffdf8;

    font-size: 13px;
    font-weight: 800;
    letter-spacing: 0.05em;

    box-shadow: 0 6px 14px rgba(94, 67, 50, 0.12);
    transition:
      transform 0.25s ease,
      box-shadow 0.25s ease,
      padding 0.25s ease,
      background-color 0.25s ease;

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      background: linear-gradient(
        120deg,
        transparent 0%,
        rgba(255, 255, 255, 0.38) 45%,
        transparent 75%
      );
      transform: translateX(-120%);
      transition: transform 0.55s ease;
    }

    .button-text {
      position: relative;
      z-index: 1;
      display: block;
      text-align: center;
    }

    .button-arrow {
      position: absolute;
      z-index: 1;
      right: 13px;
      top: 50%;
      transform: translate(-6px, -50%);
      opacity: 0;
      transition:
        transform 0.25s ease,
        opacity 0.25s ease;
    }
  }

  .text-explore {
    left: 6%;
    top: 30%;
    width: 25%;

    .link-button {
      background: #6d9daa;
    }
  }

  .text-daily {
    left: 55%;
    top: 22%;
    width: 24%;

    .link-button {
      background: var(--orange);
    }
  }

  .text-care {
    left: 7%;
    top: 65%;
    width: 28%;

    .link-button {
      background: var(--brown-soft);
    }
  }

  .text-forum {
    left: 63%;
    top: 65%;
    width: 24%;
    white-space: pre-line;

    .link-button {
      background: #6d9daa;
    }
  }
}

@media (max-width: 900px) {
  .pet-link-section {
    padding: 58px 6% 72px;

    .pet-link-heading h2 {
      font-size: 28px;
    }

    .pet-link-board {
      aspect-ratio: auto;
      min-height: auto;
      display: grid;
      grid-template-columns: 1fr;
      gap: 18px;
    }

    .pet-link-svg,
    .floating-doodle {
      display: none;
    }

    .pet-link-text {
      position: relative;
      inset: auto;
      width: 100%;
      min-height: 220px;
      padding: 28px;
      border-radius: 34px;
      background-size: cover;
      background-position: center;
      overflow: hidden;
      box-shadow: 0 18px 34px rgba(112, 78, 49, 0.12);

      &::before {
        content: '';
        position: absolute;
        inset: 0;
        background: linear-gradient(
          90deg,
          rgba(247, 239, 228, 0.96) 0%,
          rgba(247, 239, 228, 0.78) 58%,
          rgba(247, 239, 228, 0.12) 100%
        );
      }

      .text-panel {
        position: relative;
        z-index: 1;
        width: min(360px, 75%);
      }
    }

    .text-explore {
      background-image: url('@/assets/images/home/link-explore.jpg');
    }

    .text-daily {
      background-image: url('@/assets/images/home/link-daily.jpg');
    }

    .text-care {
      background-image: url('@/assets/images/home/link-care.jpg');
    }

    .text-forum {
      background-image: url('@/assets/images/home/link-forum.jpg');
    }
  }
}

@media (max-width: 520px) {
  .pet-link-section {
    .pet-link-text {
      padding: 22px;

      .text-panel {
        width: 82%;
      }
    }

    .text-panel {
      h3 {
        font-size: 19px;
      }

      p {
        font-size: 13px;
      }
    }
  }
}
</style>
