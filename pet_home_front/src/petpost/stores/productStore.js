// src/stores/productStore.js
// 商品推薦區固定資料版本。
// 重點：商品推薦頁目前只需要顯示 3 張商品卡片，右側另外顯示廣告輪播。

import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export const useProductStore = defineStore('product', () => {
  // 搜尋關鍵字
  const keyword = ref('')

  // 商品推薦區：固定只放 3 筆，避免右側廣告被商品卡片擠到或看起來像第 4 個商品。
  const products = ref([
    {
      productId: 1,
      productName: '皇家幼犬飼料',
      price: 899,
      category: '飼料',
      description: '適合幼犬日常營養補給。',
      imageUrl: 'https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243881/timeine5_dltxhj.jpg'
    },
    {
      productId: 2,
      productName: '天然狗罐頭',
      price: 499,
      category: '罐頭',
      description: '高嗜口性濕食推薦。',
      imageUrl: 'https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243881/timeine4_gybqck.jpg'
    },
    {
      productId: 3,
      productName: '貓咪主食罐',
      price: 299,
      category: '貓咪食品',
      description: '貓咪每日主食罐選擇。',
      // 修正 Cloudinary 正確檔名：原本舊網址會 404。
      imageUrl: 'https://res.cloudinary.com/dgyuw5gf1/image/upload/v1781243878/timeine3_kh0xu8.jpg'
    }
  ])

  // 前端即時搜尋：商品名稱、分類、說明、價格都可以搜尋。
  const filteredProducts = computed(() => {
    const key = keyword.value.trim().toLowerCase()

    if (!key) {
      return products.value
    }

    return products.value.filter((product) => {
      const searchableText = [
        product.productName,
        product.category,
        product.description,
        product.price
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()

      return searchableText.includes(key)
    })
  })

  // SearchBar submit 時會呼叫這個方法；因為 filteredProducts 是 computed，這裡保留方法即可。
  function searchProducts(value = '') {
    keyword.value = String(value).trim()
  }

  return {
    keyword,
    products,
    filteredProducts,
    searchProducts
  }
})
