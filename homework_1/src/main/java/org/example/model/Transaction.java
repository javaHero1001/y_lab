package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Long id;
    private Long userId;
    private double amount;
    private String category;
    private String description;
    private LocalDateTime date;
    private TransactionType type; // Добавляем поле типа TransactionType
}