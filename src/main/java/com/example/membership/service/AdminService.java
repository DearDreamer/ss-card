package com.example.membership.service;

import com.example.membership.model.dto.admin.MemberRegisterRequest;
import com.example.membership.model.dto.admin.TransactionRequest;
import com.example.membership.model.entity.Transaction;
import com.example.membership.model.entity.User;

/**
 * 管理员服务接口
 */
public interface AdminService {
    
    /**
     * 注册新会员
     * @param request 注册请求
     * @return 注册成功的用户
     */
    User registerMember(MemberRegisterRequest request);
    
    /**
     * 查询会员信息
     * @param phone 手机号
     * @return 会员信息
     */
    User getMemberInfo(String phone);
    
    /**
     * 处理交易（充值或消费）
     * @param request 交易请求
     * @return 交易结果
     */
    User processTransaction(TransactionRequest request);
} 