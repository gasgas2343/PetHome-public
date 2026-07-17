package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TotpStatusResponse {
    private boolean twoFactorEnabled;
}
