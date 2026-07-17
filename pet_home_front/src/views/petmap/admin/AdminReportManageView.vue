<script setup>
import { ref, onMounted, computed } from "vue";

import AdminNavbar from "@/components/petmap/AdminNavbar.vue";

import {
  getAllReports,
  approveReport,
  rejectReport,
  getReportStatistics,
} from "@/api/petmap/reportApi";

import Swal from "sweetalert2";

const selectedStatus = ref("ALL");

const reports = ref([]);

const statistics = ref(null);

const loadReports = async () => {
  const response = await getAllReports();

  reports.value = response.data.data;
};

const reasonMap = {
  SPAM: "垃圾廣告",

  FAKE: "不實評論",

  OFFENSIVE: "不當內容",
};

const statusMap = {
  PENDING: "待審核",

  APPROVED: "已成立",

  REJECTED: "已駁回",
};

const handleView = async (report) => {
  await Swal.fire({
    title: "檢舉內容",

    html: `
      <div
        style="
          text-align:left
        "
      >

        <p>
          <b>檢舉原因：</b>
          ${reasonMap[report.reason]}
        </p>

        <p>
          <b>補充說明：</b>
          ${report.description || "無"}
        </p>

        <p>
          <b>狀態：</b>
          ${statusMap[report.status]}
        </p>

      </div>
    `,
  });
};

const handleApprove = async (reportId) => {
  const result = await Swal.fire({
    title: "確認成立檢舉？",

    input: "text",

    inputLabel: "管理員備註",

    showCancelButton: true,
  });

  if (!result.isConfirmed) {
    return;
  }

  await approveReport(reportId, result.value);

  await loadReports();

  await loadStatistics();
};

const handleReject = async (reportId) => {
  const result = await Swal.fire({
    title: "確認駁回檢舉？",

    input: "text",

    inputLabel: "管理員備註",

    showCancelButton: true,
  });

  if (!result.isConfirmed) {
    return;
  }

  await rejectReport(reportId, result.value);

  await loadReports();

  await loadStatistics();
};

const loadStatistics = async () => {
  const response = await getReportStatistics();

  statistics.value = response.data.data;
};

onMounted(async () => {
  await loadReports();

  await loadStatistics();
});

const filteredReports = computed(() => {
  if (selectedStatus.value === "ALL") {
    return reports.value;
  }

  return reports.value.filter(
    (report) => report.status === selectedStatus.value,
  );
});
</script>

<template>
  <AdminNavbar />

  <div class="container mt-4">
    <h1>檢舉管理</h1>

    <div v-if="statistics" class="row mb-4">
      <div class="col-md-4">
        <div class="card text-center border-warning">
          <div class="card-body">
            <h5>待審核</h5>

            <h2>
              {{ statistics.pending }}
            </h2>
          </div>
        </div>
      </div>

      <div class="col-md-4">
        <div class="card text-center border-success">
          <div class="card-body">
            <h5>已成立</h5>

            <h2>
              {{ statistics.approved }}
            </h2>
          </div>
        </div>
      </div>

      <div class="col-md-4">
        <div class="card text-center border-danger">
          <div class="card-body">
            <h5>已駁回</h5>

            <h2>
              {{ statistics.rejected }}
            </h2>
          </div>
        </div>
      </div>
    </div>
    <div class="mb-4">
      <button
        :class="
          selectedStatus === 'ALL'
            ? 'btn btn-secondary me-2'
            : 'btn btn-outline-secondary me-2'
        "
        @click="selectedStatus = 'ALL'"
      >
        全部
      </button>

      <button
        :class="
          selectedStatus === 'PENDING'
            ? 'btn btn-warning me-2'
            : 'btn btn-outline-warning me-2'
        "
        @click="selectedStatus = 'PENDING'"
      >
        待審核
      </button>

      <button
        :class="
          selectedStatus === 'APPROVED'
            ? 'btn btn-success me-2'
            : 'btn btn-outline-success me-2'
        "
        @click="selectedStatus = 'APPROVED'"
      >
        已成立
      </button>

      <button
        :class="
          selectedStatus === 'REJECTED'
            ? 'btn btn-danger'
            : 'btn btn-outline-danger'
        "
        @click="selectedStatus = 'REJECTED'"
      >
        已駁回
      </button>
    </div>

    <table class="table table-bordered">
      <thead>
        <tr>
          <th>ID</th>

          <th>檢舉原因</th>

          <th>補充說明</th>

          <th>狀態</th>

          <th>操作</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="report in filteredReports" :key="report.reportId">
          <td>
            {{ report.reportId }}
          </td>

          <td>
            {{ reasonMap[report.reason] }}
          </td>

          <td>
            {{ report.description || "無補充說明" }}
          </td>

          <td>
            {{ statusMap[report.status] }}
          </td>

          <td>
            <button
              class="btn btn-primary btn-sm me-2"
              @click="handleView(report)"
            >
              查看
            </button>

            <template v-if="report.status === 'PENDING'">
              <button
                class="btn btn-success btn-sm me-2"
                @click="handleApprove(report.reportId)"
              >
                成立
              </button>

              <button
                class="btn btn-danger btn-sm"
                @click="handleReject(report.reportId)"
              >
                駁回
              </button>
            </template>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
