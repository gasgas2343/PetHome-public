<!-- <!-- <template>
  <div class="container mt-5">
    <h2>商品管理後台</h2>
    
    <div class="card p-3 mb-4">
      <input type="text" v-model="newProduct.name" placeholder="商品名稱" class="form-control mb-2">
      <input type="number" v-model="newProduct.basePrice" placeholder="價格" class="form-control mb-2">
      <input type="file" @change="handleFileChange" class="form-control mb-2">
      <button @click="addProduct" class="btn btn-primary">上傳新增</button>
    </div>

    <table class="table">
      <thead>
        <tr>
          <th>圖片</th>
          <th>名稱</th>
          <th>價格</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in products" :key="item.id">
          <td><img :src="item.imageUrl" width="50" alt="商品圖"></td>
          <td>{{ item.name }}</td>
          <td>{{ item.basePrice }}</td>
          <td><button @click="deleteProduct(item.id)" class="btn btn-danger btn-sm">刪除</button></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const products = ref([]);
const newProduct = ref({ name: '', basePrice: 0 });
let selectedFile = null;

// 1. 取得商品列表 (修正：解析後端 Result 結構)
const fetchProducts = async () => {
  try {
    const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/products`);
    // 假設你的後端 Result 結構是 { code: 200, data: [...] }
    products.value = res.data.data || res.data; 
  } catch (error) {
    console.error("獲取商品失敗", error);
  }
};

// 2. 處理檔案選擇
const handleFileChange = (e) => { 
  selectedFile = e.target.files[0]; 
};

// 3. 上傳商品 (修正：加入 Header 宣告)
const addProduct = async () => {
  if (!selectedFile) return alert("請選擇圖片");

  try {
    // 1. 先處理圖片：上傳檔案到 /api/upload
    const fileFormData = new FormData();
    fileFormData.append('file', selectedFile);

    const uploadRes = await axios.post(`${import.meta.env.VITE_API_BASE_URL}/products/upload`, fileFormData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    
    // 假設後端回傳的結構是 { code: 200, data: "http://..." }
    const imageUrl = uploadRes.data.data; 

    // 2. 再處理商品資料：將 imageUrl 作為字串傳送至 /api/products
    await axios.post(`${import.meta.env.VITE_API_BASE_URL}/products`, {
      name: newProduct.value.name,
      basePrice: newProduct.value.basePrice,
      imageUrl: imageUrl // 這裡傳入的是字串，符合資料庫需求
    });

    alert("新增成功");
    fetchProducts();
    newProduct.value = { name: '', basePrice: 0 };
    selectedFile = null;
  } catch (error) {
    console.error("新增失敗", error);
    alert("新增失敗，請檢查後端錯誤訊息");
  }
};

// 4. 刪除商品邏輯 (記得補上)
const deleteProduct = async (id) => {
  if (!confirm("確定要刪除嗎？")) return;
  await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/products/${id}`);
  fetchProducts();
};

onMounted(fetchProducts);
</script>> -->
<!-- <template>
  <div class="container mt-5">
    <h2>商品管理後台</h2>
    
   <div class="card p-3 mb-4">
  <input type="text" v-model="newProduct.name" placeholder="商品名稱" class="form-control mb-2">
  <input type="number" v-model="newProduct.basePrice" placeholder="價格" class="form-control mb-2">
  <input type="number" v-model="newProduct.brandId" placeholder="品牌 ID" class="form-control mb-2">
  <input type="number" v-model="newProduct.categoryId" placeholder="類別 ID" class="form-control mb-2">
  <input type="text" v-model="newProduct.imageUrl" placeholder="圖片網址" class="form-control mb-2">
  <button @click="addProduct" class="btn btn-primary">上傳新增</button>
</div>

    <table class="table">
      <thead>
        <tr>
          <th>圖片</th>
          <th>名稱</th>
          <th>價格</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in products" :key="item.id">
  <td>
    <img 
      v-if="item.imageUrl && item.imageUrl.startsWith('http')" 
      :src="item.imageUrl" 
      width="50" 
      alt="商品圖"
    >
    <span v-else>無圖片</span>
  </td>
  <td>{{ item.name }}</td>
  <td>{{ item.basePrice }}</td>
  <td>
    <button @click="deleteProduct(item.id)" class="btn btn-danger btn-sm">刪除</button>
  </td>
</tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const products = ref([]);

const newProduct = ref({ 
  name: '', 
  basePrice: 0, 
  brandId: 1,      // 設定預設值或從 UI 輸入
  categoryId: 1,   // 設定預設值或從 UI 輸入
  imageUrl: '', 
  status: 1        // 預設狀態為 1
});
let selectedFile = null;

const fetchProducts = async () => {
  try {
    const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/products`);
    // 如果 API 回傳格式是 { code: 200, data: [...] }
    products.value = res.data.data; 
    console.log("產品列表已更新:", products.value); // 在 Console 查看是否有 imageUrl
  } catch (error) {
    console.error("獲取商品失敗", error);
  }
};

const handleFileChange = (e) => { 
  selectedFile = e.target.files[0]; 
};

const addProduct = async () => {
  let finalImageUrl = newProduct.value.imageUrl;

  try {
    // 如果沒有輸入網址，且使用者選擇了檔案，則先執行上傳
    if (!finalImageUrl && selectedFile) {
      const fileFormData = new FormData();
      fileFormData.append('file', selectedFile);
      const uploadRes = await axios.post(`${import.meta.env.VITE_API_BASE_URL}/products/upload`, fileFormData);
      finalImageUrl = uploadRes.data.data;
    }

    if (!finalImageUrl) return alert("請貼上圖片網址或選擇檔案上傳");

    // 發送最終資料（統一為 JSON）
   await axios.post(`${import.meta.env.VITE_API_BASE_URL}/products`, {
    name: newProduct.value.name,
    basePrice: newProduct.value.basePrice,
    brandId: newProduct.value.brandId,
    categoryId: newProduct.value.categoryId,
    imageUrl: finalImageUrl,
    status: 1
    });

    alert("新增成功");
    fetchProducts();
    newProduct.value = { name: '', basePrice: 0, imageUrl: '' };
    selectedFile = null;
  } catch (error) {
    console.error("新增失敗", error);
    alert("新增失敗，請檢查後端");
  }
};

const deleteProduct = async (id) => {
  if (!confirm("確定要刪除嗎？")) return;
  await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/products/${id}`);
  fetchProducts();
};

onMounted(fetchProducts);
</script> --> 
<!-- <template>
  <div class="container mt-4">
    <h3>商品管理</h3>
    <hr>

    <div class="card p-3 mb-4">
      <h5>新增商品</h5>
      <input type="text" v-model="newProduct.name" placeholder="商品名稱" class="form-control mb-2">
      <input type="number" v-model="newProduct.basePrice" placeholder="價格" class="form-control mb-2">
      <input type="number" v-model="newProduct.brandId" placeholder="品牌 ID" class="form-control mb-2">
      <input type="number" v-model="newProduct.categoryId" placeholder="類別 ID" class="form-control mb-2">
      <input type="text" v-model="newProduct.imageUrl" placeholder="圖片網址" class="form-control mb-2">
      <button @click="addProduct" class="btn btn-primary">新增商品</button>
    </div>

    <table class="table table-hover">
      <thead class="table-light">
        <tr>
          <th>圖片</th>
          <th>名稱</th>
          <th>價格</th>
          <th>上架狀態</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in products" :key="item.id">
          <td>
            <img v-if="item.imageUrl && item.imageUrl.startsWith('http')"
              :src="item.imageUrl" width="50" alt="商品圖">
            <span v-else>無圖片</span>
          </td>
          <td>{{ item.name }}</td>
          <td>NT$ {{ item.basePrice }}</td>
          <td>
            <div class="form-check form-switch">
              <input class="form-check-input" type="checkbox"
                :checked="item.status === 1"
                @change="toggleStatus(item)">
              <label class="form-check-label">
                {{ item.status === 1 ? '上架' : '下架' }}
              </label>
            </div>
          </td>
          <td>
            <button @click="deleteProduct(item.id)" class="btn btn-danger btn-sm">刪除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const products = ref([]);
const newProduct = ref({
  name: '',
  basePrice: 0,
  brandId: 1,
  categoryId: 1,
  imageUrl: '',
  status: 1
});

const fetchProducts = async () => {
  try {
    const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/products`);
    products.value = res.data.data;
  } catch (error) {
    console.error("獲取商品失敗", error);
  }
};

const addProduct = async () => {
  if (!newProduct.value.imageUrl) return alert("請貼上圖片網址");
  try {
    await axios.post(`${import.meta.env.VITE_API_BASE_URL}/products`, {
      ...newProduct.value,
      status: 1
    });
    alert("新增成功");
    fetchProducts();
    newProduct.value = { name: '', basePrice: 0, brandId: 1, categoryId: 1, imageUrl: '', status: 1 };
  } catch (error) {
    console.error("新增失敗", error);
    alert("新增失敗，請檢查後端");
  }
};

const toggleStatus = async (item) => {
  const newStatus = item.status === 1 ? 0 : 1;
  try {
    await axios.put(`${import.meta.env.VITE_API_BASE_URL}/products/${item.id}/status?status=${newStatus}`);
    item.status = newStatus;
  } catch (error) {
    alert("狀態更新失敗");
  }
};

const deleteProduct = async (id) => {
  if (!confirm("確定要刪除嗎？")) return;
  try {
    await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/products/${id}`);
    fetchProducts();
  } catch (error) {
    alert("刪除失敗");
  }
};

onMounted(fetchProducts);
</script> -->
<template>
  <div class="container mt-4">
    <h3>商品管理</h3>
    <hr>

    <div class="card p-4 mb-4 shadow-sm">
      <h5 class="fw-bold mb-3 text-primary">✨ 新增商品（主資訊）</h5>
      
      <div class="row g-2 mb-3">
        <div class="col-md-6">
          <label class="form-label small fw-bold text-muted">商品名稱</label>
          <input type="text" v-model="newProduct.name" placeholder="請輸入商品名稱 (例如: 渴望天然無穀全犬飼料)" class="form-control">
        </div>
        <div class="col-md-3">
          <label class="form-label small fw-bold text-muted">基本價格</label>
          <input type="number" v-model="newProduct.basePrice" class="form-control">
        </div>
        <div class="col-md-3">
          <label class="form-label small fw-bold text-muted">主圖片網址</label>
          <input type="text" v-model="newProduct.imageUrl" placeholder="請貼上圖片網址 (http://...)" class="form-control">
        </div>
      </div>

      <div class="row g-2 mb-3 align-items-end">
        <div class="col-md-4">
          <label class="form-label small fw-bold text-muted">品牌 ID</label>
          <input type="number" v-model="newProduct.brandId" class="form-control">
        </div>
        <div class="col-md-4">
          <label class="form-label small fw-bold text-muted">分類 ID</label>
          <input type="number" v-model="newProduct.categoryId" class="form-control">
        </div>
        <div class="col-md-4 pb-1 ps-3">
          <label class="form-label small fw-bold text-primary d-block">預設上架狀態</label>
          <div class="form-check form-switch form-control-lg p-0 m-0 d-flex align-items-center">
            <input class="form-check-input ms-0 me-2" type="checkbox" id="defaultStatusSwitch"
              :checked="newProduct.status === 1"
              @change="newProduct.status = newProduct.status === 1 ? 0 : 1" style="cursor: pointer;">
            <label class="form-check-label fw-bold" for="defaultStatusSwitch" :class="newProduct.status === 1 ? 'text-success' : 'text-danger'">
              {{ newProduct.status === 1 ? '開啟：建立後直接上架' : '關閉：先下架隱藏' }}
            </label>
          </div>
        </div>
      </div>

      <div class="row g-2 mb-4">
        <div class="col-md-12">
          <label class="form-label small fw-bold text-muted">商品詳細描述 (description)</label>
          <textarea v-model="newProduct.description" rows="3" placeholder="請輸入商品的詳細介紹、成分、有效期限或是營養標示..." class="form-control"></textarea>
        </div>
      </div>

      <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="fw-bold text-secondary mb-0">💡 設定商品規格</h5>
        <span class="text-danger small">* 在此輸入口味與尺寸將可直接寫入資料庫</span>
      </div>
      
      <div v-for="(sku, index) in newProduct.skus" :key="index" class="card p-3 mb-2 bg-light border-light-subtle">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <span class="badge bg-secondary">規格組合 #{{ index + 1 }}</span>
          <button @click="removeSku(index)" class="btn btn-outline-danger btn-sm" v-if="newProduct.skus.length > 1">移除此規格</button>
        </div>
        
        <div class="row g-2">
          <div class="col-md-2">
            <label class="form-label small text-muted d-block mb-1">SKU 編碼 (skuCode)</label>
            <input type="text" v-model="sku.skuCode" placeholder="如: ORI-CH-2KG" class="form-control form-control-sm">
          </div>
          <div class="col-md-2">
            <label class="form-label small text-muted d-block mb-1">尺寸/重量 (size)</label>
            <input type="text" v-model="sku.size" placeholder="如: 2kg / 400g" class="form-control form-control-sm">
          </div>
          <div class="col-md-2">
            <label class="form-label small text-muted d-block mb-1">口味 (flavor)</label>
            <input type="text" v-model="sku.flavor" placeholder="如: 雞肉 / 鮭魚" class="form-control form-control-sm">
          </div>
          <div class="col-md-2">
            <label class="form-label small text-muted d-block mb-1">此規格售價 (price)</label>
            <input type="number" v-model="sku.price" class="form-control form-control-sm">
          </div>
          <div class="col-md-2">
            <label class="form-label small text-muted d-block mb-1">現貨庫存 (stock)</label>
            <input type="number" v-model="sku.stock" class="form-control form-control-sm">
          </div>
          <div class="col-md-2">
            <label class="form-label small text-muted d-block mb-1">保留庫存 (reservedStock)</label>
            <input type="number" v-model="sku.reservedStock" class="form-control form-control-sm">
          </div>
        </div>
      </div>

      <div class="d-flex justify-content-between mt-4">
        <button @click="addSkuRow" class="btn btn-outline-secondary">+ 增加一種規格/尺寸</button>
        <button @click="addProduct" class="btn btn-primary px-5 fw-bold">建立商品與全部規格</button>
      </div>
    </div>

    <div class="card p-3 shadow-sm mb-5">
      <h5 class="fw-bold text-dark mb-3">📦 目前商品總覽 (包含規格與描述)</h5>
      <div class="table-responsive">
        <table class="table table-hover align-middle border text-center">
          <thead class="table-dark text-nowrap">
            <tr>
              <th style="width: 80px;">商品圖片</th>
              <th style="width: 280px;">商品基本資訊</th>
              <th style="width: 120px;">基本價格</th>
              <th>規格與庫存清單 (SKUs) <small class="text-info d-block" style="font-size: 10px;">(💡 點擊個別規格區塊即可直接修改口味與尺寸)</small></th>
              <th style="width: 120px; text-align: center;">上架狀態</th>
              <th style="width: 160px; text-align: center;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in products" :key="item.id">
              <td>
                <img v-if="item.imageUrl && item.imageUrl.startsWith('http')"
                  :src="item.imageUrl" width="65" height="65" class="object-fit-cover rounded border shadow-sm" alt="商品圖"
                  @error="$event.target.src='https://placehold.co/100x100.png'">
                <span v-else class="badge bg-light text-muted border p-2 d-inline-block">無圖片</span>
              </td>

              <td class="text-start">
                <div class="fw-bold text-primary fs-5 mb-1">{{ item.name }}</div>
                <div class="p-2 bg-light rounded border-start border-3 border-secondary small text-secondary mb-1" style="white-space: pre-line; max-height: 80px; overflow-y: auto;">
                  <strong>📋 商品描述：</strong><br>
                  {{ item.description || '暫無詳細描述' }}
                </div>
                <div class="small text-muted d-flex gap-2">
                  <span>品牌 ID: <span class="badge bg-secondary">{{ item.brandId }}</span></span>
                  <span>分類 ID: <span class="badge bg-info text-dark">{{ item.categoryId }}</span></span>
                </div>
              </td>

              <td class="fw-bold text-danger fs-5 text-nowrap">
                NT$ {{ item.basePrice }}
              </td>

              <td class="text-start">
                <div v-if="item.skus && item.skus.length > 0" class="d-flex flex-column gap-1">
                  <div v-for="sku in item.skus" :key="sku.id" 
                       @click="openEditSkuModal(sku, item.name)"
                       title="點擊修改此規格細項"
                       class="sku-click-row p-2 rounded border d-flex align-items-center justify-content-between bg-white small shadow-xs">
                    <div>
                      <span class="badge bg-dark me-1">🔑 {{ sku.skuCode }}</span>
                      <span class="badge bg-info text-dark me-1">😋 口味: {{ sku.flavor || '單一規格' }}</span>
                      <span class="badge bg-warning text-dark me-1">📏 尺寸: {{ sku.size || '單一規格' }}</span>
                      <span class="text-muted ms-1">價格: <strong class="text-danger">NT$ {{ sku.price }}</strong></span>
                    </div>
                    <div class="text-nowrap ms-2">
                      <span class="badge bg-success me-1">現貨: {{ sku.stock }}</span>
                      <span class="badge bg-secondary me-1">保留: {{ sku.reservedStock }}</span>
                      <span class="text-primary small" style="font-size: 11px;">✏️</span>
                    </div>
                  </div>
                </div>
                <div v-else class="text-center py-2">
                  <button @click="loadSkusForProduct(item)" class="btn btn-xs btn-outline-info p-1 py-0" style="font-size: 11px;">
                    🔍 重新載入規格
                  </button>
                </div>
              </td>

              <td class="text-center">
                <div class="form-check form-switch d-inline-block">
                  <input class="form-check-input" type="checkbox" style="cursor: pointer; width: 2.2em; height: 1.1em;"
                    :checked="item.status === 1"
                    @change="toggleStatus(item)">
                  <label class="form-check-label fw-bold d-block mt-1 small" :class="item.status === 1 ? 'text-success' : 'text-danger'">
                    {{ item.status === 1 ? '上架中' : '已下架' }}
                  </label>
                </div>
              </td>

              <td class="text-center">
                <div class="d-flex justify-content-center gap-1 text-nowrap">
                  <button @click="openEditModal(item)" class="btn btn-outline-primary btn-sm fw-bold px-2">
                    ✏️ 修改
                  </button>
                  <button @click="deleteProduct(item.id)" class="btn btn-danger btn-sm fw-bold px-2 shadow-sm">
                    🗑️ 刪除
                  </button>
                </div>
              </td>
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
import { userAuthStore } from '@/stores/auth.js';

const products = ref([]);

// ✅ 每次呼叫都從 Pinia 拿最新 token
const createAuthAxios = () => {
  const authStore = userAuthStore();
  return axios.create({
    baseURL: `${import.meta.env.VITE_API_BASE_URL}`,
    headers: {
      Authorization: `Bearer ${authStore.accessToken}`
    }
  });
};

const initForm = () => ({
  name: '',
  description: '',   
  basePrice: 0,     
  brandId: 1,      
  categoryId: 1,    
  imageUrl: '',     
  status: 1,         
  skus: [
    { 
      skuCode: '',    
      size: '',       
      flavor: '',     
      price: 0,       
      stock: 10,      
      reservedStock: 0 
    }
  ]
});

const newProduct = ref(initForm());

const addSkuRow = () => {
  newProduct.value.skus.push({
    skuCode: '', size: '', flavor: '', price: 0, stock: 10, reservedStock: 0
  });
};

const removeSku = (index) => {
  newProduct.value.skus.splice(index, 1);
};

const fetchProducts = async () => {
  const api = createAuthAxios();
  try {
    const res = await api.get('/products');
    const productList = res.data.data ?? res.data;

    for (let product of productList) {
      try {
        const skuRes = await api.get(`/products/${product.id}/skus`);
        product.skus = skuRes.data.data ?? skuRes.data; 
      } catch (skuErr) {
        console.error(`無法同步獲取商品 ID ${product.id} 的規格`, skuErr);
        product.skus = []; 
      }
    }
    products.value = productList;
  } catch (error) {
    console.error("獲取商品清單失敗", error);
  }
};

const loadSkusForProduct = async (item) => {
  const api = createAuthAxios();
  try {
    const skuRes = await api.get(`/products/${item.id}/skus`);
    item.skus = skuRes.data.data ?? skuRes.data;
  } catch (error) {
    console.error("單獨載入規格失敗", error);
  }
};

const addProduct = async () => {
  if (!newProduct.value.name) {
    Swal.fire({ text: "請輸入商品名稱", icon: "warning" });
    return;
  }
  if (!newProduct.value.imageUrl) {
    Swal.fire({ text: "請貼上圖片網址", icon: "warning" });
    return;
  }

  const api = createAuthAxios();
  try {
    await api.post('/products', newProduct.value);
    Swal.fire({ text: "商品、描述與所有規格已成功同步建立！", icon: "success", timer: 1500 });
    fetchProducts();
    newProduct.value = initForm(); 
  } catch (error) {
    console.error("新增失敗", error);
    Swal.fire({ text: "新增失敗，請檢查欄位格式或後端對接狀態。", icon: "error" });
  }
};

const openEditModal = async (item) => {
  const { value: formValues } = await Swal.fire({
    title: '📝 修改商品核心主檔',
    html:
      `<div style="text-align: left; font-size: 14px;">` +
        `<label class="fw-bold mb-1">商品名稱 *：</label>` +
        `<input id="edit-name" class="swal2-input m-0 w-100 mb-3" style="font-size: 14px;" value="${item.name}">` +
        `<label class="fw-bold mb-1">基本價格 (NT$) *：</label>` +
        `<input id="edit-price" type="number" class="swal2-input m-0 w-100 mb-3" style="font-size: 14px;" value="${item.basePrice}">` +
        `<div class="row g-2 mb-3">` +
          `<div class="col-6">` +
            `<label class="fw-bold mb-1">分類 ID：</label>` +
            `<input id="edit-category" type="number" class="swal2-input m-0 w-100" style="font-size: 14px;" value="${item.categoryId || 1}">` +
          `</div>` +
          `<div class="col-6">` +
            `<label class="fw-bold mb-1">品牌 ID：</label>` +
            `<input id="edit-brand" type="number" class="swal2-input m-0 w-100" style="font-size: 14px;" value="${item.brandId || 1}">` +
          `</div>` +
        `</div>` +
        `<label class="fw-bold mb-1">主圖片網址：</label>` +
        `<input id="edit-image" class="swal2-input m-0 w-100 mb-3" style="font-size: 14px;" value="${item.imageUrl || ''}">` +
        `<label class="fw-bold mb-1">商品詳細描述：</label>` +
        `<textarea id="edit-desc" class="swal2-textarea m-0 w-100" style="font-size: 14px; height: 80px; resize: none;">${item.description || ''}</textarea>` +
      `</div>`,
    focusConfirm: false,
    showCancelButton: true,
    confirmButtonText: '儲存修改',
    cancelButtonText: '取消',
    preConfirm: () => {
      return {
        name: document.getElementById('edit-name').value.trim(),
        basePrice: Number(document.getElementById('edit-price').value),
        categoryId: Number(document.getElementById('edit-category').value),
        brandId: Number(document.getElementById('edit-brand').value),
        imageUrl: document.getElementById('edit-image').value.trim(),
        description: document.getElementById('edit-desc').value.trim()
      }
    }
  });

  if (formValues) {
    if (!formValues.name || formValues.basePrice < 0) {
      Swal.fire({ text: '商品名稱及基本價格填寫不完全！', icon: 'warning' });
      return;
    }
    const api = createAuthAxios();
    try {
      await api.put(`/products/${item.id}`, {
        ...item,
        name: formValues.name,
        basePrice: formValues.basePrice,
        categoryId: formValues.categoryId,
        brandId: formValues.brandId,
        imageUrl: formValues.imageUrl,
        description: formValues.description
      });
      Swal.fire({ text: '商品主檔修改成功！', icon: 'success', timer: 1200 });
      fetchProducts();
    } catch (error) {
      console.error('更新主檔失敗', error);
      Swal.fire({ text: '更新主檔失敗，請確認 API 對接規範。', icon: 'error' });
    }
  }
};

const openEditSkuModal = async (sku, productName) => {
  const { value: skuValues } = await Swal.fire({
    title: '🎨 修改規格細項（口味/尺寸）',
    text: `所屬商品：${productName}`,
    html:
      `<div style="text-align: left; font-size: 14px;">` +
        `<p class="mb-2 text-muted">規格條碼: <strong class="text-dark">${sku.skuCode}</strong></p>` +
        `<label class="fw-bold mb-1">商品口味 (flavor)：</label>` +
        `<input id="sku-flavor" class="swal2-input m-0 w-100 mb-3" style="font-size: 14px;" value="${sku.flavor || ''}" placeholder="如：雞肉、鮮蝦">` +
        `<label class="fw-bold mb-1">包裝尺寸 (size)：</label>` +
        `<input id="sku-size" class="swal2-input m-0 w-100 mb-3" style="font-size: 14px;" value="${sku.size || ''}" placeholder="如：2kg、500g">` +
        `<div class="row g-2">` +
          `<div class="col-6">` +
            `<label class="fw-bold mb-1">此規格售價 ($)：</label>` +
            `<input id="sku-price" type="number" class="swal2-input m-0 w-100" style="font-size: 14px;" value="${sku.price}">` +
          `</div>` +
          `<div class="col-6">` +
            `<label class="fw-bold mb-1">現貨庫存 (stock)：</label>` +
            `<input id="sku-stock" type="number" class="swal2-input m-0 w-100" style="font-size: 14px;" value="${sku.stock}">` +
          `</div>` +
        `</div>` +
      `</div>`,
    focusConfirm: false,
    showCancelButton: true,
    confirmButtonText: '確認修改規格',
    cancelButtonText: '取消',
    confirmButtonColor: '#198754',
    preConfirm: () => {
      return {
        flavor: document.getElementById('sku-flavor').value.trim() || '',
        size: document.getElementById('sku-size').value.trim() || '',
        price: Number(document.getElementById('sku-price').value),
        stock: Number(document.getElementById('sku-stock').value)
      }
    }
  });

  if (skuValues) {
    const api = createAuthAxios();
    try {
      Swal.showLoading();
      const payload = {
        id: sku.id,
        productId: sku.productId,
        skuCode: sku.skuCode,
        flavor: skuValues.flavor,
        size: skuValues.size,
        price: skuValues.price ? Number(skuValues.price).toFixed(2).toString() : "0.00",
        stock: Number(skuValues.stock) || 0,
        reservedStock: sku.reservedStock || 0
      };

      await api.put(`/products/skus/${sku.id}`, payload);
      Swal.fire({ text: '規格細項修改成功！', icon: 'success', timer: 1200 });
      fetchProducts();
    } catch (error) {
      console.error('更新 SKU 失敗', error);
      const backendErrorMessage = error.response?.data?.message || error.response?.data || '請確認後端 SKU 欄位或 API 節點開關。';
      Swal.fire({ 
        title: '修改規格失敗',
        html: `<div style="text-align:left; font-size:13px; background:#f8d7da; padding:10px; border-radius:5px;">` +
              `<strong>錯誤狀態碼:</strong> ${error.response?.status || '未知'}<br>` +
              `<strong>後端錯誤訊息:</strong> ${backendErrorMessage}` +
              `</div>`, 
        icon: 'error' 
      });
    }
  }
};

const toggleStatus = async (item) => {
  const api = createAuthAxios();
  const newStatus = item.status === 1 ? 0 : 1;
  try {
    await api.put(`/products/${item.id}/status?status=${newStatus}`);
    item.status = newStatus;
  } catch (error) {
    Swal.fire({ text: "狀態更新失敗", icon: "error" });
    fetchProducts();
  }
};

const deleteProduct = async (id) => {
  const result = await Swal.fire({
    title: '確定要刪除嗎？',
    text: "這將會一併下架並永久刪除此商品下的所有規格組合！",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#dc3545',
    confirmButtonText: '確定刪除',
    cancelButtonText: '取消'
  });

  if (result.isConfirmed) {
    const api = createAuthAxios();
    try {
      await api.delete(`/products/${id}`);
      Swal.fire({ text: "商品已安全移除", icon: "success", timer: 1000 });
      fetchProducts();
    } catch (error) {
      Swal.fire({ text: "刪除失敗，該商品可能有建立相關訂單紀錄", icon: "error" });
    }
  }
};

onMounted(fetchProducts);
</script>

<style scoped>
/* 💡 新增：滑鼠移動到規格細項時顯示可點選編輯的手勢與高亮效能 */
.sku-click-row {
  cursor: pointer;
  border-color: #e3e6ec !important;
  transition: all 0.2s ease;
}
.sku-click-row:hover {
  background-color: #edf2f7 !important;
  border-color: #3b82f6 !important;
  transform: translateX(2px);
}
</style>