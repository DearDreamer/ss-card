package com.example.membership.util;

import com.example.membership.exception.CustomException;
import com.example.membership.exception.ErrorCode;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class PasswordDecryptor {

    private final String SECRET_KEY = "12345678901234567890123456789012";
    /**
     * 解密前端传来的密码
     * 1. Base64解码得到SHA256哈希值
     */
    public String decryptPassword(String base64EncodedPassword) {
        try {
            // Base64解码
            byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedPassword);
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD, "密码格式不正确");
        }
    }

    /**
     * 解密AES加密的密码
     */
    public String decryptAES(String encryptedPassword) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedPassword);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.DECRYPTE_PASSWORD);
        }
    }
} 