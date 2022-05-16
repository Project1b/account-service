package pe.com.bank.account.client.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionEntity {

	private String transactionId;
	private Double amount;
	private Date date;
	private String type;
	private String accountNumber;
	private String accountId;
	private String creditId;
}