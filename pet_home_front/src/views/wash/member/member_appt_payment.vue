<template>
  <div class="payment-manager">
    <!-- Header -->
    <div class="page-header">
      <div>
        <h1>我的付款紀錄</h1>
        <div class="subtitle-en">PAYMENT HISTORY</div>
        <p class="subtitle">查看訂單付款與訂金收付的所有交易紀錄</p>
      </div>
    </div>

    <div v-if="forwardedPayment" class="forwarded-booking-summary mb-4 p-4 shadow-sm">
      <h4 class="fw-bold mb-3">待付款項目已選擇</h4>
      <div class="row">
        <div class="col-md-3 mb-2">
          <div class="summary-label">關聯預約單號</div>
          <div class="summary-value">{{ forwardedPayment.apptNo || '—' }}</div>
        </div>
        <div class="col-md-3 mb-2">
          <div class="summary-label">付款用途</div>
          <div class="summary-value">{{ getPurposeLabel(forwardedPayment.purpose ?? 2) }}</div>
        </div>
        <div class="col-md-3 mb-2">
          <div class="summary-label">應付金額</div>
          <div class="summary-value">NT$ {{ forwardedPayment.amount }}</div>
        </div>
        <div class="col-md-3 mb-2">
          <div class="summary-label">付款期限</div>
          <div class="summary-value">{{ formatDateTime(forwardedPayment.deadline) || '—' }}</div>
        </div>
      </div>
      <div class="text-muted mt-3 d-flex justify-content-between align-items-center">
        <span>請於期限前完成付款，逾時預約將自動取消。</span>
        <button class="btn btn-primary" style="padding: 8px 18px;" @click="payForwardedItem">
          立即付款
        </button>
      </div>
    </div>

    <!-- Stats -->
    <div class="stats-grid">
      <div class="stat-card">
        <span class="stat-label">待付款項目</span>
        <span class="stat-value">{{ pendingCount }} 件</span>
      </div>
      <div class="stat-card">
        <span class="stat-label">本月實付總額</span>
        <span class="stat-value">NT$ {{ monthRevenue }}</span>
      </div>
    </div>

    <!-- Tabs -->
    <div class="tabs">
      <button 
        v-for="purpose in purposeOptions" 
        :key="purpose.value"
        class="tab"
        :class="{ active: searchFilter.purpose === purpose.value }"
        @click="searchFilter.purpose = purpose.value"
      >
        {{ purpose.label }}
        <span class="tab-badge">{{ purposeCounts[purpose.value] || 0 }}</span>
      </button>
    </div>

    <!-- Toolbar -->
    <div class="table-toolbar" style="display: flex; justify-content: space-between; align-items: center; width: 100%;">
      <div style="display: flex; gap: 12px; align-items: center;">
        <input 
          v-model="searchFilter.keyword" 
          class="search-input" 
          placeholder="搜尋付款單號 / 交易序號..." 
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
              <th style="letter-spacing: 2px;">付款單號 / 日期</th>
              <th style="letter-spacing: 2px;">付款類型</th>
              <th style="text-align: right; padding-right: 24px; letter-spacing: 2px;">金額</th>
              <th style="text-align: center; letter-spacing: 2px;">付款方式</th>
              <th style="text-align: center; letter-spacing: 2px;">付款狀態</th>
              <th style="width:140px; text-align:center; letter-spacing: 2px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="(payment, index) in paginatedPayments" :key="payment.payment_id || payment.paymentId">
              <tr>
                <td>
                  <button @click="toggleExpand(payment.payment_id || payment.paymentId)" class="btn-icon">
                    <span v-if="expandedRows.includes(payment.payment_id || payment.paymentId)">▼</span>
                    <span v-else>▶</span>
                  </button>
                </td>
                <td style="text-align: center; font-weight: 600; color: #5A6B7C;">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                <td>
                  <div style="font-family: monospace; font-weight: 700; color: #B58A63;">{{ payment.pay_no || payment.payNo }}</div>
                  <div class="muted" style="margin-top: 4px;">{{ formatJustDate(payment.created_at || payment.createdAt) }} {{ formatJustTime(payment.created_at || payment.createdAt) }}</div>
                </td>
                <td>
                  <span class="badge" :class="getPurposeBadgeClass(payment.payment_purpose ?? payment.paymentPurpose)">
                    {{ getPurposeLabel(payment.payment_purpose ?? payment.paymentPurpose) }}
                  </span>
                  <div class="muted" style="margin-top: 4px; color: #8C6A4F;">
                    <span v-if="payment.appt_order_id || payment.apptOrderId">關聯預約訂單 #{{ payment.appt_order_id || payment.apptOrderId }}</span>
                    <span v-else-if="(payment.payment_purpose ?? payment.paymentPurpose) === 1">✨ 儲值 +{{ payment.point_topup_bonus ?? payment.pointTopupBonus ?? 0 }} 贈點</span>
                    <span v-else>—</span>
                  </div>
                </td>
                <td style="text-align: right; padding-right: 24px;">
                  <div 
                    class="price-new" 
                    :style="(payment.transaction_type ?? payment.transactionType) === 1 ? 'color:#8C9C7E;' : ''"
                  >
                    <span class="currency-symbol">$</span>{{ (payment.transaction_type ?? payment.transactionType) === 1 ? '+' : '-' }}{{ payment.amount }}
                  </div>
                  <div class="price-old" v-if="(payment.payment_purpose ?? payment.paymentPurpose) === 1 && (payment.point_topup_bonus ?? payment.pointTopupBonus) > 0">
                    含贈點 {{ payment.point_topup_bonus ?? payment.pointTopupBonus }}
                  </div>
                </td>
                <td style="text-align: center;">
                  <span class="badge badge-basic">{{ getMethodLabel(payment.payment_method ?? payment.paymentMethod) }}</span>
                </td>
                <td style="text-align: center;">
                  <span class="badge" :class="getStatusBadgeClass(payment.payment_status ?? payment.paymentStatus)">
                    {{ getStatusLabel(payment.payment_status ?? payment.paymentStatus) }}
                  </span>
                  <div class="muted" style="margin-top: 4px; font-size: 11px;" v-if="payment.paid_at || payment.paidAt">
                    {{ formatDateTime(payment.paid_at || payment.paidAt) }}
                  </div>
                </td>
                <td>
                  <div class="actions" style="justify-content: center;">
                    <button 
                      v-if="(payment.payment_status ?? payment.paymentStatus) === 0" 
                      class="btn btn-sm btn-primary" 
                      style="padding: 4px 10px; font-size: 11px; border-radius: 8px; font-weight: 600;"
                      @click="goToPayment(payment)"
                    >
                      前往付款
                    </button>
                    <button 
                      v-else-if="(payment.payment_status ?? payment.paymentStatus) === 1 && (payment.transaction_type ?? payment.transactionType) === 0 && [0,2].includes(payment.payment_purpose ?? payment.paymentPurpose)" 
                      class="btn btn-sm btn-refund" 
                      style="padding: 4px 10px; font-size: 11px; border-radius: 8px; font-weight: 600;"
                      @click="requestRefund(payment)"
                    >
                      申請退款
                    </button>
                    <span v-else class="muted">—</span>
                  </div>
                </td>
              </tr>

              <tr v-if="expandedRows.includes(payment.payment_id || payment.paymentId)" class="expanded-row-bg">
                <td colspan="8" style="padding: 18px 24px;">
                  <div class="appt-detail-card">
                    <div class="appt-detail-header">
                      <span class="appt-detail-title">📋 交易明細資訊</span>
                      <span class="appt-detail-no">付款單號：{{ payment.pay_no || payment.payNo }}</span>
                    </div>

                    <div class="detail-grid">
                      <div class="detail-grid-item">
                        <span class="note-label">交易序號</span>
                        <span class="note-content" style="font-family: monospace;">{{ payment.transaction_no || payment.transactionNo || '尚未產生' }}</span>
                      </div>
                      <div class="detail-grid-item">
                        <span class="note-label">交易性質</span>
                        <span class="note-content">{{ (payment.transaction_type ?? payment.transactionType) === 1 ? '退款 (Refund)' : '付款 (Payment)' }}</span>
                      </div>
                      <div class="detail-grid-item">
                        <span class="note-label">建立時間</span>
                        <span class="note-content">{{ formatDateTime(payment.created_at || payment.createdAt) || '—' }}</span>
                      </div>
                      <div class="detail-grid-item">
                        <span class="note-label">完成時間</span>
                        <span class="note-content">{{ formatDateTime(payment.paid_at || payment.paidAt) || '尚未付款' }}</span>
                      </div>
                      <div class="detail-grid-item">
                        <span class="note-label">關聯預約訂單</span>
                        <span class="note-content">{{ (payment.appt_order_id || payment.apptOrderId) ? `#${payment.appt_order_id || payment.apptOrderId}` : '—' }}</span>
                      </div>
                      <div class="detail-grid-item" v-if="(payment.payment_purpose ?? payment.paymentPurpose) === 3">
                        <span class="note-label">原訂單編號</span>
                        <span class="note-content" style="font-family: monospace;">{{ payment.orig_order_no || payment.origOrderNo || '—' }}</span>
                      </div>
                      <div class="detail-grid-item" v-if="(payment.payment_method ?? payment.paymentMethod) === 2">
                        <span class="note-label">使用卡片</span>
                        <span class="note-content">{{ (payment.saved_card_id || payment.savedCardId) ? `卡片 ID #${payment.saved_card_id || payment.savedCardId}` : '—' }}</span>
                      </div>
                      <div class="detail-grid-item">
                        <span class="note-label">訂金金額</span>
                        <span class="note-content">NT$ {{ payment.apptDepositAmount ?? payment.appt_deposit_amount ?? '—' }}</span>
                      </div>
                      <div class="detail-grid-item">
                        <span class="note-label">尾款金額</span>
                        <span class="note-content">
                          <span v-if="(payment.apptTotalAmount ?? payment.appt_total_amount) !== null && (payment.apptDepositAmount ?? payment.appt_deposit_amount) !== null">
                             NT$ {{ payment.apptTotalAmount ?? payment.appt_total_amount }}
                          </span>
                          <span v-else>—</span>
                        </span>
                      </div>
                    </div>

                    <div v-if="(payment.payment_purpose ?? payment.paymentPurpose) === 1" class="bonus-box mt-3">
                      <div class="row">
                        <div class="col-md-4">
                          <div class="summary-label">儲值本金</div>
                          <div class="summary-value">NT$ {{ payment.point_topup_amount ?? payment.pointTopupAmount ?? 0 }}</div>
                        </div>
                        <div class="col-md-4">
                          <div class="summary-label">贈送點數</div>
                          <div class="summary-value" style="color:#B58A63;">+{{ payment.point_topup_bonus ?? payment.pointTopupBonus ?? 0 }} 點</div>
                        </div>
                        <div class="col-md-4">
                          <div class="summary-label">合計入帳點數</div>
                          <div class="summary-value">{{ (payment.point_topup_amount ?? payment.pointTopupAmount ?? 0) + (payment.point_topup_bonus ?? payment.pointTopupBonus ?? 0) }} 點</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
            </template>
            <tr v-if="filteredPayments.length === 0">
              <td colspan="8" class="empty">找不到符合條件的付款紀錄。</td>
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


  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import axiosapi from '@/plugins/axios.js';
import { userAuthStore } from '@/stores/auth.js';

const authStore = userAuthStore();
const userStore = {
  get id() { return authStore.id; },
  get name() { return authStore.name; },
  get token() { return authStore.accessToken; }
};
const route = useRoute();
const router = useRouter();
const forwardedPayment = ref(null);
const memberPointId = ref(null);
const savedCards = ref([]);
const isSubmitting = ref(false);

const methodMap = { 0: '現金', 1: 'ATM轉帳', 2: '刷卡', 3: 'LINE Pay' };
const purposeOptions = [
  { label: '全部紀錄', value: 'all' },
  //{ label: '訂單付款', value: 0 },
  { label: '訂金收取', value: 2 },
  { label: '訂金退款', value: 3 },
];

const generatePayNo = (dateStr) => {
  const date = dateStr ? dateStr : new Date().toISOString().split('T')[0];
  const cleanDate = date.replace(/-/g, '');

  const sameDateCount = payments.value.filter(p => {
    const pDate = (p.created_at || p.createdAt || '').split('T')[0];
    return pDate === date;
  }).length;

  let sequence = sameDateCount + 1;
  let payNo = `PAY-${cleanDate}-${String(sequence).padStart(3, '0')}`;

  while (payments.value.some(p => (p.pay_no || p.payNo) === payNo)) {
    sequence++;
    payNo = `PAY-${cleanDate}-${String(sequence).padStart(3, '0')}`;
  }

  return payNo;
};

const generateTransactionNo = () => `TXN${Date.now()}${Math.floor(Math.random() * 1000)}`;



const loadForwardedPayment = () => {
  const saved = sessionStorage.getItem('pendingPaymentRequest');
  if (saved) {
    forwardedPayment.value = JSON.parse(saved);
  }
};

const loadMemberPoint = async () => {
  // 系統已移除會員點數功能
  memberPointId.value = null;
};

const fetchSavedCards = async () => {
  savedCards.value = [
    { saved_card_id: 1, card_brand: 'VISA', card_last4: '1234' },
    { saved_card_id: 2, card_brand: 'Mastercard', card_last4: '5678' }
  ];
};

const searchFilter = reactive({
  purpose: 'all',
  keyword: '',
  date: ''
});

const expandedRows = ref([]);



const payments = ref([]);
const purposeCounts = computed(() => {
  const counts = {
    all: 0,
    0: 0,
    2: 0,
    3: 0
  };
  payments.value.forEach(item => {
    const mId = item.member_id ?? item.memberId ?? '';
    if (userStore.id && String(mId) !== String(userStore.id)) return;

    const purpose = item.payment_purpose ?? item.paymentPurpose;
    if (purpose === 1) return; // Completely ignore points top-ups

    if (purpose === 0 || purpose === 2 || purpose === 3) {
      counts[purpose]++;
    }
    counts.all++;
  });
  return counts;
});

const currentPage = ref(1);
const pageSize = ref(10);
const totalPages = computed(() => Math.ceil(filteredPayments.value.length / pageSize.value) || 1);

const paginatedPayments = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredPayments.value.slice(start, start + pageSize.value);
});

const loadPayments = async () => {
  try {
    const payload = {};
    if (userStore.id) {
      payload.memberId = userStore.id;
    }
    if (searchFilter.date) {
      payload.date = searchFilter.date;
    }
    const response = await axiosapi.post('/ajax/pages/WashPayments/find', payload, {
      　//headers: { "Authorization": `Bearer ${userStore.token}` }
    });
    payments.value = Array.isArray(response.data) ? response.data : (response.data?.list || []);


  } catch (error) {
    console.error("載入付款資料失敗:", error);
    Swal.fire('錯誤', '無法取得付款資料庫連線，請稍後再試。', 'error');
  }
};

watch(() => searchFilter.date, () => {
  currentPage.value = 1;
  loadPayments();
});

watch([() => searchFilter.purpose, () => searchFilter.keyword], () => {
  currentPage.value = 1;
});

onMounted(async () => {
  loadForwardedPayment();
  await Promise.all([loadPayments(), loadMemberPoint(), fetchSavedCards()]);

  if (route.query.linePay === 'success') {
    const amount = route.query.amount || '200';
    const txnId = route.query.transactionId || '';
    const orderId = route.query.orderId || '';
    const remaining = route.query.remaining || '0';

    Swal.fire({
      title: '付款完成',
      html: `
        <div style="text-align: center; font-size: 14.5px; line-height: 1.8; color: #5A4A3E;">
          您的訂金NT$ ${amount}元已使用Line Pay付款完成，<br/>
          付款單號：${txnId}<br/>
          訂單編號：${orderId}<br/>
          尚未繳納餘額：NT$ ${remaining}
        </div>
      `,
      icon: 'success',
      confirmButtonText: '確定',
      confirmButtonColor: '#CBA37E'
    });
    router.replace({ path: route.path, query: {} });
  } else if (route.query.linePay === 'failed') {
    Swal.fire({
      title: '付款失敗',
      text: '您的 LINE Pay 付款失敗或已被取消。',
      icon: 'error',
      confirmButtonColor: '#CBA37E'
    });
    router.replace({ path: route.path, query: {} });
  }
});

const filteredPayments = computed(() => {
  return payments.value.filter(item => {
    const purpose = item.payment_purpose ?? item.paymentPurpose;
    if (purpose === 1) return false; // Filter out points top-up completely

    const date = item.created_at ?? item.createdAt;
    const payNo = item.pay_no ?? item.payNo ?? '';
    const txnNo = item.transaction_no ?? item.transactionNo ?? '';
    const mId = item.member_id ?? item.memberId ?? '';

    // Only show payments belonging to the logged in member
    if (userStore.id && String(mId) !== String(userStore.id)) return false;

    if (searchFilter.purpose !== 'all' && purpose !== searchFilter.purpose) return false;
    if (searchFilter.date && formatJustDate(date) !== searchFilter.date) return false;
    if (searchFilter.keyword) {
      const kw = searchFilter.keyword.toLowerCase();
      return (
        payNo.toLowerCase().includes(kw) ||
        txnNo.toLowerCase().includes(kw)
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
  return parts.length > 1 ? parts[1].substring(0, 5) : '';
};
const formatDateTime = (dt) => {
  if (!dt) return '';
  return dt.replace('T', ' ').substring(0, 16);
};

const todayStr = new Date().toISOString().split('T')[0];
const currentMonthStr = todayStr.substring(0, 7);

const pendingCount = computed(() => payments.value.filter(p => (p.payment_status ?? p.paymentStatus) === 0).length);

const monthRevenue = computed(() => payments.value
  .filter(p => {
    const isSuccess = (p.payment_status ?? p.paymentStatus) === 1;
    const date = formatJustDate(p.paid_at || p.paidAt || p.created_at || p.createdAt);
    return isSuccess && date.startsWith(currentMonthStr);
  })
  .reduce((sum, p) => {
    const isPayment = (p.transaction_type ?? p.transactionType) === 0;
    if (isPayment) {
      return sum + (p.amount || 0);
    } else {
      return sum - (p.amount || 0);
    }
  }, 0)
);



const toggleExpand = (id) => {
  const index = expandedRows.value.indexOf(id);
  if (index > -1) {
    expandedRows.value.splice(index, 1);
  } else {
    expandedRows.value.push(id);
  }
};

const getPurposeLabel = (p) => ['訂單付款', '點數儲值', '訂金收取', '訂金退款'][p] ?? '未知';
const getPurposeBadgeClass = (p) => {
  const styles = [
    'badge-basic',    // 0 訂單付款
    'badge-package',  // 1 點數儲值
    'badge-active',   // 2 訂金收取
    'badge-inactive'  // 3 訂金退款
  ];
  return styles[p] || 'badge-basic';
};

const getMethodLabel = (m) => methodMap[m] ?? '未知';

const getStatusLabel = (s) => ['待處理', '付款成功', '付款失敗'][s] ?? '未知';
const getStatusBadgeClass = (s) => {
  const styles = ['badge-package', 'badge-active', 'badge-danger'];
  return styles[s] || 'badge-basic';
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



const goToPayment = async (payment) => {
  const { value: paymentMethod } = await Swal.fire({
    title: '請選擇付款方式',
    html: `
      <div style="text-align: left; padding: 10px 20px;">
        <p style="margin-bottom: 12px; font-weight: 600; color: #8C6A4F;">付款單號：${payment.pay_no || payment.payNo}</p>
        <p style="margin-bottom: 12px; font-weight: 600; color: #B58A63;">應付金額：NT$ ${payment.amount}</p>
        <div style="display: flex; flex-direction: column; gap: 10px;">
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; color: #5A4A3E;">
            <input type="radio" name="payMethod" value="0" checked style="accent-color: #CBA37E;" /> 1. 現金付款
          </label>
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; color: #5A4A3E;">
            <input type="radio" name="payMethod" value="1" style="accent-color: #CBA37E;" /> 2. ATM 轉帳
          </label>
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; color: #5A4A3E;">
            <input type="radio" name="payMethod" value="2" style="accent-color: #CBA37E;" /> 3. 刷卡
          </label>
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; color: #5A4A3E;">
            <input type="radio" name="payMethod" value="3" style="accent-color: #CBA37E;" /> 4. LINE Pay
          </label>
        </div>
      </div>
    `,
    showCancelButton: true,
    confirmButtonText: '確認付款',
    cancelButtonText: '取消',
    preConfirm: () => {
      const checkedRadio = document.querySelector('input[name="payMethod"]:checked');
      return checkedRadio ? checkedRadio.value : null;
    }
  });

  if (paymentMethod !== null && paymentMethod !== undefined) {
    const methodName = methodMap[Number(paymentMethod)];
    const nowStr = new Date().toISOString().replace('T', ' ').substring(0, 19);
    const isLinePay = Number(paymentMethod) === 3;
    try {
      const id = payment.payment_id || payment.paymentId;
      const payload = {
        ...payment,
        paymentMethod: Number(paymentMethod),
        payment_method: Number(paymentMethod),
        paymentStatus: isLinePay ? 0 : 1,
        payment_status: isLinePay ? 0 : 1,
        transactionNo: isLinePay ? (payment.transactionNo || payment.transaction_no || '') : generateTransactionNo(),
        transaction_no: isLinePay ? (payment.transactionNo || payment.transaction_no || '') : generateTransactionNo(),
        paidAt: isLinePay ? null : nowStr,
        paid_at: isLinePay ? null : nowStr
      };
      const response = await axiosapi.put(`/ajax/pages/WashPayments/${id}`, payload, {
        headers: { "Authorization": `Bearer ${userStore.token}` }
      });
      let resData = response.data;
      if (typeof resData === 'string') {
        try { resData = JSON.parse(resData); } catch (e) {}
      }
      if (resData && resData.success) {
        if (isLinePay) {
          await startLinePayPayment(id);
          return;
        }
        await Swal.fire({
          title: '付款成功',
          text: `您已使用 [${methodName}] 完成付款。`,
          icon: 'success'
        });
        loadPayments();
      } else {
        throw new Error(resData?.message || '付款更新失敗');
      }
    } catch (error) {
      console.error(error);
      Swal.fire('錯誤', error.message || '付款失敗，請稍後再試。', 'error');
    }
  }
};

const payForwardedItem = async () => {
  if (!forwardedPayment.value) return;
  const { value: paymentMethod } = await Swal.fire({
    title: '請選擇付款方式',
    html: `
      <div style="text-align: left; padding: 10px 20px;">
        <p style="margin-bottom: 12px; font-weight: 600; color: #8C6A4F;">關聯預約：${forwardedPayment.value.apptNo || '—'}</p>
        <p style="margin-bottom: 12px; font-weight: 600; color: #B58A63;">應付金額：NT$ ${forwardedPayment.value.amount}</p>
        <div style="display: flex; flex-direction: column; gap: 10px;">
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; color: #5A4A3E;">
            <input type="radio" name="fwdPayMethod" value="0" checked style="accent-color: #CBA37E;" /> 1. 現金付款
          </label>
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; color: #5A4A3E;">
            <input type="radio" name="fwdPayMethod" value="1" style="accent-color: #CBA37E;" /> 2. ATM 轉帳
          </label>
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; color: #5A4A3E;">
            <input type="radio" name="fwdPayMethod" value="2" style="accent-color: #CBA37E;" /> 3. 刷卡
          </label>
          <label style="display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; color: #5A4A3E;">
            <input type="radio" name="fwdPayMethod" value="3" style="accent-color: #CBA37E;" /> 4. LINE Pay
          </label>
        </div>
      </div>
    `,
    showCancelButton: true,
    confirmButtonText: '確認付款',
    cancelButtonText: '取消',
    preConfirm: () => {
      const checkedRadio = document.querySelector('input[name="fwdPayMethod"]:checked');
      return checkedRadio ? checkedRadio.value : null;
    }
  });

  if (paymentMethod !== null && paymentMethod !== undefined) {
    const nowStr = new Date().toISOString().replace('T', ' ').substring(0, 19);
    const payNo = generatePayNo();
    const isLinePay = Number(paymentMethod) === 3;
    try {
      const payload = {
        memberId: userStore.id,
        member_id: userStore.id,
        memberPointId: memberPointId.value,
        member_point_id: memberPointId.value,
        apptOrderId: forwardedPayment.value.apptOrderId,
        appt_order_id: forwardedPayment.value.apptOrderId,
        payNo: payNo,
        pay_no: payNo,
        paymentPurpose: forwardedPayment.value.purpose ?? 2,
        payment_purpose: forwardedPayment.value.purpose ?? 2,
        transactionType: 0,
        transaction_type: 0,
        amount: forwardedPayment.value.amount,
        paymentMethod: Number(paymentMethod),
        payment_method: Number(paymentMethod),
        transactionNo: isLinePay ? '' : generateTransactionNo(),
        transaction_no: isLinePay ? '' : generateTransactionNo(),
        paidAt: isLinePay ? null : nowStr,
        paid_at: isLinePay ? null : nowStr,
        paymentStatus: isLinePay ? 0 : 1,
        payment_status: isLinePay ? 0 : 1
      };
      const response = await axiosapi.post('/ajax/pages/WashPayments', payload, {
        headers: { "Authorization": `Bearer ${userStore.token}` }
      });
      let resData = response.data;
      if (typeof resData === 'string') {
        try { resData = JSON.parse(resData); } catch (e) {}
      }
      if (resData && resData.success) {
        if (isLinePay) {
          await startLinePayPayment(resData.paymentId);
          return;
        }
        Swal.fire('付款成功', `付款單號：${payNo}\n已完成付款！`, 'success');
        forwardedPayment.value = null;
        sessionStorage.removeItem('pendingPaymentRequest');
        loadPayments();
      } else {
        throw new Error(resData?.message || '付款建立失敗');
      }
    } catch (error) {
      console.error(error);
      Swal.fire('錯誤', error.message || '付款失敗，請稍後再試。', 'error');
    }
  }
};

const requestRefund = async (payment) => {
  const apptOrderId = payment.appt_order_id ?? payment.apptOrderId;
  if (!apptOrderId) {
    Swal.fire('錯誤', '找不到關聯的預約訂單 ID', 'error');
    return;
  }

  // 1. 取得預約時段資訊
  let apptPeriod = '';
  try {
    const apptRes = await axiosapi.get(`/ajax/pages/WashAppointments/${apptOrderId}`, {
      headers: { "Authorization": `Bearer ${userStore.token}` }
    });
    const apptList = apptRes.data?.list || [];
    if (apptList.length > 0) {
      const appt = apptList[0];
      const dateStr = formatJustDate(appt.apptDate || appt.appt_date);
      const timeStr = formatJustTime(appt.apptStartTime || appt.appt_start_time);
      apptPeriod = `${dateStr} ${timeStr}`;
    }
  } catch (error) {
    console.error("無法載入預約時段:", error);
  }

  if (!apptPeriod) {
    apptPeriod = `此預約`;
  }

  // 2. 顯示第一個 Alert 提示
  const confirmResult = await Swal.fire({
    title: '確定取消預約？',
    text: `您確定要取消 ${apptPeriod}？取消後無法回復。`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '確定取消',
    cancelButtonText: '返回',
    confirmButtonColor: '#C26B70'
  });

  if (!confirmResult.isConfirmed) return;

  // 3. 顯示第二個 Alert，要求輸入申請退款的原因
  const { value: reason } = await Swal.fire({
    title: '請輸入退款原因',
    input: 'text',
    inputLabel: '請輸入申請退款的原因',
    inputPlaceholder: '例如：時間調整需退回訂金...',
    showCancelButton: true,
    confirmButtonText: '送出申請',
    cancelButtonText: '返回',
    inputValidator: (value) => {
      if (!value) return '需填寫退款原因';
    }
  });

  if (reason) {
    try {
      const payNo = generatePayNo();
      const payload = {
        memberId: payment.member_id ?? payment.memberId,
        member_id: payment.member_id ?? payment.memberId,
        memberPointId: payment.member_point_id ?? payment.memberPointId ?? null,
        member_point_id: payment.member_point_id ?? payment.memberPointId ?? null,
        apptOrderId: apptOrderId,
        appt_order_id: apptOrderId,
        payNo: payNo,
        pay_no: payNo,
        paymentPurpose: 3,
        payment_purpose: 3,
        transactionType: 1,
        transaction_type: 1,
        amount: payment.amount,
        paymentMethod: payment.payment_method ?? payment.paymentMethod,
        payment_method: payment.payment_method ?? payment.paymentMethod,
        transactionNo: '',
        transaction_no: '',
        paidAt: null,
        paid_at: null,
        paymentStatus: 0,
        payment_status: 0,
        refundReason: reason
      };
      
      // POST 建立退款付款單
      await axiosapi.post('/ajax/pages/WashPayments', payload, { 
        headers: { "Authorization": `Bearer ${userStore.token}` } 
      });

      // PUT 更新預約單狀態與取消明細
      const nowStr = new Date().toISOString().replace('T', ' ').substring(0, 19);
      const apptPayload = {
        apptStatus: 4,              // 4: 已取消
        appt_status: 4,
        depositStatus: 2,           // 2: 已退款
        deposit_status: 2,
        cancelReason: reason,
        cancel_reason: reason,
        canceledBy: '會員',
        canceled_by: '會員',
        canceledAt: nowStr,
        canceled_at: nowStr
      };
      
      await axiosapi.put(`/ajax/pages/WashAppointments/${apptOrderId}`, apptPayload, {
        headers: { "Authorization": `Bearer ${userStore.token}` }
      });

      Swal.fire('已送出', `退款申請已送出，預約已取消。\n退款單號：${payNo}`, 'success');
      loadPayments();
    } catch (error) {
      console.error(error);
      Swal.fire('錯誤', '退款申請失敗，請稍後再試。', 'error');
    }
  }
};


</script>

<style scoped>
* { box-sizing: border-box; }

.payment-manager {
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
.subtitle {
  font-size: 14px;
  color: #A28C7A;
  letter-spacing: 1px;
  font-weight: 500;
}

/* Forwarded summary */
.forwarded-booking-summary {
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(203, 163, 126, 0.2);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(140, 106, 79, 0.06);
}
.summary-label {
  font-size: 12px;
  color: #A28C7A;
  font-weight: 600;
  margin-bottom: 4px;
}
.summary-value {
  font-size: 16px;
  font-weight: 700;
  color: #5A4A3E;
}

/* Stats */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
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
  color: #4F7086;
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
  color: #4F7086;
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
.price-old {
  color: #A28C7A;
  font-size: 12px;
  font-variant-numeric: tabular-nums;
  margin-top: 2px;
}
.price-new {
  color: #B58A63;
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
.btn-refund {
  background-color: #FDF5F5;
  color: #D38282;
  border: 1px solid #FAD1D1 !important;
}
.btn-refund:hover {
  background-color: #FBE9E9;
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(211, 130, 130, 0.15);
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

/* 儲值區塊 */
.bonus-box {
  background-color: #F8EFE6;
  padding: 16px;
  border-radius: 16px;
}
.quick-amount-btn {
  padding: 8px 16px;
  border-radius: 10px;
  border: 1px solid #EFEBE4;
  background: #FCFBF9;
  color: #8C6A4F;
  font-weight: 600;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
  font-family: inherit;
}
.quick-amount-btn:hover { background: #F4EFEA; }
.quick-amount-btn.active {
  background: #CBA37E;
  color: #FFF;
  border-color: #CBA37E;
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
:global(.swal2-icon.swal2-success) { border-color: #A3B296 !important; color: #A3B296 !important; }
:global(.swal2-icon.swal2-success [class^="swal2-success-line"]) { background-color: #A3B296 !important; }
:global(.swal2-icon.swal2-success .swal2-success-ring) { border-color: rgba(163, 178, 150, 0.3) !important; }
:global(.swal2-icon.swal2-warning) { border-color: #D38282 !important; color: #D38282 !important; }
:global(.swal2-icon.swal2-error) { border-color: #D38282 !important; color: #D38282 !important; }
:global(.swal2-icon.swal2-error [class^="swal2-x-mark-line"]) { background-color: #D38282 !important; }

/* RWD 響應式設計 (手機/平板優化) */
@media (max-width: 768px) {
  .payment-manager {
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
  border-left: 4px solid #B58A63;
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
.detail-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}
.detail-grid-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  background: #FFFFFF;
  border: 1px solid #EFEBE4;
  border-radius: 8px;
  padding: 10px 14px;
}
.note-label {
  font-weight: 700;
  color: #8C6A4F;
  font-size: 12px;
  letter-spacing: 1px;
}
.note-content {
  color: #5A4A3E;
  font-size: 13px;
}

@media (max-width: 768px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
