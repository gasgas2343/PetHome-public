<template>
  <div class="container page-container mt-3 mb-4">
    <div class="text-center mb-4">
      <h1 class="page-title mb-2">我的預約與行事曆</h1>
      <div class="page-subtitle">MY APPOINTMENTS & CALENDAR</div>
    </div>

    <!-- 新增：顯示從價目表頁面傳遞過來的已選擇服務1150617 -->
    <div v-if="selectedServices" class="selected-services-summary mb-4 p-4 shadow-sm" style="background-color: rgba(255, 255, 255, 0.7); border-radius: 20px; border: 1px solid rgba(203, 163, 126, 0.2);">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="fw-bold mb-0" style="color: #8C6A4F;"><i class="bi bi-bag-check me-2"></i>已選擇的服務方案：</h5>
        <button class="btn btn-outline-secondary btn-sm rounded-pill px-3 shadow-sm" style="color: #8C6A4F; border-color: #E8E3DD; background-color: #FFFFFF;" @click="goBackToPriceList">
          <i class="bi bi-arrow-counterclockwise me-1"></i>重新選擇
        </button>
      </div>
      <ul class="mb-0 fs-6 fw-bold" style="color: #5A4A3E; list-style-type: none; padding-left: 0;">
        <template v-if="selectedServices.packageService">
          <li class="mb-2"><i class="bi bi-gem me-2" style="color: #CBA37E;"></i>{{ selectedServices.packageService.name }} ({{ selectedServices.packageService.estimatedTime }}) - NT$ {{ selectedServices.packageService.salePrice || selectedServices.packageService.originalPrice }}</li>
        </template>
        <template v-else-if="selectedServices.basicServices && selectedServices.basicServices.length > 0">
          <li v-for="srv in selectedServices.basicServices" :key="srv.id" class="mb-2">
            <i class="bi bi-stars me-2" style="color: #CBA37E;"></i>{{ srv.name }} ({{ srv.estimatedTime }}) - NT$ {{ srv.salePrice || srv.originalPrice }}
          </li>
        </template>
      </ul>
      <!-- 總計金額 -->
      <div class="mt-3 pt-3 border-top d-flex justify-content-between align-items-center" style="border-top: 1px dashed rgba(203, 163, 126, 0.3) !important;">
        <span class="fw-bold fs-5" style="color: #8C6A4F;"><i class="bi bi-wallet2 me-2"></i>總計金額：</span>
        <span class="fw-bolder fs-4" style="color: #B85C38; font-family: 'Noto Serif TC', serif;">NT$ {{ totalAmount }}</span>
      </div>
    </div>

    <div class="row g-4">
      <!-- 左半部：迷你月曆區塊 -->
      <div class="col-12 col-lg-5 col-xl-4">
        <div class="calendar-wrapper h-100">
          <FullCalendar ref="fullCalendarRef" :options="calendarOptions" />
          
          <!-- 迷你月曆下方新增圖片 (電腦版顯示) -->
          <div class="mt-2 text-center d-none d-lg-block">
            <img src="@/assets/images/member_calendar.png" alt="calendar image" class="img-fluid" style="max-width: 100%; border-radius: 12px;">
          </div>
        </div>
      </div>

      <!-- 右半部：單日預約時段選擇區塊 -->
      <div class="col-12 col-lg-7 col-xl-8">
        <div class="schedule-container h-100 position-relative" id="time-slot-section">
          
          <!-- 新增：動態顯示目前移入或選取的完整日期 -->
          <div class="text-center mb-3" style="min-height: 60px; transition: all 0.3s ease;">
            <h4 class="fw-bold m-0" style="color: #8C6A4F; letter-spacing: 2px; font-family: 'Cormorant Garamond', serif;">
              {{ activeDisplayDate }}
            </h4>
            <div class="mt-2 fw-bold" style="color: #A28C7A; font-size: 1.3rem; letter-spacing: 2px;">選擇預約時段</div>
          </div>

          <!-- 新增：左右滑動按鈕 (在手機平板上隱藏，保留觸控拖曳即可) -->
          <button class="scroll-arrow scroll-arrow-left shadow-sm d-none d-lg-flex" @click="changeWeek(-1)">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M15 18l-6-6 6-6"/></svg>
          </button>
          <button class="scroll-arrow scroll-arrow-right shadow-sm d-none d-lg-flex" @click="changeWeek(1)">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 18l6-6-6-6"/></svg>
          </button>

          <div class="weekly-grid-container custom-scrollbar"
               ref="weeklyGridRef"
               @mousedown="onMouseDown"
               @mouseleave="onMouseLeave"
               @mouseup="onMouseUp"
               @mousemove="onMouseMove">
            <div class="weekly-grid">
              <!-- 產生週日到週六共 7 欄 -->
              <div v-for="dayData in weeklySchedules" :key="dayData.dateStr" class="day-column"
                   @mouseenter="highlightCalendarDate(dayData.dateStr)"
                   @mouseleave="unhighlightCalendarDate(dayData.dateStr)">
                <!-- 星期與日期標頭 -->
                <div class="day-header text-center mb-3" 
                     :class="{'locked-header': dayData.isLocked, 'active-header': dayData.dateStr === workDate, 'cursor-pointer': !dayData.isLocked}"
                     @click="!dayData.isLocked && handleWeekDateClick(dayData.dateStr)">
                  <div class="day-name">{{ dayData.dayName }}</div>
                  <div class="day-num-large mt-1">{{ dayData.dayNum }}</div>
                </div>
                
                <!-- 每天底下的 8 個時段按鈕 -->
                <div class="slots-container d-flex flex-column align-items-center gap-3">
                  <button 
                    v-for="slot in dayData.slots" 
                    :key="slot.slotIndex"
                    class="btn slot-item"
                    :class="[isSlotAvailable(slot) ? 'available-slot' : 'unavailable-slot', slot.slotIndex <= 4 ? 'morning-slot' : 'afternoon-slot']"
                    @click="handleSlotClick(slot)"
                    :disabled="!isSlotAvailable(slot)"
                  >
                    <div v-if="isSlotAvailable(slot)" class="fw-bold slot-time-text slot-content-wrapper">
                      <span class="slot-time">{{ slot.time.split('~')[0].substring(0, 5) }}</span>
                      <span class="slot-plus">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                      </span>
                    </div>
                    <div v-else class="slot-time-text text-muted opacity-50" style="line-height: 1;">-</div>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 迷你月曆下方新增圖片 (手機版顯示於最下方) -->
    <div class="row mt-2 d-block d-lg-none">
      <div class="col-12 text-center">
        <img src="@/assets/images/member_calendar.png" alt="calendar image" class="img-fluid" style="max-width: 100%; border-radius: 12px;">
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter, onBeforeRouteLeave } from "vue-router";
import Swal from "sweetalert2";
import FullCalendar from '@fullcalendar/vue3';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import zhTwLocale from '@fullcalendar/core/locales/zh-tw';
import axiosapi from '@/plugins/axios.js';
import { userAuthStore } from '@/stores/auth.js';

const router = useRouter();
const authStore = userAuthStore();
const schedules = ref([]);
const loading = ref(false);
const adminCalendars = ref([]); // 記錄管理員營業日資料

const today = new Date();
const currentDateStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
const workDate = ref(currentDateStr); // 預設帶入今天的日期
const hoveredDateStr = ref(''); // 記錄目前滑鼠移入的日期
const allSchedules = ref([]); // 保存當月全部資料以便用於行事曆過濾計算
const selectedServices = ref(null); // 儲存從價目表帶過來的服務

// 假別對照表 (對應管理端 WashWorkCalendar)
const dayTypeMap = {
  0: '正常上班',
  1: '例假',
  2: '特休',
  3: '臨時公休'
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

const isSlotAvailable = (slot) => {
  if (!slot) return false;
  if (!slot.isPeriodOpen) return false;
  if (slot.isAvailable === false) return false;

  // 1. 過去日期一律不可預約
  if (slot.workDate < currentDateStr) return false;

  // 2. 若為今天，則已過去的時段不可預約
  if (slot.workDate === currentDateStr) {
    const slotStartStr = slot.time ? slot.time.split('~')[0] : '';
    if (slotStartStr) {
      const [sh, sm] = slotStartStr.split(':').map(Number);
      const now = new Date();
      const ch = now.getHours();
      const cm = now.getMinutes();
      // 如果目前的小時已大於時段起始小時，或小時相同但分鐘已大於等於起始分鐘，則不可預約
      if (ch > sh || (ch === sh && cm >= sm)) {
        return false;
      }
    }
  }

  return true;
};

const formatTimeRange = (timeRange) => {
  if (!timeRange) return '';
  return timeRange.replace(/:/g, '').replace(/(\d{2})(\d{2})~(\d{2})(\d{2})/, '$1:$2 - $3:$4').replace(/~/, ' - ');
};

const getSelectedServicesText = () => {
  if (!selectedServices.value) return '';

  if (selectedServices.value.packageService) {
    const service = selectedServices.value.packageService;
    const price = service.salePrice || service.originalPrice || 0;
    return `服務項目：${service.name} \n價格：NT$ ${price}`;
  }

  if (selectedServices.value.basicServices?.length) {
    const services = selectedServices.value.basicServices.map(s => s.name).join(' / ');
    const totalPrice = selectedServices.value.basicServices.reduce((sum, s) => sum + (s.salePrice || s.originalPrice || 0), 0);
    return `服務項目：${services} \n價格：NT$ ${totalPrice}`;
  }

  return '';
};

const getSelectedServicePrice = () => {
  if (!selectedServices.value) return 0;

  if (selectedServices.value.packageService) {
    const service = selectedServices.value.packageService;
    return service.salePrice || service.originalPrice || 0;
  }

  return selectedServices.value.basicServices?.reduce((sum, s) => sum + (s.salePrice || s.originalPrice || 0), 0) || 0;
};

const totalAmount = computed(() => {
  return getSelectedServicePrice();
});

const handleSlotClick = (slot) => {
  if (!selectedServices.value) {
    Swal.fire('請先選擇服務', '請先前往價目表頁面選擇服務項目，才能預約時段。', 'warning');
    return;
  }

  if (!isSlotAvailable(slot)) {
    Swal.fire('無法預約', '該時段已過期或不可預約。', 'error');
    return;
  }

  // 點擊時段時，同步更新左側日曆的選取日期
  if (workDate.value !== slot.workDate) {
    workDate.value = slot.workDate;
    if (fullCalendarRef.value) {
      fullCalendarRef.value.getApi().gotoDate(slot.workDate);
    }
    updateCalendar(); // 觸發日曆重繪
  }

  const price = getSelectedServicePrice();
  const serviceNames = selectedServices.value.packageService
    ? selectedServices.value.packageService.name
    : selectedServices.value.basicServices?.map(s => s.name).join(' / ');
  const timeText = slot.time.replace('~', ' - ');
  const summaryHtml = `
    <div style="text-align:left; line-height:1.6;">
      <div><strong>服務項目：</strong>${serviceNames}</div>
      <div><strong>價格：</strong>NT$ ${price}</div>
      <div><strong>時段：</strong>${workDate.value} ${timeText}</div>
    </div>
  `;

  Swal.fire({
    title: '確認預約資訊',
    html: summaryHtml,
    icon: 'info',
    showCancelButton: true,
    confirmButtonText: '確認',
    cancelButtonText: '取消',
    customClass: {
      popup: 'swal2-border-radius'
    }
  }).then((result) => {
    if (result.isConfirmed) {
      const bookingInfo = {
        selectedServices: selectedServices.value,
        serviceNames,
        price,
        workDate: slot.workDate,
        slotIndex: slot.slotIndex,
        slotTime: timeText,
        slotTimeRange: slot.time,
        slotStartTime: slot.time.split('~')[0],
        slotEndTime: slot.time.split('~')[1],
        dateTime: `${slot.workDate} ${timeText}`,
        scheduleId: slot.scheduleId || null
      };
      sessionStorage.setItem('appointmentConfirmation', JSON.stringify(bookingInfo));
      router.push('/wash/record');
    }
  });
};

// 返回價目表重新選擇，保留上一次選擇的服務項目
const goBackToPriceList = () => {
  sessionStorage.setItem('fromCalendar', 'true');
  router.push('/wash/price-list');
};

// 計算選定日期所在的那一週 (週日 ~ 週六) 的所有日期
const weekDates = computed(() => {
  if (!workDate.value) return [];
  const curr = new Date(workDate.value);
  const day = curr.getDay(); 
  const diff = curr.getDate() - day; // 調整到週日
  const sunday = new Date(curr.setDate(diff));
  
  const days = [];
  for (let i = 0; i < 7; i++) {
    const d = new Date(sunday);
    d.setDate(sunday.getDate() + i);
    days.push(`${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`);
  }
  return days;
});

// 構建週月曆資料 (包含 7 天，每天 8 個時段)
// 時段可用性邏輯：
//   - isPeriodOpen: 管理員班別是否開放 (由 WashWorkCalendar 決定)
//   - isAvailable: 美容師是否可預約 (由 WashSchedule 決定，true=可預約, false=請假)
//   - 該時段可點擊的條件：isPeriodOpen AND (isAvailable OR 未有美容師班表記錄)
const weeklySchedules = computed(() => {
  return weekDates.value.map(dateStr => {
    const d = new Date(dateStr);
    const dayName = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'][d.getDay()];
    const dayNum = d.getDate();
    
    const isLocked = isDateLocked(dateStr);
    
    // 查詢該日期的管理員班表，決定上午/下午班是否開放
    let isMorningOpen = false;
    let isAfternoonOpen = false;

    if (adminCalendars.value.length > 0) {
      const adminCal = adminCalendars.value.find(c => c.workDate === dateStr);
      if (adminCal && adminCal.isWorkday) {
        isMorningOpen = adminCal.periods?.some(p => p.periodOfDayId === 1 && p.isActive) || false;
        isAfternoonOpen = adminCal.periods?.some(p => p.periodOfDayId === 2 && p.isActive) || false;
      }
    } else {
      // 無 DB 測試模式：週日外的工作日開放
      isMorningOpen = isLocked ? false : true;
      isAfternoonOpen = isLocked ? false : true;
    }

    // 構建每個時段的資料
    const slots = [];
    for (let i = 1; i <= 8; i++) {
      const isPeriodOpen = i <= 4 ? isMorningOpen : isAfternoonOpen;
      
      // 查詢美容師班表，取得該時段的可用性
      const groomerSlot = allSchedules.value.find(s => s.workDate === dateStr && s.slotIndex === i);
      
      // 時段可用性：管理員班別開放 && 美容師未請假 && 未被預約
      const isBooked = groomerSlot && groomerSlot.apptOrderId && groomerSlot.apptOrderId !== 'null' && groomerSlot.apptOrderId !== '';
      const isAvailable = isPeriodOpen && (!groomerSlot || (groomerSlot.isAvailable && !isBooked));
      
      slots.push({
        workDate: dateStr,
        slotIndex: i,
        time: getSlotTimeRange(i),
        isPeriodOpen: isPeriodOpen,
        isAvailable: isAvailable,
        scheduleId: groomerSlot?.scheduleId || null,
        note: groomerSlot?.note || ''
      });
    }
    
    return { dateStr, dayName, dayNum, isLocked, slots };
  });
});


const handleWeekDateClick = (dateStr) => {
  workDate.value = dateStr;
  if (fullCalendarRef.value) {
    fullCalendarRef.value.getApi().gotoDate(dateStr);
  }
  updateCalendar(); // 觸發日曆重繪以顯示選取狀態
};

// 取得 FullCalendar 實體的參考
const fullCalendarRef = ref(null);

// === 新增：週月曆滑鼠拖曳左右滑動功能 ===
const weeklyGridRef = ref(null);
let isDragging = false;
let startX = 0;
let scrollLeft = 0;

const changeWeek = (direction) => {
  const curr = new Date(workDate.value);
  curr.setDate(curr.getDate() + direction * 7); // 加減 7 天
  const newDateStr = `${curr.getFullYear()}-${String(curr.getMonth() + 1).padStart(2, '0')}-${String(curr.getDate()).padStart(2, '0')}`;
  
  workDate.value = newDateStr;
  if (fullCalendarRef.value) {
    fullCalendarRef.value.getApi().gotoDate(newDateStr);
  }
  updateCalendar(); // 觸發日曆重繪與資料連動
};

const onMouseDown = (e) => {
  isDragging = true;
  startX = e.pageX - weeklyGridRef.value.offsetLeft;
  scrollLeft = weeklyGridRef.value.scrollLeft;
};
const onMouseLeave = () => {
  isDragging = false;
};
const onMouseUp = () => {
  isDragging = false;
};
const onMouseMove = (e) => {
  if (!isDragging) return;
  e.preventDefault(); // 避免拖曳時選取到文字
  const x = e.pageX - weeklyGridRef.value.offsetLeft;
  const walk = (x - startX) * 1.5; // 調整滑動速度比例
  weeklyGridRef.value.scrollLeft = scrollLeft - walk;
};

// 格式化日期：2026/6/13(六)
const formatFullDate = (dateString) => {
  if (!dateString) return '';
  const d = new Date(dateString);
  const y = d.getFullYear();
  const m = d.getMonth() + 1;
  const day = d.getDate();
  const dayNames = ['日', '一', '二', '三', '四', '五', '六'];
  const dayOfWeek = dayNames[d.getDay()];
  return `${y}/${m}/${day}(${dayOfWeek})`;
};

// 動態顯示日期：優先顯示滑鼠移入的日期，否則顯示目前選取的日期
const activeDisplayDate = computed(() => {
  const targetDate = hoveredDateStr.value || workDate.value;
  return formatFullDate(targetDate);
});

// 滑鼠移入時段：連動左側日曆醒目提示 (避免預約錯天)
const highlightCalendarDate = (dateStr) => {
  hoveredDateStr.value = dateStr;
  const cells = document.querySelectorAll(`.fc-day[data-date="${dateStr}"] .custom-day-cell`);
  cells.forEach(el => el.classList.add('hover-highlight'));
};
// 滑鼠移出時段：取消日曆醒目提示
const unhighlightCalendarDate = (dateStr) => {
  if (hoveredDateStr.value === dateStr) hoveredDateStr.value = '';
  const cells = document.querySelectorAll(`.fc-day[data-date="${dateStr}"] .custom-day-cell`);
  cells.forEach(el => el.classList.remove('hover-highlight'));
};

// 計算某日是否已被完全鎖起來 (無班別 或 8個時段全無可用)
// 數據流：
//   1. adminCalendars = WashWorkCalendar (管理員排班) - 控制上午/下午班是否開放
//   2. allSchedules = WashSchedule (美容師班表) - 控制每個時段是否可預約 (isAvailable)
const isDateLocked = (dateStr) => {
  // 1. 過去日期一律鎖定 (包含今天之前)
  if (dateStr < currentDateStr) return true;

  // 2. 若無管理員班表資料，進入測試模式 (週日公休，其他開放)
  if (adminCalendars.value.length === 0) {
    const d = new Date(dateStr);
    return d.getDay() === 0; // 週日鎖定
  }

  // 3. 查詢該日期的管理員班表
  const adminCal = adminCalendars.value.find(c => c.workDate === dateStr);
  
  // 3a. 該日期無班表或非工作日，鎖定
  if (!adminCal || !adminCal.isWorkday) return true;

  // 3b. 取得管理員設定的上午/下午班是否開放
  const isMorningOpen = adminCal.periods?.some(p => p.periodOfDayId === 1 && p.isActive);
  const isAfternoonOpen = adminCal.periods?.some(p => p.periodOfDayId === 2 && p.isActive);
  
  // 3c. 上午下午班都未開放，鎖定該日
  if (!isMorningOpen && !isAfternoonOpen) return true;

  // 4. 檢查該日是否至少有一個時段可預約
  //    條件：(a) 管理員班別開放 && (b) 美容師未請假 && (c) 無預約衝突
  let availableCount = 0;
  for (let i = 1; i <= 8; i++) {
    const isPeriodOpen = i <= 4 ? isMorningOpen : isAfternoonOpen;
    if (isPeriodOpen) {
      const groomerSlot = allSchedules.value.find(s => s.workDate === dateStr && s.slotIndex === i);
      const isBooked = groomerSlot && groomerSlot.apptOrderId && groomerSlot.apptOrderId !== 'null' && groomerSlot.apptOrderId !== '';
      // 美容師班表記錄不存在 || 存在且 isAvailable=true 且未被預約，表示可預約
      if (!groomerSlot || (groomerSlot.isAvailable && !isBooked)) {
        availableCount++;
      }
    }
  }
  
  // 若至少有一個可預約時段，該日不鎖定
  return availableCount === 0;
};

// 點擊行事曆日期：自動帶入該日期重新查詢，並向下滑動到按鈕區塊
const handleDateClick = async (arg) => {
  if (isDateLocked(arg.dateStr)) return; // 若已被鎖定，拒絕點擊

  workDate.value = arg.dateStr;
  
  updateCalendar(); // 僅更新日曆選中狀態即可，無須重新 fetch API
  
  // 平滑滑動至時段區塊 (僅在手機、平版螢幕時執行，電腦螢幕左右版面不需要滾動)
  const targetSection = document.getElementById('time-slot-section');
  if (targetSection && window.innerWidth < 992) {
    targetSection.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }
};

const calendarOptions = ref({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  showNonCurrentDates: false, // 隱藏非當月的日期與事件
  fixedWeekCount: false,      // 讓行事曆根據當月實際週數顯示 (4~5週)，不固定顯示 6 週
  headerToolbar: {
    left: 'prev',
    center: 'title',
    right: 'next'
  },
  dayMaxEvents: true, // 允許出現 "+X more" 連結，避免單日格子過高
  locale: zhTwLocale, // 設定繁體中文
  dateClick: handleDateClick,
  events: [],
  dayHeaderContent: (arg) => {
    // 將星期替換為英文簡寫
    const days = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
    return { html: `<span class="letter-spacing-1">${days[arg.date.getDay()]}</span>` };
  },
  dayCellContent: (arg) => {
    // 建構當前格子的 yyyy-mm-dd
    const dateStr = `${arg.date.getFullYear()}-${String(arg.date.getMonth() + 1).padStart(2, '0')}-${String(arg.date.getDate()).padStart(2, '0')}`;
    
    const isLocked = isDateLocked(dateStr);
    const isSelected = dateStr === workDate.value;

    // 建構客製化無邊框 HTML (將可預約日期的數字以圓圈標示)
    return {
        html: `
            <div class="custom-day-cell w-100 h-100 d-flex justify-content-center align-items-center position-relative ${isLocked ? 'locked-date' : 'available-date'} ${isSelected ? 'selected-date' : ''}">
              <span class="day-num ${!isLocked ? 'circle-indicator' : ''}">${arg.date.getDate()}</span>
            </div>
        `
    }
  }
});

const updateCalendar = () => {
  // 賦予一個空的隱藏事件來觸發 FullCalendar 的響應式重繪
  if (fullCalendarRef.value) {
    calendarOptions.value.events = [{ id: Date.now(), display: 'none' }];
  }
};

const handleUnifiedSearch = async () => {
  loading.value = true;

  try {
    // 並行查詢兩個 API：
    // 1. WashWorkCalendars - 管理員排班（控制上午/下午班是否開放）
    // 2. WashSchedules - 美容師班表（控制個別時段是否可預約）
    const [calendarRes, scheduleRes] = await Promise.all([
      axiosapi.get(`/ajax/pages/WashWorkCalendars/all`, { headers: { "Authorization": `Bearer ${authStore.accessToken}` } }),
      axiosapi.get(`/ajax/pages/WashSchedules/all`, { headers: { "Authorization": `Bearer ${authStore.accessToken}` } })
    ]);

    // === 處理 WashWorkCalendar 資料 ===
    const workCalData = calendarRes.data;
    const fetchedWorkCalendars = Array.isArray(workCalData) ? workCalData : (workCalData.list || []);
    adminCalendars.value = fetchedWorkCalendars;

    // 過濾過去的班表資料（只保留今天及以後的資料）
    const validAdminCalendars = fetchedWorkCalendars.filter(cal => cal.workDate >= currentDateStr);

    // 計算行事曆可見範圍 (validRange)：從最早可預約日到最晚可預約日的下個月初
    if (validAdminCalendars.length > 0) {
      const dates = validAdminCalendars.map(c => new Date(c.workDate));
      const minD = new Date(Math.min(...dates));
      const maxD = new Date(Math.max(...dates));
      
      const startRange = `${minD.getFullYear()}-${String(minD.getMonth() + 1).padStart(2, '0')}-01`;
      const maxMonthNext = new Date(maxD.getFullYear(), maxD.getMonth() + 1, 1);
      const endRange = `${maxMonthNext.getFullYear()}-${String(maxMonthNext.getMonth() + 1).padStart(2, '0')}-01`;
      calendarOptions.value.validRange = { start: startRange, end: endRange };
    }

    // === 處理 WashSchedules 資料 ===
    const data = scheduleRes.data;
    let fetchedList = Array.isArray(data) ? data : (data.list || []);
    
    // 過濾過去的班表資料
    const validSchedules = fetchedList.filter(s => s.workDate >= currentDateStr);
    allSchedules.value = validSchedules; // 保留過濾後的列表供行事曆狀態推算

    // 篩選出當前點選日期的班表資料
    let dailySchedules = validSchedules;
    if (workDate.value) {
      dailySchedules = validSchedules.filter(s => s.workDate === workDate.value);
    }

    // === 構建每日時段清單 ===
    const schedulesList = [];
    const currentAdminCal = adminCalendars.value.find(c => c.workDate === workDate.value);
    const isMorningOpen = currentAdminCal?.isWorkday && currentAdminCal?.periods?.some(p => p.periodOfDayId === 1 && p.isActive);
    const isAfternoonOpen = currentAdminCal?.isWorkday && currentAdminCal?.periods?.some(p => p.periodOfDayId === 2 && p.isActive);

    for (let i = 1; i <= 8; i++) {
      const isPeriodOpen = i <= 4 ? isMorningOpen : isAfternoonOpen;
      const found = dailySchedules.find(s => s.slotIndex === i);
      
      if (found) {
        // 美容師班表記錄存在
        const isBooked = found.apptOrderId && found.apptOrderId !== 'null' && found.apptOrderId !== '';
        schedulesList.push({
          scheduleId: found.scheduleId,
          workDate: found.workDate,
          slotIndex: i,
          isAvailable: found.isAvailable === true && !isBooked, // 確保是 boolean 且未被預約
          isPeriodOpen: isPeriodOpen,
          note: found.note || ''
        });
      } else {
        // 美容師班表記錄不存在，按管理員班別判斷
        schedulesList.push({
          scheduleId: null,
          workDate: workDate.value,
          slotIndex: i,
          isAvailable: isPeriodOpen ? true : null,
          isPeriodOpen: isPeriodOpen,
          note: ''
        });
      }
    }
    schedules.value = schedulesList;

    if (fullCalendarRef.value) {
      fullCalendarRef.value.getApi().gotoDate(workDate.value);
    }

    // 更新行事曆 (套用篩選)
    updateCalendar();
  } catch (error) {
    console.warn("無法連線至 DB，啟用無 DB 測試模式", error);
    
    // === 無 DB 測試模式 ===
    // 模擬產生下方的每日可預約時段 (週日公休)
    const dailySchedules = [];
    for (let i = 1; i <= 8; i++) {
      dailySchedules.push({
        scheduleId: null,
        workDate: workDate.value,
        slotIndex: i,
        isAvailable: !isDateLocked(workDate.value),
        isPeriodOpen: !isDateLocked(workDate.value),
        note: ''
      });
    }
    schedules.value = dailySchedules;
    calendarOptions.value.events = [];
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  handleUnifiedSearch();

  // 檢查是否是從價目表過來的
  const isFromPriceList = sessionStorage.getItem('fromPriceList') === 'true';
  sessionStorage.removeItem('fromPriceList'); // 讀取後立即清除

  if (isFromPriceList) {
    // 讀取從價目表頁面傳遞過來的服務選擇資料
    const savedServices = sessionStorage.getItem('selectedServices');
    if (savedServices) {
      selectedServices.value = JSON.parse(savedServices);
    } else if (authStore.id) {
      // 試圖從後端取得保留的狀態以回復顯示
      try {
        const resState = await axiosapi.get('/ajax/pages/WashServices/getState');
        if (resState.data && resState.data.success && resState.data.state) {
          selectedServices.value = resState.data.state;
          sessionStorage.setItem('selectedServices', JSON.stringify(resState.data.state));
        }
      } catch (e) {
        console.error("Failed to load state from backend", e);
      }
    }
  } else {
    // 否則（從其他頁面進入或直接連結），清除暫存與後端狀態，讓頁面自動刷新
    selectedServices.value = null;
    sessionStorage.removeItem('selectedServices');
    if (authStore.id) {
      axiosapi.post('/ajax/pages/WashServices/clearState').catch(() => {});
    }
  }
});
</script>

<style scoped>
.page-container {
  background: linear-gradient(135deg, #FAF6F0 0%, #F2EBE3 100%); /* 微漸層暖米背景 */
  border-radius: 24px;
  padding: 28px 32px; /* 增加上下左右內距，讓整體版面更平衡有呼吸感 */
  /* 加入內發光白邊與極柔和的外陰影，營造高級紙質浮凸感 */
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.6), 0 10px 40px rgba(140, 106, 79, 0.06);
}

.letter-spacing-1 { letter-spacing: 1px; }

.page-title {
  background: linear-gradient(135deg, #8C6A4F 0%, #5A4A3E 100%); /* 漸層金屬字體 */
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 800;
}

.page-subtitle {
  color: #A28C7A;
  letter-spacing: 3px;
  font-size: 0.95rem;
  font-weight: 600;
}

.date-title { color: #8C6A4F; font-weight: 700; font-size: 1.6rem; text-shadow: 1px 1px 0px rgba(255,255,255,0.8); }

/* 圓角卡片與柔和暖色陰影 */
.calendar-wrapper {
  background: rgba(255, 255, 255, 0.55); /* 半透明暖白底色，營造透明感 */
  backdrop-filter: blur(16px); /* 毛玻璃模糊效果 */
  -webkit-backdrop-filter: blur(16px); /* 支援 Safari 的毛玻璃效果 */
  border-radius: 24px;
  padding: 28px 24px; /* 增加上下內距，讓月曆上下空間更大 */
  /* 高級感微邊框與內反光 */
  box-shadow: 0 8px 32px rgba(140, 106, 79, 0.08), inset 0 0 0 1px rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(203, 163, 126, 0.15);
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
}
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

/* === 右側週預約時段 (Weekly Grid) === */
.weekly-grid-container {
  overflow-x: auto; /* 在小螢幕上允許水平滑動 */
  padding-bottom: 12px;
  cursor: grab; /* 提示使用者可拖曳 */
}
.weekly-grid-container:active {
  cursor: grabbing;
}
.custom-scrollbar::-webkit-scrollbar {
  height: 6px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #E8E3DD;
  border-radius: 6px;
}
.weekly-grid {
  display: flex;
  gap: 12px;
  min-width: 650px; /* 確保 7 欄不會被過度擠壓 */
}
.day-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #FFFFFF;
  border-radius: 20px;
  padding: 16px 8px; /* 恢復舒適的內距，善用垂直空間 */
  border: 1px solid rgba(203, 163, 126, 0.05); /* 極淡的預設邊框 */
  box-shadow: 0 4px 16px rgba(140, 106, 79, 0.05); /* 柔和預設陰影 */
  transition: all 0.3s ease;
}
.day-column:hover {
  border: 1px solid rgba(203, 163, 126, 0.4); /* 移入時加深金棕色邊框 */
  box-shadow: 0 12px 28px rgba(203, 163, 126, 0.12); /* 增加陰影擴散與立體感 */
  background-color: #FAF5F0; /* 移入時改變直條白色背景為柔和米色 */
  transform: translateY(-4px); /* 增加上浮物理動態 */
}
.day-header {
  color: #8C6A4F;
  transition: all 0.3s ease;
}
.day-name {
  font-size: 0.8rem;
  font-weight: 600;
  letter-spacing: 1px;
}
.day-num-large {
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.5rem; /* 適度縮小以節省空間 */
  font-weight: 400;
  font-variant-numeric: tabular-nums;
}
/* 日期鎖定反灰 */
.locked-header {
  color: #D6D0CB;
  opacity: 0.7;
}
/* 目前選取的日期突顯 */
.active-header .day-num-large {
  background: linear-gradient(145deg, #3D2B1F 0%, #5A4A3E 100%); /* 漸層墨銅色 */
  color: #FFFFFF;
  border-radius: 50%;
  width: 38px; /* 配合字體縮小 */
  height: 38px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(61, 43, 31, 0.3), inset 0 1px 1px rgba(255, 255, 255, 0.2); /* 加上內部反光 */
}
.cursor-pointer {
  cursor: pointer;
}
.cursor-pointer .day-num-large {
  transition: all 0.2s ease;
}
.cursor-pointer:hover .day-num-large {
  color: #CBA37E;
  transform: scale(1.15);
}

/* 時段按鈕改版 (圓角矩形 + 玻璃質感 + 平衡間距) */
.slot-item {
  width: 76px; /* 放大寬度 */
  height: 60px; /* 放大高度 */
  border-radius: 16px; /* 圓角矩形 */
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.5) !important; /* 晶瑩白邊 */
  transition: all 0.3s cubic-bezier(0.165, 0.84, 0.44, 1);
  padding: 0;
  background: rgba(255, 255, 255, 0.45); /* 半透明底層 */
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  box-shadow: 
    0 4px 10px rgba(140, 106, 79, 0.04), 
    inset 0 1px 2px rgba(255, 255, 255, 0.6);
}
.slot-time-text {
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.15rem; /* 字體微調大 */
  font-variant-numeric: tabular-nums;
  font-weight: 700;
}
.available-slot {
  cursor: pointer;
}
.available-slot.morning-slot {
  border: 1px solid rgba(163, 178, 150, 0.35) !important;
  color: #4A5842; /* 雅緻深綠 */
}
.available-slot.afternoon-slot {
  border: 1px solid rgba(203, 163, 126, 0.35) !important;
  color: #6D503C; /* 雅緻金棕 */
}
.available-slot:hover {
  color: #FFFFFF !important;
  transform: translateY(-4px) scale(1.05); /* 輕微上浮 */
}
.available-slot.morning-slot:hover {
  background: rgba(163, 178, 150, 0.9) !important; /* 上午時段滑入變色：柔和綠色 */
  box-shadow: 
    0 8px 24px rgba(163, 178, 150, 0.35),
    inset 0 1px 2px rgba(255, 255, 255, 0.4) !important;
  border-color: rgba(163, 178, 150, 0.6) !important;
}
.available-slot.afternoon-slot:hover {
  background: rgba(203, 163, 126, 0.9) !important; /* 下午時段滑入變色：金棕色 */
  box-shadow: 
    0 8px 24px rgba(203, 163, 126, 0.35),
    inset 0 1px 2px rgba(255, 255, 255, 0.4) !important;
  border-color: rgba(203, 163, 126, 0.6) !important;
}
.unavailable-slot {
  background-color: rgba(232, 227, 221, 0.2) !important; /* 微弱透明融入背景 */
  color: #D6D0CB !important;
  cursor: not-allowed;
  box-shadow: none !important;
  border: 1px dashed rgba(203, 163, 126, 0.2) !important; /* 虛線邊框 */
  backdrop-filter: blur(4px);
}

/* 時段按鈕懸停動畫：時間與 + 輪流顯示 */
.slot-content-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.slot-time, .slot-plus {
  position: absolute;
  transition: opacity 0.3s ease;
}
.slot-plus {
  opacity: 0;
}
@keyframes alternateShowTime {
  0%, 40% { opacity: 1; transform: scale(1.25); font-weight: 700; color: #FFFFFF; } /* 放大數字並確保顏色為對比白 */
  50%, 90% { opacity: 0; transform: scale(0.8); }
  100% { opacity: 1; transform: scale(1.25); font-weight: 700; color: #FFFFFF; }
}
@keyframes alternateShowPlus {
  0%, 40% { opacity: 0; transform: scale(0.8); }
  50%, 90% { opacity: 1; transform: scale(1.5); color: #FFFFFF; } /* 放大 icon 確保顏色為對比白 */
  100% { opacity: 0; transform: scale(0.8); }
}
.available-slot:hover .slot-time {
  animation: alternateShowTime 1s infinite; /* 加快動畫速度 */
}
.available-slot:hover .slot-plus {
  animation: alternateShowPlus 1s infinite; /* 加快動畫速度 */
}

/* 左右滑動按鈕 */
.scroll-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background-color: #FFFFFF;
  border: 1px solid #E8E3DD;
  color: #8C6A4F;
  /* 排版由 d-flex 控制，這裡專注內部對齊與外觀 */
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}
.scroll-arrow:hover {
  background-color: #CBA37E;
  color: #FFFFFF;
  border-color: #CBA37E;
  box-shadow: 0 4px 12px rgba(203, 163, 126, 0.3) !important;
}
.scroll-arrow-left {
  left: -22px;
}
.scroll-arrow-right {
  right: -22px;
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
  font-size: 0.85rem; /* 整體縮小字體營造迷你感 */
  --fc-border-color: #F4EFEA;
  --fc-today-bg-color: rgba(203, 163, 126, 0.08); /* 當天柔和背景 */
}
:deep(.fc-theme-standard td), :deep(.fc-theme-standard th) {
  border: none !important; /* 完全移除內部格線 */
}
:deep(.fc-scrollgrid) {
  border: none !important; /* 移除外框 */
}
/* 月份的底色，不顯示日期不要有底色 */
:deep(.fc td), :deep(.fc-day-other) {
  background-color: transparent !important;
}
:deep(.fc-header-toolbar) {
  margin-bottom: 1.5rem !important; /* 增加標題與日期網格之間的垂直距離 */
}
:deep(.fc-toolbar-chunk) {
  display: flex;
  align-items: center;
  justify-content: center;
}
:deep(.fc-toolbar-title) {
  color: #3D2B1F !important; 
  font-family: 'Cormorant Garamond', serif !important;
  font-weight: 600 !important; /* 加粗 */
  font-size: 1.8rem !important; /* 放大 */
  letter-spacing: 1px;
  margin: 0 !important; 
}
:deep(.fc-icon) {
  font-weight: 100 !important; /* 將導覽箭頭調細 */
  transform: scale(0.8);
  color: #8B6852;
}
:deep(.fc-col-header-cell-cushion) {
  color: #A28C7A !important;
  font-family: 'Cormorant Garamond', serif !important;
  font-size: 0.95rem !important;
  font-weight: 600 !important;
  text-decoration: none !important;
}
:deep(.fc .fc-button-primary) {
  background-color: transparent;
  border: none;
  color: #CBA37E;
  padding: 0.2rem 0.5rem;
}
:deep(.fc .fc-button-primary:hover) {
  background-color: transparent;
  color: #8C6A4F;
}

/* 行事曆滑鼠對應醒目提示 (呼吸燈效果防呆) */
@keyframes pulseHighlight {
  0% { box-shadow: 0 0 0 0 rgba(203, 163, 126, 0.7); }
  70% { box-shadow: 0 0 0 8px rgba(203, 163, 126, 0); }
  100% { box-shadow: 0 0 0 0 rgba(203, 163, 126, 0); }
}
.hover-highlight {
  background-color: #CBA37E !important;
  transform: scale(1.15) !important;
  border: 2px solid #8C6A4F !important;
  z-index: 10;
  animation: pulseHighlight 1.5s infinite !important;
}
.hover-highlight .day-num { color: #FFFFFF !important; }
.hover-highlight .status-dot {
  background-color: #FFFFFF !important;
}

/* 行事曆自訂日期與圓圈 UI */
.custom-day-cell {
  min-height: 45px; /* 增加日期格子高度，讓每週間隔的上下空間更大 */
  cursor: pointer;
  border-radius: 12px;
  border: 1px solid transparent;
  transition: all 0.2s cubic-bezier(0.165, 0.84, 0.44, 1);
}
.available-date:hover {
  background-color: rgba(203, 163, 126, 0.15); /* 透明底色上的 Hover 效果加強 */
  border: 1px solid rgba(203, 163, 126, 0.3);
  transform: scale(1.08); /* 滑鼠滑入放大點擊效果 */
  box-shadow: 0 4px 10px rgba(203, 163, 126, 0.15);
}
.selected-date {
  background: linear-gradient(145deg, #3D2B1F 0%, #5A4A3E 100%) !important;
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(61, 43, 31, 0.3), inset 0 1px 1px rgba(255, 255, 255, 0.2);
}
.selected-date .day-num {
  color: #FFFFFF !important;
}
.locked-date {
  cursor: not-allowed;
}
/* 若不能預約，月份日曆的日期顯示淺灰色 */
.locked-date .day-num {
  color: #D6D0CB !important;
  font-weight: 400 !important; /* 鎖定的日期字重放輕 */
}
.day-num {
  font-family: 'Cormorant Garamond', serif !important; /* 統一襯線字體 */
  font-size: 1.5rem !important; /* 放大左側迷你月曆內的日期數字 */
  color: #3D2B1F !important; /* 墨銅色，取代純黑 */
  text-decoration: none !important;
  font-weight: 500;
}

/* === 新增：圓圈標示可預約日期 === */
.circle-indicator {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1.5px solid #CBA37E; /* 金棕色外圈 */
  background-color: rgba(203, 163, 126, 0.1); /* 淡淡的金棕色底 */
  color: #8C6A4F !important;
}
.selected-date .circle-indicator,
.hover-highlight .circle-indicator {
  border-color: rgba(255, 255, 255, 0.5);
  background-color: transparent;
  color: #FFFFFF !important;
}

:deep(.fc-daygrid-day-number) {
  text-decoration: none !important;
  color: #000000 !important;
}
.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #CBA37E;
  margin-right: 6px;
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
:global(.swal2-icon.swal2-success) { border-color: #A8B5A0 !important; color: #A8B5A0 !important; }
:global(.swal2-icon.swal2-success [class^="swal2-success-line"]) { background-color: #A8B5A0 !important; }
:global(.swal2-icon.swal2-success .swal2-success-ring) { border-color: rgba(168, 181, 160, 0.3) !important; }
:global(.swal2-icon.swal2-warning) { border-color: #D38282 !important; color: #D38282 !important; }
:global(.swal2-icon.swal2-error) { border-color: #D38282 !important; color: #D38282 !important; }
:global(.swal2-icon.swal2-error [class^="swal2-x-mark-line"]) { background-color: #D38282 !important; }

/* RWD 響應式設計 (手機/平板優化) */
@media (max-width: 768px) {
  .page-container {
    padding: 16px;
    border-radius: 16px;
  }
  .calendar-wrapper {
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
    flex-direction: row !important;
    justify-content: space-between !important;
    gap: 0;
  }
  :deep(.fc-toolbar-chunk) {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-wrap: nowrap;
    gap: 8px;
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
  /* 週/日視圖內部主體也需要去背景，確保顯示純淨文字 */
  :deep(.fc-timegrid-event .fc-event-main) {
    background-color: transparent !important;
    border: none !important;
    box-shadow: none !important;
  }
}
</style>
