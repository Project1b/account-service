package pe.com.bank.account.client;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import pe.com.bank.account.client.entity.TransactionEntity;
import reactor.core.publisher.Flux;

@Component
public class CreditRestClient {
	
	
	  private WebClient webClient;		
	  
	  public CreditRestClient(WebClient webClient) {
	        this.webClient = webClient;
	    }
	  
	  
	  @Value("${restClient.creditUrl}")
	  private String creditUrl;
	  
	  public Flux<TransactionEntity> getCreditByCustomerId(String customerId){
		  
		  var url = creditUrl.concat("/{id}");
		  
		  return  webClient
	                .get()
	                .uri(url,customerId)
	                .retrieve()
	                .bodyToFlux(TransactionEntity.class)
	                .log();

	  }  

}
