<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from './components/layout/AppHeader.vue'
import { userAuthStore } from './stores/auth.js'

const route = useRoute()
const authStore = userAuthStore()
const appReady = ref(false)

// 這些路徑使用自己的 Navbar，不顯示 AppHeader
const hideHeaderPaths = [
  '/forum-admin',
  '/Shop',
  '/shop',
  '/cart',
  '/checkout',
  '/orders',
  '/favorites',
  '/coupon',
  '/products',
  '/shop-admin',
  '/shop/member',
  '/payment',
  '/petmap',
]

const showAppHeader = computed(() => {
  return !hideHeaderPaths.some((p) => route.path.startsWith(p))
})

onMounted(async () => {
  console.log('App initAuth 開始')
  await authStore.initAuth()
  console.log('App initAuth 完成', authStore.$state)
  appReady.value = true
})
</script>

<template>
  <!-- 中文註解：等待登入資訊初始化完成後，再渲染整個應用程式。 -->
  <template v-if="appReady">
    <!-- 中文註解：論壇後台、商城相關頁面不顯示共用 AppHeader。 -->
    <AppHeader v-if="showAppHeader" />

    <RouterView v-slot="{ Component, route }" :key="$route.fullPath">
      <main :class="{ 'petpost-scope': route.meta?.petpost === true }">
        <component :is="Component" />
      </main>
    </RouterView>
  </template>
</template>

<style scoped></style>
