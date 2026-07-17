

<script setup>
defineProps({
  users: {
    type: Array,

    required: true,
  },
});

defineEmits(["ban", "unban"]);
</script>

<template>
  <table class="table table-bordered">
    <thead>
      <tr>
        <th>編號</th>

        <th>帳號</th>

        <th>等級</th>

        <th>文章數</th>

        <th>狀態</th>

        <th>操作</th>
      </tr>
    </thead>

    <tbody>
      <tr v-for="user in users" :key="user.userId">
        <td>
          {{ user.userId }}
        </td>

        <td>
          {{ user.userName }}
        </td>

        <td>
          {{ user.level }}
        </td>

        <td>
          {{ user.postCount }}
        </td>

        <td>
          {{ user.status }}
        </td>

        <td>
          <button
            v-if="user.status === '正常'"
            class="btn btn-warning"
            @click="$emit('ban', user.userId)"
          >
            停權
          </button>

          <button
            v-else
            class="btn btn-success"
            @click="$emit('unban', user.userId)"
          >
            解除停權
          </button>
        </td>
      </tr>
    </tbody>
  </table>
</template>
