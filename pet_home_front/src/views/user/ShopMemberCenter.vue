<!-- <template>
  <div class="member-center-container">
    <div class="sidebar">
      <div class="sidebar-title">會員中心</div>
      <ul class="sidebar-menu">
        <li 
          :class="{ active: currentTab === 'profile' }" 
          @click="currentTab = 'profile'"
        >
          👤 個人基本資料
        </li>
        <li 
          :class="{ active: currentTab === 'orders' }" 
          @click="currentTab = 'orders'"
        >
          📋 我的歷史訂單
        </li>
      </ul>
    </div>

    <div class="main-content">
      <UserProfile v-if="currentTab === 'profile'" />
      <UserOrders v-if="currentTab === 'orders'" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import UserProfile from './UserProfile.vue';
import UserOrders from './UserOrders.vue';

// 預設停留在個人資料頁面
const currentTab = ref('profile');
</script>

<style scoped>
.member-center-container {
  display: flex;
  min-height: calc(100vh - 60px); /* 扣除頂部導覽列高度 */
  background-color: #f8f9fa;
}

/* 左側側邊欄樣式 */
.sidebar {
  width: 240px;
  background-color: #212529; /* 採用與你後台類似的深色系 */
  color: #fff;
  padding-top: 20px;
}
.sidebar-title {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #343a40;
  margin-bottom: 10px;
}
.sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 0;
}
.sidebar-menu li {
  padding: 15px 25px;
  cursor: pointer;
  transition: all 0.3s;
  color: #dee2e6;
}
.sidebar-menu li:hover {
  background-color: #343a40;
  color: #fff;
}
.sidebar-menu li.active {
  background-color: #f8d7da; /* 淡淡的粉橘/粉紅，呼應寵物電商溫馨感 */
  color: #212529;
  font-weight: bold;
}

/* 右側內容區樣式 */
.main-content {
  flex: 1;
  padding: 30px;
  background-color: #ffffff;
  margin: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}
</style> -->
<template>
  <div class="member-center-container">
    <div class="sidebar">
      <div class="sidebar-title">會員中心</div>
      <ul class="sidebar-menu">
        <li :class="{ active: currentTab === 'profile' }" @click="currentTab = 'profile'">
          👤 個人基本資料
        </li>
        <li :class="{ active: currentTab === 'orders' }" @click="currentTab = 'orders'">
          📋 我的歷史訂單
        </li>
      </ul>
    </div>

    <div class="main-content">
      <UserProfile v-if="currentTab === 'profile'" />
      <UserOrders v-if="currentTab === 'orders'" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { userAuthStore } from '@/stores/auth.js';
import UserProfile from './ShopUserProfile.vue';
import UserOrders from './ShopUserOrders.vue';

const authStore = userAuthStore();
const currentTab = ref('profile');

onMounted(() => {
  console.log("目前會員 ID:", authStore.id);
});
</script>

<style scoped>
.member-center-container { display: flex; min-height: 80vh; background-color: #f8f9fa; }
.sidebar { width: 240px; background-color: #212529; color: #fff; padding-top: 20px; }
.sidebar-title { font-size: 20px; font-weight: bold; text-align: center; padding-bottom: 20px; border-bottom: 1px solid #343a40; }
.sidebar-menu { list-style: none; padding: 0; margin: 0; }
.sidebar-menu li { padding: 15px 25px; cursor: pointer; color: #dee2e6; transition: 0.3s; }
.sidebar-menu li:hover { background-color: #343a40; }
.sidebar-menu li.active { background-color: #f8d7da; color: #000; font-weight: bold; }
.main-content { flex: 1; padding: 30px; background-color: #fff; margin: 20px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.05); }
</style>