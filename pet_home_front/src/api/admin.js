import http from "./http";

export function getPermissionApi(){
    return http.get('/users/me/module')
}

export function getAdminUsersApi(){
    return http.get('/admin/users')
}

export function getAdminUserDetailApi(userId){
    return http.get(`/admin/users/${userId}`)
}

export function updateAdminUserRoleApi(userId, data){
    return http.put(`/admin/users/roles/${userId}`, data)
}

export function getAllRolesApi(){
    return http.get('/admin/roles')
}

export function getAllPermissionsApi(){
    return http.get('/admin/permissions')
}

export function getRolePermissionsApi(roleId){
    return http.get(`/admin/permissions/${roleId}`)
}

export function updateRolePermissionsApi(roleId, data){
    return http.put(`/admin/permissions/${roleId}`, data)
}

export function getAuditLogsApi(){
    return http.get('/admin/logs')
}

export function getLoginAttemptsApi(){
    return http.get('/admin/login-attempts')
}

export function updateUserStatusApi(userId, data){
    return http.put(`/admin/users/status/${userId}`, data)
}

export function getAdminSessionsApi(){
    return http.get('/admin/sessions')
}