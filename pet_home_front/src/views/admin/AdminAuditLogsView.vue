<script setup>
import { computed, onMounted, ref } from 'vue'
import { getAuditLogsApi } from '@/api/admin'
import { RefreshCw, Search, Eye, X } from 'lucide-vue-next'

const auditLogs = ref([])
const loading = ref(false)
const apiError = ref('')
const keyword = ref('')
const selectedLog = ref(null)
const detailOpen = ref(false)
const selectedModule = ref('ALL')

const moduleTabs = [
  {
    label: '全部',
    value: 'ALL',
  },
  {
    label: '後台',
    value: 'ADMIN',
  },
  {
    label: '會員',
    value: 'MEMBER',
  },
  {
    label: '認證',
    value: 'AUTH',
  },
  {
    label: '權限',
    value: 'RBAC',
  },
  {
    label: '寵物',
    value: 'PET',
  },
  {
    label: '商城',
    value: 'SHOP',
  },
  {
    label: '美容',
    value: 'WASH',
  },
  {
    label: '論壇',
    value: 'BLOG',
  },
  {
    label: '地圖',
    value: 'MAP',
  },
]

async function loadAuditLogs() {
  loading.value = true
  apiError.value = ''

  try {
    const response = await getAuditLogsApi()
    auditLogs.value = response.data.data || []
  } catch (error) {
    console.error('取得 Audit Log 失敗', error)
    apiError.value = error.response?.data?.message || '取得操作紀錄失敗'
  } finally {
    loading.value = false
  }
}

const filteredLogs = computed(() => {
  const key = keyword.value.trim().toLowerCase()

  return auditLogs.value.filter((log) => {
    const matchModule = selectedModule.value === 'ALL' || log.moduleCode === selectedModule.value

    const matchKeyword =
      !key ||
      String(log.id || '').includes(key) ||
      String(log.actorEmail || '')
        .toLowerCase()
        .includes(key) ||
      String(log.actorUserId || '')
        .toLowerCase()
        .includes(key) ||
      String(log.action || '')
        .toLowerCase()
        .includes(key) ||
      String(log.moduleCode || '')
        .toLowerCase()
        .includes(key) ||
      String(log.targetType || '')
        .toLowerCase()
        .includes(key) ||
      String(log.targetId || '').includes(key) ||
      String(log.ipAddress || '')
        .toLowerCase()
        .includes(key)

    return matchModule && matchKeyword
  })
})

function openDetail(log) {
  selectedLog.value = log
  detailOpen.value = true
}

function closeDetail() {
  selectedLog.value = null
  detailOpen.value = false
}

function formatDateTime(value) {
  if (!value) return '-'

  return String(value).replace('T', ' ')
}

function formatMetadata(metadata) {
  if (!metadata) return '-'

  try {
    const parsed = typeof metadata === 'string' ? JSON.parse(metadata) : metadata
    return JSON.stringify(parsed, null, 2)
  } catch {
    return metadata
  }
}

function actionLabel(action) {
  const code = String(action || '').trim()
  const map = {
    ADMIN_UPDATE_ROLE_PERMISSION: '修改角色權限',
    PASSWORD_RESET_REQUESTED: '更改密碼請求',
    PASSWORD_RESET_SUCCESS: '更改密碼確認',
    EMAIL_CHANGE_REQUESTED: '更改信箱請求',
    EMAIL_CHANGE_CONFIRMED: '更改信箱確認',
    ADMIN_UPDATE_USER_STATUS: '修改使用者狀態',
    TOTP_DISABLED: '關閉兩步驟驗證',
    ADMIN_UPDATE_USER_ROLE: '修改使用者角色',
    ENABLE_TOTP: '啟用兩步驟驗證',
    USER_CHANGE_PASSWORD: '修改密碼',
    USER_LOGIN: '使用者登入',
    USER_LOGOUT: '使用者登出',
    PROFILE_UPDATE: '修改會員資料',
    PET_CREATE: '新增寵物',
    PET_UPDATE: '修改寵物',
    PET_DELETE: '刪除寵物',
  }

  return map[code] || code || '-'
}

function moduleLabel(moduleCode) {
  const map = {
    MEMBER: '會員',
    AUTH: '認證',
    ADMIN: '後台',
    PET: '寵物',
    RBAC: '權限',
    AUDIT: '稽核',
    USER_SECURITY: '會員',
  }

  return map[moduleCode] || moduleCode || '-'
}

onMounted(() => {
  loadAuditLogs()
})
</script>

<template>
  <section class="admin-audit-page">
    <div class="page-header">
      <div>
        <h1>操作紀錄</h1>
        <p>查看管理員與系統重要操作紀錄。</p>
      </div>

      <button class="primary-btn" type="button" :disabled="loading" @click="loadAuditLogs">
        <RefreshCw :size="16" :class="{ spinning: loading }" />
        重新整理
      </button>
    </div>

    <div class="audit-toolbar">
      <div class="search-box">
        <Search :size="18" />
        <input v-model="keyword" type="text" placeholder="搜尋操作人、模組、動作、目標 ID、IP..." />
      </div>

      <div class="record-count">共 {{ filteredLogs.length }} 筆紀錄</div>
    </div>

    <div class="module-filter-tabs">
      <button
        v-for="tab in moduleTabs"
        :key="tab.value"
        class="module-filter-tab"
        :class="{ active: selectedModule === tab.value }"
        type="button"
        @click="selectedModule = tab.value"
      >
        {{ tab.label }}
      </button>
    </div>

    <div v-if="apiError" class="error-message">
      {{ apiError }}
    </div>

    <div class="audit-table-card">
      <table class="audit-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>時間</th>
            <th>操作人</th>
            <th>模組</th>
            <th>操作</th>
            <th>目標</th>
            <th>IP</th>
            <th>User Agent</th>
            <th>詳情</th>
          </tr>
        </thead>

        <tbody>
          <tr v-if="loading">
            <td colspan="9" class="empty-cell">載入中...</td>
          </tr>

          <tr v-else-if="filteredLogs.length === 0">
            <td colspan="9" class="empty-cell">目前沒有操作紀錄</td>
          </tr>

          <tr v-for="log in filteredLogs" v-else :key="log.id">
            <td>{{ log.id }}</td>

            <td class="time-cell">
              {{ formatDateTime(log.createdAt) }}
            </td>

            <td>
              <div class="actor-cell">
                <strong>{{ log.actorEmail || '-' }}</strong>
                <span v-if="log.actorUserId">ID：{{ log.actorUserId }}</span>
              </div>
            </td>

            <td>
              <span class="module-badge">
                {{ moduleLabel(log.moduleCode) }}
              </span>
            </td>

            <td>
              <div class="action-cell">
                <strong>{{ actionLabel(log.action) }}</strong>
                <span>{{ log.action }}</span>
              </div>
            </td>

            <td>
              <div class="target-cell">
                <strong>{{ log.targetType || '-' }}</strong>
                <span v-if="log.targetId">#{{ log.targetId }}</span>
              </div>
            </td>

            <td>{{ log.ipAddress || '-' }}</td>

            <td class="user-agent-cell" :title="log.userAgent">
              {{ log.userAgent || '-' }}
            </td>

            <td>
              <button class="soft-btn" type="button" @click="openDetail(log)">
                <Eye :size="15" />
                查看
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="detailOpen" class="modal-mask" @click.self="closeDetail">
      <div class="detail-modal">
        <div class="modal-header">
          <div>
            <h3>操作紀錄詳情</h3>
            <p>Audit Log #{{ selectedLog?.id }}</p>
          </div>

          <button class="icon-btn" type="button" @click="closeDetail">
            <X :size="20" />
          </button>
        </div>

        <div class="detail-grid">
          <div class="detail-item">
            <span>時間</span>
            <strong>{{ formatDateTime(selectedLog?.createdAt) }}</strong>
          </div>

          <div class="detail-item">
            <span>操作人</span>
            <strong>{{ selectedLog?.actorEmail || '-' }}</strong>
          </div>

          <div class="detail-item">
            <span>模組</span>
            <strong>{{ selectedLog?.moduleCode || '-' }}</strong>
          </div>

          <div class="detail-item">
            <span>操作</span>
            <strong>{{ selectedLog?.action || '-' }}</strong>
          </div>

          <div class="detail-item">
            <span>目標類型</span>
            <strong>{{ selectedLog?.targetType || '-' }}</strong>
          </div>

          <div class="detail-item">
            <span>目標 ID</span>
            <strong>{{ selectedLog?.targetId || '-' }}</strong>
          </div>

          <div class="detail-item">
            <span>IP</span>
            <strong>{{ selectedLog?.ipAddress || '-' }}</strong>
          </div>

          <div class="detail-item full">
            <span>User Agent</span>
            <strong>{{ selectedLog?.userAgent || '-' }}</strong>
          </div>
        </div>

        <div class="metadata-box">
          <div class="metadata-title">Metadata</div>
          <pre>{{ formatMetadata(selectedLog?.metadata || selectedLog?.metaData) }}</pre>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped lang="scss">
.admin-audit-page {
  min-height: 100%;
  padding: 40px 32px;
  background: #f7f1e8;
  color: #2f2118;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 26px;

  h1 {
    margin: 0 0 8px;
    font-size: 32px;
    font-weight: 900;
    letter-spacing: 0.04em;
    color: #3d2516;
  }

  p {
    margin: 0;
    color: #8a6f5b;
    font-size: 15px;
  }
}

.primary-btn,
.soft-btn,
.icon-btn {
  border: 0;
  cursor: pointer;
  font-weight: 800;
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease,
    background 0.18s ease;
}

.primary-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 13px 22px;
  border-radius: 999px;
  background: #8a6040;
  color: #fff;
  box-shadow: 0 12px 26px rgba(99, 64, 39, 0.18);

  &:hover:not(:disabled) {
    transform: translateY(-1px);
    background: #795538;
  }

  &:disabled {
    opacity: 0.58;
    cursor: not-allowed;
  }
}

.spinning {
  animation: spin 0.8s linear infinite;
}

.audit-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 18px;
}

.search-box {
  width: min(560px, 100%);
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 16px;
  border-radius: 18px;
  background: #fffaf3;
  border: 1px solid rgba(171, 133, 94, 0.2);
  box-shadow: 0 10px 24px rgba(70, 47, 31, 0.05);
  color: #8a6040;

  input {
    width: 100%;
    height: 48px;
    border: 0;
    outline: 0;
    background: transparent;
    color: #2f2118;
    font-size: 14px;

    &::placeholder {
      color: #b39b86;
    }
  }
}

.record-count {
  color: #9a7558;
  font-size: 14px;
  font-weight: 700;
  white-space: nowrap;
}

.error-message {
  margin-bottom: 16px;
  padding: 13px 16px;
  border-radius: 16px;
  background: #fff0eb;
  color: #b84e3e;
  font-weight: 700;
}

.audit-table-card {
  overflow: hidden;
  border-radius: 18px;
  background: #fffaf3;
  border: 1px solid rgba(171, 133, 94, 0.16);
  box-shadow: 0 18px 40px rgba(70, 47, 31, 0.08);
}

.audit-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;

  th {
    padding: 17px 16px;
    background: #eadcc9;
    color: #2f2118;
    text-align: left;
    font-size: 14px;
    font-weight: 900;
    border-bottom: 1px solid rgba(171, 133, 94, 0.24);
  }

  td {
    padding: 16px;
    color: #2f2118;
    font-size: 14px;
    border-bottom: 1px solid rgba(171, 133, 94, 0.14);
    vertical-align: middle;
  }

  tr:last-child td {
    border-bottom: 0;
  }

  tr:hover td {
    background: #fff6e9;
  }

  th:nth-child(1),
  td:nth-child(1) {
    width: 70px;
  }

  th:nth-child(2),
  td:nth-child(2) {
    width: 170px;
  }

  th:nth-child(4),
  td:nth-child(4) {
    width: 110px;
  }

  th:nth-child(6),
  td:nth-child(6) {
    width: 150px;
  }

  th:nth-child(7),
  td:nth-child(7) {
    width: 130px;
  }

  th:nth-child(9),
  td:nth-child(9) {
    width: 100px;
  }
}

.time-cell {
  color: #7a6655;
  white-space: nowrap;
}

.actor-cell,
.action-cell,
.target-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;

  strong {
    font-size: 14px;
    color: #2f2118;
  }

  span {
    color: #9a7558;
    font-size: 12px;
  }
}

.module-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 58px;
  padding: 6px 10px;
  border-radius: 999px;
  background: #eadcc9;
  color: #7b5538;
  font-size: 12px;
  font-weight: 900;
}

.module-filter-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 0 0 18px;
}

.module-filter-tab {
  border: 1px solid rgba(171, 133, 94, 0.18);
  border-radius: 999px;
  padding: 9px 16px;
  background: #fffaf3;
  color: #7a604d;
  font-size: 14px;
  font-weight: 900;
  cursor: pointer;
  box-shadow: 0 8px 18px rgba(70, 47, 31, 0.04);
  transition:
    background 0.18s ease,
    color 0.18s ease,
    border-color 0.18s ease,
    box-shadow 0.18s ease,
    transform 0.18s ease;
}

.module-filter-tab:hover {
  transform: translateY(-1px);
  background: #f3e3cf;
  border-color: rgba(138, 96, 64, 0.24);
  color: #5f3e29;
}

.module-filter-tab.active {
  background: #8a6040;
  border-color: #8a6040;
  color: #fff;
  box-shadow: 0 12px 24px rgba(99, 64, 39, 0.18);
}

.user-agent-cell {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #7a6655;
}

.soft-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-direction: row;
  gap: 6px;

  min-width: 72px;
  height: 34px;
  padding: 0 12px;

  border: 0;
  border-radius: 12px;
  background: #efe1cf;
  color: #6d4b33;

  font-size: 13px;
  font-weight: 800;
  line-height: 1;
  white-space: nowrap;

  cursor: pointer;
  transition:
    transform 0.18s ease,
    background 0.18s ease,
    box-shadow 0.18s ease;

  svg {
    flex: 0 0 auto;
  }

  &:hover {
    transform: translateY(-1px);
    background: #e4cfb5;
    box-shadow: 0 8px 18px rgba(99, 64, 39, 0.12);
  }
}

.empty-cell {
  height: 120px;
  text-align: center;
  color: #9a7558;
  font-weight: 700;
}

.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 1200;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(41, 29, 20, 0.38);
  backdrop-filter: blur(4px);
}

.detail-modal {
  width: min(760px, 100%);
  max-height: min(82vh, 720px);
  overflow: auto;
  border-radius: 28px;
  background: #fffaf3;
  border: 1px solid rgba(171, 133, 94, 0.22);
  box-shadow: 0 26px 70px rgba(54, 42, 32, 0.28);
}

.modal-header {
  position: sticky;
  top: 0;
  z-index: 1;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  padding: 24px 26px;
  background: #eadcc9;
  border-bottom: 1px solid rgba(171, 133, 94, 0.22);

  h3 {
    margin: 0 0 6px;
    color: #2f2118;
    font-size: 22px;
    font-weight: 900;
  }

  p {
    margin: 0;
    color: #8a6f5b;
    font-size: 13px;
    font-weight: 700;
  }
}

.icon-btn {
  width: 38px;
  height: 38px;
  display: grid;
  place-items: center;
  border-radius: 999px;
  background: rgba(138, 96, 64, 0.12);
  color: #6d4b33;

  &:hover {
    background: rgba(138, 96, 64, 0.2);
  }
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  padding: 24px 26px 10px;
}

.detail-item {
  padding: 14px 16px;
  border-radius: 16px;
  background: #f6ead9;
  border: 1px solid rgba(171, 133, 94, 0.16);

  span {
    display: block;
    margin-bottom: 6px;
    color: #9a7558;
    font-size: 12px;
    font-weight: 800;
  }

  strong {
    color: #2f2118;
    font-size: 14px;
    word-break: break-word;
  }

  &.full {
    grid-column: 1 / -1;
  }
}

.metadata-box {
  margin: 14px 26px 26px;
  overflow: hidden;
  border-radius: 18px;
  background: #86674e;
  border: 1px solid rgba(171, 133, 94, 0.24);

  .metadata-title {
    padding: 12px 16px;
    background: rgba(255, 255, 255, 0.08);
    color: #f8ead6;
    font-size: 13px;
    font-weight: 900;
  }

  pre {
    margin: 0;
    padding: 16px;
    overflow: auto;
    color: #f8ead6;
    font-size: 13px;
    line-height: 1.7;
    white-space: pre-wrap;
    word-break: break-word;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 960px) {
  .admin-audit-page {
    padding: 28px 18px;
  }

  .page-header,
  .audit-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .audit-table-card {
    overflow-x: auto;
  }

  .audit-table {
    min-width: 1100px;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
