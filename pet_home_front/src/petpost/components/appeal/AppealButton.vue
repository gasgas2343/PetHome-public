<script setup>
import { useAppealStore } from "@/petpost/stores/appealStore";

const appealStore = useAppealStore();
</script>

<template>
  <button class="btn btn-outline-secondary" @click="appealStore.openDialog()">
     申訴
  </button>
</template>

<style scoped></style>
