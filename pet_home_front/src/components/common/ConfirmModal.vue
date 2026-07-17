<script setup>
defineProps({
  title: {
    type: String,
    default: '確認操作',
  },
  message: {
    type: String,
    default: '',
  },
  confirmText: {
    type: String,
    default: '確認',
  },
  cancelText: {
    type: String,
    default: '取消',
  },
  loading: {
    type: Boolean,
    default: false,
  },
  danger: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['cancel', 'confirm'])
</script>

<template>
  <div class="modal-mask" @click.self="emit('cancel')">
    <section class="confirm-modal">
      <div class="confirm-icon" :class="{ danger }">
        !
      </div>

      <h3>{{ title }}</h3>
      <p>{{ message }}</p>

      <div class="modal-actions">
        <button type="button" class="secondary-btn" @click="emit('cancel')">
          {{ cancelText }}
        </button>

        <button
          type="button"
          class="primary-btn"
          :class="{ danger }"
          :disabled="loading"
          @click="emit('confirm')"
        >
          {{ loading ? '處理中...' : confirmText }}
        </button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 120;
  display: grid;
  place-items: center;
  padding: 24px;
  background: rgba(54, 42, 32, 0.38);
  backdrop-filter: blur(8px);
}

.confirm-modal {
  width: min(420px, 100%);
  padding: 30px 28px 26px;
  border-radius: 30px;
  background: #fffaf2;
  border: 1px solid rgba(138, 113, 89, 0.16);
  box-shadow: 0 26px 70px rgba(54, 42, 32, 0.24);
  text-align: center;
  animation: modal-pop 0.22s ease both;
}

.confirm-icon {
  width: 54px;
  height: 54px;
  margin: 0 auto 18px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  background: rgba(124, 166, 160, 0.16);
  color: #5f8b84;
  font-size: 26px;
  font-weight: 900;
}

.confirm-icon.danger {
  background: rgba(199, 83, 69, 0.12);
  color: #bd4f43;
}

.confirm-modal h3 {
  margin: 0 0 10px;
  color: #3f3024;
  font-size: 22px;
  font-weight: 900;
  letter-spacing: 0.02em;
}

.confirm-modal p {
  margin: 0;
  color: #7a6655;
  font-size: 15px;
  line-height: 1.8;
}

.modal-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 26px;
}

.modal-actions button {
  min-width: 104px;
  border: 0;
  border-radius: 999px;
  padding: 12px 18px;
  font-size: 14px;
  font-weight: 900;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease,
    color 0.2s ease,
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

.primary-btn.danger {
  background: #c75345;
  box-shadow: 0 12px 24px rgba(199, 83, 69, 0.2);
}

.primary-btn.danger:hover {
  background: #b9483d;
  box-shadow: 0 15px 28px rgba(199, 83, 69, 0.26);
}

.primary-btn:disabled,
.secondary-btn:disabled {
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
</style>