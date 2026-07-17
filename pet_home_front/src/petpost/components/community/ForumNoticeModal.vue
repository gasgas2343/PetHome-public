<script setup>

// 固定公告資料
const notices = [

    '本站禁止販售活體動物',

    '請注意防詐騙',

    '禁止發布違法內容'

]

</script>

<template>

<div
    class="modal fade"
    id="noticeModal"
    tabindex="-1"
>

    <div class="modal-dialog">

        <div class="modal-content">

            <div class="modal-header">

                <h5>

                    系統公告

                </h5>

                <button
                    class="btn-close"
                    data-bs-dismiss="modal"
                >
                </button>

            </div>

            <div class="modal-body">

                <ul class="list-group">

                    <li
                        class="list-group-item"
                        v-for="notice in notices"
                        :key="notice"
                    >

                        {{ notice }}

                    </li>

                </ul>

            </div>

        </div>

    </div>

</div>

</template>

<style scoped>

</style>