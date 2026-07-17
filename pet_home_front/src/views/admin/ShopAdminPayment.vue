<template>
  <div>
    <h4>💳 金流管理</h4>
    <el-divider />

    <!-- 搜尋列 -->
    <div style="display: flex; gap: 12px; margin-bottom: 16px;">
      <el-input v-model="searchOrderId" placeholder="輸入訂單 ID 搜尋" style="width: 200px;" clearable />
      <el-select v-model="searchStatus" placeholder="付款狀態" clearable style="width: 150px;">
        <el-option label="待付款" value="pending" />
        <el-option label="已付款" value="paid" />
        <el-option label="失敗" value="failed" />
      </el-select>
      <el-button type="primary" @click="fetchPayments">搜尋</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- 付款列表 -->
    <el-table :data="payments" border stripe style="width: 100%;">
      <el-table-column prop="id" label="付款 ID" width="80" />
      <el-table-column prop="orderId" label="訂單 ID" width="100" />
      <el-table-column prop="provider" label="金流商" width="100" />
      <el-table-column prop="method" label="付款方式" width="100" />
      <el-table-column label="金額" width="120">
        <template #default="scope">
          NT$ {{ scope.row.amount }}
        </template>
      </el-table-column>
      <el-table-column label="狀態" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'paid' ? 'success' : scope.row.status === 'failed' ? 'danger' : 'warning'">
            {{ statusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="付款時間" width="180">
        <template #default="scope">
          {{ scope.row.paidAt ? formatDate(scope.row.paidAt) : '—' }}
        </template>
      </el-table-column>
      <el-table-column label="建立時間">
        <template #default="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { userAuthStore } from '@/stores/auth.js'

const authStore = userAuthStore()
const payments = ref([])
const searchOrderId = ref('')
const searchStatus = ref('')

const api = () => axios.create({
  baseURL: `${import.meta.env.VITE_API_BASE_URL}`,
  headers: { Authorization: `Bearer ${authStore.accessToken}` }
})

const fetchPayments = async () => {
  try {
    let url = '/payments/all'
    const params = {}
    if (searchOrderId.value) params.orderId = searchOrderId.value
    if (searchStatus.value) params.status = searchStatus.value
    const res = await api().get(url, { params })
    payments.value = res.data.data || res.data || []
  } catch (e) {
    ElMessage.error('載入付款記錄失敗')
  }
}

const resetSearch = () => {
  searchOrderId.value = ''
  searchStatus.value = ''
  fetchPayments()
}

const statusLabel = (s) => ({ pending: '待付款', paid: '已付款', failed: '失敗' })[s] || s

const formatDate = (val) => {
  if (!val) return '—'
  if (Array.isArray(val)) {
    return `${val[0]}-${String(val[1]).padStart(2,'0')}-${String(val[2]).padStart(2,'0')} ${String(val[3]).padStart(2,'0')}:${String(val[4]).padStart(2,'0')}`
  }
  return String(val).replace('T', ' ')
}

onMounted(fetchPayments)
</script>