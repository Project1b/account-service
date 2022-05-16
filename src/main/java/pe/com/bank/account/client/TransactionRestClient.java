package pe.com.bank.account.client;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import pe.com.bank.account.client.entity.CustomerEntity;
import pe.com.bank.account.client.entity.TransactionEntity;
import reactor.core.publisher.Flux;

@Component
public class TransactionRestClient {
	
	  private WebClient webClient;		
	  
	  public TransactionRestClient(WebClient webClient) {
	        this.webClient = webClient;
	    }
	  
	  
	  @Value("${restClient.transactionUrl}")
	  private String transactionUrl;
	  
	  public Flux<TransactionEntity> getTransactionsByDate(String idCustomer,String accountId,Date date){
		  
		  var url = transactionUrl.concat("/{id}");
		  
		  return  webClient
	                .get()
	                .uri(url,idCustomer)
	                .retrieve()
	                .bodyToFlux(TransactionEntity.class)
	                .log();

	  }  

}
