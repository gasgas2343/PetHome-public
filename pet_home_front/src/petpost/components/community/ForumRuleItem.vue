<script setup>

// 接收父元件傳入的規則文字
defineProps({

    rule: {

        type: String,

        required: true

    }

})

</script>

<template>

<li class="list-group-item">

    {{ rule }}

</li>

</template>

<style scoped>

</style>