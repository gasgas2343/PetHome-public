// 舊檔案相容用：請優先使用 stores/favoriteStore.js。
// 這裡轉接到目前正確的後端 API，避免舊元件誤 import 時打錯路徑。
export { getFavorited, addFavorite, cancelFavorite } from "@/petpost/api/favorite";
