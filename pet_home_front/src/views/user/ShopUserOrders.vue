<template>
  <div class="orders-section">
    <h3>📋 我的歷史訂單 (會員 ID: {{  authStore.id || '未登入' }})</h3>
    <el-divider />

    <el-empty v-if="!loading && orders.length === 0" description="目前沒有任何訂單" />

    <el-card v-for="order in orders" :key="order.id" style="margin-bottom: 16px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div>
            <span style="font-weight: bold; margin-right: 12px;">{{ order.orderNo }}</span>
            <span style="color: #909399; font-size: 13px;">{{ formatDate(order.createdAt) }}</span>
          </div>
          <div>
            <el-tag :type="getStatusType(order.orderStatus)" style="margin-right: 8px;">
              {{ translateStatus(order.orderStatus) }}
            </el-tag>
            <el-tag :type="order.paymentStatus === 'Paid' ? 'success' : 'danger'" effect="dark">
              {{ order.paymentStatus === 'Paid' ? '已付款' : '未付款' }}
            </el-tag>
          </div>
        </div>
      </template>

      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <p style="margin: 4px 0;">收件人：{{ order.recipientName || '—' }}</p>
          <p style="margin: 4px 0; color: #606266;">
            配送方式：{{ order.shippingMethod === 'CVS' ? '超商取貨' : '宅配到府' }}
          </p>
        </div>
        <div style="text-align: right;">
          <div style="color: #909399; font-size: 13px;">實付金額</div>
          <div style="font-size: 22px; color: #f56c6c; font-weight: bold;">${{ order.finalAmount }}</div>
          <el-button style="margin-top: 8px;" size="small" type="primary" plain @click="viewDetail(order.id)">
            查看明細
          </el-button>
          <!-- 新增這顆 -->
<el-button
  v-if="order.paymentStatus !== 'Paid'"
  style="margin-top: 8px; margin-left: 8px;"
  size="small"
  type="danger"
  @click="goToPayment(order)"
>
  前往付款
</el-button>
        </div>
      </div>
    </el-card>

    <!-- 訂單明細彈窗 -->
    <el-dialog v-model="dialogVisible" title="訂單明細" width="600px">
      <div v-if="selectedOrder">
        <el-descriptions border column="2" style="margin-bottom: 16px;">
          <el-descriptions-item label="訂單編號">{{ selectedOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="下單時間">{{ formatDate(selectedOrder.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="收件人">{{ selectedOrder.recipientName }}</el-descriptions-item>
          <el-descriptions-item label="電話">{{ selectedOrder.recipientPhone }}</el-descriptions-item>
          <el-descriptions-item label="配送方式">
            {{ selectedOrder.shippingMethod === 'CVS' ? '超商取貨' : '宅配到府' }}
          </el-descriptions-item>
          <el-descriptions-item label="配送地址">
            <span v-if="selectedOrder.shippingMethod === 'HOME_DELIVERY'">
              {{ selectedOrder.shippingCity }} {{ selectedOrder.shippingAddress }}
            </span>
            <span v-else>門市編號：{{ selectedOrder.cvsStoreId }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <el-table :data="orderItems" border style="width: 100%; margin-bottom: 16px;">
          <el-table-column prop="productSkuId" label="商品規格 ID" width="120" />
          <el-table-column label="單價" width="100">
            <template #default="scope">${{ scope.row.buyPrice }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="數量" width="80" />
          <el-table-column label="小計">
            <template #default="scope">
              <span style="color: #f56c6c; font-weight: bold;">
                ${{ (scope.row.buyPrice * scope.row.quantity).toFixed(0) }}
              </span>
            </template>
          </el-table-column>
        </el-table>

        <div style="text-align: right; line-height: 2;">
          <div>商品總計：${{ selectedOrder.totalAmount }}</div>
          <div style="color: #67c23a;">優惠折抵：-${{ selectedOrder.discountAmount }}</div>
          <div style="font-size: 18px; font-weight: bold; color: #f56c6c;">
            實付總金額：${{ selectedOrder.finalAmount }}
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { userAuthStore } from '@/stores/auth.js'; // ✅
import { useRouter } from 'vue-router'; // 新增


const router = useRouter()

const authStore = userAuthStore(); // ✅

const createAuthAxios = () => {
  return axios.create({
    baseURL: `${import.meta.env.VITE_API_BASE_URL}`,
    headers: { Authorization: `Bearer ${authStore.accessToken}` }
  });
};

const orders = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const selectedOrder = ref(null);
const orderItems = ref([]);


const goToPayment = (order) => {
  router.push({
    path: '/payment',  // ← 改這裡
    query: {
      orderId: order.id,
      amount: order.finalAmount
    }
  })
}
const fetchOrders = async (userId) => {
  if (!userId) return;
  loading.value = true;
  const api = createAuthAxios(); // ✅
  try {
    const res = await api.get(`/orders/user/${userId}`); // ✅
    orders.value = res.data.data || res.data || [];
  } catch (err) {
    ElMessage.error('訂單載入失敗');
    orders.value = [];
  } finally {
    loading.value = false;
  }
};

const viewDetail = async (orderId) => {
  const api = createAuthAxios(); // ✅
  try {
    const [orderRes, itemsRes] = await Promise.all([
      api.get(`/orders/${orderId}`), // ✅
      api.get(`/orders/${orderId}/items`) // ✅
    ]);
    selectedOrder.value = orderRes.data.data || orderRes.data;
    orderItems.value = itemsRes.data.data || itemsRes.data || [];
    dialogVisible.value = true;
  } catch (err) {
    ElMessage.error('載入訂單明細失敗');
  }
};

const formatDate = (val) => {
  if (!val) return '—';
  if (Array.isArray(val)) {
    return `${val[0]}-${String(val[1]).padStart(2,'0')}-${String(val[2]).padStart(2,'0')} ${String(val[3]).padStart(2,'0')}:${String(val[4]).padStart(2,'0')}`;
  }
  return String(val).replace('T', ' ');
};

const translateStatus = (status) => ({
  Pending: '審核中', Confirmed: '已確認', Completed: '已完成', Cancelled: '已取消'
})[status] || status;

const getStatusType = (status) => ({
  Pending: 'warning', Confirmed: 'primary', Completed: 'success', Cancelled: 'info'
})[status] || '';

watch(() => authStore.id, (newId) => { // ✅
  fetchOrders(newId);
}, { immediate: true });
</script>