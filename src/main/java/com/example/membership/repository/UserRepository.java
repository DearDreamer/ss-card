package com.example.membership.repository;

import com.example.membership.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据仓库接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
    
    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户信息
     */
    @Query("SELECT u FROM User u WHERE u.phone = :phone")
    Optional<User> findByPhone(@Param("phone") String phone);
    
    /**
     * 检查手机号是否已存在
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);
} 