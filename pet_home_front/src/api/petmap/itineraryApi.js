import apiClient from "@/api/http";

export const getMyItineraries = (userId) => {
  return apiClient.get(`/map/itineraries/user/${userId}`);
};

export const createItinerary = (data) => {
  return apiClient.post("/map/itineraries", data);
};

export const deleteItinerary = (itineraryId) => {
  return apiClient.delete(`/map/itineraries/${itineraryId}`);
};

export const updateItinerary = (itineraryId, data) => {
  return apiClient.put(`/map/itineraries/${itineraryId}`, data);
};

export const getItineraryDetail = (itineraryId) => {
  return apiClient.get(`/map/itineraries/${itineraryId}`);
};

export const addPlaceToItinerary = (itineraryId, data) => {
  return apiClient.post(`/map/itineraries/${itineraryId}/places`, data);
};

export const removePlaceFromItinerary = (itineraryId, placeId) => {
  return apiClient.delete(`/map/itineraries/${itineraryId}/places/${placeId}`);
};

export const reorderPlaces = (itineraryId, data) => {
  return apiClient.put(
    `/map/itineraries/${itineraryId}/reorder`,
    data,
  );
};
