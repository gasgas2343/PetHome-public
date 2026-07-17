import http from "./http";

export function getMeApi(){
    return http.get('/users/me')
}

export function getProfileApi(){
    return http.get('/users/me/profile')
}

export function updateProfileApi(data) {
    return http.put('/users/me/profile', data)
}

export function changePwdApi(data){
    return http.put('/users/me/password', data)
}

export function getPetApi(){
    return http.get('/users/me/pets')
}

export function createPetApi(data) {
  return http.post('/users/me/pets', data)
}

export function updatePetApi(petId, data) {
  return http.put(`/users/me/pets/${petId}`, data)
}

export function deletePetApi(petId, data) {
  return http.delete(`/users/me/pets/${petId}`, data)
}

export function getAllnotificationsApi(){
    return http.get('users/me/notifications')
}

export function getNotificationsCountApi(){
    return http.get('users/me/notifications/unread-count')
}

export function readNotificationApi(noteId){
    return http.put(`users/me/notifications/${noteId}/read`)
}

export function readAllNotificationsApi(){
    return http.put('users/me/notifications/read-all')
}

export function requestChangeEmailApi(data){
    return http.post('users/me/email', data)
}