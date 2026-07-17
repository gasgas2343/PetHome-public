<script setup>
import AdminNavbar from "@/components/petmap/AdminNavbar.vue";
</script>

<template>
  <AdminNavbar />

  <div class="container mt-4">
    <h2 class="fw-bold">PetMap 管理中心</h2>

    <div class="alert alert-info mt-3">
      歡迎使用 PetMap 管理後台
    </div>

    <div class="row mt-4">
      <div class="col-md-4">
        <div class="card text-center shadow-sm">
          <div class="card-body">
            <h5>📍 景點管理</h5>
            <RouterLink
              :to="{ name: 'PetMapAdminPlace' }"
              class="btn btn-primary mt-3"
            >
              進入管理
            </RouterLink>
          </div>
        </div>
      </div>

      <div class="col-md-4">
        <div class="card text-center shadow-sm">
          <div class="card-body">
            <h5>📝 投稿管理</h5>
            <RouterLink
              :to="{ name: 'PetMapAdminSubmission' }"
              class="btn btn-success mt-3"
            >
              進入管理
            </RouterLink>
          </div>
        </div>
      </div>

      <div class="col-md-4">
        <div class="card text-center shadow-sm">
          <div class="card-body">
            <h5>🚩 檢舉管理</h5>
            <RouterLink
              :to="{ name: 'PetMapAdminReport' }"
              class="btn btn-danger mt-3"
            >
              進入管理
            </RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
