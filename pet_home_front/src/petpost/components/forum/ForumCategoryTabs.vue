<script setup>
defineProps({
  categories: {
    type: Array,
    default: () => []
  },
  activeCategoryId: {
    type: [Number, String, null],
    default: null
  }
})

const emit = defineEmits(['change'])
</script>

<template>
  <div class="category-tabs">
    <button
      class="category-tab"
      :class="{ active: activeCategoryId === null }"
      @click="emit('change', null)"
    >
      全部
    </button>

    <button
      v-for="category in categories"
      :key="category.categoryId || category.category_id"
      class="category-tab"
      :class="{ active: activeCategoryId === (category.categoryId || category.category_id) }"
      @click="emit('change', category.categoryId || category.category_id)"
    >
      {{ category.categoryName || category.category_name }}
    </button>
  </div>
</template>

<style scoped>
.category-tabs {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding: 10px 0;
}

.category-tab {
  border: 1px solid #e5e7eb;
  background: #fff;
  color: #4b5563;
  border-radius: 999px;
  padding: 8px 16px;
  white-space: nowrap;
  transition: 0.2s;
}

.category-tab.active,
.category-tab:hover {
  background: #f97316;
  color: #fff;
  border-color: #f97316;
}
</style>
