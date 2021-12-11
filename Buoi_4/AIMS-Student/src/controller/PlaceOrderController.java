package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import entity.cart.Cart;
import entity.cart.CartMedia;
import common.exception.InvalidDeliveryInfoException;
import entity.invoice.Invoice;
import entity.order.Order;
import entity.order.OrderMedia;
import views.screen.popup.PopupScreen;

/**
 * This class controls the flow of place order usecase in our AIMS project
 * @author nguyenlm
 */
public class PlaceOrderController extends BaseController{

    /**
     * Just for logging purpose
     */
    private static Logger LOGGER = utils.Utils.getLogger(PlaceOrderController.class.getName());

    /**
     * This method checks the avalibility of product when user click PlaceOrder button
     * @throws SQLException
     */
    public void placeOrder() throws SQLException{
        Cart.getCart().checkAvailabilityOfProduct();
    }

    /**
     * This method creates the new Order based on the Cart
     * @return Order
     * @throws SQLException
     */
    public Order createOrder() throws SQLException{
        Order order = new Order();
        for (Object object : Cart.getCart().getListMedia()) {
            CartMedia cartMedia = (CartMedia) object;
            OrderMedia orderMedia = new OrderMedia(cartMedia.getMedia(), 
                                                   cartMedia.getQuantity(), 
                                                   cartMedia.getPrice());    
            order.getlstOrderMedia().add(orderMedia);
        }
        return order;
    }

    /**
     * This method creates the new Invoice based on order
     * @param order
     * @return Invoice
     */
    public Invoice createInvoice(Order order) {
        return new Invoice(order);
    }

    /**
     * This method takes responsibility for processing the shipping info from user
     * @param info
     * @throws InterruptedException
     * @throws IOException
     */
    public void processDeliveryInfo(HashMap info) throws InterruptedException, IOException{
        LOGGER.info("Process Delivery Info");
        LOGGER.info(info.toString());
        validateDeliveryInfo(info);
    }
    
    /**
   * The method validates the info
   * @param info
   * @throws InterruptedException
   * @throws IOException
   */
    public void validateDeliveryInfo(HashMap<String, String> info) throws InterruptedException, IOException{
    	if (!validateName(info.get("name"))) {
    		PopupScreen.error("Invalid name");
    		throw new InvalidDeliveryInfoException("Invalid name");
    	}
    	if (!validatePhoneNumber(info.get("phone"))) {
    		PopupScreen.error("Invalid phone number");
    		throw new InvalidDeliveryInfoException("Invalid phone number");
    	}
    	if (!validateAddress(info.get("address"))) {
    		PopupScreen.error("Invalid address");
    		throw new InvalidDeliveryInfoException("Invalid address");
    	}
    }
    
    public boolean validatePhoneNumber(String phoneNumber) {
    	//check if phone number has 10 digits
    	if (phoneNumber.length() != 10) 
    		return false;
    	
    	//check if phone number start with 0
    	if (!phoneNumber.startsWith("0"))
    		return false;
    	
    	//check if phone number contain only number
    	try {
    		Integer.parseInt(phoneNumber);
    	} catch (NumberFormatException e) {
    		return false;
    	}
    	
    	return true;
    }
    
    public boolean validateName(String name) {
    	//	check if name is null
    	if (name == null || name.isEmpty())
    		return false;
    	
    	// check for unique chars and numbers
    	for (int i = 0; i < name.length(); i++) {
    		if (!((name.charAt(i) == 32) ||
    			(name.charAt(i) >= 65 && name.charAt(i) <= 90) ||
    			(name.charAt(i) >= 97 && name.charAt(i) <= 122)))
    			return false;
    	}
    	
    	return true;
    }
    
    public boolean validateAddress(String address) {
//    	check if name is null
    	if (address == null || address.length() == 0)
    		return false;
    	
    	// check for unique chars
    	for (int i = 0; i < address.length(); i++) {
    		if (!((address.charAt(i) == 32) ||
    			(address.charAt(i) >= 65 && address.charAt(i) <= 90) ||
    			(address.charAt(i) >= 97 && address.charAt(i) <= 122) ||
    			(address.charAt(i) >= 48 && address.charAt(i) <= 57)))
    			return false;
    	}
    	
    	return true;
    }
    

    /**
     * This method calculates the shipping fees of order
     * @param order
     * @return shippingFee
     */
    public int calculateShippingFee(Order order){
        Random rand = new Random();
        int fees = (int)( ( (rand.nextFloat()*10)/100 ) * order.getAmount() );
        LOGGER.info("Order Amount: " + order.getAmount() + " -- Shipping Fees: " + fees);
        return fees;
    }
}
