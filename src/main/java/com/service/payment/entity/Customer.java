package com.service.payment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@Entity
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_generator")
    @SequenceGenerator(name = "customer_generator", sequenceName = "customer_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Customer name must not be null")
    @NotBlank(message = "Customer name must not be blank")
    @Column(name = "name", nullable = false)
    private String name;

    @PositiveOrZero(message = "Customer balanceAvailable must be positive or zero")
    @NotNull(message = "Customer balanceAvailable must not be null")
    @Column(name = "balance_available", nullable = false)
    private Long balanceAvailable;

    @PositiveOrZero(message = "Customer balanceReserved must be positive or zero")
    @NotNull(message = "Customer balanceReserved must not be null")
    @Column(name = "balance_reserved", nullable = false)
    private Long balanceReserved;

}