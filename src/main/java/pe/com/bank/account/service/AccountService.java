package pe.com.bank.account.service;

import org.springframework.stereotype.Service;

import pe.com.bank.account.entity.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public interface AccountService {
	
	

	public Flux<Account> findAll();
	
	public Mono<Account> findById(String id);
	
	public Mono<Account> save(Account account);
	
	public Mono<Void> delete(Account account);
	
	public Flux<Account> findByCustomerId(String id);

	public Mono<Account> updateAccount(Account updateAccount, String id);
	
	/*
	@Autowired
	AccountRepository accountRepository;
	
	public Flux<Account> findAllAccount(){
		return accountRepository.findAll();
	}
	*/
}
