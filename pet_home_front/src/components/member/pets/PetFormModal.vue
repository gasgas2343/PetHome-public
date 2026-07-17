<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  pet: {
    type: Object,
    default: null,
  },
  petTypeOptions: {
    type: Array,
    default: () => [],
  },
  saving: {
    type: Boolean,
    default: false,
  },
  apiError: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['close', 'submit'])

const form = reactive({
  name: '',
  petTypeId: '',
  gender: '',
  birthday: '',
  breed: '',
  weightKg: '',
  bodySize: '',
  personality: '',
})

const errors = reactive({
  name: '',
  petTypeId: '',
})

function resetForm() {
  form.name = ''
  form.petTypeId = ''
  form.gender = ''
  form.birthday = ''
  form.breed = ''
  form.weightKg = ''
  form.bodySize = ''
  form.personality = ''

  errors.name = ''
  errors.petTypeId = ''
}

function fillForm(pet) {
  form.name = pet?.name || ''
  form.petTypeId = pet?.petTypeId || ''
  form.gender = pet?.gender || ''
  form.birthday = pet?.birthday || ''
  form.breed = pet?.breed || ''
  form.weightKg = pet?.weightKg ?? ''
  form.bodySize = pet?.bodySize || ''
  form.personality = pet?.personality || ''
}

watch(
  () => props.pet,
  newPet => {
    resetForm()

    if (newPet) {
      fillForm(newPet)
    }
  },
  { immediate: true }
)

function validateForm() {
  errors.name = ''
  errors.petTypeId = ''

  let valid = true

  if (!form.name.trim()) {
    errors.name = '請輸入寵物名稱'
    valid = false
  }

  if (!form.petTypeId) {
    errors.petTypeId = '請選擇寵物類型'
    valid = false
  }

  return valid
}

function submitForm() {
  if (!validateForm()) return

  emit('submit', {
    name: form.name.trim(),
    petTypeId: Number(form.petTypeId),
    gender: form.gender || null,
    birthday: form.birthday || null,
    breed: form.breed || null,
    weightKg: form.weightKg === '' ? null : Number(form.weightKg),
    bodySize: form.bodySize || null,
    personality: form.personality || null,
  })
}
</script>

<template>
  <div class="modal-mask" @click.self="emit('close')">
    <form class="pet-form-modal" @submit.prevent="submitForm" novalidate>
      <div class="modal-head">
        <div>
          <p class="modal-kicker">Pet Form</p>
          <h3>{{ pet ? '修改寵物資料' : '新增寵物' }}</h3>
        </div>

        <button type="button" class="close-btn" @click="emit('close')">×</button>
      </div>

      <div class="form-grid">
        <label>
          <span>名稱</span>
          <input v-model="form.name" type="text" placeholder="例如：多頓" />
          <small v-if="errors.name">{{ errors.name }}</small>
        </label>

        <label>
          <span>種類</span>
          <select v-model="form.petTypeId">
            <option value="">請選擇</option>
            <option
              v-for="type in petTypeOptions"
              :key="type.id"
              :value="type.id"
            >
              {{ type.name }}
            </option>
          </select>
          <small v-if="errors.petTypeId">{{ errors.petTypeId }}</small>
        </label>

        <label>
          <span>性別</span>
          <select v-model="form.gender">
            <option value="">不指定</option>
            <option value="male">男生</option>
            <option value="female">女生</option>
          </select>
        </label>

        <label>
          <span>生日</span>
          <input v-model="form.birthday" type="date" />
        </label>

        <label>
          <span>品種</span>
          <input v-model="form.breed" type="text" placeholder="例如：柴犬、米克斯" />
        </label>

        <label>
          <span>體重</span>
          <input v-model="form.weightKg" type="number" min="0" step="0.1" placeholder="kg" />
        </label>

        <label>
          <span>體型</span>
          <select v-model="form.bodySize">
            <option value="">不指定</option>
            <option value="SMALL">小型</option>
            <option value="MEDIUM">中型</option>
            <option value="LARGE">大型</option>
          </select>
        </label>

        <label>
          <span>個性</span>
          <input v-model="form.personality" type="text" placeholder="例如：親人、怕生、活潑" />
        </label>
      </div>

      <p v-if="apiError" class="error-text">{{ apiError }}</p>

      <div class="modal-actions">
        <button type="button" class="secondary-btn" @click="emit('close')">
          取消
        </button>

        <button type="submit" class="primary-btn" :disabled="saving">
          {{ saving ? '儲存中...' : '儲存資料' }}
        </button>
      </div>
    </form>
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

.pet-form-modal {
  width: min(620px, 100%);
  max-height: min(780px, 88vh);
  overflow-y: auto;
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

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.form-grid label {
  display: block;
  min-width: 0;
}

.form-grid label span {
  display: block;
  margin-bottom: 7px;
  color: #5f4c3d;
  font-size: 14px;
  font-weight: 800;
}

.form-grid input,
.form-grid select {
  width: 100%;
  min-height: 46px;
  padding: 0 14px;
  border: 1px solid rgba(138, 113, 89, 0.22);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.88);
  color: #3f3024;
  font-size: 15px;
  outline: none;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease;
}

.form-grid input::placeholder {
  color: rgba(111, 93, 77, 0.52);
}

.form-grid input:focus,
.form-grid select:focus {
  border-color: rgba(124, 166, 160, 0.82);
  box-shadow: 0 0 0 4px rgba(124, 166, 160, 0.16);
  background: #fff;
}

.form-grid small {
  display: block;
  margin-top: 6px;
  color: #c75345;
  font-size: 12px;
  font-weight: 700;
}

.error-text {
  margin: 18px 0 0;
  padding: 12px 14px;
  border-radius: 16px;
  background: rgba(199, 83, 69, 0.08);
  color: #b94d42;
  font-size: 14px;
  font-weight: 800;
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
    background 0.2s ease,
    opacity 0.2s ease;
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

.primary-btn:disabled {
  opacity: 0.62;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
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

@media (max-width: 640px) {
  .pet-form-modal {
    padding: 24px;
    border-radius: 26px;
  }

  .form-grid {
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