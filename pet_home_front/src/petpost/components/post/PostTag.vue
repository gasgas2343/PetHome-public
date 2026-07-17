<script setup>
defineProps({
  tag: String,
});
</script>

<template>
  <span class="badge bg-primary">
    {{ tag }}
  </span>
</template>

<style scoped></style>
