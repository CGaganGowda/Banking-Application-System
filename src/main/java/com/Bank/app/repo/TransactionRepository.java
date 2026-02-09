package com.Bank.app.repo;

import com.Bank.app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>
{
    List<Transaction> findByAccountIdOrderByTimestampDesc(Long accountId);
}
