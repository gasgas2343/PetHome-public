<script setup>
defineProps({
  theme: {
    type: String,
    default: 'login'
  },
  imageSrc: {
    type: String,
    required: true
  },
  eyebrow: {
    type: String,
    default: '毛起來 Maokilai'
  },
  titleLines: {
    type: Array,
    required: true
  }
})
</script>

<template>
  <main class="auth-page" :class="`auth-page--${theme}`">
    <section class="auth-shell">
      <aside class="auth-visual">
        <img :src="imageSrc" alt="毛起來會員頁面圖片" />

        <div class="auth-copy">
          <p class="auth-eyebrow">
            {{ eyebrow }}
          </p>

          <h1>
            <span
              v-for="line in titleLines"
              :key="line"
            >
              {{ line }}
            </span>
          </h1>
        </div>
      </aside>

      <section class="auth-panel">
        <slot />
      </section>
    </section>
  </main>
</template>

<style scoped lang="scss">
.auth-page {
  --theme-main: #5d8fa1;
  --theme-soft: rgba(93, 143, 161, 0.12);
  --theme-deep: #385f6d;
  --header-height: 121px;

  min-height: calc(100dvh - var(--header-height));
  box-sizing: border-box;
  display: grid;
  place-items: center;
  padding: 24px;
  overflow: hidden;

  background:
    linear-gradient(
      145deg,
      #fffaf3 0%,
      #f8f1e8 42%,
      #f3e9dc 100%
    );
}

.auth-page--register {
  --theme-main: #d49a35;
  --theme-soft: rgba(212, 154, 53, 0.12);
  --theme-deep: #8b6426;
}

.auth-shell {
  width: min(1040px, 100%);
  height: min(620px, calc(100dvh - var(--header-height) - 48px));
  min-height: 560px;
  display: grid;
  grid-template-columns: 1.08fr 0.92fr;
  overflow: hidden;
  border-radius: 34px;
  background: rgba(255, 252, 246, 0.96);
  border: 1px solid rgba(139, 100, 63, 0.12);
  box-shadow: 0 22px 60px rgba(72, 52, 34, 0.11);
}

.auth-visual {
  position: relative;
  overflow: hidden;
  background: #efe6da;

  img {
    width: 100%;
    height: 100%;
    display: block;
    object-fit: cover;
  }

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background:
      linear-gradient(
        90deg,
        rgba(41, 32, 24, 0.42) 0%,
        rgba(41, 32, 24, 0.2) 38%,
        rgba(41, 32, 24, 0.04) 72%
      );
    pointer-events: none;
  }
}

.auth-copy {
  position: absolute;
  left: 44px;
  bottom: 346px;
  z-index: 1;
  color: #fffaf3;
}

.auth-eyebrow {
  margin: 0 0 18px;
  font-size: 0.92rem;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.auth-copy h1 {
  margin: 0;
  display: grid;
  gap: 8px;
  font-size: clamp(2rem, 4vw, 3.2rem);
  line-height: 1.18;
  font-weight: 900;
  letter-spacing: 0.06em;

  span {
    display: block;
  }
}

.auth-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: rgba(247, 240, 226, 0.98);
}

.auth-panel :deep(.auth-form-card) {
  width: min(360px, 100%);
}

@media (max-width: 900px) {
  .auth-page {
    min-height: calc(100dvh - var(--header-height));
    padding: 18px;
    overflow: auto;
  }

  .auth-shell {
    height: auto;
    min-height: 0;
    grid-template-columns: 1fr;
  }

  .auth-visual {
    height: 280px;
  }

  .auth-copy {
    left: 28px;
    bottom: 28px;
  }

  .auth-panel {
    padding: 36px 24px;
  }
}

@media (max-width: 520px) {
  .auth-page {
    padding: 12px;
  }

  .auth-shell {
    border-radius: 24px;
  }

  .auth-visual {
    height: 230px;
  }

  .auth-copy h1 {
    font-size: 2rem;
  }

  .auth-panel {
    padding: 30px 20px;
  }
}
</style>