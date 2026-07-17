<template>
    <div ref="modalRef" class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">產品資料</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <table>
                        <tbody>
                            <tr>
                                <td>Id：</td>
                                <td><input type="text" name="id" :value="modelValue.id" @input="doinput('id', $event)"></td>
                            </tr>
                            <tr>
                                <td>Name：</td>
                                <td><input type="text" name="name" :value="modelValue.name" @input="doinput('name', $event)"></td>
                            </tr>
                            <tr>
                                <td>Price：</td>
                                <td><input type="text" name="price" :value="modelValue.price" @input="doinput('price', $event)"></td>
                            </tr>
                            <tr>
                                <td>Make：</td>
                                <td><input type="text" name="make" :value="modelValue.make" @input="doinput('make', $event)"></td>
                            </tr>
                            <tr>
                                <td>Expire：</td>
                                <td><input type="text" name="expire" :value="modelValue.expire" @input="doinput('expire', $event)"></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="modal-footer">
<button type="button" class="btn btn-primary" v-show="isShowInsert" @click="emits('customInsert')">新增</button>
<button type="button" class="btn btn-primary" v-show="!isShowInsert" @click="emits('customUpdate')">修改</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</template>
    
<script setup>
    const props = defineProps(["modelValue", "isShowInsert"]);
    const emits = defineEmits(["update:modelValue", "customInsert", "customUpdate"]);
    function doinput(field, event) {
        emits("update:modelValue", {
            ...props.modelValue,
            [field]: event.target.value
        });
    }

    import bootstrap from 'bootstrap/dist/js/bootstrap.bundle.min.js';
    import { ref, onMounted } from 'vue';
    const modalRef = ref(null);
    const modal = ref(null);
    onMounted(() => {
        modal.value = new bootstrap.Modal(modalRef.value);
    });
    function showModal() {
        modal.value.show();
    }
    function hideModal() {
        modal.value.hide();
    }
    defineExpose({
        showModal, hideModal
    });
</script>
    
<style>
    
</style>