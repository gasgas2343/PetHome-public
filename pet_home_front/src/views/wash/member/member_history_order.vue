<script setup></script>

<template>
  <h1>訂單查詢與美容師回饋</h1>
  <h5>結帳完成資料才進入查詢</h5>
  <hr />

  <div>
    訂單日期：
    <select>
      <option value="DATE1">2026-06-01</option>
      <option value="DATE2">2026-06-02</option>
      <option value="DATE3">2026-06-03</option>
      <option value="DATE4">2026-06-04</option>
      <option value="DATE5">2026-06-05</option>
    </select>
    <button>查詢</button>
  </div>

    <div>
    服務項目：
    <select>
      <option value="wash">洗澡</option>
      <option value="cut_nails">剪甲</option>
      <option value="shearing">剪毛</option>
    </select>
    <button>查詢</button>
  </div>
  <hr>
  <div>
    <h2>訂單查詢與美容師回饋</h2>
  
    <table>
      <thead>
        <tr>
          <th>序號</th>
          <th>訂單編號</th>
          <th>寵物名稱</th>
          <th>預約日期</th>
          <th>服務名稱</th>
          <th>美容師回饋</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>1</td>
          <td>appt_20260601_0001</td>
          <td>mimi</td>
          <td>2026-07-01</td>
          <td>剪甲</td>
          <td><button>點選進入</button></td>
        </tr>

          <tr>
          <td>2</td>
          <td>appt_20260601_0002</td>
          <td>nina</td>
          <td>2026-07-01</td>
          <td>剪甲</td>
          <td><button>點選進入</button></td>
        </tr>
        <tr>
          <td colspan="6" style="text-align: left; padding: 10px;">
            點擊[點選進入]按鈕：<br>
            顯示：<br>
            美容前照片/美容後照片/寵物服務回饋：紀錄皮膚狀態、心情、健康狀態、建議下次預約日期、留言底下會員可回覆留言，分享社群帳號。
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
tr,
th,
td {
  border: 1px solid black;
  text-align: center;
}

img {
  width: 100 px;
}
</style>
