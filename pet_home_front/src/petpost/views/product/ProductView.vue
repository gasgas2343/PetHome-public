<script setup>

// 共用元件
import SearchBar from '@/petpost/components/common/SearchBar.vue'
import CategoryTabs from '@/petpost/components/layout/CategoryTabs.vue'

</script>

<template>

    <div class="container">

        <!-- 搜尋列 -->
        <SearchBar />

        <!-- 分類切換 -->
        <CategoryTabs />

        <div class="row mt-4">

            <!-- 左側廣告 -->
            <div class="col-2">

                <div class="card">

                    <div class="card-body">

                        左側廣告

                    </div>

                </div>

            </div>

            <!-- 中間內容 -->
            <div class="col-8">

                <!-- 商品開箱 -->
                <div class="card mb-4">

                    <div class="card-header">

                        📦 商品開箱與推薦

                    </div>

                    <div class="card-body">

                        商品開箱文章

                    </div>

                </div>

                <!-- 使用心得 -->
                <div class="card">

                    <div class="card-header">

                        ⭐ 使用心得評論

                    </div>

                    <div class="card-body">

                        使用心得文章

                    </div>

                </div>

            </div>

            <!-- 右側廣告 -->
            <div class="col-2">

                <div class="card">

                    <div class="card-body">

                        右側廣告

                    </div>

                </div>

            </div>

        </div>

    </div>

</template>

<style scoped>

</style>