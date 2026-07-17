<script setup>
// import { ref } from "vue";

// import { onMounted } from "vue";

// import { getAllTags } from "@/api/petmap/tagApi";

// import { getPlaceTypes, searchPlaces } from "@/api/petmap/placeApi";

// import UserNavbar from "@/components/petmap/UserNavbar.vue";

// const tags = ref([]);

// const placeTypes = ref([]);

// const selectedPlaceType = ref("");

// const selectedTagIds = ref([]);

// const recommendations = ref([]);

// const placeTypeMap = {
//   CAFE: "咖啡廳",

//   RESTAURANT: "餐廳",

//   HOTEL: "寵物旅館",

//   PARK: "公園",

//   HOSPITAL: "動物醫院",
// };

// const recommend = async () => {
//   const response = await searchPlaces({
//     placeType: selectedPlaceType.value,

//     tagIds: selectedTagIds.value,
//   });

//   recommendations.value = response.data;
// };

// onMounted(async () => {
//   const tagResponse = await getAllTags();

//   tags.value = tagResponse.data;

//   const typeResponse = await getPlaceTypes();

//   placeTypes.value = typeResponse.data;
// });
</script>

<template>
    <!-- <UserNavbar />
    <div class="container mt-4">
      <h1>AI 景點推薦</h1>
      <div class="card p-4">
        <div class="mb-3">
          <label>地點類型</label>

          <select v-model="selectedPlaceType" class="form-select">
            <option value="">不限</option>

            <option v-for="type in placeTypes" :key="type" :value="type">
              {{ placeTypeMap[type] }}
            </option>
          </select>
        </div>

        <div class="mb-3">
          <label>特色標籤</label>

          <div v-for="tag in tags" :key="tag.tagId">
            <input type="checkbox" :value="tag.tagId" v-model="selectedTagIds" />

            {{ tag.name }}
          </div>
          <button class="btn btn-success" @click="recommend">開始推薦</button>
          <div v-if="recommendations.length" class="mt-4">
            <h3>推薦結果</h3>

            <div
              v-for="place in recommendations"
              :key="place.placeId"
              class="card mb-3"
            >
              <div class="card-body">
                <h5>
                  {{ place.name }}
                </h5>

                <p>
                  {{ place.address }}
                </p>

                <RouterLink
                  :to="`/map/places/${place.placeId}`"
                  class="btn btn-primary"
                >
                  查看景點
                </RouterLink>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div> -->
</template>
