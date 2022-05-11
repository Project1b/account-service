package pe.com.bank.account.client.entity;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
	
	@Id
	private String id;
	private String customerType;
	private String dateAssociated;	

}
