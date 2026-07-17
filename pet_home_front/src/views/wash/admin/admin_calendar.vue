<template>
  <div class="container page-container mt-4 mb-5">
    <div class="text-center mb-5">
      <h1 class="page-title mb-2"><i class="bi bi-calendar-check me-2"></i>營業日與班表設定</h1>
      <div class="text-muted fw-light" style="letter-spacing: 3px; font-size: 0.85rem; color: #A28C7A !important;">WORKDAYS & SCHEDULE SETTINGS</div>
    </div>

    <!-- 查詢條件與操作列 -->
    <div class="filter-card mb-4 accent-top">
      <div class="table-toolbar mb-0 justify-content-between">
        <div class="d-flex flex-wrap gap-2 align-items-center flex-grow-1">
          <input type="date" class="search-input" v-model="workDate" :min="minAllowedDate" :max="maxAllowedDate" title="依日期篩選" style="width: 150px;">
          
          <select class="filter-select" v-model="selectedMonthQuery">
            <option :value="null">全部月份</option>
            <option v-for="m in availableMonths" :key="m" :value="m">{{ m }}</option>
          </select>
          <select class="filter-select" v-model.number="selectedDayTypeQuery">
            <option :value="null">全部班別/假別</option>
            <option v-for="(label, key) in dayTypeMap" :key="key" :value="Number(key)">
              {{ label }}
            </option>
          </select>

          <button class="btn btn-primary shadow-sm" @click="handleUnifiedSearch">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg> 查詢
          </button>
          <button class="btn btn-secondary shadow-sm" @click="clearFilters">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path><path d="M3 3v5h5"></path></svg> 清除
          </button>
        </div>
        <div class="d-flex flex-wrap gap-2 align-items-center">
          <button class="btn btn-warning text-white shadow-sm" data-bs-toggle="modal" data-bs-target="#batchAddModal">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><line x1="12" y1="8" x2="12" y2="16"></line><line x1="8" y1="12" x2="16" y2="12"></line></svg> 批次新增
          </button>
          <button class="btn btn-danger text-white shadow-sm" data-bs-toggle="modal" data-bs-target="#batchDeleteModal">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"></path><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg> 批次刪除
          </button>
          <button class="btn btn-success shadow-sm" data-bs-toggle="modal" data-bs-target="#addScheduleModal" @click="prepAdd(workDate)">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg> 新增單日
          </button>
        </div>
      </div>
    </div>

    <div class="table-card mb-4">
    <div class="table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th>序號</th>
            <th @click="toggleSort('calendarId')" :style="currentViewMode === 'all' ? 'cursor: pointer;' : ''" class="d-none">
              日曆ID
              <template v-if="currentViewMode === 'all'">
                <span v-if="sortColumn === 'calendarId' && sortDirection === 'desc'" class="text-primary fw-bold">↑</span>
                <span v-else-if="sortColumn === 'calendarId' && sortDirection === 'asc'" class="text-primary fw-bold">↓</span>
                <span v-else class="text-muted">↕</span>
              </template>
            </th>
            <th @click="toggleSort('workDate')" :style="currentViewMode === 'all' ? 'cursor: pointer;' : ''">
              工作日期
              <template v-if="currentViewMode === 'all'">
                <span v-if="sortColumn === 'workDate' && sortDirection === 'desc'" class="text-primary fw-bold">↑</span>
                <span v-else-if="sortColumn === 'workDate' && sortDirection === 'asc'" class="text-primary fw-bold">↓</span>
                <span v-else class="text-muted">↕</span>
              </template>
            </th>
            <th>營業班別</th>
            <th @click="toggleSort('dayType')" :style="currentViewMode === 'all' ? 'cursor: pointer;' : ''">
              類型/假別
              <template v-if="currentViewMode === 'all'">
                <span v-if="sortColumn === 'dayType' && sortDirection === 'desc'" class="text-primary fw-bold">↑</span>
                <span v-else-if="sortColumn === 'dayType' && sortDirection === 'asc'" class="text-primary fw-bold">↓</span>
                <span v-else class="text-muted">↕</span>
              </template>
            </th>
            <th>是否上班</th>
            <th>備註</th>
            <th>操作</th>
          </tr>
        </thead>

        <tbody>
          <!-- 載入中 -->
          <tr v-if="loading">
            <td colspan="8" class="text-center">
              <div class="japanese-spinner-loader py-4">
                <div class="spinner"></div>
                <div class="loading-text mt-2">資料查詢中...</div>
              </div>
            </td>
          </tr>

          <!-- 無資料 -->
          <tr
            v-else-if="schedules.length === 0"
          >
            <td colspan="8" class="text-center py-5 text-muted">
              <i class="bi bi-inbox fs-2 d-block mb-2 opacity-50"></i>
              <span class="letter-spacing-1">這天沒有班表資料</span>
            </td>
          </tr>

          <!-- 資料 -->
          <tr
            v-for="(schedule, index) in displayedSchedules"
            :key="schedule.calendarId ? 'id-' + schedule.calendarId : 'date-' + schedule.workDate"
          >
            <td>{{ currentViewMode === 'all' ? (currentPage - 1) * pageSize + index + 1 : index + 1 }}</td>
            <td class="d-none">{{ schedule.calendarId || '-' }}</td>
            <td>{{ schedule.workDate }}</td>
            <td>
              <span v-if="schedule.periods && schedule.periods.length > 0">
                <span class="badge bg-info text-dark me-1" v-if="schedule.periods.some(p => p.periodOfDayId === 1 && p.isActive)">上午班</span>
                <span class="badge bg-warning text-dark" v-if="schedule.periods.some(p => p.periodOfDayId === 2 && p.isActive)">下午班</span>
              </span>
              <span class="text-muted" v-else>未設定</span>
            </td>
            <td>
              <span v-if="schedule.isWorkday && schedule.dayType != null" class="badge bg-secondary opacity-75">{{ dayTypeMap[schedule.dayType] || '-' }}</span>
              <span v-else class="text-muted">-</span>
            </td>

            <td v-if="schedule.calendarId"
              :class="
                schedule.isWorkday
                  ? (schedule.periods && schedule.periods.some(p => p.isActive)
                      ? 'text-success fw-bold' 
                      : 'text-warning fw-bold')
                  : 'text-danger'
              "
            >
              {{ schedule.isWorkday ? '上班' : '休假' }}
            </td>
            <td v-else class="text-muted fw-bold">未設定</td>

            <td>
              {{ schedule.note || '-' }}
            </td>
            <td class="text-nowrap">
              <div class="actions">
                <template v-if="schedule.calendarId">
                  <button class="btn-icon" title="編輯" data-bs-toggle="modal" data-bs-target="#editScheduleModal" @click.prevent.stop="setEditData(schedule)">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                  </button>
                  <button class="btn-icon btn-icon-danger" title="刪除" @click.prevent.stop="deleteSchedule(schedule)">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"></path><path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                  </button>
                </template>
                <template v-else>
                  <button class="btn-icon btn-icon-success" title="新增" data-bs-toggle="modal" data-bs-target="#addScheduleModal" @click.prevent.stop="prepAdd(schedule.workDate)">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                  </button>
                </template>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分頁區塊 (僅在查詢全部班表且頁數大於 1 時顯示) -->
    <nav v-if="currentViewMode === 'all' && totalPages > 1" aria-label="Page navigation" class="mt-3">
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
        <span class="fw-bold" style="color: #8C6A4F;"><i class="bi bi-funnel me-1"></i>行事曆顯示：</span>
        <div class="form-check mb-0">
          <input class="form-check-input" type="checkbox" id="filterAvailable" v-model="filterAvailable">
          <label class="form-check-label d-flex align-items-center" for="filterAvailable">
            <span class="status-dot-available me-2"></span>開放預約
          </label>
        </div>
        <div class="form-check mb-0">
          <input class="form-check-input" type="checkbox" id="filterUnset" v-model="filterUnset">
          <label class="form-check-label d-flex align-items-center" for="filterUnset">
            <span class="status-dot-unset me-2"></span>未設定
          </label>
        </div>
        <div class="form-check mb-0">
          <input class="form-check-input" type="checkbox" id="filterUnavailable" v-model="filterUnavailable">
          <label class="form-check-label d-flex align-items-center" for="filterUnavailable">
            <span class="status-dot-unavailable me-2"></span>不開放
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
            <h5 class="modal-title" id="addScheduleModalLabel">新增營業日/排班</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="addSchedule">
              <div class="mb-3">
                <label class="form-label">工作日期 (workDate)</label>
                <input type="date" class="form-control" v-model="newSchedule.workDate" required>
              </div>
              <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" v-model="newSchedule.isWorkday" id="isWorkdayCheck">
                <label class="form-check-label" for="isWorkdayCheck">是否為工作日 (isWorkday)</label>
              </div>
              <div class="mb-3" v-if="newSchedule.isWorkday">
                <label class="form-label d-block">班別營業設定</label>
                <div class="form-check form-switch form-check-inline">
                  <input class="form-check-input" type="checkbox" role="switch" id="addMorning" v-model="newSchedule.isMorningActive">
                  <label class="form-check-label" for="addMorning">上午班</label>
                </div>
                <div class="form-check form-switch form-check-inline">
                  <input class="form-check-input" type="checkbox" role="switch" id="addAfternoon" v-model="newSchedule.isAfternoonActive">
                  <label class="form-check-label" for="addAfternoon">下午班</label>
                </div>
              </div>
              <div class="mb-3" v-if="newSchedule.isWorkday">
                <label class="form-label">假別/類型 (dayType)</label>
                <select class="form-select" v-model.number="newSchedule.dayType" required>
                  <option v-for="(label, key) in dayTypeMap" :key="key" :value="Number(key)">
                    {{ label }}
                  </option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label">備註 (note)</label>
                <textarea class="form-control" v-model="newSchedule.note"></textarea>
              </div>
              <div class="text-end">
                <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">取消</button>
                <button type="submit" class="btn btn-primary" :disabled="isSubmitting">確認新增</button>
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
            <h5 class="modal-title" id="editScheduleModalLabel">編輯營業日/排班</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="submitEdit">
              <div class="mb-3">
                <label class="form-label">工作日期 (workDate)</label>
                <input type="date" class="form-control" v-model="editData.workDate" required>
              </div>
              <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" v-model="editData.isWorkday" id="editIsWorkdayCheck">
                <label class="form-check-label" for="editIsWorkdayCheck">是否為工作日 (isWorkday)</label>
              </div>
              <div class="mb-3" v-if="editData.isWorkday">
                <label class="form-label d-block">班別營業設定</label>
                <div class="form-check form-switch form-check-inline">
                  <input class="form-check-input" type="checkbox" role="switch" id="editMorning" v-model="editData.isMorningActive">
                  <label class="form-check-label" for="editMorning">上午班</label>
                </div>
                <div class="form-check form-switch form-check-inline">
                  <input class="form-check-input" type="checkbox" role="switch" id="editAfternoon" v-model="editData.isAfternoonActive">
                  <label class="form-check-label" for="editAfternoon">下午班</label>
                </div>
              </div>
              <div class="mb-3" v-if="editData.isWorkday">
                <label class="form-label">假別/類型 (dayType)</label>
                <select class="form-select" v-model.number="editData.dayType" required>
                  <option v-for="(label, key) in dayTypeMap" :key="key" :value="Number(key)">
                    {{ label }}
                  </option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label">備註 (note)</label>
                <textarea class="form-control" v-model="editData.note"></textarea>
              </div>
              <div class="text-end">
                <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">取消</button>
                <button type="submit" class="btn btn-primary" :disabled="isSubmitting">確認修改</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- 批次新增班表 Modal -->
    <div class="modal fade" id="batchAddModal" tabindex="-1" aria-labelledby="batchAddModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="batchAddModalLabel">批次新增營業日/排班</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="submitBatchAdd">
              <div class="row mb-3">
                <div class="col">
                  <label class="form-label">選擇設定月份</label>
                  <input type="month" class="form-control" v-model="batchData.yearMonth" required>
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label">選擇營業的星期</label>
                <div class="text-muted small mb-2">打勾的星期將設為正常上班(開放)，未打勾的星期將設為例假(不開放)。</div>
                <div class="d-flex flex-wrap gap-3">
                  <div class="form-check" v-for="(dayName, idx) in ['日','一','二','三','四','五','六']" :key="idx">
                    <input class="form-check-input" type="checkbox" :value="idx" v-model="batchData.weekdays" :id="'wd'+idx">
                    <label class="form-check-label" :for="'wd'+idx">週{{ dayName }}</label>
                  </div>
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label d-block">營業日班別設定</label>
                <div class="form-check form-switch form-check-inline">
                  <input class="form-check-input" type="checkbox" role="switch" id="batchMorning" v-model="batchData.isMorningActive">
                  <label class="form-check-label" for="batchMorning">上午班</label>
                </div>
                <div class="form-check form-switch form-check-inline">
                  <input class="form-check-input" type="checkbox" role="switch" id="batchAfternoon" v-model="batchData.isAfternoonActive">
                  <label class="form-check-label" for="batchAfternoon">下午班</label>
                </div>
              </div>
              <div class="text-end">
                <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">取消</button>
                <button type="submit" class="btn btn-warning text-white" :disabled="isSubmitting">確認批次新增</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- 批次刪除班表 Modal -->
    <div class="modal fade" id="batchDeleteModal" tabindex="-1" aria-labelledby="batchDeleteModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="batchDeleteModalLabel">批次刪除營業日</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="submitBatchDelete">
              <div class="mb-3">
                <label class="form-label text-danger fw-bold">選擇欲刪除的月份</label>
                <select class="form-select" v-model="batchDeleteData.yearMonth" required>
                  <option value="" disabled>請選擇月份</option>
                  <option v-for="m in availableMonths" :key="m" :value="m">{{ m }}</option>
                </select>
                <div class="form-text text-muted mt-2">注意：將會刪除該月份「所有」的營業日設定，刪除後無法復原。</div>
              </div>
              <div class="text-end">
                <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">取消</button>
                <button type="submit" class="btn btn-danger text-white" :disabled="isSubmitting">確認批次刪除</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
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
const allCalendars = ref([]); // 儲存無過濾的所有資料，供計算請假天數使用
const minAllowedDate = ref(''); // 限制搜尋欄可選最小日期
const maxAllowedDate = ref(''); // 限制搜尋欄可選最大日期

const today = new Date();
const currentMonthStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`;

const workDate = ref('');
const selectedMonthQuery = ref(currentMonthStr);

const availableMonths = computed(() => {
  const months = new Set();
  
  // 永遠顯示當月與前一月份
  months.add(currentMonthStr);
  const prev = new Date(today.getFullYear(), today.getMonth() - 1, 1);
  const prevMonthStr = `${prev.getFullYear()}-${String(prev.getMonth() + 1).padStart(2, '0')}`;
  months.add(prevMonthStr);

  // 過去的月份若沒有資料則不顯示，有資料自然會透過下面被加入
  allCalendars.value.forEach(wc => {
    if (wc.workDate) months.add(wc.workDate.substring(0, 7));
  });
  return Array.from(months).sort();
});

watch(selectedMonthQuery, (newVal) => {
  if (newVal) workDate.value = '';
});
watch(workDate, (newVal) => {
  if (newVal) selectedMonthQuery.value = null; //789
});

const currentViewMode = ref('day'); // 紀錄目前的查詢模式 ('day' 或 'all')

// 新增班表用到的資料與狀態
const isSubmitting = ref(false);
const newSchedule = ref({
  workDate: "",
  isWorkday: true,
  dayType: 0,
  note: "",
  createdBy: "Admin",
  isMorningActive: true,
  isAfternoonActive: true
});

// 編輯班表用到的資料
const editData = ref({
  calendarId: null,
  workDate: "",
  isWorkday: true,
  dayType: 0,
  note: "",
  createdBy: "Admin",
  isMorningActive: true,
  isAfternoonActive: true
});

// 批次新增用到的資料
const batchData = ref({
  yearMonth: "",
  weekdays: [1, 2, 3, 4, 5, 6], // 預設 1~6 營業，0(週日)店休
  isMorningActive: true,
  isAfternoonActive: true
});

const batchDeleteData = ref({
  yearMonth: ""
});

const setEditData = (schedule) => {
  editData.value = { ...schedule };
  if (schedule.periods && Array.isArray(schedule.periods)) {
    editData.value.isMorningActive = schedule.periods.some(p => p.periodOfDayId === 1 && p.isActive);
    editData.value.isAfternoonActive = schedule.periods.some(p => p.periodOfDayId === 2 && p.isActive);
  } else {
    editData.value.isMorningActive = true;
    editData.value.isAfternoonActive = true;
  }
};

// 假別對照表
const dayTypeMap = {
  0: '正常上班',
  1: '例假',
  2: '特休',
  3: '臨時公休'
};

// 當點擊任一 "新增" 按鈕時，準備要帶入的預設值
const prepAdd = (date) => {
  newSchedule.value.workDate = date || workDate.value;
  newSchedule.value.isWorkday = true;
  newSchedule.value.dayType = 0;
  newSchedule.value.note = "";
  newSchedule.value.isMorningActive = true;
  newSchedule.value.isAfternoonActive = true;
};

// 驗證該月份休假天數限制：例假上限8天、請假/特休等上限4天
const checkLeaveLimits = (yearMonth, addCount1 = 0, addCount3 = 0, excludeId = null) => {
  let count1 = 0, count3 = 0;
  allCalendars.value.forEach(s => {
    if (s.calendarId === excludeId) return;
    if (s.workDate.startsWith(yearMonth)) {
      if (s.dayType === 1) count1++; // 例假
      if (s.dayType === 3) count3++; // 臨時公休
    }
  });
  let warnings = [];
  if (count1 + addCount1 > 8) warnings.push(`公休日(例假)已達 ${count1 + addCount1} 天 (建議上限 8 天)`);
  if (count3 + addCount3 > 4) warnings.push(`請假(臨時公休)已達 ${count3 + addCount3} 天 (建議上限 4 天)`);
  
  return warnings.length > 0 ? warnings.join('<br>') : null;
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

// 關閉 Modal 並移除焦點的 Helper，解決 aria-hidden 警告
const closeBootstrapModal = (modalId) => {
  const closeBtn = document.querySelector(`#${modalId} .btn-close`);
  if (closeBtn) {
    closeBtn.click();
    setTimeout(() => {
      if (document.activeElement) document.activeElement.blur();
    }, 10);
  }
};

// 取得 FullCalendar 實體的參考
const fullCalendarRef = ref(null);


// 點擊行事曆日期時，自動帶入該日期並重新查詢
const handleDateClick = (arg) => {
  workDate.value = arg.dateStr;
  handleUnifiedSearch();
};

const handleEventClick = (clickInfo) => {
  clickInfo.jsEvent.preventDefault(); // 阻止 FullCalendar 內部 a 標籤預設跳轉，解決 Vue Router 誤判跳轉問題

  // 檢查事件是否有綁定 ID
  if (!clickInfo.event.id) {
    return;
  }

  // 如果點擊的是 [未設定]，則自動開啟新增視窗
  if (clickInfo.event.extendedProps.isUnset) {
    prepAdd(clickInfo.event.extendedProps.dateStr);
    document.querySelector('[data-bs-target="#addScheduleModal"]')?.click();
    return;
  }

  // 取出事先保留的完整資料
  const scheduleToDelete = clickInfo.event.extendedProps.originalData;

  if (scheduleToDelete) {
    deleteSchedule(scheduleToDelete);
  } else {
    console.warn('Clicked event on calendar, but could not find matching schedule in local data.', clickInfo.event);
    Swal.fire('操作失敗', '找不到對應的班表資料，請重新整理後再試。', 'error');
  }
};

const calendarOptions = ref({
  plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  showNonCurrentDates: false, // 隱藏非當月的日期與事件
  fixedWeekCount: false,      // 讓行事曆根據當月實際週數顯示 (4~5週)，不固定顯示 6 週
  headerToolbar: {
    left: 'prev,next today',
    center: 'title',
    right: ''
  },
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
    const { isWorkday, isUnset, originalData } = arg.event.extendedProps;
    const longTitle = arg.event.title;
    
    if (isUnset) {
      return {
          html: `
              <div class="event-content-wrapper w-100 d-flex justify-content-center align-items-center" style="min-height: 28px;">
                <svg class="unset-plus-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                  <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/>
                </svg>
              </div>
          `
      }
    }

    let dotClass = isWorkday ? 'status-dot-available' : 'status-dot-unavailable';
    
    let badgesHtml = '';
    if (isWorkday && originalData && originalData.periods) {
      const isMorning = originalData.periods.some(p => p.periodOfDayId === 1 && p.isActive);
      const isAfternoon = originalData.periods.some(p => p.periodOfDayId === 2 && p.isActive);
      
      if (isMorning) badgesHtml += '<span class="badge bg-info text-dark" style="font-size: 0.65rem; padding: 3px 4px; margin-right: 2px;">上午</span>';
      if (isAfternoon) badgesHtml += '<span class="badge bg-warning text-dark" style="font-size: 0.65rem; padding: 3px 4px;">下午</span>';
    }

    return {
        html: `
            <div class="event-content-wrapper p-1">
              <div class="d-flex align-items-center justify-content-between flex-wrap gap-1">
                <div class="d-flex align-items-center gap-1">
                  <span class="${dotClass}"></span>
                  <span class="fw-bold">${longTitle}</span>
                </div>
                ${badgesHtml ? `<div class="d-flex">${badgesHtml}</div>` : ''}
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

const selectedDayTypeQuery = ref(null); // 紀錄使用者在第二列挑選的假別條件

// 行事曆的三種顯示狀態控制
const filterAvailable = ref(true);  // 預設顯示開放預約
const filterUnset = ref(true);      // 預設顯示未設定
const filterUnavailable = ref(true); // 預設顯示不開放

watch([filterAvailable, filterUnset, filterUnavailable], () => {
  updateCalendar(allCalendars.value);
});

const updateCalendar = (scheduleList) => {
  const events = [];
  const dateSet = new Set();

  scheduleList.forEach(schedule => {
    if (!schedule.calendarId) return;
    dateSet.add(schedule.workDate);
    const isWork = schedule.isWorkday;

    if (isWork && filterAvailable.value) {
      const titleStr = dayTypeMap[schedule.dayType] || '正常上班';
      events.push({
        id: String(schedule.calendarId),
        title: `[開放] ${titleStr}`,
        start: schedule.workDate,
        classNames: ['custom-event-available'],
        allDay: true,
        extendedProps: { isWorkday: true, dayType: schedule.dayType, originalData: schedule }
      });
    } else if (!isWork && filterUnavailable.value) {
      const titleStr = dayTypeMap[schedule.dayType] || '休假';
      events.push({
        id: String(schedule.calendarId),
        title: `[不開放] ${titleStr}`,
        start: schedule.workDate,
        classNames: ['custom-event-unavailable'],
        allDay: true,
        extendedProps: { isWorkday: false, dayType: schedule.dayType, originalData: schedule }
      });
    }
  });

  if (filterUnset.value && minAllowedDate.value && maxAllowedDate.value) {
    let current = new Date(minAllowedDate.value);
    const end = new Date(maxAllowedDate.value);
    while (current <= end) {
      const dateStr = `${current.getFullYear()}-${String(current.getMonth() + 1).padStart(2, '0')}-${String(current.getDate()).padStart(2, '0')}`;
      
      if (!dateSet.has(dateStr)) {
        events.push({
          id: 'unset-' + dateStr,
          title: '[未設定]',
          start: dateStr,
          classNames: ['custom-event-unset'],
          allDay: true,
          extendedProps: { isUnset: true, dateStr: dateStr }
        });
      }
      current.setDate(current.getDate() + 1);
    }
  }
  calendarOptions.value.events = events;
};

const clearFilters = () => {
  workDate.value = '';
  selectedDayTypeQuery.value = null;
  selectedMonthQuery.value = currentMonthStr;
  handleUnifiedSearch();
};

const handleUnifiedSearch = async () => {
  loading.value = true;

  try {
    // 若管理端 API 也同樣設定為 /all，請修改路徑如下
    const response = await axiosapi.get(`/ajax/pages/WashWorkCalendars/all`, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    });

    const data = response.data;
    let fetchedList = Array.isArray(data) ? data : (data.list || []);
    allCalendars.value = fetchedList;

    // 動態計算 Input[type=date] 允許的上下限
    if (fetchedList.length > 0) {
      const dates = fetchedList.map(c => new Date(c.workDate));
      const minD = new Date(Math.min(...dates));
      const maxD = new Date(Math.max(...dates));

      // 計算行事曆可見範圍 (validRange)，限制只能查看有資料的月份
      const startRange = `${minD.getFullYear()}-${String(minD.getMonth() + 1).padStart(2, '0')}-01`;
      const maxMonthNext = new Date(maxD.getFullYear(), maxD.getMonth() + 1, 1);
      const endRange = `${maxMonthNext.getFullYear()}-${String(maxMonthNext.getMonth() + 1).padStart(2, '0')}-01`;
      calendarOptions.value.validRange = { start: startRange, end: endRange };

      const lastDayOfMaxMonth = new Date(maxD.getFullYear(), maxD.getMonth() + 1, 0);
      minAllowedDate.value = `${minD.getFullYear()}-${String(minD.getMonth() + 1).padStart(2, '0')}-01`;
      maxAllowedDate.value = `${lastDayOfMaxMonth.getFullYear()}-${String(lastDayOfMaxMonth.getMonth() + 1).padStart(2, '0')}-${String(lastDayOfMaxMonth.getDate()).padStart(2, '0')}`;
    } else {
      const today = new Date();

      // 若完全無資料，行事曆限制僅能查看當月
      const startRange = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-01`;
      const maxMonthNext = new Date(today.getFullYear(), today.getMonth() + 1, 1);
      const endRange = `${maxMonthNext.getFullYear()}-${String(maxMonthNext.getMonth() + 1).padStart(2, '0')}-01`;
      calendarOptions.value.validRange = { start: startRange, end: endRange };

      const prev = new Date(today.getFullYear(), today.getMonth() - 1, 1);
      minAllowedDate.value = `${prev.getFullYear()}-${String(prev.getMonth() + 1).padStart(2, '0')}-01`;
      const nextMonth = new Date(today.getFullYear(), today.getMonth() + 2, 0);
      maxAllowedDate.value = `${nextMonth.getFullYear()}-${String(nextMonth.getMonth() + 1).padStart(2, '0')}-${String(nextMonth.getDate()).padStart(2, '0')}`;
    }

    // 先更新行事曆，不論條件皆顯示整個月的數據，以便使用者預覽
    updateCalendar(fetchedList);

    // 進行本地過濾
    if (selectedMonthQuery.value) {
      fetchedList = fetchedList.filter(s => s.workDate.startsWith(selectedMonthQuery.value));
    }
    if (workDate.value) {
      fetchedList = fetchedList.filter(s => s.workDate === workDate.value);
    }
    if (selectedDayTypeQuery.value !== null) {
      fetchedList = fetchedList.filter(s => s.dayType === selectedDayTypeQuery.value);
    }

    if (!workDate.value) {
      currentViewMode.value = 'all';
      currentPage.value = 1;
      sortColumn.value = 'workDate';
      sortDirection.value = 'desc';
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
      if (fullCalendarRef.value) {
        fullCalendarRef.value.getApi().gotoDate(workDate.value);
      }
    }
    schedules.value = fetchedList;
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
  // 檢查當天是否已有資料 (Database constraints 也有 UQ_WashWorkCalendar_work_date)
  const isDuplicate = schedules.value.some(
    s => s.calendarId && s.workDate === newSchedule.value.workDate
  );

  if (isDuplicate) {
    Swal.fire("錯誤", `${newSchedule.value.workDate} 資料已存在，如需更改請使用編輯功能。`, "error");
    return;
  }

  // 工作日邏輯防呆
  if (newSchedule.value.isWorkday && !newSchedule.value.isMorningActive && !newSchedule.value.isAfternoonActive) {
    return Swal.fire("錯誤", "設定為工作日(開放)，請至少選擇一個營業班別（上午或下午）", "error");
  }

  // 檢查休假上限
  const yyyyMM = newSchedule.value.workDate.substring(0, 7);
  const add1 = newSchedule.value.dayType === 1 ? 1 : 0;
  const add3 = newSchedule.value.dayType === 3 ? 1 : 0;
  const warningMsg = checkLeaveLimits(yyyyMM, add1, add3);
  if (warningMsg) {
    const confirm = await Swal.fire({
      title: '系統提醒：超過請假天數', html: warningMsg + '<br>確定要繼續儲存嗎？',
      icon: 'warning', showCancelButton: true, confirmButtonText: '繼續', cancelButtonText: '取消'
    });
    if (!confirm.isConfirmed) return;
  }

  isSubmitting.value = true;
  
  // 將前端開關對應至後端 expected JSON Array Format
  const payload = {
    ...newSchedule.value,
    dayType: newSchedule.value.isWorkday ? newSchedule.value.dayType : null, // 勾選不是工作日，假別傳送空值
    periods: [
      { periodOfDayId: 1, isActive: newSchedule.value.isMorningActive },
      { periodOfDayId: 2, isActive: newSchedule.value.isAfternoonActive }
    ]
  };

  try {
    const response = await axiosapi.post('/ajax/pages/WashWorkCalendars', payload, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    });

    console.log("新增 API 回應:", response.data);

    Swal.fire("新增成功", `${newSchedule.value.workDate} 設定已儲存`, "success");

    closeBootstrapModal('addScheduleModal');

    // 將焦點移至剛新增的日期
    workDate.value = newSchedule.value.workDate;
    handleUnifiedSearch();

    newSchedule.value = {
      workDate: "",
      isWorkday: true,
      dayType: 0,
      note: "",
      createdBy: "Admin"
    };
  } catch (error) {
    console.error(error);
    Swal.fire("錯誤", "新增班表失敗，請稍後再試", "error");
  } finally {
    isSubmitting.value = false;
  }
};

const submitBatchAdd = async () => {
  if (!batchData.value.yearMonth) {
    return Swal.fire("錯誤", "請選擇設定月份", "error");
  }
  if (batchData.value.weekdays.length > 0 && !batchData.value.isMorningActive && !batchData.value.isAfternoonActive) {
    return Swal.fire("錯誤", "請至少選擇一個營業班別（上午或下午）", "error");
  }

  const [year, month] = batchData.value.yearMonth.split('-');
  const daysInMonth = new Date(year, month, 0).getDate();

  let count1 = 0; // 紀錄即將新增多少天例假
  const payloads = [];

  for (let d = 1; d <= daysInMonth; d++) {
    const dateStr = `${year}-${month}-${String(d).padStart(2, '0')}`;
    const dayOfWeek = new Date(dateStr).getDay();
      
    const exists = allCalendars.value.some(s => s.calendarId && s.workDate === dateStr);
    if (!exists) {
      const isWork = batchData.value.weekdays.includes(dayOfWeek);
      if (!isWork) count1++;
      payloads.push({
        workDate: dateStr,
        isWorkday: isWork,
        dayType: isWork ? 0 : null, // 營業為正常上班(0)，非工作日假別為空
        createdBy: "Admin",
        periods: isWork ? [
          { periodOfDayId: 1, isActive: batchData.value.isMorningActive },
          { periodOfDayId: 2, isActive: batchData.value.isAfternoonActive }
        ] : []
      });
    }
  }

  if (payloads.length === 0) {
    return Swal.fire("提示", "該月份皆已建檔，無須新增", "info");
  }

  const warningMsg = checkLeaveLimits(batchData.value.yearMonth, count1, 0);
  if (warningMsg) {
    const confirm = await Swal.fire({
      title: '系統提醒：超過請假天數', html: warningMsg + '<br>確定要繼續批次儲存嗎？',
      icon: 'warning', showCancelButton: true, confirmButtonText: '繼續', cancelButtonText: '取消'
    });
    if (!confirm.isConfirmed) return;
  }

  isSubmitting.value = true;
  let successCount = 0;
  for (const p of payloads) {
    try {
      await axiosapi.post('/ajax/pages/WashWorkCalendars', p, { headers: { "Authorization": `Bearer ${userStore.token}` } });
      successCount++;
    } catch (e) { console.error(e); }
  }

  Swal.fire("批次新增完成", `成功新增 ${successCount} 筆營業日設定`, "success");
  closeBootstrapModal('batchAddModal');
  workDate.value = `${year}-${month}-01`;
  handleUnifiedSearch();
  isSubmitting.value = false;
};

const submitBatchDelete = async () => {
  if (!batchDeleteData.value.yearMonth) {
    return Swal.fire("錯誤", "請選擇欲刪除的月份", "error");
  }
  
  const result = await Swal.fire({
    title: `確定要刪除 ${batchDeleteData.value.yearMonth} 的所有班表嗎？`,
    text: "刪除後將無法復原！",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '確定刪除',
    cancelButtonText: '取消'
  });

  if (!result.isConfirmed) return;

  isSubmitting.value = true;
  const targets = allCalendars.value.filter(c => c.workDate.startsWith(batchDeleteData.value.yearMonth));
  
  if (targets.length === 0) {
    isSubmitting.value = false;
    return Swal.fire("提示", "該月份沒有任何排班資料", "info");
  }
  
  let successCount = 0;
  for (const t of targets) {
    try {
      await axiosapi.delete(`/ajax/pages/WashWorkCalendars/${t.calendarId}`, { headers: { "Authorization": `Bearer ${userStore.token}` } });
      successCount++;
    } catch (e) { console.error(e); }
  }

  Swal.fire("批次刪除完成", `成功刪除 ${successCount} 筆營業日設定`, "success");
  closeBootstrapModal('batchDeleteModal');
  
  workDate.value = '';
  selectedMonthQuery.value = batchDeleteData.value.yearMonth;
  handleUnifiedSearch();
  isSubmitting.value = false;
};

const submitEdit = async () => {
  // 檢查修改後的日期是否與「其他」已存在的班表衝突
  const isDuplicate = schedules.value.some(
    s => s.calendarId && 
         s.calendarId !== editData.value.calendarId && 
         s.workDate === editData.value.workDate
  );

  if (isDuplicate) {
    Swal.fire("錯誤", `${editData.value.workDate} 該日期資料已存在，請確認。`, "error");
    return;
  }

  if (editData.value.isWorkday && !editData.value.isMorningActive && !editData.value.isAfternoonActive) {
    return Swal.fire("錯誤", "設定為工作日(開放)，請至少選擇一個營業班別（上午或下午）", "error");
  }

  const yyyyMM = editData.value.workDate.substring(0, 7);
  const add1 = editData.value.dayType === 1 ? 1 : 0;
  const add3 = editData.value.dayType === 3 ? 1 : 0;
  const warningMsg = checkLeaveLimits(yyyyMM, add1, add3, editData.value.calendarId);
  if (warningMsg) {
    const confirm = await Swal.fire({
      title: '系統提醒：超過請假天數', html: warningMsg + '<br>確定要繼續儲存變更嗎？',
      icon: 'warning', showCancelButton: true, confirmButtonText: '繼續', cancelButtonText: '取消'
    });
    if (!confirm.isConfirmed) return;
  }

  const dayTypeName = dayTypeMap[editData.value.dayType];
  const result = await Swal.fire({
    title: '確定要修改嗎？',
    html: `即將修改為 <b>${editData.value.workDate}</b> [${dayTypeName}] <br>確定要儲存變更嗎？`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '確定修改',
    cancelButtonText: '取消'
  });

  if (!result.isConfirmed) return;

  isSubmitting.value = true;

  const payload = {
    ...editData.value,
    dayType: editData.value.isWorkday ? editData.value.dayType : null, // 勾選不是工作日，假別傳送空值
    periods: [
      { periodOfDayId: 1, isActive: editData.value.isMorningActive },
      { periodOfDayId: 2, isActive: editData.value.isAfternoonActive }
    ]
  };

  try {
    const response = await axiosapi.put(`/ajax/pages/WashWorkCalendars/${editData.value.calendarId}`, payload, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    });

    console.log("修改 API 回應:", response.data);

    Swal.fire("修改成功", `${editData.value.workDate} 設定修改成功`, "success");
    closeBootstrapModal('editScheduleModal');

    handleUnifiedSearch();
  } catch (error) {
    console.error(error);
    Swal.fire("錯誤", `${editData.value.workDate} 修改設定失敗，請稍後再試`, "error");
  } finally {
    isSubmitting.value = false;
  }
};

const deleteSchedule = async (schedule) => {
  const dayTypeName = dayTypeMap[schedule.dayType];
  const result = await Swal.fire({
    title: '確定要刪除嗎？',
    html: `即將刪除 <b>${schedule.workDate}</b> [${dayTypeName}]<br>刪除後將無法復原！`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '確定刪除',
    cancelButtonText: '取消'
  });

  if (result.isConfirmed) {
    try {
      await axiosapi.delete(`/ajax/pages/WashWorkCalendars/${schedule.calendarId}`, {
        headers: { "Authorization": `Bearer ${userStore.token}` }
      });
      Swal.fire("刪除成功", `${schedule.workDate} 設定已刪除`, "success");
      
      handleUnifiedSearch();
    } catch(error) {
      console.error(error);
      Swal.fire("錯誤", `${schedule.workDate} 刪除失敗，請稍後再試`, "error");
    }
  }
};

onMounted(() => {
  handleUnifiedSearch();
});
</script>

<style scoped>
.page-container {
  background-color: #FAF6F0; /* 暖米色系底色 */
  background-image: radial-gradient(#E8E3DD 1px, transparent 1px);
  background-size: 20px 20px; /* 給予背景淡淡的網點質感 */
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
}

.accent-top {
  border-top: 5px solid #CBA37E !important;
  border-radius: 20px;
}

.letter-spacing-1 { letter-spacing: 1px; }

.page-title {
  color: #8C6A4F; /* 深金棕色 */
  font-weight: 800;
}

/* 圓角卡片與柔和陰影 */
.filter-card, .table-card, .calendar-wrapper {
  background-color: #FFFFFF;
  border-radius: 20px;
  box-shadow: 0 12px 35px rgba(140, 106, 79, 0.06);
  padding: 28px;
  border: none;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}
.filter-card:hover, .table-card:hover, .calendar-wrapper:hover {
  box-shadow: 0 15px 45px rgba(140, 106, 79, 0.09);
}

/* 覆寫 Bootstrap 文字顏色 */
.text-primary { color: #CBA37E !important; }
.text-success { color: #8C9C7E !important; }
.text-danger  { color: #D38282 !important; }

/* 按鈕輕量化、金棕主色調 */
.btn {
  border-radius: 12px;
  font-weight: 500;
  border: none;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

/* Toolbar */
.table-toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
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
.filter-select { cursor: pointer; min-width: 140px; }
.search-input:focus, .filter-select:focus { 
  outline: none; 
  border-color: #CBA37E; 
  background: #FFF;
  box-shadow: 0 0 0 3px rgba(203, 163, 126, 0.15);
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
.btn-icon-success { color: #8C9C7E; }
.btn-icon-success:hover { background: #F0F4EC; }

.btn-primary {
  background-color: #CBA37E; /* 金棕色 */
  color: white;
  box-shadow: 0 4px 10px rgba(203, 163, 126, 0.2);
}
.btn-primary:hover {
  background-color: #B58A63;
  color: white;
  box-shadow: 0 6px 15px rgba(203, 163, 126, 0.3);
}
.btn-secondary {
  background-color: #E8E3DD;
  color: #6C5B4E;
}
.btn-secondary:hover {
  background-color: #D6CEC4;
  color: #5A4A3E;
}
.btn-success {
  background-color: #A3B296; /* 柔和綠色 */
  color: white;
  box-shadow: 0 4px 10px rgba(163, 178, 150, 0.2);
}
.btn-success:hover {
  background-color: #8C9C7E;
  color: white;
}

/* 無邊框 Outline 按鈕變柔和底色 */
.btn-outline-primary {
  background-color: rgba(203, 163, 126, 0.1);
  color: #B58A63;
}
.btn-outline-primary:hover {
  background-color: #CBA37E;
  color: white;
}
.btn-outline-success {
  background-color: #F6F8F4;
  border: 1px dashed #A3B296 !important;
  color: #7D8F66;
}
.btn-outline-success:hover {
  background-color: #A3B296;
  color: white;
}
.btn-outline-danger {
  background-color: rgba(211, 130, 130, 0.12);
  color: #D38282;
}
.btn-outline-danger:hover {
  background-color: #D38282;
  color: white;
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

/* 表格無邊框設計 */
.table {
  --bs-table-striped-bg: #FCFAF7;
  --bs-table-hover-bg: #F4EFEA;
  --bs-table-color: #6C5B4E;
  margin-bottom: 0;
}
.table thead tr {
  background-color: #F4EFEA;
}
.table thead th:first-child { border-top-left-radius: 12px; border-bottom-left-radius: 12px; }
.table thead th:last-child { border-top-right-radius: 12px; border-bottom-right-radius: 12px; }
.table th {
  border-bottom: 2px solid #F4EFEA;
  color: #8C6A4F;
  font-weight: 600;
  border-top: none;
  padding: 12px 16px;
  text-transform: uppercase;
  letter-spacing: 1px;
}
.table td {
  border-bottom: 1px solid #F9F6F2;
  vertical-align: middle;
}
.table tbody tr {
  transition: background-color 0.2s ease;
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
}
.page-link {
  border-radius: 8px;
  margin: 0 4px;
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

/* FullCalendar 輕量化 */
:deep(.fc) {
  --fc-border-color: #F4EFEA;
  --fc-today-bg-color: rgba(203, 163, 126, 0.08); /* 當天柔和背景 */
  --fc-neutral-bg-color: transparent; /* 清除預設的灰色背景變數 */
}
:deep(.fc-theme-standard td), :deep(.fc-theme-standard th) {
  border-color: #F4EFEA;
}
:deep(.fc-day-other), :deep(.fc-day-disabled) {
  background-color: transparent !important; /* 強制非當月/範圍外的灰色區塊為透明 */
}
:deep(.fc-toolbar-title) {
  color: #8C6A4F !important;
  font-weight: 700 !important;
  letter-spacing: 0.5px;
}
:deep(.fc-col-header-cell-cushion), :deep(.fc-daygrid-day-number) {
  color: #8C6A4F !important;
  font-weight: 600;
  text-decoration: none !important; /* 2. 移除行事曆上方文字(如:周一)的底線 */
}
:deep(.fc .fc-button-primary) {
  background-color: #CBA37E;
  border-color: #CBA37E;
  border-radius: 8px;
  box-shadow: none !important;
}
:deep(.fc .fc-button-primary:hover) {
  background-color: #B58A63;
  border-color: #B58A63;
}
:deep(.fc .fc-button-primary:not(:disabled).fc-button-active) {
  background-color: #A2764E;
  border-color: #A2764E;
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
  box-shadow: 0 4px 10px rgba(140, 106, 79, 0.08) !important;
  font-weight: 600;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
:deep(.fc-event) { cursor: pointer; }
:deep(.fc-event:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(140, 106, 79, 0.15) !important;
}
/* 可預約：白底卡片 + 柔和綠文字與邊條 */
:deep(.custom-event-available) {
  background-color: #FFFFFF !important;
  color: #6A7A5D !important; 
  border-left: 5px solid #A3B296 !important;
}
/* 不可預約：暖米底卡片 + 柔和紅文字與邊條 */
:deep(.custom-event-unavailable) {
  background-color: #FCFBF9 !important;
  color: #B56565 !important; 
  border-left: 5px solid #D38282 !important;
}
/* 未設定：無底色，僅顯示 Icon */
:deep(.custom-event-unset) {
  background-color: transparent !important;
  border: none !important;
  box-shadow: none !important;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
}
:deep(.custom-event-unset .fc-event-main) {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
:deep(.unset-plus-icon) {
  color: #CBA37E; /* 使用主題金棕色 */
  opacity: 0.7;
  transition: all 0.2s ease;
}
:deep(.custom-event-unset:hover .unset-plus-icon) {
  opacity: 1;
  transform: scale(1.15); /* 滑鼠移入時微微放大提示可以點擊 */
}
:deep(.fc-event-main), :deep(.fc-event-title),
:deep(.event-content-wrapper) {
  color: inherit !important; /* 強制繼承我們設定的字體顏色 */
  transition: color 0.3s ease;
  white-space: normal !important;
  word-break: break-word;
  line-height: 1.4;
}
:deep(.status-dot-available), :deep(.status-dot-unavailable) {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
}
:deep(.status-dot-available) {
  background-color: #8C9C7E; /* 綠色代表可預約 */
}
:deep(.status-dot-unavailable) {
  background-color: #D38282; /* 紅色代表已預約 */
}
:deep(.status-dot-unset) {
  background-color: #CBA37E; /* 棕色代表未設定 */
}
/* 3. 滑鼠移入該預約資料時，時段文字自動改變顏色對應選擇的時段 */
:deep(.custom-event-available:hover) .fc-event-main,
:deep(.custom-event-available:hover) .event-content-wrapper {
  color: #6A7A5D !important; /* 移入變為更深層的綠色 */
}
:deep(.custom-event-unavailable:hover) .fc-event-main,
:deep(.custom-event-unavailable:hover) .event-content-wrapper {
  color: #B56565 !important; /* 移入變為更深層的紅色 */
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
  .table-toolbar {
    flex-direction: column;
    align-items: stretch !important;
  }
  .table-toolbar > div {
    flex-direction: column;
    align-items: stretch !important;
    width: 100%;
  }
  .search-input, .filter-select, .table-toolbar .btn {
    width: 100%;
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
    color: #6C5B4E;
  }
  /* 手機版拔除卡片樣式，改為乾淨的清單感 */
  :deep(.fc-event) {
    background-color: transparent !important;
    border-left: none !important;
    box-shadow: none !important;
    margin: 0 !important;
    padding: 2px !important;
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
  border: 3px solid rgba(203, 163, 126, 0.2); /* 淺金棕色底圈 */
  border-top: 3px solid #8C6A4F; /* 深金棕色轉動邊 */
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

</style>
