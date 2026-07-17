import apiClient from "@/api/http";

export const createReport = (data) => {
  return apiClient.post("/map/reports", data);
};

export const getMyReports = (reporterId) => {
  return apiClient.get(`/map/reports/reporter/${reporterId}`);
};

export const getAllReports = () => {
  return apiClient.get("/map/reports");
};

export const approveReport = (reportId, adminNote) => {
  return apiClient.put(`/map/reports/${reportId}/approve`, {
    adminNote,
  });
};

export const rejectReport = (reportId, adminNote) => {
  return apiClient.put(`/map/reports/${reportId}/reject`, {
    adminNote,
  });
};

export const getReportStatistics = () => {
  return apiClient.get("/map/reports/statistics");
};
