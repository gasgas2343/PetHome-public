<script setup>
import { ref, computed } from 'vue'
import { onMounted } from 'vue'

import { getReviewsByPlaceId } from '@/api/petmap/reviewApi'

import {
  getRepliesByReviewId,
  createReply,
  updateReply,
  deleteReply,
} from '@/api/petmap/merchantReviewReplyApi'

import Swal from 'sweetalert2'

import MerchantNavbar from '@/components/petmap/MerchantNavbar.vue'

const reviews = ref([])

const replies = ref({})

const loadReviews = async () => {
  try {
    const response = await getReviewsByPlaceId(46, 'latest')

    reviews.value = response.data.data || []

    for (const review of reviews.value) {
      const replyResponse = await getRepliesByReviewId(review.reviewId)

      replies.value[review.reviewId] = replyResponse.data.data || []
    }

  } catch (error) {
    console.error(error)
  }
}

const handleCreateReply = async (reviewId) => {
  const result = await Swal.fire({
    title: '新增商家回覆',

    input: 'textarea',

    inputLabel: '回覆內容',

    inputPlaceholder: '請輸入回覆內容',

    showCancelButton: true,

    confirmButtonText: '送出',

    cancelButtonText: '取消',
  })

  if (!result.isConfirmed) {
    return
  }

  try {
    await createReply({
      reviewId: reviewId,

      merchantId: 1,

      replyContent: result.value,
    })

    await Swal.fire({
      icon: 'success',

      title: '新增成功',

      timer: 1200,

      showConfirmButton: false,
    })

    await loadReviews()
  } catch (error) {
    console.error(error)

    await Swal.fire({
      icon: 'error',

      title: '新增失敗',

      text: error.response?.data?.errors?.[0] || error.response?.data?.message || '新增失敗',
    })
  }
}

const handleUpdateReply = async (reviewId) => {
  const reply = replies.value[reviewId][0]

  const result = await Swal.fire({
    title: '修改商家回覆',

    input: 'textarea',

    inputValue: reply.replyContent,

    showCancelButton: true,

    confirmButtonText: '儲存',

    cancelButtonText: '取消',
  })

  if (!result.isConfirmed) {
    return
  }

  try {
    await updateReply(
      reply.replyId,

      {
        replyContent: result.value,
      },
    )

    await Swal.fire({
      icon: 'success',

      title: '修改成功',

      timer: 1200,

      showConfirmButton: false,
    })

    await loadReviews()
  } catch (error) {
    console.error(error)

    await Swal.fire({
      icon: 'error',

      title: '修改失敗',
    })
  }
}

const handleDeleteReply = async (reviewId) => {
  const reply = replies.value[reviewId][0]

  const result = await Swal.fire({
    title: '刪除回覆？',

    text: '刪除後無法恢復',

    icon: 'warning',

    showCancelButton: true,

    confirmButtonText: '刪除',

    cancelButtonText: '取消',
  })

  if (!result.isConfirmed) {
    return
  }

  try {
    await deleteReply(reply.replyId)

    await Swal.fire({
      icon: 'success',

      title: '刪除成功',

      timer: 1200,

      showConfirmButton: false,
    })

    await loadReviews()
  } catch (error) {
    console.error(error)

    await Swal.fire({
      icon: 'error',

      title: '刪除失敗',
    })
  }
}

const totalReviews = computed(() => reviews.value.length)

const repliedReviews = computed(
  () => reviews.value.filter((review) => replies.value[review.reviewId]?.length).length,
)

const pendingReviews = computed(() => totalReviews.value - repliedReviews.value)

const formatDateTime = (dateString) => {
  return new Date(dateString).toLocaleString('zh-TW')
}

onMounted(async () => {
  await loadReviews()
})
</script>

<template>
  <MerchantNavbar />
  <div class="container mt-4">
    <h1>商家評論管理</h1>

    <div class="row mb-4">
      <div class="col-md-4">
        <div class="card text-center">
          <div class="card-body">
            <h6>總評論數</h6>
            <h2>{{ totalReviews }}</h2>
          </div>
        </div>
      </div>

      <div class="col-md-4">
        <div class="card text-center">
          <div class="card-body">
            <h6>已回覆</h6>
            <h2>{{ repliedReviews }}</h2>
          </div>
        </div>
      </div>

      <div class="col-md-4">
        <div class="card text-center">
          <div class="card-body">
            <h6>待回覆</h6>
            <h2>{{ pendingReviews }}</h2>
          </div>
        </div>
      </div>
    </div>

    <div v-for="review in reviews" :key="review.reviewId" class="card mb-4 shadow-sm">
      <div class="card-body">
        <h5>
          {{ review.title }}
        </h5>
        <small class="text-muted">
          {{ review.createdAt }}
        </small>

        <p>
          {{ review.content }}
        </p>

        <span> ⭐ {{ review.ratingOverall }} </span>

        <span class="ms-3"> 🐾 {{ review.ratingPetFriendly }} </span>
      </div>

      <div v-if="replies[review.reviewId]?.length" class="mt-3 p-3 border-start border-success">
        <span class="badge bg-success mb-2"> 已回覆 </span>

        <br />

        <strong> 商家回覆 </strong>

        <p class="mb-0">
          {{ replies[review.reviewId][0].replyContent }}
        </p>
        <small class="text-muted">
          {{ formatDateTime(replies[review.reviewId][0].updatedAt) }}
        </small>

        <div class="mt-2">
          <button class="btn btn-warning" @click="handleUpdateReply(review.reviewId)">
            修改回覆
          </button>

          <button class="btn btn-danger ms-2" @click="handleDeleteReply(review.reviewId)">
            刪除回覆
          </button>
        </div>
      </div>
      <div v-else class="mt-3">
        <span class="badge bg-danger"> 待回覆 </span>

        <div class="mt-2">
          <button class="btn btn-success" @click="handleCreateReply(review.reviewId)">
            新增回覆
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
