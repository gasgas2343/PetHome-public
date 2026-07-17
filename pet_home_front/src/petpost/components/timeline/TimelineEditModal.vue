<script setup>
import { ref } from "vue";

import { useTimelineStore } from "@/petpost/stores/timelineStore";
import CloudImageUrlInput from "@/petpost/components/timeline/CloudImageUrlInput.vue";
import DefaultImageSelector from "@/petpost/components/timeline/DefaultImageSelector.vue";

const props = defineProps({
  timeline: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["close"]);

const timelineStore = useTimelineStore();

const title = ref(props.timeline.title || "");
const content = ref(props.timeline.content || "");
const postDate = ref(props.timeline.postDate || new Date().toISOString().slice(0, 10));
const imageUrl = ref(props.timeline.imageUrl || "");
const saving = ref(false);

async function save() {
  if (!title.value.trim()) {
    alert("請輸入標題。");
    return;
  }

  saving.value = true;

  try {
    const dto = {
      title: title.value.trim(),
      content: content.value.trim(),
      postDate: postDate.value,
      imageUrl: imageUrl.value || "",
      petId: props.timeline.petId,
    };

    await timelineStore.editTimeline(props.timeline.petPostId, dto);

    if (props.timeline.petId) {
      await timelineStore.loadTimelinesByPetId(props.timeline.petId);
    }

    emit("close");
  } catch (error) {
    const message =
      error.response?.data?.message ||
      error.response?.data?.error ||
      "修改時間軸失敗，請確認資料是否正確。";

    console.warn("修改時間軸失敗：", message);
    alert(message);
  } finally {
    saving.value = false;
  }
}
</script>

<template>
  <Teleport to="body">
    <div class="modal-mask">
      <div class="modal-card">
        <h4 class="mb-3">修改時間軸</h4>

        <input v-model="postDate" type="date" class="form-control mb-3" />
        <input v-model="title" type="text" class="form-control mb-3" />
        <textarea v-model="content" class="form-control mb-3" rows="4" />

        <CloudImageUrlInput v-model="imageUrl" />
        <DefaultImageSelector @select="imageUrl = $event" />

        <div class="text-end mt-4">
          <button
            type="button"
            class="btn btn-secondary me-2"
            :disabled="saving"
            @click="emit('close')"
          >
            取消
          </button>

          <button
            type="button"
            class="btn btn-primary"
            :disabled="saving"
            @click="save"
          >
            {{ saving ? "儲存中..." : "儲存修改" }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 99990;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(47, 36, 29, 0.58);
  backdrop-filter: none;
}

.modal-card {
  position: relative;
  z-index: 99991;
  width: 520px;
  max-width: calc(100vw - 32px);
  max-height: calc(100vh - 32px);
  overflow-y: auto;
  background: #fffaf3;
  border: 1px solid #ead8c4;
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 24px 60px rgba(47, 36, 29, 0.28);
}

.form-control {
  background: #ffffff;
  border: 1px solid #ead8c4;
  border-radius: 12px;
}
</style>
