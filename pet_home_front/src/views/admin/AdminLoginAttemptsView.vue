<script setup>
import { computed, onMounted, ref } from 'vue'
import { RefreshCw, Search } from 'lucide-vue-next'
import { getLoginAttemptsApi } from '@/api/admin'

const attempts = ref([])
const loading = ref(false)
const errorMessage = ref('')
const keyword = ref('')

const activeFilter = ref('ALL')

const loginAttemptFilters = [
  { label: '全部', value: 'ALL' },
  { label: '成功', value: 'SUCCESS' },
  { label: '失敗', value: 'FAILED' },
  { label: '帳號不存在', value: 'USER_NOT_FOUND' },
  { label: '密碼錯誤', value: 'INVALID_PASSWORD' },
  { label: '帳號鎖定', value: 'ACCOUNT_LOCKED_BY_PASSWORD_FAILED' },
]

function formatDateTime(value) {
  if (!value) return '-'
  return value.replace('T', ' ')
}

function formatResult(success) {
  return success ? '成功' : '失敗'
}

function formatFailureReason(reason) {
  const map = {
    ACCOUNT_INACTIVE:'帳號鎖定中',
    ACCOUNT_SUSPENDED:'帳號停權中',
    USER_NOT_FOUND: '帳號不存在',
    INVALID_PASSWORD: '密碼錯誤',
    INVALID_TOTP_CODE: 'TOTP驗證錯誤',
    ACCOUNT_LOCKED_BY_PASSWORD_FAILED: '密碼錯誤次數過多，帳號已鎖定',
    ACCOUNT_LOCKED: '帳號鎖定中',
    EMAIL_NOT_VERIFIED: 'Email 尚未驗證',
    TOTP_REQUIRED: '需要兩步驟驗證',
    TOTP_FAILED: '兩步驟驗證失敗',
    LOGIN_CHALLENGE_EXPIRED: '登入驗證已過期',
    LOGIN_CHALLENGE_INVALID: '登入驗證無效',
  }

  if (!reason) return '-'
  return map[reason] || reason
}

const filteredAttempts = computed(() => {
  const key = keyword.value.trim().toLowerCase()

  return attempts.value.filter((item) => {
    const matchKeyword =
      !key ||
      String(item.id).includes(key) ||
      item.email?.toLowerCase().includes(key) ||
      String(item.userId || '').includes(key) ||
      item.ipAddress?.toLowerCase().includes(key) ||
      item.failureReason?.toLowerCase().includes(key) ||
      item.userAgent?.toLowerCase().includes(key)

    let matchFilter = true

    if (activeFilter.value === 'SUCCESS') {
      matchFilter = item.success === true
    } else if (activeFilter.value === 'FAILED') {
      matchFilter = item.success === false
    } else if (activeFilter.value !== 'ALL') {
      matchFilter = item.failureReason === activeFilter.value
    }

    return matchKeyword && matchFilter
  })
})

async function fetchLoginAttempts() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getLoginAttemptsApi()
    const data = response.data.data

    if (Array.isArray(data)) {
      attempts.value = data
    } else if (Array.isArray(data?.loginAttempts)) {
      attempts.value = data.loginAttempts
    } else {
      attempts.value = []
    }
  } catch (error) {
    console.error('取得登入紀錄失敗', error)
    errorMessage.value = error.response?.data?.message || '取得登入紀錄失敗'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchLoginAttempts()
})
</script>

<template>
  <section class="admin-page login-attempts-page">
    <div class="page-header">
      <div>
        <h1>登入紀錄</h1>
        <p>查看會員登入成功、失敗與異常登入原因。</p>
      </div>

      <button class="refresh-btn" type="button" @click="fetchLoginAttempts">
        <RefreshCw :size="16" :class="{ spinning: loading }" />
        重新整理
      </button>
    </div>

    <div class="search-box">
      <Search class="search-icon" :size="18" :stroke-width="2.2" />

      <input
        v-model.trim="keyword"
        type="text"
        placeholder="搜尋 Email、IP、結果、失敗原因、User-Agent..."
      />

      <button v-if="keyword" type="button" class="clear-search" @click="keyword = ''">×</button>
    </div>

    <div class="filter-chips">
      <button
        v-for="filter in loginAttemptFilters"
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
      <div v-if="loading" class="empty-text">載入登入紀錄中...</div>

      <table v-else class="attempts-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>時間</th>
            <th>Email</th>
            <th>使用者 ID</th>
            <th>結果</th>
            <th>失敗原因</th>
            <th>IP</th>
            <th>User-Agent</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="item in filteredAttempts" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ formatDateTime(item.attemptedAt) }}</td>
            <td>{{ item.email || '-' }}</td>
            <td>{{ item.userId || '-' }}</td>

            <td>
              <span class="result-badge" :class="item.success ? 'success' : 'failed'">
                {{ formatResult(item.success) }}
              </span>
            </td>

            <td>
              <span class="reason-text">
                {{ formatFailureReason(item.failureReason) }}
              </span>
            </td>

            <td>{{ item.ipAddress || '-' }}</td>

            <td class="user-agent">
              {{ item.userAgent || '-' }}
            </td>
          </tr>

          <tr v-if="filteredAttempts.length === 0">
            <td colspan="8" class="empty-text">
              {{ keyword ? '找不到符合條件的登入紀錄' : '目前沒有登入紀錄' }}
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

.attempts-table {
  width: 100%;
  border-collapse: collapse;
}

.attempts-table th {
  background: #eadcc8;
  color: #2f1f16;
  padding: 15px 16px;
  font-size: 14px;
  font-weight: 900;
  text-align: left;
  white-space: nowrap;
}

.attempts-table td {
  padding: 14px 16px;
  border-top: 1px solid rgba(138, 113, 89, 0.1);
  color: #4f3928;
  font-size: 14px;
  vertical-align: middle;
}

.result-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 56px;
  height: 28px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 900;
  white-space: nowrap;
  line-height: 1;
}

.result-badge.success {
  background: rgba(124, 166, 160, 0.18);
  color: #4f827a;
}

.result-badge.failed {
  background: rgba(199, 83, 69, 0.12);
  color: #b94d42;
}

.reason-text {
  color: #8a5b36;
  font-weight: 800;
}

.user-agent {
  max-width: 240px;
  color: #7b6655;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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

@media (max-width: 900px) {
  .admin-page {
    padding: 56px 18px 40px;
  }

  .table-card {
    overflow-x: auto;
  }

  .attempts-table {
    min-width: 900px;
  }
}
</style>
