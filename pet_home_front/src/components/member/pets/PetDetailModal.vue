<script setup>
const props = defineProps({
  pet: {
    type: Object,
    default: null,
  },
  petTypeOptions: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['close', 'edit'])

function getPetTypeName() {
  if (props.pet?.petTypeName) return props.pet.petTypeName

  const type = props.petTypeOptions.find((item) => item.id === props.pet?.petTypeId)
  return type?.name || '未填寫'
}

function formatGender(gender) {
  if (gender === 'male') return '男生'
  if (gender === 'female') return '女生'
  return '未填寫'
}

function formatBodySize(size) {
  if (size === 'SMALL') return '小型'
  if (size === 'MEDIUM') return '中型'
  if (size === 'LARGE') return '大型'
  return '未填寫'
}
</script>

<template>
  <div class="modal-mask" @click.self="emit('close')">
    <section class="pet-detail-modal">
      <div class="modal-head">
        <div>
          <p class="modal-kicker">Pet Detail</p>
          <h3>{{ pet?.name || '寵物資料' }}</h3>
        </div>

        <button type="button" class="close-btn" @click="emit('close')">×</button>
      </div>

      <div class="detail-grid">
        <div>
          <span>種類</span>
          <strong>{{ getPetTypeName() }}</strong>
        </div>

        <div>
          <span>性別</span>
          <strong>{{ formatGender(pet?.gender) }}</strong>
        </div>

        <div>
          <span>生日</span>
          <strong>{{ pet?.birthday || '未填寫' }}</strong>
        </div>

        <div>
          <span>品種</span>
          <strong>{{ pet?.breed || '未填寫' }}</strong>
        </div>

        <div>
          <span>體重</span>
          <strong>{{ pet?.weightKg ? `${pet.weightKg} kg` : '未填寫' }}</strong>
        </div>

        <div>
          <span>體型</span>
          <strong>{{ formatBodySize(pet?.bodySize) }}</strong>
        </div>

        <div class="full">
          <span>個性</span>
          <strong>{{ pet?.personality || '未填寫' }}</strong>
        </div>
      </div>

      <div class="modal-actions">
        <button type="button" class="secondary-btn" @click="emit('close')">關閉</button>

        <button type="button" class="primary-btn" @click="emit('edit', pet)">修改資料</button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 110;
  display: grid;
  place-items: center;
  padding: 24px;
  background: rgba(54, 42, 32, 0.38);
  backdrop-filter: blur(8px);
}

.pet-detail-modal {
  width: min(560px, 100%);
  padding: 30px;
  border-radius: 32px;
  background: #fffaf2;
  border: 1px solid rgba(138, 113, 89, 0.16);
  box-shadow: 0 26px 76px rgba(54, 42, 32, 0.24);
  animation: modal-pop 0.22s ease both;
}

.modal-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 24px;
}

.modal-kicker {
  margin: 0 0 8px;
  color: #8a5e4c;
  font-size: 13px;
  letter-spacing: 0.08em;
}

.modal-head h3 {
  margin: 0;
  color: #3f3024;
  font-size: 26px;
  font-weight: 900;
}

.close-btn {
  width: 38px;
  height: 38px;
  flex: 0 0 38px;
  border: 0;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: rgba(138, 113, 89, 0.12);
  color: #6d5948;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    background 0.2s ease;
}

.close-btn:hover {
  transform: rotate(8deg);
  background: rgba(138, 113, 89, 0.18);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.detail-grid > div {
  min-height: 82px;
  padding: 15px 16px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.78);
  border: 1px solid rgba(138, 113, 89, 0.14);
}

.detail-grid > div.full {
  grid-column: 1 / -1;
}

.detail-grid span {
  display: block;
  margin-bottom: 8px;
  color: #8a7159;
  font-size: 13px;
  font-weight: 800;
}

.detail-grid strong {
  display: block;
  color: #3f3024;
  font-size: 16px;
  font-weight: 900;
  line-height: 1.5;
  word-break: break-word;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 26px;
}

.modal-actions button {
  border: 0;
  border-radius: 999px;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 900;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease;
}

.modal-actions button:hover {
  transform: translateY(-1px);
}

.secondary-btn {
  background: rgba(138, 113, 89, 0.12);
  color: #6d5948;
}

.secondary-btn:hover {
  background: rgba(138, 113, 89, 0.18);
}

.primary-btn {
  background: #7ca6a0;
  color: #fff;
  box-shadow: 0 12px 24px rgba(124, 166, 160, 0.22);
}

.primary-btn:hover {
  background: #719c96;
  box-shadow: 0 15px 28px rgba(124, 166, 160, 0.28);
}

@keyframes modal-pop {
  from {
    opacity: 0;
    transform: translateY(10px) scale(0.97);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@media (max-width: 560px) {
  .pet-detail-modal {
    padding: 24px;
    border-radius: 26px;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .modal-actions {
    flex-direction: column-reverse;
  }

  .modal-actions button {
    width: 100%;
  }
}
</style>
