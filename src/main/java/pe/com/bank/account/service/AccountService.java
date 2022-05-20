package pe.com.bank.account.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import pe.com.bank.account.dto.AccountTransactionDTO;
import pe.com.bank.account.dto.CurrentAccountValidateResponse;
import pe.com.bank.account.dto.TransactionDTO;
import pe.com.bank.account.entity.Account;
import pe.com.bank.account.entity.MovementEntity;
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
	
	public Mono<CurrentAccountValidateResponse> validateCurrentAccount(String customerId,String accountId,Date date);

	public Flux<Account> getAccounts();
	public Mono<Account> getAccountById(String id);

	public Mono<Account> newAccount(Account account);

	public Mono<Void> deleteAccountById(String id);

	public Mono<Account> getAccountByAccountNum(String accountNumber);

	public Mono<TransactionDTO> updateRestAmountByAccountId(MovementEntity movEntity);

	public Mono<TransactionDTO> updateSumAmountByAccountId( MovementEntity movEntity);

	public Mono<AccountTransactionDTO> retrieveAccountAndTransactionsByAccountId(String accountId);

	public Mono<Account> editAccount(Account account, String id);

}
