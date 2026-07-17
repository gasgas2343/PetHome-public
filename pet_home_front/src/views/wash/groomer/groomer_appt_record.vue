<template>
  <div class="wash-manager">

    <!-- Header -->
    <div class="page-header">
      <div>
        <h1>美容服務回饋</h1>
        <div class="subtitle-en">SERVICE RECORD</div>
        <p class="subtitle">記錄編號: {{ serviceForm.record_no }}</p>
      </div>
    </div>

    <!-- Tabs -->
    <div class="tabs">
      <button class="tab" :class="{ active: currentTab === 'record' }" @click="currentTab = 'record'">📝 服務紀錄</button>
      <button class="tab" :class="{ active: currentTab === 'photos' }" @click="currentTab = 'photos'">
        📷 照片管理 <span class="badge badge-package" style="margin-left: 8px;">{{ photoList.length }}</span>
      </button>
    </div>

    <div class="form-container">
      <!-- 服務紀錄 Tab -->
      <div v-if="currentTab === 'record'">
        
        <!-- 基本資訊 -->
        <div class="form-section">
          <h5 class="form-section-title">ℹ️ 基本資訊</h5>
          <div class="row g-3">
            <div class="col-md-4">
              <label class="form-label">記錄編號</label>
              <input v-model="serviceForm.record_no" type="text" disabled class="form-control" />
            </div>
            <div class="col-md-4">
              <label class="form-label">預約單 ID</label>
              <input v-model="serviceForm.appt_order_id" type="number" placeholder="appt_order_id" class="form-control" />
            </div>
            <div class="col-md-4">
              <label class="form-label">美容師 ID</label>
              <input v-model="serviceForm.pet_groomer_id" type="number" placeholder="pet_groomer_id" class="form-control" />
            </div>
            <div class="col-md-4">
              <label class="form-label">寵物 ID</label>
              <input v-model="serviceForm.pet_id" type="number" placeholder="pet_id (BIGINT)" class="form-control" />
            </div>
          </div>
        </div>

        <!-- 皮膚狀況 -->
        <div class="form-section">
          <h5 class="form-section-title">🔍 皮膚狀況 (SKIN_STATUS)</h5>
          <div class="option-group">
            <button 
              v-for="opt in skinOptions" :key="opt.value" 
              type="button"
              @click="serviceForm.skin_status = opt.value"
              :class="['btn', serviceForm.skin_status === opt.value ? 'btn-primary' : '']"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>

        <!-- 情緒狀態 -->
        <div class="form-section">
          <h5 class="form-section-title">😊 情緒狀態 (MOOD_STATUS)</h5>
          <div class="option-group">
            <button 
              v-for="opt in moodOptions" :key="opt.value" 
              type="button"
              @click="serviceForm.mood_status = opt.value"
              :class="['btn', serviceForm.mood_status === opt.value ? 'btn-primary' : '']"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>

        <!-- 健康評估與備註 -->
        <div class="form-section">
          <h5 class="form-section-title">📋 健康評估與備註</h5>
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label">健康評語 (health_comment)</label>
              <textarea v-model="serviceForm.health_comment" rows="4" placeholder="美容師填寫健康狀況評語..." class="form-control"></textarea>
            </div>
            <div class="col-md-6">
              <label class="form-label">飼主回覆 (owner_reply)</label>
              <textarea v-model="serviceForm.owner_reply" rows="4" placeholder="飼主回覆..." class="form-control"></textarea>
            </div>
            <div class="col-12">
              <label class="form-label">備註回覆 (note_reply)</label>
              <textarea v-model="serviceForm.note_reply" rows="3" placeholder="其他備註..." class="form-control"></textarea>
            </div>
          </div>
        </div>

        <!-- 下次回訪建議 -->
        <div class="form-section">
          <h5 class="form-section-title">📅 下次回訪建議</h5>
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label">建議回訪日期 (next_visit_date)</label>
              <input v-model="serviceForm.next_visit_date" type="date" class="form-control" />
            </div>
            <div class="col-md-6">
              <label class="form-label">回訪建議 (next_visit_suggestion)</label>
              <input v-model="serviceForm.next_visit_suggestion" type="text" placeholder="例：建議4週後再次洗澡..." class="form-control" />
            </div>
          </div>
        </div>

      </div>

      <!-- 照片管理 Tab -->
      <div v-if="currentTab === 'photos'" class="animate-fade-in">
        
        <div class="form-section">
          <h5 class="form-section-title">📷 服務照片 (WASHPHOTO)</h5>

          <div class="option-group mb-4">
            <button 
              v-for="typeOpt in photoTypeTabs" :key="typeOpt.value"
              type="button"
              @click="activePhotoType = typeOpt.value"
              :class="['btn', activePhotoType === typeOpt.value ? 'btn-primary' : '']"
            >
              {{ typeOpt.label }} 
              <span class="opacity-75 ms-1">({{ getPhotoCountByType(typeOpt.value) }})</span>
            </button>
          </div>

          <div class="row g-3">
            
            <div 
              v-for="photo in filteredPhotoList" :key="photo.photo_id"
              class="col-6 col-sm-4 col-md-3"
            >
              <div class="photo-card">
                <img :src="photo.thumbnail_url || photo.image_url" :alt="photo.file_name" class="photo-img" />
                
                <div class="photo-privacy-badge">
                  <span v-if="photo.is_public" class="badge badge-active">公開</span>
                  <span v-else class="badge badge-inactive">私密</span>
                </div>

                <div class="photo-caption">
                  <p class="photo-filename">{{ photo.file_name || '未命名照片' }}</p>
                </div>

                <div class="photo-overlay">
                  <button type="button" @click="togglePhotoPrivacy(photo)" class="btn btn-sm">切換權限</button>
                  <button type="button" @click="deletePhoto(photo.photo_id)" class="btn btn-sm btn-danger">刪除</button>
                </div>
              </div>
            </div>

            <div class="col-6 col-sm-4 col-md-3">
              <button 
                type="button"
                @click="triggerUpload"
                class="photo-upload-card"
              >
                <div class="upload-icon-wrap">
                  <span style="font-size: 24px; line-height: 1;">+</span>
                </div>
                <span class="upload-text">新增照片</span>
              </button>
            </div>
          </div>
        </div>

      </div>

      <!-- 操作按鈕 -->
      <div class="form-actions">
        <button type="button" @click="saveAsDraft" class="btn">
          儲存草稿
        </button>
        <button type="button" @click="submitAllData" class="btn btn-primary">
          確認送出
        </button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// ==========================================
// 1. 頁面狀態與導覽
// ==========================================
const currentTab = ref('record')       // 主頁籤：'record' (服務紀錄) 或 'photos' (照片管理)
const activePhotoType = ref(0)        // 照片子頁籤：0:Before, 1:After, 2:Process, 3:Other

// ==========================================
// 2. 表單核心資料 (完全對應 WashServiceRecord 欄位)
// ==========================================
const serviceForm = ref({
  record_no: 'REC-20250616-0001',     // 格式：REC-YYYYMMDD-NNNN
  appt_order_id: null,
  pet_groomer_id: null,
  pet_id: null,
  skin_status: 0,                     // TINYINT: 0:正常, 1:乾燥, 2:敏感, 3:異常
  mood_status: 0,                     // TINYINT: 0:快樂/放鬆, 1:焦慮/壓力, 2:害怕/警戒, 3:尋求關注/興奮
  health_comment: '',
  owner_reply: '',
  note_reply: '',
  next_visit_date: '',
  next_visit_suggestion: ''
})

// ==========================================
// 3. 多媒體資料清單 (完全對應 WashPhoto 欄位)
// ==========================================
const photoList = ref([
  { photo_id: 1, record_id: 12, image_url: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=400', thumbnail_url: '', photo_type: 0, is_public: true, file_name: '洗澡前全狗照.jpg' },
  { photo_id: 2, record_id: 12, image_url: 'https://images.unsplash.com/photo-1534361960057-19889db9621e?q=80&w=400', thumbnail_url: '', photo_type: 0, is_public: false, file_name: '皮膚紅腫局部特寫.jpg' },
  { photo_id: 3, record_id: 12, image_url: 'https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?q=80&w=400', thumbnail_url: '', photo_type: 1, is_public: true, file_name: '吹整完成照-正.jpg' },
  { photo_id: 4, record_id: 12, image_url: 'https://images.unsplash.com/photo-1601758228041-f3b2795255f1?q=80&w=400', thumbnail_url: '', photo_type: 2, is_public: true, file_name: '微泡浴進行中.jpg' }
])

// ==========================================
// 4. 定義各欄位的下拉/點選設定檔
// ==========================================
const skinOptions = [
  { label: '正常', value: 0 },
  { label: '乾燥', value: 1 },
  { label: '敏感', value: 2 },
  { label: '異常', value: 3 }
]

const moodOptions = [
  { label: '快樂/放鬆', value: 0 },
  { label: '焦慮/壓力', value: 1 },
  { label: '害怕/警戒', value: 2 },
  { label: '尋求關注/興奮', value: 3 }
]

const photoTypeTabs = [
  { label: 'Before', value: 0 },
  { label: 'After', value: 1 },
  { label: 'Process', value: 2 },
  { label: 'Other', value: 3 }
]

// ==========================================
// 5. Computed 計算屬性過濾照片
// ==========================================
const filteredPhotoList = computed(() => {
  return photoList.value.filter(photo => photo.photo_type === activePhotoType.value)
})

const getPhotoCountByType = (typeValue) => {
  return photoList.value.filter(photo => photo.photo_type === typeValue).length
}

// ==========================================
// 6. 操作方法與互動
// ==========================================
const togglePhotoPrivacy = (photo) => {
  photo.is_public = !photo.is_public
}

const deletePhoto = (id) => {
  if (confirm('確定要刪除這張照片嗎？')) {
    photoList.value = photoList.value.filter(p => p.photo_id !== id)
  }
}

const triggerUpload = () => {
  alert('這裡可以觸發 HTML `<input type="file">` 的點擊事件來實作圖片上傳功能！')
}

const saveAsDraft = () => {
  console.log('暫存草稿資料:', { serviceForm: serviceForm.value, photos: photoList.value })
  alert('草稿已儲存(請於 Console 查看資料結構)')
}

const submitAllData = () => {
  const payload = {
    ...serviceForm.value,
    photos: photoList.value // 將照片清單一同打包發送
  }
  console.log('準備打 API 送出的完整 JSON 封包:', JSON.stringify(payload, null, 2))
  alert('全部資料已統整！已為您打包好符合對應資料表的 JSON 結構。')
}
</script>

<style scoped>
* { box-sizing: border-box; }

.wash-manager {
  font-family: 'Noto Sans TC', sans-serif;
  font-size: 15px;
  color: #3A3142;
  padding: 2.5rem 3rem;
  max-width: 1200px;
  margin: 2rem auto;
  background: linear-gradient(135deg, #FAF6FC 0%, #F3EBF5 100%);
  border-radius: 24px;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.6), 0 10px 40px rgba(123, 94, 156, 0.06);
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
  background: linear-gradient(135deg, #7B5E9C 0%, #3A3142 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.subtitle-en {
  font-size: 12px;
  color: #A087B5;
  letter-spacing: 3px;
  font-weight: 600;
  margin-top: 4px;
  margin-bottom: 14px;
}
.subtitle {
  font-size: 14px;
  color: #7B5E9C;
  letter-spacing: 1px;
  font-weight: 500;
}

/* Tabs */
.tabs {
  display: flex;
  gap: 8px;
  border-bottom: 1px solid rgba(123, 94, 156, 0.2);
  margin-bottom: 1.5rem;
}
.tab {
  padding: 10px 20px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  background: none;
  color: #A087B5;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  font-family: inherit;
  transition: all 0.3s ease;
}
.tab:hover {
  color: #7B5E9C;
}
.tab.active {
  color: #7B5E9C;
  border-bottom-color: #7B5E9C;
  font-weight: 700;
}

/* Badge */
.badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
}
.badge-package { background: #E8DDF0; color: #7B5E9C; }
.badge-inactive{ background: #FAF6FC; color: #A087B5; }
.badge-active  { background: #E6F4F0; color: #53A18C; }

/* Buttons */
.btn {
  padding: 9px 18px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  border: none;
  background: #E8DDF0;
  color: #7B5E9C;
  transition: all 0.15s;
  display: inline-flex; align-items: center; justify-content: center; gap: 6px;
  box-shadow: 0 2px 6px rgba(123, 94, 156, 0.08);
}
.btn:hover { background: #C0A1C6; color: #3A3142; }
.btn-primary {
  background-color: #D1A153; /* 香檳金做為選取與確認的主跳色 */
  color: white;
  box-shadow: 0 4px 10px rgba(209, 161, 83, 0.2);
}
.btn-primary:hover { background-color: #BC8F42; color: white; box-shadow: 0 6px 15px rgba(209, 161, 83, 0.3); }
.btn-danger {
  background-color: #DB9FA2;
  color: white;
  box-shadow: 0 4px 10px rgba(219, 159, 162, 0.2);
}
.btn-danger:hover { background-color: #C88A8D; }
.btn-sm {
  padding: 5px 12px;
  font-size: 13px;
  border-radius: 8px;
}

/* Form Styles */
.form-container {
  background: #FFFFFF;
  border-radius: 20px;
  padding: 2.5rem;
  box-shadow: 0 4px 20px rgba(123, 94, 156, 0.05);
  margin-top: 1.5rem;
}

.form-section {
  margin-bottom: 2.5rem;
}

.form-section:last-child {
  margin-bottom: 0;
}

.form-section-title {
  font-size: 16px;
  font-weight: 700;
  color: #7B5E9C;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #F3EBF5;
}

.option-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 2.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #F3EBF5;
}

.form-label {
  color: #7B5E9C; 
  font-weight: 600;
  margin-bottom: 0.5rem;
  font-size: 14px;
  display: block;
}

.form-control, .form-select {
  width: 100%;
  border: 1px solid #EADFF2;
  border-radius: 12px;
  background-color: #FAF8FB;
  box-shadow: inset 0 2px 4px rgba(0,0,0,0.01);
  padding: 0.5rem 1rem;
  font-family: inherit;
  font-size: 14px;
  color: #3A3142;
}
.form-control:focus, .form-select:focus {
  outline: none;
  border-color: #7B5E9C;
  background-color: #FFFFFF;
  box-shadow: 0 0 0 0.25rem rgba(123, 94, 156, 0.2);
}
.form-control:disabled {
  background-color: #FAF6FC;
  color: #A087B5;
  cursor: not-allowed;
  border-color: #EADFF2;
}
textarea.form-control {
  resize: vertical;
}

/* Photo Tab Styles */
.photo-card {
  position: relative;
  aspect-ratio: 4 / 3;
  border-radius: 16px;
  overflow: hidden;
  background-color: #F3EBF5;
  box-shadow: 0 4px 15px rgba(123, 94, 156, 0.05);
  border: 1px solid #F3EBF5;
}
.photo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}
.photo-card:hover .photo-img {
  transform: scale(1.05);
}
.photo-privacy-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 10;
}
.photo-caption {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 1rem 0.75rem 0.75rem;
  background: linear-gradient(to top, rgba(0,0,0,0.7) 0%, transparent 100%);
}
.photo-filename {
  color: white;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0;
}
.photo-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.4);
  opacity: 0;
  transition: opacity 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.photo-card:hover .photo-overlay {
  opacity: 1;
}
.photo-upload-card {
  width: 100%;
  aspect-ratio: 4 / 3;
  border-radius: 16px;
  border: 2px dashed #E8DDF0;
  background-color: #FAF8FB;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.photo-upload-card:hover {
  border-color: #7B5E9C;
  background-color: #FAF6FC;
}
.upload-icon-wrap {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #E8DDF0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #7B5E9C;
  transition: transform 0.2s ease;
}
.photo-upload-card:hover .upload-icon-wrap {
  transform: scale(1.1);
}
.upload-text {
  font-size: 13px;
  font-weight: 600;
  color: #A087B5;
}

/* Animation */
.animate-fade-in {
  animation: fadeIn 0.3s ease-out forwards;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(5px); }
  to { opacity: 1; transform: translateY(0); }
}

/* RWD 響應式設計 (手機/平板優化) */
@media (max-width: 768px) {
  .wash-manager {
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
  .form-container {
    padding: 1.5rem;
  }
}
</style>