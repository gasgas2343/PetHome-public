import api from './http'

export function findMyPets() {
  return api.get('/pets/me')
}

export function findAllPets() {
  return api.get('/pets')
}

export function findPetByIdApi(petId) {
  return api.get(`/pets/${petId}`)
}

export function createPet(dto) {
  return api.post('/pets', dto)
}

export function updatePetApi(petId, dto) {
  return api.put(`/pets/${petId}`, dto)
}

export function deletePetApi(petId) {
  return api.delete(`/pets/${petId}`)
}