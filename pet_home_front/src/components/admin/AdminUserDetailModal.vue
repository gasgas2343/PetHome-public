<!-- src/components/admin/AdminUserDetailModal.vue -->
<script setup>
defineProps({
  user: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['close'])

function formatDateTime(value) {
  if (!value) return '-'
  return value.replace('T', ' ')
}

function formatBoolean(value) {
  return value ? '已驗證' : '未驗證'
}

function formatRoles(roles) {
  if (!Array.isArray(roles) || roles.length === 0) {
    return '-'
  }

  return roles.join('、')
}
</script>

<template>
  <div class="modal-mask">
    <div class="detail-modal">
      <div class="detail-header">
        <h2>會員詳細資料</h2>
        <button type="button" class="close-btn" @click="emit('close')">✕</button>
      </div>

      <div class="detail-grid">
        <div>
          <span>ID</span>
          <strong>{{ user.id }}</strong>
        </div>

        <div>
          <span>Email</span>
          <strong>{{ user.email }}</strong>
        </div>

        <div>
          <span>暱稱</span>
          <strong>{{ user.nickName || user.name || '-' }}</strong>
        </div>

        <div>
          <span>狀態</span>
          <strong>{{ user.status }}</strong>
        </div>

        <div>
          <span>Email 驗證</span>
          <strong>{{ formatBoolean(user.emailVerified) }}</strong>
        </div>

        <div>
          <span>二階段驗證</span>
          <strong>{{ user.twoFactorEnabled ? '已啟用' : '未啟用' }}</strong>
        </div>

        <div>
          <span>角色</span>
          <strong>{{ formatRoles(user.roles) }}</strong>
        </div>

        <div class="full-row">
          <span>權限</span>
          <strong>{{ formatRoles(user.permissions) }}</strong>
        </div>

        <div>
          <span>建立時間</span>
          <strong>{{ formatDateTime(user.createdAt) }}</strong>
        </div>

        <div>
          <span>最後登入</span>
          <strong>{{ formatDateTime(user.lastLoginAt) }}</strong>
        </div>
      </div>

      <div class="modal-actions">
        <button type="button" class="cancel-btn" @click="emit('close')">關閉</button>
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

.detail-modal {
  width: min(720px, calc(100vw - 32px));
  max-height: calc(100vh - 64px);
  overflow-y: auto;
  padding: 26px;
  border-radius: 22px;
  background: #fffaf3;
  box-shadow: 0 24px 60px rgba(35, 24, 15, 0.24);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.detail-header h2 {
  margin: 0;
  font-size: 22px;
}

.close-btn {
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 999px;
  background: #ead8c1;
  color: #5b3f2b;
  cursor: pointer;
  font-weight: 900;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.detail-grid div {
  padding: 14px;
  border-radius: 14px;
  background: #f8ead8;
}

.detail-grid span {
  display: block;
  margin-bottom: 6px;
  color: #8a7462;
  font-size: 13px;
}

.detail-grid strong {
  color: #3f2f24;
  word-break: break-word;
}

.full-row {
  grid-column: 1 / -1;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 18px;
}

.cancel-btn {
  border: none;
  border-radius: 12px;
  padding: 10px 16px;
  background: #ead8c1;
  color: #5b3f2b;
  cursor: pointer;
  font-weight: 800;
}
</style>