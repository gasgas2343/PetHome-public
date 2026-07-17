<script setup>
import { useRouter } from 'vue-router'
import { FALLBACK_IMAGE } from '@/petpost/data/cloudinaryImages'

const props = defineProps({
  pets: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['delete'])
const router = useRouter()

function goTimeline(petId) {
  router.push(`/pets/${petId}/timeline`)
}

function deletePet(pet) {
  const petName = pet.petName || pet.name || '這隻毛孩'
  const ok = confirm(`確定要刪除「${petName}」嗎？`)
  if (!ok) return
  emit('delete', pet.petId ?? pet.id)
}

function handleImageError(event) {
  event.target.src = FALLBACK_IMAGE
}
</script>

<template>
  <div class="row">
    <div class="col-md-6 mb-3" v-for="pet in props.pets" :key="pet.petId ?? pet.id">
      <div class="card h-100 shadow-sm">
        <div class="card-body d-flex gap-3 align-items-center">
          <img
            :src="pet.petAvatarUrl || pet.avatarUrl || FALLBACK_IMAGE"
            alt="寵物照片"
            class="pet-avatar"
            @error="handleImageError"
          />

          <div class="flex-grow-1">
            <h5 class="mb-1">{{ pet.petName || pet.name }}</h5>
            <div class="text-secondary small">
              {{ pet.breed || '未填品種' }}｜{{ pet.gender || '未填性別' }}
            </div>
            <p class="mb-2">{{ pet.description || pet.petIntro }}</p>

            <div class="d-flex gap-2 flex-wrap">
              <button class="btn btn-outline-warning btn-sm" @click="goTimeline(pet.petId ?? pet.id)">
                查看寵物回憶錄
              </button>

              <button class="btn btn-outline-danger btn-sm" @click="deletePet(pet)">
                刪除毛孩
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.pet-avatar {
  width: 96px;
  height: 96px;
  object-fit: cover;
  border-radius: 50%;
}
</style>
