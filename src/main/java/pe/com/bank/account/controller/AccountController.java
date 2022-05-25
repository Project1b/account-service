package pe.com.bank.account.controller;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import lombok.AllArgsConstructor;
import pe.com.bank.account.entity.AccountEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.bank.account.client.TransactionRestClient;
import pe.com.bank.account.dto.AccountTransactionDTO;
import pe.com.bank.account.dto.TransactionDTO;
import pe.com.bank.account.entity.MovementEntity;
import pe.com.bank.account.service.AccountService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class AccountController {

	private AccountService accountService;
	private TransactionRestClient transactionRestClient;
	
	@GetMapping
	public Mono<ResponseEntity<Flux<AccountEntity>>> getAccounts(){		//Listar Cuentas
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(accountService.findAll()));
	}
	
	 @GetMapping("/{id}")
	    public Mono<ResponseEntity<AccountEntity>> accountById(@PathVariable String id) {
	        return accountService.findById(id).map(p -> ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_JSON)
	                .body(p));
	   }
	
	@PostMapping
	public Mono<AccountEntity> saveAccount(@RequestBody AccountEntity account){	//Agregar nueva cuenta	
		return accountService.save(account);
	}
	

	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> deleteAccount(@PathVariable String id){	//Eliminar cuenta por Id
		return accountService.findById(id).flatMap(c -> {
			return accountService.delete(c).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<AccountEntity>> editAccount(@RequestBody AccountEntity account, @PathVariable String id) {
        return accountService.editAccount(account,id)
                .map(c -> ResponseEntity.created(URI.create("/transaction/".concat(c.getAccountNumber())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }
	
  /*  @GetMapping
    public Mono<ResponseEntity<Flux<AccountEntity>>> allAccountsList() {
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(accountService.findAll()));
    }*/

	
	@GetMapping("/productId/{id}")
	public Flux<AccountEntity> getAccountByProductId(@PathVariable String id){
		return accountService.getAccountByProductId(id);
	}
	
	/*
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Account>> editAccount(@RequestBody Account account, @PathVariable String id){
		return accountService.findById(id).flatMap(c -> {
			c.setAccountNumber(account.getAccountNumber());
			c.setAmount(account.getAmount());
			c.setAmounttype(account.getAmounttype());
			c.setDateOpen(account.getDateOpen());
			
			return accountService.save(c);
		}).map(c -> ResponseEntity.created(URI.create("/products".concat(c.getAccount_id())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(c))
				.defaultIfEmpty(ResponseEntity.notFound().build());
				
	}
	*/
	

    @PostMapping("/accounts")
    public Mono<AccountEntity> addNewAccount(@RequestBody AccountEntity account) {

        return accountService.newAccount(account);
    }

    @GetMapping("/customer/{id}")
    public Flux<AccountEntity> getAccountByCustomerId(@PathVariable String id) {
        return accountService.getByCustomerId(id);

    }

    @PutMapping("/update/{id}")
    public Mono<AccountEntity> updateAccount(@RequestBody AccountEntity account, @PathVariable String id) {
        return accountService.updateAccount(account, id);
    }

    @GetMapping("/accountsNumber/{accountNumber}")
    public Mono<AccountEntity> getAccountsByAccountNumberX(@PathVariable("accountNumber") String accountNumber) {
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

    @GetMapping("/getAccountCard")
    Flux<AccountEntity> getAccountCard(@RequestParam String cardId){
        return accountService.findAllByCardId(cardId);
    }

}
