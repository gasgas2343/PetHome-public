import apiClient from "@/api/http";

export const getAllTags = () => {
  return apiClient.get("/map/tags");
};

export const createPlaceTag = (data) => {
  return apiClient.post("/map/place-tags", data);
};

export const deletePlaceTag = (placeId, tagId) => {
  return apiClient.delete(`/map/place-tags/${placeId}/${tagId}`);
};
