package com.system.member.util;

import java.security.SecureRandom;
import java.util.Base64;

public class JWTSecretGenerateUtil {
    public static void main(String[] args){
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);

        String base64Key = Base64.getEncoder().encodeToString(key);
        System.out.println(base64Key);
    }

}
