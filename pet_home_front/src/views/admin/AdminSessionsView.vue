<script setup>
import { computed, onMounted, ref } from 'vue'
import { RefreshCw, Search } from 'lucide-vue-next'
import { getAdminSessionsApi } from '@/api/admin'

const sessions = ref([])
const loading = ref(false)
const errorMessage = ref('')
const keyword = ref('')

const activeFilter = ref('ALL')

const sessionFilters = [
  { label: '全部', value: 'ALL' },
  { label: '有效', value: 'ACTIVE' },
  { label: '已失效', value: 'REVOKED' },
  { label: '使用者登出', value: 'USER_LOGOUT' },
  { label: 'Token 刷新', value: 'REFRESH_ROTATED' },
  { label: '修改密碼', value: 'PASSWORD_CHANGE' },
  { label: '修改信箱', value: 'EMAIL_CHANGE' },
  { label: '新登入', value: 'NEW_LOGIN' },
]

function formatDateTime(value) {
  if (!value) return '-'
  return value.replace('T', ' ')
}

function formatSessionStatus(active) {
  return active ? '有效' : '已失效'
}

function formatRevokedReason(reason) {
  const map = {
    USER_STATUS_CHANGE:'使用者狀態異動',
    ROLE_CHANGE:'使用者角色更改',
    ROLE_PERMISSION_CHANGED:'角色權限已變更',
    USER_LOGOUT: '使用者登出',
    REFRESH_ROTATED: 'Token 已刷新',
    PASSWORD_RESET:'密碼重設',
    PASSWORD_CHANGE: '修改密碼',
    EMAIL_CHANGE: '修改信箱',
    NEW_LOGIN: '新登入取代舊登入',
    ADMIN_STATUS_CHANGE: '管理員異動狀態',
    TOKEN_VERSION_CHANGED: 'Token 版本已變更',
    PERMISSION_VERSION_CHANGED: '權限版本已變更',
    SESSION_EXPIRED: 'Session 已過期',
  }

  if (!reason) return '-'
  return map[reason] || reason
}

const filteredSessions = computed(() => {
  const key = keyword.value.trim().toLowerCase()

  return sessions.value.filter((item) => {
    const revokedReasonText = formatRevokedReason(item.revokedReason).toLowerCase()
    const statusText = formatSessionStatus(item.active).toLowerCase()

    const matchKeyword =
      !key ||
      String(item.id).includes(key) ||
      String(item.userId || '').includes(key) ||
      item.email?.toLowerCase().includes(key) ||
      item.ipAddress?.toLowerCase().includes(key) ||
      item.revokedReason?.toLowerCase().includes(key) ||
      revokedReasonText.includes(key) ||
      statusText.includes(key)

    let matchFilter = true

    if (activeFilter.value === 'ACTIVE') {
      matchFilter = item.active === true
    } else if (activeFilter.value === 'REVOKED') {
      matchFilter = item.active === false
    } else if (activeFilter.value !== 'ALL') {
      matchFilter = item.revokedReason === activeFilter.value
    }

    return matchKeyword && matchFilter
  })
})

async function fetchSessions() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getAdminSessionsApi()
    const data = response.data.data

    if (Array.isArray(data)) {
      sessions.value = data
    } else if (Array.isArray(data?.sessions)) {
      sessions.value = data.sessions
    } else {
      sessions.value = []
    }
  } catch (error) {
    console.error('取得 Session 紀錄失敗', error)
    errorMessage.value = error.response?.data?.message || '取得 Session 紀錄失敗'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchSessions()
})
</script>

<template>
  <section class="admin-page sessions-page">
    <div class="page-header">
      <div>
        <h1>登入 Session</h1>
        <p>查看會員登入 Session 狀態、Token Rotation 與撤銷原因。</p>
      </div>

      <button class="refresh-btn" type="button" @click="fetchSessions">
        <RefreshCw :size="16" :class="{ spinning: loading }" />
        重新整理
      </button>
    </div>

    <div class="search-box">
      <Search class="search-icon" :size="18" :stroke-width="2.2" />

      <input
        v-model.trim="keyword"
        type="text"
        placeholder="搜尋 Email、使用者 ID、IP、狀態、撤銷原因..."
      />

      <button v-if="keyword" type="button" class="clear-search" @click="keyword = ''">×</button>
    </div>

    <div class="filter-chips">
      <button
        v-for="filter in sessionFilters"
        :key="filter.value"
        type="button"
        class="filter-chip"
        :class="{ active: activeFilter === filter.value }"
        @click="activeFilter = filter.value"
      >
        {{ filter.label }}
      </button>
    </div>

    <p v-if="errorMessage" class="message error">
      {{ errorMessage }}
    </p>

    <div class="table-card">
      <div v-if="loading" class="empty-text">載入 Session 紀錄中...</div>

      <table v-else class="sessions-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>狀態</th>
            <th>Email</th>
            <th>使用者 ID</th>
            <th>IP</th>
            <th>登入時間</th>
            <th>最後使用</th>
            <th>過期時間</th>
            <th>撤銷時間</th>
            <th>撤銷原因</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="item in filteredSessions" :key="item.id">
            <td>{{ item.id }}</td>

            <td>
              <span class="status-badge" :class="item.active ? 'active' : 'revoked'">
                {{ formatSessionStatus(item.active) }}
              </span>
            </td>

            <td>{{ item.email || '-' }}</td>
            <td>{{ item.userId || '-' }}</td>
            <td>{{ item.ipAddress || '-' }}</td>
            <td>{{ formatDateTime(item.loginAt) }}</td>
            <td>{{ formatDateTime(item.lastUsedAt) }}</td>
            <td>{{ formatDateTime(item.expiresAt) }}</td>
            <td>{{ formatDateTime(item.revokedAt) }}</td>

            <td>
              <span class="reason-text">
                {{ formatRevokedReason(item.revokedReason) }}
              </span>
            </td>
          </tr>

          <tr v-if="filteredSessions.length === 0">
            <td colspan="10" class="empty-text">
              {{ keyword ? '找不到符合條件的 Session 紀錄' : '目前沒有 Session 紀錄' }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
.admin-page {
  margin: 0 auto;
  padding: 40px 32px 56px;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 26px;
}

.page-header h1 {
  margin: 0 0 8px;
  color: #2f1f16;
  font-size: 32px;
  font-weight: 950;
  letter-spacing: 0.02em;
}

.page-header p {
  margin: 0;
  color: #8b6d50;
  font-size: 15px;
  line-height: 1.7;
}

.refresh-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border: 0;
  border-radius: 999px;
  padding: 11px 18px;
  background: #8a6040;
  color: #fff;
  font-weight: 900;
  cursor: pointer;
  box-shadow: 0 12px 24px rgba(124, 166, 160, 0.2);
}

.refresh-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.spinning {
  animation: spin 0.9s linear infinite;
}

.search-box {
  width: min(560px, 100%);
  height: 50px;
  margin: 0 0 20px;
  padding: 0 16px;
  border-radius: 18px;
  background: #fffaf3;
  border: 1px solid rgba(171, 133, 94, 0.2);
  box-shadow: 0 10px 24px rgba(70, 47, 31, 0.05);
  color: #8a6040;
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-icon {
  flex: 0 0 auto;
  color: #a9825d;
}

.search-box input {
  flex: 1;
  min-width: 0;
  height: 100%;
  border: 0;
  outline: none;
  background: transparent;
  color: #3f3024;
  font-size: 14px;
  font-weight: 700;
}

.search-box input::placeholder {
  color: rgba(111, 93, 77, 0.55);
  font-weight: 700;
}

.clear-search {
  flex: 0 0 auto;
  width: 28px;
  height: 28px;
  border: 0;
  border-radius: 50%;
  background: rgba(138, 113, 89, 0.12);
  color: #7b5f49;
  font-size: 20px;
  line-height: 1;
  cursor: pointer;
}

.filter-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: 18px 0 20px;
}

.filter-chip {
  min-width: 64px;
  height: 38px;
  padding: 0 18px;
  border: 1px solid rgba(138, 113, 89, 0.16);
  border-radius: 999px;
  background: rgba(255, 250, 242, 0.96);
  color: #6f5844;
  font-size: 14px;
  font-weight: 800;
  line-height: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 8px 18px rgba(73, 50, 30, 0.06);
  transition:
    transform 0.18s ease,
    background 0.18s ease,
    color 0.18s ease,
    border-color 0.18s ease,
    box-shadow 0.18s ease;
}

.filter-chip:hover {
  transform: translateY(-1px);
  background: #fff4e5;
  box-shadow: 0 10px 22px rgba(73, 50, 30, 0.1);
}

.filter-chip.active {
  background: #8a5f3d;
  color: #fffaf2;
  border-color: #8a5f3d;
  box-shadow: 0 12px 24px rgba(138, 95, 61, 0.22);
}

.table-card {
  overflow: hidden;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(138, 113, 89, 0.14);
  box-shadow: 0 16px 36px rgba(65, 48, 34, 0.05);
}

.sessions-table {
  width: 100%;
  border-collapse: collapse;
}

.sessions-table th {
  background: #eadcc8;
  color: #2f1f16;
  padding: 15px 16px;
  font-size: 14px;
  font-weight: 900;
  text-align: left;
  white-space: nowrap;
}

.sessions-table td {
  padding: 14px 16px;
  border-top: 1px solid rgba(138, 113, 89, 0.1);
  color: #4f3928;
  font-size: 14px;
  vertical-align: middle;
  white-space: nowrap;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 64px;
  height: 28px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 900;
  white-space: nowrap;
  line-height: 1;
}

.status-badge.active {
  background: rgba(124, 166, 160, 0.18);
  color: #4f827a;
}

.status-badge.revoked {
  background: rgba(199, 83, 69, 0.12);
  color: #b94d42;
}

.reason-text {
  color: #8a5b36;
  font-weight: 800;
}

.empty-text {
  padding: 28px;
  text-align: center;
  color: #8b6d50;
  font-weight: 800;
}

.message.error {
  margin: 0 0 16px;
  padding: 12px 14px;
  border-radius: 16px;
  background: rgba(199, 83, 69, 0.08);
  color: #b94d42;
  font-size: 14px;
  font-weight: 800;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 900px) {
  .admin-page {
    padding: 56px 18px 40px;
  }

  .table-card {
    overflow-x: auto;
  }

  .sessions-table {
    min-width: 980px;
  }
}
</style>