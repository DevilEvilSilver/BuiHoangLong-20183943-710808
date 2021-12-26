package subsystem.interbank;

import java.util.Map;
import java.util.logging.Logger;

import common.exception.InternalServerErrorException;
import common.exception.InvalidCardException;
import common.exception.InvalidTransactionAmountException;
import common.exception.InvalidVersionException;
import common.exception.NotEnoughBalanceException;
import common.exception.NotEnoughTransactionInfoException;
import common.exception.SuspiciousTransactionException;
import common.exception.UnrecognizedException;
import controller.PlaceOrderController;
import entity.payment.CreditCard;
import entity.payment.DebitCard;
import entity.payment.PaymentCard;
import entity.payment.PaymentTransaction;
import utils.Configs;
import utils.MyMap;
import utils.Utils;

public class InterbankSubsystemController {

	private static final String PUBLIC_KEY = "BqPZiLmLrnw=";
	private static final String SECRET_KEY = "BLvxSsRC2PY=";
	private static final String PAY_COMMAND = "pay";
	private static final String VERSION = "1.0.0";
	
	/**
     * Just for logging purpose
     */
    private static Logger LOGGER = utils.Utils.getLogger(InterbankSubsystemController.class.getName());


	private static InterbankBoundary interbankBoundary = new InterbankBoundary();

	public PaymentTransaction refund(PaymentCard card, int amount, String contents) {
		return null;
	}
	
	private String generateData(Map<String, Object> data) {
		return ((MyMap) data).toJSON();
	}

	public PaymentTransaction payOrder(PaymentCard card, int amount, String contents) {
		Map<String, Object> transaction = new MyMap();

		try {
			transaction.putAll(MyMap.toMyMap(card));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new InvalidCardException();
		}
		transaction.put("command", PAY_COMMAND);
		transaction.put("transactionContent", contents);
		transaction.put("amount", amount);
		transaction.put("createdAt", Utils.getToday());

		Map<String, Object> requestMap = new MyMap();
		requestMap.put("version", VERSION);
		requestMap.put("transaction", transaction);
		LOGGER.info("Request info");
		LOGGER.info(generateData(requestMap).toString());

		String responseText = interbankBoundary.query(Configs.PROCESS_TRANSACTION_URL, generateData(requestMap), transaction.get("cvvCode").toString());
		MyMap response = null;
		try {
			response = MyMap.toMyMap(responseText, 0);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new UnrecognizedException();
		}

		return makePaymentTransaction(response);
	}

	private PaymentTransaction makePaymentTransaction(MyMap response) {
		if (response == null)
			return null;
		MyMap transcation = (MyMap) response.get("transaction");
		PaymentCard card = getCardInfo(transcation);
		PaymentTransaction trans = new PaymentTransaction((String) response.get("errorCode"), card,
				(String) transcation.get("transactionId"), (String) transcation.get("transactionContent"),
				Integer.parseInt((String) transcation.get("amount")), (String) transcation.get("createdAt"));

		switch (trans.getErrorCode()) {
		case "00":
			break;
		case "01":
			throw new InvalidCardException();
		case "02":
			throw new NotEnoughBalanceException();
		case "03":
			throw new InternalServerErrorException();
		case "04":
			throw new SuspiciousTransactionException();
		case "05":
			throw new NotEnoughTransactionInfoException();
		case "06":
			throw new InvalidVersionException();
		case "07":
			throw new InvalidTransactionAmountException();
		default:
			throw new UnrecognizedException();
		}

		return trans;
	}
	
	private PaymentCard getCardInfo(MyMap transcation) {
		// check if pay by credit card
		if (transcation.get("dateExpired") == null)
			return new CreditCard((String) transcation.get("cardCode"), (String) transcation.get("owner"),
				Integer.parseInt((String) transcation.get("cvvCode")), (String) transcation.get("dateExpired"));
		else {
			return new DebitCard((String) transcation.get("cardCode"), (String) transcation.get("owner"),
					Integer.parseInt((String) transcation.get("cvvCode")), (String) transcation.get("dateValid"));
		}
	}

}
