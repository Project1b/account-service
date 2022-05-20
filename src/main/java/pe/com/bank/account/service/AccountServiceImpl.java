package pe.com.bank.account.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import pe.com.bank.account.client.CreditRestClient;
import pe.com.bank.account.client.CustomerRestClient;
import pe.com.bank.account.client.TransactionRestClient;
import pe.com.bank.account.dto.AccountTransactionDTO;
import pe.com.bank.account.dto.CurrentAccountValidateResponse;
import pe.com.bank.account.dto.TransactionDTO;
import pe.com.bank.account.entity.Account;
import pe.com.bank.account.entity.MovementEntity;
import pe.com.bank.account.repository.AccountRepository;
import pe.com.bank.account.util.AccountConstant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

	TransactionRestClient transactionRestClient;
	CustomerRestClient customerRestClient;
    AccountRepository accountRepository;
    CreditRestClient creditRestClient;

    public Flux<Account> findAll() {

        return accountRepository.findAll();
    }

    public Mono<Account> findById(String id) {

        return accountRepository.findById(id);
    }

	public Mono<Account> save(Account account) {
		return customerRestClient.getCustomer(account.getCustomerId()).flatMap(customer -> {
			if (customer.getCustomerType().equals("Personal")) {			
				return accountRepository.countByCustomerIdAndProductId(account.getCustomerId(),
						account.getProductId()).flatMap(count -> count.longValue() > 0 ?  Mono.empty():accountRepository.save(account)
						);
			}else {
				return  (account.getProductId().equals(AccountConstant.PRODUCT_SAVINGS_ACCOUNT_ID) ||
						account.getProductId().equals(AccountConstant.PRODUCT_FIXED_TERM_ACCOUNT_ID))?Mono.empty():
							accountRepository.save(account);	 
			}
			
		});
	}
	
	public Mono<Account> savePersonal(Account account) {
			return customerRestClient.getCustomer(account.getCustomerId()).flatMap(customer -> {
				return	accountRepository.countByCustomerIdAndProductId(account.getCustomerId(),
					account.getProductId()).flatMap(count -> {
						if(customer.getCategory().equals("VIP")) {
							creditRestClient.getCreditByCustomerId(account.getCustomerId());
							return count.longValue() > 0 ?  Mono.empty():accountRepository.save(account);
						}
						return count.longValue() > 0 ?  Mono.empty():accountRepository.save(account);
					});
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

    public Mono<CurrentAccountValidateResponse> validateCurrentAccount(String customerId,String accountId,Date date){
		return Mono.empty();    	
    }

	public Flux<Account> getAccounts() {
		return accountRepository.findAll();
	}

	public Mono<Account> getAccountById(String id) {
		return accountRepository.findById(id);
	}

	public Mono<Account> newAccount(Account account) {
		return accountRepository.save(account);
	}

	public Mono<Void> deleteAccountById(String id) {
		return accountRepository.deleteById(id);
	}

	public Mono<Account> getAccountByAccountNum(String accountNumber) {

		return accountRepository.findAccountsByAccountNumber(accountNumber);
	}

	public Mono<Account> editAccount(Account account, String id) {
		return findById(id).flatMap(c -> {
					c.setAccountNumber(account.getAccountNumber());
					c.setAmount(account.getAmount());
					c.setAmounttype(account.getAmounttype());
					c.setDateOpen(account.getDateOpen());
					return save(c);
				});
	}

	public Mono<AccountTransactionDTO> retrieveAccountAndTransactionsByAccountId(String accountId) {
		return getAccountById(accountId).flatMap(account -> {
			return transactionRestClient.retrieveTransaction(accountId).collectList().map(a ->
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

	public Mono<TransactionDTO> updateRestAmountByAccountId(MovementEntity movEntity) {    //ERROR AL INSERTAR A TRANSACTION
		return getAccountById(movEntity.getAccount_id()).flatMap(crc -> {
			var r = updateAccount(new Account(crc.getId(),
					crc.getAccountNumber(), crc.getAmount() - movEntity.getAmount(),
					crc.getDateOpen(), crc.getAmounttype(), crc.getLimitTr(), crc.getProductId(),
					crc.getCustomerId()), movEntity.getAccount_id());
			return r.flatMap(dsf -> {
				var count = transactionRestClient.contTransactionByType("Retiro", movEntity.getAccount_id());
				return count.flatMap(c -> {
					if (c > crc.getLimitTr()) {
						var r2 = transactionRestClient.createTransactionUpdate(new TransactionDTO(
								movEntity.getAmount(), movEntity.getDate(),
								movEntity.getType(), movEntity.getAccount_id(), 10.0));
						return r2.map(sd -> new TransactionDTO(
								movEntity.getAmount(), movEntity.getDate(),
								movEntity.getType(), movEntity.getAccount_id(), 10.0));
					} else {
						var r2 = transactionRestClient.createTransactionUpdate(new TransactionDTO(
								movEntity.getAmount(), movEntity.getDate(),
								movEntity.getType(), movEntity.getAccount_id(), 0.0));
						return r2.map(sd -> new TransactionDTO(
								movEntity.getAmount(), movEntity.getDate(), movEntity.getType(),
								movEntity.getAccount_id(), 0.0));
					}
				});
			});

		});

	}

	public Mono<TransactionDTO> updateSumAmountByAccountId( MovementEntity movEntity) {    //ERROR AL INSERTAR A TRANSACTION
		return getAccountById(movEntity.getAccount_id()).flatMap(crc -> {
			var r = updateAccount(new Account(crc.getId(),
					crc.getAccountNumber(),
					crc.getAmount() + movEntity.getAmount(),
					crc.getDateOpen(),
					crc.getAmounttype(), crc.getLimitTr(), crc.getProductId(),
					crc.getCustomerId()), movEntity.getAccount_id());

			return r.flatMap(dsf -> {
				var count = transactionRestClient.contTransactionByType("Retiro", movEntity.getAccount_id());
				return count.flatMap(c -> {
					if (c > crc.getLimitTr()) {
						var r2 = transactionRestClient.createTransactionUpdate(new TransactionDTO(
								movEntity.getAmount(), movEntity.getDate(),
								movEntity.getType(), movEntity.getAccount_id(), 10.0));

						return r2.map(sd -> new TransactionDTO(
								movEntity.getAmount(), movEntity.getDate(),
								movEntity.getType(), movEntity.getAccount_id(), 10.0));
					} else {
						var r2 = transactionRestClient.createTransactionUpdate(new TransactionDTO(
								movEntity.getAmount(), movEntity.getDate(), movEntity.getType(),
								movEntity.getAccount_id(), 0.0));

						return r2.map(sd -> new TransactionDTO(
								movEntity.getAmount(), movEntity.getDate(),
								movEntity.getType(), movEntity.getAccount_id(), 0.0));
					}
				});
			});
		});
	}

}