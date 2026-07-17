<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { usePetStore } from '@/petpost/stores/petStore'

import { useTimelineStore } from '@/petpost/stores/timelineStore'
import TimelineNode from '@/petpost/components/timeline/TimelineNode.vue'

import '@/petpost/assets/petpost-theme.scss'

const route = useRoute()
const petStore = usePetStore()
const timelineStore = useTimelineStore()

const petId = computed(() => {
  const routePetId = route.params.petId

  if (routePetId) {
    return Number(routePetId)
  }

  return petStore.pets[0]?.petId || null
})

const hasPet = computed(() => petStore.pets.length > 0)

onMounted(async () => {
  await petStore.loadPets()

  if (!petId.value) {
    timelineStore.errorMessage = '尚未建立毛孩資料，請先新增毛孩後，再新增回憶紀錄'
    return
  }

  await timelineStore.loadTimelinesByPetId(petId.value)
})
</script>

<template>
  <main class="timeline-page">
    <section class="timeline-shell">
      <RouterLink to="/pets" class="back-link"> ← 返回我的寵物 </RouterLink>

      <div class="timeline-header">
        <div>
          <p class="kicker">Pet Timeline</p>
          <h1>寵物成長時間軸</h1>
          <p class="desc">記錄毛孩的生活日常、成長時刻與重要回憶。</p>
        </div>
      </div>

      <div v-if="timelineStore.loading" class="state-text">載入中...</div>

      <div v-if="timelineStore.errorMessage" class="alert alert-danger">
        {{ timelineStore.errorMessage }}
      </div>

      <div v-if="!hasPet" class="alert alert-warning">
        尚未建立毛孩資料，請先新增毛孩後，再新增回憶紀錄。
        <RouterLink to="/pets" class="btn btn-sm btn-primary ms-2"> 前往新增毛孩 </RouterLink>
      </div>

      <div v-else class="timeline-wrapper">
        <TimelineNode
          v-for="timeline in timelineStore.timelines"
          :key="timeline.petPostId"
          :timeline="timeline"
          :pet-id="petId"
        />

        <TimelineNode :timeline="null" :pet-id="petId" />
      </div>
    </section>
  </main>
</template>

<style scoped>
.timeline-page {
  min-height: 100vh;
  padding: 48px 20px 72px;
  background: transparent;
}

.timeline-shell {
  width: min(1080px, 100%);
  margin: 0 auto;
}

.back-link {
  display: inline-flex;
  margin-bottom: 24px;
  color: #8a5b28;
  font-weight: 900;
  text-decoration: none;
}

.back-link:hover {
  text-decoration: underline;
}

.timeline-header {
  margin-bottom: 48px;
}

.kicker {
  margin: 0 0 8px;
  color: #d6a158;
  font-weight: 900;
  letter-spacing: 0.08em;
}

.timeline-header h1 {
  margin: 0;
  color: #3f332b;
  font-size: 40px;
  font-weight: 900;
}

.desc {
  margin: 12px 0 0;
  color: #6f6258;
  line-height: 1.7;
}

.state-text {
  color: #6f6258;
}

.timeline-wrapper {
  display: flex;
  align-items: flex-start;
  gap: 90px;
  position: relative;
  min-height: 420px;
  padding-top: 30px;
  overflow-x: auto;
}

.timeline-wrapper::before {
  content: '';
  position: absolute;
  top: 42px;
  left: 0;
  width: 100%;
  min-width: 900px;
  height: 4px;
  background-color: #dcdcdc;
  z-index: 1;
}
</style>
