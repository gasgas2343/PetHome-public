import apiClient from "@/api/http";

export const getReviewImagesByReviewId = (reviewId) => {
  return apiClient.get(`/map/review-images/review/${reviewId}`);
};

export const createReviewImage = (data) => {
  return apiClient.post("/map/review-images", data);
};

export const deleteReviewImage = (imageId) => {
  return apiClient.delete(`/map/review-images/${imageId}`);
};
