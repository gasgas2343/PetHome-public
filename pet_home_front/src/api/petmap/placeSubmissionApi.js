import apiClient from "@/api/http";

export const getSubmissionsByUserId = (userId) => {
  return apiClient.get(`/map/place-submissions/user/${userId}`);
};

export const getSubmissionDetail = (submissionId) => {
  return apiClient.get(`/map/place-submissions/${submissionId}/detail`);
};

export const createSubmission = (data) => {
  return apiClient.post("/map/place-submissions", data);
};

export const updateSubmission = (submissionId, data) => {
  return apiClient.put(`/map/place-submissions/${submissionId}`, data);
};

export const resubmitSubmission = (submissionId) => {
  return apiClient.put(`/map/place-submissions/${submissionId}/resubmit`);
};

export const getSubmissionById = (submissionId) => {
  return apiClient.get(`/map/place-submissions/${submissionId}`);
};

export const getSubmissionStatistics = () => {
  return apiClient.get("/map/place-submissions/statistics");
};

// Admin

export const getAllSubmissions = () => {
  return apiClient.get("/map/place-submissions");
};

export const approveSubmission = (submissionId) => {
  return apiClient.put(`/map/place-submissions/${submissionId}/approve`);
};

export const rejectSubmission = (submissionId, adminNote) => {
  return apiClient.put(`/map/place-submissions/${submissionId}/reject`, {
    adminNote,
  });
};

export const requestRevision = (submissionId, adminNote) => {
  return apiClient.put(`/map/place-submissions/${submissionId}/revision`, {
    adminNote,
  });
};
