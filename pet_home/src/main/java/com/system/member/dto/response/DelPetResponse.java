package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DelPetResponse {
    private Long id;
    private Boolean isActive;
}
