import { defineStore } from 'pinia'

import { searchPlaces, getPlaceById } from '@/api/petmap/placeApi'

export const usePlaceStore = defineStore(
  'place',

  {
    state: () => ({
      places: [],

      currentPlace: null,
    }),

    actions: {
      async fetchPlaces() {
        const response = await searchPlaces({})

        this.places = response.data.data
      },

      async fetchPlaceById(id) {
        const response = await getPlaceById(id)

        this.currentPlace = response.data.data
      },
    },
  },
)
