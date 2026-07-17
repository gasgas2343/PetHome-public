<script setup></script>

<template>
  <button
    class="btn btn-danger"
    @click="petPostStore.removePetPost(post.petPostId)"
  >
    刪除
  </button>
</template>

<style scoped></style>
