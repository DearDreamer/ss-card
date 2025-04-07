package com.example.membership.controller;

import com.example.membership.model.dto.ApiResponse;
import com.example.membership.model.dto.LoginRequest;
import com.example.membership.model.dto.LoginResponse;
import com.example.membership.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", maxAge = 3600)
@Tag(name = "用户认证接口", description = "包含登录、注册等认证相关接口")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用手机号和密码进行登录")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = authService.login(loginRequest);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", loginResponse.getUsername());
            userData.put("username", loginResponse.getUsername());
            
            // 使用符合API文档的响应格式
            ApiResponse<Map<String, Object>> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("登录成功");
            response.setStatusCode(200);
            response.setData(userData);
            response.setToken(loginResponse.getToken());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 用户修改密码
     * @param request 请求
     * @return 结果
     */
    @PostMapping("/changePwd")
    @Operation(summary = "用户修改密码", description = "修改密码")
    public ApiResponse<Void> register(@Valid @RequestBody LoginRequest request) {
        try {
            authService.changePwd(request.getUsername(), request.getPassword(), request.getPassword());
            
            ApiResponse<Void> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("修改密码成功");
            response.setStatusCode(200);
            
            return response;
        } catch (Exception e) {
            log.error("修改密码失败: {}", e.getMessage());
            
            ApiResponse<Void> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            response.setStatusCode(200);
            
            return response;
        }
    }
    
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<?> options() {
        return ResponseEntity.ok().build();
    }
} 