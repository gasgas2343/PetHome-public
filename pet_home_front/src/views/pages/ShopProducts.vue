<template>
    <h3>產品</h3>
    <div class="row">
        <div class="col-2">
            <button type="button" class="btn btn-primary" @click="openModal('insert')" >開啟新增</button>
        </div>
        <div class="col-3">
            <input type="text" placeholder="請輸入產品名稱" v-model.trim="findName" @input="callFind(1)">
        </div>
        <div class="col-7">
            <ProductSelect :options="[1, 2, 3, 4, 5]" :total="total"
                        v-model="rows" @custom-change="callFind(1)">
            </ProductSelect>
        </div>
    </div>
    <br>

    <div class="row">
        <div class="col-6">
            <Paginate  v-show="total>0" :first-last-button="true" first-button-text="&lt;&lt;" last-button-text="&gt;&gt;"
                    prev-text="&lt;" next-text="&gt;"
                    :click-handler="callFind" :page-count="pages" :initial-page="current" v-model="current">
            </Paginate>
        </div>
    </div> 
    <br>

    <div class="row">
        <div v-for="item in products" :key="item.id"
                class="col-12 col-sm-6 col-md-6 col-lg-4 col-xl-3 col-xxl-3">
            <ProductCard :product="item"
                    @custom-update="openModal"
                    @custom-delete="callRemove">
            </ProductCard>
        </div>
    </div>

    <ProductModal ref="modal" v-model="product"
                :is-show-insert="isShowInsert"
                @custom-insert="callCreate"
                @custom-update="callModify">
    </ProductModal>
</template>
    
<script setup>
    import Paginate from "vuejs-paginate-next";
    import ProductCard from '@/components/ProductCard.vue'
    import ProductModal from '@/components/ProductModal.vue'
    import ProductSelect from '@/components/ProductSelect.vue'
    import axiosapi from '@/plugins/axios.js';
    import Swal from "sweetalert2";
    import { ref, onMounted } from 'vue';
    import useUserStore from "@/stores/user.js";
import axios from "axios";

	const userStore = useUserStore();
    const products = ref([]);
    const product = ref({ });
    const modal = ref(null);
    const isShowInsert = ref(true);

    //分頁 start
    const rows = ref(2);
    const total = ref(10);
    const pages = ref(0);
    const start = ref(0);
    const current = ref(1);
    const lastPageRows = ref(0);
    //分頁 stop

    const findName = ref("");

    async function callFind(page) {
        if(page) {
            start.value = (page-1)*rows.value;
            current.value = page;
        } else {
            start.value = 0;
            current.value = 1;
        }
        const body = {
            "start": start.value,
            "rows": rows.value,
            "sort": "id",
            "dir": false,
            "name" : findName.value,
        };
        try {
            const response = await axiosapi.post("/ajax/pages/products/find111", body, {
                headers: {
                    "Authorization": `Bearer ${userStore.token}`
                }
            });

            total.value = response.data.count;
            products.value = response.data.list;
            pages.value = Math.ceil(total.value/rows.value);
            lastPageRows.value = total.value%rows.value;
        } catch(error) {
			Swal.fire({
				text: "查詢錯誤:"+ error.message,
				icon: "error"
			});
        }
    }
    async function callCreate() {
		Swal.fire({
			text: "執行中......",
			showConfirmButton: false,
			allowOutsideClick: false
		});
        if(product.value.id==="") {
            product.value.id = null;
        }
        if(product.value.name==="") {
            product.value.name = null;
        }
        if(product.value.price==="") {
            product.value.price = null;
        }
        if(product.value.make==="") {
            product.value.make = null;
        }
        if(product.value.expire==="") {
            product.value.expire = null;
        }
        const body = product.value;
        try {
            const response = await axiosapi.post("/ajax/pages/products", body, {
                headers: {
                    "Authorization": `Bearer ${userStore.token}`
                }
            });
            if(response.data.success) {
                await Swal.fire({
                    text: response.data.message,
                    icon: "success"
                });

                modal.value.hideModal();
                callFind(current.value);
            } else {
                Swal.fire({
                    text: response.data.message,
                    icon: "warning"
                });
            }
        } catch(error) {
			Swal.fire({
				text: "新增錯誤:"+ error.message,
				icon: "error"
			});
        }
    }
    async function callFindById(id) {
        Swal.fire({
			text: "執行中......",
			showConfirmButton: false,
			allowOutsideClick: false
		});
        if(id) {
            try {
                const response = await axiosapi.get(`/ajax/pages/products/${id}`, {
                    headers: {
                        "Authorization": `Bearer ${userStore.token}`
                    }
                });
                if(response.data.list) {
                    product.value = response.data.list[0];
                }

                setTimeout(() => {
                    Swal.close();
                }, 500);
            } catch(error) {
                Swal.fire({
                    text: "查詢錯誤:"+ error.message,
                    icon: "error"
                });
            }
        }
    }
    async function callModify() {
		Swal.fire({
			text: "執行中......",
			showConfirmButton: false,
			allowOutsideClick: false
		});
        if(product.value.id==="") {
            product.value.id = null;
        }
        if(product.value.name==="") {
            product.value.name = null;
        }
        if(product.value.price==="") {
            product.value.price = null;
        }
        if(product.value.make==="") {
            product.value.make = null;
        }
        if(product.value.expire==="") {
            product.value.expire = null;
        }
        const body = product.value;
        try {
            const response = await axiosapi.put(`/ajax/pages/products/${product.value.id}`, body, {
                headers: {
                    "Authorization": `Bearer ${userStore.token}`
                }
            });
            if(response.data.success) {
                await Swal.fire({
                    text: response.data.message,
                    icon: "success"
                });

                modal.value.hideModal();
                callFind(current.value);
            } else {
                Swal.fire({
                    text: response.data.message,
                    icon: "warning"
                });
            }
        } catch(error) {
            Swal.fire({
                text: "修改錯誤:"+ error.message,
                icon: "error"
            });
        }
    }
    async function callRemove(id) {
        const result = await Swal.fire({
			text: "確定刪除？",
            showCancelButton: true
        });
        if(result.isConfirmed) {
            try {
                const response = await axiosapi.delete(`/ajax/pages/products/${id}`, {
                    headers: {
                        "Authorization": `Bearer ${userStore.token}`
                    }
                });
                if(response.data.success) {
                    await Swal.fire({
                        text: response.data.message, icon: "success"
                    });
                    if(current.value>1 && lastPageRows.value==1) {
                        current.value = current.value -1 ;
                    }
                    modal.value.hideModal();
                    callFind(current.value);
                } else {
                    Swal.fire({
                        text: response.data.message, icon: "warning"
                    });
                }
            } catch(error) {
                Swal.fire({
                    text: "刪除錯誤:"+ error.message, icon: "error"
                });
            }
        }
    }
    function openModal(action, data) {
        if(action==="insert") {
            product.value = { };
            isShowInsert.value = true;
        } else {
            callFindById(data);
            isShowInsert.value = false;
        }
        modal.value.showModal();
    }
    onMounted(() => {
        callFind();
    });
</script>
    
<style>
    
</style>