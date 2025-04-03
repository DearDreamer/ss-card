package com.example.membership.service.impl;

import com.example.membership.model.dto.TransactionDTO;
import com.example.membership.model.dto.UserInfoDTO;
import com.example.membership.model.entity.Transaction;
import com.example.membership.model.entity.User;
import com.example.membership.repository.TransactionRepository;
import com.example.membership.repository.UserRepository;
import com.example.membership.service.UserService;
import com.example.membership.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("0.00");
    
    /**
     * 获取当前登录用户信息
     * @return 用户信息DTO
     */
    @Override
    public UserInfoDTO getCurrentUserInfo() {
        User user = SecurityUtil.getCurrentUser();;

        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .phone(user.getPhone())
                .cardType(user.getCardType())
                .balance(DECIMAL_FORMATTER.format(user.getBalance()))
                .lastVisit(user.getLastVisit() != null ? user.getLastVisit().format(DATE_FORMATTER) : "暂无记录")
                .build();

        log.info(userInfoDTO.toString());
        return userInfoDTO;
    }
    
    /**
     * 获取用户消费记录
     * @return 消费记录列表
     */
    @Override
    public List<TransactionDTO> getUserConsumptionRecords() {
        User user = SecurityUtil.getCurrentUser();
        List<Transaction> transactions = transactionRepository.findByUserAndTypeOrderByTimeDesc(user, Transaction.TransactionType.CONSUMPTION);
        
        return transactions.stream()
                .map(transaction -> TransactionDTO.builder()
                        .time(transaction.getTime().format(DATE_FORMATTER))
                        .amount(DECIMAL_FORMATTER.format(transaction.getAmount()))
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户充值记录
     * @return 充值记录列表
     */
    @Override
    public List<TransactionDTO> getUserRechargeRecords() {
        User user = SecurityUtil.getCurrentUser();
        List<Transaction> transactions = transactionRepository.findByUserAndTypeOrderByTimeDesc(user, Transaction.TransactionType.RECHARGE);
        
        return transactions.stream()
                .map(transaction -> TransactionDTO.builder()
                        .time(transaction.getTime().format(DATE_FORMATTER))
                        .amount(DECIMAL_FORMATTER.format(transaction.getAmount()))
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户实体
     */
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

} 