import apiClient from "@/api/http";

export const getRepliesByReviewId = (reviewId) => {
  return apiClient.get(`/map/merchant-review-replies/review/${reviewId}`);
};

export const createReply = (data) => {
  return apiClient.post("/map/merchant-review-replies", data);
};

export const updateReply = (replyId, data) => {
  return apiClient.put(`/map/merchant-review-replies/${replyId}`, data);
};

export const deleteReply = (replyId) => {
  return apiClient.delete(`/map/merchant-review-replies/${replyId}`);
};
