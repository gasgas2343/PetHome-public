<!-- src/components/admin/AdminUserRoleModal.vue -->
<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  user: {
    type: Object,
    required: true,
  },
  roleOptions: {
    type: Array,
    default: () => [],
  },
  saving: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['close', 'save'])

const selectedRoles = ref([])

watch(
  () => props.user,
  (user) => {
    selectedRoles.value = Array.isArray(user?.roles) ? [...user.roles] : []
  },
  { immediate: true },
)

function toggleRole(roleCode) {
  if (selectedRoles.value.includes(roleCode)) {
    selectedRoles.value = selectedRoles.value.filter((item) => item !== roleCode)
  } else {
    selectedRoles.value.push(roleCode)
  }
}

function submit() {
  emit('save', selectedRoles.value)
}
</script>

<template>
  <div class="modal-mask">
    <div class="role-modal">
      <h2>修改角色</h2>

      <p class="target-user">
        {{ user.email }}
      </p>

      <div class="role-options">
        <label
          v-for="role in roleOptions"
          :key="role.roleCode"
          class="role-check"
          :class="{ active: selectedRoles.includes(role.roleCode) }"
        >
          <input
            type="checkbox"
            :checked="selectedRoles.includes(role.roleCode)"
            @change="toggleRole(role.roleCode)"
          />

          <span class="custom-check">
            <span class="check-mark">✓</span>
          </span>

          <span class="role-text">
            {{ role.roleName }}
            <small>{{ role.roleCode }}</small>
          </span>
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

.role-modal {
  width: min(420px, calc(100vw - 32px));
  padding: 26px;
  border-radius: 22px;
  background: #fffaf3;
  box-shadow: 0 24px 60px rgba(35, 24, 15, 0.24);
}

.role-modal h2 {
  margin: 0 0 8px;
  color: #3b2a20;
}

.target-user {
  margin: 0 0 18px;
  color: #7a6758;
}

.role-options {
  display: grid;
  gap: 12px;
  margin-bottom: 24px;
}

.role-check {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 14px;
  border: 1px solid rgba(122, 82, 48, 0.16);
  border-radius: 14px;
  background: #f8ead8;
  cursor: pointer;
  font-weight: 800;
  color: #5b3f2b;
  transition:
    background 0.2s ease,
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.15s ease;
}

.role-check:hover {
  background: #fff1df;
  border-color: rgba(122, 82, 48, 0.28);
  transform: translateY(-1px);
}

.role-check.active {
  background: #e7f2dc;
  border-color: rgba(92, 132, 61, 0.35);
  box-shadow: 0 8px 18px rgba(92, 132, 61, 0.14);
  color: #3f6b2a;
}

.role-check input {
  display: none;
}

.custom-check {
  width: 22px;
  height: 22px;
  display: inline-grid;
  place-items: center;
  border: 2px solid #c7a883;
  border-radius: 8px;
  background: #fffaf3;
  flex-shrink: 0;
}

.check-mark {
  color: #fff;
  font-size: 15px;
  font-weight: 900;
  line-height: 1;
  opacity: 0;
  transform: scale(0.6);
  transition:
    opacity 0.15s ease,
    transform 0.15s ease;
}

.role-check.active .custom-check {
  background: #6f9b4e;
  border-color: #6f9b4e;
}

.role-check.active .check-mark {
  opacity: 1;
  transform: scale(1);
}

.role-text small {
  display: block;
  margin-top: 2px;
  color: #8a7462;
  font-size: 12px;
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