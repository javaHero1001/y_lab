package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    private Long id;
    private Long userId;
    private double amount;
    private YearMonth period;

}
