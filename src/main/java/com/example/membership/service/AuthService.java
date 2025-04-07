package com.example.membership.service;

import com.example.membership.model.dto.LoginRequest;
import com.example.membership.model.dto.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     *修改密码
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @return 是否成功
     */
    boolean changePwd(String username, String password, String confirmPassword);
} 