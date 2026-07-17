<template>

  <div class="wash-manager">

    <!-- Header -->
    <div class="page-header">
      <div>
        <h1>服務項目與價格管理</h1>
        <div class="subtitle-en">SERVICE ITEMS &amp; PRICE MANAGEMENT</div>
        <p class="subtitle">基礎服務為每20分鐘/套裝服務為每12分鐘共五組，時間1小時</p>
      </div>
      <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#serviceModal" @click="openAddService">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right: 4px;"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg> 新增服務
      </button>
    </div>

    <!-- Stats -->
    <div class="stats-grid">
      <div class="stat-card">
        <span class="stat-label">全部服務</span>
        <span class="stat-value">{{ services.length }}</span>
      </div>
      <div class="stat-card">
        <span class="stat-label">基礎服務</span>
        <span class="stat-value">{{ basicCount }}</span>
      </div>
      <div class="stat-card">
        <span class="stat-label">套裝服務</span>
        <span class="stat-value">{{ packageCount }}</span>
      </div>
      <div class="stat-card">
        <span class="stat-label">套裝子項目</span>
        <span class="stat-value">{{ items.length }}</span>
      </div>
    </div>

    <!-- Tabs -->
    <div class="tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="tab"
        :class="{ active: activeTab === tab.key }"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Tab: WashService -->
    <div v-if="activeTab === 'service'">
      <div class="table-toolbar">
        <input v-model="serviceSearch" class="search-input" placeholder="搜尋服務名稱…" />
        <select v-model="serviceFilter" class="filter-select">
          <option value="">全部類型</option>
          <option value="basic">基礎服務</option>
          <option value="package">套裝服務</option>
        </select>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th style="width:48px">#</th>
              <th>服務名稱</th>
              <th>類型</th>
              <th>原價</th>
              <th>特價</th>
              <th>狀態</th>
              <th style="width:100px">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="8" class="empty">
                <div class="japanese-spinner-loader py-4">
                  <div class="spinner"></div>
                  <div class="loading-text mt-2">資料查詢中...</div>
                </div>
              </td>
            </tr>
            <tr v-else-if="filteredServices.length === 0">
              <td colspan="8" class="empty">查無資料</td>
            </tr>
            <tr v-else v-for="(s, index) in paginatedServices" :key="s.serviceId">
              <td class="muted">{{ (serviceCurrentPage - 1) * pageSize + index + 1 }}</td>
              <td class="name-cell">{{ s.serviceName }}</td>

              <td>
                <span class="badge" :class="isPackage(s.serviceId) ? 'badge-package' : 'badge-basic'">
                  {{ isPackage(s.serviceId) ? '套裝' : '基礎' }}
                </span>
              </td>
              <td><span class="price-old">NT$ {{ s.fullPrice }}</span></td>
              <td><span class="price-new">NT$ {{ s.onsalePrice }}</span></td>
              <td>
                <span class="badge" :class="s.isActive ? 'badge-active' : 'badge-inactive'">
                  {{ s.isActive ? '上架' : '下架' }}
                </span>
              </td>
              <td>
                <div class="actions">
                  <button class="btn-icon" title="編輯" data-bs-toggle="modal" data-bs-target="#serviceModal" @click="openEditService(s)">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                  </button>
                  <button class="btn-icon btn-icon-danger" title="刪除" @click="askDelete(s.serviceId, 'service')">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"></path><path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- 分頁區塊 -->
      <nav v-if="serviceTotalPages > 1" aria-label="Page navigation" class="mt-4">
        <ul class="pagination justify-content-center">
          <li class="page-item" :class="{ disabled: serviceCurrentPage === 1 }">
            <a class="page-link" href="#" @click.prevent="serviceCurrentPage--">上一頁</a>
          </li>
          <li class="page-item" v-for="page in serviceTotalPages" :key="page" :class="{ active: serviceCurrentPage === page }">
            <a class="page-link" href="#" @click.prevent="serviceCurrentPage = page">{{ page }}</a>
          </li>
          <li class="page-item" :class="{ disabled: serviceCurrentPage === serviceTotalPages }">
            <a class="page-link" href="#" @click.prevent="serviceCurrentPage++">下一頁</a>
          </li>
        </ul>
      </nav>
    </div>

    <!-- Tab: WashPackageServiceItem -->
    <div v-if="activeTab === 'items'">
      <div class="table-toolbar">
        <input v-model="itemSearch" class="search-input" placeholder="搜尋子項目名稱…" />
        <select v-model="itemFilterSid" class="filter-select">
          <option value="">全部套裝</option>
          <option v-for="s in packageServices" :key="s.serviceId" :value="s.serviceId">
            {{ s.serviceName || s.service_name }}
          </option>
        </select>
        <select v-model="itemFilterType" class="filter-select">
          <option value="">全部 type</option>
          <option value="BASIC">BASIC</option>
          <option value="PACKAGE">PACKAGE</option>
        </select>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#itemModal" @click="openAddItem">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right: 4px;"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg> 新增子項目
        </button>
        <button v-if="itemFilterSid !== ''" class="btn btn-danger" @click="handleBatchDelete" :disabled="selectedItemIds.length === 0" style="margin-left: 8px;">
          批次刪除
        </button>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th v-if="itemFilterSid !== ''" style="width:40px"><input type="checkbox" @change="toggleSelectAllItems" :checked="isAllItemsSelected" /></th>
              <th style="width:48px">#</th>
              <th>所屬服務</th>
              <th>服務類型</th>
              <th>子項目名稱</th>
              <th>時長 (分)</th>
              <th>數量</th>
              <th>建立時間</th>
              <th style="width:100px">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td :colspan="itemFilterSid !== '' ? 9 : 8" class="empty">
                <div class="japanese-spinner-loader py-4">
                  <div class="spinner"></div>
                  <div class="loading-text mt-2">資料查詢中...</div>
                </div>
              </td>
            </tr>
            <tr v-else-if="filteredItems.length === 0">
              <td :colspan="itemFilterSid !== '' ? 9 : 8" class="empty">查無資料</td>
            </tr>
            <tr v-else v-for="(it, index) in paginatedItems" :key="it.packageServiceItemId">
              <td v-if="itemFilterSid !== ''"><input type="checkbox" :value="it.packageServiceItemId" v-model="selectedItemIds" /></td>
              <td class="muted">{{ (itemCurrentPage - 1) * pageSize + index + 1 }}</td>
              <td class="name-cell">{{ it.service?.serviceName || it.service?.service_name || serviceName(it.serviceId || it.service?.serviceId) }}</td>

              <td>
                <span class="badge" :class="it.typeCode === 'BASIC' ? 'badge-basic' : 'badge-package'">
                  {{ it.typeCode }}
                </span>
              </td>
              <td>{{ it.typeName }}</td>
              <td>{{ it.periodMinutes ?? '—' }}</td>
              <td>{{ it.quantity }}</td>
              <td class="muted">{{ it.createdAt }}</td>
              <td>
                <div class="actions">
                  <button class="btn-icon" title="編輯" data-bs-toggle="modal" data-bs-target="#itemModal" @click="openEditItem(it)">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                  </button>
                  <button class="btn-icon btn-icon-danger" title="刪除" @click="askDelete(it.packageServiceItemId, 'item')">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"></path><path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分頁區塊 -->
      <nav v-if="itemTotalPages > 1" aria-label="Page navigation" class="mt-4">
        <ul class="pagination justify-content-center">
          <li class="page-item" :class="{ disabled: itemCurrentPage === 1 }">
            <a class="page-link" href="#" @click.prevent="itemCurrentPage--">上一頁</a>
          </li>
          <li class="page-item" v-for="page in itemTotalPages" :key="page" :class="{ active: itemCurrentPage === page }">
            <a class="page-link" href="#" @click.prevent="itemCurrentPage = page">{{ page }}</a>
          </li>
          <li class="page-item" :class="{ disabled: itemCurrentPage === itemTotalPages }">
            <a class="page-link" href="#" @click.prevent="itemCurrentPage++">下一頁</a>
          </li>
        </ul>
      </nav>
    </div>

    <!-- Modal: WashService -->
    <div class="modal fade" id="serviceModal" tabindex="-1" aria-labelledby="serviceModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="serviceModalLabel">{{ serviceModal.isEdit ? '編輯服務' : '新增服務' }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label" style="color: #8C6A4F; font-weight: 600;">服務名稱 <span class="text-danger">*</span></label>
              <input class="form-control" v-model="serviceForm.serviceName" placeholder="例：精緻剪甲" />
            </div>
            <div class="row g-3 mb-3">
              <div class="col-md-6">
                <label class="form-label" style="color: #8C6A4F; font-weight: 600;">原價 (NT$) <span class="text-danger">*</span></label>
                <input class="form-control" v-model.number="serviceForm.fullPrice" type="number" placeholder="300" />
              </div>
              <div class="col-md-6">
                <label class="form-label" style="color: #8C6A4F; font-weight: 600;">特價 (NT$)</label>
                <input class="form-control" v-model.number="serviceForm.onsalePrice" type="number" placeholder="250" />
              </div>
             
            </div>
            <div class="mb-3">
              <label class="form-label" style="color: #8C6A4F; font-weight: 600;">狀態</label>
              <select class="form-select" v-model="serviceForm.isActive">
                <option :value="true">上架</option>
                <option :value="false">下架</option>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn" data-bs-dismiss="modal">取消</button>
            <button type="button" class="btn btn-primary" @click="saveService">
              {{ serviceModal.isEdit ? '儲存變更' : '新增服務' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal: WashPackageServiceItem -->
    <div class="modal fade" id="itemModal" tabindex="-1" aria-labelledby="itemModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="itemModalLabel">{{ itemModal.isEdit ? '編輯子項目' : '新增子項目' }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label" style="color: #8C6A4F; font-weight: 600;">所屬服務 <span class="text-danger">*</span></label>
              <select class="form-select" v-model.number="itemForm.serviceId">
                <option :value="null" disabled>請選擇所屬服務</option>
                <option v-for="s in services" :key="s.serviceId" :value="s.serviceId">
                  {{ s.serviceName || s.service_name }}
                </option>
              </select>
            </div>
            <div class="row g-3 mb-3">
              <div class="col-md-6">
                <label class="form-label" style="color: #8C6A4F; font-weight: 600;">服務類型 <span class="text-danger">*</span></label>
                <select class="form-select" v-model="itemForm.typeCode" @change="handleTypeCodeChange">
                  <option value="BASIC">BASIC</option>
                  <option value="PACKAGE">PACKAGE</option>
                </select>
              </div>
              <div class="col-md-6">
                <label class="form-label" style="color: #8C6A4F; font-weight: 600;">數量</label>
                <input class="form-control" v-model.number="itemForm.quantity" type="number" min="1" />
              </div>
            </div>
            
            <template v-if="!itemModal.isEdit && itemForm.typeCode === 'PACKAGE'">
              <div class="mb-3">
                <label class="form-label" style="color: #8C6A4F; font-weight: 600;">五組子項目名稱與時長 (加起來須為1小時) <span class="text-danger">*</span></label>
                <div class="d-flex flex-column gap-2">
                  <div v-for="(name, index) in itemForm.packageTypeNames" :key="index" class="row g-2">
                    <div class="col-8">
                      <input class="form-control" v-model="itemForm.packageTypeNames[index]" :placeholder="'第 ' + (index + 1) + ' 組名稱（例：溫和清耳）'" />
                    </div>
                    <div class="col-4">
                      <input class="form-control" type="number" v-model.number="itemForm.packagePeriodMinutes[index]" placeholder="分鐘" />
                    </div>
                  </div>
                </div>
              </div>
            </template>
            <template v-else>
              <div class="mb-3">
                <label class="form-label" style="color: #8C6A4F; font-weight: 600;">子項目名稱 (type_name) <span class="text-danger">*</span></label>
                <input class="form-control" v-model="itemForm.typeName" placeholder="例：溫和清耳" />
              </div>
            </template>

            <div class="mb-3" v-if="itemModal.isEdit || itemForm.typeCode !== 'PACKAGE'">
              <label class="form-label" style="color: #8C6A4F; font-weight: 600;">時長 (period_minutes)</label>
              <input class="form-control" v-model.number="itemForm.periodMinutes" type="number" :readonly="itemForm.typeCode === 'BASIC'" :placeholder="itemForm.typeCode === 'BASIC' ? '20' : '12'" />
              <div v-if="itemForm.typeCode === 'BASIC'" class="form-text text-muted" style="font-size: 0.85rem;">BASIC 服務時長固定為 20 分鐘</div>
            </div>

          </div>
          <div class="modal-footer">
            <button type="button" class="btn" data-bs-dismiss="modal">取消</button>
            <button type="button" class="btn btn-primary" @click="saveItem">
              {{ itemModal.isEdit ? '儲存變更' : '新增子項目' }}
            </button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axiosapi from '@/plugins/axios.js'
import { useUserStore } from '@/stores/user.js'
import Swal from 'sweetalert2'

const userStore = useUserStore()
const loading = ref(false)

// ─── Tabs ────────────────────────────────────────────────────────────────────
const tabs = [
  { key: 'service', label: '服務項目' },
  { key: 'items',   label: '套裝子項目' },
]
const activeTab = ref('service')

// ─── Seed Data ───────────────────────────────────────────────────────────────
const services = ref([])
const items = ref([])



// 每頁顯示筆數
const pageSize = 10

// ─── Computed ─────────────────────────────────────────────────────────────────
const isPackage = (sid) => {
  const relItems = items.value.filter(it => Number(it.serviceId || it.service?.serviceId) === Number(sid))
  if (relItems.length > 0) return relItems.some(it => it.typeCode === 'PACKAGE')
  return false
}
const serviceName  = (sid) => {
  if (!sid) return '—'
  const found = services.value.find(s => Number(s.serviceId) === Number(sid))
  return found?.serviceName || found?.service_name || '—'
}
const basicCount   = computed(() => services.value.filter(s => !isPackage(s.serviceId)).length)
const packageCount = computed(() => services.value.filter(s => isPackage(s.serviceId)).length)
const packageServices = computed(() => services.value.filter(s => isPackage(s.serviceId)))

// Filters — service tab
const serviceSearch = ref('')
const serviceFilter = ref('')
const serviceCurrentPage = ref(1)
const filteredServices = computed(() => {
  return services.value.filter(s => {
    const matchSearch = (s.serviceName || s.service_name || '').includes(serviceSearch.value)
    const matchType   = serviceFilter.value === ''
      ? true
      : serviceFilter.value === 'basic'   ? !isPackage(s.serviceId)
      : serviceFilter.value === 'package' ?  isPackage(s.serviceId)
      : true
    return matchSearch && matchType
  })
})

watch([serviceSearch, serviceFilter], () => { serviceCurrentPage.value = 1 })

const serviceTotalPages = computed(() => Math.ceil(filteredServices.value.length / pageSize) || 1)
const paginatedServices = computed(() => {
  const start = (serviceCurrentPage.value - 1) * pageSize
  return filteredServices.value.slice(start, start + pageSize)
})

// Filters — items tab
const itemSearch     = ref('')
const itemFilterSid  = ref('')
const itemFilterType = ref('')
const itemCurrentPage = ref(1)
const filteredItems  = computed(() => {
  return items.value.filter(it => {
    const matchName = (it.typeName || '').includes(itemSearch.value)
    const itSid = it.serviceId || it.service?.serviceId
    const matchSid  = itemFilterSid.value  === '' ? true : Number(itSid) === Number(itemFilterSid.value)
    const matchType = itemFilterType.value === '' ? true : it.typeCode  === itemFilterType.value
    return matchName && matchSid && matchType
  })
})

watch([itemSearch, itemFilterSid, itemFilterType], () => { itemCurrentPage.value = 1 })

const itemTotalPages = computed(() => Math.ceil(filteredItems.value.length / pageSize) || 1)
const paginatedItems = computed(() => {
  const start = (itemCurrentPage.value - 1) * pageSize
  return filteredItems.value.slice(start, start + pageSize)
})

const selectedItemIds = ref([])

watch(itemFilterSid, () => {
  selectedItemIds.value = []
})

const isAllItemsSelected = computed(() => {
  if (filteredItems.value.length === 0) return false
  return selectedItemIds.value.length === filteredItems.value.length
})

const toggleSelectAllItems = (e) => {
  if (e.target.checked) {
    selectedItemIds.value = filteredItems.value.map(it => it.packageServiceItemId)
  } else {
    selectedItemIds.value = []
  }
}

async function handleBatchDelete() {
  if (selectedItemIds.value.length === 0) return

  const result = await Swal.fire({
    title: '確定要批次刪除嗎？',
    text: `將會刪除已選擇的 ${selectedItemIds.value.length} 筆子項目，此動作無法復原。`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '確定刪除',
    cancelButtonText: '取消'
  })

  if (result.isConfirmed) {
    try {
      const res = await axiosapi.delete('/ajax/pages/WashPackageServiceItems/batch', {
        data: selectedItemIds.value,
        headers: { "Authorization": `Bearer ${userStore.token}` }
      })
      const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
      if (data && data.success === false) {
        Swal.fire('錯誤', data.message || '刪除失敗', 'error')
        return
      }
      Swal.fire('刪除成功', '所選子項目已刪除', 'success')
      selectedItemIds.value = []
      await loadItems()
    } catch (error) {
      console.error(error)
      const errorMsg = error.response?.data?.message || '刪除失敗，請稍後再試';
      Swal.fire('錯誤', errorMsg, 'error')
    }
  }
}

// ─── Service Modal ────────────────────────────────────────────────────────────
const serviceModal = ref({ show: false, isEdit: false })
const serviceFormDefault = () => ({
  serviceId: null, serviceName: '', fullPrice: null, onsalePrice: null,
  isActive: true,
})
const serviceForm = ref(serviceFormDefault())

function openAddService() {
  serviceForm.value = serviceFormDefault()
  serviceModal.value = { show: true, isEdit: false }
}
function openEditService(s) {
  serviceForm.value = { ...s }
  serviceModal.value = { show: true, isEdit: true }
}
async function saveService() {
  if (!serviceForm.value.serviceName?.trim()) { Swal.fire('提示', '請輸入服務名稱', 'warning'); return }
  if (!serviceForm.value.fullPrice)           { Swal.fire('提示', '請輸入原價', 'warning'); return }
  
  // 前端重複名稱防重檢核
  const trimmed = serviceForm.value.serviceName?.trim();
  const isDuplicate = services.value.some(s => s.serviceName === trimmed && Number(s.serviceId) !== Number(serviceForm.value.serviceId));
  if (isDuplicate) {
    Swal.fire('提示', '服務名稱不能重複', 'warning');
    return;
  }

  // 依照後端需要的 JSON 格式組合 Payload，避免送出多餘的欄位
  const payload = {
    serviceName: serviceForm.value.serviceName,
    fullPrice: serviceForm.value.fullPrice,
    onsalePrice: serviceForm.value.onsalePrice || null,
    isActive: serviceForm.value.isActive,
  }

  if (serviceModal.value.isEdit) {
    const result = await Swal.fire({
      title: '確定要修改嗎？',
      html: `即將修改 <b>${serviceForm.value.serviceName}</b> <br>確定要儲存變更嗎？`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '確定修改',
      cancelButtonText: '取消'
    });
    if (!result.isConfirmed) return;
  }

  try {
    let data;
    if (serviceModal.value.isEdit) {
      const id = serviceForm.value.serviceId;
      const res = await axiosapi.put(`/ajax/pages/WashServices/${id}`, payload, {
        headers: { "Authorization": `Bearer ${userStore.token}` }
      })
      data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
      if (data && data.success === false) {
        Swal.fire('錯誤', data.message || '修改失敗', 'error');
        return;
      }
      Swal.fire('成功', '服務已更新', 'success')
    } else {
      const res = await axiosapi.post('/ajax/pages/WashServices', payload, {
        headers: { "Authorization": `Bearer ${userStore.token}` }
      })
      data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
      if (data && data.success === false) {
        Swal.fire('錯誤', data.message || '新增失敗', 'error');
        return;
      }
      Swal.fire('成功', '服務已新增', 'success')
    }
    document.querySelector('#serviceModal .btn-close')?.click()
    await loadServices()
  } catch (error) {
    console.error(error)
    Swal.fire('錯誤', '儲存失敗，請稍後再試', 'error')
  }
}

// ─── Item Modal ───────────────────────────────────────────────────────────────
const itemModal = ref({ show: false, isEdit: false })
const itemFormDefault = () => ({
  packageServiceItemId: null, serviceId: null, typeCode: 'PACKAGE',
  typeName: '', packageTypeNames: ['', '', '', '', ''],
  packagePeriodMinutes: [12, 12, 12, 12, 12], periodMinutes: 12, quantity: 1
})
const itemForm = ref(itemFormDefault())

// 新增：處理服務類型切換時的預設時長
const handleTypeCodeChange = () => {
  if (itemForm.value.typeCode === 'BASIC') {
    itemForm.value.periodMinutes = 20
  } else if (itemForm.value.typeCode === 'PACKAGE') {
    itemForm.value.periodMinutes = 12
  }
}

function openAddItem() {
  itemForm.value = itemFormDefault()
  itemModal.value = { show: true, isEdit: false }
}
function openEditItem(it) {
  itemForm.value = { 
    ...it,
    serviceId: it.serviceId || it.service?.serviceId,
    packageTypeNames: ['', '', '', '', ''], // 避免報錯
    packagePeriodMinutes: [12, 12, 12, 12, 12]
  }
  itemModal.value = { show: true, isEdit: true }
}
async function saveItem() {
  if (!itemForm.value.serviceId) { Swal.fire('提示', '請選擇所屬服務', 'warning'); return }
  
  const isBatchPackage = !itemModal.value.isEdit && itemForm.value.typeCode === 'PACKAGE';

  if (isBatchPackage) {
    // 檢查五組名稱是否填寫
    for (let i = 0; i < 5; i++) {
      const name = itemForm.value.packageTypeNames[i]?.trim();
      if (!name) {
        Swal.fire('提示', `請輸入第 ${i + 1} 組子項目名稱`, 'warning');
        return;
      }
    }
    
    // 檢查時長並加總
    let totalMinutes = 0;
    for (let i = 0; i < 5; i++) {
      const min = Number(itemForm.value.packagePeriodMinutes[i]);
      if (isNaN(min) || min <= 0) {
        Swal.fire('提示', `第 ${i + 1} 組時長必須大於 0 分鐘`, 'warning');
        return;
      }
      totalMinutes += min;
    }
    
    if (totalMinutes !== 60) {
      Swal.fire('提示', '需要加起來1小時', 'warning');
      return;
    }
  } else {
    if (!itemForm.value.typeName?.trim()) { Swal.fire('提示', '請輸入子項目名稱', 'warning'); return }
  }
  
  if (itemModal.value.isEdit) {
    const result = await Swal.fire({
      title: '確定要修改嗎？',
      html: `即將修改 <b>${itemForm.value.typeName}</b> <br>確定要儲存變更嗎？`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '確定修改',
      cancelButtonText: '取消'
    });
    if (!result.isConfirmed) return;
  }

  try {
    if (itemModal.value.isEdit) {
      const payload = {
        packageServiceItemId: itemForm.value.packageServiceItemId,
        service: { serviceId: Number(itemForm.value.serviceId) },
        typeCode: itemForm.value.typeCode,
        typeName: itemForm.value.typeName,
        periodMinutes: itemForm.value.typeCode === 'BASIC' ? 20 : (itemForm.value.periodMinutes || null),
        quantity: itemForm.value.quantity || 1
      }
      const id = itemForm.value.packageServiceItemId
      const res = await axiosapi.put(`/ajax/pages/WashPackageServiceItems/${id}`, payload, {
        headers: { "Authorization": `Bearer ${userStore.token}` }
      })
      const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
      if (data && data.success === false) {
        Swal.fire('錯誤', data.message || '修改失敗', 'error')
        return
      }
      Swal.fire('成功', '子項目已更新', 'success')
    } else {
      if (isBatchPackage) {
        // 批次發送新增
        const payloadList = [];
        for (let i = 0; i < 5; i++) {
          payloadList.push({
            service: { serviceId: Number(itemForm.value.serviceId) },
            typeCode: itemForm.value.typeCode,
            typeName: itemForm.value.packageTypeNames[i].trim(),
            periodMinutes: Number(itemForm.value.packagePeriodMinutes[i]),
            quantity: itemForm.value.quantity || 1
          });
        }
        const res = await axiosapi.post('/ajax/pages/WashPackageServiceItems/batch', payloadList, {
          headers: { "Authorization": `Bearer ${userStore.token}` }
        })
        const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
        if (data && data.success === false) {
          Swal.fire('錯誤', data.message || '新增失敗', 'error')
          return
        }
        Swal.fire('成功', '已新增 5 筆子項目', 'success')
      } else {
        // 單筆新增 (BASIC)
        const payload = {
          service: { serviceId: Number(itemForm.value.serviceId) },
          typeCode: itemForm.value.typeCode,
          typeName: itemForm.value.typeName,
          periodMinutes: itemForm.value.typeCode === 'BASIC' ? 20 : (itemForm.value.periodMinutes || null),
          quantity: itemForm.value.quantity || 1
        }
        const res = await axiosapi.post('/ajax/pages/WashPackageServiceItems', payload, { headers: { "Authorization": `Bearer ${userStore.token}` } })
        const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
        if (data && data.success === false) {
          Swal.fire('錯誤', data.message || '新增失敗', 'error')
          return
        }
        Swal.fire('成功', '子項目已新增', 'success')
      }
    }
    document.querySelector('#itemModal .btn-close')?.click()
    await loadItems()
  } catch (error) {
    console.error(error)
    const errorMsg = error.response?.data?.message || '儲存失敗，請稍後再試';
    Swal.fire('錯誤', errorMsg, 'error')
  }
}


// ─── Delete ───────────────────────────────────────────────────────────────────
async function askDelete(id, type) {
  const targetName = type === 'service' ? '服務項目' : '子項目'
  let titleText = '確定要刪除嗎？';
  let bodyText = `確定要刪除這筆${targetName}嗎？此動作無法復原。`;
  if (type === 'service') {
    const srv = services.value.find(s => Number(s.serviceId) === Number(id));
    const sName = srv ? (srv.serviceName || srv.service_name || '') : '此服務項目';
    titleText = `確定要刪除 ${sName} ？`;
    bodyText = `注意：刪除該服務將會一併刪除其所有關聯的套裝子項目。`;
  }

  const result = await Swal.fire({
    title: titleText,
    text: bodyText,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '確定刪除',
    cancelButtonText: '取消'
  })

  if (result.isConfirmed) {
    try {
      if (type === 'service') {
        await axiosapi.delete(`/ajax/pages/WashServices/${id}`, {
          headers: { "Authorization": `Bearer ${userStore.token}` }
        })
        Swal.fire('刪除成功', '服務已刪除', 'success')
        await Promise.all([loadServices(), loadItems()])
      } else {
        await axiosapi.delete(`/ajax/pages/WashPackageServiceItems/${id}`, {
          headers: { "Authorization": `Bearer ${userStore.token}` }
        })
        Swal.fire('刪除成功', '子項目已刪除', 'success')
        await loadItems()
      }
    } catch (error) {
      console.error(error)
      Swal.fire('錯誤', '刪除失敗，請稍後再試', 'error')
    }
  }
}

// ─── API Fetch ────────────────────────────────────────────────────────────────
const loadServices = async () => {
  try {
    const res = await axiosapi.post('/ajax/pages/WashServices/find', {}, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    })
    services.value = Array.isArray(res.data) ? res.data : (res.data?.list || [])
  } catch (error) {
    console.error('Error fetching services:', error)
    Swal.fire('錯誤', '載入服務失敗', 'error')
  }
}

const loadItems = async () => {
  try {
    const res = await axiosapi.post('/ajax/pages/WashPackageServiceItems/find', {}, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    })
    items.value = Array.isArray(res.data) ? res.data : (res.data?.list || [])
  } catch (error) {
    console.error('Error fetching items:', error)
    Swal.fire('錯誤', '載入子項目失敗', 'error')
  }
}

onMounted(async () => {
  loading.value = true
  await Promise.all([loadServices(), loadItems()])
  loading.value = false
})
</script>

<style scoped>
* { box-sizing: border-box; }

.wash-manager {
  font-family: 'Noto Sans TC', sans-serif;
  font-size: 15px;
  color: #4A3E3D;
  padding: 2.5rem 3rem;
  max-width: 1600px; /* 拓寬最大寬度，從 1200px 提升至 1600px */
  width: 95%;        /* 讓頁面隨視窗寬度自適應延伸 */
  margin: 2rem auto;
  background: linear-gradient(135deg, #E2E7E3 0%, #C9D2CB 100%); /* 沉穩莫蘭迪綠灰漸變 */
  border-radius: 24px;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.4), 0 12px 40px rgba(75, 110, 87, 0.05);
}

/* Header */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
}
.page-header h1 {
  font-size: 26px;
  font-weight: 800;
  margin: 0;
  background: linear-gradient(135deg, #8C6A4F 0%, #634B38 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.subtitle-en {
  font-size: 12px;
  color: #7C6049;
  letter-spacing: 3px;
  font-weight: 600;
  margin-top: 4px;
  margin-bottom: 14px;
}
.page-header h5 {
  font-size: 15px;
  color: #8C6A4F;
  margin: 0 0 6px;
  font-weight: 600;
}
.subtitle {
  font-size: 14px;
  color: #7C6049;
  letter-spacing: 1px;
  font-weight: 500;
}

/* Stats */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 2rem;
}
.stat-card {
  background: rgba(255, 255, 255, 0.95); /* 半透明白底，輕量感 */
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 1.25rem 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 6px;
  border: none;
  box-shadow: 0 10px 30px rgba(140, 106, 79, 0.03);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 35px rgba(140, 106, 79, 0.06);
}
.stat-label {
  font-size: 13px;
  color: #7C6049;
  font-weight: 600;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #B58A63; /* 質感金沙 */
}

/* Tabs */
.tabs {
  display: flex;
  gap: 8px;
  border-bottom: 1px solid rgba(140, 106, 79, 0.2);
  margin-bottom: 1.5rem;
  
  position: sticky;
  top: 120px; /* 貼齊共用 Navbar 的下方 */
  z-index: 90;
  background: rgba(226, 231, 227, 0.94); /* 配合背景色 */
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  
  /* 填滿左右 Padding */
  padding: 14px 3rem;
  margin-left: -3rem;
  margin-right: -3rem;
  margin-top: -12px;
}
.tab {
  padding: 10px 20px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  background: none;
  color: #A88262;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  font-family: inherit;
  transition: all 0.3s ease;
}
.tab:hover {
  color: #8C6A4F;
}
.tab.active {
  color: #8C6A4F;
  border-bottom-color: #8C6A4F;
  font-weight: 700;
}

/* Toolbar */
.table-toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}
.search-input, .filter-select {
  padding: 8px 14px;
  border: 1px solid #EFEAE4;
  border-radius: 12px;
  font-size: 14px;
  font-family: inherit;
  background: rgba(255, 255, 255, 0.7);
  color: #4A3E3D;
  transition: all 0.3s ease;
}
.search-input { width: 280px; }
.filter-select { cursor: pointer; }

.search-input:focus, .filter-select:focus { 
  outline: none; 
  border-color: #8C6A4F; 
  background: #FFF;
  box-shadow: 0 0 0 3px rgba(140, 106, 79, 0.12);
}

/* Table */
.table-wrap {
  border: none;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.95); /* 半透明白底，輕量感 */
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 10px 30px rgba(140, 106, 79, 0.03);
  padding: 0.5rem 1.5rem 1.5rem;
}
table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
th {
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #8C6A4F;
  padding: 14px 12px;
  border-bottom: 2px solid #EFEAE4;
}
td {
  padding: 14px 12px;
  border-bottom: 1px solid #EFEAE4;
  color: #4A3E3D;
  vertical-align: middle;
}
tr:last-child td { border-bottom: none; }
tr:hover td { background: #FAF3EB; }

/* 分頁元件 */
.pagination {
  --bs-pagination-color: #8C6A4F;
  --bs-pagination-bg: transparent;
  --bs-pagination-border-width: 0;
  --bs-pagination-hover-color: #785A41;
  --bs-pagination-hover-bg: #FAF6F0;
  --bs-pagination-active-bg: #B58A63; /* 金色作為跳色 */
  --bs-pagination-active-color: #FFF;
}
.page-link {
  border-radius: 8px;
  margin: 0 4px;
}

.muted { color: #A88262; font-size: 13px; }
.name-cell { font-weight: 600; }
.empty { text-align: center; color: #A88262; padding: 3rem; font-weight: 500; }

/* Badge */
.badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
}
.badge-basic   { background: #FAF8F5; color: #8C6A4F; }
.badge-package { background: #F0E8DF; color: #8C6A4F; } /* 金黃色跳色 */
.badge-active  { background: #E6F4F0; color: #4B6E57; } /* 綠色跳色 */
.badge-inactive{ background: #FDF6F6; color: #C26B70; } /* 紅色跳色 */

/* Price */
.price-old {
  text-decoration: line-through;
  color: #A88262;
  font-size: 13px;
}
.price-new {
  color: #B58A63;
  font-weight: 700;
  font-size: 15px;
}

/* Actions */
.actions { display: flex; gap: 8px; }
.btn-icon {
  width: 34px; height: 34px;
  display: inline-flex; align-items: center; justify-content: center;
  border: none;
  border-radius: 10px;
  background: transparent;
  cursor: pointer;
  color: #8C6A4F;
  transition: background 0.15s, color 0.15s;
}
.btn-icon:hover { background: #F0E8DF; }
.btn-icon-danger { color: #C26B70; }
.btn-icon-danger:hover { background: #FDF6F6; }

/* Buttons */
.btn {
  padding: 9px 18px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600; /* 提升清晰度 */
  font-family: inherit;
  cursor: pointer;
  border: none;
  background: #F0E8DF;
  color: #8C6A4F;
  transition: background 0.15s;
  display: inline-flex; align-items: center; gap: 6px;
  box-shadow: 0 2px 6px rgba(140, 106, 79, 0.08);
}
.btn:hover { background: #E6DAD0; color: #4A3E3D; }
.btn-primary {
  background-color: #B58A63; /* 香檳金 */
  color: white;
  box-shadow: 0 4px 10px rgba(181, 138, 99, 0.2);
}
.btn-primary:hover { background-color: #A07853; color: white; box-shadow: 0 6px 15px rgba(181, 138, 99, 0.3); }
.btn-danger {
  background-color: #C26B70;
  color: white;
  box-shadow: 0 4px 10px rgba(194, 107, 112, 0.2);
}
.btn-danger:hover { background-color: #A0484C; }

/* Modal 卡片化 */
.modal-content {
  border-radius: 24px;
  border: none;
  box-shadow: 0 10px 40px rgba(140, 106, 79, 0.12);
  background-color: #FFFFFF;
}
.modal-header {
  border-bottom: 1px solid #EFEAE4;
}
.modal-title {
  color: #8C6A4F;
  font-weight: 700;
}
/* 表單輸入框無邊框/輕量感 */
.form-control, .form-select {
  border: 1px solid #EFEAE4;
  border-radius: 12px;
  background-color: #FAF8F5;
  box-shadow: inset 0 2px 4px rgba(0,0,0,0.01);
  padding: 0.5rem 1rem;
}
.form-control:focus, .form-select:focus {
  border-color: #8C6A4F;
  background-color: #FFFFFF;
  box-shadow: 0 0 0 0.25rem rgba(140, 106, 79, 0.12);
}

/* SweetAlert2 彈出視窗設計 */
:global(.swal2-popup) {
  border-radius: 24px !important;
  box-shadow: 0 10px 40px rgba(140, 106, 79, 0.12) !important;
  background-color: #FFFFFF !important;
  color: #4A3E3D !important;
  border: none !important;
}
:global(.swal2-title) {
  color: #8C6A4F !important;
  font-weight: 700 !important;
}
:global(.swal2-actions .swal2-confirm) {
  background-color: #B58A63 !important; /* 金色確定按鈕作為主要跳色 */
  color: #FFF !important;
  border-radius: 12px !important;
  border: none !important;
  box-shadow: 0 4px 10px rgba(181, 138, 99, 0.2) !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
  padding: 0.6em 1.5em !important;
}
:global(.swal2-actions .swal2-confirm:hover) {
  background-color: #A07853 !important;
  box-shadow: 0 6px 15px rgba(181, 138, 99, 0.3) !important;
}
:global(.swal2-actions .swal2-cancel) {
  background-color: #EFEBE4 !important;
  color: #8C6A4F !important;
  border-radius: 12px !important;
  border: none !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
  padding: 0.6em 1.5em !important;
}
:global(.swal2-actions .swal2-cancel:hover) {
  background-color: #E6DAD0 !important;
}
:global(.swal2-icon.swal2-error [class^="swal2-x-mark-line"]) { background-color: #C26B70 !important; }

/* RWD 響應式設計 (手機/平板優化) */
@media (max-width: 768px) {
  .wash-manager {
    padding: 1.5rem 1rem;
    border-radius: 16px;
    margin: 1rem;
  }
  .tabs {
    padding: 10px 1rem;
    margin-left: -1rem;
    margin-right: -1rem;
    top: 76px; /* 手機版 Navbar 高度 */
  }
  .page-header {
    flex-direction: column;
    align-items: stretch;
    text-align: center;
    gap: 1rem;
  }
  .page-header h1 {
    font-size: 22px;
  }
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  .tabs {
    overflow-x: auto;
    white-space: nowrap;
    padding-bottom: 4px;
  }
  .tab {
    padding: 8px 16px;
    font-size: 14px;
  }
  .table-toolbar {
    flex-direction: column;
    align-items: stretch;
  }
  .search-input, .filter-select {
    width: 100%;
  }
  .table-wrap {
    padding: 0;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }
  table {
    min-width: 800px; /* 強制最小寬度，讓表格可滑動而不擠壓 */
  }
}

/* Loading Spinner */
.japanese-spinner-loader {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid rgba(140, 106, 79, 0.2);
  border-top: 3px solid #8C6A4F;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 8px;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
.loading-text {
  color: #8C6A4F;
  font-weight: 600;
  letter-spacing: 1px;
  animation: pulse 1s infinite alternate;
}
@keyframes pulse {
  0% { opacity: 0.5; }
  100% { opacity: 1; }
}
.py-4 { padding-top: 1.5rem; padding-bottom: 1.5rem; }
.mt-2 { margin-top: 0.5rem; }

</style>