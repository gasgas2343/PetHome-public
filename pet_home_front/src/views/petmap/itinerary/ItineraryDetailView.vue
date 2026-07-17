<script setup>
import { ref, computed,nextTick } from "vue";

import { onMounted } from "vue";

import { useRoute } from "vue-router";

import {
  getItineraryDetail,
  removePlaceFromItinerary,
} from "@/api/petmap/itineraryApi";

import Swal from "sweetalert2";

import ItineraryMap from "@/components/petmap/ItineraryMap.vue";

import UserNavbar from "@/components/petmap/UserNavbar.vue";

import Sortable from "sortablejs";

import { reorderPlaces } from "@/api/petmap/itineraryApi";

const route = useRoute();

const itinerary = ref(null);

const dayRefs = ref({});

const loadDetail = async () => {
  const response = await getItineraryDetail(route.params.id);

  itinerary.value = response.data.data;
};

const initSortable = async () => {
  await nextTick();

  Object.values(dayRefs.value).forEach((container) => {

    if (!container) {
      return;
    }

    if (container._sortable) {
      container._sortable.destroy();
    }

    container._sortable = Sortable.create(container, {
      animation: 150,

      handle: ".place-card",

      ghostClass: "sortable-ghost",

      chosenClass: "sortable-chosen",

      onEnd: async (evt) => {

  const dayNo = Number(
    container.previousElementSibling.textContent
      .replace("Day", "")
      .trim()
  );

  const dayPlaces = itinerary.value.places
    .filter(place => place.dayNo === dayNo)
    .sort((a, b) => a.sequenceNo - b.sequenceNo);

  const moved = dayPlaces.splice(evt.oldIndex, 1)[0];

  dayPlaces.splice(evt.newIndex, 0, moved);

  dayPlaces.forEach((place, index) => {
    place.sequenceNo = index + 1;
  });

  const request = dayPlaces.map(place => ({
    placeId: place.placeId,
    dayNo: place.dayNo,
    sequenceNo: place.sequenceNo,
  }));

  try {

    await reorderPlaces(
      route.params.id,
      request,
    );

  } catch (error) {

    console.error(error);

    await Swal.fire({
      icon: "error",
      title: "排序失敗",
    });

    await loadDetail();
  }

},
    });
  });
};

const handleRemovePlace = async (placeId) => {
  const result = await Swal.fire({
    title: "移除景點？",

    text: "確定從行程中移除",

    icon: "warning",

    showCancelButton: true,

    confirmButtonText: "移除",

    cancelButtonText: "取消",
  });

  if (!result.isConfirmed) {
    return;
  }

  try {
    await removePlaceFromItinerary(
      route.params.id,

      placeId,
    );

    await Swal.fire({
      icon: "success",

      title: "移除成功",

      timer: 1200,

      showConfirmButton: false,
    });

    await loadDetail();
  } catch (error) {
    console.error(error);

    await Swal.fire({
      icon: "error",

      title: "移除失敗",
    });
  }
};

const groupedPlaces = computed(() => {
  if (!itinerary.value || !itinerary.value.places) {
    return {};
  }

  const groups = {};

  itinerary.value.places.forEach((place) => {
    if (!groups[place.dayNo]) {
      groups[place.dayNo] = [];
    }

    groups[place.dayNo].push(place);
  });

  return groups;
});

const dayCount = computed(() => {
  return Object.keys(groupedPlaces.value).length;
});

onMounted(async () => {

  await loadDetail();

  await initSortable();

});
</script>
<template>
  <UserNavbar />
  <div v-if="itinerary" class="container mt-4">
    <h1>
      {{ itinerary.title }}
    </h1>
    <div class="alert alert-info">
      天數：
      {{ dayCount }}

      | 景點數：
      {{ itinerary.places?.length ?? 0 }}
    </div>
    <ItineraryMap :places="itinerary.places" />

    <div
  v-for="dayNo in Object.keys(groupedPlaces).sort((a, b) => a - b)"
  :key="dayNo"
>
  <h2 class="mb-3">
    Day {{ dayNo }}
  </h2>

  <div :ref="el => dayRefs[dayNo] = el">

    <div
      v-for="place in groupedPlaces[dayNo]"
      :key="place.placeId"
      class="card mb-3 place-card"
      :data-place-id="place.placeId"
      @click="$router.push({
        name: 'PetMapPlaceDetail',
        params: {
          id: place.placeId,
        },
      })"
    >
      <div class="card-body">

        <h5 class="fw-bold text-success mb-0">
          {{ place.sequenceNo }}.
          {{ place.name }}
        </h5>

        <button
          class="btn btn-danger btn-sm mt-2"
          @click.stop="handleRemovePlace(place.placeId)"
        >
          移除景點
        </button>

      </div>
    </div>

  </div>
</div>
  </div>
</template>
<style scoped>
.place-link {
  font-size: 1.1rem;

  font-weight: 600;

  text-decoration: none;

  color: #2f855a;
}

.place-link:hover {
  text-decoration: underline;
}

.place-card {
  cursor: grab;
}

.place-card:active {
  cursor: grabbing;
}

.sortable-ghost {
  opacity: 0.5;
}

.sortable-chosen {
  transform: scale(1.02);
}
</style>
