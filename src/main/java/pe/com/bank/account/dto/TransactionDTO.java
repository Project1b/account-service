package pe.com.bank.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

	//private String transactionId;
	private double amount;
	private String date;
	private String type;
	private String accountNumber;
}
