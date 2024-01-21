package com.service.payment.repository;

import com.service.payment.entity.Customer;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CustomerRepositoryTest {

    private static final String DATABASE_NAME = "databaseName";
    private static final String DATABASE_USERNAME = "databaseName";
    private static final String DATABASE_USER_PASSWORD = "databaseName";

    public static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:latest").withDatabaseName(DATABASE_NAME).withUsername(DATABASE_USERNAME)
                    .withPassword(DATABASE_USER_PASSWORD).withReuse(true);
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    TestEntityManager entityManager;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // Postgresql
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Flyway
        registry.add("spring.flyway.cleanDisabled", () -> false);
    }

    @BeforeEach
    void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void save_success() {
        Customer customer = new Customer();
        customer.setName("test");
        customer.setBalanceAvailable(1000L);
        customer.setBalanceReserved(0L);

        customerRepository.save(customer);

        Customer customerFromDb = entityManager.find(Customer.class, customer.getId());
        assertEquals(customer, customerFromDb);
        assertEquals(customer.getName(), customerFromDb.getName());
    }

    @Test
    public void findById_success() {
        Customer customer = new Customer();
        customer.setName("test");
        customer.setBalanceAvailable(1000L);
        customer.setBalanceReserved(0L);

        entityManager.persist(customer);

        Customer customerFromDb = customerRepository.findById(customer.getId()).orElse(null);

        assertNotNull(customerFromDb);
        assertEquals(customer, customerFromDb);
    }

    @Test
    public void deleteById_success() {
        Customer customer = new Customer();
        customer.setName("test");
        customer.setBalanceAvailable(1000L);
        customer.setBalanceReserved(0L);

        entityManager.persist(customer);

        customerRepository.deleteById(customer.getId());

        Customer customerFromDb = entityManager.find(Customer.class, customer.getId());
        assertNull(customerFromDb);
        assertEquals(0, customerRepository.findAll().size());
    }

    @Test
    public void delete_success() {
        Customer customer = new Customer();
        customer.setName("test");
        customer.setBalanceAvailable(1000L);
        customer.setBalanceReserved(0L);

        entityManager.persist(customer);

        customerRepository.delete(customer);

        Customer customerFromDb = entityManager.find(Customer.class, customer.getId());
        assertNull(customerFromDb);
        assertEquals(0, customerRepository.findAll().size());
    }
}
