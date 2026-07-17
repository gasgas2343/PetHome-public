<script setup>
import { ref, onMounted } from 'vue'
import TimelineCard from '@/petpost/components/forum/TimelineCard.vue'
import LocalImageUpload from '@/petpost/components/forum/LocalImageUpload.vue'
import CloudImageInput from '@/petpost/components/forum/CloudImageInput.vue'
import { getPetTimeline, createPetTimeline, uploadLocalPetImage, saveCloudPetImage } from '@/petpost/api/forumApi'

const petId = ref(1)
const timelines = ref([])
const localFile = ref(null)
const cloudImageUrl = ref('')

const form = ref({
  petId: 1,
  postType: 'DIARY',
  title: '',
  content: '',
  eventDate: ''
})

async function loadTimeline() {
  try {
    const res = await getPetTimeline(petId.value)
    timelines.value = Array.isArray(res.data) ? res.data : []
  } catch (error) {
    console.error('取得時間軸失敗', error)
  }
}

async function submitTimeline() {
  const res = await createPetTimeline(form.value)
  const petPostId = res.data?.petPostId || res.data?.pet_post_id

  if (petPostId && localFile.value) {
    await uploadLocalPetImage(petPostId, localFile.value)
  }

  if (petPostId && cloudImageUrl.value) {
    await saveCloudPetImage(petPostId, cloudImageUrl.value)
  }

  form.value.title = ''
  form.value.content = ''
  form.value.eventDate = ''
  localFile.value = null
  cloudImageUrl.value = ''
  await loadTimeline()
}

onMounted(loadTimeline)
</script>

<template>
  <main class="timeline-page">
    <section class="timeline-form">
      <h1>新增毛孩時間軸</h1>

      <input v-model="form.title" placeholder="標題" />
      <textarea v-model="form.content" placeholder="記錄今天的毛孩故事"></textarea>
      <input v-model="form.eventDate" type="date" />

      <div class="image-source-grid">
        <LocalImageUpload @select="localFile = $event" />
        <CloudImageInput @change="cloudImageUrl = $event" />
      </div>

      <button class="submit-btn" @click="submitTimeline">新增時間軸</button>
    </section>

    <section class="timeline-grid">
      <TimelineCard
        v-for="item in timelines"
        :key="item.petPostId || item.pet_post_id"
        :timeline="item"
      />
    </section>
  </main>
</template>

<style scoped>
.timeline-page {
  max-width: 1080px;
  margin: 0 auto;
  padding: 36px 20px 70px;
}

.timeline-form {
  background: #fff7ed;
  border-radius: 28px;
  padding: 28px;
  margin-bottom: 30px;
}

.timeline-form input,
.timeline-form textarea {
  width: 100%;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 12px 14px;
  margin-bottom: 12px;
}

.timeline-form textarea {
  min-height: 120px;
  resize: vertical;
}

.image-source-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin: 12px 0;
}

.submit-btn {
  border: 0;
  border-radius: 999px;
  background: #f97316;
  color: #fff;
  padding: 12px 24px;
  font-weight: 700;
}

.timeline-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 22px;
}

@media (max-width: 800px) {
  .image-source-grid,
  .timeline-grid {
    grid-template-columns: 1fr;
  }
}
</style>
