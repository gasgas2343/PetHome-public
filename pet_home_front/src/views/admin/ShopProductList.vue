<!-- <template>
  <el-table :data="products" style="width: 100%">
    <el-table-column prop="name" label="商品名稱" />
    <el-table-column label="狀態">
      <template #default="scope">
        <el-switch
          v-model="scope.row.status"
          :active-value="1"
          :inactive-value="0"
          @change="toggleStatus(scope.row)"
        />
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';

const products = ref([]);

// 撈取列表
const fetchProducts = async () => {
  const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/products`);
  products.value = res.data.data;
};

// 切換上下架狀態
const toggleStatus = async (row) => {
  try {
    await axios.put(`${import.meta.env.VITE_API_BASE_URL}/products/${row.id}/status?status=${row.status}`);
    ElMessage.success('狀態更新成功');
  } catch (error) {
    ElMessage.error('更新失敗');
    row.status = row.status === 1 ? 0 : 1; // 失敗時回復原狀
  }
};

onMounted(fetchProducts);
</script> -->
<template>
  <el-table :data="products" style="width: 100%" border>
    <el-table-column prop="name" label="商品名稱" align="left" />
    <el-table-column label="狀態" width="150" align="center">
      <template #default="scope">
        <el-switch
          v-model="scope.row.status"
          :active-value="1"
          :inactive-value="0"
          active-text="上架"
          inactive-text="下架"
          inline-prompt
          @change="toggleStatus(scope.row)"
        />
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage, ElLoading } from 'element-plus';

const products = ref([]);

// 撈取列表
const fetchProducts = async () => {
  try {
    const res = await axios.get('http://localhost:8080/products');
    products.value = res.data.data ?? res.data;
  } catch (error) {
    ElMessage.error('無法取得商品列表');
  }
};

// 切換上下架狀態（獨佔上架機制）
const toggleStatus = async (row) => {
  // 建立全螢幕遮罩，避免在非同步打 API 時使用者連續亂點
  const loading = ElLoading.service({
    lock: true,
    text: '正在同步更新促銷狀態...',
    background: 'rgba(0, 0, 0, 0.7)',
  });

  try {
    // 情況 A：如果使用者是把當前商品「開啟上架」
    if (row.status === 1) {
      // 1. 先跑迴圈，把資料庫裡「其他原本上架中」的商品統統下架
      for (let prod of products.value) {
        if (prod.id !== row.id && prod.status === 1) {
          try {
            // 發送請求，將其他商品在後端下架
            await axios.put(`http://localhost:8080/products/${prod.id}/status?status=0`);
            prod.status = 0; // 同步修改前端狀態，讓對方的 el-switch 自動關閉
          } catch (err) {
            console.error(`自動下架商品 ID ${prod.id} 失敗:`, err);
          }
        }
      }
    }

    // 2. 最後，發送 API 更新當前點擊的這隻商品狀態
    await axios.put(`http://localhost:8080/products/${row.id}/status?status=${row.status}`);
    
    // 3. 關閉遮罩並噴出成功提示
    loading.close();
    if (row.status === 1) {
      ElMessage.success(`「${row.name}」已獨佔上架，其餘商品已自動下架！`);
    } else {
      ElMessage.warning('商品已成功下架');
    }

  } catch (error) {
    // 4. 失敗防呆：關閉遮罩、還原開關狀態並重新整理
    loading.close();
    ElMessage.error('更新失敗，正在還原狀態...');
    // 重新從後端撈取正確的狀態，保證畫面不卡死
    await fetchProducts(); 
  }
};

onMounted(fetchProducts);
</script>