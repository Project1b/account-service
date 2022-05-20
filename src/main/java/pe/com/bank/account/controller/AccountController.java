package pe.com.bank.account.controller;

import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.bank.account.client.TransactionRestClient;
import pe.com.bank.account.dto.AccountTransactionDTO;
import pe.com.bank.account.dto.TransactionDTO;
import pe.com.bank.account.entity.Account;
import pe.com.bank.account.entity.MovementEntity;
import pe.com.bank.account.service.AccountService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    TransactionRestClient transactionRestClient;


    @GetMapping
    public Mono<ResponseEntity<Flux<Account>>> allAccountsList() {
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(accountService.findAll()));
    }


    @GetMapping("/{id}")
    public Mono<ResponseEntity<Account>> accountById(@PathVariable String id) {
        return accountService.findById(id).map(p -> ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(p));
    }


    @PostMapping("/accounts")
    public Mono<Account> addNewAccount(@RequestBody Account account) {

        return accountService.newAccount(account);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAccount(@PathVariable String id) {
        return accountService.findById(id).flatMap(c -> {
            return accountService.delete(c).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
        }).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }


    @PutMapping("/{id}")
    public Mono<ResponseEntity<Account>> editAccount(@RequestBody Account account, @PathVariable String id) {
        return accountService.editAccount(account,id)
                .map(c -> ResponseEntity.created(URI.create("/transaction/".concat(c.getAccountNumber())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @GetMapping("/customer/{id}")
    public Flux<Account> getAccountByCustomerId(@PathVariable String id) {
        return accountService.findByCustomerId(id);

    }

    @PutMapping("/update/{id}")
    public Mono<Account> updateAccount(@RequestBody Account account, @PathVariable String id) {
        return accountService.updateAccount(account, id);
    }

    @GetMapping("/accountsNumber/{accountNumber}")
    public Mono<Account> getAccountsByAccountNumberX(@PathVariable("accountNumber") String accountNumber) {
        return accountService.getAccountByAccountNum(accountNumber);
    }

    @GetMapping("/accountTransactions/{id}")
    public Mono<AccountTransactionDTO> retrieveAccountAndTransactionsByAccountId(@PathVariable("id") String accountId) {
        return accountService.retrieveAccountAndTransactionsByAccountId(accountId);
    }

    @PostMapping("/updateAmountRest")
    public Mono<TransactionDTO> updateRestAmountByAccountId(@RequestBody MovementEntity movEntity) {
        return accountService.updateRestAmountByAccountId(movEntity);
    }

    @PostMapping("/updateAmountSum")
    public Mono<TransactionDTO> updateSumAmountByAccountId(@RequestBody MovementEntity movEntity) {
        return accountService.updateSumAmountByAccountId(movEntity);
    }

    @GetMapping("/count")
    Mono<Long> retornaCount(@RequestParam(name = "accountId") String accountId, @RequestParam String typ) {
        return transactionRestClient.contTransactionByType(typ, accountId);
    }

}
