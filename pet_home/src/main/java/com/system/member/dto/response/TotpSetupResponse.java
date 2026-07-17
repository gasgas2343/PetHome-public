package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TotpSetupResponse {
    String qrCode;
    String secret;
}
