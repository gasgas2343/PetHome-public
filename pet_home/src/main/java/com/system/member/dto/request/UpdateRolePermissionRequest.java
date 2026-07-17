package com.system.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateRolePermissionRequest {
    @NotNull(message = "權限不可為空")
    private List<String> permissions;
}
