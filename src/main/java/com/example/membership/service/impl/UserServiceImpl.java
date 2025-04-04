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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
        // 1. 获取当前用户
        User user = SecurityUtil.getCurrentUser();
        if (user == null) {
            log.warn("未获取到当前登录用户");
            return Collections.emptyList(); // 返回空列表而不是抛出异常
        }

        // 2. 创建分页查询条件(前10条，按时间倒序)
        Pageable topTen = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "time"));

        // 3. 查询消费记录
        Page<Transaction> transactions = transactionRepository.findByUser_IdAndType(
                user.getId(),
                Transaction.TransactionType.CONSUMPTION,
                topTen
        );

        // 4. 打印调试信息
        log.debug("用户[{}]查询到{}条消费记录", user.getId(), transactions.getNumberOfElements());
        transactions.forEach(t ->
                log.trace("记录ID[{}], 金额[{}], 时间[{}]",
                        t.getId(), t.getAmount(), t.getTime())
        );

        // 5. 转换为DTO列表
        return transactions.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .time(transaction.getTime().format(DATE_FORMATTER))
                .amount(DECIMAL_FORMATTER.format(transaction.getAmount()))
                .build();
    }
    
    /**
     * 获取用户充值记录
     * @return 充值记录列表
     */
    @Override
    public List<TransactionDTO> getUserRechargeRecords() {
        // 获取当前用户
        User user = SecurityUtil.getCurrentUser();
        log.info(user.toString());
        if (user == null) {
            log.warn("未获取到当前登录用户");
            return Collections.emptyList(); // 返回空列表而不是抛出异常
        }

        // 创建分页查询条件(前10条，按时间倒序)
        Pageable topTen = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "time"));

        // 查询充值记录
        Page<Transaction> transactions = transactionRepository.findByUser_IdAndType(
                user.getId(),
                Transaction.TransactionType.RECHARGE,
                topTen
        );

        // 打印调试信息
        log.debug("用户[{}]查询到{}条充值记录", user.getId(), transactions.getNumberOfElements());
        transactions.forEach(t ->
                log.trace("记录ID[{}], 金额[{}], 时间[{}]",
                        t.getId(), t.getAmount(), t.getTime())
        );

        // 转换为DTO列表
        return transactions.getContent().stream()
                .map(this::convertToDTO)
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