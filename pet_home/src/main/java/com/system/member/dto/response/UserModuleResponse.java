package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserModuleResponse {
    private List<ModuleItem> modules;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ModuleItem{
        private String moduleCode;
        private String moduleName;
        private Integer sortOrder;
        private List<String> permissions;
    }
}
