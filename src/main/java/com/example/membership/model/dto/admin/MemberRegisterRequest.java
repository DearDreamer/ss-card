package com.example.membership.model.dto.admin;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 会员注册请求DTO
 */
@Data
public class MemberRegisterRequest {
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @NotBlank(message = "确认手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "确认手机号格式不正确")
    private String confirmPhone;
    
    private BigDecimal initialAmount = BigDecimal.ZERO;
} 