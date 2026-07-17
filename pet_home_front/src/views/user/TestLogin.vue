<template>
  <div class="login-box">
    <h3>測試環境：模擬會員登入</h3>
    <el-button type="warning" @click="mockLogin(1)">以會員 ID: 1 登入</el-button>
    <el-button type="info" @click="logout">登出</el-button>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus';

const mockLogin = (userId) => {
  // 將 userId 存入 localStorage，模擬已經登入
  localStorage.setItem('loginUserId', userId);
  ElMessage.success(`模擬登入成功！當前會員 ID: ${userId}`);
  // 登入後強制重新整理，讓 MemberCenter 抓取新資料
  window.location.reload(); 
};

const logout = () => {
  localStorage.removeItem('loginUserId');
  ElMessage.info('已登出');
  window.location.reload();
};
</script>