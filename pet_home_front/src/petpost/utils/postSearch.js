// src/utils/postSearch.js
// 文章搜尋工具：支援「標題 / 內容 / 分類 / Tag / 作者」任一欄位包含部分文字即可搜尋到。

function normalizeKeyword(value) {
  return String(value ?? '')
    .trim()
    .toLowerCase()
    // 移除多餘空白，避免「狗 狗」或複製貼上空格造成搜尋失敗
    .replace(/\s+/g, '')
}

function normalizeTags(tags) {
  if (!tags) return ''

  if (Array.isArray(tags)) {
    return tags
      .map((tag) => {
        if (typeof tag === 'string') return tag
        return tag.tagName ?? tag.name ?? tag.title ?? ''
      })
      .join('')
  }

  return String(tags)
}

export function matchPostKeyword(post, keyword) {
  const q = normalizeKeyword(keyword)

  // 沒輸入關鍵字時，顯示全部文章
  if (!q) return true

  const searchableText = [
    post?.title,
    post?.content,
    post?.summary,
    post?.categoryName,
    post?.category?.categoryName,
    post?.userName,
    post?.username,
    normalizeTags(post?.tags),
    normalizeTags(post?.tagNames)
  ]
    .map(normalizeKeyword)
    .join('')

  return searchableText.includes(q)
}

export function filterPostsByKeyword(posts, keyword) {
  if (!Array.isArray(posts)) return []
  return posts.filter((post) => matchPostKeyword(post, keyword))
}
