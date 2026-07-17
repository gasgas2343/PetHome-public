<script setup>
import { onMounted, ref } from "vue";

import { getSubmissionsByUserId } from "@/api/petmap/placeSubmissionApi";

import { userAuthStore } from "@/stores/auth";

import UserNavbar from "@/components/petmap/UserNavbar.vue";

const authStore = userAuthStore();

const submissions = ref([]);

const statusMap = {
  PENDING: "審核中",

  APPROVED: "已通過",

  REJECTED: "已拒絕",

  NEED_REVISION: "需補件",
};

const placeTypeMap = {
  CAFE: "☕ 咖啡廳",
  RESTAURANT: "🍽️ 餐廳",
  HOTEL: "🏨 寵物旅館",
  PARK: "🌳 公園",
  HOSPITAL: "🏥 動物醫院",
};

onMounted(async () => {
  const response = await getSubmissionsByUserId(authStore.id);

  submissions.value = response.data.data;
});
</script>

<template>
  <UserNavbar />
  <div class="container mt-4">
    <h2 class="mb-4">我的投稿</h2>

    <div
      v-for="submission in submissions"
      :key="submission.submissionId"
      class="card submission-card mb-4"
      @click="
  $router.push({
    name: 'PetMapSubmissionDetail',
    params: {
      id: submission.submissionId,
    },
  })
"
    >
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-start">
          <div>
            <h4 class="fw-bold mb-2">
              {{ submission.name }}
            </h4>

            <p class="text-muted mb-2">
              {{ placeTypeMap[submission.placeType] }}
            </p>

            <p class="mb-3">📍 {{ submission.address }}</p>
          </div>

          <span
            class="badge"
            :class="{
              'bg-warning': submission.status === 'PENDING',

              'bg-success': submission.status === 'APPROVED',

              'bg-danger': submission.status === 'REJECTED',

              'bg-info': submission.status === 'NEED_REVISION',
            }"
          >
            {{ statusMap[submission.status] }}
          </span>
        </div>

        <div v-if="submission.adminNote" class="alert alert-warning mt-3 mb-0">
          <strong> 管理員備註： </strong>

          {{ submission.adminNote }}
        </div>

        <div class="text-muted mt-3 small">
          投稿時間：

          {{ new Date(submission.createdAt).toLocaleString() }}
        </div>
      </div>
    </div>

    <div v-if="submissions.length === 0" class="text-center text-muted mt-5">
      尚無投稿紀錄
    </div>
  </div>
</template>
<style scoped>
.submission-card {
  border: none;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.2s ease;
}

.submission-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}
</style>
