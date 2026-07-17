<script setup>
import { onMounted, ref,computed } from "vue";
import { usePlaceStore } from "@/stores/petmap/placeStore";
import PlaceCard from "@/components/petmap/PlaceCard.vue";
import LeafletMap from "@/components/petmap/LeafletMap.vue";
import { watchDebounced } from "@vueuse/core";
import { getAllTags } from "@/api/petmap/tagApi";
import { searchPlaces } from "@/api/petmap/placeApi";
import { getPlaceTypes } from "@/api/petmap/placeApi";

import UserNavbar from "@/components/petmap/UserNavbar.vue";

const mapRef = ref(null);
const selectedPlaceId = ref(null);
const keyword = ref("");
const placeStore = usePlaceStore();
const tags = ref([]);
const selectedTagIds = ref([]);
const selectedPlaceType = ref("");
const showMarkers = ref(true);
const placeTypes = ref([]);
const resultCount = computed(() => placeStore.places.length);

const placeTypeMap = {
  CAFE: "☕ 咖啡廳",
  RESTAURANT: "🍽️ 餐廳",
  HOTEL: "🏨 寵物旅館",
  PARK: "🌳 公園",
  HOSPITAL: "🏥 動物醫院",
};

const unifiedSearch = async () => {
  const response = await searchPlaces({

    keyword: keyword.value || undefined,
    tagIds: selectedTagIds.value || undefined,
    placeType: selectedPlaceType.value || undefined,
  });

  placeStore.places = response.data.data;
};

const handleMarkerClick = (placeId) => {
  selectedPlaceId.value = placeId;

  const element = document.getElementById(`place-${placeId}`);

  if (element) {
    element.scrollIntoView({
      behavior: "smooth",
      block: "center",
    });
  }

  setTimeout(() => {
    if (selectedPlaceId.value === placeId) {
      selectedPlaceId.value = null;
    }
  }, 2000);
};

const handleCardClick = (placeId) => {
  selectedPlaceId.value = placeId;

  mapRef.value?.focusPlace(placeId);

  const element = document.getElementById(`place-${placeId}`);

  if (element) {
    element.scrollIntoView({
      behavior: "smooth",
      block: "center",
    });
  }
};

const removeTag = (tagId) => {
  selectedTagIds.value = selectedTagIds.value.filter(
    id => id !== tagId
  );
};

const clearAllFilters = () => {
  keyword.value = "";
  selectedPlaceType.value = "";
  selectedTagIds.value = [];
};


watchDebounced(
  [keyword, selectedTagIds, selectedPlaceType],
  async () => {
    const noCondition =
      !keyword.value &&
      selectedTagIds.value.length === 0 &&
      !selectedPlaceType.value;

    if (noCondition) {
      await placeStore.fetchPlaces();
      return;
    }

    await unifiedSearch();
  },
  {
    debounce: 400,
    deep: true,
  }
);

onMounted(async () => {
  await placeStore.fetchPlaces();

  const response = await getAllTags();
  tags.value = response.data.data;

  const placeTypeResponse = await getPlaceTypes();
  placeTypes.value = placeTypeResponse.data.data;
});


</script>

<template>
  <UserNavbar />
  <div class="container mt-4">
    <!-- Hero -->
    <h2 class="page-title">🐾 探索寵物友善地點</h2>

    <LeafletMap
  ref="mapRef"
  :places="placeStore.places"
  :show-markers="showMarkers"
  @marker-click="handleMarkerClick"
/>
<div class="d-flex justify-content-between align-items-center my-3">

  <div>

    <span
      v-if="selectedPlaceType"
      class="filter-chip"
    >
      {{ placeTypeMap[selectedPlaceType] }}
    </span>

    <span
      v-for="tagId in selectedTagIds"
      :key="tagId"
      class="filter-chip"
    >
      {{ tags.find(t => t.tagId === tagId)?.name }}

      <span
        class="chip-close"
        @click="removeTag(tagId)"
      >
        ×
      </span>

    </span>

    <button
      v-if="
        keyword ||
        selectedPlaceType ||
        selectedTagIds.length
      "
      class="btn btn-sm btn-outline-danger ms-2"
      @click="clearAllFilters"
    >
      清除全部
    </button>

  </div>

  <span class="text-muted">
    共找到
    <strong>{{ resultCount }}</strong>
    個地點
  </span>

</div>


    <div class="row mb-4">
      <!-- 類型 -->
      <div class="col-md-3">
        <select v-model="selectedPlaceType" class="form-select">
          <option value="">全部類型</option>
          <option v-for="type in placeTypes" :key="type" :value="type">
            <!-- 🎯 修正點 2：增加防呆，避免後端回傳字典以外的代碼時噴錯 -->
            {{ placeTypeMap[type] || `🐾 其他 (${type})` }}
          </option>
        </select>
      </div>

      <!-- 多選 Tag -->
      <div class="col-md-3">
  <div class="border rounded bg-white p-2 tag-filter">

    <div class="row">

      <div
        v-for="tag in tags"
        :key="tag.tagId"
        class="col-6"
      >
        <div class="form-check">

          <input
            class="form-check-input"
            type="checkbox"
            :id="`tag-${tag.tagId}`"
            :value="tag.tagId"
            v-model="selectedTagIds"
          />

          <label
            class="form-check-label small"
            :for="`tag-${tag.tagId}`"
          >
            {{ tag.name }}
          </label>

        </div>
      </div>

    </div>

  </div>

</div>


      <!-- 關鍵字 -->
      <div class="col-md-6">
        <input
          v-model="keyword"
          class="form-control"
          placeholder="搜尋地點名稱"
        />
      </div>
    </div>

    <!-- 地點卡片 -->
<div v-if="placeStore.places.length > 0">
  <PlaceCard
  v-for="place in placeStore.places"
  :key="place.placeId"
  :id="`place-${place.placeId}`"
  :place="place"
  :class="{
    active: selectedPlaceId === place.placeId,
  }"
  @card-click="handleCardClick"
/>
</div>

<div
  v-else
  class="text-center py-5"
>
  <h5>找不到符合條件的地點</h5>

  <p class="text-muted">
    請嘗試調整搜尋條件或取消部分篩選。
  </p>
</div>

  </div>
</template>

<style scoped>
.page-title {
  text-align: center;
  font-weight: 700;
  color: #6d4c41;
  margin-bottom: 30px;
}

.tag-filter {

  max-height: 220px;

  overflow-y: auto;

  overflow-x: hidden;

}

.form-check {

  margin-bottom: 6px;

}

.form-check-label {

  cursor: pointer;

}

.tag-filter {
  max-height: 220px;
  overflow-y: auto;
  overflow-x: hidden;
}

.form-check {
  margin-bottom: 6px;
}

.form-check-label {
  cursor: pointer;
}

.filter-chip {

  display: inline-flex;

  align-items: center;

  gap: 6px;

  background: #e8f5e9;

  color: #2f855a;

  border: 1px solid #c8e6c9;

  border-radius: 999px;

  padding: 6px 12px;

  margin-right: 8px;

  margin-bottom: 8px;

  font-size: .9rem;

  font-weight: 600;

}

.chip-close {

  cursor: pointer;

  font-weight: bold;

  transition: .2s;

}

.chip-close:hover {

  color: #d32f2f;

}
</style>
