<template>
  <div class="container mt-4">
    <h3>📦 訂單管理</h3>
    <hr>

    <!-- 搜尋列 -->
    <div class="card p-3 mb-4 shadow-sm">
      <div class="row g-2 align-items-end">
        <div class="col-md-3">
          <label class="form-label small fw-bold text-muted">依訂單狀態篩選</label>
          <select v-model="filterStatus" class="form-select" @change="fetchOrders">
            <option value="">全部訂單</option>
            <option value="Pending">審核中</option>
            <option value="Confirmed">已確認</option>
            <option value="Completed">已完成</option>
            <option value="Cancelled">已取消</option>
          </select>
        </div>
        <div class="col-md-3">
          <label class="form-label small fw-bold text-muted">依會員 ID 查詢</label>
          <div class="input-group">
            <input v-model="searchUserId" type="number" class="form-control" placeholder="輸入會員 ID">
            <button class="btn btn-outline-primary" @click="fetchOrdersByUser">查詢</button>
          </div>
        </div>
        <div class="col-md-2">
          <button class="btn btn-secondary w-100" @click="resetSearch">重設</button>
        </div>
      </div>
    </div>

    <!-- 訂單列表 -->
    <div class="card p-3 shadow-sm mb-5">
      <h5 class="fw-bold text-dark mb-3">所有訂單 (共 {{ orders.length }} 筆)</h5>
      <div class="table-responsive">
        <table class="table table-hover align-middle border text-center">
          <thead class="table-dark text-nowrap">
            <tr>
              <th>訂單編號</th>
              <th>會員 ID</th>
              <th>下單時間</th>
              <th>實付金額</th>
              <th>訂單狀態</th>
              <th>付款狀態</th>
              <th>配送方式</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.id">
              <td class="small fw-bold text-primary">{{ order.orderNo }}</td>
              <td><span class="badge bg-secondary">ID: {{ order.userId }}</span></td>
              <td class="small text-muted">{{ formatDate(order.createdAt) }}</td>
              <td class="fw-bold text-danger">${{ order.finalAmount }}</td>
              <td>
                <select class="form-select form-select-sm"
                  :value="order.orderStatus"
                  @change="updateStatus(order, $event.target.value)"
                  :class="getStatusClass(order.orderStatus)">
                  <option value="Pending">審核中</option>
                  <option value="Confirmed">已確認</option>
                  <option value="Completed">已完成</option>
                  <option value="Cancelled">已取消</option>
                </select>
              </td>
              <td>
                <span class="badge" :class="order.paymentStatus === 'Paid' ? 'bg-success' : 'bg-danger'">
                  {{ order.paymentStatus === 'Paid' ? '已付款' : '未付款' }}
                </span>
              </td>
              <td class="small">{{ order.shippingMethod === 'CVS' ? '超商取貨' : '宅配到府' }}</td>
              <td>
                <button class="btn btn-outline-primary btn-sm" @click="viewDetail(order)">
                  查看明細
                </button>
              </td>
            </tr>
            <tr v-if="orders.length === 0">
              <td colspan="8" class="text-muted py-4">目前沒有訂單資料</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 訂單明細彈窗 -->
    <div v-if="selectedOrder" class="modal fade show d-block" style="background: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">訂單明細 — {{ selectedOrder.orderNo }}</h5>
            <button class="btn-close" @click="selectedOrder = null"></button>
          </div>
          <div class="modal-body">
            <div class="row mb-3">
              <div class="col-md-6">
                <table class="table table-sm table-bordered">
                  <tr><th>會員 ID</th><td>{{ selectedOrder.userId }}</td></tr>
                  <tr><th>收件人</th><td>{{ selectedOrder.recipientName }}</td></tr>
                  <tr><th>電話</th><td>{{ selectedOrder.recipientPhone }}</td></tr>
                  <tr><th>配送方式</th><td>{{ selectedOrder.shippingMethod === 'CVS' ? '超商取貨' : '宅配到府' }}</td></tr>
                  <tr v-if="selectedOrder.shippingMethod === 'HOME_DELIVERY'">
                    <th>地址</th>
                    <td>{{ selectedOrder.shippingCity }} {{ selectedOrder.shippingAddress }}</td>
                  </tr>
                  <tr v-if="selectedOrder.shippingMethod === 'CVS'">
                    <th>門市編號</th><td>{{ selectedOrder.cvsStoreId }}</td>
                  </tr>
                </table>
              </div>
              <div class="col-md-6">
                <table class="table table-sm table-bordered">
                  <tr><th>訂單狀態</th><td>{{ translateStatus(selectedOrder.orderStatus) }}</td></tr>
                  <tr><th>付款狀態</th><td>{{ selectedOrder.paymentStatus === 'Paid' ? '已付款' : '未付款' }}</td></tr>
                  <tr><th>商品總計</th><td>${{ selectedOrder.totalAmount }}</td></tr>
                  <tr><th>優惠折抵</th><td class="text-success">-${{ selectedOrder.discountAmount }}</td></tr>
                  <tr><th>實付金額</th><td class="fw-bold text-danger">${{ selectedOrder.finalAmount }}</td></tr>
                </table>
              </div>
            </div>

            <h6 class="fw-bold">商品明細</h6>
            <table class="table table-sm table-bordered text-center">
              <thead class="table-light">
                <tr>
                  <th>商品規格 ID</th>
                  <th>單價</th>
                  <th>數量</th>
                  <th>小計</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in orderItems" :key="item.id">
                  <td>{{ item.productSkuId }}</td>
                  <td>${{ item.buyPrice }}</td>
                  <td>{{ item.quantity }}</td>
                  <td class="text-danger fw-bold">${{ (item.buyPrice * item.quantity).toFixed(0) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="selectedOrder = null">關閉</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import Swal from 'sweetalert2';
import { userAuthStore } from '@/stores/auth.js'; // ✅

const authStore = userAuthStore();

const createAuthAxios = () => {
  return axios.create({
    baseURL: `${import.meta.env.VITE_API_BASE_URL}`,
    headers: { Authorization: `Bearer ${authStore.accessToken}` }
  });
};

const orders = ref([]);
const filterStatus = ref('');
const searchUserId = ref('');
const selectedOrder = ref(null);
const orderItems = ref([]);

const fetchOrders = async () => {
  const api = createAuthAxios();
  try {
    let url = '/orders';
    if (filterStatus.value) url += `?status=${filterStatus.value}`;
    const res = await api.get(url);
    orders.value = res.data.data || res.data || [];
  } catch (err) {
    console.error('訂單載入失敗', err);
    orders.value = [];
  }
};

const fetchOrdersByUser = async () => {
  if (!searchUserId.value) return fetchOrders();
  const api = createAuthAxios();
  try {
    const res = await api.get(`/orders/user/${searchUserId.value}`);
    orders.value = res.data.data || res.data || [];
  } catch (err) {
    Swal.fire({ text: '查無此會員訂單', icon: 'warning' });
    orders.value = [];
  }
};

const resetSearch = () => {
  filterStatus.value = '';
  searchUserId.value = '';
  fetchOrders();
};

const updateStatus = async (order, newStatus) => {
  const api = createAuthAxios();
  try {
    await api.patch(`/orders/${order.id}/status?status=${newStatus}`);
    order.orderStatus = newStatus;
    Swal.fire({ text: '狀態已更新', icon: 'success', timer: 1000, showConfirmButton: false });
  } catch (err) {
    Swal.fire({ text: '狀態更新失敗', icon: 'error' });
  }
};

const viewDetail = async (order) => {
  const api = createAuthAxios();
  try {
    const [orderRes, itemsRes] = await Promise.all([
      api.get(`/orders/${order.id}`),
      api.get(`/orders/${order.id}/items`)
    ]);
    selectedOrder.value = orderRes.data.data || orderRes.data;
    orderItems.value = itemsRes.data.data || itemsRes.data || [];
  } catch (err) {
    Swal.fire({ text: '載入明細失敗', icon: 'error' });
  }
};

const formatDate = (val) => {
  if (!val) return '—';
  if (Array.isArray(val)) {
    return `${val[0]}-${String(val[1]).padStart(2,'0')}-${String(val[2]).padStart(2,'0')} ${String(val[3]).padStart(2,'0')}:${String(val[4]).padStart(2,'0')}`;
  }
  return String(val).replace('T', ' ');
};

const translateStatus = (s) => ({ Pending: '審核中', Confirmed: '已確認', Completed: '已完成', Cancelled: '已取消' })[s] || s;

const getStatusClass = (s) => ({
  Pending: 'text-warning', Confirmed: 'text-primary', Completed: 'text-success', Cancelled: 'text-danger'
})[s] || '';

onMounted(fetchOrders);
</script>