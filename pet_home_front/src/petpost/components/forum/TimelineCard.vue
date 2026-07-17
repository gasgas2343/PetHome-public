<script setup>
defineProps({
  timeline: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <article class="timeline-card">
    <div class="timeline-date">
      {{ timeline.eventDate || timeline.event_date || timeline.createdAt || timeline.created_at }}
    </div>

    <img
      v-if="timeline.imageUrl || timeline.image_url"
      class="timeline-image"
      :src="timeline.imageUrl || timeline.image_url"
      :alt="timeline.title"
    />

    <div class="timeline-content">
      <span class="timeline-type">{{ timeline.postType || timeline.post_type || 'DIARY' }}</span>
      <h3>{{ timeline.title }}</h3>
      <p>{{ timeline.content }}</p>
    </div>
  </article>
</template>

<style scoped>
.timeline-card {
  position: relative;
  background: #fff;
  border-radius: 22px;
  overflow: hidden;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.08);
}

.timeline-date {
  padding: 14px 18px 0;
  color: #f97316;
  font-weight: 700;
  font-size: 13px;
}

.timeline-image {
  width: 100%;
  height: 230px;
  object-fit: cover;
  margin-top: 12px;
}

.timeline-content {
  padding: 18px;
}

.timeline-type {
  color: #6b7280;
  font-size: 13px;
}

.timeline-content h3 {
  margin: 8px 0;
  font-size: 20px;
}

.timeline-content p {
  color: #6b7280;
  line-height: 1.7;
}
</style>
