package com.example.membership.controller;

import com.example.membership.model.dto.ApiResponse;
import com.example.membership.model.dto.TransactionDTO;
import com.example.membership.model.dto.UserInfoDTO;
import com.example.membership.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取用户信息
     * @return 用户信息
     */
    @GetMapping("/user/info")
    public ApiResponse<UserInfoDTO> getUserInfo(HttpServletRequest request) {
        // 打印请求头
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.info(headerName + ": " + headerValue);
        }

        return ApiResponse.success(userService.getCurrentUserInfo());
    }
    
    /**
     * 获取用户消费记录
     * @return 消费记录列表
     */
    @GetMapping("/records/consumption")
    public ResponseEntity<List<TransactionDTO>> getConsumptionRecords() {
        return ResponseEntity.ok(userService.getUserConsumptionRecords());
    }
    
    /**
     * 获取用户充值记录
     * @return 充值记录列表
     */
    @GetMapping("/records/recharge")
    public ResponseEntity<List<TransactionDTO>> getRechargeRecords() {
        return ResponseEntity.ok(userService.getUserRechargeRecords());
    }
} 