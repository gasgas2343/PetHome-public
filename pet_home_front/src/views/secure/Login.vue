<template>
	<h3>登入</h3>
	<table>
		<tbody>
			<tr>
				<td>ID : </td>
				<td><input type="text" v-model="username" ></td>
				<td></td>
			</tr>
			<tr>
				<td>PWD : </td>
				<td><input type="text" v-model="password"></td>
				<td></td>
			</tr>
			<tr>
				<td> </td>
				<td align="right"><button type="button" @click="login">登入</button></td>
			</tr>
		</tbody>
	</table>
</template>
    
<script setup>
	import Swal from "sweetalert2";
	import axiosapi from "@/plugins/axios.js";
	import { ref } from "vue";
	import { useUserStore } from "@/stores/user.js"; // 【一定要加 {}】

	const userStore = useUserStore();
	const username = ref("");
	const password = ref("");
	async function login() {
		Swal.fire({
			text: "執行中......",
			showConfirmButton: false,
			allowOutsideClick: false
		});
		const body = {
			"username": username.value,
			"password": password.value
		}
		userStore.setEmail("");
		userStore.setToken("");
		try {
			const response = await axiosapi.post("/ajax/secure/login", body);
			if(response.data.success) {
				await Swal.fire({
					text: response.data.message,
					icon: "success"
				});
				userStore.setEmail(response.data.email);
				userStore.setToken(response.data.token);
				window.location.href = "/";
			} else {
				Swal.fire({
					text: response.data.message,
					icon: "warning"
				});
			}
		} catch(error) {
			Swal.fire({
				text: "執行錯誤",
				icon: "error"
			});
		}
	}
</script>
    
<style>
    
</style>