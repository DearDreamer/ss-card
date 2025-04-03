package com.example.membership.service;

import com.example.membership.model.dto.TransactionDTO;
import com.example.membership.model.dto.UserInfoDTO;
import com.example.membership.model.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 获取当前登录用户信息
     * @return 用户信息DTO
     */
    UserInfoDTO getCurrentUserInfo();
    
    /**
     * 获取用户消费记录
     * @return 消费记录列表
     */
    List<TransactionDTO> getUserConsumptionRecords();
    
    /**
     * 获取用户充值记录
     * @return 充值记录列表
     */
    List<TransactionDTO> getUserRechargeRecords();
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户实体
     */
    User findByUsername(String username);
} 