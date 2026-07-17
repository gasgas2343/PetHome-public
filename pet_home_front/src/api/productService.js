import axios from 'axios';

// 設定 API 的基礎路徑 (假設你的後端在 http://localhost:8080)
const apiClient = axios.create({
  baseURL: `${import.meta.env.VITE_API_BASE_URL}`, 
  headers: {
    'Content-Type': 'application/json',
  },
});

// 封裝取得產品的函式
export const getProducts = (category) => {
  return apiClient.get(`/products?category=${category}`);
};