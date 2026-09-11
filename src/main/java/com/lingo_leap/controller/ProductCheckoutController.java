package com.lingo_leap.controller;

import com.lingo_leap.dto.ProductRequest;
import com.lingo_leap.dto.StripeResponse;
import com.lingo_leap.service.PurchaseService;
import com.lingo_leap.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/purchase")
public class ProductCheckoutController {

    private final StripeService stripeService;
    private final PurchaseService purchaseService;

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody ProductRequest productRequest) {
        var stripeResponse = stripeService.checkoutProducts(productRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }

    @GetMapping("/premium/{userId}")
    public ResponseEntity<Boolean> isPremium(@PathVariable Long userId) {
        var isPremium = purchaseService.isPremiumByLogin(userId);
        return ResponseEntity.ok(isPremium);
    }
}
