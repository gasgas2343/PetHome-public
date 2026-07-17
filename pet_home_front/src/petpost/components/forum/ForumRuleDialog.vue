<script setup>
// defineEmits 用來向父元件發送事件
// 這裡定義一個 close 事件
const emit = defineEmits(["close"]);

// 關閉視窗的方法
function closeDialog() {
  // 通知父元件：「我要關閉了」
  emit("close");
}
// 中文註解：目前選中的熱門標籤
const selectedTag = ref('')

</script>

<template>
  <!-- 外層遮罩 -->
  <!-- @click.self 表示只有點到灰色背景時才關閉 -->
  <div class="rule-mask" @click.self="closeDialog">
    <!-- 規則視窗 -->
    <div class="rule-dialog">
      <!-- 標題 -->
      <h3>論壇規則</h3>

      <ul>
        <li>① 禁止人身攻擊及不雅內容。</li>
        <li>② 禁止散布不實資訊。</li>
        <li>③ 禁止廣告洗版及重複發文。</li>
        <li>④ 禁止張貼違法內容。</li>
        <li>⑤ 請尊重其他會員。</li>
      </ul>

      <!-- 按鈕區 -->
      <div class="button-area">
        <!-- 按取消 -->
        <button class="cancel-btn" @click="closeDialog">取消</button>

        <!-- 按我知道了 -->
        <button class="confirm-btn" @click="closeDialog">我知道了</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ===== 灰色背景遮罩 ===== */
.rule-mask {
  /* 固定在整個視窗 */
  position: fixed;

  /* 上下左右都貼齊 */
  inset: 0;

  /* 半透明背景 */
  background: rgba(0, 0, 0, 0.35);

  /* 讓視窗永遠在最上層 */
  z-index: 9999;

  /* 使用 Flex 將彈窗置中 */
  display: flex;
  justify-content: center;
  align-items: center;
}

/* ===== 白色彈窗 ===== */
.rule-dialog {
  /* 寬度 */
  width: 450px;

  /* 背景顏色 */
  background: #fffaf4;

  /* 圓角 */
  border-radius: 24px;

  /* 內距 */
  padding: 30px;

  /* 陰影效果 */
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

/* ===== 規則內容 ===== */
.rule-dialog ul {
  margin-top: 20px;

  line-height: 2;
}

/* ===== 按鈕區 ===== */
.button-area {
  margin-top: 30px;

  display: flex;

  justify-content: flex-end;

  gap: 15px;
}

/* ===== 取消按鈕 ===== */
.cancel-btn {
  border: none;

  background: #e8ddcf;

  color: #666;

  padding: 12px 25px;

  border-radius: 20px;

  cursor: pointer;
}

/* ===== 我知道了按鈕 ===== */
.confirm-btn {
  border: none;

  background: #d7985a;

  color: white;

  padding: 12px 25px;

  border-radius: 20px;

  cursor: pointer;
}

/* ===== 滑鼠移上去效果 ===== */
.cancel-btn:hover {
  background: #d8cbb9;
}

.confirm-btn:hover {
  background: #c98644;
}
</style>
