<script setup>
import { computed } from 'vue'
import { RouterLink } from 'vue-router'

import pawcareOnlyLogo from '@/assets/images/home/pawcare-only_logo.png'
import pawcareOnlyText from '@/assets/images/home/pawcare-only_text.png'
import HeaderUserActions from '../member/HeaderUserActions.vue'
import { userAuthStore } from '@/stores/auth.js'
// 論壇下拉式選單樣式
import ForumDropdown from '@/petpost/components/header/ForumDropdown.vue'

const authStore = userAuthStore()

const washMenus = computed(() => {
  const roles = authStore.roles || []
  const menus = []

  if (!authStore.isLogin) {
    return [
      {
        label: '預約送洗',
        items: [{ label: '服務項目與價格表', to: '/wash/price-list' }],
      },
    ]
  }

  if (roles.includes('USER')) {
    menus.push({
      label: '寵物送洗-會員',
      items: [
        { label: '服務項目與價格表', to: '/wash/price-list' },
        { label: '我的預約與行事曆', to: '/wash/calendar' },
        { label: '確認預約資料', to: '/wash/record' },
        { label: '我的付款紀錄', to: '/wash/payment' },
        //{ label: '歷史訂單查詢', to: '/wash/history-order' },
        //{ label: '會員點數', to: '/wash/points' },
      ],
    })
  }

  if (roles.includes('STAFF')) {
    menus.push({
      label: '寵物送洗-美容師',
      items: [
        { label: '美容師班表', to: '/groomer/calendar' },
        { label: '價目表調整', to: '/groomer/price-list' },
        { label: '已預約行事曆', to: '/groomer/calendar-back' },
        { label: '訂單與付款查詢', to: '/groomer/payment' },
        //{ label: '美容服務回饋', to: '/groomer/record' },
      ],
    })
  }

  if (roles.includes('ADMIN')) {
    menus.push({
      label: '寵物送洗-管理師',
      items: [
        { label: '班表設定', to: '/admin/wash/calendar' },
        //{ label: '會員點數到期日設定', to: '/admin/wash/points-expiry' },
        //{ label: '通知查詢', to: '/admin/wash/notifications' },
      ],
    })
  }

  return menus
})

const petMapLink = computed(() => {
  const roles = authStore.roles || [];

  if (roles.includes("ADMIN")) {
    return {
      label: "寵物地圖-管理員",
      to: "/petmap/admin",
    };
  }

  if (roles.includes("STAFF")) {
    return {
      label: "寵物地圖-商家",
      to: "/petmap/merchant",
    };
  }

  return {
    label: "寵物地圖",
    to: "/petmap",
  };
});
</script>

<template>
  <header class="app-header">
    <div class="header-inner">
      <RouterLink to="/" class="brand">
        <img class="brand-mark" :src="pawcareOnlyLogo" alt="毛起來圖示" />
        <img class="brand-text" :src="pawcareOnlyText" alt="毛起來" />
      </RouterLink>

      <nav class="main-nav">
        <RouterLink to="/shop">寵物商城</RouterLink>
        <!--寵物論壇下拉式選單  -->
        <ForumDropdown />

        <div v-for="menu in washMenus" :key="menu.label" class="nav-dropdown">
          <button class="nav-dropdown-button" type="button">
            {{ menu.label }}
            <span aria-hidden="true">▾</span>
          </button>

          <div class="nav-dropdown-menu">
            <RouterLink
              v-for="item in menu.items"
              :key="item.to"
              class="nav-dropdown-item"
              :to="item.to"
            >
              {{ item.label }}
            </RouterLink>
          </div>
        </div>

        <RouterLink :to="petMapLink.to">
          {{ petMapLink.label }}
        </RouterLink>

        <!-- <a href="#service-links">服務導覽</a> -->
        <!-- <a href="#member-benefits">會員權益</a> -->
        <!-- <a href="#web-intro">網站介紹</a> -->

        <RouterLink v-if="authStore.canAccessBackstage" to="/admin" class="admin-link">
          管理後台
        </RouterLink>
      </nav>

      <HeaderUserActions />
    </div>
  </header>
</template>

<style scoped lang="scss">
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  width: 100%;
  background: rgba(255, 250, 243, 0.88);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(107, 79, 63, 0.08);
}

.header-inner {
  width: min(1180px, calc(100% - 72px));
  height: 120px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}

.brand-mark {
  width: 120px;
  height: 120px;
  object-fit: contain;
  display: block;
  flex: 0 0 auto;
  filter: drop-shadow(0 8px 12px rgba(107, 79, 63, 0.12));
}

.brand-text {
  height: 75px;
  width: auto;
  object-fit: contain;
  display: block;
  flex: 0 0 auto;
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 24px;

  .admin-link {
    display: inline-flex;
    align-items: center;
    color: #fff !important;
    background: #b68b65;
    padding: 8px 14px;
    border-radius: 999px;
    font-weight: 700;
    text-decoration: none;
  }

  a,
  .nav-dropdown-button {
    position: relative;
    color: #6f6258;
    text-decoration: none;
    font-size: 15px;
    font-weight: 700;
    letter-spacing: 0.04em;
    transition: color 0.2s ease;
    white-space: nowrap;
  }

  > a::after,
  > .admin-link::after {
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

  > a:hover,
  > .admin-link:hover {
    color: #3f332b;

    &::after {
      width: 100%;
    }
  }
}

.nav-dropdown {
  position: relative;
  padding: 16px 0;
}

.nav-dropdown-button {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 0;
  border: 0;
  background: transparent;
  cursor: pointer;
  font-family: inherit;
}

.nav-dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  min-width: 190px;
  padding: 8px;
  border: 1px solid rgba(107, 79, 63, 0.12);
  border-radius: 8px;
  background: rgba(255, 252, 248, 0.98);
  box-shadow: 0 16px 34px rgba(107, 79, 63, 0.14);
  opacity: 0;
  visibility: hidden;
  transform: translateY(8px);
  transition:
    opacity 0.18s ease,
    transform 0.18s ease,
    visibility 0.18s ease;
}

.nav-dropdown:hover .nav-dropdown-menu,
.nav-dropdown:focus-within .nav-dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.nav-dropdown-item {
  display: block;
  padding: 10px 12px;
  border-radius: 6px;
  color: #4f4037;
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 0;
  text-decoration: none;

  &:hover {
    background: #f5eadf;
    color: #2f251f;
  }
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 18px;
}

.login-link {
  color: #6f6258;
  text-decoration: none;
  font-size: 15px;
  font-weight: 700;
}

.register-btn {
  height: 44px;
  padding: 0 24px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  background: #dca359;
  color: #ffffff;
  text-decoration: none;
  font-size: 15px;
  font-weight: 800;
  box-shadow: 0 12px 24px rgba(220, 163, 89, 0.28);
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease;

  &:hover {
    background: #cd9348;
    transform: translateY(-2px);
    box-shadow: 0 16px 30px rgba(220, 163, 89, 0.34);
  }
}

@media (max-width: 1100px) {
  .header-inner {
    width: calc(100% - 32px);
  }

  .main-nav {
    gap: 14px;

    a,
    .nav-dropdown-button {
      font-size: 14px;
      letter-spacing: 0.02em;
    }

    .admin-link {
      padding: 7px 12px;
    }
  }

  .header-actions {
    gap: 12px;
  }

  .brand-mark {
    width: 86px;
    height: 86px;
  }

  .brand-text {
    height: 54px;
  }
}

@media (max-width: 720px) {
  .header-inner {
    width: min(100% - 32px, 720px);
    height: 76px;
  }

  .main-nav {
    display: none;
  }

  .brand {
    gap: 8px;
  }

  .brand-mark {
    width: 48px;
    height: 48px;
  }

  .brand-text {
    height: 32px;
  }
}
</style>
