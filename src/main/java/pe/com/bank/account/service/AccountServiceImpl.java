package pe.com.bank.account.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import pe.com.bank.account.entity.Account;
import pe.com.bank.account.repository.AccountRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class AccountServiceImpl implements AccountService{
	
    AccountRepository accountRepository;
    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

	public Flux<Account> findAll() {
		
		return accountRepository.findAll();
	}

	public Mono<Account> findById(String id) {
		
		return accountRepository.findById(id);
	}

	public Mono<Account> save(Account account) {
		
		return accountRepository.countByCustomerIdAndProductId(account.getCustomerId(),
				account.getProductId()).flatMap( count -> {
				System.out.println("count: "+count.longValue());
				return count.longValue() > 0 ? Mono.just(new Account()):accountRepository.save(account);
				});
	}

	
	public Mono<Void> delete(Account account) {
		
		return accountRepository.delete(account);
	}

	public Flux<Account> findByCustomerId(String id) {
		
		return accountRepository.findByCustomerId(id);
		
	}

}
