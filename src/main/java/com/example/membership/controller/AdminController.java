package com.example.membership.controller;

import com.example.membership.model.dto.ApiResponse;
import com.example.membership.model.dto.admin.MemberRegisterRequest;
import com.example.membership.model.dto.admin.TransactionRequest;
import com.example.membership.model.entity.User;
import com.example.membership.service.AdminService;
import com.example.membership.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "管理员接口", description = "提供会员管理和交易处理等功能")
public class AdminController {

    private final AdminService adminService;

    /**
     * 注册新会员
     */
    @PostMapping("/member/register")
    @Operation(summary = "注册新会员", description = "管理员注册新会员")
    public ApiResponse<User> registerMember(@Valid @RequestBody MemberRegisterRequest request) {
        if(!("18856436601").equals(SecurityUtil.getCurrentUser().getUsername())){
            log.warn("非管理员");
            return ApiResponse.error("非管理员无法操作", 404);
        }
        try {
            User member = adminService.registerMember(request);
            return ApiResponse.success("会员注册成功", member);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage(), 400);
        } catch (Exception e) {
            return ApiResponse.error("注册失败: " + e.getMessage(), 500);
        }
    }

    /**
     * 查询会员信息
     */
    @GetMapping("/member/info")
    @Operation(summary = "查询会员信息", description = "根据手机号查询会员信息")
    public ApiResponse<User> getMemberInfo(@RequestParam String phone) {
        if(!("18856436601").equals(SecurityUtil.getCurrentUser().getUsername())){
            log.warn("非管理员");
            return ApiResponse.error("非管理员无法操作", 404);
        }
        try {
            User member = adminService.getMemberInfo(phone);
            return ApiResponse.success("查询成功", member);
        } catch (Exception e) {
            if (e instanceof javax.persistence.EntityNotFoundException) {
                return ApiResponse.error("会员不存在", 404);
            }
            return ApiResponse.error("查询失败: " + e.getMessage(), 500);
        }
    }

    /**
     * 处理交易
     */
    @PostMapping("/transaction/process")
    @Operation(summary = "处理交易", description = "会员账户充值或消费")
    public ApiResponse<User> processTransaction(@Valid @RequestBody TransactionRequest request) {
        if(!("18856436601").equals(SecurityUtil.getCurrentUser().getUsername())){
            log.warn("非管理员");
            return ApiResponse.error("非管理员无法操作", 404);
        }
        try {
            User user = adminService.processTransaction(request);
            return ApiResponse.success("处理成功", user);
        } catch (IllegalStateException e) {
            return ApiResponse.error("余额不足", 400);
        } catch (Exception e) {
            if (e instanceof javax.persistence.EntityNotFoundException) {
                return ApiResponse.error("会员不存在", 404);
            }
            return ApiResponse.error("处理失败: " + e.getMessage(), 500);
        }
    }
} 