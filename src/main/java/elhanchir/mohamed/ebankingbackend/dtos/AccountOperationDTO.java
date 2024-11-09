package elhanchir.mohamed.ebankingbackend.dtos;

import elhanchir.mohamed.ebankingbackend.enums.OperationType;
import lombok.Data;

import java.util.Date;

@Data
public class AccountOperationDTO {
    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;
}

