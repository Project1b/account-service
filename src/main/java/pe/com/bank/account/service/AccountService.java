package pe.com.bank.account.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import pe.com.bank.account.dto.CurrentAccountValidateResponse;
import pe.com.bank.account.entity.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public interface AccountService {
	
	

	public Flux<Account> findAll();
	
	public Mono<Account> findById(String id);
	
	public Mono<Account> save(Account account);
	
	//public Mono<Account> savePersonalAccount(Account account);
	
	//public Mono<Account> saveEnterpriseAccount(Account account);
	
	public Mono<Void> delete(Account account);
	
	public Flux<Account> findByCustomerId(String id);

	public Mono<Account> updateAccount(Account updateAccount, String id);
	
	public Mono<CurrentAccountValidateResponse> validateCurrentAccount(String customerId,String accountId,Date date);
		
	/*
	@Autowired
	AccountRepository accountRepository;
	
	public Flux<Account> findAllAccount(){
		return accountRepository.findAll();
	}
	*/
}
