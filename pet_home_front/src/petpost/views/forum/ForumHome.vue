<script setup>
import { ref, onMounted } from "vue";
import ForumCategoryTabs from "@/petpost/components/forum/ForumCategoryTabs.vue";
import ForumImageCard from "@/petpost/components/forum/ForumImageCard.vue";
import { getForumCategories, getForumPosts } from "@/petpost/api/forumApi";
import ForumRuleDialog from "@/petpost/components/forum/ForumRuleDialog.vue";

const categories = ref([]);
const posts = ref([]);
const keyword = ref("");
const activeCategoryId = ref(null);
const showRuleDialog = ref(false);

// 首頁輪播先固定寫死；未來要串後端廣告 API 時，只要改成 axios 取得即可
const banners = ref([
  {
    title: "毛孩回憶，記錄每個成長時刻",
    imageUrl:
      "https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243881/timeine5_dltxhj.jpg",
  },
  {
    title: "分享飼養心得，讓新手不孤單",
    imageUrl:
      "https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243881/timeine4_gybqck.jpg",
  },
]);

async function loadCategories() {
  try {
    const res = await getForumCategories();
    categories.value = res.data || [];
  } catch (error) {
    console.error("取得分類失敗", error);
  }
}

async function loadPosts() {
  try {
    const res = await getForumPosts({
      keyword: keyword.value,
      categoryId: activeCategoryId.value,
    });
    posts.value = res.data || [];
  } catch (error) {
    console.error("取得文章失敗", error);
  }
}

function changeCategory(categoryId) {
  activeCategoryId.value = categoryId;
  loadPosts();
}

onMounted(() => {
  loadCategories();
  loadPosts();
});
</script>

<template>
  <main class="forum-home">
    <section class="hero-section">
      <div class="hero-text">
        <p class="eyebrow">Pet Forum</p>
        <h1>毛孩回憶、成長時刻、生活紀錄</h1>
        <p>分享日常、健康、飼養心得與每一段值得收藏的陪伴。</p>

        <div class="search-box">
          <input
            v-model="keyword"
            type="text"
            placeholder="搜尋文章標題、內容或 Tag"
            @keyup.enter="loadPosts"
          />
          <button @click="loadPosts">
            <i class="bi bi-search"></i>
          </button>
        </div>
        <div class="quick-actions">
          <button type="button" @click="loadPosts">熱門文章</button>

          <button type="button" @click="showRuleDialog = true">論壇規則</button>

          <button type="button">防詐騙資訊</button>
        </div>
      </div>

      <div class="hero-banner">
        <img :src="banners[0].imageUrl" :alt="banners[0].title" />
      </div>
    </section>

    <ForumCategoryTabs
      :categories="categories"
      :active-category-id="activeCategoryId"
      @change="changeCategory"
    />

    <section class="post-grid">
      <ForumImageCard
        v-for="post in posts"
        :key="post.postId || post.post_id"
        :post="post"
      />
    </section>
    <ForumRuleDialog v-if="showRuleDialog" @close="showRuleDialog = false" />
  </main>
</template>

<style scoped>
.forum-home {
  max-width: 1180px;
  margin: 0 auto;
  padding: 28px 20px 60px;
}

.hero-section {
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  gap: 28px;
  align-items: center;
  margin-bottom: 28px;
}

.hero-text {
  background: #fff7ed;
  border-radius: 28px;
  padding: 42px;
}

.eyebrow {
  color: #f97316;
  font-weight: 800;
  letter-spacing: 0.08em;
}

.hero-text h1 {
  font-size: 40px;
  line-height: 1.25;
  margin: 10px 0 14px;
}

.hero-text p {
  color: #6b7280;
}

.search-box {
  display: flex;
  gap: 8px;
  margin-top: 24px;
  background: #fff;
  padding: 8px;
  border-radius: 999px;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.08);
}

.search-box input {
  flex: 1;
  border: 0;
  outline: 0;
  padding: 10px 16px;
  background: transparent;
}

.search-box button {
  border: 0;
  width: 44px;
  height: 44px;
  border-radius: 999px;
  background: #f97316;
  color: #fff;
}

.hero-banner img {
  width: 100%;
  height: 360px;
  object-fit: cover;
  border-radius: 28px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.12);
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 22px;
  margin-top: 24px;
}

.quick-actions {
  display: flex;
  gap: 10px;
  margin-top: 18px;
  flex-wrap: wrap;
}

.quick-actions button {
  border: 1px solid #eadfce;
  background: #fffdf8;
  color: #5f4b3b;
  border-radius: 999px;
  padding: 8px 16px;
  font-weight: 700;
  cursor: pointer;
}

.quick-actions button:hover {
  background: #f6efe4;
}

@media (max-width: 900px) {
  .hero-section,
  .post-grid {
    grid-template-columns: 1fr;
  }
}
</style>
