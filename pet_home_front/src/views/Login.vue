<template>
  <div class="login-container">
    <el-input v-model="email" placeholder="請輸入 Email" />
    <el-button type="primary" @click="handleLogin">會員登入</el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';

const email = ref('');
const userStore = useUserStore();
const router = useRouter();

const handleLogin = async () => {
  // 假設後端有一個登入 API，會回傳該使用者的 ID
  const res = await axios.post('http://localhost:8080/api/auth/login', { email: email.value });
  if (res.data.success) {
    userStore.login(res.data.data.id); // 將 userId 存進 Pinia
    router.push('/member'); // 導向會員中心
  }
};
</script>