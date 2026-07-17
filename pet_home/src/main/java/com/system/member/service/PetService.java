package com.system.member.service;

import com.system.member.dto.request.PetDataRequest;
import com.system.member.dto.response.DelPetResponse;
import com.system.member.dto.response.PetDataResponse;
import com.system.member.entity.PetTypesBean;
import com.system.member.entity.PetsBean;
import com.system.member.entity.UserBean;
import com.system.member.exception.BusinessException;
import com.system.member.repository.PetRepository;
import com.system.member.repository.PetTypeRepository;
import com.system.member.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class PetService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PetTypeRepository petTypeRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private PermissionService permissionService;

    @Transactional(readOnly = true)
    public List<PetDataResponse> getPet(Long userId){

       List<PetsBean> list = petRepository.findByUser_IdAndIsActiveTrue(userId);

        return list.stream().map(pet -> new PetDataResponse(
                pet.getId(),
                pet.getPetTypes().getId(),
                pet.getPetTypes().getName(),
                pet.getName(),
                pet.getGender(),
                pet.getBirthday(),
                pet.getBreed(),
                pet.getWeightKg(),
                pet.getBodySize(),
                pet.getPersonality()
        )).toList();
    }

    @Transactional
    public PetDataResponse createPet(Long userId, PetDataRequest request) {
        UserBean user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        PetTypesBean type = petTypeRepository.findById(request.getPetTypeId())
                .orElseThrow(() -> new BusinessException("PET_TYPE_NOT_FOUND", "找不到寵物類型"));

        PetsBean pet = new PetsBean();
        pet.setUser(user);
        pet.setPetTypes(type);
        pet.setName(request.getName());
        pet.setGender(request.getGender());
        pet.setBirthday(request.getBirthday());
        pet.setBreed(request.getBreed());
        pet.setWeightKg(request.getWeightKg());
        pet.setBodySize(request.getBodySize());
        pet.setPersonality(request.getPersonality());
        pet.setActive(true);
        PetsBean save = petRepository.save(pet);

        return new PetDataResponse(
                save.getId(),
                type.getId(),
                type.getName(),
                save.getName(),
                save.getGender(),
                save.getBirthday(),
                save.getBreed(),
                save.getWeightKg(),
                save.getBodySize(),
                save.getPersonality()
        );
    }

    @Transactional
    public PetDataResponse updatePet(Long userId, Long petId, PetDataRequest request){

        PetsBean pet = petRepository.findByIdAndUser_IdAndIsActiveTrue(petId, userId)
                .orElseThrow(() -> new BusinessException("PET_NOT_FOUND", "找不到該寵物"));
        PetTypesBean type = petTypeRepository.findById(request.getPetTypeId())
                .orElseThrow(() -> new BusinessException("PET_TYPE_NOT_FOUND", "找不到寵物類型"));

        pet.setPetTypes(type);
        pet.setName(request.getName());
        pet.setGender(request.getGender());
        pet.setBirthday(request.getBirthday());
        pet.setBreed(request.getBreed());
        pet.setWeightKg(request.getWeightKg());
        pet.setBodySize(request.getBodySize());
        pet.setPersonality(request.getPersonality());
        PetsBean save = petRepository.save(pet);

        return new PetDataResponse(
                save.getId(),
                type.getId(),
                type.getName(),
                save.getName(),
                save.getGender(),
                save.getBirthday(),
                save.getBreed(),
                save.getWeightKg(),
                save.getBodySize(),
                save.getPersonality()
        );
    }

    @Transactional
    public DelPetResponse deletePet(Long userId, Long petId){

        PetsBean pet = petRepository.findByIdAndUser_IdAndIsActiveTrue(petId, userId)
                .orElseThrow(() -> new BusinessException("PET_NOT_FOUND", "找不到該寵物"));

        pet.setActive(false);
        petRepository.save(pet);

        return new DelPetResponse(
                pet.getId(),
                pet.isActive()
        );
    }
}
