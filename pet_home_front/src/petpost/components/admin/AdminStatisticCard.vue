<!-- 顯示：

總文章數
總會員數
待處理檢舉
待處理申訴 -->

<script setup>
// 接收父元件傳進來的資料
defineProps({
  // 卡片標題
  title: {
    type: String,

    required: true,
  },

  // 數值
  count: {
    type: Number,

    required: true,
  },
});
</script>

<template>
  <div class="col-md-3">
    <div class="card shadow">
      <div class="card-body text-center">
        <!-- 標題 -->
        <h5>
          {{ title }}
        </h5>

        <!-- 數量 -->
        <h2 class="text-primary">
          {{ count }}
        </h2>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card {
  min-height: 120px;
}
</style>
