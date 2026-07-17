<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

import { useAdminUserStore } from '@/petpost/stores/adminUserStore'
import { userAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = userAuthStore()
const adminUserStore = useAdminUserStore()

function isActiveUser(user) {
  return user.status === 'ACTIVE'
}

function isSuspendedUser(user) {
  return user.status === 'SUSPENDED'
}

onMounted(async () => {
  if (!authStore.canAccessBackstage) {
    alert('你沒有後台權限')
    router.push('/')
    return
  }

  await adminUserStore.loadUsers()
})
</script>

<template>
  <div>
    <h2 class="mb-4">會員管理</h2>

    <div v-if="adminUserStore.loading" class="alert alert-secondary">會員資料載入中...</div>

    <div v-else-if="adminUserStore.errorMessage" class="alert alert-danger">
      {{ adminUserStore.errorMessage }}
    </div>

    <div v-else-if="adminUserStore.users.length === 0" class="alert alert-info">
      目前沒有會員資料
    </div>

    <table v-else class="table table-bordered align-middle">
      <thead>
        <tr>
          <th>編號</th>
          <th>Email</th>
          <th>帳號</th>
          <th>角色</th>
          <th>文章數</th>
          <th>被檢舉次數</th>
          <th>狀態</th>
          <th>操作</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="user in adminUserStore.users" :key="user.userId">
          <td>{{ user.userId }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.userName || '未設定暱稱' }}</td>
          <td>{{ user.roleCode || 'USER' }}</td>
          <td>{{ user.postCount ?? 0 }}</td>
          <td>{{ user.reportCount ?? 0 }}</td>
          <td>
            <span v-if="isActiveUser(user)" class="badge text-bg-success"> 正常 </span>

            <span v-else-if="isSuspendedUser(user)" class="badge text-bg-warning"> 停權 </span>

            <span v-else class="badge text-bg-secondary">
              {{ user.status }}
            </span>
          </td>

          <td>
            <button
              v-if="isActiveUser(user)"
              class="btn btn-warning btn-sm"
              type="button"
              @click="adminUserStore.suspendUser(user.userId)"
            >
              停權
            </button>

            <button
              v-else-if="isSuspendedUser(user)"
              class="btn btn-success btn-sm"
              type="button"
              @click="adminUserStore.activateUser(user.userId)"
            >
              解除停權
            </button>

            <span v-else class="text-secondary"> 無可用操作 </span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
