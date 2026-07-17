<script setup>
import { ref } from "vue";

import { useTimelineStore } from "@/petpost/stores/timelineStore";
import CloudImageUrlInput from "@/petpost/components/timeline/CloudImageUrlInput.vue";
import DefaultImageSelector from "@/petpost/components/timeline/DefaultImageSelector.vue";

const props = defineProps({
  petId: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits(["close"]);

const timelineStore = useTimelineStore();

const title = ref("");
const content = ref("");
const postDate = ref(new Date().toISOString().slice(0, 10));
const imageUrl = ref("");
const saving = ref(false);

function validateForm() {
  if (!props.petId) {
    alert("找不到毛孩ID，無法新增回憶。請先從寵物清單進入回憶錄。");
    return false;
  }

  if (!postDate.value) {
    alert("請選擇日期。");
    return false;
  }

  if (!title.value.trim()) {
    alert("請輸入標題。");
    return false;
  }

  if (title.value.trim().length > 100) {
    alert("標題不可超過100字。");
    return false;
  }

  if (content.value.length > 1000) {
    alert("內容不可超過1000字。");
    return false;
  }

  return true;
}

async function save() {
  if (!validateForm()) return;

  saving.value = true;

  try {
    const dto = {
      petId: Number(props.petId),
      title: title.value.trim(),
      content: content.value.trim(),
      postDate: postDate.value,
      imageUrl: imageUrl.value || "",
    };

    await timelineStore.addTimeline(dto);

    alert("新增回憶成功");
    emit("close");
  } catch (error) {
    const message =
      error.response?.data?.message ||
      error.response?.data?.error ||
      "新增時間軸失敗，請確認資料是否正確。";

    console.warn("新增時間軸失敗：", message);
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
        <h4 class="mb-3">新增回憶紀錄</h4>

        <input v-model="postDate" type="date" class="form-control mb-3" />

        <input
          v-model="title"
          type="text"
          class="form-control mb-3"
          placeholder="標題，例如：第一次洗澡"
        />

        <textarea
          v-model="content"
          class="form-control mb-3"
          rows="4"
          placeholder="內容"
        />

        <CloudImageUrlInput v-model="imageUrl" />
        <DefaultImageSelector @select="imageUrl = $event" />
        <p class="upload-hint mt-2">
          可貼上 Cloudinary 圖片網址，或從預設圖片中選擇一張。
        </p>

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
            class="btn btn-success"
            :disabled="saving"
            @click="save"
          >
            {{ saving ? "儲存中..." : "新增" }}
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

.upload-hint {
  color: #7b6a5e;
  font-size: 13px;
}
</style>
