package com.system.member.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileRequest {
    @Size(max = 20, message = "姓名長度不可超過 20 字")
    private String fullName;
    @Size(min = 2,max = 20)
    private String nickName;
    @Pattern(regexp = "^09\\d{8}$|^$", message = "手機格式不正確")
    private String phone;
    @Past(message = "生日不可是未來日期")
    private LocalDate birthday;
    @Size(max = 500)
    private String avatarUrl;
}
