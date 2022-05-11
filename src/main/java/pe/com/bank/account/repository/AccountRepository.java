package pe.com.bank.account.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.com.bank.account.entity.Account;


@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account,String>{
	
  Flux<Account> findByCustomerId(String id);
  
  	Flux<Account> findByCustomerIdAndProductId(String customerId,String productId);
  	Mono<Long> countByCustomerIdAndProductId(String customerId,String productId);
  	
  
   
  
}
