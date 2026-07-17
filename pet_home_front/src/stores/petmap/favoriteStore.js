import { defineStore } from 'pinia'

import { getFavoritesByUserId } from '@/api/petmap/favoriteApi'

export const useFavoriteStore =
defineStore(

  'favorite',

  {

    state: () => ({

      favorites: []

    }),

    actions: {

      async fetchFavorites(
        userId
      ) {

        const response =
          await getFavoritesByUserId(
            userId
          )

        this.favorites = response.data.data
      }
    }
  }
)
