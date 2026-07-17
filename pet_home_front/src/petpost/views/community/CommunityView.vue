<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { HOT_TAGS } from '@/petpost/constants/forumConstants'
import { usePostStore } from '@/petpost/stores/postStore'
import CommunityHeroBanner from '@/petpost/components/community/CommunityHeroBanner.vue'
import SearchBar from '@/petpost/components/common/SearchBar.vue'
import PostList from '@/petpost/components/post/PostList.vue'
import ForumRuleDialog from '@/petpost/components/community/ForumRuleDialog.vue'
import '@/petpost/assets/petpost-theme.scss'

// 中文註解：控制論壇規則彈窗是否顯示。
const showRuleDialog = ref(false)

const postStore = usePostStore()

// 中文註解：搜尋框關鍵字，用來搜尋文章標題與內容。
const keyword = ref('')

// 中文註解：目前選中的熱門標籤。空字串代表不篩選標籤。
const selectedTag = ref('')

// 中文註解：首頁熱門標籤清單。
// 這三個標籤要和資料庫 forum_tags 預設資料一致。
const hotTags = HOT_TAGS

// 中文註解：頁面載入時取得文章。
onMounted(async () => {
  await postStore.loadHotPosts()
  await postStore.loadPosts()
})

// 中文註解：點擊熱門標籤時，只設定 selectedTag，不要把標籤文字塞進 keyword。
function selectHotTag(tag) {
  selectedTag.value = tag
}

// 中文註解：清除熱門標籤篩選，顯示全部文章。
function clearHotTag() {
  selectedTag.value = ''
}

// 中文註解：搜尋框搜尋文章標題與內容。
function searchPosts(value) {
  keyword.value = value
}

const filteredPosts = computed(() => {
// 中文註解：同時支援「搜尋關鍵字」和「熱門標籤」。
  const text = keyword.value.trim().toLowerCase()
  return postStore.posts.filter((post) => {
    const title = post.title ?? ''
    const content = post.content ?? ''

    // 中文註解：postStore 已經會把 tags 轉成字串陣列，這裡再做防呆。
    const tags = Array.isArray(post.tags) ? post.tags : []

    // 中文註解：如果有選熱門標籤，就檢查文章 tags 是否包含該標籤。
    const matchTag = selectedTag.value ? tags.includes(selectedTag.value) : true

    // 中文註解：如果搜尋框有輸入，就搜尋標題與內容。
    const matchKeyword = text
    ? title.toLowerCase().includes(text) || content.toLowerCase().includes(text)
    : true

    return matchTag && matchKeyword
  })
})

const sortedFilteredPosts = computed(() => {
  return [...filteredPosts.value].sort((a, b) => {
    return new Date(b.createdAt) - new Date(a.createdAt)
  })
})

</script>

<template>
  <main class="community-page">
    <section class="community-shell">
      <CommunityHeroBanner />
      <RouterLink to="/community" class="back-home-link"> ← 返回論壇首頁 </RouterLink>
      <SearchBar v-model="keyword" placeholder="搜尋文章標題或內容..." @search="searchPosts" />

      <ForumRuleDialog v-if="showRuleDialog" @close="showRuleDialog = false" />

      <button
        type="button"
        class="btn btn-outline-secondary mt-3 me-2"
        @click="showRuleDialog = true"
      >
        論壇規則
      </button>

      <div class="mt-4">
        <h4>熱門標籤</h4>

        <button
          class="btn btn-outline-secondary me-2 mb-2"
          :class="{ active: selectedTag === '' }"
          @click="clearHotTag"
        >
          全部文章
        </button>

        <button
          v-for="tag in hotTags"
          :key="tag"
          class="btn btn-outline-primary me-2 mb-2"
          :class="{ active: selectedTag === tag }"
          @click="selectHotTag(tag)"
        >
          # {{ tag }}
        </button>
      </div>
      <!-- 中文註解：熱門文章區，資料來自 GET /api/posts/hot -->
      <div v-if="selectedTag" class="alert alert-info mt-3 mb-0">
        目前篩選標籤：#{{ selectedTag }}
      </div>

      <RouterLink to="/post/create">
        <button class="btn btn-success mt-3">我要發文</button>
      </RouterLink>

      <!-- 中文註解：熱門文章區，資料來自 GET /api/posts/hot -->
      <section class="mb-4 mt-4">
        <h4> 熱門文章</h4>
        <PostList v-if="postStore.hotPosts.length" :posts="postStore.hotPosts" variant="grid" />
        <div v-else class="text-secondary">目前沒有熱門文章</div>
      </section>

      <hr />

      <!-- 中文註解：全部文章區，支援搜尋與熱門標籤篩選 -->
      <section class="mb-4 mt-4">
        <h4> 全部文章</h4>
        <PostList v-if="sortedFilteredPosts.length" :posts="sortedFilteredPosts" variant="list" />
        <div v-else class="text-secondary">找不到符合條件的文章</div>
      </section>

      <hr />
    </section>
  </main>
</template>

<style scoped>
.community-page {
  background: transparent;
}

.community-shell {
  width: min(1120px, 100%);
  margin: 0 auto;
}

.community-shell :deep(.pc-search) {
  margin-bottom: 18px;
}

.community-shell h4 {
  margin: 0 0 14px;
  color: #3f332b;
  font-size: 22px;
  font-weight: 900;
  letter-spacing: 0.04em;
}

.community-shell hr {
  border: 0;
  border-top: 1px solid rgba(111, 98, 88, 0.12);
  margin-top: 28px;
}

.back-home-link {
  display: inline-flex;
  align-items: center;
  margin-bottom: 20px;
  color: #8a5b28;
  text-decoration: none;
  font-size: 15px;
  font-weight: 900;
  transition: 0.2s;
}

.back-home-link:hover {
  color: #6f6258;
  text-decoration: underline;
}
</style>
