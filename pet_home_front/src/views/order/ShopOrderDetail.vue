<template>
  <div class="order-detail-container" style="max-width: 800px; margin: 20px auto; padding: 20px;">
    <h2>訂單詳細資料</h2>
    <el-divider />

    <div v-if="order" style="margin-bottom: 30px;">
      <el-steps :active="getStepActive(order.orderStatus)" finish-status="success" align-center style="margin-bottom: 30px;">
        <el-step title="提交訂單" description="已成功建立訂單" />
        <el-step title="商家確認" description="訂單處理中/揀貨" />
        <el-step title="商品出貨" description="物流配送中" />
        <el-step title="完成交易" description="買家已收貨" />
      </el-steps>

      <el-descriptions title="基本資料" border column="2" style="margin-bottom: 20px;">
        <el-descriptions-item label="訂單編號">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="下單時間">{{ order.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="訂單狀態">
          <el-tag size="small">{{ order.orderStatus }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="付款狀態">
          <el-tag size="small" :type="order.paymentStatus === 'Paid' ? 'success' : 'danger'">{{ order.paymentStatus }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="收件與物流資訊" border column="1" style="margin-bottom: 20px;">
        <el-descriptions-item label="收件人姓名">{{ order.recipientName }}</el-descriptions-item>
        <el-descriptions-item label="收件人電話">{{ order.recipientPhone }}</el-descriptions-item>
        <el-descriptions-item label="配送方式">{{ order.shippingMethod }}</el-descriptions-item>
        <el-descriptions-item v-if="order.shippingMethod === 'HOME_DELIVERY'" label="配送地址">
          ({{ order.postalCode }}) {{ order.shippingCity }} {{ order.shippingAddress }}
        </el-descriptions-item>
        <el-descriptions-item v-if="order.shippingMethod === 'CVS'" label="超商門市店號">
          {{ order.cvsStoreId }}
        </el-descriptions-item>
      </el-descriptions>

      <h3>商品明細</h3>
      <el-table :data="orderItems" border style="width: 100%; margin-bottom: 20px;">
        <el-table-column prop="productSkuId" label="商品規格 ID" width="120" />
        <el-table-column prop="buyPrice" label="購買單價" width="120">
          <template #default="scope">${{ scope.row.buyPrice }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="數量" width="100" />
        <el-table-column label="小計">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold;">
              ${{ scope.row.buyPrice * scope.row.quantity }}
            </span>
          </template>
        </el-table-column>
      </el-table>

      <div style="text-align: right; font-size: 14px; color: #606266; line-height: 2;">
        <div>商品總計: <span style="width: 100px; display: inline-block;">${{ order.totalAmount }}</span></div>
        <div>優惠折抵: <span style="width: 100px; display: inline-block; color: #67c23a;">-${{ order.discountAmount }}</span></div>
        <el-divider style="margin: 10px 0;" />
        <div style="font-size: 18px; font-weight: bold; color: #f56c6c;">
          實付總金額: <span style="width: 100px; display: inline-block;">${{ order.finalAmount }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const orderId = 1; // 實際應從路由獲取，例如 route.params.orderId
const order = ref(null);
const orderItems = ref([]);

const loadOrderDetail = async () => {
  try {
    // 併發同時呼叫兩個後端控制器 API
    // 1. GET /api/orders/{orderId}
    // 2. GET /api/orders/{orderId}/items
    const [orderRes, itemsRes] = await Promise.all([
      axios.get(`/api/orders/${orderId}`),
      axios.get(`/api/orders/${orderId}/items`)
    ]);

    order.value = orderRes.data;
    orderItems.value = itemsRes.data;
  } catch (error) {
    console.error('載入訂單詳情失敗', error);
  }
};

// 根據後端訂單狀態轉換為進度條 active index
const getStepActive = (status) => {
  switch (status) {
    case 'Pending': return 1;
    case 'Confirmed': return 2;
    case 'Completed': return 4; // 全部亮綠燈
    case 'Cancelled': return 0;
    default: return 1;
  }
};

onMounted(() => {
  loadOrderDetail();
});
</script>