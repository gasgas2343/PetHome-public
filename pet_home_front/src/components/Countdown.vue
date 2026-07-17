<template>
  <div class="countdown">
    距離結束：<span>{{ hours }}時 {{ minutes }}分 {{ seconds }}秒</span>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
const hours = ref(0), minutes = ref(30), seconds = ref(0)

onMounted(() => {
  setInterval(() => {
    if (seconds.value > 0) seconds.value--
    else { seconds.value = 59; if (minutes.value > 0) minutes.value-- }
  }, 1000)
})
</script>
<style scoped>
.countdown { color: #d84315; font-size: 0.9rem; font-weight: bold; margin-left: 15px; }
</style>