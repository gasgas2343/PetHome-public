

<!-- <template>
  <nav class="navbar navbar-expand-lg bg-body-tertiary">
    <div class="container-fluid">
      <a href="/" class="navbar-brand">
        <img src="/images/images/logo.png" alt="寵愛商城" style="height: 40px; width: auto;">
      </a>
      
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav">
          <li class="nav-item"><RouterLink class="nav-link" to="/">商城首頁</RouterLink></li>
          <li class="nav-item dropdown" v-for="cat in categories" :key="cat.id">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
              {{ cat.name }}
            </a>
            <ul class="dropdown-menu">
              <li v-for="sub in cat.children" :key="sub.id">
                <RouterLink class="dropdown-item" :to="'/products/category/' + sub.id">
                  {{ sub.name }}
                </RouterLink>
              </li>
            </ul>
          </li>
        </ul> -->

        <!-- 模擬角色切換（合板後刪掉這段） -->
        <!-- <div class="navbar-nav mx-3">
          <select class="form-select form-select-sm" v-model="testRole" @change="switchRole">
            <option v-for="u in mockUsers" :key="u.value" :value="u.value">{{ u.label }}</option>
          </select>
        </div>

        <ul class="navbar-nav ms-auto">
          <li class="nav-item">
            <RouterLink class="nav-link" to="/member">👤 會員中心</RouterLink>
          </li>
          <!-- 只有管理員才看得到管理後台 -->
          <!-- <li class="nav-item" v-if="userStore.isAdmin">
            <RouterLink to="/admin/products" class="nav-link">⚙️ 管理後台</RouterLink>
          </li> -->
          <!-- 只有顧客才看得到購物車和喜愛清單 -->
          <!-- <template v-if="userStore.isCustomer">
            <li class="nav-item">
              <RouterLink class="nav-link" to="/favorites">❤️ 喜愛清單</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" to="/cart">🛒 購物車</RouterLink>
            </li>
          </template>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { useUserStore } from "@/stores/user.js";

const router = useRouter();
const userStore = useUserStore();
const categories = ref([]);

const mockUsers = [
  { label: '模擬顧客 (ID: 1)', value: 'customer-1', userId: 1, role: 'CUSTOMER' },
  { label: '模擬顧客 (ID: 2)', value: 'customer-2', userId: 2, role: 'CUSTOMER' },
  { label: '模擬管理員',       value: 'admin',      userId: 99, role: 'ADMIN' }
];

const testRole = ref('customer-1');

const switchRole = () => {
  const selected = mockUsers.find(u => u.value === testRole.value);
  if (!selected) return;
  
  userStore.setUser(selected.userId, '', '', selected.role);
  
  if (selected.role === 'ADMIN') {
    router.push('/admin/products');
  } else {
    router.push('/');
  }
};

// 初始化預設角色（顧客 ID:1）
onMounted(async () => {
  if (!userStore.role) {
    userStore.setUser(1, '', '', 'CUSTOMER');
  }

  try {
    const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/products/categories`);
    categories.value = res.data.data || res.data;
  } catch (err) {
    console.error("分類載入失敗", err);
  }
});
</script> --> 
<template>
  <nav class="navbar navbar-expand-lg bg-body-tertiary">
    <div class="container-fluid">
      <a href="/" class="navbar-brand">
        <img src="/images/images/logo.png" alt="寵愛商城" style="height: 40px; width: auto;">
      </a>

      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav">
          <li class="nav-item">
            <RouterLink class="nav-link" to="/shop">商城首頁</RouterLink>
          </li>

          <!-- ✅ 一般分類（從分類樹來，已不含品牌專區） -->
          <li class="nav-item dropdown" v-for="cat in categories" :key="cat.id">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
              {{ cat.name }}
            </a>
            <ul class="dropdown-menu">
              <li v-for="sub in cat.children" :key="sub.id">
                <RouterLink class="dropdown-item" :to="'/products/category/' + sub.id">
                  {{ sub.name }}
                </RouterLink>
              </li>
            </ul>
          </li>

          <!-- ✅ 品牌專區（獨立打 /api/brands） -->
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
              品牌專區
            </a>
            <ul class="dropdown-menu">
              <li v-for="brand in brands" :key="brand.id">
                <RouterLink class="dropdown-item" :to="'/products/brand/' + brand.id">
                  {{ brand.name }}
                </RouterLink>
              </li>
            </ul>
          </li>

        </ul>

        <!-- 右側選單（不動） -->
        <ul class="navbar-nav ms-auto">
          <template v-if="!authStore.isLogin">
            <li class="nav-item">
              <RouterLink class="nav-link" to="/login">🔑 登入</RouterLink>
            </li>
          </template>
          <template v-else>
            <template v-if="authStore.canAccessBackstage">
              <li class="nav-item">
                <RouterLink to="/shop-admin/products" class="nav-link">⚙️ 管理後台</RouterLink>
              </li>
            </template>
            <template v-else>
              <li class="nav-item">
                <RouterLink class="nav-link" to="/favorites">❤️ 喜愛清單</RouterLink>
              </li>
              <li class="nav-item">
                <RouterLink class="nav-link" to="/cart">🛒 購物車</RouterLink>
              </li>
            </template>
            <li class="nav-item">
              <RouterLink class="nav-link" to="/shop/member">👤 {{ authStore.name }}</RouterLink>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#" @click.prevent="logout">🚪 登出</a>
            </li>
          </template>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { userAuthStore } from '@/stores/auth.js';
import http from '@/api/http';

const router = useRouter();
const authStore = userAuthStore();
const categories = ref([]);
const brands = ref([]); // ✅ 新增

const logout = () => {
  authStore.clearStore();
  router.push('/login');
};

onMounted(async () => {
  try {
    // ✅ 同時打兩支 API
    const [catRes, brandRes] = await Promise.all([
      http.get('/products/categories'),
      http.get('/brands')
    ]);
    categories.value = catRes.data.data || catRes.data;
    brands.value = brandRes.data.data || brandRes.data;
  } catch (err) {
    console.error('載入失敗', err);
  }
});
</script>