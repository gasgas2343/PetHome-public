<script setup>
import { onMounted, ref, watch } from "vue";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import "leaflet-routing-machine";
import "leaflet-routing-machine/dist/leaflet-routing-machine.css";

const props = defineProps({
  places: {
    type: Array,
    required: true,
  },
});

const map = ref(null);

const DAY_COLORS = [
  "#2563eb",
  "#dc2626",
  "#16a34a",
  "#ea580c",
  "#9333ea",
];

/* ------------------------- 建立地圖 ------------------------- */

const createMap = () => {
  const firstPlace = props.places[0];

  map.value = L.map("itinerary-map").setView(
    [
      Number(firstPlace.latitude),
      Number(firstPlace.longitude),
    ],
    13,
  );

  L.tileLayer(
    "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
    {
      attribution: "&copy; OpenStreetMap",
    },
  ).addTo(map.value);
};

/* ------------------------- 圖例 ------------------------- */

const createLegend = () => {
  const legend = L.control({
    position: "topright",
  });

  legend.onAdd = () => {
    const div = L.DomUtil.create(
      "div",
      "day-legend",
    );

    div.innerHTML = DAY_COLORS.map(
      (color, index) => `
      <div>
        <span
          class="legend-color"
          style="background:${color}"
        ></span>
        Day ${index + 1}
      </div>
    `,
    ).join("");

    return div;
  };

  legend.addTo(map.value);
};

/* ------------------------- Marker Icon ------------------------- */

const createMarkerIcon = (
  dayNo,
  sequenceNo,
) => {
  return L.divIcon({
    className: "",

    html: `
      <div
        class="marker-number"
        style="background:${
          DAY_COLORS[(dayNo - 1) % DAY_COLORS.length]
        }"
      >
        D${dayNo}-${sequenceNo}
      </div>
    `,

    iconSize: [40, 40],

    iconAnchor: [20, 20],
  });
};

/* ------------------------- Popup ------------------------- */

const createPopup = (place) => `
  Day ${place.dayNo}<br>
  第 ${place.sequenceNo} 站<br>
  ${place.name}
`;

/* ------------------------- Marker ------------------------- */

const createMarkers = () => {
  const bounds = [];

  const dayRoutes = {};

  const sortedPlaces = [...props.places].sort(
    (a, b) =>
      a.dayNo - b.dayNo ||
      a.sequenceNo - b.sequenceNo,
  );

  sortedPlaces.forEach((place) => {
    const lat = Number(place.latitude);

    const lng = Number(place.longitude);

    const marker = L.marker(
      [lat, lng],
      {
        icon: createMarkerIcon(
          place.dayNo,
          place.sequenceNo,
        ),
      },
    )
      .addTo(map.value)
      .bindPopup(createPopup(place));

    marker.on("click", () => {
      window.location.href =
        `/petmap/places/${place.placeId}`;
    });

    bounds.push([lat, lng]);

    if (!dayRoutes[place.dayNo]) {
      dayRoutes[place.dayNo] = [];
    }

    dayRoutes[place.dayNo].push([
      lat,
      lng,
    ]);
  });

  return {
    bounds,
    dayRoutes,
  };
};

/* ------------------------- 路線 ------------------------- */

const createRoutes = (dayRoutes) => {
  Object.entries(dayRoutes).forEach(
    ([dayNo, points]) => {
      if (points.length < 2) {
        return;
      }

      L.Routing.control({
        waypoints: points.map((p) =>
          L.latLng(p[0], p[1]),
        ),

        routeWhileDragging: false,

        addWaypoints: false,

        draggableWaypoints: false,

        fitSelectedRoutes: false,

        collapsible: true,

        show: false,

        createMarker: () => null,

        lineOptions: {
          styles: [
            {
              color:
                DAY_COLORS[
                  (dayNo - 1) %
                    DAY_COLORS.length
                ],

              weight: 7,

              opacity: 0.85,
            },
          ],
        },
      }).addTo(map.value);
    },
  );
};

/* ------------------------- 主流程 ------------------------- */

const drawMap = () => {
  if (
    !props.places ||
    props.places.length === 0
  ) {
    return;
  }

  if (map.value) {
    map.value.remove();
  }

  createMap();

  createLegend();

  const {
    bounds,
    dayRoutes,
  } = createMarkers();

  createRoutes(dayRoutes);

  map.value.fitBounds(bounds);
};

/* ------------------------- Life Cycle ------------------------- */

onMounted(drawMap);

watch(
  () => props.places,
  drawMap,
  {
    deep: true,
  },
);
</script>

<template>
  <div id="itinerary-map"></div>
</template>

<style scoped>
#itinerary-map {
  width: 100%;

  height: 450px;

  border-radius: 20px;

  margin-bottom: 20px;
}
:deep(.marker-number) {
  width: 30px;
  height: 30px;

  border-radius: 50%;

  color: white;

  display: flex;
  align-items: center;
  justify-content: center;

  font-weight: bold;

  border: 2px solid white;

  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
}

:deep(.day-legend) {
  background: white;
  padding: 10px 12px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, .2);
  font-size: 13px;
  line-height: 24px;
}

:deep(.legend-color) {
  display: inline-block;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  margin-right: 8px;
  vertical-align: middle;
}
</style>
