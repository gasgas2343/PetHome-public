<!-- <template>
  <div class="admin-wrapper d-flex">
    <aside class="sidebar bg-dark text-white p-3" style="width: 250px; min-height: 100vh;">
      <h4 class="mb-4">寵物店後台</h4>
      <ul class="nav flex-column">
        <li class="nav-item mb-2">
          <router-link class="nav-link text-white" to="/admin/products">商品管理</router-link>
        </li>
        <li class="nav-item mb-2">
          <router-link class="nav-link text-white" to="/admin/coupons">優惠券發放</router-link>
        </li>
        <li class="nav-item mb-2">
          <router-link class="nav-link text-white" to="/admin/campaigns">行銷活動</router-link>
        </li>
        <li class="nav-item mb-2">
          <router-link class="nav-link text-white" to="/admin/orders">訂單管理</router-link>
        </li>
      </ul>
    </aside>
    <main class="content flex-grow-1 p-4">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.sidebar .nav-link:hover { background-color: #495057; border-radius: 5px; }
.sidebar .router-link-active { background-color: #0d6efd; border-radius: 5px; }
</style> -->

<template>
  <div class="admin-layout">
    <!-- 加入前台導覽列 -->
    <ShopNavigation />

    <div class="admin-wrapper d-flex">
      <aside class="sidebar bg-dark text-white p-3" style="width: 250px; min-height: 100vh;">
        <h4 class="mb-4">寵物商店後台</h4>
        <ul class="nav flex-column">
          <li class="nav-item mb-2">
            <router-link class="nav-link text-white" to="/shop-admin/products">商品管理</router-link>
          </li>
          <li class="nav-item mb-2">
            <router-link class="nav-link text-white" to="/shop-admin/coupons">優惠券發放</router-link>
          </li>
          <li class="nav-item mb-2">
            <router-link class="nav-link text-white" to="/shop-admin/campaigns">行銷活動</router-link>
          </li>
          <li class="nav-item mb-2">
            <router-link class="nav-link text-white" to="/shop-admin/orders">訂單管理</router-link>
          </li>
          <li class="nav-item mb-2">
  <router-link class="nav-link text-white" to="/shop-admin/payments">金流管理</router-link>
</li>
        </ul>
      </aside>
      <main class="content flex-grow-1 p-4">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import ShopNavigation from '@/views/ShopNavigation.vue'
</script>

<style scoped>
.sidebar .nav-link:hover { background-color: #495057; border-radius: 5px; }
.sidebar .router-link-active { background-color: #0d6efd; border-radius: 5px; }
</style>
