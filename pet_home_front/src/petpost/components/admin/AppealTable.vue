<script setup>
defineProps({
  appeals: {
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

        <th>文章 ID</th>

        <th>作者</th>

        <th>申訴原因</th>

        <th>狀態</th>

        <th>操作</th>
      </tr>
    </thead>

    <tbody>
      <tr v-for="appeal in appeals" :key="appeal.appealId">
        <td>
          {{ appeal.appealId }}
        </td>

        <td>
          {{ appeal.postId }}
        </td>

        <td>
          {{ appeal.userName }}
        </td>

        <td>
          {{ appeal.reason }}
        </td>

        <td>
          {{ appeal.status }}
        </td>

        <td>
          <template v-if="appeal.status === '待審核'">
            <button
              class="btn btn-success me-2"
              @click="$emit('approve', appeal.appealId)"
            >
              通過
            </button>

            <button
              class="btn btn-danger"
              @click="$emit('reject', appeal.appealId)"
            >
              駁回
            </button>
          </template>
        </td>
      </tr>
    </tbody>
  </table>
</template>
