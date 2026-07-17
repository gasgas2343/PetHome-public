<script setup>

defineProps({

  review: Object

})

</script>

<template>

<div class="card mb-3">

  <div class="card-body">

    <h5>

      {{ review.title }}

    </h5>

    <p>

      評分：

      {{ review.ratingOverall }}

      ⭐

    </p>

    <p>

      {{ review.content }}

    </p>

  </div>

</div>

</template>