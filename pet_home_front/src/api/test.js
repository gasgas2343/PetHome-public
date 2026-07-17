import http from "./http";

export function testApi(){
    return http.get('/test')
}