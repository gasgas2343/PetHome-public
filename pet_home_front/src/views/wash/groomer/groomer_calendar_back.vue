<template>
  <div class="container page-container mt-4 mb-5">
    <!-- 右側日系現代懸浮指示牌 (玻璃擬態) -->
    <Transition name="fade">
      <div v-if="showFloatingBadge" class="floating-page-indicator">
        <i class="bi bi-paw-fill indicator-icon"></i>
        <span>已預約行事曆</span>
      </div>
    </Transition>

    <div class="text-center mb-5">
      <h1 class="page-title mb-2"><i class="bi bi-calendar2-week me-2"></i>已預約行事曆</h1>
      <div class="text-muted fw-light" style="letter-spacing: 3px; font-size: 0.85rem; color: #644686 !important;">GROOMER SCHEDULE & CALENDAR</div>
    </div>

    <!-- 查詢條件與操作列 -->
    <div class="filter-card mb-5 accent-top">
      <div class="row g-3 align-items-center justify-content-center">
        <div class="col-12 col-md-auto d-flex align-items-center">
          <label class="col-form-label text-nowrap me-2 mb-0 fw-bold" style="color: #644686;"><i class="bi bi-calendar-date me-1"></i>日期：</label>
          <input type="date" class="form-control flex-grow-1" v-model="workDate" :min="minAllowedDate" :max="maxAllowedDate" style="min-width: 200px;">
        </div>

        <div class="col-12 col-md-auto d-flex align-items-center">
          <label class="col-form-label text-nowrap me-2 mb-0 fw-bold" style="color: #644686;"><i class="bi bi-calendar-month me-1"></i>月份：</label>
          <select class="form-select flex-grow-1" v-model="selectedMonthQuery" style="min-width: 200px;">
            <option :value="null">全部</option>
            <option v-for="m in availableMonths" :key="m" :value="m">{{ m }}</option>
          </select>
        </div>

        <div class="col-12 col-md-auto d-flex align-items-center">
          <label class="col-form-label text-nowrap me-2 mb-0 fw-bold" style="color: #644686;"><i class="bi bi-clock-history me-1"></i>時段：</label>
          <select class="form-select flex-grow-1" v-model.number="selectedSlotQuery" style="min-width: 200px;">
            <option :value="null">請選擇時段 (不限)</option>
            <option v-for="(time, index) in slotTimeMap" :key="index" :value="Number(index)">
              {{ index }} ({{ time.start }}~{{ time.end }})
            </option>
          </select>
        </div>

        <div class="col-12 col-md-auto d-flex align-items-center">
          <label class="col-form-label text-nowrap me-2 mb-0 fw-bold" style="color: #644686;"><i class="bi bi-receipt me-1"></i>預約單ID：</label>
          <input type="text" class="form-control flex-grow-1" v-model="apptOrderIdQuery" placeholder="請輸入預約單 ID" style="min-width: 200px;">
        </div>

        <div class="col-12 d-flex gap-3 flex-wrap justify-content-center mt-4">
          <button class="btn btn-primary px-4 py-2 flex-grow-1 flex-md-grow-0 rounded-pill shadow-sm" @click="handleUnifiedSearch"><i class="bi bi-search me-1"></i> 查詢</button>
          <button class="btn btn-secondary px-4 py-2 flex-grow-1 flex-md-grow-0 rounded-pill shadow-sm" @click="clearFilters"><i class="bi bi-eraser me-1"></i> 清除 </button>
        </div>
      </div>
    </div>

    <!--查詢特定單日：依據上午/下午進行迴圈分組顯示-->
    <div class="schedule-container mb-4" v-if="currentViewMode === 'day'">
      <h3 class="text-center mb-4 date-title"><i class="bi bi-calendar-check me-2"></i>預約日期：{{ workDate }}</h3>
      
      <div class="row g-4">
        <div v-for="period in groupedDaySchedules" :key="period.periodOfDayId" class="col-12 col-md-6 period-section">
          <div class="card h-100 shadow-sm border-0 period-card">
            
            <!-- 顯示：上午班 / 下午班 -->
            <div class="card-header bg-transparent border-0 pt-4 pb-2 d-flex justify-content-center align-items-center gap-3">
              <h4 style="color: #644686; font-weight: bold; margin-bottom: 0;">{{ period.periodName }}</h4>
            </div>
            
            <!-- 顯示該班別底下的所有可用時間按鈕 -->
            <div class="card-body">
              <div class="time-slots">
                <button 
                  v-for="slot in period.schedules" 
                  :key="slot.scheduleId || 'slot-' + slot.slotIndex"
                  class="btn d-flex flex-column align-items-center justify-content-center slot-button gap-2 py-3"
                  :class="getSlotInfo(slot, period.isPeriodOpen).btnClass"
                  data-bs-toggle="modal"
                  :data-bs-target="getSlotInfo(slot, period.isPeriodOpen).status === 'unbooked' ? '' : (slot.scheduleId ? '#editScheduleModal' : (slot.isPeriodOpen ? '#addScheduleModal' : ''))"
                  @click="getSlotInfo(slot, period.isPeriodOpen).status === 'unbooked' ? null : ((slot.scheduleId || slot.isPeriodOpen) ? (slot.scheduleId ? setEditData(slot) : prepAdd(slot.workDate, slot.slotIndex, period.periodOfDayId)) : null)"
                  :disabled="getSlotInfo(slot, period.isPeriodOpen).status === 'unbooked' || (!slot.scheduleId && !slot.isPeriodOpen)"
                >
                  <span class="fw-bold fs-5" style="letter-spacing: 1px;">{{ slot.time.split('~')[0] }}</span>
                  <span class="badge bg-light text-dark w-100 rounded-pill shadow-sm slot-badge text-truncate py-2">
                     {{ getSlotInfo(slot, period.isPeriodOpen).title }}
                  </span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 查詢全部時顯示的清單 (當沒有指定日期時) -->
    <div class="table-card mb-4" v-if="currentViewMode === 'all'">
      <div class="table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th>序號</th>
              <th @click="toggleSort('scheduleId')" class="d-none" style="cursor: pointer;">
                班表ID
                <span v-if="sortColumn === 'scheduleId' && sortDirection === 'desc'" class="text-primary fw-bold">↑</span>
                <span v-else-if="sortColumn === 'scheduleId' && sortDirection === 'asc'" class="text-primary fw-bold">↓</span>
                <span v-else class="text-muted">↕</span>
              </th>
              <th @click="toggleSort('workDate')" style="cursor: pointer;">
                工作日期
                <span v-if="sortColumn === 'workDate' && sortDirection === 'desc'" class="text-primary fw-bold">↑</span>
                <span v-else-if="sortColumn === 'workDate' && sortDirection === 'asc'" class="text-primary fw-bold">↓</span>
                <span v-else class="text-muted">↕</span>
              </th>
              <th @click="toggleSort('slotIndex')" style="cursor: pointer;">
                時段索引
                <span v-if="sortColumn === 'slotIndex' && sortDirection === 'desc'" class="text-primary fw-bold">↑</span>
                <span v-else-if="sortColumn === 'slotIndex' && sortDirection === 'asc'" class="text-primary fw-bold">↓</span>
                <span v-else class="text-muted">↕</span>
              </th>
              <th>狀態</th>
              <th>訂單ID</th>
              <th>備註</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <!-- 載入中 -->
            <tr v-if="loading">
              <td colspan="7" class="text-center">
                <div class="japanese-spinner-loader py-4">
                  <div class="spinner"></div>
                  <div class="loading-text mt-2">資料查詢中...</div>
                </div>
              </td>
            </tr>
            <!-- 無資料 -->
            <tr v-else-if="schedules.length === 0">
          <td colspan="7" class="text-center py-5 text-muted">
            <i class="bi bi-inbox fs-2 d-block mb-2 opacity-50"></i>
            <span class="letter-spacing-1">這天沒有班表資料</span>
          </td>
            </tr>
            <!-- 資料 -->
            <tr
              v-for="(schedule, index) in displayedSchedules"
              :key="schedule.scheduleId ? 'id-' + schedule.scheduleId : 'slot-' + schedule.slotIndex"
            >
              <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td class="d-none">{{ schedule.scheduleId || '-' }}</td>
              <td>{{ schedule.workDate }}</td>
              <td>
                {{ schedule.slotIndex }}
                <span v-if="getSlotTimeRange(schedule.slotIndex)" class="text-muted ms-1">
                  ({{ getSlotTimeRange(schedule.slotIndex) }})
                </span>
              </td>
              <td v-if="schedule.scheduleId">
                <span :class="getTableSlotStatus(schedule).class">
                  {{ getTableSlotStatus(schedule).text }}
                </span>
              </td>
              <td v-else class="text-muted">-</td>
              <td>{{ schedule.apptOrderId || '-' }}</td>
              <td>{{ schedule.note || '-' }}</td>
              <td>
                <template v-if="schedule.scheduleId">
                  <button class="btn btn-sm btn-outline-primary me-1" data-bs-toggle="modal" data-bs-target="#editScheduleModal" @click="setEditData(schedule)"><i class="bi bi-pencil-square"></i> 編輯</button>
                  <button class="btn btn-sm btn-outline-danger" @click="deleteSchedule(schedule)"><i class="bi bi-trash3"></i> 刪除</button>
                </template>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分頁區塊 -->
      <nav v-if="totalPages > 1" aria-label="Page navigation" class="mt-3">
        <ul class="pagination justify-content-center">
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

    <!-- 行事曆區塊 -->
    <div class="calendar-wrapper">
      <div class="d-flex flex-wrap gap-3 align-items-center mb-3 pb-3 border-bottom">
        <span class="fw-bold" style="color: #644686;"><i class="bi bi-funnel me-1"></i>行事曆顯示：</span>
        <div class="form-check mb-0">
          <input class="form-check-input" type="checkbox" id="filterAvailable" v-model="filterAvailable">
          <label class="form-check-label d-flex align-items-center" for="filterAvailable">
            <span class="status-dot-available me-2"></span>開放中
          </label>
        </div>
        <div class="form-check mb-0">
          <input class="form-check-input" type="checkbox" id="filterUnavailable" v-model="filterUnavailable">
          <label class="form-check-label d-flex align-items-center" for="filterUnavailable">
            <span class="status-dot-unavailable me-2"></span>不開放
          </label>
        </div>
        <div class="form-check mb-0">
          <input class="form-check-input" type="checkbox" id="filterBooked" v-model="filterBooked">
          <label class="form-check-label d-flex align-items-center" for="filterBooked">
            <span class="status-dot-booked me-2"></span>已預定
          </label>
        </div>
        <div class="form-check mb-0">
          <input class="form-check-input" type="checkbox" id="filterUnset" v-model="filterUnset">
          <label class="form-check-label d-flex align-items-center" for="filterUnset">
            <span class="status-dot-unset me-2"></span>未設定
          </label>
        </div>
        <div class="form-check mb-0">
          <input class="form-check-input" type="checkbox" id="filterUnbooked" v-model="filterUnbooked">
          <label class="form-check-label d-flex align-items-center" for="filterUnbooked">
            <span class="status-dot-unbooked me-2"></span>未預定
          </label>
        </div>
      </div>
      <FullCalendar ref="fullCalendarRef" :options="calendarOptions" />
    </div>

    <!-- 新增班表 Modal -->
    <div class="modal fade" id="addScheduleModal" tabindex="-1" aria-labelledby="addScheduleModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="addScheduleModalLabel">設定時段狀態</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="addSchedule">
              <div class="mb-3">
                <label class="form-label">工作日期</label>
                <input type="date" class="form-control" v-model="newSchedule.workDate" required readonly>
              </div>
              <div class="mb-3">
                <label class="form-label">時段</label>
                <select class="form-select" v-model.number="newSchedule.slotIndex" required>
                  <option :value="null" disabled>請選擇時段索引</option>
                  <option v-for="(time, index) in slotTimeMap" :key="index" :value="Number(index)">
                    {{ index }} ({{ time.start }}~{{ time.end }})
                  </option>
                </select>
              </div>
              <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" v-model="newSchedule.isAvailable" id="isAvailableCheck">
                <label class="form-check-label" for="isAvailableCheck">開放預約 (取消勾選表示請假/已滿)</label>
              </div>
              <div class="mb-3">
                <label class="form-label">訂單編號 (apptOrderId)</label>
                <input type="text" class="form-control" v-model="newSchedule.apptOrderId" placeholder="若有預約請輸入訂單編號">
              </div>
              <div class="mb-3">
                <label class="form-label">備註 (note)</label>
                <textarea class="form-control" v-model="newSchedule.note" placeholder="可填寫請假事由或保留原因"></textarea>
              </div>
              <div class="text-end">
                <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">取消</button>
                <button type="submit" class="btn btn-primary" :disabled="isSubmitting">儲存設定</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- 編輯班表 Modal -->
    <div class="modal fade" id="editScheduleModal" tabindex="-1" aria-labelledby="editScheduleModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="editScheduleModalLabel">編輯時段狀態</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="submitEdit">
              <div class="mb-3">
                <label class="form-label">工作日期</label>
                <input type="date" class="form-control" v-model="editData.workDate" required readonly>
              </div>
              <div class="mb-3">
                <label class="form-label">時段</label>
                <select class="form-select" v-model.number="editData.slotIndex" required>
                  <option :value="null" disabled>請選擇時段索引</option>
                  <option v-for="(time, index) in slotTimeMap" :key="index" :value="Number(index)">
                    {{ index }} ({{ time.start }}~{{ time.end }})
                  </option>
                </select>
              </div>
              <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" v-model="editData.isAvailable" id="editIsAvailableCheck">
                <label class="form-check-label" for="editIsAvailableCheck">開放預約 (取消勾選表示請假/已滿)</label>
              </div>
              <div class="mb-3">
                <label class="form-label">訂單編號 (apptOrderId)</label>
                <input type="text" class="form-control" v-model="editData.apptOrderId" placeholder="若有預約請輸入訂單編號">
              </div>
              <div class="mb-3">
                <label class="form-label">備註 (note)</label>
                <textarea class="form-control" v-model="editData.note" placeholder="可填寫請假事由或保留原因"></textarea>
              </div>
              <div class="text-end">
                <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">取消</button>
                <button type="submit" class="btn btn-primary" :disabled="isSubmitting">儲存設定</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>



    <!-- 隱藏的按鈕，供行事曆點擊具體時段時觸發 Modal 使用 -->
    <button type="button" id="hiddenAddBtn" class="d-none" data-bs-toggle="modal" data-bs-target="#addScheduleModal"></button>
    <button type="button" id="hiddenEditBtn" class="d-none" data-bs-toggle="modal" data-bs-target="#editScheduleModal"></button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from "vue";
import Swal from "sweetalert2";
import FullCalendar from '@fullcalendar/vue3';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import zhTwLocale from '@fullcalendar/core/locales/zh-tw';
import axiosapi from '@/plugins/axios.js';
import { useUserStore } from '@/stores/user.js';

const userStore = useUserStore();
const schedules = ref([]);
const loading = ref(false);
const adminCalendars = ref([]); // 記錄管理員營業日資料
const minAllowedDate = ref(''); // 限制搜尋欄可選最小日期
const maxAllowedDate = ref(''); // 限制搜尋欄可選最大日期

const today = new Date();
const currentMonthStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`;
const currentDateStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
const workDate = ref(currentDateStr); // 預設帶入今天的日期
const selectedMonthQuery = ref(null);
const allSchedules = ref([]); // 保存當月全部資料以便用於行事曆過濾計算

const availableMonths = computed(() => {
  const months = new Set();
  
  // 永遠顯示當月與前一月份
  months.add(currentMonthStr);
  const prev = new Date(today.getFullYear(), today.getMonth() - 1, 1);
  const prevMonthStr = `${prev.getFullYear()}-${String(prev.getMonth() + 1).padStart(2, '0')}`;
  months.add(prevMonthStr);

  adminCalendars.value.forEach(wc => {
    if (wc.workDate) months.add(wc.workDate.substring(0, 7));
  });
  return Array.from(months).sort();
});

const apptOrderIdQuery = ref('');

watch(selectedMonthQuery, (newVal) => {
  if (newVal) {
    workDate.value = '';
    apptOrderIdQuery.value = '';
  }
});
watch(workDate, (newVal) => {
  if (newVal) {
    selectedMonthQuery.value = null;
    apptOrderIdQuery.value = '';
  }
});
watch(apptOrderIdQuery, (newVal) => {
  if (newVal) {
    workDate.value = '';
    selectedMonthQuery.value = null;
  }
});

const currentViewMode = ref('day'); // 紀錄目前的查詢模式 ('day' 或 'all')

// 新增班表用到的資料與狀態
const isSubmitting = ref(false);
const newSchedule = ref({
  workDate: "",
  slotIndex: null,
  isAvailable: true,
  apptOrderId: null,
  periodOfDayId: null,
  note: "",
  isAutoGenerated: true
});

// 假別對照表 (對應管理端 WashWorkCalendar)
const dayTypeMap = {
  0: '正常上班',
  1: '例假',
  2: '特休',
  3: '臨時公休'
};

// 編輯班表用到的資料
const editData = ref({
  scheduleId: null,
  workDate: "",
  slotIndex: null,
  isAvailable: true,
  apptOrderId: null,
  periodOfDayId: null,
  note: "",
  isAutoGenerated: true
});

const setEditData = (schedule) => {
  editData.value = { ...schedule };
};



// 時段對照表
const slotTimeMap = {
  1: { start: '09:00:00', end: '10:00:00' },
  2: { start: '10:00:00', end: '11:00:00' },
  3: { start: '11:00:00', end: '12:00:00' },
  4: { start: '12:00:00', end: '13:00:00' },
  5: { start: '14:00:00', end: '15:00:00' },
  6: { start: '15:00:00', end: '16:00:00' },
  7: { start: '16:00:00', end: '17:00:00' },
  8: { start: '17:00:00', end: '18:00:00' }
};

const getSlotTimeRange = (index) => {
  const slot = slotTimeMap[index];
  return slot ? `${slot.start}~${slot.end}` : '';
};

const normalizeApptOrderId = (value) => {
  if (value === null || value === undefined) return '';
  const normalized = String(value).trim();
  return normalized.toLowerCase() === 'null' ? '' : normalized;
};

const hasApptOrderId = (value) => normalizeApptOrderId(value) !== '';

// 當點擊任一 "新增" 按鈕時，準備要帶入的預設值
const prepAdd = (date, index = null, periodId = null) => {
  newSchedule.value.workDate = date || workDate.value;
  newSchedule.value.slotIndex = index;
  newSchedule.value.isAvailable = true;
  newSchedule.value.apptOrderId = "";
  newSchedule.value.note = "";
  if (periodId) {
    newSchedule.value.periodOfDayId = periodId;
  } else if (index) {
    newSchedule.value.periodOfDayId = index <= 4 ? 1 : 2;
  }
};

// 用於構建單日模式下的上午/下午分組資料
const groupedDaySchedules = computed(() => {
  if (currentViewMode.value !== 'day') return [];
  
  const currentAdminCal = adminCalendars.value.find(c => c.workDate === workDate.value);
  const isMorningOpen = !!(currentAdminCal?.isWorkday && currentAdminCal?.periods?.some(p => p.periodOfDayId === 1 && p.isActive));
  const isAfternoonOpen = !!(currentAdminCal?.isWorkday && currentAdminCal?.periods?.some(p => p.periodOfDayId === 2 && p.isActive));

  const groups = [
    { periodOfDayId: 1, periodName: '上午班', schedules: [], isPeriodOpen: isMorningOpen },
    { periodOfDayId: 2, periodName: '下午班', schedules: [], isPeriodOpen: isAfternoonOpen }
  ];
  
  for (let i = 1; i <= 8; i++) {
    const periodId = i <= 4 ? 1 : 2;
    const found = schedules.value.find(s => s.slotIndex === i);
    const group = groups.find(g => g.periodOfDayId === periodId);
    
    group.schedules.push(found ? { ...found, time: getSlotTimeRange(i) } : { scheduleId: null, workDate: workDate.value, slotIndex: i, periodOfDayId: periodId, time: getSlotTimeRange(i) });
  }
  return groups;
});

const getSlotInfo = (slot, isPeriodOpen) => {
  const dateStr = slot.workDate;
  const slotIndex = slot.slotIndex;
  const apptOrderId = normalizeApptOrderId(slot.apptOrderId);
  
  if (apptOrderId) {
    return {
      title: `[已預約] 訂單:${apptOrderId}`,
      btnClass: 'booked-slot text-dark shadow-sm'
    };
  }
  
  // 檢查該大時段是否有預約
  const daySlots = allSchedules.value.filter(s => s.workDate === dateStr);
  const isMorning = slotIndex <= 4;
  const periodHasBooked = daySlots.some(s => {
    const sIsMorning = s.slotIndex <= 4;
    return sIsMorning === isMorning && normalizeApptOrderId(s.apptOrderId) !== '';
  });
  
  if (periodHasBooked) {
    return {
      title: '未預定',
      btnClass: 'unbooked-slot'
    };
  } else {
    if (slot.scheduleId) {
      if (slot.isAvailable) {
        return {
          title: '開放中',
          btnClass: 'available-slot btn-success text-white shadow-sm'
        };
      } else {
        return {
          title: '請假/休假',
          btnClass: 'unavailable-slot btn-danger text-white opacity-75'
        };
      }
    } else {
      if (isPeriodOpen) {
        return {
          title: '開放中',
          btnClass: 'open-slot btn-outline-success border-dashed fw-bold'
        };
      } else {
        return {
          title: '未開班',
          btnClass: 'closed-slot btn-outline-secondary border-dashed opacity-50'
        };
      }
    }
  }
};

const getTableSlotStatus = (schedule) => {
  const apptOrderId = normalizeApptOrderId(schedule.apptOrderId);
  if (apptOrderId) {
    return {
      text: '已預定',
      class: 'text-warning fw-bold'
    };
  }
  const daySlots = allSchedules.value.filter(s => s.workDate === schedule.workDate);
  const isMorning = schedule.slotIndex <= 4;
  const periodHasBooked = daySlots.some(s => {
    const sIsMorning = s.slotIndex <= 4;
    return sIsMorning === isMorning && normalizeApptOrderId(s.apptOrderId) !== '';
  });
  
  if (periodHasBooked) {
    return {
      text: '未預定',
      class: 'text-muted fw-bold'
    };
  } else {
    if (schedule.isAvailable) {
      return {
        text: '開放中',
        class: 'text-success fw-bold'
      };
    } else {
      return {
        text: '請假/休假',
        class: 'text-danger fw-bold'
      };
    }
  }
};

// 排序與分頁狀態
const sortColumn = ref('workDate');
const sortDirection = ref('desc');
const currentPage = ref(1);
const pageSize = 10;

const toggleSort = (col) => {
  if (currentViewMode.value === 'day') return; // 單日模式下不進行排序
  if (sortColumn.value === col) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc';
  } else {
    sortColumn.value = col;
    sortDirection.value = 'desc'; // 依照需求，預設點選上箭頭(desc, 大到小)
  }
};

const totalPages = computed(() => {
  if (currentViewMode.value === 'day') return 1;
  return Math.ceil(schedules.value.length / pageSize) || 1;
});

const displayedSchedules = computed(() => {
  let list = [...schedules.value];
  if (currentViewMode.value === 'all') {
    if (sortColumn.value) {
      list.sort((a, b) => {
        let valA = a[sortColumn.value] || '';
        let valB = b[sortColumn.value] || '';
        if (valA < valB) return sortDirection.value === 'asc' ? -1 : 1;
        if (valA > valB) return sortDirection.value === 'asc' ? 1 : -1;
        return 0;
      });
    }
    const start = (currentPage.value - 1) * pageSize;
    return list.slice(start, start + pageSize);
  }
  return list;
});

// 取得 FullCalendar 實體的參考
const fullCalendarRef = ref(null);

// 點擊行事曆日期時，自動帶入該日期並重新查詢
const handleDateClick = (arg) => {
  workDate.value = arg.dateStr;
  handleUnifiedSearch();
};

const handleEventClick = (clickInfo) => {
  const { status, slotIndex, originalData, dayType } = clickInfo.event.extendedProps;
  const dateStr = clickInfo.event.startStr.split('T')[0];

  if (status === 'unset' || (status === 'unavailable' && !slotIndex)) {
    const dayTypeStr = dayTypeMap[dayType] || '休假';
    Swal.fire('休假日/未設定', `該日狀態為 [${dayTypeStr}]，此區域設定由管理員控制。`, 'info');
    return;
  }

  if (slotIndex) {
    if (originalData && originalData.scheduleId) {
        setEditData(originalData);
        document.getElementById('hiddenEditBtn')?.click();
    } else {
        prepAdd(dateStr, slotIndex);
        document.getElementById('hiddenAddBtn')?.click();
    }
  }
};

const calendarOptions = ref({
  plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
  initialView: 'timeGridWeek',
  showNonCurrentDates: false, // 隱藏非當月的日期與事件
  fixedWeekCount: false,      // 讓行事曆根據當月實際週數顯示 (4~5週)，不固定顯示 6 週
  headerToolbar: {
    left: 'prev,next today',
    center: 'title',
    right: 'dayGridMonth,timeGridWeek,timeGridDay'
  },
  dayMaxEvents: true, // 允許出現 "+X more" 連結，避免單日格子過高
  slotMinTime: '08:00:00', // 限制日/週視圖的最早顯示時間，讓畫面更簡潔
  slotMaxTime: '19:00:00', // 限制日/週視圖的最晚顯示時間
  locale: zhTwLocale, // 設定繁體中文
  dateClick: handleDateClick,
  eventClick: handleEventClick,
  events: [],
  displayEventTime: false, // 隱藏預設的時間文字，避免在「月」視圖被截斷
  views: {
    timeGridWeek: {
      dayHeaderContent: (arg) => {
        const month = arg.date.getMonth() + 1;
        const day = arg.date.getDate();
        const weekDays = ['日', '一', '二', '三', '四', '五', '六'];
        const weekDay = weekDays[arg.date.getDay()];
        return `${month}/${day}(${weekDay})`;
      }
    }
  },
  eventContent: (arg) => {
    const { status, timeRange } = arg.event.extendedProps;
    const longTitle = arg.event.title;
    let dotClass = 'status-dot-unset';
    if (status === 'available') dotClass = 'status-dot-available';
    else if (status === 'unavailable') dotClass = 'status-dot-unavailable';
    else if (status === 'booked') dotClass = 'status-dot-booked';
    else if (status === 'unbooked') dotClass = 'status-dot-unbooked';

    // 格式化顯示：若為已預約狀態，簡化顯示「單 #ID」以節省空間避免折行
    let displayTitle = longTitle;
    if (status === 'booked') {
      const match = longTitle.match(/(?:訂單:|#)(\w+)/);
      if (match) {
        displayTitle = `單 #${match[1]}`;
      } else {
        displayTitle = '已預定';
      }
    }

    // 若有具體時段，則呈現縮寫時間 (如 09:00)
    const timeHtml = timeRange ? `<span class="fw-normal me-1" style="font-size: 0.85em;">${timeRange.split('~')[0]}</span>` : '';

    return {
        html: `
            <div class="event-content-wrapper p-1">
              <div class="d-flex align-items-center gap-1" style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap; width: 100%;">
                <span class="${dotClass}" style="flex-shrink: 0;"></span>
                ${timeHtml}
                <span class="fw-bold text-truncate">${displayTitle}</span>
              </div>
            </div>
        `
    }
  },
  eventDidMount: (info) => {
    // 1. 新增浮動顯示訊息 (Tooltip)：將完整文字放入 title 屬性
    info.el.setAttribute('title', info.event.title);
  }
});

const selectedSlotQuery = ref(null); // 紀錄使用者在第二列挑選的時段

// 行事曆顯示狀態控制
const filterAvailable = ref(false);   // 預設顯示開放預約
const filterUnavailable = ref(true); // 預設顯示不開放
const filterBooked = ref(true);      // 預設顯示已預定
const filterUnset = ref(false);      // 預設不顯示未設定
const filterUnbooked = ref(false);    // 預設顯示未預定

watch([filterAvailable, filterUnset, filterUnavailable, filterBooked, filterUnbooked], () => {
  updateCalendar();
});

const updateCalendar = () => {
  const events = [];
  
  if (!minAllowedDate.value || !maxAllowedDate.value) return;
  
  let current = new Date(minAllowedDate.value);
  const end = new Date(maxAllowedDate.value);
  
  while (current <= end) {
    const dateStr = `${current.getFullYear()}-${String(current.getMonth() + 1).padStart(2, '0')}-${String(current.getDate()).padStart(2, '0')}`;
    
    const adminCal = adminCalendars.value.find(c => c.workDate === dateStr);
    
    if (!adminCal) {
      if (filterUnset.value) {
        events.push({ id: dateStr, title: '[未設定]', start: dateStr, classNames: ['custom-event-unset'], allDay: true, extendedProps: { status: 'unset' } });
      }
    } else if (!adminCal.isWorkday) {
      if (filterUnavailable.value) {
        events.push({ id: dateStr, title: `[不開放] ${dayTypeMap[adminCal.dayType] || '休假'}`, start: dateStr, classNames: ['custom-event-unavailable'], allDay: true, extendedProps: { status: 'unavailable', dayType: adminCal?.dayType } });
      }
    } else {
      const isMorningOpen = adminCal.periods?.some(p => p.periodOfDayId === 1 && p.isActive);
      const isAfternoonOpen = adminCal.periods?.some(p => p.periodOfDayId === 2 && p.isActive);
      
      if (!isMorningOpen && !isAfternoonOpen) {
          if (filterUnavailable.value) {
              events.push({ id: dateStr, title: '[不開放] 無營業班別', start: dateStr, classNames: ['custom-event-unavailable'], allDay: true, extendedProps: { status: 'unavailable' } });
          }
      } else {
          // 先篩選出當天的所有時段設定
          const daySlots = allSchedules.value.filter(s => s.workDate === dateStr);
          // 檢查上午時段(1-4)是否有任何一個被預約
          const morningHasBooked = daySlots.some(s => s.slotIndex >= 1 && s.slotIndex <= 4 && normalizeApptOrderId(s.apptOrderId));
          // 檢查下午時段(5-8)是否有任何一個被預約
          const afternoonHasBooked = daySlots.some(s => s.slotIndex >= 5 && s.slotIndex <= 8 && normalizeApptOrderId(s.apptOrderId));

          // 依序產生該日的 8 個預約時段
          for (let i = 1; i <= 8; i++) {
              const isPeriodOpen = i <= 4 ? isMorningOpen : isAfternoonOpen;
              if (!isPeriodOpen) continue; // 此時段未營業

              const groomerSlot = daySlots.find(s => s.slotIndex === i);
              let status = 'available';
              let title = '開放中';
              let classNames = ['custom-event-available'];

              const periodHasBooked = i <= 4 ? morningHasBooked : afternoonHasBooked;
              const apptOrderId = groomerSlot ? normalizeApptOrderId(groomerSlot.apptOrderId) : null;

              if (apptOrderId) {
                status = 'booked';
                title = `[已預約] 訂單:${apptOrderId}`;
                classNames = ['custom-event-booked'];
              } else {
                if (periodHasBooked) {
                  // 有被預約，其他時段顯示「未預定」
                  status = 'unbooked';
                  title = '未預定';
                  classNames = ['custom-event-unbooked'];
                } else {
                  // 無任何預定，依美容師設定顯示「請假/休假」或「開放中」
                  if (groomerSlot && !groomerSlot.isAvailable) {
                    status = 'unavailable';
                    title = '請假/休假';
                    classNames = ['custom-event-unavailable'];
                  } else {
                    status = 'available';
                    title = '開放中';
                    classNames = ['custom-event-available'];
                  }
                }
              }

              if (status === 'available' && !filterAvailable.value) continue;
              if (status === 'unavailable' && !filterUnavailable.value) continue;
              if (status === 'booked' && !filterBooked.value) continue;
              if (status === 'unbooked' && !filterUnbooked.value) continue;

              const slot = slotTimeMap[i];
              events.push({
                  id: `${dateStr}-slot-${i}`,
                  title: title,
                  start: `${dateStr}T${slot.start}`,
                  end: `${dateStr}T${slot.end}`,
                  allDay: false,
                  classNames: classNames,
                  extendedProps: {
                      status: status,
                      slotIndex: i,
                      timeRange: `${slot.start.substring(0, 5)}~${slot.end.substring(0, 5)}`,
                      originalData: groomerSlot
                  }
              });
          }
      }
    }
    
    current.setDate(current.getDate() + 1);
  }
  calendarOptions.value.events = events;
};

const clearFilters = () => {
  workDate.value = currentDateStr;
  selectedSlotQuery.value = null;
  selectedMonthQuery.value = null; // 預設帶回當日
  apptOrderIdQuery.value = '';
  handleUnifiedSearch();
};

const handleUnifiedSearch = async () => {
  loading.value = true;

  try {
    // 同時查詢管理員營業日 (控制行事曆開放月份與特休) 以及 美容師班表 (下方清單)
    const [calendarRes, scheduleRes] = await Promise.all([
      axiosapi.get(`/ajax/pages/WashWorkCalendars/all`, { headers: { "Authorization": `Bearer ${userStore.token}` } }),
      axiosapi.get(`/ajax/pages/WashSchedules/all`, { headers: { "Authorization": `Bearer ${userStore.token}` } })
    ]);

    const workCalData = calendarRes.data;
    const fetchedWorkCalendars = Array.isArray(workCalData) ? workCalData : (workCalData.list || []);
    adminCalendars.value = fetchedWorkCalendars;

    // 計算行事曆可見範圍 (validRange)
    if (fetchedWorkCalendars.length > 0) {
      const dates = fetchedWorkCalendars.map(c => new Date(c.workDate));
      const minD = new Date(Math.min(...dates));
      const maxD = new Date(Math.max(...dates));
      
      const startRange = `${minD.getFullYear()}-${String(minD.getMonth() + 1).padStart(2, '0')}-01`;
      const maxMonthNext = new Date(maxD.getFullYear(), maxD.getMonth() + 1, 1);
      const endRange = `${maxMonthNext.getFullYear()}-${String(maxMonthNext.getMonth() + 1).padStart(2, '0')}-01`;
      calendarOptions.value.validRange = { start: startRange, end: endRange };
      
      const lastDayOfMaxMonth = new Date(maxD.getFullYear(), maxD.getMonth() + 1, 0);
      minAllowedDate.value = `${minD.getFullYear()}-${String(minD.getMonth() + 1).padStart(2, '0')}-01`;
      maxAllowedDate.value = `${lastDayOfMaxMonth.getFullYear()}-${String(lastDayOfMaxMonth.getMonth() + 1).padStart(2, '0')}-${String(lastDayOfMaxMonth.getDate()).padStart(2, '0')}`;
    } else {
      const today = new Date();
      const prev = new Date(today.getFullYear(), today.getMonth() - 1, 1);
      minAllowedDate.value = `${prev.getFullYear()}-${String(prev.getMonth() + 1).padStart(2, '0')}-01`;
      const nextMonth = new Date(today.getFullYear(), today.getMonth() + 2, 0);
      maxAllowedDate.value = `${nextMonth.getFullYear()}-${String(nextMonth.getMonth() + 1).padStart(2, '0')}-${String(nextMonth.getDate()).padStart(2, '0')}`;
    }

    const data = scheduleRes.data;
    let fetchedList = Array.isArray(data) ? data : (data.list || []);
    allSchedules.value = fetchedList; // 保留原始未篩選的列表供行事曆狀態推算

    // 依照前端條件過濾給下方 Table 顯示
    if (selectedMonthQuery.value) {
      fetchedList = fetchedList.filter(s => s.workDate.startsWith(selectedMonthQuery.value));
    }
    if (workDate.value) {
      fetchedList = fetchedList.filter(s => s.workDate === workDate.value);
    }

      if (selectedSlotQuery.value) {
        fetchedList = fetchedList.filter(s => s.slotIndex === selectedSlotQuery.value);
      }

    if (apptOrderIdQuery.value) {
      fetchedList = fetchedList.filter(s => {
        const orderId = normalizeApptOrderId(s.apptOrderId);
        return orderId && orderId.includes(apptOrderIdQuery.value.trim());
      });
    }

    if (!workDate.value) {
      currentViewMode.value = 'all';
      currentPage.value = 1;
      sortColumn.value = 'workDate';
      sortDirection.value = 'desc';
      schedules.value = fetchedList;
      if (selectedMonthQuery.value && fullCalendarRef.value) {
        const api = fullCalendarRef.value.getApi();
        if (api.view.type === 'timeGridWeek' || api.view.type === 'timeGridDay') {
          const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
          if (selectedMonthQuery.value === todayStr.substring(0, 7)) {
            api.gotoDate(todayStr);
          } else {
            api.gotoDate(selectedMonthQuery.value + '-01');
          }
        } else {
          api.gotoDate(selectedMonthQuery.value + '-01');
        }
      }
    } else {
      currentViewMode.value = 'day';
      const dailySchedules = [];
      
      const currentAdminCal = adminCalendars.value.find(c => c.workDate === workDate.value);
      const isMorningOpen = currentAdminCal?.isWorkday && currentAdminCal?.periods?.some(p => p.periodOfDayId === 1 && p.isActive);
      const isAfternoonOpen = currentAdminCal?.isWorkday && currentAdminCal?.periods?.some(p => p.periodOfDayId === 2 && p.isActive);

      for (let i = 1; i <= 8; i++) {
        if (selectedSlotQuery.value && i !== selectedSlotQuery.value) {
          continue;
        }

        const found = fetchedList.find(s => s.slotIndex === i);
        if (found) {
          dailySchedules.push(found);
        } else {
          const isPeriodOpen = i <= 4 ? isMorningOpen : isAfternoonOpen;
          dailySchedules.push({
            scheduleId: null,
            workDate: workDate.value,
            slotIndex: i,
            isAvailable: isPeriodOpen ? true : null,
            isPeriodOpen: isPeriodOpen,
            apptOrderId: null,
            note: ''
          });
        }
      }
      schedules.value = dailySchedules;

      if (fullCalendarRef.value) {
        fullCalendarRef.value.getApi().gotoDate(workDate.value);
      }
    }

    // 更新行事曆 (套用篩選)
    updateCalendar();
  } catch (error) {
    console.error(error);
    Swal.fire("錯誤", "無法取得班表資料", "error");
    schedules.value = [];
    calendarOptions.value.events = [];
  } finally {
    loading.value = false;
  }
};

const addSchedule = async () => {
  // 檢查時段索引是否為空值
  if (newSchedule.value.slotIndex === null) {
    Swal.fire("錯誤", "請選擇一個有效的時段索引。", "error");
    return;
  }

  // 檢查當天當時段是否已有資料
  const isDuplicate = schedules.value.some(
    s => s.scheduleId && s.workDate === newSchedule.value.workDate && s.slotIndex === newSchedule.value.slotIndex
  );

  if (isDuplicate) {
    Swal.fire("錯誤", "資料已存在，請重新選擇預約日期。", "error");
    return;
  }

  if (hasApptOrderId(newSchedule.value.apptOrderId)) {
    newSchedule.value.isAvailable = false;
  } else {
    newSchedule.value.isAvailable = true;
  }

  isSubmitting.value = true;
  
  // 確保寫入期間欄位
  const payload = { ...newSchedule.value, periodOfDayId: newSchedule.value.slotIndex <= 4 ? 1 : 2 };

  try {
    const response = await axiosapi.post('/ajax/pages/WashSchedules', payload, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    });

    // 檢查 HTTP 200 但帶有錯誤狀態的後端回應
    if (response.data && response.data.success === false) {
      Swal.fire("錯誤", response.data.message || "資料已存在或無法新增", "error");
      return;
    }

    Swal.fire("新增成功", `${newSchedule.value.workDate} [時段 ${newSchedule.value.slotIndex}] 新增班表成功`, "success");

    // 模擬點擊關閉按鈕來關閉 Modal 視窗
    document.querySelector('#addScheduleModal .btn-close')?.click();

    // 新增成功後，自動重新查詢所有班表資料以更新畫面
    handleUnifiedSearch();

    // 重置表單狀態
    newSchedule.value = {
      workDate: "",
      slotIndex: null,
      isAvailable: true,
      periodOfDayId: null,
      apptOrderId: null,
      note: "",
      isAutoGenerated: true
    };
  } catch (error) {
    console.error(error);
    const msg = error.response?.data?.message || "新增班表失敗，請稍後再試";
    Swal.fire("錯誤", msg, "error");
  } finally {
    isSubmitting.value = false;
  }
};

const submitEdit = async () => {
  // 檢查修改後的日期與時段是否與「其他」已存在的班表衝突
  const isDuplicate = schedules.value.some(
    s => s.scheduleId && 
         s.scheduleId !== editData.value.scheduleId && 
         s.workDate === editData.value.workDate && 
         s.slotIndex === editData.value.slotIndex
  );

  if (isDuplicate) {
    Swal.fire("錯誤", `${editData.value.workDate} [時段 ${editData.value.slotIndex}] 該筆資料已存在，請確認。`, "error");
    return;
  }

  // 新增：修改前的二次確認提示窗
  const timeRange = getSlotTimeRange(editData.value.slotIndex);
  const result = await Swal.fire({
    title: '確定要修改嗎？',
    html: `即將修改為 <b>${editData.value.workDate}</b> [時段 ${editData.value.slotIndex}] (${timeRange})<br>確定要儲存變更嗎？`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '確定修改',
    cancelButtonText: '取消'
  });

  if (!result.isConfirmed) return;

  if (hasApptOrderId(editData.value.apptOrderId)) {
    editData.value.isAvailable = false;
  } else {
    editData.value.isAvailable = true;
  }

  isSubmitting.value = true;
  
  const payload = { ...editData.value, periodOfDayId: editData.value.slotIndex <= 4 ? 1 : 2 };

  try {
    const response = await axiosapi.put(`/ajax/pages/WashSchedules/${editData.value.scheduleId}`, payload, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    });

    if (response.data && response.data.success === false) {
      Swal.fire("錯誤", response.data.message || "修改失敗", "error");
      return;
    }

    Swal.fire("修改成功", `${editData.value.workDate} [時段 ${editData.value.slotIndex}] 修改班表成功`, "success");
    document.querySelector('#editScheduleModal .btn-close')?.click();

    handleUnifiedSearch();
  } catch (error) {
    console.error(error);
    // 解析後端傳回的 4xx 錯誤訊息
    const msg = error.response?.data?.message || `${editData.value.workDate} [時段 ${editData.value.slotIndex}] 修改班表失敗，請稍後再試`;
    Swal.fire("錯誤", msg, "error");
  } finally {
    isSubmitting.value = false;
  }
};



const deleteSchedule = async (schedule) => {
  const timeRange = getSlotTimeRange(schedule.slotIndex);
  const result = await Swal.fire({
    title: '確定要刪除嗎？',
    html: `即將刪除 <b>${schedule.workDate}</b> [時段 ${schedule.slotIndex}] (${timeRange})<br>刪除後將無法復原！`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '確定刪除',
    cancelButtonText: '取消'
  });

  if (result.isConfirmed) {
    try {
      const response = await axiosapi.delete(`/ajax/pages/WashSchedules/${schedule.scheduleId}`, {
        headers: { "Authorization": `Bearer ${userStore.token}` }
      });
      
      if (response.data && response.data.success === false) {
        Swal.fire("錯誤", response.data.message || "刪除失敗", "error");
        return;
      }
      
      Swal.fire("刪除成功", `${schedule.workDate} [時段 ${schedule.slotIndex}] 班表已刪除`, "success");
      
      handleUnifiedSearch();
    } catch(error) {
      console.error(error);
      const msg = error.response?.data?.message || `${schedule.workDate} [時段 ${schedule.slotIndex}] 刪除失敗，請稍後再試`;
      Swal.fire("錯誤", msg, "error");
    }
  }
};

const showFloatingBadge = ref(false);
const handleScroll = () => {
  showFloatingBadge.value = window.scrollY > 150;
};

onMounted(() => {
  handleUnifiedSearch();
  window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style scoped>
.page-container {
  max-width: 1600px !important;
  width: 95% !important;
  margin-left: auto !important;
  margin-right: auto !important;
  background: 
    /* 第一層：細緻可愛的香檳金圓點紋路 */
    radial-gradient(circle, rgba(140, 106, 79, 0.07) 1.5px, transparent 1.5px) 0 0 / 24px 24px,
    radial-gradient(circle, rgba(140, 106, 79, 0.07) 1.5px, transparent 1.5px) 12px 12px / 24px 24px,
    /* 第二層：沉穩舒壓莫蘭迪綠灰漸變 */
    linear-gradient(135deg, #E2E7E3 0%, #C9D2CB 100%);
  border-radius: 24px;
  padding: 32px;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.4), 0 12px 40px rgba(75, 110, 87, 0.05);
}

.accent-top {
  border-top: 5px solid #8C6A4F !important;
  border-radius: 20px;
}

.letter-spacing-1 { letter-spacing: 1px; }

.page-title {
  color: #8C6A4F; /* 質感焦糖棕 */
  font-weight: 800;
}

.date-title { color: #8C6A4F; font-weight: 700; font-size: 1.6rem; text-shadow: 1px 1px 0px rgba(255,255,255,0.8); }

/* 圓角卡片與柔和陰影 */
.filter-card, .table-card, .calendar-wrapper {
  background: rgba(255, 255, 255, 0.95); /* 半透明白底，輕量感 */
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(140, 106, 79, 0.03);
  padding: 28px;
  border: none;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}
.filter-card:hover, .table-card:hover, .calendar-wrapper:hover {
  box-shadow: 0 15px 35px rgba(140, 106, 79, 0.06);
  transform: translateY(-3px); /* 優雅上浮 */
}

/* 覆寫 Bootstrap 文字顏色 */
.text-primary { color: #8C6A4F !important; }
.text-success { color: #4B6E57 !important; } /* 柔和莫蘭迪綠 */
.text-danger  { color: #C26B70 !important; } /* 柔和玫瑰紅 */

/* 按鈕輕量化、金棕主色調 */
.btn {
  border-radius: 12px;
  font-weight: 600; /* 加粗提升清晰度 */
  border: none;
  transition: all 0.3s ease;
}
.btn-primary {
  background-color: #B58A63; /* 質感金沙棕 */
  color: white;
  box-shadow: 0 4px 12px rgba(181, 138, 99, 0.2);
}
.btn-primary:hover {
  background-color: #A07853;
  color: white;
  box-shadow: 0 6px 15px rgba(181, 138, 99, 0.3);
}
.btn-secondary {
  background-color: #F0E8DF; /* 溫潤淺褐 */
  color: #8C6A4F;
}
.btn-secondary:hover {
  background-color: #E6DAD0;
  color: #785A41;
}
.btn-success {
  background-color: #55725F; /* 莫蘭迪深綠 */
  color: white;
  box-shadow: 0 4px 10px rgba(85, 114, 95, 0.2);
}
.btn-success:hover {
  background-color: #465E4E;
  color: white;
}

/* 無邊框 Outline 按鈕變柔和底色 */
.btn-outline-primary {
  background-color: rgba(140, 106, 79, 0.06);
  color: #8C6A4F;
}
.btn-outline-primary:hover {
  background-color: #8C6A4F;
  color: white;
}
.btn-outline-success {
  background-color: #F0F6F2;
  border: 1px dashed #55725F !important;
  color: #55725F;
}
.btn-outline-success:hover {
  background-color: #55725F;
  color: white;
}
.btn-outline-danger {
  background-color: #FDF6F6;
  color: #C26B70;
}
.btn-outline-danger:hover {
  background-color: #C26B70;
  color: white;
}

/* 美容師單日群組排版卡片專用 */
.period-card {
  background: linear-gradient(135deg, #FFFFFF 0%, #FAF8F5 100%);
  border: 1px solid rgba(140, 106, 79, 0.08) !important; /* 邊框極柔和 */
}
.time-slots {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  max-width: 360px;
  margin: 0 auto;
}
.slot-button {
  width: 100%;
  padding: 10px 12px;
  border-radius: 12px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.slot-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(140, 106, 79, 0.08) !important;
}
.available-slot {
  background: linear-gradient(145deg, #6B8E79 0%, #55725F 100%);
}
.unavailable-slot {
  background: linear-gradient(145deg, #C26B70 0%, #A0484C 100%);
  border: none !important;
}
.booked-slot {
  background: #FAF2E6 !important;
  color: #8C6A4F !important;
  border: 1px solid #B58A63 !important;
  box-shadow: 0 4px 10px rgba(181, 138, 99, 0.1) !important;
}
.unbooked-slot {
  background-color: #FAF6F0 !important;
  color: #A89B8F !important;
  border: 1px solid #C2B8AD !important;
}
.open-slot {
  background-color: transparent !important;
}
.closed-slot {
  background-color: transparent !important;
}
.border-dashed {
  border: 1px dashed #8C6A4F;
  background-color: transparent;
  color: #8C6A4F;
}
.slot-badge {
  font-size: 0.75rem;
}

/* 表單輸入框無邊框/輕量感 */
.form-control, .form-select {
  border: 1px solid #EFEAE4;
  border-radius: 12px;
  background-color: #FAF8F5;
  box-shadow: inset 0 2px 4px rgba(0,0,0,0.01);
  padding: 0.5rem 1rem;
  color: #4A3E3D;
}
.form-control:focus, .form-select:focus {
  border-color: #8C6A4F;
  background-color: #FFFFFF;
  box-shadow: 0 0 0 0.25rem rgba(140, 106, 79, 0.12);
}

/* 表格無邊框設計 */
.table {
  --bs-table-striped-bg: #FAF8F5;
  --bs-table-hover-bg: #FAF3EB;
  --bs-table-color: #4A3E3D;
  margin-bottom: 0;
}
.table thead tr {
  background-color: #FAF6F0;
}
.table thead th:first-child { border-top-left-radius: 12px; border-bottom-left-radius: 12px; }
.table thead th:last-child { border-top-right-radius: 12px; border-bottom-right-radius: 12px; }
.table th {
  border-bottom: 2px solid #EFEAE4;
  color: #8C6A4F;
  font-weight: 600;
  border-top: none;
  padding: 12px 16px;
  text-transform: uppercase;
  letter-spacing: 1px;
}
.table td {
  border-bottom: 1px solid #EFEAE4;
  vertical-align: middle;
}
.table tbody tr {
  transition: background-color 0.2s ease;
}

/* 分頁元件 */
.pagination {
  --bs-pagination-color: #8C6A4F;
  --bs-pagination-bg: transparent;
  --bs-pagination-border-width: 0;
  --bs-pagination-hover-color: #785A41;
  --bs-pagination-hover-bg: #FAF6F0;
  --bs-pagination-active-bg: #B58A63; /* 分頁啟動狀態用質感金沙 */
  --bs-pagination-active-color: #FFF;
}
.page-link {
  border-radius: 8px;
  margin: 0 4px;
}

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

/* FullCalendar 輕量化 */
:deep(.fc) {
  --fc-border-color: #EFEAE4;
  --fc-today-bg-color: rgba(140, 106, 79, 0.05); /* 當天柔和背景 */
  --fc-neutral-bg-color: transparent;
}
:deep(.fc-theme-standard td), :deep(.fc-theme-standard th) {
  border-color: #EFEAE4;
}
:deep(.fc-day-other), :deep(.fc-day-disabled) {
  background-color: transparent !important;
}
:deep(.fc-toolbar-title) {
  color: #8C6A4F !important;
  font-weight: 700 !important;
  letter-spacing: 0.5px;
}
:deep(.fc-col-header-cell-cushion), :deep(.fc-daygrid-day-number), :deep(.fc-timegrid-slot-label-cushion) {
  color: #8C6A4F !important;
  font-weight: 600;
  text-decoration: none !important; /* 移除行事曆上方文字的底線 */
}
:deep(.fc .fc-button-primary) {
  background-color: #8C6A4F;
  border-color: #8C6A4F;
  border-radius: 8px;
  box-shadow: none !important;
}
:deep(.fc .fc-button-primary:hover) {
  background-color: #785A41;
  border-color: #785A41;
}
:deep(.fc .fc-button-primary:not(:disabled).fc-button-active) {
  background-color: #634B38;
  border-color: #634B38;
}

/* 隱藏/柔化 Week/Day 視圖的網格橫線，避免「黑線」感 */
:deep(.fc-theme-standard .fc-timegrid-slot-lane) {
  border-bottom: 1px solid #EFEAE4 !important;
}
:deep(.fc-timegrid-slot-minor) {
  border-top-style: none !important;
}
:deep(.fc-event:focus), :deep(.fc-event:active) {
  outline: none !important; /* 確保點擊時不會有黑框 */
}

/* FullCalendar 行事曆內部事件 - 圓角卡片與柔和陰影 */
:deep(.fc-event) {
  border: none !important;
  border-radius: 8px !important;
  padding: 4px 6px;
  margin: 2px 4px !important;
  box-shadow: 0 4px 10px rgba(140, 106, 79, 0.04) !important;
  font-weight: 600;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
:deep(.fc-event) { cursor: pointer; }
:deep(.fc-event:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(140, 106, 79, 0.08) !important;
}
/* 可預約：白底卡片 + 柔和綠文字與邊條 */
:deep(.custom-event-available) {
  background-color: #FFFFFF !important;
  color: #55725F !important; 
  border-left: 4px solid #55725F !important;
}
/* 不可預約：柔和紅底卡片 + 柔和紅文字與邊條 */
:deep(.custom-event-unavailable) {
  background-color: #FDF6F6 !important;
  color: #C26B70 !important; 
  border-left: 4px solid #C26B70 !important;
}
/* 未設定：柔和灰底卡片 + 灰棕文字與邊條 */
:deep(.custom-event-unset) {
  background-color: #F7F5F2 !important;
  color: #8A8279 !important;
  border-left: 4px solid #A0968C !important;
}
/* 已預約：金沙沙色底卡片 + 香檳棕高對比文字與邊條 (兩頁面一致) */
:deep(.custom-event-booked) {
  background-color: #FAF2E6 !important;
  color: #8C6A4F !important; 
  border-left: 4px solid #B58A63 !important;
}
/* 未預定：柔和灰沙底卡片 + 灰褐文字與邊條 */
:deep(.custom-event-unbooked) {
  background-color: #FAF6F0 !important;
  color: #A89B8F !important;
  border-left: 4px solid #C2B8AD !important;
}
:deep(.fc-event-main), :deep(.fc-event-title),
:deep(.event-content-wrapper) {
  color: inherit !important; /* 強制繼承我們設定的字體顏色 */
  transition: color 0.3s ease;
  white-space: nowrap !important; /* 避免文字折行，改為單行 */
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
}
:deep(.event-text-short) {
  display: none;
}
:deep(.status-dot-available), :deep(.status-dot-unavailable), :deep(.status-dot-unset), :deep(.status-dot-booked), :deep(.status-dot-unbooked) {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
}
:deep(.status-dot-available) {
  background-color: #55725F;
}
:deep(.status-dot-unavailable) {
  background-color: #C26B70;
}
:deep(.status-dot-unset) {
  background-color: #8A8279;
}
:deep(.status-dot-booked) {
  background-color: #B58A63;
}
:deep(.status-dot-unbooked) {
  background-color: #C2B8AD;
}
/* 滑鼠移入該預約資料時，時段文字自動改變顏色對應選擇的時段 */
:deep(.custom-event-available:hover) .fc-event-main,
:deep(.custom-event-available:hover) .event-content-wrapper {
  color: #465E4E !important;
}
:deep(.custom-event-unavailable:hover) .fc-event-main,
:deep(.custom-event-unavailable:hover) .event-content-wrapper {
  color: #A0484C !important;
}
:deep(.custom-event-booked:hover) {
  background-color: #F3E5D0 !important;
}
:deep(.custom-event-booked:hover) .fc-event-main,
:deep(.custom-event-booked:hover) .event-content-wrapper {
  color: #6D4F35 !important;
}
:deep(.custom-event-unbooked:hover) {
  background-color: #FAF3EB !important;
}
:deep(.custom-event-unbooked:hover) .fc-event-main,
:deep(.custom-event-unbooked:hover) .event-content-wrapper {
  color: #8C7F72 !important;
}

/* SweetAlert2 彈出視窗金棕色系與輕量化設計 */
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
  background-color: #B58A63 !important; /* 質感金沙棕 */
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
  color: #4A3E3D !important;
}
/* 調整 SweetAlert2 的圖示顏色以符合整體色彩 */
:global(.swal2-icon.swal2-success) { border-color: #55725F !important; color: #55725F !important; }
:global(.swal2-icon.swal2-success [class^="swal2-success-line"]) { background-color: #55725F !important; }
:global(.swal2-icon.swal2-success .swal2-success-ring) { border-color: rgba(85, 114, 95, 0.3) !important; }
:global(.swal2-icon.swal2-warning) { border-color: #C26B70 !important; color: #C26B70 !important; }
:global(.swal2-icon.swal2-error) { border-color: #C26B70 !important; color: #C26B70 !important; }
:global(.swal2-icon.swal2-error [class^="swal2-x-mark-line"]) { background-color: #C26B70 !important; }

.table-responsive {
  margin-top: 10px;
}

/* RWD 響應式設計 (手機/平板優化) */
@media (max-width: 768px) {
  .page-container {
    padding: 16px;
    border-radius: 16px;
  }
  .filter-card, .table-card, .calendar-wrapper {
    padding: 16px;
    border-radius: 12px;
  }
  .page-title {
    font-size: 1.5rem;
    margin-bottom: 16px;
    text-align: center;
  }
  /* 行事曆頂部工具列手機版換行與置中 */
  :deep(.fc-header-toolbar) {
    flex-direction: column;
    gap: 12px;
  }
  :deep(.fc-toolbar-chunk) {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 8px;
  }
  /* 表格內文不換行，避免資料與按鈕被過度擠壓 */
  .table td, .table th {
    white-space: nowrap;
  }
  /* 放大觸控區域 */
  .btn-sm {
    padding: 0.4rem 0.8rem;
    font-size: 0.9rem;
  }
  /* 手機版統一月/週/日檢視，事件只顯示簡短文字 */
  :deep(.fc-event .event-text-long) {
    display: none !important;
  }
  :deep(.fc-event .event-text-short) {
    display: flex !important;
    align-items: center;
    justify-content: center;
    gap: 4px;
    font-size: 0.8rem; /* 讓小字更適合小空間 */
    padding: 2px 0;
    color: #4A3E3D;
  }
  /* 手機版拔除卡片樣式，改為乾淨的清單感 */
  :deep(.fc-event) {
    background-color: transparent !important;
    border-left: none !important;
    box-shadow: none !important;
    margin: 0 !important;
    padding: 2px !important;
  }
  /* 週/日視圖內部主體也需要去背景，確保顯示純淨文字 */
  :deep(.fc-timegrid-event .fc-event-main) {
    background-color: transparent !important;
    border: none !important;
    box-shadow: none !important;
  }
}

/* 日系簡約旋轉圈圈動畫 */
.japanese-spinner-loader {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid rgba(140, 106, 79, 0.2); /* 淺褐底圈 */
  border-top: 3px solid #8C6A4F; /* 焦糖棕轉動邊 */
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

/* 已預約 (Booked) 狀態 - 沿用香檳金作為跳色，與升級版主色調呼應 */
:deep(.custom-event-booked) {
  background-color: #B58A63 !important;
  color: #FFFFFF !important;
  border-left: 4px solid #A07853 !important;
}
:deep(.custom-event-booked:hover) {
  background-color: #A07853 !important;
}
:deep(.custom-event-booked:hover) .fc-event-main,
:deep(.custom-event-booked:hover) .event-content-wrapper {
  color: #FFFFFF !important;
}
:deep(.status-dot-booked) {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #B58A63; /* 金色代表已預約 */
}
.booked-slot {
  background: linear-gradient(145deg, #B58A63 0%, #A07853 100%) !important;
  color: white !important;
  border: none !important;
  box-shadow: 0 4px 10px rgba(181, 138, 99, 0.2) !important;
}

/* 右側日系現代懸浮指示牌 (玻璃擬態 - Glassmorphism) */
.floating-page-indicator {
  position: fixed;
  right: 20px; /* 懸浮於右側邊緣旁，不緊貼，更具現代空氣感 */
  top: 45%;
  transform: translateY(-50%);
  z-index: 1050;
  
  /* 毛玻璃質感 */
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  color: #4A3E3D;
  
  /* 直立圓角膠囊狀 */
  padding: 24px 12px 20px 12px;
  border-radius: 30px;
  
  /* 極細亮面邊框與潮流雙層投影 */
  border: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: 
    0 8px 32px rgba(140, 106, 79, 0.15),
    inset 0 2px 4px rgba(255, 255, 255, 0.8);
  
  /* 直式文字與排版 */
  writing-mode: vertical-rl;
  text-orientation: mixed;
  font-family: system-ui, -apple-system, sans-serif; /* 現代俐落無襯線體 */
  font-weight: 850;
  font-size: 0.88rem;
  letter-spacing: 5px;
  
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  pointer-events: none;
}

/* 可愛發光小腳印印章 */
.indicator-icon {
  font-size: 1.15rem;
  background: linear-gradient(135deg, #FF9F43 0%, #FF5252 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: iconPulse 2s infinite ease-in-out;
  flex-shrink: 0;
}

@keyframes iconPulse {
  0% { transform: scale(1); filter: drop-shadow(0 0 2px rgba(255, 82, 82, 0.3)); }
  50% { transform: scale(1.15); filter: drop-shadow(0 0 8px rgba(255, 82, 82, 0.7)); }
  100% { transform: scale(1); filter: drop-shadow(0 0 2px rgba(255, 82, 82, 0.3)); }
}

/* 浮現過渡 (從右側滑出) */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.4s cubic-bezier(0.16, 1, 0.3, 1), transform 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translate(40px, -50%) scale(0.95); /* 從右側螢幕外滑入 */
}
</style>
