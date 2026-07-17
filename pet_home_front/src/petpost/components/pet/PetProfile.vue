<script setup>
defineProps({
  pet: Object,
});
</script>

<template>
  <div class="card">
    <div class="card-body">
      <h4>
        {{ pet.petName }}
      </h4>

      <div>
        品種：

        {{ pet.breed }}
      </div>
    </div>
  </div>
</template>
