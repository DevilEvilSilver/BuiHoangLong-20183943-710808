package entity.payment;

import java.sql.Timestamp;

public class DebitCard extends PaymentCard {
	private String cardCode;
	private String owner;
	private int cvvCode;
	private String dateValid;

	public DebitCard(String cardCode, String owner, int cvvCode, String dateValid) {
		super();
		this.cardCode = cardCode;
		this.owner = owner;
		this.cvvCode = cvvCode;
		this.dateValid = dateValid;
	}
}
