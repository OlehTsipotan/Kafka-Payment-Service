package com.service.payment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Customer balanceAvailable must not be null")
    @Column(name = "balance_available", nullable = false)
    private Long balanceAvailable;

    @NotNull(message = "Customer balanceReserved must not be null")
    @Column(name = "balance_reserved", nullable = false)
    private Long balanceReserved;

}