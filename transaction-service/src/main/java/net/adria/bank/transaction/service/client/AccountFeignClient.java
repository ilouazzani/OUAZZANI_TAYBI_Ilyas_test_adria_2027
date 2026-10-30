package net.adria.bank.transaction.service.client;

import net.adria.bank.transaction.service.dto.AccountBalanceUpdateRequestDTO;
import net.adria.bank.transaction.service.dto.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "account-service", url = "${account-service.url:http://localhost:8081}", fallback = AccountFeignFallback.class)
public interface AccountFeignClient {

    @GetMapping("/api/accounts/{id}")
    AccountDTO getAccountById(@PathVariable("id") UUID id);

    @PatchMapping("/api/accounts/{id}/balance")
    AccountDTO updateBalance(@PathVariable("id") UUID id, @RequestBody AccountBalanceUpdateRequestDTO request);
}
