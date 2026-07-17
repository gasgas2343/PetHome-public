<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { userAuthStore } from '@/stores/auth'
import { UserRound, PawPrint, ShieldCheck, LogOut } from 'lucide-vue-next'
import { logoutApi } from '@/api/auth'
import Swal from 'sweetalert2'

defineProps({
  open: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['toggle', 'close'])

const router = useRouter()
const authStore = userAuthStore()

const avatarUrl = computed(() => {
  return authStore.avatarUrl || '/images/default-avatar.png'
})

const displayName = computed(() => {
  return authStore.name || '毛孩家長'
})

const email = computed(() => {
  return authStore.email || ''
})

function goProfile() {
  console.log('點到我的資料')
  emit('close')
  router.push('/member/profile')
}

function goPets() {
  emit('close')
  router.push('/member/pets')
}

function goSecurity() {
  emit('close')
  router.push('/member/security')
}

async function logout() {
  const result = await Swal.fire({
    icon: 'question',
    title: '確認登出？',
    text: '登出後需要重新登入才能使用會員功能。',
    showCancelButton: true,
    confirmButtonText: '登出',
    cancelButtonText: '取消',
    reverseButtons: true,
    background: '#fffaf2',
    customClass: {
      popup: 'maokilai-swal-popup',
      title: 'maokilai-swal-title',
      htmlContainer: 'maokilai-swal-text',
      confirmButton: 'maokilai-swal-confirm danger',
      cancelButton: 'maokilai-swal-cancel',
      actions: 'maokilai-swal-actions',
    },
  })
  if (!result.isConfirmed) {
    return
  }

  Swal.fire({
    title: '登出中',
    text: '正在結束目前登入狀態...',
    allowOutsideClick: false,
    allowEscapeKey: false,
    showConfirmButton: false,
    background: '#fffaf2',
    customClass: {
      popup: 'maokilai-swal-popup',
      title: 'maokilai-swal-title',
      htmlContainer: 'maokilai-swal-text',
      loader: 'maokilai-swal-loader',
    },
    didOpen: () => {
      Swal.showLoading()
    },
  })
  try {
    const refreshToken = localStorage.getItem('refreshToken')

    if (refreshToken) {
      await logoutApi({ refreshToken })
    }

    Swal.close()
  } catch (error) {
    console.error('登出 API 失敗', error)
    console.error('後端錯誤內容：', error.response?.data)
    Swal.close()
  } finally {
    emit('close')
    authStore.clearStore()
    router.push('/')
  }
}
</script>

<template>
  <div class="account-menu-wrapper">
    <button class="avatar-btn" type="button" @click.stop="emit('toggle')">
      <img :src="avatarUrl" alt="使用者頭像" class="avatar-img" />
    </button>

    <div v-if="open" class="account-panel">
      <div class="account-header">
        <img :src="avatarUrl" alt="使用者頭像" class="account-avatar" />

        <div class="account-info">
          <p class="account-name">{{ displayName }}</p>
          <p class="account-email">{{ email }}</p>
        </div>
      </div>

      <div class="account-menu-list">
        <button type="button" @click="goProfile">
          <UserRound class="menu-icon" :size="20" :stroke-width="2.2" />
          <span>我的資料</span>
        </button>

        <button type="button" @click="goPets">
          <PawPrint class="menu-icon" :size="20" :stroke-width="2.2" />
          <span>我的寵物</span>
        </button>

        <button type="button" @click="goSecurity">
          <ShieldCheck class="menu-icon" :size="20" :stroke-width="2.2" />
          <span>帳號與安全</span>
        </button>
      </div>

      <div class="account-footer">
        <button type="button" class="logout-btn" @click="logout">
          <LogOut class="menu-icon" :size="20" :stroke-width="2.2" />
          <span>登出</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.account-menu-wrapper {
  position: relative;
}

.avatar-btn {
  width: 50px;
  height: 50px;
  padding: 3px;
  border: 1px solid rgba(184, 132, 76, 0.28);
  border-radius: 50%;
  transform: translateY(0px);
  background: #fff6e8;
  cursor: pointer;
  overflow: hidden;
  box-shadow: 0 6px 16px rgba(90, 58, 32, 0.1);
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease,
    border-color 0.18s ease,
    background 0.18s ease;

  &:hover {
    transform: translateY(-2px);
    border-color: rgba(184, 132, 76, 0.65);
    background: #fff0d9;
    box-shadow: 0 10px 22px rgba(90, 58, 32, 0.16);
  }

  &:active {
    transform: translateY(0) scale(0.96);
    box-shadow: 0 5px 12px rgba(90, 58, 32, 0.14);
  }
}

.avatar-img {
  width: 100%;
  height: 100%;
  display: block;
  border-radius: 50%;
  object-fit: contain;
  object-position: center;
  background: #f0dec4;
  transform: none;
  transform: scale(1.6) translateX(-4.5px);
}

.menu-icon {
  width: 22px;
  height: 22px;
  flex-shrink: 0;
  color: #b9854f;
}

.account-panel {
  position: absolute;
  top: 56px;
  right: 0;
  width: 300px;
  padding: 18px;
  border-radius: 24px;
  background: rgba(255, 250, 242, 0.98);
  border: 1px solid rgba(132, 92, 52, 0.16);
  box-shadow: 0 18px 42px rgba(68, 45, 25, 0.18);
  z-index: 200;
}

.account-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(132, 92, 52, 0.14);
}

.account-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  object-fit: cover;
  background: #f0dec4;
  object-position: 80% center;
}

.account-info {
  min-width: 0;
}

.account-name {
  margin: 0;
  color: #4f3928;
  font-size: 16px;
  font-weight: 900;
}

.account-email {
  margin: 5px 0 0;
  color: #9a7a5b;
  font-size: 13px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.account-menu-list {
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding: 14px 0;
}

.account-menu-list button,
.account-footer button {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 15px;
  background: transparent;
  color: #5f4a38;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 800;
  text-align: left;
  cursor: pointer;

  &:hover {
    background: #f2dfc5;
  }
}

.account-footer {
  padding-top: 12px;
  border-top: 1px solid rgba(132, 92, 52, 0.14);
}

.logout-btn {
  color: #c7624c !important;

  &:hover {
    background: rgba(199, 98, 76, 0.1) !important;
  }
}
</style>
