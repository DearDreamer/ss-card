package com.example.membership.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private String phone;
    private String cardType;
    private String balance; // 已格式化为字符串的余额
    private String lastVisit; // 已格式化为字符串的最后到店时间
} 