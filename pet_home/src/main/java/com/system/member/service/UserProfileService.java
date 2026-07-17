package com.system.member.service;

import com.system.member.dto.request.UserProfileRequest;
import com.system.member.dto.response.UserProfileResponse;
import com.system.member.dto.response.GetUserProfileResponse;
import com.system.member.entity.UserBean;
import com.system.member.entity.UserProfilesBean;
import com.system.member.exception.BusinessException;
import com.system.member.repository.UserProfileRepository;
import com.system.member.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public GetUserProfileResponse getUserProfile(Long id){
        UserBean user = usersRepository.findById(id)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        UserProfilesBean profile = userProfileRepository.findByUserId(id)
                .orElseThrow(() -> new BusinessException("USERPROFILE_NOT_FOUND", "找不到使用者資訊"));

        return new GetUserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.isEmailVerified(),
                profile.getFullName(),
                profile.getNickname(),
                profile.getPhone(),
                profile.getBirthday(),
                profile.getAvatarUrl(),
                profile.getPointsBalance()
        );
    }

    @Transactional
    public UserProfileResponse updateProfile(Long id, UserProfileRequest request){
        UserBean user = usersRepository.findById(id)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        UserProfilesBean profile = userProfileRepository.findByUserId(id)
                .orElseThrow(() -> new BusinessException("USERPROFILE_NOT_FOUND", "找不到使用者資訊"));

        profile.setFullName(request.getFullName());
        profile.setNickname(request.getNickName());
        profile.setPhone(request.getPhone());
        profile.setBirthday(request.getBirthday());
//        profile.setAvatarUrl(request.getAvatarUrl());
        UserProfilesBean savedProfile =  userProfileRepository.save(profile);

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.isEmailVerified(),
                savedProfile.getFullName(),
                savedProfile.getNickname(),
                savedProfile.getPhone(),
                savedProfile.getBirthday(),
                savedProfile.getAvatarUrl(),
                savedProfile.getPointsBalance()
        );
    }
}

