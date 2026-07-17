<script setup>
import { ref } from "vue";

import TimelinePopupCard from "@/petpost/components/timeline/TimelinePopupCard.vue";
import TimelineInsertModal from "@/petpost/components/timeline/TimelineInsertModal.vue";

defineProps({
  // 中文註解：有資料時是一筆 timeline，新增節點時為 null。
  timeline: {
    type: Object,
    default: null,
  },

  // 中文註解：目前毛孩 ID。
  petId: {
    type: Number,
    required: true,
  },
});

const showCard = ref(false);
const showInsertModal = ref(false);

// 中文註解：當修改視窗開啟時，鎖住 Popup，不讓 mouseleave 把 Popup 銷毀。
const lockPopupCard = ref(false);

function openInsertModal() {
  showInsertModal.value = true;
  lockPopupCard.value = true;
}

function closeInsertModal() {
  showInsertModal.value = false;
  lockPopupCard.value = false;
  showCard.value = false;
}

function handleMouseLeave() {
  // 中文註解：Modal 開啟時不要關閉 Popup，避免子元件被卸載導致修改視窗消失。
  if (lockPopupCard.value || showInsertModal.value) {
    return;
  }

  showCard.value = false;
}

function handlePopupLockChange(isLocked) {
  lockPopupCard.value = isLocked;

  // 中文註解：修改視窗關閉後，主動收起卡片，避免卡片殘留在畫面上。
  if (!isLocked) {
    showCard.value = false;
  }
}
</script>

<template>
  <div
    class="timeline-node"
    @mouseenter="showCard = true"
    @mouseleave="handleMouseLeave"
  >
    <!-- 中文註解：有資料的節點，只顯示時間軸卡片。 -->
    <template v-if="timeline">
      <div class="circle filled-circle"></div>

      <TimelinePopupCard
        v-if="showCard"
        :timeline="timeline"
        @popup-lock-change="handlePopupLockChange"
      />
    </template>

    <!-- 中文註解：新增節點。Modal 開啟時，只隱藏文字和按鈕，避免文字浮到 Modal 上。 -->
    <template v-else>
      <div class="circle empty-circle"></div>

      <div
        v-if="!showInsertModal"
        class="add-text"
      >
        新增
      </div>

      <button
        v-if="showCard && !showInsertModal"
        class="btn btn-success btn-sm mt-2"
        @click.stop="openInsertModal"
      >
        新增時間軸
      </button>
    </template>
  </div>

  <!-- 中文註解：Modal 放在節點外面，避免被節點的 hover / z-index 影響。 -->
  <TimelineInsertModal
    v-if="showInsertModal"
    :pet-id="petId"
    @close="closeInsertModal"
  />
</template>

<style scoped>
.timeline-node {
  position: relative;
  z-index: 2;
  min-width: 80px;
  text-align: center;
}

.circle {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  margin: 0 auto;
  transition: 0.2s;
}

.filled-circle {
  background-color: #198754;
  border: 4px solid #198754;
}

.empty-circle {
  background-color: white;
  border: 4px solid #198754;
}

.timeline-node:hover .circle {
  transform: scale(1.2);
}

.add-text {
  margin-top: 8px;
  color: #6c757d;
  font-size: 14px;
}
</style>
