package pe.com.bank.account.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import pe.com.bank.account.client.entity.Customer;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
@Component
public class CustomerRestClient {
	
	
	  private WebClient webClient;		
	  
	  public CustomerRestClient(WebClient webClient) {
	        this.webClient = webClient;
	    }
	  
	  
	  @Value("${restClient.customersUrl}")
	  private String customerUrl;
	  
	  public Mono<Customer> getCustomer(String idCustomer){
		  
		  var url = customerUrl.concat("/6275a7aab557542205eb1c1d");
		  
		  return  webClient
	                .get()
	                .uri(url)
	                .retrieve()
	                .bodyToMono(Customer.class)
	                .log();
  
	  }  
	  
}
