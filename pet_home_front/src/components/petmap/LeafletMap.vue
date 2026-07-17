<script setup>
import { onMounted, watch,nextTick } from "vue";
import L from "leaflet";

import "leaflet/dist/leaflet.css";
import "leaflet.markercluster";
import "leaflet.markercluster/dist/MarkerCluster.css";
import "leaflet.markercluster/dist/MarkerCluster.Default.css";


const markerMap = new Map();


const props = defineProps({
  places: {
    type: Array,
    default: () => [],
  },
  showMarkers: {
    type: Boolean,
    default: true,
  },
});

const emit = defineEmits([
  "marker-click",
]);

let map = null;
let markerCluster = null;

const placeTypeMap = {
  CAFE: "☕ 咖啡廳",
  RESTAURANT: "🍽️ 餐廳",
  HOTEL: "🏨 寵物旅館",
  PARK: "🌳 公園",
  HOSPITAL: "🏥 動物醫院",
};

const focusPlace = (placeId) => {
  const marker = markerMap.get(placeId);

  if (!marker) return;

  map.flyTo(marker.getLatLng(), 18, {
    animate: true,
    duration: 1,
  });
};

onMounted(() => {
  map = L.map("map").setView([24.1477, 120.6736], 13);

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "&copy; OpenStreetMap",
  }).addTo(map);

  markerCluster = L.markerClusterGroup();
  map.addLayer(markerCluster);

  // 🎯 修正：在初始化完成後，手動觸發第一次的標記渲染
  updateMarkers();
});

// 封裝成一個獨立的函式，方便重複呼叫
  const updateMarkers = async () => {
  // 🎯 修正 1：安全檢查，如果 Leaflet 還沒準備好，直接跳出避免報錯
  if (!map || !markerCluster) return;

  await nextTick();

  setTimeout(() => {
    if (map) map.invalidateSize();
  }, 100);

  const places = props.places;

  markerMap.clear();

  // 先清空舊有的地標群組
  markerCluster.clearLayers();

  if (!props.showMarkers || !places || places.length === 0) {
    return;
  }

  const bounds = [];

  places.forEach((place) => {
   if (place.latitude && place.longitude && Number(place.latitude) !== 0 && Number(place.longitude) !== 0) {
      const lat = Number(place.latitude);
      const lng = Number(place.longitude);
      bounds.push([lat, lng]);


     const marker = L.marker([lat, lng])
  .bindTooltip(
    `
    <strong>${place.name}</strong><br>
    ${placeTypeMap[place.placeType] ?? place.placeType}
    `,
    {
      direction: "top",
      offset: [0, -15],
    }
  )
  .bindPopup(`
<div style="min-width:220px">

  <h6>${place.name}</h6>

  <div style="color:#666;margin-bottom:6px;">
    ${placeTypeMap[place.placeType] ?? place.placeType}
  </div>

  <div>
    ⭐ ${place.ratingAvg ?? "-"}
    （${place.reviewCount ?? 0}）
  </div>

  <div
    style="
      font-size:13px;
      color:#777;
      margin:10px 0;
    "
  >
    ${place.address}
  </div>

  <button
    class="btn btn-success btn-sm w-100"
    id="card-btn-${place.placeId}"
  >
    查看卡片
  </button>

</div>
`);

markerMap.set(place.placeId, marker);

markerCluster.addLayer(marker);

marker.on("popupopen", () => {
  const btn = document.getElementById(
    `card-btn-${place.placeId}`
  );

  if (btn) {
    btn.onclick = () => {
      emit("marker-click", place.placeId);
    };
  }
});
    }
  });

  if (bounds.length === 1) {
    map.flyTo(bounds[0], 18, { animate: true, duration: 1.5 });
  } else if (bounds.length > 1) {
    map.fitBounds(bounds, { padding: [50, 50] });
  }
};

watch(
  [() => props.places, () => props.showMarkers],
  () => {
    updateMarkers();
  },
  {
    // 🎯 修正 3：拿掉 immediate: true，交給 onMounted 負責第一次
  }
);
defineExpose({
  focusPlace,
});
</script>

<template>
  <div id="map" class="mb-4"></div>
</template>

<style scoped>
#map {
  width: 100%;
  height: 500px;
  border-radius: 16px;
}
</style>
