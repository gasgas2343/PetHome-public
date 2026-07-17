<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from "vue";
import bannerImages from '@/petpost/data/bannerImages'

const activeIndex = ref(0);
let timerId = null;

const activeBanner = computed(() => bannerImages[activeIndex.value] || bannerImages[0]);

function nextBanner() {
  activeIndex.value = (activeIndex.value + 1) % bannerImages.length;
}

function prevBanner() {
  activeIndex.value = (activeIndex.value - 1 + bannerImages.length) % bannerImages.length;
}

function goBanner(index) {
  activeIndex.value = index;
}

function startAutoPlay() {
  stopAutoPlay();
  timerId = window.setInterval(nextBanner, 3000);
}

function stopAutoPlay() {
  if (timerId) {
    window.clearInterval(timerId);
    timerId = null;
  }
}

onMounted(startAutoPlay);
onBeforeUnmount(stopAutoPlay);
</script>

<template>
  <aside
    class="ad-card"
    @mouseenter="stopAutoPlay"
    @mouseleave="startAutoPlay"
  >
    <button class="ad-arrow ad-arrow-left" type="button" @click="prevBanner" aria-label="上一張廣告">
      ‹
    </button>

    <img
      v-if="activeBanner"
      :src="activeBanner.imageUrl"
      class="ad-image"
      :alt="activeBanner.title"
    />

    <div v-if="activeBanner" class="ad-caption">
      <h3>{{ activeBanner.title }}</h3>
      <p>{{ activeBanner.description }}</p>
    </div>

    <button class="ad-arrow ad-arrow-right" type="button" @click="nextBanner" aria-label="下一張廣告">
      ›
    </button>

    <div class="ad-dots" aria-label="廣告切換圓點">
      <button
        v-for="(banner, index) in bannerImages"
        :key="banner.id"
        type="button"
        class="ad-dot"
        :class="{ active: index === activeIndex }"
        :aria-label="`切換到第 ${index + 1} 張廣告`"
        @click="goBanner(index)"
      ></button>
    </div>
  </aside>
</template>

<style scoped>
.ad-card {
  position: sticky;
  top: 96px;
  min-height: 520px;
  overflow: hidden;
  border-radius: 28px;
  background: #f2eadf;
  box-shadow: 0 18px 46px rgba(93, 63, 38, 0.14);
}

.ad-image {
  width: 100%;
  height: 520px;
  display: block;
  object-fit: cover;
}

.ad-caption {
  position: absolute;
  left: 22px;
  right: 22px;
  bottom: 34px;
  padding: 18px 16px;
  border-radius: 20px;
  text-align: center;
  color: #fff;
  background: rgba(44, 33, 24, 0.64);
  backdrop-filter: blur(8px);
}

.ad-caption h3 {
  margin: 0 0 6px;
  font-size: 20px;
  font-weight: 900;
}

.ad-caption p {
  margin: 0;
  line-height: 1.5;
  font-size: 14px;
}

.ad-arrow {
  position: absolute;
  top: 50%;
  z-index: 2;
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 999px;
  color: #fff;
  background: rgba(44, 33, 24, 0.44);
  transform: translateY(-50%);
  font-size: 28px;
  line-height: 28px;
}

.ad-arrow-left {
  left: 10px;
}

.ad-arrow-right {
  right: 10px;
}

.ad-dots {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 12px;
  display: flex;
  justify-content: center;
  gap: 8px;
}

.ad-dot {
  width: 9px;
  height: 9px;
  padding: 0;
  border: none;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.55);
}

.ad-dot.active {
  width: 24px;
  background: #fff;
}

@media (max-width: 991px) {
  .ad-card {
    position: relative;
    top: auto;
    min-height: 360px;
    margin-bottom: 40px;
  }

  .ad-image {
    height: 360px;
  }
}
</style>
