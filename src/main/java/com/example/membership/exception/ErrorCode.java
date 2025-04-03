package com.example.membership.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    
    // 通用错误
    SYSTEM_ERROR(500, "服务器内部错误"),
    INVALID_PARAMETER(400, "无效的请求参数"),
    
    // 认证相关错误
    USER_NOT_FOUND(1001, "用户不存在"),
    INVALID_PASSWORD(1002, "密码错误"),
    DECRYPTE_PASSWORD(2003,"密码解密失败"),

    USERNAME_ALREADY_EXISTS(1003, "用户名已存在"),
    PASSWORD_NOT_MATCH(1004, "两次输入的密码不一致"),
    LOGIN_FAILED(1005, "登录失败，请检查用户名和密码"),
    
    // 业务相关错误
    INSUFFICIENT_BALANCE(2001, "余额不足"),
    INVALID_CARD_TYPE(2002, "无效的会员卡类型");

    private final int code;
    private final String message;
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
} 