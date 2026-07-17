<!-- <template>
    <div class="card">
        <img class="card-img-top" :alt="product.name" :src="`${path}${product.id}`">
        <div class="card-body">
            <h5 class="card-title">{{ product.name }}</h5>
            <div class="card-text text-danger text-end">NT$ {{ product.price }}</div>
            <div class="row">
                <div class="col text-start">
                    <button type="button" class="btn btn-primary" @click="emits('customUpdate', 'update', props.product.id)">開啟修改</button>
                </div>
                <div class="col text-end">
                    <button type="button" class="btn btn-danger" @click="emits('customDelete', props.product.id)">刪除</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
    const path = import.meta.env.VITE_DETAIL_API;
    const props = defineProps(["product"]);
    const emits = defineEmits(["customUpdate", "customDelete"]);

</script>
    
<style>
    
</style> -->
<template>
  <div class="card h-100 shadow-sm border-light-subtle rounded-3">
    <img class="card-img-top object-fit-cover" 
         :alt="product.name" 
         :src="`${path}${product.id}`" 
         style="height: 180px;">
         
    <div class="card-body d-flex flex-column p-3">
      <h5 class="card-title fw-bold text-dark text-truncate mb-2">{{ product.name }}</h5>
      
      <div class="card-text text-secondary bg-light rounded p-2 mb-2 small" 
           style="white-space: pre-line; max-height: 65px; overflow-y: auto; font-size: 12px; line-height: 1.4;">
        {{ product.description || '暫無詳細描述' }}
      </div>

      <div class="sku-display-zone mb-3 flex-grow-1">
        <div v-if="product.skus && product.skus.length > 0" class="d-flex flex-wrap gap-1">
          <span v-for="sku in product.skus" :key="sku.id" 
                class="badge text-dark border p-1 px-2 rounded-pill bg-white text-start shadow-xs"
                style="font-size: 11px; border-color: #dee2e6;">
            <span v-if="sku.flavor" class="text-primary">🎨 {{ sku.flavor }}</span>
            <span v-if="sku.flavor && sku.size" class="text-muted mx-1">|</span>
            <span v-if="sku.size" class="text-warning-emphasis">📏 {{ sku.size }}</span>
            <span class="text-muted ms-1">(${ { sku.price } })</span>
          </span>
        </div>
        <div v-else class="text-muted text-center py-1 rounded bg-light border-0" style="font-size: 11px;">
          基本單一規格
        </div>
      </div>

      <div class="card-text text-danger text-end fw-bold fs-5 mb-3">
        <span class="fs-6 text-muted fw-normal me-1">NT$</span>{{ product.basePrice || product.price || 0 }}
      </div>

      <div class="row g-2 mt-auto">
        <div class="col-6">
          <button type="button" class="btn btn-outline-primary btn-sm w-100 fw-bold rounded-pill py-2" 
                  @click="emits('customUpdate', 'update', props.product.id)">
            ⚙️ 修改商品
          </button>
        </div>
        <div class="col-6">
          <button type="button" class="btn btn-danger btn-sm w-100 fw-bold rounded-pill py-2" 
                  @click="emits('customDelete', props.product.id)">
            🗑️ 刪除
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  const path = import.meta.env.VITE_DETAIL_API;
  
  // 接收父元件傳過來的 product
  const props = defineProps(["product"]);
  const emits = defineEmits(["customUpdate", "customDelete"]);
</script>
    
<style scoped>
/* 讓卡片滑鼠滑過時有一點點精緻的浮起陰影感 */
.card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(0,0,0,0.08) !important;
}

/* 微調滾動條樣式，讓描述區更好看 */
div::-webkit-scrollbar {
  width: 4px;
}
div::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}
</style>