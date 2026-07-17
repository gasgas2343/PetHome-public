<!-- 點擊後開啟檢舉視窗
對應 reportStore.openDialog() -->

<script setup>

// 引入 Report Store
import { useReportStore } from "@/petpost/stores/reportStore";

// 建立 Store
const reportStore = useReportStore();


// 開啟檢舉視窗
function openReportModal() {

    reportStore.openDialog();

}

</script>


<template>

<!-- 檢舉按鈕 -->
<button

    class="btn btn-outline-danger"

    @click="openReportModal"

>

    🚨 檢舉

</button>

</template>


<style scoped>

</style>