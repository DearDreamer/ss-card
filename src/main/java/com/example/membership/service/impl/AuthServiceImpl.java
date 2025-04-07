package com.example.membership.service.impl;

import com.example.membership.exception.CustomException;
import com.example.membership.exception.ErrorCode;
import com.example.membership.model.dto.LoginRequest;
import com.example.membership.model.dto.LoginResponse;
import com.example.membership.model.entity.User;
import com.example.membership.repository.UserRepository;
import com.example.membership.service.AuthService;
import com.example.membership.util.AESUtil;
import com.example.membership.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AESUtil aesUtil;
    private final JwtUtil jwtUtil;
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        log.debug("开始处理登录请求: {}", loginRequest.getUsername());
        
        try {
            // 解密AES加密的密码
            String decryptedPassword = aesUtil.decrypt(loginRequest.getPassword());
            
            // 获取用户信息
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            
            // 验证密码
            if (!passwordEncoder.matches(decryptedPassword, userDetails.getPassword())) {
                throw new BadCredentialsException("密码错误");
            }
            
            // 生成JWT
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            log.info("用户登录成功: {}", loginRequest.getUsername());
            
            // 返回登录响应
            return LoginResponse.builder()
                    .username(loginRequest.getUsername())
                    .token(jwt)
                    .build();

        } catch (UsernameNotFoundException e) {
            log.error("用户不存在: {}", loginRequest.getUsername());
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        } catch (BadCredentialsException e) {
            log.error("密码错误: {}", loginRequest.getUsername());
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        } catch (Exception e) {
            log.error("登录过程发生异常: {}", e.getMessage());
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
    }
    
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @return 注册是否成功
     */
    @Override
    @Transactional
    public boolean changePwd(String username, String password, String confirmPassword) {
        log.debug("开始处理注册请求: {}", username);

        User user = userRepository.findByPhone(username).orElse(null);
        // 检查用户名是否已存在
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 检查两次密码是否一致
        if (!Objects.equals(password, confirmPassword)) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        
        try {
            // 解密密码
            String decryptedPassword = aesUtil.decrypt(password);
            
            // 修改用户
            user.setPassword(passwordEncoder.encode(decryptedPassword));

            userRepository.save(user);
            log.info("用户修改密码成功: {}", username);
            return true;
        } catch (Exception e) {
            log.error("修改密码过程发生异常: {}", e.getMessage());
            throw new CustomException(ErrorCode.SYSTEM_ERROR);
        }
    }
} 