import http from "./http"

export function setupTotpApi(){
    return http.post('users/me/2fa/setup')
}

export function confirmTotpApi(code){
    return http.post('users/me/2fa/confirm', {code})
}

export function disableTotpApi(data){
  return http.post('/users/me/2fa/disable', data)
}
