package com.lingo_leap.service;

import com.lingo_leap.model.Purchase;
import com.lingo_leap.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    public static final int PREMIUM_DAYS = 7;
    private final PurchaseRepository purchaseRepository;

    //TODO test
    public Boolean isPremiumByLogin(Long userId) {
        var purchasesOptional = purchaseRepository.findFirstByUserIdOrderByCreatedDesc(userId);
        if (!purchasesOptional.isPresent()) {
            return false;
        } else {
            var expiryDate = purchasesOptional.get().getCreated().plusDays(PREMIUM_DAYS);
            return LocalDateTime.now().isBefore(expiryDate);
        }
    }

    public void buy(Long userId) {
        var purchase = new Purchase();
        purchase.setUserId(userId);
        purchaseRepository.save(purchase);
    }

}