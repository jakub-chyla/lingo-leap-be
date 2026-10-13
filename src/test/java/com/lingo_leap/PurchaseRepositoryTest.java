package com.lingo_leap;

import com.lingo_leap.model.Purchase;
import com.lingo_leap.repository.PurchaseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class PurchaseRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Test
    void shouldFindLatestPurchaseByUserId() {
        Purchase p1 = new Purchase();
        p1.setUserId(1L);
        purchaseRepository.save(p1);

        Purchase p2 = new Purchase();
        p2.setUserId(1L);
        purchaseRepository.save(p2);

        Optional<Purchase> result =
                purchaseRepository
                        .findFirstByUserIdOrderByCreatedDesc(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(p2.getId());
    }
}