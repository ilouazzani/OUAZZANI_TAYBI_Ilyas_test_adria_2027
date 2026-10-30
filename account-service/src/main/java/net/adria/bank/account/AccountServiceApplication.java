package net.adria.bank.account;

import net.adria.bank.account.domain.AccountStatus;
import net.adria.bank.account.service.AccountService;
import net.adria.bank.account.service.dto.AccountRequestDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(AccountService accountService) {
        return args -> {
            accountService.createAccount(AccountRequestDTO.builder()
                    .customerReference("CUST-1001")
                    .currency("EUR")
                    .balance(5000.0)
                    .status(AccountStatus.ACTIVE)
                    .build());

            accountService.createAccount(AccountRequestDTO.builder()
                    .customerReference("CUST-1002")
                    .currency("EUR")
                    .balance(1200.0)
                    .status(AccountStatus.ACTIVE)
                    .build());

            accountService.createAccount(AccountRequestDTO.builder()
                    .customerReference("CUST-1003")
                    .currency("USD")
                    .balance(350.0)
                    .status(AccountStatus.BLOCKED)
                    .build());
        };
    }
}
