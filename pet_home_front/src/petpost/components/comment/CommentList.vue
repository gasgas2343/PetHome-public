<script setup>

import CommentItem from './CommentItem.vue'
defineProps({
    comments: {
        type: Array,
        required: true
    }
})

</script>

<template>

<div>
    <CommentItem
        v-for="comment in comments"
        :key="comment.commentId"
        :comment="comment"
    />
</div>

</template>

<style scoped>

</style>