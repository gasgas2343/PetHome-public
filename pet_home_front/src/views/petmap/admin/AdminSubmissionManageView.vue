<script setup>
import { ref, computed } from "vue";
import { onMounted } from "vue";

import {
  getAllSubmissions,
  approveSubmission,
  rejectSubmission,
  requestRevision,
  getSubmissionDetail,
  getSubmissionStatistics,
} from "@/api/petmap/placeSubmissionApi";

import AdminNavbar from "@/components/petmap/AdminNavbar.vue";

import Swal from "sweetalert2";

const selectedStatus = ref("ALL");

const submissions = ref([]);

const statistics = ref(null);

const loadSubmissions = async () => {
  const response = await getAllSubmissions();

  submissions.value = response.data.data;

};

const handleApprove = async (submissionId) => {
  await approveSubmission(submissionId);

  await loadSubmissions();

  await loadStatistics();
};

const handleReject = async (submissionId) => {
  const result = await Swal.fire({
    title: "拒絕原因",

    input: "text",

    showCancelButton: true,
  });

  if (!result.isConfirmed) {
    return;
  }

  await rejectSubmission(submissionId, result.value);

  await loadSubmissions();

  await loadStatistics();
};

const handleRevision = async (submissionId) => {
  const result = await Swal.fire({
    title: "修改建議",

    input: "text",

    showCancelButton: true,
  });

  if (!result.isConfirmed) {
    return;
  }

  await requestRevision(submissionId, result.value);

  await loadSubmissions();

  await loadStatistics();
};

const handleViewDetail = async (submissionId) => {
  const response = await getSubmissionDetail(submissionId);

  const data = response.data.data;

  await Swal.fire({
    title: data.name,

    html: `
        <div style="text-align:left">

          <p>
            <b>類型：</b>
            ${data.placeType}
          </p>

          <p>
            <b>地址：</b>
            ${data.address}
          </p>

          <p>
            <b>電話：</b>
            ${data.phone ?? "-"}
          </p>

          <p>
            <b>描述：</b>
            ${data.description ?? "-"}
          </p>

          <p>
            <b>標籤：</b>
            ${data.tags.join(", ")}
          </p>

        </div>
      `,

    width: 700,
  });
};

const loadStatistics = async () => {
  const response = await getSubmissionStatistics();

  statistics.value = response.data.data;
};

const statusMap = {
  PENDING: "待審核",
  APPROVED: "已通過",
  REJECTED: "已拒絕",
  NEED_REVISION: "待修改",
};

const filteredSubmissions = computed(() => {
  if (selectedStatus.value === "ALL") {
    return submissions.value;
  }

  return submissions.value.filter(
    (submission) => submission.status === selectedStatus.value,
  );
});

onMounted(async () => {
  await loadSubmissions();

  await loadStatistics();
});
</script>

<template>
  <AdminNavbar />
  <div class="container mt-4">
    <h1>投稿管理</h1>
    <div v-if="statistics" class="row mb-4">
      <div class="col-md-3">
        <div class="card text-center border-warning">
          <div class="card-body">
            <h5>待審核</h5>

            <h2>
              {{ statistics.pending }}
            </h2>
          </div>
        </div>
      </div>

      <div class="col-md-3">
        <div class="card text-center border-success">
          <div class="card-body">
            <h5>已通過</h5>

            <h2>
              {{ statistics.approved }}
            </h2>
          </div>
        </div>
      </div>

      <div class="col-md-3">
        <div class="card text-center border-danger">
          <div class="card-body">
            <h5>已拒絕</h5>

            <h2>
              {{ statistics.rejected }}
            </h2>
          </div>
        </div>
      </div>

      <div class="col-md-3">
        <div class="card text-center border-primary">
          <div class="card-body">
            <h5>待修改</h5>

            <h2>
              {{ statistics.needRevision }}
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
        已通過
      </button>

      <button
        :class="
          selectedStatus === 'REJECTED'
            ? 'btn btn-danger me-2'
            : 'btn btn-outline-danger me-2'
        "
        @click="selectedStatus = 'REJECTED'"
      >
        已拒絕
      </button>

      <button
        :class="
          selectedStatus === 'NEED_REVISION'
            ? 'btn btn-primary'
            : 'btn btn-outline-primary'
        "
        @click="selectedStatus = 'NEED_REVISION'"
      >
        待修改
      </button>
    </div>

    <table class="table table-bordered">
      <thead>
        <tr>
          <th>ID</th>

          <th>名稱</th>

          <th>狀態</th>

          <th>操作</th>
        </tr>
      </thead>

      <tbody>
        <tr
          v-for="submission in filteredSubmissions"
          :key="submission.submissionId"
        >
          <td>
            {{ submission.submissionId }}
          </td>

          <td>
            {{ submission.name }}
          </td>

          <td>
            {{ statusMap[submission.status] }}
          </td>
          <td>
            <button
              class="btn btn-primary btn-sm me-2"
              @click="handleViewDetail(submission.submissionId)"
            >
              查看
            </button>
            <template v-if="submission.status === 'PENDING'">
              <button
                class="btn btn-success btn-sm me-2"
                @click="handleApprove(submission.submissionId)"
              >
                通過
              </button>

              <button
                class="btn btn-warning btn-sm me-2"
                @click="handleRevision(submission.submissionId)"
              >
                退回修改
              </button>

              <button
                class="btn btn-danger btn-sm"
                @click="handleReject(submission.submissionId)"
              >
                拒絕
              </button>
            </template>
            <template v-else-if="submission.status === 'NEED_REVISION'">
              <button
                class="btn btn-success btn-sm me-2"
                @click="handleApprove(submission.submissionId)"
              >
                通過
              </button>
              <button
                class="btn btn-danger btn-sm"
                @click="handleReject(submission.submissionId)"
              >
                拒絕
              </button>
            </template>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
