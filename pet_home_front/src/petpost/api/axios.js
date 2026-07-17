// 中文註解：petpost 模組相容用 axios 轉接檔。
// 若舊檔案仍寫 import api from './axios'，也會導到全專案共用 axios。
// 正式建議：petpost/api 內統一改成 import api from './http'
export { default } from '@/api/http'
