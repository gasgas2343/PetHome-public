<template>
  <main class="home-page">
    <HomeHeroSlider />
    <HomeLink></HomeLink>
    <HomeBenefits />
    <HomeIntro />
    <AppFooter />
  </main>
</template>

<script setup>
import HomeHeroSlider from '@/components/home/HomeHeroSlider.vue'
import HomeLink from '@/components/home/HomeLink.vue'
import HomeBenefits from '@/components/home/HomeBenefits.vue'
import HomeIntro from '@/components/home/HomeIntro.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
</script>

<style scoped lang="scss">
.home-page {
  position: relative;
  min-height: 100vh;
  background:
    /* 上方 hero：光暈放高一點，不碰 wave */
    radial-gradient(circle at 12% 8%, rgba(207, 231, 238, 0.18), transparent 14%),
    radial-gradient(circle at 88% 10%, rgba(246, 216, 159, 0.16), transparent 24%),
    /* 中段 link 區塊：避開 wave，往下放 */
    radial-gradient(circle at 6% 63%, rgba(207, 231, 238, 0.24), transparent 28%),
    radial-gradient(circle at 84% 56%, rgba(246, 216, 159, 0.18), transparent 30%),
    /* 下方 intro / footer */
    radial-gradient(circle at 12% 82%, rgba(217, 154, 108, 0.1), transparent 26%),
    radial-gradient(circle at 88% 88%, rgba(207, 231, 238, 0.18), transparent 28%),
    #fff8ef;
  overflow: hidden;
}
</style>
