// 中文註解：PetPost 模組共用 API 回應與錯誤處理工具。
// 目的：讓各個 Pinia Store 不需要重複撰寫 response.data?.data 與 error message 判斷。

// 中文註解：支援後端可能回傳 ApiResponse<T> 或直接回傳資料本體。
export function unwrapApiData(response, fallback = null) {
  const body = response?.data

  if (
    body &&
    typeof body === 'object' &&
    !Array.isArray(body) &&
    Object.prototype.hasOwnProperty.call(body, 'data')
  ) {
    return body.data ?? fallback
  }

  return body ?? fallback
}

// 中文註解：把 API 回傳內容統一轉成陣列，避免畫面 v-for 爆掉。
export function unwrapApiList(response) {
  const data = unwrapApiData(response, [])
  return Array.isArray(data) ? data : []
}

// 中文註解：統一取得後端錯誤訊息。
export function getApiErrorMessage(error, fallback = '操作失敗') {
  return (
    error?.response?.data?.message ||
    error?.response?.data?.error ||
    error?.response?.data?.msg ||
    error?.message ||
    fallback
  )
}

// 中文註解：統一輸出 API 錯誤，方便開發時追蹤。
export function logApiError(label, error) {
  console.error(`${label}：`, error)
  console.error('後端狀態碼 =', error?.response?.status)
  console.error('後端回傳資料 =', error?.response?.data)
}

// 中文註解：共用 Store action 包裝器，統一 loading / errorMessage / catch / finally。
export async function runStoreAction(options) {
  const {
    loading,
    errorMessage,
    action,
    fallbackMessage = '操作失敗',
    logLabel = fallbackMessage,
    rethrow = false,
  } = options

  if (loading) {
    loading.value = true
  }

  if (errorMessage) {
    errorMessage.value = ''
  }

  try {
    return await action()
  } catch (error) {
    logApiError(logLabel, error)

    if (errorMessage) {
      errorMessage.value = getApiErrorMessage(error, fallbackMessage)
    }

    if (rethrow) {
      throw error
    }

    return null
  } finally {
    if (loading) {
      loading.value = false
    }
  }
}
