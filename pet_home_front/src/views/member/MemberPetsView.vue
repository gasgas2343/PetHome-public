<script setup>
import { onMounted, ref } from 'vue'
import { getPetApi, createPetApi, updatePetApi, deletePetApi } from '@/api/user'

import PetList from '@/components/member/pets/PetList.vue'
import PetFormModal from '@/components/member/pets/PetFormModal.vue'
import PetDetailModal from '@/components/member/pets/PetDetailModal.vue'
import ConfirmModal from '@/components/common/ConfirmModal.vue'

const pets = ref([])
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const apiError = ref('')

const showFormModal = ref(false)
const showDetailModal = ref(false)
const showDeleteModal = ref(false)

const selectedPet = ref(null)
const editingPet = ref(null)
const deletingPet = ref(null)

const petTypeOptions = [
  { id: 1, name: '狗' },
  { id: 2, name: '貓' },
]

async function loadPets() {
  loading.value = true
  apiError.value = ''

  try {
    const res = await getPetApi()
    console.log('寵物列表回應：', res.data)

    pets.value = res.data.data || []
  } catch (error) {
    console.error('取得寵物資料失敗', error)
    console.error('後端錯誤內容：', error.response?.data)

    apiError.value = error.response?.data?.message || '取得寵物資料失敗'
  } finally {
    loading.value = false
  }
}

function openCreateModal() {
  apiError.value = ''
  editingPet.value = null
  showFormModal.value = true
}

function openEditModal(pet) {
  apiError.value = ''
  editingPet.value = pet
  showDetailModal.value = false
  showFormModal.value = true
}

function closeFormModal() {
  showFormModal.value = false
  editingPet.value = null
  apiError.value = ''
}

function openDetailModal(pet) {
  selectedPet.value = pet
  showDetailModal.value = true
}

function closeDetailModal() {
  showDetailModal.value = false
  selectedPet.value = null
}

function openDeleteModal(pet) {
  deletingPet.value = pet
  showDeleteModal.value = true
}

function closeDeleteModal() {
  showDeleteModal.value = false
  deletingPet.value = null
}

async function handleSubmitPet(formData) {
  saving.value = true
  apiError.value = ''

  try {
    if (editingPet.value?.id) {
      await updatePetApi(editingPet.value.id, formData)
    } else {
      await createPetApi(formData)
    }

    closeFormModal()
    await loadPets()
  } catch (error) {
    console.error('儲存寵物資料失敗', error)
    console.error('後端錯誤內容：', error.response?.data)

    const errorData = error.response?.data?.data

    apiError.value =
      errorData?.name ||
      errorData?.petTypeId ||
      errorData?.gender ||
      errorData?.birthday ||
      errorData?.breed ||
      errorData?.weightKg ||
      errorData?.bodySize ||
      errorData?.personality ||
      error.response?.data?.message ||
      '儲存寵物資料失敗'
  } finally {
    saving.value = false
  }
}

async function handleDeletePet() {
  if (!deletingPet.value?.id) {
    return
  }

  deleting.value = true
  apiError.value = ''

  try {
    await deletePetApi(deletingPet.value.id)

    closeDeleteModal()
    await loadPets()
  } catch (error) {
    console.error('刪除寵物失敗', error)
    console.error('後端錯誤內容：', error.response?.data)

    apiError.value = error.response?.data?.message || '刪除寵物失敗'
  } finally {
    deleting.value = false
  }
}

onMounted(() => {
  loadPets()
})
</script>

<template>
  <section class="member-pets">
    <div class="section-head">
      <div>
        <p class="section-kicker">Pet Profile</p>
        <h1>寵物資料</h1>
        <p class="description">完善毛孩資料，讓我們更了解你的需求，也讓後續服務安排更準確。</p>
      </div>

      <button type="button" class="add-pet-btn" @click="openCreateModal">新增寵物</button>
    </div>

    <div v-if="loading" class="state-card">
      <p>讀取中...</p>
    </div>

    <div v-else>
      <p v-if="apiError && !showFormModal && !showDeleteModal" class="error-text">
        {{ apiError }}
      </p>

      <PetList
        :pets="pets"
        :pet-type-options="petTypeOptions"
        @view="openDetailModal"
        @edit="openEditModal"
        @delete="openDeleteModal"
      />
    </div>

    <PetFormModal
      v-if="showFormModal"
      :pet="editingPet"
      :pet-type-options="petTypeOptions"
      :saving="saving"
      :api-error="apiError"
      @close="closeFormModal"
      @submit="handleSubmitPet"
    />

    <PetDetailModal
      v-if="showDetailModal"
      :pet="selectedPet"
      :pet-type-options="petTypeOptions"
      @close="closeDetailModal"
      @edit="openEditModal"
    />

    <ConfirmModal
      v-if="showDeleteModal"
      title="刪除寵物資料"
      :message="`確定要刪除「${deletingPet?.name || '這筆寵物資料'}」嗎？這個動作完成後不會顯示在列表中。`"
      confirm-text="確認刪除"
      cancel-text="先保留"
      :loading="deleting"
      danger
      @cancel="closeDeleteModal"
      @confirm="handleDeletePet"
    />
  </section>
</template>

<style scoped>
.member-pets {
  padding: 4px 0 0;
  border-radius: 0;
  background: transparent;
  border: 0;
  box-shadow: none;
  color: #4f3928;
}

.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 28px;
  margin-bottom: 28px;
}

.section-kicker {
  margin: 0 0 8px;
  color: #b9854f;
  font-size: 13px;
  font-weight: 900;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.description {
  margin: 10px 0 0;
  color: #8b6d50;
  font-size: 15px;
  line-height: 1.7;
}

h1 {
  margin: 0;
  font-size: 32px;
  font-weight: 950;
  letter-spacing: 0.04em;
}

.add-pet-btn {
  flex: 0 0 auto;
  border: 0;
  border-radius: 999px;
  padding: 13px 20px;
  background: #7ca6a0;
  color: #fff;
  font-size: 16px;
  font-weight: 900;
  cursor: pointer;
  box-shadow: 0 12px 26px rgba(124, 166, 160, 0.24);
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease;
}

.add-pet-btn:hover {
  transform: translateY(-2px);
  background: #719c96;
  box-shadow: 0 16px 30px rgba(124, 166, 160, 0.3);
}

.state-card {
  padding: 30px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.78);
  border: 1px solid rgba(138, 113, 89, 0.14);
}

.state-card p {
  margin: 0;
  color: #7a6655;
  font-weight: 700;
}

.error-text {
  margin: 0 0 16px;
  padding: 12px 14px;
  border-radius: 16px;
  background: rgba(199, 83, 69, 0.08);
  color: #b94d42;
  font-size: 14px;
  font-weight: 800;
}

@media (max-width: 720px) {
  .member-pets {
    padding: 24px;
    border-radius: 26px;
  }

  .section-head {
    flex-direction: column;
  }

  .add-pet-btn {
    width: 100%;
  }
}
</style>
