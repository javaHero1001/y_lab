package org.example.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.example.model.Transaction;

@Data
public class TransactionRepository {
    private Map <Long,Transaction> transactions = new HashMap<>();
    private Long transactionId=0L;

    public Transaction save(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        if(transaction.getId()==null){
            transaction.setId(++transactionId);
        }
        transactions.put(transaction.getId(),transaction);
        return transaction;
    }

    public Transaction findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return transactions.get(id);
    }

    public List<Transaction> findByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return transactions.values().stream()
                .filter(t -> t.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        transactions.remove(id);
    }
}
