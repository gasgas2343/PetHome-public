<script setup>
import { ref, watch, nextTick, onBeforeUnmount } from "vue";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

const props = defineProps({
  latitude: Number,
  longitude: Number,
  placeName: String,
});

const mapRef = ref(null);

let map = null;
let marker = null;

const createMap = async () => {
  await nextTick();

  if (!mapRef.value) return;

  map = L.map(mapRef.value);

  L.tileLayer(
    "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
    {
      attribution: "&copy; OpenStreetMap",
    }
  ).addTo(map);

  updateMarker();

  requestAnimationFrame(() => {
    map.invalidateSize();
  });
};

const updateMarker = () => {
  if (!map) return;

  const lat = Number(props.latitude);
  const lng = Number(props.longitude);

  if (Number.isNaN(lat) || Number.isNaN(lng)) return;

  if (marker) {
    map.removeLayer(marker);
  }

 marker = L.marker([lat, lng])
  .addTo(map)
  .bindPopup(props.placeName);

map.setView([lat, lng], 16);

// 等地圖完成定位後再開啟 Popup
setTimeout(() => {
  marker.openPopup();
}, 100);
};

watch(
  () => [props.latitude, props.longitude],
  async ([lat, lng]) => {
    if (!lat || !lng) return;

    if (!map) {
      await createMap();
    } else {
      updateMarker();

      requestAnimationFrame(() => {
        map.invalidateSize();
      });
    }
  },
  {
    immediate: true,
  }
);

onBeforeUnmount(() => {
  if (map) {
    map.remove();
    map = null;
    marker = null;
  }
});
</script>

<template>
  <div
    ref="mapRef"
    class="detail-map"
  ></div>
</template>

<style scoped>
.detail-map {
  width: 100%;
  height: 350px;
  border-radius: 20px;
  overflow: hidden;
}
</style>
