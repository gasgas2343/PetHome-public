<script setup>
// ===== Vue =====
import { onMounted, ref } from 'vue'

// ===== Router =====
import { useRoute, useRouter } from 'vue-router'

// ===== Store =====
import { userAuthStore } from '@/stores/auth'
import { usePlaceStore } from '@/stores/petmap/placeStore'

// ===== API =====
import {
  createReview,
  deleteReview,
  getReviewSummary,
  getReviewsByPlaceId,
  updateReview,
} from '@/api/petmap/reviewApi'

import {
  addPlaceToItinerary,
  getMyItineraries,
} from '@/api/petmap/itineraryApi'

import { getRepliesByReviewId } from '@/api/petmap/merchantReviewReplyApi'
import { getPlaceImagesByPlaceId } from '@/api/petmap/placeImageApi'
import {
  createReviewImage,
  getReviewImagesByReviewId,
} from '@/api/petmap/reviewImageApi'
import { createReport } from '@/api/petmap/reportApi'
import { getTagsByPlaceId } from '@/api/petmap/tagApi'

// ===== Component =====
import FavoriteButton from '@/components/petmap/FavoriteButton.vue'
import PlaceMap from '@/components/petmap/PlaceMap.vue'
import UserNavbar from '@/components/petmap/UserNavbar.vue'

// ===== Library =====
import Swal from 'sweetalert2'

// ===== Router =====
const route = useRoute()
const router = useRouter()

// ===== Store =====
const authStore = userAuthStore()
const placeStore = usePlaceStore()

// ===== Route Params =====
const placeId = Number(route.params.id)


// ===== Constant =====
const placeTypeMap = {
  CAFE: '☕ 咖啡廳',
  RESTAURANT: '🍽️ 餐廳',
  HOTEL: '🏨 寵物旅館',
  PARK: '🌳 公園',
  HOSPITAL: '🏥 動物醫院',
}

// ===== State =====
const images = ref([])
const tags = ref([])
const reviews = ref([])
const replies = ref({})
const reviewImages = ref({})
const reviewSummary = ref(null)

const selectedImage = ref(null)
const previewImage = ref(null)

const reviewSort = ref('latest')

const editingReviewId = ref(null)

const reviewImageUrl = ref('')

const newReview = ref({
  userId: authStore.id,
  placeId: route.params.id,
  ratingOverall: 5,
  ratingPetFriendly: 5,
  title: '',
  content: '',
})

const editForm = ref({
  title: '',
  content: '',
  ratingOverall: 5,
  ratingPetFriendly: 5,
})


const loadReviews = async () => {

  const response = await getReviewsByPlaceId(route.params.id, reviewSort.value)

  reviews.value = response.data.data

  for (const review of reviews.value) {
    try {
      const replyResponse = await getRepliesByReviewId(review.reviewId)

      replies.value[review.reviewId] = replyResponse.data.data
    } catch (e) {
      console.error('讀取商家回覆失敗', review.reviewId, e.response?.data)

      replies.value[review.reviewId] = []
    }

    try {
      const imageResponse = await getReviewImagesByReviewId(review.reviewId)

      reviewImages.value[review.reviewId] = imageResponse.data
    } catch (e) {
      console.error('取得商家回覆失敗', review.reviewId, e.response?.data)

      replies.value[review.reviewId] = []
    }
  }
}

onMounted(async () => {

  const imageResponse = await getPlaceImagesByPlaceId(placeId)

  images.value = imageResponse.data

  if (imageResponse.data.length > 0) {
    selectedImage.value = imageResponse.data[0].imageUrl
  }

  await placeStore.fetchPlaceById(placeId)

  await loadReviewSummary()

  const tagResponse = await getTagsByPlaceId(placeId)

  tags.value = tagResponse.data

  await loadReviews()
})



const submitReview = async () => {

  try {

    const result = await createReview({
      ...newReview.value,
      placeId,
    })

    const reviewId = result.data.reviewId

    if (reviewImageUrl.value.trim()) {
      await createReviewImage({
        reviewId,
        imageUrl: reviewImageUrl.value,
        sortOrder: 1,
      })
    }

    reviewImageUrl.value = ''

    await Swal.fire({
      icon: 'success',
      title: '評論新增成功',
      timer: 1200,
      showConfirmButton: false,
    })

    // 重新載入評論
    await loadReviews()

    await loadReviewSummary()

    // 清空表單
    newReview.value = {
      userId: authStore.id,
      placeId,
      ratingOverall: 5,
      ratingPetFriendly: 5,
      title: '',
      content: '',
    }
  } catch (error) {
    console.error('新增評論失敗:', error)

    await Swal.fire({
      icon: 'error',
      title: '新增失敗',
      text: error.response?.data?.errors?.[0] || error.response?.data?.message || '評論新增失敗',
    })
  }
}

const handleDeleteReview = async (reviewId) => {
  const result = await Swal.fire({
    title: '刪除評論？',
    text: '刪除後無法恢復',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '刪除',
    cancelButtonText: '取消',
  })

  if (!result.isConfirmed) return

  try {
    await deleteReview(reviewId)

    await Swal.fire({
      icon: 'success',
      title: '刪除成功',
      timer: 1200,
      showConfirmButton: false,
    })

    await loadReviews()

    await loadReviewSummary()
  } catch (error) {
    console.error(error)

    await Swal.fire({
      icon: 'error',
      title: '刪除失敗',
      text: error.message,
    })
  }
}
const handleReportReview = async (reviewId) => {
  // Step1 選擇檢舉原因

  const reasonResult = await Swal.fire({
    title: '檢舉評論',

    input: 'select',

    inputOptions: {
      SPAM: '垃圾訊息',

      FAKE: '不實內容',

      OFFENSIVE: '攻擊言論',

      OTHER: '其他',
    },

    inputPlaceholder: '請選擇檢舉原因',

    showCancelButton: true,

    confirmButtonText: '下一步',

    cancelButtonText: '取消',
  })

  if (!reasonResult.isConfirmed) {
    return
  }

  // Step2 補充說明

  const descriptionResult = await Swal.fire({
    title: '補充說明',

    input: 'textarea',

    inputPlaceholder: '請輸入詳細檢舉原因',

    inputAttributes: {
      rows: 5,
    },

    showCancelButton: true,

    confirmButtonText: '送出',

    cancelButtonText: '取消',
  })

  if (!descriptionResult.isConfirmed) {
    return
  }

  try {
    await createReport({
      reporterId: authStore.id,

      reviewId: reviewId,

      reason: reasonResult.value,

      description: descriptionResult.value || '',
    })

    await Swal.fire({
      icon: 'success',

      title: '檢舉成功',

      timer: 1200,

      showConfirmButton: false,
    })
  } catch (error) {
    console.error(error)

    await Swal.fire({
      icon: 'error',

      title: '檢舉失敗',
    })
  }
}

const handleAddToItinerary = async () => {
  try {
    const { data } = await getMyItineraries(authStore.id)

    const itineraries = data.data

    if (itineraries.length === 0) {
      await Swal.fire({
        icon: 'warning',
        title: '請先建立行程',
      })

      return
    }

    const options = Object.fromEntries(
      itineraries.map((itinerary) => [
        itinerary.itineraryId,
        itinerary.title,
      ]),
    )

    const itineraryResult = await Swal.fire({
      title: '選擇行程',

      input: 'select',

      inputOptions: options,

      showCancelButton: true,

      confirmButtonText: '下一步',

      cancelButtonText: '取消',
    })

    if (!itineraryResult.isConfirmed) {
      return
    }

    const dayResult = await Swal.fire({
      title: '第幾天',

      input: 'number',

      inputValue: 1,

      confirmButtonText: '下一步',
    })

    if (!dayResult.isConfirmed) {
      return
    }

    await addPlaceToItinerary(itineraryResult.value, {
      placeId,

      dayNo: Number(dayResult.value),

      transportTime: 0,
    })
    const successResult = await Swal.fire({
      icon: 'success',

      title: '加入行程成功',

      text: '是否立即查看行程？',

      showCancelButton: true,

      confirmButtonText: '查看行程',

      cancelButtonText: '繼續瀏覽景點',
    })

    if (successResult.isConfirmed) {
      router.push({
        name: 'PetMapItineraryDetail',
        params: {
          id: itineraryResult.value,
        },
      })
    }
  } catch (error) {
    console.error(error)

    await Swal.fire({
      icon: 'error',

      title: '加入失敗',
    })
  }
}

const startEditReview = (review) => {

  editingReviewId.value = review.reviewId

  editForm.value = {
    title: review.title,
    content: review.content,
    ratingOverall: review.ratingOverall,
    ratingPetFriendly: review.ratingPetFriendly,
  }
}

const saveReview = async () => {
  try {

    await updateReview(editingReviewId.value, editForm.value)

    await loadReviewSummary()

    const review = reviews.value.find((r) => r.reviewId === editingReviewId.value)

    if (review) {
      review.title = editForm.value.title
      review.content = editForm.value.content
      review.ratingOverall = editForm.value.ratingOverall
      review.ratingPetFriendly = editForm.value.ratingPetFriendly

    }

    editingReviewId.value = null

  } catch (error) {
    console.error(error)
  }
}

const loadReviewSummary = async () => {
  const summaryResponse = await getReviewSummary(route.params.id)

  reviewSummary.value = summaryResponse.data.data
}
</script>
<template>
  <UserNavbar />
  <div class="container mt-4" v-if="placeStore.currentPlace">
    <div class="place-header">
      <div class="title-row">
        <h1 class="place-title">
          {{ placeStore.currentPlace.name }}
        </h1>

        <FavoriteButton :user-id="authStore.id || 1" :place-id="placeStore.currentPlace.placeId" />
        <button class="btn btn-success ms-2" @click="handleAddToItinerary">➕ 加入行程</button>
      </div>

      <div v-if="images.length" class="image-gallery">
        <img :src="selectedImage" class="main-image" />

        <div class="thumbnail-list">
          <img v-for="image in images" :key="image.imageId" :src="image.imageUrl" class="thumbnail-image" :class="{
            active: selectedImage === image.imageUrl,
          }" @click="selectedImage = image.imageUrl" />
        </div>
      </div>

      <div class="mt-4">
        <span class="info-chip">
          {{ placeTypeMap[placeStore.currentPlace.placeType] }}
        </span>

        <span v-if="reviewSummary" class="info-chip">
          ⭐
          {{ reviewSummary.averageOverall.toFixed(1) }}
        </span>

        <span v-if="reviewSummary" class="info-chip">
          🐾
          {{ reviewSummary.averagePetFriendly.toFixed(1) }}
        </span>

        <span v-if="reviewSummary" class="info-chip">
          👥
          {{ reviewSummary.reviewCount }}
          人評分
        </span>
      </div>

      <div v-if="tags.length" class="tag-list">
        <span v-for="tag in tags" :key="tag.tagId" class="tag-badge">
          {{ tag.name }}
        </span>
      </div>

      <p class="address-text mt-4">
        📍
        {{ placeStore.currentPlace.address }}
      </p>
    </div>
    <!-- <hr> -->
    <div class="map-card">
      <h3 class="section-title">📍 地圖位置</h3>

      <PlaceMap v-if="
        placeStore.currentPlace &&
        placeStore.currentPlace.latitude &&
        placeStore.currentPlace.longitude
      " :latitude="Number(placeStore.currentPlace.latitude)" :longitude="Number(placeStore.currentPlace.longitude)"
        :place-name="placeStore.currentPlace.name" />
    </div>
    <!-- <hr> -->
    <div class="review-toolbar"></div>

    <div class="review-form">
      <h3>新增評論</h3>

      <input v-model="newReview.title" placeholder="評論標題" />

      <textarea v-model="newReview.content" placeholder="評論內容" />

      <!-- <input v-model="reviewImageUrl" placeholder="評論圖片網址(可選)" /> -->

      <label>整體評分</label>
      <select v-model="newReview.ratingOverall">
        <option :value="1">★</option>
        <option :value="2">★★</option>
        <option :value="3">★★★</option>
        <option :value="4">★★★★</option>
        <option :value="5">★★★★★</option>
      </select>

      <label>寵物友善評分</label>
      <select v-model="newReview.ratingPetFriendly">
        <option :value="1">★</option>
        <option :value="2">★★</option>
        <option :value="3">★★★</option>
        <option :value="4">★★★★</option>
        <option :value="5">★★★★★</option>
      </select>

      <button @click="submitReview" class="btn btn-primary submit-btn">送出評論</button>
    </div>

    <div class="review-header">
      <h3 class="section-title">💬 使用者評論</h3>

      <select v-model="reviewSort" @change="loadReviews" class="form-select">
        <option value="latest">最新評論</option>

        <option value="rating_desc">評分高到低</option>

        <option value="rating_asc">評分低到高</option>

        <option value="pet_desc">寵物友善高到低</option>

        <option value="pet_asc">寵物友善低到高</option>
      </select>
    </div>
    <div v-for="review in reviews" :key="review.reviewId" class="card review-card mb-3">
      <div class="card-body">
        <template v-if="editingReviewId === review.reviewId">
          <input v-model="editForm.title" class="form-control mb-2" placeholder="評論標題" />

          <textarea v-model="editForm.content" class="form-control mb-2" placeholder="評論內容"></textarea>

          <div class="mb-2">
            <label>整體評分：</label>

            <select v-model="editForm.ratingOverall">
              <option :value="1">★</option>
              <option :value="2">★★</option>
              <option :value="3">★★★</option>
              <option :value="4">★★★★</option>
              <option :value="5">★★★★★</option>
            </select>
          </div>

          <div class="mb-2">
            <label>寵物友善評分：</label>

            <select v-model="editForm.ratingPetFriendly">
              <option :value="1">★</option>
              <option :value="2">★★</option>
              <option :value="3">★★★</option>
              <option :value="4">★★★★</option>
              <option :value="5">★★★★★</option>
            </select>
          </div>

          <button @click="saveReview" class="btn btn-success me-2">儲存修改</button>

          <button @click="editingReviewId = null" class="btn btn-secondary">取消</button>
        </template>

        <template v-else>
          <h5>
            {{ review.title }}
          </h5>

          <div class="text-muted small mb-2">👤 {{ review.userName }}</div>

          <div class="mb-3">
            <span class="info-chip"> ⭐ {{ review.ratingOverall }} </span>

            <span class="info-chip"> 🐾 {{ review.ratingPetFriendly }} </span>
          </div>

          <p>
            {{ review.content }}
          </p>

          <div v-if="reviewImages[review.reviewId]?.length" class="review-image-list">
            <img v-for="image in reviewImages[review.reviewId]" :key="image.imageId" :src="image.imageUrl"
              class="review-image" @click="previewImage = image.imageUrl" />
          </div>

          <div class="review-actions">
            <template v-if="review.userId === authStore.id">
              <button @click="startEditReview(review)" class="btn btn-success me-2">
                編輯評論
              </button>

              <button @click="handleDeleteReview(review.reviewId)" class="btn btn-danger">
                刪除評論
              </button>
            </template>

            <template v-else>
              <button @click="handleReportReview(review.reviewId)" class="btn btn-outline-danger">
                檢舉評論
              </button>
            </template>
          </div>
        </template>

        <div v-for="reply in replies[review.reviewId]" :key="reply.replyId"
          class="border-start border-success ps-3 ms-4 mt-3">
          <strong>商家回覆</strong>

          <p>
            {{ reply.replyContent }}
          </p>
        </div>
      </div>
    </div>
  </div>
  <div v-if="previewImage" class="image-modal" @click="previewImage = null">
    <img :src="previewImage" class="modal-image" />
  </div>
</template>
<style scoped>
.place-header {
  background: white;

  border-radius: 20px;

  padding: 30px;

  margin-bottom: 30px;

  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.title-row {
  display: flex;

  justify-content: space-between;

  align-items: center;

  margin-bottom: 24px;
}

.main-image {
  width: 100%;

  height: 360px;

  object-fit: cover;

  border-radius: 20px;

  display: block;
}

.thumbnail-list {
  display: flex;

  gap: 12px;

  margin-top: 16px;

  overflow-x: auto;
}

.thumbnail-image {
  width: 140px;

  height: 90px;

  object-fit: cover;

  border-radius: 10px;

  cursor: pointer;

  flex-shrink: 0;

  transition: 0.2s;
}

.thumbnail-image:hover {
  transform: scale(1.05);
}

.info-chip {
  display: inline-block;

  padding: 8px 14px;

  margin-right: 10px;

  border-radius: 999px;

  background: #f3f4f6;

  font-weight: 600;
}

.tag-list {
  display: flex;

  flex-wrap: wrap;

  gap: 8px;

  margin-top: 20px;
}

.tag-badge {
  background: #e8f5e9;

  color: #2f855a;

  padding: 6px 12px;

  border-radius: 999px;

  font-weight: 600;
}

.address-text {
  font-size: 1.1rem;

  color: #666;

  margin-top: 20px;
}

.title-row :deep(button) {
  flex-shrink: 0;
}

.thumbnail-image.active {
  border: 3px solid #2f855a;
}

.review-image-list {
  display: flex;

  gap: 12px;

  margin-top: 12px;

  margin-bottom: 12px;
}

.review-image {
  width: 140px;
  height: 100px;
  object-fit: cover;

  border-radius: 10px;

  border: 1px solid #e5e7eb;

  cursor: pointer;
}

.image-modal {
  position: fixed;

  top: 0;

  left: 0;

  width: 100vw;

  height: 100vh;

  background: rgba(0, 0, 0, 0.8);

  display: flex;

  justify-content: center;

  align-items: center;

  z-index: 9999;
}

.modal-image {
  max-width: 90%;

  max-height: 90%;

  border-radius: 16px;
}

.review-form {
  background: white;

  padding: 24px;

  border-radius: 16px;

  margin-bottom: 24px;

  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);

  display: flex;

  flex-direction: column;

  gap: 12px;
}

.review-form input,
.review-form textarea {
  width: 100%;

  padding: 10px 12px;

  border: 1px solid #ddd;

  border-radius: 8px;
}

.review-form textarea {
  min-height: 120px;

  resize: vertical;
}

.review-card {
  border-radius: 16px;

  border: none;

  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.btn-primary {
  background: #2f855a;
  border: none;
}

.btn-primary:hover {
  background: #276749;
}

.btn-danger {
  background: #e53e3e;
  border: none;
}

.section-title {
  font-size: 1.8rem;

  font-weight: 700;

  margin-bottom: 20px;
}

.submit-btn {
  align-self: flex-end;

  min-width: 140px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.review-header .form-select {
  width: 180px;
}

.review-actions {
  display: flex;

  gap: 10px;

  margin-top: 12px;
}

.border-start {
  background: #f8fff8;

  border-left: 4px solid #2f855a !important;

  border-radius: 8px;

  padding: 12px;

  margin-top: 16px;
}

.review-image:hover {
  transform: scale(1.05);

  transition: 0.2s;
}

.map-card {
  background: white;

  border-radius: 16px;

  padding: 20px;

  margin-bottom: 24px;

  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.review-card p {
  margin-top: 12px;

  line-height: 1.8;

  color: #444;
}
</style>
