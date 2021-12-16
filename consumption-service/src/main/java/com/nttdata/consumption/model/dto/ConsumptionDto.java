package com.nttdata.consumption.model.dto;

import lombok.Data;

@Data
public class ConsumptionDto {
    private String cardNumber;
    private double amount;
    private String concept;
}
