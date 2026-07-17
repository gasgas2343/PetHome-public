<script setup>
import AuthLayout from '@/components/auth/AuthLayout.vue'
import RegisterForm from '@/components/auth/RegisterForm.vue'
import registerImage from '@/assets/images/auth/auth-register.png'
</script>

<template>
  <AuthLayout
    theme="register"
    :image-src="registerImage"
    :title-lines="[
      '從建立帳號開始',
      '收藏毛孩每一天的生活'
    ]"
  >
    <RegisterForm />
  </AuthLayout>
</template>