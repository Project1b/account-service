package pe.com.bank.account.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	// --- CRUD - INI
	
	@GetMapping
	public Mono<ResponseEntity<Flux<Account>>> AllAccountsList(){	
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(accountService.findAll()));
	}
	
	/*
	@GetMapping("/accounts")
	public Mono<ResponseEntity<Flux<Account>>> getAccountsList(){

		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(accountService.getAccounts()));
	}	*/
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Account>> AccountById(@PathVariable String id){	
		return accountService.findById(id).map(p -> ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(p));
	}
	
	/*
	@GetMapping("/accounts/{id}")
	public Mono<ResponseEntity<Account>> getAccountById(@PathVariable String id){

		return accountService.getAccountById(id).map(p -> ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(p)).defaultIfEmpty(ResponseEntity.notFound().build());
	}
	*/
	
	@PostMapping("/accounts")
	public Mono<Account> addNewAccount(@RequestBody Account account){	

		return accountService.newAccount(account);
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> deleteAccount(@PathVariable String id){
		return accountService.findById(id).flatMap(c -> {
			return accountService.delete(c).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}
	
	/*
	@DeleteMapping("/accounts/{id}")
	public Mono<Void> deleteAccountById(@PathVariable String id){

		return accountService.deleteAccountById(id);
	}	*/
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Account>> editAccount(@RequestBody Account account, @PathVariable String id){	//ERROR
		return accountService.findById(id).flatMap(c -> {
			c.setAccountNumber(account.getAccountNumber());
			c.setAmount(account.getAmount());
			c.setAmounttype(account.getAmounttype());
			c.setDateOpen(account.getDateOpen());
			
			return accountService.save(c);
		    }).map(c -> ResponseEntity.created(URI.create("/transaction/".concat(c.getAccountNumber())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(c))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}
	
	@GetMapping("/customer/{id}")
	public Flux<Account> getAccountByCustomerId(@PathVariable String id){
		return accountService.findByCustomerId(id);
		
	}
	
	@PutMapping("/update/{id}")
	public Mono<Account> updateAccount (@RequestBody Account account,@PathVariable String id){
		return accountService.updateAccount(account,id);
	}
	
	@GetMapping("/accountsNumber/{accountNumber}")
	public Mono<Account> getAccountsByAccountNumberX(@PathVariable("accountNumber") String accountNumber){
		return accountService.getAccountByAccountNum(accountNumber);
	}
	
	@GetMapping("/accountTransactions/{id}")
	public Mono<AccountTransactionDTO> retrieveAccountAndTransactionsByAccountId(@PathVariable("id") String accountId) {	// ERROR

		return accountService.getAccountById(accountId).flatMap(account -> {
			return transactionRestClient.retrieveTransaction(account.getAccountNumber()).collectList().map(a ->
					new AccountTransactionDTO(
							account.getId(),
							account.getAccountNumber(),
							account.getAmount(),
							account.getDateOpen(),
							account.getAmounttype(),
							a
					));
		});
	}
	
	@PostMapping("/updateAmountRest")
	public Mono<TransactionDTO> updateRestAmountByAccountId(@RequestBody MovementEntity movEntity){	//ERROR AL INSERTAR A TRANSACTION

		return accountService.getAccountById(movEntity.getAccount_id()).flatMap(crc -> {
			var r = accountService.updateAccount(new Account(	crc.getId(),
					crc.getAccountNumber(),
					crc.getAmount() - movEntity.getAmount(),
					crc.getDateOpen(),
					crc.getAmounttype(),crc.getProductId(),
					crc.getCustomerId()), movEntity.getAccount_id());

			return r.flatMap( dsf -> {
				var r2 = transactionRestClient.createTransactionUpdate(new TransactionDTO(	
						movEntity.getAmount(),
						movEntity.getDate(),
						movEntity.getType(),
						movEntity.getAccount_id()));

				return r2.map( sd -> new TransactionDTO(
						movEntity.getAmount(),
						movEntity.getDate(),
						movEntity.getType(),
						movEntity.getAccount_id()));


			});

		});

	}

	// Actualizar ammount : Deposito

	@PostMapping("/updateAmountSum")
	public Mono<TransactionDTO> updateSumAmountByAccountId(@RequestBody MovementEntity movEntity){	//ERROR AL INSERTAR A TRANSACTION

		return accountService.getAccountById(movEntity.getAccount_id()).flatMap(crc -> {
			var r = accountService.updateAccount(new Account(	crc.getId(),
					crc.getAccountNumber(),
					crc.getAmount() + movEntity.getAmount(),
					crc.getDateOpen(),
					crc.getAmounttype(),crc.getProductId(),
					crc.getCustomerId()), movEntity.getAccount_id());
			return r.flatMap( dsf -> {
				var r2 = transactionRestClient.createTransactionUpdate(new TransactionDTO(	
						movEntity.getAmount(),
						movEntity.getDate(),
						movEntity.getType(),
						movEntity.getAccount_id()));

				return r2.map( sd -> new TransactionDTO(
						movEntity.getAmount(),
						movEntity.getDate(),
						movEntity.getType(),
						movEntity.getAccount_id()));

			});

		});

	}
	
	/*
	@PostMapping
	public Mono<Account> agregarAccount(@RequestBody Account account){	//Agregar nueva cuenta	
		return accountService.save(account);
	}
	*/
	
	/*
	@PostMapping
	public Mono<ResponseEntity<Account>> crearAccount(@RequestBody Account account){
		if(account.getDateOpen()==null) {
			account.setDateOpen(new Date());
		}
		
		return accountService.save(account).map(c -> ResponseEntity
				.created(URI.create("productos".concat(c.getAccount_id())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(c));
				
	} */


}
