<script setup>
import { ref, onMounted } from 'vue'
import PetForm from '@/petpost/components/forum/PetForm.vue'
import { getPetsByUser, createPet, updatePet, deletePet } from '@/petpost/api/forumApi'

// 合板前先固定 userId；正式整合登入後改成從 userStore / JWT 取得
const userId = ref(1)
const pets = ref([])
const editingPet = ref(null)
const loading = ref(false)

function getPetId(pet) {
  return pet.petId || pet.pet_id
}

function resetEdit() {
  editingPet.value = null
}

async function loadPets() {
  try {
    const res = await getPetsByUser(userId.value)
    pets.value = Array.isArray(res.data) ? res.data : []
  } catch (error) {
    console.error('取得毛孩資料失敗', error)
  }
}

async function savePet(payload) {
  loading.value = true

  try {
    if (editingPet.value) {
      await updatePet(getPetId(editingPet.value), payload)
    } else {
      await createPet({ ...payload, userId: userId.value })
    }

    resetEdit()
    await loadPets()
  } catch (error) {
    console.error('儲存毛孩資料失敗', error)
    alert('儲存失敗，請確認後端 API 是否正常')
  } finally {
    loading.value = false
  }
}

async function removePet(pet) {
  const id = getPetId(pet)
  if (!id) return

  if (!confirm(`確定要刪除「${pet.petName || pet.pet_name || '這隻毛孩'}」嗎？`)) {
    return
  }

  try {
    await deletePet(id)
    await loadPets()
  } catch (error) {
    console.error('刪除毛孩失敗', error)
    alert('刪除失敗，請確認後端 API 是否正常')
  }
}

onMounted(loadPets)
</script>

<template>
  <main class="pet-page">
    <PetForm
      :model-value="editingPet || {}"
      :editing="!!editingPet"
      :loading="loading"
      @submit="savePet"
      @cancel="resetEdit"
    />

    <section class="pet-list-section">
      <div class="section-title">
        <p class="eyebrow">Pet List</p>
        <h2>我的毛孩</h2>
      </div>

      <div class="pet-grid">
        <article v-for="pet in pets" :key="getPetId(pet)" class="pet-card">
          <img
            v-if="pet.petAvatarUrl || pet.pet_avatar_url"
            class="pet-avatar"
            :src="pet.petAvatarUrl || pet.pet_avatar_url"
            :alt="pet.petName || pet.pet_name"
          />
          <div v-else class="pet-avatar placeholder">🐾</div>

          <div class="pet-info">
            <h3>{{ pet.petName || pet.pet_name }}</h3>
            <p>{{ pet.breed || '未填寫品種' }}</p>
            <p>{{ pet.birthday || '未填寫生日' }}</p>
            <p class="intro">{{ pet.petIntro || pet.pet_intro }}</p>
          </div>

          <div class="pet-actions">
            <button class="edit-btn" type="button" @click="editingPet = pet">修改</button>
            <button class="delete-btn" type="button" @click="removePet(pet)">刪除</button>
          </div>
        </article>
      </div>
    </section>
  </main>
</template>

<style scoped>
.pet-page {
  max-width: 1080px;
  margin: 0 auto;
  padding: 36px 20px 70px;
}

.pet-list-section {
  margin-top: 30px;
}

.section-title {
  margin-bottom: 16px;
}

.eyebrow {
  margin: 0 0 4px;
  color: #f97316;
  font-weight: 800;
  letter-spacing: 0.08em;
}

h2 {
  margin: 0;
  font-size: 28px;
}

.pet-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.pet-card {
  position: relative;
  background: #fff;
  border-radius: 22px;
  padding: 16px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.08);
}

.pet-avatar {
  width: 100%;
  height: 170px;
  object-fit: cover;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff7ed;
  font-size: 54px;
}

.pet-info h3 {
  margin: 14px 0 6px;
  font-size: 22px;
}

.pet-info p {
  margin: 4px 0;
  color: #6b7280;
}

.intro {
  line-height: 1.6;
  word-break: break-word;
}

.pet-actions {
  display: flex;
  gap: 8px;
  margin-top: 14px;
}

.pet-actions button {
  flex: 1;
  border: none;
  border-radius: 999px;
  padding: 9px 12px;
  font-weight: 800;
  cursor: pointer;
}

.edit-btn {
  background: #ecfdf5;
  color: #16875f;
}

.delete-btn {
  background: #fef2f2;
  color: #dc2626;
}

@media (max-width: 980px) {
  .pet-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 680px) {
  .pet-grid {
    grid-template-columns: 1fr;
  }
}
</style>
