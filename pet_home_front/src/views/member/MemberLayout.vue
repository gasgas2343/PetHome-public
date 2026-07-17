<script setup>
import { computed, onMounted } from 'vue'
import { RouterLink, RouterView } from 'vue-router'
import { UserRound, PawPrint, ShieldCheck } from 'lucide-vue-next'
import { userAuthStore } from '@/stores/auth'
import { getMeApi } from '@/api/user'

const authStore = userAuthStore()

const displayName = computed(() => {
  return authStore.name || '毛孩家長'
})

const email = computed(() => {
  return authStore.email || ''
})

async function fetchCurrentUser() {
  try {
    const response = await getMeApi()
    authStore.setUser(response.data.data)
  } catch (error) {
    console.error('取得登入者資料失敗', error)
  }
}

onMounted(() => {
  if (authStore.accessToken) {
    fetchCurrentUser()
  }
})
</script>

<template>
  <main class="member-page">
    <section class="member-shell">
      <aside class="member-sidebar">
        <div class="member-user-card">
          <div class="avatar-wrap">
            <img
              :src="authStore.avatarUrl || '/images/default-avatar.png'"
              alt="會員頭像"
              class="member-avatar"
            />
          </div>

          <div class="member-user-info">
            <p class="member-name">{{ displayName }}</p>
            <p class="member-email">{{ email }}</p>
          </div>
        </div>

        <nav class="member-nav">
          <RouterLink to="/member/profile" class="member-nav-link">
            <UserRound class="nav-icon" :size="20" :stroke-width="2.2" />
            <span>我的資料</span>
          </RouterLink>

          <RouterLink to="/member/pets" class="member-nav-link">
            <PawPrint class="nav-icon" :size="20" :stroke-width="2.2" />
            <span>我的寵物</span>
          </RouterLink>

          <RouterLink to="/member/security" class="member-nav-link">
            <ShieldCheck class="nav-icon" :size="20" :stroke-width="2.2" />
            <span>帳號與安全</span>
          </RouterLink>
        </nav>
      </aside>

      <section class="member-content">
        <RouterView />
      </section>
    </section>
  </main>
</template>

<style scoped lang="scss">
.member-page {
  min-height: calc(100vh - 72px);
  padding: 42px 56px 64px;
  background:
    radial-gradient(circle at 10% 10%, rgba(224, 163, 84, 0.14), transparent 28%),
    radial-gradient(circle at 90% 18%, rgba(120, 167, 166, 0.12), transparent 30%), #fff8ee;
}

.member-shell {
  max-width: 1180px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 270px 1fr;
  gap: 28px;
}

.member-sidebar {
  align-self: start;
  padding: 22px;
  border-radius: 28px;
  background: rgba(255, 250, 242, 0.92);
  border: 1px solid rgba(132, 92, 52, 0.14);
  box-shadow: 0 18px 42px rgba(68, 45, 25, 0.12);
}

.member-user-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 8px 6px 20px;
  border-bottom: 1px solid rgba(132, 92, 52, 0.14);
}

.avatar-wrap {
  width: 58px;
  height: 58px;
  flex-shrink: 0;
  border-radius: 50%;
  background: #fff0d9;
  box-shadow:
    0 0 0 1px rgba(184, 132, 76, 0.2),
    0 8px 18px rgba(90, 58, 32, 0.1);
  overflow: hidden;
}

.member-avatar {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: contain;
  transform: scale(1.25) translateX(2px);
}

.member-user-info {
  min-width: 0;
}

.member-name {
  margin: 0;
  color: #4f3928;
  font-size: 17px;
  font-weight: 900;
}

.member-email {
  margin: 5px 0 0;
  color: #9a7a5b;
  font-size: 13px;
  font-weight: 600;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.member-nav {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-top: 18px;
}

.member-nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 14px;
  border-radius: 16px;
  color: #5f4a38;
  font-size: 15px;
  font-weight: 850;
  text-decoration: none;
  transition:
    background 0.18s ease,
    color 0.18s ease,
    transform 0.18s ease;

  &:hover {
    background: #f2dfc5;
    transform: translateX(2px);
  }

  &.router-link-active {
    background: #e9c99f;
    color: #4f3928;
  }
}

.nav-icon {
  flex-shrink: 0;
  color: #b9854f;
}

.member-content {
  min-width: 0;
  padding: 32px;
  border-radius: 32px;
  background: rgba(255, 250, 242, 0.96);
  border: 1px solid rgba(132, 92, 52, 0.14);
  box-shadow: 0 18px 42px rgba(68, 45, 25, 0.12);
}

@media (max-width: 900px) {
  .member-page {
    padding: 28px 18px 48px;
  }

  .member-shell {
    grid-template-columns: 1fr;
  }

  .member-sidebar {
    padding: 18px;
  }

  .member-nav {
    flex-direction: row;
    overflow-x: auto;
  }

  .member-nav-link {
    flex-shrink: 0;
  }

  .member-content {
    padding: 24px 18px;
  }
}
</style>
