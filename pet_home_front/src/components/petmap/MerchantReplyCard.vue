<script setup>

defineProps({

  reply: Object

})

</script>

<template>

<div
  class="card border-success ms-5 mb-2"
>

  <div class="card-body">

    <h6>

      商家回覆

    </h6>

    <p>

      {{ reply.replyContent }}

    </p>

  </div>

</div>

</template>