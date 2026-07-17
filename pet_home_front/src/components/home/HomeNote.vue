<script setup>

</script>

<template>
  <section class="daily-note">
    <div class="note-deco note-deco-left">🐾</div>
    <div class="note-deco note-deco-right">☀️</div>

    <div class="note-icon">
      <span>💡</span>
    </div>

    <div class="note-content">
      <div class="note-label">
        毛孩今日小叮嚀
      </div>

      <p>
        天氣炎熱時，記得幫毛孩準備足夠飲水，<br />
        外出散步也要避開正午高溫時段。
      </p>
    </div>
  </section>
</template>

<style scoped>
.daily-note {
  position: relative;
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 28px 36px;
  margin: 40px auto;
  max-width: 1100px;
  border-radius: 30px;
  background:
    radial-gradient(circle at 8% 20%, rgba(255, 181, 108, 0.18), transparent 28%),
    radial-gradient(circle at 92% 80%, rgba(159, 184, 201, 0.25), transparent 30%),
    linear-gradient(135deg, #fffaf2 0%, #ffffff 58%, #f3f8fb 100%);
  box-shadow:
    0 18px 40px rgba(92, 67, 44, 0.08),
    inset 0 0 0 2px rgba(255, 255, 255, 0.8);
  overflow: hidden;
}

.daily-note::before {
  content: "";
  position: absolute;
  left: 28px;
  right: 28px;
  top: 12px;
  height: 3px;
  border-radius: 999px;
  background: repeating-linear-gradient(
    90deg,
    #f6a15f 0 10px,
    transparent 10px 18px
  );
  opacity: 0.55;
}

.daily-note::after {
  content: "";
  position: absolute;
  right: -35px;
  bottom: -45px;
  width: 150px;
  height: 150px;
  border-radius: 46% 54% 50% 50%;
  background: rgba(159, 184, 201, 0.22);
  transform: rotate(-18deg);
}

.note-icon {
  position: relative;
  z-index: 2;
  flex: 0 0 72px;
  width: 72px;
  height: 72px;
  border-radius: 24px;
  background: #f6a15f;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 22px rgba(246, 161, 95, 0.28);
  transform: rotate(-4deg);
}

.note-icon span {
  font-size: 34px;
  transform: rotate(4deg);
}

.note-content {
  position: relative;
  z-index: 2;
}

.note-label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  padding: 6px 14px;
  border-radius: 999px;
  background: rgba(246, 161, 95, 0.15);
  color: #f07d2f;
  font-size: 15px;
  font-weight: 800;
  letter-spacing: 0.04em;
}

.note-label::before {
  content: "";
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #f6a15f;
}

.note-content p {
  margin: 0;
  color: #354052;
  font-size: 18px;
  line-height: 1.9;
  letter-spacing: 0.04em;
}

.note-deco {
  position: absolute;
  z-index: 1;
  font-size: 34px;
  opacity: 0.16;
  pointer-events: none;
}

.note-deco-left {
  left: 18px;
  bottom: 14px;
  transform: rotate(-18deg);
}

.note-deco-right {
  right: 34px;
  top: 22px;
  transform: rotate(12deg);
}

@media (max-width: 768px) {
  .daily-note {
    align-items: flex-start;
    gap: 16px;
    padding: 26px 22px;
    border-radius: 26px;
  }

  .note-icon {
    width: 58px;
    height: 58px;
    flex-basis: 58px;
    border-radius: 20px;
  }

  .note-icon span {
    font-size: 28px;
  }

  .note-content p {
    font-size: 16px;
    line-height: 1.8;
  }
}
</style>