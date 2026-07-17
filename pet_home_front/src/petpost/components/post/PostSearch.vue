<script setup>
// src/components/post/PostSearch.vue
// 可重複使用的文章搜尋輸入框，支援 v-model。
const props = defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: '搜尋文章標題或內容...' }
})

const emit = defineEmits(['update:modelValue', 'search'])

function updateKeyword(event) {
  const value = event.target.value
  emit('update:modelValue', value)
  emit('search', value)
}
</script>

<template>
  <input
    class="form-control"
    :value="props.modelValue"
    :placeholder="placeholder"
    @input="updateKeyword"
  />
</template>

<style scoped></style>
