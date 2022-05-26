package pe.com.bank.account.service;

import java.util.Comparator;
import java.util.Date;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import pe.com.bank.account.client.CardRestClient;
import pe.com.bank.account.client.CreditRestClient;
import pe.com.bank.account.client.CustomerRestClient;
import pe.com.bank.account.client.TransactionRestClient;
import pe.com.bank.account.dto.AccountTransactionDTO;
import pe.com.bank.account.dto.CurrentAccountValidateResponse;
import pe.com.bank.account.dto.OperationCard;
import pe.com.bank.account.dto.AccountCardDTO;
import pe.com.bank.account.dto.TransactionDTO;
import pe.com.bank.account.entity.AccountEntity;
import pe.com.bank.account.entity.DebitCardEntity;
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
    CardRestClient cardRestClient;
    

    public Flux<AccountEntity> findAll() {

        return accountRepository.findAll();
    }

    public Mono<AccountEntity> findById(String id) {

        return accountRepository.findById(id);
    }
    
	public Mono<AccountEntity> save(AccountEntity account) {
		
		return customerRestClient.getCustomer(account.getCustomerId()).flatMap(customer -> {
			if (customer.getCustomerType().equals("Personal")) {			
				return this.savePersonal(account);
			}else {
				return  this.saveEnterprise(account);
			}
			
		});
	}	
	
	public Mono<AccountEntity> createAccountCard(AccountCardDTO accountCard) {			
		
				var r = cardRestClient.createDebitCard(accountCard.getAbc());
				
				return r.flatMap(dsf -> {
						 return save(new AccountEntity(null,//cardId
														accountCard.getAccountNumber(),
														accountCard.getAmount(),
														accountCard.getDateOpen(),
														accountCard.getAmounttype(),
														accountCard.getLimitTr(),
														accountCard.getProductId(),
														accountCard.getCustomerId(),
														dsf.getCardId(),
														accountCard.getCardLabel(),
														accountCard.getCardAssociation()
														));
				});
	}
	
	public Mono<AccountEntity> savePersonal(AccountEntity account) {
		
			return customerRestClient.getCustomer(account.getCustomerId()).flatMap(customer -> {		
				return	accountRepository.countByCustomerIdAndProductId(account.getCustomerId(),
					account.getProductId()).flatMap(count -> {					
						if(customer.getCategory().equals("VIP")) {							
						return	creditRestClient.getCountByCustomerIdAndProductId(account.getCustomerId(),
									AccountConstant.PRODUCT_CREDIT_CARD_ID).flatMap( countCredit ->  {
										if(countCredit.longValue() < 1 ) { return	Mono.empty();
										}
										return count.longValue() > 0 ?  Mono.empty():accountRepository.save(account);
									});
						}
						return count.longValue() > 0 ?  Mono.empty():accountRepository.save(account);
					});
			});
	}
	
	public Mono<AccountEntity> saveEnterprise(AccountEntity account) {
		
		return customerRestClient.getCustomer(account.getCustomerId()).flatMap(customer -> {		
			return	accountRepository.countByCustomerIdAndProductId(account.getCustomerId(),
				account.getProductId()).flatMap(count -> {					
					if(customer.getCategory().equals("PYME")) {							
					return	creditRestClient.getCountByCustomerIdAndProductId(account.getCustomerId(),
								AccountConstant.PRODUCT_CREDIT_CARD_ID).flatMap( countCredit ->  {
									if(countCredit.longValue() < 1 ) { return	Mono.empty();
									}
									return count.longValue() > 0 ?  Mono.empty():accountRepository.save(account);
								});
					}
					return (account.getProductId().equals(AccountConstant.PRODUCT_SAVINGS_ACCOUNT_ID) ||
							account.getProductId().equals(AccountConstant.PRODUCT_FIXED_TERM_ACCOUNT_ID))?Mono.empty():
								accountRepository.save(account);
				});
		});
}

    public Mono<Void> delete(AccountEntity account) {

        return accountRepository.delete(account);
    }

    public Flux<AccountEntity> getByCustomerId(String id) {
        return accountRepository.findByCustomerId(id);
    }

    public Mono<AccountEntity> updateAccount(AccountEntity updateAccount, String id) {

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

    public Mono<CurrentAccountValidateResponse> validateCurrentAccount(String customerId, String accountId, Date date) {
        return Mono.empty();
    }

    public Flux<AccountEntity> getAccounts() {
        return accountRepository.findAll();
    }

    public Flux<AccountEntity> getAccountByProductId(String productId) {
        return accountRepository.findByProductId(productId);
    }

    public Mono<AccountEntity> getAccountById(String id) {
        return accountRepository.findById(id);
    }

    public Mono<AccountEntity> newAccount(AccountEntity account) {
        return accountRepository.save(account);
    }

    public Mono<Void> deleteAccountById(String id) {
        return accountRepository.deleteById(id);
    }

    public Mono<AccountEntity> getAccountByAccountNum(String accountNumber) {

        return accountRepository.findAccountsByAccountNumber(accountNumber);
    }

    public Mono<AccountEntity> editAccount(AccountEntity account, String id) {
        return findById(id).flatMap(c -> {
            c.setAccountNumber(account.getAccountNumber());
            c.setAmount(account.getAmount());
            c.setAmounttype(account.getAmounttype());
            c.setDateOpen(account.getDateOpen());
            return save(c);
        });
    }


	public Mono<TransactionDTO> updateRestAmountByAccountId(MovementEntity movEntity) {   
		
		return getAccountById(movEntity.getAccount_id()).flatMap(crc -> {
			var r = updateAccount(new AccountEntity(crc.getId(),
					crc.getAccountNumber(), crc.getAmount() - movEntity.getAmount(),
					crc.getDateOpen(), crc.getAmounttype(), crc.getLimitTr(), crc.getProductId(),
					crc.getCustomerId(),
					crc.getCardId(),
					crc.getCardLabel(),
					crc.getCardAssociation()), movEntity.getAccount_id());
			
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


    public Mono<TransactionDTO> updateSumAmountByAccountId(MovementEntity movEntity) {    //ERROR AL INSERTAR A TRANSACTION
        return getAccountById(movEntity.getAccount_id()).flatMap(crc -> {
            var r = updateAccount(new AccountEntity(crc.getId(),
                    crc.getAccountNumber(),
                    crc.getAmount() + movEntity.getAmount(),
                    crc.getDateOpen(),
                    crc.getAmounttype(), crc.getLimitTr(), crc.getProductId(),
                    crc.getCustomerId(), crc.getCardId(), crc.getCardLabel(), crc.getCardAssociation()), movEntity.getAccount_id());

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

    public Flux<AccountEntity> findAllByCardId(String id) {
        return accountRepository.findByCardId(id);
    }

    public Mono<AccountEntity> operationCardAssociation(OperationCard operationCard) {
        return accountRepository.findByCardId(operationCard.getCardId())
                .filter(account -> account.getAmount() > operationCard.getAmount())
                .sort(Comparator.comparing(AccountEntity::getCardAssociation))
                .take(1).next();
    }


    public Mono<TransactionDTO> operationCard(OperationCard operationCard) {
        return accountRepository.findByCardIdAndCardLabel(operationCard.getCardId(), "CP")
                .filter(account -> account.getAmount() > operationCard.getAmount())
                .switchIfEmpty(operationCardAssociation(operationCard))
                .flatMap(accountR -> updateAccount(new AccountEntity(accountR.getId(), accountR.getAccountNumber(), accountR.getAmount() - operationCard.getAmount(), accountR.getDateOpen(), accountR.getAmounttype(), accountR.getLimitTr(), accountR.getProductId(), accountR.getCustomerId(), accountR.getCardId(), accountR.getCardLabel(), accountR.getCardAssociation()), accountR.getId()))
                .flatMap(tr -> transactionRestClient.createTransactionUpdate(new TransactionDTO(operationCard.getAmount(), tr.getDateOpen(), "Retiro", tr.getId(), 0.0)));
    }
}