package com.devrun.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Data
public class MentoMoneyDTO {
    private String name;
    private int paidAmount;
    private String paymentDate;
    private int totalAmount;
    private int afterAmount;
    private int moneyNo;
  
}
