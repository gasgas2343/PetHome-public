<script setup>
import { onMounted, ref } from 'vue'
import { RefreshCw } from 'lucide-vue-next'
import Swal from 'sweetalert2'
import {
  getAllRolesApi,
  getAllPermissionsApi,
  getRolePermissionsApi,
  updateRolePermissionsApi,
} from '@/api/admin'

const roles = ref([])
const permissionGroups = ref([])
const selectedRole = ref(null)
const selectedRoleId = ref(null)
const checkedPermissions = ref([])

const roleLoading = ref(false)
const permissionLoading = ref(false)
const saving = ref(false)

const errorMessage = ref('')
const successMessage = ref('')

onMounted(() => {
  loadInitialData()
})

async function loadInitialData() {
  errorMessage.value = ''
  successMessage.value = ''
  roleLoading.value = true
  permissionLoading.value = true

  try {
    const [roleRes, permissionRes] = await Promise.all([getAllRolesApi(), getAllPermissionsApi()])

    roles.value = roleRes.data.data || []
    permissionGroups.value = permissionRes.data.data || []

    if (roles.value.length > 0) {
      await selectRole(roles.value[0])
    } else {
      selectedRole.value = null
      selectedRoleId.value = null
      checkedPermissions.value = []
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '資料讀取失敗，請稍後再試'
  } finally {
    roleLoading.value = false
    permissionLoading.value = false
  }
}

async function selectRole(role) {
  errorMessage.value = ''
  successMessage.value = ''

  const roleId = role.roleId || role.id

  selectedRole.value = role
  selectedRoleId.value = roleId
  permissionLoading.value = true

  try {
    const response = await getRolePermissionsApi(roleId)

    checkedPermissions.value = response.data.data?.permissions || []
  } catch (error) {
    checkedPermissions.value = []
    errorMessage.value = error.response?.data?.message || '角色權限讀取失敗'
  } finally {
    permissionLoading.value = false
  }
}

function isGroupAllChecked(group) {
  if (!group.permissions || group.permissions.length === 0) {
    return false
  }

  return group.permissions.every((permission) =>
    checkedPermissions.value.includes(permission.permissionCode),
  )
}

function toggleGroup(group) {
  const groupCodes = group.permissions.map((permission) => permission.permissionCode)

  if (isGroupAllChecked(group)) {
    checkedPermissions.value = checkedPermissions.value.filter((code) => !groupCodes.includes(code))
    return
  }

  const merged = new Set([...checkedPermissions.value, ...groupCodes])
  checkedPermissions.value = Array.from(merged)
}

async function saveRolePermissions() {
  if (!selectedRoleId.value) {
    return
  }

  errorMessage.value = ''
  successMessage.value = ''
  saving.value = true

  try {
    await updateRolePermissionsApi(selectedRoleId.value, {
      permissions: checkedPermissions.value,
    })

    await Swal.fire({
      icon: 'success',
      title: '儲存成功',
      text: '角色權限已更新',
      confirmButtonText: '確認',
      buttonsStyling: false,
      customClass: {
        popup: 'maokilai-swal-popup',
        title: 'maokilai-swal-title',
        htmlContainer: 'maokilai-swal-text',
        confirmButton: 'maokilai-swal-confirm',
      },
    })
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '角色權限更新失敗'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="role-permission-page">
    <div class="page-head">
      <div>
        <h1>角色權限管理</h1>
        <p>查看角色與調整後台功能權限。</p>
      </div>

      <button class="refresh-btn" type="button" @click="loadInitialData">
        <RefreshCw :size="16" :class="{ spinning: loading }" />
        重新整理
      </button>
    </div>

    <div class="content-grid">
      <!-- 左側：角色列表 -->
      <section class="panel role-panel">
        <div class="panel-head">
          <h2>角色列表</h2>
          <span>{{ roles.length }} 個角色</span>
        </div>

        <div v-if="roleLoading" class="empty-state">角色讀取中...</div>

        <div v-else class="role-list">
          <button
            v-for="role in roles"
            :key="role.roleId || role.id"
            type="button"
            class="role-item"
            :class="{ active: selectedRoleId === (role.roleId || role.id) }"
            @click="selectRole(role)"
          >
            <div>
              <strong>{{ role.roleName }}</strong>
              <small>{{ role.roleCode }}</small>
            </div>

            <span class="role-badge">編輯</span>
          </button>
        </div>
      </section>

      <!-- 右側：權限列表 -->
      <section class="panel permission-panel">
        <div class="panel-head">
          <div>
            <h2>權限設定</h2>
            <span v-if="selectedRole"> 目前角色：{{ selectedRole.roleName }} </span>
            <span v-else>請先選擇角色</span>
          </div>
        </div>

        <div v-if="!selectedRole" class="empty-state">請從左側選擇一個角色來編輯權限。</div>

        <div v-else-if="permissionLoading" class="empty-state">權限讀取中...</div>

        <div v-else class="permission-groups">
          <div v-for="group in permissionGroups" :key="group.moduleCode" class="permission-group">
            <div class="group-head">
              <div>
                <h3>{{ group.moduleName }}</h3>
                <p>{{ group.moduleCode }}</p>
              </div>

              <button type="button" class="group-check-btn" @click="toggleGroup(group)">
                {{ isGroupAllChecked(group) ? '取消全選' : '全選' }}
              </button>
            </div>

            <div class="permission-list">
              <label
                v-for="permission in group.permissions"
                :key="permission.permissionCode"
                class="permission-item"
              >
                <input
                  v-model="checkedPermissions"
                  type="checkbox"
                  :value="permission.permissionCode"
                />

                <span class="custom-check"></span>

                <span class="permission-text">
                  <strong>{{ permission.permissionName }}</strong>
                  <small>{{ permission.permissionCode }}</small>
                </span>
              </label>
            </div>
          </div>

          <div class="form-actions">
            <button
              class="save-btn"
              type="button"
              :disabled="!selectedRole || saving"
              @click="saveRolePermissions"
            >
              {{ saving ? '儲存中...' : '儲存權限' }}
            </button>
          </div>
        </div>

        <p v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </p>
      </section>
    </div>
  </div>
</template>

<style scoped lang="scss">
.role-permission-page {
  padding: 40px 30px 48px;
  color: #3d2516;
}

.page-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 26px;

  h1 {
    margin: 0 0 8px;
    font-size: 32px;
    font-weight: 800;
    letter-spacing: 0.04em;
    color: #3d2516;
  }

  p {
    margin: 0;
    font-size: 15px;
    color: #8a6a55;
  }
}

.refresh-btn,
.save-btn {
  border: none;
  border-radius: 999px;
  padding: 11px 22px;
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  background: #8a6542;
  cursor: pointer;
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease,
    background 0.18s ease;

  &:hover:not(:disabled) {
    transform: translateY(-1px);
    background: #7a5737;
    box-shadow: 0 8px 18px rgba(103, 70, 42, 0.18);
  }

  &:disabled {
    opacity: 0.55;
    cursor: not-allowed;
  }
}

.refresh-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.save-btn {
  min-width: 160px;
  padding: 12px 30px;
}

.content-grid {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 22px;
}

.panel {
  background: rgba(255, 250, 243, 0.9);
  border: 1px solid rgba(188, 154, 117, 0.22);
  border-radius: 18px;
  box-shadow: 0 12px 28px rgba(92, 64, 38, 0.08);
  overflow: hidden;
}

.panel-head {
  min-height: 58px;
  padding: 18px 20px;
  background: #eadcc8;
  border-bottom: 1px solid rgba(188, 154, 117, 0.25);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;

  h2 {
    margin: 0;
    font-size: 18px;
    font-weight: 800;
    color: #3d2516;
  }

  span {
    display: block;
    margin-top: 4px;
    font-size: 13px;
    color: #8a6a55;
  }
}

.role-list {
  padding: 14px;
}

.role-item {
  width: 100%;
  border: 1px solid transparent;
  border-radius: 14px;
  padding: 14px 14px;
  background: transparent;
  color: #3d2516;
  text-align: left;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition:
    background 0.18s ease,
    border-color 0.18s ease,
    transform 0.18s ease;

  & + .role-item {
    margin-top: 8px;
  }

  strong {
    display: block;
    font-size: 15px;
    font-weight: 800;
  }

  small {
    display: block;
    margin-top: 4px;
    font-size: 12px;
    color: #9a7a62;
  }

  &:hover {
    background: #f4eadc;
  }

  &.active {
    background: #f1dfc8;
    border-color: rgba(138, 101, 66, 0.28);
    transform: translateX(2px);

    .role-badge {
      color: #fff;
      background: #8a6542;
    }
  }
}

.role-badge {
  flex: 0 0 auto;
  padding: 6px 10px;
  border-radius: 999px;
  background: #eadcc8;
  color: #6c4b31;
  font-size: 12px;
  font-weight: 800;
}

.permission-panel {
  min-height: 560px;
}

.permission-groups {
  padding: 18px;
}

.permission-group {
  border: 1px solid rgba(188, 154, 117, 0.2);
  border-radius: 16px;
  background: rgba(255, 253, 249, 0.78);
  overflow: hidden;

  & + .permission-group {
    margin-top: 16px;
  }
}

.group-head {
  padding: 16px 18px;
  background: #f5eadc;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 14px;

  h3 {
    margin: 0;
    font-size: 17px;
    font-weight: 800;
    color: #3d2516;
  }

  p {
    margin: 4px 0 0;
    font-size: 12px;
    color: #9a7a62;
  }
}

.group-check-btn {
  border: none;
  border-radius: 999px;
  padding: 8px 14px;
  background: #eadcc8;
  color: #5d3f27;
  font-size: 13px;
  font-weight: 800;
  cursor: pointer;

  &:hover {
    background: #dfccb5;
  }
}

.permission-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  padding: 16px 18px 18px;
}

.permission-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 11px;
  min-height: 54px;
  padding: 12px 14px;
  border: 1px solid rgba(188, 154, 117, 0.22);
  border-radius: 14px;
  background: #fffaf3;
  cursor: pointer;
  transition:
    border-color 0.18s ease,
    background 0.18s ease,
    transform 0.18s ease;

  &:hover {
    border-color: rgba(138, 101, 66, 0.35);
    background: #fff4e6;
    transform: translateY(-1px);
  }

  input {
    position: absolute;
    opacity: 0;
    pointer-events: none;
  }

  input:checked + .custom-check {
    background: #8a6542;
    border-color: #8a6542;

    &::after {
      opacity: 1;
      transform: rotate(45deg) scale(1);
    }
  }
}

.form-actions {
  display: flex;
  justify-content: center;
  padding: 26px 0 6px;
}

.custom-check {
  position: relative;
  flex: 0 0 auto;
  width: 20px;
  height: 20px;
  border-radius: 6px;
  border: 2px solid #d6bfa2;
  background: #fff;
  transition:
    background 0.16s ease,
    border-color 0.16s ease;

  &::after {
    content: '';
    position: absolute;
    left: 6px;
    top: 2px;
    width: 5px;
    height: 10px;
    border: solid #fff;
    border-width: 0 2px 2px 0;
    opacity: 0;
    transform: rotate(45deg) scale(0.7);
    transition:
      opacity 0.16s ease,
      transform 0.16s ease;
  }
}

.permission-text {
  min-width: 0;

  strong {
    display: block;
    font-size: 14px;
    font-weight: 800;
    color: #3d2516;
  }

  small {
    display: block;
    margin-top: 3px;
    font-size: 12px;
    color: #9a7a62;
    word-break: break-all;
  }
}

.empty-state {
  padding: 42px 20px;
  text-align: center;
  color: #9a7a62;
  font-weight: 700;
}

.error-message,
.success-message {
  margin: 0 18px 18px;
  padding: 12px 14px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
}

.error-message {
  background: #fbe6de;
  color: #a33a26;
}

.success-message {
  background: #e7f3df;
  color: #4f7a2f;
}

@media (max-width: 960px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .permission-list {
    grid-template-columns: 1fr;
  }
}
</style>
