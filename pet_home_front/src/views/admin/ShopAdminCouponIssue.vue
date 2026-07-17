<template>
  <div class="card p-4">
    <h3>管理員發放優惠券</h3>
    <input v-model="form.userId" placeholder="會員 ID" class="form-control mb-2">
    <select v-model="form.couponId" class="form-control mb-2">
      <option v-for="c in coupons" :value="c.id">{{ c.title }}</option>
    </select>
    <button @click="submitIssue" class="btn btn-primary">發放優惠券</button>
  </div>
</template>

<script setup>
const form = ref({ userId: '', couponId: '', sourceType: 'MANUAL', sourceKey: 'ADMIN_' + Date.now() });

const submitIssue = async () => {
  await axios.post('/coupons/issue', form.value);
  alert("發送成功！");
};
</script>