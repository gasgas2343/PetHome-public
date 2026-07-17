<script setup>
import { onMounted } from 'vue'
import { RouterLink } from 'vue-router'

import PetForm from '@/petpost/components/pet/PetForm.vue'
import PetList from '@/petpost/components/pet/PetList.vue'
import { usePetStore } from '@/petpost/stores/petStore'
import { userAuthStore } from '@/stores/auth'
import '@/petpost/assets/petpost-theme.scss'

const petStore = usePetStore()
const authStore = userAuthStore()

onMounted(async () => {
  if (!authStore.isLogin) {
    petStore.errorMessage = '請先登入後再查看毛孩資料'
    return
  }

  await petStore.loadPets()
})

async function createPet(dto) {
  try {
    if (!authStore.isLogin) {
      throw new Error('請先登入後再新增毛孩資料')
    }

    await petStore.addPet(dto)
    alert('寵物新增成功')
  } catch (error) {
    console.log('寵物新增失敗', error)
    alert(error?.message || '寵物新增失敗，請確認是否存在。')
  }
}

async function deletePet(petId) {
  if (!petId) return

  try {
    await petStore.removePet(petId)
    alert('毛孩已刪除')
  } catch (error) {
    console.log('刪除毛孩失敗', error)
    alert('刪除毛孩失敗，請確認是否正常。')
  }
}
</script>

<template>
  <main class="pet-page">
    <div class="container mt-4">
      <RouterLink to="/community" class="back-community-link"> ← 返回論壇首頁 </RouterLink>

      <div class="pet-page-header">
        <h2>我的寵物</h2>
      </div>

      <PetForm @created="createPet" />

      <div v-if="petStore.loading" class="alert alert-info">寵物資料載入中...</div>

      <PetList :pets="petStore.pets" @delete="deletePet" />
    </div>
  </main>
</template>

<style scoped>
.back-community-link {
  display: inline-flex;
  align-items: center;

  margin-bottom: 24px;

  color: #8a5b28;

  text-decoration: none;

  font-weight: 900;

  transition: 0.2s;
}

.back-community-link:hover {
  color: #6f6258;

  text-decoration: underline;
}

.pet-page {
  min-height: 100vh;
  padding: 32px 0 72px;
  background: transparent;
}

.pet-page-header {
  margin-bottom: 24px;
}

.pet-page-header h2 {
  margin: 0;
  color: #3f332b;
  font-weight: 900;
}
</style>
