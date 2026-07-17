<script setup>
import { ref } from "vue";
import { onMounted } from "vue";

import { getMyReports } from "@/api/petmap/reportApi";
import { userAuthStore } from "@/stores/auth";

import UserNavbar from "@/components/petmap/UserNavbar.vue";

const authStore = userAuthStore();

const reports = ref([]);

const loadReports = async () => {
  const response = await getMyReports(authStore.id);

  reports.value = response.data.data;
};

const reasonMap = {
  SPAM: "垃圾訊息",
  FAKE: "不實內容",
  OFFENSIVE: "攻擊言論",
  OTHER: "其他",
};

const statusMap = {
  PENDING: "🟡 待處理",
  APPROVED: "🟢 檢舉成立",
  REJECTED: "🔴 已駁回",
};

const formatDate = (dateString) => {
  const date = new Date(dateString);

  return (
    date.getFullYear() +
    "/" +
    String(date.getMonth() + 1).padStart(2, "0") +
    "/" +
    String(date.getDate()).padStart(2, "0") +
    " " +
    String(date.getHours()).padStart(2, "0") +
    ":" +
    String(date.getMinutes()).padStart(2, "0")
  );
};

onMounted(async () => {
  await loadReports();
});
</script>
<template>
  <UserNavbar />
  <div class="container mt-4">
    <h1>我的檢舉紀錄</h1>

    <div v-for="report in reports" :key="report.reportId" class="card mt-3">
      <div class="card-body">
        <h5>
          檢舉原因：
          {{ reasonMap[report.reason] || report.reason }}
        </h5>

        <p>
          描述：
          {{ report.description }}
        </p>

        <p>
          狀態：
          {{ statusMap[report.status] || report.status }}
        </p>

        <small>
          建立時間：
          {{ formatDate(report.createdAt) }}
        </small>
      </div>
    </div>
  </div>
</template>
