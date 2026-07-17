<template>
  <div class="wash-manager">
    <!-- Header -->
    <div class="page-header">
      <div>
        <h1>確認預約資料</h1>
        <div class="subtitle-en">APPOINTMENT CHECK</div>
        <p class="subtitle">確認預約與付款狀態</p>
      </div>
      <button 
        class="btn btn-primary" 
        data-bs-toggle="modal" data-bs-target="#apptModal" 
        @click="openCreateModal"
      >
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right: 4px;"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg> 新增預約
      </button>
    </div>

    <div v-if="forwardedBooking" class="forwarded-booking-summary mb-4 p-4 shadow-sm">
      <h4 class="fw-bold mb-3">預約時段已選擇</h4>
      <div class="row">
        <div class="col-md-4 mb-2">
          <div class="summary-label">服務項目</div>
          <div class="summary-value">{{ forwardedBooking.serviceNames }}</div>
        </div>
        <div class="col-md-4 mb-2">
          <div class="summary-label">價格</div>
          <div class="summary-value">NT$ {{ forwardedBooking.price }}</div>
        </div>
        <div class="col-md-4 mb-2">
          <div class="summary-label">時段</div>
          <div class="summary-value">{{ forwardedBooking.dateTime }}</div>
        </div>
      </div>
      <div class="text-muted mt-3">若要確認此預約，請點選右上方「新增預約」並於預約單中填寫對應資料。</div>
    </div>

    <!-- Stats -->
    <div class="stats-grid">
      <div class="stat-card">
        <span class="stat-label">今日預約</span>
        <span class="stat-value">{{ todayCount }} 件</span>
      </div>
      <div class="stat-card">
        <span class="stat-label">待確認預約</span>
        <span class="stat-value">{{ pendingCount }} 件</span>
      </div>
      <div class="stat-card">
        <span class="stat-label">訂金未付</span>
        <span class="stat-value">{{ unpaidCount }} 件</span>
      </div>
    </div>

    <!-- Tabs -->
    <div class="tabs">
      <button 
        v-for="status in statusOptions" 
        :key="status.value"
        class="tab"
        :class="{ active: searchFilter.status === status.value }"
        @click="searchFilter.status = status.value"
      >
        {{ status.label }}
        <span class="tab-badge">{{ statusCounts[status.value] || 0 }}</span>
      </button>
    </div>

    <!-- Toolbar -->
    <div class="table-toolbar" style="display: flex; justify-content: space-between; align-items: center; width: 100%;">
      <div style="display: flex; gap: 12px; align-items: center;">
        <input 
          v-model="searchFilter.keyword" 
          class="search-input" 
          placeholder="搜尋預約單號 / 會員 / 寵物..." 
        />
        <input 
          v-model="searchFilter.date" 
          type="date" 
          class="search-input" 
        />
      </div>

      <!-- 上方分頁元件 -->
      <nav aria-label="Top page navigation" v-if="totalPages > 1" style="margin: 0;">
        <ul class="pagination justify-content-end" style="margin: 0;">
          <li class="page-item" :class="{ disabled: currentPage === 1 }">
            <a class="page-link" href="#" @click.prevent="currentPage--">上一頁</a>
          </li>
          <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }">
            <a class="page-link" href="#" @click.prevent="currentPage = page">{{ page }}</a>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
            <a class="page-link" href="#" @click.prevent="currentPage++">下一頁</a>
          </li>
        </ul>
      </nav>
    </div>

    <!-- Table -->
    <div class="table-wrap">
      <table>
          <thead>
            <tr>
              <th style="width:48px"></th>
              <th style="width:70px; text-align: center; letter-spacing: 2px;">序號</th>
              <th style="letter-spacing: 2px;">預約單號 / 預約日期時段</th>
              <th style="letter-spacing: 2px;">顧客與寵物</th>
              <th style="text-align: right; padding-right: 24px; letter-spacing: 2px;">實付總額</th>
              <th style="text-align: center; letter-spacing: 2px;">訂金狀態</th>
              <th style="text-align: center; letter-spacing: 2px;">預約狀態</th>
              <th style="width:120px; text-align:center; letter-spacing: 2px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="(appt, index) in paginatedAppointments" :key="appt.appt_order_id">
              <tr>
                <td>
                  <button @click="toggleExpand(appt.appt_order_id || appt.apptOrderId)" class="btn-icon">
                    <span v-if="expandedRows.includes(appt.appt_order_id || appt.apptOrderId)">▼</span>
                    <span v-else>▶</span>
                  </button>
                </td>
                <td style="text-align: center; font-weight: 600; color: #5A6B7C;">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                <td>
                  <div style="font-family: monospace; font-weight: 700; color: #B58A63;">{{ appt.appt_no || appt.apptNo }}</div>
                  <div class="muted" style="margin-top: 4px;">{{ formatJustDate(appt.appt_date || appt.apptDate) }} {{ formatJustTime(appt.appt_start_time || appt.apptStartTime) }}</div>
                </td>
                <td>
                  <div style="font-weight: 600;">
                    會員: {{ getCustomerName(appt.pet_id || appt.petId) || '未知顧客' }} (ID: {{ appt.member_id || appt.memberId }})
                  </div>
                  <div class="muted" style="margin-top: 4px; color: #8C6A4F;">
                    🐾 {{ getPetName(appt.pet_id || appt.petId) || '寵物' }}
                    <span v-if="getPetBreed(appt.pet_id || appt.petId)"> ({{ getPetBreed(appt.pet_id || appt.petId) }})</span>
                  </div>
                </td>
                <td style="text-align: right; padding-right: 24px;">
                  <div class="price-new"><span class="currency-symbol">$</span>{{ appt.total_amount || appt.totalAmount }}</div>
                  <div class="price-old" v-if="(appt.discount_amount || appt.discountAmount) > 0"><span class="currency-symbol">$</span>{{ appt.subtotal_amount || appt.subtotalAmount }}</div>
                </td>
                <td style="text-align: center;">
                  <div style="display: flex; align-items: center; justify-content: center; gap: 8px; flex-wrap: wrap;">
                    <span class="badge" :class="getDepositBadgeClass(appt.deposit_status ?? appt.depositStatus)">
                      {{ getDepositStatusLabel(appt.deposit_status ?? appt.depositStatus) }}
                    </span>
                    <button 
                      v-if="(appt.deposit_status ?? appt.depositStatus) === 0 && (appt.appt_status ?? appt.apptStatus) !== 4" 
                      class="btn btn-sm btn-primary" 
                      style="padding: 2px 8px; font-size: 11px; border-radius: 8px; font-weight: 600;"
                      @click="goToPayment(appt)"
                    >
                      前往付款
                    </button>
                  </div>
                  <div class="muted" style="margin-top: 4px; font-size: 11px;" v-if="(appt.deposit_status ?? appt.depositStatus) === 0">期限: {{ formatDateTime(appt.deposit_deadline || appt.depositDeadline) }}</div>
                </td>
                <td style="text-align: center;">
                  <span class="badge" :class="getApptBadgeClass(appt.appt_status ?? appt.apptStatus)">
                    {{ getApptStatusLabel(appt.appt_status ?? appt.apptStatus) }}
                  </span>
                </td>
                <td>
                  <div class="actions" style="justify-content: center;">
                    <button @click="editAppointment(appt)" class="btn-icon" data-bs-toggle="modal" data-bs-target="#apptModal" title="編輯">
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                    </button>
                    <button v-if="(appt.appt_status ?? appt.apptStatus) !== 4" @click="cancelAppointment(appt)" class="btn-icon btn-icon-danger" title="取消">
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>
                    </button>
                  </div>
                </td>
              </tr>

              <tr v-if="expandedRows.includes(appt.appt_order_id || appt.apptOrderId)" class="expanded-row-bg">
                <td colspan="8" style="padding: 18px 24px;">
                  <div class="appt-detail-card">
                    <div class="appt-detail-header">
                      <span class="appt-detail-title">📋 預約服務項目明細</span>
                      <span class="appt-detail-no">預約單號：{{ appt.appt_no || appt.apptNo }}</span>
                    </div>
                    
                    <table class="appt-detail-table">
                      <thead>
                        <tr>
                          <th style="width: 120px;">明細 ID</th>
                          <th>服務項目</th>
                          <th style="text-align: right; width: 150px;">單價</th>
                          <th style="text-align: right; width: 150px;">小計</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr v-if="!(appt.details || appt.washApptDetails || appt.apptDetails)?.length">
                          <td colspan="4" class="appt-detail-empty">目前尚無明細資料</td>
                        </tr>
                        <tr v-for="detail in (appt.details || appt.washApptDetails || appt.apptDetails || [])" :key="detail.apptdetail_id || detail.apptdetailId">
                          <td class="appt-detail-id">#{{ detail.apptdetail_id || detail.apptdetailId }}</td>
                          <td class="appt-detail-service">
                            <span class="appt-detail-badge" :class="(detail.package_service_item_id || detail.packageServiceItemId || detail.isPackage) ? 'badge-pkg' : 'badge-bsc'">
                              {{ (detail.package_service_item_id || detail.packageServiceItemId || detail.isPackage) ? '套餐' : '基礎' }}
                            </span>
                            <span class="appt-detail-service-name">
                              {{ detail.serviceName || detail.service_name || detail.name || detail.serviceId || detail.service_id || '' }}
                            </span>
                          </td>
                          <td class="appt-detail-price">
                            <span class="currency">$</span>{{ detail.unit_price || detail.unitPrice }}
                          </td>
                          <td class="appt-detail-subtotal">
                            <span class="currency">$</span>{{ detail.item_amount || detail.itemAmount }}
                          </td>
                        </tr>
                        <!-- 小計總額 row -->
                        <tr v-if="(appt.details || appt.washApptDetails || appt.apptDetails)?.length">
                          <td colspan="3" style="text-align: right; font-weight: 700; background-color: #FAF6F0; border-top: 1px solid #EFEBE4; color: #8C6A4F; font-size: 13px;">小計總額：</td>
                          <td style="text-align: right; font-weight: 700; background-color: #FAF6F0; border-top: 1px solid #EFEBE4; color: #B58A63; font-size: 15px; font-variant-numeric: tabular-nums;">
                            <span class="currency">$</span>{{ appt.subtotal_amount || appt.subtotalAmount || 0 }}
                          </td>
                        </tr>
                      </tbody>
                    </table>
                    
                    <div class="appt-detail-footer">
                      <div class="detail-note-box">
                        <span class="note-label">預約備註：</span>
                        <span class="note-content">{{ appt.note || '無備註事項' }}</span>
                      </div>
                      
                      <div v-if="appt.cancel_reason || appt.cancelReason" class="detail-cancel-box">
                        <span class="cancel-label">⚠️ 取消原因 ({{ (appt.canceled_by || appt.canceledBy) === '系統自動' ? '系統自動 ' : '由 ' + (appt.canceled_by || appt.canceledBy) }}):</span>
                        <span class="cancel-content">{{ appt.cancel_reason || appt.cancelReason }} ({{ formatDateTime(appt.canceled_at || appt.canceledAt) }})</span>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
            </template>
            <tr v-if="filteredAppointments.length === 0">
              <td colspan="8" class="empty">找不到符合條件的預約單。</td>
            </tr>
          </tbody>
        </table>

        <!-- 下方分頁元件 -->
        <nav aria-label="Page navigation" class="mt-4" v-if="totalPages > 1">
          <ul class="pagination justify-content-end">
            <li class="page-item" :class="{ disabled: currentPage === 1 }">
              <a class="page-link" href="#" @click.prevent="currentPage--">上一頁</a>
            </li>
            <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }">
              <a class="page-link" href="#" @click.prevent="currentPage = page">{{ page }}</a>
            </li>
            <li class="page-item" :class="{ disabled: currentPage === totalPages }">
              <a class="page-link" href="#" @click.prevent="currentPage++">下一頁</a>
            </li>
          </ul>
        </nav>
    </div>

    <!-- Modal: Appt -->
    <div class="modal" id="apptModal" tabindex="-1" aria-labelledby="apptModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="apptModalLabel">{{ isReadOnlyForm ? '檢視美容預約 (唯讀)' : (modal.isEdit ? '編輯美容預約' : '新建立美容預約') }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="submitForm">
              <div class="row g-3 mb-3">
                <div class="col-md-6">
                  <label class="form-label">會員 <span class="text-danger">*</span></label>
                  <div class="form-control" style="background-color: #f8f9fa; cursor: default;">
                    {{ userStore.name || '會員' }} （ID:{{ userStore.id }}）
                  </div>
                  <input type="hidden" v-model="form.member_id">
                </div>
                <div class="col-md-6">
                  <label class="form-label">寵物 <span class="text-danger">*</span></label>
                  <select v-model="form.pet_id" required :disabled="modal.isEdit" class="form-select">
                    <option value="" disabled>請選擇寵物</option>
                    <option v-for="pet in selectPetsByMember" :key="pet.pet_id || pet.id || pet.petId" :value="pet.pet_id ?? pet.id ?? pet.petId">
                      {{ getPetLabel(pet) }}
                    </option>
                  </select>
                </div>
              </div>

              <div v-if="modalDetails && modalDetails.length > 0" class="row g-3 mb-3">
                <div class="col-12">
                  <div style="background: #FFFFFF; border: 1px solid #EFEBE4; border-radius: 12px; padding: 16px; box-shadow: inset 0 2px 4px rgba(0,0,0,0.01);">
                    <h4 style="font-size: 13px; font-weight: 700; color: #8C6A4F; margin-bottom: 12px; border-bottom: 1px solid #F4EFEA; padding-bottom: 8px;">📋 預約服務項目明細</h4>
                    <table style="width: 100%; margin-bottom: 0; border-collapse: separate; border-spacing: 0;">
                      <thead>
                        <tr>
                          <th style="padding: 10px 16px; color: #A28C7A; font-weight: 600; border-bottom: 2px solid #EFEBE4; border-top: none; font-size: 13px;">明細 ID</th>
                          <th style="padding: 10px 16px; color: #A28C7A; font-weight: 600; border-bottom: 2px solid #EFEBE4; border-top: none; font-size: 13px;">服務項目</th>
                          <th style="padding: 10px 16px; text-align: right; color: #A28C7A; font-weight: 600; border-bottom: 2px solid #EFEBE4; border-top: none; font-size: 13px;">原價（元）</th>
                          <th style="padding: 10px 16px; text-align: right; color: #8C6A4F; font-weight: 700; background-color: #FAF6F0; border-radius: 8px 8px 0 0; border-bottom: 2px solid #E8E3DD; border-top: none; font-size: 13px;">特價（元）</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr v-for="(detail, index) in modalDetails" :key="detail.apptdetail_id || detail.apptdetailId">
                          <td style="padding: 12px 16px; font-family: monospace; border-bottom: 1px dashed #EFEBE4;" class="text-muted">#{{ detail.apptdetail_id || detail.apptdetailId }}</td>
                          <td style="padding: 12px 16px; border-bottom: 1px dashed #EFEBE4;">
                            
                            <span style="font-weight: 600; color: #5A4A3E;">
                              {{ (detail.package_service_item_id || detail.packageServiceItemId) ? '套餐服務' : '基礎服務' }}: {{ detail.name || detail.serviceName || detail.service_id || detail.serviceId || detail.package_service_item_id || detail.packageServiceItemId || '' }}
                            </span>
                          </td>
                          <td style="padding: 12px 16px; text-align: right; border-bottom: 1px dashed #EFEBE4;" class="text-muted">
                            <span class="currency-symbol-sm">$</span><span style="font-variant-numeric: tabular-nums;">{{ detail.full_price ?? detail.fullPrice ?? detail.originalPrice ?? detail.unit_price ?? detail.unitPrice }}</span>
                          </td>
                          <td style="padding: 12px 16px; text-align: right; background-color: #FAF6F0;" :style="index === modalDetails.length - 1 ? 'border-radius: 0 0 8px 8px; border-bottom: none;' : 'border-bottom: 1px dashed #E8E3DD;'">
                            <div class="detail-amount"><span class="currency-symbol-sm" style="color: #CBA37E;">$</span>{{ detail.onsale_price ?? detail.onsalePrice ?? detail.salePrice ?? detail.item_amount ?? detail.itemAmount }}</div>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>

              <div style="background-color: #F8EFE6; padding: 16px; border-radius: 16px; margin-bottom: 16px;">
                <div class="row g-3">
                  <div class="col-md-6">
                    <label class="form-label" style="font-size: 12px;">預約日期</label>
                    <input v-model="form.appt_date" type="date" required @change="fetchAvailableSlots" :disabled="modal.isEdit" class="form-control" />
                  </div>
                  <div class="col-md-6" v-if="modal.isEdit">
                    <label class="form-label" style="font-size: 12px;">預約時段</label>
                    <div class="form-control" style="background-color: #f8f9fa; cursor: not-allowed;">
                      時段 {{ form.slot_index }} ({{ form.slot_time }})
                    </div>
                  </div>
                  <div class="col-md-6" v-else>
                    <label class="form-label" style="font-size: 12px;">可用班表時段</label>
                    <select v-model="form.selected_schedule_id" required class="form-select">
                      <option v-for="slot in mockAvailableSchedules" :key="slot.schedule_id" :value="slot.schedule_id">
                        時段 {{ slot.slot_index }} ({{ slot.period }})
                      </option>
                    </select>
                  </div>
                </div>
                <div class="text-danger" style="font-size: 11px; margin-top: 6px; font-weight: 600;" v-if="modal.isEdit && form.appt_status === 0">
                  *要異動請取消此單，重新建立預約單
                </div>
              </div>

              <div class="row g-3 mb-3">
                <div class="col-md-4">
                  <label class="form-label">小計總額</label>
                  <input v-model.number="form.subtotal_amount" type="number" :disabled="modal.isEdit" class="form-control" @input="calculateTotal" />
                </div>
                <div class="col-md-4">
                  <label class="form-label">預扣訂金 (預設 $200)</label>
                  <input v-model.number="form.deposit_amount" type="number" :disabled="modal.isEdit" class="form-control" @input="calculateTotal" />
                </div>
                <div class="col-md-4">
                  <label class="form-label">最終實付總額</label>
                  <input v-model.number="form.total_amount" type="number" disabled class="form-control" style="font-weight: bold; color: #CBA37E;" />
                </div>
              </div>

              <div class="row g-3 mb-3">
                <div class="col-md-6">
                  <label class="form-label">預約備註說明</label>
                  <textarea v-model="form.note" rows="2" :disabled="!isNoteEditable" class="form-control" placeholder="例如：毛孩皮膚過敏需使用草本洗毛精..."></textarea>
                </div>
                <div class="col-md-6">
                  <label class="form-label">訂金截止時間 (預設4小時)</label>
                  <input v-model="form.deposit_deadline" type="datetime-local" :disabled="modal.isEdit" class="form-control" />
                </div>
              </div>

              <div class="modal-footer" style="padding-bottom: 0; border-top: 1px solid #F4EFEA;">
                <button type="button" class="btn" data-bs-dismiss="modal">{{ isReadOnlyForm ? '關閉' : '取消' }}</button>
                <button v-if="!isReadOnlyForm" type="submit" class="btn btn-primary" :disabled="isSubmitting">
                  {{ isSubmitting ? '儲存中...' : '儲存預約' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import axiosapi from '@/plugins/axios.js';
import { userAuthStore } from '@/stores/auth.js';

const router = useRouter();

const authStore = userAuthStore();
const userStore = {
  get id() { return authStore.id; },
  get name() { return authStore.name; },
  get token() { return authStore.accessToken; }
};
const forwardedBooking = ref(null);
const pets = ref([]);
const autoBookingAttempted = ref(false);
const isSubmitting = ref(false);

const generateApptNo = (dateStr, slotIndex) => {
  const date = dateStr ? dateStr : new Date().toISOString().split('T')[0];
  const cleanDate = date.replace(/-/g, '');
  const slot = slotIndex || form.slot_index || 1;
  const prefix = `APT-${cleanDate}-${slot}-`;
  
  const sameSlotAppointments = appointments.value.filter(a => {
    const aApptNo = a.appt_no || a.apptNo;
    return aApptNo && aApptNo.startsWith(prefix);
  });
  
  let sequence = sameSlotAppointments.length + 1;
  let apptNo = `${prefix}${String(sequence).padStart(3, '0')}`;
  
  while (appointments.value.some(a => (a.appt_no || a.apptNo) === apptNo)) {
    sequence++;
    apptNo = `${prefix}${String(sequence).padStart(3, '0')}`;
  }
  
  return apptNo;
};

const getLocalDateTimeString = (date, time) => {
  if (!date) return null;
  if (!time) return `${date}T00:00:00`;
  if (time.includes('T')) return time;
  let formattedTime = time;
  if (time.split(':').length === 2) {
    formattedTime = `${time}:00`;
  }
  return `${date}T${formattedTime}`;
};

const getDefaultDepositDeadline = () => {
  const now = new Date();
  const deadline = new Date(now.getTime() + 4 * 60 * 60 * 1000); // 4 hours later
  
  const yyyy = deadline.getFullYear();
  const MM = String(deadline.getMonth() + 1).padStart(2, '0');
  const dd = String(deadline.getDate()).padStart(2, '0');
  const hh = String(deadline.getHours()).padStart(2, '0');
  const mm = String(deadline.getMinutes()).padStart(2, '0');
  
  return `${yyyy}-${MM}-${dd}T${hh}:${mm}`;
};

const loadForwardedBooking = async () => {
  const saved = sessionStorage.getItem('appointmentConfirmation');
  if (saved) {
    forwardedBooking.value = JSON.parse(saved);
    await prefillBookingForm();
  }
};

const slotTimeToRange = (slotTime) => {
  if (!slotTime) return { start: '', end: '' };
  const parts = slotTime.split(' - ');
  const formatTime = (t) => {
    if (!t) return '';
    const p = t.split(':');
    if (p.length === 2) return `${t}:00`;
    return t;
  };
  return {
    start: formatTime(parts[0]),
    end: formatTime(parts[1])
  };
};

const prefillBookingForm = async () => {
  if (!forwardedBooking.value) return;
  const {
    workDate,
    slotIndex,
    slotTime,
    slotTimeRange,
    slotStartTime,
    slotEndTime,
    price,
    selectedServices,
    serviceNames,
    scheduleId
  } = forwardedBooking.value;

  const formatTimeWithSeconds = (t) => {
    if (!t) return '00:00:00';
    const parts = t.split(':');
    if (parts.length === 2) return `${t}:00`;
    return t;
  };

  const range = slotStartTime && slotEndTime
    ? { start: formatTimeWithSeconds(slotStartTime), end: formatTimeWithSeconds(slotEndTime) }
    : slotTimeRange
      ? { start: formatTimeWithSeconds(slotTimeRange.split('~')[0]), end: formatTimeWithSeconds(slotTimeRange.split('~')[1]) }
      : slotTimeToRange(slotTime);

  // 1. 先設定日期，以便載入可用時段
  form.appt_date = workDate;
  
  // 2. 獲取該日期的時段清單
  await fetchAvailableSlots();

  // 3. 尋找與當前預約的 slotIndex 匹配的時段ID
  let targetScheduleId = scheduleId;
  const matchedSlot = mockAvailableSchedules.value.find(s => Number(s.slot_index) === Number(slotIndex));
  if (matchedSlot) {
    targetScheduleId = matchedSlot.schedule_id;
  } else {
    const mockSlotId = scheduleId || (1000 + Number(slotIndex));
    mockAvailableSchedules.value.push({
      schedule_id: mockSlotId,
      slot_index: Number(slotIndex),
      period: slotTimeMap[slotIndex] || `時段 ${slotIndex}`
    });
    targetScheduleId = mockSlotId;
  }

  // 4. 取得符合會員的寵物ID
  let initialPetId = null;
  if (userStore.id && pets.value && pets.value.length > 0) {
    const matched = pets.value.find(p => {
      const petOwner = p.user?.id ?? p.userId ?? p.user_id ?? p.member_id ?? p.memberId ?? p.owner_id ?? p.ownerId;
      return String(petOwner) === String(userStore.id);
    });
    if (matched) {
      initialPetId = matched.pet_id ?? matched.id ?? matched.petId;
    } else {
      initialPetId = pets.value[0].pet_id ?? pets.value[0].id ?? pets.value[0].petId;
    }
  }

  Object.assign(form, {
    appt_order_id: null,
    appt_no: '',
    apptNo: '',
    member_id: userStore.id || null,
    pet_id: initialPetId,
    pet_groomer_id: 1,
    appt_date: workDate,
    appt_start_time: range.start,
    appt_end_time: range.end,
    selected_schedule_id: targetScheduleId,
    slot_index: slotIndex,
    slot_time: slotTimeRange || slotTime,
    subtotal_amount: price,
    discount_amount: 0,
    total_amount: Math.max(0, price - 200),
    note: '',
    service_summary: serviceNames || selectedServices?.packageService?.name || selectedServices?.basicServices?.map(s => s.name).join(' / ') || '',
    appt_status: 0,
    deposit_status: 0,
    source_type: 1,
    deposit_amount: 200,
    deposit_deadline: getDefaultDepositDeadline()
  });
};

const loadUsersAndPets = async () => {
  try {
    if (userStore.id) {
      form.member_id = userStore.id;
    }

    const petsRes = await axiosapi.post('/ajax/pages/Pets/find', {}, { headers: { "Authorization": `Bearer ${userStore.token}` } });
    pets.value = Array.isArray(petsRes.data) ? petsRes.data : (petsRes.data?.list || []);

    if (forwardedBooking.value && pets.value.length > 0) {
      const loginMemberId = userStore.id;
      const matchedPet = pets.value.find(p => {
        const petOwner = p.user?.id ?? p.userId ?? p.user_id ?? p.member_id ?? p.memberId ?? p.owner_id ?? p.ownerId;
        return String(petOwner) === String(loginMemberId);
      });
      if (matchedPet) {
        form.pet_id = matchedPet.pet_id ?? matchedPet.id ?? matchedPet.petId;
      } else if (pets.value.length > 0) {
        form.pet_id = pets.value[0].pet_id ?? pets.value[0].id ?? pets.value[0].petId;
      }
    }
  } catch (error) {
    console.warn('無法載入寵物資料', error);
  }
};

const selectPetsByMember = computed(() => {
  if (!form.member_id) return pets.value;
  return pets.value.filter(p => {
    const petOwner = p.user?.id ?? p.userId ?? p.user_id ?? p.member_id ?? p.memberId ?? p.owner_id ?? p.ownerId;
    return String(petOwner) === String(form.member_id);
  });
});

const getMemberLabel = () => {
  return `${userStore.name || '會員'} （ID:${userStore.id}）`;
};

const getPetLabel = (pet) => {
  const name = pet.pet_name || pet.name || pet.petName || '寵物';
  const id = pet.pet_id ?? pet.id ?? pet.petId;
  return `${name} （ID:${id}）`;
};

const getPetName = (petId) => {
  if (!petId || !pets.value) return '';
  const pet = pets.value.find(p => {
    const id = p.pet_id ?? p.id ?? p.petId;
    return String(id) === String(petId);
  });
  return pet ? (pet.pet_name || pet.name || pet.petName || '') : '';
};

const getCustomerName = (petId) => {
  if (!petId || !pets.value) return '';
  const pet = pets.value.find(p => {
    const id = p.pet_id ?? p.id ?? p.petId;
    return String(id) === String(petId);
  });
  if (!pet || !pet.user) return '';
  return pet.user.nickname || pet.user.fullName || pet.user.email || '';
};

const getPetBreed = (petId) => {
  if (!petId || !pets.value) return '';
  const pet = pets.value.find(p => {
    const id = p.pet_id ?? p.id ?? p.petId;
    return String(id) === String(petId);
  });
  return pet ? (pet.pet_breed || pet.breed || pet.petBreed || '') : '';
};

const statusOptions = [
  { label: '全部預約', value: 'all' },
  { label: '待確認', value: 0 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 4 },
  { label: '未到', value: 5 },
];

const searchFilter = reactive({
  status: 'all',
  keyword: '',
  date: ''
});

const expandedRows = ref([]);

const modal = reactive({ show: false, isEdit: false });
const form = reactive({
  appt_order_id: null,
  appt_no: '',
  apptNo: '',
  member_id: null,
  pet_id: null,
  pet_groomer_id: 1,
  appt_date: '',
  appt_start_time: '',
  appt_end_time: '',
  selected_schedule_id: null,
  slot_index: null,
  slot_time: '',
  service_summary: '',
  deposit_amount: 200,
  deposit_deadline: getDefaultDepositDeadline(),
  subtotal_amount: 0,
  discount_amount: 0,
  total_amount: 0,
  note: '',
  appt_status: 0,
  deposit_status: 0,
  source_type: 1
});

const isReadOnlyForm = computed(() => {
  if (!modal.isEdit) return false;
  return form.appt_status !== 0; // if status is not '待確認', it is fully read-only (save button hidden)
});

const isNoteEditable = computed(() => {
  if (!modal.isEdit) return true; // new appointment can edit note
  return form.appt_status === 0; // only '待確認' can edit note in edit mode
});

const mockAvailableSchedules = ref([
  { schedule_id: 101, slot_index: 1, period: '09:00 - 11:00' },
  { schedule_id: 102, slot_index: 2, period: '11:00 - 13:00' },
  { schedule_id: 103, slot_index: 4, period: '15:00 - 17:00' }
]);

const appointments = ref([]);
const statusCounts = ref({
  all: 0,
  0: 0,
  1: 0,
  3: 0,
  4: 0,
  5: 0
});

const currentPage = ref(1);
const pageSize = ref(10);
const totalPages = computed(() => Math.ceil(filteredAppointments.value.length / pageSize.value) || 1);

const paginatedAppointments = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredAppointments.value.slice(start, start + pageSize.value);
});

const loadAppointments = async () => {
  try {
    const payload = {};
    if (userStore.id) {
      payload.memberId = userStore.id;
    }
    if (searchFilter.date) {
      payload.apptDate = searchFilter.date;
    }
    const response = await axiosapi.post('/ajax/pages/WashAppointments/find', payload, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    });
    appointments.value = Array.isArray(response.data) ? response.data : (response.data?.list || []);
    
    if (response.data && response.data.statusCounts) {
      statusCounts.value = response.data.statusCounts;
    }
  } catch (error) {
    console.error("載入預約資料失敗:", error);
    Swal.fire('錯誤', '無法取得預約資料庫連線，請稍後再試。', 'error');
  }
};

watch(() => searchFilter.date, () => {
  currentPage.value = 1;
  loadAppointments();
});

watch([() => searchFilter.status, () => searchFilter.keyword], () => {
  currentPage.value = 1;
});

const modalDetails = computed(() => {
  if (modal.isEdit) {
    const appt = appointments.value.find(a => (a.appt_order_id || a.apptOrderId) === form.appt_order_id);
    return appt?.details || appt?.washApptDetails || appt?.apptDetails || [];
  } else if (forwardedBooking.value && forwardedBooking.value.selectedServices) {
    const sel = forwardedBooking.value.selectedServices;
    const list = [];
    if (sel.packageService) {
      list.push({
        apptdetailId: 1,
        apptdetail_id: 1,
        packageServiceItemId: null,
        package_service_item_id: null,
        serviceId: sel.packageService.id,
        service_id: sel.packageService.id,
        unitPrice: sel.packageService.salePrice || sel.packageService.originalPrice || 0,
        unit_price: sel.packageService.salePrice || sel.packageService.originalPrice || 0,
        consumedPoints: 0,
        consumed_points: 0,
        itemAmount: sel.packageService.salePrice || sel.packageService.originalPrice || 0,
        item_amount: sel.packageService.salePrice || sel.packageService.originalPrice || 0,
        isPackage: true,
        name: sel.packageService.name,
        fullPrice: sel.packageService.originalPrice || 0,
        full_price: sel.packageService.originalPrice || 0,
        onsalePrice: sel.packageService.salePrice || sel.packageService.originalPrice || 0,
        onsale_price: sel.packageService.salePrice || sel.packageService.originalPrice || 0
      });
    }
    if (sel.basicServices && sel.basicServices.length > 0) {
      sel.basicServices.forEach((srv, idx) => {
        list.push({
          apptdetailId: idx + 2,
          apptdetail_id: idx + 2,
          packageServiceItemId: null,
          package_service_item_id: null,
          serviceId: srv.id,
          service_id: srv.id,
          unitPrice: srv.salePrice || srv.originalPrice || 0,
          unit_price: srv.salePrice || srv.originalPrice || 0,
          consumedPoints: 0,
          consumed_points: 0,
          itemAmount: srv.salePrice || srv.originalPrice || 0,
          item_amount: srv.salePrice || srv.originalPrice || 0,
          isPackage: false,
          name: srv.name,
          fullPrice: srv.originalPrice || 0,
          full_price: srv.originalPrice || 0,
          onsalePrice: srv.salePrice || srv.originalPrice || 0,
          onsale_price: srv.salePrice || srv.originalPrice || 0
        });
      });
    }
    return list;
  }
  return [];
});

onMounted(async () => {
  await Promise.all([loadAppointments(), loadUsersAndPets()]);
  await loadForwardedBooking();
  if (forwardedBooking.value) {
    await openCreateModal();
    setTimeout(() => {
      const modalBtn = document.querySelector('[data-bs-target="#apptModal"]');
      if (modalBtn) {
        modalBtn.click();
      }
    }, 150);
  }
});

const filteredAppointments = computed(() => {
  return appointments.value.filter(item => {
    const status = item.appt_status ?? item.apptStatus;
    const date = item.appt_date ?? item.apptDate;
    const no = item.appt_no ?? item.apptNo ?? '';
    const mId = item.member_id ?? item.memberId ?? '';
    const pId = item.pet_id ?? item.petId ?? '';

    // Only show appointments of the logged in member
    if (userStore.id && String(mId) !== String(userStore.id)) return false;

    if (searchFilter.status !== 'all' && status !== searchFilter.status) return false;
    if (searchFilter.date && formatJustDate(date) !== searchFilter.date) return false;
    if (searchFilter.keyword) {
      const kw = searchFilter.keyword.toLowerCase();
      return (
        no.toLowerCase().includes(kw) ||
        String(mId).includes(kw) ||
        String(pId).includes(kw)
      );
    }
    return true;
  });
});

const formatJustDate = (d) => {
  if (!d) return '';
  return d.split('T')[0].split(' ')[0];
};
const formatJustTime = (t) => {
  if (!t) return '';
  if (t.includes('T')) return t.split('T')[1].substring(0, 5);
  const parts = t.split(' ');
  return parts.length > 1 ? parts[1].substring(0, 5) : t.substring(0, 5);
};
const formatDateTime = (dt) => {
  if (!dt) return '';
  return dt.replace('T', ' ').substring(0, 16);
};

const todayStr = new Date().toISOString().split('T')[0];
const todayCount = computed(() => appointments.value.filter(a => formatJustDate(a.appt_date || a.apptDate) === todayStr).length);
const pendingCount = computed(() => appointments.value.filter(a => (a.appt_status ?? a.apptStatus) === 0).length);
const unpaidCount = computed(() => appointments.value.filter(a => (a.deposit_status ?? a.depositStatus) === 0 && (a.appt_status ?? a.apptStatus) !== 4).length);
const todayRevenue = computed(() => appointments.value
  .filter(a => formatJustDate(a.appt_date || a.apptDate) === todayStr && (a.appt_status ?? a.apptStatus) !== 4 && (a.appt_status ?? a.apptStatus) !== 5)
  .reduce((sum, a) => sum + (a.total_amount || a.totalAmount || 0), 0)
);

const toggleExpand = async (id) => {
  const index = expandedRows.value.indexOf(id);
  if (index > -1) {
    expandedRows.value.splice(index, 1);
  } else {
    expandedRows.value.push(id);
    const appt = appointments.value.find(a => (a.appt_order_id || a.apptOrderId) === id);
    if (appt && !appt.detailsLoaded) {
      try {
        const response = await axiosapi.post('/ajax/pages/WashApptDetails/find', { apptOrderId: id }, {
          headers: { "Authorization": `Bearer ${userStore.token}` }
        });
        appt.details = Array.isArray(response.data) ? response.data : (response.data?.list || []);
        appt.detailsLoaded = true;
      } catch (error) {
        console.error(`無法載入預約單 ${id} 的明細資料:`, error);
      }
    }
  }
};

const getApptStatusLabel = (s) => {
  const map = { 0: '待確認', 1: '已確認', 2: '服務中', 3: '已完成', 4: '已取消', 5: '未到' };
  return map[s] || '未知';
};
const getApptBadgeClass = (s) => {
  const map = {
    0: 'badge-package',
    1: 'badge-active',
    2: 'badge-active',
    3: 'badge-basic',
    4: 'badge-inactive',
    5: 'badge-danger'
  };
  return map[s] || 'badge-basic';
};

const getDepositStatusLabel = (s) => ['未付', '已付', '已退款', '已沒收'][s] || '未知';
const getDepositBadgeClass = (s) => {
  return s === 1 
    ? 'badge-active'
    : s === 0 ? 'badge-danger'
    : 'badge-inactive';
};

const calculateTotal = () => {
  form.total_amount = Math.max(0, form.subtotal_amount - (form.deposit_amount || 0));
};

const slotTimeMap = {
  1: '09:00 - 10:00',
  2: '10:00 - 11:00',
  3: '11:00 - 12:00',
  4: '12:00 - 13:00',
  5: '14:00 - 15:00',
  6: '15:00 - 16:00',
  7: '16:00 - 17:00',
  8: '17:00 - 18:00'
};

const fetchAvailableSlots = async () => {
  if (!form.appt_date) {
    mockAvailableSchedules.value = [];
    return;
  }
  try {
    const response = await axiosapi.get(`/ajax/pages/WashSchedules/workDate/${form.appt_date}`, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    });
    const fetchedList = Array.isArray(response.data) ? response.data : (response.data?.list || []);
    
    if (fetchedList.length > 0) {
      const avail = fetchedList.filter(s => s.isAvailable && !s.apptOrderId);
      if (avail.length > 0) {
        mockAvailableSchedules.value = avail.map(s => ({
          schedule_id: s.scheduleId,
          slot_index: s.slotIndex,
          period: slotTimeMap[s.slotIndex] || `時段 ${s.slotIndex}`
        }));
        return;
      }
    }
  } catch (e) {
    console.warn("無法取得該日期的班表，將啟用模擬時段", e);
  }

  mockAvailableSchedules.value = Array.from({ length: 8 }, (_, i) => {
    const index = i + 1;
    return {
      schedule_id: 1000 + index,
      slot_index: index,
      period: slotTimeMap[index]
    };
  });
};

const openCreateModal = async () => {
  modal.isEdit = false;
  if (forwardedBooking.value) {
    await prefillBookingForm();
  } else {
    let initialPetId = null;
    if (userStore.id && pets.value && pets.value.length > 0) {
      const matched = pets.value.find(p => {
        const petOwner = p.user?.id ?? p.userId ?? p.user_id ?? p.member_id ?? p.memberId ?? p.owner_id ?? p.ownerId;
        return String(petOwner) === String(userStore.id);
      });
      if (matched) {
        initialPetId = matched.pet_id ?? matched.id ?? matched.petId;
      }
    }
    Object.assign(form, {
      appt_order_id: null,
      appt_no: '',
      apptNo: '',
      member_id: userStore.id || null,
      pet_id: initialPetId,
      pet_groomer_id: 1,
      appt_date: '',
      appt_start_time: '',
      appt_end_time: '',
      selected_schedule_id: null,
      slot_index: null,
      slot_time: '',
      service_summary: '',
      deposit_amount: 200,
      deposit_deadline: getDefaultDepositDeadline(),
      subtotal_amount: 0,
      discount_amount: 0,
      total_amount: 0,
      note: '',
      appt_status: 0,
      deposit_status: 0,
      source_type: 1
    });
  }
};



const getSlotIndexAndTime = (appt) => {
  let startTime = appt.appt_start_time || appt.apptStartTime || '';
  if (!startTime) return { index: 1, range: '09:00 - 10:00' };
  if (startTime.includes('T')) {
    startTime = startTime.split('T')[1];
  }
  const parts = startTime.split(':');
  if (parts.length >= 2) {
    const hh = parts[0];
    const mm = parts[1];
    const timeKey = `${hh}:${mm}`;
    const mapping = {
      '09:00': { index: 1, range: '09:00 - 10:00' },
      '10:00': { index: 2, range: '10:00 - 11:00' },
      '11:00': { index: 3, range: '11:00 - 12:00' },
      '12:00': { index: 4, range: '12:00 - 13:00' },
      '14:00': { index: 5, range: '14:00 - 15:00' },
      '15:00': { index: 6, range: '15:00 - 16:00' },
      '16:00': { index: 7, range: '16:00 - 17:00' },
      '17:00': { index: 8, range: '17:00 - 18:00' }
    };
    return mapping[timeKey] || { index: 1, range: `${timeKey}` };
  }
  return { index: 1, range: startTime };
};

const editAppointment = async (appt) => {
  modal.isEdit = true;
  
  const id = appt.appt_order_id || appt.apptOrderId;
  
  // 點選[編輯]，要顯示預約服務項目明細，若無明細則在此載入
  if (appt && !appt.detailsLoaded) {
    try {
      const response = await axiosapi.post('/ajax/pages/WashApptDetails/find', { apptOrderId: id }, {
        headers: { "Authorization": `Bearer ${userStore.token}` }
      });
      appt.details = Array.isArray(response.data) ? response.data : (response.data?.list || []);
      appt.detailsLoaded = true;
    } catch (error) {
      console.error(`編輯時無法載入預約單 ${id} 的明細資料:`, error);
    }
  }
  
  const slotInfo = getSlotIndexAndTime(appt);
  
  let deadline = appt.deposit_deadline || appt.depositDeadline || '';
  if (deadline) deadline = deadline.replace(' ', 'T').substring(0, 16);
  
  let apptDate = appt.appt_date || appt.apptDate || '';
  if (apptDate) apptDate = apptDate.split('T')[0].split(' ')[0];

  Object.assign(form, {
    appt_order_id: appt.appt_order_id || appt.apptOrderId,
    appt_no: appt.appt_no || appt.apptNo,
    apptNo: appt.appt_no || appt.apptNo,
    member_id: appt.member_id || appt.memberId,
    pet_id: appt.pet_id || appt.petId,
    pet_groomer_id: appt.pet_groomer_id || appt.petGroomerId || 1,
    appt_date: apptDate,
    appt_start_time: formatJustTime(appt.appt_start_time || appt.apptStartTime || ''),
    appt_end_time: formatJustTime(appt.appt_end_time || appt.apptEndTime || ''),
    selected_schedule_id: appt.selected_schedule_id || appt.selectedScheduleId,
    slot_index: slotInfo.index,
    slot_time: slotInfo.range,
    service_summary: appt.service_summary || appt.serviceSummary || '',
    deposit_amount: appt.deposit_amount ?? appt.depositAmount ?? 200,
    deposit_deadline: deadline,
    subtotal_amount: appt.subtotal_amount ?? appt.subtotalAmount,
    discount_amount: appt.discount_amount ?? appt.discountAmount,
    total_amount: appt.total_amount ?? appt.totalAmount,
    note: appt.note,
    appt_status: appt.appt_status ?? appt.apptStatus ?? 0,
    deposit_status: appt.deposit_status ?? appt.depositStatus ?? 0,
    source_type: appt.source_type ?? appt.sourceType ?? 1
  });
};

const cancelAppointment = async (appt) => {
  const { value: reason } = await Swal.fire({
    title: '取消預約',
    input: 'text',
    inputLabel: '請輸入取消預約的原因',
    inputPlaceholder: '例如：客戶來電取消...',
    showCancelButton: true,
    confirmButtonText: '確定取消',
    cancelButtonText: '返回',
    inputValidator: (value) => {
      if (!value) return '需填寫取消原因'
    }
  });

  if (reason) {
    try {
      const id = appt.appt_order_id || appt.apptOrderId;
      const payload = {
        ...appt,
        apptStatus: 4,
        appt_status: 4,
        cancelReason: reason,
        cancel_reason: reason,
        canceledBy: 'member',
        canceled_by: 'member',
        canceledAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
        canceled_at: new Date().toISOString().replace('T', ' ').substring(0, 19)
      };
      await axiosapi.put(`/ajax/pages/WashAppointments/${id}`, payload, { headers: { "Authorization": `Bearer ${userStore.token}` } });
      Swal.fire('已取消', `預約單號：${appt.appt_no || appt.apptNo}\n已成功取消！`, 'success');
      loadAppointments();
    } catch (error) {
      console.error(error);
      Swal.fire('錯誤', '取消失敗，請稍後再試。', 'error');
    }
  }
};

const startLinePayPayment = async (paymentId) => {
  const response = await axiosapi.post('/ajax/pages/WashLinePay/request', { paymentId }, {
    headers: { "Authorization": `Bearer ${userStore.token}` }
  });
  let resData = response.data;
  if (typeof resData === 'string') {
    try { resData = JSON.parse(resData); } catch (e) {}
  }
  if (!resData?.success || !resData?.paymentUrl) {
    throw new Error(resData?.message || 'LINE Pay 付款連結建立失敗');
  }
  window.location.href = resData.paymentUrl;
};

const goToPayment = async (appt) => {
  if ((appt.appt_status ?? appt.apptStatus) === 4) {
    Swal.fire('錯誤', '此預約已取消，無法進行付款。', 'error');
    return;
  }
  const { value: paymentMethod } = await Swal.fire({
    title: '請選擇付款方式',
    html: `
      <div style="text-align: left; padding: 10px 20px;">
        <p style="margin-bottom: 12px; font-weight: 600; color: #8C6A4F;">預約單號：${appt.apptNo || appt.appt_no}</p>
        <div style="display: flex; flex-direction: column; gap: 10px;">
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; color: #5A4A3E;">
            <input type="radio" name="payMethod" value="1" checked style="accent-color: #CBA37E;" /> 1. 現金付款
          </label>
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; color: #5A4A3E;">
            <input type="radio" name="payMethod" value="2" style="accent-color: #CBA37E;" /> 2. ATM 轉帳
          </label>
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; color: #5A4A3E;">
            <input type="radio" name="payMethod" value="3" style="accent-color: #CBA37E;" /> 3. 刷卡
          </label>
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; color: #5A4A3E;">
            <input type="radio" name="payMethod" value="4" style="accent-color: #CBA37E;" /> 4. LINE Pay
          </label>
        </div>
      </div>
    `,
    showCancelButton: true,
    confirmButtonText: '確認選擇',
    cancelButtonText: '取消',
    preConfirm: () => {
      const checkedRadio = document.querySelector('input[name="payMethod"]:checked');
      return checkedRadio ? checkedRadio.value : null;
    }
  });

  if (paymentMethod) {
    const methods = { '1': '現金付款', '2': 'ATM轉帳', '3': '刷卡', '4': 'LINE Pay' };
    const methodName = methods[paymentMethod];
    try {
      // 1. 產生待付款編號 PMT-YYYYMMDD-XXX
      const today = new Date();
      const yyyy = today.getFullYear();
      const MM = String(today.getMonth() + 1).padStart(2, '0');
      const dd = String(today.getDate()).padStart(2, '0');
      const cleanDate = `${yyyy}${MM}${dd}`;
      const dateStr = `${yyyy}-${MM}-${dd}`;

      let sameDateCount = 0;
      let payNo = `PMT-${cleanDate}-001`;
      try {
        const res = await axiosapi.post('/ajax/pages/WashPayments/find', {
          createdDate: dateStr
        }, {
          headers: { "Authorization": `Bearer ${userStore.token}` }
        });
        const list = Array.isArray(res.data) ? res.data : (res.data?.list || []);
        sameDateCount = list.filter(p => (p.pay_no || p.payNo || '').startsWith(`PMT-${cleanDate}`)).length;
        
        let sequence = sameDateCount + 1;
        payNo = `PMT-${cleanDate}-${String(sequence).padStart(3, '0')}`;
        while (list.some(p => (p.pay_no || p.payNo) === payNo)) {
          sequence++;
          payNo = `PMT-${cleanDate}-${String(sequence).padStart(3, '0')}`;
        }
      } catch (err) {
        console.warn("無法取得今日付款筆數:", err);
        payNo = `PMT-${cleanDate}-${String(Math.floor(Math.random() * 1000)).padStart(3, '0')}`;
      }

      // 2. 先更新預約單狀態 (保持未付款 0)
      const id = appt.apptOrderId || appt.appt_order_id;
      const payload = { 
        ...appt, 
        depositStatus: 0, 
        deposit_status: 0 
      };
      const response = await axiosapi.put(`/ajax/pages/WashAppointments/${id}`, payload, {
        headers: { "Authorization": `Bearer ${userStore.token}` }
      });
      let resData = response.data;
      if (typeof resData === 'string') {
        try { resData = JSON.parse(resData); } catch (e) {}
      }

      if (resData && resData.success) {
        // 3. 寫入付款資料表 WashPayment (狀態為 0: 待處理)
        let dbMethod = 0;
        if (paymentMethod === '1') dbMethod = 0; // 現金
        else if (paymentMethod === '2') dbMethod = 1; // ATM
        else if (paymentMethod === '3') dbMethod = 2; // 刷卡
        else if (paymentMethod === '4') dbMethod = 3; // LINE Pay

        const paymentPayload = {
          apptOrderId: id,
          memberId: appt.memberId || appt.member_id || userStore.id,
          payNo: payNo,
          paymentPurpose: 2, // 訂金收取
          transactionType: 0, // 支付
          amount: appt.depositAmount || appt.deposit_amount || 200,
          paymentMethod: dbMethod,
          transactionNo: 'TXN' + Date.now() + Math.floor(Math.random() * 1000),
          paidAt: null,
          paymentStatus: 0 // 待處理
        };

        const paymentResponse = await axiosapi.post('/ajax/pages/WashPayments', paymentPayload, {
          headers: { "Authorization": `Bearer ${userStore.token}` }
        });
        let paymentResult = paymentResponse.data;
        if (typeof paymentResult === 'string') {
          try { paymentResult = JSON.parse(paymentResult); } catch (e) {}
        }
        if (!paymentResult?.success) {
          throw new Error(paymentResult?.message || '付款失敗，請稍後再試。');
        }

        if (paymentMethod === '4') {
          await startLinePayPayment(paymentResult.paymentId);
          return;
        }

        await Swal.fire({
          title: '已選擇付款方式',
          text: `已為您選擇 [${methodName}] 付款方式，待美容師確認後將正式生成訂單。`,
          icon: 'success'
        });
        
        loadAppointments();
      } else {
        throw new Error(resData?.message || '付款更新失敗');
      }
    } catch (error) {
      console.error(error);
      Swal.fire('錯誤', error.message || '付款失敗，請稍後再試。', 'error');
    }
  }
};


const submitForm = async () => {
  if (isSubmitting.value) return;

  if (!modal.isEdit && (!modalDetails.value || modalDetails.value.length === 0)) {
    Swal.fire('提示', '預約必須包含至少一個服務項目。請回到價目表選取服務。', 'warning');
    return;
  }

  isSubmitting.value = true;
  try {
    const apptNo = form.appt_no || form.apptNo || generateApptNo(form.appt_date, form.slot_index);
    const payload = {
      memberId: form.member_id,
      petId: form.pet_id,
      apptDate: form.appt_date ? `${form.appt_date}T00:00:00` : null,
      apptStartTime: getLocalDateTimeString(form.appt_date, form.appt_start_time),
      apptNo: apptNo,
      apptStatus: modal.isEdit ? form.appt_status : 0,
      depositAmount: form.deposit_amount || 200,
      depositStatus: modal.isEdit ? form.deposit_status : 0,
      depositDeadline: form.deposit_deadline || null,
      subtotalAmount: form.subtotal_amount,
      discountAmount: form.discount_amount,
      totalAmount: form.total_amount,
      sourceType: modal.isEdit ? form.source_type : 1,
      note: form.note,

      appt_order_id: form.appt_order_id,
      appt_no: apptNo,
      member_id: form.member_id,
      pet_id: form.pet_id,
      pet_groomer_id: form.pet_groomer_id,
      appt_date: form.appt_date,
      appt_start_time: form.appt_start_time,
      appt_end_time: form.appt_end_time,
      selected_schedule_id: form.selected_schedule_id,
      slot_index: form.slot_index,
      slot_time: form.slot_time,
      service_summary: form.service_summary,
      subtotal_amount: form.subtotal_amount,
      discount_amount: form.discount_amount,
      total_amount: form.total_amount,
      deposit_amount: form.deposit_amount,
      deposit_deadline: form.deposit_deadline,
      note: form.note,
      appt_status: modal.isEdit ? form.appt_status : 0,
      deposit_status: modal.isEdit ? form.deposit_status : 0,
      source_type: modal.isEdit ? form.source_type : 1
    };

    if (modal.isEdit) {
      const id = form.appt_order_id || form.apptOrderId;
      const response = await axiosapi.put(`/ajax/pages/WashAppointments/${id}`, payload, { headers: { "Authorization": `Bearer ${userStore.token}` } });
      
      let resData = response.data;
      if (typeof resData === 'string') {
        try { resData = JSON.parse(resData); } catch (e) {}
      }
      
      if (resData && resData.success) {
        Swal.fire('成功', `預約單號：${apptNo}\n修改成功！`, 'success');
      } else {
        throw new Error(resData?.message || '修改失敗');
      }
    } else {
      const response = await axiosapi.post('/ajax/pages/WashAppointments', payload, { headers: { "Authorization": `Bearer ${userStore.token}` } });
      
      let resData = response.data;
      if (typeof resData === 'string') {
        try { resData = JSON.parse(resData); } catch (e) {}
      }
      
      if (resData && resData.success) {
        const createdApptOrderId = resData.apptOrderId;
        
        if (modalDetails.value && modalDetails.value.length > 0) {
          for (const detail of modalDetails.value) {
            const detailPayload = {
              apptOrderId: createdApptOrderId,
              serviceId: detail.serviceId || detail.service_id || null,
              packageServiceItemId: detail.packageServiceItemId || detail.package_service_item_id || null,
              consumedPoints: detail.consumedPoints || detail.consumed_points || 0,
              unitPrice: detail.unitPrice || detail.unit_price || 0,
              itemAmount: detail.itemAmount || detail.item_amount || 0
            };
            await axiosapi.post('/ajax/pages/WashApptDetails', detailPayload, { headers: { "Authorization": `Bearer ${userStore.token}` } });
          }
        }

        if (form.selected_schedule_id) {
          const isMockId = Number(form.selected_schedule_id) >= 1000;
          if (isMockId) {
            await axiosapi.post('/ajax/pages/WashSchedules', {
              workDate: form.appt_date,
              slotIndex: Number(form.slot_index),
              isAvailable: false,
              apptOrderId: String(createdApptOrderId)
            }, { headers: { "Authorization": `Bearer ${userStore.token}` } });
          } else {
            await axiosapi.put(`/ajax/pages/WashSchedules/${form.selected_schedule_id}`, {
              scheduleId: form.selected_schedule_id,
              apptOrderId: String(createdApptOrderId)
            }, { headers: { "Authorization": `Bearer ${userStore.token}` } });
          }
        }

        const finalApptNo = resData.apptNo || apptNo;
        Swal.fire('成功', `預約單號：${finalApptNo}\n新增成功！\n請點擊[前往付款]按鈕，並於期限內繳納訂金，逾時訂金未繳，系統將自動取消預約`, 'success');
        forwardedBooking.value = null;
        sessionStorage.removeItem('appointmentConfirmation');
        sessionStorage.removeItem('selectedServices');
      } else {
        throw new Error(resData?.message || '新增預約失敗');
      }
    }
    document.querySelector('#apptModal .btn-close')?.click();
    loadAppointments();
  } catch (error) {
    console.error(error);
    Swal.fire('錯誤', error.message || '儲存失敗，請稍後再試。', 'error');
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
* { box-sizing: border-box; }

.wash-manager {
  font-family: 'Noto Sans TC', sans-serif;
  font-size: 15px;
  color: #3D2B1F;
  padding: 2.5rem 3rem;
  max-width: 1200px;
  margin: 2rem auto;
  background: linear-gradient(135deg, #FAF6F0 0%, #F2EBE3 100%);
  border-radius: 24px;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.6), 0 10px 40px rgba(140, 106, 79, 0.06);
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
  background: linear-gradient(135deg, #8C6A4F 0%, #5A4A3E 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.subtitle-en {
  font-size: 12px;
  color: #A28C7A;
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
  color: #A28C7A;
  letter-spacing: 1px;
  font-weight: 500;
}

/* Stats */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 2rem;
}
.stat-card {
  background: rgba(255, 255, 255, 0.55);
  backdrop-filter: blur(16px);
  border-radius: 20px;
  padding: 1.25rem 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 6px;
  border: 1px solid rgba(203, 163, 126, 0.15);
  box-shadow: 0 8px 32px rgba(140, 106, 79, 0.05);
  transition: transform 0.2s ease;
}
.stat-card:hover {
  transform: translateY(-2px);
}
.stat-label {
  font-size: 13px;
  color: #A28C7A;
  font-weight: 600;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #8C6A4F;
}

/* Tabs */
.tabs {
  display: flex;
  gap: 8px;
  border-bottom: 1px solid rgba(107, 142, 117, 0.2);
  margin-bottom: 1.5rem;
}
.tab {
  padding: 10px 20px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  background: none;
  color: #A28C7A;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  font-family: inherit;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.tab:hover {
  color: #8C6A4F;
}
.tab-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  font-size: 11px;
  font-weight: 700;
  line-height: 1;
  color: #4F7086; /* Quantity count in cool slate-blue */
  background-color: #EBF0F3;
  border-radius: 10px;
  transition: all 0.3s ease;
}
.tab:hover .tab-badge {
  color: #35607A;
  background-color: #DEE6EC;
}
.tab.active .tab-badge {
  color: #FFF;
  background-color: #4F7086;
}
.tab.active {
  color: #4F7086; /* Accent color matching quantity theme */
  border-bottom-color: #4F7086;
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
  border: 1px solid rgba(203, 163, 126, 0.2);
  border-radius: 12px;
  font-size: 14px;
  font-family: inherit;
  background: rgba(255, 255, 255, 0.7);
  color: #5A4A3E;
  transition: all 0.3s ease;
}
.search-input { width: 220px; }
.filter-select { cursor: pointer; }

.search-input:focus, .filter-select:focus { 
  outline: none; 
  border-color: #CBA37E; 
  background: #FFF;
  box-shadow: 0 0 0 3px rgba(203, 163, 126, 0.15);
}

/* Table */
.table-wrap {
  border: none;
  border-radius: 20px;
  background: #FFFFFF;
  box-shadow: 0 4px 20px rgba(140, 106, 79, 0.05);
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
  border-bottom: 2px solid #F4EFEA;
}
td {
  padding: 14px 12px;
  border-bottom: 1px solid #F4EFEA;
  color: #3D2B1F;
  vertical-align: middle;
}
tr:last-child td { border-bottom: none; }
tr:hover td { background: #FAF5F0; }

.muted { color: #A28C7A; font-size: 13px; }
.name-cell { font-weight: 600; }
.empty { text-align: center; color: #A28C7A; padding: 3rem; font-weight: 500; }

/* Badge */
.badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
}
.badge-basic   { background: #F4EFEA; color: #8C6A4F; }
.badge-package { background: #FAF1E6; color: #B58A63; }
.badge-active  { background: #EBF0E6; color: #8C9C7E; }
.badge-inactive{ background: #F4EFEA; color: #A28C7A; }
.badge-danger  { background: #FDF5F5; color: #D38282; }

/* Price */
.currency-symbol {
  font-size: 0.75rem;
  color: #A28C7A;
  margin-right: 3px;
  font-weight: 600;
  vertical-align: baseline;
}
.currency-symbol-sm {
  font-size: 0.8rem;
  margin-right: 2px;
  font-weight: 500;
  opacity: 0.6;
}
.points-badge {
  background-color: #F4EFEA;
  color: #8C6A4F;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.5px;
}
.detail-amount {
  font-weight: 800;
  color: #CBA37E;
  font-size: 16px;
  font-variant-numeric: tabular-nums;
}
.price-old {
  text-decoration: line-through;
  color: #D6D0CB;
  font-size: 13px;
  font-variant-numeric: tabular-nums;
  margin-top: 2px;
}
.price-new {
  color: #B58A63; /* Amount price in gold-bronze */
  font-weight: 800;
  font-size: 18px;
  font-variant-numeric: tabular-nums;
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
.btn-icon:hover { background: #F4EFEA; }
.btn-icon-danger { color: #D38282; }
.btn-icon-danger:hover { background: #FDF5F5; }

/* Buttons */
.btn {
  padding: 9px 22px;
  border-radius: 999px; /* 膠囊造型 */
  font-size: 14px;
  font-weight: 800; /* 加粗 */
  font-family: inherit;
  cursor: pointer;
  border: none;
  background: #E8E3DD;
  color: #6C5B4E;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
  display: inline-flex; align-items: center; gap: 6px;
  box-shadow: 0 2px 6px rgba(140, 106, 79, 0.08);
}
.btn:hover {
  background: #D6CEC4;
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(108, 91, 78, 0.15);
}
.btn:active {
  transform: translateY(0);
}
.btn-primary {
  background-color: #5d919f; /* LoginForm 藍綠色 */
  color: white;
  box-shadow: 0 4px 10px rgba(93, 145, 159, 0.2);
}
.btn-primary:hover {
  background-color: #4a7784;
  color: white;
  box-shadow: 0 8px 20px rgba(93, 145, 159, 0.35);
}
.btn-danger {
  background-color: #D38282;
  color: white;
  box-shadow: 0 4px 10px rgba(211, 130, 130, 0.2);
}
.btn-danger:hover {
  background-color: #C57272;
  box-shadow: 0 8px 20px rgba(211, 130, 130, 0.35);
}

/* Modal 卡片化 */
.modal-content {
  border-radius: 24px;
  border: none;
  box-shadow: 0 10px 40px rgba(140, 106, 79, 0.15);
  background-color: #FFFFFF;
}
.modal-header {
  border-bottom: 1px solid #F4EFEA;
}
.modal-title {
  color: #8C6A4F;
  font-weight: 700;
}
/* 表單輸入框無邊框/輕量感 */
.form-control, .form-select {
  border: 1px solid #EFEBE4;
  border-radius: 12px;
  background-color: #FCFBF9;
  box-shadow: inset 0 2px 4px rgba(0,0,0,0.01);
  padding: 0.5rem 1rem;
}
.form-control:focus, .form-select:focus {
  border-color: #CBA37E;
  background-color: #FFFFFF;
  box-shadow: 0 0 0 0.25rem rgba(203, 163, 126, 0.2);
}

/* SweetAlert2 彈出視窗金棕色系與輕量化設計 */
:global(.swal2-popup) {
  border-radius: 24px !important;
  box-shadow: 0 10px 40px rgba(140, 106, 79, 0.15) !important;
  background-color: #FFFFFF !important;
  color: #6C5B4E !important;
  border: none !important;
}
:global(.swal2-title) {
  color: #8C6A4F !important;
  font-weight: 700 !important;
}
:global(.swal2-actions .swal2-confirm) {
  background-color: #CBA37E !important;
  color: #FFF !important;
  border-radius: 12px !important;
  border: none !important;
  box-shadow: 0 4px 10px rgba(203, 163, 126, 0.2) !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
  padding: 0.6em 1.5em !important;
}
:global(.swal2-actions .swal2-confirm:hover) {
  background-color: #B58A63 !important;
  box-shadow: 0 6px 15px rgba(203, 163, 126, 0.3) !important;
}
:global(.swal2-actions .swal2-cancel) {
  background-color: #E8E3DD !important;
  color: #6C5B4E !important;
  border-radius: 12px !important;
  border: none !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
  padding: 0.6em 1.5em !important;
}
:global(.swal2-actions .swal2-cancel:hover) {
  background-color: #D6CEC4 !important;
}
/* 調整 SweetAlert2 的圖示顏色以符合整體色彩 */
:global(.swal2-icon.swal2-success) { border-color: #A3B296 !important; color: #A3B296 !important; }
:global(.swal2-icon.swal2-success [class^="swal2-success-line"]) { background-color: #A3B296 !important; }
:global(.swal2-icon.swal2-success .swal2-success-ring) { border-color: rgba(163, 178, 150, 0.3) !important; }
:global(.swal2-icon.swal2-warning) { border-color: #D38282 !important; color: #D38282 !important; }
:global(.swal2-icon.swal2-error) { border-color: #D38282 !important; color: #D38282 !important; }
:global(.swal2-icon.swal2-error [class^="swal2-x-mark-line"]) { background-color: #D38282 !important; }

/* RWD 響應式設計 (手機/平板優化) */
@media (max-width: 768px) {
  .wash-manager {
    padding: 1.5rem 1rem;
    border-radius: 16px;
    margin: 1rem;
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
    min-width: 800px;
  }
}

/* 移除 Modal 開啟時瀏覽器預設對焦點的黑框 outline 樣式 */
.modal, .modal-dialog, .modal-content {
  outline: none !important;
  box-shadow: none !important;
}
.modal:focus, .modal-dialog:focus, .modal-content:focus {
  outline: none !important;
  box-shadow: none !important;
}

/* 分頁元件 */
.pagination {
  --bs-pagination-color: #CBA37E;
  --bs-pagination-bg: transparent;
  --bs-pagination-border-width: 0;
  --bs-pagination-hover-color: #B58A63;
  --bs-pagination-hover-bg: #F4EFEA;
  --bs-pagination-active-bg: #CBA37E;
  --bs-pagination-active-color: #FFF;
  margin-top: 1.5rem;
}
.page-link {
  border-radius: 8px;
  margin: 0 4px;
  cursor: pointer;
}

/* Form Label Spacing Alignment */
.form-label {
  letter-spacing: 2px;
  font-weight: 600;
  color: #5A4A3E;
}

/* Detail expanded row stylings */
.expanded-row-bg {
  background: #FAF8F5 !important;
}
.appt-detail-card {
  background: #FCFBF9;
  border: 1px solid #EFEBE4;
  border-left: 4px solid #B58A63; /* Accent golden sienna line */
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(140, 106, 79, 0.03);
}
.appt-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  padding-bottom: 8px;
  border-bottom: 1px solid #EFEBE4;
}
.appt-detail-title {
  font-size: 14px;
  font-weight: 700;
  color: #8C6A4F;
  letter-spacing: 1px;
}
.appt-detail-no {
  font-size: 12px;
  color: #A28C7A;
  font-family: monospace;
}
.appt-detail-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background: #FFFFFF;
  border: 1px solid #EFEBE4;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 16px;
}
.appt-detail-table th {
  padding: 10px 16px;
  font-size: 13px;
  font-weight: 600;
  color: #8C6A4F;
  background-color: #FAF6F0;
  border-bottom: 1px solid #EFEBE4;
  border-top: none;
  letter-spacing: 1px;
}
.appt-detail-table td {
  padding: 12px 16px;
  border-bottom: 1px dashed #EFEBE4;
  vertical-align: middle;
  background: #FFFFFF;
}
.appt-detail-table tr:last-child td {
  border-bottom: none;
}
.appt-detail-id {
  font-family: monospace;
  color: #A28C7A;
  font-size: 13px;
}
.appt-detail-service {
  display: flex;
  align-items: center;
}
.appt-detail-badge {
  display: inline-block;
  padding: 2px 6px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
  margin-right: 8px;
  line-height: 1.4;
}
.badge-pkg {
  background: #FAF1E6;
  color: #B58A63;
}
.badge-bsc {
  background: #F4EFEA;
  color: #8C6A4F;
}
.appt-detail-service-name {
  font-weight: 600;
  color: #3D2B1F;
}
.appt-detail-price {
  text-align: right;
  color: #A28C7A;
  font-variant-numeric: tabular-nums;
}
.appt-detail-subtotal {
  text-align: right;
  font-weight: 700;
  color: #B58A63; /* Distinct price/amount color */
  font-size: 15px;
  font-variant-numeric: tabular-nums;
}
.currency {
  font-size: 12px;
  color: #A28C7A;
  margin-right: 2px;
}
.appt-detail-footer {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 14px;
}
.detail-note-box {
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 13px;
  background: #FFFFFF;
  border: 1px solid #EFEBE4;
}
.note-label {
  font-weight: 700;
  color: #8C6A4F;
  letter-spacing: 1px;
}
.note-content {
  color: #5A4A3E;
}
.detail-cancel-box {
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 13px;
  background: #FDF5F5;
  border: 1px solid #FAD1D1;
}
.cancel-label {
  font-weight: 700;
  color: #D38282;
  letter-spacing: 1px;
}
.cancel-content {
  color: #B25E5E;
}
</style>
