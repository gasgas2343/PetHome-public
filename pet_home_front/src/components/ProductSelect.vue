<template>
    <span>
        <span v-if="total<=0" >查無資料</span>
        <span v-else>查到{{total}}筆資料</span>
        ，每頁顯示
        <select :value="modelValue" @change="dochange">
            <option v-for="option in options" :key="option" :value="option">{{ option }}</option>
        </select>筆
    </span>
</template>
    
<script setup>
    const props = defineProps(["options", "modelValue", "total"]);
    const emits = defineEmits(["customChange", "update:modelValue"]);

    function dochange(event) {
        emits("update:modelValue", event.target.value);
        emits("customChange");
    }
</script>
    
<style>
    
</style>