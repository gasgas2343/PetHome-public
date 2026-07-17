<script setup>
import { ref, computed, onMounted } from "vue";
import { getAllPlaces } from "@/api/petmap/placeApi";
import AdminNavbar from "@/components/petmap/AdminNavbar.vue";
import Swal from "sweetalert2";
import { updatePlace } from "@/api/petmap/placeApi";
import { deletePlace } from "@/api/petmap/placeApi";

const loading = ref(false);

const places = ref([]);

const keyword = ref("");

const selectedType = ref("ALL");

const selectedStatus = ref("ALL");

const sortBy = ref("newest");

const typeMap = {
    RESTAURANT: "餐廳",
    CAFE: "咖啡廳",
    HOTEL: "住宿",
    PARK: "公園",
    HOSPITAL: "動物醫院"
};

const getPlaceTypeName = (type) => {
    return typeMap[type] || type;
};

const loadPlaces = async () => {
    loading.value = true;

    try {
        const res = await getAllPlaces();

        places.value = res.data.data || [];
    } catch (e) {
        console.error(e);
    } finally {
        loading.value = false;
    }
};

onMounted(loadPlaces);

const statusMap = {
    APPROVED: "已通過",
    PENDING: "待審核",
    REJECTED: "已駁回"
};

const viewPlace = (place) => {

    Swal.fire({
        title: `📍 ${place.name}`,
        width: 700,
        html: `
            <div style="text-align:left;line-height:2">

                <b>類型：</b>${getPlaceTypeName(place.placeType)}<br>

                <b>狀態：</b>${statusMap[place.status] ?? place.status}<br>

                <b>地址：</b>${place.address}<br>

                <b>電話：</b>${place.phone ?? "-"}<br>

                <b>描述：</b>${place.description ?? "-"}<br>

                <b>平均評分：</b>${place.ratingAvg ?? "-"}<br>

                <b>評論數：</b>${place.reviewCount ?? 0}<br>

                <b>建立時間：</b>${place.createdAt ?? "-"}<br>

                <b>更新時間：</b>${place.updatedAt ?? "-"}

            </div>
        `,
        confirmButtonText: "關閉"
    });

};

const filteredPlaces = computed(() => {

    let data = [...places.value];

    if (keyword.value) {

        const k = keyword.value.toLowerCase();

        data = data.filter(place =>
            place.name?.toLowerCase().includes(k) ||
            place.address?.toLowerCase().includes(k)
        );

    }

    if (selectedType.value !== "ALL") {

        data = data.filter(
            place => place.placeType === selectedType.value
        );

    }

    if (selectedStatus.value !== "ALL") {

    data = data.filter(
        place => place.status === selectedStatus.value
    );

}

    // 排序
switch (sortBy.value) {
    case "newest":
        data.sort((a, b) => b.placeId - a.placeId);
        break;

    case "oldest":
        data.sort((a, b) => a.placeId - b.placeId);
        break;

    case "rating":
        data.sort((a, b) => (b.ratingAvg || 0) - (a.ratingAvg || 0));
        break;

    case "name":
        data.sort((a, b) => a.name.localeCompare(b.name, "zh-Hant"));
        break;
}

return data;

});

const totalCount = computed(() => places.value.length);

const approvedCount = computed(() =>

    places.value.filter(

        place => place.status === "APPROVED"

    ).length

);

const pendingCount = computed(() =>

    places.value.filter(

        place => place.status === "PENDING"

    ).length

);

const rejectedCount = computed(() =>

    places.value.filter(

        place => place.status === "REJECTED"

    ).length

);


const editPlace = async (place) => {

    const { value: formValues } = await Swal.fire({

        title: "修改景點",

        width: 700,

        html: `

            <label style="display:block;text-align:left;margin-top:10px;">名稱</label>
            <input id="swal-name" class="swal2-input" value="${place.name}">

            <label style="display:block;text-align:left;margin-top:10px;">類型</label>
            <select id="swal-type" class="swal2-select">

                <option value="RESTAURANT" ${place.placeType === "RESTAURANT" ? "selected" : ""}>餐廳</option>

                <option value="CAFE" ${place.placeType === "CAFE" ? "selected" : ""}>咖啡廳</option>

                <option value="HOTEL" ${place.placeType === "HOTEL" ? "selected" : ""}>住宿</option>

                <option value="PARK" ${place.placeType === "PARK" ? "selected" : ""}>公園</option>

                <option value="HOSPITAL" ${place.placeType === "HOSPITAL" ? "selected" : ""}>動物醫院</option>

            </select>

            <label style="display:block;text-align:left;margin-top:10px;">地址</label>

            <input id="swal-address" class="swal2-input" value="${place.address}">

            <label style="display:block;text-align:left;margin-top:10px;">電話</label>

            <input id="swal-phone" class="swal2-input" value="${place.phone ?? ""}">

            <label style="display:block;text-align:left;margin-top:10px;">描述</label>

            <textarea
                id="swal-description"
                class="swal2-textarea">${place.description ?? ""}</textarea>

            <label style="display:block;text-align:left;margin-top:10px;">狀態</label>

            <select id="swal-status" class="swal2-select">

                <option value="APPROVED" ${place.status === "APPROVED" ? "selected" : ""}>已通過</option>

                <option value="PENDING" ${place.status === "PENDING" ? "selected" : ""}>待審核</option>

                <option value="REJECTED" ${place.status === "REJECTED" ? "selected" : ""}>已駁回</option>

            </select>

        `,

        showCancelButton: true,

        confirmButtonText: "儲存",

        cancelButtonText: "取消",

        focusConfirm: false,

        preConfirm: () => ({

            name: document.getElementById("swal-name").value,

            placeType: document.getElementById("swal-type").value,

            address: document.getElementById("swal-address").value,

            phone: document.getElementById("swal-phone").value,

            description: document.getElementById("swal-description").value,

            status: document.getElementById("swal-status").value

        })

    });

    if (!formValues) return;

    try {

        await updatePlace(place.placeId, {

            name: formValues.name,

            placeType: formValues.placeType,

            address: formValues.address,

            latitude: place.latitude,

            longitude: place.longitude,

            phone: formValues.phone,

            description: formValues.description,

            status: formValues.status

        });

        Swal.fire({

            icon: "success",

            title: "修改成功",

            timer: 1200,

            showConfirmButton: false

        });

        await loadPlaces();

    } catch (e) {

        console.error(e);

        Swal.fire({

            icon: "error",

            title: "修改失敗"

        });

    }

};

const deletePlaceById = async (place) => {

    const result = await Swal.fire({

        title: "確認刪除？",

        text: `確定要刪除「${place.name}」嗎？`,

        icon: "warning",

        showCancelButton: true,

        confirmButtonText: "刪除",

        cancelButtonText: "取消",

        confirmButtonColor: "#d33"

    });

    if (!result.isConfirmed) return;

    try {

        await deletePlace(place.placeId);

        Swal.fire({

            icon: "success",

            title: "刪除成功",

            timer: 1200,

            showConfirmButton: false

        });

        await loadPlaces();

    } catch (e) {

        console.error(e);

        Swal.fire({

            icon: "error",

            title: "刪除失敗"

        });

    }

};
</script>
<template>

<AdminNavbar />

<div class="container py-4">

    <h2 class="fw-bold mb-4">

        景點管理

    </h2>

    <div class="row mb-4">

    <div class="col-md-3">

        <div class="card shadow-sm">

            <div class="card-body text-center">

                <h6 class="text-secondary">景點總數</h6>

                <h2 class="fw-bold">{{ totalCount }}</h2>

            </div>

        </div>

    </div>

    <div class="col-md-3">

        <div class="card shadow-sm border-success">

            <div class="card-body text-center">

                <h6 class="text-success">已通過</h6>

                <h2 class="fw-bold text-success">

                    {{ approvedCount }}

                </h2>

            </div>

        </div>

    </div>

    <div class="col-md-3">

        <div class="card shadow-sm border-warning">

            <div class="card-body text-center">

                <h6 class="text-warning">

                    待審核

                </h6>

                <h2 class="fw-bold text-warning">

                    {{ pendingCount }}

                </h2>

            </div>

        </div>

    </div>

    <div class="col-md-3">

        <div class="card shadow-sm border-danger">

            <div class="card-body text-center">

                <h6 class="text-danger">

                    已駁回

                </h6>

                <h2 class="fw-bold text-danger">

                    {{ rejectedCount }}

                </h2>

            </div>

        </div>

    </div>

</div>

    <div class="card shadow-sm mb-4">

        <div class="card-body">

            <div class="row">

                <div class="col-md-3">

                    <input
                        v-model="keyword"
                        class="form-control"
                        placeholder="搜尋名稱或地址">

                </div>

                <div class="col-md-3">

                    <select
                        v-model="selectedType"
                        class="form-select">

                        <option value="ALL">全部類型</option>
                        <option value="CAFE">咖啡廳</option>
                        <option value="RESTAURANT">餐廳</option>
                        <option value="HOTEL">住宿</option>
                        <option value="PARK">公園</option>
                        <option value="HOSPITAL">醫院</option>

                    </select>

                </div>

                <div class="col-md-3">

                    <select
                        v-model="selectedStatus"
                        class="form-select">

                        <option value="ALL">

全部狀態

</option>

<option value="APPROVED">

已通過

</option>

<option value="PENDING">

待審核

</option>

<option value="REJECTED">

已駁回

</option>

                    </select>

                </div>

                <div class="col-md-3">

    <select
        v-model="sortBy"
        class="form-select">

        <option value="newest">最新建立</option>
        <option value="oldest">最舊建立</option>
        <option value="rating">評分最高</option>
        <option value="name">名稱 A ~ Z</option>

    </select>

</div>

            </div>

        </div>

    </div>

    <div class="card shadow-sm">

        <div class="table-responsive">

            <table class="table table-hover align-middle mb-0">

                <thead class="table-light">

                <tr>

                    <th>ID</th>
                    <th>名稱</th>
                    <th>類型</th>
                    <th>地址</th>
                    <th>電話</th>
                    <th>評分</th>
                    <th>狀態</th>
                    <th width="200">操作</th>

                </tr>

                </thead>

                <tbody>

                <tr v-if="loading">

                    <td colspan="8" class="text-center py-4">

                        載入中...

                    </td>

                </tr>

                <tr
                    v-else-if="filteredPlaces.length===0">

                    <td colspan="8" class="text-center py-4">

                        查無資料

                    </td>

                </tr>

                <tr
                    v-for="place in filteredPlaces"
                    :key="place.placeId">

                    <td>{{ place.placeId }}</td>

                    <td>{{ place.name }}</td>

                    <td>{{ getPlaceTypeName(place.placeType) }}</td>

                    <td class="address-cell"
                    :title="place.address">

{{ place.address }}

</td>

                    <td>{{ place.phone }}</td>

                    <td> {{ place.ratingAvg ? Number(place.ratingAvg).toFixed(2) : "-" }}</td>

                    <td>
    <span
        v-if="place.status === 'APPROVED'"
        class="badge rounded-pill bg-success px-3 py-2">
        已通過
    </span>

    <span
        v-else-if="place.status === 'PENDING'"
        class="badge rounded-pill bg-warning text-dark px-3 py-2">
        待審核
    </span>

    <span
        v-else-if="place.status === 'REJECTED'"
        class="badge rounded-pill bg-danger px-3 py-2">
        已駁回
    </span>

    <span
        v-else
        class="badge rounded-pill bg-secondary px-3 py-2">
        {{ place.status }}
    </span>
</td>

                    <td>

                        <button
                            class="btn btn-sm btn-outline-primary me-2"
                            @click="viewPlace(place)">

                            查看

                        </button>

                        <button
                            class="btn btn-sm btn-outline-warning me-2"
                            @click="editPlace(place)">

                            修改

                        </button>

                        <button
                            class="btn btn-sm btn-outline-danger"
                            @click="deletePlaceById(place)">

                            刪除

                        </button>

                    </td>

                </tr>

                </tbody>

            </table>

        </div>

    </div>

</div>

</template>

<style scoped>
.address-cell{

    max-width:320px;

    white-space:nowrap;

    overflow:hidden;

    text-overflow:ellipsis;

}

.badge{
    min-width:72px;
    text-align:center;
}
</style>
