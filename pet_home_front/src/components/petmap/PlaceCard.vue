<script setup>
import FavoriteButton from './FavoriteButton.vue'

import { userAuthStore } from '@/stores/auth'

const authStore = userAuthStore()

defineProps({
  place: Object,
})

const placeTypeMap = {
  CAFE: '☕ 咖啡廳',
  RESTAURANT: '🍽️ 餐廳',
  PARK: '🌳 公園',
  HOTEL: '🏨 寵物旅館',
  HOSPITAL: '🏥 動物醫院',
}

const emit = defineEmits(['favorite-removed', 'card-click'])

const handleImageError = (event) => {
  event.target.src = 'https://picsum.photos/600/400'
}
</script>

<template>
  <div class="place-card">
    <div class="d-flex justify-content-between align-items-start mb-3">
      <h3 class="place-title clickable" @click="emit('card-click', place.placeId)">
        {{ place.name }}
      </h3>

      <div @click.stop>
        <FavoriteButton
          :user-id="authStore.id"
          :place-id="place.placeId"
          @favorite-removed="emit('favorite-removed', $event)"
        />
      </div>
    </div>

    <img
      :src="place.coverImageUrl || 'https://picsum.photos/600/400'"
      class="place-image clickable"
      @error="handleImageError"
      @click="emit('card-click', place.placeId)"
    />

    <div class="place-info">
      <p>
        📍 {{ place.address }}
      </p>

      <div class="place-badges">
        <span class="place-badge">
          {{ placeTypeMap[place.placeType] || place.placeType }}
        </span>

        <span class="place-badge">
          ⭐
          {{ place.ratingAvg ? Number(place.ratingAvg).toFixed(1) : '尚無評價' }}
        </span>

        <span class="place-badge">
          🐾
          {{
            place.ratingPetFriendly
              ? Number(place.ratingPetFriendly).toFixed(1)
              : '尚無評價'
          }}
        </span>

        <span class="place-badge">
          💬
          {{ place.reviewCount > 0 ? place.reviewCount : '尚無評論' }}
        </span>
      </div>

      <div v-if="place.tags && place.tags.length" class="tag-list">
        <span
          v-for="tag in place.tags"
          :key="tag"
          class="tag-badge"
        >
          {{ tag }}
        </span>
      </div>
    </div>

    <RouterLink
      :to="{
        name: 'PetMapPlaceDetail',
        params: { id: place.placeId },
      }"
      class="btn detail-btn"
      @click.stop
    >
      查看詳情 →
    </RouterLink>
  </div>
</template>

<style scoped>
.place-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  border: none;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.place-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.12);
}

.place-title {
  color: #2f855a;
  font-weight: 700;
  margin: 0;
}

.place-info p {
  color: #666;
  margin-bottom: 10px;
  font-size: 1rem;
}

.place-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.place-badge {
  background: #f3ede3;
  color: #6d4c41;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 0.9rem;
  font-weight: 600;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
  margin-bottom: 22px;
}

.tag-badge {
  background: #e8f5e9;
  color: #2f855a;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 600;
}

.detail-btn {
  display: inline-block;
  margin-top: 6px;
  border-radius: 12px;
  padding: 10px 20px;
  border: 2px solid #2f855a;
  color: #2f855a;
  font-weight: 600;
}

.detail-btn:hover {
  background: #2f855a;
  color: white;
}

.place-image {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 16px;
  margin-bottom: 16px;
}

.clickable {
  cursor: pointer;
}

.clickable:hover {
  opacity: 0.85;
}

.place-title.clickable:hover {
  color: #1f6f43;
}

.place-card.active {
  border: 2px solid #2f855a;
  box-shadow:
    0 0 0 4px rgba(47, 133, 90, 0.12),
    0 12px 28px rgba(47, 133, 90, 0.25);
  transform: translateY(-4px) scale(1.02);
  transition: all 0.35s ease;
  animation: highlightCard 0.6s ease;
}

@keyframes highlightCard {
  0% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.03);
  }

  100% {
    transform: scale(1.02);
  }
}
</style>
