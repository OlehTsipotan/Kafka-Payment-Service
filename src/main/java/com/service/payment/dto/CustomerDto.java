package com.service.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.service.payment.entity.Customer}
 */
@Data
public class CustomerDto extends Dto implements Serializable {
    @NotNull(message = "Customer id must not be null") @PositiveOrZero Long id;
    @NotNull(message = "Customer name must not be null") @NotBlank String name;
    @NotNull(message = "Customer balanceAvailable must not be null") @PositiveOrZero Long balanceAvailable;
    @NotNull(message = "Customer balanceReserved must not be null") @PositiveOrZero Long balanceReserved;
}