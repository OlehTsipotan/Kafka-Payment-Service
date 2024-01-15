package com.service.payment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class Product {

    @NotNull(message = "Product ID cannot be null")
    private Long id;

    @Positive(message = "Product quantity must be greater than 0")
    private Integer quantity;

    @Min(value = 0, message = "Price cannot be negative")
    private Long price;

    @JsonIgnore
    public Long getTotalPrice() {
        return price * quantity;
    }
}
