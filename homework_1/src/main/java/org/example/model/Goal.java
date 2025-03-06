package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Goal {

    private Long id;
    private Long userId;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private LocalDate deadline;

}
