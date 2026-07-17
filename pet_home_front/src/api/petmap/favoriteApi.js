import apiClient from "../http";

export const getFavoritesByUserId = (userId) => {
  return apiClient.get(`/map/favorites/${userId}`);
};

export const createFavorite = (favoriteData) => {
  return apiClient.post("/map/favorites", favoriteData);
};

export const deleteFavorite = (userId, placeId) => {
  return apiClient.delete(`/map/favorites/${userId}/${placeId}`);
};

export const checkFavorite = (userId, placeId) => {
  return apiClient.get(`/map/favorites/check/${userId}/${placeId}`);
};
