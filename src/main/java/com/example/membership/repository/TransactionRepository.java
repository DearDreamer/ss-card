package com.example.membership.repository;

import com.example.membership.model.entity.Transaction;
import com.example.membership.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 交易记录数据仓库接口
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * 根据用户和交易类型查询交易记录（分页）
     * @param id 用户ID
     * @param type 交易类型
     * @param pageable 分页参数
     * @return 交易记录分页
     */
    Page<Transaction> findByUser_IdAndType(Long id, Transaction.TransactionType type, Pageable pageable);
    
    /**
     * 根据用户和交易类型查询所有交易记录
     * @param user 用户
     * @param type 交易类型
     * @return 交易记录列表
     */
    List<Transaction> findByUserAndTypeOrderByTimeDesc(User user, Transaction.TransactionType type);
    
    /**
     * 根据用户查询所有交易记录
     * @param user 用户
     * @return 交易记录列表
     */
    List<Transaction> findByUserOrderByTimeDesc(User user);
} 