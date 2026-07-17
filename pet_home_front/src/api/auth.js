import axios from 'axios'
import http from './http'
import { defineStore } from 'pinia'
export function registerApi(data) {
  return http.post('/auth/register', data)
}

export function loginApi(data) {
  return http.post('/auth/login', data)
}

export function verifyLoginTotpApi(data){
  return http.post('/auth/login/totp', data)
}

export function totpLoginApi(data){
  return http.put('/auth/login/totp', data)
}

export function logoutApi(data) {
  return http.post('/auth/logout', data)
}

export function refreshTokenApi(data) {
  return axios.post(`${import.meta.env.VITE_API_BASE_URL}/auth/refresh`, data)
}

export function forgotPasswordApi(data) {
  return http.post('/auth/forgot-password', data)
}

export function resetPasswordApi(data) {
  return http.post('/auth/reset-password', data)
}

export function sendVerificationEmailApi(data) {
  return http.post('/auth/send-email-verification', data)
}

export function verifyEmailApi(token) {
  return http.post('/auth/verify-email',null, {
    params: {
      token,
    },
  })
}

export function changeEmailConfirmApi(token){
  return http.post('/auth/email-change-confirm', null,{
    params: {
      token,
    },
  })
}