<script setup>
import AuthLayout from '@/components/auth/AuthLayout.vue'
import LoginForm from '@/components/auth/LoginForm.vue'
import loginImage from '@/assets/images/auth/auth-login.png'
</script>

<template>
  <AuthLayout
    theme="login"
    :image-src="loginImage"
    :title-lines="[
      '每一次登入',
      '都是回到毛孩的生活日常'
    ]"
  >
    <LoginForm />
  </AuthLayout>
</template>