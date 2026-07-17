import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useUserStore = defineStore("user", () => {
    const userId = ref(null);
    const email = ref("");
    const token = ref("");
    const role = ref(null); // 'CUSTOMER' 或 'ADMIN'

    // 角色判斷
    const isAdmin = computed(() => role.value === 'ADMIN');
    const isCustomer = computed(() => role.value === 'CUSTOMER');

    function setUserId(id) {
        userId.value = id;
    }

    function setEmail(newEmail) {
        email.value = newEmail;
    }

    function setToken(newToken) {
        token.value = newToken;
    }

    function setRole(newRole) {
        role.value = newRole;
    }

    // 之後登入系統呼叫這一個方法就好
    function setUser(id, userEmail, userToken, userRole) {
        userId.value = id;
        email.value = userEmail;
        token.value = userToken;
        role.value = userRole;
    }

    function clearUser() {
        userId.value = null;
        email.value = "";
        token.value = "";
        role.value = null;
    }

    return {
        userId, email, token, role,
        isAdmin, isCustomer,
        setUserId, setEmail, setToken, setRole,
        setUser, clearUser
    }
}, {
    persist: {
        storage: localStorage,
    },
});