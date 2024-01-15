package com.service.payment;

import com.service.payment.entity.Customer;
import com.service.payment.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentServiceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

    @Autowired
    CustomerService customerService;

    @Override
    public void run(String... args) throws Exception {
        customerService.save(
                Customer.builder()
                        .name("Oleh")
                        .balanceAvailable(1000L)
                        .balanceReserved(0L)
                .build());
    }
}
