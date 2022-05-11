package pe.com.bank.account.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import pe.com.bank.account.dto.CurrentAccountValidateResponse;
import pe.com.bank.account.entity.AccountEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public interface AccountService {
	
	

	public Flux<AccountEntity> findAll();
	
	public Mono<AccountEntity> findById(String id);
	
	public Mono<AccountEntity> save(AccountEntity account);
	
	//public Mono<Account> savePersonalAccount(Account account);
	
	//public Mono<Account> saveEnterpriseAccount(Account account);
	
	public Mono<Void> delete(AccountEntity account);
	
	public Flux<AccountEntity> getByCustomerId(String id);

	public Mono<AccountEntity> updateAccount(AccountEntity updateAccount, String id);
	
	public Mono<CurrentAccountValidateResponse> validateCurrentAccount(String customerId,String accountId,Date date);
	
	public Flux<AccountEntity> getAccountByProductId (String productId);
		
	/*
	@Autowired
	AccountRepository accountRepository;
	
	public Flux<Account> findAllAccount(){
		return accountRepository.findAll();
	}
	*/
}
