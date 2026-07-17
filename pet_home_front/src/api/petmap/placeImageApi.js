import apiClient from "@/api/http";

export const getPlaceImagesByPlaceId = (placeId) => {
  return apiClient.get(`/map/place-images/place/${placeId}`);
};

export const createPlaceImage = (data) => {
  return apiClient.post("/map/place-images", data);
};

export const deletePlaceImage = (imageId) => {
  return apiClient.delete(`/map/place-images/${imageId}`);
};
