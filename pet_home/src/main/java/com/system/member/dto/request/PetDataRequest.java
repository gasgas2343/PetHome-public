package com.system.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PetDataRequest {
    @NotNull(message = "請選擇寵物類型")
    private Long petTypeId;
    @NotBlank(message = "請輸入寵物名稱")
    @Size(max = 50)
    private String name;
    private String gender;
    @Past
    private LocalDate birthday;
    @Size(max = 50)
    private String breed;
    private BigDecimal weightKg;
    private String bodySize;
    @Size(max = 100)
    private String personality;
}
