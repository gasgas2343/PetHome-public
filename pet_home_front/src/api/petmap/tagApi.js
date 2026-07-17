import apiClient from "@/api/http";

export const getAllTags = () => {
  return apiClient.get("/map/tags");
};

export const getTagsByPlaceId = (placeId) => {
  return apiClient.get(`/map/places/${placeId}/tags`);
};
