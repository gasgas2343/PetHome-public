import apiClient from "@/api/http";

export const getReviewsByPlaceId = (placeId, sort = "latest") => {
  return apiClient.get(`/map/reviews/place/${placeId}`, {
    params: {
      sort,
    },
  });
};

export const createReview = (reviewData) => {
  return apiClient.post("/map/reviews", reviewData);
};

export const updateReview = (reviewId, reviewData) => {
  return apiClient.put(`/map/reviews/${reviewId}`, reviewData);
};

export const deleteReview = (reviewId) => {
  return apiClient.delete(`/map/reviews/${reviewId}`);
};

export const getReviewSummary = (placeId) => {
  return apiClient.get(`/map/reviews/place/${placeId}/summary`);
};
