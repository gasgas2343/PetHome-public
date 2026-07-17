<script setup>
import { RouterLink, useRouter } from "vue-router";

const router = useRouter();

function backToForum() {
  router.push("/community");
}
</script>

<template>
  <div class="list-group">
    <button
      type="button"
      class="list-group-item list-group-item-action"
      @click="backToForum"
    >
      ← 返回論壇首頁
    </button>

    <RouterLink class="list-group-item list-group-item-action" to="/admin">
      管理首頁
    </RouterLink>

    <RouterLink class="list-group-item list-group-item-action" to="/admin/posts">
      文章管理
    </RouterLink>

    <RouterLink class="list-group-item list-group-item-action" to="/admin/users">
      會員管理
    </RouterLink>

    <RouterLink class="list-group-item list-group-item-action" to="/admin/reports">
      檢舉管理
    </RouterLink>

    <RouterLink class="list-group-item list-group-item-action" to="/admin/appeals">
      申訴管理
    </RouterLink>
  </div>
</template>

<style scoped>
.list-group {
  width: 200px;
}
</style>
