<script setup>
import { RouterLink } from 'vue-router'
import { userAuthStore } from '@/stores/auth'

const authStore = userAuthStore()
</script>

<template>
  <div class="forum-dropdown">
    <RouterLink to="/community/posts" class="forum-dropdown-btn"> 寵物論壇 </RouterLink>

    <div class="forum-dropdown-menu">
      <RouterLink to="/community/posts"> 論壇 </RouterLink>

      <RouterLink to="/community/ads"> 廣告專區 </RouterLink>

      <RouterLink to="/pets"> 時間軸 </RouterLink>

      <RouterLink v-if="authStore.canAccessBackstage" to="/forum-admin"> 論壇後台管理 </RouterLink>
    
    </div>
  </div>
</template>

<style scoped lang="scss">
/* 全部 forum-dropdown 的 CSS 都放這 */
/* 中文註解：寵物論壇專用下拉選單，不影響其他導覽 */
.forum-dropdown {
  position: relative;
  display: inline-flex;
  align-items: center;
}

.forum-dropdown:hover .forum-dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translate(-50%, 0);
}

/* 中文註解：寵物論壇按鈕 */
.forum-dropdown-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 4px;

  color: #6f6258;
  text-decoration: none;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.04em;

  transition: color 0.2s ease;
}

/* 底線 */
.forum-dropdown-btn::after {
  content: '';

  position: absolute;

  left: 50%;

  bottom: -8px;

  width: 0;

  height: 2px;

  border-radius: 999px;

  background: #d6a158;

  transform: translateX(-50%);

  transition: width 0.2s ease;
}

/* Hover */

.forum-dropdown:hover .forum-dropdown-btn {
  color: #3f332b;
}

.forum-dropdown:hover .forum-dropdown-btn::after {
  width: 100%;
}

/* 中文註解：寵物論壇下拉選單 */
.forum-dropdown-menu {
  position: absolute;
  top: 32px;
  left: 50%;
  min-width: 170px;
  padding: 8px;
  background: #fffaf3;
  border: 1px solid rgba(107, 79, 63, 0.12);
  border-radius: 14px;
  box-shadow: 0 10px 24px rgba(107, 79, 63, 0.14);
  transform: translate(-50%, 8px);
  opacity: 0;
  visibility: hidden;
  transition: 0.2s ease;
  z-index: 999;
}

.forum-dropdown-menu a {
  display: block;
  padding: 10px 12px;
  border-radius: 10px;
  color: #6f6258;
  text-decoration: none;
  white-space: nowrap;
}

.forum-dropdown-menu a:hover {
  background: #f3e4d2;
  color: #3f332b;
}

.forum-header-actions {
  display: inline-flex;
  align-items: center;
  gap: 14px;
}
</style>
