import { defineStore } from "pinia";

import { uploadImage } from "@/petpost/api/upload";

const USE_FIXED_CLOUDINARY_IMAGES =
  import.meta.env.VITE_USE_FIXED_CLOUDINARY_IMAGES === "true";

export const useUploadStore = defineStore("upload", () => {
  /**
   * 處理圖片來源。
   *
   * 固定雲端圖片模式：
   * - ImageUploader 會傳入 Cloudinary URL 字串
   * - 這裡直接回傳 URL，不呼叫 Cloudinary upload API
   *
   * 本機檔案模式：
   * - 如果之後把 allowLocalFile 打開，才會呼叫 uploadImage(file, type)
   */
  async function uploadImages(filesOrUrls, type = "default") {
    const imageUrls = [];

    for (const item of filesOrUrls || []) {
      if (!item) continue;

      // 已經是雲端網址，直接使用
      if (typeof item === "string") {
        imageUrls.push(item);
        continue;
      }

      // 固定圖片模式下，不允許 File 上傳，避免 Cloudinary 400
      if (USE_FIXED_CLOUDINARY_IMAGES) {
        console.warn("目前使用固定雲端圖片模式，已略過本機檔案上傳。");
        continue;
      }

      const url = await uploadImage(item, type);
      if (url) imageUrls.push(url);
    }

    return imageUrls;
  }

  return {
    uploadImages,
  };
});
