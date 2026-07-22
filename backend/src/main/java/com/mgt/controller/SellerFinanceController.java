package com.mgt.controller;
import com.mgt.model.*;import com.mgt.service.SellerWalletService;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.http.ResponseEntity;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/finance") @CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class SellerFinanceController {
 @Autowired private SellerWalletService service;
 @GetMapping("/seller/{sellerId}/wallet") public ResponseEntity<SellerWallet> wallet(@PathVariable long sellerId){return ResponseEntity.ok(service.get(sellerId));}
 @GetMapping("/seller/{sellerId}/transactions") public List<WalletTransaction> tx(@PathVariable long sellerId){return service.transactions(sellerId);}
}
