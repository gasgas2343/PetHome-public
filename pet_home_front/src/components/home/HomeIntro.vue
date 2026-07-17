<!-- src/components/HomeIntro.vue -->
<script setup>
const introItems = [
  {
    title: '安心嚴選',
    desc: ['嚴格確認商家與商品', '品質有保障'],
    icon: `
      <svg viewBox="0 0 24 24">
        <path d="M12 3l7 3v5c0 4.6-2.8 8.6-7 10-4.2-1.4-7-5.4-7-10V6l7-3Z"/>
        <path d="M9 12l2 2 4-5"/>
      </svg>
    `
  },
  {
    title: '專業可靠',
    desc: ['與專業團隊合作', '提供安心服務'],
    icon: `
      <svg viewBox="0 0 24 24">
        <path d="M12 3l2.2 4.5 5 .7-3.6 3.5.9 5-4.5-2.4-4.5 2.4.9-5L4.8 8.2l5-.7L12 3Z"/>
        <path d="M12 9v4"/>
        <path d="M10 11h4"/>
      </svg>
    `
  },
  {
    title: '貼心陪伴',
    desc: ['從日常到特殊時刻', '陪你與毛孩一起成長'],
    icon: `
      <svg viewBox="0 0 24 24">
        <path d="M7.4 5.2c-2.2 0-3.9 1.7-3.9 4 0 4.2 7.2 8.4 8.5 9.1 1.3-.7 8.5-4.9 8.5-9.1 0-2.3-1.7-4-3.9-4-1.8 0-3.1 1-3.8 2.2-.2.4-.5.6-.8.6s-.6-.2-.8-.6c-.7-1.2-2-2.2-3.8-2.2Z"/>
        <path d="M8.2 11.2h2.5l1.2-2.2 1.7 4.1 1.1-1.9h1.9"/>
      </svg>
    `
  },
  {
    title: '透明公開',
    desc: ['價格與資訊清楚透明', '消費更安心'],
    icon: `
      <svg viewBox="0 0 24 24">
        <circle cx="12" cy="12" r="8"/>
        <path d="M12 8v5"/>
        <path d="M12 16h.01"/>
        <path d="M9 4.8C9.7 3.7 10.7 3 12 3s2.3.7 3 1.8"/>
      </svg>
    `
  },
  {
    title: '在地支持',
    desc: ['支持在地好店', '一起讓社區更美好'],
    icon: `
      <svg viewBox="0 0 24 24">
        <path d="M12 21v-7"/>
        <path d="M8 21h8"/>
        <path d="M12 14c-3 0-5-2-5-5 0-2.8 2.2-5 5-5s5 2.2 5 5c0 3-2 5-5 5Z"/>
        <path d="M12 14c-4 0-7-2.2-7-5.5"/>
        <path d="M12 14c4 0 7-2.2 7-5.5"/>
      </svg>
    `
  }
]
</script>

<template>
  <section class="home-intro">
    <!-- 背景裝飾 -->
    <div class="intro-blue-wave"></div>
    <div class="intro-bottom-clouds"></div>

    <div class="intro-inner">
      <!-- 左側文字 -->
      <div class="intro-copy">
        <h2 class="intro-title">
          <span>安心陪伴</span>
          <span>
            信任每個選擇
            <i class="intro-heart">♡</i>
          </span>
        </h2>

        <p class="intro-desc">
          <span>我們用心把關每個服務與商品，</span>
          <span>只為給毛孩與你最好的日常。</span>
        </p>
      </div>

      <!-- 右側特色 -->
      <div class="intro-list">
        <article
          v-for="item in introItems"
          :key="item.title"
          class="intro-item"
        >
          <div class="intro-icon" v-html="item.icon"></div>

          <h3>{{ item.title }}</h3>

          <p class="intro-item-desc">
            <span
              v-for="line in item.desc"
              :key="line"
            >
              {{ line }}
            </span>
          </p>
        </article>
      </div>
    </div>
  </section>
</template>

<style scoped>
.home-intro {
  position: relative;
  width: 100%;
  overflow: hidden;
  background: transparent;
  padding: 56px 16px 64px;
}

.intro-inner {
  position: relative;
  z-index: 2;
  max-width: 1280px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 0.95fr 1.7fr;
  align-items: center;
  gap: 48px;
}

/* 左側文字排版 */
.intro-copy {
  position: relative;
  padding-left: 6px;
  color: #4d3729;
}

.intro-title {
  position: relative;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 30px;
  line-height: 1.25;
  font-weight: 900;
  letter-spacing: 0.12em;
  color: #4b3324;
}

.intro-title span {
  position: relative;
  display: inline-block;
  width: fit-content;
}

.intro-title span:nth-child(2) {
  padding-left: 18px;
}

.intro-heart {
  position: absolute;
  top: -8px;
  right: -34px;
  font-style: normal;
  font-size: 27px;
  font-weight: 400;
  color: #d79a5f;
  transform: rotate(-10deg);
}

.intro-desc {
  margin: 22px 0 0;
  display: flex;
  flex-direction: column;
  gap: 5px;
  font-size: 15px;
  line-height: 1.75;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: #7a6657;
}

.intro-desc span {
  display: block;
}

/* 右側卡片 */
.intro-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  align-items: start;
  gap: 28px;
}

.intro-item {
  position: relative;
  text-align: center;
  color: #5a4030;
}

.intro-icon {
  width: 76px;
  height: 76px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 22px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.9), rgba(248, 237, 224, 0.82));
  box-shadow:
    0 14px 30px rgba(116, 79, 46, 0.09),
    inset 0 0 0 1px rgba(210, 166, 116, 0.18);
}

.intro-icon :deep(svg) {
  width: 40px;
  height: 40px;
  fill: none;
  stroke: #8a633f;
  stroke-width: 1.75;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.intro-item h3 {
  margin: 14px 0 10px;
  font-size: 17px;
  line-height: 1.3;
  font-weight: 900;
  letter-spacing: 0.08em;
  color: #5a4030;
  white-space: nowrap;
}

.intro-item-desc {
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
  font-size: 13px;
  line-height: 1.55;
  font-weight: 700;
  letter-spacing: 0.05em;
  color: #8a7463;
}

.intro-item-desc span {
  display: block;
  white-space: nowrap;
}

/* hover 可留可不留 */
.intro-item {
  transition: transform 0.25s ease;
}

.intro-item:hover {
  transform: translateY(-4px);
}

/* RWD */
@media (max-width: 1100px) {
  .intro-inner {
    grid-template-columns: 1fr;
    gap: 36px;
  }

  .intro-copy {
    text-align: center;
    padding-left: 0;
  }

  .intro-title {
    align-items: center;
  }

  .intro-title span:nth-child(2) {
    padding-left: 0;
  }

  .intro-desc {
    align-items: center;
  }

  .intro-list {
    grid-template-columns: repeat(5, minmax(120px, 1fr));
    overflow-x: auto;
    padding-bottom: 8px;
  }
}

@media (max-width: 768px) {
  .home-intro {
    padding: 44px 16px 54px;
  }

  .intro-title {
    font-size: 26px;
  }

  .intro-desc {
    font-size: 13px;
    letter-spacing: 0.04em;
  }

  .intro-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 28px 18px;
    overflow: visible;
  }

  .intro-item-desc span {
    white-space: normal;
  }
}

@media (max-width: 480px) {
  .intro-list {
    grid-template-columns: 1fr;
  }

  .intro-item {
    display: grid;
    grid-template-columns: 76px 1fr;
    grid-template-areas:
      "icon title"
      "icon desc";
    column-gap: 16px;
    text-align: left;
    align-items: center;
  }

  .intro-icon {
    grid-area: icon;
    margin: 0;
  }

  .intro-item h3 {
    grid-area: title;
    margin: 0 0 6px;
  }

  .intro-item-desc {
    grid-area: desc;
  }
}
</style>