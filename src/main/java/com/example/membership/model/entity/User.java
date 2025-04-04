package com.example.membership.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@Entity
@DynamicUpdate
@NoArgsConstructor
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username; // 用户名，即手机号
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "phone_number", length = 20)
    private String phone;
    
    @Column(name = "card_type", length = 20)
    private String cardType; // 会员卡类型：普通会员、黄金会员等
    
    @Column(precision = 10, scale = 2)
    private BigDecimal balance; // 卡内余额

     @Column(precision = 10, scale = 2)
    private BigDecimal totalRecharge; // 总充值金额

     @Column(precision = 10, scale = 2)
    private BigDecimal totalSpent; // 总消费金额
    
    @Column(name = "last_visit")
    private LocalDateTime lastVisit; // 上次到店时间
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 