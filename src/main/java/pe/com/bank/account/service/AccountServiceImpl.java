package pe.com.bank.account.service;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import pe.com.bank.account.client.CustomerRestClient;
import pe.com.bank.account.entity.Account;
import pe.com.bank.account.repository.AccountRepository;
import pe.com.bank.account.util.AccountConstant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

	AccountConstant accountConstant;
	CustomerRestClient customerRestClient;
    AccountRepository accountRepository;
    //private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    public Flux<Account> findAll() {

        return accountRepository.findAll();
    }

    public Mono<Account> findById(String id) {

        return accountRepository.findById(id);
    }

	public Mono<Account> save(Account account) {
		
		return customerRestClient.getCustomer(account.getCustomerId()).flatMap(customer -> {
			return (customer.getCustomerType().equals("Personal"))?
				
				 accountRepository.countByCustomerIdAndProductId(account.getCustomerId(),
						account.getProductId()).flatMap(count -> {
							
							return count.longValue() > 0 ?  Mono.empty():accountRepository.save(account);
						})			
				 
				: (account.getProductId().equals(accountConstant.PRODUCT_SAVINGS_ACCOUNT_ID) ||
						account.getProductId().equals(accountConstant.PRODUCT_FIXED_TERM_ACCOUNT_ID))?Mono.empty():
							accountRepository.save(account);
	 
			
			
		});
		

	}

    public Mono<Void> delete(Account account) {

        return accountRepository.delete(account);
    }

    public Flux<Account> findByCustomerId(String id) {
        return accountRepository.findByCustomerId(id);
    }

    public Mono<Account> updateAccount(Account updateAccount, String id) {

        return accountRepository.findById(id)
                .flatMap(account2 -> {
                    account2.setAccountNumber(updateAccount.getAccountNumber() != null ? updateAccount.getAccountNumber() : account2.getAccountNumber());
                    account2.setAmount(updateAccount.getAmount() != null ? updateAccount.getAmount() : account2.getAmount());
                    account2.setDateOpen(updateAccount.getDateOpen() != null ? updateAccount.getDateOpen() : account2.getDateOpen());
                    account2.setAmounttype(updateAccount.getAmounttype() != null ? updateAccount.getAmounttype() : account2.getAmounttype());
                    account2.setProductId(updateAccount.getProductId() != null ? updateAccount.getProductId() : account2.getProductId());
                    account2.setCustomerId(updateAccount.getCustomerId() != null ? updateAccount.getCustomerId() : account2.getCustomerId());
                    return accountRepository.save(account2);
                });
    }





}
