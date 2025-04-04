package com.example.membership.service.impl;

import com.example.membership.model.dto.admin.MemberRegisterRequest;
import com.example.membership.model.dto.admin.TransactionRequest;
import com.example.membership.model.entity.Transaction;
import com.example.membership.model.entity.User;
import com.example.membership.repository.TransactionRepository;
import com.example.membership.repository.UserRepository;
import com.example.membership.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员服务实现类
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerMember(MemberRegisterRequest request) {
        // 验证手机号是否一致
        if (!request.getPhone().equals(request.getConfirmPhone())) {
            throw new IllegalArgumentException("两次输入的手机号不一致");
        }

        // 检查手机号是否已存在
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("该手机号已注册");
        }

        // 创建新会员
        User user = new User();
        user.setUsername(request.getPhone()); // 使用手机号作为用户名
        user.setPhone(request.getPhone());
        // 设置一个默认密码，后续可以修改
        user.setPassword(passwordEncoder.encode(request.getPhone().substring(request.getPhone().length() - 6)));
        user.setBalance(request.getInitialAmount());
        user.setTotalRecharge(request.getInitialAmount());
        user.setTotalSpent(BigDecimal.ZERO);
        user.setLastVisit(LocalDateTime.now());
        user.setCardType("普通会员");

        return userRepository.save(user);
    }

    @Override
    public User getMemberInfo(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new EntityNotFoundException("会员不存在"));
    }

    @Override
    @Transactional
    public User processTransaction(TransactionRequest request) {
        // 查找用户
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new EntityNotFoundException("会员不存在"));

        // 根据交易类型处理余额
        switch (request.getType()) {
            case RECHARGE:
                user.setBalance(user.getBalance().add(request.getAmount()));
                user.setTotalRecharge(user.getTotalRecharge().add(request.getAmount()));
                break;
            case CONSUMPTION:
                if (user.getBalance().compareTo(request.getAmount()) < 0) {
                    throw new IllegalStateException("余额不足");
                }
                user.setBalance(user.getBalance().subtract(request.getAmount()));
                user.setTotalSpent(user.getTotalSpent().add(request.getAmount()));
                break;
            default:
                throw new IllegalArgumentException("不支持的交易类型");
        }

        // 记录交易
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setPhone_number(request.getPhone());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setTime(LocalDateTime.now());
        transaction.setDescription(request.getType() == Transaction.TransactionType.RECHARGE ? "管理员充值" : "管理员消费");
        
        // 保存交易记录
        transactionRepository.save(transaction);
        
        // 更新用户最后访问时间
        user.setLastVisit(LocalDateTime.now());
        
        return userRepository.save(user);
    }
} 