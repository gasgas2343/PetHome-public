package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PetDataResponse {
    private Long id;
    private Long petTypeId;
    private String petTypeName;
    private String name;
    private String gender;
    private LocalDate birthday;
    private String breed;
    private BigDecimal weightKg;
    private String bodySize;
    private String personality;
}
