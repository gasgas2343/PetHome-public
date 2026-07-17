<template>
  <div class="wash-manager">
    <!-- Header -->
    <div class="page-header">
      <div>
        <h1>訂單與付款查詢</h1>
        <div class="subtitle-en">APPOINTMENT MANAGEMENT</div>
        <p class="subtitle">管理客戶預約與付款狀態 ({{ currentDateDisplay }})</p>
      </div>
    </div>

    <!-- Tabs -->
    <div class="tabs">
      <button class="tab" :class="{ active: currentTab === 'appt' }" @click="currentTab = 'appt'">預約清單</button>
      <button class="tab" :class="{ active: currentTab === 'order' }" @click="currentTab = 'order'">訂單</button>
    </div>

    <div class="content-wrapper">
      <!-- Sidebar Stats -->
      <aside class="sidebar-stats">
        <div class="stats-grid">
          <div 
            v-for="card in statusCards" 
            :key="card.title" 
            class="stat-card clickable"
            :class="{ active: isCardActive(card.title) }"
            @click="handleCardClick(card.title)"
            style="cursor: pointer;"
          >
            <span class="stat-label">{{ card.title }}</span>
            <span class="stat-value" :style="{ color: card.color }">{{ card.count }}</span>
          </div>
        </div>
      </aside>

      <main class="main-panel">
        <!-- Toolbar -->
        <div class="table-toolbar">
          <input 
            v-model="filters.keyword" 
            class="search-input" 
            placeholder="訂單編號 / 預約單號 / 寵物名稱..." 
            style="width: 280px;"
          />
          <input v-model="filters.startDate" type="date" class="search-input" style="width: 140px;" />
          <span style="color: #8C6A4F;">~</span>
          <input v-model="filters.endDate" type="date" class="search-input" style="width: 140px;" />
          
          <select v-model="filters.depositStatus" class="filter-select">
            <option value="">全部訂金</option>
            <option value="0">未付</option>
            <option value="1">已付</option>
            <option value="2">已退款</option>
            <option value="3">已沒收</option>
          </select>

          <div class="actions" style="margin-left: auto;">
            <button @click="resetFilters" class="btn">清除</button>
            <button @click="handleSearch" class="btn btn-primary">搜尋</button>
          </div>
        </div>

        <!-- Table -->
        <div class="table-wrap">
          <div style="padding-bottom: 12px; margin-bottom: 8px; border-bottom: 1px solid #EFEAE4; color: #8C6A4F; font-weight: 600; display: flex; justify-content: space-between; align-items: center;">
            <div>
              {{ currentTab === 'appt' ? '預約清單' : '訂單' }} <span class="muted" style="margin-left: 8px; font-weight: 400;">共 {{ filteredAppointments.length }} 筆</span>
            </div>
          </div>

          <table>
            <thead>
              <tr>
                <th style="width: 50px"></th>
                <th style="width: 60px; text-align: center;">序號</th>
                <th style="width: 210px; white-space: nowrap;">
                  <template v-if="currentTab === 'order'">
                    <span style="color: #DB9FA2; font-weight: 700;">訂單編號</span> /<br>
                    預約單號
                  </template>
                  <template v-else>
                    預約單號
                  </template>
                </th>
                <th style="width: 140px">預約日期</th>
                <th style="width: 180px">顧客與寵物</th>
                <th style="width: 100px; text-align: center;">狀態</th>
                <th style="width: 120px; text-align: center;">訂金</th>
                <th style="width: 140px; text-align: right; white-space: nowrap;">金額</th>
                <th style="width: 120px; text-align: center;">操作</th>
              </tr>
            </thead>
            <tbody>
              <template v-for="(appt, index) in paginatedAppointments" :key="appt.apptOrderId || appt.appt_order_id">
                <tr>
                  <td>
                    <button @click="toggleExpand(appt.apptOrderId || appt.appt_order_id)" class="btn-icon">
                      <span v-if="expandedRows.includes(appt.apptOrderId || appt.appt_order_id)">▼</span>
                      <span v-else>▶</span>
                    </button>
                  </td>
                  <td style="text-align: center; color: #7B5E9C; font-weight: 600;">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                  <td style="white-space: nowrap;">
                    <template v-if="currentTab === 'order'">
                      <div v-if="getOrderPayNo(appt.apptOrderId || appt.appt_order_id)" style="font-family: monospace; font-weight: 700; color: #DB9FA2;">
                        {{ getOrderPayNo(appt.apptOrderId || appt.appt_order_id) }}
                      </div>
                      <div style="font-family: monospace; font-weight: 700; color: #7B5E9C; margin-top: 4px;">{{ appt.apptNo || appt.appt_no }}</div>
                    </template>
                    <template v-else>
                      <div style="font-family: monospace; font-weight: 700; color: #7B5E9C;">{{ appt.apptNo || appt.appt_no }}</div>
                    </template>
                  </td>
                  <td>
                    <div class="name-cell">{{ formatJustDate(appt.apptDate || appt.appt_date) }}</div>
                    <div class="muted" style="margin-top: 4px;">{{ formatJustTime(appt.apptStartTime || appt.appt_start_time) }}</div>
                  </td>
                  <td>
                    <div class="name-cell">
                      顧客: <span style="font-weight: 700; color: #3A3142;">{{ getCustomerName(appt.petId || appt.pet_id) || '未知顧客' }}</span> (ID: {{ appt.memberId || appt.member_id }})
                    </div>
                    <div class="muted" style="margin-top: 4px; color: #7B5E9C;">
                      🐾 {{ getPetName(appt.petId || appt.pet_id) || '寵物' }}
                      <span v-if="getPetBreed(appt.petId || appt.pet_id)"> ({{ getPetBreed(appt.petId || appt.pet_id) }})</span>
                    </div>
                  </td>
                  <td style="text-align: center;">
                    <span class="badge" :class="getStatusBadgeClass(appt.apptStatus ?? appt.appt_status)">
                      {{ getStatusLabel(appt.apptStatus ?? appt.appt_status) }}
                    </span>
                  </td>
                  <td style="text-align: center;">
                    <span class="badge" :class="getDepositBadgeClass(appt.depositStatus ?? appt.deposit_status)">
                      {{ getDepositLabel(appt.depositStatus ?? appt.deposit_status) }}
                    </span>
                    <div class="muted" style="margin-top: 4px; font-size: 11px;">NT$ {{ appt.depositAmount ?? appt.deposit_amount }}</div>
                  </td>
                  <td style="text-align: right; white-space: nowrap;">
                    <div class="price-new" style="white-space: nowrap;">NT$ {{ appt.totalAmount ?? appt.total_amount }}</div>
                    <div class="price-old" v-if="(appt.discountAmount ?? appt.discount_amount) > 0" style="white-space: nowrap;">NT$ {{ appt.subtotalAmount ?? appt.subtotal_amount }}</div>
                    <div class="muted" v-else style="white-space: nowrap;">NT$ {{ appt.subtotalAmount ?? appt.subtotal_amount }}</div>
                  </td>
                  <td>
                    <div class="actions" style="justify-content: center;">
                      <button @click="editAppointment(appt)" class="btn-icon" title="編輯">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                      </button>
                      <button v-if="(appt.apptStatus ?? appt.appt_status) === 0" @click="confirmAppointment(appt)" class="btn-icon" style="color: #53A18C;" title="確認預約">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>
                      </button>
                      <button v-if="(appt.apptStatus ?? appt.appt_status) !== 4" @click="cancelAppointment(appt)" class="btn-icon btn-icon-danger" title="取消預約">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>
                      </button>
                    </div>
                  </td>
                </tr>

                <!-- Expanded Details Row -->
                <tr v-if="expandedRows.includes(appt.apptOrderId || appt.appt_order_id)" style="background: rgba(140, 106, 79, 0.05);">
                  <td colspan="9" style="padding: 16px 24px;">
                    <div style="background: #FFFFFF; border: 1px solid #EFEBE4; border-radius: 12px; padding: 16px; box-shadow: inset 0 2px 4px rgba(0,0,0,0.01);">
                      <h4 style="font-size: 13px; font-weight: 700; color: #8C6A4F; margin-bottom: 12px; border-bottom: 1px solid #EFEBE4; padding-bottom: 8px;">📋 預約服務項目明細</h4>
                      <table style="width: 100%; margin-bottom: 0; border-collapse: separate; border-spacing: 0;">
                        <thead>
                          <tr>
                            <th style="padding: 10px 16px; color: #8C6A4F; font-weight: 600; border-bottom: 2px solid #EFEBE4; border-top: none;">明細 ID</th>
                            <th style="padding: 10px 16px; color: #8C6A4F; font-weight: 600; border-bottom: 2px solid #EFEBE4; border-top: none;">服務項目</th>
                            <th style="padding: 10px 16px; text-align: right; color: #8C6A4F; font-weight: 600; border-bottom: 2px solid #EFEBE4; border-top: none;">原價（元）</th>
                            <th style="padding: 10px 16px; text-align: right; color: #8C6A4F; background-color: #FAF6F0; border-radius: 8px 8px 0 0; border-bottom: 2px solid #EFEBE4; border-top: none;">特價（元）</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr v-if="!(appt.details || appt.washApptDetails || appt.apptDetails)?.length">
                            <td colspan="4" style="padding: 24px; text-align: center; color: #8C6A4F; font-size: 13px; background-color: #FAF8FB; border-radius: 0 0 8px 8px; border-bottom: none;">目前尚無明細資料</td>
                          </tr>
                          <tr v-for="(detail, index) in (appt.details || appt.washApptDetails || appt.apptDetails || [])" :key="detail.apptdetailId || detail.apptdetail_id">
                            <td style="padding: 12px 16px; font-family: monospace; border-bottom: 1px dashed #EFEBE4;" class="text-muted">#{{ detail.apptdetailId || detail.apptdetail_id }}</td>
                            <td style="padding: 12px 16px; border-bottom: 1px dashed #EFEBE4;">
                              <span style="font-weight: 600; color: #3A3142;">
                                {{ (detail.packageServiceItemId || detail.package_service_item_id || detail.isPackage) ? '套餐服務' : '基礎服務' }}: {{ detail.serviceName || detail.service_name || detail.name || detail.serviceId || detail.service_id || '' }}
                              </span>
                            </td>
                            <td style="padding: 12px 16px; text-align: right; border-bottom: 1px dashed #EFEBE4;" class="text-muted">
                              <span class="currency-symbol-sm">$</span><span style="font-variant-numeric: tabular-nums;">{{ getDetailFullPrice(detail) }}</span>
                            </td>
                            <td style="padding: 12px 16px; text-align: right; background-color: #FAF6F0;" :style="index === (appt.details || appt.washApptDetails || appt.apptDetails || []).length - 1 ? 'border-radius: 0 0 8px 8px; border-bottom: none;' : 'border-bottom: 1px dashed #EFEBE4;'">
                              <div class="detail-amount"><span class="currency-symbol-sm" style="color: #B58A63;">$</span>{{ getDetailOnsalePrice(detail) }}</div>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                      <div style="display: flex; justify-content: flex-end; gap: 24px; margin-top: 12px; padding-bottom: 12px; border-bottom: 1px dashed #EFEBE4; font-size: 14px;">
                        <div>
                          <span style="font-weight: 600; color: #8C6A4F;">訂金金額：</span>
                          <span style="color: #B58A63; font-weight: 700;">NT$ {{ appt.depositAmount ?? appt.deposit_amount ?? 200 }}</span>
                        </div>
                        <div>
                          <span style="font-weight: 600; color: #8C6A4F;">尾款金額：</span>
                          <span style="color: #B58A63; font-weight: 700;">NT$ {{ appt.totalAmount ?? appt.total_amount ?? 0 }}</span>
                        </div>
                      </div>
                      <div style="margin-top: 12px; padding-top: 12px; font-size: 13px; color: #3A3142; display: flex; flex-direction: column; gap: 8px;">
                        <div><span style="font-weight: 600; color: #8C6A4F;">預約備註：</span>{{ appt.note || '無備註事項' }}</div>
                        <div><span style="font-weight: 600; color: #8C6A4F;">關聯預約訂單 ID (appt_order_id)：</span>{{ appt.apptOrderId || appt.appt_order_id }}</div>
                        <div v-if="appt.cancelReason || appt.cancel_reason" style="color: #C26B70; margin-top: 4px;">
                          <span style="font-weight: 600;">取消原因 ({{ (appt.canceledBy || appt.canceled_by) === '系統自動' ? '系統自動 ' : '由 ' + (appt.canceledBy || appt.canceled_by) }}):</span> {{ appt.cancelReason || appt.cancel_reason }} ({{ formatDateTime(appt.canceledAt || appt.canceled_at) }})
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
              </template>
              <tr v-if="paginatedAppointments.length === 0">
                <td colspan="9" class="empty">查無符合條件的資料</td>
              </tr>
            </tbody>
          </table>

          <!-- 分頁元件 -->
          <nav v-if="totalPages > 1" aria-label="Page navigation" class="mt-4">
            <ul class="pagination justify-content-center">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <a class="page-link" href="#" @click.prevent="currentPage--">上一頁</a>
              </li>
              <li 
                v-for="page in totalPages" 
                :key="page" 
                class="page-item" 
                :class="{ active: currentPage === page }"
              >
                <a class="page-link" href="#" @click.prevent="currentPage = page">{{ page }}</a>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <a class="page-link" href="#" @click.prevent="currentPage++">下一頁</a>
              </li>
            </ul>
          </nav>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import axiosapi from '@/plugins/axios.js'
import { useUserStore } from '@/stores/user.js'

const userStore = useUserStore()
const router = useRouter()

// Reactive States
const appointments = ref([])
const pets = ref([])
const expandedRows = ref([])
const currentTab = ref('appt') // 'appt' (預約清單) 或 'order' (訂單)
const currentPage = ref(1)
const pageSize = ref(10)

// Current date display (calculated dynamically)
const currentDateDisplay = computed(() => {
  const date = new Date()
  const days = ['週日', '週一', '週二', '週三', '週四', '週五', '週六']
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 ${days[date.getDay()]}`
})

// Filter Form
const filters = ref({
  keyword: '',
  startDate: '',
  endDate: '',
  depositStatus: ''
})

// Load data from DB
const loadAppointments = async () => {
  try {
    const response = await axiosapi.post('/ajax/pages/WashAppointments/find', {}, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    })
    appointments.value = Array.isArray(response.data) ? response.data : (response.data?.list || [])
  } catch (error) {
    console.error("載入預約資料失敗:", error)
    Swal.fire('錯誤', '無法取得預約資料，請稍後再試。', 'error')
  }
}

const loadPets = async () => {
  try {
    const response = await axiosapi.post('/ajax/pages/Pets/find', {}, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    })
    pets.value = Array.isArray(response.data) ? response.data : (response.data?.list || [])
  } catch (error) {
    console.warn('無法載入寵物資料', error)
  }
}

const payments = ref([])

const loadPayments = async () => {
  try {
    const response = await axiosapi.post('/ajax/pages/WashPayments/find', {}, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    })
    payments.value = Array.isArray(response.data) ? response.data : (response.data?.list || [])
  } catch (error) {
    console.warn("無法載入付款資料", error)
  }
}

const getOrderPayNo = (apptOrderId) => {
  if (!apptOrderId || !payments.value) return ''
  const apptPayments = payments.value.filter(p => Number(p.apptOrderId || p.appt_order_id) === Number(apptOrderId))
  if (apptPayments.length === 0) return ''
  apptPayments.sort((a, b) => {
    const idA = a.paymentId || a.payment_id || 0
    const idB = b.paymentId || b.payment_id || 0
    return idB - idA
  })
  const latest = apptPayments[0]
  return latest ? (latest.payNo || latest.pay_no || '') : ''
}

onMounted(() => {
  loadAppointments()
  loadPets()
  loadPayments()
  loadServices()
})

const services = ref([])
const loadServices = async () => {
  try {
    const res = await axiosapi.post('/ajax/pages/WashServices/find', {}, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    })
    services.value = Array.isArray(res.data) ? res.data : (res.data?.list || [])
  } catch (error) {
    console.warn('無法載入服務品項', error)
  }
}

const getDetailFullPrice = (detail) => {
  const sId = detail.serviceId || detail.service_id;
  if (!sId) return detail.fullPrice ?? detail.full_price ?? detail.unitPrice ?? detail.unit_price ?? 0;
  const srv = services.value.find(s => Number(s.serviceId) === Number(sId));
  return srv ? (srv.fullPrice ?? srv.full_price ?? 0) : (detail.unitPrice ?? detail.unit_price ?? 0);
};

const getDetailOnsalePrice = (detail) => {
  const sId = detail.serviceId || detail.service_id;
  if (!sId) return detail.onsalePrice ?? detail.onsale_price ?? detail.itemAmount ?? detail.item_amount ?? 0;
  const srv = services.value.find(s => Number(s.serviceId) === Number(sId));
  return srv ? (srv.onsalePrice ?? srv.onsale_price ?? srv.fullPrice ?? srv.full_price ?? 0) : (detail.itemAmount ?? detail.item_amount ?? 0);
};

// Pet Resolvers
const getPetName = (petId) => {
  if (!petId || !pets.value) return ''
  const pet = pets.value.find(p => {
    const id = p.pet_id ?? p.id ?? p.petId
    return String(id) === String(petId)
  })
  return pet ? (pet.pet_name || pet.name || pet.petName || '') : ''
}

const getPetBreed = (petId) => {
  if (!petId || !pets.value) return ''
  const pet = pets.value.find(p => {
    const id = p.pet_id ?? p.id ?? p.petId
    return String(id) === String(petId)
  })
  return pet ? (pet.pet_breed || pet.breed || pet.petBreed || '') : ''
}

const getCustomerName = (petId) => {
  if (!petId || !pets.value) return ''
  const pet = pets.value.find(p => {
    const id = p.pet_id ?? p.id ?? p.petId
    return String(id) === String(petId)
  })
  if (!pet || !pet.user) return ''
  return pet.user.nickname || pet.user.fullName || pet.user.email || ''
}

// Date & Time Formatters
const formatJustDate = (d) => {
  if (!d) return ''
  return d.split('T')[0].split(' ')[0]
}

const formatJustTime = (t) => {
  if (!t) return ''
  if (t.includes('T')) return t.split('T')[1].substring(0, 5)
  const parts = t.split(' ')
  return parts.length > 1 ? parts[1].substring(0, 5) : t.substring(0, 5)
}

const formatDateTime = (dt) => {
  if (!dt) return ''
  return dt.replace('T', ' ').substring(0, 16)
}

// Toggle Row Detail & Fetch Details
const toggleExpand = async (id) => {
  const index = expandedRows.value.indexOf(id)
  if (index > -1) {
    expandedRows.value.splice(index, 1)
  } else {
    expandedRows.value.push(id)
    const appt = appointments.value.find(a => (a.apptOrderId || a.appt_order_id) === id)
    if (appt && !appt.detailsLoaded) {
      try {
        const response = await axiosapi.post('/ajax/pages/WashApptDetails/find', { apptOrderId: id }, {
          headers: { "Authorization": `Bearer ${userStore.token}` }
        })
        appt.details = Array.isArray(response.data) ? response.data : (response.data?.list || [])
        appt.detailsLoaded = true
      } catch (error) {
        console.error(`無法載入預約單 ${id} 的明細資料:`, error)
      }
    }
  }
}

// Stats computed from DB data
const statusCards = computed(() => {
  const hasOrder = (a) => {
    const status = a.apptStatus ?? a.appt_status
    if (status === 4) return false
    const payNo = getOrderPayNo(a.apptOrderId || a.appt_order_id)
    return !!(payNo && (payNo.startsWith('ODR-') || payNo.startsWith('PAY-')))
  }

  if (currentTab.value === 'appt') {
    const appts = appointments.value.filter(a => !hasOrder(a))
    const all = appts.length
    const pending = appts.filter(a => (a.apptStatus ?? a.appt_status) === 0).length
    const cancelled = appts.filter(a => (a.apptStatus ?? a.appt_status) === 4).length
    return [
      { title: '全部預約', count: all, color: '#7B5E9C' },
      { title: '預約待確認', count: pending, color: '#DB9FA2' },
      { title: '預約已取消', count: cancelled, color: '#A087B5' }
    ]
  } else {
    const orders = appointments.value.filter(a => hasOrder(a))
    const all = orders.length
    const paid = orders.filter(a => (a.depositStatus ?? a.deposit_status) === 1).length
    const unpaid = orders.filter(a => (a.depositStatus ?? a.deposit_status) === 0).length
    const refunded = orders.filter(a => (a.depositStatus ?? a.deposit_status) === 2).length
    const confiscated = orders.filter(a => (a.depositStatus ?? a.deposit_status) === 3).length
    return [
      { title: '全部訂單', count: all, color: '#7B5E9C' },
      { title: '已付', count: paid, color: '#53A18C' },
      { title: '未收', count: unpaid, color: '#DB9FA2' },
      { title: '已退款', count: refunded, color: '#A087B5' },
      { title: '已沒收', count: confiscated, color: '#D1A153' }
    ]
  }
})

// UI Mappings
const getStatusLabel = (status) => {
  const map = { 0: '待確認', 1: '已確認', 2: '服務中', 3: '已完成', 4: '已取消', 5: '未到' }
  return map[status] || '未知'
}

const getStatusBadgeClass = (status) => {
  const map = {
    0: 'badge-package',
    1: 'badge-active',
    2: 'badge-active',
    3: 'badge-basic',
    4: 'badge-inactive',
    5: 'badge-danger'
  }
  return map[status] || 'badge-basic'
}

const getDepositLabel = (status) => {
  const map = { 0: '未付', 1: '已付', 2: '已退款', 3: '已沒收' }
  return map[status] || '未知'
}

const getDepositBadgeClass = (status) => {
  const map = {
    0: 'badge-danger',
    1: 'badge-active',
    2: 'badge-inactive',
    3: 'badge-inactive'
  }
  return map[status] || 'badge-basic'
}

const getSourceLabel = (type) => {
  const map = { 1: 'LINE', 2: '電話', 3: '臨櫃' }
  return map[type] || '其他'
}

// Table filtering logic
const filteredAppointments = computed(() => {
  let list = appointments.value.filter(appt => {
    const apptNo = appt.apptNo || appt.appt_no || ''
    const apptDate = formatJustDate(appt.apptDate || appt.appt_date || '')
    const petId = appt.petId || appt.pet_id
    const petName = getPetName(petId)

    // Tab filter
    const status = appt.apptStatus ?? appt.appt_status
    const payNo = getOrderPayNo(appt.apptOrderId || appt.appt_order_id)
    const hasOrderNo = status !== 4 && payNo && (payNo.startsWith('ODR-') || payNo.startsWith('PAY-'))

    if (currentTab.value === 'appt') {
      if (hasOrderNo) return false
      if (selectedApptStatus.value !== 'all' && status !== selectedApptStatus.value) {
        return false
      }
    } else if (currentTab.value === 'order') {
      if (!hasOrderNo) return false
    }

    // Keyword filter (no / payNo / pet name)
    if (filters.value.keyword) {
      const kw = filters.value.keyword.toLowerCase()
      const matchesApptNo = apptNo.toLowerCase().includes(kw)
      const apptPayments = payments.value.filter(p => Number(p.apptOrderId || p.appt_order_id) === Number(appt.apptOrderId || appt.appt_order_id))
      const matchesPayNo = apptPayments.some(p => (p.payNo || p.pay_no || '').toLowerCase().includes(kw))
      const matchesPetName = petName.toLowerCase().includes(kw)
      if (!matchesApptNo && !matchesPayNo && !matchesPetName) {
        return false
      }
    }

    // Deposit status filter
    const depStatus = appt.depositStatus ?? appt.deposit_status
    if (filters.value.depositStatus !== '' && depStatus !== parseInt(filters.value.depositStatus)) {
      return false
    }

    // Date range filter
    if (filters.value.startDate && apptDate < filters.value.startDate) return false
    if (filters.value.endDate && apptDate > filters.value.endDate) return false

    return true
  })

  // Sort by order number descending (latest generated order on top) when in 'order' tab
  if (currentTab.value === 'order') {
    list.sort((a, b) => {
      const payA = getOrderPayNo(a.apptOrderId || a.appt_order_id)
      const payB = getOrderPayNo(b.apptOrderId || b.appt_order_id)
      const numA = (payA || '').replace(/\D/g, '')
      const numB = (payB || '').replace(/\D/g, '')
      return numB.localeCompare(numA)
    })
  }

  return list
})

// Pagination logic
const totalPages = computed(() => {
  return Math.ceil(filteredAppointments.value.length / pageSize.value) || 1
})

const paginatedAppointments = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredAppointments.value.slice(start, end)
})

const selectedApptStatus = ref('all')

const isCardActive = (title) => {
  if (currentTab.value === 'appt') {
    if (title === '全部預約') return selectedApptStatus.value === 'all'
    if (title === '預約待確認') return selectedApptStatus.value === 0
    if (title === '預約已取消') return selectedApptStatus.value === 4
  } else {
    if (title === '全部訂單') return filters.value.depositStatus === ''
    if (title === '已付') return filters.value.depositStatus === '1'
    if (title === '未收') return filters.value.depositStatus === '0'
    if (title === '已退款') return filters.value.depositStatus === '2'
    if (title === '已沒收') return filters.value.depositStatus === '3'
  }
  return false
}

const handleCardClick = (title) => {
  if (currentTab.value === 'appt') {
    if (title === '全部預約') selectedApptStatus.value = 'all'
    else if (title === '預約待確認') selectedApptStatus.value = 0
    else if (title === '預約已取消') selectedApptStatus.value = 4
  } else {
    if (title === '全部訂單') filters.value.depositStatus = ''
    else if (title === '已付') filters.value.depositStatus = '1'
    else if (title === '未收') filters.value.depositStatus = '0'
    else if (title === '已退款') filters.value.depositStatus = '2'
    else if (title === '已沒收') filters.value.depositStatus = '3'
  }
}

// Reset to page 1 when tab changes
watch(currentTab, () => {
  currentPage.value = 1
  selectedApptStatus.value = 'all'
  filters.value.depositStatus = ''
})

// Reset to page 1 when filtered list changes
watch(filteredAppointments, () => {
  currentPage.value = 1
})

// Actions
const handleSearch = () => {
  // Filters are reactive, computed filteredAppointments updates automatically
}

const resetFilters = () => {
  filters.value = { keyword: '', startDate: '', endDate: '', depositStatus: '' }
  selectedApptStatus.value = 'all'
}

const goToApptRecord = () => {
  router.push('/groomer/record')
}

const editAppointment = (appt) => {
  Swal.fire('編輯預約', '若需編輯預約內容或修改時間，請至「預約排班」頁面進行操作。', 'info')
}

const confirmAppointment = async (appt) => {
  const id = appt.apptOrderId || appt.appt_order_id
  const apptNo = appt.apptNo || appt.appt_no
  //1150625新增
  const now = new Date()
  const hh = String(now.getHours()).padStart(2, '0')
  const mm = String(now.getMinutes()).padStart(2, '0')
  const ss = String(now.getSeconds()).padStart(2, '0')
  const yyyy = now.getFullYear()
  const MM = String(now.getMonth() + 1).padStart(2, '0')
  const dd = String(now.getDate()).padStart(2, '0')
  const cleanDate = `${yyyy}${MM}${dd}`
  const dateStr = `${yyyy}-${MM}-${dd}`
  const nowStr = `${yyyy}-${MM}-${dd}T${hh}:${mm}:${ss}`


  const result = await Swal.fire({
    title: '確認預約',
    text: `您確定要確認預約單 ${apptNo} 並建立正式訂單嗎？`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '確定',
    cancelButtonText: '取消'
  })

  if (result.isConfirmed) {
    try {
      // 1. 產生正式訂單付款編號 ODR-YYYYMMDD-時段-流水號
      const apptNo = appt.apptNo || appt.appt_no || ''
      let payNo = ''
      if (apptNo.startsWith('APT-')) {
        payNo = apptNo.replace('APT-', 'ODR-')
      } else {
         //1150625修改
        // Fallback
        // const today = new Date()
        // const yyyy = today.getFullYear()
        // const MM = String(today.getMonth() + 1).padStart(2, '0')
        // const dd = String(today.getDate()).padStart(2, '0')
        // const cleanDate = `${yyyy}${MM}${dd}`
        // const dateStr = `${yyyy}-${MM}-${dd}`

        let sameDateCount = 0
        payNo = `ODR-${cleanDate}-1-001`
        try {
          const res = await axiosapi.post('/ajax/pages/WashPayments/find', {
            createdDate: dateStr
          }, {
            headers: { "Authorization": `Bearer ${userStore.token}` }
          })
          const list = Array.isArray(res.data) ? res.data : (res.data?.list || [])
          sameDateCount = list.filter(p => (p.pay_no || p.payNo || '').startsWith(`ODR-${cleanDate}`)).length
          
          let sequence = sameDateCount + 1
          payNo = `ODR-${cleanDate}-1-${String(sequence).padStart(3, '0')}`
          while (list.some(p => (p.pay_no || p.payNo) === payNo)) {
            sequence++
            payNo = `ODR-${cleanDate}-1-${String(sequence).padStart(3, '0')}`
          }
        } catch (err) {
          console.warn("無法取得今日訂單付款筆數:", err)
          payNo = `ODR-${cleanDate}-1-${String(Math.floor(Math.random() * 1000)).padStart(3, '0')}`
        }
      }

      // 2. 尋找此預約單底下的待處理 (status = 0) 訂金付款紀錄 (Purpose = 2)
      let pendingPayment = null
      try {
        const payRes = await axiosapi.post('/ajax/pages/WashPayments/find', {
          memberId: appt.memberId || appt.member_id
        }, {
          headers: { "Authorization": `Bearer ${userStore.token}` }
        })
        const payList = Array.isArray(payRes.data) ? payRes.data : (payRes.data?.list || [])
        pendingPayment = payList.find(p => 
          Number(p.apptOrderId || p.appt_order_id) === Number(id) && 
          Number(p.paymentPurpose || p.payment_purpose) === 2 && 
          Number(p.paymentStatus || p.payment_status) === 0
        )
      } catch (err) {
        console.warn("尋找待處理付款紀錄失敗:", err)
      }

      //const now = new Date()
      const hh = String(now.getHours()).padStart(2, '0')
      const mm = String(now.getMinutes()).padStart(2, '0')
      const ss = String(now.getSeconds()).padStart(2, '0')
      //const nowStr = `${yyyy}-${MM}-${dd}T${hh}:${mm}:${ss}`
      // ↓ 補上這幾行
      const yyyy = now.getFullYear()
      const MM = String(now.getMonth() + 1).padStart(2, '0')
      const dd = String(now.getDate()).padStart(2, '0')
      const cleanDate = `${yyyy}${MM}${dd}`
      const dateStr = `${yyyy}-${MM}-${dd}`
      const nowStr = `${yyyy}-${MM}-${dd}T${hh}:${mm}:${ss}`

      if (pendingPayment) {
        // 更新該筆待處理付款紀錄為付款成功，並變更為 ODR- 單號
        const paymentPayload = {
          ...pendingPayment,
          payNo: payNo,
          pay_no: payNo,
          paymentStatus: 1,
          payment_status: 1,
          paidAt: nowStr,
          paid_at: nowStr,
          transactionNo: 'TXN' + Date.now() + Math.floor(Math.random() * 1000),
          transaction_no: 'TXN' + Date.now() + Math.floor(Math.random() * 1000)
        }
        await axiosapi.put(`/ajax/pages/WashPayments/${pendingPayment.paymentId || pendingPayment.payment_id}`, paymentPayload, {
          headers: { "Authorization": `Bearer ${userStore.token}` }
        })
      } else {
        // 如果沒找到待處理付款紀錄，則直接新建一筆付款成功紀錄
        const paymentPayload = {
          apptOrderId: id,
          memberId: appt.memberId || appt.member_id || userStore.id,
          payNo: payNo,
          paymentPurpose: 2, // 訂金收取
          transactionType: 0, // 支付
          amount: appt.depositAmount || appt.deposit_amount || 200,
          paymentMethod: 0, // 預設現金
          transactionNo: 'TXN' + Date.now() + Math.floor(Math.random() * 1000),
          paidAt: nowStr,
          paymentStatus: 1 // 付款成功
        }
        await axiosapi.post('/ajax/pages/WashPayments', paymentPayload, {
          headers: { "Authorization": `Bearer ${userStore.token}` }
        })
      }

      // 3. 更新預約單狀態：將預約狀態 apptStatus 設為 3 (已完成 / 成為訂單)，訂金狀態 depositStatus 設為 1 (已付)
      const payload = {
        ...appt,
        apptStatus: 3,
        appt_status: 3,
        depositStatus: 1,
        deposit_status: 1,
        confirmedAt: nowStr,
        confirmed_at: nowStr
      }
      const response = await axiosapi.put(`/ajax/pages/WashAppointments/${id}`, payload, {
        headers: { "Authorization": `Bearer ${userStore.token}` }
      })
      let resData = response.data
      if (typeof resData === 'string') {
        try { resData = JSON.parse(resData) } catch (e) {}
      }
      if (resData && resData.success) {
        Swal.fire('成功', `預約單 ${apptNo} 已確認並轉為訂單，成功建立付款單號：${payNo}！`, 'success')
        loadAppointments()
        loadPayments()
      } else {
        throw new Error(resData?.message || '確認失敗')
      }
    } catch (error) {
      console.error(error)
      Swal.fire('錯誤', error.message || '操作失敗，請稍後再試。', 'error')
    }
  }
}

const cancelAppointment = async (appt) => {
  const id = appt.apptOrderId || appt.appt_order_id
  const apptNo = appt.apptNo || appt.appt_no
  const depStatus = appt.depositStatus ?? appt.deposit_status

  // Resolve Customer Name
  const pet = pets.value.find(p => String(p.petId ?? p.id) === String(appt.petId || appt.pet_id))
  const customerName = pet?.user?.nickname || pet?.user?.fullName || pet?.user?.email || '客戶'

  // Resolve Appointment Time
  const apptDate = formatJustDate(appt.apptDate || appt.appt_date)
  const apptTime = formatJustTime(appt.apptStartTime || appt.appt_start_time)
  const apptPeriod = `${apptDate} ${apptTime}`

  let reason = ''

  if (depStatus === 1) {
    // 已付費的訂單 (Paid Order) - Double alert flow
    // First Alert: 是否確認與顧客：{客戶}聯繫，取消{預約時段}?
    const firstResult = await Swal.fire({
      title: '確認取消已付費訂單',
      text: `是否確認與顧客：${customerName}聯繫，取消${apptPeriod}的預約?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '確認',
      cancelButtonText: '取消'
    })

    if (!firstResult.isConfirmed) {
      return // User cancelled
    }
    reason = '已付費訂單已與客戶聯繫取消'
  } else {
    // 未付費 (Unpaid) or other statuses - normal cancel flow
    const { value: cancelReason } = await Swal.fire({
      title: '取消預約',
      input: 'text',
      inputLabel: '請輸入取消預約的原因',
      inputPlaceholder: '例如：客戶來電取消...',
      showCancelButton: true,
      confirmButtonText: '確定取消',
      cancelButtonText: '返回'
    })

    if (!cancelReason) {
      return // User cancelled
    }
    reason = cancelReason
  }

  // Process the cancellation
  try {
    const payload = {
      ...appt,
      apptStatus: 4,
      appt_status: 4,
      cancelReason: reason,
      cancel_reason: reason,
      canceledBy: 'groomer',
      canceled_by: 'groomer',
      canceledAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
      canceled_at: new Date().toISOString().replace('T', ' ').substring(0, 19)
    }
    const response = await axiosapi.put(`/ajax/pages/WashAppointments/${id}`, payload, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    })
    let resData = response.data
    if (typeof resData === 'string') {
      try { resData = JSON.parse(resData) } catch (e) {}
    }
    if (resData && resData.success) {
      if (depStatus === 1) {
        // Second Alert for paid order: 該｛客戶｝已取消{預約時段}，訂單編號為OOO
        const orderNo = getOrderPayNo(appt.apptOrderId || appt.appt_order_id) || apptNo
        await Swal.fire({
          title: '取消成功',
          text: `該${customerName}已取消${apptPeriod}，訂單編號為${orderNo}`,
          icon: 'success',
          confirmButtonText: '確定'
        })
      } else {
        Swal.fire('已取消', `預約單 ${apptNo} 已成功取消`, 'success')
      }
      loadAppointments()
      loadPayments()
    } else {
      throw new Error(resData?.message || '取消失敗')
    }
  } catch (error) {
    console.error(error)
    Swal.fire('錯誤', error.message || '取消失敗，請稍後再試。', 'error')
  }
}
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
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.4), 0 10px 40px rgba(75, 110, 87, 0.05);
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
.subtitle {
  font-size: 14px;
  color: #8C6A4F;
  letter-spacing: 1px;
  font-weight: 500;
}

/* Content Layout */
.content-wrapper {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
}
.sidebar-stats {
  width: 120px; /* 側邊欄寬度稍微增加，讓統計卡片文字更舒適不擠壓 */
  flex-shrink: 0;
  position: sticky;
  top: 2.5rem;
  z-index: 10;
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

.main-panel {
  flex-grow: 1;
  min-width: 0;
}

/* Stats */
.stats-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.stat-card {
  background: rgba(250, 252, 255, 0.7);
  backdrop-filter: blur(16px);
  border-radius: 16px;
  padding: 0.75rem 0.25rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  gap: 4px;
  border: 1px solid rgba(140, 106, 79, 0.15);
  box-shadow: 0 8px 32px rgba(140, 106, 79, 0.05);
  transition: transform 0.2s ease;
}
.stat-card:hover {
  transform: translateY(-2px);
}
.stat-card.active {
  background: #F0E8DF;
  border-color: #8C6A4F;
  box-shadow: 0 8px 32px rgba(140, 106, 79, 0.15);
  transform: translateY(-2px);
}
.stat-label {
  font-size: 12px;
  color: #8C6A4F;
  font-weight: 600;
}
.stat-value {
  font-size: 20px;
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
  border: 1px solid rgba(140, 106, 79, 0.2);
  border-radius: 12px;
  font-size: 14px;
  font-family: inherit;
  background: rgba(255, 255, 255, 0.7);
  color: #4A3E3D;
  transition: all 0.3s ease;
}
.search-input { width: 200px; }
.filter-select { cursor: pointer; }
.search-input:focus, .filter-select:focus { 
  outline: none; 
  border-color: #8C6A4F; 
  background: #FFF;
  box-shadow: 0 0 0 3px rgba(140, 106, 79, 0.15);
}

/* Table */
.table-wrap {
  border: none;
  border-radius: 20px;
  background: #FFFFFF;
  box-shadow: 0 4px 20px rgba(140, 106, 79, 0.03);
  padding: 1.5rem;
  overflow-x: auto;
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
.badge-package { background: #F0E8DF; color: #8C6A4F; }
.badge-active  { background: #E6F4F0; color: #4B6E57; }
.badge-inactive{ background: #FAF8F5; color: #A88262; }
.badge-danger  { background: #FDF6F6; color: #C26B70; }

/* Price */
.price-old {
  text-decoration: line-through;
  color: #A88262;
  font-size: 13px;
  white-space: nowrap;
}
.price-new {
  color: #B58A63; /* 質感金沙 */
  font-weight: 700;
  font-size: 15px;
  white-space: nowrap;
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
  font-weight: 500;
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
  background-color: #B58A63;
  color: white;
  box-shadow: 0 4px 10px rgba(181, 138, 99, 0.2);
}
.btn-primary:hover { background-color: #A07853; color: white; box-shadow: 0 6px 15px rgba(181, 138, 99, 0.3); }

/* 分頁元件 */
.pagination {
  --bs-pagination-color: #8C6A4F;
  --bs-pagination-bg: transparent;
  --bs-pagination-border-width: 0;
  --bs-pagination-hover-color: #785A41;
  --bs-pagination-hover-bg: #FAF6F0;
  --bs-pagination-active-bg: #B58A63;
  --bs-pagination-active-color: #FFF;
}
.page-link {
  border-radius: 8px;
  margin: 0 4px;
}

.currency-symbol-sm {
  font-size: 0.8rem;
  margin-right: 2px;
  font-weight: 500;
  opacity: 0.6;
}
.points-badge {
  background-color: #FAF8F5;
  color: #8C6A4F;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.5px;
}
.detail-amount {
  font-weight: 800;
  color: #B58A63;
  font-size: 16px;
  font-variant-numeric: tabular-nums;
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
  background-color: #B58A63 !important; /* 質感金沙 */
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
  background-color: #D6DFEA !important;
  color: #2D3A4A !important;
}
:global(.swal2-icon.swal2-success) { border-color: #55725F !important; color: #55725F !important; }
:global(.swal2-icon.swal2-success [class^="swal2-success-line"]) { background-color: #55725F !important; }
:global(.swal2-icon.swal2-success .swal2-success-ring) { border-color: rgba(85, 114, 95, 0.3) !important; }
:global(.swal2-icon.swal2-warning) { border-color: #C26B70 !important; color: #C26B70 !important; }
:global(.swal2-icon.swal2-error) { border-color: #C26B70 !important; color: #C26B70 !important; }
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
  .content-wrapper {
    flex-direction: column;
    gap: 1.5rem;
  }
  .sidebar-stats {
    width: 100%;
    position: static;
  }
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  .table-toolbar {
    flex-direction: column;
    align-items: stretch;
  }
  .search-input, .filter-select {
    width: 100% !important;
  }
  .actions {
    margin-left: 0 !important;
    width: 100%;
    justify-content: flex-end;
  }
  table {
    min-width: 800px;
  }
}
</style>
