import axios from "axios";

// =====================================================
// Cloudinary 圖片上傳設定
// =====================================================
// 注意：
// 1. Vite 前端環境變數必須使用 VITE_ 開頭。
// 2. 修改 .env / .env.development / .env.production 後，必須重啟 npm run dev。
// 3. 目前 Cloudinary 後台已存在的 Unsigned preset 是 pet_forum。
// =====================================================

const CLOUD_NAME = import.meta.env.VITE_CLOUDINARY_CLOUD_NAME || "dgyuw5gf1";

const PRESET_MAP = {
  // 論壇文章圖片
  post:
    import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET_POST ||
    import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET ||
    "pet_forum",

  // 寵物資料圖片
  pet:
    import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET_PET ||
    import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET ||
    "pet_forum",

  // 回憶紀錄 / 時間軸圖片
  timeline:
    import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET_TIMELINE ||
    import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET ||
    "pet_forum",

  // 預設值，避免舊程式沒有傳 type 時壞掉
  default: import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET || "pet_forum",
};

function resolveUploadPreset(type = "default") {
  return PRESET_MAP[type] || PRESET_MAP.default;
}

/**
 * 上傳單張圖片到 Cloudinary。
 *
 * @param {File} file 使用者選取的圖片檔案
 * @param {'post'|'pet'|'timeline'|'default'} type 圖片用途，用來決定使用哪個 upload preset
 * @returns {Promise<string>} Cloudinary secure_url，前端會把這個 URL 存進後端資料庫
 */
export async function uploadImage(file, type = "default") {
  if (!file) return "";

  const uploadPreset = resolveUploadPreset(type);

  const formData = new FormData();
  formData.append("file", file);
  formData.append("upload_preset", uploadPreset);

  try {
    const response = await axios.post(
      `https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`,
      formData,
    );

    return response.data.secure_url;
  } catch (error) {
    const cloudinaryMessage = error.response?.data?.error?.message;

    console.error("Cloudinary 圖片上傳失敗", {
      cloudName: CLOUD_NAME,
      uploadPreset,
      type,
      cloudinaryMessage,
      response: error.response?.data,
    });

    throw new Error(
      cloudinaryMessage ||
        `圖片上傳失敗：請確認 Cloudinary cloudName=${CLOUD_NAME}、upload_preset=${uploadPreset}，且 preset 已開啟 Unsigned。`,
    );
  }
}
