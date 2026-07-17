<!-- 文章管理表格 -->

<script setup>
// 接收文章資料
defineProps({
  posts: {
    type: Array,

    required: true,
  },
});

// 事件
defineEmits(["disable", "enable", "delete"]);
</script>

<template>
  <table class="table table-bordered">
    <thead>
      <tr>
        <th>編號</th>

        <th>標題</th>

        <th>作者</th>

        <th>狀態</th>

        <th>操作</th>
      </tr>
    </thead>

    <tbody>
      <tr v-for="post in posts" :key="post.postId">
        <td>
          {{ post.postId }}
        </td>

        <td>
          {{ post.title }}
        </td>

        <td>
          {{ post.userName }}
        </td>

        <td>
          {{ post.status }}
        </td>

        <td>
          <button
            v-if="post.status === '正常'"
            class="btn btn-warning me-2"
            @click="$emit('disable', post.postId)"
          >
            下架
          </button>

          <button
            v-else
            class="btn btn-success me-2"
            @click="$emit('enable', post.postId)"
          >
            恢復
          </button>

          <button class="btn btn-danger" @click="$emit('delete', post.postId)">
            刪除
          </button>
        </td>
      </tr>
    </tbody>
  </table>
</template>
