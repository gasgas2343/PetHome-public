<script setup>
import { ref, onMounted } from "vue";

import { useRouter } from "vue-router";

import Swal from "sweetalert2";

import { getAllTags } from "@/api/petmap/tagApi";

import { useRoute } from "vue-router";

import {
  getSubmissionDetail,
  updateSubmission,
  resubmitSubmission,
} from "@/api/petmap/placeSubmissionApi";

import { userAuthStore } from "@/stores/auth";

import UserNavbar from "@/components/petmap/UserNavbar.vue";

const authStore = userAuthStore();

const route = useRoute();

const router = useRouter();

const form = ref({
  userId: authStore.id,

  name: "",

  placeType: "",

  address: "",

  phone: "",

  description: "",

  tagIds: [],
});

const tags = ref([]);

onMounted(async () => {
  const [submissionResponse, tagResponse] = await Promise.all([
  getSubmissionDetail(route.params.id),
  getAllTags(),
]);

  const submission = submissionResponse.data.data;

  form.value = {
    userId: authStore.id,
    name: submission.name,
    placeType: submission.placeType,
    address: submission.address,
    phone: submission.phone,
    description: submission.description,
    tagIds: submission.tagIds ?? [],
  };

  tags.value = tagResponse.data.data ?? tagResponse.data;

});


const submit = async () => {
  try {
    await updateSubmission(route.params.id, form.value);

    await resubmitSubmission(route.params.id);

    await Swal.fire({
      icon: "success",

      title: "修改成功",

      text: "已重新送出審核",
    });

    router.push({
  name: "PetMapMySubmission",
});
  } catch (error) {
    await Swal.fire({
      icon: "error",

      title: "修改失敗",

      text: error.response?.data?.message ?? "請稍後再試",
    });
  }
};
</script>

<template>
  <UserNavbar />
  <div class="container mt-4">
    <h2 class="mb-4">編輯投稿</h2>

    <p class="submission-tip">發現新的寵物友善地點嗎？ 歡迎投稿與大家分享。</p>

    <div class="card submission-card">
      <div class="card-body">
        <div class="mb-3">
          <label> 地點名稱 </label>

          <input v-model="form.name" class="form-control" />
        </div>

        <div class="mb-3">
          <label> 地點類型 </label>

          <select v-model="form.placeType" class="form-select">
            <option value="">請選擇</option>

            <option value="CAFE">咖啡廳</option>

            <option value="RESTAURANT">餐廳</option>

            <option value="HOTEL">寵物旅館</option>

            <option value="PARK">公園</option>

            <option value="HOSPITAL">動物醫院</option>
          </select>
        </div>

        <div class="mb-3">
          <label> 地址 </label>

          <input v-model="form.address" class="form-control" />
        </div>

        <div class="mb-3">
          <label> 電話 </label>

          <input v-model="form.phone" class="form-control" />
        </div>

        <div class="mb-4">
          <label class="form-label"> 地點特色 </label>

          <div class="tag-selector">
            <label v-for="tag in tags" :key="tag.tagId" class="tag-option">
              <input type="checkbox" :value="tag.tagId" v-model="form.tagIds" />

              <span>
                {{ tag.name }}
              </span>
            </label>
          </div>
        </div>

        <div class="mb-3">
          <label> 描述 </label>

          <textarea v-model="form.description" rows="4" class="form-control" />
        </div>

        <button class="btn btn-success submit-btn" @click="submit">
          送出投稿
        </button>
      </div>
    </div>
  </div>
</template>
<style scoped>
.tag-selector {
  display: flex;

  flex-wrap: wrap;

  gap: 12px;
}

.tag-option {
  display: flex;

  align-items: center;

  gap: 6px;

  padding: 10px 16px;

  border-radius: 999px;

  background: #f3f4f6;

  cursor: pointer;
}

.submission-tip {
  color: #666;

  margin-bottom: 24px;
}

.submission-card {
  border: none;

  border-radius: 20px;

  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.submit-btn {
  min-width: 140px;

  font-weight: 600;
}
</style>
