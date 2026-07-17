<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axiosapi from '@/plugins/axios.js';
import { userAuthStore } from '@/stores/auth.js';

const router = useRouter();
const authStore = userAuthStore();

// === 預約小契約提示狀態與方法 ===
const showContractModal = ref(false);
const dontShowToday = ref(false);

const checkContractModal = () => {
  const lastDismissedDate = localStorage.getItem('bookingContractDismissedDate');
  const todayStr = new Date().toISOString().split('T')[0];
  if (lastDismissedDate === todayStr) {
    showContractModal.value = false;
  } else {
    showContractModal.value = true;
  }
};

const confirmContract = () => {
  if (dontShowToday.value) {
    const todayStr = new Date().toISOString().split('T')[0];
    localStorage.setItem('bookingContractDismissedDate', todayStr);
  }
  showContractModal.value = false;
};

const steps = [
  {
    no: '01',
    name: '挑選方案',
    parts: [
      { text: '請先為寶貝選擇 ' },
      { text: '3 項基礎服務', strong: true },
      { text: ' 或 ' },
      { text: '精緻套裝項目', strong: true },
      { text: '。' },
    ],
  },
  {
    no: '02',
    name: '鎖定時間',
    parts: [{ text: '挑選您最方便的預約時段，並點擊送出。' }],
  },
  {
    no: '03',
    name: '繳納訂金',
    parts: [
      { text: '送出後系統會產生預約單，請於 ' },
      { text: '4 小時內', strong: true },
      { text: ' 完成 ' },
      { text: '$200 元', strong: true },
      { text: ' 的訂金繳費。' },
    ],
  },
  {
    no: '04',
    name: '預約成功',
    parts: [
      { text: '付款成功後，系統將會自動寄發「預約成功通知信」，這樣才算真正卡位成功喔！' },
    ],
  },
];


// === 行事曆狀態與邏輯 ===
const todayDate = new Date();
const baseYear = todayDate.getFullYear();
const baseMonth = todayDate.getMonth();
// 計算次月的年份與月份 (用於限制只能預約兩個月)
const maxYear = baseMonth === 11 ? baseYear + 1 : baseYear;
const maxMonth = baseMonth === 11 ? 0 : baseMonth + 1;

const currentYear = ref(todayDate.getFullYear());
const currentMonth = ref(todayDate.getMonth());
const selectedDay = ref(null);

const daysOfWeek = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];

const calendarDays = computed(() => {
  const firstDay = new Date(currentYear.value, currentMonth.value, 1).getDay();
  const daysInMonth = new Date(currentYear.value, currentMonth.value + 1, 0).getDate();
  const days = [];
  // 填充月初空白與日期
  for (let i = 0; i < firstDay; i++) days.push(null);
  for (let i = 1; i <= daysInMonth; i++) days.push(i);
  return days;
});

const canGoPrev = computed(() => currentYear.value > baseYear || (currentYear.value === baseYear && currentMonth.value > baseMonth));
const canGoNext = computed(() => currentYear.value < maxYear || (currentYear.value === maxYear && currentMonth.value < maxMonth));

const prevMonth = () => {
  if (!canGoPrev.value) return;
  if (currentMonth.value === 0) { currentMonth.value = 11; currentYear.value--; }
  else currentMonth.value--;
  selectedDay.value = null;
};
const nextMonth = () => {
  if (!canGoNext.value) return;
  if (currentMonth.value === 11) { currentMonth.value = 0; currentYear.value++; }
  else currentMonth.value++;
  selectedDay.value = null;
};
const selectDay = (day) => {
  if (!day) return;
  if (currentYear.value === baseYear && currentMonth.value === baseMonth && day < todayDate.getDate()) return;
  selectedDay.value = day;
};
const isToday = (day) => day === todayDate.getDate() && currentMonth.value === todayDate.getMonth() && currentYear.value === todayDate.getFullYear();
const isPastDate = (day) => {
  if (!day) return false;
  return currentYear.value === baseYear && currentMonth.value === baseMonth && day < todayDate.getDate();
};

// === 價目表資料 (從後端 API 取得) ===
const services = ref([]);
const items = ref([]);
const schedules = ref([]); // 存放班表資料

const loadData = async () => {
  try {
    const [resServices, resItems, resSchedules] = await Promise.all([
      axiosapi.post('/ajax/pages/WashServices/find', {}),
      axiosapi.post('/ajax/pages/WashPackageServiceItems/find', {}),
      axiosapi.get('/ajax/pages/WashWorkCalendars/all') // 取得營業日與班別 API
    ]);
    services.value = Array.isArray(resServices.data) ? resServices.data : (resServices.data?.list || []);
    items.value = Array.isArray(resItems.data) ? resItems.data : (resItems.data?.list || []);
    schedules.value = Array.isArray(resSchedules.data) ? resSchedules.data : (resSchedules.data?.list || []);

    // 檢查是否是從行事曆頁面點擊「重新選擇」過來的
    const isFromCalendar = sessionStorage.getItem('fromCalendar') === 'true';
    sessionStorage.removeItem('fromCalendar'); // 讀取後立即清除

    if (isFromCalendar) {
      if (authStore.id) {
        // 從行事曆過來，向後端載入先前保留的狀態
        const resState = await axiosapi.get('/ajax/pages/WashServices/getState');
        if (resState.data && resState.data.success && resState.data.state) {
          const state = resState.data.state;
          selectedBasicIds.value = state.basicIds || [];
          selectedPackageId.value = state.packageId || null;
        }
      }
    } else {
      // 否則（直接連結、重整或從其他頁面進入），清除前端的選擇與暫存紀錄，讓頁面自動刷新（呈現乾淨的狀態）
      selectedBasicIds.value = [];
      selectedPackageId.value = null;
      sessionStorage.removeItem('selectedServices');
      if (authStore.id) {
        await axiosapi.post('/ajax/pages/WashServices/clearState').catch(() => {});
      }
    }
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

onMounted(() => {
  loadData();
  checkContractModal();
});

// === 計算當月每天的班別狀態 ===
const monthShifts = computed(() => {
  const shifts = {};
  const monthPrefix = `${currentYear.value}-${String(currentMonth.value + 1).padStart(2, '0')}`;
  
  schedules.value.forEach(s => {
    // 只篩選當前畫面月份的資料且為工作日
    if (s.workDate && s.workDate.startsWith(monthPrefix) && s.isWorkday) {
      const day = parseInt(s.workDate.substring(8, 10), 10);
      if (!shifts[day]) {
        shifts[day] = { hasMorning: false, hasAfternoon: false };
      }
      if (s.periods && Array.isArray(s.periods)) {
        s.periods.forEach(p => {
          if (p.periodOfDayId === 1 && p.isActive) {
            shifts[day].hasMorning = true;
          }
          if (p.periodOfDayId === 2 && p.isActive) {
            shifts[day].hasAfternoon = true;
          }
        });
      }
    }
  });
  return shifts;
});

const isPackage = (sid) => {
  const relItems = items.value.filter(it => Number(it.serviceId || it.service?.serviceId) === Number(sid));
  if (relItems.length > 0) return relItems.some(it => it.typeCode === 'PACKAGE');
  return false;
};

const basicServices = computed(() => {
  return services.value
    .filter(s => s.isActive !== false && !isPackage(s.serviceId))
    .map(s => {
      const relItems = items.value.filter(it => Number(it.serviceId || it.service?.serviceId) === Number(s.serviceId));
      const totalMinutes = relItems.reduce((sum, it) => sum + (it.periodMinutes || 0), 0) || 20;
      return {
        id: s.serviceId,
        name: s.serviceName || s.service_name,
        estimatedTime: `${totalMinutes}分鐘`,
        originalPrice: s.fullPrice,
        salePrice: s.onsalePrice || null
      };
    });
});

const packageServices = computed(() => {
  return services.value
    .filter(s => s.isActive !== false && isPackage(s.serviceId))
    .map(s => {
      const relItems = items.value.filter(it => Number(it.serviceId || it.service?.serviceId) === Number(s.serviceId));
      const totalMinutes = relItems.reduce((sum, it) => sum + (it.periodMinutes || 0), 0) || 120;
      const hours = Math.floor(totalMinutes / 60);
      const minutes = totalMinutes % 60;
      let timeStr = hours > 0 ? `${hours}小時` : '';
      if (minutes > 0) timeStr += `${minutes}分鐘`;
      if (!timeStr) timeStr = '2小時'; // 如果皆為 0 的預設時長

      const descriptions = relItems.map(it => it.typeName).join('、');

      return {
        id: s.serviceId,
        name: s.serviceName || s.service_name,
        description: descriptions ? `包含：${descriptions}。` : '提供全方位尊榮寵物美容與護理服務。',
        estimatedTime: timeStr,
        originalPrice: s.fullPrice,
        salePrice: s.onsalePrice || null
      };
    });
});

// === 服務選取互斥與按鈕邏輯 ===
const selectedBasicIds = ref([]);
const selectedPackageId = ref(null);

const toggleBasic = (id) => {
  if (selectedPackageId.value !== null) {
    selectedPackageId.value = null; // 放開套裝服務
  }
  
  const index = selectedBasicIds.value.indexOf(id);
  if (index > -1) {
    selectedBasicIds.value.splice(index, 1); // 取消選取
  } else {
    if (selectedBasicIds.value.length >= 3) {
      selectedBasicIds.value.shift(); // 若已滿 3 個，移除最早選取的項目 (自動放棄第一個)
    }
    selectedBasicIds.value.push(id); // 加入新選取的項目
  }
};

const togglePackage = (id) => {
  if (selectedBasicIds.value.length > 0) {
    selectedBasicIds.value = []; // 放開基礎服務
  }
  
  // 點擊已選的則取消，點擊未選的則選取
  selectedPackageId.value = selectedPackageId.value === id ? null : id;
};

const isBookingReady = computed(() => selectedBasicIds.value.length === 3 || selectedPackageId.value !== null);
const buttonText = computed(() => {
  if (isBookingReady.value) {
    return '馬上預約';
  }
  if (selectedBasicIds.value.length > 0 && selectedBasicIds.value.length < 3) {
    return `再選擇 ${3 - selectedBasicIds.value.length} 個基礎方案`;
  }
  return '選擇服務方案';
});

// === 底部主要按鈕點擊事件 ===
const handleMainButtonClick = async () => {
  if (!isBookingReady.value) {
    // 若未滿足條件，平滑滾動至服務項目區塊
    const targetSection = document.getElementById('services-section');
    if (targetSection) {
      targetSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  } else {
    // 將已選擇的服務資料打包並存入 sessionStorage (不需要透過 DB)
    const selectedData = {
      basicIds: selectedBasicIds.value,
      packageId: selectedPackageId.value,
      basicServices: basicServices.value.filter(s => selectedBasicIds.value.includes(s.id)),
      packageService: packageServices.value.find(s => s.id === selectedPackageId.value) || null
    };
    sessionStorage.setItem('selectedServices', JSON.stringify(selectedData));
    
    // 呼叫後端 API 用物件保留狀態
    if (authStore.id) {
      try {
        await axiosapi.post('/ajax/pages/WashServices/saveState', selectedData);
      } catch (e) {
        console.error("Failed to save state to backend:", e);
      }
    }
    
    // 跳轉至預約行事曆頁面 (請根據實際的路由設定名稱調整路徑)
    sessionStorage.setItem('fromPriceList', 'true');
    router.push('/wash/calendar');
  }
};
</script>

<template>
  <div class="page-layout">
    <!-- 洗沐預約小契約 系統通知提醒彈窗 -->
    <div v-if="showContractModal" class="contract-modal-backdrop">
      <div class="spa-page">
        <!-- Hero -->
        <section class="hero">
          <img
            class="hero__image"
            src="@/assets/images/Generated_Image.png"
            alt="金毛犬與貓咪在溫馨的居家空間中，周圍擺放著寵物洗沐用品"
          />
          <div class="hero__tag">
            <div class="hero__tag-text">
              <p class="hero__eyebrow">毛孩専屬・洗沐預約</p>
              <h1 class="hero__title">歡迎來到<br />洗沐預約派對</h1>
            </div>
          </div>
        </section>

        <!-- Intro -->
        <section class="intro">
          <p class="intro__text">
            親愛的家長您好！為了給寶貝最舒適、最尊榮的洗沐享受，
            在我們開始挑選時間之前，請先花
            <strong>1 分鐘</strong>
            看一下我們的「預約小契約」唷！
          </p>
        </section>

        <!-- Steps -->
        <section class="steps">
          <h2 class="steps__title">
            輕鬆預約 4 步驟
          </h2>

          <ul class="ticket-list">
            <li v-for="step in steps" :key="step.no" class="ticket">
              <div class="ticket__stub">
                <span class="ticket__no">{{ step.no }}</span>
              </div>
              <div class="ticket__body">
                <h3 class="ticket__name">{{ step.name }}</h3>
                <p class="ticket__desc">
                  <template v-for="(part, i) in step.parts" :key="i">
                    <strong v-if="part.strong" class="ticket__highlight">{{ part.text }}</strong>
                    <template v-else>{{ part.text }}</template>
                  </template>
                </p>
              </div>
            </li>
          </ul>
        </section>

        <!-- Warning notice -->
        <section class="notice">
          <div class="notice__stamp">注意</div>
          <p class="notice__text">
            <strong>小提醒：</strong>
            剛送出預約單時<strong class="notice__strong">「並不代表預約成功」</strong>喔！
            如果超過 <strong class="notice__strong">4 小時</strong>未付訂金，
            系統會自動把名額釋出給其他毛孩。再請爸爸媽媽特別留意時間呀！
          </p>
        </section>

        <!-- CTA -->
        <section class="cta">
          <p class="cta__text">快去幫寶貝預約一場清爽舒服的 SPA 行程吧！我們超級期待見到你們！</p>
          <div class="cta__options mb-3 d-flex align-items-center justify-content-center gap-2">
            <input type="checkbox" id="dontShowCheckbox" v-model="dontShowToday" class="checkbox-input" />
            <label for="dontShowCheckbox" class="checkbox-label">不用再通知 (今天內不再顯示)</label>
          </div>
          <button class="cta__button" type="button" @click="confirmContract">
            確認
          </button>
        </section>
      </div>
    </div>
    
    <!-- 滿版 Banner：結合左側 1/2 資訊 -->
    <div class="banner-container">

      <img src="@/assets/images/banner.jpg" class="banner-img" alt="寵物美容">
      
      <!-- 左側 1/2 覆蓋區 -->
      <div class="banner-left-content d-flex flex-column justify-content-center align-items-center text-center">
        <img src="@/assets/images/logo.png" alt="毛起來" class="cute-logo" onerror="this.style.display='none'">
        <div class="elegant-subtitle mb-1">給毛孩最溫柔的呵護時光</div>
        <div class="text-golden fw-bold mb-3" style="font-size: 0.85rem; letter-spacing: 1px;">
          <i class="bi bi-clock me-1"></i>營業時間：09:00 - 18:00
        </div>
        
        <!-- 輕盈透明的迷你日曆班表 -->
        <div class="transparent-calendar">
          <div class="cal-header">
            <button @click="prevMonth" class="cal-btn" :disabled="!canGoPrev" :class="{ 'btn-disabled': !canGoPrev }"><i class="bi bi-chevron-left"></i></button>
            <span class="cal-title">{{ currentYear }} / {{ String(currentMonth + 1).padStart(2, '0') }}</span>
            <button @click="nextMonth" class="cal-btn" :disabled="!canGoNext" :class="{ 'btn-disabled': !canGoNext }"><i class="bi bi-chevron-right"></i></button>
          </div>
          <div class="cal-grid cal-weekdays">
            <div v-for="(day, index) in daysOfWeek" :key="day" class="cal-cell fw-bold" :class="{ 'text-weekend': index === 0 || index === 6 }">{{ day }}</div>
          </div>
          <div class="cal-grid cal-days">
            <div v-for="(day, index) in calendarDays" :key="index" class="cal-cell-wrap">
              <div v-if="day" class="cal-day-container">
                <div class="cal-cell cal-day-num" 
                     :class="{ 
                       'day-today': isToday(day), 
                       'day-selected': selectedDay === day, 
                       'text-weekend': !isPastDate(day) && ((index % 7) === 0 || (index % 7) === 6),
                       'day-past': isPastDate(day),
                       'day-available': !isPastDate(day) && monthShifts[day] && monthShifts[day].hasMorning && monthShifts[day].hasAfternoon
                     }" 
                     @click="selectDay(day)">
                  {{ day }}
                </div>
                <!-- 班別標示 (只有單班時才顯示) -->
                <div v-if="!isPastDate(day) && monthShifts[day] && !(monthShifts[day].hasMorning && monthShifts[day].hasAfternoon) && (monthShifts[day].hasMorning || monthShifts[day].hasAfternoon)" class="shift-indicators">
                  <span v-if="monthShifts[day].hasMorning" class="shift-dot morning" title="上午班"></span>
                  <span v-if="monthShifts[day].hasAfternoon" class="shift-dot afternoon" title="下午班"></span>
                </div>
              </div>
            </div>
          </div>
          <!-- 班別註解 -->
          <div class="calendar-legend">
            <span class="legend-item"><span class="legend-circle"></span>全天班</span>
            <span class="legend-item"><span class="shift-dot morning"></span>上午班</span>
            <span class="legend-item"><span class="shift-dot afternoon"></span>下午班</span>
          </div>
        </div>
      </div>
    </div>
        
    <div class="container pt-5 pb-5">
      <!-- 服務項目區塊 (左右排版) -->
      <section class="services-section" id="services-section">
        <div class="row g-4">
          <!-- 左半邊：基礎服務 -->
          <div class="col-12 col-md-6">
            <div class="text-center mb-4 header-block">
              <h3 class="section-title">基礎服務</h3>
              <p class="section-subtitle">日常基礎護理，讓毛孩保持乾淨清爽</p>
              <div class="badge-elegant-warning mt-2 shadow-sm">
                <i class="bi bi-exclamation-circle me-1"></i>基礎服務需任選 3 個，才能預約
              </div>
            </div>
            <div class="service-list">
              <div class="elegant-service-card" 
                   v-for="item in basicServices" 
                   :key="item.id"
                   :class="{ 'selected-basic-card': selectedBasicIds.includes(item.id) }"
                   @click="toggleBasic(item.id)">
                <!-- 左側：選取狀態指示圈 -->
                <div class="check-circle me-3">
                  <i class="bi" :class="selectedBasicIds.includes(item.id) ? 'bi-check-circle-fill' : 'bi-circle text-muted opacity-50'"></i>
                </div>
                <div class="d-flex flex-column flex-grow-1">
                  <div class="d-flex align-items-center">
                    <i class="bi bi-stars text-golden me-2"></i>
                    <span class="menu-name">{{ item.name }}</span>
                  </div>
                  <span class="text-muted mt-1" style="font-size: 0.8rem; margin-left: 24px;"><i class="bi bi-hourglass-split me-1"></i>{{ item.estimatedTime }}</span>
                </div>
                <div class="price-block text-end">
                  <span class="original-price text-muted text-decoration-line-through me-2" v-if="item.salePrice">NT$ {{ item.originalPrice }}</span>
                  <span class="menu-price text-golden">NT$ {{ item.salePrice || item.originalPrice }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 右半邊：套裝服務 -->
          <div class="col-12 col-md-6">
            <div class="text-center mb-4 header-block">
              <h3 class="section-title">套裝服務</h3>
              <p class="section-subtitle">全方位極致 SPA 享受</p>
              <div class="badge-elegant-premium mt-2 shadow-sm">
                <i class="bi bi-award me-1"></i>打造專屬長效極致護理
              </div>
            </div>
            <div class="service-list">
              <div class="elegant-service-card" 
                   v-for="item in packageServices" 
                   :key="item.id"
                   :class="{ 'selected-package-card': selectedPackageId === item.id }"
                   @click="togglePackage(item.id)">
                <!-- 左側：選取狀態指示圈 -->
                <div class="check-circle me-3">
                  <i class="bi" :class="selectedPackageId === item.id ? 'bi-check-circle-fill' : 'bi-circle text-muted opacity-50'"></i>
                </div>
                <div class="d-flex flex-column flex-grow-1">
                  <div class="d-flex align-items-center">
                    <i class="bi bi-gem text-golden me-2"></i>
                    <span class="menu-name">{{ item.name }}</span>
                  </div>
                  <span class="text-muted mt-1" style="font-size: 0.8rem; margin-left: 24px;"><i class="bi bi-hourglass-split me-1"></i>{{ item.estimatedTime }}</span>
                  <span class="mt-1" style="font-size: 0.8rem; color: #A28C7A; margin-left: 24px;">{{ item.description }}</span>
                </div>
                <div class="price-block text-end">
                  <span class="original-price text-muted text-decoration-line-through me-2" v-if="item.salePrice">NT$ {{ item.originalPrice }}</span>
                  <span class="menu-price text-golden">NT$ {{ item.salePrice || item.originalPrice }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      
        <!-- 動態價格說明 (優雅提示) -->
        <div class="text-end px-2 mt-4 mb-4">
          <span class="price-disclaimer">* 價格可能依節慶檔期或寵物實際體型微調，以結帳為準。</span>
        </div>
      </section>
    </div>

    <!-- 懸浮置底列：預約按鈕 -->
    <div class="sticky-bottom-bar shadow-lg">
      <div class="container text-center">
        <button 
          class="elegant-btn px-5" 
          :class="{ 'disabled-btn': !isBookingReady }"
          @click="handleMainButtonClick"
          style="max-width: 400px;">
          <i v-if="isBookingReady" class="bi bi-sparkles me-2" style="color: #FFF4E6; animation: star-blink 1.5s infinite;"></i>
          {{ buttonText }}
        </button>
      </div>
    </div>

  </div>
</template>

<style>
/* 整體佈局背景 */
.page-layout {
  font-family: 'Noto Sans TC', sans-serif;
  background-color: #FAF6F0; /* 暖米色系底色 */
  min-height: 100vh;
  padding-bottom: 100px; /* 預留底部按鈕空間 */
}

/* 滿版 Banner 區塊 */
.banner-container {
  position: relative;
  width: 100%;
}
.banner-img {
  width: 100%;
  height: auto;
  min-height: 380px; /* 確保有足夠高度容納日曆 */
  display: block;
  object-fit: cover;
  object-position: right center; /* 確保右側人物始終可見 */
}
/* 讓左側有淡出漸層底，避免文字看不清，同時不影響右側照片 */
.banner-left-content {
  position: absolute;
  top: 0; left: 0;
  width: 50%;
  height: 100%;
  padding: 20px;
  background: linear-gradient(to right, rgba(250, 246, 240, 0.85) 0%, rgba(250, 246, 240, 0.5) 65%, transparent 100%);
}

/* Logo 與可愛文案 */
.cute-logo {
  width: 150px; /* 放大 Logo 尺寸 */
  height: auto;
  margin: 0 auto 12px auto; /* 底部增加一點間隔，避免與下方文字太近 */
}
.cute-title {
  font-family: 'Noto Sans TC', sans-serif;
  color: #8C6A4F; /* 金棕色 */
  font-weight: 800;
  font-size: 1.9rem;
  margin-bottom: 2px;
  letter-spacing: 2px;
}
.elegant-subtitle {
  color: #A28C7A;
  font-size: 0.95rem;
  font-weight: 700;
  letter-spacing: 1px;
}

/* 警告提示標籤 */
.badge-elegant-warning {
  display: flex;
  align-items: center;
  justify-content: center;
  width: fit-content;
  margin: 0 auto;
  background-color: #FCFBF9;
  color: #D38282;
  padding: 8px 20px;
  border-radius: 30px;
  font-size: 0.9rem;
  font-weight: 700;
  border: 1px dashed #D38282;
}

/* 高級套裝標籤 (對稱用) */
.badge-elegant-premium {
  display: flex;
  align-items: center;
  justify-content: center;
  width: fit-content;
  margin: 0 auto;
  background: linear-gradient(135deg, #CBA37E 0%, #B58A63 100%);
  color: #FFFFFF;
  padding: 8px 20px;
  border-radius: 30px;
  font-size: 0.9rem;
  font-weight: 700;
}

/* --- 輕盈透明迷你日曆 --- */
.transparent-calendar {
  width: 100%;
  max-width: 260px;
  margin: 0 auto;
}
.cal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.cal-title {
  font-weight: 700;
  font-size: 1rem;
  color: #8C6A4F;
  letter-spacing: 1px;
}
.cal-btn {
  background: none;
  border: none;
  color: #CBA37E; 
  font-size: 1.1rem;
  cursor: pointer;
  padding: 0 5px;
  transition: color 0.2s;
}
.cal-btn:hover:not(:disabled) {
  color: #8C6A4F;
}
.btn-disabled {
  opacity: 0.3;
  cursor: not-allowed;
}
.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  gap: 2px;
}
.cal-weekdays {
  font-size: 0.65rem;
  letter-spacing: 1px;
  margin-bottom: 4px;
  color: #A28C7A;
}
.cal-cell-wrap {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 42px;
}
.cal-day-num {
  font-size: 0.85rem;
  width: 30px;
  height: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  color: #6C5B4E;
  font-weight: 600;
  border-radius: 50%;
  transition: all 0.2s ease;
  box-sizing: border-box;
}
.cal-day-num:hover:not(.day-selected):not(.day-past) {
  background-color: rgba(203, 163, 126, 0.15);
}
.day-past {
  color: #D6D0CB !important;
  text-decoration: line-through;
  cursor: not-allowed;
}
.text-weekend { color: #A28C7A !important; }
.day-today {
  border: 1px dashed #CBA37E;
}
.day-available {
  border: 2px solid #8C9C7E; /* 明顯的綠色粗邊框圈起 */
  color: #4A5B3D; /* 字體顏色加深增加對比度 */
  font-weight: 800; /* 字體加粗 */
}
.day-selected {
  background-color: #CBA37E !important;
  color: #FFFFFF !important;
  border-color: #CBA37E !important;
  box-shadow: 0 2px 8px rgba(203, 163, 126, 0.4);
}

.cal-day-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}
.shift-indicators {
  display: flex;
  gap: 4px;
  margin-top: 2px;
}
.shift-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
}
.shift-dot.morning { background-color: #5C8ED6; } /* 藍色 */
.shift-dot.afternoon { background-color: #E6C25C; } /* 黃色 */

.calendar-legend {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
  font-size: 0.75rem;
  color: #8C6A4F;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
.legend-circle {
  display: inline-block;
  width: 10px;
  height: 10px;
  border: 2px solid #8C9C7E;
  border-radius: 50%;
}

/* --- 服務項目列表 --- */
.services-section {
  position: relative;
  z-index: 2;
}
.section-title {
  color: #6C5B4E;
  font-weight: 800;
  font-size: 1.4rem;
}
.section-subtitle {
  color: #A28C7A;
  font-size: 0.9rem;
}
/* 讓兩邊標題區塊高度一致，解決不對齊問題 */
.header-block {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  min-height: 110px; 
}

/* 移除原本的 grid，因已改為左右 row 排版 */
.service-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.elegant-service-card {
  background-color: #FFFFFF; /* 純白底卡片 */
  border: none; /* 無邊框的輕量感設計 */
  box-shadow: 0 4px 15px rgba(140, 106, 79, 0.05); /* 輕柔陰影 */
  border-radius: 20px;
  padding: 13px 17px; /* 移除邊框後微調內距，保持圓潤飽滿 */
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
}

/* === 基礎服務：點選後的狀態 (活潑焦糖橘棕) === */
.elegant-service-card.selected-basic-card {
  background-color: #F8EFE6;
  box-shadow: 0 12px 30px rgba(140, 106, 79, 0.18);
  transform: translateY(-4px);
}
.elegant-service-card.selected-basic-card .menu-name {
  color: #5C4738;
  font-weight: 800; 
}
.elegant-service-card.selected-basic-card .menu-price {
  color: #C27A4B; /* 活潑焦糖橘棕色 */
  font-size: 1.3rem;
}
.elegant-service-card.selected-basic-card .text-muted { color: #A28C7A !important; }
.elegant-service-card.selected-basic-card .original-price { color: #D6D0CB !important; }
.elegant-service-card.selected-basic-card .check-circle i {
  color: #C27A4B;
  font-size: 1.6rem;
  animation: popIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
.elegant-service-card.selected-basic-card .bi-stars {
  color: #C27A4B !important;
  transform: scale(1.3);
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

/* === 套裝服務：點選後的狀態 (奢華香檳金棕) === */
.elegant-service-card.selected-package-card {
  background: linear-gradient(135deg, #F8EFE6 0%, #EFE5D8 100%); /* 帶有微漸層的高級底色 */
  box-shadow: 0 12px 35px rgba(181, 138, 99, 0.25); /* 陰影更偏金棕且範圍更大 */
  transform: translateY(-4px);
}
.elegant-service-card.selected-package-card .menu-name {
  color: #4A3B32; /* 比基礎服務更深的墨棕色，穩重奢華 */
  font-weight: 800; 
}
.elegant-service-card.selected-package-card .menu-price {
  color: #A2764E; /* 古典金棕色，較沉穩 */
  font-size: 1.3rem;
}
.elegant-service-card.selected-package-card .text-muted { color: #8F7B6B !important; }
.elegant-service-card.selected-package-card .original-price { color: #D6D0CB !important; }
.elegant-service-card.selected-package-card .check-circle i {
  color: #A2764E;
  font-size: 1.6rem;
  animation: popIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
.elegant-service-card.selected-package-card .bi-gem {
  color: #A2764E !important;
  transform: scale(1.3);
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

/* 卡片流光特效 */
.elegant-service-card::after {
  content: '';
  position: absolute;
  top: 0; left: -100%;
  width: 50%; height: 100%;
  background: linear-gradient(to right, rgba(255,255,255,0) 0%, rgba(203, 163, 126, 0.15) 50%, rgba(255,255,255,0) 100%);
  transform: skewX(-25deg);
  transition: all 0.6s ease;
}
.elegant-service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 25px rgba(140, 106, 79, 0.12); /* Hover 時使用柔和陰影 */
  background-color: #FCFBF9; /* Hover 時微微透出米色 */
}
.elegant-service-card:hover::after {
  left: 150%;
}
/* 左側核取圓圈圖示 */
.check-circle {
  font-size: 1.4rem;
  display: flex;
}
@keyframes popIn {
  0% { transform: scale(0); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}
.menu-name {
  font-size: 1.05rem;
  font-weight: 700;
  color: #6C5B4E;
  letter-spacing: 1px;
}
.text-golden { color: #CBA37E; }
.menu-price {
  font-weight: 700;
  color: #8C6A4F;
  font-size: 1.1rem;
}
.original-price { font-size: 0.85rem; }

/* 動態價格說明提示 */
.price-disclaimer {
  font-size: 0.8rem;
  color: #A28C7A;
}

/* --- 懸浮底部預約按鈕 --- */
.sticky-bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 16px 20px;
  z-index: 99;
  border-top: 1px solid #E8E3DD;
}

/* --- 3D 玻璃擬物化按鈕 (Glassmorphism & Neumorphism) --- */
.elegant-btn {
  position: relative;
  overflow: hidden;
  width: 100%;
  background: linear-gradient(135deg, #E2C2A4 0%, #CBA37E 50%, #B58A63 100%);
  color: #FFFFFF;
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-top: 2px solid rgba(255, 255, 255, 0.8); /* 頂部高光 */
  border-bottom: 1px solid rgba(140, 106, 79, 0.4); /* 底部暗面 */
  border-radius: 50px;
  padding: 16px 0;
  font-size: 1.15rem;
  font-weight: 800;
  letter-spacing: 2px;
  text-shadow: 0 1px 2px rgba(140, 106, 79, 0.6);
  box-shadow: 
    inset 0 4px 8px rgba(255, 255, 255, 0.5), /* 內發光 */
    inset 0 -4px 8px rgba(140, 106, 79, 0.4), /* 內陰影 */
    0 12px 24px rgba(203, 163, 126, 0.4), /* 外立體陰影 */
    0 4px 10px rgba(140, 106, 79, 0.2);
  backdrop-filter: blur(8px);
  transition: all 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

/* 滿足條件時的呼吸跳動特效，引誘點擊 */
.elegant-btn:not(.disabled-btn) {
  animation: pulse-glow 2s infinite ease-in-out;
}

/* 玻璃光澤掃光特效 */
.elegant-btn::before {
  content: '';
  position: absolute;
  top: 0; left: -100%;
  width: 50%; height: 100%;
  background: linear-gradient(to right, rgba(255,255,255,0) 0%, rgba(255,255,255,0.7) 50%, rgba(255,255,255,0) 100%);
  transform: skewX(-25deg);
  animation: glass-sweep 3s infinite cubic-bezier(0.4, 0, 0.2, 1);
}

/* 游標移入時按鈕稍微浮起、光暈擴大 */
.elegant-btn:hover:not(.disabled-btn) {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 
    inset 0 4px 8px rgba(255, 255, 255, 0.6),
    inset 0 -4px 8px rgba(140, 106, 79, 0.3),
    0 16px 30px rgba(203, 163, 126, 0.5),
    0 6px 15px rgba(140, 106, 79, 0.3);
  background: linear-gradient(135deg, #EAD0B6 0%, #D2AE8C 50%, #BD946E 100%);
  animation: none; /* Hover 時暫停呼吸 */
}
.elegant-btn:active {
  transform: translateY(2px) scale(0.98) !important;
  box-shadow: 
    inset 0 6px 12px rgba(140, 106, 79, 0.6), /* 按壓深陷感 */
    inset 0 -2px 4px rgba(255, 255, 255, 0.4),
    0 2px 5px rgba(203, 163, 126, 0.3) !important;
  border-top: 1px solid rgba(140, 106, 79, 0.4);
  background: linear-gradient(135deg, #B58A63 0%, #CBA37E 50%, #E2C2A4 100%);
  animation: none;
}

/* 未滿足條件時的「微透灰玻璃」立體按鈕 */
.disabled-btn {
  background: linear-gradient(135deg, #E8E3DD 0%, #D6D0CB 100%) !important;
  color: #8C6A4F !important;
  border: 1px solid rgba(255, 255, 255, 0.6) !important;
  border-top: 2px solid rgba(255, 255, 255, 0.9) !important;
  border-bottom: 1px solid rgba(140, 106, 79, 0.1) !important;
  box-shadow: 
    inset 0 4px 8px rgba(255, 255, 255, 0.6),
    inset 0 -4px 8px rgba(140, 106, 79, 0.05),
    0 8px 16px rgba(0, 0, 0, 0.05) !important;
  text-shadow: 0 1px 1px rgba(255, 255, 255, 0.8) !important;
  animation: none !important;
  cursor: pointer; /* 改回 pointer 讓顧客知道可以點擊觸發滾動 */
  transform: none !important;
}
.disabled-btn::before {
  display: none; /* 隱藏掃光特效 */
}

/* --- 特效動畫定義 --- */
@keyframes glass-sweep {
  0% { left: -100%; }
  20% { left: 200%; }
  100% { left: 200%; }
}
@keyframes pulse-glow {
  0% { box-shadow: inset 0 4px 8px rgba(255,255,255,0.5), inset 0 -4px 8px rgba(140,106,79,0.4), 0 12px 24px rgba(203,163,126,0.4); transform: scale(1); }
  50% { box-shadow: inset 0 4px 8px rgba(255,255,255,0.6), inset 0 -4px 8px rgba(140,106,79,0.3), 0 16px 32px rgba(203,163,126,0.7); transform: scale(1.02); }
  100% { box-shadow: inset 0 4px 8px rgba(255,255,255,0.5), inset 0 -4px 8px rgba(140,106,79,0.4), 0 12px 24px rgba(203,163,126,0.4); transform: scale(1); }
}
@keyframes star-blink {
  0%, 100% { opacity: 1; transform: scale(1) rotate(0deg); }
  50% { opacity: 0.6; transform: scale(1.3) rotate(15deg); text-shadow: 0 0 10px #FFFFFF; }
}

/* ---------- Booking Contract Modal & Spa Page Popup ---------- */
.contract-modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(74, 63, 49, 0.45); /* 半透明遮罩 */
  backdrop-filter: blur(8px); /* 毛玻璃模糊背景 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999; /* 最上層 */
  padding: 20px;
}

.contract-modal-backdrop .spa-page {
  --cream: #f8f4ea;
  --cream-deep: #f0ead9;
  --sage: #7c8b6f;
  --sage-deep: #5c6b50;
  --sage-pale: #e9edE1;
  --gold: #b8944a;
  --ink: #4a3f31;
  --ink-soft: #7a6f5e;
  --rust: #a85c32;
  --paper: #fffdf8;

  font-family: 'Noto Sans TC', 'PingFang TC', 'Microsoft JhengHei', sans-serif;
  color: var(--ink);
  background: var(--cream);
  max-width: 640px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  border-radius: 28px;
  box-shadow: 0 20px 60px rgba(74, 63, 49, 0.25);
  animation: modal-fade-in 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  border: 1px solid var(--cream-deep);
}

@keyframes modal-fade-in {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.contract-modal-backdrop h1, 
.contract-modal-backdrop h2, 
.contract-modal-backdrop h3 {
  font-family: 'Noto Serif TC', 'Songti TC', serif;
  margin: 0;
}

/* ---------- Hero ---------- */
.contract-modal-backdrop .hero {
  position: relative;
}

.contract-modal-backdrop .hero__image {
  display: block;
  width: 100%;
  height: 290px;
  object-fit: cover;
  object-position: center 30%;
}

.contract-modal-backdrop .hero__tag {
  position: relative;
  margin: -22px 20px 0;
  background: rgba(255, 253, 248, 0.72);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: 0 8px 24px rgba(74, 63, 49, 0.12);
  border: 1px solid rgba(240, 234, 217, 0.6);
}

.contract-modal-backdrop .hero__eyebrow {
  margin: 0 0 4px;
  font-size: 12px;
  letter-spacing: 0.12em;
  color: var(--gold);
  font-weight: 700;
}

.contract-modal-backdrop .hero__title {
  font-size: 21px;
  line-height: 1.35;
  color: var(--ink);
}

/* ---------- Intro ---------- */
.contract-modal-backdrop .intro {
  padding: 22px 26px 4px;
}

.contract-modal-backdrop .intro__text {
  margin: 0;
  font-size: 14.5px;
  line-height: 1.9;
  color: var(--ink-soft);
}

.contract-modal-backdrop .intro__text strong {
  color: var(--sage-deep);
}

/* ---------- Steps ---------- */
.contract-modal-backdrop .steps {
  padding: 22px 20px 6px;
}

.contract-modal-backdrop .steps__title {
  font-size: 17px;
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--sage-deep);
  margin-bottom: 14px;
  padding: 0 6px;
}

.contract-modal-backdrop .ticket-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contract-modal-backdrop .ticket {
  display: flex;
  background: var(--paper);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 14px rgba(74, 63, 49, 0.07);
  position: relative;
}

.contract-modal-backdrop .ticket::after {
  content: '';
  position: absolute;
  left: 74px;
  top: 0;
  bottom: 0;
  width: 0;
  border-left: 2px dashed var(--cream-deep);
}

.contract-modal-backdrop .ticket__stub {
  flex-shrink: 0;
  width: 74px;
  background: var(--sage-pale);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 14px 0;
}

.contract-modal-backdrop .ticket__no {
  font-family: 'Noto Serif TC', serif;
  font-size: 22px;
  font-weight: 700;
  color: var(--sage-deep);
}

.contract-modal-backdrop .ticket__body {
  padding: 14px 16px 14px 18px;
  flex: 1;
}

.contract-modal-backdrop .ticket__name {
  font-size: 15px;
  color: var(--ink);
  margin-bottom: 5px;
}

.contract-modal-backdrop .ticket__desc {
  margin: 0;
  font-size: 13.5px;
  line-height: 1.75;
  color: var(--ink-soft);
}

.contract-modal-backdrop .ticket__highlight {
  color: var(--gold);
  background: linear-gradient(transparent 60%, rgba(184, 148, 74, 0.18) 60%);
  font-weight: 700;
  padding: 0 1px;
}

/* ---------- Notice ---------- */
.contract-modal-backdrop .notice {
  margin: 22px 20px 0;
  background: #fbf1e9;
  border: 1.5px dashed var(--rust);
  border-radius: 16px;
  padding: 16px 18px 16px 76px;
  position: relative;
}

.contract-modal-backdrop .notice__stamp {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%) rotate(-8deg);
  border: 2px solid var(--rust);
  color: var(--rust);
  font-family: 'Noto Serif TC', serif;
  font-weight: 700;
  font-size: 13px;
  padding: 4px 6px;
  border-radius: 8px;
  letter-spacing: 0.05em;
}

.contract-modal-backdrop .notice__text {
  margin: 0;
  font-size: 13.5px;
  line-height: 1.8;
  color: #6b4326;
}

.contract-modal-backdrop .notice__strong {
  color: var(--rust);
}

/* ---------- Checkbox Custom Options ---------- */
.contract-modal-backdrop .checkbox-input {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: var(--sage-deep);
}

.contract-modal-backdrop .checkbox-label {
  cursor: pointer;
  font-size: 13.5px;
  color: var(--ink-soft);
  user-select: none;
}

/* ---------- CTA ---------- */
.contract-modal-backdrop .cta {
  padding: 26px 26px 30px;
  text-align: center;
}

.contract-modal-backdrop .cta__text {
  margin: 0 0 16px;
  font-size: 14px;
  color: var(--ink-soft);
  line-height: 1.7;
}

.contract-modal-backdrop .cta__button {
  appearance: none;
  border: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 48px;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--sage) 0%, var(--sage-deep) 100%);
  color: var(--cream);
  font-size: 15.5px;
  font-weight: 700;
  letter-spacing: 0.02em;
  box-shadow: 0 10px 24px rgba(92, 107, 80, 0.35);
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.contract-modal-backdrop .cta__button:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 30px rgba(92, 107, 80, 0.42);
}

.contract-modal-backdrop .cta__button:focus-visible {
  outline: 3px solid var(--gold);
  outline-offset: 3px;
}

/* ---------- Responsive ---------- */
@media (max-width: 420px) {
  .contract-modal-backdrop .hero__image {
    height: 200px;
  }
  .contract-modal-backdrop .hero__tag {
    flex-direction: column;
    align-items: flex-start;
    margin: -36px 14px 0;
  }
  .contract-modal-backdrop .ticket::after {
    left: 62px;
  }
  .contract-modal-backdrop .ticket__stub {
    width: 62px;
  }
}
/* RWD 手機版微調 */
@media (max-width: 768px) {
  .banner-left-content {
    width: 65%; /* 手機上多佔一點左側空間給日曆，右側仍保留人物 */
    background: linear-gradient(to right, rgba(250, 246, 240, 0.95) 0%, rgba(250, 246, 240, 0.85) 75%, transparent 100%);
  }
}
</style>
