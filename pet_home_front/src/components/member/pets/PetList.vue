<script setup>
const props = defineProps({
  pets: {
    type: Array,
    default: () => [],
  },
  petTypeOptions: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['view', 'edit', 'delete'])

function getPetTypeName(pet) {
  if (pet.petTypeName) return pet.petTypeName

  const type = props.petTypeOptions.find(item => item.id === pet.petTypeId)
  return type?.name || '未填種類'
}

function formatGender(gender) {
  if (gender === 'male') return '男生'
  if (gender === 'female') return '女生'
  return '未填性別'
}
</script>

<template>
  <div v-if="pets.length === 0" class="empty-card">
    <p class="empty-title">還沒有新增寵物</p>
    <p class="empty-desc">新增後會顯示在這裡，方便你查看與管理毛孩資料。</p>
  </div>

  <div v-else class="pet-list">
    <article
      v-for="pet in pets"
      :key="pet.id"
      class="pet-row"
      @click="emit('view', pet)"
    >
      <div class="pet-avatar">
        {{ pet.name?.slice(0, 1) || '寵' }}
      </div>

      <div class="pet-main">
        <h3>{{ pet.name || '未命名' }}</h3>

        <p>
          {{ getPetTypeName(pet) }}・{{ formatGender(pet.gender) }}
        </p>

        <p>
          {{ pet.breed || '未填品種' }}
        </p>
      </div>

      <div class="pet-actions" @click.stop>
        <button type="button" class="edit-btn" @click="emit('edit', pet)">
          修改
        </button>

        <button type="button" class="delete-btn" @click="emit('delete', pet)">
          刪除
        </button>
      </div>
    </article>
  </div>
</template>

<style scoped>
.pet-list {
  display: grid;
  gap: 14px;
}

.pet-row {
  display: flex;
  align-items: center;
  gap: 16px;
  min-height: 118px;
  padding: 20px 18px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(138, 113, 89, 0.16);
  box-shadow: 0 14px 34px rgba(65, 48, 34, 0.04);
  cursor: pointer;
  transition:
    transform 0.22s ease,
    box-shadow 0.22s ease,
    border-color 0.22s ease,
    background 0.22s ease;
}

.pet-row:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(124, 166, 160, 0.34);
  box-shadow: 0 18px 42px rgba(65, 48, 34, 0.08);
}

.pet-avatar {
  width: 56px;
  height: 56px;
  flex: 0 0 56px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  background: #f2dfc7;
  color: #6d4f35;
  font-size: 22px;
  font-weight: 900;
}

.pet-main {
  flex: 1;
  min-width: 0;
}

.pet-main h3 {
  margin: 0 0 6px;
  color: #3f3024;
  font-size: 18px;
  font-weight: 900;
}

.pet-main p {
  margin: 3px 0;
  color: #6f5d4d;
  font-size: 14px;
  line-height: 1.4;
}

.pet-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

.pet-actions button {
  border: 0;
  border-radius: 999px;
  padding: 9px 14px;
  font-size: 13px;
  font-weight: 800;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    background 0.2s ease,
    color 0.2s ease;
}

.pet-actions button:hover {
  transform: translateY(-1px);
}

.edit-btn {
  background: rgba(124, 166, 160, 0.14);
  color: #547b75;
}

.edit-btn:hover {
  background: rgba(124, 166, 160, 0.24);
}

.delete-btn {
  background: rgba(199, 83, 69, 0.1);
  color: #b94d42;
}

.delete-btn:hover {
  background: rgba(199, 83, 69, 0.18);
}

.empty-card {
  padding: 30px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.78);
  border: 1px dashed rgba(138, 113, 89, 0.34);
}

.empty-title {
  margin: 0 0 8px;
  color: #3f3024;
  font-weight: 900;
}

.empty-desc {
  margin: 0;
  color: #7a6655;
  line-height: 1.7;
}
</style>