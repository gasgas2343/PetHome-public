<script setup>
import { ref } from "vue";

import { useTimelineStore } from "@/petpost/stores/timelineStore";

import TimelineEditModal from "./TimelineEditModal.vue";

const props = defineProps({
  timeline: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["popup-lock-change"]);

const showEdit = ref(false);

const timelineStore = useTimelineStore();

function openEditModal() {
  // 中文註解：通知父層鎖住 Popup，避免滑鼠離開節點時把修改視窗一起卸載。
  showEdit.value = true;
  emit("popup-lock-change", true);
}

function closeEditModal() {
  showEdit.value = false;
  emit("popup-lock-change", false);
}

/**
 * 刪除時間軸
 */
async function removeTimeline() {
  if (!confirm("確定要刪除這筆回憶紀錄嗎？")) {
    return;
  }

  await timelineStore.removeTimeline(
    props.timeline.petPostId,
    props.timeline.petId,
  );
}
</script>

<template>
  <div class="popup-card">
    <!-- 修改 / 刪除操作按鈕 -->
    <div class="timeline-action-bar">
      <button
        type="button"
        class="timeline-action-btn"
        @click.stop="openEditModal"
      >
        <i class="bi bi-pencil"></i>
        <span>修改</span>
      </button>

      <button
        type="button"
        class="timeline-action-btn danger"
        @click.stop="removeTimeline"
      >
        <i class="bi bi-trash3"></i>
        <span>刪除</span>
      </button>
    </div>

    <!-- 時間軸圖片 -->
    <img
      v-if="timeline.imageUrl"
      :src="timeline.imageUrl"
      class="timeline-image"
      alt="毛孩回憶圖片"
    />

    <div class="card-body">
      <div class="timeline-date">
        {{ timeline.postDate }}
      </div>

      <h5 class="timeline-title">
        {{ timeline.title }}
      </h5>

      <p class="timeline-content">
        {{ timeline.content }}
      </p>
    </div>

    <!-- 修改 Modal -->
    <TimelineEditModal
      v-if="showEdit"
      :timeline="timeline"
      @close="closeEditModal"
    />
  </div>
</template>

<style scoped>
.popup-card {
  position: relative;
  z-index: 20;
  width: 280px;
  min-height: 250px;
  padding: 46px 18px 20px;
  background: #fffaf3;
  border: 1px solid #eadfce;
  border-radius: 24px;
  box-shadow: 0 10px 28px rgba(92, 64, 41, 0.12);
  margin: auto;
  overflow: visible;
}

.timeline-action-bar {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 50;
  display: flex;
  gap: 8px;
}

.timeline-action-btn {
  height: 30px;
  padding: 0 10px;
  border: 1px solid #eadfce;
  border-radius: 999px;
  background: #fffdf8;
  color: #5f4b3b;
  font-size: 13px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(92, 64, 41, 0.12);
}

.timeline-action-btn:hover {
  background: #f6efe4;
}

.timeline-action-btn.danger {
  color: #b45b4d;
}

.timeline-image {
  width: 96px;
  height: 96px;
  object-fit: cover;
  border-radius: 20px;
  display: block;
  margin: 0 auto 16px;
}

.card-body {
  padding: 0;
  text-align: center;
}

.timeline-date {
  color: #7b6a5e;
  font-size: 14px;
  margin-bottom: 8px;
}

.timeline-title {
  color: #3f3128;
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 8px;
  word-break: break-word;
}

.timeline-content {
  color: #4f4238;
  font-size: 14px;
  line-height: 1.7;
  margin: 0;
  word-break: break-word;
}
</style>
