<script setup>
import { onMounted, ref } from "vue";

import { useRoute } from "vue-router";

import { getSubmissionDetail } from "@/api/petmap/placeSubmissionApi";

import { useRouter } from "vue-router";

import UserNavbar from "@/components/petmap/UserNavbar.vue";

const router = useRouter();

const route = useRoute();

const submission = ref(null);

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
  const response = await getSubmissionDetail(route.params.id);

  submission.value = response.data.data;
});
</script>

<template>
  <UserNavbar />
  <div class="container mt-4" v-if="submission">
    <div class="card detail-card">
      <div class="card-body">
        <h2 class="mb-3">
          {{ submission.name }}
        </h2>

        <div class="mb-3">
          <span class="info-chip">
            {{ placeTypeMap[submission.placeType] }}
          </span>

          <span
            class="info-chip"
            :class="{
              'bg-warning text-dark': submission.status === 'PENDING',

              'bg-success text-white': submission.status === 'APPROVED',

              'bg-danger text-white': submission.status === 'REJECTED',

              'bg-info text-white': submission.status === 'NEED_REVISION',
            }"
          >
            {{ statusMap[submission.status] }}
          </span>
        </div>

        <p class="mb-3">📍 {{ submission.address }}</p>

        <p v-if="submission.phone" class="mb-3">📞 {{ submission.phone }}</p>

        <div v-if="submission.tags?.length" class="tag-list mb-4">
          <span v-for="tag in submission.tags" :key="tag" class="tag-badge">
            {{ tag }}
          </span>
        </div>

        <div v-if="submission.description" class="mb-4">
          <h5>地點描述</h5>

          <p>
            {{ submission.description }}
          </p>
        </div>

        <div v-if="submission.adminNote" class="alert alert-warning">
          <strong> 管理員備註： </strong>

          {{ submission.adminNote }}
        </div>

        <hr />

        <div class="text-muted">
          <p>
            投稿時間：
            {{ new Date(submission.createdAt).toLocaleString() }}
          </p>

          <p v-if="submission.reviewedAt">
            審核時間：
            {{ new Date(submission.reviewedAt).toLocaleString() }}
          </p>
        </div>
        <div v-if="submission.status === 'NEED_REVISION'" class="mt-4">
          <button
  class="btn btn-warning"
  @click="
    router.push({
      name: 'PetMapEditSubmission',
      params: {
        id: submission.submissionId,
      },
    })
  "
>
  編輯投稿
</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.detail-card {
  border: none;

  border-radius: 20px;

  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.info-chip {
  display: inline-block;

  padding: 8px 14px;

  margin-right: 10px;

  border-radius: 999px;

  background: #f3f4f6;

  font-weight: 600;
}

.tag-list {
  display: flex;

  flex-wrap: wrap;

  gap: 8px;
}

.tag-badge {
  background: #e8f5e9;

  color: #2f855a;

  padding: 6px 12px;

  border-radius: 999px;

  font-weight: 600;
}
</style>
