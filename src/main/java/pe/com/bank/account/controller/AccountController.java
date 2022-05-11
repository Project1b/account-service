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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.bank.account.entity.Account;
import pe.com.bank.account.service.AccountService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	@GetMapping
	public Mono<ResponseEntity<Flux<Account>>> listarAccounts(){		//Listar Cuentas
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(accountService.findAll()));
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Account>> listarDetalle(@PathVariable String id){	//Listar Cuenta por Id
		return accountService.findById(id).map(p -> ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(p)).defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	public Mono<Account> agregarAccount(@RequestBody Account account){	//Agregar nueva cuenta	
		return accountService.save(account);
	}
	
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
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> deleteAccount(@PathVariable String id){	//Eliminar cuenta por Id
		return accountService.findById(id).flatMap(c -> {
			return accountService.delete(c).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}


	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Account>> editAccount(@RequestBody Account account, @PathVariable String id){	//Falta probar
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
	

	
	/*
	@GetMapping("/listAccount")
	public Flux<Account> findAllAccount(){
		return accountService.findAllAccount();
	}
	*/
	
}
