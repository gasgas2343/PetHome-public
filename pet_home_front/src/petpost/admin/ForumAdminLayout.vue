<script setup>
const menus = [
  { title: '文章管理', path: '/forum-admin/posts' },
  { title: '留言管理', path: '/forum-admin/comments' },
  { title: '檢舉管理', path: '/forum-admin/reports' },
  { title: '申訴管理', path: '/forum-admin/appeals' },
  { title: '通知管理', path: '/forum-admin/notifications' },

]
import '@/petpost/assets/admin.css'
</script>

<template>
  <main class="petpost-scope forum-admin-page">
    <section class="forum-admin-shell">
      <aside class="forum-admin-sidebar">
        <p class="kicker">PawCare Forum</p>
        <h2>論壇後台管理</h2>

        <nav class="forum-admin-menu">
          <RouterLink
            v-for="menu in menus"
            :key="menu.path"
            :to="menu.path"
            class="forum-admin-link"
            active-class="active"
          >
            {{ menu.title }}
          </RouterLink>
        </nav>

        <div class="forum-admin-actions">
          <RouterLink to="/community" class="back-link"> 返回論壇首頁 </RouterLink>

          <RouterLink to="/" class="back-link"> 返回會員首頁 </RouterLink>
        </div>
      </aside>

      <section class="forum-admin-content">
        <RouterView />
      </section>
    </section>
  </main>
</template>

<style scoped>
.forum-admin-page {
  min-height: 100vh;
  padding: 48px 24px;
  background:
    radial-gradient(circle at 10% 0%, rgba(220, 163, 89, 0.16), transparent 30%),
    linear-gradient(180deg, #fffaf3 0%, #fff7ed 100%);
}

.forum-admin-shell {
  width: min(1180px, 100%);
  margin: 0 auto;
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr);
  gap: 24px;
}

.forum-admin-sidebar {
  min-height: 680px;
  padding: 28px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(111, 98, 88, 0.12);
  box-shadow: 0 18px 40px rgba(92, 61, 35, 0.1);
}

.kicker {
  margin: 0 0 8px;
  color: #d6a158;
  font-weight: 900;
  letter-spacing: 0.08em;
}

.forum-admin-sidebar h2 {
  margin: 0 0 24px;
  color: #3f332b;
  font-size: 26px;
  font-weight: 900;
}

.forum-admin-menu {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.forum-admin-link {
  padding: 13px 16px;
  border-radius: 16px;
  color: #5c4033;
  background: #fff7ed;
  text-decoration: none;
  font-weight: 800;
  border: 1px solid rgba(111, 98, 88, 0.1);
}

.forum-admin-link:hover,
.forum-admin-link.active {
  color: #fff;
  background: #b98252;
}

.back-link {
  display: inline-block;
  margin-top: 28px;
  color: #8b5e3c;
  font-weight: 800;
  text-decoration: none;
}

.forum-admin-content {
  min-height: 680px;
  padding: 32px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(111, 98, 88, 0.12);
  box-shadow: 0 18px 40px rgba(92, 61, 35, 0.1);
}

.forum-admin-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 28px;
}

.back-link {
  display: block;
  padding: 12px 14px;
  border-radius: 14px;
  color: #8b5e3c;
  background: #fff7ed;
  font-weight: 800;
  text-decoration: none;
  border: 1px solid rgba(111, 98, 88, 0.12);
}

.back-link:hover {
  color: #fff;
  background: #b98252;
}
@media (max-width: 768px) {
  .forum-admin-shell {
    grid-template-columns: 1fr;
  }

  .forum-admin-sidebar {
    min-height: auto;
  }

  .forum-admin-content {
    min-height: 420px;
  }
}
</style>
