<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView } from 'vue-router'
import { userAuthStore } from '@/stores/auth'

const authStore = userAuthStore()

const isAdmin = computed(() => authStore.hasRoles('ADMIN'))
const isStaff = computed(() => authStore.hasRoles('STAFF'))
const canManage = computed(() => isAdmin.value || isStaff.value)
</script>

<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <!-- 返回論壇 -->
      <RouterLink class="back-btn" to="/community"> ← 返回論壇首頁 </RouterLink>

      <h2>後台管理</h2>

      <nav>
<RouterLink v-if="canManage" to="/forum-admin/dashboard">後台首頁</RouterLink>
<RouterLink v-if="canManage" to="/forum-admin/posts">文章管理</RouterLink>
<RouterLink v-if="isAdmin" to="/forum-admin/users">會員管理</RouterLink>
<RouterLink v-if="isAdmin" to="/forum-admin/roles">角色權限</RouterLink>
<RouterLink v-if="canManage" to="/forum-admin/reports">檢舉管理</RouterLink>
<RouterLink v-if="canManage" to="/forum-admin/appeals">申訴管理</RouterLink>
      </nav>
    </aside>

    <main class="admin-main">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f8f3ea;
}

.admin-sidebar {
  width: 240px;
  padding: 24px;
  background: #75533b;
  color: #fff;
}

.admin-sidebar h2 {
  margin-bottom: 24px;
  font-size: 22px;
}

.admin-sidebar nav {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.admin-sidebar a {
  color: #f7ead7;
  text-decoration: none;
  padding: 10px 12px;
  border-radius: 10px;
}

.admin-sidebar a.router-link-active {
  background: rgba(255, 255, 255, 0.18);
}

.admin-main {
  flex: 1;
  padding: 32px;
}
</style>
