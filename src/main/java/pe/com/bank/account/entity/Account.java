package pe.com.bank.account.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="account")
public class Account {

	@Id
	private String id;
	private String accountNumber;
	private Double amount;
	private Date dateOpen;
	private String amounttype;	
	private String productId;
	private String customerId;
}
