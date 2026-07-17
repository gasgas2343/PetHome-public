<template>
  <div class="order-list-container" style="max-width: 800px; margin: 20px auto; padding: 20px;">
    <h2>我的歷史訂單</h2>
    <el-divider />

    <el-skeleton :rows="5" animated :loading="loading">
      <template #default>
        <el-empty v-if="orders.length === 0" description="目前沒有任何訂單" />

        <el-card v-for="order in orders" :key="order.id" style="margin-bottom: 20px;">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <div>
                <span style="font-weight: bold; margin-right: 15px;">訂單號：{{ order.orderNo }}</span>
                <span style="color: #909399; font-size: 13px;">下單時間：{{ formatTime(order.createdAt) }}</span>
              </div>
              <div>
                <el-tag :type="getOrderStatusTag(order.orderStatus)" style="margin-right: 10px;">
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
              <p style="margin: 5px 0;">收件人：{{ order.recipientName }}</p>
              <p style="margin: 5px 0; color: #606266;">配送方式：{{ order.shippingMethod === 'CVS' ? '超商取貨' : '宅配到府' }}</p>
            </div>
            <div style="text-align: right;">
              <span style="font-size: 14px; color: #909399;">應付總額：</span>
              <span style="font-size: 20px; color: #f56c6c; font-weight: bold;">${{ order.finalAmount }}</span>
              <div style="margin-top: 10px;">
                <el-button type="primary" plain size="small" @click="viewDetail(order.id)">
                  查看訂單明細
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';

const orders = ref([]);
const loading = ref(true);
const currentUserId = 1; // 模擬當前登入用戶

const fetchOrders = async () => {
  try {
    // 對接後端：@GetMapping("/user/{userId}") public Result<List<Order>> getUserOrders(...)
    const response = await axios.get(`/api/orders/user/${currentUserId}`);
    orders.value = response.data;
  } catch (error) {
    ElMessage.error('無法載入訂單列表');
  } finally {
    loading.value = false;
  }
};

// 狀態文字中文化轉換
const translateStatus = (status) => {
  const statusMap = {
    'Pending': '訂單審核中',
    'Confirmed': '已確認/準備出貨',
    'Completed': '訂單已完成',
    'Cancelled': '訂單已取消'
  };
  return statusMap[status] || status;
};

// 依狀態動態決定 Tag 樣式
const getOrderStatusTag = (status) => {
  switch (status) {
    case 'Pending': return 'warning';
    case 'Confirmed': return 'brand';
    case 'Completed': return 'success';
    case 'Cancelled': return 'info';
    default: return '';
  }
};

// 簡單格式化時間
const formatTime = (timeArray) => {
  if (!timeArray) return '';
  // Spring Boot LocalDateTime 傳到前端有時會變成陣列 [2026, 6, 22, 15, 30...] 或字串
  if (Array.isArray(timeArray)) {
    return `${timeArray[0]}-${String(timeArray[1]).padStart(2,'0')}-${String(timeArray[2]).padStart(2,'0')} ${String(timeArray[3]).padStart(2,'0')}:${String(timeArray[4]).padStart(2,'0')}`;
  }
  return timeArray.replace('T', ' ');
};

const viewDetail = (orderId) => {
  ElMessage.info(`點擊了查看訂單 ID: ${orderId}，此處應執行路由跳轉到詳情頁`);
  // router.push(`/orders/${orderId}`);
};

onMounted(() => {
  fetchOrders();
});
</script>