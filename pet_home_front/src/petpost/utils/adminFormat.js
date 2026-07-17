// 中文註解：取得會員顯示名稱。
export function formatMemberName(item) {
  return item.username || item.userName || item.name || `會員 ${item.userId || '-'}`
}

// 中文註解：會員帳號只顯示前三碼，其餘隱藏。
export function maskMemberAccount(value) {
  const text = String(value || '')

  if (!text) {
    return '-'
  }

  if (text.length <= 3) {
    return `${text}*********`
  }

  return `${text.slice(0, 3)}*********`
}

// 中文註解：文章狀態文字。
export function formatPostStatus(status) {
  if (Number(status) === 2) {
    return '此文章已被隱藏'
  }

  if (Number(status) === 3) {
    return '此文章已刪除'
  }

  return '正常'
}

// 中文註解：留言狀態文字。
export function formatCommentStatus(status) {
  if (Number(status) === 2) {
    return '此留言已被隱藏'
  }

  if (Number(status) === 3) {
    return '此留言已刪除'
  }

  return '正常'
}
