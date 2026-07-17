<!-- <!-- <template>
  <div class="profile-section">
    <h3>✨ 個人基本資料</h3>
    <el-divider />
    <el-form :model="userInfo" label-width="100px" style="max-width: 500px;">
      <el-form-item label="會員 ID"><el-input v-model="userInfo.userId" disabled /></el-form-item>
      <el-form-item label="真實姓名"><el-input v-model="userInfo.name" /></el-form-item>
      <el-form-item label="手機號碼"><el-input v-model="userInfo.phone" /></el-form-item>
      <el-form-item label="電子信箱"><el-input v-model="userInfo.email" /></el-form-item>
      <el-form-item><el-button type="primary" @click="saveProfile">儲存修改資料</el-button></el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const userInfo = ref({
  userId: localStorage.getItem('loginUserId') || '未登入',
  name: '陳秋雅',
  phone: '0987654321',
  email: 'ccy@example.com'
});

const saveProfile = async () => {
  // 這裡可以加入呼叫後端更新資料的 API
  // await axios.put(`${import.meta.env.VITE_API_BASE_URL}/users/${userInfo.value.userId}`, userInfo.value);
  ElMessage.success('個人資料已儲存！');
};
</script> -->
<!-- <template>
  <div class="profile-section">
    <h3>✨ 個人基本資料</h3>
    <el-divider />
    <el-form :model="userInfo" label-width="100px" style="max-width: 500px;" v-loading="loading">
      <el-form-item label="會員 ID">
        <el-input v-model="userInfo.userId" disabled />
      </el-form-item>
      <el-form-item label="真實姓名">
        <el-input v-model="userInfo.name" placeholder="請輸入姓名" />
      </el-form-item>
      <el-form-item label="手機號碼">
        <el-input v-model="userInfo.phone" placeholder="請輸入手機號碼" />
      </el-form-item>
      <el-form-item label="電子信箱">
        <el-input v-model="userInfo.email" placeholder="請輸入 Email" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="saving" @click="saveProfile">儲存修改資料</el-button>
      </el-form-item>
    </el-form>
  </div>
</template> -->

<!-- <script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import { useUserStore } from '@/stores/user'; // 引入你的 Store

const userStore = useUserStore();
const loading = ref(false);
const saving = ref(false);

const userInfo = ref({
  userId: userStore.userId, // 從 Pinia 取得
  name: '',
  phone: '',
  email: ''
});

// 1. 從資料庫讀取資料
const fetchUserProfile = async () => {
  if (!userStore.userId) return;
  loading.value = true;
  try {
    // 呼叫後端 API，例如 GET /api/users/{id}
    const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/users/${userStore.userId}`);
    // 將後端傳回的資料映射到 userInfo
    userInfo.value = {
      userId: userStore.userId,
      name: res.data.name || '',
      phone: res.data.phone || '',
      email: res.data.email || ''
    };
  } catch (err) {
    ElMessage.error("無法讀取會員資料");
  } finally {
    loading.value = false;
  }
};

// 2. 將修改後的資料寫入資料庫
const saveProfile = async () => {
  saving.value = true;
  try {
    // 呼叫後端 API，例如 PUT /api/users/{id}
    await axios.put(`${import.meta.env.VITE_API_BASE_URL}/users/${userStore.userId}`, userInfo.value);
    ElMessage.success('資料已同步至資料庫！');
  } catch (err) {
    ElMessage.error('更新失敗，請檢查後端連線');
  } finally {
    saving.value = false;
  }
};

onMounted(() => {
  fetchUserProfile();
});
</script> -->
<!-- <script setup>
 
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
const userInfo = ref({ userId: '', name: '', phone: '', email: '' });

// 監聽 userId，當切換成「模擬顧客 2」時，自動重新抓取對應資料
watch(() => userStore.userId, async (newId) => {
  if (newId) await fetchUserData(newId);
}, { immediate: true });

async function fetchUserData(id) {
  try {
    // 【關鍵連動】這裡的 id 就是從 Pinia 傳來的 1 或 2
    const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/users/${id}`);
    userInfo.value = {
      userId: id,
      name: res.data.name || '無名氏', // 對應資料庫欄位
      phone: res.data.phone || '',
      email: res.data.email || ''
    };
  } catch (err) {
    console.error("無法連動資料庫", err);
  }
}
</script> --> 
<template>
  <div class="profile-section">
    <h3>✨ 個人基本資料 
      
    </h3>
    <el-divider />
    <el-form :model="userInfo" label-width="120px" style="max-width: 500px;">
      <el-form-item label="會員 ID">
        <el-input v-model="userInfo.id" disabled />
      </el-form-item>
      <el-form-item label="電子信箱 (Email)">
        <el-input v-model="userInfo.email" disabled />
      </el-form-item>
      <el-form-item label="帳號狀態">
        <el-tag :type="userInfo.status === 'ACTIVE' ? 'success' : 'danger'">
          {{ userInfo.status }}
        </el-tag>
      </el-form-item>
      <el-form-item label="註冊時間">
        <el-input v-model="userInfo.createdAt" disabled />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { userAuthStore } from '@/stores/auth.js'; // ✅

const authStore = userAuthStore();

const createAuthAxios = () => {
  return axios.create({
    baseURL: `${import.meta.env.VITE_API_BASE_URL}`,
    headers: { Authorization: `Bearer ${authStore.accessToken}` }
  });
};

const userInfo = ref({ id: '', email: '', status: '', createdAt: '' });

onMounted(async () => {
  const api = createAuthAxios();
  try {
    const res = await api.get('/users/me'); // ✅ 從後端拿真實資料
    const data = res.data.data || res.data;
    userInfo.value = {
      id: data.id,
      email: data.email,
      status: data.status,
      createdAt: data.createdAt || '--'
    };
  } catch (err) {
    // 後端拿不到就用 authStore 的資料
    userInfo.value = {
      id: authStore.id,
      email: authStore.email,
      status: 'ACTIVE',
      createdAt: '--'
    };
  }
});
</script>