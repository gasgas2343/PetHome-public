<!-- <template>
  <div class="container mt-4">
    <h3>閃購活動管理</h3>
    <hr>

    <div class="card p-3 mb-4 shadow-sm">
      <h5>新增閃購活動</h5>
      <div class="row g-2">
        <div class="col-md-2">
          <input v-model="form.productSkuId" type="number" class="form-control" placeholder="SKU ID">
        </div>
        <div class="col-md-2">
          <input v-model="form.salePrice" type="number" step="0.01" class="form-control" placeholder="特賣價格">
        </div>
        <div class="col-md-2">
          <input v-model="form.saleStock" type="number" class="form-control" placeholder="特賣庫存">
        </div>
        <div class="col-md-3">
          <input v-model="form.startTime" type="datetime-local" class="form-control">
        </div>
        <div class="col-md-3">
          <input v-model="form.endTime" type="datetime-local" class="form-control">
        </div>
        <div class="col-12 mt-2">
          <button @click="createSale" class="btn btn-primary w-100">建立閃購活動</button>
        </div>
      </div>
    </div>

    <table class="table table-hover">
      <thead class="table-light">
        <tr>
          <th>SKU ID</th>
          <th>特賣價</th>
          <th>庫存</th>
          <th>開始時間</th>
          <th>結束時間</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="sale in flashSales" :key="sale.id">
          <td>{{ sale.productSkuId }}</td>
          <td>{{ sale.salePrice }}</td>
          <td>{{ sale.saleStock }}</td>
          <td>{{ formatTime(sale.startTime) }}</td>
          <td>{{ formatTime(sale.endTime) }}</td>
          <td>
            <button @click="deleteSale(sale.id)" class="btn btn-danger btn-sm">刪除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const flashSales = ref([]);
const form = ref({
  productSkuId: '',
  salePrice: '',
  saleStock: '',
  startTime: '',
  endTime: ''
});

// 取得活動列表
const fetchSales = async () => {
  try {
    const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/flash-sales/all`);  // ← 改這裡
    flashSales.value = res.data.data;
  } catch (err) {
    console.error("載入活動失敗", err);
  }
};

// 建立活動
const createSale = async () => {
  try {
    await axios.post(`${import.meta.env.VITE_API_BASE_URL}/flash-sales/create`, form.value);
    alert("建立成功！");
    fetchSales(); // 重載列表
  } catch (err) {
    alert("建立失敗：" + err.response?.data?.message || "請檢查輸入");
  }
};

// 刪除活動
const deleteSale = async (id) => {
  if (!confirm("確定刪除此活動？")) return;
  try {
    await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/flash-sales/delete/${id}`);
    fetchSales();
  } catch (err) {
    alert("刪除失敗");
  }
};

// 簡單的時間格式化
const formatTime = (time) => time ? time.replace('T', ' ') : '';

onMounted(fetchSales);
</script> -->
<!-- <template>
  <div class="container mt-4">
    <h3>⚡ 閃購活動管理</h3>
    <hr>

    <div class="card p-4 mb-4 shadow-sm border-primary-subtle">
      <h5 class="fw-bold text-primary mb-3">✨ 新增閃購活動</h5>
      <div class="row g-2">
        <div class="col-md-2">
          <label class="form-label small fw-bold text-muted">SKU ID</label>
          <input v-model="form.productSkuId" type="number" class="form-control" placeholder="例如: 1">
        </div>
        <div class="col-md-2">
          <label class="form-label small fw-bold text-muted">特賣價格</label>
          <input v-model="form.salePrice" type="number" step="0.01" class="form-control" placeholder="NT$">
        </div>
        <div class="col-md-2">
          <label class="form-label small fw-bold text-muted">特賣庫存</label>
          <input v-model="form.saleStock" type="number" class="form-control" placeholder="組數">
        </div>
        <div class="col-md-3">
          <label class="form-label small fw-bold text-muted">開始時間</label>
          <input v-model="form.startTime" type="datetime-local" class="form-control">
        </div>
        <div class="col-md-3">
          <label class="form-label small fw-bold text-muted">結束時間</label>
          <input v-model="form.endTime" type="datetime-local" class="form-control">
        </div>
        <div class="col-12 mt-3">
          <button @click="createSale" class="btn btn-primary w-100 fw-bold py-2">🔥 建立閃購活動（並自動下架其他商品）</button>
        </div>
      </div>
    </div>

    <div class="card p-3 shadow-sm">
      <h5 class="fw-bold text-dark mb-3">📋 目前閃購活動清單</h5>
      <table class="table table-hover align-middle text-center border">
        <thead class="table-dark">
          <tr>
            <th>SKU ID</th>
            <th>特賣價</th>
            <th>特賣庫存</th>
            <th>開始時間</th>
            <th>結束時間</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="sale in flashSales" :key="sale.id">
            <td class="fw-bold text-primary"># {{ sale.productSkuId }}</td>
            <td class="fw-bold text-danger">NT$ {{ sale.salePrice }}</td>
            <td><span class="badge bg-success fs-6">{{ sale.saleStock }}</span></td>
            <td class="small">{{ formatTime(sale.startTime) }}</td>
            <td class="small">{{ formatTime(sale.endTime) }}</td>
            <td>
              <button @click="deleteSale(sale.id)" class="btn btn-danger btn-sm px-3 fw-bold">🗑️ 刪除</button>
            </td>
          </tr>
          <tr v-if="flashSales.length === 0">
            <td colspan="6" class="text-muted py-4">目前沒有進行中的閃購活動</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const flashSales = ref([]);

const initForm = () => ({
  productSkuId: '',
  salePrice: '',
  saleStock: '',
  startTime: '',
  endTime: ''
});

const form = ref(initForm());

// 1. 取得活動列表
const fetchSales = async () => {
  try {
    const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/flash-sales/all`);
    flashSales.value = res.data.data ?? res.data;
  } catch (err) {
    console.error("載入活動失敗", err);
  }
};

// 2. 建立活動（核心邏輯：自動補秒數 + 下架其他商品）
// 建立活動（修正版：建立時自動刪除/結束其他舊的閃購活動，不再錯誤下架商品主檔）
const createSale = async () => {
  if (!form.value.productSkuId || !form.value.salePrice || !form.value.saleStock || !form.value.startTime || !form.value.endTime) {
    alert("請填寫所有必要欄位！");
    return;
  }

  try {
    // 1. 防呆處理 datetime-local 遺失「秒數」問題
    let payload = { ...form.value };
    if (payload.startTime && payload.startTime.length === 16) payload.startTime += ":00";
    if (payload.endTime && payload.endTime.length === 16) payload.endTime += ":00";

    // 💡 關鍵修正：我們不再呼叫 /api/products 去下架一般商品！
    // 而是直接跑迴圈把舊的「閃購活動」清空，確保同一時間只有一個閃購活動存在
    if (flashSales.value && flashSales.value.length > 0) {
      for (let oldSale of flashSales.value) {
        try {
          // 呼叫刪除舊活動的 API，把之前其他的閃購活動通通清掉
          await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/flash-sales/delete/${oldSale.id}`);
        } catch (delErr) {
          console.warn(`嘗試自動刪除舊閃購活動 ID ${oldSale.id} 失敗`, delErr);
        }
      }
    }

    // 2. 正式送出「新閃購活動」建立請求
    await axios.post(`${import.meta.env.VITE_API_BASE_URL}/flash-sales/create`, payload);
    
    alert("⚡ 新閃購活動建立成功！其餘歷史閃購活動已自動清理完畢。");
    fetchSales(); // 重新整理後台活動列表
    form.value = initForm(); // 清空輸入欄位
  } catch (err) {
    console.error(err);
    alert("建立失敗：" + (err.response?.data?.message || "請檢查輸入欄位與格式"));
  }
};

// 3. 刪除活動
const deleteSale = async (id) => {
  if (!confirm("確定刪除此活動？")) return;
  try {
    await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/flash-sales/delete/${id}`);
    alert("已成功刪除");
    fetchSales();
  } catch (err) {
    alert("刪除失敗");
  }
};

// 時間格式化優化
const formatTime = (time) => {
  if (!time) return '';
  // 將 2026-06-22T10:30:00 改為更易讀的 2026-06-22 10:30:00
  return time.replace('T', ' ').substring(0, 19);
};

onMounted(fetchSales);
</script> -->
<template>
  <div class="container mt-4">
    <h3>⚡ 閃購活動管理</h3>
    <hr>

    <div class="card p-4 mb-4 shadow-sm">
      <h5 class="fw-bold mb-3 text-primary">🚀 建立全新特賣活動</h5>
      
      <div class="row g-3 mb-3">
        <div class="col-md-3">
          <label class="form-label small fw-bold text-muted">商品 SKU ID</label>
          <input type="number" v-model="newCampaign.productSkuId" placeholder="請輸入 SKU ID (例如: 1)" class="form-control">
        </div>
        <div class="col-md-3">
          <label class="form-label small fw-bold text-muted">特賣價格 (NT$)</label>
          <input type="number" v-model="newCampaign.salePrice" placeholder="NT$" class="form-control">
        </div>
        <div class="col-md-3">
          <label class="form-label small fw-bold text-muted">特賣庫存</label>
          <input type="number" v-model="newCampaign.saleStock" placeholder="組數" class="form-control">
        </div>
      </div>

      <div class="row g-3 mb-4">
        <div class="col-md-4">
          <label class="form-label small fw-bold text-muted">開始時間</label>
          <input type="datetime-local" v-model="newCampaign.startTime" class="form-control">
        </div>
        <div class="col-md-4">
          <label class="form-label small fw-bold text-muted">結束 time</label>
          <input type="datetime-local" v-model="newCampaign.endTime" class="form-control">
        </div>
      </div>

      <button @click="handleCreate" class="btn btn-primary px-5 fw-bold shadow-sm">
        🔥 建立特賣活動（不會影響其他商品）
      </button>
    </div>

    <div class="card p-3 shadow-sm mb-5">
      <h5 class="fw-bold text-dark mb-3">📋 目前特賣活動總覽</h5>
      <div class="table-responsive">
        <table class="table table-hover align-middle border text-center">
          <thead class="table-dark text-nowrap">
            <tr>
              <th style="width: 100px;">活動 ID</th>
              <th style="width: 150px;">商品 SKU ID</th>
              <th style="width: 150px;">特賣價格</th>
              <th style="width: 120px;">活動庫存</th>
              <th>開始時間</th>
              <th>結束時間</th>
              <th style="width: 150px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in campaignList" :key="item.id">
              <td><span class="badge bg-secondary">#{{ item.id }}</span></td>
              <td><span class="badge bg-info text-dark">SKU: {{ item.productSkuId }}</span></td>
              <td class="fw-bold text-danger fs-5">NT$ {{ item.salePrice }}</td>
              <td>
                <span class="badge" :class="item.saleStock > 0 ? 'bg-success' : 'bg-danger'">
                  {{ item.saleStock }}
                </span>
              </td>
              <td class="small text-muted">{{ formatTime(item.startTime) }}</td>
              <td class="small text-muted">{{ formatTime(item.endTime) }}</td>
              <td>
                <button @click="handleDelete(item.id)" class="btn btn-outline-danger btn-sm fw-bold px-3">
                  🗑️ 刪除
                </button>
              </td>
            </tr>
            <tr v-if="campaignList.length === 0">
              <td colspan="7" class="text-muted py-4">目前沒有任何特賣活動，快去建立一個吧！</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import Swal from 'sweetalert2';
import { userAuthStore } from '@/stores/auth.js'; // ✅ 新增

const createAuthAxios = () => { // ✅ 新增
  const authStore = userAuthStore();
  return axios.create({
    baseURL: `${import.meta.env.VITE_API_BASE_URL}`,
    headers: { Authorization: `Bearer ${authStore.accessToken}` }
  });
};

const campaignList = ref([]);

const initForm = () => ({
  productSkuId: null,
  salePrice: 0,
  saleStock: 10,
  startTime: '',
  endTime: ''
});
const newCampaign = ref(initForm());

const fetchAllCampaigns = async () => {
  const api = createAuthAxios(); // ✅
  try {
    const res = await api.get('/flash-sales/all'); // ✅
    campaignList.value = res.data.data ?? res.data;
  } catch (error) {
    console.error("抓取活動清單失敗", error);
  }
};

const handleCreate = async () => {
  if (!newCampaign.value.productSkuId || !newCampaign.value.startTime || !newCampaign.value.endTime) {
    Swal.fire({ text: "請確認所有欄位（SKU ID、時間）填寫完整！", icon: "warning" });
    return;
  }
  const api = createAuthAxios(); // ✅
  try {
    const payload = {
      ...newCampaign.value,
      salePrice: Number(newCampaign.value.salePrice).toFixed(2).toString()
    };
    const res = await api.post('/flash-sales/create', payload); // ✅
    if (res.data.code === 500 || res.data.status === 'fail') {
      throw new Error(res.data.message || "後端阻擋建立");
    }
    Swal.fire({ text: "特賣活動成功同步上線！", icon: "success", timer: 1200 });
    fetchAllCampaigns();
    newCampaign.value = initForm();
  } catch (error) {
    const errMsg = error.response?.data?.message || error.message || "請檢查後端主控台日誌。";
    Swal.fire({ title: "建立失敗", text: errMsg, icon: "error" });
  }
};

const handleDelete = async (id) => {
  const result = await Swal.fire({
    title: '確定要移除此特賣活動嗎？',
    text: "刪除後前台將恢復原價販售！",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#dc3545',
    confirmButtonText: '確定刪除',
    cancelButtonText: '取消'
  });
  if (result.isConfirmed) {
    const api = createAuthAxios(); // ✅
    try {
      await api.delete(`/flash-sales/delete/${id}`); // ✅
      Swal.fire({ text: "活動已安全移除", icon: "success", timer: 1000 });
      fetchAllCampaigns();
    } catch (error) {
      Swal.fire({ text: "刪除失敗，請檢查後端 API 連線", icon: "error" });
    }
  }
};

const formatTime = (timeStr) => {
  if (!timeStr) return '';
  return timeStr.replace('T', ' ');
};

onMounted(fetchAllCampaigns);
</script>