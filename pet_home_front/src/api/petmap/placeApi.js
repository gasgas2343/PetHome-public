// 🎯 關鍵修正：往上一層 (..) 回到 src/api/，引入別人的 http.js 封裝
import apiClient from "../http";

export const getAllPlaces = () => {
  // 🎯 將原本的 axios 改成 apiClient，這樣才會自動帶入 ${import.meta.env.VITE_API_BASE_URL} 的基礎路徑
  return apiClient.get("/map/places");
};

export const getPlaceById = (id) => {
  return apiClient.get(`/map/places/${id}`);
};

export const searchPlaces = (params) => {
  return apiClient.get("/map/places/search", {
    params,
    paramsSerializer: {
      indexes: null,
    },
  });
};

export const searchPlaceByName = (keyword) => {
  return apiClient.get("/map/places/search-name", {
    params: {
      keyword,
    },
  });
};

export const getTagsByPlaceId = (placeId) => {
  return apiClient.get(`/map/places/${placeId}/tags`);
};

export const getPlaceTypes = () => {
  return apiClient.get("/map/places/types");
};

export const updatePlace = (id, data) => {
  return apiClient.put(`/map/places/${id}`, data);
};

export const deletePlace = (id) => {
    return apiClient.delete(`/map/places/${id}`);
};
