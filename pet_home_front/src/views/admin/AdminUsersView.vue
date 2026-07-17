<script setup>
import { computed, onMounted, ref } from 'vue'
import Swal from 'sweetalert2'
import { RefreshCw, Search } from 'lucide-vue-next'
import AdminUserDetailModal from '@/components/admin/AdminUserDetailModal.vue'
import AdminUserRoleModal from '@/components/admin/AdminUserRoleModal.vue'
import AdminUserStatusModal from '@/components/admin/AdminUserStatusModal.vue'
import {
  getAdminUsersApi,
  getAdminUserDetailApi,
  updateAdminUserRoleApi,
  getAllRolesApi,
  updateUserStatusApi,
} from '@/api/admin'

const users = ref([])
const selectedUser = ref(null)
const selectedRoleUser = ref(null)
const selectedStatusUser = ref(null)

const loading = ref(false)
const detailLoading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const roleOptions = ref([])
const keyword = ref('')

const activeFilter = ref('ALL')

const statusFilters = [
  {
    label: '全部',
    value: 'ALL',
  },
  {
    label: '啟用中',
    value: 'ACTIVE',
  },
  {
    label: '停權',
    value: 'BANNED',
  },
  {
    label: '鎖定',
    value: 'LOCKED',
  },
  {
    label: '暫停',
    value: 'SUSPENDED',
  },
  {
    label: 'Email 已驗證',
    value: 'EMAIL_VERIFIED',
  },
  {
    label: 'Email 未驗證',
    value: 'EMAIL_UNVERIFIED',
  },
]

const filteredUsers = computed(() => {
  const key = keyword.value.trim().toLowerCase()

  return users.value.filter((user) => {
    const matchKeyword =
      !key ||
      String(user.id).includes(key) ||
      user.email?.toLowerCase().includes(key) ||
      user.nickname?.toLowerCase().includes(key) ||
      user.status?.toLowerCase().includes(key) ||
      user.roles?.some((role) => role.toLowerCase().includes(key))

    let matchFilter = true

    if (activeFilter.value === 'ACTIVE') {
      matchFilter = user.status === 'ACTIVE'
    } else if (activeFilter.value === 'BANNED') {
      matchFilter = user.status === 'BANNED'
    } else if (activeFilter.value === 'LOCKED') {
      matchFilter = user.status === 'LOCKED'
    } else if (activeFilter.value === 'SUSPENDED') {
      matchFilter = user.status === 'SUSPENDED'
    } else if (activeFilter.value === 'EMAIL_VERIFIED') {
      matchFilter = user.emailVerified === true
    } else if (activeFilter.value === 'EMAIL_UNVERIFIED') {
      matchFilter = user.emailVerified === false
    }

    return matchKeyword && matchFilter
  })
})

function formatDateTime(value) {
  if (!value) return '-'
  return value.replace('T', ' ')
}

async function fetchUsers() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getAdminUsersApi()
    const data = response.data.data

    // 後端如果回 { users: [...] }
    if (Array.isArray(data?.users)) {
      users.value = data.users
    }
    // 後端如果直接回 [...]
    else if (Array.isArray(data)) {
      users.value = data
    } else {
      users.value = []
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '取得會員列表失敗'
  } finally {
    loading.value = false
  }
}

async function fetchRoles() {
  try {
    const response = await getAllRolesApi()
    const data = response.data.data

    if (Array.isArray(data)) {
      roleOptions.value = data
    } else if (Array.isArray(data?.roles)) {
      roleOptions.value = data.roles
    } else {
      roleOptions.value = []
    }
  } catch (error) {
    console.error('取得角色列表失敗', error)
    errorMessage.value = error.response?.data?.message || '取得角色列表失敗'
  }
}

async function handleViewDetail(userId) {
  detailLoading.value = true
  errorMessage.value = ''
  selectedUser.value = null

  try {
    const response = await getAdminUserDetailApi(userId)
    selectedUser.value = response.data.data
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '取得會員詳細資料失敗'
  } finally {
    detailLoading.value = false
  }
}

function openRoleEditor(user) {
  selectedRoleUser.value = user
  successMessage.value = ''
  errorMessage.value = ''
}

function closeRoleEditor() {
  selectedRoleUser.value = null
}

function openStatusEditor(user) {
  selectedStatusUser.value = user
  successMessage.value = ''
  errorMessage.value = ''
}

function closeStatusEditor() {
  selectedStatusUser.value = null
}

function normalizeDateTime(value) {
  if (!value) return null

  // datetime-local 通常是 2026-06-30T23:59
  // 後端 LocalDateTime 可以吃，但補秒數比較穩
  return value.length === 16 ? `${value}:00` : value
}

async function handleUpdateStatus(form) {
  if (!selectedStatusUser.value) return

  if (form.error) {
    errorMessage.value = form.error
    return
  }

  saving.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    await updateUserStatusApi(selectedStatusUser.value.id, {
      status: form.status,
      statusUntil: form.status === 'LOCKED' ? normalizeDateTime(form.statusUntil) : null,
      reason: form.reason || null,
    })

    await Swal.fire({
      icon: 'success',
      title: '儲存成功',
      text: '使用者狀態已更新',
      confirmButtonText: '確認',
      buttonsStyling: false,
      customClass: {
        popup: 'maokilai-swal-popup',
        title: 'maokilai-swal-title',
        htmlContainer: 'maokilai-swal-text',
        confirmButton: 'maokilai-swal-confirm',
      },
    })

    closeStatusEditor()
    await fetchUsers()
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '狀態更新失敗'
  } finally {
    saving.value = false
  }
}

async function handleUpdateRoles(roles) {
  if (!selectedRoleUser.value) return

  saving.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    await updateAdminUserRoleApi(selectedRoleUser.value.id, {
      roles,
    })

    await Swal.fire({
      icon: 'success',
      title: '儲存成功',
      text: '使用者角色已更新',
      confirmButtonText: '確認',
      buttonsStyling: false,
      customClass: {
        popup: 'maokilai-swal-popup',
        title: 'maokilai-swal-title',
        htmlContainer: 'maokilai-swal-text',
        confirmButton: 'maokilai-swal-confirm',
      },
    })

    closeRoleEditor()
    await fetchUsers()
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '角色更新失敗'
  } finally {
    saving.value = false
  }
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

onMounted(() => {
  fetchUsers()
  fetchRoles()
})
</script>

<template>
  <section class="admin-users">
    <div class="page-header">
      <div>
        <h1>會員管理</h1>
        <p>查看會員資料、角色與權限狀態。</p>
      </div>

      <button class="refresh-btn" type="button" @click="fetchUsers">
        <RefreshCw :size="16" :class="{ spinning: loading }" />
        重新整理
      </button>
    </div>

    <div class="search-box">
      <Search class="search-icon" :size="18" :stroke-width="2.2" />

      <input v-model.trim="keyword" type="text" placeholder="搜尋 ID、Email、暱稱、狀態、角色..." />

      <button v-if="keyword" type="button" class="clear-search" @click="keyword = ''">×</button>
    </div>

    <div class="filter-chips">
      <button
        v-for="filter in statusFilters"
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
      <div v-if="loading" class="empty-text">載入會員資料中...</div>

      <table v-else class="users-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Email</th>
            <th>暱稱</th>
            <th>狀態</th>
            <th>Email 驗證</th>
            <th>角色</th>
            <th>最後登入</th>
            <th>操作</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.nickName || user.name || '-' }}</td>
            <td>
              <span class="status-badge">
                {{ user.status }}
              </span>
            </td>
            <td>
              <span
                class="verify-badge"
                :class="{ verified: user.emailVerified, unverified: !user.emailVerified }"
              >
                {{ formatBoolean(user.emailVerified) }}
              </span>
            </td>
            <td>{{ formatRoles(user.roles) }}</td>
            <td>{{ formatDateTime(user.lastLoginAt) || '-' }}</td>
            <td>
              <div class="actions">
                <button type="button" @click="handleViewDetail(user.id)">查看</button>

                <button type="button" @click="openRoleEditor(user)">修改角色</button>

                <button type="button" @click="openStatusEditor(user)">修改狀態</button>
              </div>
            </td>
          </tr>

          <tr v-if="filteredUsers.length === 0">
            <td colspan="8" class="empty-text">
              {{ keyword ? '找不到符合條件的會員' : '目前沒有會員資料' }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="detailLoading" class="modal-mask">
      <div class="detail-modal loading-modal">載入詳細資料中...</div>
    </div>

    <AdminUserDetailModal v-if="selectedUser" :user="selectedUser" @close="selectedUser = null" />

    <AdminUserRoleModal
      v-if="selectedRoleUser"
      :user="selectedRoleUser"
      :role-options="roleOptions"
      :saving="saving"
      @close="closeRoleEditor"
      @save="handleUpdateRoles"
    />

    <AdminUserStatusModal
      v-if="selectedStatusUser"
      :user="selectedStatusUser"
      :saving="saving"
      @close="closeStatusEditor"
      @save="handleUpdateStatus"
    />
  </section>
</template>

<style scoped lang="scss">
.admin-users {
  color: #3f2f24;
  margin: 0 auto;
  padding: 40px 32px 56px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 26px;
}

.page-header h1 {
  margin: 0 0 8px;
  font-size: 32px;
  font-weight: 950;
  letter-spacing: 0.02em;
  color: #2f1f16;
}

.page-header p {
  font-size: 15px;
  margin: 0;
  color: #7a6758;
}

.refresh-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border: none;
  border-radius: 999px;
  padding: 10px 18px;
  background: #7a5230;
  color: #fff;
  cursor: pointer;
  font-weight: 700;
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
  border: 1px solid rgba(138, 113, 89, 0.12);
  border-radius: 999px;
  background: rgba(255, 250, 242, 0.92);
  color: #6f5844;
  font-size: 14px;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 8px 18px rgba(73, 50, 30, 0.06);
  transition:
    transform 0.18s ease,
    background 0.18s ease,
    color 0.18s ease,
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
  background: #fffaf3;
  border: 1px solid rgba(122, 82, 48, 0.14);
  border-radius: 18px;
  box-shadow: 0 12px 28px rgba(80, 54, 34, 0.08);
}

.table-card {
  overflow: hidden;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th,
.users-table td {
  padding: 14px 16px;
  border-bottom: 1px solid rgba(122, 82, 48, 0.12);
  text-align: left;
  font-size: 14px;
}

.users-table th {
  background: #efe1cf;
  color: #4a3527;
  font-weight: 800;
}

.users-table tbody tr:hover {
  background: #fff4e4;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 4px 10px;
  background: #ead8c1;
  color: #5b3f2b;
  font-weight: 700;
  font-size: 12px;
}

.verify-badge {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 4px 10px;
  font-weight: 700;
  font-size: 12px;
}

.verify-badge.verified {
  background: #dff2d8;
  color: #4d7a30;
}

.verify-badge.unverified {
  background: #f3dfcf;
  color: #9a3636;
}

.search-box {
  width: min(560px, 100%);
  height: 50px;
  margin: 0 0 18px;
  padding: 0 16px;
  border-radius: 18px;
  background: #fffaf3;
  border: 1px solid rgba(171, 133, 94, 0.2);
  box-shadow: 0 10px 24px rgba(70, 47, 31, 0.05);
  color: #8a6040;
  display: flex;
  align-items: center;
  gap: 10px;

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

.search-icon {
  flex: 0 0 auto;
  color: #a9825d;
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

.clear-search:hover {
  background: rgba(138, 113, 89, 0.2);
}

.actions {
  display: flex;
  gap: 8px;
}

.actions button,
.detail-header button {
  border: none;
  border-radius: 10px;
  padding: 7px 10px;
  background: #f0dfc9;
  color: #5b3f2b;
  cursor: pointer;
  font-weight: 700;
}

.message {
  margin-bottom: 16px;
  padding: 12px 14px;
  border-radius: 12px;
  font-weight: 700;
}

.message.error {
  background: #ffe8e4;
  color: #b5422f;
}

.message.success {
  background: #e8f5df;
  color: #4d7a30;
}

.empty-text {
  padding: 28px;
  text-align: center;
  color: #8a7462;
}

.loading-modal {
  text-align: center;
  color: #7a6758;
  font-weight: 700;
}
</style>
