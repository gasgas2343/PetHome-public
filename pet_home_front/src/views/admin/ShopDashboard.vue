<template>
  <div class="row">
    <div class="col-md-4">
      <div class="card p-3">
        <h3>商品管理</h3>
        <router-link to="/admin/products" class="btn btn-primary">前往管理</router-link>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card p-3">
        <h3>優惠券發放</h3>
        <router-link to="/admin/coupons" class="btn btn-success">前往設定</router-link>
      </div>
    </div>
  </div>
</template>