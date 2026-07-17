<script setup>
import { reactive, ref, onMounted } from 'vue'
import { UserRound, Save, RotateCcw, CalendarDays } from 'lucide-vue-next'
import { userAuthStore } from '@/stores/auth'
import { getProfileApi, updateProfileApi } from '@/api/user'

const authStore = userAuthStore()

const loading = ref(false)
const saving = ref(false)
const successMessage = ref('')
const apiError = ref('')

const form = reactive({
  fullName: '',
  nickName: '',
  phone: '',
  birthday: '',
  avatarUrl: '',
  pointsBalance: 0,
  email: '',
  emailVerified: false,
})

const errors = reactive({
  fullName: '',
  nickName: '',
  phone: '',
  birthday: '',
})

const originalForm = reactive({
  fullName: '',
  nickName: '',
  phone: '',
  birthday: '',
  avatarUrl: '',
  pointsBalance: 0,
  email: '',
  emailVerified: false,
})

function resetErrors() {
  errors.fullName = ''
  errors.nickName = ''
  errors.phone = ''
  errors.birthday = ''
}

function setForm(data) {
  form.fullName = data.fullName || ''
  form.nickName = data.nickName || ''
  form.phone = data.phone || ''
  form.birthday = data.birthday || ''
  form.avatarUrl = data.avatarUrl || '/images/default-avatar.png'
  form.pointsBalance = data.pointsBalance ?? 0
  form.email = data.email || ''
  form.emailVerified = data.emailVerified || false

  originalForm.fullName = form.fullName
  originalForm.nickName = form.nickName
  originalForm.phone = form.phone
  originalForm.birthday = form.birthday
  originalForm.avatarUrl = form.avatarUrl
  originalForm.pointsBalance = form.pointsBalance
  originalForm.email = form.email
  originalForm.emailVerified = form.emailVerified
}

function resetForm() {
  resetErrors()
  successMessage.value = ''
  apiError.value = ''

  form.fullName = originalForm.fullName
  form.nickName = originalForm.nickName
  form.phone = originalForm.phone
  form.birthday = originalForm.birthday
  form.avatarUrl = originalForm.avatarUrl
  form.pointsBalance = originalForm.pointsBalance
  form.email = originalForm.email
  form.emailVerified = originalForm.emailVerified
}

function validateForm() {
  resetErrors()

  let result = true

  if (form.fullName && form.fullName.length > 100) {
    errors.fullName = '姓名不能超過 100 個字'
    result = false
  }

  if (!form.nickName) {
    errors.nickName = '請輸入暱稱'
    result = false
  } else if (form.nickName.length > 50) {
    errors.nickName = '暱稱不能超過 50 個字'
    result = false
  }

  if (form.phone && !/^09\d{8}$/.test(form.phone)) {
    errors.phone = '手機格式不正確，例如 0912345678'
    result = false
  }

  return result
}

async function fetchProfile() {
  loading.value = true
  apiError.value = ''
  successMessage.value = ''

  try {
    const response = await getProfileApi()
    const data = response.data.data || response.data

    setForm(data)

    authStore.updateProfile({
      nickName: data.nickName || data.nickname,
      avatarUrl: data.avatarUrl,
      emailVerified: data.emailVerified,
    })
  } catch (error) {
    apiError.value = error.response?.data?.message || '取得會員資料失敗，請稍後再試'
  } finally {
    loading.value = false
  }
}

async function submitProfile() {
  if (!validateForm()) return

  saving.value = true
  apiError.value = ''
  successMessage.value = ''

  try {
    const requestBody = {
      fullName: form.fullName || null,
      nickName: form.nickName,
      phone: form.phone || null,
      birthday: form.birthday || null,
      // avatarUrl: form.avatarUrl || null,
    }

    const response = await updateProfileApi(requestBody)
    const data = response.data.data || response.data

    setForm(data)

    authStore.updateProfile({
      nickName: data.nickName || data.nickname || form.nickName,
      avatarUrl: data.avatarUrl || form.avatarUrl,
      emailVerified: data.emailVerified,
      twoFactorEnabled: data.twoFactorEnabled,
    })

    successMessage.value = '會員資料已更新'
  } catch (error) {
    const errorData = error.response?.data

    if (errorData?.code === 'VALIDATION_FAILED' && errorData.data) {
      Object.keys(errorData.data).forEach((key) => {
        if (errors[key] !== undefined) {
          errors[key] = errorData.data[key]
        }
      })
    }

    apiError.value = errorData?.message || '更新會員資料失敗，請稍後再試'
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<template>
  <section class="profile-view">
    <div class="profile-header">
      <div>
        <p class="eyebrow">Member Profile</p>
        <h1>我的資料</h1>
        <p class="description">管理你的基本會員資訊，讓毛起來提供更完整的服務體驗。</p>
      </div>

      <div class="profile-avatar-card">
        <div class="avatar-frame">
          <img :src="form.avatarUrl || '/images/default-avatar.png'" alt="會員頭像" />
        </div>

        <div class="avatar-text">
          <p>{{ form.nickName || '毛孩家長' }}</p>
          <span>{{ form.email || '尚未取得 Email' }}</span>
        </div>
      </div>
    </div>

    <div v-if="loading" class="state-card">正在載入會員資料...</div>

    <form v-else class="profile-form" @submit.prevent="submitProfile">
      <div class="form-section">
        <div class="section-title">
          <UserRound class="section-icon" :size="22" :stroke-width="2.2" />
          <div>
            <h2>基本資料</h2>
            <p>這些資料會用於會員中心與平台服務識別。</p>
          </div>
        </div>

        <div class="form-grid">
          <label class="form-field">
            <span>暱稱 <em>*</em></span>
            <input
              v-model.trim="form.nickName"
              type="text"
              placeholder="請輸入暱稱"
              :class="{ 'is-invalid': errors.nickName }"
            />
            <small v-if="errors.nickName">{{ errors.nickName }}</small>
          </label>

          <label class="form-field">
            <span>真實姓名</span>
            <input
              v-model.trim="form.fullName"
              type="text"
              placeholder="請輸入姓名"
              :class="{ 'is-invalid': errors.fullName }"
            />
            <small v-if="errors.fullName">{{ errors.fullName }}</small>
          </label>

          <label class="form-field">
            <span>手機</span>
            <input
              v-model.trim="form.phone"
              type="tel"
              placeholder="0912345678"
              :class="{ 'is-invalid': errors.phone }"
            />
            <small v-if="errors.phone">{{ errors.phone }}</small>
          </label>

          <label class="form-field">
            <span>生日</span>
            <div class="date-input-wrap" :class="{ 'is-invalid': errors.birthday }">
              <input v-model="form.birthday" type="date" class="date-input" />
              <CalendarDays class="date-icon" :size="19" :stroke-width="2.2" />
            </div>
            <small v-if="errors.birthday">{{ errors.birthday }}</small>
          </label>

          <!-- <label class="form-field form-field-full">
            <span>頭像路徑</span>
            <input
              v-model.trim="form.avatarUrl"
              type="text"
              placeholder="/images/default-avatar.png"
            />
          </label> -->
        </div>
      </div>

      <div class="form-section readonly-section">
        <div class="readonly-item">
          <span>Email</span>
          <strong>{{ form.email || '-' }}</strong>
        </div>

        <div class="readonly-item">
          <span>Email 驗證狀態</span>
          <strong :class="form.emailVerified ? 'status-ok' : 'status-warning'">
            {{ form.emailVerified ? '已驗證' : '尚未驗證' }}
          </strong>
        </div>

        <div class="readonly-item">
          <span>目前點數</span>
          <strong>{{ form.pointsBalance }} 點</strong>
        </div>
      </div>

      <p v-if="apiError" class="api-message error-message">
        {{ apiError }}
      </p>

      <p v-if="successMessage" class="api-message success-message">
        {{ successMessage }}
      </p>

      <div class="form-actions">
        <button type="button" class="secondary-btn" @click="resetForm">
          <RotateCcw :size="18" :stroke-width="2.2" />
          還原
        </button>

        <button type="submit" class="primary-btn" :disabled="saving">
          <Save :size="18" :stroke-width="2.2" />
          {{ saving ? '儲存中...' : '儲存變更' }}
        </button>
      </div>
    </form>
  </section>
</template>

<style scoped lang="scss">
.profile-view {
  color: #4f3928;
}

.profile-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 28px;
  margin-bottom: 28px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #b9854f;
  font-size: 13px;
  font-weight: 900;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

h1 {
  margin: 0;
  font-size: 32px;
  font-weight: 950;
  letter-spacing: 0.04em;
}

.description {
  margin: 10px 0 0;
  color: #8b6d50;
  font-size: 15px;
  line-height: 1.7;
}

.profile-avatar-card {
  min-width: 260px;
  padding: 16px;
  border-radius: 24px;
  background: #fff3e2;
  border: 1px solid rgba(132, 92, 52, 0.12);
  display: flex;
  align-items: center;
  gap: 14px;
}

.avatar-frame {
  width: 62px;
  height: 62px;
  border-radius: 50%;
  background: #fff8ee;
  overflow: hidden;
  box-shadow:
    0 0 0 1px rgba(184, 132, 76, 0.2),
    0 8px 18px rgba(90, 58, 32, 0.1);

  img {
    width: 100%;
    height: 100%;
    display: block;
    object-fit: contain;
    transform: scale(1.3) translateX(2px);
  }
}

.avatar-text {
  min-width: 0;

  p {
    margin: 0;
    color: #4f3928;
    font-size: 16px;
    font-weight: 900;
  }

  span {
    display: block;
    margin-top: 5px;
    color: #9a7a5b;
    font-size: 13px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}

.state-card {
  padding: 28px;
  border-radius: 24px;
  background: #fff3e2;
  color: #8b6d50;
  font-weight: 800;
}

.date-input-wrap {
  position: relative;
  height: 46px;
  border-radius: 15px;
  border: 1px solid rgba(132, 92, 52, 0.18);
  background: #fffaf2;
  overflow: hidden;
  transition:
    border-color 0.18s ease,
    box-shadow 0.18s ease,
    background 0.18s ease;

  &:focus-within {
    border-color: rgba(185, 133, 79, 0.75);
    background: #fffdf8;
    box-shadow: 0 0 0 4px rgba(224, 163, 84, 0.14);
  }

  &.is-invalid {
    border-color: #d96b57;
    box-shadow: 0 0 0 4px rgba(217, 107, 87, 0.12);
  }
}

.date-input {
  width: 100%;
  height: 100%;
  padding: 0 44px 0 14px;
  border: none;
  background: transparent;
  color: #4f3928;
  font-size: 15px;
  font-weight: 700;
  outline: none;
  cursor: pointer;
  color-scheme: light;
}

.date-input::-webkit-calendar-picker-indicator {
  opacity: 0;
  cursor: pointer;
}

.date-icon {
  position: absolute;
  top: 50%;
  right: 14px;
  transform: translateY(-50%);
  color: #b9854f;
  pointer-events: none;
}

.profile-form {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.form-section {
  padding: 24px;
  border-radius: 26px;
  background: #fffdf8;
  border: 1px solid rgba(132, 92, 52, 0.12);
}

.section-title {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 22px;

  h2 {
    margin: 0;
    color: #4f3928;
    font-size: 20px;
    font-weight: 950;
  }

  p {
    margin: 6px 0 0;
    color: #9a7a5b;
    font-size: 14px;
  }
}

.section-icon {
  color: #b9854f;
  margin-top: 1px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;

  span {
    color: #5f4a38;
    font-size: 14px;
    font-weight: 850;
  }

  em {
    color: #d96b57;
    font-style: normal;
  }

  input {
    width: 100%;
    height: 46px;
    padding: 0 14px;
    border-radius: 15px;
    border: 1px solid rgba(132, 92, 52, 0.18);
    background: #fffaf2;
    color: #4f3928;
    font-size: 15px;
    outline: none;
    transition:
      border-color 0.18s ease,
      box-shadow 0.18s ease,
      background 0.18s ease;

    &:focus {
      border-color: rgba(185, 133, 79, 0.75);
      background: #fffdf8;
      box-shadow: 0 0 0 4px rgba(224, 163, 84, 0.14);
    }

    &.is-invalid {
      border-color: #d96b57;
      box-shadow: 0 0 0 4px rgba(217, 107, 87, 0.12);
    }
  }

  small {
    color: #c7624c;
    font-size: 13px;
    font-weight: 700;
  }
}

.form-field-full {
  grid-column: 1 / -1;
}

.readonly-section {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  background: #fff3e2;
}

.readonly-item {
  padding: 16px;
  border-radius: 18px;
  background: rgba(255, 250, 242, 0.85);

  span {
    display: block;
    margin-bottom: 7px;
    color: #9a7a5b;
    font-size: 13px;
    font-weight: 800;
  }

  strong {
    color: #4f3928;
    font-size: 15px;
    font-weight: 950;
  }
}

.status-ok {
  color: #4f8b66 !important;
}

.status-warning {
  color: #c7624c !important;
}

.api-message {
  margin: 0;
  padding: 13px 16px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 800;
}

.error-message {
  background: rgba(217, 107, 87, 0.12);
  color: #c7624c;
}

.success-message {
  background: rgba(87, 145, 105, 0.12);
  color: #4f8b66;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.primary-btn,
.secondary-btn {
  height: 44px;
  padding: 0 20px;
  border: none;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 900;
  cursor: pointer;
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease,
    background 0.18s ease;
}

.primary-btn {
  background: #e0a354;
  color: #fff;
  box-shadow: 0 10px 22px rgba(185, 124, 45, 0.22);

  &:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 14px 26px rgba(185, 124, 45, 0.28);
  }

  &:disabled {
    opacity: 0.65;
    cursor: not-allowed;
  }
}

.secondary-btn {
  background: #f2dfc5;
  color: #6b4d32;

  &:hover {
    background: #ead1ad;
    transform: translateY(-1px);
  }
}

@media (max-width: 900px) {
  .profile-header {
    flex-direction: column;
  }

  .profile-avatar-card {
    width: 100%;
    min-width: 0;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .readonly-section {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column-reverse;

    button {
      width: 100%;
      justify-content: center;
    }
  }
}
</style>
