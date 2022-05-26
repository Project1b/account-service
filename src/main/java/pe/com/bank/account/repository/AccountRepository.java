package pe.com.bank.account.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.com.bank.account.entity.AccountEntity;


@Repository
public interface AccountRepository extends ReactiveMongoRepository<AccountEntity,String>{
	
   Flux<AccountEntity> findByCustomerId(String id);
  
   Flux<AccountEntity> findByCustomerIdAndProductId(String customerId,String productId);
  	
   Mono<Long> countByCustomerIdAndProductId(String customerId,String productId);
  	
   Flux<AccountEntity> findByProductId(String productId);
  	
   Mono<AccountEntity> findAccountsByAccountNumber(String accountNumber);
   
   Mono<AccountEntity> findByCardId (String cardId);

}
