<script setup>
defineProps({
  reports: {
    type: Array,

    required: true,
  },
});

defineEmits(["approve", "reject"]);
</script>

<template>
  <table class="table table-bordered">
    <thead>
      <tr>
        <th>編號</th>

        <th>類型</th>

        <th>目標 ID</th>

        <th>原因</th>

        <th>狀態</th>

        <th>操作</th>
      </tr>
    </thead>

    <tbody>
      <tr v-for="report in reports" :key="report.reportId">
        <td>{{ report.reportId }}</td>

        <td>{{ report.targetType }}</td>

        <td>{{ report.targetId }}</td>

        <td>{{ report.reason }}</td>

        <td>{{ report.status }}</td>

        <td>
          <template v-if="report.status === '待審核'">
            <button
              class="btn btn-success me-2"
              @click="$emit('approve', report.reportId)"
            >
              通過
            </button>

            <button
              class="btn btn-danger"
              @click="$emit('reject', report.reportId)"
            >
              駁回
            </button>
          </template>
        </td>
      </tr>
    </tbody>
  </table>
</template>
