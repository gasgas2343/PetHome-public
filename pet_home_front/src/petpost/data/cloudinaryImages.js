// PawCare 固定雲端圖片清單
// 目前不走 Cloudinary 上傳 API，而是從已存在的 Cloudinary 公開網址中選一張。
// 好處：展示、開發、換環境時都不會受到 upload preset 或 signed/unsigned 設定影響。

export const FALLBACK_IMAGE =
  'https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781242144/matthewdminardi-shiba-inu-1592018_cbe5ql.jpg'

export const postImages = [
  {
    name: '文章圖片 1｜毛孩生活',
    url: 'https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243881/timeine5_dltxhj.jpg',
  },
  {
    name: '文章圖片 2｜貓咪特寫',
    url: 'https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243881/timeine4_gybqck.jpg',
  },
  {
    name: '文章圖片 3｜柴犬紀錄',
    url: 'https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243878/timeine3_kh0xu8.jpg',
  },
]

export const petImages = [
  {
    name: '獅子頭像',
    url: 'https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243881/timeine5_dltxhj.jpg',
  },
  {
    name: '貓咪頭像',
    url: 'https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243881/timeine4_gybqck.jpg',
  },
  {
    name: '柴犬頭像',
    url: 'https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243878/timeine3_kh0xu8.jpg',
  },
]

export const timelineImages = [
  {
    name: '回憶圖片 1｜毛孩日常',
    url: 'https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243881/timeine5_dltxhj.jpg',
  },
  {
    name: '回憶圖片 2｜陪伴時刻',
    url: 'https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243881/timeine4_gybqck.jpg',
  },
  {
    name: '回憶圖片 3｜成長紀錄',
    url: 'https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243878/timeine3_kh0xu8.jpg',
  },
]

export const cloudinaryImageMap = {
  post: postImages,
  pet: petImages,
  timeline: timelineImages,
  default: postImages,
}

export function getPresetImages(type = 'default') {
  return cloudinaryImageMap[type] || cloudinaryImageMap.default
}

export function getFallbackImage(type = 'default') {
  return getPresetImages(type)[0]?.url || FALLBACK_IMAGE
}


// 舊版元件/Store 會引用 FALLBACK_IMAGE_URL，保留別名避免 import 錯誤。
export const FALLBACK_IMAGE_URL = FALLBACK_IMAGE

// 取得指定類型的預設圖片網址。
// type: post | pet | timeline | default
export function getDefaultCloudinaryImage(type = 'default') {
  return getFallbackImage(type)
}

// 統一整理圖片網址：
// 1. 如果是有效 URL，直接回傳。
// 2. 如果是空字串、null、undefined，回傳該模組預設圖片。
// 3. 如果不是字串，回傳該模組預設圖片。
// 這樣可避免畫面因圖片欄位異常而爆掉。
export function normalizeCloudinaryImageUrl(url, type = 'default') {
  if (typeof url !== 'string') {
    return getDefaultCloudinaryImage(type)
  }

  const trimmedUrl = url.trim()

  if (!trimmedUrl) {
    return getDefaultCloudinaryImage(type)
  }

  return trimmedUrl
}
