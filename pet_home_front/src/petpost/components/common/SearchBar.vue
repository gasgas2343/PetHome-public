<script setup>
import { ref, watch } from "vue";

const props = defineProps({
  modelValue: { type: String, default: "" },
  placeholder: { type: String, default: "搜尋文章、商品、寵物..." },
});

const emit = defineEmits(["update:modelValue", "search"]);
const keyword = ref(props.modelValue);

watch(() => props.modelValue, (value) => { keyword.value = value; });

function updateKeyword() {
  emit("update:modelValue", keyword.value);
}

function submitSearch() {
  const value = keyword.value.trim();
  emit("update:modelValue", value);
  emit("search", value);
}
</script>

<template>
  <form class="pc-search" @submit.prevent="submitSearch">
    <i class="bi bi-search"></i>
    <input
      v-model="keyword"
      type="text"
      :placeholder="placeholder"
      @input="updateKeyword"
    />
    <button type="submit">搜尋</button>
  </form>
</template>

<style scoped>
.pc-search {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 10px 10px 10px 18px;
  border-radius: 999px;
  background: rgba(255, 255, 255, .76);
  border: 1px solid var(--pc-border);
  box-shadow: 0 12px 30px rgba(92, 61, 35, .08);
}
.pc-search i { color: var(--pc-brown); font-size: 19px; }
.pc-search input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  color: var(--pc-title);
  min-width: 0;
}
.pc-search button {
  border: none;
  border-radius: 999px;
  padding: 10px 20px;
  background: var(--pc-primary);
  color: white;
  font-weight: 700;
}
</style>
