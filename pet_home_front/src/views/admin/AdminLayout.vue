<script setup>
import { onMounted } from 'vue'
import { RouterLink, RouterView } from 'vue-router'
import { userAuthStore } from '@/stores/auth'
import { getPermissionApi } from '@/api/admin'

const authStore = userAuthStore()

onMounted(async () => {
  if (!authStore.modulesLoaded) {
    const response = await getPermissionApi()
    authStore.setModules(response.data.data)
  }
})
</script>

<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <h2>後台管理</h2>

      <nav>
        <RouterLink v-if="authStore.hasModules('ADMIN')" to="/admin/users"> 會員管理 </RouterLink>

        <RouterLink v-if="authStore.hasModules('RBAC')" to="/admin/roles"> 角色權限 </RouterLink>

        <RouterLink v-if="authStore.hasModules('RBAC')" to="/admin/audit-logs">
          操作日誌
        </RouterLink>

        <RouterLink v-if="authStore.hasModules('ADMIN')" to="/admin/login-attempts">
          登入紀錄
        </RouterLink>

        <RouterLink v-if="authStore.hasModules('ADMIN')" to="/admin/sessions">
          Session 紀錄
        </RouterLink>
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
  min-width: 240px;
  flex: 0 0 240px;
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
