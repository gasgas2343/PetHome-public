<!-- src/components/admin/AdminUserStatusModal.vue -->
<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  user: {
    type: Object,
    required: true,
  },
  saving: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['close', 'save'])

const form = ref({
  status: 'ACTIVE',
  statusUntil: '',
  reason: '',
})

watch(
  () => props.user,
  (user) => {
    form.value = {
      status: user?.status || 'ACTIVE',
      statusUntil: '',
      reason: '',
    }
  },
  { immediate: true },
)

function submit() {
  if (form.value.status === 'LOCKED' && !form.value.statusUntil) {
    emit('save', {
      ...form.value,
      error: '鎖定狀態需要設定結束時間',
    })
    return
  }

  emit('save', {
    status: form.value.status,
    statusUntil: form.value.statusUntil,
    reason: form.value.reason,
  })
}
</script>

<template>
  <div class="modal-mask">
    <div class="status-modal">
      <h2>修改狀態</h2>

      <p class="target-user">
        {{ user.email }}
      </p>

      <div class="status-form">
        <label class="form-field">
          <span>帳號狀態</span>

          <select v-model="form.status">
            <option value="ACTIVE">ACTIVE 正常</option>
            <option value="LOCKED">LOCKED 鎖定</option>
            <option value="SUSPENDED">SUSPENDED 停權</option>
            <option value="BANNED">BANNED 封鎖</option>
          </select>
        </label>

        <label v-if="form.status === 'LOCKED'" class="form-field">
          <span>鎖定到期時間</span>
          <input v-model="form.statusUntil" type="datetime-local" />
        </label>

        <label class="form-field">
          <span>修改原因</span>

          <textarea
            v-model.trim="form.reason"
            rows="3"
            placeholder="例如：違反平台規範、測試鎖定、解除鎖定..."
          ></textarea>
        </label>
      </div>

      <div class="modal-actions">
        <button type="button" class="cancel-btn" @click="emit('close')">取消</button>

        <button type="button" class="save-btn" :disabled="saving" @click="submit">
          {{ saving ? '儲存中...' : '儲存' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: grid;
  place-items: center;
  background: rgba(37, 25, 16, 0.45);
}

.status-modal {
  width: min(460px, calc(100vw - 32px));
  padding: 26px;
  border-radius: 22px;
  background: #fffaf3;
  box-shadow: 0 24px 60px rgba(35, 24, 15, 0.24);
}

.status-modal h2 {
  margin: 0 0 8px;
  color: #3b2a20;
}

.target-user {
  margin: 0 0 18px;
  color: #7a6758;
}

.status-form {
  display: grid;
  gap: 14px;
  margin-bottom: 22px;
}

.form-field {
  display: grid;
  gap: 8px;
}

.form-field span {
  color: #6f5846;
  font-size: 13px;
  font-weight: 800;
}

.form-field select,
.form-field input,
.form-field textarea {
  width: 100%;
  border: 1px solid rgba(122, 82, 48, 0.18);
  border-radius: 14px;
  background: #fffaf3;
  color: #3f2f24;
  padding: 11px 12px;
  font-size: 14px;
  outline: none;
}

.form-field textarea {
  resize: vertical;
  min-height: 86px;
}

.form-field select:focus,
.form-field input:focus,
.form-field textarea:focus {
  border-color: rgba(122, 82, 48, 0.45);
  box-shadow: 0 0 0 3px rgba(122, 82, 48, 0.08);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn,
.save-btn {
  border: none;
  border-radius: 12px;
  padding: 10px 16px;
  cursor: pointer;
  font-weight: 800;
}

.cancel-btn {
  background: #ead8c1;
  color: #5b3f2b;
}

.save-btn {
  background: #7a5230;
  color: #fff;
}

.save-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
</style>