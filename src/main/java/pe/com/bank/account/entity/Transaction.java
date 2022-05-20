package pe.com.bank.account.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="transaction")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction {

	@Id
	private String transactionId;
	private double amount;
	private String date;
	private String type;
	private String accountNumber;
	private String creditId;

}
