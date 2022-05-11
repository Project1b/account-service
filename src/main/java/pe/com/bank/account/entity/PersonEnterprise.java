package pe.com.bank.account.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="personEnterprise")
public class PersonEnterprise {
	
	@Id
	private String id;
	private String rol;
	private String personId;
	private String enterpriseId;

}
